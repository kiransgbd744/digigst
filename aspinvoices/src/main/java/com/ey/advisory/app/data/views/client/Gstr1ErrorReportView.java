package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;

/**
 * This class represents GSTN Save And Submit View 
 * @author Laxmi S
 *
 */
public class Gstr1ErrorReportView {
	
	@Column(name = "SUPPLIER_GSTIN")
	private String supplierGstin;
	
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;
	
	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedReturnPeriod;
	
	@Column(name = "RECEIVED_DATE")
	private LocalDate receivedDate;
	
	@Column(name = "SUPPLY_TYPE")
	private String supplyType;
	
	@Column(name = "DOC_TYPE")
	private String docType;
	
	@Column(name = "DOC_NUM")
	private String docNum;
	
	@Column(name = "DOC_DATE")
	private LocalDate docDate;
	
	@Column(name = "DOC_AMT")
	private BigDecimal docAmount;
	
	@Column(name = "ORIGINAL_DOC_NUM")
	private String orgDocNum;
	
	@Column(name = "ORIGINAL_DOC_DATE")
	private LocalDate orgDocDate;
	
	@Column(name = "ORIGINAL_CUST_GSTIN")
	private String orgCustGstin;
	
	@Column(name = "TAX_DOC_TYPE")
	private String taxDocType;
	
	@Column(name = "TABLE_SECTION")
	private String tableSection;
	
	@Column(name = "UINORCOMPOSITION")
	private String uinOrComposition;
	
	@Column(name = "CUST_GSTIN")
	private String customerGstin;
	
	@Column(name = "CUST_NAME")
	private String customerName;
	
	@Column(name = "BILL_TO_STATE")
	private String billToState;
	
	@Column(name = "SHIP_TO_STATE")
	private String shipToState;
	
	@Column(name = "SHIP_BILL_NUM")
	private String shipBillNum;
	
	@Column(name = "SHIP_BILL_DATE")
	private LocalDate shipBillDate;
	
	@Column(name = "SHIP_PORT_CODE")
	private String shipPortCode;
	
	@Column(name = "REVERSE_CHARGE")
	private String reverseCharge;
	
	@Column(name = "ECOM_CUST_GSTIN")
	private String ecomCustomerGstin;
	
	@Column(name = "ITC_FLAG")
	private String itcFlag;
	
	@Column(name = "EXPORT_DUTY")
	private BigDecimal exportDuty;
	
	@Column(name = "CUSTOMER_CODE")
	private String customerCode;
	
	@Column(name = "ACCOUNTING_VOUCHER_NUM")
	private String accountingVoucherNo;
	
	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	private LocalDate accountingVoucherDate;
	
	@Column(name = "GLACCOUNT_CODE")
	private String glaAccountCode;
	
	@Column(name = "FOB")
	private BigDecimal fob;
	
	@Column(name = "TCS_FLAG")
	private String tcsFlag;
	
	@Column(name = "CRDR_PRE_GST")
	private String crDrPreGst;
	
	@Column(name = "CRDR_REASON")
	private String crDrReason;
	
	@Column(name = "SOURCE_IDENTIFIER")
	private String sourceIdentifier;
	
	@Column(name = "SOURCE_FILENAME")
	private String sourceFileName;
	
	@Column(name = "DIVISION")
	private String division;
	
	@Column(name = "SUBDIVISION")
	private String subDivision;
	
	@Column(name = "PROFIT_CENTRE1")
	private String profitCenter1;
	
	@Column(name = "PROFIT_CENTRE2")
	private String profitCenter2;
	
	@Column(name = "PLANT_CODE")
	private String plantCode;
	
	@Column(name = "USERDEFINED_FIELD1")
	private String userDefinied1;
	
	@Column(name = "USERDEFINED_FIELD2")
	private String userDefinied2;
	
	@Column(name = "USERDEFINED_FIELD3")
	private String userDefinied3;
	
	@Column(name = "POS")
	private String pos;
	
	@Column(name = "GSTN_STATUS")
	private String gstnStatus;
	
	@Column(name = "ITM_HSNSAC")
	private String itemHsnSac;
	
	@Column(name = "ITM_DESCRIPTION")
	private String itemDesc;
	
	@Column(name = "ITM_TYPE")
	private String itemType;
	
	@Column(name = "ITM_UQC")
	private String itemUqc;
	
