package com.watch.customer.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.model.Category;

public class CategoryAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Category> itemList;
	public CategoryAdapter(Context context, ArrayList<Category> items) {
		this.context = context;
		this.itemList = items;
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public Object getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DataList data = null;
		if (convertView == null) {
			data = new DataList();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.menu_category_item, null);
			data.mNameTextView = (TextView) convertView
					.findViewById(R.id.category_name);
			data.countTextView = (TextView) convertView
					.findViewById(R.id.category_count);
			convertView.setTag(data);
		} else {
			data = (DataList) convertView.getTag();
		}
		data.mNameTextView.setText(itemList.get(position).getName());
		int count = itemList.get(position).getCount();
		if (count > 0) {
			data.countTextView.setText(String.valueOf(count));
			data.countTextView.setVisibility(View.VISIBLE);
		} else {
			data.countTextView.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}
    public void setNumber(String typeid,boolean isadd){
    	for (int i = 0; i < itemList.size(); i++) {
			if (typeid.equals(itemList.get(i).getId())) {
				if (isadd) {
					int count=itemList.get(i).getCount();
					count=count+1;
					itemList.get(i).setCount(count);
					notifyDataSetChanged();
					continue;
				}else {
					int count=itemList.get(i).getCount();
					count=count-1;
					itemList.get(i).setCount(count);
					notifyDataSetChanged();
					continue;
				}
			}
		}
    }
	private class DataList {
		public TextView mNameTextView, countTextView;
	}

}
