package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.ui.viewholder.FolderListViewHolder;

public class FolderListAdapter extends BaseListAdapter<Folder, FolderListViewHolder> {

    public FolderListAdapter(Context context) {
        super(context);
        setItems(new ArrayList<>());
    }

    @Override
    protected int getLayoutResId() {
        return FolderListViewHolder.VIEW;
    }

    @Override
    protected FolderListViewHolder getViewHolder(View view, FragmentType fragmentType) {
        return new FolderListViewHolder(view, fragmentType);
    }
}
