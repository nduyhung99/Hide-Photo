package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importvideo;

import java.io.Serializable;

public class Video implements Serializable {
    String pathVideo;
    boolean isChecked;

    public Video() {
    }

    public Video(String pathVideo, boolean isChecked) {
        this.pathVideo = pathVideo;
        this.isChecked = isChecked;
    }

    public String getPathVideo() {
        return pathVideo;
    }

    public void setPathVideo(String pathVideo) {
        this.pathVideo = pathVideo;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
