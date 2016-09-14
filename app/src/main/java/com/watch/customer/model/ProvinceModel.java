package com.watch.customer.model;

import java.util.ArrayList;

public class ProvinceModel {
	public CityJson province;
	public ArrayList<CityJson> city_list;
	public ProvinceModel(CityJson province, ArrayList<CityJson> city_list) {
		super();
		this.province = province;
		this.city_list = city_list;
	}
	public CityJson getProvince() {
		return province;
	}
	public void setProvince(CityJson province) {
		this.province = province;
	}
	public ArrayList<CityJson> getCity_list() {
		return city_list;
	}
	public void setCity_list(ArrayList<CityJson> city_list) {
		this.city_list = city_list;
	}
	@Override
	public String toString() {
		return "ProvinceModel [province=" + province + ", city_list="
				+ city_list + "]";
	}
	
}
