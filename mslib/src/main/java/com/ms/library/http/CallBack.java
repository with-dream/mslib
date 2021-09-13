package com.ms.library.http;

public interface CallBack<T> {
    void onSuccess(T entity);
    void onFailure(String msg);
}
