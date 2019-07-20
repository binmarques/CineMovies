package br.com.binmarques.cinemovies.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.List;

import br.com.binmarques.cinemovies.R;
import br.com.binmarques.cinemovies.listener.RecyclerViewOnItemClickListener;
import br.com.binmarques.cinemovies.model.MoviesWrapper;
import br.com.binmarques.cinemovies.util.CustomSnapHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created By Daniel Marques on 05/08/2018
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private Context mContext;
    private List<MoviesWrapper> mItems;
    private RecyclerViewOnItemClickListener mListener;
    private SectionAdapter.OnSectionItemClickListener mSectionListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.RecycledViewPool mSharedPool;

    private static final int[] titles = {
        R.string.section_title_theaters,
        R.string.section_title_popular,
        R.string.section_title_top_rated,
        R.string.section_title_upcoming,
    };

    private static final int[] subtitles = {
        R.string.section_subtitle_theaters,
        R.string.section_subtitle_popular,
        R.string.section_subtitle_top_rated,
        R.string.section_subtitle_upcoming
    };

    public MoviesAdapter(Context context, List<MoviesWrapper> items) {
        this.mContext = context;
        this.mItems = items;
        mSharedPool = new RecyclerView.RecycledViewPool();
    }

    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnSectionItemClickListener(SectionAdapter.OnSectionItemClickListener listener) {
        this.mSectionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_movies, parent, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerViewSection);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setRecycledViewPool(mSharedPool);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setInitialPrefetchItemCount(4);
        mRecyclerView.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new CustomSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        bindMovieViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItems(List<MoviesWrapper> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    private void bindMovieViewHolder(ViewHolder holder, int position) {
        SectionAdapter sectionAdapter = new SectionAdapter(mContext);
        sectionAdapter.setOnSectionItemClickListener(mSectionListener);
        MoviesWrapper item = mItems.get(position);
        sectionAdapter.addItems(item.getResults());
        mRecyclerView.setAdapter(sectionAdapter);
        holder.mTitle.setText(titles[position]);
        holder.mSubtitle.setText(subtitles[position]);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvSectionTitle) TextView mTitle;
        @BindView(R.id.tvSectionSubtitle) TextView mSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.btnSeeAll)
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClickView(view, getAdapterPosition());
            }
        }
    }

}
