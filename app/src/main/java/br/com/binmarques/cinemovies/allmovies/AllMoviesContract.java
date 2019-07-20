package br.com.binmarques.cinemovies.allmovies;

import androidx.annotation.NonNull;

import java.util.List;

import br.com.binmarques.cinemovies.base.BasePresenter;
import br.com.binmarques.cinemovies.base.BaseView;
import br.com.binmarques.cinemovies.model.Result;
import br.com.binmarques.cinemovies.util.MovieTypes;

/**
 * Created By Daniel Marques on 10/08/2018
 */

public interface AllMoviesContract {

    interface View extends BaseView<Presenter> {

        void addLoadingFooter();

        void removeLoadingFooter();

        void showReload(boolean show);

        void setLastPage(boolean isLastPage);

        void setLoading(boolean isLoading);

        void showEmptyView(boolean visible);

        void showProgress(boolean visible);

        void showConnectionError(String errorMessage);

        int getCurrentPage();

        void backPreviousPage();

        void addItems(@NonNull List<Result> movies);

        List<Result> getMovies();

        void hideConnectionError();

        void hideRefreshing();

        void setErrorMessage(String errorMessage);

        void setErrorMessage(int errorMessage);

        boolean isLoadingAdded();

        boolean isLastPage();

    }

    interface Presenter extends BasePresenter {

        void loadFirstPage(@NonNull MovieTypes type);

        void loadNextPage(@NonNull MovieTypes type);

        void clearSubscriptions();

        void dispatchMessageEvent();

    }
}
