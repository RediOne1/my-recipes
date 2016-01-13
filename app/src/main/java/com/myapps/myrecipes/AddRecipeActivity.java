package com.myapps.myrecipes;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.myapps.myrecipes.parseobjects.Ingredient;
import com.myapps.myrecipes.parseobjects.Recipe;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddRecipeActivity extends AppCompatActivity {

	public static final String ID = "recipeId";
	public static final String TITLE = "title";
	public static final String SELECTED_CATEGORY = "category";
	public static final String SELECTED_DIFFICULTY = "difficulty";
	public static final String BITMAP = "bitmap";
	public static final String PHOTO_PATH = "path";
	public static final String INGREDIENTS = "ingredients";
	public static final String DESCRIPTION = "description";
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static ProgressDialog progressDialog;
	private ImageView recipeImage;
	private EditText title;
	private String mCurrentPhotoPath;
	private ParseFile photoFile;
	private Recipe recipe = null;
	private Spinner categorySpinner;
	private Spinner difficultySpinner;
	private EditText description;
	private LinearLayout ingredientsLayout;
	private ArrayAdapter<Ingredient> ingredientsAdapter;
	private List<Ingredient> ingredientsList;
	private Button addIngredient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_recipe);

		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage(getString(R.string.adding_recipe));
		progressDialog.setCancelable(false);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_photo);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dispatchTakePictureIntent();
			}
		});

		ingredientsLayout = (LinearLayout) findViewById(R.id.ingredients_layout);
		ingredientsList = new ArrayList<>();
		ingredientsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ingredientsList);

		ParseQuery<Ingredient> parseQuery = Ingredient.getQuery();
		parseQuery.findInBackground(new FindCallback<Ingredient>() {
			@Override
			public void done(List<Ingredient> list, ParseException e) {
				ingredientsList.clear();
				ingredientsList.addAll(list);
				ingredientsAdapter.notifyDataSetChanged();
			}
		});

		addIngredient = (Button) findViewById(R.id.add_ingredient);
		addIngredient.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addIngredient();
			}
		});

		recipeImage = (ImageView) findViewById(R.id.add_recipe_image);

		categorySpinner = (Spinner) findViewById(R.id.category_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(adapter);

		difficultySpinner = (Spinner) findViewById(R.id.difficulty_spinner);
		ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(this, R.array.difficulty, android.R.layout.simple_spinner_item);
		difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		difficultySpinner.setAdapter(difficultyAdapter);

		description = (EditText) findViewById(R.id.add_recipe_description);
		title = (EditText) findViewById(R.id.add_title_editText);
		setupAddTitle();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String objectId = bundle.getString(ID);
			ParseQuery<Recipe> recipeParseQuery = Recipe.getQuery();
			recipeParseQuery.fromLocalDatastore();
			recipeParseQuery.getInBackground(objectId, new GetCallback<Recipe>() {
				@Override
				public void done(Recipe recipe2, ParseException e) {
					if (recipe2 != null) {
						recipe = recipe2;
						title.setText(recipe.getTitle());
						addIngredientsFromJSON(recipe.getIngredientJSON());
						description.setText(recipe.getDescription());

					}
				}
			});
		}
		if (recipe == null)
			recipe = new Recipe();

		recipe.setAuthor(ParseUser.getCurrentUser());
	}

	private void addIngredient() {
		addIngredient("", "");
	}

	private void addIngredient(String name, String amount) {
		View itemView = getLayoutInflater().inflate(R.layout.single_add_ingredient_layout, ingredientsLayout, false);
		itemView.setVisibility(View.INVISIBLE);
		AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) itemView.findViewById(R.id.ingredient_name);
		autoCompleteTextView.setText(name);
		autoCompleteTextView.setAdapter(ingredientsAdapter);
		((EditText) itemView.findViewById(R.id.ingredient_amount)).setText(amount);
		float translationY = addIngredient.getTranslationY();
		addIngredient.setTranslationY(translationY + itemView.getHeight());
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

	private void setupAddTitle() {
		final TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.title_input_layout);
		final int titleMaxLength = getResources().getInteger(R.integer.title_max_length);
		title.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				int length = editable.length();
				if (length > titleMaxLength) {
					textInputLayout.setErrorEnabled(true);
					textInputLayout.setError(getString(R.string.title_too_long));
				} else {
					if (editable.length() == 0)
						setTitle(R.string.title_activity_add_recipe);
					else
						setTitle(editable.toString());
					textInputLayout.setErrorEnabled(false);
				}
			}
		});
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				Log.e("DEBUG_TAG", "createFile", ex);
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
		}
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = getAlbumDir();
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

			storageDir = new File(Environment.getExternalStorageDirectory()
					+ "/dcim/"
					+ "MyRecipes");

			if (!storageDir.mkdirs()) {
				if (!storageDir.exists()) {
					Log.d("CameraSample", "failed to create directory");
					return null;
				}
			}

		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = recipeImage.getWidth();
		int targetH = recipeImage.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 2;
		if ((targetW > 0) && (targetH > 0)) {
			scaleFactor = Math.max(photoW / targetW, photoH / targetH);
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Matrix matrix = new Matrix();
		matrix.postRotate(getRotation());

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);

		/* Associate the Bitmap to the ImageView */
		recipeImage.setImageBitmap(bitmap);
	}

	private float getRotation() {
		try {
			ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					return 90f;
				case ExifInterface.ORIENTATION_ROTATE_180:
					return 180f;
				case ExifInterface.ORIENTATION_ROTATE_270:
					return 270f;
				default:
					return 0f;
			}
		} catch (Exception e) {
			Log.e("Add Recipe", "getRotation", e);
			return 0f;
		}
	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		sendBroadcast(mediaScanIntent);
	}

	private void handleBigCameraPhoto() {

		if (mCurrentPhotoPath != null) {
			setPic();
			galleryAddPic();
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
			handleBigCameraPhoto();
		}
	}

	private void saveScaledPhoto(SaveCallback saveCallback) {

		Bitmap rotatedScaledMealImage = ((BitmapDrawable) recipeImage.getDrawable()).getBitmap();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		rotatedScaledMealImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

		byte[] scaledData = bos.toByteArray();

		// Save the scaled image to Parse
		photoFile = new ParseFile("meal_photo.jpg", scaledData);
		photoFile.saveInBackground(saveCallback, new ProgressCallback() {
			@Override
			public void done(Integer progress) {
				if (!progressDialog.isShowing())
					progressDialog.show();
				progressDialog.setProgress(progress);
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_add_recipe, menu);
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
		} else if (id == R.id.action_save) {
			progressDialog.show();
			if (mCurrentPhotoPath != null)
				saveScaledPhoto(new SaveCallback() {

					public void done(ParseException e) {
						if (e != null) {
							Toast.makeText(getApplicationContext(),
									"Error saving: " + e.getMessage(),
									Toast.LENGTH_LONG).show();
						} else {
							recipe.setPhotoFile(photoFile);
							recipe.setPhotoUrl(photoFile.getUrl());
							saveRecipe();
						}
					}
				});
			else
				saveRecipe();
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveRecipe() {
		saveNewIngredients();
		recipe.setName(title.getText().toString());
		recipe.setCategory(categorySpinner.getSelectedItem().toString());
		recipe.setDifficulty(difficultySpinner.getSelectedItem().toString());
		recipe.setIngredients(ingredientsToStringJSON());
		recipe.setDescription(description.getText().toString());
		recipe.pinInBackground();
		recipe.saveEventually(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e != null) {
					Toast.makeText(getApplicationContext(),
							"ErrorSaving: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				} else {
					Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
					intent.putExtra(RecipeActivity.RECIPE_ID, recipe.getObjectId());
					intent.putExtra(RecipeActivity.RECIPE_NAME, recipe.getTitle());
					intent.putExtra(RecipeActivity.CATEGORY, recipe.getCategory());
					intent.putExtra(RecipeActivity.DIFFICULTY, recipe.getDifficulty());
					intent.putExtra(RecipeActivity.INGREDIENTS, recipe.getIngredientJSON());
					intent.putExtra(RecipeActivity.PHOTO_URL, recipe.getPhotoUrl());
					intent.putExtra(RecipeActivity.DESCRIPTION, recipe.getDescription());
					startActivity(intent);
					finish();
				}
				progressDialog.cancel();
			}
		});
	}

	private void saveNewIngredients() {
		List<Ingredient> newIngredients = new ArrayList<>();
		int ingredientsCount = ingredientsLayout.getChildCount();
		for (int i = 0; i < ingredientsCount; i++) {
			View itemView = ingredientsLayout.getChildAt(i);
			AutoCompleteTextView textView = (AutoCompleteTextView) itemView.findViewById(R.id.ingredient_name);
			String name = textView.getText().toString();
			if (!ingredientsListContains(name)) {
				Ingredient ingredient = new Ingredient();
				ingredient.setName(name);
				newIngredients.add(ingredient);
			}
		}
		ParseObject.saveAllInBackground(newIngredients);
	}

	private boolean ingredientsListContains(String name) {
		for (Ingredient ingredient : ingredientsList) {
			if (ingredient.toString().equals(name))
				return true;
		}
		return false;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(TITLE, title.getText().toString());
		outState.putInt(SELECTED_CATEGORY, categorySpinner.getSelectedItemPosition());
		outState.putInt(SELECTED_DIFFICULTY, difficultySpinner.getSelectedItemPosition());
		String ingredients = ingredientsToStringJSON();
		outState.putString(PHOTO_PATH, mCurrentPhotoPath);
		outState.putString(INGREDIENTS, ingredients);
		outState.putString(DESCRIPTION, description.getText().toString());
		BitmapDrawable drawable = (BitmapDrawable) recipeImage.getDrawable();
		if (drawable != null)
			outState.putParcelable(BITMAP, drawable.getBitmap());
	}

	@Override
	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		title.setText(savedInstanceState.getString(TITLE, ""));
		categorySpinner.setSelection(savedInstanceState.getInt(SELECTED_CATEGORY), true);
		difficultySpinner.setSelection(savedInstanceState.getInt(SELECTED_DIFFICULTY), true);
		description.setText(savedInstanceState.getString(DESCRIPTION, ""));
		String jsonIngredients = savedInstanceState.getString(INGREDIENTS);
		addIngredientsFromJSON(jsonIngredients);
		mCurrentPhotoPath = savedInstanceState.getString(PHOTO_PATH, null);
		Bitmap bitmap = savedInstanceState.getParcelable(BITMAP);
		if (bitmap != null) {
			recipeImage.setImageBitmap(bitmap);
			recipeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}
	}

	private String ingredientsToStringJSON() {
		int childCount = ingredientsLayout.getChildCount();
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < childCount; i++) {
			View itemView = ingredientsLayout.getChildAt(i);
			EditText name = (EditText) itemView.findViewById(R.id.ingredient_name);
			EditText amount = (EditText) itemView.findViewById(R.id.ingredient_amount);
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("name", name.getText().toString());
				jsonObject.put("amount", amount.getText().toString());
				jsonArray.put(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		JSONObject ingredientJSONObject = new JSONObject();
		try {
			ingredientJSONObject.put("ingredients", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();

		}
		return ingredientJSONObject.toString();
	}

	private void addIngredientsFromJSON(String jsonIngredients) {
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
}
