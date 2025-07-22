/**
 * 
 */
package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;

/**
 * @author BalaKrishna S
 *
 */
public class Gstr1SummaryScreenNilRespDto implements Cloneable {

    private String taxDocType;
    private Integer total;
    private BigDecimal aspNitRated;
    private BigDecimal aspExempted;
    private BigDecimal aspNonGst;
    private BigDecimal gstnNitRated;
    private BigDecimal gstnExempted;
    private BigDecimal gstnNonGst;
    private BigDecimal diffNitRated;
    private BigDecimal diffExempted;
    private BigDecimal diffNonGst;
    
    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }
    
	/**
	 * @return the taxDocType
	 */
	public String getTaxDocType() {
		return taxDocType;
	}
	/**
	 * @param taxDocType the taxDocType to set
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
	 * @param aspNitRated the aspNitRated to set
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
	 * @param aspExempted the aspExempted to set
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
	 * @param aspNonGst the aspNonGst to set
	 */
	public void setAspNonGst(BigDecimal aspNonGst) {
		this.aspNonGst = aspNonGst;
	}
	/**
	 * @return the gstnNitRated
	 */
	public BigDecimal getGstnNitRated() {
		return gstnNitRated;
	}
	/**
	 * @param gstnNitRated the gstnNitRated to set
	 */
	public void setGstnNitRated(BigDecimal gstnNitRated) {
		this.gstnNitRated = gstnNitRated;
	}
	/**
	 * @return the gstnExempted
	 */
	public BigDecimal getGstnExempted() {
		return gstnExempted;
	}
	/**
	 * @param gstnExempted the gstnExempted to set
	 */
	public void setGstnExempted(BigDecimal gstnExempted) {
		this.gstnExempted = gstnExempted;
	}
	/**
	 * @return the gstnNonGst
	 */
	public BigDecimal getGstnNonGst() {
		return gstnNonGst;
	}
	/**
	 * @param gstnNonGst the gstnNonGst to set
	 */
	public void setGstnNonGst(BigDecimal gstnNonGst) {
		this.gstnNonGst = gstnNonGst;
	}
	/**
	 * @return the diffNitRated
	 */
	public BigDecimal getDiffNitRated() {
		return diffNitRated;
	}
	/**
	 * @param diffNitRated the diffNitRated to set
	 */
	public void setDiffNitRated(BigDecimal diffNitRated) {
		this.diffNitRated = diffNitRated;
	}
	/**
	 * @return the diffExempted
	 */
	public BigDecimal getDiffExempted() {
		return diffExempted;
	}
	/**
	 * @param diffExempted the diffExempted to set
	 */
	public void setDiffExempted(BigDecimal diffExempted) {
		this.diffExempted = diffExempted;
	}
	/**
	 * @return the diffNonGst
	 */
	public BigDecimal getDiffNonGst() {
		return diffNonGst;
	}
	/**
	 * @param diffNonGst the diffNonGst to set
	 */
	public void setDiffNonGst(BigDecimal diffNonGst) {
		this.diffNonGst = diffNonGst;
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
