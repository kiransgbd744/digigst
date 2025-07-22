package com.ey.advisory.app.gstr3b;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rajesh N K
 *
 */
@ToString
@Getter
@Setter
public class Gstr3BAutoCalcReportDownloadDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("gstin")
	@Expose
	private String gstin;

	@SerializedName("ret_period")
	@Expose
	private String retPeriod;

	@SerializedName("sectionNo")
	@Expose
	private String sectionNo;

	@SerializedName("tableNo")
	@Expose
	private String tableNo;

	@SerializedName("txval")
	@Expose
	private BigDecimal taxableValue = BigDecimal.ZERO;

	@SerializedName("iamt")
	@Expose
	private BigDecimal igstAmount = BigDecimal.ZERO;

	@SerializedName("camt")
	@Expose
	private BigDecimal cgstAmount = BigDecimal.ZERO;

	@SerializedName("samt")
	@Expose
	private BigDecimal sgstAmount = BigDecimal.ZERO;

	@SerializedName("csamt")
	@Expose
	private BigDecimal cessAmount = BigDecimal.ZERO;

	@SerializedName("pos")
	@Expose
	private String pos;

	@SerializedName("igst")
	@Expose
	private BigDecimal igstItc = BigDecimal.ZERO;

	@SerializedName("cgst")
	@Expose
	private BigDecimal cgstItc = BigDecimal.ZERO;

	@SerializedName("sgst")
	@Expose
	private BigDecimal sgstItc = BigDecimal.ZERO;

	@SerializedName("cess")
	@Expose
	private BigDecimal cessItc = BigDecimal.ZERO;

	public Gstr3BAutoCalcReportDownloadDto() {
		super();
	}

	/**
	 * @param sectionName
	 * @param subSectionName
	 * @param taxableVal
	 * @param igst
	 * @param cgst
	 * @param sgst
	 * @param cess
	 * @param pos
	 * @param interState
	 * @param intraState
	 */
	public Gstr3BAutoCalcReportDownloadDto(String sectionNo, String tableNo,
			BigDecimal taxableVal, BigDecimal igst, BigDecimal cgst,
			BigDecimal sgst, BigDecimal cess, String pos) {
		super();
		this.sectionNo = sectionNo;
		this.tableNo = tableNo;
		this.taxableValue = taxableVal;
		this.igstAmount = igst;
		this.cgstAmount = cgst;
		this.sgstAmount = sgst;
		this.cessAmount = cess;
		this.pos = pos;
	}

	public Gstr3BAutoCalcReportDownloadDto(String sectionNo) {
		super();
		this.sectionNo = sectionNo;
	}

	public Gstr3BAutoCalcReportDownloadDto(String gstin, String sectionNo,
			String tableNo, BigDecimal taxableValue, BigDecimal igstAmount,
			BigDecimal cgstAmount, BigDecimal sgstAmount, BigDecimal cessAmount,
			String pos, BigDecimal igstItc, BigDecimal cgstItc,
			BigDecimal sgstItc, BigDecimal cessItc) {
		super();
		this.gstin = gstin;
		this.sectionNo = sectionNo;
		this.tableNo = tableNo;
		this.taxableValue = taxableValue;
		this.igstAmount = igstAmount;
		this.cgstAmount = cgstAmount;
		this.sgstAmount = sgstAmount;
		this.cessAmount = cessAmount;
		this.pos = pos;
		this.igstItc = igstItc;
		this.cgstItc = cgstItc;
		this.sgstItc = sgstItc;
		this.cessItc = cessItc;
	}

}
