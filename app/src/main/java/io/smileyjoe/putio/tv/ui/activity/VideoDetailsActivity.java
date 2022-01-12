package io.smileyjoe.putio.tv.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.fragment.VideoDetailsFragment;
import io.smileyjoe.putio.tv.util.VideoDetailsHelper;

/*
 * Details activity class that loads LeanbackDetailsFragment class
 */
public class VideoDetailsActivity extends Activity implements VideoDetailsFragment.Listener {
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String VIDEO = "video";
    public static final String RELATED_VIDEOS = "related_videos";

    public static Intent getIntent(Context context, Video video) {
        return getIntent(context, video, null);
    }

    public static Intent getIntent(Context context, Video video, ArrayList<Video> relatedVideos) {
        Intent intent = new Intent(context, VideoDetailsActivity.class);

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
    public void onRelatedClicked(Video video, ArrayList<Video> relatedVideos) {
        startActivity(getIntent(getBaseContext(), video, relatedVideos));
        finish();
    }
}
