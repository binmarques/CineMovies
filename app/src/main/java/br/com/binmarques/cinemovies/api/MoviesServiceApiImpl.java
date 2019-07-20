package br.com.binmarques.cinemovies.api;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import br.com.binmarques.cinemovies.BuildConfig;
import br.com.binmarques.cinemovies.model.MovieCollection;
import br.com.binmarques.cinemovies.model.MoviesWrapper;
import br.com.binmarques.cinemovies.model.Result;
import br.com.binmarques.cinemovies.model.ReviewsWrapper;
import br.com.binmarques.cinemovies.model.VideosWrapper;
import br.com.binmarques.cinemovies.util.MovieTypes;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created By Daniel Marques on 06/08/2018
 */

public class MoviesServiceApiImpl implements MoviesServiceApi {

    @Override
    public Disposable findMovies(@NonNull Map<String, String> map, @NonNull MovieTypes types,
                                 MoviesServiceCallback<MoviesWrapper> callback) {
        return callServiceFindMovies(map, types).subscribe(callback::onLoaded, callback::onError);
    }

    @Override
    public Disposable findMovies(@NonNull Map<String, String> map, MoviesServiceCallback<MoviesWrapper> callback) {
        return callServiceFindMovies(map).subscribe(callback::onLoaded, callback::onError);
    }

    @Override
    public Disposable findMovieWrappers(@NonNull Map<String, String> map, MoviesServiceCallback<List<MoviesWrapper>> callback) {
        Observable<List<MoviesWrapper>> combined =
                Observable.zip(
                        callServiceFindMovies(map, MovieTypes.THEATERS),
                        callServiceFindMovies(map, MovieTypes.POPULAR),
                        callServiceFindMovies(map, MovieTypes.TOP_RATED),
                        callServiceFindMovies(map, MovieTypes.UPCOMING),
                        (wrapperOne, wrapperTwo, wrapperThree, wrapperFour) ->
                                Arrays.asList(wrapperOne, wrapperTwo, wrapperThree, wrapperFour)
                );

        return combined.subscribe(callback::onLoaded, callback::onError);
    }

    @Override
    public Disposable findMovieAndSimilarMovies(long movieId, int page, MoviesServiceCallback<MovieCollection> callback) {
        Observable<MovieCollection> combined =
                Observable.zip(
                        callServiceFindMovie(movieId),
                        callServiceFindSimilarMovies(movieId, page),
                        callServiceFindTrailers(movieId),
                        callServiceFindReviews(movieId, page),
                        MovieCollection::new
                );

        return combined.subscribe(callback::onLoaded, callback::onError);
    }

    private Observable<MoviesWrapper> callServiceFindMovies(Map<String, String> map, MovieTypes types) {
        Observable<MoviesWrapper> observable = null;

        switch (types) {
            case THEATERS:
                observable = ServiceGenerator
                        .createService(MoviesService.class)
                        .findMoviesThatAreInTheaters(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                break;

            case POPULAR:
                observable = ServiceGenerator
                        .createService(MoviesService.class)
                        .findPopularMovies(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                break;

            case TOP_RATED:
                observable = ServiceGenerator
                        .createService(MoviesService.class)
                        .findTopRatedMovies(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                break;

            case UPCOMING:
                observable = ServiceGenerator
                        .createService(MoviesService.class)
                        .findUpComingMovies(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                break;
        }

        return observable;
    }

    private Observable<MoviesWrapper> callServiceFindMovies(Map<String, String> map) {
        return ServiceGenerator
                .createService(MoviesService.class)
                .findMovies(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Result> callServiceFindMovie(long movieId) {
        return ServiceGenerator
                .createService(MoviesService.class)
                .findMovie(movieId, BuildConfig.API_KEY, BuildConfig.LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<MoviesWrapper> callServiceFindSimilarMovies(long movieId, int page) {
        return ServiceGenerator
                .createService(MoviesService.class)
                .findSimilarMovies(movieId, BuildConfig.API_KEY, BuildConfig.LANGUAGE, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<VideosWrapper> callServiceFindTrailers(long movieId) {
        return ServiceGenerator
                .createService(MoviesService.class)
                .findMovieTrailers(movieId, BuildConfig.API_KEY, BuildConfig.LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<ReviewsWrapper> callServiceFindReviews(long movieId, int page) {
        return ServiceGenerator
                .createService(MoviesService.class)
                .findReviews(movieId, BuildConfig.API_KEY, BuildConfig.LANGUAGE, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
