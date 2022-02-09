package io.smileyjoe.putio.tv.ui.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

import io.smileyjoe.putio.tv.R;

public class AnimationFrameLayout extends FrameLayout {

    public enum Type {
        LEFT(0), RIGHT(1);

        private int mAttrId;

        Type(int attrId) {
            mAttrId = attrId;
        }

        private int getAttrId() {
            return mAttrId;
        }

        public static Optional<Type> fromAttr(int attrId){
            return Optional.ofNullable(Stream.of(values())
                    .filter(type -> type.getAttrId() == attrId)
                    .findFirst()
                    .orElse(null));
        }
    }

    public enum StartPosition{
        ENTER(0), EXIT(1);

        private int mAttrId;

        StartPosition(int attrId) {
            mAttrId = attrId;
        }

        public int getAttrId() {
            return mAttrId;
        }

        public static Optional<StartPosition> fromAttr(int attrId){
            return Optional.ofNullable(Stream.of(values())
                    .filter(start -> start.getAttrId() == attrId)
                    .findFirst()
                    .orElse(null));
        }
    }

    private Type mType;
    private int mScreenWidth;
    private final int mDuration = 300;

    public AnimationFrameLayout(@NonNull Context context) {
        super(context);
        init(null);
    }

    public AnimationFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AnimationFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs){
        mScreenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.AnimationFrameLayout, 0, 0);
        try {
            Type
                    .fromAttr(ta.getInt(R.styleable.AnimationFrameLayout_enter_direction, -1))
                    .ifPresent(type -> setAnimation(type));
            StartPosition.fromAttr(ta.getInt(R.styleable.AnimationFrameLayout_enter_direction, -1))
                    .ifPresent(this::setStartPosition);
        } finally {
            ta.recycle();
        }
    }

    private void setStartPosition(StartPosition position){
        switch (position){
            case ENTER:
                enter();
                break;
            case EXIT:
                exit();
                break;
        }
    }

    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }

    public void setAnimation(Type type){
        mType = type;
    }

    public void enter(){
        int start;
        int end;

        switch (mType){
            case RIGHT:
                start = mScreenWidth;
                end = mScreenWidth - getMeasuredWidth();
                break;
            case LEFT:
            default:
                start = 0 - getMeasuredWidth();
                end = 0;
                break;
        }

        setVisibility(VISIBLE);
        setTranslationX(start);
        ObjectAnimator animation = ObjectAnimator.ofFloat(this, "translationX", end);
        animation.setDuration(mDuration);
        animation.start();

    }

    public void exit(){
        int end;

        switch (mType){
            case RIGHT:
                end = mScreenWidth;
                break;
            case LEFT:
            default:
                end = 0 - getMeasuredWidth();
                break;
        }

        ObjectAnimator animation = ObjectAnimator.ofFloat(this, "translationX", end);
        animation.setDuration(mDuration);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.start();
    }
}
