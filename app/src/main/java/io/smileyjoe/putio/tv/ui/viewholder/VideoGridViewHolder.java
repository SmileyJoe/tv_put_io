package io.smileyjoe.putio.tv.ui.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;

public class VideoGridViewHolder extends BaseViewHolder<Video> {

    private TextView mTextTitle;
    private ImageView mImagePoster;
    private FrameLayout mFrameWatched;
    private FrameLayout mFrameSelected;
    private View mItemView;
    private int mPosterPadding;

    public VideoGridViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);

        mItemView = itemView;
        mTextTitle = itemView.findViewById(R.id.text_title);
        mImagePoster = itemView.findViewById(R.id.image_poster);
        mFrameWatched = itemView.findViewById(R.id.frame_watched);
        mFrameSelected = itemView.findViewById(R.id.frame_selected);

        mPosterPadding = itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.file_grid_poster_padding);
    }

    @Override
    public void bindView(Video video, int position) {
        super.bindView(video, position);

        Context context = mItemView.getContext();
        mTextTitle.setText(video.getTitle());

        if (video.isWatched()) {
            mFrameWatched.setVisibility(View.VISIBLE);
        } else {
            mFrameWatched.setVisibility(View.GONE);
        }
        if (video.getPosterAsUri() != null) {
            mImagePoster.setPadding(0,0,0,0);
            Glide.with(context)
                    .load(video.getPosterAsUri())
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImagePoster);
        } else {
            mImagePoster.setPadding(mPosterPadding,mPosterPadding,mPosterPadding,mPosterPadding);
            mImagePoster.setImageResource(R.drawable.ic_movie_24);
        }
    }
}
