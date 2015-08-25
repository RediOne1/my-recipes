package com.myapps.myrecipes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.appevents.AppEventsLogger;
import com.myapps.myrecipes.parseobjects.Category;
import com.myapps.myrecipes.parseobjects.Ingredient;
import com.myapps.myrecipes.parseobjects.Recipe;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;


public class MainActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ParseObject.registerSubclass(Category.class);
		ParseObject.registerSubclass(Ingredient.class);
		ParseObject.registerSubclass(Recipe.class);
		Parse.enableLocalDatastore(this);
		Parse.initialize(this, "sOtPvBFE5R8JWOcfm2gPFAUlNQLjFgadZ9KiQJMj", "jI625qcX1LCCoKaKBSMJa9dNWGzfgFhZBu3Zw5p3");
		ParseFacebookUtils.initialize(this);
		findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this, NaviagtionDrawerActivity.class));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Logs 'install' and 'app activate' App Events.
		// Pozwala sprawdzić ile osób korzysta z mojej aplikacji
		AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Logs 'app deactivate' App Event.
		// Dzięki temu wiem jak długo ktoś korzystał z mojej aplikacji
		AppEventsLogger.deactivateApp(this);
	}
}
