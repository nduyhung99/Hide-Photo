package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto;

import java.io.Serializable;

public class ImageSelected implements Serializable {
    String pathImageSelected;

    public String getPathImageSelected() {
        return pathImageSelected;
    }

    public void setPathImageSelected(String pathImageSelected) {
        this.pathImageSelected = pathImageSelected;
    }

    public ImageSelected() {
    }

    public ImageSelected(String pathImageSelected) {
        this.pathImageSelected = pathImageSelected;
    }
}
