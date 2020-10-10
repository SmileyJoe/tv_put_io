package io.smileyjoe.putio.tv.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.fragment.PlaybackVideoFragment;

public class PlaybackActivity extends FragmentActivity implements PlaybackVideoFragment.Listener{

    public static final String EXTRA_VIDEO = "video";
    public static final String EXTRA_VIDEOS = "videos";
    public static final String EXTRA_SELECTED_PUT_IO = "selected_put_id";
    public static final String EXTRA_SHOULD_RESUME = "should_resume";

    private PlaybackVideoFragment mPlaybackVideoFragment;
    private ArrayList<Video> mVideos;
    private TextView mTextTime;
    private BroadcastReceiver mBroadcastReceiver;
    private final SimpleDateFormat mFormatWatchTime = new SimpleDateFormat("HH:mm");

    public static Intent getIntent(Context context, Video video) {
        return getIntent(context, video, false);
    }

    public static Intent getIntent(Context context, Video video, boolean shouldResume) {
        Intent intent = new Intent(context, PlaybackActivity.class);
        intent.putExtra(EXTRA_VIDEO, video);
        intent.putExtra(EXTRA_SHOULD_RESUME, shouldResume);
        return intent;
    }

    public static Intent getIntent(Context context, ArrayList<Video> videos, long selectedPutId, boolean shouldResume) {
        Intent intent = new Intent(context, PlaybackActivity.class);
        intent.putExtra(EXTRA_VIDEOS, videos);
        intent.putExtra(EXTRA_SELECTED_PUT_IO, selectedPutId);
        intent.putExtra(EXTRA_SHOULD_RESUME, shouldResume);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playback);

        mTextTime = findViewById(R.id.text_time);
        mTextTime.setText(mFormatWatchTime.format(new Date()));

        if (savedInstanceState == null) {
            mPlaybackVideoFragment = new PlaybackVideoFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_content, mPlaybackVideoFragment)
                    .commit();
        }

        handleExtras();
    }

    @Override
    public void onStart() {
        super.onStart();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    mTextTime.setText(mFormatWatchTime.format(new Date()));
            }
        };

        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBroadcastReceiver != null)
            unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onControlsVisibilityChanged(boolean isShown) {
        if(isShown){
            mTextTime.setVisibility(View.VISIBLE);
        } else {
            mTextTime.setVisibility(View.GONE);
        }
    }

    private void handleExtras(){
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            if(extras.containsKey(EXTRA_VIDEO)){
                play(getIntent().getParcelableExtra(PlaybackActivity.EXTRA_VIDEO));
            }

            if(extras.containsKey(EXTRA_VIDEOS)){
                mVideos = extras.getParcelableArrayList(EXTRA_VIDEOS);
                long selectedPutId = extras.getLong(EXTRA_SELECTED_PUT_IO);

                for(Video video:mVideos){
                    if(video.getPutId() == selectedPutId){
                        play(video);
                        break;
                    }
                }
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
}
