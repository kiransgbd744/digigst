/**
 * 
 */
package com.ey.advisory.app.data.entities.simplified.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "GETANX1_IMPG_ITEM")
public class GetAnx1ImpgInvoicesItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "HSN")
	private Integer hsn;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue;

	@Column(name = "TAX_RATE")
	private BigDecimal taxRate;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@Column(name = "DERIVED_RET_PERIOD")
	private int derTaxPeriod;

	@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", nullable = false)
	protected GetAnx1ImpgInvoicesHeaderEntity header;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getHsn() {
		return hsn;
	}

	public void setHsn(Integer hsn) {
		this.hsn = hsn;
	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
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

	public int getDerTaxPeriod() {
		return derTaxPeriod;
	}

	public void setDerTaxPeriod(int derTaxPeriod) {
		this.derTaxPeriod = derTaxPeriod;
	}

	public GetAnx1ImpgInvoicesHeaderEntity getHeader() {
		return header;
	}

	public void setHeader(GetAnx1ImpgInvoicesHeaderEntity header) {
		this.header = header;
	}

}