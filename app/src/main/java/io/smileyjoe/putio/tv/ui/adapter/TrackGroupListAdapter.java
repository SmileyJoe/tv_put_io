package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.TracksInfo;
import com.google.android.exoplayer2.source.TrackGroup;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
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

        for(int i = 0; i < items.size(); i++){
            if(items.get(i).isSelected()){
                setSelectedPosition(i);
                break;
            }
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.list_item_audio_track;
    }

    @Override
    protected TrackGroupViewHolder getViewHolder(View view, FragmentType fragmentType) {
        return new TrackGroupViewHolder(view, fragmentType);
    }
}
