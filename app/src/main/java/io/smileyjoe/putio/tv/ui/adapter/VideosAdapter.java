package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.viewholder.BaseViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.BaseVideosViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideosGridViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideosListViewHolder;

public class VideosAdapter extends BaseListAdapter<Video, BaseVideosViewHolder<? extends ViewBinding>> {

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

        public BaseVideosViewHolder<? extends ViewBinding> getViewHolder(View view, FragmentType fragmentType){
            switch (this){
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

    public void update(Video video){
        ArrayList<Video> videos = getItems();
        for(int i = 0; i < videos.size(); i++){
            if(videos.get(i).getPutId() == video.getPutId()){
                videos.set(i, video);
                notifyItemChanged(i);
                break;
            }
        }
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
