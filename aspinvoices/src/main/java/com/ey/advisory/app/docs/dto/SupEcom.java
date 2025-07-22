package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class SupEcom {

	@Expose
	@SerializedName("clttx")
	private List<SupEcomInvoices> clttx;

	@Expose
	@SerializedName("paytx")
	private List<SupEcomInvoices> paytx;

}
