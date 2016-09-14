package com.watch.customer.model;

import java.io.Serializable;

public class Dish implements Serializable{
	private String id;
	private String store_id;
	private String dishes_name;
	private String dishes_type;
	private String image;
	private String image_thumb;
	private String price;
	private String discount;
	private String create_time;
	private boolean isselect;
	public Dish(String id, String store_id, String dishes_name,
			String dishes_type, String image, String image_thumb, String price,
			String discount, String create_time, boolean isselect) {
		super();
		this.id = id;
		this.store_id = store_id;
		this.dishes_name = dishes_name;
		this.dishes_type = dishes_type;
		this.image = image;
		this.image_thumb = image_thumb;
		this.price = price;
		this.discount = discount;
		this.create_time = create_time;
		this.isselect = isselect;
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
	public String getDishes_name() {
		return dishes_name;
	}
	public void setDishes_name(String dishes_name) {
		this.dishes_name = dishes_name;
	}
	public String getDishes_type() {
		return dishes_type;
	}
	public void setDishes_type(String dishes_type) {
		this.dishes_type = dishes_type;
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
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
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
	public boolean isIsselect() {
		return isselect;
	}
	public void setIsselect(boolean isselect) {
		this.isselect = isselect;
	}
	@Override
	public String toString() {
		return "Dish [id=" + id + ", store_id=" + store_id + ", dishes_name="
				+ dishes_name + ", dishes_type=" + dishes_type + ", image="
				+ image + ", image_thumb=" + image_thumb + ", price=" + price
				+ ", discount=" + discount + ", create_time=" + create_time
				+ ", isselect=" + isselect + "]";
	}
	
}
