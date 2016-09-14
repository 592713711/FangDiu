package com.watch.customer.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.app.MyApplication;


public class UpdateManager {
	private Context mContext;
	// 提示语
	private String updateMsg;
	// 返回的安装包url
	private String apkUrl;
	private Dialog noticeDialog;
	private Dialog downloadDialog;
	/* 下载包安装路径 */
	private static final String savePath = Environment
			.getExternalStorageDirectory().getPath() + "/btantilost/";
	private static final String apkname = "customer.apk";
	private static final String saveFileName = savePath + apkname;
	/* 进度条与通知ui刷新的handler和msg常量 */
	private boolean ishide = false;
	private ProgressBar mProgress;
	private TextView mTextView;
	private Notification downloadNotification;
	private NotificationManager downloadNM;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private int downNotiID = 21;
	private int progress = 0;
	private boolean interceptFlag = false;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				if (ishide) {
					updateProgress(progress);
				} else {
					mProgress.setProgress(progress);
				}
				break;
			case DOWN_OVER:
				if (ishide) {
					downloadNM.cancel(downNotiID);
				} else {
					downloadDialog.cancel();
				}
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public UpdateManager(Context context, String updatemsg, String dlurl) {
		this.mContext = context;
		this.updateMsg = getUpdateMsg(updatemsg);
		this.apkUrl = dlurl;
	}

	public void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.software_update);
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.update, null);
		mTextView = (TextView) v.findViewById(R.id.update_msg);
		mTextView.setText(updateMsg);
		builder.setView(v);
		builder.setPositiveButton(R.string.download, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				noticeDialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton(R.string.system_exit, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MyApplication.getInstance().exit();
			}
		}).setCancelable(false);
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	private String getUpdateMsg(String msg) {
		// TODO Auto-generated method stub
		if (!msg.equals("") && msg.length() > 0) {
			msg = msg.replace(";", "\n");
		}
		return msg;
	}

	public  void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.software_update);
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.update, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);
		mTextView = (TextView) v.findViewById(R.id.update_msg);
		mTextView.setText(updateMsg);
		mProgress.setVisibility(View.VISIBLE);
		builder.setView(v).setCancelable(false);
		builder.setPositiveButton(R.string.hide, new OnClickListener() {
		 @Override
		 public void onClick(DialogInterface dialog, int which) {
			 downloadDialog.dismiss();
			 initNotif();
			 ishide = true;
		 }
		});
		builder.setNegativeButton(R.string.system_cancel, new OnClickListener() {
		 @Override
		 public void onClick(DialogInterface dialog, int which) {
			 downloadDialog.dismiss();
			 interceptFlag = true;
		 }
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		downloadApk();
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(apkUrl);

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];
				Timer mTimer = new Timer();
				mTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mHandler.sendEmptyMessage(DOWN_UPDATE);
					}
				}, 0, 1000);

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度

					// mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_UPDATE);
						mHandler.sendEmptyMessage(DOWN_OVER);
						mTimer.cancel();
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.
				mTimer.cancel();
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	@SuppressWarnings("deprecation")
	public void initNotif() {
		downloadNM = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		downloadNotification = new Notification(/*R.drawable.ic_launcher*/android.R.drawable.sym_def_app_icon, apkname
				+ mContext.getResources().getString(R.string.download),
				System.currentTimeMillis());
		downloadNotification.contentView = new RemoteViews(
				mContext.getPackageName(), R.layout.notification);
		// 显示下载的包名
		downloadNotification.contentView.setTextViewText(R.id.down_tv, apkname);
		// 显示下载的进度
		downloadNotification.contentView.setTextViewText(R.id.down_rate, "0%");
		downloadNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		downloadNM.notify(downNotiID, downloadNotification);
	}

	public void updateProgress(int progress) {
		downloadNotification.contentView.setTextViewText(R.id.down_rate,
				progress + "%");
		downloadNM.notify(downNotiID, downloadNotification);

	}

	/**
	 * 下载apk
	 * 
	 * @param url
	 */

	private void downloadApk() {
		ThreadPoolManager.getInstance().addTask(mdownApkRunnable);
	}

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}