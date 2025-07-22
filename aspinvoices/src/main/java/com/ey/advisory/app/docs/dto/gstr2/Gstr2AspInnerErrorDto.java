package com.ey.advisory.app.docs.dto.gstr2;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2AspInnerErrorDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	@Expose
	@SerializedName("docType")
	private String docType;
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
	@SerializedName("originalDocNum")
	private String originalDocNum;
	@Expose
	@SerializedName("originalDocDate")
	private LocalDate originalDocDate;
	@Expose
	@SerializedName("crdPreGst")
	private String crdPreGst;
	@Expose
	@SerializedName("lineNumber")
	private Integer lineNumber;
	@Expose
	@SerializedName("cgstin")
	private String cgstin;
	@Expose
	@SerializedName("uinOrComposition")
	private String uinOrComposition;
	@Expose
	@SerializedName("originalCgstin")
	private String originalCgstin;
	@Expose
	@SerializedName("customerNum")
	private String customerNum;
	@Expose
	@SerializedName("customerCode")
	private String customerCode;
	@Expose
	@SerializedName("billToState")
	private String billToState;
	@Expose
	@SerializedName("shipToState")
	private String shipToState;
	@Expose
	@SerializedName("pos")
	private String pos;
	@Expose
	@SerializedName("portCode")
	private String portCode;
	@Expose
	@SerializedName("shippingBillNo")
	private String shippingBillNo;
	@Expose
	@SerializedName("shippingBillDate")
	private LocalDate shippingBillDate;
	@Expose
	@SerializedName("fob")
	private String fob;
	@Expose
	@SerializedName("exportDuty")
	private String exportDuty;
	@Expose
	@SerializedName("hsnSac")
	private String hsnSac;
	@Expose
	@SerializedName("itemCode")
	private String itemCode;
	@Expose
	@SerializedName("itemDescp")
	private String itemDescp;
	@Expose
	@SerializedName("itemCategory")
	private String itemCategory;
	@Expose
	@SerializedName("uom")
	private String uom;
	@Expose
	@SerializedName("qtySupplied")
	private String qtySupplied;
	@Expose
	@SerializedName("taxableValue")
	private BigDecimal taxableValue;
	@Expose
	@SerializedName("igstRate")
	private BigDecimal igstRate;
	@Expose
	@SerializedName("igstAmount")
	private BigDecimal igstAmount;
	@Expose
	@SerializedName("cgstRate")
	private BigDecimal cgstRate;
	@Expose
	@SerializedName("cgstAmount")
	private BigDecimal cgstAmount;
	@Expose
	@SerializedName("sgstRate")
	private BigDecimal sgstRate;
	@Expose
	@SerializedName("sgstAmount")
	private BigDecimal sgstAmount;
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
	@SerializedName("invoiceValue")
	private BigDecimal invoiceValue;
	@Expose
	@SerializedName("reverseCharge")
	private Boolean reverseCharge;
	@Expose
	@SerializedName("egstin")
	private String egstin;
	@Expose
	@SerializedName("itcFlag")
	private Boolean itcFlag;
	@Expose
	@SerializedName("reasonFrCrDbNote")
	private String reasonFrCrDbNote;
	@Expose
	@SerializedName("accountVoucherNum")
	private String accountVoucherNum;
	@Expose
	@SerializedName("accountVoucherDate")
	private LocalDate accountVoucherDate;
	@Expose
	@SerializedName("userdefinedField1")
	private String userdefinedField1;
	@Expose
	@SerializedName("userdefinedField2")
	private String userdefinedField2;
	@Expose
	@SerializedName("userdefinedField3")
	private String userdefinedField3;
	@Expose
	@SerializedName("errorCode")
	private String errorCode;
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
	 * @return the originalDocNum
	 */
	public String getOriginalDocNum() {
		return originalDocNum;
	}
	/**
	 * @param originalDocNum the originalDocNum to set
	 */
	public void setOriginalDocNum(String originalDocNum) {
		this.originalDocNum = originalDocNum;
	}
	/**
	 * @return the originalDocDate
	 */
	public LocalDate getOriginalDocDate() {
		return originalDocDate;
	}
	/**
	 * @param originalDocDate the originalDocDate to set
	 */
	public void setOriginalDocDate(LocalDate originalDocDate) {
		this.originalDocDate = originalDocDate;
	}
	/**
	 * @return the crdPreGst
	 */
	public String getCrdPreGst() {
		return crdPreGst;
	}
	/**
	 * @param crdPreGst the crdPreGst to set
	 */
	public void setCrdPreGst(String crdPreGst) {
		this.crdPreGst = crdPreGst;
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
	 * @return the cgstin
	 */
	public String getCgstin() {
		return cgstin;
	}
	/**
	 * @param cgstin the cgstin to set
	 */
	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
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
	 * @return the originalCgstin
	 */
	public String getOriginalCgstin() {
		return originalCgstin;
	}
	/**
	 * @param originalCgstin the originalCgstin to set
	 */
	public void setOriginalCgstin(String originalCgstin) {
		this.originalCgstin = originalCgstin;
	}
	/**
	 * @return the customerNum
	 */
	public String getCustomerNum() {
		return customerNum;
	}
	/**
	 * @param customerNum the customerNum to set
	 */
	public void setCustomerNum(String customerNum) {
		this.customerNum = customerNum;
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
	 * @return the shippingBillNo
	 */
	public String getShippingBillNo() {
		return shippingBillNo;
	}
	/**
	 * @param shippingBillNo the shippingBillNo to set
	 */
	public void setShippingBillNo(String shippingBillNo) {
		this.shippingBillNo = shippingBillNo;
	}
	/**
	 * @return the shippingBillDate
	 */
	public LocalDate getShippingBillDate() {
		return shippingBillDate;
	}
	/**
	 * @param shippingBillDate the shippingBillDate to set
	 */
	public void setShippingBillDate(LocalDate shippingBillDate) {
		this.shippingBillDate = shippingBillDate;
	}
	/**
	 * @return the fob
	 */
	public String getFob() {
		return fob;
	}
	/**
	 * @param fob the fob to set
	 */
	public void setFob(String fob) {
		this.fob = fob;
	}
	/**
	 * @return the exportDuty
	 */
	public String getExportDuty() {
		return exportDuty;
	}
	/**
	 * @param exportDuty the exportDuty to set
	 */
	public void setExportDuty(String exportDuty) {
		this.exportDuty = exportDuty;
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
	 * @return the itemDescp
	 */
	public String getItemDescp() {
		return itemDescp;
	}
	/**
	 * @param itemDescp the itemDescp to set
	 */
	public void setItemDescp(String itemDescp) {
		this.itemDescp = itemDescp;
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
	 * @return the qtySupplied
	 */
	public String getQtySupplied() {
		return qtySupplied;
	}
	/**
	 * @param qtySupplied the qtySupplied to set
	 */
	public void setQtySupplied(String qtySupplied) {
		this.qtySupplied = qtySupplied;
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
	 * @return the reverseCharge
	 */
	public Boolean getReverseCharge() {
		return reverseCharge;
	}
	/**
	 * @param reverseCharge the reverseCharge to set
	 */
	public void setReverseCharge(Boolean reverseCharge) {
		this.reverseCharge = reverseCharge;
	}
	/**
	 * @return the egstin
	 */
	public String getEgstin() {
		return egstin;
	}
	/**
	 * @param egstin the egstin to set
	 */
	public void setEgstin(String egstin) {
		this.egstin = egstin;
	}
	/**
	 * @return the itcFlag
	 */
	public Boolean getItcFlag() {
		return itcFlag;
	}
	/**
	 * @param itcFlag the itcFlag to set
	 */
	public void setItcFlag(Boolean itcFlag) {
		this.itcFlag = itcFlag;
	}
	/**
	 * @return the reasonFrCrDbNote
	 */
	public String getReasonFrCrDbNote() {
		return reasonFrCrDbNote;
	}
	/**
	 * @param reasonFrCrDbNote the reasonFrCrDbNote to set
	 */
	public void setReasonFrCrDbNote(String reasonFrCrDbNote) {
		this.reasonFrCrDbNote = reasonFrCrDbNote;
	}
	/**
	 * @return the accountVoucherNum
	 */
	public String getAccountVoucherNum() {
		return accountVoucherNum;
	}
	/**
	 * @param accountVoucherNum the accountVoucherNum to set
	 */
	public void setAccountVoucherNum(String accountVoucherNum) {
		this.accountVoucherNum = accountVoucherNum;
	}
	/**
	 * @return the accountVoucherDate
	 */
	public LocalDate getAccountVoucherDate() {
		return accountVoucherDate;
	}
	/**
	 * @param accountVoucherDate the accountVoucherDate to set
	 */
	public void setAccountVoucherDate(LocalDate accountVoucherDate) {
		this.accountVoucherDate = accountVoucherDate;
	}
	/**
	 * @return the userdefinedField1
	 */
	public String getUserdefinedField1() {
		return userdefinedField1;
	}
	/**
	 * @param userdefinedField1 the userdefinedField1 to set
	 */
	public void setUserdefinedField1(String userdefinedField1) {
		this.userdefinedField1 = userdefinedField1;
	}
	/**
	 * @return the userdefinedField2
	 */
	public String getUserdefinedField2() {
		return userdefinedField2;
	}
	/**
	 * @param userdefinedField2 the userdefinedField2 to set
	 */
	public void setUserdefinedField2(String userdefinedField2) {
		this.userdefinedField2 = userdefinedField2;
	}
	/**
	 * @return the userdefinedField3
	 */
	public String getUserdefinedField3() {
		return userdefinedField3;
	}
	/**
	 * @param userdefinedField3 the userdefinedField3 to set
	 */
	public void setUserdefinedField3(String userdefinedField3) {
		this.userdefinedField3 = userdefinedField3;
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
	
	
	
	
	

}
