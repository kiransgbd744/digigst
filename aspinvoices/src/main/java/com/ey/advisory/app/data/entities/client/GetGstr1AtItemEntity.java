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


/**
 * 
 * @author Anand3.M
 *
 */

@Entity
@Table(name = "GETGSTR1_AT_ITEM")
public class GetGstr1AtItemEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR1_AT_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;//

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;//

	@Column(name = "TAX_RATE")
	protected BigDecimal taxRate = BigDecimal.ZERO;

	@Column(name = "ADVREC_AMT")
	protected BigDecimal advRecAmt = BigDecimal.ZERO;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt = BigDecimal.ZERO;

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt = BigDecimal.ZERO;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt = BigDecimal.ZERO;

	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt = BigDecimal.ZERO;

	@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", nullable = false)
	private GetGstr1AtHeaderEntity document;

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

	public BigDecimal getAdvRecAmt() {
		return advRecAmt;
	}

	public void setAdvRecAmt(BigDecimal advRecAmt) {
		this.advRecAmt = advRecAmt;
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

	public GetGstr1AtHeaderEntity getDocument() {
		return document;
	}

	public void setDocument(GetGstr1AtHeaderEntity document) {
		this.document = document;
	}

}
