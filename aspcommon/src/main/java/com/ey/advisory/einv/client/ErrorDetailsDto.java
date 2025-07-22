package com.ey.advisory.einv.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ErrorDetailsDto {

	@Expose
	@SerializedName("errorfield")
	private String errorField;
	
	@Expose
	@SerializedName("errordesc")
	private String errorDesc;

}
