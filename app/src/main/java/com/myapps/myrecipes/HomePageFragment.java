package com.myapps.myrecipes;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment {


	private ObservableGridView observableGridView;
	private View toolbar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_home_page, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		toolbar = ((NaviagtionDrawerActivity) getActivity()).getToolbarView();

		observableGridView = (ObservableGridView) view.findViewById(R.id.home_page_gridView);
		observableGridView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
			@Override
			public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

			}

			@Override
			public void onDownMotionEvent() {

			}

			@Override
			public void onUpOrCancelMotionEvent(ScrollState scrollState) {
				if (scrollState == ScrollState.UP) {
					if (toolbarIsShown()) {
						hideToolbar();
					}
				} else if (scrollState == ScrollState.DOWN) {
					if (toolbarIsHidden()) {
						showToolbar();
					}
				}
			}
		});

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Recipe");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				try {
					observableGridView.setAdapter(new GridAdapter(getActivity(), list));
				}catch (Exception ignored){

				}
			}
		});
	}

	private boolean toolbarIsShown() {

		// Toolbar is 0 in Y-axis, so we can say it's shown.
		return toolbar.getTranslationY() == 0;
	}

	private boolean toolbarIsHidden() {
		// Toolbar is outside of the screen and absolute Y matches the height of it.
		// So we can say it's hidden.
		return toolbar.getTranslationY() == -toolbar.getHeight();
	}

	private void showToolbar() {
		moveToolbar(0);
	}

	private void hideToolbar() {
		moveToolbar(-toolbar.getHeight());
	}

	private void moveToolbar(int toTranslationY) {
		// Check the current translationY
		if (toolbar.getTranslationY() == toTranslationY) {
			return;
		}
		ValueAnimator animator = ValueAnimator.ofFloat(toolbar.getTranslationY(), toTranslationY).setDuration(200);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float translationY = (float) animation.getAnimatedValue();
				toolbar.setTranslationY(translationY);
				observableGridView.setTranslationY(translationY);
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) observableGridView.getLayoutParams();
				lp.height = (int) -translationY + getScreenHeight() - lp.topMargin;
				observableGridView.requestLayout();
			}
		});
		animator.start();
	}

	private int getScreenHeight() {
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.y;
	}
}
