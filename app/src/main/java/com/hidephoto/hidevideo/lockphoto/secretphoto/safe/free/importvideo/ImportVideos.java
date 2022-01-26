package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importvideo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.AndroidXI;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.FolderAdapter;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.MainActivity;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.SetThemeColor;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.Butket;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.ImageSelected;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.ImportPhotos;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.Photo;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.PhotoAdapter;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.pinandsecurityquestion.SecurityQuestionActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImportVideos extends AppCompatActivity {

    private static final String TAG = "tag";
    private String folder_name;
    private File[] listFile;
    private RecyclerView rcvImportVideos;
    private ButketVideoAdapter butketVideoAdapter;
    private static ArrayList<ButketVideo> buckets = new ArrayList<>();
    private int statusApp=0;

    private RecyclerView rcvVideoInFolder;
    private VideoAdapter videoAdapter;
    private ArrayList<Video> listVideo = new ArrayList<Video>();
    private ArrayList<Video> listVideoSelected = new ArrayList<Video>();
    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this,R.color.button),true,false, ImportVideos.this);
        setContentView(R.layout.activity_import_videos);

        Intent intent = getIntent();
        folder_name = intent.getStringExtra("FOLDER_NAME_2");

        rcvImportVideos = findViewById(R.id.rcvImportVideos);
        butketVideoAdapter = new ButketVideoAdapter(ImportVideos.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ImportVideos.this,RecyclerView.VERTICAL,false);
        rcvImportVideos.setLayoutManager(linearLayoutManager);
        butketVideoAdapter.setData(getImageBuckets(this));
        rcvImportVideos.setAdapter(butketVideoAdapter);
        rcvImportVideos.setHasFixedSize(true);

        rcvVideoInFolder = findViewById(R.id.rcvVideoInFolder);
        videoAdapter = new VideoAdapter(ImportVideos.this,listVideo);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ImportVideos.this,3);
        rcvVideoInFolder.setLayoutManager(gridLayoutManager);
        videoAdapter.setData(listVideo);
        rcvVideoInFolder.setAdapter(videoAdapter);
        rcvVideoInFolder.setHasFixedSize(true);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()== Activity.RESULT_OK){
                    statusApp=1;
                    deleteCopiedData();
                    finish();
                }else {
                    deleteCopiedData();
                    deleteFileHide();
                }
            }
        });

        butketVideoAdapter.setOnItemClickListener(new FolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (buckets.isEmpty()){
                    return;
                }
                ButketVideo butketVideo = buckets.get(position);
                String pathFolder= butketVideo.getFirstImageContainedPath().substring(0,butketVideo.getFirstImageContainedPath().lastIndexOf("/"));
                File file = new File(pathFolder);
                loadDataVideoAdapter(file);
            }
        });

        videoAdapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Video video = listVideo.get(position);
                if (listVideo.get(position).isChecked == false){
                    listVideoSelected.add(video);
                }else {
                    for (int i = 0; i<listVideoSelected.size(); i++){
                        if (listVideoSelected.get(i).getPathVideo().equals(listVideo.get(position).getPathVideo())){
                            listVideoSelected.remove(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void deleteCopiedData() {
        SharedPreferences sharedPreferences = getSharedPreferences("COPIED",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("copied",null);
        editor.commit();
    }

    private void deleteFileHide() {
        for (int i=0; i<listVideoSelected.size(); i++){
            String fileName = String.format("%s.txt",listVideoSelected.get(i).getPathVideo());
            String fileName1 = fileName.replaceAll("/","&");
            String path = MainActivity.getStore(ImportVideos.this) +"/.HidePhoto/Video/"+folder_name;
            File outFile = new File(path,fileName1);
            outFile.delete();
        }
        Toast.makeText(ImportVideos.this, getString(R.string.deny), Toast.LENGTH_SHORT).show();
    }

    private void loadDataVideoAdapter(File file) {
        if (file.isDirectory()) {
            listFile = file.listFiles();
            ArrayList<Video> listVideo1 = new ArrayList<Video>();
            for (int i = 0; i < listFile.length; i++) {
                String path = listFile[i].getAbsolutePath();
                if (path.endsWith(".mp4") || path.endsWith(".avi") || path.endsWith(".mov") || path.endsWith(".flv") || path.endsWith(".wmv")) {
                    listVideo1.add(new Video(path, false));
                }
            }
            listVideo = reverse(listVideo1);

            if (listVideoSelected.size()>0){
                for (int i=0; i<listVideo.size(); i++){
                    for (int j=0; j<listVideoSelected.size(); j++){
                        if (listVideo.get(i).getPathVideo().equals(listVideoSelected.get(j).getPathVideo())){
                            listVideo.get(i).setChecked(true);
                        }
                    }
                }
            }

            if (listVideo.isEmpty()){
                Toast.makeText(ImportVideos.this,R.string.haveNoVideoInThisFolder,Toast.LENGTH_SHORT).show();
            }else {
                rcvImportVideos.setVisibility(View.GONE);
                rcvVideoInFolder.setVisibility(View.VISIBLE);
            }

            videoAdapter.setData(listVideo);
            rcvVideoInFolder.setAdapter(videoAdapter);
        }
    }

    public void clickBackVideo(View view){
        if (rcvVideoInFolder.getVisibility()==View.VISIBLE){
            rcvVideoInFolder.setVisibility(View.GONE);
            rcvImportVideos.setVisibility(View.VISIBLE);
        }else {
            statusApp=1;
            finish();
        }
    }

    public void tickVideo(View view){
        if (listVideoSelected.size()>0){
//            StringBuilder stringBuilder = new StringBuilder();
//            for (int i=0; i< listVideoSelected.size(); i++){
//                stringBuilder.append(listVideoSelected.get(i).getPathVideo());
//                stringBuilder.append("\n");
//            }
//            Toast.makeText(ImportVideos.this,stringBuilder.toString().trim(),Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                AlertDialog.Builder builder = new AlertDialog.Builder(ImportVideos.this);
                builder.setTitle(R.string.attention)
                        .setMessage(R.string.deleteFileRootVideo)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hideVideo();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#03A9F4"));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#03A9F4"));
            }else{
                hideVideo();
            }
        }else {
            Toast.makeText(ImportVideos.this,R.string.noneIsSelected,Toast.LENGTH_SHORT).show();
        }
    }

    private void hideVideo() {
        Set<String> copied= new HashSet<>();
        List<String> listCopied = new ArrayList<>();
        ArrayList<Uri> listUri = new ArrayList<>();
        for (int i=0; i<listVideoSelected.size(); i++){
            String fileName = String.format("%s.txt",listVideoSelected.get(i).getPathVideo());
            String fileName1 = fileName.replaceAll("/","&");
            String path = MainActivity.getStore(ImportVideos.this) +"/.HidePhoto/Video/"+folder_name;
            File outFile = new File(path,fileName1);
            Uri uri = Uri.fromFile(new File(listVideoSelected.get(i).getPathVideo()));
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                copyStream(inputStream,fileOutputStream);
                fileOutputStream.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
                File file = new File(listVideoSelected.get(i).getPathVideo());
                file.delete();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outFile)));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            }else {
                String data = listVideoSelected.get(i).getPathVideo();
                listUri.add(getUriFromPath(data));
                listCopied.add(outFile.getAbsolutePath());
            }
        }


        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
            statusApp=1;
            finish();
        }else {
            copied.addAll(listCopied);
            SharedPreferences sharedPreferences = getSharedPreferences("COPIED",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("copied",copied);
            editor.commit();

            AndroidXI androidXI = new AndroidXI(ImportVideos.this);
            androidXI.deleteList(activityResultLauncher,listUri);
        }
    }

    private Uri getUriFromPath(String data) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Uri uri1=null;
        ContentResolver contentResolver = getContentResolver();
        String[] projection ={MediaStore.Video.VideoColumns._ID};
        Cursor cursor = contentResolver.query(uri,projection,MediaStore.Video.VideoColumns.DATA+"=?",new String[]{data},null);
        while (cursor.moveToNext()){
            uri1=Uri.withAppendedPath(uri,cursor.getString(cursor.getColumnIndex(projection[0])));
        }
        return uri1;
    }

    public static List<ButketVideo> getImageBuckets(Context mContext){
        ArrayList<ButketVideo> lol = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String [] projection = {MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA};

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        ArrayList<String> listPath = new ArrayList<>();
        if(cursor != null){
            File file;
            while (cursor.moveToNext()){
                String bucketPath = cursor.getString(cursor.getColumnIndex(projection[0]));
                String fisrtImage = cursor.getString(cursor.getColumnIndex(projection[1]));
                file = new File(fisrtImage);
                if (file.exists()) {
                    if (listPath.contains(bucketPath)){
                        for (int i=0; i<lol.size(); i++){
                            if (lol.get(i).getName().equals(bucketPath)){
                                lol.remove(i);
                                lol.add(new ButketVideo(bucketPath,fisrtImage));
                                break;
                            }
                        }
                    }else {
                        lol.add(new ButketVideo(bucketPath,fisrtImage));
                        listPath.add(bucketPath);
                    }
//                    buckets.add(new Butket(bucketPath, fisrtImage));
//                    listPath.add(bucketPath);
                }
            }
            cursor.close();
        }
//        if (buckets.isEmpty()){
//            Log.d(TAG, "savedInstanceState is null");
//        }
        buckets = lol;
        return buckets;
    }

    @Override
    public void onBackPressed() {
        if (rcvVideoInFolder.getVisibility()==View.VISIBLE){
            rcvVideoInFolder.setVisibility(View.GONE);
            rcvImportVideos.setVisibility(View.VISIBLE);
        }else {
            statusApp=1;
            super.onBackPressed();
        }

    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (statusApp==0){
            finishAffinity();
        }
    }

    public ArrayList<Video> reverse(ArrayList<Video> list) {
        if(list.size() > 1) {
            Video value = list.remove(0);
            reverse(list);
            list.add(value);
        }
        return list;
    }
}