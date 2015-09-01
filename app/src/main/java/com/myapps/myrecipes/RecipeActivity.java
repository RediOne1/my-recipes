package com.myapps.myrecipes;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class RecipeActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

	private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

	private int mFlexibleSpaceImageHeight;
	private int mFlexibleSpaceShowFabOffset;
	private int mFabMargin;
	private View mImageView;
	private View mOverlayView;
	private ObservableScrollView mScrollView;
	private TextView mTitleView;
	private View mFab;
	private boolean mFabIsShown;
	private int mActionBarSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);

		mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
		mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
		mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
		mActionBarSize = getActionBarSize();

		mImageView = findViewById(R.id.image);
		mOverlayView = findViewById(R.id.overlay);
		mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
		mScrollView.setScrollViewCallbacks(this);
		mTitleView = (TextView) findViewById(R.id.title);
		mFab = findViewById(R.id.fab);
		mFab.setScaleX(0);
		mFab.setScaleY(0);
	}

	protected int getActionBarSize() {
		TypedValue typedValue = new TypedValue();
		int[] textSizeAttr = new int[]{R.attr.actionBarSize};
		int indexOfAttrTextSize = 0;
		TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
		int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
		a.recycle();
		return actionBarSize;
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
		float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
		int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
		mOverlayView.setTranslationY(ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
		mImageView.setTranslationY(ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));
		mOverlayView.setAlpha(ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

		float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
		mTitleView.setPivotX(0);
		mTitleView.setPivotY(0);
		mTitleView.setScaleX(scale);
		mTitleView.setScaleY(scale);

		int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
		int titleTranslationY = maxTitleTranslationY - scrollY;
		mTitleView.setTranslationY(titleTranslationY);


		int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
		float fabTranslationY = ScrollUtils.getFloat(
				-scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
				mActionBarSize - mFab.getHeight() / 2,
				maxFabTranslationY);
		mFab.setTranslationX(mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
		mFab.setTranslationY(fabTranslationY);

		if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
			hideFab();
		} else {
			showFab();
		}
	}

	private void showFab() {
		if (!mFabIsShown) {
			ViewPropertyAnimator.animate(mFab).cancel();
			ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
			mFabIsShown = true;
		}
	}

	private void hideFab() {
		if (mFabIsShown) {
			ViewPropertyAnimator.animate(mFab).cancel();
			ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
			mFabIsShown = false;
		}
	}

	@Override
	public void onDownMotionEvent() {

	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {

	}
}
