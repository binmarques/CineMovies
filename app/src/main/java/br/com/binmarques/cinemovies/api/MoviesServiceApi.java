package br.com.binmarques.cinemovies.api;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

import br.com.binmarques.cinemovies.model.MovieCollection;
import br.com.binmarques.cinemovies.model.MoviesWrapper;
import br.com.binmarques.cinemovies.util.MovieTypes;
import io.reactivex.disposables.Disposable;

/**
 * Created By Daniel Marques on 06/08/2018
 */

public interface MoviesServiceApi {

    interface MoviesServiceCallback<T> {

        void onLoaded(T t);

        void onError(Throwable e);

    }

    Disposable findMovies(@NonNull Map<String, String> map, @NonNull MovieTypes types, MoviesServiceCallback<MoviesWrapper> callback);

    Disposable findMovies(@NonNull Map<String, String> map, MoviesServiceCallback<MoviesWrapper> callback);

    Disposable findMovieWrappers(@NonNull Map<String, String> map, MoviesServiceCallback<List<MoviesWrapper>> callback);

    Disposable findMovieAndSimilarMovies(long movieId, int page, MoviesServiceCallback<MovieCollection> callback);

}
