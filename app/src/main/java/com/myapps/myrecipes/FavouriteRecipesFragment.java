package com.myapps.myrecipes;


import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.myapps.myrecipes.parseobjects.Favourite;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteRecipesFragment extends MyAllRecipesFragment {


	public FavouriteRecipesFragment() {
		// Required empty public constructor
	}

	public static FavouriteRecipesFragment newInstance() {
		return new FavouriteRecipesFragment();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ParseQuery<Favourite> query = Favourite.getQuery();
		ParseUser user = ParseUser.getCurrentUser();
		if (user == null)
			return;

		query.whereEqualTo("user", user);


		this.query.whereMatchesKeyInQuery("objectId", "recipeId", query);
	}
}
