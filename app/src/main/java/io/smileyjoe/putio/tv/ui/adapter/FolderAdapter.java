package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.putio.File;

public class FolderAdapter extends FileAdapter {

    public FolderAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File file = getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_folder, null);
        TextView textTitle = view.findViewById(R.id.text_title);
        ImageView imageIcon = view.findViewById(R.id.image_icon);

        if(file.isParent()){
            textTitle.setText(R.string.text_back);
            imageIcon.setImageResource(R.drawable.ic_up_folder_24);
        } else {
            textTitle.setText(file.getName());
            imageIcon.setImageResource(R.drawable.ic_folder_24);
        }

        return view;
    }
}
