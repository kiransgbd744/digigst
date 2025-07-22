package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class EcomAmd {

	@Expose
	@SerializedName("b2ba")
	private List<B2BEcomInvoices> b2ba;

	@Expose
	@SerializedName("b2ca")
	private List<B2CSInvoices> b2ca;

	@Expose
	@SerializedName("urp2ba")
	private List<B2BEcomInvoices> urp2ba;
	
	@Expose
	@SerializedName("urp2ca")
	private List<B2CSInvoices> urp2ca;

}
