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
import br.com.binmarques.cinemovies.model.Video;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created By Daniel Marques on 04/11/2018
 */

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.ViewHolder> {

    private Context mContext;
    private List<Video> mItems;
    private OnItemClickListener mListener;

    public MovieTrailersAdapter(Context context, List<Video> items) {
        this.mContext = context;
        this.mItems = items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_trailer, parent, false);

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        bindVideoViewHolder(viewHolder, position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private Video getItem(int position) {
        return mItems.get(position);
    }

    public void addItems(List<Video> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    private void bindVideoViewHolder(ViewHolder viewHolder, int position) {
        Video trailer = getItem(position);
        viewHolder.mTrailerName.setText(trailer.getName());
        loadImage(mContext,
                Video.YOUTUBE_THUMBNAIL_URL + trailer.getKey() + "/0.jpg")
                .into(viewHolder.mImgTrailer);
    }

    private RequestBuilder<Drawable> loadImage(@NonNull Context context, @NonNull String posterPath) {
        return Glide
                .with(context)
                .load(posterPath)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL).centerCrop())
                .transition(new DrawableTransitionOptions().crossFade());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTrailerName) TextView mTrailerName;
        @BindView(R.id.imgTrailer) ImageView mImgTrailer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.cardViewTrailer)
        public void onClick() {
            if (mListener != null) {
                Video trailer = getItem(getAdapterPosition());
                mListener.onVideoItemClick(trailer);
            }
        }
    }

    public interface OnItemClickListener {

        void onVideoItemClick(@NonNull Video trailer);

    }

}
