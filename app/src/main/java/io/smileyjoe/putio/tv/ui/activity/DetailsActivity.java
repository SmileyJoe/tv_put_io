package io.smileyjoe.putio.tv.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.fragment.PlaybackVideoFragment;
import io.smileyjoe.putio.tv.ui.fragment.VideoDetailsFragment;

/*
 * Details activity class that loads LeanbackDetailsFragment class
 */
public class DetailsActivity extends Activity implements VideoDetailsFragment.Listener {
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String VIDEO = "video";
    public static final String RELATED_VIDEOS = "related_videos";

    public static Intent getIntent(Context context, Video video) {
        return getIntent(context, video, null);
    }

    public static Intent getIntent(Context context, Video video, ArrayList<Video> relatedVideos) {
        Intent intent = new Intent(context, DetailsActivity.class);

        intent.putExtra(VIDEO, video);

        if (relatedVideos != null && !relatedVideos.isEmpty()) {
            intent.putExtra(RELATED_VIDEOS, relatedVideos);
        }

        return intent;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }

    @Override
    public void onWatchClicked(Video video, ArrayList<Video> videos) {
        play(video, videos, false);
    }

    @Override
    public void onConvertClicked(Video video) {
        Putio.convertFile(getBaseContext(), video.getPutId(), new OnConvertResponse());
    }

    @Override
    public void onRelatedClicked(Video video, ArrayList<Video> relatedVideos) {
        startActivity(getIntent(getBaseContext(), video, relatedVideos));
    }

    @Override
    public void onResumeClick(Video video, ArrayList<Video> videos) {
        play(video, videos, true);
    }

    private void play(Video video, ArrayList<Video> videos, boolean shouldResume){
        if(video.getVideoType() == VideoType.EPISODE){
            if(videos != null && !videos.isEmpty()) {
                startActivity(PlaybackActivity.getIntent(getBaseContext(), videos, video.getPutId(), shouldResume));
                return;
            }
        }

        startActivity(PlaybackActivity.getIntent(getBaseContext(), video, shouldResume));
    }

    private class OnConvertResponse extends Response {
        @Override
        public void onSuccess(JsonObject result) {
            // todo: this isn't working correctly //
            VideoDetailsFragment detailsFragment = (VideoDetailsFragment) getFragmentManager().findFragmentById(R.id.details_fragment);
            detailsFragment.conversionStarted();
        }
    }
}
