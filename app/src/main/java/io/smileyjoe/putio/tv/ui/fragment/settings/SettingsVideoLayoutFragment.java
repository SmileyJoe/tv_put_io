package io.smileyjoe.putio.tv.ui.fragment.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import java.util.Arrays;
import java.util.List;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.ui.adapter.VideosAdapter;
import io.smileyjoe.putio.tv.util.Settings;

public class SettingsVideoLayoutFragment extends SettingsBaseFragment {

    private static final int GROUP_ID_STYLE = 1;

    public static GuidedAction getAction(Context context, int id) {
        return new GuidedAction.Builder(context)
                .id(id)
                .title(R.string.settings_title_video_layout)
                .description(Settings.getInstance(context).getVideoLayout().getTitle())
                .build();
    }

    @Override
    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(@NonNull Bundle savedInstanceState) {
        return getGuidance(R.string.settings_title_video_layout, R.string.settings_description_video_layout);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions,
                                Bundle savedInstanceState) {

        Arrays.stream(VideosAdapter.Style.values())
                .forEach(style -> {
                    actions.add(getActionGroup(
                            getString(style.getTitle()),
                            null,
                            style.getIcon(),
                            GROUP_ID_STYLE,
                            style.getId(),
                            style == getSettings().getVideoLayout()
                    ));
                });
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        VideosAdapter.Style style = VideosAdapter.Style.fromId(Math.toIntExact(action.getId()));

        if (style != null) {
            getSettings().setVideoLayout(getContext(), style.getId());
        }

        getParentFragmentManager().popBackStack();
    }

}
