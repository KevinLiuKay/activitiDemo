package com.kevin.demo.model;

import java.io.Serializable;

public class Person implements Serializable{
	private static final long serialVersionUID = -2485554908660237381L;
	private Integer id;
	private String name;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
