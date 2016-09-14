package com.watch.customer.model;

public class Coin {
	private String id;
	private String shibi;
	private String user_id;
	private String create_time;
	private String type;

	public Coin(String id, String shibi, String user_id, String create_time,
			String type) {
		super();
		this.id = id;
		this.shibi = shibi;
		this.user_id = user_id;
		this.create_time = create_time;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getShibi() {
		return shibi;
	}

	public void setShibi(String shibi) {
		this.shibi = shibi;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Coin [id=" + id + ", shibi=" + shibi + ", user_id=" + user_id
				+ ", create_time=" + create_time + ", type=" + type + "]";
	}

}
