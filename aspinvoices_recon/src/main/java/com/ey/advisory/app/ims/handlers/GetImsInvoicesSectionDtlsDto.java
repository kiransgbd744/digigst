package com.ey.advisory.app.ims.handlers;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetImsInvoicesSectionDtlsDto {

	@Expose
	@SerializedName("stin")
	private String supplierGstin;

	@Expose
	@SerializedName("oinum")
	private String originalInvoiceNumber;

	@Expose
	@SerializedName("oidt")
	private String originalInvoiceDate;

	@Expose
	@SerializedName("rtnprd")
	private String returnPeriod;

	@Expose
	@SerializedName(value = "inum", alternate = ("nt_num"))
	private String invoiceNumber;
	
	@Expose
	@SerializedName("inv_typ")
	private String invoiceType;

	@Expose
	@SerializedName("action")
	private String action;

	@Expose
	@SerializedName("srcform")
	private String sourceForm;

	@Expose
	@SerializedName("ispendactnallwd")
	private String isPendingActionAllowed;
	
	@Expose
	@SerializedName("ispendactblocked")
	private String isPendingActionBlocked;

	@Expose
	@SerializedName(value = "idt", alternate = ("nt_dt"))
	private String invoiceDate;

	@Expose
	@SerializedName("val")
	private BigDecimal invoiceValue;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("txval")
	private BigDecimal taxableValue;

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
	@SerializedName("cess")
	private BigDecimal cess;

	@Expose
	@SerializedName("srcfilstatus")
	private String srcfilstatus;

	@Expose
	@SerializedName("hash")
	private String hash;
	
	@Expose
	@SerializedName("itcRedReq")
    private String itcRedReq;
	
	@Expose
	@SerializedName("declIgst")
    private BigDecimal declIgst;
	
	@Expose
	@SerializedName("declSgst")
    private BigDecimal declSgst;
	
	@Expose
	@SerializedName("declCgst")
    private BigDecimal declCgst;
	
	@Expose
	@SerializedName("declCess")
    private BigDecimal declCess;
	
	@Expose
	@SerializedName("remarks")
    private String remarks;

}
