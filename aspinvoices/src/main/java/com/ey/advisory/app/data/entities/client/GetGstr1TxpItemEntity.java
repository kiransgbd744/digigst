package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Anand3.M
 *
 */

@Entity
@Table(name = "GETGSTR1_TXP_ITEM")

public class GetGstr1TxpItemEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR1_TXP_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("ret_period")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;//

	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;//

	@Expose
	@SerializedName("taxRate")
	@Column(name = "TAX_RATE")
	protected BigDecimal taxRate;

	@Expose
	@SerializedName("advAdjAmt")
	@Column(name = "ADVADJ_AMT")
	protected BigDecimal advAdjAmt;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Expose
	@SerializedName("cessAmt")
	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt;

	@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", nullable = false)
	private GetGstr1TxpHeaderEntity document;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public Integer getDerivedTaxperiod() {
		return derivedTaxperiod;
	}

	public void setDerivedTaxperiod(Integer derivedTaxperiod) {
		this.derivedTaxperiod = derivedTaxperiod;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public BigDecimal getAdvAdjAmt() {
		return advAdjAmt;
	}

	public void setAdvAdjAmt(BigDecimal advAdjAmt) {
		this.advAdjAmt = advAdjAmt;
	}

	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}

	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}

	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = cgstAmt;
	}

	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}

	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
	}

	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}

	public GetGstr1TxpHeaderEntity getDocument() {
		return document;
	}

	public void setDocument(GetGstr1TxpHeaderEntity document) {
		this.document = document;
	}

}
