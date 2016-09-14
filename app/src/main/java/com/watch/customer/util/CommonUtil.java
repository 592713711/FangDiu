package com.watch.customer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class CommonUtil {
	public static String DouToStr1(double dd) {
		DecimalFormat df = new DecimalFormat("#.#");
		return df.format(dd);
	}

	public static String DouToStr2(double dd) {
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(dd);
	}

	public static int getWeekOfDate(Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return w;
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static double GetDistance(double lat1, double lng1, double lat2,
			double lng2) {
		double EARTH_RADIUS = 6378.137;
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	public static String getTimestr(String create_time) {
		String daString = create_time.substring(0, 17);
		int weekid = Integer.parseInt(create_time.substring(create_time
                .length() - 1));
		String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
		return daString.substring(0, 11) + weekDays[weekid] + " "
				+ daString.substring(11, 17);
	}
	public static String getOrderTimestr(String create_time) {
		String daString = create_time.substring(0, 17);
		int weekid = Integer.parseInt(create_time.substring(create_time
				.length() - 1));
		String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
		return daString.substring(0, 11) + " "
		+ daString.substring(11, 17)+" "+ weekDays[weekid];
	}
	public static String getOrderDataAndWeekstr(String create_time) {
		String daString = create_time.substring(0, 17);
		int weekid = Integer.parseInt(create_time.substring(create_time
				.length() - 1));
		String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
		return daString.substring(0, 11) + " "
		+ weekDays[weekid];
	}
	
	public static String getWeekstr(String str) {
		int weekid = Integer.parseInt(str)-1;
		String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
		return weekDays[weekid];
	}
	

	public static String getVersionName(Context context) {
		// 获取packagemanager的实例
		String version = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}
	public static String getOrderStatus(String status) {
		// 获取packagemanager的实例
		if (status.trim().equals("submit")) {
			return "已提交";
		}else if (status.trim().equals("ensure")) {
			return "已确认";
		}else if (status.trim().equals("pay")) {
			return "已支付";
		}else if (status.trim().equals("cancel")) {
			return "已取消";
		}else if (status.trim().equals("consumption")) {
			return "已消费";
		}else if (status.trim().equals("localpay")) {
			return "现金支付";
		}else if (status.trim().equals("finish")) {
			return "已完成";
		}else if (status.trim().equals("drawback")) {
			return "退款中";
		}else if (status.trim().equals("payback")) {
			return "退款完成";
		}
		
		return "";
	}
	public static String getDealOrderStatus(String status) {
		// 获取packagemanager的实例
		if (status.trim().equals("submit")) {
			return "未付款";
		}else if (status.trim().equals("pay")) {
			return "未消费";
		}else if (status.trim().equals("finish")) {
			return "已消费";
		}else if (status.trim().equals("drawback")) {
			return "退款中";
		}else if (status.trim().equals("failure")) {
			return "已失效";
		}else if (status.trim().equals("payback")) {
			return "退款完成";
		}
		
		return "";
	}


	public static void saveMyBitmap(Bitmap mBitmap, String bitName)
			throws IOException {
		File f = new File(Environment.getExternalStorageDirectory()
				+ "/ble_anti_lost/" + bitName + ".png");
		File dirFile = new File(Environment.getExternalStorageDirectory()
				+ "/ble_anti_lost/");
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}

		if (f.exists()) {
			f.delete();
		}
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public static Bitmap getImagePath(String bitName) {
		String path = "";
		File f = new File(Environment.getExternalStorageDirectory()
				+ "/ble_anti_lost/" + bitName + ".png");
		try {
			if (f.exists()) {
				path = f.getAbsolutePath();
				FileInputStream fis = new FileInputStream(path);
				return BitmapFactory.decodeStream(fis);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

    public static String getImageFilePath(String bitmapName) {
        String path = "";
        File f = new File(Environment.getExternalStorageDirectory()
                + "/ble_anti_lost/" + bitmapName + ".png");

        if (f.exists()) {
            path = f.getAbsolutePath();
            return path;
        }

        return null;
    }

	/**
	 * 提交表单并上传文件到网站
	 * 
	 * @param url
	 *            提交的接口
	 * @param param
	 *            参数 <键，值>
	 * @param bitmap
	 *            图片内容
	 */
	public static String postForm(String url, Map<String, String> param) {
		try {
//			url = "http://localhost:4657" + "/api/SaveNeed";
			HttpPost post = new HttpPost(url);
			HttpClient client = new DefaultHttpClient();
			String BOUNDARY = "*****"; // 边界标识
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE, BOUNDARY, null);
			if (param != null && !param.isEmpty()) {
				entity.addPart(JsonUtil.IMAGE, new FileBody(new File(param.get(JsonUtil.IMAGE))));
				Log.e("hjq", param.get(JsonUtil.IMAGE));
				entity.addPart(JsonUtil.USER_ID, new StringBody(
						param.get(JsonUtil.USER_ID), Charset.forName("UTF-8")));
				Log.e("hjq", param.get(JsonUtil.USER_ID));
			}
			post.setEntity(entity);

			HttpResponse response;

			response = client.execute(post);

			int stateCode = response.getStatusLine().getStatusCode();
			StringBuffer sb = new StringBuffer();
			if (stateCode == HttpStatus.SC_OK) {
				HttpEntity result = response.getEntity();
				if (result != null) {
					InputStream is = result.getContent();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(is));
					String tempLine;
					while ((tempLine = br.readLine()) != null) {
						sb.append(tempLine);
					}
				}
			}
			post.abort();

			return sb.toString();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };


	public static String generateShortUuid() {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(chars[x % 0x3E]);
		}
		return shortBuffer.toString();
	}
}
