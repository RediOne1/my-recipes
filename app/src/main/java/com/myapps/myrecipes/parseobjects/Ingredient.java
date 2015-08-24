package com.myapps.myrecipes.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * author:  Adrian Kuta
 * index:   204423
 * date:    11.08.15
 */
@ParseClassName("Ingredient")
public class Ingredient extends ParseObject {

	public String getName() {
		return getString("name");
	}

	public static ParseQuery<Ingredient> getQuery() {
		return ParseQuery.getQuery(Ingredient.class);
	}

	@Override
	public String toString() {
		return getName();
	}
}
