package com.myapps.myrecipes.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * author:  Adrian Kuta
 * index:   204423
 * date:    03.08.15
 * <p/>
 * Obiekt dzięki ktoremu możemy w łatwy sposób dostać dane kategorii.
 */
@ParseClassName("Category")
public class Category extends ParseObject {

	/**
	 * Metoda która z obiketu {@link ParseObject} zwraca wartość pola odpowiadającego za nazwe kategorii.
	 *
	 * @return Zwraca nazwe kategorii.
	 */
	public String getName() {
		return getString("name");
	}

	/**
	 * Metoda która z obiketu {@link ParseObject} zwraca wartość pola odpowiadającego za zdjęcie kategorii.
	 *
	 * @return Zwraca zdjęcie kategorii.
	 */
	public String getIamgeUrl() {
		return getString("imageUrl");
	}

	public static ParseQuery<Category> getQuery() {
		return ParseQuery.getQuery(Category.class);
	}
}
