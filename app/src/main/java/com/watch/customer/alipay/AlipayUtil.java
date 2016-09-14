package com.watch.customer.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.alipay.android.app.sdk.AliPay;
import com.uacent.watchapp.R;

import java.net.URLEncoder;


@SuppressWarnings("deprecation")
public class AlipayUtil {

	// private static final String TAG = "AlipayUtil";
	private Activity context;
	private Handler mHandler;
	public static final int RQF_PAY = 99;

	/**
	 * String(256) 商品的标题/交易标题/订单标题/订单关键字等。 该参数最长为128个汉字。
	 */
	private String subject;// 商品or订单名称
	/**
	 * String(1000) 对一笔交易的具体描述信息。 如果是多种商品，请将商品描述字符串累加传给body。
	 */
	private String body;// 商品详情
	/**
	 * Number 该笔订单的资金总额，单位为RMB-Yuan。 取值范围为[0.01，100000000.00]，精确到小数点后两位。
	 */
	private String price;// 总金额
	private String out_trade_no;// 传给支付宝的订单号
	private String url;//回调函数

	public AlipayUtil(Activity context, Handler mHandler, String subject, String body, String price, String out_trade_no,String url) {
		this.context = context;
		this.mHandler = mHandler;
		this.subject = subject;
		this.body = body;
		this.price = price;
		this.out_trade_no = out_trade_no;
		this.url = url;
	}

	public void doAlipay() {
		try {
			String info = getNewOrderInfo();
			String sign = Rsa.sign(info, Keys.PRIVATE);
			sign = URLEncoder.encode(sign);
			// 签名 +签名方式
			info += "&sign=\"" + sign + "\"&" + getSignType();

			// start the pay.
			// Log.i(TAG, "info = " + info);
			final String orderInfo = info;
			new Thread() {
				public void run() {
					// 获取Alipay对象，构造参数为当前Activity和Handler实例对象
					AliPay alipay = new AliPay(context, mHandler);
					// 设置为沙箱模式，不设置默认为线上环境
					// alipay.setSandBox(true);
					// 调用pay方法，将订单信息传入
					String result = alipay.pay(orderInfo);
					// Log.i(TAG, "result = " + result);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();
		} catch (Exception ex) {
			ex.printStackTrace();
//			Toast.makeText(context, text, duration)(context, context.getResources().getString(R.string.remote_call_failed));
		}
	}

	/*
	 * private String getOutTradeNo() { SimpleDateFormat format = new
	 * SimpleDateFormat("MMddHHmmss"); Date date = new Date(); String key =
	 * format.format(date);
	 * 
	 * java.util.Random r = new java.util.Random(); key += r.nextInt(); key =
	 * key.substring(0, 15); //Log.i(TAG, "outTradeNo: " + key); return key; }
	 */

	private String getNewOrderInfo() {
		StringBuilder sb = new StringBuilder();
		// 合作商户ID
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		// 订单号
		sb.append("\"&out_trade_no=\"");
		// sb.append(getOutTradeNo());
		sb.append(out_trade_no);
		// 订单or商品名称
		sb.append("\"&subject=\"");
		sb.append(subject);
		// 商品详情
		sb.append("\"&body=\"");
		sb.append(body);
		// 总金额
		sb.append("\"&total_fee=\"");
		sb.append(price);
		// 服务器异步通知页面路径 (可空)
		sb.append("\"&notify_url=\"");
		// 网址需要做URL编码
//		String url = DConfig.getUrl(DConfig.alipayNotify);
		sb.append(URLEncoder.encode(url));
//		sb.append(URLEncoder.encode("http://115.29.225.109:8080/lymc/main/orderMainController/alipayNotify.do"));
		// 接口名称。固定值。
		sb.append("\"&service=\"mobile.securitypay.pay");
		// 商户网站使用的编码格式，固定为utf-8。
		sb.append("\"&_input_charset=\"UTF-8");
		//
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		// 支付类型。默认值为：1（商品购买）。
		sb.append("\"&payment_type=\"1");
		// 卖家支付宝账号
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		sb.append("\"&app_id=\"");
		sb.append(context.getString(R.string.app_name));

		// 商品展示的超链接。预留参数。 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		// 设置未付款交易的超时时间，一旦超时，该笔交易就会自动被关闭。 取值范围：1m～15d。
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}
}