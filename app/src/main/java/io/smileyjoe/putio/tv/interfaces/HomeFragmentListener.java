package io.smileyjoe.putio.tv.interfaces;

import io.smileyjoe.putio.tv.object.FragmentType;

public interface HomeFragmentListener<T> {

    void hasFocus(FragmentType type, T item, int position);

}
