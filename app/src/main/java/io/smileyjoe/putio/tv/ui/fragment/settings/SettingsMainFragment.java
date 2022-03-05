package io.smileyjoe.putio.tv.ui.fragment.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import java.util.List;

import io.smileyjoe.putio.tv.R;

public class SettingsMainFragment extends SettingsBaseFragment {

    private static final int ID_ABOUT = 1;
    private static final int ID_ACCOUNT = 2;
    private static final int ID_SETTINGS = 3;
    private static final int ID_GROUPS = 4;

    @Override
    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(@NonNull Bundle savedInstanceState) {
        return getGuidance(R.string.settings_title_main, R.string.settings_description_main);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions,
                                Bundle savedInstanceState) {
        actions.add(SettingsGroupFragment.getAction(getContext(), ID_GROUPS));
        actions.add(SettingsAccountFragment.getAction(getContext(), ID_ACCOUNT));
        actions.add(SettingsAboutFragment.getAction(getContext(), ID_ABOUT));
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        if (action.getId() == ID_ABOUT) {
            GuidedStepSupportFragment.add(getParentFragmentManager(), new SettingsAboutFragment());
        } else if (action.getId() == ID_ACCOUNT) {
            GuidedStepSupportFragment.add(getParentFragmentManager(), new SettingsAccountFragment());
        } else if (action.getId() == ID_GROUPS) {
            GuidedStepSupportFragment.add(getParentFragmentManager(), new SettingsGroupFragment());
        } else {
            getActivity().finishAfterTransition();
        }
    }

}
