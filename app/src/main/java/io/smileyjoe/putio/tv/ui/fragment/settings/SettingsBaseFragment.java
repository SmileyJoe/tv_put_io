package io.smileyjoe.putio.tv.ui.fragment.settings;

import androidx.annotation.StringRes;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import io.smileyjoe.putio.tv.R;

public class SettingsBaseFragment extends GuidedStepSupportFragment {

    protected GuidedAction getAction(@StringRes int title, String description){
        return new GuidedAction.Builder(getContext())
                .title(title)
                .description(description)
                .build();
    }

    protected GuidanceStylist.Guidance getGuidance(@StringRes int title, @StringRes int description){
        return new GuidanceStylist.Guidance(getString(title), getString(description), null, null);
    }

}
