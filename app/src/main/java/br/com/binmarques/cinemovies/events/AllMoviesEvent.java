package br.com.binmarques.cinemovies.events;

import java.util.List;

import br.com.binmarques.cinemovies.model.Result;

/**
 * Created By Daniel Marques on 14/08/2018
 */

public class AllMoviesEvent {

    public final int mCurrentPage;

    public final boolean mShouldRemoveLoadingFooter;

    public final boolean mIsLastPage;

    public final List<Result> mItems;

    public AllMoviesEvent(int currentPage, boolean shouldRemoveLoadingFooter, boolean isLastPage, List<Result> items) {
        this.mCurrentPage = currentPage;
        this.mShouldRemoveLoadingFooter = shouldRemoveLoadingFooter;
        this.mIsLastPage = isLastPage;
        this.mItems = items;
    }

}
