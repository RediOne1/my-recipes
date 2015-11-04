package com.myapps.myrecipes;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.myapps.myrecipes.parseobjects.Recipe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAllRecipesFragment extends Fragment {


	protected ParseQuery<Recipe> query;
	protected RecyclerView recyclerView;
	protected List<Recipe> recipeList;
	protected RecipeAdapter adapter;

	public MyAllRecipesFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_my_all_recipes, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		recipeList = new ArrayList<>();

		recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
		RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.grid_column_count), GridLayoutManager.VERTICAL, false);
		adapter = new RecipeAdapter(recipeList);
		adapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Recipe recipe = recipeList.get(position);
				Intent intent = new Intent(getActivity(), RecipeActivity.class);
				intent.putExtra(RecipeActivity.RECIPE_ID, recipe.getObjectId());
				intent.putExtra(RecipeActivity.RECIPE_NAME, recipe.getTitle());
				intent.putExtra(RecipeActivity.CATEGORY, recipe.getCategory());
				intent.putExtra(RecipeActivity.DIFFICULTY, recipe.getDifficulty());
				intent.putExtra(RecipeActivity.INGREDIENTS, recipe.getIngredientJSON());
				intent.putExtra(RecipeActivity.PHOTO_URL, recipe.getPhotoUrl());
				intent.putExtra(RecipeActivity.DESCRIPTION, recipe.getDescription());
				startActivity(intent);
			}
		});

		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(adapter);
		query = Recipe.getQuery();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateRecipes();
	}

	private void updateRecipes() {
		query.findInBackground(new FindCallback<Recipe>() {
			@Override
			public void done(List<Recipe> list, ParseException e) {
				if (e != null) {
					Toast.makeText(getActivity(),
							"Error saving: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				} else {
					for (int i = 0; i < recipeList.size(); i++) {
						Recipe recipe = recipeList.get(i);
						if (!list.contains(recipe)) {
							recipeList.remove(i);
							adapter.notifyItemRemoved(i);
							i--;
						}
					}

					for (int i = 0; i < list.size(); i++) {
						Recipe recipe = list.get(i);
						if (!recipeList.contains(recipe)) {
							recipeList.add(i, recipe);
							adapter.notifyItemInserted(i);
						} else {
							int oldPosition = recipeList.indexOf(recipe);
							recipeList.add(i, recipeList.remove(oldPosition));
							adapter.notifyItemMoved(oldPosition, i);
						}
					}
				}
			}
		});
	}
}
