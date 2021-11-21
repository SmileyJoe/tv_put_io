package io.smileyjoe.putio.tv.ui.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import java.util.ArrayList;

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
    private boolean mIsMinimized = false;
    private ViewGroup mView;
    private ArrayList<View> mToggledViews = new ArrayList<>();

    public VideoDetailsViewHolder(ViewGroup view) {
        super(view);
        mView = view;
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

            int count = 0;
            for(Character character:video.getCharacters()){
                count++;
                if(!TextUtils.isEmpty(cast)){
                    cast += ", ";
                }

                cast += character.getCastMemberName();

                if(count >= 10){
                    break;
                }
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

    public void toggleMinimized(){
        setMinimized(!mIsMinimized);
    }

    public void setMinimized(boolean minimized){
        if(mIsMinimized != minimized) {
            ViewGroup viewGroup = (ViewGroup) mView.getChildAt(0);
            if (minimized) {
                mToggledViews = new ArrayList<>();
                for(int i = 0; i < viewGroup.getChildCount(); i++){
                    View child = viewGroup.getChildAt(i);

                    if(child.getId() != mTextTitle.getId() && child.getVisibility() == View.VISIBLE) {
                        child.setVisibility(View.GONE);
                        mToggledViews.add(child);
                    }
                }
            } else {
                for(View view:mToggledViews){
                    view.setVisibility(View.VISIBLE);
                }
                mToggledViews = null;
            }

            mIsMinimized = minimized;
        }
    }

    public boolean isMinimized() {
        return mIsMinimized;
    }
}
