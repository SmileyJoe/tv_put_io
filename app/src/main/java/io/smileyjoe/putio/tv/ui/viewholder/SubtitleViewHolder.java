package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Subtitle;

public class SubtitleViewHolder extends BaseViewHolder<Subtitle> {

    private TextView mTextTitle;
    private TextView mTextDetails;

    public SubtitleViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);

        mTextTitle = itemView.findViewById(R.id.text_title);
        mTextDetails = itemView.findViewById(R.id.text_details);
    }

    @Override
    public void bindView(Subtitle item, int position) {
        super.bindView(item, position);

        mTextTitle.setText(item.getLanguage());
        mTextDetails.setText(item.getName());
    }
}
