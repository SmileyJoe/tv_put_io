package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.stream.IntStream;

import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.GroupType;
import io.smileyjoe.putio.tv.util.Async;

public class GroupFragment extends ToggleFragment<Group> {

    public interface LoadedListener {
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

        getGroups(null);
    }

    public void reload(LoadedListener listener) {
        clear();
        getGroups(listener);
    }

    private void getGroups(LoadedListener listener) {
        Async.run(() -> AppDatabase.getInstance(getContext()).groupDao().getByType(GroupType.DIRECTORY.getId()), groups -> {
            groups.stream()
                    .filter(Group::isEnabled)
                    .forEach(group -> {
                        mGroups.add(group);
                        mViews.add(addOption(group));
                    });

            if (!hasItems()) {
                hide();
            }

            if (listener != null) {
                listener.loaded();
            }
        });
    }

    @Override
    protected FragmentType getFragmentType() {
        return FragmentType.GROUP;
    }

    public void setCurrentPutId(long currentPutId) {
        if (hasItems()) {
            IntStream.range(0, mGroups.size())
                    .forEach(i -> mViews.get(i).setSelected(mGroups.get(i).getPutIds().contains(currentPutId)));
        }
    }

    @Override
    public boolean show() {
        if (hasItems()) {
            return super.show();
        } else {
            return false;
        }
    }
}
