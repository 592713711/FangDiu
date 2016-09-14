package com.watch.customer.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.model.Coin;

public class CoinListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Coin> data = new ArrayList<Coin>();

	public CoinListAdapter(Context context, ArrayList<Coin> coinlist) {
		super();
		this.context = context;
		data = coinlist;
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
					R.layout.coin_item, null);
			dl.dtime = (TextView) convertView.findViewById(R.id.dtime);
			dl.add = (TextView) convertView.findViewById(R.id.add);
			dl.money = (TextView) convertView.findViewById(R.id.money);
			convertView.setTag(dl);
		} else {
			dl = (DataList) convertView.getTag();
		}

		dl.dtime.setText(data.get(position).getCreate_time());
		if (data.get(position).getType().equals("add")) {
			dl.add.setText("充值");
		}else {
			dl.add.setText("消费");
		}
		dl.money.setText(data.get(position).getShibi());
		return convertView;

	}

	private class DataList {
		public TextView dtime;
		public TextView add;
		public TextView money;
	}

}
