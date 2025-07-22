package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class TaxTransactionDetails {

	@Expose
	@SerializedName("dt")
	private String transDate;

	@Expose
	@SerializedName("desc")
	private String desc;

	@Expose
	@SerializedName("ref_no")
	private String referenceNo;

	@Expose
	@SerializedName("tot_rng_bal")
	private TaxIgstCgstSgstCess totRunningBal;

	@Expose
	@SerializedName("tot_tr_amt")
	private TaxIgstCgstSgstCess totTransAmt;

	@Expose
	@SerializedName("dschrg_typ")
	private String dischargeType;

	@Expose
	@SerializedName("tr_type")
	private String transactionType;

	@Expose
	@SerializedName("igst")
	private TaxIgstCgstSgstCess igst;

	@Expose
	@SerializedName("cgst")
	private TaxIgstCgstSgstCess cgst;

	@Expose
	@SerializedName("sgst")
	private TaxIgstCgstSgstCess sgst;

	@Expose
	@SerializedName("cess")
	private TaxIgstCgstSgstCess cess;

	@Expose
	@SerializedName("igstbal")
	private TaxIgstCgstSgstCess igstbal;

	@Expose
	@SerializedName("cgstbal")
	private TaxIgstCgstSgstCess cgstbal;

	@Expose
	@SerializedName("sgstbal")
	private TaxIgstCgstSgstCess sgstbal;

	@Expose
	@SerializedName("cessbal")
	private TaxIgstCgstSgstCess cessbal;

	public String getTransDate() {
		return transDate;
	}

	public String getDesc() {
		return desc;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public TaxIgstCgstSgstCess getTotRunningBal() {
		return totRunningBal;
	}

	public TaxIgstCgstSgstCess getTotTransAmt() {
		return totTransAmt;
	}

	public String getDischargeType() {
		return dischargeType;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public TaxIgstCgstSgstCess getIgst() {
		return igst;
	}

	public TaxIgstCgstSgstCess getCgst() {
		return cgst;
	}

	public TaxIgstCgstSgstCess getSgst() {
		return sgst;
	}

	public TaxIgstCgstSgstCess getCess() {
		return cess;
	}

	public TaxIgstCgstSgstCess getIgstbal() {
		return igstbal;
	}

	public TaxIgstCgstSgstCess getCgstbal() {
		return cgstbal;
	}

	public TaxIgstCgstSgstCess getSgstbal() {
		return sgstbal;
	}

	public TaxIgstCgstSgstCess getCessbal() {
		return cessbal;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public void setTotRunningBal(TaxIgstCgstSgstCess totRunningBal) {
		this.totRunningBal = totRunningBal;
	}

	public void setTotTransAmt(TaxIgstCgstSgstCess totTransAmt) {
		this.totTransAmt = totTransAmt;
	}

	public void setDischargeType(String dischargeType) {
		this.dischargeType = dischargeType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public void setIgst(TaxIgstCgstSgstCess igst) {
		this.igst = igst;
	}

	public void setCgst(TaxIgstCgstSgstCess cgst) {
		this.cgst = cgst;
	}

	public void setSgst(TaxIgstCgstSgstCess sgst) {
		this.sgst = sgst;
	}

	public void setCess(TaxIgstCgstSgstCess cess) {
		this.cess = cess;
	}

	public void setIgstbal(TaxIgstCgstSgstCess igstbal) {
		this.igstbal = igstbal;
	}

	public void setCgstbal(TaxIgstCgstSgstCess cgstbal) {
		this.cgstbal = cgstbal;
	}

	public void setSgstbal(TaxIgstCgstSgstCess sgstbal) {
		this.sgstbal = sgstbal;
	}

	public void setCessbal(TaxIgstCgstSgstCess cessbal) {
		this.cessbal = cessbal;
	}

}
