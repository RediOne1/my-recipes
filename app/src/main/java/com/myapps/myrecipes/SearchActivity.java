package com.myapps.myrecipes;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.SearchRecentSuggestions;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

	private static final String[] SUGGESTIONS = {
			"Bauru", "Sao Paulo", "Rio de Janeiro",
			"Bahia", "Mato Grosso", "Minas Gerais",
			"Tocantins", "Rio Grande do Sul"
	};
	private SimpleCursorAdapter simpleCursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		final String[] from = new String[]{"cityName"};
		final int[] to = new int[]{android.R.id.text1};
		simpleCursorAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1,
				null,
				from,
				to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
					MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
			suggestions.saveRecentQuery(query, null);
			Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
			//doMySearch(query);
		}
	}

	@Override
	public boolean onSearchRequested() {
		//pauseSomeStuff();
		return super.onSearchRequested();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_search, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchItem = menu.findItem(R.id.action_search);

		SearchView searchView = null;
		if (searchItem != null) {
			searchView = (SearchView) searchItem.getActionView();
		}
		if (searchView != null) {
			searchView.setIconifiedByDefault(false);
			searchView.setSuggestionsAdapter(simpleCursorAdapter);
			searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
			searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}

				@Override
				public boolean onQueryTextChange(String newText) {
					populateAdapter(newText);
					return true;
				}
			});
		}

		return true;
	}

	private void populateAdapter(String query) {
		MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "cityName"});
		for (int i = 0; i < SUGGESTIONS.length; i++) {
			if (SUGGESTIONS[i].toLowerCase().startsWith(query.toLowerCase())) {
				c.addRow(new Object[]{i, SUGGESTIONS[i]});
			}
		}
		simpleCursorAdapter.changeCursor(c);
		simpleCursorAdapter.notifyDataSetChanged();
	}
}
