package br.com.binmarques.cinemovies.moviedetails;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import br.com.binmarques.cinemovies.R;
import br.com.binmarques.cinemovies.api.MoviesServiceApiImpl;
import br.com.binmarques.cinemovies.base.BaseActivity;
import br.com.binmarques.cinemovies.base.BaseFragment;
import br.com.binmarques.cinemovies.searchable.SearchableActivity;
import br.com.binmarques.cinemovies.util.ActivityUtils;
import butterknife.BindView;

/**
 * Created By Daniel Marques on 09/08/2018
 */

public class MovieDetailsActivity extends BaseActivity {

    @BindView(R.id.toolbar) protected Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarBackground(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        MovieDetailsFragment fragment =
                (MovieDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (fragment == null) {
            fragment = MovieDetailsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentFrame);
        }

        long movieId = getIntent().getLongExtra(BaseFragment.EXTRA_ID, 0);
        new MovieDetailsPresenter(new MoviesServiceApiImpl(), fragment, movieId);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_movie_details;
    }

    @Override
    protected Toolbar getToolbar() {
        mToolbar.setTitleTextColor(Color.TRANSPARENT);
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
        } else if (item.getItemId() == android.R.id.home) {
            onSupportNavigateUp();
        }

        return super.onOptionsItemSelected(item);
    }
}
