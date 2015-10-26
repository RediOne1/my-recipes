package com.myapps.myrecipes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;


/**
 * A simple {@link Fragment} subclass.
 */
public class TopRatedRecipesFragment extends MyAllRecipesFragment {



	public TopRatedRecipesFragment() {
		// Required empty public constructor
	}

	public static TopRatedRecipesFragment newInstance() {
		return new TopRatedRecipesFragment();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		query.orderByDescending("averageRate");
	}
}
