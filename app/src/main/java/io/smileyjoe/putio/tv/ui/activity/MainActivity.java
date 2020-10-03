package io.smileyjoe.putio.tv.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.putio.Putio;
import io.smileyjoe.putio.tv.putio.Response;
import io.smileyjoe.putio.tv.tmdb.Tmdb;
import io.smileyjoe.putio.tv.ui.fragment.FolderListFragment;
import io.smileyjoe.putio.tv.ui.fragment.VideoListFragment;
import io.smileyjoe.putio.tv.util.VideoUtil;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity implements FolderListFragment.Listener, VideoListFragment.Listener{

    private TextView mTextTitle;

    private ArrayList<Video> mParentFiles;
    private Video mCurrentFile = null;

    private FolderListFragment mFragmentFolderList;
    private VideoListFragment mFragmentVideoList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParentFiles = new ArrayList<>();
        mTextTitle = findViewById(R.id.text_title);

        Putio.getFiles(getBaseContext(), new OnPutResponse());

        mFragmentFolderList = (FolderListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_folder_list);
        mFragmentVideoList = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_list);
    }

    @Override
    public void onBackPressed() {
        if(mParentFiles != null && !mParentFiles.isEmpty()){
            Video parentFile = mParentFiles.get(mParentFiles.size()-1);
            getFiles(parentFile.getPutId());
            mParentFiles.remove(parentFile);
            mCurrentFile = null;
        } else {
            super.onBackPressed();
        }
    }

    private void getFiles(long parentId){
        Putio.getFiles(getBaseContext(), parentId, new OnPutResponse());
    }

    @Override
    public void onFolderClicked(Video video) {
        getFiles(video.getPutId());
    }

    @Override
    public void onVideoClicked(Video video, ArrayList<Video> relatedVideos) {
//        startActivity(PlaybackActivity.getIntent(getBaseContext(), file));
        startActivity(DetailsActivity.getIntent(getBaseContext(), video, relatedVideos));
    }

    private void populate(ArrayList<Video> videos){
        ArrayList<Video> folders = new ArrayList<>();
        ArrayList<Video> videosSorted = new ArrayList<>();

        // todo: this has a loop issue, but it also means that the folder list shows //
        // to only show back, I don't know if I want that //
//            mCurrentFile.setParent(true);
//
//            if(mParentFiles != null && !mParentFiles.isEmpty()) {
//                folders.add(mParentFiles.get(mParentFiles.size()-1));
//            }

        for(Video video:videos){
            switch (video.getType()){
                case VIDEO:
                case MOVIE:
                case EPISODE:
                    videosSorted.add(video);
                    break;
                case FOLDER:
                    folders.add(video);
                    break;
            }
        }

        mTextTitle.setText(mCurrentFile.getTitle());

        mFragmentVideoList.setVideos(videosSorted);
        populateFolders(folders);
    }

    private void populateFolders(ArrayList<Video> videos){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(videos == null || videos.isEmpty()){
            transaction.hide(mFragmentFolderList);
        } else {
            transaction.show(mFragmentFolderList);
            mFragmentFolderList.setVideos(videos);
        }

        transaction.commit();
    }

    private class OnPutResponse extends Response{
        @Override
        public void onSuccess(JsonObject result) {
            if(mCurrentFile != null){
                mParentFiles.add(mCurrentFile);
            }

            JsonArray filesJson = result.getAsJsonArray("files");
            JsonObject parentObject = result.getAsJsonObject("parent");

            ArrayList<Video> videos = VideoUtil.filter(VideoUtil.parseFromPut(filesJson));
            VideoUtil.sort(videos);
            mCurrentFile = VideoUtil.parseFromPut(parentObject);

            // todo: testing, honey I shrunk the kids //
            for(Video video:videos){
                if(video.getPutId() == 774524118){
                    Tmdb.searchMovie(getBaseContext(), video.getTitle(), video.getYear(), new OnTmdbSearchResponse(video));
                    break;
                }
            }

            populate(videos);
        }
    }

    private class OnTmdbSearchResponse extends Response{

        private Video mVideo;

        public OnTmdbSearchResponse(Video video) {
            mVideo = video;
        }

        @Override
        public void onSuccess(JsonObject result) {
            VideoUtil.updateFromTmdb(mVideo, result.get("results").getAsJsonArray());
        }
    }
}
