package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CDNURInvoices {

	@Expose
	@SerializedName("flag")
	private String invoiceStatus;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("typ")
	private String type;

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
	private List<CdnurLineItem> cdnrLineItem;

	@Expose(serialize = false, deserialize = false)
	@SerializedName("docId")
	private Long docId;

	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;

	/**
	 * version v2.0 changes
	 */

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("d_flag")
	private String delinkStatus;

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