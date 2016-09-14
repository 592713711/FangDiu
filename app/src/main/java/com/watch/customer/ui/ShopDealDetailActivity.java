package com.watch.customer.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.model.Deal;
import com.watch.customer.model.Shop;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.ImageLoaderUtil;
import com.watch.customer.util.PreferenceUtil;

public class ShopDealDetailActivity extends BaseActivity implements OnClickListener {
	private Button buybtn;
    private ImageView imageiv;
    private TextView nametv;
    private TextView pricetv;
    private TextView originpricetv;
    private TextView refundtv;
    private TextView hassellednumtv;
    private TextView scoretv;
    private TextView commentnumtv;
    private TextView detailnametv;
    private TextView detailaddresstv;
    private ImageView detailphoneim;
    private TextView buynotice_timetv;
    private TextView buynotice_ruletv;
    private Deal mDeal;
    private Shop mShop;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deal_detail);
		mDeal=(Deal)getIntent().getSerializableExtra("deal");
		mShop=(Shop)getIntent().getSerializableExtra("object");
		initView();
		initData();
		findViewById(R.id.back).setOnClickListener(this);
		detailphoneim.setOnClickListener(this);
		buybtn.setOnClickListener(this);
	}

	private void initData() {
		// TODO Auto-generated method stub
		ImageLoaderUtil.getImageLoader(this).displayImage(HttpUtil.SERVER+mDeal.getImage(), imageiv);
		nametv.setText(mDeal.getTitle());
		pricetv.setText(mDeal.getGroup_price());
		originpricetv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		originpricetv.setText(mDeal.getOld_price());
		detailnametv.setText(mDeal.getStore_name());
		detailaddresstv.setText(mDeal.getAddress());
		scoretv.setText(mDeal.getScore());
		originpricetv.setText(mDeal.getOld_price());
		buynotice_timetv.setText(mDeal.getStart_time().substring(0,10)+"~"+mDeal.getEnd_time().substring(0,10));
		buynotice_ruletv.setText(mDeal.getContent());
	}

	private void initView() {
		// TODO Auto-generated method stub
		buybtn = (Button) findViewById(R.id.deal_buy_btn);
		  imageiv=(ImageView)findViewById(R.id.image);
	      nametv=(TextView)findViewById(R.id.dealname);
	      pricetv=(TextView)findViewById(R.id.deal_price);
	      originpricetv=(TextView)findViewById(R.id.old_price);
	      refundtv=(TextView)findViewById(R.id.deal_refund);
	      hassellednumtv=(TextView)findViewById(R.id.hassellednum);
	      scoretv=(TextView)findViewById(R.id.dealscore);
	      commentnumtv=(TextView)findViewById(R.id.recommentnum);
	      detailnametv=(TextView)findViewById(R.id.detail_title);
	      detailaddresstv=(TextView)findViewById(R.id.detail_address);
	      detailphoneim=(ImageView)findViewById(R.id.deal_phonebtn);
	      buynotice_timetv=(TextView)findViewById(R.id.deal_time);
	      buynotice_ruletv=(TextView)findViewById(R.id.deal_rule);
	      
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.deal_phonebtn:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final String phonestr = mDeal.getStore_phone();
			if (phonestr.equals("")) {
				return ;
			}
			builder.setTitle("电话").setMessage("拨号到"+phonestr)
			  .setPositiveButton(R.string.system_sure, new DialogInterface.OnClickListener() {	
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Intent intent=new Intent();
					intent.setAction("android.intent.action.CALL");
					intent.setData(Uri.parse("tel:"+phonestr));
					startActivity(intent);
				}
			}).setNegativeButton(R.string.system_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
			}).create().show();
			
			break;
		case R.id.deal_buy_btn:
			if (PreferenceUtil.getInstance(this).getUid().equals("")) {
				startActivity(new Intent(this, AuthLoginActivity.class));
			}else {
				Intent resultin=new Intent(this, ShopDealResultActivity.class);
				resultin.putExtra("deal", mDeal);
				startActivity(resultin);
			}
			
			break;
		default:
			break;
		}
		
	}

}
