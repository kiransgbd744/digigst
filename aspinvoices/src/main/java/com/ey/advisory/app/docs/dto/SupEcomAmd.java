package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class SupEcomAmd {

	@Expose
	@SerializedName("clttxa")
	private List<SupEcomInvoices> clttxa;

	@Expose
	@SerializedName("paytxa")
	private List<SupEcomInvoices> paytxa;

}
