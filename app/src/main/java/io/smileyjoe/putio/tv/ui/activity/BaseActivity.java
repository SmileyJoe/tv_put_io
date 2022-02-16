package io.smileyjoe.putio.tv.ui.activity;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import io.smileyjoe.putio.tv.util.FragmentUtil;

public abstract class BaseActivity<T extends ViewBinding> extends FragmentActivity {

    protected T mView;

    protected abstract T inflate();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = inflate();
        setContentView(mView.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mView = null;
    }

    protected void show(Fragment... fragments) {
        FragmentUtil.showFragment(getSupportFragmentManager(), fragments);
    }

    protected void hide(Fragment... fragments) {
        FragmentUtil.hideFragment(getSupportFragmentManager(), fragments);
    }

    protected Fragment getFragment(@IdRes int fragmentId) {
        return getSupportFragmentManager().findFragmentById(fragmentId);
    }
}
