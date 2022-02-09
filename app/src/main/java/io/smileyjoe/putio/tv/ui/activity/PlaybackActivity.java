package io.smileyjoe.putio.tv.ui.activity;

import static android.view.View.FOCUS_LEFT;
import static android.view.View.FOCUS_RIGHT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.TracksInfo;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.ui.SubtitleView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.ActivityPlaybackBinding;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.MediaType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.fragment.BaseFragment;
import io.smileyjoe.putio.tv.ui.fragment.ErrorFragment;
import io.smileyjoe.putio.tv.ui.fragment.PlaybackVideoFragment;
import io.smileyjoe.putio.tv.ui.fragment.SubtitleFragment;
import io.smileyjoe.putio.tv.ui.fragment.TrackGroupSelectionFragment;

public class PlaybackActivity extends BaseActivity<ActivityPlaybackBinding> implements PlaybackVideoFragment.Listener, SubtitleFragment.Listener, ErrorFragment.Listener, TrackGroupSelectionFragment.Listener, BaseFragment.OnFocusSearchListener {

    private enum PlayAction {
        NEXT, PREVIOUS
    }

    public static final String EXTRA_VIDEO = "video";
    public static final String EXTRA_VIDEOS = "videos";
    public static final String EXTRA_SHOULD_RESUME = "should_resume";
    public static final String EXTRA_YOUTUBE_URL = "youtube_url";
    public static final String EXTRA_MEDIA_TYPE = "media_type";

    private PlaybackVideoFragment mPlaybackVideoFragment;
    private ArrayList<Video> mVideos;
    private BroadcastTick mBroadcastTick;
    private final SimpleDateFormat mFormatWatchTime = new SimpleDateFormat("HH:mm");
    private SubtitleFragment mSubtitleFragment;
    private TrackGroupSelectionFragment mTrackGroupSelectionFragment;
    private Video mVideo;
    private String mYoutubeUrl;
    private MediaType mMediaType;
    @IdRes
    private final int[] mRightPanelIds = new int[]{R.id.fragment_subtitle, R.id.fragment_track_group_selection};

    public static Intent getIntent(Context context, Video video) {
        return getIntent(context, video, false);
    }

    public static Intent getIntent(Context context, Video video, boolean shouldResume) {
        Intent intent = new Intent(context, PlaybackActivity.class);
        intent.putExtra(EXTRA_VIDEO, video);
        intent.putExtra(EXTRA_SHOULD_RESUME, shouldResume);
        intent.putExtra(EXTRA_MEDIA_TYPE, MediaType.VIDEO);
        return intent;
    }

    public static Intent getIntent(Context context, ArrayList<Video> videos, Video video, boolean shouldResume) {
        Intent intent = new Intent(context, PlaybackActivity.class);
        intent.putExtra(EXTRA_VIDEOS, videos);
        intent.putExtra(EXTRA_VIDEO, video);
        intent.putExtra(EXTRA_SHOULD_RESUME, shouldResume);
        intent.putExtra(EXTRA_MEDIA_TYPE, MediaType.VIDEO);
        return intent;
    }

    public static Intent getIntent(Context context, String youtubeUrl) {
        Intent intent = new Intent(context, PlaybackActivity.class);
        intent.putExtra(EXTRA_YOUTUBE_URL, youtubeUrl);
        intent.putExtra(EXTRA_MEDIA_TYPE, MediaType.YOUTUBE);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleExtras();

        mView.textTime.setText(mFormatWatchTime.format(new Date()));

        mSubtitleFragment = (SubtitleFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_subtitle);
        mTrackGroupSelectionFragment = (TrackGroupSelectionFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_track_group_selection);

        if (mMediaType == MediaType.VIDEO) {
            mSubtitleFragment.setPutId(mVideo.getPutId());
            mSubtitleFragment.setListener(this);
            mSubtitleFragment.setFocusSearchListener(this);
            mSubtitleFragment.setForceFocus(true);

            mTrackGroupSelectionFragment.setListener(this);
            mTrackGroupSelectionFragment.setFocusSearchListener(this);
            mTrackGroupSelectionFragment.setForceFocus(true);
        }

        hideRightPanel();
        mPlaybackVideoFragment = (PlaybackVideoFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_playback);

        if (mVideos != null && mVideos.size() > 1) {
            mPlaybackVideoFragment.showNextPrevious();
        }

        play();
    }

