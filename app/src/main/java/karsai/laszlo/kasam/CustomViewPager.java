package karsai.laszlo.kasam;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

    private boolean mIsSwipingAllowed;

    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return this.mIsSwipingAllowed && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.mIsSwipingAllowed && super.onInterceptTouchEvent(ev);
    }

    public void setIsSwipingAllowed(boolean isSwipingAllowed) {
        this.mIsSwipingAllowed = isSwipingAllowed;
    }
}
