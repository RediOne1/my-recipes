package com.myapps.myrecipes;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapps.myrecipes.displayingbitmaps.ImageFetcher;

import java.util.List;

/**
 * author:  Adrian Kuta
 * index:   204423
 * date:    29.07.15
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

	public interface OnCategoryClickListener {
		void selectedCategory(Category category);
	}

	private List<Category> categories;
	private ImageFetcher imageFetcher;
	private OnCategoryClickListener categoryClickListener;

	public CategoryAdapter(Context context, List<Category> categories, OnCategoryClickListener listener) {
		this.categories = categories;
		imageFetcher = ((NaviagtionDrawerActivity) context).getImageFetcher();
		categoryClickListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent,
	                                     int viewType) {
		// create a new view
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.category_item, parent, false);
		ViewHolder vh = new ViewHolder(view, new ViewHolder.OnItemClickListener() {
			@Override
			public void itemPosition(int position) {
				if (categoryClickListener != null)
					categoryClickListener.selectedCategory(categories.get(position));
			}
		});
		vh.name = (TextView) view.findViewById(R.id.category_name);
		vh.image = (ImageView) view.findViewById(R.id.category_image);
		return vh;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

		Category category = categories.get(position);
		holder.name.setText(category.getName());
		imageFetcher.loadImage(category.getIamgeUrl(), holder.image);
	}


	@Override
	public int getItemCount() {
		return categories.size();
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {

		public TextView name;
		public ImageView image;
		private OnItemClickListener onItemClickListener;


		public ViewHolder(View itemView, OnItemClickListener listener) {
			super(itemView);
			onItemClickListener = listener;
			CardView cardView = (CardView) itemView.findViewById(R.id.card_view);
			cardView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onItemClickListener.itemPosition(getPosition());
				}
			});
		}

		public interface OnItemClickListener {
			/**
			 * Listener który zwraca pozycję na liście kategorii, która zostałą wybrana
			 *
			 * @param position Pozycja kategorii na liście.
			 */
			void itemPosition(int position);
		}
	}
}
