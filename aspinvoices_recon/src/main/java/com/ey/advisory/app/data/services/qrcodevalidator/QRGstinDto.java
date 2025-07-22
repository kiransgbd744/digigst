package com.ey.advisory.app.data.services.qrcodevalidator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class QRGstinDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("errMsg")
	private String errMsg;

}
