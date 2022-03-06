package io.smileyjoe.putio.tv.ui.viewholder;

import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.ListItemVideoBinding;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.util.ViewUtil;
import io.smileyjoe.putio.tv.util.ZoomView;

public class VideosListViewHolder extends BaseVideosViewHolder<ListItemVideoBinding> {

    private final float mMultiplierFocused = 1.50f;
    private final float mTextSize;
    private final float mTextSizeFocused;
    private final int mPadding;
    private final int mPaddingFocused;
    private ZoomView mZoomView;

    public VideosListViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);

        mTextSize = getContext().getResources().getDimension(R.dimen.text_default);
        mTextSizeFocused = getContext().getResources().getDimension(R.dimen.text_list_image_focused);
        mPaddingFocused = (int) getContext().getResources().getDimension(R.dimen.padding_list_focus);
        mPadding = (int) getContext().getResources().getDimension(R.dimen.padding_general);

        mZoomView = new ZoomView();
        mZoomView.setIncludeHeight(false);
    }

    @Override
    protected ListItemVideoBinding inflate(View itemView) {
        return ListItemVideoBinding.bind(itemView);
    }

    @Override
    public void bindView(Video video, int position) {
        super.bindView(video, position);

        mView.textTitle.setText(video.getTitleFormatted(getContext(), video.getVideoType() == VideoType.SEASON));
        populateSummary(video, mView.textSummary);
        populatePoster(video, mView.imagePoster);
        ViewUtil.populateResumeTime(mView.textResumeTime, video);

        if (video.isWatched()) {
            mView.imageWatched.setVisibility(View.VISIBLE);
        } else {
            mView.imageWatched.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int padding;
        float textSize;

        if (hasFocus) {
            padding = mPaddingFocused;
            textSize = mTextSizeFocused;
            mZoomView.zoom(mView.imagePoster, mMultiplierFocused);
        } else {
            padding = mPadding;
            textSize = mTextSize;
            mZoomView.reset(mView.imagePoster);
        }

        mView.textTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mView.getRoot().setPadding(padding, padding, padding, padding);

        super.onFocusChange(v, hasFocus);
    }
}
