package com.ms.library.utils.luban;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.ms.library.utils.D;
import com.ms.library.utils.FileUtils;
import com.ms.library.utils.media_data.MediaUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class Luban implements Handler.Callback {
    private static final String TAG = "Luban";
    private static final String DEFAULT_DISK_CACHE_DIR = "luban_disk_cache";

    private static final int MSG_COMPRESS_SUCCESS = 0;
    private static final int MSG_COMPRESS_LIST_SUCCESS = 1;
    private static final int MSG_COMPRESS_START = 2;
    private static final int MSG_COMPRESS_ERROR = 3;

    private String mTargetDir;
    private boolean focusAlpha;
    private int mLeastCompressSize;
    private OnRenameListener mRenameListener;
    private OnCompressListener mCompressListener;
    private CompressionPredicate mCompressionPredicate;
    private List<InputStreamAdapter> mStreamProviders;

    private Handler mHandler;

    private Luban(Builder builder) {
        this.mTargetDir = builder.mTargetDir;
        this.mRenameListener = builder.mRenameListener;
        this.mStreamProviders = builder.mStreamProviders;
        this.mCompressListener = builder.mCompressListener;
        this.mLeastCompressSize = builder.mLeastCompressSize;
        this.mCompressionPredicate = builder.mCompressionPredicate;
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static Builder with(WeakReference<Context> context) {
        return new Builder(context);
    }

    /**
     * Returns a file with a cache image name in the private cache directory.
     *
     * @param context A context.
     */
    private File getImageCacheFile(WeakReference<Context> context) {
        if (TextUtils.isEmpty(mTargetDir)) {
            mTargetDir = getImageCacheDir(context).getAbsolutePath();
        }

        String cacheBuilder = mTargetDir + "/"
                + UUID.randomUUID().toString().replaceAll("-", "")
                + (focusAlpha ? ".png" : ".jpg");

        return new File(cacheBuilder);
    }

    private File getImageCustomFile(WeakReference<Context> context, String filename) {
        if (TextUtils.isEmpty(mTargetDir)) {
            mTargetDir = getImageCacheDir(context).getAbsolutePath();
        }

        String cacheBuilder = mTargetDir + "/" + filename;

        return new File(cacheBuilder);
    }

    /**
     * Returns a directory with a default name in the private cache directory of the application to
     * use to store retrieved audio.
     *
     * @param context A context.
     * @see #getImageCacheDir(WeakReference<Context>, String)
     */
    private File getImageCacheDir(WeakReference<Context> context) {
        return getImageCacheDir(context, DEFAULT_DISK_CACHE_DIR);
    }

    /**
     * Returns a directory with the given name in the private cache directory of the application to
     * use to store retrieved media and thumbnails.
     *
     * @param context   A context.
     * @param cacheName The name of the subdirectory in which to store the cache.
     * @see #getImageCacheDir(WeakReference<Context>)
     */
    private static File getImageCacheDir(WeakReference<Context> context, String cacheName) {
        File cacheDir = context.get().getExternalCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }
            return result;
        }
        D.e("default disk cache dir is null");
        return null;
    }

    /**
     * start asynchronous compress thread
     */
    private void launch(final WeakReference<Context> context) {
        if (mStreamProviders == null || mStreamProviders.isEmpty())
            return;

        AsyncTask.SERIAL_EXECUTOR.execute(() -> {
            Iterator<InputStreamAdapter> iterator = mStreamProviders.iterator();
            List<UploadFileEntity> resList = new ArrayList<>();
            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START));

            while (iterator.hasNext()) {
                final InputStreamAdapter path = iterator.next();
                UploadFileEntity res = compress(context, path);
                if (res.index == -1)
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_SUCCESS, res));
                else
                    resList.add(res);
                iterator.remove();
            }

            if (!resList.isEmpty())
                mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_LIST_SUCCESS, resList));
        });
    }

    /**
     * start compress and return the file
     */
    private File get(InputStreamProvider input, WeakReference<Context> context) throws IOException {
        try {
            return new Engine(input, getImageCacheFile(context), focusAlpha).compress();
        } finally {
            input.close();
        }
    }

    private List<UploadFileEntity> get(WeakReference<Context> context) {
        List<UploadFileEntity> results = new ArrayList<>();
        Iterator<InputStreamAdapter> iterator = mStreamProviders.iterator();

        while (iterator.hasNext()) {
            results.add(compress(context, iterator.next()));
            iterator.remove();
        }

        return results;
    }

    private UploadFileEntity compress(WeakReference<Context> context, InputStreamAdapter path) {
        try {
            return compressReal(context, path);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            path.close();
        }
        return new UploadFileEntity(path.index, path.getPath(), "", MediaUtils.IMAGE);
    }

    private UploadFileEntity compressReal(WeakReference<Context> context, InputStreamAdapter path) throws IOException {
        File result;
        UploadFileEntity resultLB = new UploadFileEntity();
        resultLB.srcPath = path.getPath();
        resultLB.index = path.index;

        File outFile = getImageCacheFile(context);

        if (mRenameListener != null) {
            String filename = mRenameListener.rename(path.getPath(), focusAlpha);
            outFile = getImageCustomFile(context, filename);
        }

        if (mCompressionPredicate != null) {
            if (mCompressionPredicate.apply(path.getPath())
                    && Checker.SINGLE.needCompress(mLeastCompressSize, path.getPath())) {
                result = new Engine(path, outFile, focusAlpha).compress();
            } else {
                result = new File(path.getPath());
            }
        } else {
            result = Checker.SINGLE.needCompress(mLeastCompressSize, path.getPath()) ?
                    new Engine(path, outFile, focusAlpha).compress() :
                    new File(path.getPath());
        }
        resultLB.dstPath = result == null ? "" : result.getAbsolutePath();
        return resultLB;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mCompressListener == null) return false;

        switch (msg.what) {
            case MSG_COMPRESS_START:
                mCompressListener.onStart();
                break;
            case MSG_COMPRESS_SUCCESS:
                mCompressListener.onSuccess((UploadFileEntity) msg.obj);
                break;
            case MSG_COMPRESS_LIST_SUCCESS:
                mCompressListener.onSuccessList((List<UploadFileEntity>) msg.obj);
                break;
        }
        return false;
    }

    public static class Builder {
        private WeakReference<Context> context;
        private String mTargetDir;
        private boolean focusAlpha;
        private int mLeastCompressSize = 1080;
        private OnRenameListener mRenameListener;
        private OnCompressListener mCompressListener;
        private CompressionPredicate mCompressionPredicate;
        private List<InputStreamAdapter> mStreamProviders;

        Builder(WeakReference<Context> context) {
            this.context = context;
            this.mStreamProviders = new ArrayList<>();
        }

        private Luban build() {
            return new Luban(this);
        }

        public Builder load(InputStreamAdapter inputStreamProvider) {
            mStreamProviders.add(inputStreamProvider);
            return this;
        }

        public Builder load(final File file) {
            return load(file, -1);
        }

        public Builder load(final File file, int index) {
            mStreamProviders.add(new InputStreamAdapter(index) {
                @Override
                public InputStream openInternal() throws IOException {
                    return new FileInputStream(file);
                }

                @Override
                public String getPath() {
                    return file.getAbsolutePath();
                }
            });
            return this;
        }

        public Builder load(final String string) {
            return load(string, -1);
        }

        public Builder load(final String string, final int index) {
            mStreamProviders.add(new InputStreamAdapter(index) {
                @Override
                public InputStream openInternal() throws IOException {
                    return new FileInputStream(string);
                }

                @Override
                public String getPath() {
                    return string;
                }
            });
            return this;
        }

        public <T> Builder load(List<T> list) {
            int index = list.size();
            for (T src : list) {
                if (src instanceof String) {
                    load((String) src, index);
                } else if (src instanceof File) {
                    load((File) src, index);
                } else if (src instanceof Uri) {
                    load((Uri) src, index);
                } else if (src instanceof UploadFileEntity) {
                    load(((UploadFileEntity) src).srcPath, index);
                } else {
                    throw new IllegalArgumentException("Incoming data type exception, it must be String, File, Uri or Bitmap");
                }
                index++;
            }
            return this;
        }

        public Builder load(final Uri uri) {
            return load(uri, -1);
        }

        public Builder load(final Uri uri, int index) {
            mStreamProviders.add(new InputStreamAdapter(index) {
                @Override
                public InputStream openInternal() throws IOException {
                    return context.get().getContentResolver().openInputStream(uri);
                }

                @Override
                public String getPath() {
                    return uri.getPath();
                }
            });
            return this;
        }

        public Builder putGear(int gear) {
            return this;
        }

        public Builder setRenameListener(OnRenameListener listener) {
            this.mRenameListener = listener;
            return this;
        }

        public Builder setCompressListener(OnCompressListener listener) {
            this.mCompressListener = listener;
            return this;
        }

        public Builder setTargetDir(String targetDir) {
            this.mTargetDir = targetDir;
            if (!FileUtils.checkAndMkdir(new File(targetDir)))
                throw new RuntimeException(targetDir + " mkdir failed!!");
            return this;
        }

        /**
         * Do I need to keep the image's alpha channel
         *
         * @param focusAlpha <p> true - to keep alpha channel, the compress speed will be slow. </p>
         *                   <p> false - don't keep alpha channel, it might have a black background.</p>
         */
        public Builder setFocusAlpha(boolean focusAlpha) {
            this.focusAlpha = focusAlpha;
            return this;
        }

        /**
         * do not compress when the origin image file size less than one value
         *
         * @param size the value of file size, unit KB, default 100K
         */
        public Builder ignoreBy(int size) {
            this.mLeastCompressSize = size;
            return this;
        }

        /**
         * do compress image when return value was true, otherwise, do not compress the image file
         *
         * @param compressionPredicate A predicate callback that returns true or false for the given input path should be compressed.
         */
        public Builder filter(CompressionPredicate compressionPredicate) {
            this.mCompressionPredicate = compressionPredicate;
            return this;
        }


        /**
         * begin compress image with asynchronous
         */
        public void launch() {
            build().launch(context);
        }

        public File get(final String path, int index) throws IOException {
            return build().get(new InputStreamAdapter(index) {
                @Override
                public InputStream openInternal() throws IOException {
                    return new FileInputStream(path);
                }

                @Override
                public String getPath() {
                    return path;
                }
            }, context);
        }

        /**
         * begin compress image with synchronize
         *
         * @return the thumb image file list
         */
        public List<UploadFileEntity> get() throws IOException {
            return build().get(context);
        }
    }
}