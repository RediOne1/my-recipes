package com.myapps.myrecipes;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Author:  Adrian Kuta
 * Date:    15.11.2015
 * Index:   204423
 */
public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
	public final static String AUTHORITY = "com.myapps.myrecipes.MySuggestionProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;

	public MySuggestionProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}