package io.smileyjoe.putio.tv.ui.adapter;

import android.view.View;
import android.widget.AdapterView;

public class FileSelectedListener implements AdapterView.OnItemSelectedListener {
    private FileAdapter mFileAdapter;

    public FileSelectedListener(FileAdapter fileAdapter) {
        mFileAdapter = fileAdapter;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mFileAdapter.setSelectedPosition(position);
        mFileAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mFileAdapter.setSelectedPosition(-1);
        mFileAdapter.notifyDataSetChanged();
    }
}