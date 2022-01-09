package io.smileyjoe.putio.tv.ui.viewholder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.util.ViewUtil;

public class VideosViewHolder extends BaseViewHolder<Video> {

    public enum Style{
        GRID(R.layout.grid_item_video),
        LIST(R.layout.list_item_video);

        private @LayoutRes
        int mLayoutResId;

        Style(int layoutResId) {
            mLayoutResId = layoutResId;
        }

        public @LayoutRes int getLayoutResId() {
            return mLayoutResId;
        }
    }

    private TextView mTextTitle;
    private TextView mTextSummary;
    private TextView mTextResumeTime;
    private ImageView mImagePoster;
    private ImageView mImageWatched;
    private FrameLayout mFrameWatched;
    private View mItemView;
    private int mPosterPadding;
    private Style mStyle;

    public VideosViewHolder(@NonNull View itemView, FragmentType fragmentType, Style style) {
        super(itemView, fragmentType);

        mStyle = style;
        mItemView = itemView;
        mTextTitle = itemView.findViewById(R.id.text_title);
        mTextSummary = itemView.findViewById(R.id.text_summary);
        mTextResumeTime = itemView.findViewById(R.id.text_resume_time);
        mImagePoster = itemView.findViewById(R.id.image_poster);
        mFrameWatched = itemView.findViewById(R.id.frame_watched);
        mImageWatched = itemView.findViewById(R.id.image_watched);

        mPosterPadding = itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.file_grid_poster_padding);
    }

    @Override
    public void bindView(Video video, int position) {
        super.bindView(video, position);

        Context context = mItemView.getContext();

        String title = video.getTitleFormatted();
        if(video.getVideoType() == VideoType.SEASON){
            title = title + ": " + context.getString(R.string.text_season) + " " + video.getSeason();
        }
        mTextTitle.setText(title);

        mTextSummary.setText(video.getOverView());

        if(mStyle == Style.LIST) {
            ViewUtil.populateResumeTime(mTextResumeTime, video);
        }

        if(mFrameWatched != null) {
            if (video.isWatched()) {
                mFrameWatched.setVisibility(View.VISIBLE);
            } else {
                mFrameWatched.setVisibility(View.GONE);
            }
        } else if(mImageWatched != null){
            if(video.isWatched()){
                mImageWatched.setVisibility(View.VISIBLE);
            } else {
                mImageWatched.setVisibility(View.INVISIBLE);
            }
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
