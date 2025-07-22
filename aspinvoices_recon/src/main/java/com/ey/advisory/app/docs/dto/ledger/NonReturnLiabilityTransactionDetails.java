package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class NonReturnLiabilityTransactionDetails {

	@Expose
	@SerializedName("dt")
	private String transDate;

	@Expose
	@SerializedName("ref_no")
	private String referenceNo;

	@Expose
	@SerializedName("tx_prd_frm")
	private String taxPrdFrom;

	@Expose
	@SerializedName("tx_prd_to")
	private String taxPrdTo;

	@Expose
	@SerializedName("dschrg_typ")
	private String dischargeTyp;

	@Expose
	@SerializedName("dem_liab_id")
	private String demandOrLiabId;

	@Expose
	@SerializedName("desc")
	private String desc;

	@Expose
	@SerializedName("tr_type")
	private String transactionType;

	@Expose
	@SerializedName("igst")
	private NonReturnIgstCgstSgstCess igst;

	@Expose
	@SerializedName("cgst")
	private NonReturnIgstCgstSgstCess cgst;

	@Expose
	@SerializedName("sgst")
	private NonReturnIgstCgstSgstCess sgst;

	@Expose
	@SerializedName("cess")
	private NonReturnIgstCgstSgstCess cess;

	@Expose
	@SerializedName("igstbal")
	private NonReturnIgstCgstSgstCess igstBal;

	@Expose
	@SerializedName("cgstbal")
	private NonReturnIgstCgstSgstCess cgstBal;

	@Expose
	@SerializedName("sgstbal")
	private NonReturnIgstCgstSgstCess sgstBal;

	@Expose
	@SerializedName("cessbal")
	private NonReturnIgstCgstSgstCess cessBal;

	public String getTransDate() {
		return transDate;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public String getTaxPrdFrom() {
		return taxPrdFrom;
	}

	public String getTaxPrdTo() {
		return taxPrdTo;
	}

	public String getDischargeTyp() {
		return dischargeTyp;
	}

	public String getDemandOrLiabId() {
		return demandOrLiabId;
	}

	public String getDesc() {
		return desc;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public NonReturnIgstCgstSgstCess getIgst() {
		return igst;
	}

	public NonReturnIgstCgstSgstCess getCgst() {
		return cgst;
	}

	public NonReturnIgstCgstSgstCess getSgst() {
		return sgst;
	}

	public NonReturnIgstCgstSgstCess getCess() {
		return cess;
	}

	public NonReturnIgstCgstSgstCess getIgstBal() {
		return igstBal;
	}

	public NonReturnIgstCgstSgstCess getCgstBal() {
		return cgstBal;
	}

	public NonReturnIgstCgstSgstCess getSgstBal() {
		return sgstBal;
	}

	public NonReturnIgstCgstSgstCess getCessBal() {
		return cessBal;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public void setTaxPrdFrom(String taxPrdFrom) {
		this.taxPrdFrom = taxPrdFrom;
	}

	public void setTaxPrdTo(String taxPrdTo) {
		this.taxPrdTo = taxPrdTo;
	}

	public void setDischargeTyp(String dischargeTyp) {
		this.dischargeTyp = dischargeTyp;
	}

	public void setDemandOrLiabId(String demandOrLiabId) {
		this.demandOrLiabId = demandOrLiabId;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public void setIgst(NonReturnIgstCgstSgstCess igst) {
		this.igst = igst;
	}

	public void setCgst(NonReturnIgstCgstSgstCess cgst) {
		this.cgst = cgst;
	}

	public void setSgst(NonReturnIgstCgstSgstCess sgst) {
		this.sgst = sgst;
	}

	public void setCess(NonReturnIgstCgstSgstCess cess) {
		this.cess = cess;
	}

	public void setIgstBal(NonReturnIgstCgstSgstCess igstBal) {
		this.igstBal = igstBal;
	}

	public void setCgstBal(NonReturnIgstCgstSgstCess cgstBal) {
		this.cgstBal = cgstBal;
	}

	public void setSgstBal(NonReturnIgstCgstSgstCess sgstBal) {
		this.sgstBal = sgstBal;
	}

	public void setCessBal(NonReturnIgstCgstSgstCess cessBal) {
		this.cessBal = cessBal;
	}

}
