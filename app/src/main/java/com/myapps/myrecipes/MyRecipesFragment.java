package com.myapps.myrecipes;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.myapps.myrecipes.parseobjects.Recipe;
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
		query.fromLocalDatastore();
		registerForContextMenu(gridView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {

			case R.id.edit_recipe:
				Recipe recipe = recipeList.get(info.position);
				editRecipe(recipe);
				return true;
			case R.id.delete_recipe:
				Recipe recipe2 = recipeList.remove(info.position);
				recipe2.deleteEventually();
				gridAdapter.notifyDataSetChanged();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void editRecipe(Recipe recipe) {
		Intent intent = new Intent(getContext(), AddRecipeActivity.class);
		intent.putExtra(AddRecipeActivity.ID, recipe.getObjectId());
		startActivity(intent);
	}
}
