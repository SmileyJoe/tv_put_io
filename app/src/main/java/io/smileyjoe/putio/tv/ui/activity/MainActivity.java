package io.smileyjoe.putio.tv.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.DetailsActivity;
import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.putio.File;
import io.smileyjoe.putio.tv.putio.Putio;
import io.smileyjoe.putio.tv.putio.Response;
import io.smileyjoe.putio.tv.ui.adapter.FileSelectedListener;
import io.smileyjoe.putio.tv.ui.adapter.FolderAdapter;
import io.smileyjoe.putio.tv.ui.adapter.VideoAdapter;
import io.smileyjoe.putio.tv.ui.fragment.FolderListFragment;
import io.smileyjoe.putio.tv.ui.fragment.VideoListFragment;
import io.smileyjoe.putio.tv.util.FileUtils;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity implements FolderListFragment.Listener, VideoListFragment.Listener{

    private TextView mTextTitle;

    private ArrayList<File> mParentFiles;
    private File mCurrentFile = null;

    private FolderListFragment mFragmentFolderList;
    private VideoListFragment mFragmentVideoList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParentFiles = new ArrayList<>();
        mTextTitle = findViewById(R.id.text_title);

        Putio.getFiles(getBaseContext(), new OnFileResponse());

        mFragmentFolderList = (FolderListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_folder_list);
        mFragmentVideoList = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_list);
    }

    @Override
    public void onBackPressed() {
        if(mParentFiles != null && !mParentFiles.isEmpty()){
            File parentFile = mParentFiles.get(mParentFiles.size()-1);
            getFiles(parentFile.getId());
            mParentFiles.remove(parentFile);
            mCurrentFile = null;
        } else {
            super.onBackPressed();
        }
    }

    private void getFiles(long parentId){
        Putio.getFiles(getBaseContext(), parentId, new OnFileResponse());
    }

    @Override
    public void onFolderClicked(File file) {
        getFiles(file.getId());
    }

    @Override
    public void onVideoClicked(File file) {
//        startActivity(PlaybackActivity.getIntent(getBaseContext(), file));
        startActivity(DetailsActivity.getIntent(getBaseContext(), file));
    }

    private void populate(ArrayList<File> files){
        ArrayList<File> folders = new ArrayList<>();
        ArrayList<File> videos = new ArrayList<>();

        // todo: this has a loop issue, but it also means that the folder list shows //
        // to only show back, I don't know if I want that //
//            mCurrentFile.setParent(true);
//
//            if(mParentFiles != null && !mParentFiles.isEmpty()) {
//                folders.add(mParentFiles.get(mParentFiles.size()-1));
//            }

        for(File file:files){
            switch (file.getFileType()){
                case VIDEO:
                    videos.add(file);
                    break;
                case FOLDER:
                    folders.add(file);
                    break;
            }
        }

        mTextTitle.setText(mCurrentFile.getName());

        mFragmentVideoList.setVideos(videos);
        populateFolders(folders);
    }

    private void populateFolders(ArrayList<File> files){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(files == null || files.isEmpty()){
            transaction.hide(mFragmentFolderList);
        } else {
            transaction.show(mFragmentFolderList);
            mFragmentFolderList.setFolders(files);
        }

        transaction.commit();
    }

    private class OnFileResponse extends Response{
        @Override
        public void onSuccess(JsonObject result) {
            if(mCurrentFile != null){
                mParentFiles.add(mCurrentFile);
            }

            JsonArray filesJson = result.getAsJsonArray("files");
            JsonObject parentObject = result.getAsJsonObject("parent");

            ArrayList<File> files = FileUtils.filter(File.fromApi(filesJson));
            FileUtils.sort(files);
            mCurrentFile = File.fromApi(parentObject);

            populate(files);
        }
    }
}
