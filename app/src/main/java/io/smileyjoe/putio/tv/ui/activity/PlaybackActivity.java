package io.smileyjoe.putio.tv.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import io.smileyjoe.putio.tv.ui.fragment.PlaybackVideoFragment;
import io.smileyjoe.putio.tv.putio.File;

/**
 * Loads {@link PlaybackVideoFragment}.
 */
public class PlaybackActivity extends FragmentActivity {

    public static final String EXTRA_FILE = "file";
    public static final String EXTRA_SHOULD_RESUME = "should_resume";

    public static Intent getIntent(Context context, File file){
        return getIntent(context, file, false);
    }

    public static Intent getIntent(Context context, File file, boolean shouldResume){
        Intent intent = new Intent(context, PlaybackActivity.class);
        intent.putExtra(EXTRA_FILE, file);
        intent.putExtra(EXTRA_SHOULD_RESUME, shouldResume);
        return intent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, new PlaybackVideoFragment())
                    .commit();
        }
    }
}
