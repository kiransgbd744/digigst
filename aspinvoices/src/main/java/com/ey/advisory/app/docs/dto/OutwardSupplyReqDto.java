/*package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;

import org.springframework.stereotype.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Component
public class OutwardSupplyReqDto {

	@SerializedName("id")
	private Long id;

	@SerializedName("sourceIdentifier")
	private String sourceIdentifier;

	@SerializedName("sourceFileName")
	private String sourceFileName;

	@SerializedName("glAccountCode")
	private String glAccountCode;

	@SerializedName("division")
	private String division;

	@SerializedName("subDivision")
	private String subDivision;

	@SerializedName("profitCentre1")
	private String profitCentre1;

	@SerializedName("profitCentre2")
	private String profitCentre2;

	@SerializedName("plantCode")
	private String plantCode;

	@SerializedName("returnPeriod")
	private String returnPeriod;

	@SerializedName("supplierGSTIN")
	private String supplierGSTIN;

	@SerializedName("documentType")
	private String documentType;

	@SerializedName("supplyType")
	private String supplyType;

	@SerializedName("documentNumber")
	private String documentNumber;

	@SerializedName("documentDate")
	private String documentDate;

	@SerializedName("originalDocNumber")
	private String originalDocNumber;

	@SerializedName("originalDocDate")
	private String originalDocDate;

	@SerializedName("crdrPreGST")
	private String crdrPreGST;

	@SerializedName("lineNumber")
	private String lineNumber;

	@SerializedName("customerGSTIN")
	private String customerGSTIN;

	@SerializedName("uinOrComposition")
	private String uinOrComposition;

	@SerializedName("originalCustomerGSTIN")
	private String originalCustomerGSTIN;

	@SerializedName("customerName")
	private String customerName;

	@SerializedName("customerCode")
	private String customerCode;

	@SerializedName("billToState")
	private String billToState;

	@SerializedName("shipToState")
	private String shipToState;

	@SerializedName("pos")
	private String pos;

	@SerializedName("portCode")
	private String portCode;

	@SerializedName("shippingBillNumber")
	private String shippingBillNumber;

	@SerializedName("shippingBillDate")
	private LocalDateTime shippingBillDate;

	@SerializedName("fob")
	private BigDecimal fob;

	@SerializedName("exportDuty")
	private BigDecimal exportDuty;

	@SerializedName("hsnOrSAC")
	private String hsnOrSAC;

	@SerializedName("productCode")
	private String productCode;

	@SerializedName("productDescription")
	private String productDescription;

	@SerializedName("categoryOfProduct")
	private String categoryOfProduct;

	@SerializedName("unitOfMeasurement")
	private String unitOfMeasurement;

	@SerializedName("quantity")
	private BigDecimal quantity;

	@SerializedName("taxableValue")
	private BigDecimal taxableValue;

//	@SerializedName("integratedTaxRate")
//	private BigDecimal integratedTaxRate;

	@SerializedName("integratedTaxAmount")
	private BigDecimal integratedTaxAmount;

//	@SerializedName("centralTaxRate")
//	private BigDecimal centralTaxRate;

	@SerializedName("centralTaxAmount")
	private BigDecimal centralTaxAmount;

//	@SerializedName("stateUtTaxRate")
//	private BigDecimal stateUtTaxRate;

	@SerializedName("stateUtTaxAmount")
	private BigDecimal stateUtTaxAmount;

	@SerializedName("cessRateAdvalorem")
	private BigDecimal cessRateAdvalorem;

	@SerializedName("cessAmountAdvalorem")
	private BigDecimal cessAmountAdvalorem;

	@SerializedName("cessRateSpecific")
	private BigDecimal cessRateSpecific;

	@SerializedName("cessAmountSpecific")
	private BigDecimal cessAmountSpecific;

	@SerializedName("invoiceValue")
	private BigDecimal invoiceValue;

	@SerializedName("reverseChargeFlag")
	private String reverseChargeFlag;

	@SerializedName("tcsFlag")
	private Boolean tcsFlag;

	@SerializedName("eComGSTIN")
	private String eComGSTIN;

	@SerializedName("itcFlag")
	private Boolean itcFlag;

	@SerializedName("reasonForCreditDebitNote")
	private String reasonForCreditDebitNote;

	@SerializedName("accountingVoucherNumber")
	private String accountingVoucherNumber;

	@SerializedName("accountingVoucherDate")
	private String accountingVoucherDate;

	@SerializedName("userdefinedfield1")
	private String userdefinedfield1;

	@SerializedName("userdefinedfield2")
	private String userdefinedfield2;

	@SerializedName("userdefinedfield3")
	private String userdefinedfield3;
	
	@SerializedName("status")
	private String status;
	
	@Column(name = "TAXRATE", precision = 15, scale = 3)
	private BigDecimal taxRate;

	@SerializedName("totalAmt")
	private BigDecimal totalAmt;


	*//**
	 * @return the id
	 *//*
	public Long getId() {
		return id;
	}

	*//**
	 * @param id
	 *            the id to set
	 *//*
	public void setId(Long id) {
		this.id = id;
	}

	*//**
	 * @return the sourceIdentifier
	 *//*
	public String getSourceIdentifier() {
		return sourceIdentifier;
	}

	*//**
	 * @param sourceIdentifier
	 *            the sourceIdentifier to set
	 *//*
	public void setSourceIdentifier(String sourceIdentifier) {
		this.sourceIdentifier = sourceIdentifier;
	}

	*//**
	 * @return the sourceFileName
	 *//*
	public String getSourceFileName() {
		return sourceFileName;
	}

	*//**
	 * @param sourceFileName
	 *            the sourceFileName to set
	 *//*
	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	*//**
	 * @return the glAccountCode
	 *//*
	public String getGlAccountCode() {
		return glAccountCode;
	}

	*//**
	 * @param glAccountCode
	 *            the glAccountCode to set
	 *//*
	public void setGlAccountCode(String glAccountCode) {
		this.glAccountCode = glAccountCode;
	}

	*//**
	 * @return the division
	 *//*
	public String getDivision() {
		return division;
	}

	*//**
	 * @param division
	 *            the division to set
	 *//*
	public void setDivision(String division) {
		this.division = division;
	}

	*//**
	 * @return the subDivision
	 *//*
	public String getSubDivision() {
		return subDivision;
	}

	*//**
	 * @param subDivision
	 *            the subDivision to set
	 *//*
	public void setSubDivision(String subDivision) {
		this.subDivision = subDivision;
	}

	*//**
	 * @return the profitCentre1
	 *//*
	public String getProfitCentre1() {
		return profitCentre1;
	}

	*//**
	 * @param profitCentre1
	 *            the profitCentre1 to set
	 *//*
	public void setProfitCentre1(String profitCentre1) {
		this.profitCentre1 = profitCentre1;
	}

	*//**
	 * @return the profitCentre2
	 *//*
	public String getProfitCentre2() {
		return profitCentre2;
	}

	*//**
	 * @param profitCentre2
	 *            the profitCentre2 to set
	 *//*
	public void setProfitCentre2(String profitCentre2) {
		this.profitCentre2 = profitCentre2;
	}

	*//**
	 * @return the plantCode
	 *//*
	public String getPlantCode() {
		return plantCode;
	}

	*//**
	 * @param plantCode
	 *            the plantCode to set
	 *//*
	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}

	*//**
	 * @return the returnPeriod
	 *//*
	public String getReturnPeriod() {
		return returnPeriod;
	}

	*//**
	 * @param returnPeriod
	 *            the returnPeriod to set
	 *//*
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	*//**
	 * @return the supplierGSTIN
	 *//*
	public String getSupplierGSTIN() {
		return supplierGSTIN;
	}

	*//**
	 * @param supplierGSTIN
	 *            the supplierGSTIN to set
	 *//*
	public void setSupplierGSTIN(String supplierGSTIN) {
		this.supplierGSTIN = supplierGSTIN;
	}

	*//**
	 * @return the documentType
	 *//*
	public String getDocumentType() {
		return documentType;
	}

	*//**
	 * @param documentType
	 *            the documentType to set
	 *//*
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	*//**
	 * @return the supplyType
	 *//*
	public String getSupplyType() {
		return supplyType;
	}

	*//**
	 * @param supplyType
	 *            the supplyType to set
	 *//*
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	*//**
	 * @return the documentNumber
	 *//*
	public String getDocumentNumber() {
		return documentNumber;
	}

	*//**
	 * @param documentNumber
	 *            the documentNumber to set
	 *//*
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	*//**
	 * @return the documentDate
	 *//*
	public String getDocumentDate() {
		return documentDate;
	}

	*//**
	 * @param documentDate
	 *            the documentDate to set
	 *//*
	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	*//**
	 * @return the originalDocNumber
	 *//*
	public String getOriginalDocNumber() {
		return originalDocNumber;
	}

	*//**
	 * @param originalDocNumber
	 *            the originalDocNumber to set
	 *//*
	public void setOriginalDocNumber(String originalDocNumber) {
		this.originalDocNumber = originalDocNumber;
	}

	*//**
	 * @return the originalDocDate
	 *//*
	public String getOriginalDocDate() {
		return originalDocDate;
	}

	*//**
	 * @param originalDocDate
	 *            the originalDocDate to set
	 *//*
	public void setOriginalDocDate(String originalDocDate) {
		this.originalDocDate = originalDocDate;
	}

	*//**
	 * @return the crdrPreGST
	 *//*
	public String getCrdrPreGST() {
		return crdrPreGST;
	}

	*//**
	 * @param crdrPreGST
	 *            the crdrPreGST to set
	 *//*
	public void setCrdrPreGST(String crdrPreGST) {
		this.crdrPreGST = crdrPreGST;
	}

	*//**
	 * @return the lineNumber
	 *//*
	public String getLineNumber() {
		return lineNumber;
	}

	*//**
	 * @param lineNumber
	 *            the lineNumber to set
	 *//*
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	*//**
	 * @return the customerGSTIN
	 *//*
	public String getCustomerGSTIN() {
		return customerGSTIN;
	}

	*//**
	 * @param customerGSTIN
	 *            the customerGSTIN to set
	 *//*
	public void setCustomerGSTIN(String customerGSTIN) {
		this.customerGSTIN = customerGSTIN;
	}

	*//**
	 * @return the uinOrComposition
	 *//*
	public String getUinOrComposition() {
		return uinOrComposition;
	}

	*//**
	 * @param uinOrComposition
	 *            the uinOrComposition to set
	 *//*
	public void setUinOrComposition(String uinOrComposition) {
		this.uinOrComposition = uinOrComposition;
	}

	*//**
	 * @return the originalCustomerGSTIN
	 *//*
	public String getOriginalCustomerGSTIN() {
		return originalCustomerGSTIN;
	}

	*//**
	 * @param originalCustomerGSTIN
	 *            the originalCustomerGSTIN to set
	 *//*
	public void setOriginalCustomerGSTIN(String originalCustomerGSTIN) {
		this.originalCustomerGSTIN = originalCustomerGSTIN;
	}

	*//**
	 * @return the customerName
	 *//*
	public String getCustomerName() {
		return customerName;
	}

	*//**
	 * @param customerName
	 *            the customerName to set
	 *//*
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	*//**
	 * @return the customerCode
	 *//*
	public String getCustomerCode() {
		return customerCode;
	}

	*//**
	 * @param customerCode
	 *            the customerCode to set
	 *//*
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	*//**
	 * @return the billToState
	 *//*
	public String getBillToState() {
		return billToState;
	}

	*//**
	 * @param billToState
	 *            the billToState to set
	 *//*
	public void setBillToState(String billToState) {
		this.billToState = billToState;
	}

	*//**
	 * @return the shipToState
	 *//*
	public String getShipToState() {
		return shipToState;
	}

	*//**
	 * @param shipToState
	 *            the shipToState to set
	 *//*
	public void setShipToState(String shipToState) {
		this.shipToState = shipToState;
	}

	*//**
	 * @return the pos
	 *//*
	public String getPos() {
		return pos;
	}

	*//**
	 * @param pos
	 *            the pos to set
	 *//*
	public void setPos(String pos) {
		this.pos = pos;
	}

	*//**
	 * @return the portCode
	 *//*
	public String getPortCode() {
		return portCode;
	}

	*//**
	 * @param portCode
	 *            the portCode to set
	 *//*
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	*//**
	 * @return the shippingBillNumber
	 *//*
	public String getShippingBillNumber() {
		return shippingBillNumber;
	}

	*//**
	 * @param shippingBillNumber
	 *            the shippingBillNumber to set
	 *//*
	public void setShippingBillNumber(String shippingBillNumber) {
		this.shippingBillNumber = shippingBillNumber;
	}

	*//**
	 * @return the shippingBillDate
	 *//*
	public LocalDateTime getShippingBillDate() {
		return shippingBillDate;
	}

	*//**
	 * @param shippingBillDate
	 *            the shippingBillDate to set
	 *//*
	public void setShippingBillDate(LocalDateTime shippingBillDate) {
		this.shippingBillDate = shippingBillDate;
	}

	*//**
	 * @return the fob
	 *//*
	public BigDecimal getFob() {
		return fob;
	}

	*//**
	 * @param fob
	 *            the fob to set
	 *//*
	public void setFob(BigDecimal fob) {
		this.fob = fob;
	}

	*//**
	 * @return the exportDuty
	 *//*
	public BigDecimal getExportDuty() {
		return exportDuty;
	}

	*//**
	 * @param exportDuty
	 *            the exportDuty to set
	 *//*
	public void setExportDuty(BigDecimal exportDuty) {
		this.exportDuty = exportDuty;
	}

	*//**
	 * @return the hsnOrSAC
	 *//*
	public String getHsnOrSAC() {
		return hsnOrSAC;
	}

	*//**
	 * @param hsnOrSAC
	 *            the hsnOrSAC to set
	 *//*
	public void setHsnOrSAC(String hsnOrSAC) {
		this.hsnOrSAC = hsnOrSAC;
	}

	*//**
	 * @return the productCode
	 *//*
	public String getProductCode() {
		return productCode;
	}

	*//**
	 * @param productCode
	 *            the productCode to set
	 *//*
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	*//**
	 * @return the productDescription
	 *//*
	public String getProductDescription() {
		return productDescription;
	}

	*//**
	 * @param productDescription
	 *            the productDescription to set
	 *//*
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	*//**
	 * @return the categoryOfProduct
	 *//*
	public String getCategoryOfProduct() {
		return categoryOfProduct;
	}

	*//**
	 * @param categoryOfProduct
	 *            the categoryOfProduct to set
	 *//*
	public void setCategoryOfProduct(String categoryOfProduct) {
		this.categoryOfProduct = categoryOfProduct;
	}

	*//**
	 * @return the unitOfMeasurement
	 *//*
	public String getUnitOfMeasurement() {
		return unitOfMeasurement;
	}

	*//**
	 * @param unitOfMeasurement
	 *            the unitOfMeasurement to set
	 *//*
	public void setUnitOfMeasurement(String unitOfMeasurement) {
		this.unitOfMeasurement = unitOfMeasurement;
	}

	*//**
	 * @return the quantity
	 *//*
	public BigDecimal getQuantity() {
		return quantity;
	}

	*//**
	 * @param quantity
	 *            the quantity to set
	 *//*
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	*//**
	 * @return the taxableValue
	 *//*
	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	*//**
	 * @param taxableValue
	 *            the taxableValue to set
	 *//*
	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	*//**
	 * @return the integratedTaxRate
	 *//*
	public BigDecimal getIntegratedTaxRate() {
		return integratedTaxRate;
	}

	*//**
	 * @param integratedTaxRate
	 *            the integratedTaxRate to set
	 *//*
	public void setIntegratedTaxRate(BigDecimal integratedTaxRate) {
		this.integratedTaxRate = integratedTaxRate;
	}

	*//**
	 * @return the integratedTaxAmount
	 *//*
	public BigDecimal getIntegratedTaxAmount() {
		return integratedTaxAmount;
	}

	*//**
	 * @param integratedTaxAmount
	 *            the integratedTaxAmount to set
	 *//*
	public void setIntegratedTaxAmount(BigDecimal integratedTaxAmount) {
		this.integratedTaxAmount = integratedTaxAmount;
	}

	*//**
	 * @return the centralTaxRate
	 *//*
	public BigDecimal getCentralTaxRate() {
		return centralTaxRate;
	}

	*//**
	 * @param centralTaxRate
	 *            the centralTaxRate to set
	 *//*
	public void setCentralTaxRate(BigDecimal centralTaxRate) {
		this.centralTaxRate = centralTaxRate;
	}

	*//**
	 * @return the centralTaxAmount
	 *//*
	public BigDecimal getCentralTaxAmount() {
		return centralTaxAmount;
	}

	*//**
	 * @param centralTaxAmount
	 *            the centralTaxAmount to set
	 *//*
	public void setCentralTaxAmount(BigDecimal centralTaxAmount) {
		this.centralTaxAmount = centralTaxAmount;
	}

	*//**
	 * @return the stateUtTaxRate
	 *//*
	public BigDecimal getStateUtTaxRate() {
		return stateUtTaxRate;
	}

	*//**
	 * @param stateUtTaxRate
	 *            the stateUtTaxRate to set
	 *//*
	public void setStateUtTaxRate(BigDecimal stateUtTaxRate) {
		this.stateUtTaxRate = stateUtTaxRate;
	}

	*//**
	 * @return the stateUtTaxAmount
	 *//*
	public BigDecimal getStateUtTaxAmount() {
		return stateUtTaxAmount;
	}

	*//**
	 * @param stateUtTaxAmount
	 *            the stateUtTaxAmount to set
	 *//*
	public void setStateUtTaxAmount(BigDecimal stateUtTaxAmount) {
		this.stateUtTaxAmount = stateUtTaxAmount;
	}

	*//**
	 * @return the cessRateAdvalorem
	 *//*
	public BigDecimal getCessRateAdvalorem() {
		return cessRateAdvalorem;
	}

	*//**
	 * @param cessRateAdvalorem
	 *            the cessRateAdvalorem to set
	 *//*
	public void setCessRateAdvalorem(BigDecimal cessRateAdvalorem) {
		this.cessRateAdvalorem = cessRateAdvalorem;
	}

	*//**
	 * @return the cessAmountAdvalorem
	 *//*
	public BigDecimal getCessAmountAdvalorem() {
		return cessAmountAdvalorem;
	}

	*//**
	 * @param cessAmountAdvalorem
	 *            the cessAmountAdvalorem to set
	 *//*
	public void setCessAmountAdvalorem(BigDecimal cessAmountAdvalorem) {
		this.cessAmountAdvalorem = cessAmountAdvalorem;
	}

	*//**
	 * @return the cessRateSpecific
	 *//*
	public BigDecimal getCessRateSpecific() {
		return cessRateSpecific;
	}

	*//**
	 * @param cessRateSpecific
	 *            the cessRateSpecific to set
	 *//*
	public void setCessRateSpecific(BigDecimal cessRateSpecific) {
		this.cessRateSpecific = cessRateSpecific;
	}

	*//**
	 * @return the cessAmountSpecific
	 *//*
	public BigDecimal getCessAmountSpecific() {
		return cessAmountSpecific;
	}

	*//**
	 * @param cessAmountSpecific
	 *            the cessAmountSpecific to set
	 *//*
	public void setCessAmountSpecific(BigDecimal cessAmountSpecific) {
		this.cessAmountSpecific = cessAmountSpecific;
	}

	*//**
	 * @return the invoiceValue
	 *//*
	public BigDecimal getInvoiceValue() {
		return invoiceValue;
	}

	*//**
	 * @param invoiceValue
	 *            the invoiceValue to set
	 *//*
	public void setInvoiceValue(BigDecimal invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	*//**
	 * @return the reverseChargeFlag
	 *//*
	public String getReverseChargeFlag() {
		return reverseChargeFlag;
	}

	*//**
	 * @param reverseChargeFlag
	 *            the reverseChargeFlag to set
	 *//*
	public void setReverseChargeFlag(String reverseChargeFlag) {
		this.reverseChargeFlag = reverseChargeFlag;
	}

	*//**
	 * @return the tcsFlag
	 *//*
	public Boolean getTcsFlag() {
		return tcsFlag;
	}

	*//**
	 * @param tcsFlag
	 *            the tcsFlag to set
	 *//*
	public void setTcsFlag(Boolean tcsFlag) {
		this.tcsFlag = tcsFlag;
	}

	*//**
	 * @return the eComGSTIN
	 *//*
	public String geteComGSTIN() {
		return eComGSTIN;
	}

	*//**
	 * @param eComGSTIN
	 *            the eComGSTIN to set
	 *//*
	public void seteComGSTIN(String eComGSTIN) {
		this.eComGSTIN = eComGSTIN;
	}

	*//**
	 * @return the itcFlag
	 *//*
	public Boolean getItcFlag() {
		return itcFlag;
	}

	*//**
	 * @param itcFlag
	 *            the itcFlag to set
	 *//*
	public void setItcFlag(Boolean itcFlag) {
		this.itcFlag = itcFlag;
	}

	*//**
	 * @return the reasonForCreditDebitNote
	 *//*
	public String getReasonForCreditDebitNote() {
		return reasonForCreditDebitNote;
	}

	*//**
	 * @param reasonForCreditDebitNote
	 *            the reasonForCreditDebitNote to set
	 *//*
	public void setReasonForCreditDebitNote(String reasonForCreditDebitNote) {
		this.reasonForCreditDebitNote = reasonForCreditDebitNote;
	}

	*//**
	 * @return the accountingVoucherNumber
	 *//*
	public String getAccountingVoucherNumber() {
		return accountingVoucherNumber;
	}

	*//**
	 * @param accountingVoucherNumber
	 *            the accountingVoucherNumber to set
	 *//*
	public void setAccountingVoucherNumber(String accountingVoucherNumber) {
		this.accountingVoucherNumber = accountingVoucherNumber;
	}

	*//**
	 * @return the accountingVoucherDate
	 *//*
	public String getAccountingVoucherDate() {
		return accountingVoucherDate;
	}

	*//**
	 * @param accountingVoucherDate
	 *            the accountingVoucherDate to set
	 *//*
	public void setAccountingVoucherDate(String accountingVoucherDate) {
		this.accountingVoucherDate = accountingVoucherDate;
	}

	*//**
	 * @return the userdefinedfield1
	 *//*
	public String getUserdefinedfield1() {
		return userdefinedfield1;
	}

	*//**
	 * @param userdefinedfield1
	 *            the userdefinedfield1 to set
	 *//*
	public void setUserdefinedfield1(String userdefinedfield1) {
		this.userdefinedfield1 = userdefinedfield1;
	}

	*//**
	 * @return the userdefinedfield2
	 *//*
	public String getUserdefinedfield2() {
		return userdefinedfield2;
	}

	*//**
	 * @param userdefinedfield2
	 *            the userdefinedfield2 to set
	 *//*
	public void setUserdefinedfield2(String userdefinedfield2) {
		this.userdefinedfield2 = userdefinedfield2;
	}

	*//**
	 * @return the userdefinedfield3
	 *//*
	public String getUserdefinedfield3() {
		return userdefinedfield3;
	}

	*//**
	 * @param userdefinedfield3
	 *            the userdefinedfield3 to set
	 *//*
	public void setUserdefinedfield3(String userdefinedfield3) {
		this.userdefinedfield3 = userdefinedfield3;
	}

	*//**
	 * @return the status
	 *//*
	public String getStatus() {
		return status;
	}

	*//**
	 * @param status the status to set
	 *//*
	public void setStatus(String status) {
		this.status = status;
	}
	
	*//**
	 * @return the taxRate
	 *//*
	public BigDecimal getTaxRate() {
		return taxRate;
	}

	*//**
	 * @param taxRate the taxRate to set
	 *//*
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	*//**
	 * @return the totalAmt
	 *//*
	public BigDecimal getTotalAmt() {
		return (this.getIntegratedTaxAmount().add(this.getStateUtTaxAmount().add(this.getCentralTaxAmount())));
	}

	*//**
	 * @param totalAmt the totalAmt to set
	 *//*
	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	
}

*/