package com.watch.customer.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.uacent.watchapp.R;

public class MenuWaysActivity extends Activity implements OnClickListener{
    private Button bookmenubtn,shopmenubtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menuways);
		bookmenubtn=(Button)findViewById(R.id.bookmenubtn);
		shopmenubtn=(Button)findViewById(R.id.shopmenubtn);
		bookmenubtn.setOnClickListener(this);
		shopmenubtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bookmenubtn:
			startActivity(new Intent(this, ShopMenuActivity.class));
			break;
	    case R.id.shopmenubtn:
			
			break;

		default:
			break;
		}
	}
	
}
