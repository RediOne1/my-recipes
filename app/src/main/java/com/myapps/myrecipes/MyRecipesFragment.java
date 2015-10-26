package com.myapps.myrecipes;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyRecipesFragment extends MyAllRecipesFragment {

	public MyRecipesFragment() {
		// Required empty public constructor
	}

	@NonNull
	public static MyRecipesFragment newInstance() {
		return new MyRecipesFragment();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ParseUser parseUser = ParseUser.getCurrentUser();
		if (parseUser == null)
			return;
		query.whereEqualTo("author", parseUser);
	}
}
