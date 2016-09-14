package com.watch.customer.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.dao.BtDeviceDao;
import com.watch.customer.model.BtDevice;
import com.watch.customer.model.LocationRecord;
import com.watch.customer.util.CommonUtil;
import com.watch.customer.util.ImageLoaderUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 16-3-24.
 */
public class LocationRecordAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<LocationRecord> mListData;
    private Map<String, List<String>> mMap = new HashMap<>();
    public LocationRecordAdapter(Context context, ArrayList<LocationRecord> data) {
        mContext = context;
        mListData = data;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return  mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holderView;

        if (convertView == null) {
            holderView = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.location_item, null);
            holderView.image = (ImageView) convertView.findViewById(R.id.iv_icon);
            holderView.name = (TextView) convertView.findViewById(R.id.tv_name);
            holderView.datatime = (TextView) convertView.findViewById(R.id.tv_datetime);
            holderView.longlat = (TextView) convertView.findViewById(R.id.tv_longlat);
            holderView.address = (TextView) convertView.findViewById(R.id.tv_address);

            convertView.setTag(holderView);
        } else {
            holderView = (ViewHolder)convertView.getTag();
        }
        
        String btaddress = mListData.get(position).getBtaddress();
        List<String> list = mMap.get(btaddress);
        if (list == null && (list = queryBluedevice(btaddress)) != null) {
            mMap.put(btaddress, list);
        }

        String name = null;
        String path = null;
        if (list != null) {
            String thumbnail = list.get(0);
            name = list.get(1);
            path =  CommonUtil.getImageFilePath(thumbnail);
        }
        // Log.e("hjq", "path = " + path);
        if (path != null) {
            ImageLoaderUtil.displayImage("file://" + path, holderView.image, mContext);
        }  else {
            Drawable d = mContext.getResources().getDrawable(R.drawable.device_icon);
            holderView.image.setImageDrawable(d);
        }

        if (name != null) {
            holderView.name.setText(name);
        } else {
            holderView.name.setText("unkown");
        }

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime =  f.format(mListData.get(position).getDt_time());
        String longlat = mListData.get(position).getLong_lat();
        String address = mListData.get(position).getAddress();

        holderView.datatime.setText(datetime);
        holderView.longlat.setText(mContext.getResources().getText(R.string.location) + longlat);
        holderView.address.setText(address);

        return convertView;
    }

    private List<String> queryBluedevice(String btaddress) {
        BtDeviceDao dao = new BtDeviceDao(mContext);
        BtDevice d = dao.queryById(btaddress);
        if (d != null) {
            List<String> list = new ArrayList<String>(2);
            list.add(0, d.getThumbnail());
            list.add(1, d.getName());

            return list;
        }

        return null;
    }

    private final static class ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView datatime;
        public TextView longlat;
        public TextView address;
    }

}
