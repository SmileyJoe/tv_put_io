package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.interfaces.HomeFragmentListener;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.viewholder.VideoDetailsViewHolder;

public class SeasonDetailsFragment extends Fragment implements View.OnFocusChangeListener {

    public interface Listener extends HomeFragmentListener<Video>{

    }

    private VideoDetailsViewHolder mVideoDetailsViewHolder;
    private ImageView mImagePoster;
    private LinearLayout mLayoutContent;
    private FrameDetailsLayoutListener mFrameDetailsLayoutListener;
    private Listener mListener;
    private Video mVideo;
    private Button mButtonHide;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_season_details, null);

        mLayoutContent = view.findViewById(R.id.layout_content);
        ViewGroup viewDetails = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.item_video_details, view.findViewById(R.id.frame_details), true);
        mVideoDetailsViewHolder = new VideoDetailsViewHolder(viewDetails);
        mImagePoster = view.findViewById(R.id.image_poster);
        mFrameDetailsLayoutListener = new FrameDetailsLayoutListener();
        mButtonHide = view.findViewById(R.id.button_hide);
        mButtonHide.setOnFocusChangeListener(this);
        mButtonHide.setOnClickListener(new OnHideClick());

        return view;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(mListener != null){
            mListener.hasFocus(FragmentType.SERIES_DETAILS, mVideo, mButtonHide, 0);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(hidden){
            mLayoutContent.getViewTreeObserver().removeOnGlobalLayoutListener(mFrameDetailsLayoutListener);
        } else {
            mLayoutContent.getViewTreeObserver().addOnGlobalLayoutListener(mFrameDetailsLayoutListener);
        }
    }

    public void update(Video video){
        mVideo = video;
        mVideoDetailsViewHolder.bind(video);
        if (video.getPosterAsUri() != null) {
            mImagePoster.setVisibility(View.VISIBLE);
            mImagePoster.setPadding(0,0,0,0);
            Glide.with(getContext())
                    .load(video.getPosterAsUri())
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImagePoster);
        } else {
            mImagePoster.setVisibility(View.GONE);
        }
    }

    private class OnHideClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(mVideoDetailsViewHolder.isMinimized()){
                mImagePoster.setVisibility(View.VISIBLE);
                mButtonHide.setText(R.string.text_hide);
            } else {
                mImagePoster.setVisibility(View.GONE);
                mButtonHide.setText(R.string.text_show);
            }
            mVideoDetailsViewHolder.toggleMinimized();
        }
    }

    private class FrameDetailsLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener{

        private int mFrameHeight = 0;

        @Override
        public void onGlobalLayout() {
            if(mImagePoster != null) {
                int frameHeight = mLayoutContent.getMeasuredHeight();

                if(mFrameHeight != frameHeight) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mImagePoster.getLayoutParams();
                    params.height = mLayoutContent.getMeasuredHeight();
                    mImagePoster.setLayoutParams(params);
                    mFrameHeight = frameHeight;
                }
            }
        }
    }

}
