package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

/**
 * DocErrorDetailDto class is responsible for transferring line item details
 * response data from server to UI
 *
 * @author Mohana.Dasari
 */
public class DocLineItemDto {

	private Long id;
	
	private int itemNo;
	
	private String hsnsacCode;
	
	private String itemDesc;
	
	private String itemType;
	
	private String itemUqc;
	
	private BigDecimal itemQty;
	
	private BigDecimal taxableAmt;
	
	private BigDecimal igstAmt;
	
	private BigDecimal cgstAmt;
	
	private BigDecimal sgstAmt;
	
	private BigDecimal cessRateSpecfic;
	
	private BigDecimal cessAmtSpecfic;
	
	private BigDecimal cessRateAdvalorem;
	
	private BigDecimal cessAmtAdvalorem;
	
	private String supplyType;
	
	private BigDecimal taxableValue;
	
	private BigDecimal taxRate;

	private String productCode;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the itemNo
	 */
	public int getItemNo() {
		return itemNo;
	}

	/**
	 * @param itemNo the itemNo to set
	 */
	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
	}

	/**
	 * @return the hsnsacCode
	 */
	public String getHsnsacCode() {
		return hsnsacCode;
	}

	/**
	 * @param hsnsacCode the hsnsacCode to set
	 */
	public void setHsnsacCode(String hsnsacCode) {
		this.hsnsacCode = hsnsacCode;
	}

	/**
	 * @return the itemDesc
	 */
	public String getItemDesc() {
		return itemDesc;
	}

	/**
	 * @param itemDesc the itemDesc to set
	 */
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	/**
	 * @return the itemType
	 */
	public String getItemType() {
		return itemType;
	}

	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	/**
	 * @return the itemUqc
	 */
	public String getItemUqc() {
		return itemUqc;
	}

	/**
	 * @param itemUqc the itemUqc to set
	 */
	public void setItemUqc(String itemUqc) {
		this.itemUqc = itemUqc;
	}

	/**
	 * @return the itemQty
	 */
	public BigDecimal getItemQty() {
		return itemQty;
	}

	/**
	 * @param itemQty the itemQty to set
	 */
	public void setItemQty(BigDecimal itemQty) {
		this.itemQty = itemQty;
	}

	/**
	 * @return the taxableAmt
	 */
	public BigDecimal getTaxableAmt() {
		return taxableAmt;
	}

	/**
	 * @param taxableAmt the taxableAmt to set
	 */
	public void setTaxableAmt(BigDecimal taxableAmt) {
		this.taxableAmt = taxableAmt;
	}

	/**
	 * @return the igstAmt
	 */
	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	/**
	 * @param igstAmt the igstAmt to set
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
	 * @param cgstAmt the cgstAmt to set
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
	 * @param sgstAmt the sgstAmt to set
	 */
	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
	}

	/**
	 * @return the cessRateSpecfic
	 */
	public BigDecimal getCessRateSpecfic() {
		return cessRateSpecfic;
	}

	/**
	 * @param cessRateSpecfic the cessRateSpecfic to set
	 */
	public void setCessRateSpecfic(BigDecimal cessRateSpecfic) {
		this.cessRateSpecfic = cessRateSpecfic;
	}

	/**
	 * @return the cessAmtSpecfic
	 */
	public BigDecimal getCessAmtSpecfic() {
		return cessAmtSpecfic;
	}

	/**
	 * @param cessAmtSpecfic the cessAmtSpecfic to set
	 */
	public void setCessAmtSpecfic(BigDecimal cessAmtSpecfic) {
		this.cessAmtSpecfic = cessAmtSpecfic;
	}

	/**
	 * @return the cessRateAdvalorem
	 */
	public BigDecimal getCessRateAdvalorem() {
		return cessRateAdvalorem;
	}

	/**
	 * @param cessRateAdvalorem the cessRateAdvalorem to set
	 */
	public void setCessRateAdvalorem(BigDecimal cessRateAdvalorem) {
		this.cessRateAdvalorem = cessRateAdvalorem;
	}

	/**
	 * @return the cessAmtAdvalorem
	 */
	public BigDecimal getCessAmtAdvalorem() {
		return cessAmtAdvalorem;
	}

	/**
	 * @param cessAmtAdvalorem the cessAmtAdvalorem to set
	 */
	public void setCessAmtAdvalorem(BigDecimal cessAmtAdvalorem) {
		this.cessAmtAdvalorem = cessAmtAdvalorem;
	}

	/**
	 * @return the supplyType
	 */
	public String getSupplyType() {
		return supplyType;
	}

	/**
	 * @param supplyType the supplyType to set
	 */
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
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
	 * @return the taxRate
	 */
	public BigDecimal getTaxRate() {
		return taxRate;
	}

	/**
	 * @param taxRate the taxRate to set
	 */
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

}
