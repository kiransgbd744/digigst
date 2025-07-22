/**
 * 
 */
package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;

/**
 * @author BalaKrishna S
 *
 */
public class Gstr1SummaryScreenItemRespDto implements Cloneable {

	private String taxDocType;
	private Integer aspCount = 0;
	private BigDecimal aspInvoiceValue = BigDecimal.ZERO;
	private BigDecimal aspTaxableValue = BigDecimal.ZERO;
	private BigDecimal aspTaxPayble = BigDecimal.ZERO;
	private BigDecimal aspIgst = BigDecimal.ZERO;
	private BigDecimal aspCgst = BigDecimal.ZERO;
	private BigDecimal aspSgst = BigDecimal.ZERO;
	private BigDecimal aspCess = BigDecimal.ZERO;
	
	private Integer gstnCount = 0;
	private BigDecimal gstnInvoiceValue = BigDecimal.ZERO;
	private BigDecimal gstnTaxableValue = BigDecimal.ZERO;
	private BigDecimal gstnTaxPayble = BigDecimal.ZERO;
	private BigDecimal gstnIgst = BigDecimal.ZERO;
	private BigDecimal gstnCgst = BigDecimal.ZERO;
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	private BigDecimal gstnCess = BigDecimal.ZERO;
  
	private Integer diffCount = 0;
	private BigDecimal diffInvoiceValue = BigDecimal.ZERO;
	private BigDecimal diffTaxableValue = BigDecimal.ZERO;
	private BigDecimal diffTaxPayble = BigDecimal.ZERO;
	private BigDecimal diffIgst = BigDecimal.ZERO;
	private BigDecimal diffCgst = BigDecimal.ZERO;
	private BigDecimal diffSgst = BigDecimal.ZERO;
	private BigDecimal diffCess = BigDecimal.ZERO;
	
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
	 * @return the aspCount
	 */
	public Integer getAspCount() {
		return aspCount;
	}
	/**
	 * @param aspCount the aspCount to set
	 */
	public void setAspCount(Integer aspCount) {
		this.aspCount = aspCount;
	}
	/**
	 * @return the aspTaxableValue
	 */
	public BigDecimal getAspTaxableValue() {
		return aspTaxableValue;
	}
	/**
	 * @param aspTaxableValue the aspTaxableValue to set
	 */
	public void setAspTaxableValue(BigDecimal aspTaxableValue) {
		this.aspTaxableValue = aspTaxableValue;
	}
	/**
	 * @return the aspTaxPayble
	 */
	public BigDecimal getAspTaxPayble() {
		return aspTaxPayble;
	}
	/**
	 * @param aspTaxPayble the aspTaxPayble to set
	 */
	public void setAspTaxPayble(BigDecimal aspTaxPayble) {
		this.aspTaxPayble = aspTaxPayble;
	}
	/**
	 * @return the aspIgst
	 */
	public BigDecimal getAspIgst() {
		return aspIgst;
	}
	/**
	 * @param aspIgst the aspIgst to set
	 */
	public void setAspIgst(BigDecimal aspIgst) {
		this.aspIgst = aspIgst;
	}
	/**
	 * @return the aspCgst
	 */
	public BigDecimal getAspCgst() {
		return aspCgst;
	}
	/**
	 * @param aspCgst the aspCgst to set
	 */
	public void setAspCgst(BigDecimal aspCgst) {
		this.aspCgst = aspCgst;
	}
	/**
	 * @return the aspSgst
	 */
	public BigDecimal getAspSgst() {
		return aspSgst;
	}
	/**
	 * @param aspSgst the aspSgst to set
	 */
	public void setAspSgst(BigDecimal aspSgst) {
		this.aspSgst = aspSgst;
	}
	/**
	 * @return the aspCess
	 */
	public BigDecimal getAspCess() {
		return aspCess;
	}
	/**
	 * @param aspCess the aspCess to set
	 */
	public void setAspCess(BigDecimal aspCess) {
		this.aspCess = aspCess;
	}
	/**
	 * @return the gstnCount
	 */
	public Integer getGstnCount() {
		return gstnCount;
	}
	/**
	 * @param gstnCount the gstnCount to set
	 */
	public void setGstnCount(Integer gstnCount) {
		this.gstnCount = gstnCount;
	}
	/**
	 * @return the gstnTaxableValue
	 */
	public BigDecimal getGstnTaxableValue() {
		return gstnTaxableValue;
	}
	/**
	 * @param gstnTaxableValue the gstnTaxableValue to set
	 */
	public void setGstnTaxableValue(BigDecimal gstnTaxableValue) {
		this.gstnTaxableValue = gstnTaxableValue;
	}
	/**
	 * @return the gstnTaxPayble
	 */
	public BigDecimal getGstnTaxPayble() {
		return gstnTaxPayble;
	}
	/**
	 * @param gstnTaxPayble the gstnTaxPayble to set
	 */
	public void setGstnTaxPayble(BigDecimal gstnTaxPayble) {
		this.gstnTaxPayble = gstnTaxPayble;
	}
	/**
	 * @return the gstnIgst
	 */
	public BigDecimal getGstnIgst() {
		return gstnIgst;
	}
	/**
	 * @param gstnIgst the gstnIgst to set
	 */
	public void setGstnIgst(BigDecimal gstnIgst) {
		this.gstnIgst = gstnIgst;
	}
	/**
	 * @return the gstnCgst
	 */
	public BigDecimal getGstnCgst() {
		return gstnCgst;
	}
	/**
	 * @param gstnCgst the gstnCgst to set
	 */
	public void setGstnCgst(BigDecimal gstnCgst) {
		this.gstnCgst = gstnCgst;
	}
	/**
	 * @return the gstnSgst
	 */
	public BigDecimal getGstnSgst() {
		return gstnSgst;
	}
	/**
	 * @param gstnSgst the gstnSgst to set
	 */
	public void setGstnSgst(BigDecimal gstnSgst) {
		this.gstnSgst = gstnSgst;
	}
	/**
	 * @return the gstnCess
	 */
	public BigDecimal getGstnCess() {
		return gstnCess;
	}
	/**
	 * @param gstnCess the gstnCess to set
	 */
	public void setGstnCess(BigDecimal gstnCess) {
		this.gstnCess = gstnCess;
	}
	/**
	 * @return the diffCount
	 */
	public Integer getDiffCount() {
		return diffCount;
	}
	/**
	 * @param diffCount the diffCount to set
	 */
	public void setDiffCount(Integer diffCount) {
		this.diffCount = diffCount;
	}
	/**
	 * @return the diffTaxableValue
	 */
	public BigDecimal getDiffTaxableValue() {
		return diffTaxableValue;
	}
	/**
	 * @param diffTaxableValue the diffTaxableValue to set
	 */
	public void setDiffTaxableValue(BigDecimal diffTaxableValue) {
		this.diffTaxableValue = diffTaxableValue;
	}
	/**
	 * @return the diffTaxPayble
	 */
	public BigDecimal getDiffTaxPayble() {
		return diffTaxPayble;
	}
	/**
	 * @param diffTaxPayble the diffTaxPayble to set
	 */
	public void setDiffTaxPayble(BigDecimal diffTaxPayble) {
		this.diffTaxPayble = diffTaxPayble;
	}
	/**
	 * @return the diffIgst
	 */
	public BigDecimal getDiffIgst() {
		return diffIgst;
	}
	/**
	 * @param diffIgst the diffIgst to set
	 */
	public void setDiffIgst(BigDecimal diffIgst) {
		this.diffIgst = diffIgst;
	}
	/**
	 * @return the diffCgst
	 */
	public BigDecimal getDiffCgst() {
		return diffCgst;
	}
	/**
	 * @param diffCgst the diffCgst to set
	 */
	public void setDiffCgst(BigDecimal diffCgst) {
		this.diffCgst = diffCgst;
	}
	/**
	 * @return the diffSgst
	 */
	public BigDecimal getDiffSgst() {
		return diffSgst;
	}
	/**
	 * @param diffSgst the diffSgst to set
	 */
	public void setDiffSgst(BigDecimal diffSgst) {
		this.diffSgst = diffSgst;
	}
	/**
	 * @return the diffCess
	 */
	public BigDecimal getDiffCess() {
		return diffCess;
	}
	/**
	 * @param diffCess the diffCess to set
	 */
	public void setDiffCess(BigDecimal diffCess) {
		this.diffCess = diffCess;
	}
	/**
	 * @return the aspInvoiceValue
	 */
	public BigDecimal getAspInvoiceValue() {
		return aspInvoiceValue;
	}
	/**
	 * @param aspInvoiceValue the aspInvoiceValue to set
	 */
	public void setAspInvoiceValue(BigDecimal aspInvoiceValue) {
		this.aspInvoiceValue = aspInvoiceValue;
	}
	/**
	 * @return the gstnInvoiceValue
	 */
	public BigDecimal getGstnInvoiceValue() {
		return gstnInvoiceValue;
	}
	/**
	 * @param gstnInvoiceValue the gstnInvoiceValue to set
	 */
	public void setGstnInvoiceValue(BigDecimal gstnInvoiceValue) {
		this.gstnInvoiceValue = gstnInvoiceValue;
	}
	/**
	 * @return the diffInvoiceValue
	 */
	public BigDecimal getDiffInvoiceValue() {
		return diffInvoiceValue;
	}
	/**
	 * @param diffInvoiceValue the diffInvoiceValue to set
	 */
	public void setDiffInvoiceValue(BigDecimal diffInvoiceValue) {
		this.diffInvoiceValue = diffInvoiceValue;
	}
	
	
	
	
}
