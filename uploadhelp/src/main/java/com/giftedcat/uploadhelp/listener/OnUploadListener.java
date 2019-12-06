package com.giftedcat.uploadhelp.listener;

import com.giftedcat.uploadhelp.db.Attendance;

public interface OnUploadListener<T extends Attendance> {

    /**
     * 有需要上传的文件回调
     * */
    void onUpload(T t);

}
