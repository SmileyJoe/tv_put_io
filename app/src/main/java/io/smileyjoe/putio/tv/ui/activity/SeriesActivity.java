package io.smileyjoe.putio.tv.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.adapter.VideosAdapter;
import io.smileyjoe.putio.tv.ui.fragment.SeasonDetailsFragment;
import io.smileyjoe.putio.tv.ui.fragment.VideosFragment;

public class SeriesActivity extends FragmentActivity {

    private static final String EXTRA_PARENT = "parent";
    private static final String EXTRA_VIDEOS = "videos";

    private SeasonDetailsFragment mFragmentSeasonDetails;
    private VideosFragment mFragmentVideoList;
    private ImageView mImagePoster;

    public static Intent getIntent(Context context, Video parent, ArrayList<Video> videos){
        Intent intent = new Intent(context, SeriesActivity.class);

        intent.putExtra(EXTRA_PARENT, parent);
        intent.putExtra(EXTRA_VIDEOS, videos);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_series);

        mImagePoster = findViewById(R.id.image_poster);
        mFragmentVideoList = (VideosFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_list);
        mFragmentVideoList.setType(FragmentType.VIDEO);
        mFragmentVideoList.setStyle(VideosFragment.Style.LIST);
        mFragmentSeasonDetails = (SeasonDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_season_details);

        mFragmentVideoList.setListener(new VideoListListener());

        handleExtras();
    }

    private void handleExtras(){
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            if(extras.containsKey(EXTRA_PARENT)){
                Video parent = extras.getParcelable(EXTRA_PARENT);
                mFragmentSeasonDetails.update(parent);

                Glide.with(getBaseContext())
                        .load(parent.getPosterAsUri())
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mImagePoster);
            }

            if(extras.containsKey(EXTRA_VIDEOS)){
                mFragmentVideoList.setVideos(extras.getParcelableArrayList(EXTRA_VIDEOS));
            }
        }
    }

    private class VideoListListener implements VideosAdapter.Listener<Video>{
        @Override
        public void onItemClicked(View view, Video item) {
            startActivity(PlaybackActivity.getIntent(getBaseContext(), mFragmentVideoList.getVideos(), item, true));
        }

        @Override
        public void hasFocus(FragmentType type, Video item, View view, int position) {
            // do nothing //
        }
    }
}
