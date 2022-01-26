package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto;

import java.io.Serializable;

public class Butket implements Serializable {
    private String name;
    private String firstImageContainedPath;

    public Butket() {
    }

    public Butket(String name, String firstImageContainedPath) {
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
