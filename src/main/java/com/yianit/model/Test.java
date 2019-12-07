package com.yianit.model;

import java.io.Serializable;

import javax.persistence.Table;

@Table(name = "test")
public class Test implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3331851904748212737L;
	private int userId;
	private String userName;
	private String address;
	private String tel;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

}
