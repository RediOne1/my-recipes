package com.myapps.myrecipes;

import android.animation.Animator;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.myapps.myrecipes.displayingbitmaps.ImageCache;
import com.myapps.myrecipes.displayingbitmaps.ImageFetcher;
import com.myapps.myrecipes.parseobjects.Rating;
import com.myapps.myrecipes.parseobjects.Recipe;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

	public static final String RECIPE_ID = "recipeId";
	public static final String RECIPE_NAME = "recipe_name";
	public static final String CATEGORY = "category";
	public static final String DIFFICULTY = "difficulty";
	public static final String INGREDIENTS = "ingredients";
	public static final String PHOTO_URL = "photo_url";
	public static final String DESCRIPTION = "description";

	private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
	private static final String IMAGE_CACHE_DIR = "images";
	private int mFlexibleSpaceImageHeight;
	private int mFlexibleSpaceShowFabOffset;
	private int mFabMargin;
	private ImageView mImageView;
	private View mOverlayView;
	private TextView mTitleView;
	private View mFab;
	private boolean mFabIsShown;
	private int mActionBarSize;
	private LinearLayout ingredientsLayout;
	private TextView category;
	private TextView difficulty;
	private TextView description;

	private ImageFetcher imageFetcher;

	private String recipeId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);

		mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
		mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
		mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
		mActionBarSize = getActionBarSize();

		mImageView = (ImageView) findViewById(R.id.image);
		mOverlayView = findViewById(R.id.overlay);
		ObservableScrollView mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
		mScrollView.setScrollViewCallbacks(this);
		mTitleView = (TextView) findViewById(R.id.title);
		ingredientsLayout = (LinearLayout) findViewById(R.id.recipe_activity_ingredients_layout);
		category = (TextView) findViewById(R.id.category_name);
		difficulty = (TextView) findViewById(R.id.difficulty_name);
		description = (TextView) findViewById(R.id.recipe_activity_description);
		mFab = findViewById(R.id.fab);
		mFab.setScaleX(0);
		mFab.setScaleY(0);
		mOverlayView.setAlpha(0f);
		ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
			@Override
			public void run() {
				onScrollChanged(0, false, false);
			}
		});
		setUpImageLoader();
		Bundle bundle = getIntent().getExtras();
		try {
			recipeId = bundle.getString(RECIPE_ID);
			mTitleView.setText(bundle.getString(RECIPE_NAME));
			category.setText(bundle.getString(CATEGORY));
			difficulty.setText(bundle.getString(DIFFICULTY));
			description.setText(bundle.getString(DESCRIPTION));
			displayIngredientsFromJSON(bundle.getString(INGREDIENTS));
			String photoUrl = bundle.getString(PHOTO_URL);
			if (photoUrl != null)
				imageFetcher.loadImage(photoUrl, mImageView);
		} catch (Exception e) {
			if (recipeId != null) {
				ParseQuery<Recipe> query = Recipe.getQuery();
				query.getInBackground(recipeId, new GetCallback<Recipe>() {
					@Override
					public void done(Recipe recipe, ParseException e) {
						mTitleView.setText(recipe.getName());
						category.setText(recipe.getCategory());
						difficulty.setText(recipe.getDifficulty());
						description.setText(recipe.getDescription());
						displayIngredientsFromJSON(recipe.getIngredientJSON());
						String photoUrl = recipe.getPhotoUrl();
						if (photoUrl != null)
							imageFetcher.loadImage(photoUrl, mImageView);
					}
				});

				RatingBar ratingBar = (RatingBar) findViewById(R.id.recipeRatingBar);
				ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
					@Override
					public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
						Rating ratingObject = new Rating();
						ratingObject.setRating(rating);
						ratingObject.setUser(ParseUser.getCurrentUser());
						ratingObject.put("recipe", recipeId);
						ratingObject.saveEventually(new SaveCallback() {
							@Override
							public void done(ParseException e) {
								if (e != null) {
									Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				});
			}
		}
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

	protected int getActionBarSize() {
		TypedValue typedValue = new TypedValue();
		int[] textSizeAttr = new int[]{R.attr.actionBarSize};
		int indexOfAttrTextSize = 0;
		TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
		int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
		a.recycle();
		return actionBarSize;
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
		float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
		int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
		mOverlayView.setTranslationY(ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
		mImageView.setTranslationY(ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));
		mOverlayView.setAlpha(ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

		float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
		mTitleView.setPivotX(0);
		mTitleView.setPivotY(0);
		mTitleView.setScaleX(scale);
		mTitleView.setScaleY(scale);

		int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
		int titleTranslationY = maxTitleTranslationY - scrollY;
		mTitleView.setTranslationY(Math.max(titleTranslationY, 0));
		/*if (mTitleView.getTranslationY() == 0)
			mTitleView.setBackground(mOverlayView.getBackground());
		else
			mTitleView.setBackground(null);*/
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			mTitleView.setElevation(ScrollUtils.getFloat(-mFlexibleSpaceImageHeight + scrollY, 0, 15));


		int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
		float fabTranslationY = ScrollUtils.getFloat(
				-scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
				mActionBarSize - mFab.getHeight() / 2,
				maxFabTranslationY);
		mFab.setTranslationX(mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
		mFab.setTranslationY(fabTranslationY);

		if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
			hideFab();
		} else {
			showFab();
		}
	}

	private void showFab() {
		if (!mFabIsShown) {
			ViewPropertyAnimator.animate(mFab).cancel();
			ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
			mFabIsShown = true;
		}
	}

	private void hideFab() {
		if (mFabIsShown) {
			ViewPropertyAnimator.animate(mFab).cancel();
			ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
			mFabIsShown = false;
		}
	}

	@Override
	public void onDownMotionEvent() {

	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {

	}
}
