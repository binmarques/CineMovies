package br.com.binmarques.cinemovies.model;

public class MovieCollection {

    private Result movie;

    private MoviesWrapper moviesWrapper;

    private VideosWrapper videosWrapper;

    private ReviewsWrapper reviewsWrapper;

    public MovieCollection(Result movie, MoviesWrapper moviesWrapper,
                           VideosWrapper videosWrapper, ReviewsWrapper reviewsWrapper) {
        this.movie = movie;
        this.moviesWrapper = moviesWrapper;
        this.videosWrapper = videosWrapper;
        this.reviewsWrapper = reviewsWrapper;
    }

    public Result getMovie() {
        return movie;
    }

    public MoviesWrapper getMoviesWrapper() {
        return moviesWrapper;
    }

    public VideosWrapper getVideosWrapper() {
        return videosWrapper;
    }

    public ReviewsWrapper getReviewsWrapper() {
        return reviewsWrapper;
    }
}
