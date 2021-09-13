package com.ms.library.base;

import com.ms.library.utils.ErrCode;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<V extends BaseView> {
    protected static final int ERROR = ErrCode.ERROR_NET;
    protected WeakReference<V> view;

    public void attachView(V view) {
        this.view = new WeakReference<>(view);
    }

    public void detachView() {
        if (view != null) {
            view.clear();
            view = null;
        }
    }

    public boolean isAttached() {
        return view != null && view.get() != null;
    }

    public V getView() {
        return view != null ? view.get() : null;
    }
}
