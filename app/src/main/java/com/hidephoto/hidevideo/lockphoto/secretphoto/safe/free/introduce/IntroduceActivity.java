package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.introduce;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.MainActivity;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.SetThemeColor;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.pinandsecurityquestion.PinActivity;

import me.relex.circleindicator.CircleIndicator;

public class IntroduceActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;
    ViewPager viewPager;
    CircleIndicator circleIndicator;
    private long backPressTime;
    private int statusApp=0;
    private LinearLayout layoutneVerAskAgaint;
    private TextView btnSettingPermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE,Color.parseColor("#303f9f"),true,false, IntroduceActivity.this);
        Window window = getWindow();
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(1792);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window2 = getWindow();
            window2.setNavigationBarColor(0);
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(67108864);
            window.setStatusBarColor(0);
        }
        setContentView(R.layout.activity_introduce);
        viewPager = findViewById(R.id.viewPager);
        circleIndicator = findViewById(R.id.circleIndicator);
        layoutneVerAskAgaint = findViewById(R.id.layoutNeverAskAgaint);
        btnSettingPermission = findViewById(R.id.btnSettingPermission);

        checkAppPermission();

        IntroAdapter introAdapter = new IntroAdapter(getSupportFragmentManager());
        viewPager.setAdapter(introAdapter);

        circleIndicator.setViewPager(viewPager);
        introAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());

        btnSettingPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri=Uri.fromParts("package",getPackageName(),null);
                intent.setData(uri);
                statusApp=1;
                startActivity(intent);
            }
        });

    }

    private void checkAppPermission() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return;
        }
        if (checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            statusApp=1;
            Intent intent = new Intent(IntroduceActivity.this, PinActivity.class);
            startActivity(intent);
        }
    }

    public void clickDone(View view){
        checkPermissions();
    }

    public void clickGrantPermission(View view){
        checkPermissions();
    }

    private void checkPermissions(){
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return;
        }
        if (IntroduceActivity.this.checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED &&
                IntroduceActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && IntroduceActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            statusApp=1;
            Intent intent = new Intent(IntroduceActivity.this,PinActivity.class);
            startActivity(intent);
        }else {
            String[] permissions= {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions,REQUEST_PERMISSION_CODE);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                statusApp=1;
                Intent intent = new Intent(IntroduceActivity.this, PinActivity.class);
                startActivity(intent);
                Toast.makeText(IntroduceActivity.this,R.string.permissionGranted,Toast.LENGTH_SHORT).show();
            }else{
                boolean showRationale = shouldShowRequestPermissionRationale(String.valueOf(permissions));
                if (!showRationale){
                    layoutneVerAskAgaint.setVisibility(View.VISIBLE);
                }
                else {
                    requestPermission();
                }
            }
        }
    }

    private void requestPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(IntroduceActivity.this);
        builder.setTitle(R.string.permissionNeeded)
                .setMessage(R.string.messagePermissionNeeded)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(IntroduceActivity.this,
                                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},REQUEST_PERMISSION_CODE );
                        if (ContextCompat.checkSelfPermission(IntroduceActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                ==PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission(IntroduceActivity.this,Manifest.permission.CAMERA)
                                        ==PackageManager.PERMISSION_GRANTED
                                && ContextCompat.checkSelfPermission(IntroduceActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                                ==PackageManager.PERMISSION_GRANTED){
                            statusApp=1;
                            Intent intent = new Intent(IntroduceActivity.this,PinActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        IntroduceActivity.this.finishAndRemoveTask();
                    }
                });
        if (ActivityCompat.shouldShowRequestPermissionRationale(IntroduceActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(IntroduceActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)||ActivityCompat.shouldShowRequestPermissionRationale(IntroduceActivity.this,
                Manifest.permission.CAMERA)) {
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#03A9F4"));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#03A9F4"));
        } else {
            ActivityCompat.requestPermissions(IntroduceActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == android.R.id.redo) {
//
//            finishAffinity();
//            return true;
//        }
//        if (id == android.R.id.home) {
//            finishAffinity();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (statusApp==0){
            finishAffinity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusApp=0;
        checkAppPermission();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (backPressTime + 2000 > System.currentTimeMillis()){
            finishAffinity();
        }else {
            Toast.makeText(IntroduceActivity.this, R.string.pressBackAgaint,Toast.LENGTH_SHORT).show();
        }
        backPressTime = System.currentTimeMillis();
    }
}