package com.ey.advisory.app.data.business.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ey.advisory.common.EwbLocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.MappedSuperclass;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * This class represents the basic information contained at a line item level
 * for all financial documents. The class is marked as abstract so that relevant
 * subclasses can be used to instantiate.
 * 
 * @author Sai.Pakanati
 *
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class TransDocLineItem extends BasicLineItemValues {

	/**
	 * This is a line number field given by the user. This can contain errors or
	 * can be empty or null. Hence this field cannot be reliably used for
	 * identifying the order of line numbers in the input document.
	 */
	@Expose
	@SerializedName("itemNo")
	@XmlElement(name = "itm-no")
	protected int lineNo;

	// Required only if the doc is Credit/Debit Note.
	@Expose
	@SerializedName("crDrReason")
	@XmlElement(name = "cr-dr-rsn")
	protected String crDrReason;

	@Expose
	@SerializedName("hsnsacCode")
	@XmlElement(name = "hsn-sac")
	protected String hsnSac;

	@Expose
	@SerializedName("supplyType")
	@XmlElement(name = "supp-type")
	protected String supplyType;

	@Expose
	@SerializedName("productCode")
	@XmlElement(name = "itm-cd")
	protected String itemCode;

	@Expose
	@SerializedName("itemDesc")
	@XmlElement(name = "itm-desc")
	protected String itemDescription;

	@Expose
	@SerializedName("itemType")
	@XmlElement(name = "itm-type")
	protected String itemCategory;

	@Expose
	@SerializedName("itemUqc")
	@XmlElement(name = "uom")
	protected String uom;

	@Expose
	@SerializedName("glCodeTaxableVal")
	@XmlElement(name = "gl-cd-tx-val")
	protected String glCodeTaxableValue;

	@Expose
	@SerializedName("adjustmentRefNo")
	@XmlElement(name = "adj-ref-no")
	protected String adjustmentRefNo;

	@Expose
	@SerializedName("adjustmentRefDate")
	@XmlElement(name = "adj-ref-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate adjustmentRefDate;

	@Expose
	@SerializedName("adjustedTaxableValue")
	@XmlElement(name = "adj-tx-val")
	protected BigDecimal adjustedTaxableValue;

	@Expose
	@SerializedName("adjustedIgstAmt")
	@XmlElement(name = "adj-igst-amt")
	protected BigDecimal adjustedIgstAmt;

	@Expose
	@SerializedName("adjustedCgstAmt")
	@XmlElement(name = "adj-cgst-amt")
	protected BigDecimal adjustedCgstAmt;

	@Expose
	@SerializedName("adjustedSgstAmt")
	@XmlElement(name = "adj-sgst-amt")
	protected BigDecimal adjustedSgstAmt;

	@Expose
	@SerializedName("adjustedCessAmtAdvalorem")
	@XmlElement(name = "adj-cess-amt-adv")
	protected BigDecimal adjustedCessAmtAdvalorem;

	@Expose
	@SerializedName("adjustedCessAmtSpecific")
	@XmlElement(name = "adj-cs-amt-spec")
	protected BigDecimal adjustedCessAmtSpecific;

	@Expose
	@SerializedName("adjustedStateCessAmt")
	@XmlElement(name = "adj-st-cess-amt")
	protected BigDecimal adjustedStateCessAmt;

	@Expose
	@SerializedName("tcsAmt")
	@XmlElement(name = "tcs-amt")
	protected BigDecimal tcsAmount;

	@Expose
	@SerializedName("itemQty")
	@XmlElement(name = "qty")
	protected BigDecimal qty;

	@Expose
	@SerializedName("lineItemAmt")
	@XmlElement(name = "line-itm-amt")
	private BigDecimal lineItemAmt;

	@Expose
	@SerializedName("taxPayable")
	@XmlElement(name = "tx-paybl")
	protected BigDecimal taxPayable;

	@Expose
	@SerializedName("location")
	protected String location;

	@Expose
	@SerializedName("profitCentre")
	@XmlElement(name = "profit-cntr")
	protected String profitCentre;

	@Expose
	@SerializedName("plantCode")
	@XmlElement(name = "plnt-cd")
	protected String plantCode;

	/* Amendment details */
	@Expose
	@SerializedName("originalDocNo")
	@XmlElement(name = "orig-doc-num")
	protected String origDocNo;

	@Expose
	@SerializedName("originalDocDate")
	@XmlElement(name = "orig-doc-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate origDocDate;

	@Expose
	@SerializedName("udf1")
	@XmlElement(name = "udf1")
	protected String userdefinedfield1;

	@Expose
	@SerializedName("udf2")
	@XmlElement(name = "udf2")
	protected String userdefinedfield2;

	@Expose
	@SerializedName("udf3")
	@XmlElement(name = "udf3")
	protected String userdefinedfield3;

	@Expose
	@SerializedName("udf4")
	@XmlElement(name = "udf4")
	protected String userDefinedField4;

	@Expose
	@SerializedName("udf5")
	@XmlElement(name = "udf5")
	protected String userDefinedField5;

	@Expose
	@SerializedName("udf6")
	@XmlElement(name = "udf6")
	protected String userDefinedField6;

	@Expose
	@SerializedName("udf7")
	@XmlElement(name = "udf7")
	protected String userDefinedField7;

	@Expose
	@SerializedName("udf8")
	@XmlElement(name = "udf-8")
	protected String userDefinedField8;

	@Expose
	@SerializedName("udf9")
	@XmlElement(name = "udf9")
	protected String userDefinedField9;

	@Expose
	@SerializedName("udf10")
	@XmlElement(name = "udf10")
	protected String userDefinedField10;

	@Expose
	@SerializedName("udf11")
	@XmlElement(name = "udf11")
	protected String userDefinedField11;

	@Expose
	@SerializedName("udf12")
	@XmlElement(name = "udf12")
	protected String userDefinedField12;

	@Expose
	@SerializedName("udf13")
	@XmlElement(name = "udf13")
	protected String userDefinedField13;

	@Expose
	@SerializedName("udf14")
	@XmlElement(name = "udf14")
	protected String userDefinedField14;

	@Expose
	@SerializedName("udf15")
	@XmlElement(name = "udf15")
	protected String userDefinedField15;

	// Start - Fix for Defect Error code "ER0037"
	@XmlElement(name = "user-id")
	protected String userId;

	@XmlElement(name = "src-file-nm")
	protected String sourceFileName;

	protected String division;

	@Expose
	@SerializedName("profitCentre3")
	@XmlElement(name = "profit-cntr3")
	protected String userAccess1;

	@Expose
	@SerializedName("profitCentre4")
	@XmlElement(name = "profit-cntr4")
	protected String userAccess2;

	@Expose
	@SerializedName("profitCentre5")
	@XmlElement(name = "profit-cntr5")
	protected String userAccess3;

	@Expose
	@SerializedName("profitCentre6")
	@XmlElement(name = "profit-cntr6")
	protected String userAccess4;

	@Expose
	@SerializedName("profitCentre7")
	@XmlElement(name = "profit-cntr7")
	protected String userAccess5;

	@Expose
	@SerializedName("profitCentre8")
	@XmlElement(name = "profit-cntr8")
	protected String userAccess6;

	@XmlElement(name = "ret-period")
	protected String taxperiod;

	@XmlElement(name = "doc-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate docDate;

	@XmlElement(name = "cr-dr-pre-gst")
	protected String crDrPreGst;

	@XmlElement(name = "cust-supp-type")
	protected String custOrSuppType;

	@XmlElement(name = "diff-prcnt")
	protected String diffPercent;

	@XmlElement(name = "cust-supp-nm")
	protected String custOrSuppName;

	@XmlElement(name = "cust-supp-cd")
	protected String custOrSuppCode;

	@XmlElement(name = "cust-supp-add1")
	protected String custOrSuppAddress1;

	@XmlElement(name = "cust-supp-add2")
	protected String custOrSuppAddress2;

	@XmlElement(name = "cust-supp-add3")
	protected String custOrSuppAddress3;

	@XmlElement(name = "cust-supp-add4")
	protected String custOrSuppAddress4;

	protected String pos;

	@XmlElement(name = "state-app-cs")
	protected String stateApplyingCess;

	@XmlElement(name = "shp-prt-cd")
	protected String portCode;

	@XmlElement(name = "sec70-igst-flg")
	protected String section7OfIgstFlag;

	@XmlElement(name = "rev-chrg")
	protected String reverseCharge;

	@XmlElement(name = "clm-ref-flg")
	protected String claimRefundFlag;

	@XmlElement(name = "auto-pop-to-ref")
	protected String autoPopToRefundFlag;

	@XmlElement(name = "ewb-no")
	protected String eWayBillNo;

	@XmlElement(name = "ewb-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate eWayBillDate;
	// End - Fix for Defect Error code "ER0037"

	// This field is added in line items for report purpose
	@Expose
	@SerializedName("derivedTaxperiod")
	@XmlElement(name = "derv-ret-perd")
	protected Integer derivedTaxperiod;

	@Expose
	@SerializedName("glCodeIgst")
	@XmlElement(name = "gl-cd-igst")
	protected String glCodeIgst;

	@Expose
	@SerializedName("glCodeCgst")
	@XmlElement(name = "gl-cd-cgst")
	protected String glCodeCgst;

	@Expose
	@SerializedName("glCodeSgst")
	@XmlElement(name = "gl-cd-sgst")
	protected String glCodeSgst;

	@Expose
	@SerializedName("glCodeAdvCess")
	@XmlElement(name = "glcd-adv-cess")
	protected String glCodeAdvCess;

	@Expose
	@SerializedName("glCodeSpCess")
	@XmlElement(name = "glcd-sp-cess")
	protected String glCodeSpCess;

	@Expose
	@SerializedName("glCodeStateCess")
	@XmlElement(name = "glcd-st-cess")
	protected String glCodeStateCess;

	/**
	 * @return the lineNo
	 */
	public int getLineNo() {
		return lineNo;
	}

	/**
	 * @param lineNo
	 *            the lineNo to set
	 */
	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	/**
	 * @return the crDrReason
	 */
	public String getCrDrReason() {
		return crDrReason;
	}

	/**
	 * @param crDrReason
	 *            the crDrReason to set
	 */
	public void setCrDrReason(String crDrReason) {
		this.crDrReason = crDrReason;
	}

	/**
	 * @return the hsnSac
	 */
	public String getHsnSac() {
		return hsnSac;
	}

	/**
	 * @param hsnSac
	 *            the hsnSac to set
	 */
	public void setHsnSac(String hsnSac) {
		this.hsnSac = hsnSac;
	}

	/**
	 * @return the supplyType
	 */
	public String getSupplyType() {
		return supplyType;
	}

	/**
	 * @param supplyType
	 *            the supplyType to set
	 */
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	/**
	 * @return the itemCode
	 */
	public String getItemCode() {
		return itemCode;
	}

	/**
	 * @param itemCode
	 *            the itemCode to set
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
	 * @param itemDescription
	 *            the itemDescription to set
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	/**
	 * @return the itemCategory
	 */
	public String getItemCategory() {
		return itemCategory;
	}

	/**
	 * @param itemCategory
	 *            the itemCategory to set
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
	 * @param uom
	 *            the uom to set
	 */
	public void setUom(String uom) {
		this.uom = uom;
	}

	/**
	 * @return the glCodeTaxableValue
	 */
	public String getGlCodeTaxableValue() {
		return glCodeTaxableValue;
	}

	/**
	 * @param glCodeTaxableValue
	 *            the glCodeTaxableValue to set
	 */
	public void setGlCodeTaxableValue(String glCodeTaxableValue) {
		this.glCodeTaxableValue = glCodeTaxableValue;
	}

	/**
	 * @return the adjustmentRefNo
	 */
	public String getAdjustmentRefNo() {
		return adjustmentRefNo;
	}

	/**
	 * @param adjustmentRefNo
	 *            the adjustmentRefNo to set
	 */
	public void setAdjustmentRefNo(String adjustmentRefNo) {
		this.adjustmentRefNo = adjustmentRefNo;
	}

	/**
	 * @return the adjustmentRefDate
	 */
	public LocalDate getAdjustmentRefDate() {
		return adjustmentRefDate;
	}

	/**
	 * @param adjustmentRefDate
	 *            the adjustmentRefDate to set
	 */
	public void setAdjustmentRefDate(LocalDate adjustmentRefDate) {
		this.adjustmentRefDate = adjustmentRefDate;
	}

	/**
	 * @return the adjustedTaxableValue
	 */
	public BigDecimal getAdjustedTaxableValue() {
		return adjustedTaxableValue;
	}

	/**
	 * @param adjustedTaxableValue
	 *            the adjustedTaxableValue to set
	 */
	public void setAdjustedTaxableValue(BigDecimal adjustedTaxableValue) {
		this.adjustedTaxableValue = adjustedTaxableValue;
	}

	/**
	 * @return the adjustedIgstAmt
	 */
	public BigDecimal getAdjustedIgstAmt() {
		return adjustedIgstAmt;
	}

	/**
	 * @param adjustedIgstAmt
	 *            the adjustedIgstAmt to set
	 */
	public void setAdjustedIgstAmt(BigDecimal adjustedIgstAmt) {
		this.adjustedIgstAmt = adjustedIgstAmt;
	}

	/**
	 * @return the adjustedCgstAmt
	 */
	public BigDecimal getAdjustedCgstAmt() {
		return adjustedCgstAmt;
	}

	/**
	 * @param adjustedCgstAmt
	 *            the adjustedCgstAmt to set
	 */
	public void setAdjustedCgstAmt(BigDecimal adjustedCgstAmt) {
		this.adjustedCgstAmt = adjustedCgstAmt;
	}

	/**
	 * @return the adjustedSgstAmt
	 */
	public BigDecimal getAdjustedSgstAmt() {
		return adjustedSgstAmt;
	}

	/**
	 * @param adjustedSgstAmt
	 *            the adjustedSgstAmt to set
	 */
	public void setAdjustedSgstAmt(BigDecimal adjustedSgstAmt) {
		this.adjustedSgstAmt = adjustedSgstAmt;
	}

	/**
	 * @return the adjustedCessAmtAdvalorem
	 */
	public BigDecimal getAdjustedCessAmtAdvalorem() {
		return adjustedCessAmtAdvalorem;
	}

	/**
	 * @param adjustedCessAmtAdvalorem
	 *            the adjustedCessAmtAdvalorem to set
	 */
	public void setAdjustedCessAmtAdvalorem(
			BigDecimal adjustedCessAmtAdvalorem) {
		this.adjustedCessAmtAdvalorem = adjustedCessAmtAdvalorem;
	}

	/**
	 * @return the adjustedCessAmtSpecific
	 */
	public BigDecimal getAdjustedCessAmtSpecific() {
		return adjustedCessAmtSpecific;
	}

	/**
	 * @param adjustedCessAmtSpecific
	 *            the adjustedCessAmtSpecific to set
	 */
	public void setAdjustedCessAmtSpecific(BigDecimal adjustedCessAmtSpecific) {
		this.adjustedCessAmtSpecific = adjustedCessAmtSpecific;
	}

	/**
	 * @return the adjustedStateCessAmt
	 */
	public BigDecimal getAdjustedStateCessAmt() {
		return adjustedStateCessAmt;
	}

	/**
	 * @param adjustedStateCessAmt
	 *            the adjustedStateCessAmt to set
	 */
	public void setAdjustedStateCessAmt(BigDecimal adjustedStateCessAmt) {
		this.adjustedStateCessAmt = adjustedStateCessAmt;
	}

	/**
	 * @return the tcsAmount
	 */
	public BigDecimal getTcsAmount() {
		return tcsAmount;
	}

	/**
	 * @param tcsAmount
	 *            the tcsAmount to set
	 */
	public void setTcsAmount(BigDecimal tcsAmount) {
		this.tcsAmount = tcsAmount;
	}

	/**
	 * @return the qty
	 */
	public BigDecimal getQty() {
		return qty;
	}

	/**
	 * @param qty
	 *            the qty to set
	 */
	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	/**
	 * @return the lineItemAmt
	 */
	public BigDecimal getLineItemAmt() {
		return lineItemAmt;
	}

	/**
	 * @param lineItemAmt
	 *            the lineItemAmt to set
	 */
	public void setLineItemAmt(BigDecimal lineItemAmt) {
		this.lineItemAmt = lineItemAmt;
	}

	/**
	 * @return the origDocNo
	 */
	public String getOrigDocNo() {
		return origDocNo;
	}

	/**
	 * @param origDocNo
	 *            the origDocNo to set
	 */
	public void setOrigDocNo(String origDocNo) {
		this.origDocNo = origDocNo;
	}

	/**
	 * @return the origDocDate
	 */
	public LocalDate getOrigDocDate() {
		return origDocDate;
	}

	/**
	 * @param origDocDate
	 *            the origDocDate to set
	 */
	public void setOrigDocDate(LocalDate origDocDate) {
		this.origDocDate = origDocDate;
	}

	/**
	 * @return the userdefinedfield1
	 */
	public String getUserdefinedfield1() {
		return userdefinedfield1;
	}

	/**
	 * @param userdefinedfield1
	 *            the userdefinedfield1 to set
	 */
	public void setUserdefinedfield1(String userdefinedfield1) {
		this.userdefinedfield1 = userdefinedfield1;
	}

	/**
	 * @return the userdefinedfield2
	 */
	public String getUserdefinedfield2() {
		return userdefinedfield2;
	}

	/**
	 * @param userdefinedfield2
	 *            the userdefinedfield2 to set
	 */
	public void setUserdefinedfield2(String userdefinedfield2) {
		this.userdefinedfield2 = userdefinedfield2;
	}

	/**
	 * @return the userdefinedfield3
	 */
	public String getUserdefinedfield3() {
		return userdefinedfield3;
	}

	/**
	 * @param userdefinedfield3
	 *            the userdefinedfield3 to set
	 */
	public void setUserdefinedfield3(String userdefinedfield3) {
		this.userdefinedfield3 = userdefinedfield3;
	}

	/**
	 * @return the userDefinedField4
	 */
	public String getUserDefinedField4() {
		return userDefinedField4;
	}

	/**
	 * @param userDefinedField4
	 *            the userDefinedField4 to set
	 */
	public void setUserDefinedField4(String userDefinedField4) {
		this.userDefinedField4 = userDefinedField4;
	}

	/**
	 * @return the userDefinedField5
	 */
	public String getUserDefinedField5() {
		return userDefinedField5;
	}

	/**
	 * @param userDefinedField5
	 *            the userDefinedField5 to set
	 */
	public void setUserDefinedField5(String userDefinedField5) {
		this.userDefinedField5 = userDefinedField5;
	}

	/**
	 * @return the userDefinedField6
	 */
	public String getUserDefinedField6() {
		return userDefinedField6;
	}

	/**
	 * @param userDefinedField6
	 *            the userDefinedField6 to set
	 */
	public void setUserDefinedField6(String userDefinedField6) {
		this.userDefinedField6 = userDefinedField6;
	}

	/**
	 * @return the userDefinedField7
	 */
	public String getUserDefinedField7() {
		return userDefinedField7;
	}

	/**
	 * @param userDefinedField7
	 *            the userDefinedField7 to set
	 */
	public void setUserDefinedField7(String userDefinedField7) {
		this.userDefinedField7 = userDefinedField7;
	}

	/**
	 * @return the userDefinedField8
	 */
	public String getUserDefinedField8() {
		return userDefinedField8;
	}

	/**
	 * @param userDefinedField8
	 *            the userDefinedField8 to set
	 */
	public void setUserDefinedField8(String userDefinedField8) {
		this.userDefinedField8 = userDefinedField8;
	}

	/**
	 * @return the userDefinedField9
	 */
	public String getUserDefinedField9() {
		return userDefinedField9;
	}

	/**
	 * @param userDefinedField9
	 *            the userDefinedField9 to set
	 */
	public void setUserDefinedField9(String userDefinedField9) {
		this.userDefinedField9 = userDefinedField9;
	}

	/**
	 * @return the userDefinedField10
	 */
	public String getUserDefinedField10() {
		return userDefinedField10;
	}

	/**
	 * @param userDefinedField10
	 *            the userDefinedField10 to set
	 */
	public void setUserDefinedField10(String userDefinedField10) {
		this.userDefinedField10 = userDefinedField10;
	}

	/**
	 * @return the userDefinedField11
	 */
	public String getUserDefinedField11() {
		return userDefinedField11;
	}

	/**
	 * @param userDefinedField11
	 *            the userDefinedField11 to set
	 */
	public void setUserDefinedField11(String userDefinedField11) {
		this.userDefinedField11 = userDefinedField11;
	}

	/**
	 * @return the userDefinedField12
	 */
	public String getUserDefinedField12() {
		return userDefinedField12;
	}

	/**
	 * @param userDefinedField12
	 *            the userDefinedField12 to set
	 */
	public void setUserDefinedField12(String userDefinedField12) {
		this.userDefinedField12 = userDefinedField12;
	}

	/**
	 * @return the userDefinedField13
	 */
	public String getUserDefinedField13() {
		return userDefinedField13;
	}

	/**
	 * @param userDefinedField13
	 *            the userDefinedField13 to set
	 */
	public void setUserDefinedField13(String userDefinedField13) {
		this.userDefinedField13 = userDefinedField13;
	}

	/**
	 * @return the userDefinedField14
	 */
	public String getUserDefinedField14() {
		return userDefinedField14;
	}

	/**
	 * @param userDefinedField14
	 *            the userDefinedField14 to set
	 */
	public void setUserDefinedField14(String userDefinedField14) {
		this.userDefinedField14 = userDefinedField14;
	}

	/**
	 * @return the userDefinedField15
	 */
	public String getUserDefinedField15() {
		return userDefinedField15;
	}

	/**
	 * @param userDefinedField15
	 *            the userDefinedField15 to set
	 */
	public void setUserDefinedField15(String userDefinedField15) {
		this.userDefinedField15 = userDefinedField15;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getProfitCentre() {
		return profitCentre;
	}

	public void setProfitCentre(String profitCentre) {
		this.profitCentre = profitCentre;
	}

	public String getPlantCode() {
		return plantCode;
	}

	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getUserAccess1() {
		return userAccess1;
	}

	public void setUserAccess1(String userAccess1) {
		this.userAccess1 = userAccess1;
	}

	public String getUserAccess2() {
		return userAccess2;
	}

	public void setUserAccess2(String userAccess2) {
		this.userAccess2 = userAccess2;
	}

	public String getUserAccess3() {
		return userAccess3;
	}

	public void setUserAccess3(String userAccess3) {
		this.userAccess3 = userAccess3;
	}

	public String getUserAccess4() {
		return userAccess4;
	}

	public void setUserAccess4(String userAccess4) {
		this.userAccess4 = userAccess4;
	}

	public String getUserAccess5() {
		return userAccess5;
	}

	public void setUserAccess5(String userAccess5) {
		this.userAccess5 = userAccess5;
	}

	public String getUserAccess6() {
		return userAccess6;
	}

	public void setUserAccess6(String userAccess6) {
		this.userAccess6 = userAccess6;
	}

	public String getTaxperiod() {
		return taxperiod;
	}

	public void setTaxperiod(String taxperiod) {
		this.taxperiod = taxperiod;
	}

	public LocalDate getDocDate() {
		return docDate;
	}

	public void setDocDate(LocalDate docDate) {
		this.docDate = docDate;
	}

	public String getCrDrPreGst() {
		return crDrPreGst;
	}

	public void setCrDrPreGst(String crDrPreGst) {
		this.crDrPreGst = crDrPreGst;
	}

	public String getCustOrSuppType() {
		return custOrSuppType;
	}

	public void setCustOrSuppType(String custOrSuppType) {
		this.custOrSuppType = custOrSuppType;
	}

	public String getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(String diffPercent) {
		this.diffPercent = diffPercent;
	}

	public String getCustOrSuppName() {
		return custOrSuppName;
	}

	public void setCustOrSuppName(String custOrSuppName) {
		this.custOrSuppName = custOrSuppName;
	}

	public String getCustOrSuppCode() {
		return custOrSuppCode;
	}

	public void setCustOrSuppCode(String custOrSuppCode) {
		this.custOrSuppCode = custOrSuppCode;
	}

	public String getCustOrSuppAddress1() {
		return custOrSuppAddress1;
	}

	public void setCustOrSuppAddress1(String custOrSuppAddress1) {
		this.custOrSuppAddress1 = custOrSuppAddress1;
	}

	public String getCustOrSuppAddress2() {
		return custOrSuppAddress2;
	}

	public void setCustOrSuppAddress2(String custOrSuppAddress2) {
		this.custOrSuppAddress2 = custOrSuppAddress2;
	}

	public String getCustOrSuppAddress3() {
		return custOrSuppAddress3;
	}

	public void setCustOrSuppAddress3(String custOrSuppAddress3) {
		this.custOrSuppAddress3 = custOrSuppAddress3;
	}

	public String getCustOrSuppAddress4() {
		return custOrSuppAddress4;
	}

	public void setCustOrSuppAddress4(String custOrSuppAddress4) {
		this.custOrSuppAddress4 = custOrSuppAddress4;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getStateApplyingCess() {
		return stateApplyingCess;
	}

	public void setStateApplyingCess(String stateApplyingCess) {
		this.stateApplyingCess = stateApplyingCess;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getSection7OfIgstFlag() {
		return section7OfIgstFlag;
	}

	public void setSection7OfIgstFlag(String section7OfIgstFlag) {
		this.section7OfIgstFlag = section7OfIgstFlag;
	}

	public String getReverseCharge() {
		return reverseCharge;
	}

	public void setReverseCharge(String reverseCharge) {
		this.reverseCharge = reverseCharge;
	}

	public String getClaimRefundFlag() {
		return claimRefundFlag;
	}

	public void setClaimRefundFlag(String claimRefundFlag) {
		this.claimRefundFlag = claimRefundFlag;
	}

	public String getAutoPopToRefundFlag() {
		return autoPopToRefundFlag;
	}

	public void setAutoPopToRefundFlag(String autoPopToRefundFlag) {
		this.autoPopToRefundFlag = autoPopToRefundFlag;
	}

	public String geteWayBillNo() {
		return eWayBillNo;
	}

	public void seteWayBillNo(String eWayBillNo) {
		this.eWayBillNo = eWayBillNo;
	}

	public LocalDate geteWayBillDate() {
		return eWayBillDate;
	}

	public void seteWayBillDate(LocalDate eWayBillDate) {
		this.eWayBillDate = eWayBillDate;
	}

	public BigDecimal getTaxPayable() {
		return taxPayable;
	}

	public void setTaxPayable(BigDecimal taxPayable) {
		this.taxPayable = taxPayable;
	}

	public Integer getDerivedTaxperiod() {
		return derivedTaxperiod;
	}

	public void setDerivedTaxperiod(Integer derivedTaxperiod) {
		this.derivedTaxperiod = derivedTaxperiod;
	}

	/**
	 * @return the glCodeIgst
	 */
	public String getGlCodeIgst() {
		return glCodeIgst;
	}

	/**
	 * @param glCodeIgst
	 *            the glCodeIgst to set
	 */
	public void setGlCodeIgst(String glCodeIgst) {
		this.glCodeIgst = glCodeIgst;
	}

	/**
	 * @return the glCodeCgst
	 */
	public String getGlCodeCgst() {
		return glCodeCgst;
	}

	/**
	 * @param glCodeCgst
	 *            the glCodeCgst to set
	 */
	public void setGlCodeCgst(String glCodeCgst) {
		this.glCodeCgst = glCodeCgst;
	}

	/**
	 * @return the glCodeSgst
	 */
	public String getGlCodeSgst() {
		return glCodeSgst;
	}

	/**
	 * @param glCodeSgst
	 *            the glCodeSgst to set
	 */
	public void setGlCodeSgst(String glCodeSgst) {
		this.glCodeSgst = glCodeSgst;
	}

	/**
	 * @return the glCodeAdvCess
	 */
	public String getGlCodeAdvCess() {
		return glCodeAdvCess;
	}

	/**
	 * @param glCodeAdvCess
	 *            the glCodeAdvCess to set
	 */
	public void setGlCodeAdvCess(String glCodeAdvCess) {
		this.glCodeAdvCess = glCodeAdvCess;
	}

	/**
	 * @return the glCodeSpCess
	 */
	public String getGlCodeSpCess() {
		return glCodeSpCess;
	}

	/**
	 * @param glCodeSpCess
	 *            the glCodeSpCess to set
	 */
	public void setGlCodeSpCess(String glCodeSpCess) {
		this.glCodeSpCess = glCodeSpCess;
	}

	/**
	 * @return the glCodeStateCess
	 */
	public String getGlCodeStateCess() {
		return glCodeStateCess;
	}

	/**
	 * @param glCodeStateCess
	 *            the glCodeStateCess to set
	 */
	public void setGlCodeStateCess(String glCodeStateCess) {
		this.glCodeStateCess = glCodeStateCess;
	}

	@Override
	public String toString() {
		return String.format(
				"TransDocLineItem [id=%s, lineNo=%s, crDrReason=%s, hsnSac=%s, "
						+ "supplyType=%s, itemCode=%s, itemDescription=%s, "
						+ "itemCategory=%s, uom=%s, glCodeTaxableValue=%s, "
						+ "glCodeIgst=%s, glCodeCgst=%s, glCodeSgst=%s, "
						+ "glCodeAdvCess=%s, glCodeSpCess=%s, glCodeStateCess=%s, "
						+ "adjustmentRefNo=%s, adjustmentRefDate=%s, "
						+ "adjustedTaxableValue=%s, adjustedIgstAmt=%s, "
						+ "adjustedCgstAmt=%s, adjustedSgstAmt=%s, "
						+ "adjustedCessAmtAdvalorem=%s, adjustedCessAmtSpecific=%s, "
						+ "adjustedStateCessAmt=%s, tcsAmount=%s, qty=%s, "
						+ "lineItemAmt=%s, taxPayable=%s, location=%s, "
						+ "profitCentre=%s, plantCode=%s, origDocNo=%s, "
						+ "origDocDate=%s, userdefinedfield1=%s, "
						+ "userdefinedfield2=%s, userdefinedfield3=%s, "
						+ "userDefinedField4=%s, userDefinedField5=%s, "
						+ "userDefinedField6=%s, userDefinedField7=%s, "
						+ "userDefinedField8=%s, userDefinedField9=%s, "
						+ "userDefinedField10=%s, userDefinedField11=%s, "
						+ "userDefinedField12=%s, userDefinedField13=%s, "
						+ "userDefinedField14=%s, userDefinedField15=%s, "
						+ "userId=%s, sourceFileName=%s, division=%s, "
						+ "userAccess1=%s, userAccess2=%s, "
						+ "userAccess4=%s, userAccess5=%s, userAccess6=%s, "
						+ "taxperiod=%s, docDate=%s, crDrPreGst=%s, custOrSuppType=%s, "
						+ "diffPercent=%s, custOrSuppName=%s, custOrSuppCode=%s, "
						+ "custOrSuppAddress1=%s, custOrSuppAddress2=%s, "
						+ "custOrSuppAddress3=%s, custOrSuppAddress4=%s, pos=%s, "
						+ "stateApplyingCess=%s, portCode=%s, section7OfIgstFlag=%s, "
						+ "reverseCharge=%s, claimRefundFlag=%s, "
						+ "autoPopToRefundFlag=%s, eWayBillNo=%s, eWayBillDate=%s, "
						+ "derivedTaxperiod=%s]",
				lineNo, crDrReason, hsnSac, supplyType, itemCode,
				itemDescription, itemCategory, uom, glCodeTaxableValue,
				glCodeIgst, glCodeCgst, glCodeSgst, glCodeAdvCess, glCodeSpCess,
				glCodeStateCess, adjustmentRefNo, adjustmentRefDate,
				adjustedTaxableValue, adjustedIgstAmt, adjustedCgstAmt,
				adjustedSgstAmt, adjustedCessAmtAdvalorem,
				adjustedCessAmtSpecific, adjustedStateCessAmt, tcsAmount, qty,
				lineItemAmt, taxPayable, location, profitCentre, plantCode,
				origDocNo, origDocDate, userdefinedfield1, userdefinedfield2,
				userdefinedfield3, userDefinedField4, userDefinedField5,
				userDefinedField6, userDefinedField7, userDefinedField8,
				userDefinedField9, userDefinedField10, userDefinedField11,
				userDefinedField12, userDefinedField13, userDefinedField14,
				userDefinedField15, userId, sourceFileName, division,
				userAccess1, userAccess2, userAccess3, userAccess4, userAccess5,
				userAccess6, taxperiod, docDate, crDrPreGst, custOrSuppType,
				diffPercent, custOrSuppName, custOrSuppCode, custOrSuppAddress1,
				custOrSuppAddress2, custOrSuppAddress3, custOrSuppAddress4, pos,
				stateApplyingCess, portCode, section7OfIgstFlag, reverseCharge,
				claimRefundFlag, autoPopToRefundFlag, eWayBillNo, eWayBillDate,
				derivedTaxperiod);
	}

}
