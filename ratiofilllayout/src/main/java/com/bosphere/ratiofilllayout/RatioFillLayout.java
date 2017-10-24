package com.bosphere.ratiofilllayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yangbo on 24/10/17.
 */

public class RatioFillLayout extends ViewGroup {

    public static final int GRAVITY_NOT_DEFINED = -1;
    public static final int GRAVITY_TOP = 0;
    public static final int GRAVITY_BOTTOM = 1;
    public static final int GRAVITY_LEFT = 2;
    public static final int GRAVITY_RIGHT = 3;
    public static final int GRAVITY_CENTER_HORIZONTAL = 4;
    public static final int GRAVITY_CENTER_VERTICAL = 5;
    
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int mOrientation = HORIZONTAL;
    private int mGravity = GRAVITY_NOT_DEFINED;

    public RatioFillLayout(Context context) {
        super(context);
    }

    public RatioFillLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RatioFillLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RatioFillLayout);
        mOrientation = a.getInt(R.styleable.RatioFillLayout_rfl_orientation, HORIZONTAL);
        mGravity = a.getInt(R.styleable.RatioFillLayout_rfl_gravity, GRAVITY_NOT_DEFINED);
        a.recycle();
    }

    public void setOrientation(int orientation) {
        if (mOrientation == orientation) {
            return;
        }
        mOrientation = orientation;
        requestLayout();
    }

    /**
     * Refer to {@link #GRAVITY_TOP}, {@link #GRAVITY_BOTTOM}, {@link #GRAVITY_LEFT}, {@link #GRAVITY_RIGHT}, {@link #GRAVITY_CENTER_HORIZONTAL}, {@link #GRAVITY_CENTER_VERTICAL}
     * @param gravity
     */
    public void setGravity(int gravity) {
        if (mGravity == gravity) {
            return;
        }
        mGravity = gravity;
        requestLayout();
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOrientation == HORIZONTAL) {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        int maxHeight = 0;
        int childState = 0;

        int accumWidth = 0;
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, childWidthMeasureSpec, 0, heightMeasureSpec, 0);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                lp.layoutWidth = child.getMeasuredWidth();
                lp.layoutHeight = child.getMeasuredHeight();
                accumWidth += lp.layoutWidth + lp.leftMargin + lp.rightMargin;
                maxHeight = Math.max(maxHeight, lp.layoutHeight + lp.topMargin + lp.bottomMargin);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        int usableWidth = MeasureSpec.getSize(widthMeasureSpec);
        usableWidth -= getPaddingLeft() + getPaddingRight();
        if (usableWidth > 0 && accumWidth < usableWidth) {
            // let's stretch children to fill the width
            maxHeight = 0;
            childState = 0;
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() != GONE) {
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    int oldChildWidth = lp.layoutWidth + lp.leftMargin + lp.rightMargin;
                    int newChildWidth = (int) (((float) oldChildWidth / accumWidth) * usableWidth + 0.5f);
                    lp.layoutWidth = newChildWidth - lp.leftMargin - lp.rightMargin;
                    int widthSpec = MeasureSpec.makeMeasureSpec(lp.layoutWidth, MeasureSpec.EXACTLY);
                    int heightSpec = getChildMeasureSpec(heightMeasureSpec,
                            getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
                            lp.height);
                    child.measure(widthSpec, heightSpec);
                    lp.layoutHeight = child.getMeasuredHeight();
                    maxHeight = Math.max(maxHeight, lp.layoutHeight + lp.topMargin + lp.bottomMargin);
                    childState = combineMeasuredStates(childState, child.getMeasuredState());
                }
            }
        } else {
            usableWidth = accumWidth;
        }

        // Account for padding too
        usableWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        usableWidth = Math.max(usableWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(usableWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    private void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        int maxWidth = 0;
        int childState = 0;

        int accumHeight = 0;
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, childHeightMeasureSpec, 0);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                lp.layoutWidth = child.getMeasuredWidth();
                lp.layoutHeight = child.getMeasuredHeight();
                accumHeight += lp.layoutHeight + lp.topMargin + lp.bottomMargin;
                maxWidth = Math.max(maxWidth, lp.layoutWidth + lp.leftMargin + lp.rightMargin);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        int usableHeight = MeasureSpec.getSize(heightMeasureSpec);
        usableHeight -= getPaddingTop() + getPaddingBottom();
        if (usableHeight > 0 && accumHeight < usableHeight) {
            // let's stretch children to fill the width
            maxWidth = 0;
            childState = 0;
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() != GONE) {
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    int oldChildHeight = lp.layoutHeight + lp.topMargin + lp.bottomMargin;
                    int newChildHeight = (int) (((float) oldChildHeight / accumHeight) * usableHeight + 0.5f);
                    lp.layoutHeight = newChildHeight - lp.topMargin - lp.bottomMargin;
                    int heightSpec = MeasureSpec.makeMeasureSpec(lp.layoutHeight, MeasureSpec.EXACTLY);
                    int widthSpec = getChildMeasureSpec(widthMeasureSpec,
                            getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                            lp.width);
                    child.measure(widthSpec, heightSpec);
                    lp.layoutWidth = child.getMeasuredWidth();
                    maxWidth = Math.max(maxWidth, lp.layoutWidth + lp.leftMargin + lp.rightMargin);
                    childState = combineMeasuredStates(childState, child.getMeasuredState());
                }
            }
        } else {
            usableHeight = accumHeight;
        }

        // Account for padding too
        maxWidth += getPaddingLeft() + getPaddingRight();
        usableHeight += getPaddingTop() + getPaddingBottom();

        // Check against our minimum height and width
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        usableHeight = Math.max(usableHeight, getSuggestedMinimumHeight());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(usableHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mOrientation == HORIZONTAL) {
            layoutHorizontal(changed, l, t, r, b);
        } else {
            layoutVertical(changed, l, t, r, b);
        }
    }

    private void layoutHorizontal(boolean changed, int l, int t, int r, int b) {
        int cl = getPaddingLeft();
        int parentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = lp.layoutHeight + lp.topMargin + lp.bottomMargin;
            cl += lp.leftMargin;
            int ct = getPaddingTop();
            if (mGravity == GRAVITY_CENTER_VERTICAL) {
                ct += ((parentHeight - childHeight) >> 1) + lp.topMargin;
            } else if (mGravity == GRAVITY_BOTTOM) {
                ct += parentHeight - childHeight + lp.topMargin;
            } else {
                ct += lp.topMargin;
            }
            child.layout(cl, ct, cl + lp.layoutWidth, ct + lp.layoutHeight);
            cl += lp.layoutWidth + lp.rightMargin;
        }
    }

    private void layoutVertical(boolean changed, int l, int t, int r, int b) {
        int ct = getPaddingTop();
        int parentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childWidth = lp.layoutWidth + lp.leftMargin + lp.rightMargin;
            ct += lp.topMargin;
            int cl = getPaddingLeft();
            if (mGravity == GRAVITY_CENTER_HORIZONTAL) {
                cl += ((parentWidth - childWidth) >> 1) + lp.leftMargin;
            } else if (mGravity == GRAVITY_RIGHT) {
                cl += parentWidth - childWidth + lp.leftMargin;
            } else {
                cl += lp.leftMargin;
            }
            child.layout(cl, ct, cl + lp.layoutWidth, ct + lp.layoutHeight);
            ct += lp.layoutHeight + lp.bottomMargin;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends MarginLayoutParams {

        int layoutWidth = 0;
        int layoutHeight = 0;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
