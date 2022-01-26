package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.introduce;


import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.SetThemeColor;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.openads.MyApplication;

public class IntrodutionActivity extends AppCompatActivity {
    ImageView background;
    LottieAnimationView lottieAnimationView;

    protected boolean _active = true;
    protected int _splashTime = 2400;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, Color.parseColor("#44c3fd"), true, false, IntrodutionActivity.this);
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

        setContentView(R.layout.activity_introdution);
        background = findViewById(R.id.background);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);

        Application application = getApplication();

        // If the application is not an instance of MyApplication, log an error message and
        // start the MainActivity without showing the app open ad.
        if (!(application instanceof MyApplication)) {
            Log.e("error", "Failed to cast application to MyApplication.");
            restartApp();
            return;
        }

        // Show the app open ad.
        ((MyApplication) application)
                .showAdIfAvailable(
                        IntrodutionActivity.this,
                        new MyApplication.OnShowAdCompleteListener() {
                            @Override
                            public void onShowAdComplete() {
                                restartApp();
                            }
                        });

        background.animate().translationZBy(-3600).setDuration(1000).setStartDelay(1000);
    }


    public void restartApp() {

        lottieAnimationView.animate().translationY(-2400).setDuration(1000).setStartDelay(1000);

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    e.toString();
                } finally {
                    Intent intent = new Intent(getApplicationContext(),
                            IntroduceActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        splashTread.start();
    }
}