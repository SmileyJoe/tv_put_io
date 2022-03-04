package io.smileyjoe.putio.tv.ui.fragment.settings;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import java.util.ArrayList;
import java.util.List;

import io.smileyjoe.putio.tv.BuildConfig;
import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.db.GroupDao;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.GroupType;

public class SettingsGroupFragment extends SettingsBaseFragment{

    public static GuidedAction getAction(Context context, int id){
        return new GuidedAction.Builder(context)
                .id(id)
                .title(R.string.settings_title_group)
                .description(R.string.settings_description_group)
                .build();
    }

    @Override
    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(@NonNull Bundle savedInstanceState) {
        return getGuidance(R.string.settings_title_group, R.string.settings_description_group);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new GetGroups().execute();
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {

        if(action.getId() == Putio.Files.PARENT_ID_RECENT){

        } else {
            new UpdateGroupEnabled(action.getId(), action.isChecked()).execute();
        }
    }

    private class UpdateGroupEnabled extends AsyncTask<Void, Void, Void> {

        private long mGroupId;
        private boolean mEnabled;

        public UpdateGroupEnabled(long groupId, boolean enabled) {
            mGroupId = groupId;
            mEnabled = enabled;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase.getInstance(getContext()).groupDao().enabled(mGroupId, mEnabled);
            return null;
        }
    }

    private class GetGroups extends AsyncTask<Void, Void, List<Group>> {
        @Override
        protected List<Group> doInBackground(Void... voids) {
            return AppDatabase.getInstance(getContext()).groupDao().getByType(GroupType.DIRECTORY.getId());
        }

        @Override
        protected void onPostExecute(List<Group> groups) {
            super.onPostExecute(groups);

            List<GuidedAction> actions = new ArrayList<>();

            groups.forEach(group -> actions.add(getAction(group.getTitle(), null, group.getIconResId(), group.getId(), group.isEnabled())));

            actions.add(getAction(getString(R.string.text_recently_added), null, R.drawable.ic_sort_by_created_24, Math.toIntExact(Putio.Files.PARENT_ID_RECENT), false));
            setActions(actions);
        }
    }

}
