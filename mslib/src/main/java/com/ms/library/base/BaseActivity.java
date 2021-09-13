package com.ms.library.base;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;

/**
 * Created by smz on 2021/6/11.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout());
        initMVP(savedInstanceState);
        init();
    }

    public abstract int layout();

    protected void initMVP(Bundle savedInstanceState) {

    }

    public void init() {
    }
}
