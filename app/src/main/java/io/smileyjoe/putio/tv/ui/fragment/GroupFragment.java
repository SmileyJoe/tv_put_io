package io.smileyjoe.putio.tv.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.GroupType;
import io.smileyjoe.putio.tv.util.FragmentUtil;

public class GroupFragment extends ToggleFragment<Group> {

    public interface LoadedListener{
        void loaded();
    }

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

    public void reload(LoadedListener listener){
        clear();
        GetGroups task = new GetGroups(listener);
        task.execute();
    }

    @Override
    protected FragmentType getFragmentType() {
        return FragmentType.GROUP;
    }

    public void setCurrentPutId(long currentPutId) {
        if(hasItems()) {
            IntStream.range(0, mGroups.size())
                    .forEach(i -> mViews.get(i).setSelected(mGroups.get(i).getPutIds().contains(currentPutId)));
        }
    }

    @Override
    public boolean show() {
        if(hasItems()){
            return super.show();
        } else {
            return false;
        }
    }

    private class GetGroups extends AsyncTask<Void, Void, List<Group>> {

        private Optional<LoadedListener> mLoaded;

        public GetGroups() {
            this(null);
        }

        public GetGroups(LoadedListener loaded) {
            mLoaded = Optional.ofNullable(loaded);
        }

        @Override
        protected List<Group> doInBackground(Void... voids) {
            return AppDatabase.getInstance(getContext()).groupDao().getByType(GroupType.DIRECTORY.getId());
        }

        @Override
        protected void onPostExecute(List<Group> groups) {
            super.onPostExecute(groups);

            groups.stream()
                    .filter(Group::isEnabled)
                    .forEach(group -> {
                        mGroups.add(group);
                        mViews.add(addOption(group));
                    });

            if(!hasItems()){
                hide();
            }

            mLoaded.ifPresent(l -> l.loaded());
        }
    }
}
