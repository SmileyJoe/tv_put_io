package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.View;

import com.google.android.exoplayer2.TracksInfo;

import java.util.ArrayList;
import java.util.stream.IntStream;

import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.ui.viewholder.TrackGroupViewHolder;

public class TrackGroupListAdapter extends BaseListAdapter<TracksInfo.TrackGroupInfo, TrackGroupViewHolder> {

    public TrackGroupListAdapter(Context context) {
        super(context);
        setItems(new ArrayList<>());
        setSelectedPosition(0);
        shouldMarkSelected(true);
    }

    @Override
    public void setItems(ArrayList<TracksInfo.TrackGroupInfo> items) {
        super.setItems(items);

        IntStream.range(0, items.size())
                .filter(i -> items.get(i).isSelected())
                .forEach(this::setSelectedPosition);
    }

    @Override
    protected int getLayoutResId() {
        return TrackGroupViewHolder.VIEW;
    }

    @Override
    protected TrackGroupViewHolder getViewHolder(View view, FragmentType fragmentType) {
        return new TrackGroupViewHolder(view, fragmentType);
    }
}
