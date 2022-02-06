package io.smileyjoe.putio.tv.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.GroupType;

public class GroupFragment extends ToggleFragment<Group> {

    private ArrayList<Group> mGroups;
    private ArrayList<View> mViews;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setType(FragmentType.GROUP);

        mGroups = new ArrayList<>();
        mViews = new ArrayList<>();

        GetGroups task = new GetGroups();
        task.execute();
    }

    @Override
    protected FragmentType getFragmentType() {
        return FragmentType.GROUP;
    }

    public void setCurrentPutId(long currentPutId) {
        IntStream.range(0, mGroups.size())
                .forEach(i -> mViews.get(i).setSelected(mGroups.get(i).getPutIds().contains(currentPutId)));
    }

    public View getFocusableView(){
        return mView.getRoot();
    }

    private class GetGroups extends AsyncTask<Void, Void, List<Group>> {
        @Override
        protected List<Group> doInBackground(Void... voids) {
            return AppDatabase.getInstance(getContext()).groupDao().getByType(GroupType.DIRECTORY.getId());
        }

        @Override
        protected void onPostExecute(List<Group> groups) {
            super.onPostExecute(groups);

            mGroups.addAll(groups);

            groups.forEach(group -> mViews.add(addOption(group)));
        }
    }
}
