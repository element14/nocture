package com.bime.nocturne.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bime.nocturne.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class UserConnectRequestActivityFragment extends Fragment {

    public UserConnectRequestActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_connect_request, container, false);
    }
}
