package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CreditDebitNote {

	@Expose
	@SerializedName("flag")
	private String invoiceStatus;

	@Expose
	@SerializedName("cflag")
	private String cPartyFlag;

	@Expose
	@SerializedName("opd")
	private String orgPeriod;

	@Expose
	@SerializedName("updby")
	private String uploadedBy;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("ont_num")
	private String oriCredDebNum;

	@Expose
	@SerializedName("ont_dt")
	private String oriCredDebDate;

	@Expose
	@SerializedName("ntty")
	private String credDebRefVoucher;

	@Expose
	@SerializedName("nt_num")
	private String credDebRefVoucherNum;

	@Expose
	@SerializedName("nt_dt")
	private String credDebRefVoucherDate;

	@Expose
	@SerializedName("p_gst")
	private String preGstRegNote;

	@Expose
	@SerializedName("inum")
	private String invNum;

	@Expose
	@SerializedName("idt")
	private String invDate;

	@Expose
	@SerializedName("val")
	private BigDecimal totalNoteVal;

	@Expose
	@SerializedName("diff_percent")
	private BigDecimal diffPercent;

	@Expose
	@SerializedName("itms")
	private List<CdnrLineItem> cdnrLineItem;

	@Expose(serialize = false, deserialize = true)
	@SerializedName("docId")
	private Long docId;

	/**
	 * version v2.0 changes
	 */

	@Expose
	@SerializedName("inv_typ")
	private String invoiceType;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("rchrg")
	private String reverseCharge;

	@Expose
	@SerializedName("d_flag")
	private String delinkStatus;

	
	/**
	 * GET2a version v2.0 changes
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
