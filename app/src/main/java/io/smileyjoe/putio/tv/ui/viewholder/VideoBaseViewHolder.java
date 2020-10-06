package io.smileyjoe.putio.tv.ui.viewholder;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;

public abstract class VideoBaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {

    public interface Listener{
        void onVideoClicked(Video video);
        void hasFocus(FragmentType fragmentType, Video video, int position);
    }

    private Video mVideo;
    private FragmentType mFragmentType;
    private int mPosition;

    public VideoBaseViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView);

        mFragmentType = fragmentType;
        itemView.setOnClickListener(this);
        itemView.setOnFocusChangeListener(this);
    }

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void bindView(Video video, int position){
        mVideo = video;
        mPosition = position;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus && mListener != null){
            mListener.hasFocus(mFragmentType, mVideo, mPosition);
        }
    }

    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onVideoClicked(mVideo);
        }
    }
}