	@Column(name = "ITM_QTY")
	private BigDecimal itemQuantity;
	
	@Column(name = "PRODUCT_CODE")
	private String productCode;
	
	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue;
	
	@Column(name = "IGST_RATE")
	private BigDecimal igstRate;
	
	@Column(name = "IGST_AMT")
	private BigDecimal igstAmount;
	
	@Column(name = "CGST_RATE")
	private BigDecimal cgstRate;
	
	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmount;
	
	@Column(name = "SGST_RATE")
	private BigDecimal sgstRate;
	
	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmount;
	
	@Column(name = "CESS_RATE_SPECIFIC")
	private BigDecimal cessRateSpecific;
	
	@Column(name = "CESS_AMT_SPECIFIC")
	private BigDecimal cessAmountSpecific;
	
	@Column(name = "CESS_RATE_ADVALOREM")
	private BigDecimal cessRateAdv;
	
	@Column(name = "CESS_AMT_ADVALOREM")
	private BigDecimal cessAmountAdv;
	
	@Column(name = "DOC_HEADER_ID")
	private Long docHeaderId;
	
	@Column(name = "ITM_NO")
	private Integer itemNo;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;
	
	@Column(name = "ERROR_TYPE")
	private String errorType;
	
	@Column(name = "ERROR_DESCRIPTION")
	private String errorDesc;
	
	@Column(name = "INFO_CODE")
	private String infoCode;
	
	@Column(name = "INFO_DESCRIPTION")
	private String infoDesc;

	/**
	 * @return the supplierGstin
	 */
	public String getSupplierGstin() {
		return supplierGstin;
	}

