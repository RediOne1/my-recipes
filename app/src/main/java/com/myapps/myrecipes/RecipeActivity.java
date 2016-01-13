package com.myapps.myrecipes;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.myapps.myrecipes.displayingbitmaps.ImageCache;
import com.myapps.myrecipes.displayingbitmaps.ImageFetcher;
import com.myapps.myrecipes.parseobjects.Favourite;
import com.myapps.myrecipes.parseobjects.Rating;
import com.myapps.myrecipes.parseobjects.Recipe;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeActivity extends AppCompatActivity {

	public static final String RECIPE_ID = "recipeId";
	public static final String RECIPE_NAME = "recipe_name";
	public static final String CATEGORY = "category";
	public static final String DIFFICULTY = "difficulty";
	public static final String INGREDIENTS = "ingredients";
	public static final String PHOTO_URL = "photo_url";
	public static final String DESCRIPTION = "description";

	private static final String IMAGE_CACHE_DIR = "images";
	private ImageView mImageView;
	private LinearLayout ingredientsLayout;
	private TextView category;
	private TextView difficulty;
	private TextView description;

	private ImageFetcher imageFetcher;

	private String recipeId;
	private Favourite favouriteRecipe;
	private FloatingActionButton fab;
	private Recipe recipe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);

		setSupportActionBar(((Toolbar) findViewById(R.id.toolbar)));

		mImageView = (ImageView) findViewById(R.id.image);
		ingredientsLayout = (LinearLayout) findViewById(R.id.recipe_activity_ingredients_layout);
		category = (TextView) findViewById(R.id.category_name);
		difficulty = (TextView) findViewById(R.id.difficulty_name);
		description = (TextView) findViewById(R.id.recipe_activity_description);

		setUpImageLoader();
		Bundle bundle = getIntent().getExtras();
		try {
			recipeId = bundle.getString(RECIPE_ID);
			setTitle(bundle.getString(RECIPE_NAME));
			category.setText(bundle.getString(CATEGORY));
			difficulty.setText(bundle.getString(DIFFICULTY));
			description.setText(bundle.getString(DESCRIPTION));
			displayIngredientsFromJSON(bundle.getString(INGREDIENTS));
			String photoUrl = bundle.getString(PHOTO_URL);
			if (photoUrl != null)
				imageFetcher.loadImage(photoUrl, mImageView);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (recipeId != null)
			fetchRecipe();
		else
			return;

		fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (favouriteRecipe != null) {
					favouriteRecipe.deleteEventually(new DeleteCallback() {
						@Override
						public void done(ParseException e) {
							if (e == null) {
								favouriteRecipe = null;
								updateFabButtton();
							} else
								Log.e("DEBUG_TAG", "Favourite", e);
						}

					});
				} else {
					favouriteRecipe = new Favourite();
					favouriteRecipe.setRecipe(recipeId);
					favouriteRecipe.setUser(ParseUser.getCurrentUser());
					favouriteRecipe.saveEventually(new SaveCallback() {
						@Override
						public void done(ParseException e) {
							if (e == null) {
								updateFabButtton();
							} else
								Log.e("DEBUG_TAG", "Favourite", e);
						}
					});
				}
			}
		});

		ParseQuery<Favourite> query = Favourite.getQuery();
		query.whereEqualTo("recipe", ParseObject.createWithoutData("Recipe", recipeId));
		query.whereEqualTo("user", ParseUser.getCurrentUser());
		query.getFirstInBackground(new GetCallback<Favourite>() {
			@Override
			public void done(Favourite favourite, ParseException e) {
				if (e == null)
					favouriteRecipe = favourite;
				updateFabButtton();
			}
		});
		final RatingBar ratingBar = (RatingBar) findViewById(R.id.recipeRatingBar);
		ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
				ParseQuery<Rating> query = Rating.getQuery();
				query.whereEqualTo("recipe", ParseObject.createWithoutData("Recipe", recipeId));
				query.whereEqualTo("user", ParseUser.getCurrentUser());
				query.getFirstInBackground(new GetCallback<Rating>() {
					@Override
					public void done(Rating ratingObject, ParseException e) {
						if (e == null) {
							ratingObject.setRating(rating);
							ratingObject.setRecipe(recipeId);
							ratingObject.saveEventually();
						} else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
							ratingObject = new Rating();
							ratingObject.setRating(rating);
							ratingObject.setRecipe(recipeId);
							ratingObject.setUser(ParseUser.getCurrentUser());
							ratingObject.saveEventually();
						} else
							Log.e("DEBUG_TAG", "Rating", e);
					}
				});
			}
		});

		ParseQuery<Rating> ratingParseQuery = Rating.getQuery();
		ratingParseQuery.whereEqualTo("recipe", ParseObject.createWithoutData("Recipe", recipeId));
		ratingParseQuery.whereEqualTo("user", ParseUser.getCurrentUser());
		ratingParseQuery.getFirstInBackground(new GetCallback<Rating>() {
			@Override
			public void done(Rating rating, ParseException e) {
				if (e == null) {
					ratingBar.setRating(rating.getRating());
				}
			}
		});

	}

	private void fetchRecipe() {
		ParseQuery<Recipe> query = Recipe.getQuery();
		query.getInBackground(recipeId, new GetCallback<Recipe>() {
			@Override
			public void done(Recipe recipe, ParseException e) {
				if (e == null) {
					RecipeActivity.this.recipe = recipe;
					setTitle(recipe.getTitle());
					category.setText(recipe.getCategory());
					difficulty.setText(recipe.getDifficulty());
					description.setText(recipe.getDescription());
					displayIngredientsFromJSON(recipe.getIngredientJSON());
					String photoUrl = recipe.getPhotoUrl();
					if (photoUrl != null)
						imageFetcher.loadImage(photoUrl, mImageView);
					invalidateOptionsMenu();
				}
			}
		});
	}

	private void updateFabButtton() {
		if (favouriteRecipe != null)
			fab.setImageResource(R.drawable.ic_favorite_black_24dp);
		else
			fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);
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

	private void displayIngredientsFromJSON(String jsonIngredients) {
		try {
			JSONObject jsonObject = new JSONObject(jsonIngredients);
			JSONArray jsonArray = jsonObject.getJSONArray("ingredients");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				String name = object.getString("name");
				String amount = object.getString("amount");
				addIngredient(name, amount);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void addIngredient(String name, String amount) {
		View itemView = getLayoutInflater().inflate(R.layout.single_ingredient_layout, ingredientsLayout, false);
		itemView.setVisibility(View.INVISIBLE);
		((TextView) itemView.findViewById(R.id.recipe_activity_ingredient_name)).setText(name);
		((TextView) itemView.findViewById(R.id.recipe_activity_ingredient_amount)).setText(amount);
		ingredientsLayout.addView(itemView);
		itemView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				view.removeOnLayoutChangeListener(this);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					// get the final radius for the clipping circle
					int finalRadius = Math.max(view.getWidth(), view.getHeight());

					// create the animator for this view (the start radius is zero)
					Animator anim =
							ViewAnimationUtils.createCircularReveal(view, view.getWidth(), view.getHeight(), 0, finalRadius);

					// make the view visible and start the animation
					view.setVisibility(View.VISIBLE);
					anim.start();
				} else
					view.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if (recipe != null && recipe.getAuthor() == ParseUser.getCurrentUser())
			inflater.inflate(R.menu.menu_recipe_author, menu);
		else
			inflater.inflate(R.menu.menu_recipe, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.edit_recipe:
				editRecipe(recipe);
				return true;
			case R.id.delete_recipe:
				showDeleteDialog();
				return true;
			case R.id.add_comment:
				Intent intent = new Intent(this, AddCommentActivity.class);
				intent.putExtra("recipeId", recipeId);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void showDeleteDialog() {
		new AlertDialog.Builder(this)
				.setMessage(R.string.delete_confirm)
				.setCancelable(false)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						deleteRecipe();
					}
				})
				.setNegativeButton(android.R.string.no, null)
				.show();
	}

	private void deleteRecipe() {
		recipe.deleteInBackground(new DeleteCallback() {
			@Override
			public void done(ParseException e) {
				if (e != null)
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				else
					finish();
			}
		});
	}

	private void editRecipe(Recipe recipe) {

	}

}
