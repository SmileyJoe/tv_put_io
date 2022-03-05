package io.smileyjoe.putio.tv.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.GuidedStepSupportFragment;

import io.smileyjoe.putio.tv.ui.fragment.settings.SettingsMainFragment;

/**
 * Activity that showcases different aspects of GuidedStepFragments.
 */
public class SettingsActivity extends FragmentActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            GuidedStepSupportFragment.addAsRoot(this, new SettingsMainFragment(), android.R.id.content);
        }
    }
}
