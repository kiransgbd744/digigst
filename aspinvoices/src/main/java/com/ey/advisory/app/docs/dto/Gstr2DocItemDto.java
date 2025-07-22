package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2DocItemDto {
	
	@Expose
	@SerializedName("glAccountCode")
	private String glAccountCode;
	
	@Expose
	@SerializedName("supplyType")
	private String supplyType;
	
	@Expose
	@SerializedName("lineNo")
	private Integer lineNo;
	
	@Expose
	@SerializedName("portCode")
	private String portCode;
	
	@Expose
	@SerializedName("billOfEntry")
	private Integer billOfEntry;
	
	@Expose
	@SerializedName("billOfEntryDate")
	private LocalDate billOfEntryDate;
	
	@Expose
	@SerializedName("cifValue")
	private BigDecimal cifValue;
	
	@Expose
	@SerializedName("customDuty")
	private BigDecimal customDuty;
	
	@Expose
	@SerializedName("hsnSac")
	private String hsnSac;
	
	@Expose
	@SerializedName("itemCode")
	private String itemCode;
	
	@Expose
	@SerializedName("itemDesc")
	private String itemDesc;
	
	@Expose
	@SerializedName("itemCategory")
	private String itemCategory;
	
	@Expose
	@SerializedName("uom")
	private String uom;
	
	@Expose
	@SerializedName("quantity")
	private BigDecimal quantity;
	
	@Expose
	@SerializedName("taxableValue")
	private BigDecimal taxableValue;
	
	@Expose
	@SerializedName("igstRate")
	private BigDecimal igstRate;
	
	@Expose
	@SerializedName("igstAmt")
	private BigDecimal igstAmt;
	
	@Expose
	@SerializedName("cgstRate")
	private BigDecimal cgstRate;
	
	@Expose
	@SerializedName("cgstAmt")
	private BigDecimal cgstAmt;
	
	@Expose
	@SerializedName("sgstRate")
	private BigDecimal sgstRate;
	
	@Expose
	@SerializedName("sgstAmt")
	private BigDecimal sgstAmt;
	
	@Expose
	@SerializedName("cessRateAdvalorem")
	private BigDecimal cessRateAdvalorem;
	
	@Expose
	@SerializedName("cessAmtAdvalorem")
	private BigDecimal cessAmtAdvalorem;
	
	@Expose
	@SerializedName("cessRateSpecific")
	private BigDecimal cessRateSpecific;		
	
	@Expose
	@SerializedName("cessAmtSpecific")
	private BigDecimal cessAmtSpecific;
	
	@Expose
	@SerializedName("lineItemAmt")
	private BigDecimal lineItemAmt;
	
	@Expose
	@SerializedName("eligibilityInd")
	private String eligibilityInd;
	
	@Expose
	@SerializedName("commonSupplyInd")
	private String commonSupplyInd;
	
	@Expose
	@SerializedName("avlGst")
	private BigDecimal avlGst;
	
	@Expose
	@SerializedName("avlCgst")
	private BigDecimal avlCgst;
	
	@Expose
	@SerializedName("avlSgst")
	private BigDecimal avlSgst;
	
	@Expose
	@SerializedName("avlCess")
	private BigDecimal avlCess;
	
	@Expose
	@SerializedName("itcRevId")
	private String itcRevId;
	
	@Expose
	@SerializedName("crdrReason")
	private String crdrReason;
	
	@Expose
	@SerializedName("paymtVoucherNum")
	private String paymtVoucherNum;
	
	@Expose
	@SerializedName("paymentDate")
	private LocalDate paymentDate;	
	
	@Expose
	@SerializedName("contractNum")
	private String contractNum;
	
	@Expose
	@SerializedName("contractDate")
	private LocalDate contractDate;
	
	@Expose
	@SerializedName("contractVal")
	private BigDecimal contractVal;

	/**
	 * @return the glAccountCode
	 */
	public String getGlAccountCode() {
		return glAccountCode;
	}

	/**
	 * @param glAccountCode the glAccountCode to set
	 */
	public void setGlAccountCode(String glAccountCode) {
		this.glAccountCode = glAccountCode;
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
	 * @return the lineNo
	 */
	public Integer getLineNo() {
		return lineNo;
	}

	/**
	 * @param lineNo the lineNo to set
	 */
	public void setLineNo(Integer lineNo) {
		this.lineNo = lineNo;
	}

	/**
	 * @return the portCode
	 */
	public String getPortCode() {
		return portCode;
	}

	/**
	 * @param portCode the portCode to set
	 */
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	/**
	 * @return the billOfEntry
	 */
	public Integer getBillOfEntry() {
		return billOfEntry;
	}

	/**
	 * @param billOfEntry the billOfEntry to set
	 */
	public void setBillOfEntry(Integer billOfEntry) {
		this.billOfEntry = billOfEntry;
	}

	/**
	 * @return the billOfEntryDate
	 */
	public LocalDate getBillOfEntryDate() {
		return billOfEntryDate;
	}

	/**
	 * @param billOfEntryDate the billOfEntryDate to set
	 */
	public void setBillOfEntryDate(LocalDate billOfEntryDate) {
		this.billOfEntryDate = billOfEntryDate;
	}

	/**
	 * @return the cifValue
	 */
	public BigDecimal getCifValue() {
		return cifValue;
	}

	/**
	 * @param cifValue the cifValue to set
	 */
	public void setCifValue(BigDecimal cifValue) {
		this.cifValue = cifValue;
	}

	/**
	 * @return the customDuty
	 */
	public BigDecimal getCustomDuty() {
		return customDuty;
	}

	/**
	 * @param customDuty the customDuty to set
	 */
	public void setCustomDuty(BigDecimal customDuty) {
		this.customDuty = customDuty;
	}

	/**
	 * @return the hsnSac
	 */
	public String getHsnSac() {
		return hsnSac;
	}

	/**
	 * @param hsnSac the hsnSac to set
	 */
	public void setHsnSac(String hsnSac) {
		this.hsnSac = hsnSac;
	}

	/**
	 * @return the itemCode
	 */
	public String getItemCode() {
		return itemCode;
	}

	/**
	 * @param itemCode the itemCode to set
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
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
	 * @return the itemCategory
	 */
	public String getItemCategory() {
		return itemCategory;
	}

	/**
	 * @param itemCategory the itemCategory to set
	 */
	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}

	/**
	 * @return the uom
	 */
	public String getUom() {
		return uom;
	}

	/**
	 * @param uom the uom to set
	 */
	public void setUom(String uom) {
		this.uom = uom;
	}

	/**
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
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
	 * @return the igstRate
	 */
	public BigDecimal getIgstRate() {
		return igstRate;
	}

	/**
	 * @param igstRate the igstRate to set
	 */
	public void setIgstRate(BigDecimal igstRate) {
		this.igstRate = igstRate;
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
	 * @return the cgstRate
	 */
	public BigDecimal getCgstRate() {
		return cgstRate;
	}

	/**
	 * @param cgstRate the cgstRate to set
	 */
	public void setCgstRate(BigDecimal cgstRate) {
		this.cgstRate = cgstRate;
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
	 * @return the sgstRate
	 */
	public BigDecimal getSgstRate() {
		return sgstRate;
	}

	/**
	 * @param sgstRate the sgstRate to set
	 */
	public void setSgstRate(BigDecimal sgstRate) {
		this.sgstRate = sgstRate;
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
	 * @return the cessRateSpecific
	 */
	public BigDecimal getCessRateSpecific() {
		return cessRateSpecific;
	}

	/**
	 * @param cessRateSpecific the cessRateSpecific to set
	 */
	public void setCessRateSpecific(BigDecimal cessRateSpecific) {
		this.cessRateSpecific = cessRateSpecific;
	}

	/**
	 * @return the cessAmtSpecific
	 */
	public BigDecimal getCessAmtSpecific() {
		return cessAmtSpecific;
	}

	/**
	 * @param cessAmtSpecific the cessAmtSpecific to set
	 */
	public void setCessAmtSpecific(BigDecimal cessAmtSpecific) {
		this.cessAmtSpecific = cessAmtSpecific;
	}

	/**
	 * @return the lineItemAmt
	 */
	public BigDecimal getLineItemAmt() {
		return lineItemAmt;
	}

	/**
	 * @param lineItemAmt the lineItemAmt to set
	 */
	public void setLineItemAmt(BigDecimal lineItemAmt) {
		this.lineItemAmt = lineItemAmt;
	}

	/**
	 * @return the eligibilityInd
	 */
	public String getEligibilityInd() {
		return eligibilityInd;
	}

	/**
	 * @param eligibilityInd the eligibilityInd to set
	 */
	public void setEligibilityInd(String eligibilityInd) {
		this.eligibilityInd = eligibilityInd;
	}

	/**
	 * @return the commonSupplyInd
	 */
	public String getCommonSupplyInd() {
		return commonSupplyInd;
	}

	/**
	 * @param commonSupplyInd the commonSupplyInd to set
	 */
	public void setCommonSupplyInd(String commonSupplyInd) {
		this.commonSupplyInd = commonSupplyInd;
	}

	/**
	 * @return the avlGst
	 */
	public BigDecimal getAvlGst() {
		return avlGst;
	}

	/**
	 * @param avlGst the avlGst to set
	 */
	public void setAvlGst(BigDecimal avlGst) {
		this.avlGst = avlGst;
	}

	/**
	 * @return the avlCgst
	 */
	public BigDecimal getAvlCgst() {
		return avlCgst;
	}

	/**
	 * @param avlCgst the avlCgst to set
	 */
	public void setAvlCgst(BigDecimal avlCgst) {
		this.avlCgst = avlCgst;
	}

	/**
	 * @return the avlSgst
	 */
	public BigDecimal getAvlSgst() {
		return avlSgst;
	}

	/**
	 * @param avlSgst the avlSgst to set
	 */
	public void setAvlSgst(BigDecimal avlSgst) {
		this.avlSgst = avlSgst;
	}

	/**
	 * @return the avlCess
	 */
	public BigDecimal getAvlCess() {
		return avlCess;
	}

	/**
	 * @param avlCess the avlCess to set
	 */
	public void setAvlCess(BigDecimal avlCess) {
		this.avlCess = avlCess;
	}

	/**
	 * @return the itcRevId
	 */
	public String getItcRevId() {
		return itcRevId;
	}

	/**
	 * @param itcRevId the itcRevId to set
	 */
	public void setItcRevId(String itcRevId) {
		this.itcRevId = itcRevId;
	}

	/**
	 * @return the crdrReason
	 */
	public String getCrdrReason() {
		return crdrReason;
	}

	/**
	 * @param crdrReason the crdrReason to set
	 */
	public void setCrdrReason(String crdrReason) {
		this.crdrReason = crdrReason;
	}

	/**
	 * @return the paymtVoucherNum
	 */
	public String getPaymtVoucherNum() {
		return paymtVoucherNum;
	}

	/**
	 * @param paymtVoucherNum the paymtVoucherNum to set
	 */
	public void setPaymtVoucherNum(String paymtVoucherNum) {
		this.paymtVoucherNum = paymtVoucherNum;
	}

	/**
	 * @return the paymentDate
	 */
	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	/**
	 * @param paymentDate the paymentDate to set
	 */
	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}

	/**
	 * @return the contractNum
	 */
	public String getContractNum() {
		return contractNum;
	}

	/**
	 * @param contractNum the contractNum to set
	 */
	public void setContractNum(String contractNum) {
		this.contractNum = contractNum;
	}

	/**
	 * @return the contractDate
	 */
	public LocalDate getContractDate() {
		return contractDate;
	}

	/**
	 * @param contractDate the contractDate to set
	 */
	public void setContractDate(LocalDate contractDate) {
		this.contractDate = contractDate;
	}

	/**
	 * @return the contractVal
	 */
	public BigDecimal getContractVal() {
		return contractVal;
	}

	/**
	 * @param contractVal the contractVal to set
	 */
	public void setContractVal(BigDecimal contractVal) {
		this.contractVal = contractVal;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"Gstr2DocItemDto [glAccountCode=%s, supplyType=%s, lineNo=%s, "
				+ "portCode=%s, billOfEntry=%s, billOfEntryDate=%s, "
				+ "cifValue=%s, customDuty=%s, hsnSac=%s, itemCode=%s, "
				+ "itemDesc=%s, itemCategory=%s, uom=%s, quantity=%s,"
				+ " taxableValue=%s, igstRate=%s, igstAmt=%s, cgstRate=%s, "
				+ "cgstAmt=%s, sgstRate=%s, sgstAmt=%s, cessRateAdvalorem=%s, "
				+ "cessAmtAdvalorem=%s, cessRateSpecific=%s, "
				+ "cessAmtSpecific=%s, lineItemAmt=%s, eligibilityInd=%s, "
				+ "commonSupplyInd=%s, avlGst=%s, avlCgst=%s, avlSgst=%s, "
				+ "avlCess=%s, itcRevId=%s, crdrReason=%s, "
				+ "paymtVoucherNum=%s, paymentDate=%s, contractNum=%s, "
				+ "contractDate=%s, contractVal=%s]",
				glAccountCode, supplyType, lineNo, portCode, billOfEntry,
				billOfEntryDate, cifValue, customDuty, hsnSac, itemCode,
				itemDesc, itemCategory, uom, quantity, taxableValue, igstRate,
				igstAmt, cgstRate, cgstAmt, sgstRate, sgstAmt,
				cessRateAdvalorem, cessAmtAdvalorem, cessRateSpecific,
				cessAmtSpecific, lineItemAmt, eligibilityInd, commonSupplyInd,
				avlGst, avlCgst, avlSgst, avlCess, itcRevId, crdrReason,
				paymtVoucherNum, paymentDate, contractNum, contractDate,
				contractVal);
	}

	

}
