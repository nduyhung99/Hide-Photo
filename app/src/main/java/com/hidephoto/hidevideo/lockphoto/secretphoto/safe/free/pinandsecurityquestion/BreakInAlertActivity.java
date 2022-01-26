package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.pinandsecurityquestion;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.FileUltils;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.SetThemeColor;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.Photo;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.viewfileinfolder.ViewFileInFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R.color.biometric_error_color;

public class BreakInAlertActivity extends AppCompatActivity {

    private RecyclerView rcvBreakInAlert;
    private ArrayList<ImageSecurity> mListImageSecurity = new ArrayList<ImageSecurity>();
    private ImageSecurityAdapter imageSecurityAdapter;
    private File[] listFile;
    private LinearLayout layoutSecurityImage;
    private ImageView securityImage, btnDeleteAlert;
    private TextView dateAndTimeOfImage1,passwordWrong1;
    private View view;
    private int statusApp=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this,R.color.button),true,false, BreakInAlertActivity.this);
        setContentView(R.layout.activity_break_in_alert);
        addControls();


        imageSecurityAdapter.setOnItemClickListener(new ImageSecurityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                rcvBreakInAlert.setVisibility(View.GONE);
                layoutSecurityImage.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
                dateAndTimeOfImage1.setText(mListImageSecurity.get(position).getDateAndTime());
                passwordWrong1.setText(mListImageSecurity.get(position).getPasswordWrong());
                Bitmap myBitmap = BitmapFactory.decodeFile(mListImageSecurity.get(position).getPath());
                securityImage.setImageBitmap(FileUltils.modifyOrientation(BreakInAlertActivity.this,myBitmap,mListImageSecurity.get(position).path));
//                securityImage.setImageBitmap(myBitmap);

                btnDeleteAlert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAlert(mListImageSecurity.get(position).path);
                    }
                });
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (mListImageSecurity.isEmpty()){
                    Toast.makeText(BreakInAlertActivity.this,R.string.haveNoImage,Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(BreakInAlertActivity.this);
                builder.setTitle(R.string.delete)
                        .setMessage(R.string.deleteAllDetails)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(getStore(BreakInAlertActivity.this));
                                if (file.isDirectory()){
                                    listFile = file.listFiles();
                                    ArrayList<ImageSecurity> list = new ArrayList<ImageSecurity>();
                                    for (int i=0; i<listFile.length; i++){
                                        String path = listFile[i].getAbsolutePath();
                                        if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")){
                                            File file1 = new File(path);
                                            file1.delete();
                                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file1)));
                                        }
                                    }
                                    dialog.dismiss();
                                }
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
            }
        });

    }

    private void deleteAlert(String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BreakInAlertActivity.this);
        builder.setTitle(R.string.delete)
                .setMessage(R.string.deleteFile)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(path);
                        file.delete();
                        imageSecurityAdapter.setData(getData());
                        rcvBreakInAlert.setVisibility(View.VISIBLE);
                        layoutSecurityImage.setVisibility(View.GONE);
                        view.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#03A9F4"));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#03A9F4"));
    }

    private void addControls() {
        rcvBreakInAlert = findViewById(R.id.rcvBreakInAlert);
        layoutSecurityImage = findViewById(R.id.layoutSecurityImage);
        securityImage  = findViewById(R.id.securityImage);
        dateAndTimeOfImage1 = findViewById(R.id.dateAndTimeOfImage1);
        passwordWrong1 = findViewById(R.id.passwordWrong1);
        btnDeleteAlert = findViewById(R.id.btnDeleteAlert);
        view = findViewById(R.id.deleteAll);
        imageSecurityAdapter = new ImageSecurityAdapter(BreakInAlertActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BreakInAlertActivity.this,RecyclerView.VERTICAL,false);
        rcvBreakInAlert.setLayoutManager(linearLayoutManager);
        imageSecurityAdapter.setData(getData());
        rcvBreakInAlert.setAdapter(imageSecurityAdapter);
        rcvBreakInAlert.setHasFixedSize(true);
    }

    private ArrayList<ImageSecurity> getData() {
        File file = new File(getStore(BreakInAlertActivity.this));
        if (file.isDirectory()){
            listFile = file.listFiles();
            ArrayList<ImageSecurity> list = new ArrayList<ImageSecurity>();
            for (int i=0; i<listFile.length; i++){
                String path = listFile[i].getAbsolutePath();
                if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")){
                    String name = listFile[i].getName();
                    String dateAndTime = name.substring(4,23).replaceAll("&","/");
                    String passwordWrong = name.substring(24,name.lastIndexOf("."));
                    list.add(new ImageSecurity(path,dateAndTime,passwordWrong));
                }
            }
            mListImageSecurity = list;
        }
        return reverse(mListImageSecurity);
    }

    public void clickBack(View view){
        if (rcvBreakInAlert.getVisibility()==View.GONE){
            rcvBreakInAlert.setVisibility(View.VISIBLE);
            layoutSecurityImage.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }else {
            statusApp=1;
            super.onBackPressed();
        }
    }

    public void clickDeleteAll(View view){

    }

    public static String getStore(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File f = c.getExternalFilesDir(null);
            if (f != null)
                return f.getAbsolutePath();
            else
                return "/storage/emulated/0/Android/data/" + c.getPackageName();
        } else {
            return Environment.getExternalStorageDirectory()
                    + "/Android/data/" + c.getPackageName();
        }
    }

    @Override
    public void onBackPressed() {
        if (rcvBreakInAlert.getVisibility()==View.GONE){
            rcvBreakInAlert.setVisibility(View.VISIBLE);
            layoutSecurityImage.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }else {
            statusApp=1;
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageSecurityAdapter.setData(getData());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (statusApp==0){
            finishAffinity();
        }
    }

    public ArrayList<ImageSecurity> reverse(ArrayList<ImageSecurity> list) {
        if(list.size() > 1) {
            ImageSecurity value = list.remove(0);
            reverse(list);
            list.add(value);
        }
        return list;
    }
}