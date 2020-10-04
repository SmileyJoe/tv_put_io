package io.smileyjoe.putio.tv.ui.viewholder;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;

public abstract class VideoBaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {

    public interface Listener{
        void onVideoClicked(Video video);
        void hasFocus(VideoType videoType);
    }

    private Video mVideo;
    private VideoType mVideoType;

    public VideoBaseViewHolder(@NonNull View itemView, VideoType videoType) {
        super(itemView);

        mVideoType = videoType;
        itemView.setOnClickListener(this);
        itemView.setOnFocusChangeListener(this);
    }

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void bindView(Video video){
        mVideo = video;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus && mListener != null){
            mListener.hasFocus(mVideoType);
        }
    }

    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onVideoClicked(mVideo);
        }
    }
}
