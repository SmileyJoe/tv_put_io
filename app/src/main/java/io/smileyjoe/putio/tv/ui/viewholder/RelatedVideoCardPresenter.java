package io.smileyjoe.putio.tv.ui.viewholder;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class RelatedVideoCardPresenter extends Presenter {
    private static final String TAG = "CardPresenter";

    private static final int CARD_WIDTH = 313;
    private static final int CARD_HEIGHT = 176;
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;
    private static int sDefaultTextColor;
    private static int sSelectedTextColor;
    private Drawable mDefaultCardImage;

    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    private static void updateTextColor(ImageCardView imageCardView, boolean selected) {
        int color = selected ? sSelectedTextColor : sDefaultTextColor;
        ViewGroup viewInfo = imageCardView.findViewById(R.id.info_field);

        for (int i = 0; i < viewInfo.getChildCount(); i++) {
            View view = viewInfo.getChildAt(i);

            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        sDefaultBackgroundColor = ContextCompat.getColor(parent.getContext(), R.color.bg_default);
        sSelectedBackgroundColor = ContextCompat.getColor(parent.getContext(), R.color.bg_selected);
        sSelectedTextColor = ContextCompat.getColor(parent.getContext(), R.color.text_selected);
        sDefaultTextColor = ContextCompat.getColor(parent.getContext(), R.color.text);

        ImageCardView cardView =
                new ImageCardView(parent.getContext()) {
                    @Override
                    public void setSelected(boolean selected) {
                        updateCardBackgroundColor(this, selected);
                        updateTextColor(this, selected);
                        super.setSelected(selected);
                    }
                };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);
        updateTextColor(cardView, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Video video = (Video) item;
        ImageCardView cardView = (ImageCardView) viewHolder.view;

        if (video.getPosterAsUri() != null) {
            cardView.setTitleText(video.getTitle());
            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
            Glide.with(viewHolder.view.getContext())
                    .load(video.getPosterAsUri())
                    .centerCrop()
                    .error(mDefaultCardImage)
                    .into(cardView.getMainImageView());
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
