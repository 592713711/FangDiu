package com.watch.customer.model;

import java.io.Serializable;

public class OrderDeal implements Serializable{
	private String id;
	private String order_id;
	private String user_id;
	private String phone;
	private String store_id;
	private String create_time;
	private String people;
	private String is_room;
	private String order_time;
	private String type;
	private String status;
	private String checkgroup;
	private String group_count;
	private String group_id;
	private String userName;
	private String total_price;
	private String pay_type;
	private String pay_time;
	private String title;
	private String group_price;
	private String start_time;
	private String end_time;
	public OrderDeal(String id, String order_id, String user_id, String phone,
			String store_id, String create_time, String people, String is_room,
			String order_time, String type, String status, String checkgroup,
			String group_count, String group_id, String userName,
			String total_price, String pay_type, String pay_time, String title,
			String group_price, String start_time, String end_time) {
		super();
		this.id = id;
		this.order_id = order_id;
		this.user_id = user_id;
		this.phone = phone;
		this.store_id = store_id;
		this.create_time = create_time;
		this.people = people;
		this.is_room = is_room;
		this.order_time = order_time;
		this.type = type;
		this.status = status;
		this.checkgroup = checkgroup;
		this.group_count = group_count;
		this.group_id = group_id;
		this.userName = userName;
		this.total_price = total_price;
		this.pay_type = pay_type;
		this.pay_time = pay_time;
		this.title = title;
		this.group_price = group_price;
		this.start_time = start_time;
		this.end_time = end_time;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getPeople() {
		return people;
	}
	public void setPeople(String people) {
		this.people = people;
	}
	public String getIs_room() {
		return is_room;
	}
	public void setIs_room(String is_room) {
		this.is_room = is_room;
	}
	public String getOrder_time() {
		return order_time;
	}
	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCheckgroup() {
		return checkgroup;
	}
	public void setCheckgroup(String checkgroup) {
		this.checkgroup = checkgroup;
	}
	public String getGroup_count() {
		return group_count;
	}
	public void setGroup_count(String group_count) {
		this.group_count = group_count;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTotal_price() {
		return total_price;
	}
	public void setTotal_price(String total_price) {
		this.total_price = total_price;
	}
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	public String getPay_time() {
		return pay_time;
	}
	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getGroup_price() {
		return group_price;
	}
	public void setGroup_price(String group_price) {
		this.group_price = group_price;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	@Override
	public String toString() {
		return "Deal [id=" + id + ", order_id=" + order_id + ", user_id="
				+ user_id + ", phone=" + phone + ", store_id=" + store_id
				+ ", create_time=" + create_time + ", people=" + people
				+ ", is_room=" + is_room + ", order_time=" + order_time
				+ ", type=" + type + ", status=" + status + ", checkgroup="
				+ checkgroup + ", group_count=" + group_count + ", group_id="
				+ group_id + ", userName=" + userName + ", total_price="
				+ total_price + ", pay_type=" + pay_type + ", pay_time="
				+ pay_time + ", title=" + title + ", group_price="
				+ group_price + ", start_time=" + start_time + ", end_time="
				+ end_time + "]";
	}
	
}
