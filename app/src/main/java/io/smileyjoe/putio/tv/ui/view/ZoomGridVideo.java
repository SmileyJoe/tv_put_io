package io.smileyjoe.putio.tv.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;

public class ZoomGridVideo extends RelativeLayout{

    private RelativeLayout mLayoutContent;

    public ZoomGridVideo(Context context) {
        super(context);
        init(null);
    }

    public ZoomGridVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ZoomGridVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        mLayoutContent = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.grid_item_video, this, false);
        addView(mLayoutContent);

        getViewTreeObserver().addOnGlobalLayoutListener(new OnContentLayoutListener());
    }

    public void hide(){
        setVisibility(INVISIBLE);
    }

    public void show(View view, Video video){
        float x = view.getX();
        float y = view.getY();
        float height = view.getHeight();
        float width = view.getWidth();
        float centerX = x + width/2;
        float centerY = y + height/2;
        float largeWidth = getWidth();
        float largeHeight = getHeight();
        float largeCenterX = centerX - (largeWidth/2);
        float largeCenterY = centerY - (largeHeight/2);
        ViewGroup parent = (ViewGroup) getParent();

        if(largeCenterY < 0){
            largeCenterY = 0;
        } else if((largeCenterY + largeHeight) > parent.getHeight()){
            largeCenterY = parent.getHeight() - largeHeight;
        }

        if(largeCenterX < 0){
            largeCenterX = 0;
        } else if((largeCenterX + largeWidth) > parent.getWidth()){
            largeCenterX = parent.getWidth() - largeWidth;
        }

        parent.setElevation(10);

        Glide.with(getContext())
                .load(video.getPosterAsUri())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into((ImageView) findViewById(R.id.image_poster));

        TextView textSummary = findViewById(R.id.text_summary);
        TextView textTitle = findViewById(R.id.text_title);
        textTitle.setText(video.getTitle());
        textTitle.setTypeface(null, Typeface.BOLD);
        textSummary.setText(video.getOverView());
        textSummary.setVisibility(View.VISIBLE);

        setX(largeCenterX);
        setY(largeCenterY);
        setVisibility(View.VISIBLE);
    }

    private class OnContentLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            float height = getHeight();
            float width = getWidth();
            if(height > 0 && width > 0) {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) mLayoutContent.getLayoutParams();
                params.height = getHeight();
                params.width = getWidth();
                mLayoutContent.setLayoutParams(params);
            }
        }
    }
}
