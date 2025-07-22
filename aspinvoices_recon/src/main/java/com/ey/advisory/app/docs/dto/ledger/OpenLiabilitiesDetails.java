package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class OpenLiabilitiesDetails {

	@Expose
	@SerializedName("liab_id")
	private Integer liabilitiyId;

	@Expose
	@SerializedName("tran_desc")
	private String description;

	@Expose
	@SerializedName("tran_cd")
	private Integer transactionCode;

	@Expose
	@SerializedName("igst")
	private TaxPayableIgstCgstSgstCess igst;

	@Expose
	@SerializedName("cgst")
	private TaxPayableIgstCgstSgstCess cgst;

	@Expose
	@SerializedName("sgst")
	private TaxPayableIgstCgstSgstCess sgst;

	@Expose
	@SerializedName("cess")
	private TaxPayableIgstCgstSgstCess cess;

	public Integer getLiabilitiyId() {
		return liabilitiyId;
	}

	public String getDescription() {
		return description;
	}

	public Integer getTransactionCode() {
		return transactionCode;
	}

	public TaxPayableIgstCgstSgstCess getIgst() {
		return igst;
	}

	public TaxPayableIgstCgstSgstCess getCgst() {
		return cgst;
	}

	public TaxPayableIgstCgstSgstCess getSgst() {
		return sgst;
	}

	public TaxPayableIgstCgstSgstCess getCess() {
		return cess;
	}

	public void setLiabilitiyId(Integer liabilitiyId) {
		this.liabilitiyId = liabilitiyId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTransactionCode(Integer transactionCode) {
		this.transactionCode = transactionCode;
	}

	public void setIgst(TaxPayableIgstCgstSgstCess igst) {
		this.igst = igst;
	}

	public void setCgst(TaxPayableIgstCgstSgstCess cgst) {
		this.cgst = cgst;
	}

	public void setSgst(TaxPayableIgstCgstSgstCess sgst) {
		this.sgst = sgst;
	}

	public void setCess(TaxPayableIgstCgstSgstCess cess) {
		this.cess = cess;
	}

}
