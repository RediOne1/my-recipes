package com.myapps.myrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.myapps.myrecipes.displayingbitmaps.ImageCache;
import com.myapps.myrecipes.displayingbitmaps.ImageFetcher;
import com.myapps.myrecipes.displayingbitmaps.ImageResizer;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private static final String IMAGE_CACHE_DIR = "images";
	private LoginButton loginButton;
	private ProfilePictureView profilePictureView;
	private TextView profileName;
	private TextView profileMail;
	private CallbackManager callbackManager;
	private ImageResizer imageResizer;
	private ImageFetcher imageFetcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setUpImageLoader();

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigation = (NavigationView) findViewById(R.id.nav_view);
		navigation.setNavigationItemSelectedListener(this);
		navigation.setCheckedItem(R.id.home_page_menuitem);
		View navigationView = navigation.inflateHeaderView(R.layout.nav_header_main);
		loginButton = (LoginButton) navigationView.findViewById(R.id.login_button);
		profilePictureView = (ProfilePictureView) navigationView.findViewById(R.id.profile_picture);
		profileName = (TextView) navigationView.findViewById(R.id.profile_name);
		profileMail = (TextView) navigationView.findViewById(R.id.profile_mail);
		setupLogin();
		updateUI();
	}

	private void setUpImageLoader() {
		// Fetch screen height and width, to use as our max size when loading images as this
		// activity runs full screen
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		final int height = displayMetrics.heightPixels;
		final int width = displayMetrics.widthPixels;

		// For this sample we'll use half of the longest width to resize our images. As the
		// image scaling ensures the image is larger than this, we should be left with a
		// resolution that is appropriate for both portrait and landscape. For best image quality
		// we shouldn't divide by 2, but this will use more memory and require a larger memory
		// cache.
		final int longest = (height > width ? height : width) / 2;

		ImageCache.ImageCacheParams cacheParams =
				new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

		imageResizer = new ImageResizer(this, longest);
		imageResizer.addImageCache(getSupportFragmentManager(), cacheParams);

		imageFetcher = new ImageFetcher(this, longest);
		imageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
	}

	public ImageFetcher getImageFetcher() {
		return imageFetcher;
	}

	public ImageResizer getImageResizer() {
		return imageResizer;
	}

	private void setupLogin() {
		callbackManager = CallbackManager.Factory.create();
		loginButton.setReadPermissions("user_friends");
		AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
			@Override
			protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
				if (currentAccessToken == null) {
					ParseUser.logOut();
					updateUI();
				} else
					ParseFacebookUtils.logInInBackground(currentAccessToken, new LogInCallback() {
						@Override
						public void done(ParseUser parseUser, ParseException e) {
							updateUI();

						}
					});

			}
		};
		accessTokenTracker.startTracking();
	}

	private void updateUI() {
		Profile profile = Profile.getCurrentProfile();
		if (profile != null) {
			profilePictureView.setVisibility(View.VISIBLE);
			profilePictureView.setProfileId(profile.getId());
			profileName.setText(profile.getName());
			GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
				@Override
				public void onCompleted(JSONObject object, GraphResponse response) {
					if (object != null)
						try {
							if (object.has("email")) {
								String mail = object.getString("email");
								profileMail.setText(mail);
								ParseUser user = ParseUser.getCurrentUser();
								user.setEmail(mail);
								user.saveEventually();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
				}
			});
			request.executeAsync();
		} else {
			profilePictureView.setVisibility(View.INVISIBLE);
			profilePictureView.setProfileId(null);
			profileName.setText("");
			profileMail.setText("");
		}
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main2, menu);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	private void showFragment(Fragment fragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container, fragment)
				.commit();
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.home_page_menuitem) {
			showFragment(LastAddedFragment.newInstance());
		} else if (id == R.id.favourite_recipes_menuitem) {

		} else if (id == R.id.my_recipes_menuitem) {
			showFragment(MyRecipesFragment.newInstance());
		} else if (id == R.id.add_recipe_menuitem) {

		} else if (id == R.id.top_rated_menuitem) {

		} else if (id == R.id.last_added_menuitem) {

		} else if (id == R.id.categories_menuitem) {

		} else if (id == R.id.settings_menuitem) {

		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
