package com.watch.customer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.model.OrderDeal;
import com.watch.customer.util.CommonUtil;

public class ShopDealRefundActivity extends BaseActivity{
	private TextView orderid;
	private TextView create_time;
	private TextView dealname;
	private TextView dealprice;
	private TextView dealtotalprice;
	private Button zfbbtn;
	private Button bankcardbtn;
	private Button shibibtn;
    private OrderDeal mOrderDeal;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deal_refund);
		mOrderDeal=(OrderDeal)getIntent().getSerializableExtra("orderdeal");
		orderid=(TextView)findViewById(R.id.orderid);
		create_time=(TextView)findViewById(R.id.ordertime);
		dealname=(TextView)findViewById(R.id.dealname);
		dealprice=(TextView)findViewById(R.id.dealprice);
		dealtotalprice=(TextView)findViewById(R.id.dealtotalprice);
		zfbbtn = (Button) findViewById(R.id.zfb_btn);
		bankcardbtn = (Button) findViewById(R.id.bankcard_btn);
		shibibtn = (Button) findViewById(R.id.shibi_btn);
		if (mOrderDeal!=null) {
			orderid.setText(mOrderDeal.getOrder_id());
			create_time.setText(CommonUtil.getTimestr(mOrderDeal.getCreate_time()));
			dealname.setText(mOrderDeal.getTitle());
			dealprice.setText(getString(R.string.system_rmb)+mOrderDeal.getGroup_price());
			dealtotalprice.setText(getString(R.string.system_rmb)
					+CommonUtil.DouToStr1(Double.parseDouble(mOrderDeal.getGroup_price())*Integer.parseInt(mOrderDeal.getGroup_count())));
		}
		findViewById(R.id.back).setOnClickListener(this);
		zfbbtn.setOnClickListener(this);
		bankcardbtn.setOnClickListener(this);
		shibibtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.zfb_btn:
          
			break;
		case R.id.bankcard_btn:
			Intent mIntent=new Intent(this, ShopDealRefundDetailActivity.class);
			mIntent.putExtra("orderdeal", mOrderDeal);
			startActivity(mIntent);
			break;
		case R.id.shibi_btn:

			break;
		}
	}

}
