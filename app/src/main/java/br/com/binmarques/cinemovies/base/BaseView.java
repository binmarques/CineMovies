package br.com.binmarques.cinemovies.base;

import androidx.annotation.NonNull;

/**
 * Created By Daniel Marques on 05/08/2018
 */

public interface BaseView<T> {

    void setPresenter(@NonNull T presenter);

}
