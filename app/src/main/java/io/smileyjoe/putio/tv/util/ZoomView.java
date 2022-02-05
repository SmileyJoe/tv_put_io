package io.smileyjoe.putio.tv.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

public class ZoomView {

    private HashMap<Integer, int[]> mOriginalSizes;
    private static final int sPositionWidth = 0;
    private static final int sPositionHeight = 1;

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
        getOriginalSizes(zoomView);
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
        params.width = originalSizes[sPositionWidth];

        if(mIncludeHeight) {
            params.height = originalSizes[sPositionHeight];
        }
        view.setLayoutParams(params);
    }

    private int[] getOriginalSizes(View view){
        int id = view.getId();

        if(!mOriginalSizes.containsKey(id)){
            mOriginalSizes.put(id, new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()});
        }

        return mOriginalSizes.get(id);
    }

}
