package io.smileyjoe.putio.tv.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.action.video.ActionOption;
import io.smileyjoe.putio.tv.action.video.GroupAction;
import io.smileyjoe.putio.tv.action.video.PlayAction;
import io.smileyjoe.putio.tv.action.video.RefreshAction;
import io.smileyjoe.putio.tv.action.video.ResumeAction;
import io.smileyjoe.putio.tv.action.video.TrailerAction;
import io.smileyjoe.putio.tv.databinding.ActivityDetailsBackdropBinding;
import io.smileyjoe.putio.tv.interfaces.VideoDetails;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.viewholder.VideoDetailsViewHolder;

public class VideoDetailsBackdropActivity extends BaseActivity<ActivityDetailsBackdropBinding> implements VideoDetails, PlayAction, ResumeAction, RefreshAction, GroupAction, TrailerAction {

    private static final String EXTRA_VIDEO = "video";

    private Video mVideo;
    private VideoDetailsViewHolder mVideoDetailsViewHolder;
    private int mButtonMargin;
    private HashMap<Long, Group> mHashGroups;

    public static Intent getIntent(Context context, Video video) {
        Intent intent = new Intent(context, VideoDetailsBackdropActivity.class);

        intent.putExtra(EXTRA_VIDEO, video);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHashGroups = new HashMap<>();
        mButtonMargin = getResources().getDimensionPixelOffset(R.dimen.padding_general);

        handleExtras();
        populate();
        getData();
    }

    @Override
    protected ActivityDetailsBackdropBinding inflate() {
        return ActivityDetailsBackdropBinding.inflate(getLayoutInflater());
    }

    @Override
    public void setupActions() {
        mView.layoutButtons.removeAllViews();
        PlayAction.super.setupActions();
        ResumeAction.super.setupActions();
        GroupAction.super.setupActions();
        RefreshAction.super.setupActions();
        TrailerAction.super.setupActions();
    }

    @Override
    public void onResume() {
        super.onResume();
        getResumeTime();
    }

    @Override
    public void update(Video video) {
        populate();
    }

    @Override
    public void handleClick(ActionOption option) {
        switch (option) {
            case RESUME:
                ResumeAction.super.handleClick(option);
                break;
            case WATCH:
                PlayAction.super.handleClick(option);
                break;
            case REFRESH_DATA:
                RefreshAction.super.handleClick(option);
                break;
            case TRAILER:
                TrailerAction.super.handleClick(option);
                break;
        }
    }

    private void populate() {
        setImage(mView.imagePoster, mVideo.getPosterAsUri());
        setImage(mView.imageBackdrop, mVideo.getBackdropAsUri());
        setupActions();

        if (mVideoDetailsViewHolder == null) {
            mView.frameDetails.removeAllViews();
            mVideoDetailsViewHolder = VideoDetailsViewHolder.getInstance(getBaseContext(), mView.frameDetails, true);
            mVideoDetailsViewHolder.addTextShadow();
        }

        mVideoDetailsViewHolder.bind(mVideo);
    }

    @Override
    public void addAction(ActionOption option, String title, String subtitle, boolean shouldShow) {
        MaterialButton button = addActionButton(getTitleFormatted(title, subtitle), option.getId());
        button.setOnClickListener(new OnButtonClicked(option, this));
        if (!shouldShow) {
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public void addActionGroup(Group group, String verb, String title) {
        MaterialButton button = addActionButton(verb + " " + title, getGroupActionId(group.getId()));
        button.setOnClickListener(view -> onGroupActionClicked(group.getId()));
        mHashGroups.put(group.getIdAsLong(), group);
    }

    private MaterialButton addActionButton(String title, long tag) {
        MaterialButton button = (MaterialButton) LayoutInflater.from(getBaseContext()).inflate(R.layout.include_button_backdrop, null);
        button.setText(title);
        button.setTag(tag);
        mView.layoutButtons.addView(button);
        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) button.getLayoutParams();
        params.leftMargin = mButtonMargin;
        params.rightMargin = mButtonMargin;
        return button;
    }

    private void setImage(ImageView image, Uri uri) {
        Glide.with(getBaseContext())
                .load(uri)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image);
    }

    private void handleExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(EXTRA_VIDEO)) {
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
        ((MaterialButton) mView.layoutButtons.findViewWithTag(getGroupActionId(groupId))).setText(getString(verb) + " " + group.getTitle());
    }

    @Override
    public void updateActionResume() {
        ActionOption option = ActionOption.RESUME;
        MaterialButton button = mView.layoutButtons.findViewWithTag(option.getId());
        String title = getString(option.getTitleResId()) + " : " + mVideo.getResumeTimeFormatted();

        if (button.getVisibility() != View.VISIBLE) {
            button.setVisibility(View.VISIBLE);
        }

        button.setText(title);
    }
}
