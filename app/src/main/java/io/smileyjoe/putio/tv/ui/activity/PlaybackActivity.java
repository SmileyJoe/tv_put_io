package io.smileyjoe.putio.tv.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.fragment.PlaybackVideoFragment;
import io.smileyjoe.putio.tv.ui.fragment.SubtitleFragment;

public class PlaybackActivity extends FragmentActivity implements PlaybackVideoFragment.Listener, SubtitleFragment.Listener{

    public static final String EXTRA_VIDEO = "video";
    public static final String EXTRA_VIDEOS = "videos";
    public static final String EXTRA_SHOULD_RESUME = "should_resume";

    private PlaybackVideoFragment mPlaybackVideoFragment;
    private ArrayList<Video> mVideos;
    private TextView mTextTime;
    private BroadcastTick mBroadcastTick;
    private final SimpleDateFormat mFormatWatchTime = new SimpleDateFormat("HH:mm");
    private SubtitleFragment mSubtitleFragment;
    private Video mVideo;
    private TextView mTextSubtitle;

    public static Intent getIntent(Context context, Video video) {
        return getIntent(context, video, false);
    }

    public static Intent getIntent(Context context, Video video, boolean shouldResume) {
        Intent intent = new Intent(context, PlaybackActivity.class);
        intent.putExtra(EXTRA_VIDEO, video);
        intent.putExtra(EXTRA_SHOULD_RESUME, shouldResume);
        return intent;
    }

    public static Intent getIntent(Context context, ArrayList<Video> videos, Video video, boolean shouldResume) {
        Intent intent = new Intent(context, PlaybackActivity.class);
        intent.putExtra(EXTRA_VIDEOS, videos);
        intent.putExtra(EXTRA_VIDEO, video);
        intent.putExtra(EXTRA_SHOULD_RESUME, shouldResume);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playback);

        handleExtras();

        mTextSubtitle = findViewById(R.id.text_subtitle);

        mTextTime = findViewById(R.id.text_time);
        mTextTime.setText(mFormatWatchTime.format(new Date()));

        mSubtitleFragment = (SubtitleFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_subtitle);
        setSubtitleVisibility(false);
        mSubtitleFragment.setPutId(mVideo.getPutId());
        mSubtitleFragment.setListener(this);

        if (savedInstanceState == null) {
            mPlaybackVideoFragment = new PlaybackVideoFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_content, mPlaybackVideoFragment)
                    .commit();
        }

        play(mVideo);
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
            mTextTime.setVisibility(View.VISIBLE);
        } else {
            mTextTime.setVisibility(View.GONE);
            setSubtitleVisibility(false);
        }
    }

    @Override
    public void onSubtitlesClicked() {
        setSubtitleVisibility(!mSubtitleFragment.isVisible());
    }

    @Override
    public void showSubtitles(Uri uri) {
        mPlaybackVideoFragment.showSubtitles(uri);
    }

    @Override
    public void showSubtitle(String subTitle) {
        if(TextUtils.isEmpty(subTitle)){
            mTextSubtitle.setVisibility(View.GONE);
        } else {
            mTextSubtitle.setVisibility(View.VISIBLE);
            mTextSubtitle.setText(subTitle);
        }
    }

    @Override
    public void onBackPressed() {
        if(mSubtitleFragment.isVisible()){
            setSubtitleVisibility(false);
        } else {
            super.onBackPressed();
        }
    }

    private void setSubtitleVisibility(boolean visible){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(!visible){
            transaction.hide(mSubtitleFragment);
        } else {
            transaction.show(mSubtitleFragment);
        }

        transaction.commit();
    }

    private void handleExtras(){
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            if(extras.containsKey(EXTRA_VIDEOS)){
                mVideos = extras.getParcelableArrayList(EXTRA_VIDEOS);
            }

            if(extras.containsKey(EXTRA_VIDEO)){
                mVideo = getIntent().getParcelableExtra(PlaybackActivity.EXTRA_VIDEO);
            }
        }
    }

    private void play(Video video){
        mPlaybackVideoFragment.play(video);
    }

    @Override
    public void onPlayComplete(Video videoCompleted) {
        if(mVideos != null){
            int nextEpisode = videoCompleted.getEpisode() + 1;

            for(Video video:mVideos){
                if(video.getEpisode() == nextEpisode){
                    play(video);
                    return;
                }
            }
        }

        finish();
    }

    private class BroadcastTick extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                mTextTime.setText(mFormatWatchTime.format(new Date()));
            }
        }
    }
}
