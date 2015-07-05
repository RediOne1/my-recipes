package com.myapps.myrecipes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class TopRatedRecipesFragment extends Fragment {


	public TopRatedRecipesFragment() {
		// Required empty public constructor
	}

	public static TopRatedRecipesFragment newInstance() {
		return new TopRatedRecipesFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_top_rated_recipes, container, false);
	}


}
