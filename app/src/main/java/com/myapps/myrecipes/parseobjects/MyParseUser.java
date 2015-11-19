package com.myapps.myrecipes.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseUser;

/**
 * Author:  Adrian Kuta
 * Date:    19.11.2015
 * Index:   204423
 */

@ParseClassName("_User")
public class MyParseUser extends ParseUser {

	public static MyParseUser getCurrentUser() {
		return (MyParseUser) ParseUser.getCurrentUser();
	}

	public void setMiddleName(String middleName) {
		put("middleName", middleName);
	}

	public String getFirstName() {
		return getString("firstName");
	}

	public void setFirstName(String firstName) {
		put("firstName", firstName);
	}

	public String getLastName() {
		return getString("lastName");
	}

	public void setLastName(String lastName) {
		put("lastName", lastName);
	}
}
