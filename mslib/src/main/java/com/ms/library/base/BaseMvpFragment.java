package com.ms.library.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by smz on 2021/6/11.
 */

public abstract class BaseMvpFragment<V extends BaseView, P extends BasePresenter<V>> extends BaseFragment {
    protected P presenter;
    protected boolean defaultInstance = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(layout(), container, false);
        presenter = createPresenter();
        if (presenter == null) defaultInstance();
        if (presenter == null)
            throw new RuntimeException("==>BaseMvpActivity Presenter is null");
        presenter.attachView((V) this);
        init();
        return view;
    }

    public void defaultInstance() {
        if (presenter == null && defaultInstance) {
            try {
                Type superclass = getClass().getGenericSuperclass();
                ParameterizedType parameterizedType = null;
                if (superclass instanceof ParameterizedType) {
                    parameterizedType = (ParameterizedType) superclass;
                    Type[] typeArray = parameterizedType.getActualTypeArguments();
                    if (typeArray != null && typeArray.length > 1) {
                        Class<P> clazz = (Class<P>) typeArray[1];
                        presenter = clazz.newInstance();
                    }
                }
            } catch (IllegalAccessException | java.lang.InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public P createPresenter() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null)
            presenter.detachView();
    }
}
