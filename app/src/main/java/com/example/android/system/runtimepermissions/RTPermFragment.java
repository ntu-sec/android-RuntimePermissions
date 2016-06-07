package com.example.android.system.runtimepermissions;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RTPermFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, null);

        if (Build.VERSION.SDK_INT < 23) {
            root.findViewById(R.id.button_contacts).setVisibility(View.GONE);
        }
        return root;
    }
}
