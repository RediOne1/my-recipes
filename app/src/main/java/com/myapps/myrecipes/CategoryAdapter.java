package com.myapps.myrecipes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapps.myrecipes.displayingbitmaps.ImageFetcher;
import com.parse.ParseObject;

import java.util.List;

/**
 * author:  Adrian Kuta
 * index:   204423
 * date:    29.07.15
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

	private List<ParseObject> categories;
	private ImageFetcher imageFetcher;

	public CategoryAdapter(Context context, List<ParseObject> categories) {
		this.categories = categories;
		imageFetcher = ((NaviagtionDrawerActivity) context).getImageFetcher();
	}

	@Override
	public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
	                                                     int viewType) {
		// create a new view
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.category_item, parent, false);
		ViewHolder vh = new ViewHolder(view);
		vh.name = (TextView) view.findViewById(R.id.category_name);
		vh.image = (ImageView) view.findViewById(R.id.category_image);
		return vh;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

		ParseObject category = categories.get(position);
		holder.name.setText(category.getString("name"));
		imageFetcher.loadImage(category.getString("imageUrl"), holder.image);


	}

	@Override
	public int getItemCount() {
		return categories.size();
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {

		public TextView name;
		public ImageView image;

		public ViewHolder(View itemView) {
			super(itemView);
		}
	}
}
