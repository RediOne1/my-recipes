package com.myapps.myrecipes.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Author:  Adrian
 * Index:   204423
 * Date:    24.08.2015
 */

@ParseClassName("Recipe")
public class Recipe extends ParseObject {

	public static ParseQuery<Recipe> getQuery() {
		return ParseQuery.getQuery(Recipe.class);
	}

	public void setAuthor(ParseUser currentUser) {
		put("author", currentUser);
	}

	public ParseFile getPhotoFile() {
		return getParseFile("photo");
	}

	public void setPhotoFile(ParseFile photoFile) {
		put("photo", photoFile);
	}

	public String getTitle() {
		return getString("name");
	}

	public void setName(String name) {
		put("name", name);
	}

	public String getCategory() {
		return getString("category");
	}

	public void setCategory(String s) {
		put("category", s);
	}

	public String getDifficulty() {
		return getString("difficulty");
	}

	public void setDifficulty(String s) {
		put("difficulty", s);
	}

	public void setIngredients(String s) {
		put("ingredients", s);
	}


	public String getIngredientJSON() {
		return getString("ingredients");
	}

	public String getPhotoUrl() {
		return getString("photoUrl");
	}

	public void setPhotoUrl(String url) {
		put("photoUrl", url);
	}

	public String getDescription() {
		return getString("description");
	}

	public void setDescription(String s) {
		put("description", s);
	}
}
