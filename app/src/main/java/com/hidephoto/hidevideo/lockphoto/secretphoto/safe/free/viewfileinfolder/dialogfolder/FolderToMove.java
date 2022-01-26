package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.viewfileinfolder.dialogfolder;

import java.io.Serializable;

public class FolderToMove implements Serializable {
    String pathFolder;

    public FolderToMove() {
    }

    public FolderToMove(String pathFolder) {
        this.pathFolder = pathFolder;
    }

    public String getPathFolder() {
        return pathFolder;
    }

    public void setPathFolder(String pathFolder) {
        this.pathFolder = pathFolder;
    }
}
