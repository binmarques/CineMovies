package br.com.binmarques.cinemovies.movies;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import br.com.binmarques.cinemovies.R;
import br.com.binmarques.cinemovies.api.MoviesServiceApiImpl;
import br.com.binmarques.cinemovies.base.BaseActivity;
import br.com.binmarques.cinemovies.searchable.SearchableActivity;
import br.com.binmarques.cinemovies.util.ActivityUtils;
import butterknife.BindView;

/**
 * Created By Daniel Marques on 05/08/2018
 */

public class MoviesActivity extends BaseActivity {

    @BindView(R.id.toolbar) protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.updateAndroidSecurityProvider(this);

        MoviesFragment fragment =
                (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (fragment == null) {
            fragment = MoviesFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentFrame);
        }

        new MoviesPresenter(new MoviesServiceApiImpl(), fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_movie, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            final Intent intent = new Intent(this, SearchableActivity.class);
            intent.setAction(Intent.ACTION_SEARCH);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_movies;
    }

    @Override
    protected Toolbar getToolbar() {
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        return mToolbar;
    }

    @Override
    protected boolean isDisplayHomeAsUpEnabled() {
        return false;
    }
}
