package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Umesha .M
 *
 */
@Data
public class EXPInvoiceData {

	@Expose
	@SerializedName("flag")
	private String invoiceStatus;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("inum")
	private String invoiceNum;

	@Expose
	@SerializedName("idt")
	private String invoiceDate;

	@Expose
	@SerializedName("val")
	private BigDecimal invoiceValue;

	@Expose
	@SerializedName("sbpcode")
	private String shipBillPortCode;

	@Expose
	@SerializedName("sbnum")
	private String shipBillNum;

	@Expose
	@SerializedName("sbdt")
	private String shipBillDate;

	@Expose
	@SerializedName("diff_percent")
	private BigDecimal diffPercent;

	@Expose
	@SerializedName("ctin")
	private String couPartyGstn;

	@Expose
	@SerializedName("oinum")
	private String oinum;

	@Expose
	@SerializedName("oidt")
	protected String oidt;

	@Expose
	@SerializedName("itms")
	private List<EXPLineItem> lineItems;

	@Expose(serialize = false, deserialize = true)
	@SerializedName("docId")
	private Long docId;

	/**
	 * version v2.1 changes
	 */

	@Expose
	@SerializedName("irn")
	private String irn;

	@Expose
	@SerializedName("irngendate")
	private String irngendate;
	
	@Expose
	@SerializedName("srctyp")
	private String srctyp;
	
	@Expose
	@SerializedName("einvstatus")
	private String einvstatus;

	@Expose
	@SerializedName("autodft")
	private String autodft;

	@Expose
	@SerializedName("autodftdt")
	private String autodftdt;

	@Expose
	@SerializedName("errorCd")
	private String errorCd;

	@Expose
	@SerializedName("errorMsg")
	private String errorMsg;

}