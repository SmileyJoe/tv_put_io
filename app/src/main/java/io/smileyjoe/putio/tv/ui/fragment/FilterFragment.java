package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.smileyjoe.putio.tv.object.Filter;
import io.smileyjoe.putio.tv.object.FragmentType;

public class FilterFragment extends ToggleFragment<Filter> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for(Filter filter:Filter.values()){
            addOption(filter);
        }
    }

    @Override
    protected FragmentType getFragmentType() {
        return FragmentType.FILTER;
    }

    @Override
    protected void onItemClick(View view, Filter item) {
        for(View optionView:getOptionViews()){
            int tag = (Integer) optionView.getTag();

            if(tag != item.getId()) {
                Filter filter = Filter.getById(tag);

                if(filter != null && filter.isSort()){
                    optionView.setSelected(false);
                }
            }
        }

        super.onItemClick(view, item);
    }
}
