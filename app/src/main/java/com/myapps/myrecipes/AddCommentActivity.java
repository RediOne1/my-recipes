package com.myapps.myrecipes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.myapps.myrecipes.parseobjects.Recipe;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class AddCommentActivity extends AppCompatActivity {

	private Recipe recipe;
	private EditText comment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_comment);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		Bundle bundle = getIntent().getExtras();
		String recipeId = bundle.getString("recipeId");

		recipe = ParseObject.createWithoutData(Recipe.class, recipeId);
		comment = (EditText) findViewById(R.id.comment_content);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				ParseObject parseObject = new ParseObject("Comments");
				parseObject.put("recipe", recipe);
				parseObject.put("author", ParseUser.getCurrentUser());
				parseObject.put("comment", comment.getText().toString());
				parseObject.saveEventually();

				Snackbar.make(view, "Dodano komentarz. Chcesz wyjść?", Snackbar.LENGTH_LONG)
						.setAction("Wychodzę", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								finish();
							}
						}).show();
			}
		});
	}

}
