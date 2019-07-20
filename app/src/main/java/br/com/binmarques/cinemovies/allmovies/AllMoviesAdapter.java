package br.com.binmarques.cinemovies.allmovies;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import br.com.binmarques.cinemovies.R;
import br.com.binmarques.cinemovies.listener.RecyclerViewOnItemClickListener;
import br.com.binmarques.cinemovies.listener.ReloadPageCallback;
import br.com.binmarques.cinemovies.model.Result;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created By Daniel Marques on 10/08/2018
 */

public class AllMoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Result> mMovies;
    private boolean mIsLoadingAddedItem = false;
    private boolean mReloading = false;
    private ReloadPageCallback mCallback;
    private RecyclerViewOnItemClickListener mListener;

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    public AllMoviesAdapter(Context context, List<Result> movies, ReloadPageCallback callback) {
        this.mContext = context;
        this.mMovies = movies;
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM) {
            View rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_all_movies, parent, false);

            return new ViewHolder(rootView);

        } else {
            View rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);

            return new LoadingHolder(rootView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM) {
            bindMovieViewHolder((ViewHolder) holder, position);
        } else if (getItemViewType(position) == LOADING) {
            bindLoadingHolder((LoadingHolder) holder);
        }
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mMovies.size() - 1 && mIsLoadingAddedItem) ? LOADING : ITEM;
    }

    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener listener) {
        this.mListener = listener;
    }

    private void addItem(Result movie) {
        mMovies.add(movie);
        notifyItemInserted(mMovies.size() - 1);
    }

    public void addItems(List<Result> movies) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(Observable.fromIterable(movies)
                .subscribe(this::addItem, Throwable::printStackTrace, disposable::clear));
    }

    private RequestBuilder<Drawable> loadImage(@NonNull Context context, @NonNull String path) {
        return Glide
                .with(context)
                .load(path)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL).centerCrop())
                .transition(new DrawableTransitionOptions().crossFade());
    }

    public void showReload(boolean show) {
        mReloading = show;
        notifyItemChanged(mMovies.size() - 1);
    }

    public boolean isNotReloading() {
        return !mReloading;
    }

    public void addLoadingFooter() {
        mIsLoadingAddedItem = true;
        addItem(new Result());
    }

    public boolean isLoadingAdded() {
        return mIsLoadingAddedItem;
    }

    public void removeLoadingFooter() {
        mIsLoadingAddedItem = false;
        int position = mMovies.size() - 1;
        Result movie = getItem(position);

        if (movie != null) {
            mMovies.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearItems() {
        mIsLoadingAddedItem = false;
        mMovies.clear();
        notifyDataSetChanged();
    }

    private Result getItem(int position) {
        return mMovies.get(position);
    }

    public List<Result> getMovies() {
        return mMovies;
    }

    private void bindMovieViewHolder(ViewHolder holder, int position) {
        Result movie = getItem(position);

        loadImage(mContext, !TextUtils.isEmpty(movie.getBackdropPath()) ?
                movie.getBackdropPath() : movie.getPosterAsBackdrop())
                .into(holder.mBackdrop);

        loadImage(mContext, movie.getPosterPath()).into(holder.mPoster);
        holder.mTitle.setText(movie.getTitle());
        String text = movie.getReleaseDate() + " | " + movie.getVoteAverage();
        holder.mVoteAverage.setText(text);
        holder.mSynopsis.setText(movie.getOverview());
    }

    private void bindLoadingHolder(LoadingHolder holder) {
        if (mReloading) {
            holder.mErrorContainer.setVisibility(View.VISIBLE);
            holder.mProgressBar.setVisibility(View.GONE);
        } else {
            holder.mErrorContainer.setVisibility(View.GONE);
            holder.mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public class LoadingHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.loadMoreProgress) ProgressBar mProgressBar;
        @BindView(R.id.loadMoreError) LinearLayout mErrorContainer;

        public LoadingHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.loadMoreError)
        public void onClick() {
            showReload(false);
            mCallback.onReloadPage();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.backdrop) ImageView mBackdrop;
        @BindView(R.id.moviePoster) ImageView mPoster;
        @BindView(R.id.tvMovieTitle) TextView mTitle;
        @BindView(R.id.tvVoteAverage) TextView mVoteAverage;
        @BindView(R.id.tvSynopsis) TextView mSynopsis;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.mainContent)
        public void onClick(View view) {
            if (mListener != null) {
                Result movie = getItem(getAdapterPosition());
                mListener.onItemClick(view, movie);
            }
        }
    }

}
