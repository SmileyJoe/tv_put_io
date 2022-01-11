package io.smileyjoe.putio.tv.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.viewholder.VideoDetailsViewHolder;
import io.smileyjoe.putio.tv.util.TmdbUtil;
import io.smileyjoe.putio.tv.util.VideoDetailsHelper;

public class VideoDetailsBackdropActivity extends FragmentActivity implements TmdbUtil.Listener{

    private static final String EXTRA_VIDEO = "video";

    private Video mVideo;
    private VideoDetailsViewHolder mVideoDetailsViewHolder;

    public static Intent getIntent(Context context, Video video){
        Intent intent = new Intent(context, VideoDetailsBackdropActivity.class);

        intent.putExtra(EXTRA_VIDEO, video);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_backdrop);

        handleExtras();
        populate();
        getData();
    }

    private void getData(){
        if(mVideo.getVideoType() == VideoType.MOVIE && mVideo.isTmdbFound() && TextUtils.isEmpty(mVideo.getTagLine())) {
            TmdbUtil.OnTmdbResponse response = new TmdbUtil.OnTmdbResponse(getBaseContext(), mVideo);
            response.setListener(this);
            Tmdb.Movie.get(getBaseContext(), mVideo.getTmdbId(), response);
        }
    }

    @Override
    public void update(Video video) {
        populate();
    }

    private void populate(){
        setImage(findViewById(R.id.image_poster), mVideo.getPosterAsUri());
        setImage(findViewById(R.id.image_backdrop), mVideo.getBackdropAsUri());
        findViewById(R.id.button_watch).setOnClickListener(view -> VideoDetailsHelper.play(this, mVideo, false));
        MaterialButton buttonResume = findViewById(R.id.button_resume);
        buttonResume.setText(buttonResume.getText().toString() + " - " + mVideo.getResumeTimeFormatted());
        buttonResume.setOnClickListener(view -> VideoDetailsHelper.play(this, mVideo, true));

        if(mVideoDetailsViewHolder == null) {
            FrameLayout frameDetails = findViewById(R.id.frame_details);
            frameDetails.removeAllViews();
            ViewGroup viewDetails = (ViewGroup) LayoutInflater.from(getBaseContext()).inflate(R.layout.item_video_details, frameDetails, true);
            mVideoDetailsViewHolder = new VideoDetailsViewHolder(viewDetails);
            mVideoDetailsViewHolder.addTextShadow();
        }

        mVideoDetailsViewHolder.bind(mVideo);
    }

    private void setImage(ImageView image, Uri uri){
        Glide.with(getBaseContext())
                .load(uri)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image);
    }

    private void handleExtras() {
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            if(extras.containsKey(EXTRA_VIDEO)){
                mVideo = extras.getParcelable(EXTRA_VIDEO);
            }
        }
    }
}
