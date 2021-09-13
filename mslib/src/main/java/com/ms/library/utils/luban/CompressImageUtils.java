package com.ms.library.utils.luban;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * Created by smz on 2021/8/16.
 * 压缩图片
 */

public class CompressImageUtils {
    private static final String DEFAULT_DISK_CACHE_DIR = "disk_img_cache";

    public static String compress(WeakReference<Context> context, String path) {
        return compress(context, path, 1080, new DefaultCompressionPredicate());
    }

    public static String compress(WeakReference<Context> context, String path, int mLeastCompressSize, CompressionPredicate predicate) {
        if (Checker.SINGLE.needCompress(mLeastCompressSize, path) &&
                (predicate == null || predicate.apply(path))) {
            try {
                return compress(path, getImageCacheDir(context, DEFAULT_DISK_CACHE_DIR));
            } catch (IOException e) {
                e.printStackTrace();
                return path;
            }
        }
        return path;
    }

    private static String compress(String path, String cachePath) throws IOException {
        BitmapFactory.Options optionsTest = new BitmapFactory.Options();
        optionsTest.inJustDecodeBounds = true;
        optionsTest.inSampleSize = 1;
        BitmapFactory.decodeFile(path, optionsTest);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize(options.outWidth, options.outHeight);

        Bitmap tagBitmap = BitmapFactory.decodeFile(path, options);
        if (tagBitmap == null) return null;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        InputStream is = new FileInputStream(path);
        if (Checker.SINGLE.isJPG(is)) {
            Matrix matrix = new Matrix();
            matrix.postRotate(Checker.SINGLE.getOrientation(is));
            tagBitmap = Bitmap.createBitmap(tagBitmap, 0, 0, tagBitmap.getWidth(), tagBitmap.getHeight(), matrix, true);
        }
        tagBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);

        String resPath = cachePath + "/"
                + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
        FileOutputStream fos = new FileOutputStream(resPath);
        fos.write(stream.toByteArray());
        fos.flush();
        fos.close();
        stream.close();

        return resPath;
    }

    private static int computeSize(int srcWidth, int srcHeight) {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    private static String getImageCacheDir(WeakReference<Context> context, String cacheName) {
        File cacheDir = context.get().getExternalCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }
            return result.getAbsolutePath();
        }

        return null;
    }

}
