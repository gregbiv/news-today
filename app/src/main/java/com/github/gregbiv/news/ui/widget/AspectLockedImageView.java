
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.widget;

import com.github.gregbiv.news.R;

import android.content.Context;
import android.content.res.TypedArray;

import android.util.AttributeSet;

import android.view.View;

import android.widget.ImageView;

public final class AspectLockedImageView extends ImageView {
    private float             aspectRatio       = 0;
    private AspectRatioSource aspectRatioSource = null;

    public AspectLockedImageView(Context context) {
        super(context);
    }

    public AspectLockedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectLockedImageView);

        aspectRatio = a.getFloat(R.styleable.AspectLockedImageView_imageAspectRatio, 0);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        float localRatio = aspectRatio;

        if ((localRatio == 0.0) && (aspectRatioSource != null) && (aspectRatioSource.getHeight() > 0)) {
            localRatio = (float) aspectRatioSource.getWidth() / (float) aspectRatioSource.getHeight();
        }

        if (localRatio == 0.0) {
            super.onMeasure(widthSpec, heightSpec);
        } else {
            int lockedWidth  = MeasureSpec.getSize(widthSpec);
            int lockedHeight = MeasureSpec.getSize(heightSpec);

            if ((lockedWidth == 0) && (lockedHeight == 0)) {
                throw new IllegalArgumentException(
                    "Both width and height cannot be zero -- watch out for scrollable containers");
            }

            // Get the padding of the border background.
            int hPadding = getPaddingLeft() + getPaddingRight();
            int vPadding = getPaddingTop() + getPaddingBottom();

            // Resize the preview frame with correct aspect ratio.
            lockedWidth  -= hPadding;
            lockedHeight -= vPadding;

            if ((lockedHeight > 0) && (lockedWidth > lockedHeight * localRatio)) {
                lockedWidth = (int) (lockedHeight * localRatio + .5);
            } else {
                lockedHeight = (int) (lockedWidth / localRatio + .5);
            }

            // Add the padding of the border.
            lockedWidth  += hPadding;
            lockedHeight += vPadding;

            // Ask children to follow the new preview dimension.
            super.onMeasure(MeasureSpec.makeMeasureSpec(lockedWidth, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(lockedHeight, MeasureSpec.EXACTLY));
        }
    }

    // from com.android.camera.PreviewFrameLayout, with slight
    // modifications
    public void setAspectRatio(float aspectRatio) {
        if (aspectRatio <= 0.0) {
            throw new IllegalArgumentException("aspect ratio must be positive");
        }

        if (this.aspectRatio != aspectRatio) {
            this.aspectRatio = aspectRatio;
            requestLayout();
        }
    }

    public void setAspectRatioSource(AspectRatioSource aspectRatioSource) {
        this.aspectRatioSource = aspectRatioSource;
    }

    public void setAspectRatioSource(View v) {
        this.aspectRatioSource = new ViewAspectRatioSource(v);
    }

    public interface AspectRatioSource {
        int getHeight();

        int getWidth();
    }


    private static class ViewAspectRatioSource implements AspectRatioSource {
        private View v = null;

        ViewAspectRatioSource(View v) {
            this.v = v;
        }

        @Override
        public int getHeight() {
            return (v.getHeight());
        }

        @Override
        public int getWidth() {
            return (v.getWidth());
        }
    }
}
