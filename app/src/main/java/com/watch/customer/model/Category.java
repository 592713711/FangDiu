package com.watch.customer.model;

public class Category {
	private String id;
	private String name;
	private String store_id;
	private String order;
	private int count;
	public Category(String id, String name, String store_id, String order,
			int count) {
		super();
		this.id = id;
		this.name = name;
		this.store_id = store_id;
		this.order = order;
		this.count = count;
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
	public String getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ", store_id="
				+ store_id + ", order=" + order + ", count=" + count + "]";
	}
	
	
}
