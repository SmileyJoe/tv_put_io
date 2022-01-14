package io.smileyjoe.putio.tv.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.action.video.ActionOption;
import io.smileyjoe.putio.tv.action.video.GroupAction;
import io.smileyjoe.putio.tv.action.video.Play;
import io.smileyjoe.putio.tv.action.video.Refresh;
import io.smileyjoe.putio.tv.action.video.Resume;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.viewholder.VideoDetailsViewHolder;
import io.smileyjoe.putio.tv.util.TmdbUtil;

public class VideoDetailsBackdropActivity extends FragmentActivity implements TmdbUtil.Listener, Play, Resume, Refresh, GroupAction {

    private static final String EXTRA_VIDEO = "video";

    private Video mVideo;
    private VideoDetailsViewHolder mVideoDetailsViewHolder;
    private LinearLayoutCompat mLayoutButtons;
    private int mButtonMargin;
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

        mHashGroups = new HashMap<>();
        mLayoutButtons = findViewById(R.id.layout_buttons);
        mButtonMargin = getResources().getDimensionPixelOffset(R.dimen.padding_general);

        handleExtras();
        populate();
        getData();
    }

    @Override
    public void setupActions() {
        mLayoutButtons.removeAllViews();
        Play.super.setupActions();
        Resume.super.setupActions();
        GroupAction.super.setupActions();
        Refresh.super.setupActions();
    }

    @Override
    public void onResume() {
        super.onResume();
        getResumeTime();
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

    @Override
    public void handleClick(ActionOption option) {
        switch (option){
            case RESUME:
                Resume.super.handleClick(option);
                break;
            case WATCH:
                Play.super.handleClick(option);
                break;
            case REFRESH_DATA:
                Refresh.super.handleClick(option);
                break;
        }
    }

    private void populate(){
        setImage(findViewById(R.id.image_poster), mVideo.getPosterAsUri());
        setImage(findViewById(R.id.image_backdrop), mVideo.getBackdropAsUri());
        setupActions();

        if(mVideoDetailsViewHolder == null) {
            FrameLayout frameDetails = findViewById(R.id.frame_details);
            frameDetails.removeAllViews();
            ViewGroup viewDetails = (ViewGroup) LayoutInflater.from(getBaseContext()).inflate(R.layout.item_video_details, frameDetails, true);
            mVideoDetailsViewHolder = new VideoDetailsViewHolder(viewDetails);
            mVideoDetailsViewHolder.addTextShadow();
        }

        mVideoDetailsViewHolder.bind(mVideo);
    }

    @Override
    public void addAction(ActionOption option, String title, boolean shouldShow) {
        MaterialButton button = addActionButton(title, option.getId());
        button.setOnClickListener(new OnButtonClicked(option, this));
        if (!shouldShow) {
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public void addActionGroup(Group group, String verb, String title) {
        MaterialButton button = addActionButton(verb + " " + title, getGroupActionId(group.getId()));
        button.setOnClickListener(view -> {
            onGroupActionClicked(group.getId());
        });
        mHashGroups.put(group.getIdAsLong(), group);
    }

    private MaterialButton addActionButton(String title, long tag){
        MaterialButton button = (MaterialButton) LayoutInflater.from(getBaseContext()).inflate(R.layout.include_button_backdrop, null);
        button.setText(title);
        button.setTag(tag);
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
    public void updateActionGroup(long groupId, int verb) {
        Group group = mHashGroups.get(groupId);
        ((MaterialButton) mLayoutButtons.findViewWithTag(getGroupActionId(groupId))).setText(getString(verb) + " " + group.getTitle());
    }

    @Override
    public void updateActionResume() {
        ActionOption option = ActionOption.RESUME;
        MaterialButton button = mLayoutButtons.findViewWithTag(option.getId());
        String title = getString(option.getTitleResId()) + " : " + mVideo.getResumeTimeFormatted();

        if(button.getVisibility() != View.VISIBLE) {
            button.setVisibility(View.VISIBLE);
        }

        button.setText(title);
    }
}
