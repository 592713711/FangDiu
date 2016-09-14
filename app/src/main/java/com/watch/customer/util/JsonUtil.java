package com.watch.customer.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * json解析的的工具类
 * 
 * @author 黄家强
 */
public class JsonUtil {
	// 传入的参数
	public static final String NAME = "username";
	public static final String PHONE = "phone";
	public static final String SEX = "sex";
	public static final String PASSWORD = "password";
	public static final String REPASSWORD = "repassword";
	public static final String PAGE = "page";
	public static final String STOREID = "storeId";
	public static final String ORDER_TIME = "order_time";
	public static final String IS_ROOM = "is_room";
	public static final String TYPE = "type";
	public static final String TOTAL_PRICE = "total_price";
	public static final String DISHESLIST = "disheslist";
	public static final String USER_ID = "user_id";
	public static final String ORDER_ID="order_id";
	public static final String DETAIL="detail";
	public static final String SORCE="sorce";
	public static final String PROTOCOL="protocol";
	public static final String ABOUTUS="aboutus";
	public static final String VERSION="version";
	public static final String REG_ID="reg_id";
	public static final String ALIAS="alias";
	// 传出的参数
	public static final String CODE = "code";
	public static final String MSG = "msg";
	public static final String ID = "id";
	public static final String TYPE_ID = "type_id";
	public static final String CITY = "city";
	public static final String AVERAGE_BUY = "average_buy";
	public static final String START_HOURS = "start_hours";
	public static final String END_HOURS = "end_hours";
	public static final String ROUTES = "routes";
	public static final String ADDRESS = "address";
	public static final String STORE_PHONE = "store_phone";
	public static final String IS_ROOMS = "is_rooms";
	public static final String LONG = "long";
	public static final String LAT = "lat";
	public static final String LICENSE = "license";
	public static final String PERMIT = "permit";
	public static final String SHORT_MESSAGE = "short_message";
	public static final String SHORT_MESSAGE_REMARK = "short_message_remark";
	public static final String BANK_NAME = "bank_name";
	public static final String BANK_NUMBER = "bank_number";
	public static final String BANE_USERNAME = "bane_username";
	public static final String ZHIFUBAO = "zhifubao";
	public static final String DISCOUNT = "discount";
	public static final String CREATE_TIME = "create_time";
	public static final String IMAGE = "image";
	public static final String IMAGE_THUMB = "image_thumb";
	public static final String IS_SCHEDULE = "is_schedule";
	public static final String IS_POINT = "is_point";
	public static final String IS_GROUP = "is_group";
	public static final String IS_CARD = "is_card";
	public static final String IS_PAY = "is_pay";
	public static final String INTRO = "intro";
	public static final String USERNAME = "username";
	public static final String SERVICEID = "serviceId";
	public static final String STORE_ID="store_id";
	public static final String STORE_NAME="store_name";
	public static final String ORDER="order";
	public static final String DISHES_NAME="dishes_name";
	public static final String DISHES_TYPE="dishes_type";
	public static final String PRICE="price";
	public static final String DISHES_ID="dishes_id";
	public static final String COUNT="count";
	public static final String PEOPLE="people";
	public static final String STATUS="status";
	public static final String GROUP_ID="group_id";
	public static final String CHECKGROUP="checkgroup";
	public static final String GROUP_COUNT="group_count";
	public static final String STORENAME="storeName";
	public static final String TITLE="title";
	public static final String CONTENT="content";
	public static final String OLD_PRICE="old_price";
	public static final String START_TIME="start_time";
	public static final String END_TIME="end_time";
	public static final String SCORE="score";
	public static final String GROUP_PRICE="group_price";
	public static final String PATH="path";
	public static final String PAY_TYPE="pay_type";
	public static final String PAY_TIME="pay_time";
	public static final String BANK="bank";
	public static final String BANK_USER="bank_user";
	public static final String MAIL="mail";
	public static final String SHIBI="shibi";
	public static final String ADD="add";
	public static final String COMMENT_TIME="comment_time";
	public static final String ORDER_TYPE="order_type";
	public static final String USER_NAME="user_name";
	public static final String LOCALPAY="localPay";
	public static final String IS_LOCAL="is_local";
	public static final String SCHEDULE_MONEY="schedule_money";
	public static final String TEMP_DISTANCE="temp_distance";
	/**
	 * 从json对象中得到，对应的String数据
	 * 
	 * @param json
	 *            json对象
	 * @param keyname
	 *            对应的键名
	 * @return 对应的String类型的键值
	 */
	public static String getStr(JSONObject json, String keyname) {
		String result = "";
		try {
			result = json.getString(keyname);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 从json对象中得到，对应的integer数据
	 * 
	 * @param json
	 *            json对象
	 * @param keyname
	 *            对应的键名
	 * @return 对应的integer类型的键值
	 */
	public static int getInt(JSONObject json, String keyname) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			result = json.getInt(keyname);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 从json对象中得到，对应的long数据
	 * 
	 * @param json
	 *            json对象
	 * @param keyname
	 *            对应的键名
	 * @return 对应的long类型的键值
	 */
	public static Long getLong(JSONObject json, String keyname) {
		// TODO Auto-generated method stub
		Long result = null;
		try {
			result = json.getLong(keyname);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
