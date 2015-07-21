package com.myapps.myrecipes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.myapps.myrecipes.displayingbitmaps.ImageCache;
import com.myapps.myrecipes.displayingbitmaps.ImageFetcher;
import com.myapps.myrecipes.displayingbitmaps.ImageResizer;


public class NaviagtionDrawerActivity extends AppCompatActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	private static final String IMAGE_CACHE_DIR = "images";
	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private ImageFetcher imageFetcher;
	private ImageResizer imageResizer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setUpImageLoader();
		setContentView(R.layout.activity_naviagtion_drawer);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		onNavigationDrawerItemSelected(NavigationDrawerFragment.HOME_PAGE);

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

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		Fragment fragment = getFragment(position);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, fragment)
				.commit();
	}

	private Fragment getFragment(int position) {
		switch (position) {
			case NavigationDrawerFragment.HOME_PAGE:
				return new HomePageFragment();
			case NavigationDrawerFragment.TOP_RATED:
				return TopRatedRecipesFragment.newInstance();
			case NavigationDrawerFragment.ADD_RECIPE:
				return new AddRecipeFragment();
			default:
				return TopRatedRecipesFragment.newInstance();
		}
	}

	@Override
	public void onBackPressed() {
		if (mNavigationDrawerFragment.isDrawerOpen()) {
			mNavigationDrawerFragment.closeDrawer();
			return;
		}
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.naviagtion_drawer, menu);
			return true;
		}
		return super.onCreateOptionsMenu(menu);
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

	public View getToolbarView() {
		return findViewById(R.id.toolbar);
	}
}
