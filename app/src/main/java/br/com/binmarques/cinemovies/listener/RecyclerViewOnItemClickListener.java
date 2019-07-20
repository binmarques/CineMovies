package br.com.binmarques.cinemovies.listener;

import androidx.annotation.NonNull;
import android.view.View;

import br.com.binmarques.cinemovies.model.Result;

/**
 * Created By Daniel Marques on 05/08/2018
 */

public interface RecyclerViewOnItemClickListener {

    void onItemClick(@NonNull View view, @NonNull Result movie);

    void onItemClickView(@NonNull View view, int position);

    void onItemLongPressClick(@NonNull View view, int position);

}
