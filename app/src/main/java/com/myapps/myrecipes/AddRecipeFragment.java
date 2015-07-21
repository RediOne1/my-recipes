package com.myapps.myrecipes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddRecipeFragment extends Fragment implements ObservableScrollViewCallbacks {


	private ImageView mImageView;
	private View mToolbarView;
	private ObservableScrollView mScrollView;
	private int mParallaxImageHeight;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_add_recipe, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mImageView = (ImageView) view.findViewById(R.id.image);
		mToolbarView = ((NaviagtionDrawerActivity) getActivity()).getToolbarView();
		mToolbarView.setBackgroundColor(
				ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));

		mScrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
		mScrollView.setScrollViewCallbacks(this);

		mParallaxImageHeight = getResources().getDimensionPixelSize(
				R.dimen.parallax_image_height);

	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
		mImageView.setTranslationY(scrollY / 2);
		int baseColor = getResources().getColor(R.color.primary);
		float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
		mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));

	}

	@Override
	public void onDownMotionEvent() {

	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {

	}

	@Override
	public void onDetach() {
		super.onDetach();
		mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(1, getResources().getColor(R.color.primary)));
	}
}
