package com.ms.library.utils;

import java.io.File;

/**
 * Created by smz on 2021/6/30.
 */

public class FileUtils {
    public static boolean checkAndMkdir(File file) {
        if (file == null) return false;
        if (file.exists()) return true;
        return file.mkdirs();
    }
}
