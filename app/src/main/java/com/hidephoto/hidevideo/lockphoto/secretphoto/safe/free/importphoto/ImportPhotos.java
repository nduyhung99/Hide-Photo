package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.AndroidXI;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.FolderAdapter;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.MainActivity;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.SetThemeColor;

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

public class ImportPhotos extends AppCompatActivity{

    private static final String TAG = "tag";
    private static final int REQUEST_PERM_DELETE = 1;
    private File[] listFile;
    private RecyclerView rcvImportPhotos;
    private ButketAdapter butketAdapter;
    private static ArrayList<Butket> buckets = new ArrayList<>();
    private String folder_name;
    private int statusApp=0;

    private RecyclerView rcvPhotoInFolder;
    private PhotoAdapter photoAdapter;
    private ArrayList<Photo> listPhoto = new ArrayList<Photo>();

    private TextView countImageSelected;
    private RecyclerView rcvImageSelected;
    private ImageSelectedAdapter imageSelectedAdapter;
    private ArrayList<ImageSelected> listImageSelected = new ArrayList<ImageSelected>();
    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE,ContextCompat.getColor(this,R.color.button),true,false, ImportPhotos.this);
        setContentView(R.layout.activity_import_photos);

        Intent intent = getIntent();
        folder_name = intent.getStringExtra("FOLDER_NAME_1");

        rcvImportPhotos = findViewById(R.id.rcvImportPhotos);
        countImageSelected = findViewById(R.id.countImageSelected);
        rcvImageSelected = findViewById(R.id.rcvImageSelected);
        rcvPhotoInFolder = findViewById(R.id.rcvPhotoInFolder);

        countImageSelected.setText("0 "+getString(R.string.imageSelected));

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(ImportPhotos.this,RecyclerView.VERTICAL,false);
        rcvImportPhotos.setLayoutManager(linearLayoutManager);
        butketAdapter= new ButketAdapter(this);
        butketAdapter.setData(getImageBuckets(this));
        rcvImportPhotos.setAdapter(butketAdapter);
        rcvImportPhotos.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        rcvPhotoInFolder.setLayoutManager(gridLayoutManager);
        photoAdapter = new PhotoAdapter(ImportPhotos.this,listPhoto);
        rcvPhotoInFolder.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        rcvImageSelected.setLayoutManager(linearLayoutManager1);
        imageSelectedAdapter = new ImageSelectedAdapter(ImportPhotos.this, listImageSelected);
        imageSelectedAdapter.setData(listImageSelected);
        rcvImageSelected.setAdapter(imageSelectedAdapter);
        rcvImageSelected.setHasFixedSize(true);

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


        imageSelectedAdapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                for (int i=0; i<listPhoto.size(); i++){
                    if (listPhoto.get(i).getPathPhoto().equals(listImageSelected.get(position).getPathImageSelected())){
                        listPhoto.get(i).setChecked(false);
                    }
                }
                listImageSelected.remove(position);
                countImageSelected.setText(listImageSelected.size()+" "+getString(R.string.imageSelected));

                imageSelectedAdapter.setData(listImageSelected);
                photoAdapter.setData(listPhoto);

                rcvImageSelected.setAdapter(imageSelectedAdapter);
                rcvPhotoInFolder.setAdapter(photoAdapter);

            }
        });

        photoAdapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ImageSelected imageSelected = new ImageSelected(listPhoto.get(position).getPathPhoto());
                if (listPhoto.get(position).isChecked == false){
                    listImageSelected.add(imageSelected);
                }else {
                    for (int i = 0; i<listImageSelected.size(); i++){
                        if (listImageSelected.get(i).getPathImageSelected().equals(listPhoto.get(position).getPathPhoto())){
                            listImageSelected.remove(i);
                            break;
                        }
                    }
                }
                countImageSelected.setText(listImageSelected.size()+" "+getString(R.string.imageSelected));
                imageSelectedAdapter.setData(listImageSelected);
                rcvImageSelected.setAdapter(imageSelectedAdapter);
            }
        });


        butketAdapter.setOnItemClickListener(new FolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (buckets.isEmpty()){
                    return;
                }
                Butket butket = buckets.get(position);
                String pathFolder= butket.getFirstImageContainedPath().substring(0,butket.getFirstImageContainedPath().lastIndexOf("/"));
                File file = new File(pathFolder);
                loadDataPhotoAdapter(file);
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
        for (int i = 0; i < listImageSelected.size(); i++) {
            String fileName = String.format("%s.txt",listImageSelected.get(i).getPathImageSelected());
            String fileName1 = fileName.replaceAll("/","&");
            String path = MainActivity.getStore(ImportPhotos.this) +"/.HidePhoto/Photo/"+folder_name;
            File outFile = new File(path,fileName1);
            outFile.delete();
        }
        Toast.makeText(ImportPhotos.this, getString(R.string.deny), Toast.LENGTH_SHORT).show();
    }

    private void loadDataPhotoAdapter(File file) {
        if (file.isDirectory()){
            listFile = file.listFiles();
            ArrayList<Photo> listPhoto1 = new ArrayList<Photo>();
            for (int i=0; i<listFile.length; i++){
                String path= listFile[i].getAbsolutePath();
                if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")||path.endsWith(".gif")){
                    listPhoto1.add(new Photo(path,false));
                }
            }
            listPhoto=reverse(listPhoto1);
            if (listImageSelected.size()>0){
                for (int i=0; i<listPhoto.size(); i++){
                    for (int j=0; j<listImageSelected.size(); j++){
                        if (listPhoto.get(i).getPathPhoto().equals(listImageSelected.get(j).getPathImageSelected())){
                            listPhoto.get(i).setChecked(true);
                        }
                    }
                }
            }

            if (listPhoto.isEmpty()){
                Toast.makeText(ImportPhotos.this,R.string.haveNoImageInThisFolder,Toast.LENGTH_SHORT).show();
            }else {
                rcvImportPhotos.setVisibility(View.GONE);
                rcvPhotoInFolder.setVisibility(View.VISIBLE);
            }
            photoAdapter.setData(listPhoto);
            rcvPhotoInFolder.setAdapter(photoAdapter);
        }
    }


    public static List<Butket> getImageBuckets(Context mContext){
        ArrayList<Butket> lol = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String [] projection = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED);
        ArrayList<String> listPath = new ArrayList<>();
        if(cursor != null){
            while (cursor.moveToNext()){
                String bucketPath = cursor.getString(cursor.getColumnIndex(projection[0]));
                String fisrtImage = cursor.getString(cursor.getColumnIndex(projection[1]));
                File file = new File(fisrtImage);
                if (file.exists()) {
                    if (listPath.contains(bucketPath)){
                        for (int i=0; i<lol.size(); i++){
                            if (lol.get(i).getName()!=null){
                                if (lol.get(i).getName().equals(bucketPath)){
                                    lol.remove(i);
                                    lol.add(new Butket(bucketPath,fisrtImage));
                                    break;
                                }
                            }

                        }
                    }else {
                        lol.add(new Butket(bucketPath,fisrtImage));
                        listPath.add(bucketPath);
                    }
//                    buckets.add(new Butket(bucketPath, fisrtImage));
//                    listPath.add(bucketPath);
                }
            }
            cursor.close();
        }
