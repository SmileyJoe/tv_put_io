package io.smileyjoe.putio.tv.ui.activity;

import android.app.Activity;
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
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.widget.Action;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.DetailsAction;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.fragment.VideoDetailsFragment;
import io.smileyjoe.putio.tv.ui.viewholder.VideoDetailsViewHolder;
import io.smileyjoe.putio.tv.util.TmdbUtil;
import io.smileyjoe.putio.tv.util.VideoDetailsHelper;

public class VideoDetailsBackdropActivity extends FragmentActivity implements TmdbUtil.Listener, VideoDetailsHelper.ActionListener{

    private static final String EXTRA_VIDEO = "video";

    private Video mVideo;
    private VideoDetailsViewHolder mVideoDetailsViewHolder;
    private LinearLayoutCompat mLayoutButtons;
    private int mButtonMargin;
    private HashMap<Long, MaterialButton> mHashButtonGroups;
    private HashMap<Long, Group> mHashGroups;

    public static Intent getIntent(Context context, Video video){
        Intent intent = new Intent(context, VideoDetailsBackdropActivity.class);

        intent.putExtra(EXTRA_VIDEO, video);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_backdrop);

        mHashButtonGroups = new HashMap<>();
        mHashGroups = new HashMap<>();
        mLayoutButtons = findViewById(R.id.layout_buttons);
        mButtonMargin = getResources().getDimensionPixelOffset(R.dimen.padding_general);

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
//        findViewById(R.id.button_watch).setOnClickListener(view -> VideoDetailsHelper.play(this, mVideo, false));
//        MaterialButton buttonResume = findViewById(R.id.button_resume);
//        buttonResume.setText(buttonResume.getText().toString() + " - " + mVideo.getResumeTimeFormatted());
//        buttonResume.setOnClickListener(view -> VideoDetailsHelper.play(this, mVideo, true));
        addButtons();

        if(mVideoDetailsViewHolder == null) {
            FrameLayout frameDetails = findViewById(R.id.frame_details);
            frameDetails.removeAllViews();
            ViewGroup viewDetails = (ViewGroup) LayoutInflater.from(getBaseContext()).inflate(R.layout.item_video_details, frameDetails, true);
            mVideoDetailsViewHolder = new VideoDetailsViewHolder(viewDetails);
            mVideoDetailsViewHolder.addTextShadow();
        }

        mVideoDetailsViewHolder.bind(mVideo);
    }

    private void addButtons(){
        mLayoutButtons.removeAllViews();

        for(DetailsAction action:DetailsAction.values()){
            if(action.shouldShow()){
                boolean shouldAdd = true;
                String title = getString(action.getTitleResId());
                switch (action){
                    case RESUME:
                        if(mVideo.getResumeTime() > 0){
                            title = title + " : " + mVideo.getResumeTimeFormatted();
                        } else {
                            shouldAdd = false;
                        }
                        break;
                }

                if(shouldAdd) {
                    MaterialButton button = addActionButton(title);
                    button.setOnClickListener(new VideoDetailsHelper.OnActionButtonClicked(action, this));
                }
            }
        }

        addGroupActions((group, verb, title) -> {
            MaterialButton button = addActionButton(verb + " " + title);
            button.setOnClickListener(view -> {
                onGroupClicked(group.getId());
            });
            mHashButtonGroups.put(group.getIdAsLong(), button);
            mHashGroups.put(group.getIdAsLong(), group);
        });
    }

    private MaterialButton addActionButton(String title){
        MaterialButton button = (MaterialButton) LayoutInflater.from(getBaseContext()).inflate(R.layout.include_button_backdrop, null);
        button.setText(title);
        mLayoutButtons.addView(button);
        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) button.getLayoutParams();
        params.leftMargin = mButtonMargin;
        params.rightMargin = mButtonMargin;
        return button;
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

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Video getVideo() {
        return mVideo;
    }

    @Override
    public void updateGroupAction(long groupId, int verb) {
        MaterialButton button = mHashButtonGroups.get(groupId);
        Group group = mHashGroups.get(groupId);
        button.setText(getString(verb) + " " + group.getTitle());
    }
}
