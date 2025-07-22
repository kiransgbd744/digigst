package com.ey.advisory.globaleinv.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class SAPERPItemParticular {

	@SerializedName("invoicelineno")
	@Expose
	public int invoicelineno;

	@SerializedName("itemname")
	@Expose
	public String itemname;

	@SerializedName("itemgrossprice")
	@Expose
	public Double itemgrossprice;

	@SerializedName("currencyigp")
	@Expose
	public String currencyigp;
	
	@SerializedName("currencyinp")
	@Expose
	public String currencyinp;
	
	@SerializedName("quantity")
	@Expose
	public Double quantity;
	
	@SerializedName("uom")
	@Expose
	public String uom;
}
