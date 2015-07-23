package com.myapps.myrecipes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
	private EditText title;


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

		title = (EditText) view.findViewById(R.id.add_title_editText);
		setupAddTitle(view);

		mParallaxImageHeight = getResources().getDimensionPixelSize(
				R.dimen.parallax_image_height);

	}

	private void setupAddTitle(View rootView) {
		final TextView counter = (TextView) rootView.findViewById(R.id.add_title_counter);
		TextView errorText = (TextView) rootView.findViewById(R.id.add_title_errorText);
		final int titleMaxLength = getResources().getInteger(R.integer.title_max_length);
		final String counterPattern = getResources().getString(R.string.character_counter);
		counter.setText(String.format(counterPattern, 0, titleMaxLength));
		title.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				int length = editable.length();
				counter.setText(String.format(counterPattern, length, titleMaxLength));
				if (length > titleMaxLength)
					counter.setTextColor(getResources().getColor(R.color.text_field_error));
				else
					counter.setTextColor(getResources().getColor(R.color.helper_text_light));
			}
		});
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
