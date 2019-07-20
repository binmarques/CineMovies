package br.com.binmarques.cinemovies.allmovies;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import br.com.binmarques.cinemovies.R;
import br.com.binmarques.cinemovies.base.BaseFragment;
import br.com.binmarques.cinemovies.events.AllMoviesEvent;
import br.com.binmarques.cinemovies.events.MovieDetailsEvent;
import br.com.binmarques.cinemovies.listener.EndlessScrollListener;
import br.com.binmarques.cinemovies.listener.RecyclerViewOnItemClickListener;
import br.com.binmarques.cinemovies.listener.ReloadPageCallback;
import br.com.binmarques.cinemovies.model.Result;
import br.com.binmarques.cinemovies.moviedetails.MovieDetailsActivity;
import br.com.binmarques.cinemovies.movies.MoviesFragment;
import br.com.binmarques.cinemovies.util.MovieTypes;
import butterknife.BindView;

/**
 * Created By Daniel Marques on 10/08/2018
 */

public class AllMoviesFragment extends BaseFragment implements ReloadPageCallback,
                                                               RecyclerViewOnItemClickListener,
                                                               SwipeRefreshLayout.OnRefreshListener,
                                                               AllMoviesContract.View {

    @BindView(R.id.recyclerViewAllMovies) protected RecyclerView mRecyclerView;
    @BindView(R.id.loadingProgress) protected ProgressBar mProgressBar;
    @BindView(R.id.tvEmptyView) protected TextView mEmptyView;
    @BindView(R.id.containerSwipe) protected SwipeRefreshLayout mSwipeRefresh;

    private AllMoviesAdapter mAdapter;
    private boolean mIsLoading = false;
    private boolean mIsLastPage = false;
    private Snackbar mSnackbar;
    private AllMoviesContract.Presenter mPresenter;
    private MovieTypes mCurrentMovieType;
    private EventBus mEventBus;

    private static final int PAGE_START = 1;
    private int mCurrentPage = PAGE_START;

    public AllMoviesFragment() {}

    public static AllMoviesFragment newInstance() {
        return new AllMoviesFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefresh.setOnRefreshListener(this);
        mEventBus = EventBus.getDefault();

        mSwipeRefresh.setColorSchemeResources(
                R.color.colorAccent,
                R.color.background,
                R.color.colorPrimaryLight
        );

        mSwipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.cardBackground));

        if (getArguments() != null) {
            int movieType = getArguments().getInt(MoviesFragment.EXTRA_MOVIE_TYPE, -1);

            switch (movieType) {
                case 0:
                    mCurrentMovieType = MovieTypes.THEATERS;
                    break;

                case 1:
                    mCurrentMovieType = MovieTypes.POPULAR;
                    break;

                case 2:
                    mCurrentMovieType = MovieTypes.TOP_RATED;
                    break;

                case 3:
                    mCurrentMovieType = MovieTypes.UPCOMING;
                    break;
            }

            setTitle(mCurrentMovieType.getDescription());
        }

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new AllMoviesAdapter(getActivity(), new ArrayList<>(), this);
        mAdapter.setRecyclerViewOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                if (isNetworkAvailable()) {
                    setLoading(true);
                    mCurrentPage++;
                    mPresenter.loadNextPage(mCurrentMovieType);
                } else if (mAdapter.isNotReloading()) {
                    showReload(true);
                }
            }

            @Override
            public boolean isLastPage() {
                return mIsLastPage;
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }
        });

        if (savedInstanceState == null) {
            mPresenter.loadFirstPage(mCurrentMovieType);
        } else {
            boolean posted = mEventBus.getStickyEvent(AllMoviesEvent.class) != null;

            if (!posted) {
                showProgress(false);
                showEmptyView(true);
            }
        }

        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                animateContent();
                return true;
            }
        });
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_all_movies;
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
            AllMoviesEvent event = mEventBus.getStickyEvent(AllMoviesEvent.class);

            if (event != null) {
                mEventBus.removeStickyEvent(event);
            }

        } else if (mAdapter.getItemCount() > 0) {
            mPresenter.dispatchMessageEvent();
        }
    }

    @Override
    public void onRefresh() {
        if (mSwipeRefresh.isRefreshing()) {
            int duration = getResources()
                    .getInteger(android.R.integer.config_longAnimTime);

            mSwipeRefresh.postDelayed(() -> {
                setLastPage(false);
                mCurrentPage = PAGE_START;
                mAdapter.clearItems();
                mPresenter.loadFirstPage(mCurrentMovieType);
            }, duration);
        }
    }

    @Override
    public void setPresenter(@NonNull AllMoviesContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onReloadPage() {
        mCurrentPage++;
        mPresenter.loadNextPage(mCurrentMovieType);
    }

    @Override
    public void onItemClick(@NonNull View view, @NonNull Result movie) {
        goToDetailsActivity(view, movie.getId());
    }

    @Override
    public void onItemClickView(@NonNull View view, int position) {}

    @Override
    public void onItemLongPressClick(@NonNull View view, int position) {}

    @Override
    public void addLoadingFooter() {
        mAdapter.addLoadingFooter();
    }

    @Override
    public void removeLoadingFooter() {
        mAdapter.removeLoadingFooter();
    }

    @Override
    public void showReload(final boolean show) {
        mRecyclerView.post(() -> mAdapter.showReload(show));
    }

    @Override
    public void setLastPage(boolean isLastPage) {
        this.mIsLastPage = isLastPage;
    }

    @Override
    public void setLoading(boolean isLoading) {
        this.mIsLoading = isLoading;
    }

    @Override
    public void showEmptyView(boolean visible) {
        mEmptyView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showProgress(boolean visible) {
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
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

        if (!mSnackbar.isShown()) {
            mSnackbar.show();
        }

        mSnackbar.setAction(R.string.title_retry_snackbar, v -> {
            showEmptyView(false);

            if (mAdapter.getItemCount() <= 0) {
                showProgress(true);
                mPresenter.loadFirstPage(mCurrentMovieType);
            }
        });
    }

    @Override
    public int getCurrentPage() {
        return mCurrentPage;
    }

    @Override
    public void backPreviousPage() {
        mCurrentPage--;
    }

    @Override
    public void addItems(@NonNull List<Result> movies) {
        mAdapter.addItems(movies);
    }

    @Override
    public List<Result> getMovies() {
        return mAdapter.getMovies();
    }

    @Override
    public void hideConnectionError() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
    }

    @Override
    public void hideRefreshing() {
        if (mSwipeRefresh.isRefreshing()) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        String message = getString(R.string.title_fail) + errorMessage;
        showMessage(message);
    }

    @Override
    public void setErrorMessage(int errorMessage) {
        showMessage(getString(errorMessage));
    }

    @Override
    public boolean isLoadingAdded() {
        return mAdapter.isLoadingAdded();
    }

    @Override
    public boolean isLastPage() {
        return mIsLastPage;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NonNull AllMoviesEvent event) {
        if (mAdapter.getItemCount() > 0) {
            return;
        }

        showProgress(false);
        addItems(event.mItems);
        showReload(false);
        setLoading(false);
        mCurrentPage = event.mCurrentPage;
        mIsLastPage = event.mIsLastPage;

        if (event.mShouldRemoveLoadingFooter && !mIsLastPage) {
            removeLoadingFooter();
            addLoadingFooter();
        }
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

    private void setTitle(String title) {
        if (getActivity() != null) {
            getActivity().setTitle(title);
        }
    }

    private void animateContent() {
        Interpolator interpolator = new DecelerateInterpolator();
        mRecyclerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mRecyclerView.setAlpha(0f);
        mRecyclerView.setTranslationY(75);
        mRecyclerView.animate()
                .setInterpolator(interpolator)
                .alpha(1.0f)
                .translationY(0)
                .setStartDelay(400)
                .start();
    }
}
