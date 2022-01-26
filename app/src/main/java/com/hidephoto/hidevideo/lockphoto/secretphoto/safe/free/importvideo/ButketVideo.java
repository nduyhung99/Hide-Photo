package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importvideo;

import java.io.Serializable;

public class ButketVideo implements Serializable {
    private String name;
    private String firstImageContainedPath;

    public ButketVideo() {
    }

    public ButketVideo(String name, String firstImageContainedPath) {
        this.name = name;
        this.firstImageContainedPath = firstImageContainedPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstImageContainedPath() {
        return firstImageContainedPath;
    }

    public void setFirstImageContainedPath(String firstImageContainedPath) {
        this.firstImageContainedPath = firstImageContainedPath;
    }
}
