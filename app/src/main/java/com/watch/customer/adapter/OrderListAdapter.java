package com.watch.customer.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.model.OrderItem;
import com.watch.customer.util.CommonUtil;

public class OrderListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<OrderItem> data = new ArrayList<OrderItem>();

	public OrderListAdapter(Context context, ArrayList<OrderItem> orderlist) {
		super();
		this.context = context;
		data = orderlist;
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
					R.layout.order_item, null);	
			dl.name = (TextView) convertView.findViewById(R.id.name);
			dl.order_time = (TextView) convertView.findViewById(R.id.time);
			dl.local = (TextView) convertView.findViewById(R.id.local);
			dl.state = (TextView) convertView.findViewById(R.id.state);
			convertView.setTag(dl);
		} else {
			dl = (DataList) convertView.getTag();
		}
		dl.name.setText(data.get(position).getStoreName());
		dl.order_time.setText(CommonUtil.getOrderTimestr(data.get(position).getCreate_time()));
		if (data.get(position).getIs_local().equals("1")) {
			dl.local.setVisibility(View.VISIBLE);
		}else {
			dl.local.setVisibility(View.GONE);
		}
		if (data.get(position).getType().equals("group")) {
			dl.state.setText(CommonUtil.getDealOrderStatus(data.get(position).getStatus()));
		}else {
			dl.state.setText(CommonUtil.getOrderStatus(data.get(position).getStatus()));
		}
		return convertView;
	}

	private class DataList {
		public TextView name;
		public TextView order_time;
		public TextView local;
		public TextView state;
	}

}
