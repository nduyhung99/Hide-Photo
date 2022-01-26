package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.pinandsecurityquestion;

public class ImageSecurity {
    String path;
    String dateAndTime;
    String passwordWrong;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getPasswordWrong() {
        return passwordWrong;
    }

    public void setPasswordWrong(String passwordWrong) {
        this.passwordWrong = passwordWrong;
    }

    public ImageSecurity() {
    }

    public ImageSecurity(String path, String dateAndTime, String passwordWrong) {
        this.path = path;
        this.dateAndTime = dateAndTime;
        this.passwordWrong = passwordWrong;
    }
}
