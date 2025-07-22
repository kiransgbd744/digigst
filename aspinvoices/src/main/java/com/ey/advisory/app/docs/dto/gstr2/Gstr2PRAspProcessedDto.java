package com.ey.advisory.app.docs.dto.gstr2;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2PRAspProcessedDto {
	
	@Expose
	@SerializedName("view")
	private String view;
	@Expose
	@SerializedName("dataCategory")
	private String dataCategory;
	@Expose
	@SerializedName("tableNo")
	private String tableNo;
	@Expose
	@SerializedName("gstnStatus")
	private String gstnStatus;
	@Expose
	@SerializedName("sourceIdentifier")
	private String sourceIdentifier;
	@Expose
	@SerializedName("sourceFileName")
	private String sourceFileName;
	@Expose
	@SerializedName("glAccountCode")
	private String glAccountCode;
	@Expose
	@SerializedName("division")
	private String division;
	@Expose
	@SerializedName("subDivision")
	private String subDivision;
	@Expose
	@SerializedName("profitCentre1")
	private String profitCentre1;
	@Expose
	@SerializedName("profitCentre2")
	private String profitCentre2;
	@Expose
	@SerializedName("plantCode")
	private String plantCode;
	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;
	@Expose
	@SerializedName("recipientGstin")
	private String recipientGstin;
	@Expose
	@SerializedName("documentType")
	private String documentType;
	@Expose
	@SerializedName("supplyType")
	private String supplyType;
	@Expose
	@SerializedName("docNum")
	private String docNum;
	@Expose
	@SerializedName("docDate")
	private LocalDate docDate;
	@Expose
	@SerializedName("OrigDocNum")
	private String OrigDocNum;
	@Expose
	@SerializedName("OrigDocDate")
	private LocalDate OrigDocDate;
	@Expose
	@SerializedName("crdrPreGst")
	private String crdrPreGst;
	@Expose
	@SerializedName("lineNumber")
	private Integer lineNumber;
	@Expose
	@SerializedName("sgstin")
	private String sgstin;
	@Expose
	@SerializedName("origSgstin")
	private String origSgstin;
	@Expose
	@SerializedName("supplierName")
	private String supplierName;
	@Expose
	@SerializedName("supplierCode")
	private String supplierCode;
	@Expose
	@SerializedName("pos")
	private String pos;
	@Expose
	@SerializedName("portCode")
	private String portCode;
	@Expose
	@SerializedName("billOfEntry")
	private String billOfEntry;
	@Expose
	@SerializedName("billOfEntryDate")
	private LocalDate billOfEntryDate;
	@Expose
	@SerializedName("cifValue")
	private String cifValue;
	@Expose
	@SerializedName("customDuty")
	private String customDuty;
	@Expose
	@SerializedName("hsnOrSac")
	private String hsnOrSac;
	@Expose
	@SerializedName("itemCode")
	private String itemCode;
	@Expose
	@SerializedName("itemDescription")
	private String itemDescription;
	@Expose
	@SerializedName("categoryOfItem")
	private String categoryOfItem;
	@Expose
	@SerializedName("UnitOfMeasurement")
	private String uom;
	@Expose
	@SerializedName("quantity")
	private Integer quantity;
	@Expose
	@SerializedName("taxableValue")
	private BigDecimal taxableValue;
	@Expose
	@SerializedName("integratedTaxRate")
	private BigDecimal integratedTaxRate;
	@Expose
	@SerializedName("integratedTaxAmount")
	private BigDecimal integratedTaxAmount;
	@Expose
	@SerializedName("centralTaxRate")
	private BigDecimal centralTaxRate;
	@Expose
	@SerializedName("centralTaxAmount")
	private BigDecimal centralTaxAmount;
	@Expose
	@SerializedName("stateUTTaxRate")
	private BigDecimal stateUTTaxRate;
	@Expose
	@SerializedName("stateUTTaxAmount")
	private BigDecimal stateUTTaxAmount;
	@Expose
	@SerializedName("cessRateAdvalorem")
	private BigDecimal cessRateAdvalorem;
	@Expose
	@SerializedName("cessAmountAdvalorem")
	private BigDecimal cessAmountAdvalorem;
	@Expose
	@SerializedName("cessRateSpecific")
	private BigDecimal cessRateSpecific;
	@Expose
	@SerializedName("cessAmountSpecific")
	private BigDecimal cessAmountSpecific;
	@Expose
	@SerializedName("invoiceValue")
	private BigDecimal invoiceValue;
	@Expose
	@SerializedName("reverseChargeFlag")
	private Boolean reverseChargeFlag;
	@Expose
	@SerializedName("eligibilityIndicator")
	private String eligibilityIndicator;
	@Expose
	@SerializedName("commonSupplyIndicator")
	private String commonSupplyIndicator;
	@Expose
	@SerializedName("availableIgst")
	private BigDecimal availableIgst;
	@Expose
	@SerializedName("availableCgst")
	private BigDecimal availableCgst;
	@Expose
	@SerializedName("availableSgst")
	private BigDecimal availableSgst;
	@Expose
	@SerializedName("availableCess")
	private BigDecimal availableCess;
	@Expose
	@SerializedName("ITCReversalIdentifier")
	private String ITCReversalIdentifier;
	@Expose
	@SerializedName("reasonForCrDbNote")
	private String reasonForCrDbNote;
	@Expose
	@SerializedName("purchasevoucherNum")
	private String purchasevoucherNum;
	@Expose
	@SerializedName("purchaseVoucherDate")
	private LocalDate purchaseVoucherDate;
	@Expose
	@SerializedName("paymentVoucherNumber")
	private String paymentVoucherNum;
	@Expose
	@SerializedName("paymentDate")
	private LocalDate paymentDate;
	@Expose
	@SerializedName("contractNumber")
	private String contractNumber;
	@Expose
	@SerializedName("contractDate")
	private LocalDate contractDate;
	@Expose
	@SerializedName("contractValue")
	private String contractValue;
	@Expose
	@SerializedName("userDefinedField1")
	private String userDefinedField1;
	@Expose
	@SerializedName("userDefinedField2")
	private String userDefinedField2;
	@Expose
	@SerializedName("userDefinedField3")
	private String userDefinedField3;
	/**
	 * @return the view
	 */
	public String getView() {
		return view;
	}
	/**
	 * @param view the view to set
	 */
	public void setView(String view) {
		this.view = view;
	}
	/**
	 * @return the dataCategory
	 */
	public String getDataCategory() {
		return dataCategory;
	}
	/**
	 * @param dataCategory the dataCategory to set
	 */
	public void setDataCategory(String dataCategory) {
		this.dataCategory = dataCategory;
	}
	/**
	 * @return the tableNo
	 */
	public String getTableNo() {
		return tableNo;
	}
	/**
	 * @param tableNo the tableNo to set
	 */
	public void setTableNo(String tableNo) {
		this.tableNo = tableNo;
	}
	/**
	 * @return the gstnStatus
	 */
	public String getGstnStatus() {
		return gstnStatus;
	}
	/**
	 * @param gstnStatus the gstnStatus to set
	 */
	public void setGstnStatus(String gstnStatus) {
		this.gstnStatus = gstnStatus;
	}
	/**
	 * @return the sourceIdentifier
	 */
	public String getSourceIdentifier() {
		return sourceIdentifier;
	}
	/**
	 * @param sourceIdentifier the sourceIdentifier to set
	 */
	public void setSourceIdentifier(String sourceIdentifier) {
		this.sourceIdentifier = sourceIdentifier;
	}
	/**
	 * @return the sourceFileName
	 */
	public String getSourceFileName() {
		return sourceFileName;
	}
	/**
	 * @param sourceFileName the sourceFileName to set
	 */
	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}
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
	 * @return the division
	 */
	public String getDivision() {
		return division;
	}
	/**
	 * @param division the division to set
	 */
	public void setDivision(String division) {
		this.division = division;
	}
	/**
	 * @return the subDivision
	 */
	public String getSubDivision() {
		return subDivision;
	}
	/**
	 * @param subDivision the subDivision to set
	 */
	public void setSubDivision(String subDivision) {
		this.subDivision = subDivision;
	}
	/**
	 * @return the profitCentre1
	 */
	public String getProfitCentre1() {
		return profitCentre1;
	}
	/**
	 * @param profitCentre1 the profitCentre1 to set
	 */
	public void setProfitCentre1(String profitCentre1) {
		this.profitCentre1 = profitCentre1;
	}
	/**
	 * @return the profitCentre2
	 */
	public String getProfitCentre2() {
		return profitCentre2;
	}
	/**
	 * @param profitCentre2 the profitCentre2 to set
	 */
	public void setProfitCentre2(String profitCentre2) {
		this.profitCentre2 = profitCentre2;
	}
	/**
	 * @return the plantCode
	 */
	public String getPlantCode() {
		return plantCode;
	}
	/**
	 * @param plantCode the plantCode to set
	 */
	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}
	/**
	 * @return the returnPeriod
	 */
	public String getReturnPeriod() {
		return returnPeriod;
	}
	/**
	 * @param returnPeriod the returnPeriod to set
	 */
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}
	/**
	 * @return the recipientGstin
	 */
	public String getRecipientGstin() {
		return recipientGstin;
	}
	/**
	 * @param recipientGstin the recipientGstin to set
	 */
	public void setRecipientGstin(String recipientGstin) {
		this.recipientGstin = recipientGstin;
	}
	/**
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}
	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
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
	 * @return the docNum
	 */
	public String getDocNum() {
		return docNum;
	}
	/**
	 * @param docNum the docNum to set
	 */
	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}
	/**
	 * @return the docDate
	 */
	public LocalDate getDocDate() {
		return docDate;
	}
	/**
	 * @param docDate the docDate to set
	 */
	public void setDocDate(LocalDate docDate) {
		this.docDate = docDate;
	}
	/**
	 * @return the origDocNum
	 */
	public String getOrigDocNum() {
		return OrigDocNum;
	}
	/**
	 * @param origDocNum the origDocNum to set
	 */
	public void setOrigDocNum(String origDocNum) {
		OrigDocNum = origDocNum;
	}
	/**
	 * @return the origDocDate
	 */
	public LocalDate getOrigDocDate() {
		return OrigDocDate;
	}
	/**
	 * @param origDocDate the origDocDate to set
	 */
	public void setOrigDocDate(LocalDate origDocDate) {
		OrigDocDate = origDocDate;
	}
	/**
	 * @return the crdrPreGst
	 */
	public String getCrdrPreGst() {
		return crdrPreGst;
	}
	/**
	 * @param crdrPreGst the crdrPreGst to set
	 */
	public void setCrdrPreGst(String crdrPreGst) {
		this.crdrPreGst = crdrPreGst;
	}
	/**
	 * @return the lineNumber
	 */
	public Integer getLineNumber() {
		return lineNumber;
	}
	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}
	/**
	 * @return the sgstin
	 */
	public String getSgstin() {
		return sgstin;
	}
	/**
	 * @param sgstin the sgstin to set
	 */
	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}
	/**
	 * @return the origSgstin
	 */
	public String getOrigSgstin() {
		return origSgstin;
	}
	/**
	 * @param origSgstin the origSgstin to set
	 */
	public void setOrigSgstin(String origSgstin) {
		this.origSgstin = origSgstin;
	}
	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}
	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	/**
	 * @return the supplierCode
	 */
	public String getSupplierCode() {
		return supplierCode;
	}
	/**
	 * @param supplierCode the supplierCode to set
	 */
	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}
	/**
	 * @return the pos
	 */
	public String getPos() {
		return pos;
	}
	/**
	 * @param pos the pos to set
	 */
	public void setPos(String pos) {
		this.pos = pos;
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
	public String getBillOfEntry() {
		return billOfEntry;
	}
	/**
	 * @param billOfEntry the billOfEntry to set
	 */
	public void setBillOfEntry(String billOfEntry) {
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
	public String getCifValue() {
		return cifValue;
	}
	/**
	 * @param cifValue the cifValue to set
	 */
	public void setCifValue(String cifValue) {
		this.cifValue = cifValue;
	}
	/**
	 * @return the customDuty
	 */
	public String getCustomDuty() {
		return customDuty;
	}
	/**
	 * @param customDuty the customDuty to set
	 */
	public void setCustomDuty(String customDuty) {
		this.customDuty = customDuty;
	}
	/**
	 * @return the hsnOrSac
	 */
	public String getHsnOrSac() {
		return hsnOrSac;
	}
	/**
	 * @param hsnOrSac the hsnOrSac to set
	 */
	public void setHsnOrSac(String hsnOrSac) {
		this.hsnOrSac = hsnOrSac;
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
	 * @return the itemDescription
	 */
	public String getItemDescription() {
		return itemDescription;
	}
	/**
	 * @param itemDescription the itemDescription to set
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	/**
	 * @return the categoryOfItem
	 */
	public String getCategoryOfItem() {
		return categoryOfItem;
	}
	/**
	 * @param categoryOfItem the categoryOfItem to set
	 */
	public void setCategoryOfItem(String categoryOfItem) {
		this.categoryOfItem = categoryOfItem;
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
	public Integer getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
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
	 * @return the integratedTaxRate
	 */
	public BigDecimal getIntegratedTaxRate() {
		return integratedTaxRate;
	}
	/**
	 * @param integratedTaxRate the integratedTaxRate to set
	 */
	public void setIntegratedTaxRate(BigDecimal integratedTaxRate) {
		this.integratedTaxRate = integratedTaxRate;
	}
	/**
	 * @return the integratedTaxAmount
	 */
	public BigDecimal getIntegratedTaxAmount() {
		return integratedTaxAmount;
	}
	/**
	 * @param integratedTaxAmount the integratedTaxAmount to set
	 */
	public void setIntegratedTaxAmount(BigDecimal integratedTaxAmount) {
		this.integratedTaxAmount = integratedTaxAmount;
	}
	/**
	 * @return the centralTaxRate
	 */
	public BigDecimal getCentralTaxRate() {
		return centralTaxRate;
	}
	/**
	 * @param centralTaxRate the centralTaxRate to set
	 */
	public void setCentralTaxRate(BigDecimal centralTaxRate) {
		this.centralTaxRate = centralTaxRate;
	}
	/**
	 * @return the centralTaxAmount
	 */
	public BigDecimal getCentralTaxAmount() {
		return centralTaxAmount;
	}
	/**
	 * @param centralTaxAmount the centralTaxAmount to set
	 */
	public void setCentralTaxAmount(BigDecimal centralTaxAmount) {
		this.centralTaxAmount = centralTaxAmount;
	}
	/**
	 * @return the stateUTTaxRate
	 */
	public BigDecimal getStateUTTaxRate() {
		return stateUTTaxRate;
	}
	/**
	 * @param stateUTTaxRate the stateUTTaxRate to set
	 */
	public void setStateUTTaxRate(BigDecimal stateUTTaxRate) {
		this.stateUTTaxRate = stateUTTaxRate;
	}
	/**
	 * @return the stateUTTaxAmount
	 */
	public BigDecimal getStateUTTaxAmount() {
		return stateUTTaxAmount;
	}
	/**
	 * @param stateUTTaxAmount the stateUTTaxAmount to set
	 */
	public void setStateUTTaxAmount(BigDecimal stateUTTaxAmount) {
		this.stateUTTaxAmount = stateUTTaxAmount;
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
	 * @return the cessAmountAdvalorem
	 */
	public BigDecimal getCessAmountAdvalorem() {
		return cessAmountAdvalorem;
	}
	/**
	 * @param cessAmountAdvalorem the cessAmountAdvalorem to set
	 */
	public void setCessAmountAdvalorem(BigDecimal cessAmountAdvalorem) {
		this.cessAmountAdvalorem = cessAmountAdvalorem;
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
	 * @return the cessAmountSpecific
	 */
	public BigDecimal getCessAmountSpecific() {
		return cessAmountSpecific;
	}
	/**
	 * @param cessAmountSpecific the cessAmountSpecific to set
	 */
	public void setCessAmountSpecific(BigDecimal cessAmountSpecific) {
		this.cessAmountSpecific = cessAmountSpecific;
	}
	/**
	 * @return the invoiceValue
	 */
	public BigDecimal getInvoiceValue() {
		return invoiceValue;
	}
	/**
	 * @param invoiceValue the invoiceValue to set
	 */
	public void setInvoiceValue(BigDecimal invoiceValue) {
		this.invoiceValue = invoiceValue;
	}
	/**
	 * @return the reverseChargeFlag
	 */
	public Boolean getReverseChargeFlag() {
		return reverseChargeFlag;
	}
	/**
	 * @param reverseChargeFlag the reverseChargeFlag to set
	 */
	public void setReverseChargeFlag(Boolean reverseChargeFlag) {
		this.reverseChargeFlag = reverseChargeFlag;
	}
	/**
	 * @return the eligibilityIndicator
	 */
	public String getEligibilityIndicator() {
		return eligibilityIndicator;
	}
	/**
	 * @param eligibilityIndicator the eligibilityIndicator to set
	 */
	public void setEligibilityIndicator(String eligibilityIndicator) {
		this.eligibilityIndicator = eligibilityIndicator;
	}
	/**
	 * @return the commonSupplyIndicator
	 */
	public String getCommonSupplyIndicator() {
		return commonSupplyIndicator;
	}
	/**
	 * @param commonSupplyIndicator the commonSupplyIndicator to set
	 */
	public void setCommonSupplyIndicator(String commonSupplyIndicator) {
		this.commonSupplyIndicator = commonSupplyIndicator;
	}
	/**
	 * @return the availableCess
	 */
	public BigDecimal getAvailableCess() {
		return availableCess;
	}
	/**
	 * @param d the availableCess to set
	 */
	public void setAvailableCess(BigDecimal d) {
		this.availableCess = d;
	}
	/**
	 * @return the iTCReversalIdentifier
	 */
	public String getITCReversalIdentifier() {
		return ITCReversalIdentifier;
	}
	/**
	 * @param iTCReversalIdentifier the iTCReversalIdentifier to set
	 */
	public void setITCReversalIdentifier(String iTCReversalIdentifier) {
		ITCReversalIdentifier = iTCReversalIdentifier;
	}
	/**
	 * @return the reasonForCrDbNote
	 */
	public String getReasonForCrDbNote() {
		return reasonForCrDbNote;
	}
	/**
	 * @param reasonForCrDbNote the reasonForCrDbNote to set
	 */
	public void setReasonForCrDbNote(String reasonForCrDbNote) {
		this.reasonForCrDbNote = reasonForCrDbNote;
	}
	/**
	 * @return the purchasevoucherNum
	 */
	public String getPurchasevoucherNum() {
		return purchasevoucherNum;
	}
	/**
	 * @param purchasevoucherNum the purchasevoucherNum to set
	 */
	public void setPurchasevoucherNum(String purchasevoucherNum) {
		this.purchasevoucherNum = purchasevoucherNum;
	}
	/**
	 * @return the purchaseVoucherDate
	 */
	public LocalDate getPurchaseVoucherDate() {
		return purchaseVoucherDate;
	}
	/**
	 * @param purchaseVoucherDate the purchaseVoucherDate to set
	 */
	public void setPurchaseVoucherDate(LocalDate purchaseVoucherDate) {
		this.purchaseVoucherDate = purchaseVoucherDate;
	}
	/**
	 * @return the paymentVoucherNum
	 */
	public String getPaymentVoucherNum() {
		return paymentVoucherNum;
	}
	/**
	 * @param paymentVoucherNum the paymentVoucherNum to set
	 */
	public void setPaymentVoucherNum(String paymentVoucherNum) {
		this.paymentVoucherNum = paymentVoucherNum;
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
	 * @return the contractNumber
	 */
	public String getContractNumber() {
		return contractNumber;
	}
	/**
	 * @param contractNumber the contractNumber to set
	 */
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
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
	 * @return the contractValue
	 */
	public String getContractValue() {
		return contractValue;
	}
	/**
	 * @param contractValue the contractValue to set
	 */
	public void setContractValue(String contractValue) {
		this.contractValue = contractValue;
	}
	/**
	 * @return the userDefinedField1
	 */
	public String getUserDefinedField1() {
		return userDefinedField1;
	}
	/**
	 * @param userDefinedField1 the userDefinedField1 to set
	 */
	public void setUserDefinedField1(String userDefinedField1) {
		this.userDefinedField1 = userDefinedField1;
	}
	/**
	 * @return the userDefinedField2
	 */
	public String getUserDefinedField2() {
		return userDefinedField2;
	}
	/**
	 * @param userDefinedField2 the userDefinedField2 to set
	 */
	public void setUserDefinedField2(String userDefinedField2) {
		this.userDefinedField2 = userDefinedField2;
	}
	/**
	 * @return the userDefinedField3
	 */
	public String getUserDefinedField3() {
		return userDefinedField3;
	}
	/**
	 * @param userDefinedField3 the userDefinedField3 to set
	 */
	public void setUserDefinedField3(String userDefinedField3) {
		this.userDefinedField3 = userDefinedField3;
	}
	/**
	 * @return the availableIgst
	 */
	public BigDecimal getAvailableIgst() {
		return availableIgst;
	}
	/**
	 * @param availableIgst the availableIgst to set
	 */
	public void setAvailableIgst(BigDecimal availableIgst) {
		this.availableIgst = availableIgst;
	}
	/**
	 * @return the availableCgst
	 */
	public BigDecimal getAvailableCgst() {
		return availableCgst;
	}
	/**
	 * @param availableCgst the availableCgst to set
	 */
	public void setAvailableCgst(BigDecimal availableCgst) {
		this.availableCgst = availableCgst;
	}
	/**
	 * @return the availableSgst
	 */
	public BigDecimal getAvailableSgst() {
		return availableSgst;
	}
	/**
	 * @param availableSgst the availableSgst to set
	 */
	public void setAvailableSgst(BigDecimal availableSgst) {
		this.availableSgst = availableSgst;
	}
	

}
