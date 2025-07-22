package com.ey.advisory.app.docs.dto.gstr2;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GSTR2APRMatchingDto {
	
	@Expose
	@SerializedName("section")
	private String section;
	@Expose
	@SerializedName("diffCount")
	private Integer diffCount;
	@Expose
	@SerializedName("count2A")
	private Integer count2A;
	@Expose
	@SerializedName("taxableValue2A")
	private BigDecimal taxableValue2A;
	@Expose
	@SerializedName("igst2A")
	private BigDecimal igst2A;
	@Expose
	@SerializedName("cgst2A")
	private BigDecimal cgst2A;
	@Expose
	@SerializedName("sgst2A")
	private BigDecimal sgst2A;
	@Expose
	@SerializedName("cess2A")
	private  BigDecimal cess2A;
	@Expose
	@SerializedName("prCount")
	private Integer prCount;
	@Expose
	@SerializedName("prTaxableValue")
	private BigDecimal prTaxableValue;
	@Expose
	@SerializedName("prIgst")
	private BigDecimal prIgst;
	@Expose
	@SerializedName("prCgst")
	private BigDecimal prCgst;
	@Expose
	@SerializedName("prSgst")
	private BigDecimal prSgst;
	@Expose
	@SerializedName("prCess")
	private BigDecimal prCess;
	/**
	 * @return the section
	 */
	public String getSection() {
		return section;
	}
	/**
	 * @param section the section to set
	 */
	public void setSection(String section) {
		this.section = section;
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
	 * @return the count2A
	 */
	public Integer getCount2A() {
		return count2A;
	}
	/**
	 * @param count2a the count2A to set
	 */
	public void setCount2A(Integer count2a) {
		count2A = count2a;
	}
	/**
	 * @return the taxableValue2A
	 */
	public BigDecimal getTaxableValue2A() {
		return taxableValue2A;
	}
	/**
	 * @param taxableValue2A the taxableValue2A to set
	 */
	public void setTaxableValue2A(BigDecimal taxableValue2A) {
		this.taxableValue2A = taxableValue2A;
	}
	/**
	 * @return the igst2A
	 */
	public BigDecimal getIgst2A() {
		return igst2A;
	}
	/**
	 * @param igst2a the igst2A to set
	 */
	public void setIgst2A(BigDecimal igst2a) {
		igst2A = igst2a;
	}
	/**
	 * @return the cgst2A
	 */
	public BigDecimal getCgst2A() {
		return cgst2A;
	}
	/**
	 * @param cgst2a the cgst2A to set
	 */
	public void setCgst2A(BigDecimal cgst2a) {
		cgst2A = cgst2a;
	}
	/**
	 * @return the sgst2A
	 */
	public BigDecimal getSgst2A() {
		return sgst2A;
	}
	/**
	 * @param sgst2a the sgst2A to set
	 */
	public void setSgst2A(BigDecimal sgst2a) {
		sgst2A = sgst2a;
	}
	/**
	 * @return the cess2A
	 */
	public BigDecimal getCess2A() {
		return cess2A;
	}
	/**
	 * @param cess2a the cess2A to set
	 */
	public void setCess2A(BigDecimal cess2a) {
		cess2A = cess2a;
	}
	/**
	 * @return the prCount
	 */
	public Integer getPrCount() {
		return prCount;
	}
	/**
	 * @param prCount the prCount to set
	 */
	public void setPrCount(Integer prCount) {
		this.prCount = prCount;
	}
	/**
	 * @return the prTaxableValue
	 */
	public BigDecimal getPrTaxableValue() {
		return prTaxableValue;
	}
	/**
	 * @param prTaxableValue the prTaxableValue to set
	 */
	public void setPrTaxableValue(BigDecimal prTaxableValue) {
		this.prTaxableValue = prTaxableValue;
	}
	/**
	 * @return the prIgst
	 */
	public BigDecimal getPrIgst() {
		return prIgst;
	}
	/**
	 * @param prIgst the prIgst to set
	 */
	public void setPrIgst(BigDecimal prIgst) {
		this.prIgst = prIgst;
	}
	/**
	 * @return the prCgst
	 */
	public BigDecimal getPrCgst() {
		return prCgst;
	}
	/**
	 * @param prCgst the prCgst to set
	 */
	public void setPrCgst(BigDecimal prCgst) {
		this.prCgst = prCgst;
	}
	/**
	 * @return the prSgst
	 */
	public BigDecimal getPrSgst() {
		return prSgst;
	}
	/**
	 * @param prSgst the prSgst to set
	 */
	public void setPrSgst(BigDecimal prSgst) {
		this.prSgst = prSgst;
	}
	/**
	 * @return the prCess
	 */
	public BigDecimal getPrCess() {
		return prCess;
	}
	/**
	 * @param prCess the prCess to set
	 */
	public void setPrCess(BigDecimal prCess) {
		this.prCess = prCess;
	}
	
	
	

}
