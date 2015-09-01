package com.myapps.myrecipes;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.myapps.myrecipes.parseobjects.Recipe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment {


	private ObservableGridView observableGridView;
	private View toolbar;
	private GridAdapter gridAdapter;
	private List<Recipe> recipeList;
	private ParseQuery<Recipe> query;

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
		observableGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Recipe recipe = recipeList.get(position);
				Intent intent = new Intent(getActivity(), RecipeActivity.class);
				intent.putExtra(RecipeActivity.RECIPE_ID, recipe.getObjectId());
				intent.putExtra(RecipeActivity.RECIPE_NAME, recipe.getName());
				intent.putExtra(RecipeActivity.CATEGORY, recipe.getCategory());
				intent.putExtra(RecipeActivity.DIFFICULTY, recipe.getDifficulty());
				intent.putExtra(RecipeActivity.INGREDIENTS, recipe.getIngredientJSON());
				intent.putExtra(RecipeActivity.PHOTO_URL, recipe.getPhotoUrl());
				startActivity(intent);
			}
		});
		recipeList = new ArrayList<>();
		gridAdapter = new GridAdapter(getActivity(), recipeList);
		observableGridView.setAdapter(gridAdapter);
		query = Recipe.getQuery();
		query.findInBackground(new FindCallback<Recipe>() {
			@Override
			public void done(List<Recipe> list, ParseException e) {
				if (e != null) {
					Toast.makeText(getActivity(),
							"Error saving: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
					query.fromLocalDatastore();
					query.findInBackground(new FindCallback<Recipe>() {
						@Override
						public void done(List<Recipe> list, ParseException e) {
							if (e == null) {
								recipeList.clear();
								recipeList.addAll(list);
								gridAdapter.notifyDataSetChanged();
							}
						}
					});
				} else {
					recipeList.clear();
					recipeList.addAll(list);
					gridAdapter.notifyDataSetChanged();
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
