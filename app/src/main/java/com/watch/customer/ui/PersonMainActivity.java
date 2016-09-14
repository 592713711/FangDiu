package com.watch.customer.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.dao.UserDao;
import com.watch.customer.model.User;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.ImageLoaderUtil;
import com.watch.customer.util.PreferenceUtil;

public class PersonMainActivity extends BaseActivity implements OnClickListener {
	private LinearLayout personInfo;
	private LinearLayout personSetting;
	private LinearLayout personLogout;
	private ImageView headIv;
    private TextView phonetv;
    private TextView nametv;
    private User mUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_main);
		personInfo = (LinearLayout) findViewById(R.id.personmainInformation);
		personSetting = (LinearLayout) findViewById(R.id.personmainSetting);
		personLogout = (LinearLayout) findViewById(R.id.personmainLogout);
		headIv=(ImageView)findViewById(R.id.personmainPhoto);
		phonetv=(TextView)findViewById(R.id.personmainTextTelephone);
		nametv=(TextView)findViewById(R.id.personmainTextName);
		personInfo.setOnClickListener(this);
		personSetting.setOnClickListener(this);
		personLogout.setOnClickListener(this);
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mUser = new UserDao(this).queryById(PreferenceUtil.getInstance(this).getUid());
		if (mUser!=null) {
			phonetv.setText(mUser.getPhone());
			nametv.setText(mUser.getName());
			if (mUser.getImage().equals("")) {
				headIv.setImageResource(R.drawable.null_user);
			}else {
				ImageLoaderUtil.displayImage(HttpUtil.SERVER+mUser.getImage(), headIv, this);
			}
		}else {
            nametv.setText("");
            phonetv.setText("");
            headIv.setImageResource(R.drawable.null_user);
        }
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (PreferenceUtil.getInstance(this).getUid().equals("")) {
			startActivity(new Intent(this, AuthLoginActivity.class));
			return ;
		}
		switch (v.getId()) {
		case R.id.personmainInformation:
			startActivity(new Intent(this, PersonInfoActivity.class));
			break;

		case R.id.personmainSetting:
			startActivity(new Intent(this, PersonSettingActivity.class));
			break;

		case R.id.personmainLogout:
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			builder.setMessage("是否要注销当前账户")
			.setPositiveButton(R.string.system_sure, new DialogInterface.OnClickListener() {		
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
//					   new UserDao(PersonMainActivity.this).deleteAll();
			            PreferenceUtil.getInstance(PersonMainActivity.this).setUid("");
			            new UserDao(PersonMainActivity.this).deleteAll();
			            startActivity(new Intent(PersonMainActivity.this, AuthLoginActivity.class));
				}
			}).setNegativeButton(R.string.cancel, null).create().show();
           
			break;
		}
	}
}