//        if (lol.isEmpty()){
//            Log.d(TAG, "savedInstanceState is null");
//        }
        buckets = lol;
        return buckets;
    }

    public void clickBack(View view){
        if (rcvPhotoInFolder.getVisibility()==View.VISIBLE){
            rcvPhotoInFolder.setVisibility(View.GONE);
            rcvImportPhotos.setVisibility(View.VISIBLE);
        }else {
            statusApp=1;
            finish();
        }
    }

    public void tick(View view){
        if (listImageSelected.size()>0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                AlertDialog.Builder builder = new AlertDialog.Builder(ImportPhotos.this);
                builder.setTitle(R.string.attention)
                        .setMessage(R.string.deleteFileRoot)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                hidePhoto();
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
                hidePhoto();
            }
        }else {
            Toast.makeText(ImportPhotos.this,R.string.noneIsSelected,Toast.LENGTH_SHORT).show();
        }
    }

    private void hidePhoto() {
        ArrayList<Uri> listUri = new ArrayList<>();
        Set<String> copied= new HashSet<>();
        List<String> listCopied = new ArrayList<>();
        for (int i=0; i<listImageSelected.size(); i++) {
            String fileName = String.format("%s.txt", listImageSelected.get(i).getPathImageSelected());
            String fileName1 = fileName.replaceAll("/", "&");
            String path = MainActivity.getStore(ImportPhotos.this) + "/.HidePhoto/Photo/" + folder_name;
            File outFile = new File(path, fileName1);
            Uri uri = Uri.fromFile(new File(listImageSelected.get(i).getPathImageSelected()));
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File file = new File(listImageSelected.get(i).getPathImageSelected());
//                long mediaID = getFilePathToMediaID(file.getAbsolutePath(),ImportPhotos.this);
//                Uri Uri_one = ContentUris.withAppendedId( MediaStore.Images.Media.getContentUri("external"),mediaID);
//                List<Uri> uris=new ArrayList<>();
//                uris.add(Uri_one);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                file.delete();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outFile)));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            } else {
                String data = listImageSelected.get(i).getPathImageSelected();
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

            AndroidXI androidXI = new AndroidXI(ImportPhotos.this);
            androidXI.deleteList(activityResultLauncher,listUri);
        }
    }

    private Uri getUriFromPath(String data) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uri1=null;
        ContentResolver contentResolver = getContentResolver();
        String[] projection ={MediaStore.Images.ImageColumns._ID};
        Cursor cursor = contentResolver.query(uri,projection,MediaStore.Images.ImageColumns.DATA+"=?",new String[]{data},null);
        while (cursor.moveToNext()){
            uri1=Uri.withAppendedPath(uri,cursor.getString(cursor.getColumnIndex(projection[0])));
        }
        return uri1;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onBackPressed() {
        if (rcvImportPhotos.getVisibility() == View.VISIBLE){
            super.onBackPressed();
            statusApp=1;
            listPhoto=null;
            photoAdapter.setData(listPhoto);
        }else {
            rcvImportPhotos.setVisibility(View.VISIBLE);
            rcvPhotoInFolder.setVisibility(View.GONE);
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

    public long getFilePathToMediaID(String songPath, Context context)
    {
        long id = 0;
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.Images.Media.DATA;
        String[] selectionArgs = {songPath};
        String[] projection = {MediaStore.Images.Media._ID};
        String sortOrder = MediaStore.Images.Media.TITLE + " ASC";

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                id = Long.parseLong(cursor.getString(idIndex));
            }
        }

        return id;
    }

    private void requestDeletePermission(List<Uri> uriList){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            PendingIntent pi = MediaStore.createDeleteRequest(ImportPhotos.this.getContentResolver(), uriList);

            try {
                startIntentSenderForResult(pi.getIntentSender(), REQUEST_PERM_DELETE, null, 0, 0,
                        0);
            } catch (IntentSender.SendIntentException e) { }
        }
    }

    public ArrayList<Photo> reverse(ArrayList<Photo> list) {
        if(list.size() > 1) {
            Photo value = list.remove(0);
            reverse(list);
            list.add(value);
        }
        return list;
    }
}