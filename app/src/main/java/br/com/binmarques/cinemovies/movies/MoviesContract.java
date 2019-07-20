package br.com.binmarques.cinemovies.movies;

import br.com.binmarques.cinemovies.base.BasePresenter;
import br.com.binmarques.cinemovies.base.BaseView;

/**
 * Created By Daniel Marques on 05/08/2018
 */

public interface MoviesContract {

    interface View extends BaseView<Presenter> {

        void showProgress(boolean visible);

        void showEmptyView(boolean visible);

        void showConnectionError(String errorMessage);

        void hideRefreshing();

        void hideConnectionError();

    }

    interface Presenter extends BasePresenter {

        void clearSubscriptions();

    }
}
