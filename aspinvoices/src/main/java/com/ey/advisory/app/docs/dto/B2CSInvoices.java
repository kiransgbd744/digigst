package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Umesha.M
 *
 */
@Data
public class B2CSInvoices {

	@Expose
	@SerializedName("flag")
	private String invoiceStatus;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("sply_ty")
	private String supplyType;

	@Expose
	@SerializedName("txval")
	private BigDecimal taxableValue;

	@Expose
	@SerializedName("diff_percent")
	private BigDecimal diffPercent;

	@Expose
	@SerializedName("rt")
	private BigDecimal rate;

	@Expose
	@SerializedName("iamt")
	private BigDecimal igstAmount;

	@Expose
	@SerializedName("camt")
	private BigDecimal cgstAmount;

	@Expose
	@SerializedName("samt")
	private BigDecimal sgstAmount;

	@Expose
	@SerializedName("csamt")
	private BigDecimal cessAmount;

	@Expose
	@SerializedName("etin")
	private String ecomTin;

	@Expose
	@SerializedName("pos")
	private String pointOfSupply;

	@Expose
	@SerializedName("typ")
	private String type;

	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;

	@Expose
	@SerializedName("omon")
	private String orgMonthInv;

	@Expose
	@SerializedName("opos")
	private String orgPlaceSupp;

	@Expose
	@SerializedName("itms")
	private List<B2CSALineItem> lineItems;

	@Expose(serialize = false, deserialize = true)
	@SerializedName("docId")
	private Long docId;

	// Only for Ecom
	@Expose
	@SerializedName("stin")
	private String stin;

	@Expose
	@SerializedName("ostin")
	private String ostin;
	
	@Expose
	@SerializedName("ortin")
	private String ortin;
}
