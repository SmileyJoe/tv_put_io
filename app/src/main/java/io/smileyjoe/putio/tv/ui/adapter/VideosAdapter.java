package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.viewholder.BaseVideosViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideosGridViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideosListViewHolder;

public class VideosAdapter extends BaseListAdapter<Video, BaseVideosViewHolder<? extends ViewBinding>> {

    public enum Style {
        GRID(0, R.string.text_grid, R.layout.grid_item_video, R.drawable.ic_grid_24),
        LIST(1, R.string.text_list, R.layout.list_item_video, R.drawable.ic_list_24);

        private int mId;
        @StringRes
        private int mTitle;
        @LayoutRes
        private int mLayoutResId;
        @DrawableRes
        private int mIcon;

        Style(int id, @StringRes int title, @LayoutRes int layoutResId, @DrawableRes int icon) {
            mId = id;
            mTitle = title;
            mLayoutResId = layoutResId;
            mIcon = icon;
        }

        public int getId() {
            return mId;
        }

        @StringRes
        public int getTitle(){
            return mTitle;
        }

        @LayoutRes
        public int getLayoutResId() {
            return mLayoutResId;
        }

        @DrawableRes
        public int getIcon() {
            return mIcon;
        }

        public BaseVideosViewHolder<? extends ViewBinding> getViewHolder(View view, FragmentType fragmentType) {
            switch (this) {
                case GRID:
                    return new VideosGridViewHolder(view, fragmentType);
                case LIST:
                default:
                    return new VideosListViewHolder(view, fragmentType);
            }
        }

        public static Style fromId(int id){
            return Stream.of(values())
                    .filter(style -> style.getId() == id)
                    .findFirst()
                    .orElse(null);
        }
    }

    private Style mStyle;

    public VideosAdapter(Context context, Style style) {
        super(context);
        mStyle = style;
        setItems(new ArrayList<>());
    }

    public void update(Video video) {
        IntStream.range(0, getItems().size())
                .filter(i -> getItem(i).getPutId() == video.getPutId())
                .forEach(i -> {
                    getItems().set(i, video);
                    notifyItemChanged(i);
                });
    }

    public void setStyle(Style style) {
        mStyle = style;
    }

    @Override
    protected int getLayoutResId() {
        return mStyle.getLayoutResId();
    }

    @Override
    protected BaseVideosViewHolder<? extends ViewBinding> getViewHolder(View view, FragmentType fragmentType) {
        return mStyle.getViewHolder(view, fragmentType);
    }
}
