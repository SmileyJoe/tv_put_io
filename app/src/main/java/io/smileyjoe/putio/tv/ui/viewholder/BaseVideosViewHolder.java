package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;

public abstract class BaseVideosViewHolder<V extends ViewBinding> extends BaseViewHolder<Video, V> {

    private int mPosterPadding;

    public BaseVideosViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);
        mPosterPadding = itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.file_grid_poster_padding);
    }

    protected void populateTitle(Video video, TextView view) {
        String title = video.getTitleFormatted();
        if (video.getVideoType() == VideoType.SEASON) {
            title = title + ": " + getContext().getString(R.string.text_season) + " " + video.getSeason();
        }
        view.setText(title);
    }

    protected void populateSummary(Video video, TextView view) {
        view.setText(video.getOverView());
    }

    protected void populatePoster(Video video, ImageView view) {
        if (video.getPosterAsUri() != null) {
            view.setPadding(0, 0, 0, 0);
            Glide.with(getContext())
                    .load(video.getPosterAsUri())
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view);
        } else {
            view.setPadding(mPosterPadding, mPosterPadding, mPosterPadding, mPosterPadding);
            view.setImageResource(R.drawable.ic_movie_24);
        }
    }
}
