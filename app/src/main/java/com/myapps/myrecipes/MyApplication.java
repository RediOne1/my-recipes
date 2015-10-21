package com.myapps.myrecipes;

import android.app.Application;
import android.preference.PreferenceManager;

import com.facebook.FacebookSdk;
import com.myapps.myrecipes.parseobjects.Category;
import com.myapps.myrecipes.parseobjects.Ingredient;
import com.myapps.myrecipes.parseobjects.Rating;
import com.myapps.myrecipes.parseobjects.Recipe;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

/**
 * Author:  Adrian
 * Index:   204423
 * Date:    25.09.2015
 */
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ParseObject.registerSubclass(Category.class);
		ParseObject.registerSubclass(Ingredient.class);
		ParseObject.registerSubclass(Recipe.class);
		ParseObject.registerSubclass(Rating.class);
		Parse.enableLocalDatastore(this);
		Parse.initialize(this, "sOtPvBFE5R8JWOcfm2gPFAUlNQLjFgadZ9KiQJMj", "jI625qcX1LCCoKaKBSMJa9dNWGzfgFhZBu3Zw5p3");
		ParseFacebookUtils.initialize(this);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		FacebookSdk.sdkInitialize(getApplicationContext());
	}
}
