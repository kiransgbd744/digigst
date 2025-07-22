package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Entity
@Table(name = "ANX_OUTWARD_DOC_RATE_SUMMARY")
public class DocRateSummary {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ANX_OUTWARD_DOC_RATE_SUMMARY_SEQ", 
	 allocationSize = 100)
	 @GeneratedValue(generator = "sequence", strategy=GenerationType.SEQUENCE)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "DOC_HEADER_ID")
	private Long docHeaderId;

	@Column(name = "TAX_RATE")
	private BigDecimal taxRate;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxValue;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;
	
	@Expose
	@SerializedName("gstr1SubCategory")
	@Column(name = "ITM_TAX_DOC_TYPE")
	protected String itmGstnBifurcation;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the docHeaderId
	 */
	public Long getDocHeaderId() {
		return docHeaderId;
	}

	/**
	 * @param docHeaderId
	 *            the docHeaderId to set
	 */
	public void setDocHeaderId(Long docHeaderId) {
		this.docHeaderId = docHeaderId;
	}

	/**
	 * @return the taxRate
	 */
	public BigDecimal getTaxRate() {
		return taxRate;
	}

	/**
	 * @param taxRate
	 *            the taxRate to set
	 */
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	/**
	 * @return the taxValue
	 */
	public BigDecimal getTaxValue() {
		return taxValue;
	}

	/**
	 * @param taxValue
	 *            the taxValue to set
	 */
	public void setTaxValue(BigDecimal taxValue) {
		this.taxValue = taxValue;
	}

	/**
	 * @return the igstAmt
	 */
	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	/**
	 * @param igstAmt
	 *            the igstAmt to set
	 */
	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}

	/**
	 * @return the cgstAmt
	 */
	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}

	/**
	 * @param cgstAmt
	 *            the cgstAmt to set
	 */
	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = cgstAmt;
	}

	/**
	 * @return the sgstAmt
	 */
	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}

	/**
	 * @param sgstAmt
	 *            the sgstAmt to set
	 */
	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
	}

	/**
	 * @return the cessAmt
	 */
	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	/**
	 * @param cessAmt
	 *            the cessAmt to set
	 */
	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}

	public String getItmGstnBifurcation() {
		return itmGstnBifurcation;
	}

	public void setItmGstnBifurcation(String itmGstnBifurcation) {
		this.itmGstnBifurcation = itmGstnBifurcation;
	}

	

}
