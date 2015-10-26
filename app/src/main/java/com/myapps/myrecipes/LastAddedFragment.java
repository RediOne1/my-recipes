package com.myapps.myrecipes;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;


/**
 * A simple {@link Fragment} subclass.
 */
public class LastAddedFragment extends MyAllRecipesFragment {



	@NonNull
	public static LastAddedFragment newInstance() {
		return new LastAddedFragment();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		query.orderByDescending("createdAt");
	}
}
