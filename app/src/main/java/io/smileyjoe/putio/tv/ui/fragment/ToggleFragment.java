package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.interfaces.ToggleItem;
import io.smileyjoe.putio.tv.interfaces.HomeFragmentListener;
import io.smileyjoe.putio.tv.object.FragmentType;

public abstract class ToggleFragment<T extends ToggleItem> extends Fragment {

    public interface Listener<T> extends HomeFragmentListener<T>{
        void onItemClicked(View view, T filter, boolean isSelected);
    }

    private LinearLayout mLayoutRoot;
    private Listener<T> mListener;
    private ArrayList<View> mOptionViews = new ArrayList<>();
    private ArrayList<T> mOptions = new ArrayList<>();

    protected abstract FragmentType getFragmentType();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayoutRoot = (LinearLayout) inflater.inflate(R.layout.fragment_filter, null);

        return mLayoutRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    protected View addOption(T filter){
        mOptions.add(filter);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_filter, null);
        OnFilterListener listener = new OnFilterListener(filter);

        ImageView imageIcon = view.findViewById(R.id.image_icon);

        imageIcon.setImageResource(filter.getIconResId());

        view.setSelected(filter.isSelected());
        view.setOnClickListener(listener);
        view.setOnFocusChangeListener(listener);
        view.setTag(filter.getId());
        mOptionViews.add(view);
        mLayoutRoot.addView(view);
        return view;
    }

    public ArrayList<T> getOptions() {
        return mOptions;
    }

    public void reset(){
        for(int i = 0; i < mLayoutRoot.getChildCount(); i++){
            mLayoutRoot.getChildAt(i).setSelected(false);
        }
    }

    protected void onItemClick(View view, T item){
        boolean newState = !view.isSelected();

        view.setSelected(newState);

        if(mListener != null){
            mListener.onItemClicked(view, item, newState);
        }
    }

    protected ArrayList<View> getOptionViews() {
        return mOptionViews;
    }

    private class OnFilterListener implements View.OnClickListener, View.OnFocusChangeListener {
        private T mFilter;

        public OnFilterListener(T filter) {
            mFilter = filter;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(mListener != null){
                mListener.hasFocus(getFragmentType(), mFilter, v, 0);
            }
        }

        @Override
        public void onClick(View v) {
            onItemClick(v, mFilter);
        }
    }
}
