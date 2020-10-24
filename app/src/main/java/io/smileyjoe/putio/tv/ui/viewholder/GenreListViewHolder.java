package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.ui.view.PillView;

public class GenreListViewHolder extends BaseViewHolder<Genre> {

    private PillView mPillTitle;

    public GenreListViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);

        mPillTitle = itemView.findViewById(R.id.text_title);
    }

    @Override
    public void bindView(Genre item, int position) {
        super.bindView(item, position);

        mPillTitle.setText(item.getTitle());
    }
}
