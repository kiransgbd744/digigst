/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.NoArgsConstructor;

/**
 * @author BalaKrishna S
 *
 */
@NoArgsConstructor
public class VendorSummaryRespDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String level;
	
	@Expose
	@SerializedName("vendorPAN")
	private String vendorPAN;
	@Expose
	@SerializedName("vendorName")
	private String vendorName;
	@Expose
	@SerializedName("GSTIN")
	private String gstin;
	@Expose
	@SerializedName("docType")
	private String docType;
	@Expose
	@SerializedName("tableType")
	private String tableType;
	@Expose
	@SerializedName("count")
	private Integer count = 0;
	@Expose
	@SerializedName("taxableValue")
	private BigDecimal taxableValue = BigDecimal.ZERO;
	@Expose
	@SerializedName("totalTaxPayable")
	private BigDecimal totalTaxPayable = BigDecimal.ZERO;
	@Expose
	@SerializedName("totalCreditEligible")
	private BigDecimal totalCreditEligible = BigDecimal.ZERO;
	@Expose
	@SerializedName("tpIGST")
	private BigDecimal tpIGST = BigDecimal.ZERO;
	@Expose
	@SerializedName("tpCGST")
	private BigDecimal tpCGST = BigDecimal.ZERO;
	@Expose
	@SerializedName("tpSGST")
	private BigDecimal tpSGST = BigDecimal.ZERO;
	@Expose
	@SerializedName("tpCess")
	private BigDecimal tpCess = BigDecimal.ZERO;
	@Expose
	@SerializedName("ceIGST")
	private BigDecimal ceIGST = BigDecimal.ZERO;
	@Expose
	@SerializedName("ceCGST")
	private BigDecimal ceCGST = BigDecimal.ZERO;
	@Expose
	@SerializedName("ceSGST")
	private BigDecimal ceSGST = BigDecimal.ZERO;
	@Expose
	@SerializedName("ceCess")
	private BigDecimal ceCess = BigDecimal.ZERO;
	@Expose
	@SerializedName("invValue")
	private BigDecimal invValue = BigDecimal.ZERO;
	
	
	/**
	 * @return the vendorPAN
	 */
	public String getVendorPAN() {
		return vendorPAN;
	}
	
	
	/**
	 * @param level
	 */
	public VendorSummaryRespDto(String level) {
		super();
		this.level = level;
	}


	public VendorSummaryRespDto(String level, String vendorPAN,
			String vendorName,String gstin, String docType,
			String tableType, Integer count, BigDecimal taxableValue,
			BigDecimal totalTaxPayable, BigDecimal totalCreditEligible,
			BigDecimal tpIGST, BigDecimal tpCGST, BigDecimal tpSGST,
			BigDecimal tpCess, BigDecimal ceIGST, BigDecimal ceCGST,
			BigDecimal ceSGST, BigDecimal ceCess, BigDecimal invValue) {
		super();
		this.level = level;
		this.vendorPAN = vendorPAN;
		this.vendorName = vendorName;
		this.gstin = gstin;
		this.docType = docType;
		this.tableType = tableType;
		this.count = count;
		this.taxableValue = taxableValue;
		this.totalTaxPayable = totalTaxPayable;
		this.totalCreditEligible = totalCreditEligible;
		this.tpIGST = tpIGST;
		this.tpCGST = tpCGST;
		this.tpSGST = tpSGST;
		this.tpCess = tpCess;
		this.ceIGST = ceIGST;
		this.ceCGST = ceCGST;
		this.ceSGST = ceSGST;
		this.ceCess = ceCess;
		this.invValue = invValue;
	}
	/**
	 * @param vendorPAN the vendorPAN to set
	 */
	public void setVendorPAN(String vendorPAN) {
		this.vendorPAN = vendorPAN;
	}
	/**
	 * @return the vendorName
	 */
	public String getVendorName() {
		return vendorName;
	}
	/**
	 * @param vendorName the vendorName to set
	 */
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	/**
	 * @return the gstin
	 */
	public String getGstin() {
		return gstin;
	}
	/**
	 * @param gstin the gstin to set
	 */
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}
	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}
	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}
	/**
	 * @return the tableType
	 */
	public String getTableType() {
		return tableType;
	}
	/**
	 * @param tableType the tableType to set
	 */
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}
	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}
	/**
	 * @return the taxableValue
	 */
	public BigDecimal getTaxableValue() {
		return taxableValue;
	}
	/**
	 * @param taxableValue the taxableValue to set
	 */
	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}
	/**
	 * @return the totalTaxPayable
	 */
	public BigDecimal getTotalTaxPayable() {
		return totalTaxPayable;
	}
	/**
	 * @param totalTaxPayable the totalTaxPayable to set
	 */
	public void setTotalTaxPayable(BigDecimal totalTaxPayable) {
		this.totalTaxPayable = totalTaxPayable;
	}
	/**
	 * @return the totalCreditEligible
	 */
	public BigDecimal getTotalCreditEligible() {
		return totalCreditEligible;
	}
	/**
	 * @param totalCreditEligible the totalCreditEligible to set
	 */
	public void setTotalCreditEligible(BigDecimal totalCreditEligible) {
		this.totalCreditEligible = totalCreditEligible;
	}
	/**
	 * @return the tpIGST
	 */
	public BigDecimal getTpIGST() {
		return tpIGST;
	}
	/**
	 * @param tpIGST the tpIGST to set
	 */
	public void setTpIGST(BigDecimal tpIGST) {
		this.tpIGST = tpIGST;
	}
	/**
	 * @return the tpCGST
	 */
	public BigDecimal getTpCGST() {
		return tpCGST;
	}
	/**
	 * @param tpCGST the tpCGST to set
	 */
	public void setTpCGST(BigDecimal tpCGST) {
		this.tpCGST = tpCGST;
	}
	/**
	 * @return the tpSGST
	 */
	public BigDecimal getTpSGST() {
		return tpSGST;
	}
	/**
	 * @param tpSGST the tpSGST to set
	 */
	public void setTpSGST(BigDecimal tpSGST) {
		this.tpSGST = tpSGST;
	}
	/**
	 * @return the tpCess
	 */
	public BigDecimal getTpCess() {
		return tpCess;
	}
	/**
	 * @param tpCess the tpCess to set
	 */
	public void setTpCess(BigDecimal tpCess) {
		this.tpCess = tpCess;
	}
	/**
	 * @return the ceIGST
	 */
	public BigDecimal getCeIGST() {
		return ceIGST;
	}
	/**
	 * @param ceIGST the ceIGST to set
	 */
	public void setCeIGST(BigDecimal ceIGST) {
		this.ceIGST = ceIGST;
	}
	/**
	 * @return the ceCGST
	 */
	public BigDecimal getCeCGST() {
		return ceCGST;
	}
	/**
	 * @param ceCGST the ceCGST to set
	 */
	public void setCeCGST(BigDecimal ceCGST) {
		this.ceCGST = ceCGST;
	}
	/**
	 * @return the ceSGST
	 */
	public BigDecimal getCeSGST() {
		return ceSGST;
	}
	/**
	 * @param ceSGST the ceSGST to set
	 */
	public void setCeSGST(BigDecimal ceSGST) {
		this.ceSGST = ceSGST;
	}
	/**
	 * @return the ceCess
	 */
	public BigDecimal getCeCess() {
		return ceCess;
	}
	/**
	 * @param ceCess the ceCess to set
	 */
	public void setCeCess(BigDecimal ceCess) {
		this.ceCess = ceCess;
	}
	/**
	 * @return the invValue
	 */
	public BigDecimal getInvValue() {
		return invValue;
	}
	/**
	 * @param invValue the invValue to set
	 */
	public void setInvValue(BigDecimal invValue) {
		this.invValue = invValue;
	}
	
	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}
	public String getKey() {
		if ("L1".equals(level)) return vendorPAN;
		if ("L2".equals(level)) return vendorPAN + "#" + gstin ;
		if ("L3".equals(level)) 
			return vendorPAN + "#" + gstin + "#" + tableType;
		return vendorPAN + "#" + gstin + "#" + tableType + "#" + docType ;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VendorSummaryRespDto [level=" + level + ", vendorPAN="
				+ vendorPAN + ", vendorName=" + vendorName + ", gstin=" + gstin
				+ ", docType=" + docType + ", tableType=" + tableType
				+ ", count=" + count + ", taxableValue=" + taxableValue
				+ ", totalTaxPayable=" + totalTaxPayable
				+ ", totalCreditEligible=" + totalCreditEligible + ", tpIGST="
				+ tpIGST + ", tpCGST=" + tpCGST + ", tpSGST=" + tpSGST
				+ ", tpCess=" + tpCess + ", ceIGST=" + ceIGST + ", ceCGST="
				+ ceCGST + ", ceSGST=" + ceSGST + ", ceCess=" + ceCess
				+ ", invValue=" + invValue + "]";
	}	
	
	
	
	
}
