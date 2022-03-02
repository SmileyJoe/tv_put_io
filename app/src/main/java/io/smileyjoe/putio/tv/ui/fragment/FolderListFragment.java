package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.databinding.FragmentFolderListBinding;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.ui.adapter.FolderListAdapter;

public class FolderListFragment extends BaseFragment<FragmentFolderListBinding> {

    public interface Listener extends FolderListAdapter.Listener<Folder> {
    }

    private FolderListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected FragmentFolderListBinding inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState) {
        return FragmentFolderListBinding.inflate(inflater, container, savedInstanceState);
    }

    @Override
    public void setType(FragmentType fragmentType) {
        super.setType(fragmentType);
        if (mAdapter != null) {
            mAdapter.setFragmentType(fragmentType);
        }

        setLayoutManager(true);
    }

    @Override
    public View getFocusableView() {
        return mView.recycler;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setType(FragmentType.FOLDER);

        mAdapter = new FolderListAdapter(getContext());

        mView.recycler.setAdapter(mAdapter);
        setLayoutManager(false);
    }

    public void setListener(Listener listener) {
        mAdapter.setListener(listener);
    }

    private boolean setLayoutManager(boolean force) {
        boolean created = false;
        if (mView.recycler != null) {
            if (mLayoutManager == null || force) {
                mLayoutManager = new LinearLayoutManager(getContext());
                mView.recycler.setLayoutManager(mLayoutManager);
                created = true;
            }
        }
        return created;
    }

    public void setFolders(ArrayList<Folder> folders) {
        if (folders == null || folders.isEmpty()) {
            mView.layoutEmpty.setVisibility(View.VISIBLE);
            mView.recycler.setVisibility(View.GONE);
        } else {
            mView.layoutEmpty.setVisibility(View.GONE);
            mView.recycler.setVisibility(View.VISIBLE);
        }
        mAdapter.setItems(folders);
    }

    public boolean hasItems(){
        if(mAdapter == null){
            return false;
        } else {
            return mAdapter.getItemCount() > 0;
        }
    }

}
