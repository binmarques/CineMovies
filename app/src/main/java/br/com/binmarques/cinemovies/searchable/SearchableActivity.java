package br.com.binmarques.cinemovies.searchable;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import br.com.binmarques.cinemovies.R;
import br.com.binmarques.cinemovies.allmovies.AllMoviesAdapter;
import br.com.binmarques.cinemovies.api.MoviesServiceApiImpl;
import br.com.binmarques.cinemovies.base.BaseActivity;
import br.com.binmarques.cinemovies.events.AllMoviesEvent;
import br.com.binmarques.cinemovies.events.MovieDetailsEvent;
import br.com.binmarques.cinemovies.listener.EndlessScrollListener;
import br.com.binmarques.cinemovies.listener.RecyclerViewOnItemClickListener;
import br.com.binmarques.cinemovies.listener.ReloadPageCallback;
import br.com.binmarques.cinemovies.model.Result;
import br.com.binmarques.cinemovies.moviedetails.MovieDetailsActivity;
import butterknife.BindView;

/**
 * Created By Daniel Marques on 31/08/2018
 */

public class SearchableActivity extends BaseActivity implements SearchableContract.View,
                                                                ReloadPageCallback,
                                                                RecyclerViewOnItemClickListener {

    @BindView(R.id.toolbar) protected Toolbar mToolbar;
    @BindView(R.id.recyclerViewSearch) protected RecyclerView mRecyclerView;
    @BindView(R.id.loadingProgress) protected ProgressBar mProgressBar;
    @BindView(R.id.tvEmptyView) protected TextView mEmptyView;
    @BindView(R.id.mainContent) protected CoordinatorLayout mContainer;
    @BindView(R.id.search) protected SearchView mSearchView;
    private ImageView mSearchClose;

    private SearchableContract.Presenter mPresenter;
    private AutoCompleteTextView mSearchAutoCompleteTextView;
    private AllMoviesAdapter mAdapter;
    private boolean mIsLoading = false;
    private boolean mIsLastPage = false;
    private Snackbar mSnackbar;
    private EventBus mEventBus;
    private static Toast sToast;

    private static final int PAGE_START = 1;
    private int mCurrentPage = PAGE_START;
    public static final String EXTRA_ID = "EXTRA_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overrideTransition();
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchView.setQueryHint(getString(R.string.search_hint));
        setupSearchView();
        mEventBus = EventBus.getDefault();
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new AllMoviesAdapter(this, new ArrayList<>(), this);
        mAdapter.setRecyclerViewOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            boolean posted = mEventBus.getStickyEvent(AllMoviesEvent.class) != null;

            if (!posted) {
                showEmptyView(true);
            }
        }

        mRecyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                if (isNetworkAvailable()) {
                    setLoading(true);
                    mCurrentPage++;
                    mPresenter.loadNextPage(mSearchView.getQuery().toString());
                } else if (mAdapter.isNotReloading()) {
                    showReload(true);
                }
            }

            @Override
            public boolean isLastPage() {
                return mIsLastPage;
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }
        });

        mPresenter = new SearchablePresenter(new MoviesServiceApiImpl(), this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_searchable;
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected boolean isDisplayHomeAsUpEnabled() {
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mEventBus.unregister(this);
        mPresenter.clearSubscriptions();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            removeStickEvent();
        } else if (mAdapter.getItemCount() > 0) {
            mPresenter.dispatchMessageEvent();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_clear_history) {
            clearHistory();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handlerSearch(intent);
    }

    @Override
    public void setPresenter(@NonNull SearchableContract.Presenter presenter) {}

    @Override
    public void addLoadingFooter() {
        mAdapter.addLoadingFooter();
    }

    @Override
    public void removeLoadingFooter() {
        mAdapter.removeLoadingFooter();
    }

    @Override
    public void showReload(boolean show) {
        mRecyclerView.post(() -> mAdapter.showReload(show));
    }

    @Override
    public void setLastPage(boolean isLastPage) {
        this.mIsLastPage = isLastPage;
    }

    @Override
    public void setLoading(boolean isLoading) {
        this.mIsLoading = isLoading;
    }

    @Override
    public void showEmptyView(boolean visible) {
        mEmptyView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showProgress(boolean visible) {
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showConnectionError(String errorMessage) {
        mSnackbar = Snackbar.make(
                mContainer
                , !TextUtils.isEmpty(errorMessage)
                        ? errorMessage
                        : getString(R.string.connection_failed_to_network_snackbar)
                , Snackbar.LENGTH_INDEFINITE
        );

        if (!mSnackbar.isShown()) {
            mSnackbar.show();
        }

        mSnackbar.setAction(R.string.title_retry_snackbar, v -> {
            showEmptyView(false);

            if (mAdapter.getItemCount() <= 0) {
                showProgress(true);
                mPresenter.loadFirstPage(mSearchView.getQuery().toString());
            }
        });
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        String message = getString(R.string.title_fail) + errorMessage;
        showMessage(message);
    }

    @Override
    public void setErrorMessage(int errorMessage) {
        showMessage(getString(errorMessage));
    }

    @Override
    public int getCurrentPage() {
        return mCurrentPage;
    }

    @Override
    public void backPreviousPage() {
        mCurrentPage--;
    }

    @Override
    public void addItems(@NonNull List<Result> movies) {
        mAdapter.addItems(movies);
    }

    @Override
    public void hideConnectionError() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
    }

    @Override
    public boolean isLoadingAdded() {
        return mAdapter.isLoadingAdded();
    }

    @Override
    public boolean isLastPage() {
        return mIsLastPage;
    }

    @Override
    public List<Result> getMovies() {
        return mAdapter.getMovies();
    }

    @Override
    public void onReloadPage() {
        mCurrentPage++;
        mPresenter.loadNextPage(mSearchView.getQuery().toString());
    }

    @Override
    public void onItemClick(@NonNull View view, @NonNull Result movie) {
        goToDetailsActivity(view, movie.getId());
    }

    @Override
    public void onItemClickView(@NonNull View view, int position) {}

    @Override
    public void onItemLongPressClick(@NonNull View view, int position) {}

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NonNull AllMoviesEvent event) {
        if (mAdapter.getItemCount() > 0) {
            return;
        }

        showProgress(false);
        addItems(event.mItems);
        showReload(false);
        setLoading(false);
        mCurrentPage = event.mCurrentPage;
        mIsLastPage = event.mIsLastPage;

        if (event.mShouldRemoveLoadingFooter && !mIsLastPage) {
            removeLoadingFooter();
            addLoadingFooter();
        }
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.requestFocus();

        ImageView searchIcon = mSearchView
                .findViewById(androidx.appcompat.R.id.search_mag_icon);

        searchIcon.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        mSearchClose = mSearchView
                .findViewById(androidx.appcompat.R.id.search_close_btn);

        mSearchClose.setOnClickListener(v -> {
            mSearchAutoCompleteTextView.getText().clear();
            setLastPage(false);
            mCurrentPage = PAGE_START;
            mAdapter.clearItems();
            removeStickEvent();
            showKeyboard(mSearchAutoCompleteTextView);
        });

        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int margin = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());

        params.setMargins(margin, margin, margin, margin);
        params.gravity = (Gravity.END | Gravity.CENTER_VERTICAL);
        mSearchClose.setLayoutParams(params);

        View searchPlate = mSearchView
                .findViewById(androidx.appcompat.R.id.search_plate);

        searchPlate.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        mSearchAutoCompleteTextView =
                mSearchView.findViewById(androidx.appcompat.R.id.search_src_text);

        mSearchAutoCompleteTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        mSearchAutoCompleteTextView.setHintTextColor(ContextCompat.getColor(this, R.color.background));
        mSearchAutoCompleteTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        mSearchAutoCompleteTextView.setDropDownBackgroundResource(android.R.color.transparent);
        final View dropDownAnchor = mSearchView.findViewById(mSearchAutoCompleteTextView.getDropDownAnchor());

        if (dropDownAnchor != null) {
            dropDownAnchor.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                Rect screenSize = new Rect();
                getWindowManager().getDefaultDisplay().getRectSize(screenSize);
                mSearchAutoCompleteTextView.setDropDownWidth(screenSize.width());
                mSearchAutoCompleteTextView.setDropDownVerticalOffset(14);
            });
        }

        mSearchAutoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (mSnackbar != null) {
                mSnackbar.dismiss();
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchManager != null) {
            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            mSearchView.setSearchableInfo(info);
        }

        mSearchView.setOnQueryTextListener(onQuerySearchView);
    }

    private SearchView.OnQueryTextListener onQuerySearchView = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText.isEmpty()) {
                filterMovies(newText);
                showEmptyView(false);
            }

            return false;
        }
    };

    private void filterMovies(String query) {
        mSearchClose.performClick();

        if (query.isEmpty()) {
            mSearchAutoCompleteTextView.setDropDownHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            return;
        }

        int duration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        new Handler().postDelayed(() -> {
            showProgress(true);
            mPresenter.loadFirstPage(query);
            mSearchAutoCompleteTextView.setDropDownHeight(0);
        }, duration);
    }

    private void handlerSearch(Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SEARCH)) {
                String query = intent.getStringExtra(SearchManager.QUERY);

                if (query == null) {
                    return;
                }

                mToolbar.setTitle("");
                filterMovies(query);

                SearchRecentSuggestions searchRecentSuggestions =
                        new SearchRecentSuggestions(
                                this,
                                SearchableProvider.AUTHORITY,
                                SearchableProvider.MODE
                        );

                searchRecentSuggestions.saveRecentQuery(query, null);

                if (!TextUtils.isEmpty(query)) {
                    mSearchAutoCompleteTextView.setText(query);
                    mSearchAutoCompleteTextView.setSelection(query.length());
                    mSearchAutoCompleteTextView.dismissDropDown();
                    mSearchView.clearFocus();
                }
            }
        }
    }

    private void clearHistory() {
        MaterialAlertDialogBuilder builder =
                new MaterialAlertDialogBuilder(this, R.style.AppTheme_AlertDialogCustom);

        builder.setMessage(R.string.text_clear_history);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            SearchRecentSuggestions searchRecentSuggestions =
                    new SearchRecentSuggestions(
                            SearchableActivity.this,
                            SearchableProvider.AUTHORITY,
                            SearchableProvider.MODE
                    );

            searchRecentSuggestions.clearHistory();
        });

        builder.setNegativeButton(R.string.text_cancel, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void goToDetailsActivity(View view, long id) {
        MovieDetailsEvent event = mEventBus.getStickyEvent(MovieDetailsEvent.class);

        if (event != null) {
            mEventBus.removeStickyEvent(event);
        }

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeClipRevealAnimation(view, 0, 0, view.getWidth(), view.getHeight());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(new Intent(this,
                    MovieDetailsActivity.class).putExtra(EXTRA_ID, id), options.toBundle());
        } else {
            startActivity(new Intent(this,
                    MovieDetailsActivity.class).putExtra(EXTRA_ID, id));
        }
    }

    public void showMessage(String message) {
        if (sToast != null) {
            sToast.cancel();
        }

        sToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        sToast.show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if (currentNetworkInfo != null && currentNetworkInfo.isAvailable()) {
                return currentNetworkInfo.isConnected();
            }
        }

        return false;
    }

    public void showKeyboard(EditText editText) {
        if (!editText.hasFocus()) {
            editText.requestFocus();

            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        }
    }

    private void overrideTransition() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void removeStickEvent() {
        AllMoviesEvent event = mEventBus.getStickyEvent(AllMoviesEvent.class);

        if (event != null) {
            mEventBus.removeStickyEvent(event);
        }
    }

}
