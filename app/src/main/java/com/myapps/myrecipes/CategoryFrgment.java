package com.myapps.myrecipes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFrgment extends Fragment implements ListView.OnItemClickListener {


	private String[] categoryList;

	public CategoryFrgment() {
		// Required empty public constructor
	}

	public static Fragment newInstance() {
		return new CategoryFrgment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_category_frgment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		categoryList = getResources().getStringArray(R.array.categories);
		ListView listView = (ListView) view.findViewById(R.id.listView);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, categoryList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String category = categoryList[position];
		MainActivity mainActivity = (MainActivity) getActivity();
		mainActivity.showFragment(RecipesInCategoryFragment.newInstance(category));
	}
}
