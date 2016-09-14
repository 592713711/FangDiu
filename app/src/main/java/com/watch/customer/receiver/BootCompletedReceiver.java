package com.watch.customer.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.uacent.watchapp.R;
import com.watch.customer.app.MyApplication;
import com.watch.customer.ui.FirstActivity;

/**
 * 开启 广播接收器
 * Created by zsg on 2016/9/1.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d("testBootCompletedReceiver", "recevie boot completed ... ");
        //判断是否开启开机提醒
        SharedPreferences mSharedPreferences = context.getSharedPreferences("watch_app_preference", 0);
        int boothint = mSharedPreferences.getInt("boothint", 1);
        if (boothint == 1)
            initNotification(context);
        //context.startService(new Intent(context, TestService.class));
    }

    public void initNotification(Context context) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_launcher) //设置通知小ICON
                .setTicker(context.getString(R.string.str_haitunhint)) //通知首次出现在通知栏，带上升动画效果的
                .setPriority(Notification.PRIORITY_MAX) //设置该通知优先级
                .setOngoing(false);//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;// 用户单击后通知消失

        //设置点击通知后执行的意图
        Intent openIntent = new Intent(context, FirstActivity.class);


        notification.contentIntent = PendingIntent.getActivity(context, 1
                , openIntent
                , PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.notification_item);

        notification.contentView = view;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);

    }
}