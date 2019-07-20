package br.com.binmarques.cinemovies.movies;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.binmarques.cinemovies.BuildConfig;
import br.com.binmarques.cinemovies.api.HandleUnexpectedError;
import br.com.binmarques.cinemovies.api.MoviesServiceApi;
import br.com.binmarques.cinemovies.events.MoviesEvent;
import br.com.binmarques.cinemovies.model.ApiError;
import br.com.binmarques.cinemovies.model.MoviesWrapper;
import br.com.binmarques.cinemovies.model.Result;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.HttpException;

/**
 * Created By Daniel Marques on 05/08/2018
 */

public class MoviesPresenter implements MoviesContract.Presenter {

    private MoviesServiceApi mServiceApi;
    private MoviesContract.View mMoviesView;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public MoviesPresenter(MoviesServiceApi api, MoviesContract.View view) {
        this.mServiceApi = api;
        this.mMoviesView = view;
        mMoviesView.setPresenter(this);
    }

    @Override
    public void start() {
        loadMovies();
    }

    private void loadMovies() {
        mDisposable.add(mServiceApi.findMovieWrappers(getParams(),
        new MoviesServiceApi.MoviesServiceCallback<List<MoviesWrapper>>() {
            @Override
            public void onLoaded(List<MoviesWrapper> wrappers) {
                mMoviesView.showProgress(false);
                mMoviesView.hideRefreshing();
                mMoviesView.hideConnectionError();
                
                if (wrappers != null && !wrappers.isEmpty()) {
                    mMoviesView.showEmptyView(false);
                    Result featuredMovie = wrappers.get(0).getResults().get(0);
                    EventBus.getDefault().postSticky(new MoviesEvent(wrappers, featuredMovie));
                } else {
                    mMoviesView.showEmptyView(true);
                }
            }

            @Override
            public void onError(Throwable e) {
                mMoviesView.showProgress(false);
                mMoviesView.showEmptyView(true);
                mMoviesView.hideRefreshing();

                if (e instanceof HttpException) {
                    ApiError error = HandleUnexpectedError.parseError(((HttpException) e).response());

                    if (error != null) {
                        mMoviesView.showConnectionError(error.getStatusMessage());
                    }

                } else {
                    Log.i(TAG, "onError: " + e.getMessage());
                    e.printStackTrace();
                    mMoviesView.showConnectionError(null);
                }
            }
        }));
    }

    private Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        map.put("api_key", BuildConfig.API_KEY);
        map.put("language", BuildConfig.LANGUAGE);
        map.put("page", String.valueOf(PAGE));
        return map;
    }

    @Override
    public void clearSubscriptions() {
        if (mDisposable.isDisposed()) {
            mDisposable.clear();
        }
    }
}
