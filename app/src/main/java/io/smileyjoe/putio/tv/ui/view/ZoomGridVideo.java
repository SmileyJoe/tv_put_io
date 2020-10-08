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
    private TextView mTextSummary;
    private TextView mTextTitle;
    private ImageView mImagePoster;

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

        mTextSummary = mLayoutContent.findViewById(R.id.text_summary);
        mTextTitle = mLayoutContent.findViewById(R.id.text_title);
        mImagePoster = mLayoutContent.findViewById(R.id.image_poster);

        getViewTreeObserver().addOnGlobalLayoutListener(new OnContentLayoutListener());
    }

    public void hide(){
        setVisibility(INVISIBLE);
    }

    private float getDimension(float viewPosition, float viewSize, float size, float parentSize){
        float center = viewPosition + viewSize/2;
        float position = center - (size/2);

        if(position < 0){
            position = 0;
        } else if((position + size) > parentSize){
            position = parentSize - size;
        }

        return position;
    }

    public void show(View view, Video video){
        ViewGroup parent = (ViewGroup) getParent();
        parent.setElevation(10);

        Glide.with(getContext())
                .load(video.getPosterAsUri())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(mImagePoster);


        mTextTitle.setText(video.getTitle());
        mTextTitle.setTypeface(null, Typeface.BOLD);

        mTextSummary.setText(video.getOverView());
        mTextSummary.setVisibility(View.VISIBLE);

        setX(getDimension(view.getX(), view.getWidth(), getWidth(), parent.getWidth()));
        setY(getDimension(view.getY(), view.getHeight(), getHeight(), parent.getHeight()));
        setVisibility(View.VISIBLE);
    }

    private class OnContentLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            float height = getHeight();
            float width = getWidth();

            if(height > 0 && width > 0) {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ViewGroup.LayoutParams params = mLayoutContent.getLayoutParams();
                params.height = getHeight();
                params.width = getWidth();
                mLayoutContent.setLayoutParams(params);
            }
        }
    }
}
