package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.ui.viewholder.BaseViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.GenreListViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideoGridViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideoListViewHolder;

public class GenreListAdapter extends BaseListAdapter<Genre, GenreListViewHolder> {

    public GenreListAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public GenreListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_genre, parent, false);
        GenreListViewHolder holder = new GenreListViewHolder(view, getFragmentType());
        holder.setListener(getListener());
        return holder;
    }
}