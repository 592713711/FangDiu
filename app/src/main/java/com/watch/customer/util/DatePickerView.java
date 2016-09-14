package com.watch.customer.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uacent.watchapp.R;

public class DatePickerView {
	private Context mContext;
	private Calendar mCalendar;
	private TextView[] textViews = new TextView[14];
	private Calendar[] calendars = new Calendar[3];
	private View parentView;
	private int position;
	private int selectpos;
	private TextView month;
	private onDateSelectedListener mDateSelectedListener;
	private SimpleDateFormat dateformat;

	public DatePickerView(Context context, View datepickView,
			onDateSelectedListener mDateSelectedListener) {
		mContext = context;
		parentView = datepickView;
		this.mDateSelectedListener = mDateSelectedListener;
		initView();
		initDate();
	}

	public View getCurDateView() {
		return parentView;
	}

	private void initView() {
		// TODO Auto-generated method stub
		if (parentView == null) {
			parentView = LayoutInflater.from(mContext).inflate(
					R.layout.datepicker, null);
		}
		calendars[0] = Calendar.getInstance();
		calendars[1] = Calendar.getInstance();
		calendars[2] = Calendar.getInstance();
		// calendars[0].add(Calendar.DAY_OF_MONTH, 25);
		calendars[1].add(Calendar.DAY_OF_MONTH, 1);
		calendars[2].add(Calendar.DAY_OF_MONTH, 2);
		position = calendars[0].get(Calendar.DAY_OF_WEEK)-1;
		mCalendar= Calendar.getInstance();
		mCalendar.add(Calendar.DAY_OF_MONTH,-position);
		Log.e("hjq", "position:" + position);
		Log.e("hjq", "mCalendar:" + mCalendar.get(Calendar.DAY_OF_MONTH));
		LinearLayout lin1 = (LinearLayout) parentView
				.findViewById(R.id.weekone);
		LinearLayout lin2 = (LinearLayout) parentView
				.findViewById(R.id.weektwo);
		for (int i = 0; i < textViews.length; i++) {
			if (i<7) {
				textViews[i] = (TextView) lin1.getChildAt(i);
				textViews[i].setId(i);
			}else {
				textViews[i ] = (TextView) lin2.getChildAt(i-7);
				textViews[i].setId(i);
			}	
			if (i==0) {
				textViews[i].setText(""
						+mCalendar.get(Calendar.DAY_OF_MONTH));
			}else {
				mCalendar.add(Calendar.DAY_OF_MONTH, 1);
				textViews[i].setText(""
						+mCalendar.get(Calendar.DAY_OF_MONTH));
			}
			
		}
		month = (TextView) parentView.findViewById(R.id.month);
		// dateformat = new SimpleDateFormat("yyyy年MM月");
		// month.setText(dateformat.format(new Date()));
		month.setText("时间");
		changeItemBg(position );
	}

	private void initDate() {
		// TODO Auto-generated method stub
		for (int i = 0; i < textViews.length; i++) {
			if (i >= position && i < position + 3) {
				int index = i - position;
				if (i == position) {
					textViews[i ].setText("今天");
					textViews[i].setTextColor(Color.WHITE);
					textViews[i ].setBackgroundResource(R.drawable.timepick_today_bg);
				} else {
					textViews[i].setTextColor(Color.BLACK);
					textViews[i ].setText(""
							+ calendars[index].get(Calendar.DAY_OF_MONTH));
				}
				textViews[i ].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						changeItemBg(v.getId());
						mDateSelectedListener.onDateSelect(calendars[v.getId()
								- position ].getTime());
					}
				});
			}
		}

	}

	private void changeItemBg(int id) {
		for (int i = position; i < position+3; i++) {
			textViews[i].setBackgroundColor(Color.TRANSPARENT);
			textViews[i].setTextColor(Color.BLACK);
			textViews[i].setTextSize(20);
			textViews[i].getPaint().setFakeBoldText(true);
		}
		textViews[id].setTextColor(Color.WHITE);
		textViews[id].setBackgroundResource(R.drawable.timepick_today_bg);
	}

	public interface onDateSelectedListener {
		public void onDateSelect(Date date);
	}
}
