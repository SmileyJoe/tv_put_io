package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.IntStream;

import io.smileyjoe.putio.tv.databinding.FragmentFilterBinding;
import io.smileyjoe.putio.tv.databinding.ItemFilterBinding;
import io.smileyjoe.putio.tv.interfaces.HomeFragmentListener;
import io.smileyjoe.putio.tv.interfaces.ToggleItem;
import io.smileyjoe.putio.tv.object.FragmentType;

public abstract class ToggleFragment<T extends ToggleItem> extends BaseFragment<FragmentFilterBinding> {

    public interface Listener<T> extends HomeFragmentListener<T> {
        void onItemClicked(View view, T filter, boolean isSelected);
    }

    private Optional<Listener<T>> mListener = Optional.empty();
    private ArrayList<View> mOptionViews = new ArrayList<>();
    private ArrayList<T> mOptions = new ArrayList<>();

    protected abstract FragmentType getFragmentType();

    @Override
    protected FragmentFilterBinding inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState) {
        return FragmentFilterBinding.inflate(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setListener(Listener listener) {
        mListener = Optional.ofNullable(listener);
    }

    protected View addOption(T filter) {
        mOptions.add(filter);
        ItemFilterBinding binding = ItemFilterBinding.inflate(LayoutInflater.from(getContext()));
        ViewGroup root = binding.getRoot();

        OnFilterListener listener = new OnFilterListener(filter);

        binding.imageIcon.setImageResource(filter.getIconResId());

        root.setSelected(filter.isSelected());
        root.setOnClickListener(listener);
        root.setOnFocusChangeListener(listener);
        root.setTag(filter.getId());
        mOptionViews.add(root);
        mView.getRoot().addView(root);
        return root;
    }

    public ArrayList<T> getOptions() {
        return mOptions;
    }

    public void reset() {
        IntStream.range(0, mView.getRoot().getChildCount())
                .forEach(i -> mView.getRoot().getChildAt(i).setSelected(false));
    }

    protected void onItemClick(View view, T item) {
        boolean newState = !view.isSelected();

        view.setSelected(newState);

        mListener.ifPresent(listener -> listener.onItemClicked(view, item, newState));
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
            mListener.ifPresent(listener -> listener.hasFocus(getFragmentType(), mFilter, v, 0));
        }

        @Override
        public void onClick(View v) {
            onItemClick(v, mFilter);
        }
    }
}
