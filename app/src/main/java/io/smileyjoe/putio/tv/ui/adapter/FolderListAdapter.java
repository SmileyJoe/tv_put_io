package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.putio.File;
import io.smileyjoe.putio.tv.ui.viewholder.FolderListViewHolder;

public class FolderListAdapter  extends RecyclerView.Adapter<FolderListViewHolder>{

    public interface Listener extends FolderListViewHolder.Listener{}

    private ArrayList<File> mFolders;
    private Context mContext;
    private Listener mListener;

    public FolderListAdapter(Context context) {
        mContext = context;
        setFolders(new ArrayList<>());
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setFolders(ArrayList<File> folders) {
        mFolders = folders;
    }

    @NonNull
    @Override
    public FolderListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_folder, parent, false);

        FolderListViewHolder holder = new FolderListViewHolder(view);
        holder.setListener(mListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FolderListViewHolder holder, int position) {
        holder.bindView(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mFolders.size();
    }

    public File getItem(int position){
        return mFolders.get(position);
    }
}
