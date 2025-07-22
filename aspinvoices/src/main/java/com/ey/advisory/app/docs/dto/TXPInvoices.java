package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class TXPInvoices {

	@Expose
	@SerializedName("flag")
	private String invoiceStatus;
	
	@Expose
	@SerializedName("chksum")
	private String invoiceCheckSum;
	
	@Expose
	@SerializedName("omon")
	private String originalMonth;
	
	@Expose
	@SerializedName("pos")
	private String placeOfSupply;
	
	@Expose
	@SerializedName("sply_ty")
	private String supplyType;
	
	@Expose
	@SerializedName("diff_percent")
	private BigDecimal diffPercent;
	
	@Expose
	@SerializedName("itms")
	private List<TXPItemDetails> itemDetails;

}
