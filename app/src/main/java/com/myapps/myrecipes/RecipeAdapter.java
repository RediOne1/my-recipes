package com.myapps.myrecipes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapps.myrecipes.displayingbitmaps.ImageFetcher;
import com.myapps.myrecipes.parseobjects.Recipe;

import java.util.List;

/**
 * Author:  Adrian Kuta
 * Date:    04.11.2015
 * Index:   204423
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

	private List<Recipe> recipeList;
	private ImageFetcher imageFetcher;
	private OnItemClickListener onItemClickListener;

	public RecipeAdapter(List<Recipe> recipeList) {
		this.recipeList = recipeList;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		imageFetcher = ((MainActivity) parent.getContext()).getImageFetcher();
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_item, parent, false);

		ViewHolder viewHolder = new ViewHolder(view, new ViewHolder.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				if (onItemClickListener != null)
					onItemClickListener.onItemClick(view, position);
			}
		});

		viewHolder.title = (TextView) view.findViewById(R.id.recipe_title);
		viewHolder.image = (ImageView) view.findViewById(R.id.recipe_image);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Recipe recipe = recipeList.get(position);
		holder.title.setText(recipe.getTitle());
		imageFetcher.loadImage(recipe.getPhotoUrl(), holder.image);
	}

	@Override
	public int getItemCount() {
		return recipeList.size();
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public interface OnItemClickListener {
		void onItemClick(View view, int position);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		public TextView title;
		public ImageView image;
		public OnItemClickListener listener;

		public ViewHolder(View itemView, OnItemClickListener listener) {
			super(itemView);

			this.listener = listener;
			if (this.listener != null) {
				itemView.setOnClickListener(this);
			}
		}

		@Override
		public void onClick(View v) {
			listener.onItemClick(v, getAdapterPosition());
		}

		public interface OnItemClickListener {
			void onItemClick(View view, int position);
		}
	}
}
