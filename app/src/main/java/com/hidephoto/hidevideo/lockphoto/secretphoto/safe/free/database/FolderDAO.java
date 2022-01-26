package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.Folder;

import java.util.List;

@Dao
public interface FolderDAO {
    @Insert
    void insertFolder(Folder folder);

//    @Query("select * from folder where checkFolder==1")
//    List<Folder> getListFolderPhoto();
//
//    @Query("select * from folder where checkFolder==0")
//    List<Folder> getListFolderVideo();

    @Query("select * from folder where nameFolder= :folderName")
    List<Folder> checkUser(String folderName);
}
