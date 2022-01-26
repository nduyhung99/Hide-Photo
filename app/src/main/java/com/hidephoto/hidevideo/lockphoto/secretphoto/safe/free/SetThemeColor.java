package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.view.View;

public class SetThemeColor {
    public static void setThemeColor(int colorNavigation, int colorStatusBar, boolean topLight, boolean botLight,Context context) {
        int decor = 0;
        if (!topLight && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            decor = decor | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        if (!botLight && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            decor = decor | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity(context).getWindow().setNavigationBarColor(colorNavigation);
            getActivity(context).getWindow().setStatusBarColor(colorStatusBar);
        }
        getActivity(context).getWindow().getDecorView().setSystemUiVisibility(decor);
    }

    public static Activity getActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) return (Activity) context;
        if (context instanceof ContextWrapper) return getActivity(((ContextWrapper)context).getBaseContext());
        return null;
    }
}
