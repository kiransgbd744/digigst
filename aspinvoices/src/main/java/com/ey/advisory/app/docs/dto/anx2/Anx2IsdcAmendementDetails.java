package com.ey.advisory.app.docs.dto.anx2;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx2IsdcAmendementDetails {

	@Expose
	@SerializedName("ctin")
	private String cgstin;

	@Expose
	@SerializedName("dtaxprd")
	private String distbtrRetPeriod;

	@Expose
	@SerializedName("num")
	private String docNum;

	@Expose
	@SerializedName("dt")
	private String docDate;

	@Expose
	@SerializedName("docTyp")
	private String docType;

	@Expose
	@SerializedName("igst")
	private BigDecimal igstAmount;

	@Expose
	@SerializedName("sgst")
	private BigDecimal sgstAmount;

	@Expose
	@SerializedName("cgst")
	private BigDecimal cgstAmount;

	@Expose
	@SerializedName("cess")
	private BigDecimal cessAmount;

	public String getCgstin() {
		return cgstin;
	}

	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
	}

	public String getDistbtrRetPeriod() {
		return distbtrRetPeriod;
	}

	public void setDistbtrRetPeriod(String distbtrRetPeriod) {
		this.distbtrRetPeriod = distbtrRetPeriod;
	}

	public String getDocNum() {
		return docNum;
	}

	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}

	public String getDocDate() {
		return docDate;
	}

	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public BigDecimal getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(BigDecimal cessAmount) {
		this.cessAmount = cessAmount;
	}

}
