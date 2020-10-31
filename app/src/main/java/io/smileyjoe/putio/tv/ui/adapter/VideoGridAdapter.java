package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.viewholder.BaseViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideoGridViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.FolderListViewHolder;

public class VideoGridAdapter extends BaseListAdapter<Video, BaseViewHolder<Video>> {

    public VideoGridAdapter(Context context) {
        super(context);
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

    @Override
    protected int getLayoutResId() {
        return R.layout.grid_item_video;
    }

    @Override
    protected BaseViewHolder<Video> getViewHolder(View view, FragmentType fragmentType) {
        return new VideoGridViewHolder(view, fragmentType);
    }
}
