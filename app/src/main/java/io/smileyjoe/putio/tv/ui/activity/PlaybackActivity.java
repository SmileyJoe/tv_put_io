package io.smileyjoe.putio.tv.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.TracksInfo;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.ui.SubtitleView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.ActivityPlaybackBinding;
import io.smileyjoe.putio.tv.object.MediaType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.fragment.TrackGroupSelectionFragment;
import io.smileyjoe.putio.tv.ui.fragment.ErrorFragment;
import io.smileyjoe.putio.tv.ui.fragment.PlaybackVideoFragment;
import io.smileyjoe.putio.tv.ui.fragment.SubtitleFragment;

public class PlaybackActivity extends BaseActivity<ActivityPlaybackBinding> implements PlaybackVideoFragment.Listener, SubtitleFragment.Listener, ErrorFragment.Listener, TrackGroupSelectionFragment.Listener {

    private enum PlayAction{
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

    public static Intent getIntent(Context context, String youtubeUrl){
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
        setFragmentVisibility(mSubtitleFragment, false);
        if(mMediaType == MediaType.VIDEO) {
            mSubtitleFragment.setPutId(mVideo.getPutId());
            mSubtitleFragment.setListener(this);
        }

        mTrackGroupSelectionFragment = (TrackGroupSelectionFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_track_group_selection);
        mTrackGroupSelectionFragment.setListener(this);
        setFragmentVisibility(mTrackGroupSelectionFragment, false);

        if (savedInstanceState == null) {
            mPlaybackVideoFragment = new PlaybackVideoFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_content, mPlaybackVideoFragment)
                    .commit();
        }

        if(mVideos != null && mVideos.size() > 1){
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
        if(isShown){
            mView.textTime.setVisibility(View.VISIBLE);
        } else {
            mView.textTime.setVisibility(View.GONE);
            setFragmentVisibility(mSubtitleFragment, false);
        }
    }

    @Override
    public void onSubtitlesClicked() {
        setFragmentVisibility(mSubtitleFragment, !mSubtitleFragment.isVisible());
        setFragmentVisibility(mTrackGroupSelectionFragment, false);
    }

    @Override
    public void onAudioTracksClicked(TracksInfo tracksInfo) {
        mTrackGroupSelectionFragment.setTracksInfo(C.TRACK_TYPE_AUDIO, tracksInfo);
        setFragmentVisibility(mTrackGroupSelectionFragment, !mTrackGroupSelectionFragment.isVisible());
        setFragmentVisibility(mSubtitleFragment, false);
    }

    @Override
    public void showSubtitles(Uri uri) {
        if(uri != null) {
            mView.exoSubtitle.setVisibility(View.VISIBLE);
            setFragmentVisibility(mSubtitleFragment, false);
            mPlaybackVideoFragment.showSubtitles(uri);
        } else {
            mView.exoSubtitle.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTrackSelected(@C.TrackType int trackType,  TrackGroup item) {
        setFragmentVisibility(mTrackGroupSelectionFragment, false);
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
        if(mSubtitleFragment.isVisible()){
            setFragmentVisibility(mSubtitleFragment, false);
        } else if(mTrackGroupSelectionFragment.isVisible()) {
            setFragmentVisibility(mTrackGroupSelectionFragment, false);
        } else {
            super.onBackPressed();
        }
    }

    private void setFragmentVisibility(Fragment fragment, boolean visible){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(!visible){
            transaction.hide(fragment);
        } else {
            transaction.show(fragment);
        }

        transaction.commit();
    }

    private void handleExtras(){
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            if(extras.containsKey(EXTRA_MEDIA_TYPE)){
                mMediaType = (MediaType) extras.getSerializable(EXTRA_MEDIA_TYPE);
            }

            if(extras.containsKey(EXTRA_VIDEOS)){
                mVideos = extras.getParcelableArrayList(EXTRA_VIDEOS);
            }

            if(extras.containsKey(EXTRA_VIDEO)){
                mVideo = getIntent().getParcelableExtra(PlaybackActivity.EXTRA_VIDEO);
            }

            if(extras.containsKey(EXTRA_YOUTUBE_URL)){
                mYoutubeUrl = getIntent().getStringExtra(EXTRA_YOUTUBE_URL);
            }
        }
    }

    private void play(){
        switch (mMediaType){
            case YOUTUBE:
                play(mYoutubeUrl);
                break;
            case VIDEO:
                play(mVideo);
                break;
        }
    }

    private void play(String youtubeUrl){
        mPlaybackVideoFragment.play(youtubeUrl);
    }

    private void play(Video video){
        mPlaybackVideoFragment.play(video);
    }

    private boolean play(Video current, PlayAction action){
        if(mVideos != null){
            int nextEpisode = current.getEpisode();

            switch (action){
                case NEXT:
                    nextEpisode = nextEpisode + 1;
                    break;
                case PREVIOUS:
                    nextEpisode = nextEpisode - 1;
                    break;
            }

            for(Video video:mVideos){
                if(video.getEpisode() == nextEpisode){
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
        switch (mMediaType){
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

        if(!playingNext) {
            finish();
        }
    }

    private class BroadcastTick extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                mView.textTime.setText(mFormatWatchTime.format(new Date()));
            }
        }
    }
}
