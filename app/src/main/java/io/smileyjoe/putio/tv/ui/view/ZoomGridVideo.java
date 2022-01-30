package io.smileyjoe.putio.tv.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.GridItemVideoBinding;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.util.PopulateGenres;
import io.smileyjoe.putio.tv.util.ViewUtil;

public class ZoomGridVideo extends RelativeLayout{

    private float mMultiplier;
    private boolean mSizeSet = false;
    private GridItemVideoBinding mView;

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
        mView = GridItemVideoBinding.inflate(LayoutInflater.from(getContext()));
        mView.getRoot().setSelected(true);
        addView(mView.getRoot());

        mView.textTitle.setMaxLines(2);

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
            ViewGroup.LayoutParams params = mView.getRoot().getLayoutParams();
            params.width = (int) (view.getWidth() * mMultiplier);
            params.height = (int) (view.getHeight() * mMultiplier);
            mView.getRoot().setLayoutParams(params);
            mSizeSet = true;
        }

        Drawable currentPoster = mView.imagePoster.getDrawable();

        if(currentPoster == null){
            Glide.with(getContext())
                    .load(video.getPosterAsUri())
                    .into(mView.imagePoster);
        } else {
            mView.imagePoster.setImageDrawable(currentPoster);
        }

        String title = video.getTitleFormatted();
        if(video.getVideoType() == VideoType.SEASON){
            title = title + ": " + getContext().getString(R.string.text_season) + " " + video.getSeason();
        }
        mView.textTitle.setText(title);
        mView.textTitle.setTypeface(null, Typeface.BOLD);

        if(!TextUtils.isEmpty(video.getOverView())) {
            mView.textSummary.setText(video.getOverView());
            mView.textSummary.setVisibility(View.GONE);
        } else {
            mView.textSummary.setVisibility(GONE);
        }

        if(video.isWatched()){
            mView.frameWatched.setVisibility(View.VISIBLE);
        } else {
            mView.frameWatched.setVisibility(View.GONE);
        }

        if(video.getReleaseDate() > 0){
            mView.textReleaseDate.setVisibility(View.VISIBLE);
            mView.textReleaseDate.setText(video.getReleaseDateFormatted());
        } else {
            mView.textReleaseDate.setVisibility(View.GONE);
        }

        ViewUtil.populateResumeTime(mView.textResumeTime, video);

        mView.textGenres.setText(null);
        PopulateGenres genresTask = new PopulateGenres(mView.textGenres, video);
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
            int lineSize = mView.textSummary.getLineHeight();
            int numLines = (getHeight()/2)/lineSize;
            mView.textSummary.setLines(numLines);
        }

        private void setViewDimensions(){
            ViewGroup.LayoutParams params = mView.getRoot().getLayoutParams();
            params.height = getHeight();
            params.width = getWidth();
            mView.getRoot().setLayoutParams(params);
        }
    }
}
