package com.ms.library.utils.luban;

/**
 * Created by smz on 2021/7/13.
 */

public class UploadFileEntity {
    public UploadFileEntity() {
    }

    public UploadFileEntity(int index, String srcPath, String dstPath, int mediaType, int width, int height) {
        this.index = index;
        this.srcPath = srcPath;
        this.dstPath = dstPath;
        this.mediaType = mediaType;
        this.width = width;
        this.height = height;
    }

    public UploadFileEntity(int index, String srcPath, String dstPath, int mediaType) {
        this(index, srcPath, dstPath, mediaType, -1, -1);
    }

    public int index;
    public int mediaType;
    public int width;
    public int height;
    public String md5;
    public String srcPath;
    public String dstPath;

    @Override
    public String toString() {
        return "UploadFileEntity{" +
                "index=" + index +
                ", mediaType=" + mediaType +
                ", width=" + width +
                ", height=" + height +
                ", md5='" + md5 + '\'' +
                ", srcPath='" + srcPath + '\'' +
                ", dstPath='" + dstPath + '\'' +
                '}';
    }
}
