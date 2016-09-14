package com.watch.customer.model;

import java.io.Serializable;

public class Order implements Serializable{
	private String id;
	private String order_id;
	private String user_id;
	private String store_id;
	private String create_time;
	private String phone;
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
	public Order(String id, String order_id, String user_id, String store_id,
			String create_time, String phone, String people, String is_room,
			String order_time, String type, String status, String checkgroup,
			String group_count, String group_id, String userName,
			String total_price) {
		super();
		this.id = id;
		this.order_id = order_id;
		this.user_id = user_id;
		this.store_id = store_id;
		this.create_time = create_time;
		this.phone = phone;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
	@Override
	public String toString() {
		return "Order [id=" + id + ", order_id=" + order_id + ", user_id="
				+ user_id + ", store_id=" + store_id + ", create_time="
				+ create_time + ", phone=" + phone + ", people=" + people
				+ ", is_room=" + is_room + ", order_time=" + order_time
				+ ", type=" + type + ", status=" + status + ", checkgroup="
				+ checkgroup + ", group_count=" + group_count + ", group_id="
				+ group_id + ", userName=" + userName + ", total_price="
				+ total_price + "]";
	}
	
}
