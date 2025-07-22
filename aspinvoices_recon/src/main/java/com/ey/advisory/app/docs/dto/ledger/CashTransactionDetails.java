package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class CashTransactionDetails {

	@Expose
	@SerializedName("dpt_dt")
	private String depositedDate;

	@Expose
	@SerializedName("desc")
	private String desc;

	@Expose
	@SerializedName("refno")
	private String referenceNo;

	@Expose
	@SerializedName("tot_rng_bal")
	private BigDecimal totRunningBal;

	@Expose
	@SerializedName("tr_type")
	private String transactionType;

	@Expose
	@SerializedName("igst")
	private CashIgstCgstSgstCess igst;

	@Expose
	@SerializedName("cgst")
	private CashIgstCgstSgstCess cgst;

	@Expose
	@SerializedName("sgst")
	private CashIgstCgstSgstCess sgst;

	@Expose
	@SerializedName("cess")
	private CashIgstCgstSgstCess cess;

	@Expose
	@SerializedName("rpt_dt")
	private BigDecimal bankReportingDate;

	@Expose
	@SerializedName("dpt_time")
	private String depositedTime;

	@Expose
	@SerializedName("tot_tr_amt")
	private BigDecimal totTransAmt;

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;

	@Expose
	@SerializedName("igstbal")
	private CashIgstCgstSgstCess igstBal;

	@Expose
	@SerializedName("cgstbal")
	private CashIgstCgstSgstCess cgstBal;

	@Expose
	@SerializedName("sgstbal")
	private CashIgstCgstSgstCess sgstBal;

	@Expose
	@SerializedName("cessbal")
	private CashIgstCgstSgstCess cessBal;

	public String getDepositedDate() {
		return depositedDate;
	}

	public String getDesc() {
		return desc;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public BigDecimal getTotRunningBal() {
		return totRunningBal;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public CashIgstCgstSgstCess getIgst() {
		return igst;
	}

	public CashIgstCgstSgstCess getCgst() {
		return cgst;
	}

	public CashIgstCgstSgstCess getSgst() {
		return sgst;
	}

	public CashIgstCgstSgstCess getCess() {
		return cess;
	}

	public BigDecimal getBankReportingDate() {
		return bankReportingDate;
	}

	public String getDepositedTime() {
		return depositedTime;
	}

	public BigDecimal getTotTransAmt() {
		return totTransAmt;
	}

	public String getRetPeriod() {
		return retPeriod;
	}

	public CashIgstCgstSgstCess getIgstBal() {
		return igstBal;
	}

	public CashIgstCgstSgstCess getCgstBal() {
		return cgstBal;
	}

	public CashIgstCgstSgstCess getSgstBal() {
		return sgstBal;
	}

	public CashIgstCgstSgstCess getCessBal() {
		return cessBal;
	}

	public void setDepositedDate(String depositedDate) {
		this.depositedDate = depositedDate;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public void setTotRunningBal(BigDecimal totRunningBal) {
		this.totRunningBal = totRunningBal;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public void setIgst(CashIgstCgstSgstCess igst) {
		this.igst = igst;
	}

	public void setCgst(CashIgstCgstSgstCess cgst) {
		this.cgst = cgst;
	}

	public void setSgst(CashIgstCgstSgstCess sgst) {
		this.sgst = sgst;
	}

	public void setCess(CashIgstCgstSgstCess cess) {
		this.cess = cess;
	}

	public void setBankReportingDate(BigDecimal bankReportingDate) {
		this.bankReportingDate = bankReportingDate;
	}

	public void setDepositedTime(String depositedTime) {
		this.depositedTime = depositedTime;
	}

	public void setTotTransAmt(BigDecimal totTransAmt) {
		this.totTransAmt = totTransAmt;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	public void setIgstBal(CashIgstCgstSgstCess igstBal) {
		this.igstBal = igstBal;
	}

	public void setCgstBal(CashIgstCgstSgstCess cgstBal) {
		this.cgstBal = cgstBal;
	}

	public void setSgstBal(CashIgstCgstSgstCess sgstBal) {
		this.sgstBal = sgstBal;
	}

	public void setCessBal(CashIgstCgstSgstCess cessBal) {
		this.cessBal = cessBal;
	}

}
