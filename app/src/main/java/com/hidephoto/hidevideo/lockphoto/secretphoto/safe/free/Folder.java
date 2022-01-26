package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free;

import android.graphics.drawable.Drawable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity (tableName = "folder")
public class Folder implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int image;
    private String firstPath;
    private String nameFolder;
    private int countItem;
    private String pathFolder;

    public String getFirstPath() {
        return firstPath;
    }

    public void setFirstPath(String firstPath) {
        this.firstPath = firstPath;
    }

    public String getPathFolder() {
        return pathFolder;
    }

    public void setPathFolder(String pathFolder) {
        this.pathFolder = pathFolder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getNameFolder() {
        return nameFolder;
    }

    public void setNameFolder(String nameFolder) {
        this.nameFolder = nameFolder;
    }

    public int getCountItem() {
        return countItem;
    }

    public void setCountItem(int countItem) {
        this.countItem = countItem;
    }

    public Folder() {
    }

    public Folder(int image, String nameFolder, int countItem, String pathFolder, String firstPath) {
        this.image = image;
        this.nameFolder = nameFolder;
        this.countItem = countItem;
        this.pathFolder = pathFolder;
        this.firstPath  = firstPath;
    }
}
