package br.com.binmarques.cinemovies.movies;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import br.com.binmarques.cinemovies.R;
import br.com.binmarques.cinemovies.model.Result;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created By Daniel Marques on 07/08/2018
 */

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    private Context mContext;
    private final List<Result> mMovies = new ArrayList<>();
    private OnSectionItemClickListener mSectionListener;

    public SectionAdapter(Context context) {
        this.mContext = context;
    }

    public void setOnSectionItemClickListener(OnSectionItemClickListener listener) {
        this.mSectionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        bindMovieViewHolder(viewHolder, position);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void addItems(List<Result> movies) {
        mMovies.clear();
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(Observable.fromIterable(movies)
                .subscribe(this::addItem, Throwable::printStackTrace, disposable::clear));
    }

    private void addItem(Result movie) {
        mMovies.add(movie);
        notifyItemInserted(mMovies.size() - 1);
    }

    private Result getItem(int position) {
        return mMovies.get(position);
    }

    public List<Result> getMovies() {
        return mMovies;
    }

    private RequestBuilder<Drawable> loadImage(@NonNull Context context, @NonNull String path) {
        return Glide
                .with(context)
                .load(path)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL).centerCrop())
                .transition(new DrawableTransitionOptions().crossFade());
    }

    private void bindMovieViewHolder(ViewHolder holder, int position) {
        Result movie = getItem(position);
        holder.mCardView.setTag(movie);
        holder.mTitle.setText(movie.getTitle());
        holder.mYear.setText(movie.getReleaseDate());
        String value = String.valueOf(movie.getVoteAverage());
        holder.mVoteAverage.setText(value);
        loadImage(mContext, movie.getPosterPath()).into(holder.mPoster);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cardViewMovie) CardView mCardView;
        @BindView(R.id.moviePoster) ImageView mPoster;
        @BindView(R.id.tvMovieTitle) TextView mTitle;
        @BindView(R.id.tvVoteAverage) TextView mVoteAverage;
        @BindView(R.id.tvYear) TextView mYear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.cardViewMovie)
        public void onClick(View view) {
            if (mSectionListener != null) {
                Result movie = (Result) view.getTag();

                if (movie != null) {
                    mSectionListener.onSectionItemClick(view, movie);
                }
            }
        }
    }

    public interface OnSectionItemClickListener {

        void onSectionItemClick(@NonNull View view, @NonNull Result movie);

    }
}
