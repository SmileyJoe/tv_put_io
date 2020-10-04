package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.smileyjoe.putio.tv.object.Video;

public abstract class VideoBaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public interface Listener{
        void onVideoClicked(Video video);
    }

    private Video mVideo;

    public VideoBaseViewHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);
    }

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void bindView(Video video){
        mVideo = video;
    }

    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onVideoClicked(mVideo);
        }
    }
}
