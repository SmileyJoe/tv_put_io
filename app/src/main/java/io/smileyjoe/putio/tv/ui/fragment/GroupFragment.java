package io.smileyjoe.putio.tv.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.Filter;
import io.smileyjoe.putio.tv.object.Group;

public class GroupFragment extends FilterFragment<Group> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GetGroups task = new GetGroups();
        task.execute();
    }

    private class GetGroups extends AsyncTask<Void, Void, List<Group>>{
        @Override
        protected List<Group> doInBackground(Void... voids) {
            return AppDatabase.getInstance(getContext()).groupDao().getAll();
        }

        @Override
        protected void onPostExecute(List<Group> groups) {
            super.onPostExecute(groups);

            for(Group group:groups){
                addOption(group);
            }
        }
    }
}
