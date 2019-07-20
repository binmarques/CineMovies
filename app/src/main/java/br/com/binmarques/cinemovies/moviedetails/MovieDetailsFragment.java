package br.com.binmarques.cinemovies.moviedetails;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import br.com.binmarques.cinemovies.R;
import br.com.binmarques.cinemovies.base.BaseFragment;
import br.com.binmarques.cinemovies.events.MovieDetailsEvent;
import br.com.binmarques.cinemovies.model.Result;
import br.com.binmarques.cinemovies.model.Review;
import br.com.binmarques.cinemovies.model.Video;
import br.com.binmarques.cinemovies.util.CustomSnapHelper;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created By Daniel Marques on 09/08/2018
 */

public class MovieDetailsFragment extends BaseFragment implements MovieDetailsAdapter.OnItemClickListener,
                                                                  MovieTrailersAdapter.OnItemClickListener,
                                                                  MovieDetailsContract.View {

    @BindView(R.id.containerDetails) protected NestedScrollView mContainer;
    @BindView(R.id.moviePoster) protected ImageView mPoster;
    @BindView(R.id.tvMovieTitle) protected TextView mTitle;
    @BindView(R.id.tvMovieYearAndRuntime) protected TextView mYearAndRuntime;
    @BindView(R.id.tvVoteAverage) protected TextView mVoteAverage;
    @BindView(R.id.tvSynopsis) protected TextView mSynopsis;
    @BindView(R.id.recyclerViewSimilarMovies) protected RecyclerView mRecyclerView;
    @BindView(R.id.tvSimilarMoviesTitle) protected TextView mSimilarMoviesTitle;
    @BindView(R.id.tvHomepage) protected TextView mHomepage;
    @BindView(R.id.recyclerViewTrailers) protected RecyclerView mRecyclerViewTrailers;
    @BindView(R.id.tvTrailersTitle) protected TextView mTrailerTitle;
    @BindView(R.id.horizontalContainer) protected HorizontalScrollView mHorizontalContainer;
    @BindView(R.id.chipGroup) protected ChipGroup mChipGroup;
    @BindView(R.id.reviewsContainer) protected ViewGroup mReviewsContainer;
    @BindView(R.id.tvReviewsTitle) protected TextView mReviewsTitle;

    private TextView mEmptyView;
    private ProgressBar mProgressBar;

    private MovieDetailsContract.Presenter mPresenter;
    private MovieDetailsAdapter mAdapter;
    private MovieTrailersAdapter mTrailersAdapter;
    private Result mCurrentMovie;
    private EventBus mEventBus;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Video mShareTrailer;
    private MenuItem mShareItem;
    private List<Review> mReviews;

    private static final int ANIM_FADE_DURATION = 800;
    private static final int TEXT_MAX_LINES = 3;

    public MovieDetailsFragment() {}

    public static MovieDetailsFragment newInstance() {
        return new MovieDetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() == null) {
            return;
        }

        mEventBus = EventBus.getDefault();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerViewTrailers.setHasFixedSize(true);
        mEmptyView = getActivity().findViewById(R.id.tvEmptyView);
        mProgressBar = getActivity().findViewById(R.id.loadingProgress);
        mCollapsingToolbarLayout = getActivity().findViewById(R.id.collapsingToolbar);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        LinearLayoutManager layoutManagerTrailers =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerViewTrailers.setLayoutManager(layoutManagerTrailers);
        mAdapter = new MovieDetailsAdapter(getActivity(), new ArrayList<>());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mTrailersAdapter = new MovieTrailersAdapter(getActivity(), new ArrayList<>());
        mTrailersAdapter.setOnItemClickListener(this);
        mRecyclerViewTrailers.setAdapter(mTrailersAdapter);
        SnapHelper snapHelper = new CustomSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        mCollapsingToolbarLayout.setTitleEnabled(false);

        if (savedInstanceState == null) {
            if (isNetworkAvailable()) {
                mPresenter.start();
            } else {
                showProgress(false);
                showEmptyView(true);
                showConnectionError(null);
            }
        } else {
            boolean posted = mEventBus.getStickyEvent(MovieDetailsEvent.class) != null;

            if (!posted) {
                showProgress(false);
                showEmptyView(true);
            }
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_movie_details;
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mShareItem != null) {
            mShareItem.setVisible(mShareTrailer != null);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterEvent();
        mPresenter.clearSubscriptions();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mShareItem = menu.findItem(R.id.action_share);

        if (mShareItem != null) {
            mShareItem.setVisible(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            createShareIntent();
        }
        return true;
    }

    @Override
    public void setPresenter(@NonNull MovieDetailsContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showProgress(boolean visible) {
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
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

        Snackbar snackbar = Snackbar.make(
                getView(),
                !TextUtils.isEmpty(errorMessage) ?
                        errorMessage : getString(R.string.connection_failed_to_network_snackbar),
                Snackbar.LENGTH_INDEFINITE
        );

        if (!snackbar.isShown()) {
            snackbar.show();
        }

        snackbar.setAction(R.string.title_retry_snackbar, v -> {
            showEmptyView(false);
            showProgress(true);
            mPresenter.start();
        });
    }

    @Override
    public void hideSimilarMovieViews() {
        mSimilarMoviesTitle.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideMovieTrailerViews() {
        mTrailerTitle.setVisibility(View.GONE);
        mRecyclerViewTrailers.setVisibility(View.GONE);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NonNull MovieDetailsEvent event) {
        if (mAdapter.getItemCount() > 0 || mRecyclerView.getVisibility() == View.GONE) {
            return;
        }

        List<Result> movies = event.mMovies;
        List<Video> trailers = event.mTrailers;
        mReviews = event.mReviews;
        setCurrentMovie(event.mCurrentMovie);
        showMovieData(getCurrentMovie());

        if (!movies.isEmpty()) {
            mAdapter.addItems(movies);
        } else {
            hideSimilarMovieViews();
        }

        if (!trailers.isEmpty()) {
            mTrailersAdapter.addItems(trailers);
            mShareTrailer = trailers.get(0);

            if (mShareItem != null) {
                mShareItem.setVisible(true);
            }

        } else {
            hideMovieTrailerViews();
        }
    }

    @Override
    public void onItemClick(@NonNull View view, @NonNull Result movie) {
        removeStickEvent();
        goToDetailsActivity(view, movie.getId());
    }

    @Override
    public void onVideoItemClick(@NonNull Video trailer) {
        if (trailer.getSite().equalsIgnoreCase(Video.SITE_YOUTUBE)) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Video.YOUTUBE_URL + trailer.getKey())));
        } else {
            throw new RuntimeException("Unsupported video format!");
        }
    }

    private void showMovieData(@NonNull Result movie) {
        if (getActivity() == null) {
            return;
        }

        ImageView backdrop = getActivity().findViewById(R.id.backdrop);
        final View view = getActivity().getWindow().getDecorView();

        loadImage(getActivity(), !TextUtils.isEmpty(movie.getBackdropPath()) ?
                movie.getBackdropPath() : movie.getPosterAsBackdrop(), ANIM_FADE_DURATION).into(backdrop);

        loadImage(getActivity(), movie.getPosterPath(), 0).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        bindMovieData(movie);
                        return true;
                    }
                });

                return false;
            }
        }).into(mPoster);
    }

    private void setCurrentMovie(@NonNull Result currentMovie) {
        this.mCurrentMovie = currentMovie;
    }

    private Result getCurrentMovie() {
        return mCurrentMovie;
    }

    private void bindMovieData(Result movie) {
        if (getActivity() == null) {
            return;
        }

        mContainer.setVisibility(View.VISIBLE);
        mCollapsingToolbarLayout.setTitleEnabled(true);
        animateContent();
        showProgress(false);
        CompositeDisposable disposable = new CompositeDisposable();
        mTitle.setText(movie.getTitle());

        String text = movie.getReleaseDate() + ", " +
                movie.getRunTimeInHoursAndMinutes() + getString(R.string.text_minutes_abbreviated);

        mYearAndRuntime.setText(text);
        mVoteAverage.setText(String.valueOf(movie.getVoteAverage()));
        mChipGroup.removeAllViews();

        disposable.add(Observable.just(movie.getGenres())
                .flatMap(Observable::fromIterable)
                .subscribe(genre -> {
                    Chip chip = new Chip(getActivity());
                    chip.setText(genre.getName());
                    chip.setTextAppearanceResource(R.style.AppTheme_TextViewChip);
                    chip.setChipBackgroundColorResource(R.color.layoutBackground);
                    chip.setChipStrokeColorResource(R.color.background);
                    chip.setChipStrokeWidthResource(R.dimen.dimen_width_stroke);
                    mChipGroup.addView(chip);
                }, Throwable::printStackTrace, disposable::clear));

        mSynopsis.setText(movie.getOverview());

        if (!mReviews.isEmpty()) {
            mReviewsContainer.removeAllViews();
            int lastItem = (mReviews.size() - 1);
            int lastPosition = 2;
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

            disposable.add(Observable
                    .just(mReviews)
                    .flatMap(Observable::fromIterable)
                    .take(3)
                    .subscribe(review -> {
                        View view = LayoutInflater.from(getActivity())
                                .inflate(R.layout.item_review, mReviewsContainer, false);

                        TextView textAuthor = view.findViewById(R.id.author);
                        TextView textContent = view.findViewById(R.id.content);
                        TextView textMore = view.findViewById(R.id.tvMoreLess);
                        String author = review.getAuthor();
                        String str = getString(R.string.reviewed_by) + author;
                        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(str);

                        ssBuilder.setSpan(boldSpan,
                                str.indexOf(author),
                                str.indexOf(author) + author.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        textAuthor.setText(ssBuilder);
                        textContent.setText(review.getContent());
                        textMore.setOnClickListener(onMoreClickListener(textContent));
                        int index = mReviews.indexOf(review);

                        if (index < lastItem && index < lastPosition) {
                            view.findViewById(R.id.divider).setVisibility(View.VISIBLE);
                        }

                        mReviewsContainer.addView(view);
                    }, Throwable::printStackTrace, disposable::clear));
        } else {
            mReviewsTitle.setVisibility(View.GONE);
            mReviewsContainer.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(movie.getHomepage())) {
            String str = getString(R.string.text_know_more) + movie.getHomepage() + ".";
            SpannableStringBuilder ssBuilder = new SpannableStringBuilder(str);

            ssBuilder.setSpan(new URLSpan(movie.getHomepage()),
                    str.indexOf(movie.getHomepage()),
                    str.indexOf(movie.getHomepage()) + movie.getHomepage().length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mHomepage.setText(ssBuilder);
            mHomepage.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            mHomepage.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener onMoreClickListener(TextView textView) {
        return view -> {
            TextView t = (TextView) view;
            int maxLines = TextViewCompat.getMaxLines(textView);

            if (maxLines == TEXT_MAX_LINES) {
                setupTextView(textView, Integer.MAX_VALUE, null);
                t.setText(R.string.title_less);
            } else {
                setupTextView(textView, TEXT_MAX_LINES, TextUtils.TruncateAt.END);
                t.setText(R.string.title_more);
            }
        };
    }

    private void setupTextView(@NonNull TextView textView, int maxLines, TextUtils.TruncateAt truncateAt) {
        textView.setMaxLines(maxLines);
        textView.setEllipsize(truncateAt);
    }

    private RequestBuilder<Drawable> loadImage(@NonNull Context context, @NonNull String path, int duration) {
        return Glide
                .with(context)
                .load(path)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL).centerCrop())
                .transition(new DrawableTransitionOptions().crossFade(duration));
    }

    private void createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, Video.YOUTUBE_URL + mShareTrailer.getKey());
        startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
    }

    private void goToDetailsActivity(View view, long id) {
        if (getActivity() == null) {
            return;
        }

        getActivity().finish();

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeClipRevealAnimation(view, 0, 0, view.getWidth(), view.getHeight());

        startActivity(new Intent(getActivity(),
                MovieDetailsActivity.class).putExtra(EXTRA_ID, id), options.toBundle());
    }

    private void removeStickEvent() {
        MovieDetailsEvent event = mEventBus.getStickyEvent(MovieDetailsEvent.class);

        if (event != null) {
            unregisterEvent();
            mEventBus.removeStickyEvent(event);
        }
    }

    private void unregisterEvent() {
        mEventBus.unregister(this);
    }

    private void animateContent() {
        View[] animatedViews = new View[] {
                mPoster, mTitle, mYearAndRuntime, mVoteAverage, mHorizontalContainer, mSynopsis, mReviewsTitle,
                mReviewsContainer, mSimilarMoviesTitle, mRecyclerView, mTrailerTitle, mRecyclerViewTrailers, mHomepage
        };

        Interpolator interpolator = new DecelerateInterpolator();

        for (int i = 0; i < animatedViews.length; ++i) {
            View v = animatedViews[i];
            v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            v.setAlpha(0f);
            v.setTranslationY(75);
            v.animate()
                    .setInterpolator(interpolator)
                    .alpha(1.0f)
                    .translationY(0)
                    .setStartDelay(100 + 75 * i)
                    .start();
        }
    }

}
