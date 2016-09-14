package com.watch.customer.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.model.Menu;

public class MenuAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Menu> data = new ArrayList<Menu>();
    private boolean[] checkArr;
	public MenuAdapter(Context context, ArrayList<Menu> orderlist) {
		super();
		this.context = context;
		data = orderlist;
		this.checkArr=checkArr;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DataList dl = null;
		if (convertView == null) {
			dl = new DataList();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.menu_item, null);
			dl.name = (TextView) convertView.findViewById(R.id.name);
			dl.price = (TextView) convertView.findViewById(R.id.price);
			
			convertView.setTag(dl);
		} else {
			dl = (DataList) convertView.getTag();
		}

		dl.name.setText(data.get(position).getName());
		dl.price.setText(data.get(position).getPrice()+"/浠�");
		return convertView;
	}

	private class DataList {

		public TextView name;
		public TextView price;
	
	}

}