	/**
	 * @param supplierGstin the supplierGstin to set
	 */
	public void setSupplierGstin(String supplierGstin) {
		this.supplierGstin = supplierGstin;
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
	 * @return the derivedReturnPeriod
	 */
	public Integer getDerivedReturnPeriod() {
		return derivedReturnPeriod;
	}

	/**
	 * @param derivedReturnPeriod the derivedReturnPeriod to set
	 */
	public void setDerivedReturnPeriod(Integer derivedReturnPeriod) {
		this.derivedReturnPeriod = derivedReturnPeriod;
	}


	/**
	 * @return the receivedDate
	 */
	public LocalDate getReceivedDate() {
		return receivedDate;
	}

	/**
	 * @param receivedDate the receivedDate to set
	 */
	public void setReceivedDate(LocalDate receivedDate) {
		this.receivedDate = receivedDate;
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
	 * @return the docAmount
	 */
	public BigDecimal getDocAmount() {
		return docAmount;
	}

	/**
	 * @param docAmount the docAmount to set
	 */
	public void setDocAmount(BigDecimal docAmount) {
		this.docAmount = docAmount;
	}

	/**
	 * @return the orgDocNum
	 */
	public String getOrgDocNum() {
		return orgDocNum;
	}

	/**
	 * @param orgDocNum the orgDocNum to set
	 */
	public void setOrgDocNum(String orgDocNum) {
		this.orgDocNum = orgDocNum;
	}

	/**
	 * @return the orgDocDate
	 */
	public LocalDate getOrgDocDate() {
		return orgDocDate;
	}

	/**
	 * @param orgDocDate the orgDocDate to set
	 */
	public void setOrgDocDate(LocalDate orgDocDate) {
		this.orgDocDate = orgDocDate;
	}

	/**
	 * @return the orgCustGstin
	 */
	public String getOrgCustGstin() {
		return orgCustGstin;
	}

	/**
	 * @param orgCustGstin the orgCustGstin to set
	 */
	public void setOrgCustGstin(String orgCustGstin) {
		this.orgCustGstin = orgCustGstin;
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
	 * @return the tableSection
	 */
	public String getTableSection() {
		return tableSection;
	}

	/**
	 * @param tableSection the tableSection to set
	 */
	public void setTableSection(String tableSection) {
		this.tableSection = tableSection;
	}

	/**
	 * @return the uinOrComposition
	 */
	public String getUinOrComposition() {
		return uinOrComposition;
	}

	/**
	 * @param uinOrComposition the uinOrComposition to set
	 */
	public void setUinOrComposition(String uinOrComposition) {
		this.uinOrComposition = uinOrComposition;
	}

	/**
	 * @return the customerGstin
	 */
	public String getCustomerGstin() {
		return customerGstin;
	}

	/**
	 * @param customerGstin the customerGstin to set
	 */
	public void setCustomerGstin(String customerGstin) {
		this.customerGstin = customerGstin;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return the billToState
	 */
	public String getBillToState() {
		return billToState;
	}

	/**
	 * @param billToState the billToState to set
	 */
	public void setBillToState(String billToState) {
		this.billToState = billToState;
	}

	/**
	 * @return the shipToState
	 */
	public String getShipToState() {
		return shipToState;
	}

	/**
	 * @param shipToState the shipToState to set
	 */
	public void setShipToState(String shipToState) {
		this.shipToState = shipToState;
	}

	/**
	 * @return the shipBillNum
	 */
	public String getShipBillNum() {
		return shipBillNum;
	}

	/**
	 * @param shipBillNum the shipBillNum to set
	 */
	public void setShipBillNum(String shipBillNum) {
		this.shipBillNum = shipBillNum;
	}

	/**
	 * @return the shipBillDate
	 */
	public LocalDate getShipBillDate() {
		return shipBillDate;
	}

	/**
	 * @param shipBillDate the shipBillDate to set
	 */
	public void setShipBillDate(LocalDate shipBillDate) {
		this.shipBillDate = shipBillDate;
	}

	/**
	 * @return the shipPortCode
	 */
	public String getShipPortCode() {
		return shipPortCode;
	}

	/**
	 * @param shipPortCode the shipPortCode to set
	 */
	public void setShipPortCode(String shipPortCode) {
		this.shipPortCode = shipPortCode;
	}

	/**
	 * @return the reverseCharge
	 */
	public String getReverseCharge() {
		return reverseCharge;
	}

	/**
	 * @param reverseCharge the reverseCharge to set
	 */
	public void setReverseCharge(String reverseCharge) {
		this.reverseCharge = reverseCharge;
	}

	/**
	 * @return the ecomCustomerGstin
	 */
	public String getEcomCustomerGstin() {
		return ecomCustomerGstin;
	}

	/**
	 * @param ecomCustomerGstin the ecomCustomerGstin to set
	 */
	public void setEcomCustomerGstin(String ecomCustomerGstin) {
		this.ecomCustomerGstin = ecomCustomerGstin;
	}

	/**
	 * @return the itcFlag
	 */
	public String getItcFlag() {
		return itcFlag;
	}

	/**
	 * @param itcFlag the itcFlag to set
	 */
	public void setItcFlag(String itcFlag) {
		this.itcFlag = itcFlag;
	}

	/**
	 * @return the exportDuty
	 */
	public BigDecimal getExportDuty() {
		return exportDuty;
	}

	/**
	 * @param exportDuty the exportDuty to set
	 */
	public void setExportDuty(BigDecimal exportDuty) {
		this.exportDuty = exportDuty;
	}

	/**
	 * @return the customerCode
	 */
	public String getCustomerCode() {
		return customerCode;
	}

	/**
	 * @param customerCode the customerCode to set
	 */
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	/**
	 * @return the accountingVoucherNo
	 */
	public String getAccountingVoucherNo() {
		return accountingVoucherNo;
	}

	/**
	 * @param accountingVoucherNo the accountingVoucherNo to set
	 */
	public void setAccountingVoucherNo(String accountingVoucherNo) {
		this.accountingVoucherNo = accountingVoucherNo;
	}

	/**
	 * @return the accountingVoucherDate
	 */
	public LocalDate getAccountingVoucherDate() {
		return accountingVoucherDate;
	}

	/**
	 * @param accountingVoucherDate the accountingVoucherDate to set
	 */
	public void setAccountingVoucherDate(LocalDate accountingVoucherDate) {
		this.accountingVoucherDate = accountingVoucherDate;
	}

	/**
	 * @return the glaAccountCode
	 */
	public String getGlaAccountCode() {
		return glaAccountCode;
	}

	/**
	 * @param glaAccountCode the glaAccountCode to set
	 */
	public void setGlaAccountCode(String glaAccountCode) {
		this.glaAccountCode = glaAccountCode;
	}

	/**
	 * @return the fob
	 */
	public BigDecimal getFob() {
		return fob;
	}

	/**
	 * @param fob the fob to set
	 */
	public void setFob(BigDecimal fob) {
		this.fob = fob;
	}

	/**
	 * @return the tcsFlag
	 */
	public String getTcsFlag() {
		return tcsFlag;
	}

	/**
	 * @param tcsFlag the tcsFlag to set
	 */
	public void setTcsFlag(String tcsFlag) {
		this.tcsFlag = tcsFlag;
	}

	/**
	 * @return the crDrPreGst
	 */
	public String getCrDrPreGst() {
		return crDrPreGst;
	}

	/**
	 * @param crDrPreGst the crDrPreGst to set
	 */
	public void setCrDrPreGst(String crDrPreGst) {
		this.crDrPreGst = crDrPreGst;
	}

	/**
	 * @return the crDrReason
	 */
	public String getCrDrReason() {
		return crDrReason;
	}

	/**
	 * @param crDrReason the crDrReason to set
	 */
	public void setCrDrReason(String crDrReason) {
		this.crDrReason = crDrReason;
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
	 * @return the profitCenter1
	 */
	public String getProfitCenter1() {
		return profitCenter1;
	}

	/**
	 * @param profitCenter1 the profitCenter1 to set
	 */
	public void setProfitCenter1(String profitCenter1) {
		this.profitCenter1 = profitCenter1;
	}

	/**
	 * @return the profitCenter2
	 */
	public String getProfitCenter2() {
		return profitCenter2;
	}

	/**
	 * @param profitCenter2 the profitCenter2 to set
	 */
	public void setProfitCenter2(String profitCenter2) {
		this.profitCenter2 = profitCenter2;
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
	 * @return the userDefinied1
	 */
	public String getUserDefinied1() {
		return userDefinied1;
	}

	/**
	 * @param userDefinied1 the userDefinied1 to set
	 */
	public void setUserDefinied1(String userDefinied1) {
		this.userDefinied1 = userDefinied1;
	}

	/**
	 * @return the userDefinied2
	 */
	public String getUserDefinied2() {
		return userDefinied2;
	}

	/**
	 * @param userDefinied2 the userDefinied2 to set
	 */
	public void setUserDefinied2(String userDefinied2) {
		this.userDefinied2 = userDefinied2;
	}

	/**
	 * @return the userDefinied3
	 */
	public String getUserDefinied3() {
		return userDefinied3;
	}

	/**
	 * @param userDefinied3 the userDefinied3 to set
	 */
	public void setUserDefinied3(String userDefinied3) {
		this.userDefinied3 = userDefinied3;
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
	 * @return the itemHsnSac
	 */
	public String getItemHsnSac() {
		return itemHsnSac;
	}

	/**
	 * @param itemHsnSac the itemHsnSac to set
	 */
	public void setItemHsnSac(String itemHsnSac) {
		this.itemHsnSac = itemHsnSac;
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
	 * @return the itemQuantity
	 */
	public BigDecimal getItemQuantity() {
		return itemQuantity;
	}

	/**
	 * @param itemQuantity the itemQuantity to set
	 */
	public void setItemQuantity(BigDecimal itemQuantity) {
		this.itemQuantity = itemQuantity;
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
	 * @return the igstAmount
	 */
	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	/**
	 * @param igstAmount the igstAmount to set
	 */
	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
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
	 * @return the cgstAmount
	 */
	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	/**
	 * @param cgstAmount the cgstAmount to set
	 */
	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
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
	 * @return the sgstAmount
	 */
	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	/**
	 * @param sgstAmount the sgstAmount to set
	 */
	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
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
	 * @return the cessRateAdv
	 */
	public BigDecimal getCessRateAdv() {
		return cessRateAdv;
	}

	/**
	 * @param cessRateAdv the cessRateAdv to set
	 */
	public void setCessRateAdv(BigDecimal cessRateAdv) {
		this.cessRateAdv = cessRateAdv;
	}

	/**
	 * @return the cessAmountAdv
	 */
	public BigDecimal getCessAmountAdv() {
		return cessAmountAdv;
	}

	/**
	 * @param cessAmountAdv the cessAmountAdv to set
	 */
	public void setCessAmountAdv(BigDecimal cessAmountAdv) {
		this.cessAmountAdv = cessAmountAdv;
	}

	/**
	 * @return the docHeaderId
	 */
	public Long getDocHeaderId() {
		return docHeaderId;
	}

	/**
	 * @param docHeaderId the docHeaderId to set
	 */
	public void setDocHeaderId(Long docHeaderId) {
		this.docHeaderId = docHeaderId;
	}

	/**
	 * @return the itemNo
	 */
	public Integer getItemNo() {
		return itemNo;
	}

	/**
	 * @param itemNo the itemNo to set
	 */
	public void setItemNo(Integer itemNo) {
		this.itemNo = itemNo;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorType
	 */
	public String getErrorType() {
		return errorType;
	}

	/**
	 * @param errorType the errorType to set
	 */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	/**
	 * @return the errorDesc
	 */
	public String getErrorDesc() {
		return errorDesc;
	}

	/**
	 * @param errorDesc the errorDesc to set
	 */
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	/**
	 * @return the infoCode
	 */
	public String getInfoCode() {
		return infoCode;
	}

	/**
	 * @param infoCode the infoCode to set
	 */
	public void setInfoCode(String infoCode) {
		this.infoCode = infoCode;
	}

	/**
	 * @return the infoDesc
	 */
	public String getInfoDesc() {
		return infoDesc;
	}

	/**
	 * @param infoDesc the infoDesc to set
	 */
	public void setInfoDesc(String infoDesc) {
		this.infoDesc = infoDesc;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"Gstr1ProcessedReportView [supplierGstin=%s, returnPeriod=%s, "
				+ "derivedReturnPeriod=%s, recevedDate=%s, supplyType=%s,"
				+ "docType=%s, docNum=%s, docDate=%s, docAmount=%s, "
				+ "orgDocNum=%s, orgDocDate=%s, orgCustGstin=%s, "
				+ "taxDocType=%s, tableSection=%s, uinOrComposition=%s, "
				+ "customerGstin=%s, customerName=%s, billToState=%s, "
				+ "shipToState=%s, shipBillNum=%s, shipBillDate=%s, "
				+ "shipPortCode=%s, reverseCharge=%s, ecomCustomerGstin=%s, "
				+ "itcFlag=%s, exportDuty=%s, customerCode=%s, "
				+ "accountingVoucherNo=%s, accountingVoucherDate=%s, "
				+ "glaAccountCode=%s, fob=%s, tcsFlag=%s, crDrPreGst=%s, "
				+ "crDrReason=%s, sourceIdentifier=%s, sourceFileName=%s, "
				+ "division=%s, subDivision=%s, profitCenter1=%s, "
				+ "profitCenter2=%s, plantCode=%s, userDefinied1=%s, "
				+ "userDefinied2=%s, userDefinied3=%s, pos=%s, gstnStatus=%s, "
				+ "itemHsnSac=%s, itemDesc=%s, itemType=%s, itemUqc=%s, "
				+ "itemQuantity=%s, productCode=%s, taxableValue=%s, "
				+ "igstRate=%s, igstAmount=%s, cgstRate=%s, cgstAmount=%s, "
				+ "sgstRate=%s, sgstAmount=%s, cessRateSpecific=%s, "
				+ "cessAmountSpecific=%s, cessRateAdv=%s, cessAmountAdv=%s, "
				+ "docHeaderId=%s, itemNo=%s, errorCode=%s, errorType=%s, "
				+ "errorDesc=%s, infoCode=%s, infoDesc=%s]",
				supplierGstin, returnPeriod, derivedReturnPeriod, receivedDate,
				supplyType, docType, docNum, docDate, docAmount, orgDocNum,
				orgDocDate, orgCustGstin, taxDocType, tableSection,
				uinOrComposition, customerGstin, customerName, billToState,
				shipToState, shipBillNum, shipBillDate, shipPortCode,
				reverseCharge, ecomCustomerGstin, itcFlag, exportDuty,
				customerCode, accountingVoucherNo, accountingVoucherDate,
				glaAccountCode, fob, tcsFlag, crDrPreGst, crDrReason,
				sourceIdentifier, sourceFileName, division, subDivision,
				profitCenter1, profitCenter2, plantCode, userDefinied1,
				userDefinied2, userDefinied3, pos, gstnStatus, itemHsnSac,
				itemDesc, itemType, itemUqc, itemQuantity, productCode,
				taxableValue, igstRate, igstAmount, cgstRate, cgstAmount,
				sgstRate, sgstAmount, cessRateSpecific, cessAmountSpecific,
				cessRateAdv, cessAmountAdv, docHeaderId, itemNo, errorCode,
				errorType, errorDesc, infoCode, infoDesc);
	}

}
