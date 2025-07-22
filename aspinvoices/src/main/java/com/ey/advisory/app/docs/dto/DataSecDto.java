package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;

public class DataSecDto {
	
	@Expose
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
