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
public class B2CLInvoiceData {

	@Expose
	@SerializedName("flag")
	private String invoiceStatus;

	@Expose
	@SerializedName("inum")
	private String invoiceNum;

	@Expose
	@SerializedName("idt")
	private String invoiceDate;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("oinum")
	private String oinum;

	@Expose
	@SerializedName("oidt")
	protected String oidt;

	@Expose
	@SerializedName("val")
	private BigDecimal invoiceValue;

	@Expose
	@SerializedName("etin")
	private String ecomTin;

	@Expose
	@SerializedName("inv_typ")
	private String invoiceType;

	@Expose
	@SerializedName("diff_percent")
	private BigDecimal diffPercent;

	@Expose
	@SerializedName("itms")
	private List<B2CLLineItem> lineItems;

	@Expose(serialize = false, deserialize = true)
	@SerializedName("docId")
	private Long docId;

	
}
