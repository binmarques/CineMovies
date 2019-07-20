package br.com.binmarques.cinemovies.api;

import java.util.Map;

import br.com.binmarques.cinemovies.model.MoviesWrapper;
import br.com.binmarques.cinemovies.model.Result;
import br.com.binmarques.cinemovies.model.ReviewsWrapper;
import br.com.binmarques.cinemovies.model.VideosWrapper;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created By Daniel Marques on 06/08/2018
 */

public interface MoviesService {

    @GET("movie/now_playing")
    Observable<MoviesWrapper> findMoviesThatAreInTheaters(@QueryMap Map<String, String> map);

    @GET("movie/popular")
    Observable<MoviesWrapper> findPopularMovies(@QueryMap Map<String, String> map);

    @GET("movie/top_rated")
    Observable<MoviesWrapper> findTopRatedMovies(@QueryMap Map<String, String> map);

    @GET("movie/upcoming")
    Observable<MoviesWrapper> findUpComingMovies(@QueryMap Map<String, String> map);

    @GET("movie/{movie_id}")
    Observable<Result> findMovie(@Path("movie_id") long movieId,
                                 @Query("api_key") String apiKey,
                                 @Query("language") String language);

    @GET("movie/{movie_id}/similar")
    Observable<MoviesWrapper> findSimilarMovies(@Path("movie_id") long movieId,
                                                @Query("api_key") String apiKey,
                                                @Query("language") String language,
                                                @Query("page") int page);

    @GET("search/movie")
    Observable<MoviesWrapper> findMovies(@QueryMap Map<String, String> map);

    @GET("movie/{movie_id}/videos")
    Observable<VideosWrapper> findMovieTrailers(@Path("movie_id") long movieId,
                                                @Query("api_key") String apiKey,
                                                @Query("language") String language);

    @GET("movie/{movie_id}/reviews")
    Observable<ReviewsWrapper> findReviews(@Path("movie_id") long movieId,
                                           @Query("api_key") String apiKey,
                                           @Query("language") String language,
                                           @Query("page") int page);
}
