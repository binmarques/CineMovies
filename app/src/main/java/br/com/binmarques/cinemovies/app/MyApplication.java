package br.com.binmarques.cinemovies.app;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Created By Daniel Marques on 08/08/2018
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

}
