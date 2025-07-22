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
@Table(name = "GETGSTR1_EINV_EXP_ITEM")
public class GetGstr1EInvoicesExpItemEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR1_EINV_EXP_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;//

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Column(name = "TAX_RATE")
	protected BigDecimal taxRate;

	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxVal;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt;

	@Column(name = "INV_VALUE")
	protected BigDecimal invValue;
	
	@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", nullable = false)
	private GetGstr1EInvoicesExpHeaderEntity document;

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

	public BigDecimal getTaxVal() {
		return taxVal;
	}

	public void setTaxVal(BigDecimal taxVal) {
		this.taxVal = taxVal;
	}

	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}

	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}

	public BigDecimal getInvValue() {
		return invValue;
	}

	public void setInvValue(BigDecimal invValue) {
		this.invValue = invValue;
	}

	public GetGstr1EInvoicesExpHeaderEntity getDocument() {
		return document;
	}

	public void setDocument(GetGstr1EInvoicesExpHeaderEntity document) {
		this.document = document;
	}

}
