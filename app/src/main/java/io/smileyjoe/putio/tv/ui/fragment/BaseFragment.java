package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.leanback.widget.BrowseFrameLayout;
import androidx.viewbinding.ViewBinding;

import java.util.Optional;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.util.FragmentUtil;

public abstract class BaseFragment<T extends ViewBinding> extends Fragment implements BrowseFrameLayout.OnFocusSearchListener {

    public interface OnFocusSearchListener {
        View onFocusSearch(View focused, int direction, FragmentType type);
    }

    protected T mView;
    private Optional<OnFocusSearchListener> mFocusSearchListener = Optional.empty();
    private FragmentType mType;
    private BrowseFrameLayout mBrowseFrameLayout;
    private boolean mForceFocus = false;

    protected abstract T inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState);

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        mBrowseFrameLayout = (BrowseFrameLayout) inflater.inflate(R.layout.fragment_base, container);
        mView = inflate(inflater, mBrowseFrameLayout, false);
        mBrowseFrameLayout.addView(mView.getRoot());
        return mBrowseFrameLayout;
    }

    public void setForceFocus(boolean forceFocus) {
        mForceFocus = forceFocus;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && mForceFocus) {
            requestFocus();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mView = null;
    }

    public void setType(FragmentType fragmentType) {
        mType = fragmentType;
    }

    public FragmentType getType() {
        return mType;
    }

    public void requestFocus() {
        View focusable = getFocusableView();
        if (focusable != null) {
            focusable.requestFocus();
        }
    }

    public View getFocusableView() {
        return mView.getRoot();
    }

    protected BrowseFrameLayout getBrowserFrameLayout() {
        return mBrowseFrameLayout;
    }

    public void setFocusSearchListener(OnFocusSearchListener focusSearchListener) {
        if (getBrowserFrameLayout() != null) {
            mFocusSearchListener = Optional.ofNullable(focusSearchListener);
            getBrowserFrameLayout().setOnFocusSearchListener(this);
        }
    }

    public boolean show() {
        FragmentUtil.showFragment(getParentFragmentManager(), this);
        return true;
    }

    public void hide() {
        FragmentUtil.hideFragment(getParentFragmentManager(), this);
    }

    @Override
    public View onFocusSearch(View focused, int direction) {
        if (mFocusSearchListener.isPresent()) {
            return mFocusSearchListener.get().onFocusSearch(focused, direction, getType());
        } else {
            return null;
        }
    }
}
