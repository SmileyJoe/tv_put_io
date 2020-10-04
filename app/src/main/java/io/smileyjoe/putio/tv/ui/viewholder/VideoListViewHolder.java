package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;

public class VideoListViewHolder extends VideoBaseViewHolder {

    private TextView mTextTitle;
    private ImageView mImageIcon;

    public VideoListViewHolder(@NonNull View itemView, VideoType videoType) {
        super(itemView, videoType);

        mTextTitle = itemView.findViewById(R.id.text_title);
        mImageIcon = itemView.findViewById(R.id.image_icon);
    }

    @Override
    public void bindView(Video video) {
        super.bindView(video);
        mTextTitle.setText(video.getTitle());
        mImageIcon.setImageResource(R.drawable.ic_folder_24);
    }
}
