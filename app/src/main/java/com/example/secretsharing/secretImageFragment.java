package com.example.secretsharing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class secretImageFragment extends Fragment {

    private static final String TAG = "SecretImageFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.secretimage_fragment, container,false);
        return view;
    }
}
