package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2DocReqDto {
	
	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;
	
	@Expose
	@SerializedName("recipientGstin")
	private String recipientGstin;
	
	@Expose
	@SerializedName("docType")
	private String docType;
	
	@Expose
	@SerializedName("docNum")
	private String docNum;
	
	@Expose
	@SerializedName("docDate")
	private LocalDate docDate;
	
	@Expose
	@SerializedName("origDocNum")
	private String origDocNum;
	
	@Expose
	@SerializedName("origDocDate")
	private LocalDate origDocDate;
	
	@Expose
	@SerializedName("crdrPreGst")
	private String crdrPreGst;
	
	@Expose
	@SerializedName("supplierGstin")
	private String supplierGstin;
	
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
	@SerializedName("reverseCharge")
	private boolean reverseCharge;
	
	@Expose
	@SerializedName("purVoucherNum")
	private String purVoucherNum;
	
	@Expose
	@SerializedName("purVoucherDate")
	private LocalDate purVoucherDate;
	
	@Expose
	@SerializedName("items")
	private List<Gstr2DocItemDto> items;

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
	 * @return the origDocNum
	 */
	public String getOrigDocNum() {
		return origDocNum;
	}

	/**
	 * @param origDocNum the origDocNum to set
	 */
	public void setOrigDocNum(String origDocNum) {
		this.origDocNum = origDocNum;
	}

	/**
	 * @return the origDocDate
	 */
	public LocalDate getOrigDocDate() {
		return origDocDate;
	}

	/**
	 * @param origDocDate the origDocDate to set
	 */
	public void setOrigDocDate(LocalDate origDocDate) {
		this.origDocDate = origDocDate;
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
	 * @return the reverseCharge
	 */
	public boolean isReverseCharge() {
		return reverseCharge;
	}

	/**
	 * @param reverseCharge the reverseCharge to set
	 */
	public void setReverseCharge(boolean reverseCharge) {
		this.reverseCharge = reverseCharge;
	}

	/**
	 * @return the purVoucherNum
	 */
	public String getPurVoucherNum() {
		return purVoucherNum;
	}

	/**
	 * @param purVoucherNum the purVoucherNum to set
	 */
	public void setPurVoucherNum(String purVoucherNum) {
		this.purVoucherNum = purVoucherNum;
	}

	/**
	 * @return the purVoucherDate
	 */
	public LocalDate getPurVoucherDate() {
		return purVoucherDate;
	}

	/**
	 * @param purVoucherDate the purVoucherDate to set
	 */
	public void setPurVoucherDate(LocalDate purVoucherDate) {
		this.purVoucherDate = purVoucherDate;
	}

	/**
	 * @return the items
	 */
	public List<Gstr2DocItemDto> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<Gstr2DocItemDto> items) {
		this.items = items;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"Gstr2DocReqDto [returnPeriod=%s, recipientGstin=%s, "
				+ "docType=%s, docNum=%s, docDate=%s, origDocNum=%s, "
				+ "origDocDate=%s, crdrPreGst=%s, supplierGstin=%s, "
				+ "origSgstin=%s, supplierName=%s, supplierCode=%s, "
				+ "pos=%s, reverseCharge=%s, purVoucherNum=%s, "
				+ "purVoucherDate=%s, items=%s]",
				returnPeriod, recipientGstin, docType, docNum, docDate,
				origDocNum, origDocDate, crdrPreGst, supplierGstin, origSgstin,
				supplierName, supplierCode, pos, reverseCharge, purVoucherNum,
				purVoucherDate, items);
	}

}
