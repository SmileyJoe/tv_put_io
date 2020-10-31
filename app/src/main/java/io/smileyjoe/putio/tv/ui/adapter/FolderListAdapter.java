package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.ui.viewholder.BaseViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.FolderListViewHolder;

public class FolderListAdapter extends BaseListAdapter<Folder, BaseViewHolder<Folder>> {

    public FolderListAdapter(Context context) {
        super(context);
        setItems(new ArrayList<>());
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.list_item_folder;
    }

    @Override
    protected BaseViewHolder<Folder> getViewHolder(View view, FragmentType fragmentType) {
        return new FolderListViewHolder(view, fragmentType);
    }
}
