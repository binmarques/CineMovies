package br.com.binmarques.cinemovies.searchable;

import androidx.annotation.NonNull;

import java.util.List;

import br.com.binmarques.cinemovies.base.BasePresenter;
import br.com.binmarques.cinemovies.base.BaseView;
import br.com.binmarques.cinemovies.model.Result;

/**
 * Created By Daniel Marques on 31/08/2018
 */

public interface SearchableContract {

    interface View extends BaseView<Presenter> {

        void addLoadingFooter();

        void removeLoadingFooter();

        void showReload(boolean show);

        void setLastPage(boolean isLastPage);

        void setLoading(boolean isLoading);

        void showEmptyView(boolean visible);

        void showProgress(boolean visible);

        void showConnectionError(String errorMessage);

        void setErrorMessage(String errorMessage);

        void setErrorMessage(int errorMessage);

        int getCurrentPage();

        void backPreviousPage();

        void addItems(@NonNull List<Result> movies);

        void hideConnectionError();

        boolean isLoadingAdded();

        boolean isLastPage();

        List<Result> getMovies();

    }

    interface Presenter extends BasePresenter {

        void loadFirstPage(@NonNull String query);

        void loadNextPage(@NonNull String query);

        void clearSubscriptions();

        void dispatchMessageEvent();

    }

}
