package io.smileyjoe.putio.tv.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

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
}
