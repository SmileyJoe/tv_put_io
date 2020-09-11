package io.smileyjoe.putio.tv.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.ui.fragment.VideoDetailsFragment;
import io.smileyjoe.putio.tv.putio.File;

/*
 * Details activity class that loads LeanbackDetailsFragment class
 */
public class DetailsActivity extends Activity implements VideoDetailsFragment.Listener {
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String VIDEO = "video";
    public static final String RELATED_VIDEOS = "related_videos";

    public static Intent getIntent(Context context, File video){
        return getIntent(context, video, null);
    }

    public static Intent getIntent(Context context, File video, ArrayList<File> relatedVideos){
        Intent intent = new Intent(context, DetailsActivity.class);

        intent.putExtra(VIDEO, video);

        if(relatedVideos != null && !relatedVideos.isEmpty()){
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
    public void onWatchClicked(File file) {
        startActivity(PlaybackActivity.getIntent(getBaseContext(), file));
    }

    @Override
    public void onConvertClicked(File file) {
        // todo: convert to mp4 //
    }

    @Override
    public void onRelatedClicked(File file, ArrayList<File> relatedVideos) {
        startActivity(getIntent(getBaseContext(), file, relatedVideos));
    }

    @Override
    public void onResumeClick(File file) {
        startActivity(PlaybackActivity.getIntent(getBaseContext(), file, true));
    }
}
