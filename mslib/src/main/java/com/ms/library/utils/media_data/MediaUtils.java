package com.ms.library.utils.media_data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.ms.library.utils.StrUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by smz on 2021/6/30.
 */

public class MediaUtils {
    public static final int IMAGE = 1;
    public static final int VIDEO = 2;
    public static final int AUDIO = 3;
    public static final int ALL = 4;

    public static List<MediaEntity> getMediaData(Context context, int type) {
        switch (type) {
            case IMAGE:
                return getImageData(context);
            case VIDEO:
                return getVideoData(context);
            case AUDIO:
                return getAudioData(context);
        }
        return null;
    }

    public static List<MediaFolderEntity> getAllMediaFolder(Context context) {
        List<MediaEntity> data = new ArrayList<>();
        data.addAll(getImageData(context));
        data.addAll(getVideoData(context));
        data.addAll(getAudioData(context));
        return splitMedia(data);
    }

    public static List<MediaFolderEntity> getMediaFolder(Context context, int type) {
        List<MediaEntity> data = null;
        switch (type) {
            case IMAGE:
                data = getImageData(context);
                break;
            case VIDEO:
                data = getVideoData(context);
                break;
            case AUDIO:
                data = getAudioData(context);
                break;
            case ALL:
                return getAllMediaFolder(context);
        }
        if (data == null || data.isEmpty()) return null;

        return splitMedia(data);
    }

    private static List<MediaFolderEntity> splitMedia(List<MediaEntity> data) {
        Map<String, MediaFolderEntity> map = new HashMap<>();
        for (MediaEntity me : data) {
            int end = me.path.lastIndexOf("/");
            String tmp = me.path.substring(0, end);
            int start = tmp.lastIndexOf("/") + 1;
            String name = me.path.substring(start, end);

            if (map.containsKey(name)) {
                map.get(name).datas.add(me);
            } else {
                MediaFolderEntity folder = new MediaFolderEntity();
                folder.name = name;
                folder.mediaType = me.mediaType;
                folder.datas = new ArrayList<>();
                folder.datas.add(me);
                map.put(name, folder);
            }
        }

        List<MediaFolderEntity> res = new ArrayList<>();
        MediaFolderEntity folder = new MediaFolderEntity();
        folder.name = "全部";
        folder.mediaType = -1;
        folder.datas = data;
        folder.selected = true;
        res.add(folder);
        res.addAll(map.values());

        return res;
    }

    public static List<MediaEntity> getImageData(Context context) {
        ContentResolver mContentResolver = context.getContentResolver();
        ArrayList<MediaEntity> medias = new ArrayList<>();

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor mCursor = mContentResolver.query(mImageUri, new String[]{
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.MIME_TYPE,
                        MediaStore.Images.Media.SIZE},
                MediaStore.MediaColumns.SIZE + ">0",
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC");

        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                long id = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
                String path = mCursor.getString(
                        mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                if (!checkExist(path))
                    continue;
                String name = mCursor.getString(
                        mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                long time = mCursor.getLong(
                        mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                if (String.valueOf(time).length() < 13) {
                    time *= 1000;
                }

                String mimeType = mCursor.getString(
                        mCursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));

                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                        .appendPath(String.valueOf(id)).build();

                medias.add(new MediaEntity(IMAGE, path, time, name, mimeType, uri.toString()));
            }
            mCursor.close();
        }
        return medias;
    }

    public static List<MediaEntity> getVideoData(Context context) {
        ContentResolver mContentResolver = context.getContentResolver();
        ArrayList<MediaEntity> medias = new ArrayList<>();

        Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor mCursor = mContentResolver.query(mImageUri, new String[]{
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DATE_ADDED,
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.MIME_TYPE,
                        MediaStore.Video.Media.SIZE},
                MediaStore.MediaColumns.SIZE + ">0",
                null,
                MediaStore.Video.Media.DATE_ADDED + " DESC");

        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                long id = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                String path = mCursor.getString(
                        mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                if (!checkExist(path))
                    continue;
                String name = mCursor.getString(
                        mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                long time = mCursor.getLong(
                        mCursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                if (String.valueOf(time).length() < 13) {
                    time *= 1000;
                }

                String mimeType = mCursor.getString(
                        mCursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));

                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI.buildUpon()
                        .appendPath(String.valueOf(id)).build();

                medias.add(new MediaEntity(VIDEO, path, time, name, mimeType, uri.toString()));
            }
            mCursor.close();
        }
        return medias;
    }

    public static List<MediaEntity> getAudioData(Context context) {
        ContentResolver mContentResolver = context.getContentResolver();
        ArrayList<MediaEntity> medias = new ArrayList<>();

        Uri mImageUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor mCursor = mContentResolver.query(mImageUri, new String[]{
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATE_ADDED,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.SIZE},
                MediaStore.MediaColumns.SIZE + ">0",
                null,
                MediaStore.Audio.Media.DATE_ADDED + " DESC");

        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                long id = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String path = mCursor.getString(
                        mCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                if (!checkExist(path))
                    continue;
                String name = mCursor.getString(
                        mCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                long time = mCursor.getLong(
                        mCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
                if (String.valueOf(time).length() < 13) {
                    time *= 1000;
                }

                String mimeType = mCursor.getString(
                        mCursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));

                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.buildUpon()
                        .appendPath(String.valueOf(id)).build();

                medias.add(new MediaEntity(AUDIO, path, time, name, mimeType, uri.toString()));
            }
            mCursor.close();
        }
        return medias;
    }

    private static boolean checkExist(String path) {
        if (StrUtils.isEmpty(path)) return false;
        return new File(path).exists();
    }
}
