package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.TracksInfo;
import com.google.android.exoplayer2.source.TrackGroup;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;

public class TrackGroupViewHolder extends BaseViewHolder<TracksInfo.TrackGroupInfo> {

    private TextView mTextTitle;
    private TextView mTextDetails;

    public TrackGroupViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);

        mTextTitle = itemView.findViewById(R.id.text_title);
        mTextDetails = itemView.findViewById(R.id.text_details);
    }

    @Override
    public void bindView(TracksInfo.TrackGroupInfo item, int position) {
        super.bindView(item, position);

        TrackGroup group = item.getTrackGroup();
        Format trackFormat = group.getFormat(0);

        mTextTitle.setText(trackFormat.label);
        setText(mTextDetails, trackFormat.sampleMimeType);
    }
}
