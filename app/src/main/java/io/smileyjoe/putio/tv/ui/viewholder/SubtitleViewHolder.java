package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.ListItemSubtitleBinding;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Subtitle;

public class SubtitleViewHolder extends BaseViewHolder<Subtitle, ListItemSubtitleBinding> {

    public static final @LayoutRes int VIEW = R.layout.list_item_subtitle;

    public SubtitleViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);
    }

    @Override
    protected ListItemSubtitleBinding inflate(View itemView) {
        return ListItemSubtitleBinding.bind(itemView);
    }

    @Override
    public void bindView(Subtitle item, int position) {
        super.bindView(item, position);

        mView.textTitle.setText(item.getLanguage());
        setText(mView.textDetails, item.getName());
    }
}
