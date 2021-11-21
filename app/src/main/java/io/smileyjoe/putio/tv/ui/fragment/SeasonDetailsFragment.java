package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.viewholder.VideoDetailsViewHolder;

public class SeasonDetailsFragment extends Fragment {

    private VideoDetailsViewHolder mVideoDetailsViewHolder;
    private ImageView mImagePoster;
    private FrameLayout mFrameDetails;
    private FrameDetailsLayoutListener mFrameDetailsLayoutListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_season_details, null);

        mFrameDetails = view.findViewById(R.id.frame_details);
        View viewDetails = LayoutInflater.from(getContext()).inflate(R.layout.item_video_details, mFrameDetails, true);
        mVideoDetailsViewHolder = new VideoDetailsViewHolder(viewDetails);
        mImagePoster = view.findViewById(R.id.image_poster);
        mFrameDetailsLayoutListener = new FrameDetailsLayoutListener();



        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(hidden){
            mFrameDetails.getViewTreeObserver().removeOnGlobalLayoutListener(mFrameDetailsLayoutListener);
        } else {
            mFrameDetails.getViewTreeObserver().addOnGlobalLayoutListener(mFrameDetailsLayoutListener);
        }
    }

    public void update(Video video){
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

    private class FrameDetailsLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener{

        private int mFrameHeight = 0;

        @Override
        public void onGlobalLayout() {
            if(mImagePoster != null) {
                int frameHeight = mFrameDetails.getMeasuredHeight();

                if(mFrameHeight != frameHeight) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mImagePoster.getLayoutParams();
                    params.height = mFrameDetails.getMeasuredHeight();
                    mImagePoster.setLayoutParams(params);
                    mFrameHeight = frameHeight;
                }
            }
        }
    }

}
