package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.viewfileinfolder.ViewFileInFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoVaultActivity extends AppCompatActivity {
    private RecyclerView rcvVideoVault;
    private FolderAdapter folderAdapter;
    private List<Folder> list = new ArrayList<>();
    private int statusApp = 0;
    private NativeAd nativeAd;
    private ImageView imgLoading;
    private RelativeLayout rltLayoutAdsLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this, R.color.button), true, false, VideoVaultActivity.this);
        setContentView(R.layout.activity_video_vault);
        rcvVideoVault = findViewById(R.id.rcvVideoVault);
        folderAdapter = new FolderAdapter(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcvVideoVault.setLayoutManager(gridLayoutManager);
        folderAdapter.setData(loadDataFolder());
        rcvVideoVault.setAdapter(folderAdapter);
        loadDataFolder();

        folderAdapter.setOnItemClickListener(new FolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Folder folder = list.get(position);
                String folderName = folder.getNameFolder();
                statusApp = 1;
                Intent intent = new Intent(VideoVaultActivity.this, ViewFileInFolder.class);
                intent.putExtra("FOLDER NAME", folderName);
                intent.putExtra("TYPE", false);
                startActivity(intent);
            }
        });

        rltLayoutAdsLoading = findViewById(R.id.rltLayoutAdsLoading);
        imgLoading = findViewById(R.id.imgLoading);
        Glide.with(this).asGif().load(R.drawable.loading_gif).into(imgLoading);
        refreshAd();

    }

    public void clickBack(View view) {
        statusApp = 1;
        finish();
    }

    public void addFolderVideo(View view) {
        openBottomSheetDialog();
    }

    private void openBottomSheetDialog() {
        View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.show();

        Button btnCancel = viewDialog.findViewById(R.id.btnCancel);
        Button btnCreate = viewDialog.findViewById(R.id.btnCreate);
        EditText albumName = viewDialog.findViewById(R.id.albumName);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strFolderName = albumName.getText().toString().trim();
                String name = strFolderName;
                if (TextUtils.isEmpty(strFolderName)) {
                    return;
                }
                File file = new File(MainActivity.getStore(VideoVaultActivity.this) + "/.HidePhoto/Video", name);
                if (!file.exists()) {
                    file.mkdirs();
                    list.add(new Folder(R.drawable.ic_folder, strFolderName, 0, file.getAbsolutePath(), ""));
                    folderAdapter.setData(list);
                    Toast.makeText(VideoVaultActivity.this, R.string.createFolderSuccessfully, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VideoVaultActivity.this, R.string.folderExist, Toast.LENGTH_SHORT).show();
                }
                bottomSheetDialog.dismiss();

            }
        });
    }

    private List<File> takeAllFolderPhoto1() {
        List<File> list = new ArrayList<>();
        String path = MainActivity.getStore(VideoVaultActivity.this) + "/.HidePhoto/Video";
//        String path1 = Environment.getExternalStoragePublicDirectory("file/*").getAbsolutePath()+"/.HidePhoto/.Photo";
        File directory = new File(path);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                list.add(new File(files[i].getAbsolutePath()));
            }
        }
        return list;
    }

    private List<Folder> loadDataFolder() {
        List<File> listFile = takeAllFolderPhoto1();
        List<Folder> list1 = new ArrayList<>();
        for (int j = 0; j < listFile.size(); j++) {
            File file = new File(listFile.get(j).getAbsolutePath());
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                int count = files.length;
                if (files.length == 0) {
                    list1.add(new Folder(R.drawable.ic_folder, listFile.get(j).getName(), count, listFile.get(j).getAbsolutePath(), ""));
                } else {
                    String firstPath = files[0].getAbsolutePath();
                    list1.add(new Folder(R.drawable.ic_folder, listFile.get(j).getName(), count, listFile.get(j).getAbsolutePath(), firstPath));
                }
            }
        }
        list = list1;
        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        folderAdapter.setData(loadDataFolder());
        statusApp = 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (statusApp == 0) {
            finishAffinity();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        statusApp = 1;
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
     */
    private void refreshAd() {

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
                        if (VideoVaultActivity.this.nativeAd != null) {
                            VideoVaultActivity.this.nativeAd.destroy();
                        }
                        VideoVaultActivity.this.nativeAd = nativeAd;
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
}