package br.com.binmarques.cinemovies.moviedetails;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import br.com.binmarques.cinemovies.api.HandleUnexpectedError;
import br.com.binmarques.cinemovies.api.MoviesServiceApi;
import br.com.binmarques.cinemovies.events.MovieDetailsEvent;
import br.com.binmarques.cinemovies.model.ApiError;
import br.com.binmarques.cinemovies.model.MovieCollection;
import br.com.binmarques.cinemovies.model.Result;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.HttpException;

/**
 * Created By Daniel Marques on 09/08/2018
 */

public class MovieDetailsPresenter implements MovieDetailsContract.Presenter {

    private MovieDetailsContract.View mMovieDetailsView;
    private MoviesServiceApi mServiceApi;
    private long mMovieId;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public MovieDetailsPresenter(MoviesServiceApi api, MovieDetailsContract.View view, long movieId) {
        this.mMovieDetailsView = view;
        this.mServiceApi = api;
        this.mMovieId = movieId;
        mMovieDetailsView.setPresenter(this);
    }

    @Override
    public void start() {
        loadMovieData();
    }

    private void loadMovieData() {
        mDisposable.add(mServiceApi.findMovieAndSimilarMovies(mMovieId, PAGE,
        new MoviesServiceApi.MoviesServiceCallback<MovieCollection>() {
            @Override
            public void onLoaded(MovieCollection collection) {
                mMovieDetailsView.showEmptyView(false);

                Result currentMovie = collection.getMovie();
                EventBus.getDefault().postSticky(
                        new MovieDetailsEvent(
                                collection.getMoviesWrapper().getResults(),
                                currentMovie,
                                collection.getVideosWrapper().getResults(),
                                collection.getReviewsWrapper().getResults()
                        )
                );
            }

            @Override
            public void onError(Throwable e) {
                mMovieDetailsView.showProgress(false);
                mMovieDetailsView.showEmptyView(true);

                if (e instanceof HttpException) {
                    ApiError error = HandleUnexpectedError.parseError(((HttpException) e).response());

                    if (error != null) {
                        if (error.getStatusCode() == HandleUnexpectedError.RESOURCE_NOT_FOUND) {
                            mMovieDetailsView.hideSimilarMovieViews();
                            mMovieDetailsView.hideMovieTrailerViews();
                        } else {
                            mMovieDetailsView.showConnectionError(error.getStatusMessage());
                        }
                    }

                } else {
                    Log.i(TAG, "onError: " + e.getMessage());
                    e.printStackTrace();
                    mMovieDetailsView.showConnectionError(null);
                }
            }
        }));
    }

    @Override
    public void clearSubscriptions() {
        if (mDisposable.isDisposed()) {
            mDisposable.clear();
        }
    }

}
