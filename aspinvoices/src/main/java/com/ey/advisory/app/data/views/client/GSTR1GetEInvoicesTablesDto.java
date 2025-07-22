package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;

/**
 * 
 * @author Anand3.M
 *
 */
public class GSTR1GetEInvoicesTablesDto {

	private String irnNum;
	private String irnGenDate;
	private String eInvStatus;
	private String autoDraftStatus;
	private String autoDraftDate;
	private String errorCode;
	private String errorMsg;
	private String returnPeriod;
	private String supplierGSTIN;
	private String customerGSTIN;
	private String custTradeName;
	private String documentType;
	private String supplyType;
	private String documentNo;
	private String documentDate;
	private String pos;
	private String portCode;
	private String shippingbillNumber;
	private String shippingbillDate;
	private String reverseCharge;
	private String ecomGSTIN;
	private String itemSerialNumber;
	private BigDecimal itemAssessAmount = BigDecimal.ZERO;
	private BigDecimal taxRate = BigDecimal.ZERO;
	private BigDecimal igstAmount = BigDecimal.ZERO;
	private BigDecimal cgstAmount = BigDecimal.ZERO;
	private BigDecimal sgstAmount = BigDecimal.ZERO;
	private BigDecimal cessAmount = BigDecimal.ZERO;
	private BigDecimal invoiceValue = BigDecimal.ZERO;
	private String irnSourceType;
	private String tableType;

	public String getIrnNum() {
		return irnNum;
	}

	public void setIrnNum(String irnNum) {
		this.irnNum = irnNum;
	}

	public String getIrnGenDate() {
		return irnGenDate;
	}

	public void setIrnGenDate(String irnGenDate) {
		this.irnGenDate = irnGenDate;
	}

	public String geteInvStatus() {
		return eInvStatus;
	}

	public void seteInvStatus(String eInvStatus) {
		this.eInvStatus = eInvStatus;
	}

	public String getAutoDraftStatus() {
		return autoDraftStatus;
	}

	public void setAutoDraftStatus(String autoDraftStatus) {
		this.autoDraftStatus = autoDraftStatus;
	}

	public String getAutoDraftDate() {
		return autoDraftDate;
	}

	public void setAutoDraftDate(String autoDraftDate) {
		this.autoDraftDate = autoDraftDate;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getSupplierGSTIN() {
		return supplierGSTIN;
	}

	public void setSupplierGSTIN(String supplierGSTIN) {
		this.supplierGSTIN = supplierGSTIN;
	}

	public String getCustomerGSTIN() {
		return customerGSTIN;
	}

	public void setCustomerGSTIN(String customerGSTIN) {
		this.customerGSTIN = customerGSTIN;
	}

	public String getCustTradeName() {
		return custTradeName;
	}

	public void setCustTradeName(String custTradeName) {
		this.custTradeName = custTradeName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getShippingbillNumber() {
		return shippingbillNumber;
	}

	public void setShippingbillNumber(String shippingbillNumber) {
		this.shippingbillNumber = shippingbillNumber;
	}

	public String getShippingbillDate() {
		return shippingbillDate;
	}

	public void setShippingbillDate(String shippingbillDate) {
		this.shippingbillDate = shippingbillDate;
	}

	public String getReverseCharge() {
		return reverseCharge;
	}

	public void setReverseCharge(String reverseCharge) {
		this.reverseCharge = reverseCharge;
	}

	public String getEcomGSTIN() {
		return ecomGSTIN;
	}

	public void setEcomGSTIN(String ecomGSTIN) {
		this.ecomGSTIN = ecomGSTIN;
	}

	public String getItemSerialNumber() {
		return itemSerialNumber;
	}

	public void setItemSerialNumber(String itemSerialNumber) {
		this.itemSerialNumber = itemSerialNumber;
	}

	public BigDecimal getItemAssessAmount() {
		return itemAssessAmount;
	}

	public void setItemAssessAmount(BigDecimal itemAssessAmount) {
		this.itemAssessAmount = itemAssessAmount;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	public BigDecimal getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(BigDecimal cessAmount) {
		this.cessAmount = cessAmount;
	}

	public BigDecimal getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(BigDecimal invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	public String getIrnSourceType() {
		return irnSourceType;
	}

	public void setIrnSourceType(String irnSourceType) {
		this.irnSourceType = irnSourceType;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	@Override
	public String toString() {
		return "GSTR1GetEInvoicesTablesDto [irnNum=" + irnNum + ", irnGenDate="
				+ irnGenDate + ", eInvStatus=" + eInvStatus
				+ ", autoDraftStatus=" + autoDraftStatus + ", autoDraftDate="
				+ autoDraftDate + ", errorCode=" + errorCode + ", errorMsg="
				+ errorMsg + ", returnPeriod=" + returnPeriod
				+ ", supplierGSTIN=" + supplierGSTIN + ", customerGSTIN="
				+ customerGSTIN + ", custTradeName=" + custTradeName
				+ ", documentType=" + documentType + ", supplyType="
				+ supplyType + ", documentNo=" + documentNo + ", documentDate="
				+ documentDate + ", pos=" + pos + ", portCode=" + portCode
				+ ", shippingbillNumber=" + shippingbillNumber
				+ ", shippingbillDate=" + shippingbillDate + ", reverseCharge="
				+ reverseCharge + ", ecomGSTIN=" + ecomGSTIN
				+ ", itemSerialNumber=" + itemSerialNumber
				+ ", itemAssessAmount=" + itemAssessAmount + ", taxRate="
				+ taxRate + ", igstAmount=" + igstAmount + ", cgstAmount="
				+ cgstAmount + ", sgstAmount=" + sgstAmount + ", cessAmount="
				+ cessAmount + ", invoiceValue=" + invoiceValue
				+ ", irnSourceType=" + irnSourceType + ", tableType="
				+ tableType + "]";
	}

}
