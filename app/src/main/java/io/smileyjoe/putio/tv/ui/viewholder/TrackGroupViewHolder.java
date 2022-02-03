package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.TracksInfo;
import com.google.android.exoplayer2.source.TrackGroup;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.ListItemTrackBinding;
import io.smileyjoe.putio.tv.object.FragmentType;

public class TrackGroupViewHolder extends BaseViewHolder<TracksInfo.TrackGroupInfo, ListItemTrackBinding> {

    @LayoutRes
    public static final int VIEW = R.layout.list_item_track;

    public TrackGroupViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);
    }

    @Override
    protected ListItemTrackBinding inflate(View itemView) {
        return ListItemTrackBinding.bind(itemView);
    }

    @Override
    public void bindView(TracksInfo.TrackGroupInfo item, int position) {
        super.bindView(item, position);

        TrackGroup group = item.getTrackGroup();
        Format trackFormat = group.getFormat(0);

        mView.textTitle.setText(trackFormat.label);
        setText(mView.textDetails, trackFormat.sampleMimeType);
    }
}
