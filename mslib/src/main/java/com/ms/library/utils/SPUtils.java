package com.ms.library.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.content.SharedPreferencesCompat;

import com.google.gson.Gson;


/**
 * Created by smz on 2021/6/29.
 */

public class SPUtils {
    public static String packageName;

    public static void putObj(Context context, String key, Object value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(packageName, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        sharedPreferences.edit().putString(key, gson.toJson(value)).apply();
    }

    public static <T> T getObj(Context context, String key, Class<T> classOfT) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(packageName, Context.MODE_PRIVATE);
        String res = sharedPreferences.getString(key, "");
        if (StrUtils.isEmpty(res))
            return null;
        return new Gson().fromJson(res, classOfT);
    }

    public static void remove(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(packageName, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(key).apply();
    }

    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(packageName, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
}
