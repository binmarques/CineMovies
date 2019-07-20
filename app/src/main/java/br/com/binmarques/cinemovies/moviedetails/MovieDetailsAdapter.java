package br.com.binmarques.cinemovies.moviedetails;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import br.com.binmarques.cinemovies.model.Result;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created By Daniel Marques on 24/08/2018
 */

public class MovieDetailsAdapter extends RecyclerView.Adapter<MovieDetailsAdapter.ViewHolder> {

    private Context mContext;
    private List<Result> mMovies;
    private OnItemClickListener mListener;

    public MovieDetailsAdapter(Context context, List<Result> movies) {
        this.mContext = context;
        this.mMovies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        bindMovieViewHolder(viewHolder, position);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void addItems(List<Result> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
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

    private void bindMovieViewHolder(ViewHolder viewHolder, int position) {
        Result movie = getItem(position);
        viewHolder.mTitle.setText(movie.getTitle());
        viewHolder.mYear.setText(movie.getReleaseDate());
        String value = String.valueOf(movie.getVoteAverage());
        viewHolder.mVoteAverage.setText(value);
        loadImage(mContext, movie.getPosterPath()).into(viewHolder.mPoster);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
            if (mListener != null) {
                Result movie = getItem(getAdapterPosition());
                mListener.onItemClick(view, movie);
            }
        }
    }

    public interface OnItemClickListener {

        void onItemClick(@NonNull View view, @NonNull Result movie);

    }

}
