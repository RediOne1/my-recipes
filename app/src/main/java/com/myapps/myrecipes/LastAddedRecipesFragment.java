package com.myapps.myrecipes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LastAddedRecipesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LastAddedRecipesFragment extends Fragment {
	public LastAddedRecipesFragment() {
		// Required empty public constructor
	}

	// TODO: Rename and change types and number of parameters
	public static LastAddedRecipesFragment newInstance() {
		return new LastAddedRecipesFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_last_added_recipes, container, false);
	}


}
