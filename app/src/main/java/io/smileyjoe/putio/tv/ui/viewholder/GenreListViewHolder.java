package io.smileyjoe.putio.tv.ui.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.ListItemGenreBinding;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.ui.view.PillView;

public class GenreListViewHolder extends BaseViewHolder<Genre, ListItemGenreBinding> {

    @LayoutRes
    public static final int VIEW = R.layout.list_item_genre;

    public GenreListViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView, fragmentType);
    }

    @Override
    protected ListItemGenreBinding inflate(View itemView) {
        return ListItemGenreBinding.bind(itemView);
    }

    @Override
    public void bindView(Genre item, int position) {
        super.bindView(item, position);

        mView.textTitle.setText(item.getTitle());
    }
}
