package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.viewfileinfolder;

import java.io.Serializable;

public class FileInFolder implements Serializable {
    String pathFileInFolder;
    boolean fileChecked;

    public FileInFolder(String pathFileInFolder, boolean fileChecked) {
        this.pathFileInFolder = pathFileInFolder;
        this.fileChecked = fileChecked;
    }

    public FileInFolder() {
    }

    public String getPathFileInFolder() {
        return pathFileInFolder;
    }

    public void setPathFileInFolder(String pathFileInFolder) {
        this.pathFileInFolder = pathFileInFolder;
    }

    public boolean isFileChecked() {
        return fileChecked;
    }

    public void setFileChecked(boolean fileChecked) {
        this.fileChecked = fileChecked;
    }
}
