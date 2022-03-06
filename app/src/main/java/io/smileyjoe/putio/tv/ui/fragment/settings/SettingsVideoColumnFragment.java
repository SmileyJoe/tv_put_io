package io.smileyjoe.putio.tv.ui.fragment.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import java.util.Arrays;
import java.util.List;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.util.Settings;

public class SettingsVideoColumnFragment extends SettingsBaseFragment{

    private static final int GROUP_ID_COLS = 1;

    private final Integer[] mOptions = new Integer[]{5,7,9};

    public static GuidedAction getAction(Context context, int id) {
        return new GuidedAction.Builder(context)
                .id(id)
                .title(R.string.settings_title_video_cols)
                .description(Integer.toString(Settings.getInstance(context).getVideoNumCols()))
                .build();
    }

    @Override
    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(@NonNull Bundle savedInstanceState) {
        return getGuidance(R.string.settings_title_video_cols, R.string.settings_description_video_cols);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions,
                                Bundle savedInstanceState) {

        Arrays.stream(mOptions).forEach(cols -> {
            actions.add(getActionGroup(
                    Integer.toString(cols),
                    null,
                    0,
                    GROUP_ID_COLS,
                    cols,
                    cols == getSettings().getVideoNumCols()
            ));
        });
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        getSettings().setVideoNumCols(Math.toIntExact(action.getId()));
        getParentFragmentManager().popBackStack();
    }

}
