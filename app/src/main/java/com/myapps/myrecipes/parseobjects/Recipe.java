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

	public String getName() {
		return getString("name");
	}

	public void setName(String name) {
		put("name", name);
	}

	public void setCategory(String s) {
		put("category", s);
	}

	public void setDifficulty(String s) {
		put("difficulty", s);
	}
}
