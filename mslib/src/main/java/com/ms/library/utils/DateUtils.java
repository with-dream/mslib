package com.ms.library.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by smz on 2021/7/9.
 */

public class DateUtils {
    public static String time2Date(long time) {
        return time2Date(time, null);
    }

    public static String time2Date(long time, String format) {
        if (time <= 0L) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }
}
