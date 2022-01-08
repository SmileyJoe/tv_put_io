package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.ui.adapter.FolderListAdapter;

public class FolderListFragment extends Fragment {

    public interface Listener extends FolderListAdapter.Listener<Folder> {
    }

    private RecyclerView mRecycler;
    private LinearLayout mLayoutEmpty;
    private FolderListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_folder_list, null);

        mRecycler = view.findViewById(R.id.recycler);
        mLayoutEmpty = view.findViewById(R.id.layout_empty);

        return view;
    }

    public void setType(FragmentType fragmentType) {

        if(mAdapter != null){
            mAdapter.setFragmentType(fragmentType);
        }

        setLayoutManager(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new FolderListAdapter(getContext());

        mRecycler.setAdapter(mAdapter);
        setLayoutManager(false);
    }

    public void setListener(Listener listener) {
        mAdapter.setListener(listener);
    }

    private boolean setLayoutManager(boolean force){
        boolean created = false;
        if(mRecycler != null) {
            if(mLayoutManager == null || force) {
                mLayoutManager = new LinearLayoutManager(getContext());
                mRecycler.setLayoutManager(mLayoutManager);
                created = true;
            }
        }
        return created;
    }

    public void setFolders(ArrayList<Folder> folders) {
        if (folders == null || folders.isEmpty()) {
            mLayoutEmpty.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        } else {
            mLayoutEmpty.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);

            mAdapter.setItems(folders);
            mAdapter.notifyDataSetChanged();
        }
    }

}
