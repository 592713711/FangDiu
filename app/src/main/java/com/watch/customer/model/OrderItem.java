package com.watch.customer.model;

import java.io.Serializable;

public class OrderItem implements Serializable{
	private String order_id;
	private String type;
	private String user_id;
	private String create_time;
	private String order_time;
	private String status;
	private String store_id;
	private String group_id;
	private String group_count;
	private String check_group;
	private String storeName;
	private String is_local;
	private String people;
	private String is_room;
	private String userName;
	private String phone;
	private String add_food;
	public OrderItem(String order_id, String type, String user_id,
			String create_time, String order_time, String status,
			String store_id, String group_id, String group_count,
			String check_group, String storeName, String is_local,
			String people, String is_room, String userName, String phone,
			String add_food) {
		super();
		this.order_id = order_id;
		this.type = type;
		this.user_id = user_id;
		this.create_time = create_time;
		this.order_time = order_time;
		this.status = status;
		this.store_id = store_id;
		this.group_id = group_id;
		this.group_count = group_count;
		this.check_group = check_group;
		this.storeName = storeName;
		this.is_local = is_local;
		this.people = people;
		this.is_room = is_room;
		this.userName = userName;
		this.phone = phone;
		this.add_food = add_food;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getOrder_time() {
		return order_time;
	}
	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getGroup_count() {
		return group_count;
	}
	public void setGroup_count(String group_count) {
		this.group_count = group_count;
	}
	public String getCheck_group() {
		return check_group;
	}
	public void setCheck_group(String check_group) {
		this.check_group = check_group;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getIs_local() {
		return is_local;
	}
	public void setIs_local(String is_local) {
		this.is_local = is_local;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAdd_food() {
		return add_food;
	}
	public void setAdd_food(String add_food) {
		this.add_food = add_food;
	}
	
	
}
