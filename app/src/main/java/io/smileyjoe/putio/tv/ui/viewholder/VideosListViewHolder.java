package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

import io.smileyjoe.putio.tv.databinding.ListItemVideoBinding;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.ViewUtil;

public class VideosListViewHolder extends BaseVideosViewHolder<ListItemVideoBinding> {

    public VideosListViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);
    }

    @Override
    protected ListItemVideoBinding inflate(View itemView) {
        return ListItemVideoBinding.bind(itemView);
    }

    @Override
    public void bindView(Video video, int position) {
        super.bindView(video, position);

        populateTitle(video, mView.textTitle);
        populateSummary(video, mView.textSummary);
        populatePoster(video, mView.imagePoster);
        ViewUtil.populateResumeTime(mView.textResumeTime, video);

        if(video.isWatched()){
            mView.imageWatched.setVisibility(View.VISIBLE);
        } else {
            mView.imageWatched.setVisibility(View.INVISIBLE);
        }
    }
}
