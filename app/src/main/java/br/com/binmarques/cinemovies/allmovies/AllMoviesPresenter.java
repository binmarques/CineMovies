package br.com.binmarques.cinemovies.allmovies;

import android.util.Log;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import br.com.binmarques.cinemovies.BuildConfig;
import br.com.binmarques.cinemovies.R;
import br.com.binmarques.cinemovies.api.HandleUnexpectedError;
import br.com.binmarques.cinemovies.api.MoviesServiceApi;
import br.com.binmarques.cinemovies.events.AllMoviesEvent;
import br.com.binmarques.cinemovies.model.ApiError;
import br.com.binmarques.cinemovies.model.MoviesWrapper;
import br.com.binmarques.cinemovies.util.MovieTypes;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.HttpException;

/**
 * Created By Daniel Marques on 10/08/2018
 */

public class AllMoviesPresenter implements AllMoviesContract.Presenter {

    private MoviesServiceApi mServiceApi;
    private AllMoviesContract.View mAllMoviesView;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public AllMoviesPresenter(MoviesServiceApi api, AllMoviesContract.View view) {
        this.mServiceApi = api;
        this.mAllMoviesView = view;
        mAllMoviesView.setPresenter(this);
    }

    @Override
    public void start() {}

    @Override
    public void loadNextPage(@NonNull MovieTypes type) {
        Log.i(TAG, "loadNextPage() " + mAllMoviesView.getCurrentPage());

        mDisposable.add(mServiceApi.findMovies(getParams(), type,
        new MoviesServiceApi.MoviesServiceCallback<MoviesWrapper>() {
            @Override
            public void onLoaded(MoviesWrapper moviesWrapper) {
                mAllMoviesView.showReload(false);
                mAllMoviesView.removeLoadingFooter();
                mAllMoviesView.setLoading(false);

                if (moviesWrapper != null) {
                    if (!moviesWrapper.getResults().isEmpty()) {
                        mAllMoviesView.addItems(moviesWrapper.getResults());
                    }

                    if (mAllMoviesView.getCurrentPage() != moviesWrapper.getTotalPages()) {
                        mAllMoviesView.addLoadingFooter();
                    } else {
                        mAllMoviesView.setLastPage(true);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mAllMoviesView.setLoading(false);
                mAllMoviesView.backPreviousPage();

                if (e instanceof HttpException) {
                    ApiError error = HandleUnexpectedError.parseError(((HttpException) e).response());

                    if (error != null) {
                        mAllMoviesView.setErrorMessage(error.getStatusMessage());
                    }

                } else {
                    Log.i(TAG, "onError: " + e.getMessage());
                    e.printStackTrace();
                    mAllMoviesView.setErrorMessage(R.string.connection_failed_to_network);
                    mAllMoviesView.showReload(true);
                }
            }
        }));
    }

    @Override
    public void clearSubscriptions() {
        if (mDisposable.isDisposed()) {
            mDisposable.clear();
        }
    }

    @Override
    public void dispatchMessageEvent() {
        EventBus.getDefault().postSticky(new AllMoviesEvent(
                mAllMoviesView.getCurrentPage(),
                mAllMoviesView.isLoadingAdded(),
                mAllMoviesView.isLastPage(),
                mAllMoviesView.getMovies())
        );
    }

    @Override
    public void loadFirstPage(@NonNull MovieTypes type) {
        Log.i(TAG, "loadFirstPage() ");

        mDisposable.add(mServiceApi.findMovies(getParams(), type,
        new MoviesServiceApi.MoviesServiceCallback<MoviesWrapper>() {
            @Override
            public void onLoaded(MoviesWrapper moviesWrapper) {
                mAllMoviesView.showEmptyView(false);
                mAllMoviesView.showProgress(false);
                mAllMoviesView.hideRefreshing();
                mAllMoviesView.hideConnectionError();

                if (moviesWrapper != null) {
                    if (!moviesWrapper.getResults().isEmpty()) {
                        mAllMoviesView.addItems(moviesWrapper.getResults());
                    } else {
                        mAllMoviesView.showEmptyView(true);
                    }

                    if (mAllMoviesView.getCurrentPage() <= moviesWrapper.getTotalPages()) {
                        mAllMoviesView.addLoadingFooter();
                    } else {
                        mAllMoviesView.setLastPage(true);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mAllMoviesView.showProgress(false);
                mAllMoviesView.showEmptyView(true);
                mAllMoviesView.hideRefreshing();

                if (e instanceof HttpException) {
                    ApiError error = HandleUnexpectedError.parseError(((HttpException) e).response());

                    if (error != null) {
                        mAllMoviesView.showConnectionError(error.getStatusMessage());
                    }

                } else {
                    Log.i(TAG, "onError: " + e.getMessage());
                    e.printStackTrace();
                    mAllMoviesView.showConnectionError(null);
                }
            }
        }));
    }

    private Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        map.put("api_key", BuildConfig.API_KEY);
        map.put("language", BuildConfig.LANGUAGE);
        map.put("page", String.valueOf(mAllMoviesView.getCurrentPage()));
        map.put("region", "US");
        return map;
    }
}
