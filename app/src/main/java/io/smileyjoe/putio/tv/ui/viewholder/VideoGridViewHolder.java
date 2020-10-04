package io.smileyjoe.putio.tv.ui.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;

public class VideoGridViewHolder extends VideoBaseViewHolder {

    private TextView mTextTitle;
    private ImageView mImagePoster;
    private FrameLayout mFrameWatched;
    private FrameLayout mFrameSelected;
    private View mItemView;

    public VideoGridViewHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);

        mItemView = itemView;
        mTextTitle = itemView.findViewById(R.id.text_title);
        mImagePoster = itemView.findViewById(R.id.image_poster);
        mFrameWatched = itemView.findViewById(R.id.frame_watched);
        mFrameSelected = itemView.findViewById(R.id.frame_selected);
    }

    @Override
    public void bindView(Video video) {
        super.bindView(video);

        Context context = mItemView.getContext();
        mTextTitle.setText(video.getTitle());

        if (video.isWatched()) {
            mFrameWatched.setVisibility(View.VISIBLE);
        } else {
            mFrameWatched.setVisibility(View.GONE);
        }
        if (video.getPoster() != null) {
            Glide.with(context)
                    .load(video.getPoster())
                    .into(mImagePoster);
        }
    }
}
