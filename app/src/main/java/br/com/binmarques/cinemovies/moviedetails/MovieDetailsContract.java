package br.com.binmarques.cinemovies.moviedetails;

import br.com.binmarques.cinemovies.base.BasePresenter;
import br.com.binmarques.cinemovies.base.BaseView;

/**
 * Created By Daniel Marques on 09/08/2018
 */

public interface MovieDetailsContract {

    interface View extends BaseView<Presenter> {

        void showProgress(boolean visible);

        void showEmptyView(boolean visible);

        void showConnectionError(String errorMessage);

        void hideSimilarMovieViews();

        void hideMovieTrailerViews();

    }

    interface Presenter extends BasePresenter {

        void clearSubscriptions();

    }

}
