package io.smileyjoe.putio.tv.ui.activity;

import android.content.Context;
import android.content.Intent;

import io.smileyjoe.putio.tv.databinding.ActivitySettingsBinding;

public class SettingsActivity extends BaseActivity<ActivitySettingsBinding> {

    public static Intent getIntent(Context context){
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected ActivitySettingsBinding inflate() {
        return ActivitySettingsBinding.inflate(getLayoutInflater());
    }
}
