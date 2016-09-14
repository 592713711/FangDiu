package com.watch.customer.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.device.BluetoothLeClass;
import com.watch.customer.model.BtDevice;
import com.watch.customer.util.CommonUtil;
import com.watch.customer.util.ImageLoaderUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 16-3-7.
 */
public class DeviceListAdapter extends BaseAdapter {
    private static final long ANIMATION_DURATION = 300;
    private Context context;
    private ArrayList<BtDevice> data;
    private int mId;
    OnItemClickCallback mCallback;

    public DeviceListAdapter(Context context, ArrayList<BtDevice> list, OnItemClickCallback listener) {
        this.context = context;
        data = list;
        mId = 0;
        mCallback = listener;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public int getmId() {
        return mId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holderView;

        if (convertView == null) {
            holderView = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.device_item, null);
            holderView.image = (ImageView) convertView.findViewById(R.id.device_image);
            holderView.name = (TextView) convertView.findViewById(R.id.device_name);

            holderView.status = (TextView) convertView.findViewById(R.id.device_status);

            holderView.button = (Button) convertView.findViewById(R.id.device_button);

            holderView.right_arrow = (ImageView) convertView.findViewById(R.id.right_arrow);

            convertView.setTag(holderView);
        } else {
            holderView = (ViewHolder) convertView.getTag();
        }

        String path = CommonUtil.getImageFilePath(data.get(position).getThumbnail());
        // Log.e("hjq", "path = " + path);
        if (path != null) {
            ImageLoaderUtil.displayImage("file://" + path, holderView.image, context);
        } else {
            Drawable d = context.getResources().getDrawable(R.drawable.device_icon);
            holderView.image.setImageDrawable(d);
        }

        holderView.name.setText(data.get(position).getName());

        int status = data.get(position).getStatus();
        Log.d("hjq", "postion " + position + " status = " + status);

        if (status == BluetoothLeClass.BLE_STATE_CONNECTED ||
                status == BluetoothLeClass.BLE_STATE_ALERTING) {
            holderView.status.setText(R.string.connected);
        } else if (status == BluetoothLeClass.BLE_STATE_CONNECTING) {
            holderView.status.setText(R.string.connecting);
        } else {
            holderView.status.setText(R.string.disconnected);
        }

        holderView.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String address = data.get(position).getAddress();
                if (address == null) {
                    return;
                }
                mId = position;

                mCallback.onButtonClick(view, position);
            }
        });

        switch (status) {
            case BluetoothLeClass.BLE_STATE_CONNECTED: {
                holderView.button.setText(R.string.alert);
                if (data.get(position).isAlertService()) {
                    holderView.button.setEnabled(true);
                } else {
                    holderView.button.setEnabled(false);
                }

                break;
            }

            //断开报警
            case BluetoothLeClass.BLE_STATE_BREAK:
            case BluetoothLeClass.BLE_STATE_ALERTING: {
                holderView.button.setText(R.string.stop_alert);
                holderView.button.setEnabled(true);
                break;
            }

            case BluetoothLeClass.BLE_STATE_CONNECTING: {
                break;
            }

            case BluetoothLeClass.BLE_STATE_INIT:
            default: {
                holderView.button.setText(R.string.connect);
                holderView.button.setEnabled(true);
                break;
            }
        }

        holderView.right_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   Log.d("hjq", "gggg");
                mId = position;
                mCallback.onRightArrowClick(position);
            }
        });

        return convertView;
    }

    private void showCell(final View v, final int index) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        };

        collapse(v, al);
    }

    private void collapse(final View v, Animation.AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al != null) {
            anim.setAnimationListener(al);
        }

        anim.setDuration(ANIMATION_DURATION);
        v.startAnimation(anim);
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void updateDataSet(int position) {
        Log.i("hjq", "update position =" + position);
        data.remove(position);
        notifyDataSetChanged();
    }

    private final static class ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView status;
        public Button button;
        public ImageView right_arrow;
    }

    public interface OnItemClickCallback {
        void onButtonClick(View v, int position);

        void onRightArrowClick(int postion);
    }

    ;
}
