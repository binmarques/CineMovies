package br.com.binmarques.cinemovies.base;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import br.com.binmarques.cinemovies.R;
import butterknife.ButterKnife;

/**
 * Created By Daniel Marques on 05/08/2018
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        bindViews();

        if (getToolbar() != null) {
            Toolbar toolbar = getToolbar();
            setSupportActionBar(toolbar);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            window.setStatusBarColor(ContextCompat
                    .getColor(this, R.color.colorPrimaryDark));
        }

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected abstract int getLayoutResource();

    protected abstract Toolbar getToolbar();

    protected abstract boolean isDisplayHomeAsUpEnabled();

    private void bindViews() {
        ButterKnife.bind(this);
    }

    public void setToolbarBackground(int color) {
        if (getToolbar() != null) {
            getToolbar().setBackgroundColor(color);
        }
    }

}
