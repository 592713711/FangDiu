package com.watch.customer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.watch.customer.app.MyApplication;
import com.watch.customer.ui.AuthLoginActivity;

/**
 * 访问网络的工具类
 * 
 * @author 黄家强
 */
public class HttpUtil {
	public static String IP="112.74.23.39:3000";
	public static String SERVER="http://" + IP + "/";
	public static String URL_LOGIN = "http://" + IP + "/server/User/login";
	public static String URL_REGISTER = "http://" + IP + "/server/User/register";
	public static String URL_CHECKMOBILE = "http://"+IP+"/server/User/getcheckmobile";
	public static String URL_FORGETPSDCHECKMOBILE = "http://"+IP+"/server/User/forgetPsdCheckMobile";
	public static String URL_EDITPROFILE = "http://"+IP+"/server/User/eidtprofile";
	public static String URL_FORGETPASSWORD = "http://"+IP+"/server/User/forgetPassword";
	public static String URL_RESETPASSWORD = "http://"+IP+"/server/User/resetPassword";
	public static String URL_ADDORDELUSERHOUSE = "http://"+IP+"/server/User/addOrDeluserhouse";
	public static String URL_MYHOUSE = "http://"+IP+"/server/User/myhouse";
	public static String URL_ISHOUSE = "http://"+IP+"/server/User/ishouse";
	public static String URL_USERCOMMENT = "http://"+IP+"/server/User/userComment";
	public static String URL_STORECOMMENT = "http://"+IP+"/server/User/storeComment";
	public static String URL_STATICPAGE = "http://"+IP+"/server/User/staticPage";
	public static String URL_USERFEEDBACK = "http://"+IP+"/server/User/userfeedback";
	public static String URL_UPLOADUSERIMAGE = "http://"+IP+"/server/User/uploadUserImage";
	public static String URL_GETSHIBIBYUSERID = "http://"+IP+"/server/User/getShibiByUserId";
	public static String URL_USESHIBIPAY = "http://"+IP+"/server/User/useShibiPay";
	public static String URL_ADDSHIBIBYUSERID = "http://"+IP+"/server/User/addshibiByUserId";
	public static String URL_TOTALSHIBIBYUSERID = "http://"+IP+"/server/User/totalShibiByUserId";
	
	public static String URL_TYPELIST = "http://"+IP+"/server/Store/typeList";
	public static String URL_STORELIST = "http://"+IP+"/server/Store/storeList";
	public static String URL_FINDSTOREBYNAME = "http://"+IP+"/server/Store/findStoreByName";
	public static String URL_FINDSTOREBYALL = "http://"+IP+"/server/Store/findStoreByAllService";
	public static String URL_FINDDISHESBYPOINT = "http://"+IP+"/server/Store/findDishesBypoint";
	public static String URL_SEARCHDISHESBYSTORE = "http://"+IP+"/server/Store/searchDishesByStore";
	public static String URL_SUBMITORDER = "http://"+IP+"/server/Store/submitOrder";
	public static String URL_SUBMITLOCALORDER = "http://"+IP+"/server/Store/submitLocalOrder";
	public static String URL_GETGROUPBYSTORE = "http://"+IP+"/server/Store/getGroupByStore";
	public static String URL_ORDERLIST = "http://"+IP+"/server/Store/orderList";
	public static String URL_ORDERDETAIL = "http://"+IP+"/server/Store/orderDetail";
	public static String URL_SUBMITGROUPORDER = "http://"+IP+"/server/Store/submitGroupOrder";
	public static String URL_DELORDERBYORDERID = "http://"+IP+"/server/Store/delOrderByOrderId";
	public static String URL_GETGROUPBYORDERID= "http://"+IP+"/server/Order/getGroupByOrderId";
	public static String URL_DRAWBACKGROUP= "http://"+IP+"/server/Order/drawbackGroup";
	public static String URL_LOCALPAYORDER= "http://"+IP+"/server/Order/localPayOrder";
	public static String URL_SUBMITLOCALORDERPAY= "http://"+IP+"/server/Order/submitLocalOrderPay";
	public static String URL_UPDATEORDERSTATUS= "http://"+IP+"/server/Order/updateOrderStatus";
	public static String URL_ADDFOODBYORDERID= "http://"+IP+"/server/Order/addFoodByOrderId";
	public static String URL_ANDROIDUPDATE= "http://"+IP+"/ble/updateSoftware";
	public static String URL_GETALLCITY= "http://"+IP+"/server/Other/getAllcity";
	public static String URL_INITAPP= "http://"+IP+"/server/Other/initApp";
	public static String URL_GETREGIDANDUSERID= "http://"+IP+"/server/Push/getRegIdAndUserId";
	public static String URL_POINTORSCHEDULELOCALPAYORDER= "http://"+IP+"/server/Order/PointOrScheduleLocalPayOrder";
	public static String URL_GETSTOREDISCOUNTBYSTOREID = "http://"+IP+"/server/Store/getStoreDiscountByStoreId";
	/**
	 * 用post方式来访问网络
	 * 
	 * @param url
	 *            要访问的网址
	 * @param nameValuePairs
	 *            需要的参数
	 * @return 网络返回的结果数据
	 */
	public static String post(String url, NameValuePair... nameValuePairs) throws IOException, ClientProtocolException {
		HttpClient httpClient = new DefaultHttpClient();
		String msg = "";
		HttpPost post = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (int i = 0; i < nameValuePairs.length; i++) {
			params.add(nameValuePairs[i]);
		}

		String token = MyApplication.getInstance().mToken;
		Log.e("hjq", "token = " + token);
		try {
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            post.addHeader("Authorization", "Bearer " + token);
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
			HttpResponse response = httpClient.execute(post);
			int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				msg = EntityUtils.toString(response.getEntity());
			} else if (status == HttpStatus.SC_UNAUTHORIZED) {
				JSONObject json = new JSONObject();
				json.put("code", 0);
				json.put("msg", "password error");
				msg = json.toString();
			} else {
				Log.e("hjq", "网络请求失败");
                throw new IOException("post error");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg = e.getMessage();
			throw new ClientProtocolException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg = e.getMessage();
			throw new IOException(e);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return msg;
	}

	/**
	 * 用get方式来访问网络
	 * 
	 * @param url
	 *            要访问的网址
	 * @return 网络返回的结果数据
	 */
	public static String get(String url) throws IOException, ClientProtocolException {
		HttpClient httpClient = new DefaultHttpClient();

		String msg = "";
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (entity != null) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(entity.getContent()));
					String line = null;
					while ((line = br.readLine()) != null) {
						msg += line;
					}
				}
			} else {
				Log.e("hjq", "网络请求失败");
                throw new IOException("get error");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new ClientProtocolException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new IOException(e);
		}

		return msg;
	}

	private static String getCookie(Context context) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		String cookie = cookieManager.getCookie("cookie");
		Log.e("hjq", "getCookie=" + cookie);
		return cookie;
	}

	public static String getURlStr(String url, NameValuePair... namevalues) {
		StringBuilder result = new StringBuilder(url + "?");
		for (int i = 0; i < namevalues.length; i++) {
			if (i != 0) {
				result.append("&" + namevalues[i].getName() + "="
						+ namevalues[i].getValue());
			} else {
				result.append(namevalues[i].getName() + "="
						+ namevalues[i].getValue());
			}

		}
		return result.toString();

	}
}
