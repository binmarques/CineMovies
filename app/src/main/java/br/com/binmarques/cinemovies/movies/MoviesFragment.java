package br.com.binmarques.cinemovies.movies;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import br.com.binmarques.cinemovies.R;
import br.com.binmarques.cinemovies.allmovies.AllMoviesActivity;
import br.com.binmarques.cinemovies.base.BaseFragment;
import br.com.binmarques.cinemovies.events.MovieDetailsEvent;
import br.com.binmarques.cinemovies.events.MoviesEvent;
import br.com.binmarques.cinemovies.listener.RecyclerViewOnItemClickListener;
import br.com.binmarques.cinemovies.model.Result;
import br.com.binmarques.cinemovies.moviedetails.MovieDetailsActivity;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created By Daniel Marques on 05/08/2018
 */

public class MoviesFragment extends BaseFragment implements RecyclerViewOnItemClickListener,
                                                            SwipeRefreshLayout.OnRefreshListener,
                                                            SectionAdapter.OnSectionItemClickListener,
                                                            MoviesContract.View {

    @BindView(R.id.containerSwipe) protected SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.recyclerViewMovies) protected RecyclerView mRecyclerView;
    @BindView(R.id.loadingProgress) protected ProgressBar mProgressBar;
    @BindView(R.id.tvEmptyView) protected TextView mEmptyView;
    @BindView(R.id.featuredMoviePoster) protected ImageView mPoster;
    @BindView(R.id.tvFeaturedMovieTitle) protected TextView mTitle;
    @BindView(R.id.tvFeaturedMovieDescription) protected TextView mDescription;
    @BindView(R.id.cardViewFeatured) protected CardView mCardView;

    private Result mFeaturedMovie;
    private MoviesAdapter mAdapter;
    private Snackbar mSnackbar;
    private MoviesContract.Presenter mPresenter;
    private EventBus mEventBus;

    public static final String EXTRA_MOVIE_TYPE = "EXTRA_MOVIE_TYPE";
    private static final int FEATURED_MOVIE_ANIM_DELAY = 800;

    public MoviesFragment() {}

    public static MoviesFragment newInstance() {
        return new MoviesFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEventBus = EventBus.getDefault();
        mSwipeRefresh.setOnRefreshListener(this);

        mSwipeRefresh.setColorSchemeResources(
                R.color.colorAccent,
                R.color.background,
                R.color.colorPrimaryLight
        );

        mSwipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.cardBackground));

        if (getActivity() != null) {
            mRecyclerView.setHasFixedSize(true);

            LinearLayoutManager layoutManager =
                    new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

            mRecyclerView.setLayoutManager(layoutManager);
            mAdapter = new MoviesAdapter(getActivity(), new ArrayList<>());
            mAdapter.setRecyclerViewOnItemClickListener(this);
            mAdapter.setOnSectionItemClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        }

        if (savedInstanceState == null) {
            if (isNetworkAvailable()) {
                mPresenter.start();
            } else {
                showProgress(false);
                showEmptyView(true);
                showConnectionError(null);
            }
        } else {
            boolean posted = mEventBus.getStickyEvent(MoviesEvent.class) != null;

            if (!posted) {
                showProgress(false);
                showEmptyView(true);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mEventBus.unregister(this);
        mPresenter.clearSubscriptions();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null && getActivity().isFinishing()) {
            mEventBus.removeAllStickyEvents();
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_movies;
    }

    @Override
    public void onRefresh() {
        if (mSwipeRefresh.isRefreshing()) {
            int duration = getResources()
                    .getInteger(android.R.integer.config_longAnimTime);

            mSwipeRefresh.postDelayed(() -> {
                if (mAdapter.getItemCount() > 0) {
                    mSwipeRefresh.setRefreshing(false);
                    return;
                }

                mPresenter.start();
            }, duration);
        }
    }

    @Override
    public void setPresenter(@NonNull MoviesContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onItemClick(@NonNull View view, @NonNull Result movie) {}

    @Override
    public void onItemClickView(@NonNull View view, int position) {
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());

        startActivity(new Intent(getActivity(), AllMoviesActivity.class)
                .putExtra(EXTRA_MOVIE_TYPE, position), options.toBundle());
    }

    @Override
    public void onItemLongPressClick(@NonNull View view, int position) {}

    @Override
    public void onSectionItemClick(@NonNull View view, @NonNull Result movie) {
        goToDetailsActivity(view, movie.getId());
    }

    @Override
    public void showProgress(boolean visible) {
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private Result getFeaturedMovie() {
        return mFeaturedMovie;
    }

    @Override
    public void showEmptyView(boolean visible) {
        mEmptyView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showConnectionError(String errorMessage) {
        if (getView() == null) {
            return;
        }

        mSnackbar = Snackbar.make(
                getView(),
                !TextUtils.isEmpty(errorMessage) ?
                        errorMessage : getString(R.string.connection_failed_to_network_snackbar),
                Snackbar.LENGTH_INDEFINITE
        );

        mSnackbar.show();

        mSnackbar.setAction(R.string.title_retry_snackbar, v -> {
            showEmptyView(false);
            showProgress(true);
            mPresenter.start();
        });
    }

    @Override
    public void hideRefreshing() {
        if (mSwipeRefresh.isRefreshing()) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void hideConnectionError() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NonNull MoviesEvent event) {
        if (mAdapter.getItemCount() > 0) {
            return;
        }

        showProgress(false);
        mAdapter.addItems(event.mItems);
        setFeaturedMovie(event.mFeaturedMovie);
        bindFeaturedView();
    }

    @OnClick(R.id.cardViewFeatured)
    public void onFeaturedClick(View view) {
        Result featuredMovie = getFeaturedMovie();
        goToDetailsActivity(view, featuredMovie.getId());
    }

    public void setFeaturedMovie(@NonNull Result featuredMovie) {
        this.mFeaturedMovie = featuredMovie;
    }

    private RequestBuilder<Drawable> loadImage(@NonNull Context context, @NonNull String path) {
        return Glide
                .with(context)
                .load(path)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL).centerCrop())
                .transition(new DrawableTransitionOptions().crossFade());
    }

    private void bindFeaturedView() {
        if (getActivity() == null) {
            return;
        }

        Result featuredMovie = getFeaturedMovie();
        mTitle.setText(featuredMovie.getTitle());
        mDescription.setText(featuredMovie.getOverview());
        loadImage(getActivity(), featuredMovie.getBackdropPath()).into(mPoster);
        mCardView.postDelayed(() -> mCardView.setVisibility(View.VISIBLE), FEATURED_MOVIE_ANIM_DELAY);
    }

    private void goToDetailsActivity(View view, long id) {
        MovieDetailsEvent event = mEventBus.getStickyEvent(MovieDetailsEvent.class);

        if (event != null) {
            mEventBus.removeStickyEvent(event);
        }

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeClipRevealAnimation(view, 0, 0, view.getWidth(), view.getHeight());

        startActivity(new Intent(getActivity(),
                MovieDetailsActivity.class).putExtra(EXTRA_ID, id), options.toBundle());
    }
}
