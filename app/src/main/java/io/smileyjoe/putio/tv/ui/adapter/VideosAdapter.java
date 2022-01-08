package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.fragment.VideosFragment;
import io.smileyjoe.putio.tv.ui.viewholder.BaseViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideosViewHolder;

public class VideosAdapter extends BaseListAdapter<Video, BaseViewHolder<Video>> {

    private VideosFragment.Style mStyle;

    public VideosAdapter(Context context, VideosFragment.Style style) {
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

    public void setStyle(VideosFragment.Style style) {
        mStyle = style;
    }

    @Override
    protected int getLayoutResId() {
        return mStyle.getLayoutResId();
    }

    @Override
    protected BaseViewHolder<Video> getViewHolder(View view, FragmentType fragmentType) {
        return new VideosViewHolder(view, fragmentType);
    }
}
