package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;

public class FolderListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public interface Listener{
        void onFolderClicked(Video video);
    }

    private TextView mTextTitle;
    private ImageView mImageIcon;
    private Video mVideo;
    private Listener mListener;

    public FolderListViewHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);

        mTextTitle = itemView.findViewById(R.id.text_title);
        mImageIcon = itemView.findViewById(R.id.image_icon);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void bindView(Video video){
        mVideo = video;

        mTextTitle.setText(video.getTitle());
        mImageIcon.setImageResource(R.drawable.ic_folder_24);
    }

    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onFolderClicked(mVideo);
        }
    }
}