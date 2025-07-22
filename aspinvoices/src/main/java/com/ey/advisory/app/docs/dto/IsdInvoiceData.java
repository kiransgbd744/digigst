package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Santosh.Gururaj
 *
 */
public class IsdInvoiceData {

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("docnum")
	private String documentNumber;
	@Expose
	@SerializedName("docdt")
	private String documentDate;

	@Expose
	@SerializedName("isd_docty")
	private String documentType;

	@Expose
	@SerializedName("itc_elg")
	private String itcEligible;

	@Expose
	@SerializedName("iamt")
	private BigDecimal igst;

	@Expose
	@SerializedName("camt")
	private BigDecimal cgst;

	@Expose
	@SerializedName("samt")
	private BigDecimal sgst;

	@Expose
	@SerializedName("cess")
	private BigDecimal cess;

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getItcEligible() {
		return itcEligible;
	}

	public void setItcEligible(String itcEligible) {
		this.itcEligible = itcEligible;
	}

	public BigDecimal getIgst() {
		return igst;
	}

	public void setIgst(BigDecimal igst) {
		this.igst = igst;
	}

	public BigDecimal getCgst() {
		return cgst;
	}

	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst;
	}

	public BigDecimal getSgst() {
		return sgst;
	}

	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst;
	}

	public BigDecimal getCess() {
		return cess;
	}

	public void setCess(BigDecimal cess) {
		this.cess = cess;
	}
}
	
