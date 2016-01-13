package com.myapps.myrecipes;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipesInCategoryFragment extends MyAllRecipesFragment {


	@NonNull
	public static RecipesInCategoryFragment newInstance(String categoryName) {
		Bundle bundle = new Bundle();
		bundle.putString("categoryName", categoryName);

		RecipesInCategoryFragment recipesInCategoryFragment = new RecipesInCategoryFragment();
		recipesInCategoryFragment.setArguments(bundle);
		return recipesInCategoryFragment;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		String categoryName = getArguments().getString("categoryName");
		query.whereEqualTo("category", categoryName);
	}
}
