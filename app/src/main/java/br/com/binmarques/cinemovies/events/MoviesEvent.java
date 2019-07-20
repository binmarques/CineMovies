package br.com.binmarques.cinemovies.events;

import java.util.List;

import br.com.binmarques.cinemovies.model.MoviesWrapper;
import br.com.binmarques.cinemovies.model.Result;

/**
 * Created By Daniel Marques on 14/08/2018
 */

public class MoviesEvent {

    public final List<MoviesWrapper> mItems;

    public final Result mFeaturedMovie;

    public MoviesEvent(List<MoviesWrapper> mItems, Result featuredMovie) {
        this.mItems = mItems;
        this.mFeaturedMovie = featuredMovie;
    }

}
