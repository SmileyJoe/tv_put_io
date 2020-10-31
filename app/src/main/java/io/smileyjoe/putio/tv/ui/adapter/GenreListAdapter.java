package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.ui.viewholder.GenreListViewHolder;

public class GenreListAdapter extends BaseListAdapter<Genre, GenreListViewHolder> {

    private View mSelectedView;

    public GenreListAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public GenreListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(getLayoutResId(), parent, false);
        GenreListViewHolder holder = getViewHolder(view, getFragmentType());
        holder.setListener(new ViewHolderListener());
        return holder;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.list_item_genre;
    }

    @Override
    protected GenreListViewHolder getViewHolder(View view, FragmentType fragmentType) {
        return new GenreListViewHolder(view, fragmentType);
    }

    public void clearSelected(){
        if(mSelectedView != null){
            mSelectedView.setSelected(false);
        }
    }

    private class ViewHolderListener implements Listener<Genre>{
        @Override
        public void onItemClicked(View view, Genre item) {
            if(mSelectedView != view) {
                if(mSelectedView != null){
                    mSelectedView.setSelected(false);
                }

                mSelectedView = view;
                mSelectedView.setSelected(true);
            } else {
                mSelectedView.setSelected(!mSelectedView.isSelected());
            }

            if(getListener() != null){
                getListener().onItemClicked(view, item);
            }
        }

        @Override
        public void hasFocus(FragmentType type, Genre item, View view, int position) {
            if(getListener() != null){
                getListener().hasFocus(type, item, view, position);
            }
        }
    }
}
