package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.captureimage;

import java.util.TreeMap;

public interface PictureCapturingListener {

    void onCaptureDone(String pictureUrl, byte[] pictureData);

    void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken);

}
