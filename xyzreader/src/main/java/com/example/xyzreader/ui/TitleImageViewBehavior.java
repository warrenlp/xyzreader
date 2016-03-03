package com.example.xyzreader.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.example.xyzreader.R;

/**
 * Created by Warren on 3/1/2016.
 */
@SuppressWarnings("unused")
public class TitleImageViewBehavior extends CoordinatorLayout.Behavior<ImageView> {

    private final static float MIN_TITLE_PERCENTAGE_SIZE = 0.3f;
    private final static int EXTRA_FINAL_TITLE_PADDING = 80;

    private final static String TAG = "behavior";
    private final Context mContext;
    private float mImageMaxSize;

    private float mFinalTitlePadding;
    private float mStartPosition;
    private int mStartXPosition;
    private int mStartYPosition;
    private float mStartToolbarPosition;
    private int mFinalYPosition;
    private int finalHeight;
    private int mStartHeight;
    private int mFinalXPosition;

    public TitleImageViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();

        mFinalTitlePadding = context.getResources().getDimension(R.dimen.spacing_normal);
    }

    private void init() { bindDimensions(); }

    private void bindDimensions() {
        mImageMaxSize = mContext.getResources().getDimension(R.dimen.image_width);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof Toolbar;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {
        maybeInitProperties(child, dependency);

        final int maxScrollDistance = (int) (mStartToolbarPosition - getStatusBarHeight());
        float expandedPercentageFactor = dependency.getY() / maxScrollDistance;

        float distanceYToSubtract = ((mStartYPosition - mFinalYPosition)
            * (1f - expandedPercentageFactor)) + (child.getHeight() / 2);

        float distanceXToSubtract = ((mStartXPosition - mFinalXPosition)
                * (1f - expandedPercentageFactor)) + (child.getWidth() / 2);

        float heightToSubstract = ((mStartHeight - finalHeight) * (1f - expandedPercentageFactor));

        child.setY(mStartYPosition - distanceYToSubtract);
        child.setX(mStartXPosition - distanceXToSubtract);

        int proportionalImageSize = (int) (mImageMaxSize * (expandedPercentageFactor));

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.width = (int) (mStartHeight - heightToSubstract);
        lp.height = (int) (mStartHeight - heightToSubstract);
        child.setLayoutParams(lp);

        return true;
    }

    private void maybeInitProperties(ImageView child, View dependency) {
        if (mStartYPosition == 0) {
            float y = dependency.getY();
            mStartYPosition = (int) (y);
        }

        if (mFinalYPosition == 0) {
            mFinalYPosition = (dependency.getHeight() / 2);
        }

        if (mStartHeight == 0) {
            mStartHeight = child.getHeight();
        }

        if (finalHeight == 0) {
            finalHeight = mContext.getResources().getDimensionPixelOffset(R.dimen.image_final_width);
        }

        if (mStartXPosition == 0) {
            mStartXPosition = (int) (child.getX() + (child.getWidth() / 2));
        }

        if (mFinalXPosition == 0) {
            mFinalXPosition = mContext.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material) +
                    (finalHeight / 2);
        }

        if (mStartToolbarPosition == 0) {
            mStartToolbarPosition = dependency.getY() + (dependency.getHeight() / 2);
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
