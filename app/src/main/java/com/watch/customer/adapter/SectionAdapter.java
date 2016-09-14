package com.watch.customer.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uacent.watchapp.R;

public class SectionAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> itemList;
	private int selectid;
	private int secindex;

	public SectionAdapter(Context context, ArrayList<String> item,
			int selectid, int secindex) {
		this.context = context;
		this.itemList = item;
		this.selectid = selectid;
		this.secindex = secindex;
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
					R.layout.section_item, null);
			data.mText = (TextView) convertView.findViewById(R.id.name);
			data.mImage = (ImageView) convertView.findViewById(R.id.checkimg);
			convertView.setTag(data);
		} else {
			data = (DataList) convertView.getTag();
		}
		data.mText.setText(itemList.get(position));
		if (selectid == position) {
			data.mImage.setVisibility(View.VISIBLE);
			convertView.setBackgroundResource(R.color.border_gray);
			data.mText.setTextColor(0xff1398a7);
		} else {
			data.mImage.setVisibility(View.INVISIBLE);
			convertView.setBackgroundResource(R.drawable.white_btn_text);
			data.mText.setTextColor(Color.BLACK);
		}
		if (secindex == 2) {
			//
		}
		return convertView;
	}

	public void freshdata() {
		this.notifyDataSetChanged();
	}

	private class DataList {
		public TextView mText;
		public ImageView mImage;
	}

}
