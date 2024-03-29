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

    @Override
    public View getFocusableView() {
        if (isVisible() && hasItems()) {
            return mView.getRoot().getChildAt(0);
        } else {
            return null;
        }
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

    protected void clear() {
        mOptions = new ArrayList<>();
        mOptionViews = new ArrayList<>();
        mView.getRoot().removeAllViews();
    }

    public boolean canFocus(View view, int direction) {
        int currentPosition = mView.getRoot().indexOfChild(view);
        int numberChildren = mView.getRoot().getChildCount() - 1;
        switch (direction) {
            case View.FOCUS_UP:
            case View.FOCUS_DOWN:
                return true;
            case View.FOCUS_LEFT:
                if (currentPosition - 1 < 0) {
                    return false;
                } else {
                    return true;
                }
            case View.FOCUS_RIGHT:
                if (currentPosition + 1 > numberChildren) {
                    return false;
                } else {
                    return true;
                }
            default:
                return false;
        }
    }

    public ArrayList<T> getOptions() {
        return mOptions;
    }

    public void reset() {
        if (hasItems()) {
            IntStream.range(0, mView.getRoot().getChildCount())
                    .forEach(i -> mView.getRoot().getChildAt(i).setSelected(false));
        }
    }

    public void select(T item) {
        getOptionViews()
                .stream()
                .filter(v -> ((Integer) v.getTag()) == item.getId())
                .findFirst()
                .ifPresent(v -> onItemClick(v, item));
    }

    public boolean hasItems() {
        return mOptions != null && !mOptions.isEmpty();
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
