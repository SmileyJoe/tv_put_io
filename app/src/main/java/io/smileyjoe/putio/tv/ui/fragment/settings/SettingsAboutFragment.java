package io.smileyjoe.putio.tv.ui.fragment.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import java.util.List;

import io.smileyjoe.putio.tv.BuildConfig;
import io.smileyjoe.putio.tv.R;

public class SettingsAboutFragment extends SettingsBaseFragment {

    public static GuidedAction getAction(Context context, int id) {
        return new GuidedAction.Builder(context)
                .id(id)
                .title(R.string.settings_title_about)
                .description(R.string.settings_description_about)
                .build();
    }

    @Override
    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(@NonNull Bundle savedInstanceState) {
        return getGuidance(R.string.settings_title_about, R.string.settings_description_about);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions,
                                Bundle savedInstanceState) {
        actions.add(getAction(R.string.settings_title_version, BuildConfig.VERSION_NAME));
    }

}
