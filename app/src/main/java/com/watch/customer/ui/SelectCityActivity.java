package com.watch.customer.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uacent.watchapp.R;
import com.watch.customer.model.CityJson;
import com.watch.customer.model.ProvinceModel;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

public class SelectCityActivity extends BaseActivity implements OnClickListener {
	private ListView mylist;
	private List<ProvinceModel> provinceList=new ArrayList<ProvinceModel>(); // 地址列表
	private int pPosition;
	private boolean hasCity = true;
	private int listtype;
    private String cityName="";
    private Handler mHandler=new Handler(){
    	public void handleMessage(Message msg) {
    		String result=msg.obj.toString();
    		Log.e("hjq", result);
    		try {
				JSONArray array=new JSONArray(result);
				for (int i = 0; i < array.length(); i++) {
					JSONObject json=array.getJSONObject(i);
					String id=json.getString("id");
					String name=json.getString("name");
					String parent_id=json.getString("parent_id");
					if (json.getString("parent_id").equals("0")) {
							provinceList.add(new ProvinceModel(new CityJson(id, name, parent_id),new ArrayList<CityJson>()));
					}else {
						Log.e("hjq",json.toString());
						for (int j = 0; j < provinceList.size(); j++) {
							if (provinceList.get(j).getProvince().getId().equals(parent_id)) {
								ArrayList<CityJson> list=provinceList.get(j).getCity_list();
								list.add(new CityJson(id, name, parent_id));
								provinceList.get(j).setCity_list(list);
							}
						}
					
				}
				}
				pPosition = 0;
				 initList(1);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	};
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectcity);
		cityName=getIntent().getStringExtra("city");
		initFindView();
//		initData();
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result= null;
				try {
					result = HttpUtil.get(HttpUtil.URL_GETALLCITY);
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg=new Message();
				msg.obj=result;
				mHandler.sendMessage(msg);
			}
		});
//        initList(1);
	}

	public void initFindView() {
		((ImageView)findViewById(R.id.back)).setOnClickListener(this);
		mylist = (ListView) findViewById(R.id.area_list);
	}

	/**
	 * 获取地区raw里的地址xml内容
	 * */
	public StringBuffer getRawAddress() {
		InputStream in = getResources().openRawResource(R.raw.address);
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer();
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			br.close();
			isr.close();
			in.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return sb;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;

		default:
			break;
		}
	}

	/**
	 * 根据调用类型显示相应的数据列表
	 * 
	 * @param type
	 *            显示类型 1.省；2.市；3.县、区
	 */
	public void initList(final int type) {
		if (type == 1) {
			ProvinceAdapter pAdapter = new ProvinceAdapter(provinceList);
			mylist.setAdapter(pAdapter);

		} else if (type == 2) {
			CityAdapter cAdapter = new CityAdapter(provinceList.get(pPosition)
					.getCity_list());
			mylist.setAdapter(cAdapter);
		}

		mylist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Log.e("hjq", provinceList.get(position).getCity_list().size()+"");
				if (type == 1) {
					pPosition = position;
					// btn_province.setText(provinceList.get(position).getProvince());
					// 判断该省下是否有市级
					if (provinceList.get(position).getCity_list().size() < 1) {
						// btn_city.setText("");
						// btn_county.setText("");
						hasCity = false;
					} else {
						hasCity = true;
						listtype=2;
                        initList(2);
					}

				} else if (type == 2) {
					// btn_city.setText(provinceList.get(pPosition).getCity_list().get(position).getCity());
					Toast.makeText(SelectCityActivity.this, "您选择了"+provinceList.get(pPosition).getCity_list().get(position).getName(), Toast.LENGTH_LONG)
					.show();
					cityName=provinceList.get(pPosition).getCity_list().get(position).getName();
					Intent aintent = new Intent(SelectCityActivity.this,ShopListActivity.class);
					aintent.putExtra("city", cityName);
					PreferenceUtil.getInstance(SelectCityActivity.this).setString(PreferenceUtil.CITYID, provinceList.get(pPosition).getCity_list().get(position).getId());
					setResult(RESULT_OK,aintent);
					finish();

				}
			}
		});

	}

	class ProvinceAdapter extends BaseAdapter {
		public List<ProvinceModel> adapter_list;

		public ProvinceAdapter(List<ProvinceModel> list) {
			adapter_list = list;
		}

		@Override
		public int getCount() {
			return adapter_list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			TextView tv = new TextView(SelectCityActivity.this);
			tv.setPadding(30, 30, 30, 30);
			tv.setTextSize(20);
			tv.setText(adapter_list.get(position).getProvince().getName());
			return tv;
		}

	}

	class CityAdapter extends BaseAdapter {
		public ArrayList<CityJson> adapter_list;

		public CityAdapter(ArrayList<CityJson> list) {
			adapter_list = list;
		}

		@Override
		public int getCount() {
			return adapter_list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			TextView tv = new TextView(SelectCityActivity.this);
			tv.setPadding(30, 30, 30, 30);
			tv.setTextSize(20);
			tv.setText(adapter_list.get(position).getName());
			tv.setBackgroundResource(R.drawable.null_btn_text);
			return tv;
		}

	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (listtype==2) {
			listtype=1;
			initList(listtype);
		}else {
			Intent aintent = new Intent(SelectCityActivity.this,ShopListActivity.class);
			aintent.putExtra("city", cityName);
			setResult(RESULT_OK,aintent);
			finish();
			super.onBackPressed();
		}
	}

}