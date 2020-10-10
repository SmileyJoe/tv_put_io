package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Filter;

public class FilterFragment extends Fragment {

    private LinearLayout mLayoutRoot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayoutRoot = (LinearLayout) inflater.inflate(R.layout.fragment_filter, null);

        return mLayoutRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for(Filter filter:Filter.values()){
            addOption(filter);
        }
    }

    private void addOption(Filter filter){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_filter, null);

        ImageView imageIcon = view.findViewById(R.id.image_icon);

        imageIcon.setImageResource(filter.getIconResId());

        view.setSelected(filter.isDefaultSelected());
        view.setOnClickListener(new OnFilterClicked(filter));
        mLayoutRoot.addView(view);
    }

    private class OnFilterClicked implements View.OnClickListener{
        private Filter mFilter;

        public OnFilterClicked(Filter filter) {
            mFilter = filter;
        }

        @Override
        public void onClick(View v) {
            boolean newState = !v.isSelected();

            v.setSelected(newState);
        }
    }
}
