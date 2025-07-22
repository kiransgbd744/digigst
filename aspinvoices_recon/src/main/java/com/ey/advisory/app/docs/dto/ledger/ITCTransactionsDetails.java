package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class ITCTransactionsDetails {

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
	@SerializedName("igstTaxBal")
	private BigDecimal igstTaxBal;

	@Expose
	@SerializedName("cgstTaxBal")
	private BigDecimal cgstTaxBal;

	@Expose
	@SerializedName("sgstTaxBal")
	private BigDecimal sgstTaxBal;

	@Expose
	@SerializedName("cessTaxBal")
	private BigDecimal cessTaxBal;

	@Expose
	@SerializedName("igstTaxAmt")
	private BigDecimal igstTaxAmt;

	@Expose
	@SerializedName("cgstTaxAmt")
	private BigDecimal cgstTaxAmt;

	@Expose
	@SerializedName("sgstTaxAmt")
	private BigDecimal sgstTaxAmt;

	@Expose
	@SerializedName("cessTaxAmt")
	private BigDecimal cessTaxAmt;

	@Expose
	@SerializedName("tot_rng_bal")
	private BigDecimal totRunningBal;

	@Expose
	@SerializedName("tr_type")
	private String transactionType;

	@Expose
	@SerializedName("tot_tr_amt")
	private BigDecimal totTransAmt;

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;

	public String getTransDate() {
		return transDate;
	}

	public String getDesc() {
		return desc;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public BigDecimal getIgstTaxBal() {
		return igstTaxBal;
	}

	public BigDecimal getCgstTaxBal() {
		return cgstTaxBal;
	}

	public BigDecimal getSgstTaxBal() {
		return sgstTaxBal;
	}

	public BigDecimal getCessTaxBal() {
		return cessTaxBal;
	}

	public BigDecimal getIgstTaxAmt() {
		return igstTaxAmt;
	}

	public BigDecimal getCgstTaxAmt() {
		return cgstTaxAmt;
	}

	public BigDecimal getSgstTaxAmt() {
		return sgstTaxAmt;
	}

	public BigDecimal getCessTaxAmt() {
		return cessTaxAmt;
	}

	public BigDecimal getTotRunningBal() {
		return totRunningBal;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public BigDecimal getTotTransAmt() {
		return totTransAmt;
	}

	public String getRetPeriod() {
		return retPeriod;
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

	public void setIgstTaxBal(BigDecimal igstTaxBal) {
		this.igstTaxBal = igstTaxBal;
	}

	public void setCgstTaxBal(BigDecimal cgstTaxBal) {
		this.cgstTaxBal = cgstTaxBal;
	}

	public void setSgstTaxBal(BigDecimal sgstTaxBal) {
		this.sgstTaxBal = sgstTaxBal;
	}

	public void setCessTaxBal(BigDecimal cessTaxBal) {
		this.cessTaxBal = cessTaxBal;
	}

	public void setIgstTaxAmt(BigDecimal igstTaxAmt) {
		this.igstTaxAmt = igstTaxAmt;
	}

	public void setCgstTaxAmt(BigDecimal cgstTaxAmt) {
		this.cgstTaxAmt = cgstTaxAmt;
	}

	public void setSgstTaxAmt(BigDecimal sgstTaxAmt) {
		this.sgstTaxAmt = sgstTaxAmt;
	}

	public void setCessTaxAmt(BigDecimal cessTaxAmt) {
		this.cessTaxAmt = cessTaxAmt;
	}

	public void setTotRunningBal(BigDecimal totRunningBal) {
		this.totRunningBal = totRunningBal;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public void setTotTransAmt(BigDecimal totTransAmt) {
		this.totTransAmt = totTransAmt;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

}
