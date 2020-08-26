package com.drplacid.topreddit.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class AnimationManager {

    public static final int ANIMATION_TIME = 400;
    public static boolean fullSizeImageShown = false;


    public static void verticalExpand(final FrameLayout expandableLayout, final CoordinatorLayout parent) {
        final int targetHeight = parent.getMeasuredHeight();

        expandableLayout.getLayoutParams().height = 1;
        expandableLayout.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                expandableLayout.getLayoutParams().height = interpolatedTime == 1
                        ? FrameLayout.LayoutParams.MATCH_PARENT
                        : (int) (targetHeight * interpolatedTime);
                expandableLayout.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration(ANIMATION_TIME);
        expandableLayout.startAnimation(a);
    }


    public static void verticalCollapse(final FrameLayout expandableLayout) {
        final int initialHeight = expandableLayout.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    expandableLayout.setVisibility(View.GONE);
                } else {
                    expandableLayout.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    expandableLayout.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration(ANIMATION_TIME);
        expandableLayout.startAnimation(a);
    }
}

