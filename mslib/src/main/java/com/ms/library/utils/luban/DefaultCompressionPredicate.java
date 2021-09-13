package com.ms.library.utils.luban;

import android.text.TextUtils;

/**
 * Created by smz on 2021/8/16.
 * 只压缩jpg和png 且结果都为jpg
 */

public class DefaultCompressionPredicate implements CompressionPredicate {
    @Override
    public boolean apply(String path) {
        return !TextUtils.isEmpty(path) && (path.endsWith(".jpg") || path.endsWith(".png"));
    }
}
