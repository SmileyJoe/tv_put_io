package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.stream.Stream;

import io.smileyjoe.putio.tv.object.FolderAction;
import io.smileyjoe.putio.tv.object.FragmentType;

public class FolderActionFragment extends ToggleFragment<FolderAction> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setType(FragmentType.FOLDER_ACTION);

        Stream.of(FolderAction.values())
                .forEach(action -> addOption(action));
    }

    @Override
    protected FragmentType getFragmentType() {
        return FragmentType.FOLDER_ACTION;
    }
}
