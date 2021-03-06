package com.myapps.myrecipes;

import android.app.Application;
import android.preference.PreferenceManager;

import com.facebook.FacebookSdk;
import com.myapps.myrecipes.parseobjects.Category;
import com.myapps.myrecipes.parseobjects.Favourite;
import com.myapps.myrecipes.parseobjects.Ingredient;
import com.myapps.myrecipes.parseobjects.MyParseUser;
import com.myapps.myrecipes.parseobjects.Rating;
import com.myapps.myrecipes.parseobjects.Recipe;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Author:  Adrian
 * Index:   204423
 * Date:    25.09.2015
 */
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ParseUser.enableAutomaticUser();
		ParseObject.registerSubclass(Category.class);
		ParseObject.registerSubclass(Ingredient.class);
		ParseObject.registerSubclass(Recipe.class);
		ParseObject.registerSubclass(Rating.class);
		ParseObject.registerSubclass(Favourite.class);
		ParseUser.registerSubclass(MyParseUser.class);
		Parse.enableLocalDatastore(this);
		Parse.initialize(this, "sOtPvBFE5R8JWOcfm2gPFAUlNQLjFgadZ9KiQJMj", "jI625qcX1LCCoKaKBSMJa9dNWGzfgFhZBu3Zw5p3");
		ParseInstallation.getCurrentInstallation().saveInBackground();
		ParseFacebookUtils.initialize(this);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		FacebookSdk.sdkInitialize(this);
	}
}
