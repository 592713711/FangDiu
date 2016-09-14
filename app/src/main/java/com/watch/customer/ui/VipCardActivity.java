package com.watch.customer.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.model.Shop;

public class VipCardActivity extends BaseActivity {
   private Shop mShop;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_vipcard);
		mShop=(Shop)getIntent().getSerializableExtra("object");
		((TextView)findViewById(R.id.title)).setText(mShop.getName());
		findViewById(R.id.back).setOnClickListener(this);
	}
@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}
}
