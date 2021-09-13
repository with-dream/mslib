package com.ms.library.utils;

import android.util.Log;

/**
 * Created by smz on 2021/6/17.
 */

public class D {
    public static void e(String str) {
        if (Config.OKHTTP_LOG)
            Log.e("=====", str);
    }
}
