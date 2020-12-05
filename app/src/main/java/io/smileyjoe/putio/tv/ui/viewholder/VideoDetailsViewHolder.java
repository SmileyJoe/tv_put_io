package io.smileyjoe.putio.tv.ui.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Character;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.Format;
import io.smileyjoe.putio.tv.util.PopulateGenres;

public class VideoDetailsViewHolder extends Presenter.ViewHolder {

    private TextView mTextTitle;
    private TextView mTextOverview;
    private TextView mTextTagline;
    private TextView mTextRuntime;
    private TextView mTextReleaseDate;
    private TextView mTextCreatedAt;
    private TextView mTextCast;
    private LinearLayout mLayoutGenres;
    private LinearLayout mLayoutCast;
    private Context mContext;

    public VideoDetailsViewHolder(View view) {
        super(view);

        mContext = view.getContext();

        mTextTitle = view.findViewById(R.id.text_title);
        mTextOverview = view.findViewById(R.id.text_overview);
        mTextTagline = view.findViewById(R.id.text_tagline);
        mTextRuntime = view.findViewById(R.id.text_runtime);
        mTextReleaseDate = view.findViewById(R.id.text_release_date);
        mTextCreatedAt = view.findViewById(R.id.text_created_at);
        mLayoutGenres = view.findViewById(R.id.layout_genres);
        mLayoutCast = view.findViewById(R.id.layout_cast);
        mTextCast = view.findViewById(R.id.text_cast);
    }

    public void bind(Video video){
        setText(mTextTitle, video.getTitleFormatted());
        setText(mTextOverview, video.getOverView());
        setText(mTextTagline, video.getTagLine());
        setText(mTextRuntime, Format.runtime(mContext, video.getRuntime()));
        setText(mTextCreatedAt, mContext.getString(R.string.text_added_on, video.getCreatedAtFormatted()));

        if(video.getReleaseDate() > 0) {
            setText(mTextReleaseDate, mContext.getString(R.string.text_released_on, video.getReleaseDateFormatted()));
        }

        if(video.getCharacters() != null && !video.getCharacters().isEmpty()){
            mLayoutCast.setVisibility(View.VISIBLE);
            String cast = "";

            for(Character character:video.getCharacters()){
                if(!TextUtils.isEmpty(cast)){
                    cast += ", ";
                }

                cast += character.getCastMemberName();
            }

            mTextCast.setText(cast);
        } else {
            mLayoutCast.setVisibility(View.GONE);
        }

        PopulateGenres task = new PopulateGenres(mLayoutGenres, video);
        task.setHideOnEmpty(true);
        task.execute();
    }

    private void setText(TextView view, String text){
        if(TextUtils.isEmpty(text)){
            view.setVisibility(View.GONE);
        } else {
            view.setText(text);
            view.setVisibility(View.VISIBLE);
        }
    }
}
