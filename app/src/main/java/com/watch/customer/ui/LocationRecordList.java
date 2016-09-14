package com.watch.customer.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.adapter.LocationRecordAdapter;
import com.watch.customer.dao.LocationDao;
import com.watch.customer.model.BtDevice;
import com.watch.customer.model.LocationRecord;
import com.watch.customer.xlistview.Menu;
import com.watch.customer.xlistview.MenuItem;
import com.watch.customer.xlistview.SlideAndDragListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 16-3-24.
 */
public class LocationRecordList extends BaseActivity implements  AdapterView.OnItemClickListener, SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemDeleteListener{

    private static final String TAG = "hjq";
    private SlideAndDragListView mListView;
    private ArrayList<LocationRecord> mListData;
    private LocationRecordAdapter mAdapter;
    private LocationDao mDao;
    private int mStatus;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loc_recordlist_activity);

        Intent intent = getIntent();
        mStatus = intent.getIntExtra("status", LocationRecord.LOST);


        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);

        mListView = (SlideAndDragListView)findViewById(R.id.loclist);

        TextView tv_title = (TextView) findViewById(R.id.device_text);
        if (mStatus == LocationRecord.LOST) {
            tv_title.setText(R.string.lost_hist);
        } else {
            tv_title.setText(R.string.loc_hist);
        }

        mDao = new LocationDao(LocationRecordList.this);
        mListView.setOnItemClickListener(LocationRecordList.this);
        initMenu();
        initUiAndListener();
        fillListData();
    }

    public void initUiAndListener() {
        mListView.setMenu(mMenu);
        mListView.setOnListItemLongClickListener(this);
        mListView.setOnDragListener(this, mListData);
        mListView.setOnListItemClickListener(this);
        mListView.setOnSlideListener(this);
        mListView.setOnMenuItemClickListener(this);
        mListView.setOnItemDeleteListener(this);
    }

    public void initMenu() {
        mMenu = new Menu(new ColorDrawable(Color.WHITE), true);
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn2_width) * 2)
                .setBackground(new ColorDrawable(Color.RED))
                .setText(getResources().getString(R.string.system_delete))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setTextColor(Color.BLACK)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_size))
                .build());
    }

    private void fillListData() {
        mListData = mDao.queryAll(mStatus);
        mAdapter = new LocationRecordAdapter(LocationRecordList.this, mListData);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LocationRecord r = mListData.get(position);
        String longlat = r.getLong_lat();
        String[] array = longlat.split(",");
        float longitude = 0f;
        float latitude = 0f;
        if (array.length == 2) {
            longitude = Float.parseFloat(array[0]);
            latitude = Float.parseFloat(array[1]);

            Intent intent = new Intent();
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);

            setResult(RESULT_OK, intent);
        }

        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }

            default:
                break;
        }


        super.onClick(v);
    }

    @Override
    public void onListItemLongClick(View view, int position) {
        // Toast.makeText(DeviceListActivity.this, "onItemLongClick   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onListItemLongClick   " + position);
    }

    @Override
    public void onDragViewStart(int position) {
        // Toast.makeText(DeviceListActivity.this, "onDragViewStart   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onDragViewStart   " + position);
    }

    @Override
    public void onDragViewMoving(int position) {
//        Toast.makeText(DemoActivity.this, "onDragViewMoving   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i("yuyidong", "onDragViewMoving   " + position);
    }

    @Override
    public void onDragViewDown(int position) {
        //Toast.makeText(DeviceListActivity.this, "onDragViewDown   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onDragViewDown   " + position);
    }

    @Override
    public void onListItemClick(View v, int position) {
        // Toast.makeText(DeviceListActivity.this, "onItemClick   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onListItemClick   " + position);

        if (position < 0) {
            return;
        }

        LocationRecord r = mListData.get(position);
        String longlat = r.getLong_lat();
        String[] array = longlat.split(",");
        float longitude = 0f;
        float latitude = 0f;
        if (array.length == 2) {
            longitude = Float.parseFloat(array[0]);
            latitude = Float.parseFloat(array[1]);

            Intent intent = new Intent();
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);

            setResult(RESULT_OK, intent);
        }

        finish();
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
        //   Toast.makeText(DeviceListActivity.this, "onSlideOpen   position--->" + position + "  direction--->" + direction, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onSlideOpen   " + position);
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
        //     Toast.makeText(DeviceListActivity.this, "onSlideClose   position--->" + position + "  direction--->" + direction, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onSlideClose   " + position);
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        Log.i(TAG, "onMenuItemClick   " + itemPosition + "   " + buttonPosition + "   " + direction);
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_NOTHING;
                    case 1:
                        return Menu.ITEM_SCROLL_BACK;
                }
                break;

            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0: {
                        LocationRecord r = mListData.get(itemPosition);
                        mDao.deleteById(r.getId());
                        mListData.remove(itemPosition);
                        mAdapter.notifyDataSetChanged();

                        return Menu.ITEM_SCROLL_BACK;
                    }

                    case 1: {
                        return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                    }
                }
        }

        return Menu.ITEM_NOTHING;
    }

    @Override
    public void onItemDelete(View view, int position) {

    }
}
