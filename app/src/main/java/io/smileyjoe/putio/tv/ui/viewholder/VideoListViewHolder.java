package io.smileyjoe.putio.tv.ui.viewholder;

import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;

public class VideoListViewHolder extends BaseViewHolder<Video> {

    private TextView mTextTitle;
    private TextView mTextSize;
    private TextView mTextUpdatedAt;
    private ImageView mImageIcon;

    public VideoListViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);

        mTextTitle = itemView.findViewById(R.id.text_title);
        mImageIcon = itemView.findViewById(R.id.image_icon);
        mTextSize = itemView.findViewById(R.id.text_size);
        mTextUpdatedAt = itemView.findViewById(R.id.text_updated_at);
    }

    @Override
    public void bindView(Video video, int position) {
        super.bindView(video, position);
        mTextTitle.setText(video.getTitle());
        mTextSize.setText(video.getSizeFormatted(mTextSize.getContext()));
        mImageIcon.setImageResource(R.drawable.ic_folder_24);
        mTextUpdatedAt.setText(video.getUpdatedAgo(mTextUpdatedAt.getContext()));
    }
}
