/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

/**
 * @author BalaKrishna S
 *
 */
public class Gstr1SummaryNilSectionDto {

	private String taxDocType;
	private String docType;
	private Integer total = 0;
	private BigDecimal aspNitRated = BigDecimal.ZERO;
	private BigDecimal aspExempted = BigDecimal.ZERO;
	private BigDecimal aspNonGst = BigDecimal.ZERO;

	private BigDecimal gstnNitRated = BigDecimal.ZERO;
	private BigDecimal gstnExempted = BigDecimal.ZERO;
	private BigDecimal gstnNonGst = BigDecimal.ZERO;

	/**
	 * @return the taxDocType
	 */
	public String getTaxDocType() {
		return taxDocType;
	}

	/**
	 * @param taxDocType
	 *            the taxDocType to set
	 */
	public void setTaxDocType(String taxDocType) {
		this.taxDocType = taxDocType;
	}

	/**
	 * @return the aspNitRated
	 */
	public BigDecimal getAspNitRated() {
		return aspNitRated;
	}

	/**
	 * @param aspNitRated
	 *            the aspNitRated to set
	 */
	public void setAspNitRated(BigDecimal aspNitRated) {
		this.aspNitRated = aspNitRated;
	}

	/**
	 * @return the aspExempted
	 */
	public BigDecimal getAspExempted() {
		return aspExempted;
	}

	/**
	 * @param aspExempted
	 *            the aspExempted to set
	 */
	public void setAspExempted(BigDecimal aspExempted) {
		this.aspExempted = aspExempted;
	}

	/**
	 * @return the aspNonGst
	 */
	public BigDecimal getAspNonGst() {
		return aspNonGst;
	}

	/**
	 * @param aspNonGst
	 *            the aspNonGst to set
	 */
	public void setAspNonGst(BigDecimal aspNonGst) {
		this.aspNonGst = aspNonGst;
	}

	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * @param docType
	 *            the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	public BigDecimal getGstnNitRated() {
		return gstnNitRated;
	}

	public void setGstnNitRated(BigDecimal gstnNitRated) {
		this.gstnNitRated = gstnNitRated;
	}

	public BigDecimal getGstnExempted() {
		return gstnExempted;
	}

	public void setGstnExempted(BigDecimal gstnExempted) {
		this.gstnExempted = gstnExempted;
	}

	public BigDecimal getGstnNonGst() {
		return gstnNonGst;
	}

	public void setGstnNonGst(BigDecimal gstnNonGst) {
		this.gstnNonGst = gstnNonGst;
	}

	/**
	 * @return the total
	 */
	public Integer getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}

	
}
