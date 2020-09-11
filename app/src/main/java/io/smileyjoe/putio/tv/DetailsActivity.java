package io.smileyjoe.putio.tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import io.smileyjoe.putio.tv.putio.File;
import io.smileyjoe.putio.tv.ui.activity.PlaybackActivity;

/*
 * Details activity class that loads LeanbackDetailsFragment class
 */
public class DetailsActivity extends Activity implements VideoDetailsFragment.Listener{
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String VIDEO = "video";

    public static Intent getIntent(Context context, File video){
        Intent intent = new Intent(context, DetailsActivity.class);

        intent.putExtra(VIDEO, video);

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
}
