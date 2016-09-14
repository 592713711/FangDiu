package com.watch.customer.http;

import org.apache.http.entity.StringEntity;

import android.content.Context;



/**
 * •处理异步Http请求，并通过匿名内部类处理回调结果,请求均位于非UI线程，不会阻塞UI操作 •通过线程池处理并发请求 •GET/POST
 * 参数（RequestParams） •多文件上传，没有额外的第三方库 •微型尺寸的开销到您的应用程序，只有25KB的一切
 * •自动智能优化的为参差不齐移动连接请求重试,自动处理连接断开时请求重连 •自动GZIP响应解码，支持超快速的请求
 * •二进制文件（图像等）与BinaryHttpResponseHandler下载 •内置响应解析成JSON JsonHttpResponseHandler
 * •持久性cookie存储，保存到您的应用程序的SharedPreferences里的cookies
 * 
 * @author Administrator
 * 
 */
public class CustomHttpUtil {
	// private static final String BASE_URL = "http://api.twitter.com/1/";
	private static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象
	static {
		client.setTimeout(11000); // 设置链接超时，如果不设置，默认为10s
	}

	public static void get(String urlString, AsyncHttpResponseHandler res) // 用一个完整url获取一个string对象
	{
		client.get(urlString, res);
	}

	public static void get(String urlString, RequestParams params, AsyncHttpResponseHandler res) // url里面带参数
	{
		client.get(urlString, params, res);
	}

	public static void get(String urlString, JsonHttpResponseHandler res) // 不带参数，获取json对象或者数组
	{
		client.get(urlString, res);
	}

	public static void get(String urlString, RequestParams params, JsonHttpResponseHandler res) // 带参数，获取json对象或者数组
	{
		client.get(urlString, params, res);
	}

	public static void get(String uString, BinaryHttpResponseHandler bHandler) // 下载数据使用，会返回byte数据
	{
		client.get(uString, bHandler);
	}

	public static void post(String uString, BinaryHttpResponseHandler bHandler) // 下载数据使用，会返回byte数据
	{
		client.post(uString, bHandler);
	}

	// post 带json参数，获取json对象或者数组
	public static void post(Context context, String url, StringEntity entity, String contentType, JsonHttpResponseHandler responseHandler) {
		client.post(context, url, entity, contentType, responseHandler);
	}

	/**
	 * 取消请求 Cancels any pending (or potentially active) requests associated with
	 * the passed Context.
	 * <p>
	 * &nbsp;
	 * </p>
	 * <b>Note:</b> This will only affect requests which were created with a
	 * non-null android Context. This method is intended to be used in the
	 * onDestroy method of your android activities to destroy all requests which
	 * are no longer required.
	 * 
	 * @param context
	 *            the android Context instance associated to the request.
	 * @param mayInterruptIfRunning
	 *            specifies if active requests should be cancelled along with
	 *            pending requests.如果为true则正在执行的线程将会中断，如果false
	 *            ，则会允许正在执行的任务线程执行完毕
	 */
	public static void cancelRequests(Context context, boolean mayInterruptIfRunning) {
		client.cancelRequests(context, mayInterruptIfRunning);
	}

	public static AsyncHttpClient getClient() {
		return client;
	}

	public static void post(String urlString, RequestParams params, AsyncHttpResponseHandler res) // url里面带参数
	{
		client.post(urlString, params, res);
	}

	public static void post(String urlString, AsyncHttpResponseHandler res) {
		client.post(urlString, res);
	}
}
