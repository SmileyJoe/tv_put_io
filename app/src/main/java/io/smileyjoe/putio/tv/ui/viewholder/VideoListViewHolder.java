package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;

public class VideoListViewHolder extends BaseViewHolder<Video> {

    private TextView mTextTitle;
    private TextView mTextDetails;
    private ImageView mImageIcon;

    public VideoListViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);

        mTextTitle = itemView.findViewById(R.id.text_title);
        mImageIcon = itemView.findViewById(R.id.image_icon);
        mTextDetails = itemView.findViewById(R.id.text_details);
    }

    @Override
    public void bindView(Video video, int position) {
        super.bindView(video, position);
        mTextTitle.setText(video.getTitle());
        mTextDetails.setText(video.getSizeFormatted(mTextDetails.getContext()));
        mImageIcon.setImageResource(R.drawable.ic_folder_24);
    }
}
