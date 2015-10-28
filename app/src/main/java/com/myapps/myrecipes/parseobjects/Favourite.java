package com.myapps.myrecipes.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Author:  Adrian Kuta
 * Date:    28.10.2015
 * Index:   204423
 */

@ParseClassName("Favourites")
public class Favourite extends ParseObject {

	public static ParseQuery<Favourite> getQuery() {
		return ParseQuery.getQuery(Favourite.class);
	}

	public void setUser(ParseUser parseUser) {
		put("user", parseUser);
	}

	public void setRecipe(ParseObject recipe) {
		put("recipe", recipe);
	}

	public void setRecipe(String recipe) {
		put("recipe", ParseObject.createWithoutData("Recipe", recipe));
	}
}
