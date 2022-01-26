package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.textViewFont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class SvnBookTextView extends AppCompatTextView {
    public SvnBookTextView(Context context) {
        super(context);
        setFontTextView();
    }

    public SvnBookTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFontTextView();
    }

    public SvnBookTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFontTextView();
    }

    private void setFontTextView (){
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/IOS_0.ttf");
        this.setTypeface(font);
    }
}
