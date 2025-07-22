package com.ey.advisory.app.data.services.qrcodevalidator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class QRMisMatchCount {

	@Expose
	@SerializedName("reportType")
	private String reportType;

	@Expose
	@SerializedName("mismatCount")
	private int qrCnt;

}
