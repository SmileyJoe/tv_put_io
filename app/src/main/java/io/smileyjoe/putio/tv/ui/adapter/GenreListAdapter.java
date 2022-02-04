package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.View;

import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.ui.viewholder.GenreListViewHolder;

public class GenreListAdapter extends BaseListAdapter<Genre, GenreListViewHolder> {

    public GenreListAdapter(Context context) {
        super(context);
        shouldMarkSelected(true);
        shouldAllowDeselect(true);
    }

    @Override
    protected int getLayoutResId() {
        return GenreListViewHolder.VIEW;
    }

    @Override
    protected GenreListViewHolder getViewHolder(View view, FragmentType fragmentType) {
        return new GenreListViewHolder(view, fragmentType);
    }
}
