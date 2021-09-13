package com.ms.library.utils.media_data;

import java.util.List;

/**
 * Created by smz on 2021/6/30.
 */

public class MediaFolderEntity {
    public String name;
    public int mediaType;
    public boolean selected;
    public List<MediaEntity> datas;

    @Override
    public String toString() {
        return "MediaFolderEntity{" +
                "name='" + name + '\'' +
                "mediaType='" + mediaType + '\'' +
                ", datas=" + datas +
                '}';
    }
}
