package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.stream.Stream;

import io.smileyjoe.putio.tv.object.Filter;
import io.smileyjoe.putio.tv.object.FragmentType;

public class FilterFragment extends ToggleFragment<Filter> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setType(FragmentType.FILTER);

        Stream.of(Filter.values())
                .forEach(filter -> addOption(filter));
    }

    @Override
    protected FragmentType getFragmentType() {
        return FragmentType.FILTER;
    }

    @Override
    protected void onItemClick(View view, Filter item) {
        if (item.getGroup().isUnique()) {
            getOptionViews()
                    .stream()
                    .filter(v -> ((Integer) v.getTag()) != item.getId())
                    .filter(v -> Filter.getById((Integer) v.getTag()).getGroup() == item.getGroup())
                    .forEach(optionView -> optionView.setSelected(false));
        }

        super.onItemClick(view, item);
    }
}
