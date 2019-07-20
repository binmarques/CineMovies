package br.com.binmarques.cinemovies.searchable;

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
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.HttpException;

/**
 * Created By Daniel Marques on 31/08/2018
 */

public class SearchablePresenter implements SearchableContract.Presenter {

    private MoviesServiceApi mServiceApi;
    private SearchableContract.View mSearchableView;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public SearchablePresenter(MoviesServiceApi api, SearchableContract.View view) {
        this.mServiceApi = api;
        this.mSearchableView = view;
        mSearchableView.setPresenter(this);
    }

    @Override
    public void start() {}

    @Override
    public void loadFirstPage(@NonNull String query) {
        Log.i(TAG, "loadNextPage() " + mSearchableView.getCurrentPage());

        mDisposable.add(mServiceApi.findMovies(getParams(query),
        new MoviesServiceApi.MoviesServiceCallback<MoviesWrapper>() {
            @Override
            public void onLoaded(MoviesWrapper moviesWrapper) {
                mSearchableView.showEmptyView(false);
                mSearchableView.showProgress(false);
                mSearchableView.hideConnectionError();

                if (moviesWrapper != null) {
                    if (!moviesWrapper.getResults().isEmpty()) {
                        mSearchableView.addItems(moviesWrapper.getResults());
                    } else {
                        mSearchableView.showEmptyView(true);
                    }

                    if (mSearchableView.getCurrentPage() <= moviesWrapper.getTotalPages()
                            && moviesWrapper.getTotalPages() > 1) {
                        mSearchableView.addLoadingFooter();
                    } else {
                        mSearchableView.setLastPage(true);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mSearchableView.showProgress(false);
                mSearchableView.showEmptyView(true);

                if (e instanceof HttpException) {
                    ApiError error = HandleUnexpectedError.parseError(((HttpException) e).response());

                    if (error != null) {
                        mSearchableView.showConnectionError(error.getStatusMessage());
                    }

                } else {
                    Log.i(TAG, "onError: " + e.getMessage());
                    e.printStackTrace();
                    mSearchableView.showConnectionError(null);
                }
            }
        }));
    }

    @Override
    public void loadNextPage(@NonNull String query) {
        Log.i(TAG, "loadNextPage() " + mSearchableView.getCurrentPage());
        
        mDisposable.add(mServiceApi.findMovies(getParams(query),
        new MoviesServiceApi.MoviesServiceCallback<MoviesWrapper>() {
            @Override
            public void onLoaded(MoviesWrapper moviesWrapper) {
                mSearchableView.showReload(false);
                mSearchableView.removeLoadingFooter();
                mSearchableView.setLoading(false);

                if (moviesWrapper != null) {
                    if (!moviesWrapper.getResults().isEmpty()) {
                        mSearchableView.addItems(moviesWrapper.getResults());
                    }

                    if (mSearchableView.getCurrentPage() != moviesWrapper.getTotalPages()) {
                        mSearchableView.addLoadingFooter();
                    } else {
                        mSearchableView.setLastPage(true);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mSearchableView.setLoading(false);
                mSearchableView.backPreviousPage();

                if (e instanceof HttpException) {
                    ApiError error = HandleUnexpectedError.parseError(((HttpException) e).response());

                    if (error != null) {
                        mSearchableView.setErrorMessage(error.getStatusMessage());
                    }

                } else {
                    Log.i(TAG, "onError: " + e.getMessage());
                    e.printStackTrace();
                    mSearchableView.setErrorMessage(R.string.connection_failed_to_network);
                    mSearchableView.showReload(true);
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
                mSearchableView.getCurrentPage(),
                mSearchableView.isLoadingAdded(),
                mSearchableView.isLastPage(),
                mSearchableView.getMovies())
        );
    }

    private Map<String, String> getParams(String query) {
        Map<String, String> map = new HashMap<>();
        map.put("api_key", BuildConfig.API_KEY);
        map.put("language", BuildConfig.LANGUAGE);
        map.put("query", query);
        map.put("page", String.valueOf(mSearchableView.getCurrentPage()));
        map.put("region", "BR");
        return map;
    }

}
