package com.watch.customer.model;

import java.io.Serializable;

public class Shop implements Serializable {
	private String id;
	private String name;
	private String type_name;
	private String city;
	private String phone;
	private String average_buy;
	private String start_hours;
	private String end_hours;
	private String routes;
	private String address;
	private String is_rooms;
	private String lon;
	private String lat;
	private String license;
	private String permit;
	private String short_message;
	private String short_message_remark;
	private String bank_name;
	private String bank_number;
	private String bane_username;
	private String zhifubao;
	private String discount;
	private String create_time;
	private String image;
	private String image_thumb;
	private String is_schedule;
	private String is_point;
	private String is_group;
	private String is_card;
	private String is_pay;
	private String intro;
	private String username;
	private String password;
	private String temp_distance;
	public Shop(String id, String name, String type_name, String city,
			String phone, String average_buy, String start_hours,
			String end_hours, String routes, String address, String is_rooms,
			String lon, String lat, String license, String permit,
			String short_message, String short_message_remark,
			String bank_name, String bank_number, String bane_username,
			String zhifubao, String discount, String create_time, String image,
			String image_thumb, String is_schedule, String is_point,
			String is_group, String is_card, String is_pay, String intro,
			String username, String password, String temp_distance) {
		super();
		this.id = id;
		this.name = name;
		this.type_name = type_name;
		this.city = city;
		this.phone = phone;
		this.average_buy = average_buy;
		this.start_hours = start_hours;
		this.end_hours = end_hours;
		this.routes = routes;
		this.address = address;
		this.is_rooms = is_rooms;
		this.lon = lon;
		this.lat = lat;
		this.license = license;
		this.permit = permit;
		this.short_message = short_message;
		this.short_message_remark = short_message_remark;
		this.bank_name = bank_name;
		this.bank_number = bank_number;
		this.bane_username = bane_username;
		this.zhifubao = zhifubao;
		this.discount = discount;
		this.create_time = create_time;
		this.image = image;
		this.image_thumb = image_thumb;
		this.is_schedule = is_schedule;
		this.is_point = is_point;
		this.is_group = is_group;
		this.is_card = is_card;
		this.is_pay = is_pay;
		this.intro = intro;
		this.username = username;
		this.password = password;
		this.temp_distance = temp_distance;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAverage_buy() {
		return average_buy;
	}
	public void setAverage_buy(String average_buy) {
		this.average_buy = average_buy;
	}
	public String getStart_hours() {
		return start_hours;
	}
	public void setStart_hours(String start_hours) {
		this.start_hours = start_hours;
	}
	public String getEnd_hours() {
		return end_hours;
	}
	public void setEnd_hours(String end_hours) {
		this.end_hours = end_hours;
	}
	public String getRoutes() {
		return routes;
	}
	public void setRoutes(String routes) {
		this.routes = routes;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIs_rooms() {
		return is_rooms;
	}
	public void setIs_rooms(String is_rooms) {
		this.is_rooms = is_rooms;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getPermit() {
		return permit;
	}
	public void setPermit(String permit) {
		this.permit = permit;
	}
	public String getShort_message() {
		return short_message;
	}
	public void setShort_message(String short_message) {
		this.short_message = short_message;
	}
	public String getShort_message_remark() {
		return short_message_remark;
	}
	public void setShort_message_remark(String short_message_remark) {
		this.short_message_remark = short_message_remark;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getBank_number() {
		return bank_number;
	}
	public void setBank_number(String bank_number) {
		this.bank_number = bank_number;
	}
	public String getBane_username() {
		return bane_username;
	}
	public void setBane_username(String bane_username) {
		this.bane_username = bane_username;
	}
	public String getZhifubao() {
		return zhifubao;
	}
	public void setZhifubao(String zhifubao) {
		this.zhifubao = zhifubao;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getImage_thumb() {
		return image_thumb;
	}
	public void setImage_thumb(String image_thumb) {
		this.image_thumb = image_thumb;
	}
	public String getIs_schedule() {
		return is_schedule;
	}
	public void setIs_schedule(String is_schedule) {
		this.is_schedule = is_schedule;
	}
	public String getIs_point() {
		return is_point;
	}
	public void setIs_point(String is_point) {
		this.is_point = is_point;
	}
	public String getIs_group() {
		return is_group;
	}
	public void setIs_group(String is_group) {
		this.is_group = is_group;
	}
	public String getIs_card() {
		return is_card;
	}
	public void setIs_card(String is_card) {
		this.is_card = is_card;
	}
	public String getIs_pay() {
		return is_pay;
	}
	public void setIs_pay(String is_pay) {
		this.is_pay = is_pay;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getTemp_distance() {
		return temp_distance;
	}
	public void setTemp_distance(String temp_distance) {
		this.temp_distance = temp_distance;
	}
	@Override
	public String toString() {
		return "Shop [id=" + id + ", name=" + name + ", type_name=" + type_name
				+ ", city=" + city + ", phone=" + phone + ", average_buy="
				+ average_buy + ", start_hours=" + start_hours + ", end_hours="
				+ end_hours + ", routes=" + routes + ", address=" + address
				+ ", is_rooms=" + is_rooms + ", lon=" + lon + ", lat=" + lat
				+ ", license=" + license + ", permit=" + permit
				+ ", short_message=" + short_message
				+ ", short_message_remark=" + short_message_remark
				+ ", bank_name=" + bank_name + ", bank_number=" + bank_number
				+ ", bane_username=" + bane_username + ", zhifubao=" + zhifubao
				+ ", discount=" + discount + ", create_time=" + create_time
				+ ", image=" + image + ", image_thumb=" + image_thumb
				+ ", is_schedule=" + is_schedule + ", is_point=" + is_point
				+ ", is_group=" + is_group + ", is_card=" + is_card
				+ ", is_pay=" + is_pay + ", intro=" + intro + ", username="
				+ username + ", password=" + password + ", temp_distance="
				+ temp_distance + "]";
	}
    
}
