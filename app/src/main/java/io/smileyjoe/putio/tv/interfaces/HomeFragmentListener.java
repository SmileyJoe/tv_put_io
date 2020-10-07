package io.smileyjoe.putio.tv.interfaces;

import android.view.View;

import io.smileyjoe.putio.tv.object.FragmentType;

public interface HomeFragmentListener<T> {

    void hasFocus(FragmentType type, T item, View view, int position);

}
