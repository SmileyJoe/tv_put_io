package io.smileyjoe.putio.tv.ui.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.ItemVideoDetailsBinding;
import io.smileyjoe.putio.tv.object.Character;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.Format;
import io.smileyjoe.putio.tv.util.PopulateGenres;

public class VideoDetailsViewHolder extends Presenter.ViewHolder {

    private Context mContext;
    private boolean mIsMinimized = false;
    private ItemVideoDetailsBinding mView;
    private ArrayList<View> mToggledViews = new ArrayList<>();

    public static VideoDetailsViewHolder getInstance(Context context, ViewGroup parent, boolean attach){
        return new VideoDetailsViewHolder(ItemVideoDetailsBinding.inflate(LayoutInflater.from(context), parent, attach));
    }

    private VideoDetailsViewHolder(ItemVideoDetailsBinding view) {
        super(view.getRoot());
        mView = view;
        mContext = view.getRoot().getContext();
    }

    public void addTextShadow(){
        addTextShadow(mView.textTitle);
        addTextShadow(mView.textSeason);
        addTextShadow(mView.textOverview);
        addTextShadow(mView.textTagline);
        addTextShadow(mView.textRuntime);
        addTextShadow(mView.textReleaseDate);
        addTextShadow(mView.textCreatedAt);
        addTextShadow(mView.textCast);
        addTextShadow(mView.textCastTitle);
    }

    private void addTextShadow(TextView view){
        view.setShadowLayer(2, 1, 1, Color.BLACK);
    }

    public void bind(Video video){
        setText(mView.textTitle, video.getTitleFormatted());
        setText(mView.textOverview, video.getOverView());
        setText(mView.textTagline, video.getTagLine());
        setText(mView.textRuntime, Format.runtime(mContext, video.getRuntime()));
        setText(mView.textCreatedAt, mContext.getString(R.string.text_added_on, video.getCreatedAtFormatted()));

        if(video.getReleaseDate() > 0) {
            setText(mView.textReleaseDate, mContext.getString(R.string.text_released_on, video.getReleaseDateFormatted()));
        }

        if(video.getSeason() > 0){
            mView.textSeason.setText(mContext.getString(R.string.text_season) + " " + video.getSeason());
            mView.textSeason.setVisibility(View.VISIBLE);
        } else {
            mView.textSeason.setVisibility(View.GONE);
        }

        if(video.getCharacters() != null && !video.getCharacters().isEmpty()){
            mView.layoutCast.setVisibility(View.VISIBLE);
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

            mView.textCast.setText(cast);
        } else {
            mView.layoutCast.setVisibility(View.GONE);
        }

        PopulateGenres task = new PopulateGenres(mView.layoutGenres, video);
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
            ViewGroup viewGroup = (ViewGroup) mView.getRoot().getChildAt(0);
            if (minimized) {
                mToggledViews = new ArrayList<>();
                for(int i = 0; i < viewGroup.getChildCount(); i++){
                    View child = viewGroup.getChildAt(i);

                    if(child.getId() != mView.textTitle.getId() && child.getVisibility() == View.VISIBLE) {
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
