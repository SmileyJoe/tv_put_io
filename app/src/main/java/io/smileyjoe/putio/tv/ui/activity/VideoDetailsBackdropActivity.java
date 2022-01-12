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

    private void populate(){
        setImage(findViewById(R.id.image_poster), mVideo.getPosterAsUri());
        setImage(findViewById(R.id.image_backdrop), mVideo.getBackdropAsUri());
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
            if(action != DetailsAction.UNKNOWN) {
                boolean shouldAdd = true;
                String title = getString(action.getTitleResId());
                switch (action) {
                    case RESUME:
                        if (mVideo.getResumeTime() > 0) {
                            title = title + " : " + mVideo.getResumeTimeFormatted();
                        } else {
                            shouldAdd = false;
                        }
                        break;
                }

                MaterialButton button = addActionButton(title, action.getId(), action.getPosition());
                button.setOnClickListener(new VideoDetailsHelper.OnActionButtonClicked(action, this));
                if (!action.shouldShow() || !shouldAdd) {
                    button.setVisibility(View.GONE);
                }
            }
        }

        addGroupActions((group, verb, title) -> {
            MaterialButton button = addActionButton(verb + " " + title, group.getId() + 100);
            button.setOnClickListener(view -> {
                onGroupClicked(group.getId());
            });
            mHashGroups.put(group.getIdAsLong(), group);
        });
    }

    private MaterialButton addActionButton(String title, long tag){
        return addActionButton(title, tag, -1);
    }

    private MaterialButton addActionButton(String title, long tag, int position){
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
    public void updateGroupAction(long groupId, int verb) {
        Group group = mHashGroups.get(groupId);
        ((MaterialButton) mLayoutButtons.findViewWithTag(groupId + 100)).setText(getString(verb) + " " + group.getTitle());
    }

    @Override
    public void updateResumeAction() {
        DetailsAction action = DetailsAction.RESUME;
        MaterialButton button = mLayoutButtons.findViewWithTag(action.getId());
        String title = getString(action.getTitleResId()) + " : " + mVideo.getResumeTimeFormatted();

        if(button.getVisibility() != View.VISIBLE) {
            button.setVisibility(View.VISIBLE);
        }

        button.setText(title);
    }
}
