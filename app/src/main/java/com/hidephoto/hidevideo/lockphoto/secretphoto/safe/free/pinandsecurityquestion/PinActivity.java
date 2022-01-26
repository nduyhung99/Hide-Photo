package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.pinandsecurityquestion;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.MainActivity;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.SetThemeColor;

import java.io.File;
import java.util.concurrent.Executor;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PinActivity extends AppCompatActivity {

    private TextView statusPin, forgetPin;
    private EditText edtPassword;
    int countWrongPin = 0;
    String keyPin="KEY_PIN",pin="",count = "",count1 = "",keyFingerprint = "KEY_FINGERPRINT",answer="";
    int offCapture,offCaptureIntent=0;
    int i,status=0, timeCapture;
    private int statusApp=0;
    static int captureImage;
    private TextView one,two,three,four,five,six,seven,eight,nine,zero;
    private ImageView btnFingerprint;
    private TextView statusFingerprint;
    private ImageView btnCheckPin, btnBackSpace;
    private long backPressTime;
    private AutoFitTextureView textureView;
    CameraControllerV2WithPreview ccv2WithPreview;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE,Color.parseColor("#303f9f"),true,false, PinActivity.this);
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
        setContentView(R.layout.activity_pin);
        addControls();


        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getText().length()<6){
                    int pos = edtPassword.getSelectionStart();
                    edtPassword.setText(updateText("1", edtPassword.getText().toString(), pos));
                    edtPassword.setSelection(pos + 1);
                    closeCameraButton();
                }
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getText().length()<6){
                    int pos = edtPassword.getSelectionStart();
                    edtPassword.setText(updateText("2", edtPassword.getText().toString(), pos));
                    edtPassword.setSelection(pos + 1);
                    closeCameraButton();
                }
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getText().length()<6){
                    int pos = edtPassword.getSelectionStart();
                    edtPassword.setText(updateText("3", edtPassword.getText().toString(), pos));
                    edtPassword.setSelection(pos + 1);
                    closeCameraButton();
                }
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getText().length()<6){
                    int pos = edtPassword.getSelectionStart();
                    edtPassword.setText(updateText("4", edtPassword.getText().toString(), pos));
                    edtPassword.setSelection(pos + 1);
                    closeCameraButton();
                }
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getText().length()<6){
                    int pos = edtPassword.getSelectionStart();
                    edtPassword.setText(updateText("5", edtPassword.getText().toString(), pos));
                    edtPassword.setSelection(pos + 1);
                    closeCameraButton();
                }
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getText().length()<6){
                    int pos = edtPassword.getSelectionStart();
                    edtPassword.setText(updateText("6", edtPassword.getText().toString(), pos));
                    edtPassword.setSelection(pos + 1);
                    closeCameraButton();
                }
            }
        });
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getText().length()<6){
                    int pos = edtPassword.getSelectionStart();
                    edtPassword.setText(updateText("7", edtPassword.getText().toString(), pos));
                    edtPassword.setSelection(pos + 1);
                    closeCameraButton();
                }
            }
        });
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getText().length()<6){
                    int pos = edtPassword.getSelectionStart();
                    edtPassword.setText(updateText("8", edtPassword.getText().toString(), pos));
                    edtPassword.setSelection(pos + 1);
                    closeCameraButton();
                }
            }
        });
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getText().length()<6){
                    int pos = edtPassword.getSelectionStart();
                    edtPassword.setText(updateText("9", edtPassword.getText().toString(), pos));
                    edtPassword.setSelection(pos + 1);
                    closeCameraButton();
                }
            }
        });
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getText().length()<6){
                    int pos = edtPassword.getSelectionStart();
                    edtPassword.setText(updateText("0", edtPassword.getText().toString(), pos));
                    edtPassword.setSelection(pos + 1);
                    closeCameraButton();
                }
            }
        });

        btnBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cursorPosEnd = edtPassword.getSelectionEnd();
                int textLength = edtPassword.getText().length();

                if (cursorPosEnd != 0 && textLength != 0){
                    SpannableStringBuilder selection = (SpannableStringBuilder) edtPassword.getText();
                    selection.replace(cursorPosEnd - 1, cursorPosEnd, "");
                    //updates the text
                    edtPassword.setText(selection);
                    //puts the cursor back in the correct spot
                    edtPassword.setSelection(cursorPosEnd - 1);
                }
                closeCameraButton();
            }
        });

        btnCheckPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeCameraButton();
                if (i==1){
                    changePin();
                }else {
                    enterPin();
                }

            }
        });

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(PinActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (answer.isEmpty()){
                    if (offCaptureIntent==0){
                        closeCamera();
                    }
                    Intent intent = new Intent(PinActivity.this,SecurityQuestionActivity.class);
                    startActivity(intent);
                }else {
                    if (offCaptureIntent==0){
                        closeCamera();
                    }
                    Intent intent = new Intent(PinActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.login))
                .setDescription(getString(R.string.useFingerprint))
                .setNegativeButtonText(getString(R.string.cancel))
                .build();

        btnFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pin.isEmpty()){
                    Toast.makeText(PinActivity.this,R.string.pleaseCreateANewPin,Toast.LENGTH_SHORT).show();
                }else {
//                    create BiometricManager and check if device can use the fingerprint or not
                    BiometricManager biometricManager = BiometricManager.from(PinActivity.this);
                    switch (biometricManager.canAuthenticate()){
                        case BiometricManager.BIOMETRIC_SUCCESS:
//                        statusFingerprint.setText("You can use the fingerprint sensor to login");
                            biometricPrompt.authenticate(promptInfo);
                            break;
                        case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                            YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(statusPin);
                            statusFingerprint.setText(R.string.fingerprintUnavailable);
                            break;
                        case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                            YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(statusPin);
                            statusFingerprint.setText(R.string.haveNoFingerprintSendor);
                            break;
                        case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                            YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(statusPin);
                            statusFingerprint.setText(R.string.haveNoFingerprintSaved);
                            break;
                    }
                }

            }
        });

        forgetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (captureImage==1 && offCapture==0){
//                    ccv2WithPreview.closeCamera();
                    textureView.setVisibility(View.GONE);
                }
                statusApp=1;
                Intent intent = new Intent(PinActivity.this, SecurityQuestionActivity.class);
                intent.putExtra("FORGET_PIN",1);
                startActivity(intent);
            }
        });
    }

    private void changePin() {
        if (edtPassword.getText().length()<6){
            YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(statusPin);
            statusPin.setText(R.string.digitOfPin);
            return;
        }
        if (status==0){
            if (!edtPassword.getText().toString().equals(pin)){
                YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(statusPin);
                statusPin.setText(R.string.wrongPin);
                edtPassword.setText("");
            }else {
                statusPin.setText(R.string.enterANewPin);
                edtPassword.setText("");
                status=1;
            }
        }else if (count1.isEmpty()){
                count1 = edtPassword.getText().toString();
                statusPin.setText(R.string.confirmPin);
                edtPassword.setText("");
                }else   if (!edtPassword.getText().toString().equals(count1)){
                    YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(statusPin);
                    statusPin.setText(R.string.wrongPin);
                    edtPassword.setText("");
                    }else {
                        pin = count1;
                        SharedPreferences preferences=getSharedPreferences(keyPin,MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("PIN",pin);
                        editor.commit();
                        Toast.makeText(PinActivity.this,R.string.changePinSuccessfully,Toast.LENGTH_SHORT).show();
                        statusApp=1;
                        finish();
                    }

    }

    private void enterPin() {
        if (edtPassword.getText().length()<6){
            YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(statusPin);
            statusPin.setText(R.string.digitOfPin);
            return;
        }
        if (pin.isEmpty()){
            if (count.isEmpty()){
                count = edtPassword.getText().toString();
                statusPin.setText(R.string.confirmPin);
                edtPassword.setText("");
            }else if (!edtPassword.getText().toString().equals(count)){
                YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(statusPin);
                statusPin.setText(R.string.wrongPin);
                edtPassword.setText("");
            }else {
                pin = count;
                if (offCaptureIntent==0){
                    closeCamera();
                }
                Intent intent = new Intent(PinActivity.this,SecurityQuestionActivity.class);
                intent.putExtra("PIN",pin);
                startActivity(intent);
            }
        }else if (!edtPassword.getText().toString().equals(pin)){
            if (countWrongPin<2){
                if (countWrongPin==timeCapture-1 && captureImage==1){
                    YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(statusPin);
                    statusPin.setText(R.string.wrongPin);
                    ccv2WithPreview.takePicture(edtPassword.getText().toString());
                    edtPassword.setText("");
                    offCapture=1;
                    countWrongPin++;
//                    if (offCapture==1){
//                        ccv2WithPreview.closeCamera();
//                    }
                }else {
                    YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(statusPin);
                    statusPin.setText(R.string.wrongPin);
                    edtPassword.setText("");
                    countWrongPin++;
                }

            }else {
                if (countWrongPin==timeCapture-1 && captureImage==1){
                    YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(statusPin);
                    statusPin.setText(R.string.wrongPin);
                    ccv2WithPreview.takePicture(edtPassword.getText().toString());
                    offCapture=1;
                    edtPassword.setText("");
                    forgetPin.setVisibility(View.VISIBLE);
                    countWrongPin++;
//                    if (offCapture==1){
//                        ccv2WithPreview.closeCamera();
//                    }
                }else {
                    YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(statusPin);
                    statusPin.setText(R.string.wrongPin);
                    edtPassword.setText("");
                    countWrongPin++;
                    forgetPin.setVisibility(View.VISIBLE);
                }
            }
        }else if (!answer.isEmpty()){
            if (offCaptureIntent==0){
                closeCamera();
            }
            Intent intent = new Intent(PinActivity.this,MainActivity.class);
            startActivity(intent);
        }else {
            if (offCaptureIntent==0){
                closeCamera();
            }
            Intent intent = new Intent(PinActivity.this,SecurityQuestionActivity.class);
            startActivity(intent);
        }
    }

    private void addControls() {
        edtPassword=findViewById(R.id.edtPassword);
        one=findViewById(R.id.one);
        two=findViewById(R.id.two);
        three=findViewById(R.id.three);
        four=findViewById(R.id.four);
        five=findViewById(R.id.five);
        six=findViewById(R.id.six);
        seven=findViewById(R.id.seven);
        eight=findViewById(R.id.eight);
        nine=findViewById(R.id.nine);
        zero=findViewById(R.id.zero);
        btnBackSpace=findViewById(R.id.btnBackSpace);
        btnCheckPin=findViewById(R.id.btnCheckPin);
        statusPin=findViewById(R.id.statusPin);
        forgetPin=findViewById(R.id.forgetPin);
        textureView=findViewById(R.id.textureView);

        btnFingerprint=findViewById(R.id.btnFingerprint);
        statusFingerprint=findViewById(R.id.statusFingerprint);



        offCapture=0;

        SharedPreferences sharedPreferences =getSharedPreferences(keyPin,MODE_PRIVATE);
        pin =sharedPreferences.getString("PIN","");

        SharedPreferences sharedPreferences2 = getSharedPreferences("CAPTURE",MODE_PRIVATE);
        timeCapture = sharedPreferences2.getInt("TIME_CAPTURE",2);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
            captureImage=0;
        }else {
            SharedPreferences sharedPreferences3 = getSharedPreferences("CAPTURE_IMAGE",MODE_PRIVATE);
            captureImage = sharedPreferences3.getInt("ON_OFF",0);
        }
        SharedPreferences preferencesSecurityQuestion = getSharedPreferences("ANSWER",MODE_PRIVATE);
        answer = preferencesSecurityQuestion.getString("SECURITY_ANSWER","");

        SharedPreferences sharedPreferences1 = getSharedPreferences(keyFingerprint,MODE_PRIVATE);
        int statusOfFingerprint = sharedPreferences1.getInt("FINGERPRINT",1);
        if (statusOfFingerprint!=1){
            enableDisableView(btnFingerprint,false);
            statusFingerprint.setText(R.string.fingerprintTurnOff);
        }

        if (pin.isEmpty()){
            statusFingerprint.setText("");
        }

        if (captureImage==1){
            ccv2WithPreview = new CameraControllerV2WithPreview(PinActivity.this,textureView);
            textureView.setVisibility(View.VISIBLE);
        }else {
            textureView.setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        i = intent.getIntExtra("CHANGE_PIN",0);
        if (i==1){
            statusPin.setText(R.string.enterYourOldPin);
            btnFingerprint.setVisibility(View.GONE);
            statusFingerprint.setVisibility(View.GONE);
        }else {
            if (pin.isEmpty()){
                statusPin.setText(R.string.createANewPin);
            }else {
                statusPin.setText(R.string.enterYourPin);
            }
        }
    }

    private String updateText(String stringToAdd, String oldString, int cursorPos){
        String leftOfCursor = oldString.substring(0, cursorPos);
        String rightOfCursor = oldString.substring(cursorPos);

        return leftOfCursor + stringToAdd + rightOfCursor;

    }
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (i==1){
            statusApp=1;
            super.onBackPressed();
        }else {
            if (backPressTime + 2000 > System.currentTimeMillis()){
                finishAffinity();
            }else {
                Toast.makeText(PinActivity.this, R.string.pressBackAgaint,Toast.LENGTH_SHORT).show();
            }
            backPressTime = System.currentTimeMillis();
        }
    }

    public static void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if ( view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    public void closeCamera(){
        if (captureImage==1 && offCapture==0){
            ccv2WithPreview.closeCamera();
        }
    }

    public void closeCameraButton(){
        if (offCapture==1 && captureImage==1){
            ccv2WithPreview.closeCamera();
            offCapture=0;
            offCaptureIntent=1;
        }
    }

    public static void wait(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusApp=0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (statusApp==0){
            finishAffinity();
        }
    }


}