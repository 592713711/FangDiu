package com.watch.customer.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.model.Comment;

public class CommentAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Comment> slist;
	private int selectid = 0;

	public CommentAdapter(Context mContext, ArrayList<Comment> slist) {
		super();
		this.mContext = mContext;
		this.slist = slist;
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
		Holder holder;
		if (convertview == null) {
			convertview = LayoutInflater.from(mContext).inflate(
					R.layout.comment_item, null);
			holder = new Holder();
			holder.title = (TextView) convertview.findViewById(R.id.username);
			holder.time = (TextView) convertview
					.findViewById(R.id.comment_time);
			holder.content = (TextView) convertview.findViewById(R.id.content);
			convertview.setTag(holder);
		} else {
			holder = (Holder) convertview.getTag();
		}
		holder.title.setText(slist.get(position).getUser_name());
		holder.time.setText(slist.get(position).getComment_time());
		holder.content.setText(slist.get(position).getContent());
		return convertview;
	}

	class Holder {
		public TextView title;
		public TextView time;
		public TextView content;
	}

}
