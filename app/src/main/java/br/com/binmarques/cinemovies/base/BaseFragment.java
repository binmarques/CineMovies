package br.com.binmarques.cinemovies.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * Created By Daniel Marques on 05/08/2018
 */

public abstract class BaseFragment extends Fragment {

    private static Toast sToast;

    public static final String EXTRA_ID = "EXTRA_ID";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater
                .inflate(getFragmentLayout(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
    }

    protected abstract int getFragmentLayout();

    private void bindViews(final View view) {
        ButterKnife.bind(this, view);
    }

    public boolean isNetworkAvailable() {
        if (getActivity() != null) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager == null) {
                return false;
            } else {
                NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();

                if (currentNetworkInfo != null && currentNetworkInfo.isAvailable()) {
                    return currentNetworkInfo.isConnected();
                }
            }
        }

        return false;
    }

    public void showMessage(String message) {
        if (sToast != null) {
            sToast.cancel();
        }

        sToast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        sToast.show();
    }
}
