package io.smileyjoe.putio.tv.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.fragment.PlaybackVideoFragment;

public class PlaybackActivity extends FragmentActivity implements PlaybackVideoFragment.Listener{

    public static final String EXTRA_VIDEO = "video";
    public static final String EXTRA_VIDEOS = "videos";
    public static final String EXTRA_SELECTED_PUT_IO = "selected_put_id";
    public static final String EXTRA_SHOULD_RESUME = "should_resume";

    private PlaybackVideoFragment mPlaybackVideoFragment;
    private ArrayList<Video> mVideos;

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
        if (savedInstanceState == null) {
            mPlaybackVideoFragment = new PlaybackVideoFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, mPlaybackVideoFragment)
                    .commit();
        }

        handleExtras();
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
