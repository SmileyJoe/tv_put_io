package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.putio.File;

public class FolderListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public interface Listener{
        void onFolderClicked(File folder);
    }

    private TextView mTextTitle;
    private ImageView mImageIcon;
    private File mFile;
    private Listener mListener;

    public FolderListViewHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);

        mTextTitle = itemView.findViewById(R.id.text_title);
        mImageIcon = itemView.findViewById(R.id.image_icon);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void bindView(File file){
        mFile = file;

        if(file.isParent()){
            mTextTitle.setText(R.string.text_back);
            mImageIcon.setImageResource(R.drawable.ic_up_folder_24);
        } else {
            mTextTitle.setText(file.getName());
            mImageIcon.setImageResource(R.drawable.ic_folder_24);
        }
    }

    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onFolderClicked(mFile);
        }
    }
}