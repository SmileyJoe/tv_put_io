package io.smileyjoe.putio.tv.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

public class ZoomView {

    private HashMap<Integer, int[]> mOriginalSizes;
    private final int mPositionWidth = 0;
    private final int mPositionHeight = 1;

    private boolean mIncludeHeight;

    public ZoomView(){
        mOriginalSizes = new HashMap<>();
        mIncludeHeight = true;
    }

    public void setIncludeHeight(boolean includeHeight) {
        mIncludeHeight = includeHeight;
    }

    private float getPosition(float viewPosition, float viewSize, float size, float parentSize) {
        float center = viewPosition + viewSize / 2;
        float position = center - (size / 2);

        if (position < 0) {
            position = 0;
        } else if ((position + size) > parentSize) {
            position = parentSize - size;
        }

        return position;
    }

    public void reposition(View originalView, View zoomView){
        ViewGroup parent = (ViewGroup) zoomView.getParent();
        ViewGroup grandParent = (ViewGroup) parent.getParent();
        parent.setX(getPosition(originalView.getX(), originalView.getWidth(), parent.getWidth(), grandParent.getWidth()));
        parent.setY(getPosition(originalView.getY(), originalView.getHeight(), parent.getHeight(), grandParent.getHeight()));
    }

    public void zoom(View view, float multiplier){
        zoom(view, view, multiplier);
    }

    public void zoom(View originalView, View zoomView, float multiplier){
        saveOriginalSize(zoomView);
        ViewGroup.LayoutParams params = zoomView.getLayoutParams();
        params.width = (int) (originalView.getMeasuredWidth() * multiplier);
        if(mIncludeHeight) {
            params.height = (int) (originalView.getMeasuredHeight() * multiplier);
        }
        zoomView.setLayoutParams(params);
    }

    public void reset(View view){
        int[] originalSizes = getOriginalSizes(view);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = originalSizes[mPositionWidth];

        if(mIncludeHeight) {
            params.height = originalSizes[mPositionHeight];
        }
        view.setLayoutParams(params);
    }

    private int saveOriginalSize(View view){
        int id = view.getId();

        if(!mOriginalSizes.containsKey(id)){
            mOriginalSizes.put(id, new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()});
        }

        return id;
    }

    private int[] getOriginalSizes(View view){
        int id = saveOriginalSize(view);
        return mOriginalSizes.get(id);
    }

}
