package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;

public class VideoDetailsViewHolder extends Presenter.ViewHolder {

    private TextView mTextTitle;

    public VideoDetailsViewHolder(View view) {
        super(view);

        mTextTitle = view.findViewById(R.id.text_title);
    }

    public void bind(Video video){
        mTextTitle.setText(video.getTitle());
    }
}
