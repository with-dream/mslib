package com.ms.library.base;

import com.ms.library.utils.ErrCode;

/**
 * Created by smz on 2021/6/24.
 */

public class BaseEntity<T> extends Entity {
    public int res;
    public String msg;
    public T data;

    public boolean succ() {
        return res == ErrCode.SUCC;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "res=" + res +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
