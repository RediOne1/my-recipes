package com.myapps.myrecipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapps.myrecipes.displayingbitmaps.ImageFetcher;
import com.parse.ParseObject;

import java.util.List;

/**
 * author:  Adrian Kuta
 * index:   204423
 * date:    09.07.15
 */
public class GridAdapter extends BaseAdapter {

	private List<ParseObject> recipes;
	private LayoutInflater inflater;
	private ImageFetcher imageFetcher;

	public GridAdapter(Context context, List<ParseObject> recipes) {
		inflater = LayoutInflater.from(context);
		this.recipes = recipes;
		imageFetcher = ((NaviagtionDrawerActivity) context).getImageFetcher();
	}

	@Override
	public int getCount() {
		return recipes.size();
	}

	@Override
	public ParseObject getItem(int position) {
		return recipes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ParseObject parseObject = getItem(position);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.grid_recipe_item, parent, false);

		ImageView imageView = (ImageView) convertView.findViewById(R.id.recipe_image);
		TextView title = (TextView) convertView.findViewById(R.id.info_text);

		imageFetcher.loadImage(parseObject.getString("image"), imageView);
		title.setText(parseObject.getString("name"));

		return convertView;
	}
}
