package com.ms.library.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by smz on 2021/6/8.
 */

public abstract class BaseFragment extends Fragment {
    protected View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(layout(), container, false);
        init();
        return view;
    }

    public abstract int layout();

    public void init() {
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return view.findViewById(id);
    }
}
