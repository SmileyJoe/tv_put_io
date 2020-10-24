package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.smileyjoe.putio.tv.object.Filter;

public class FilterTwoFragment extends FilterFragment<Filter> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for(Filter filter:Filter.values()){
            addOption(filter);
        }
    }
}
