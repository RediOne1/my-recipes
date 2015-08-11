package com.myapps.myrecipes;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

/**
 * author:  Adrian Kuta
 * index:   204423
 * date:    11.08.15
 */
public class IngredientsCompletionView extends TokenCompleteTextView<Ingredient> {

	public IngredientsCompletionView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View getViewForObject(Ingredient ingredient) {

		LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		LinearLayout view = (LinearLayout) l.inflate(R.layout.ingredient_token, (ViewGroup) IngredientsCompletionView.this.getParent(), false);
		((TextView) view.findViewById(R.id.name)).setText(ingredient.getName());

		return view;
	}

	@Override
	protected Ingredient defaultObject(String s) {
		return null;
	}
}
