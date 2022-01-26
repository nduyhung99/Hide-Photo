package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto;

import java.io.Serializable;

public class Photo implements Serializable {
    String pathPhoto;
    boolean isChecked;

    public String getPathPhoto() {
        return pathPhoto;
    }

    public void setPathPhoto(String pathPhoto) {
        this.pathPhoto = pathPhoto;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Photo() {
    }

    public Photo(String pathPhoto, boolean isChecked) {
        this.pathPhoto = pathPhoto;
        this.isChecked = isChecked;
    }

}
