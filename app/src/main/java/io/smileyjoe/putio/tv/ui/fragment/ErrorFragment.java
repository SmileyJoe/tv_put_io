package io.smileyjoe.putio.tv.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.ErrorSupportFragment;

import io.smileyjoe.putio.tv.R;

public class ErrorFragment extends ErrorSupportFragment implements View.OnClickListener{

    public interface Listener{
        void onErrorDismissed();
    }

    public static void show(FragmentActivity activity, @StringRes int titleResId, @StringRes int messageResId, @IdRes int viewId){
        ErrorFragment errorFragment = new ErrorFragment();
        activity.getSupportFragmentManager().beginTransaction().add(viewId, errorFragment).commit();
        errorFragment.setErrorContent(activity.getBaseContext(), titleResId, messageResId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setErrorContent(Context context, @StringRes int titleResId, @StringRes int messageResId) {
        setTitle(context.getString(titleResId));
        setMessage(context.getString(messageResId));
        setDefaultBackground(false);
        setButtonText(context.getString(R.string.button_error));
        setButtonClickListener(this);
    }

    @Override
    public void onClick(View arg0) {

        if(getActivity() instanceof Listener){
            ((Listener) getActivity()).onErrorDismissed();
        } else {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        }
    }
}
