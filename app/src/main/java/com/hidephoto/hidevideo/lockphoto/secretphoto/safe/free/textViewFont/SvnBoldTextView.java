package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.textViewFont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class SvnBoldTextView extends AppCompatTextView {
    public SvnBoldTextView(Context context) {
        super(context);
        setFontTextView(context);
    }

    public SvnBoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFontTextView(context);
    }

    public SvnBoldTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFontTextView(context);
    }

    private void setFontTextView (Context context){
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/SVN-Gotham Bold.ttf");
        this.setTypeface(font);
    }
}
