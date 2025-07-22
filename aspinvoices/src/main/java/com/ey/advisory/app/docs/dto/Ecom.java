package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Ecom {

	@Expose
	@SerializedName("b2b")
	private List<B2BEcomInvoices> b2b;

	@Expose
	@SerializedName("b2c")
	private List<B2CSInvoices> b2c;

	@Expose
	@SerializedName("urp2b")
	private List<B2BEcomInvoices> urp2b;
	
	@Expose
	@SerializedName("urp2c")
	private List<B2CSInvoices> urp2c;

}