    @Override
    protected ActivityPlaybackBinding inflate() {
        return ActivityPlaybackBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onStart() {
        super.onStart();
        mBroadcastTick = new BroadcastTick();

        registerReceiver(mBroadcastTick, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mBroadcastTick != null) {
            unregisterReceiver(mBroadcastTick);
        }
    }

    @Override
    public void onControlsVisibilityChanged(boolean isShown) {
        if (isShown) {
            mView.textTime.setVisibility(View.VISIBLE);
        } else {
            mView.textTime.setVisibility(View.GONE);
            hideRightPanel();
        }
    }

    @Override
    public void onSubtitlesClicked() {
        toggleRightPanel(R.id.fragment_subtitle);

        if(mSubtitleFragment.isVisible()){
            mSubtitleFragment.requestFocus();
        }
    }

    @Override
    public void onAudioTracksClicked(TracksInfo tracksInfo) {
        mTrackGroupSelectionFragment.setTracksInfo(C.TRACK_TYPE_AUDIO, tracksInfo);
        toggleRightPanel(R.id.fragment_track_group_selection);

        if(mTrackGroupSelectionFragment.isVisible()){
            mTrackGroupSelectionFragment.requestFocus();
        }
    }

    @Override
    public void showSubtitles(Uri uri) {
        if (uri != null) {
            mView.exoSubtitle.setVisibility(View.VISIBLE);
            hideRightPanel();
            mPlaybackVideoFragment.showSubtitles(uri);
        } else {
            mView.exoSubtitle.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTrackSelected(@C.TrackType int trackType, TrackGroup item) {
        hideRightPanel();
        mPlaybackVideoFragment.loadTrack(item);
    }

    @Override
    public SubtitleView getSubtitleView() {
        return mView.exoSubtitle;
    }

    @Override
    public void onNextClicked(Video current) {
        play(current, PlayAction.NEXT);
    }

    @Override
    public void onPreviousClicked(Video current) {
        play(current, PlayAction.PREVIOUS);
    }

    @Override
    public void onBackPressed() {
        if (mSubtitleFragment.isVisible() || mTrackGroupSelectionFragment.isVisible()) {
            hideRightPanel();
        } else {
            super.onBackPressed();
        }
    }

    private void toggleRightPanel(@IdRes int idToToggle){
        AtomicBoolean isShowing = new AtomicBoolean(false);
        Arrays.stream(mRightPanelIds)
                .forEach(id -> {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(id);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    if(id == idToToggle && !fragment.isVisible()){
                        transaction.show(fragment);
                        isShowing.set(true);
                    } else {
                        transaction.hide(fragment);
                    }

                    transaction.commit();
                });
        if(isShowing.get()){
            mView.animLayoutRightPanel.enter();
        } else {
            mView.animLayoutRightPanel.exit();
        }
    }

    private void hideRightPanel(){
        mView.animLayoutRightPanel.exit();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(mSubtitleFragment);
        transaction.hide(mTrackGroupSelectionFragment);
        transaction.commit();
    }

    private void handleExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(EXTRA_MEDIA_TYPE)) {
                mMediaType = (MediaType) extras.getSerializable(EXTRA_MEDIA_TYPE);
            }

            if (extras.containsKey(EXTRA_VIDEOS)) {
                mVideos = extras.getParcelableArrayList(EXTRA_VIDEOS);
            }

            if (extras.containsKey(EXTRA_VIDEO)) {
                mVideo = getIntent().getParcelableExtra(PlaybackActivity.EXTRA_VIDEO);
            }

            if (extras.containsKey(EXTRA_YOUTUBE_URL)) {
                mYoutubeUrl = getIntent().getStringExtra(EXTRA_YOUTUBE_URL);
            }
        }
    }

    private void play() {
        switch (mMediaType) {
            case YOUTUBE:
                play(mYoutubeUrl);
                break;
            case VIDEO:
                play(mVideo);
                break;
        }
    }

    private void play(String youtubeUrl) {
        mPlaybackVideoFragment.play(youtubeUrl);
    }

    private void play(Video video) {
        mPlaybackVideoFragment.play(video);
    }

    private boolean play(Video current, PlayAction action) {
        if (mVideos != null) {
            int nextEpisode = current.getEpisode();

            switch (action) {
                case NEXT:
                    nextEpisode = nextEpisode + 1;
                    break;
                case PREVIOUS:
                    nextEpisode = nextEpisode - 1;
                    break;
            }

            for (Video video : mVideos) {
                if (video.getEpisode() == nextEpisode) {
                    play(video);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void showError() {
        @StringRes int message;
        switch (mMediaType) {
            case YOUTUBE:
                message = R.string.error_trailer;
                break;
            case VIDEO:
                message = R.string.error_video;
                break;
            default:
                message = R.string.error_generic;
                break;
        }

        ErrorFragment.show(this, R.string.title_error, message, R.id.layout_main);
    }

    @Override
    public void onErrorDismissed() {
        finish();
    }

    @Override
    public void onPlayComplete(Video videoCompleted) {
        boolean playingNext = play(videoCompleted, PlayAction.NEXT);

        if (!playingNext) {
            finish();
        }
    }

    @Override
    public View onFocusSearch(View focused, int direction, FragmentType type) {
        return focused;
    }

    private class BroadcastTick extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                mView.textTime.setText(mFormatWatchTime.format(new Date()));
            }
        }
    }
}
