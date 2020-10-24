package io.smileyjoe.putio.tv.ui.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import io.smileyjoe.putio.tv.R;

public class PillView extends AppCompatTextView {

    public PillView(Context context) {
        super(context);
        init(null);
    }

    public PillView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PillView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attributeSet){
        // nothing to setup yet //
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        GradientDrawable background = (GradientDrawable) getContext().getResources().getDrawable(R.drawable.bg_pill, null);
        background.setCornerRadius(getMeasuredHeight());

        setBackgroundDrawable(background);
    }
}
