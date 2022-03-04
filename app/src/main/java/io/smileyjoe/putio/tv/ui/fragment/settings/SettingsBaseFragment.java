package io.smileyjoe.putio.tv.ui.fragment.settings;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import io.smileyjoe.putio.tv.R;

public class SettingsBaseFragment extends GuidedStepSupportFragment {

    protected GuidedAction getAction(@StringRes int title, String description, @DrawableRes int icon){
        return new GuidedAction.Builder(getContext())
                .title(title)
                .description(description)
                .icon(icon)
                .infoOnly(true)
                .build();
    }

    protected GuidedAction getAction(@StringRes int title, String description){
        return getAction(title, description, 0);
    }

    protected GuidedAction getAction(@StringRes int title, String description, int id, boolean checked) {
        return getAction(getString(title), description, 0, id, checked);
    }

    protected GuidedAction getAction(String title, String description, @DrawableRes int icon, int id, boolean checked) {
        GuidedAction guidedAction = new GuidedAction.Builder(getContext())
                .title(title)
                .id(id)
                .description(description)
                .checkSetId(GuidedAction.CHECKBOX_CHECK_SET_ID)
                .icon(icon)
                .build();
        guidedAction.setChecked(checked);
        return guidedAction;
    }

    protected GuidanceStylist.Guidance getGuidance(@StringRes int title, @StringRes int description){
        return new GuidanceStylist.Guidance(getString(title), getString(description), null, null);
    }

}
