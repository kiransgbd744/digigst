package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;

public class DataSecuriDto {
	
	@Expose
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
