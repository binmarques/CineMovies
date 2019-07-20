package br.com.binmarques.cinemovies.events;

import java.util.List;

import br.com.binmarques.cinemovies.model.Result;
import br.com.binmarques.cinemovies.model.Review;
import br.com.binmarques.cinemovies.model.Video;

/**
 * Created By Daniel Marques on 20/08/2018
 */

public class MovieDetailsEvent {

    public final List<Result> mMovies;

    public final Result mCurrentMovie;

    public final List<Video> mTrailers;

    public final List<Review> mReviews;

    public MovieDetailsEvent(List<Result> movies, Result currentMovie, List<Video> trailers, List<Review> reviews) {
        this.mMovies = movies;
        this.mCurrentMovie = currentMovie;
        this.mTrailers = trailers;
        this.mReviews = reviews;
    }

}
