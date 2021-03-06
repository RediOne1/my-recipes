package com.myapps.myrecipes.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Author:  Adrian
 * Index:   204423
 * Date:    04.09.2015
 */

@ParseClassName("Rating")
public class Rating extends ParseObject {

	public static ParseQuery<Rating> getQuery() {
		return ParseQuery.getQuery(Rating.class);
	}

	public void setUser(ParseUser user) {
		put("user", user);
	}

	public void setRecipe(Recipe recipe) {
		put("recipe", recipe);
	}

	public void setRecipe(String recipeId) {
		put("recipe", ParseObject.createWithoutData("Recipe", recipeId));
	}

	public float getRating() {
		return getNumber("rate").floatValue();
	}

	public void setRating(float rating) {
		put("rate", rating);
	}
}
