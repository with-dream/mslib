package com.ms.library.utils.luban;

import java.util.List;

public interface OnCompressListener {

    /**
     * Fired when the compression is started, override to handle in your own code
     */
    void onStart();

    /**
     * Fired when a compression returns successfully, override to handle in your own code
     */
    default void onSuccess(UploadFileEntity res) {
    }

    default void onSuccessList(List<UploadFileEntity> res) {
    }

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     */
//  void onError(ResultLB res);
}
