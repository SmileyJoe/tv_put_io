package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.stream.IntStream;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.viewholder.BaseVideosViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideosGridViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideosListViewHolder;

public class VideosAdapter extends BaseListAdapter<Video, BaseVideosViewHolder<? extends ViewBinding>> {

    public enum Style {
        GRID(R.layout.grid_item_video),
        LIST(R.layout.list_item_video);

        @LayoutRes
        private int mLayoutResId;

        Style(@LayoutRes int layoutResId) {
            mLayoutResId = layoutResId;
        }

        @LayoutRes
        public int getLayoutResId() {
            return mLayoutResId;
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
