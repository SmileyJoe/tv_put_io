package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;

public class VideoListViewHolderTwo extends VideoBaseViewHolder {

    private TextView mTextTitle;
    private ImageView mImageIcon;

    public VideoListViewHolderTwo(@NonNull View itemView) {
        super(itemView);

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
