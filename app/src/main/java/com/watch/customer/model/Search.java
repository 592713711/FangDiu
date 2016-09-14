package com.watch.customer.model;

public class Search {
	private int id;
	private String content;
	private String time;

	public Search(int id, String content, String time) {
		super();
		this.id = id;
		this.content = content;
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "Search [id=" + id + ", content=" + content + ", time=" + time
				+ "]";
	}

}
