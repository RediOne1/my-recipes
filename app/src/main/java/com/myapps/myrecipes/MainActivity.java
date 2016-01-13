package com.myapps.myrecipes;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.myapps.myrecipes.displayingbitmaps.ImageCache;
import com.myapps.myrecipes.displayingbitmaps.ImageFetcher;
import com.myapps.myrecipes.parseobjects.MyParseUser;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private static final String IMAGE_CACHE_DIR = "images";
	private ProfilePictureView profilePictureView;
	private TextView profileName;
	private CallbackManager callbackManager;
	private ImageFetcher imageFetcher;
	private boolean onLogOutClick;

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

		callbackManager = CallbackManager.Factory.create();

		NavigationView navigation = (NavigationView) findViewById(R.id.nav_view);
		navigation.setNavigationItemSelectedListener(this);
		navigation.setCheckedItem(R.id.home_page_menuitem);
		onNavigationItemSelected(navigation.getMenu().findItem(R.id.home_page_menuitem));


		View navigationView = navigation.inflateHeaderView(R.layout.nav_header_main);
		profilePictureView = (ProfilePictureView) navigationView.findViewById(R.id.profile_picture);
		profileName = (TextView) navigationView.findViewById(R.id.profile_name);

		LoginButton loginButton = (LoginButton) navigationView.findViewById(R.id.login_button);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Profile.getCurrentProfile() != null)
					onLogOutClick = true;
			}
		});
		loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

			private ProfileTracker mProfileTracker;

			@Override
			public void onSuccess(LoginResult loginResult) {
				if (Profile.getCurrentProfile() == null) {
					mProfileTracker = new ProfileTracker() {
						@Override
						protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
							linkParseUserIfNeeded(AccessToken.getCurrentAccessToken());
							updateParseUser();
							updateUI();
						}
					};
					mProfileTracker.startTracking();
				} else {
					linkParseUserIfNeeded(AccessToken.getCurrentAccessToken());
					updateParseUser();
					updateUI();
				}
			}

			@Override
			public void onCancel() {

			}

			@Override
			public void onError(FacebookException error) {

			}
		});

		startTrackingAccessToken();
		updateUI();
	}

	private void startTrackingAccessToken() {
		AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
			@Override
			protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
				if (onLogOutClick && currentAccessToken == null) {
					ParseUser.logOut();
					ParseAnonymousUtils.logInInBackground();
					updateUI();
					onLogOutClick = false;
				}
			}
		};
		if (!accessTokenTracker.isTracking())
			accessTokenTracker.startTracking();
	}

	private void linkParseUserIfNeeded(AccessToken currentAccessToken) {
		ParseFacebookUtils.logInInBackground(currentAccessToken);
	}

	private void updateParseUser() {
		Profile profile = Profile.getCurrentProfile();
		MyParseUser parseUser = MyParseUser.getCurrentUser();
		parseUser.setFirstName(profile.getFirstName());
		parseUser.setMiddleName(profile.getMiddleName());
		parseUser.setLastName(profile.getLastName());
		parseUser.saveEventually();
	}

	private void updateUI() {
		MyParseUser parseUser = MyParseUser.getCurrentUser();
		String firstName = parseUser.getFirstName();
		String lastName = parseUser.getLastName();

		if (firstName == null)
			firstName = "";
		lastName = lastName == null ? "" : lastName.substring(0, 1) + ".";

		String profileNameString = firstName + " " + lastName;
		profileName.setText(profileNameString);

		Profile profile = Profile.getCurrentProfile();
		if (profile != null)
			profilePictureView.setProfileId(profile.getId());
		else
			profilePictureView.setProfileId(null);
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

		imageFetcher = new ImageFetcher(this, longest);
		imageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
	}

	public ImageFetcher getImageFetcher() {
		return imageFetcher;
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
		getMenuInflater().inflate(R.menu.menu_main, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchItem = menu.findItem(R.id.action_search);

		SearchView searchView = null;
		if (searchItem != null) {
			searchView = (SearchView) searchItem.getActionView();
		}
		if (searchView != null) {
			searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	public void showFragment(Fragment fragment) {
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
		CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

		int id = item.getItemId();

		if (id == R.id.home_page_menuitem) {
			showFragment(LastAddedFragment.newInstance());
			collapsingToolbarLayout.setTitle(getString(R.string.home_page));
		} else if (id == R.id.favourite_recipes_menuitem) {
			showFragment(FavouriteRecipesFragment.newInstance());
			collapsingToolbarLayout.setTitle(getString(R.string.favourite_recipes));
		} else if (id == R.id.my_recipes_menuitem) {
			showFragment(MyRecipesFragment.newInstance());
			collapsingToolbarLayout.setTitle(getString(R.string.my_recipes));
		} else if (id == R.id.add_recipe_menuitem) {
			Intent i = new Intent(this, AddRecipeActivity.class);
			startActivity(i);
		} else if (id == R.id.top_rated_menuitem) {
			showFragment(TopRatedRecipesFragment.newInstance());
			collapsingToolbarLayout.setTitle(getString(R.string.top_rated));
		} else if (id == R.id.last_added_menuitem) {
			showFragment(LastAddedFragment.newInstance());
			collapsingToolbarLayout.setTitle(getString(R.string.last_added));
		} else if (id == R.id.categories_menuitem) {
			AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
			appBarLayout.setExpanded(false, true);
			showFragment(CategoryFrgment.newInstance());
			collapsingToolbarLayout.setTitle(getString(R.string.categories));
		} else if (id == R.id.settings_menuitem) {
			startActivity(new Intent(this, SettingsActivity.class));
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
