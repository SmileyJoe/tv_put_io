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
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.viewholder.FolderListViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideoBaseViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideoGridViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideoListViewHolderTwo;

public class VideoListAdapterTwo extends RecyclerView.Adapter<VideoBaseViewHolder> {

    public interface Listener extends VideoBaseViewHolder.Listener {
    }

    public enum Type{
        LIST(R.layout.list_item_folder), GRID(R.layout.grid_item_video);

        @LayoutRes int mLayoutResId;

        Type(int layoutResId) {
            mLayoutResId = layoutResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
        }
    }

    private ArrayList<Video> mVideos;
    private Context mContext;
    private Listener mListener;
    private Type mType;

    public VideoListAdapterTwo(Context context, Type type) {
        mContext = context;
        mType = type;
        setVideos(new ArrayList<>());
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setType(Type type) {
        mType = type;
    }

    public void setVideos(ArrayList<Video> videos) {
        mVideos = videos;
    }

    public ArrayList<Video> getVideos() {
        return mVideos;
    }

    @NonNull
    @Override
    public VideoBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mType.getLayoutResId(), parent, false);
        VideoBaseViewHolder holder;

        switch (mType){
            case GRID:
                holder = new VideoGridViewHolder(view);
                break;
            case LIST:
            default:
                holder = new VideoListViewHolderTwo(view);
                break;
        }

        holder.setListener(mListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoBaseViewHolder holder, int position) {
        holder.bindView(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public Video getItem(int position) {
        return mVideos.get(position);
    }
}
