package io.smileyjoe.putio.tv.ui.viewholder;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.ListItemFolderBinding;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.Video;

public class FolderListViewHolder extends BaseViewHolder<Folder, ListItemFolderBinding> {

    public static final @LayoutRes int VIEW = R.layout.list_item_folder;

    public FolderListViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);
    }

    @Override
    protected ListItemFolderBinding inflate(View view) {
        return ListItemFolderBinding.bind(view);
    }

    @Override
    public void bindView(Folder folder, int position) {
        super.bindView(folder, position);

        mView.textTitle.setText(folder.getTitle());
        mView.imageIcon.setImageResource(folder.getIconResId());

        setText(mView.textSize, folder.getSubTextOne(mView.textSize.getContext()));
        setText(mView.textUpdatedAt, folder.getSubTextTwo(mView.textUpdatedAt.getContext()));
    }
}
