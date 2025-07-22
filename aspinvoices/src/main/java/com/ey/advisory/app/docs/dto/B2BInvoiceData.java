package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Data
public class B2BInvoiceData {

	@Expose
	@SerializedName("inum")
	private String invoiceNumber;

	@Expose
	@SerializedName("flag")
	private String invoiceStatus;

	@Expose
	@SerializedName("idt")
	private String invoiceDate;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("val")
	private BigDecimal invoiceValue;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("rchrg")
	private String reverseCharge;

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
	@SerializedName("updby")
	private String updby;

	@Expose
	@SerializedName("cflag")
	private String cflag;

	@Expose
	@SerializedName("opd")
	private String opd;

	@Expose
	@SerializedName("sply_ty")
	private String supplyType;

	@Expose
	@SerializedName("oinum")
	private String origInvNumber;

	@Expose
	@SerializedName("oidt")
	private String origInvDate;

	@Expose
	@SerializedName("itms")
	private List<B2bLineItem> lineItems;

	@Expose(serialize = false, deserialize = false)
	@SerializedName("docId")
	private Long docId;

	/**
	 * version v2.0 changes
	 */

	@Expose
	@SerializedName("aspd")
	private String orgInvPeriod;

	@Expose
	@SerializedName("atyp")
	private String orgInvType;

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
