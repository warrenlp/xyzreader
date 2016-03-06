package com.example.xyzreader.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.xyzreader.R;

/**
 * Created by Warren on 3/1/2016.
 */
@SuppressWarnings("unused")
public class TitleImageViewBehavior extends CoordinatorLayout.Behavior<ImageView> {

//    private final static float MIN_TITLE_PERCENTAGE_SIZE = 0.3f;
//    private final static int EXTRA_FINAL_TITLE_PADDING = 80;

    private final static String TAG = "behavior";
    private final Context mContext;
    private float mImageMaxSize;

    private float mFinalTitlePadding;
    private float mStartPosition;
    private int mStartXPosition;
    private int mStartYPosition;
    private float mStartToolbarPosition;
    private int mFinalYPosition;
    private int finalWidth;
    private int finalHeight;
    private int mStartHeight;
    private int mStartWidth;
    private int mFinalXPosition;

    public TitleImageViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() { bindDimensions(); }

    private void bindDimensions() {
        mFinalTitlePadding = mContext.getResources().getDimension(R.dimen.spacing_normal);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof Toolbar;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {
        maybeInitProperties(child, dependency);

        final float maxScrollDistance = mStartToolbarPosition;
        float expandedPercentageFactor = dependency.getY() / maxScrollDistance;

        float distanceYToSubtract = ((mStartYPosition - mFinalYPosition)
            * (1f - expandedPercentageFactor));

        float distanceXToSubtract = ((mStartXPosition - mFinalXPosition)
                * (1f - expandedPercentageFactor));

        float heightToSubstract = ((mStartHeight - finalHeight) * (1f - expandedPercentageFactor));
        float widthToSubstract = ((mStartWidth - finalWidth) * (1f - expandedPercentageFactor));

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.width = (int) (mStartWidth - widthToSubstract);
        lp.height = (int) (mStartHeight - heightToSubstract);
        child.setLayoutParams(lp);

        child.setY(mStartYPosition - distanceYToSubtract);
        child.setX(mStartXPosition - distanceXToSubtract);

        return true;
    }

    private void maybeInitProperties(ImageView child, View dependency) {
        int rawWidth = child.getDrawable().getIntrinsicWidth();
        int rawHeight = child.getDrawable().getIntrinsicHeight();
        float aspectRatio = (float)rawWidth/(float)rawHeight;

        if (mImageMaxSize == 0) {
            int dw = dependency.getWidth();
            mImageMaxSize = dependency.getWidth() - (2 * mFinalTitlePadding);
        }

        if (mStartWidth == 0) {
            mStartWidth = (int) mImageMaxSize;
        }

        if (mStartXPosition == 0) {
            mStartXPosition = (int) (dependency.getX() + mFinalTitlePadding);
        }

        if (mStartHeight == 0) {
            mStartHeight = (int)((float) mStartWidth / aspectRatio);
        }

        if (mStartYPosition == 0) {
            int deltaHeight = dependency.getHeight() - mStartHeight;
            mStartYPosition = (int) dependency.getY() + deltaHeight;
        }

        if (finalHeight == 0) {
            finalHeight = dependency.getHeight();
        }

        if (finalWidth == 0) {
            finalWidth = (int)(finalHeight * aspectRatio);
        }

        if (mFinalXPosition == 0) {
            mFinalXPosition = mContext.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material); // +
        }

        if (mStartToolbarPosition == 0) {
            mStartToolbarPosition = dependency.getY(); // - (dependency.getHeight() / 2);
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
