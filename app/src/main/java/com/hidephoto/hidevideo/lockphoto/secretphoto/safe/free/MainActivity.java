package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.pinandsecurityquestion.BreakInAlertActivity;
import com.hsalf.smileyrating.SmileyRating;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;
    private DrawerLayout drawerLayout;
    private CardView cardPhotoVault, cardVideoVault;
    private long backPressTime;
    private int statusApp = 0;
    private int countingsStars;
    private int createDefaultPhoto, createDefaultVideo;
    private String rateStatus = "RateStatus";
    private NativeAd nativeAd;
    private ImageView imgLoading;
    private RelativeLayout rltLayoutAdsLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this, R.color.button), true, false, MainActivity.this);
        setContentView(R.layout.activity_main);
        checkPermissions();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            deleteCopied();
        }
        addControls();
        addEvents();

        rltLayoutAdsLoading = findViewById(R.id.rltLayoutAdsLoading);
        imgLoading = findViewById(R.id.imgLoading);
        Glide.with(this).asGif().load(R.drawable.loading_gif).into(imgLoading);
        refreshAd();
    }

    private void addEvents() {
        cardPhotoVault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPhotoDefaultFolder();
                statusApp = 1;
                Intent intent = new Intent(MainActivity.this, PhotoVaultActivity.class);
                startActivity(intent);
            }
        });
        cardVideoVault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createVideoDefaultFolder();
                statusApp = 1;
                Intent intent = new Intent(MainActivity.this, VideoVaultActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addControls() {
        drawerLayout = findViewById(R.id.drawerLayout);
        cardVideoVault = findViewById(R.id.cardVideoVault);
        cardPhotoVault = findViewById(R.id.cardPhotoVault);
    }

    public void clickLogo(View view) {
        closeDrawer(drawerLayout);
    }

    public void clickSetting(View view) {
        statusApp = 1;
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    public void clickBreakInAlert(View view) {
        statusApp = 1;
        Intent intent = new Intent(MainActivity.this, BreakInAlertActivity.class);
        startActivity(intent);
    }

    public void clickShareToFriend(View view) {
        shareApp(MainActivity.this);
    }

    public void clickLikeApp(View view) {
        showRatingDialog();
    }

    public void clickAboutUs(View view) {
    }

    public void clickMenu(View view) {
        openDrawer(drawerLayout);
    }

    private static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            createDirectoty();
        } else {
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, R.string.permissionGranted, Toast.LENGTH_SHORT).show();
                createDirectoty();
            } else {
                Toast.makeText(MainActivity.this, R.string.permissionDenied, Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAndRemoveTask();
                }
            }
        }
    }

    public void createDirectoty() {
        File file = new File(getStore(this), ".Hidephoto");
        if (!file.exists()) {
            file.mkdirs();
            createDirectotyPhotoAndVideo();
        } else {
            createDirectotyPhotoAndVideo();
        }
    }

    public void createDirectotyPhotoAndVideo() {
        String[] folderName = {"Photo", "Video", "Temporary"};
        for (int i = 0; i < folderName.length; i++) {
            File file = new File(getStore(this) + "/.HidePhoto", folderName[i]);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    public void createPhotoDefaultFolder() {
        String[] folderName = {"Document", "Private Photo", "Security Card"};
        for (int i = 0; i < 3; i++) {
            File file = new File(getStore(this) + "/.HidePhoto/Photo", folderName[i]);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    public void createVideoDefaultFolder() {
        File file = new File(getStore(this) + "/.HidePhoto/Video", "Default");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String getStore(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File f = c.getExternalFilesDir(null);
            if (f != null) {
//                return f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf("0") + 1);
                return f.getAbsolutePath();
            } else
//                return "/storage/emulated/0";
                return "/storage/emulated/0/Android/data/" + c.getPackageName();
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }

    private void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void shareApp(Context context) {
        try {
            statusApp = 1;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hide photos and videos.");
            String shareMessage = "If you are looking for the best app to hide photos and videos, \"\" This app is the best choice for you.";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }

    }

    private void showRatingDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setLayout(getWindowWidth() - 50, LinearLayout.LayoutParams.WRAP_CONTENT);

        SmileyRating rating = dialog.findViewById(R.id.smileyRating);
        rating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                switch (type) {
                    case TERRIBLE:
                        countingsStars = 1;
                        ratePkg(MainActivity.this, getPackageName());
                        break;
                    case BAD:
                        countingsStars = 2;
                        ratePkg(MainActivity.this, getPackageName());
                        break;
                    case OKAY:
                        countingsStars = 3;
                        ratePkg(MainActivity.this, getPackageName());
                        break;
                    case GOOD:
                        countingsStars = 4;
                        ratePkg(MainActivity.this, getPackageName());
                        break;
                    case GREAT:
                        countingsStars = 5;
                        ratePkg(MainActivity.this, getPackageName());
                        break;
                }
                SharedPreferences preferences = getSharedPreferences(rateStatus, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("STAR", countingsStars);
                editor.commit();
            }
        });
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finishAffinity();
            }
        });
        dialog.show();
    }

    private int getWindowWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public void ratePkg(Context context, String pkg) {
        if (pkg == null)
            return;
        Uri uri = Uri.parse("market://details?id=" + pkg);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        SharedPreferences preferences = getSharedPreferences(rateStatus, MODE_PRIVATE);
        int star = preferences.getInt("STAR", 0);
        if (star < 3) {
            showRatingDialog();
        } else {
            if (backPressTime + 2000 > System.currentTimeMillis()) {
                finishAffinity();
            } else {
                Toast.makeText(MainActivity.this, R.string.pressBackAgaint, Toast.LENGTH_SHORT).show();
            }
            backPressTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (statusApp == 0) {
            finishAffinity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusApp = 0;
    }

    /**
     * Populates a {@link NativeAdView} object with data from a given {@link NativeAd}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView   the view to be populated
     */
    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.

    }


    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     */
    private void refreshAd( ) {

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.id_native));

        builder.forNativeAd(
                new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        // If this callback occurs after the activity is destroyed, you must call
                        // destroy and return or you may get a memory leak.
                        boolean isDestroyed = false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            isDestroyed = isDestroyed();
                        }
                        if (isDestroyed || isFinishing() || isChangingConfigurations()) {
                            nativeAd.destroy();
                            return;
                        }
                        // You must call destroy on old ads when you are done with them,
                        // otherwise you will have a memory leak.
                        if (MainActivity.this.nativeAd != null) {
                            MainActivity.this.nativeAd.destroy();
                        }
                        MainActivity.this.nativeAd = nativeAd;
                        FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                        NativeAdView adView =
                                (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified, null);
                        populateNativeAdView(nativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }
                });


        VideoOptions videoOptions =
                new VideoOptions.Builder().setStartMuted(true).build();

        com.google.android.gms.ads.nativead.NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader =
                builder
                        .withAdListener(
                                new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                                        rltLayoutAdsLoading.setVisibility(View.GONE);
                                    }
                                })
                        .build();

        adLoader.loadAd(new AdManagerAdRequest.Builder().build());

    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }

        super.onDestroy();
    }

    private void deleteCopied(){
        SharedPreferences sharedPreferences = getSharedPreferences("COPIED",MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet("copied",null);
        if (set!=null){
            Object[] lol = set.toArray();
            for (int i=0; i<lol.length; i++){
                File file = new File(lol[i].toString());
                if (file.exists()){
                    file.delete();
                }
            }
        }
    }
}