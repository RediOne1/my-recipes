package com.myapps.myrecipes;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
public class LastAddedFragment extends Fragment {


	private GridAdapter gridAdapter;
	private List<Recipe> recipeList;
	private ParseQuery<Recipe> query;

	@NonNull
	public static LastAddedFragment newInstance() {
		return new LastAddedFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_last_added, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		GridView observableGridView = (GridView) view.findViewById(R.id.last_added_gridView);
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
		query.orderByDescending("createdAt");
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
}