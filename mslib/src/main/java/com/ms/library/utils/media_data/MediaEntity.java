package com.ms.library.utils.media_data;

import com.ms.library.base.Entity;

/**
 * Created by smz on 2021/6/30.
 */

public class MediaEntity extends Entity {
    public String path;
    public int mediaType;
    public long time;
    public String name;
    public String mimeType;
    public String uri;
    public int seleIndex;

    public MediaEntity(int mediaType, String path, long time, String name, String mimeType, String uri) {
        this.mediaType = mediaType;
        this.path = path;
        this.time = time;
        this.name = name;
        this.mimeType = mimeType;
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "MediaEntity{" +
                "mediaType='" + mediaType + '\'' +
                "path='" + path + '\'' +
                ", time=" + time +
                ", name='" + name + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", uri=" + uri +
                ", seleIndex=" + seleIndex +
                '}';
    }
}
