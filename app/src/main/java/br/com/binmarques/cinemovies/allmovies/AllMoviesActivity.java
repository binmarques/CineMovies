package br.com.binmarques.cinemovies.allmovies;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import br.com.binmarques.cinemovies.R;
import br.com.binmarques.cinemovies.api.MoviesServiceApiImpl;
import br.com.binmarques.cinemovies.base.BaseActivity;
import br.com.binmarques.cinemovies.movies.MoviesFragment;
import br.com.binmarques.cinemovies.searchable.SearchableActivity;
import br.com.binmarques.cinemovies.util.ActivityUtils;
import butterknife.BindView;

/**
 * Created By Daniel Marques on 10/08/2018
 */

public class AllMoviesActivity extends BaseActivity {

    @BindView(R.id.toolbar) protected Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AllMoviesFragment fragment =
                (AllMoviesFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (fragment == null) {
            fragment = AllMoviesFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentFrame);
        }

        int tag = getIntent().getIntExtra(MoviesFragment.EXTRA_MOVIE_TYPE, -1);
        Bundle args = new Bundle();
        args.putInt(MoviesFragment.EXTRA_MOVIE_TYPE, tag);
        fragment.setArguments(args);

        new AllMoviesPresenter(new MoviesServiceApiImpl(), fragment);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_all_movies;
    }

    @Override
    protected Toolbar getToolbar() {
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        return mToolbar;
    }

    @Override
    protected boolean isDisplayHomeAsUpEnabled() {
        return true;
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
}
