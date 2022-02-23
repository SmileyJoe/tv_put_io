package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

import io.smileyjoe.putio.tv.databinding.GridItemVideoBinding;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;

public class VideosGridViewHolder extends BaseVideosViewHolder<GridItemVideoBinding> {

    public VideosGridViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);
    }

    @Override
    protected GridItemVideoBinding inflate(View itemView) {
        return GridItemVideoBinding.bind(itemView);
    }

    @Override
    public void bindView(Video video, int position) {
        super.bindView(video, position);

        mView.textTitle.setText(video.getTitleFormatted(getContext(), true));
        populateSummary(video, mView.textSummary);
        populatePoster(video, mView.imagePoster);

        if (video.isWatched()) {
            mView.frameWatched.setVisibility(View.VISIBLE);
        } else {
            mView.frameWatched.setVisibility(View.GONE);
        }
    }
}
