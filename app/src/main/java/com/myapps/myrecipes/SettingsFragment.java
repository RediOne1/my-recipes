package com.myapps.myrecipes;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.myapps.myrecipes.R;
import com.myapps.myrecipes.parseobjects.Recipe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Author:  Adrian
 * Index:   204423
 * Date:    01.09.2015
 */
public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		findPreference(getString(R.string.key_clear_local_data_store)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				ParseQuery<Recipe> query = Recipe.getQuery();
				query.fromLocalDatastore();
				query.findInBackground(new FindCallback<Recipe>() {
					@Override
					public void done(List<Recipe> list, ParseException e) {
						try {
							ParseObject.unpinAll(list);
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
				});
				return true;
			}
		});
	}
}
