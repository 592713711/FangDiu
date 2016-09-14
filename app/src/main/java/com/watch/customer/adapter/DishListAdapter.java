package com.watch.customer.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.model.Dish;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.ImageLoaderUtil;

public class DishListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Dish> data = new ArrayList<Dish>();
	private ArrayList<Dish> catadata = new ArrayList<Dish>();
	private String type_id="";
	public DishListAdapter(Context context, ArrayList<Dish> shoplist) {
		super();
		this.context = context;
		data = shoplist;
		catadata=data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return catadata.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return catadata.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DataList dl = null;
		if (convertView == null) {
			dl = new DataList();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.dish_item, null);
			dl.image = (ImageView) convertView.findViewById(R.id.menu_image);
			dl.name = (TextView) convertView.findViewById(R.id.menu_name);
			dl.price = (TextView) convertView.findViewById(R.id.menu_price);
			dl.discount = (ImageView) convertView.findViewById(R.id.menu_discount);
			dl.select = (ImageView) convertView.findViewById(R.id.menu_select);
			convertView.setTag(dl);
		} else {
			dl = (DataList) convertView.getTag();
		}
		ImageLoaderUtil.displayImage(HttpUtil.SERVER+catadata.get(position).getImage(), dl.image,
				context);
		dl.name.setText(catadata.get(position).getDishes_name());
		dl.price.setText("¥" + catadata.get(position).getPrice() + "/个");
		if (catadata.get(position).getDiscount().equals("1")) {
			dl.discount.setVisibility(View.VISIBLE);
		} else {
			dl.discount.setVisibility(View.GONE);
		}
		if (catadata.get(position).isIsselect()) {
			dl.select.setVisibility(View.VISIBLE);
		}else {
			dl.select.setVisibility(View.INVISIBLE);
		}
		return convertView;

	}
    public void showCatalist(String type_id){
    	this.type_id=type_id;
    	ArrayList<Dish> temp=new ArrayList<Dish>();
    	for (int i = 0; i < data.size(); i++) {
    		Log.e("hjq","data.size()="+data.size());
			if (data.get(i).getDishes_type().equals(type_id)) {
				temp.add(data.get(i));
				Log.e("hjq",data.get(i).toString());
			}
		}
    	catadata=temp;
    	Log.e("hjq","data.size()="+data.size());
    	notifyDataSetChanged();
    }
    public void refresh(){
    	notifyDataSetChanged();
//    	for (int i = 0; i < data.size(); i++) {
//    		Log.e("hjq",data.get(i).toString());
//		}
//    	
    	if (type_id.equals("")) {
			catadata=data;
			notifyDataSetChanged();
		}else {
		 	showCatalist(type_id);
	    	notifyDataSetChanged();
		}
   
    }
	private class DataList {
		public ImageView image;
		public TextView name;
		public TextView price;
		public ImageView discount;
		public ImageView select;

	}

}
