package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.leanback.widget.Presenter;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;

public class VideoDetailsDescriptionPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return VideoDetailsViewHolder.getInstance(parent.getContext(), parent, false);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Video video = (Video) item;
        VideoDetailsViewHolder holder = (VideoDetailsViewHolder) viewHolder;
        holder.bind(video);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
