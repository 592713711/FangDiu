package com.watch.customer.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uacent.watchapp.R;

public class TextAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<String> slist;
	private onSelectListener listener;
	private int selectid = 0;

	public TextAdapter(Context mContext, ArrayList<String> slist) {
		super();
		this.mContext = mContext;
		this.slist = slist;
	}

	public void setonSelectListener(onSelectListener selectListener) {
		listener = selectListener;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return slist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return slist.get(position);
	}

	@Override
	public long getItemId(int id) {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public View getView(int position, View convertview, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView tv;
		if (convertview == null) {
			convertview = LayoutInflater.from(mContext).inflate(
					R.layout.single_text_item, null);
			tv = (TextView) convertview.findViewById(R.id.text);
			convertview.setTag(tv);
		} else {
			tv = (TextView) convertview.getTag();
		}
		convertview.setId(position);
		convertview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectid = v.getId();
				notifyDataSetChanged();
				if (listener != null) {
					listener.select(selectid, slist.get(selectid));
				}
			}
		});
		tv.setText(slist.get(position));
		if (selectid == position) {
			convertview.setBackgroundColor(Color.GRAY);
		} else {
			convertview.setBackgroundColor(Color.WHITE);
		}
		return convertview;
	}

	public interface onSelectListener {
		public void select(int position, String content);
	}

}
