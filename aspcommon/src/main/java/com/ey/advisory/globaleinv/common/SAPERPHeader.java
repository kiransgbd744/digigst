package com.ey.advisory.globaleinv.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class SAPERPHeader {

	@SerializedName("invoiceno")
	@Expose
	public String invoiceno;

	@SerializedName("invoicedate")
	@Expose
	public String invoicedate;

	@SerializedName("invoicetype")
	@Expose
	public String invoicetype;

	@SerializedName("invoicenote")
	@Expose
	public String invoicenote;

	@SerializedName("invoicetime")
	@Expose
	public String invoicetime;

	@SerializedName("spltaxtreat")
	@Expose
	public String spltaxtreat;

	@SerializedName("currencycode")
	@Expose
	public String currencycode;

	@SerializedName("supplydate")
	@Expose
	public String supplydate;

	@SerializedName("creationdatetime")
	@Expose
	public String creationDateTime;
}
