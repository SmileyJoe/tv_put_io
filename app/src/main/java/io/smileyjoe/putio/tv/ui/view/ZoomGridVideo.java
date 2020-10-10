package io.smileyjoe.putio.tv.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.PopulateGenres;

public class ZoomGridVideo extends RelativeLayout{

    private RelativeLayout mLayoutContent;
    private TextView mTextSummary;
    private TextView mTextTitle;
    private ImageView mImagePoster;
    private FrameLayout mFrameWatched;
    private float mMultiplier;
    private boolean mSizeSet = false;
    private TextView mTextGenres;

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
        handleAttributes(attrs);
        mLayoutContent = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.grid_item_video, this, false);
        mLayoutContent.setSelected(true);
        addView(mLayoutContent);

        mTextSummary = mLayoutContent.findViewById(R.id.text_summary);
        mTextTitle = mLayoutContent.findViewById(R.id.text_title);
        mImagePoster = mLayoutContent.findViewById(R.id.image_poster);
        mFrameWatched = mLayoutContent.findViewById(R.id.frame_watched);
        mTextGenres = mLayoutContent.findViewById(R.id.text_genres);

        getViewTreeObserver().addOnGlobalLayoutListener(new OnContentLayoutListener());
    }

    private void handleAttributes(AttributeSet attributeSet){
        TypedArray ta = getContext().obtainStyledAttributes(attributeSet, R.styleable.ZoomGridVideo, 0, 0);
        try {
            mMultiplier = ta.getFloat(R.styleable.ZoomGridVideo_multiplier, 2);
        } finally {
            ta.recycle();
        }
    }

    public void hide(){
        setVisibility(INVISIBLE);
    }

    private float getPosition(float viewPosition, float viewSize, float size, float parentSize){
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
        if(!mSizeSet){
            ViewGroup.LayoutParams params = mLayoutContent.getLayoutParams();
            params.width = (int) (view.getWidth() * mMultiplier);
            params.height = (int) (view.getHeight() * mMultiplier);
            mLayoutContent.setLayoutParams(params);
            mSizeSet = true;
        }

        mImagePoster.setImageDrawable(((ImageView) view.findViewById(R.id.image_poster)).getDrawable());

        mTextTitle.setText(video.getTitle());
        mTextTitle.setTypeface(null, Typeface.BOLD);

        if(!TextUtils.isEmpty(video.getOverView())) {
            mTextSummary.setText(video.getOverView());
            mTextSummary.setVisibility(View.VISIBLE);
        } else {
            mTextSummary.setVisibility(GONE);
        }

        if(video.isWatched()){
            mFrameWatched.setVisibility(View.VISIBLE);
        } else {
            mFrameWatched.setVisibility(View.GONE);
        }

        mTextGenres.setText(null);
        PopulateGenres genresTask = new PopulateGenres(mTextGenres, video);
        genresTask.setHideOnEmpty(true);
        genresTask.execute();

        ViewGroup parent = (ViewGroup) getParent();
        setX(getPosition(view.getX(), view.getWidth(), getWidth(), parent.getWidth()));
        setY(getPosition(view.getY(), view.getHeight(), getHeight(), parent.getHeight()));
        setVisibility(View.VISIBLE);
    }

    public void reset(){
        mSizeSet = false;
    }

    private class OnContentLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            int height = getHeight();
            int width = getWidth();

            if(height > 0 && width > 0) {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setViewDimensions();
                setSummaryLines();
            }
        }

        private void setSummaryLines(){
            int lineSize = mTextSummary.getLineHeight();
            int numLines = (getHeight()/2)/lineSize;
            mTextSummary.setLines(numLines);
        }

        private void setViewDimensions(){
            ViewGroup.LayoutParams params = mLayoutContent.getLayoutParams();
            params.height = getHeight();
            params.width = getWidth();
            mLayoutContent.setLayoutParams(params);
        }
    }
}
