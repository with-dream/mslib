package com.ms.library.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by smz on 2021/6/11.
 */

public abstract class BaseMvpActivity<V extends BaseView, P extends BasePresenter<V>> extends BaseActivity {
    public P presenter;
    protected boolean defaultInstance = true;

    @Override
    protected final void initMVP(Bundle savedInstanceState) {
        presenter = createPresenter();
        if (presenter == null) defaultInstance();
        if (presenter == null)
            throw new RuntimeException("==>BaseMvpActivity Presenter is null");
        presenter.attachView((V) this);
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
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null)
            presenter.detachView();
    }
}
