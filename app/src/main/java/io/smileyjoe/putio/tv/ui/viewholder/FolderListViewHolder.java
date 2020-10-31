package io.smileyjoe.putio.tv.ui.viewholder;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.Video;

public class FolderListViewHolder extends BaseViewHolder<Folder> {

    private TextView mTextTitle;
    private TextView mTextSize;
    private TextView mTextUpdatedAt;
    private ImageView mImageIcon;

    public FolderListViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);

        mTextTitle = itemView.findViewById(R.id.text_title);
        mImageIcon = itemView.findViewById(R.id.image_icon);
        mTextSize = itemView.findViewById(R.id.text_size);
        mTextUpdatedAt = itemView.findViewById(R.id.text_updated_at);
    }

    @Override
    public void bindView(Folder folder, int position) {
        super.bindView(folder, position);

        mTextTitle.setText(folder.getTitle());
        mImageIcon.setImageResource(folder.getIconResId());

        setText(mTextSize, folder.getSubTextOne(mTextSize.getContext()));
        setText(mTextUpdatedAt, folder.getSubTextTwo(mTextUpdatedAt.getContext()));
    }

    private void setText(TextView textView, String text){
        if(!TextUtils.isEmpty(text)) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }
}
