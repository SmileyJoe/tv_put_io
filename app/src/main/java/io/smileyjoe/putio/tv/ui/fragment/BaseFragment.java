package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

public abstract class BaseFragment<T extends ViewBinding> extends Fragment {

    protected T mView;

    protected abstract T inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState);

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflate(inflater, container, false);
        return mView.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mView = null;
    }
}
