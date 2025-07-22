package com.ey.advisory.app.data.services.qrcodevalidator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class QRGstnPanStats {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("invoices")
	private int invoices;

	@Expose
	@SerializedName("match")
	private String match;

}
