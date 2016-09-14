package com.watch.customer.model;

import java.io.Serializable;

public class Deal implements Serializable{
	private String id;
	private String store_id;
	private String title;
	private String content;
	private String image;
	private String image_thumb;
	private String old_price;
	private String start_time;
	private String end_time;
	private String score;
	private String dishes_id;
	private String group_price;
	private String store_name;
	private String address;
	private String store_phone;
	public Deal(String id, String store_id, String title, String content,
			String image, String image_thumb, String old_price,
			String start_time, String end_time, String score, String dishes_id,
			String group_price, String store_name, String address,
			String store_phone) {
		super();
		this.id = id;
		this.store_id = store_id;
		this.title = title;
		this.content = content;
		this.image = image;
		this.image_thumb = image_thumb;
		this.old_price = old_price;
		this.start_time = start_time;
		this.end_time = end_time;
		this.score = score;
		this.dishes_id = dishes_id;
		this.group_price = group_price;
		this.store_name = store_name;
		this.address = address;
		this.store_phone = store_phone;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public String getOld_price() {
		return old_price;
	}
	public void setOld_price(String old_price) {
		this.old_price = old_price;
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
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getDishes_id() {
		return dishes_id;
	}
	public void setDishes_id(String dishes_id) {
		this.dishes_id = dishes_id;
	}
	public String getGroup_price() {
		return group_price;
	}
	public void setGroup_price(String group_price) {
		this.group_price = group_price;
	}
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getStore_phone() {
		return store_phone;
	}
	public void setStore_phone(String store_phone) {
		this.store_phone = store_phone;
	}
	@Override
	public String toString() {
		return "Deal [id=" + id + ", store_id=" + store_id + ", title=" + title
				+ ", content=" + content + ", image=" + image
				+ ", image_thumb=" + image_thumb + ", old_price=" + old_price
				+ ", start_time=" + start_time + ", end_time=" + end_time
				+ ", score=" + score + ", dishes_id=" + dishes_id
				+ ", group_price=" + group_price + ", store_name=" + store_name
				+ ", address=" + address + ", store_phone=" + store_phone + "]";
	}
	
}
