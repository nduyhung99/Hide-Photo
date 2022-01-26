package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.pinandsecurityquestion.PinActivity;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.pinandsecurityquestion.SecurityQuestionActivity;

public class SettingActivity extends AppCompatActivity {

    TextView changePin, changeSecurityQuestion, txtTimeCapture, underTimeCapture;
    SwitchCompat switchCapture, switchFingerprint;
    LinearLayout timeToTakePicture;
    String keyFingerprint = "KEY_FINGERPRINT";
    int i;
    private int statusApp=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this,R.color.button),true,false,SettingActivity.this);
        setContentView(R.layout.activity_setting);
        addControls();

        changePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusApp=1;
                Intent intent = new Intent(SettingActivity.this, PinActivity.class);
                intent.putExtra("CHANGE_PIN",1);
                startActivity(intent);
            }
        });

        switchFingerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences preferences=getSharedPreferences(keyFingerprint,MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putInt("FINGERPRINT",1);
                    editor.commit();
                }else {
                    SharedPreferences preferences=getSharedPreferences(keyFingerprint,MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putInt("FINGERPRINT",0);
                    editor.commit();
                }
            }
        });

        switchCapture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
                    switchCapture.setChecked(false);
                    Toast.makeText(SettingActivity.this,R.string.sorryCamera,Toast.LENGTH_LONG).show();
                }else {
                    if (isChecked){
                        SharedPreferences preferences=getSharedPreferences("CAPTURE_IMAGE",MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putInt("ON_OFF",1);
                        editor.commit();
                    }else {
                        SharedPreferences preferences=getSharedPreferences("CAPTURE_IMAGE",MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putInt("ON_OFF",0);
                        editor.commit();
                    }
                }
            }
        });

        timeToTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_times_capture,null);

                BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(SettingActivity.this,R.style.BottomSheetDialogTheme);
                bottomSheetDialog.setContentView(viewDialog);
                bottomSheetDialog.show();
                RadioButton radioButton1,radioButton2,radioButton3,radioButton4;
                radioButton1 = viewDialog.findViewById(R.id.radioButton1);
                radioButton2 = viewDialog.findViewById(R.id.radioButton2);
                radioButton3 = viewDialog.findViewById(R.id.radioButton3);
                radioButton4 = viewDialog.findViewById(R.id.radioButton4);
                radioButton1.setText("1 "+getString(R.string.time));
                radioButton2.setText("2 "+getString(R.string.times));
                radioButton3.setText("3 "+getString(R.string.times));
                radioButton4.setText("4 "+getString(R.string.times));
                SharedPreferences sharedPreferences = getSharedPreferences("CAPTURE",MODE_PRIVATE);
                i = sharedPreferences.getInt("TIME_CAPTURE",2);
                switch (i){
                    case 1:
                        radioButton1.setChecked(true);
                        break;
                    case 2:
                        radioButton2.setChecked(true);
                        break;
                    case 3:
                        radioButton3.setChecked(true);
                        break;
                    case 4:
                        radioButton4.setChecked(true);
                        break;
                }

                radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked==true){
                            SharedPreferences preferences=getSharedPreferences("CAPTURE",MODE_PRIVATE);
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putInt("TIME_CAPTURE",1);
                            editor.commit();
                            txtTimeCapture.setText("1 "+getString(R.string.time));
                        }
                    }
                });
                radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked==true){
                            SharedPreferences preferences=getSharedPreferences("CAPTURE",MODE_PRIVATE);
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putInt("TIME_CAPTURE",2);
                            editor.commit();
                            txtTimeCapture.setText("2 "+getString(R.string.times));
                        }
                    }
                });
                radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked==true){
                            SharedPreferences preferences=getSharedPreferences("CAPTURE",MODE_PRIVATE);
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putInt("TIME_CAPTURE",3);
                            editor.commit();
                            txtTimeCapture.setText("3 "+getString(R.string.times));
                        }
                    }
                });
                radioButton4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked==true){
                            SharedPreferences preferences=getSharedPreferences("CAPTURE",MODE_PRIVATE);
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putInt("TIME_CAPTURE",4);
                            editor.commit();
                            txtTimeCapture.setText("4 "+getString(R.string.times));
                        }
                    }
                });
            }
        });

        changeSecurityQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusApp=1;
                Intent intent = new Intent(SettingActivity.this, SecurityQuestionActivity.class);
                startActivity(intent);
            }
        });
    }

    public void clickBack(View view){
        statusApp=1;
        finish();
    }

    private void addControls() {
        changePin = findViewById(R.id.changePin);
        switchCapture = findViewById(R.id.switchCapture);
        switchFingerprint = findViewById(R.id.switchFingerprint);
        timeToTakePicture = findViewById(R.id.timesToTakePicture);
        changeSecurityQuestion = findViewById(R.id.changeSecurityQuestion);
        txtTimeCapture = findViewById(R.id.txtTimeCapture);
        underTimeCapture = findViewById(R.id.underTimeCapture);


        SharedPreferences sharedPreferences = getSharedPreferences("CAPTURE",MODE_PRIVATE);
        int i1 = sharedPreferences.getInt("TIME_CAPTURE",2);
        txtTimeCapture.setText(String.valueOf(i1)+" "+getString(R.string.times));

        SharedPreferences sharedPreferences3 = getSharedPreferences("CAPTURE_IMAGE",MODE_PRIVATE);
        int captureImage = sharedPreferences3.getInt("ON_OFF",1);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
            switchCapture.setChecked(false);
            underTimeCapture.setVisibility(View.GONE);
            timeToTakePicture.setVisibility(View.GONE);
        }else {
            if (captureImage==1){
                switchCapture.setChecked(true);
            }else {
                switchCapture.setChecked(false);
            }
        }
        SharedPreferences sharedPreferences1 = getSharedPreferences(keyFingerprint,MODE_PRIVATE);
        int statusOfFingerprint = sharedPreferences1.getInt("FINGERPRINT",1);
        int i = statusOfFingerprint;
        if (i==1){
            switchFingerprint.setChecked(true);
        }else {
            switchFingerprint.setChecked(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (statusApp==0){
            finishAffinity();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        statusApp=1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusApp=0;
    }
}