package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Subtitle;
import io.smileyjoe.putio.tv.ui.viewholder.BaseViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.SubtitleViewHolder;

public class SubtitleListAdapter extends BaseListAdapter<Subtitle, SubtitleViewHolder> {

    public SubtitleListAdapter(Context context) {
        super(context);
        setItems(new ArrayList<>());
        setSelectedPosition(0);
        shouldMarkSelected(true);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.list_item_subtitle;
    }

    @Override
    protected SubtitleViewHolder getViewHolder(View view, FragmentType fragmentType) {
        return new SubtitleViewHolder(view, fragmentType);
    }
}
