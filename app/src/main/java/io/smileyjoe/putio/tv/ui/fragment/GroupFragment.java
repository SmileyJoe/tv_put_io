package io.smileyjoe.putio.tv.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.GroupType;

public class GroupFragment extends ToggleFragment<Group> {

    private ArrayList<Group> mGroups;
    private ArrayList<View> mViews;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGroups = new ArrayList<>();
        mViews = new ArrayList<>();

        GetGroups task = new GetGroups();
        task.execute();
    }

    public void setCurrentPutId(long currentPutId) {
        for(int i = 0; i < mGroups.size(); i++){
            if(mGroups.get(i).getPutIds().contains(currentPutId)){
                mViews.get(i).setSelected(true);
            } else {
                mViews.get(i).setSelected(false);
            }
        }
    }

    private class GetGroups extends AsyncTask<Void, Void, List<Group>>{
        @Override
        protected List<Group> doInBackground(Void... voids) {
            return AppDatabase.getInstance(getContext()).groupDao().getByType(GroupType.DIRECTORY.getId());
        }

        @Override
        protected void onPostExecute(List<Group> groups) {
            super.onPostExecute(groups);

            mGroups.addAll(groups);

            for(Group group:groups){
                mViews.add(addOption(group));
            }
        }
    }
}
