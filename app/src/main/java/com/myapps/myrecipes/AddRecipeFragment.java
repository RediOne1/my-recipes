package com.myapps.myrecipes;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddRecipeFragment extends Fragment implements ObservableScrollViewCallbacks {

	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final String TITLE = "title";
	private View mToolbarView;

	private ImageView recipeImage;

	private int mParallaxImageHeight;
	private EditText title;
	private String mCurrentPhotoPath;
	private ParseFile photoFile;
	private ArrayAdapter<Ingredient> ingredientArrayAdapter;

	private static int selected_category = 0;
	private static int selected_difficulty = 0;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_add_recipe, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mToolbarView = ((NaviagtionDrawerActivity) getActivity()).getToolbarView();
		recipeImage = (ImageView) view.findViewById(R.id.add_recipe_image);
		recipeImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dispatchTakePictureIntent();
			}
		});

		Spinner categorySpinner = (Spinner) view.findViewById(R.id.category_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.categories, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(adapter);
		categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				selected_category = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		categorySpinner.setSelection(selected_category, true);

		Spinner difficultySpinner = (Spinner) view.findViewById(R.id.difficulty_spinner);
		ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.difficulty, android.R.layout.simple_spinner_item);
		difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		difficultySpinner.setAdapter(difficultyAdapter);
		difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				selected_difficulty = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		difficultySpinner.setSelection(selected_difficulty, true);

		final IngredientsCompletionView completionView = (IngredientsCompletionView) view.findViewById(R.id.ingredients_view);
		if (savedInstanceState == null)
			completionView.setPrefix(getString(R.string.ingredients));
		ParseQuery<Ingredient> query = Ingredient.getQuery();
		query.findInBackground(new FindCallback<Ingredient>() {
			@Override
			public void done(List<Ingredient> list, ParseException e) {
				ingredientArrayAdapter = new ArrayAdapter<Ingredient>(getActivity(), android.R.layout.simple_list_item_1, list);
				completionView.setAdapter(ingredientArrayAdapter);
			}
		});


		ObservableScrollView mScrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
		mScrollView.setScrollViewCallbacks(this);

		title = (EditText) view.findViewById(R.id.add_title_editText);
		setupAddTitle(view);
		mParallaxImageHeight = getResources().getDimensionPixelSize(
				R.dimen.parallax_image_height);

		mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));

		if (savedInstanceState != null) {
			title.setText(savedInstanceState.getString(TITLE));
		}
	}

	private void setupAddTitle(View rootView) {
		final TextView counter = (TextView) rootView.findViewById(R.id.add_title_counter);
		TextView errorText = (TextView) rootView.findViewById(R.id.add_title_errorText);
		final int titleMaxLength = getResources().getInteger(R.integer.title_max_length);
		final String counterPattern = getResources().getString(R.string.character_counter);
		counter.setText(String.format(counterPattern, 0, titleMaxLength));
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
				counter.setText(String.format(counterPattern, length, titleMaxLength));
				if (length > titleMaxLength)
					counter.setTextColor(getResources().getColor(R.color.text_field_error));
				else
					counter.setTextColor(getResources().getColor(R.color.helper_text_light));
			}
		});
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
		recipeImage.setImageBitmap(bitmap);
	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		getActivity().sendBroadcast(mediaScanIntent);
	}

	private void handleBigCameraPhoto() {

		if (mCurrentPhotoPath != null) {
			setPic();
			galleryAddPic();
			/*saveScaledPhoto(mCurrentPhotoPath);*/
			mCurrentPhotoPath = null;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
			/*Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			recipeImage.setImageBitmap(imageBitmap);*/
			handleBigCameraPhoto();
		}
	}

	private void saveScaledPhoto(String filePath) {

		// Resize photo from camera byte array
		Bitmap mealImage = BitmapFactory.decodeFile(filePath);
		Bitmap mealImageScaled = Bitmap.createScaledBitmap(mealImage, 1024, 1024
				* mealImage.getHeight() / mealImage.getWidth(), false);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		mealImageScaled.compress(Bitmap.CompressFormat.JPEG, 100, bos);

		byte[] scaledData = bos.toByteArray();

		// Save the scaled image to Parse
		photoFile = new ParseFile("meal_photo.jpg", scaledData);
		photoFile.saveInBackground(new SaveCallback() {

			public void done(ParseException e) {
				if (e != null) {
					Toast.makeText(getActivity(),
							"Error saving: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
		recipeImage.setTranslationY(scrollY / 2);
		int baseColor = getResources().getColor(R.color.primary);
		float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
		mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));

	}

	@Override
	public void onDownMotionEvent() {

	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {

	}

	@Override
	public void onDetach() {
		super.onDetach();
		mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(1, getResources().getColor(R.color.primary)));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(TITLE, title.getText().toString());
	}
}
