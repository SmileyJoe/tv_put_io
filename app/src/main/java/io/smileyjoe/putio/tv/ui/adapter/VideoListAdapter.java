package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.viewholder.BaseViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideoGridViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideoListViewHolder;

public class VideoListAdapter extends BaseListAdapter<Video, BaseViewHolder<Video>> {

    public enum Type{
        LIST(R.layout.list_item_video), GRID(R.layout.grid_item_video);

        @LayoutRes int mLayoutResId;

        Type(int layoutResId) {
            mLayoutResId = layoutResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
        }
    }

    private Type mType;

    public VideoListAdapter(Context context, Type type) {
        super(context);
        mType = type;
        setItems(new ArrayList<>());
    }

    public void setType(Type type) {
        mType = type;
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

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(mType.getLayoutResId(), parent, false);
        BaseViewHolder holder;

        switch (mType){
            case GRID:
                holder = new VideoGridViewHolder(view, getFragmentType());
                break;
            case LIST:
            default:
                holder = new VideoListViewHolder(view, getFragmentType());
                break;
        }

        holder.setListener(getListener());

        return holder;
    }
}
