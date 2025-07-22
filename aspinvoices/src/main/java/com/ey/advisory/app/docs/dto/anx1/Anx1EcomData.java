package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx1EcomData {

	@Expose
	@SerializedName("etin")
	private String etin;

	@Expose
	@SerializedName("sup")
	private BigDecimal madeSupVal;

	@Expose
	@SerializedName("supr")
	private BigDecimal retSupVal;

	@Expose
	@SerializedName("nsup")
	private BigDecimal netSupval;

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

	@Expose(serialize = false, deserialize = false)
	@SerializedName("docId")
	private Long docId;

	/**
	 * Anx1 Get API fields which is not part of Anx1 saveToGstn
	 */
	@Expose
	@SerializedName("chksum")
	private String checkSum;

	public String getEtin() {
		return etin;
	}

	public void setEtin(String etin) {
		this.etin = etin;
	}

	public BigDecimal getMadeSupVal() {
		return madeSupVal;
	}

	public void setMadeSupVal(BigDecimal madeSupVal) {
		this.madeSupVal = madeSupVal;
	}

	public BigDecimal getRetSupVal() {
		return retSupVal;
	}

	public void setRetSupVal(BigDecimal retSupVal) {
		this.retSupVal = retSupVal;
	}

	public BigDecimal getNetSupval() {
		return netSupval;
	}

	public void setNetSupval(BigDecimal netSupval) {
		this.netSupval = netSupval;
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

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

}
