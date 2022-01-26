package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.Folder;

@Database(entities = {Folder.class}, version = 1)
public abstract class FolderDatabase extends RoomDatabase {

    private static final String DATABASE_NAME="user.db";
    private static FolderDatabase instance;

    public static synchronized FolderDatabase getInstance(Context context){
        if (instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(),FolderDatabase.class,DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract FolderDAO folderDAO();
}
