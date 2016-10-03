package com.jaridaapp.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jaridaapp.R;
import com.jaridaapp.model.category.Category;

/**
 * Parses each row of the categories view
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class CategoriesAdapter extends BaseAdapter {

		private List<Category> listData;
		private LayoutInflater layoutInflater;
		Category category;
		Context context;
		
		
		public CategoriesAdapter(Context context, List<Category> listData) {
			this.listData = listData;
			this.context = context;
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return listData.size();
		}

		@Override
		public Object getItem(int position) {
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null) {
				convertView = layoutInflater.inflate(R.layout.row_category, null);
				holder = new ViewHolder();
				holder.categoryCount = (TextView) convertView.findViewById(R.id.category_title);
				holder.categoryTitle = (TextView) convertView.findViewById(R.id.category_count);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}

			category = (Category) listData.get(position);
	        if(category != null) {
	        	
	        	/** preparation for a later update
	        	if(category.getParent() != 0){
				    holder.categoryTitle.setText(Html.fromHtml("&#8212;&nbsp;"+category.getTitle()));
	        	}else{
	        		holder.categoryTitle.setText(Html.fromHtml(category.getTitle()));
	        	} 
	        	**/
	        	
	        	holder.categoryTitle.setText(Html.fromHtml(category.getTitle()));
	        	
				if(category.getPostCount() != 1) {
				    holder.categoryCount.setText(Html.fromHtml(category.getPostCount()+" "+context.getString(R.string.articles)));
				}else {
					holder.categoryCount.setText(Html.fromHtml(category.getPostCount()+" "+context.getString(R.string.article)));
				}
		  }
			return convertView;
		}
		

		static class ViewHolder {
			TextView categoryTitle;
			TextView categoryCount;
		}
	}
