/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Laxmi.Salukuti
 *
 */
public class InwardListingResponseDto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("docType")
	private String docType;

	@Expose
	@SerializedName("docNo")
	private String docNo;

	@Expose
	@SerializedName("docDate")
	private LocalDate docDate;

	@Expose
	@SerializedName("counterPartyGstin")
	private String counterPartyGstin;
	
	@Expose
	@SerializedName("supplierLegalName")
	private String supplierLegalName;

	@Expose
	@SerializedName("aspErrorCode")
	private String aspErrorCode;

	@Expose
	@SerializedName("aspErrorDesc")
	private String aspErrorDesc;

	@Expose
	@SerializedName("is240Format")
	private boolean is240Format;

	@Expose
	@SerializedName("docKey")
	private String docKey;

	@Expose
	@SerializedName("tableNumber")
	private String tableNumber;

	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("gstReturnStatus")
	private String gstReturnStatus;

	@Expose
	@SerializedName("gstnErrorCode")
	private String gstnErrorCode;

	@Expose
	@SerializedName("gstnErrorDesc")
	private String gstnErrorDesc;
	
	@Expose
	@SerializedName("accVoucherNo")
	private String accVoucherNo;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the gstin
	 */
	public String getGstin() {
		return gstin;
	}

	/**
	 * @param gstin
	 *            the gstin to set
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
	 * @param docType
	 *            the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * @return the docNo
	 */
	public String getDocNo() {
		return docNo;
	}

	/**
	 * @param docNo
	 *            the docNo to set
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	/**
	 * @return the docDate
	 */
	public LocalDate getDocDate() {
		return docDate;
	}

	/**
	 * @param docDate
	 *            the docDate to set
	 */
	public void setDocDate(LocalDate docDate) {
		this.docDate = docDate;
	}

	/**
	 * @return the counterPartyGstin
	 */
	public String getCounterPartyGstin() {
		return counterPartyGstin;
	}

	/**
	 * @param counterPartyGstin
	 *            the counterPartyGstin to set
	 */
	public void setCounterPartyGstin(String counterPartyGstin) {
		this.counterPartyGstin = counterPartyGstin;
	}

	/**
	 * @return the aspErrorCode
	 */
	public String getAspErrorCode() {
		return aspErrorCode;
	}

	/**
	 * @param aspErrorCode
	 *            the aspErrorCode to set
	 */
	public void setAspErrorCode(String aspErrorCode) {
		this.aspErrorCode = aspErrorCode;
	}

	/**
	 * @return the aspErrorDesc
	 */
	public String getAspErrorDesc() {
		return aspErrorDesc;
	}

	/**
	 * @param aspErrorDesc
	 *            the aspErrorDesc to set
	 */
	public void setAspErrorDesc(String aspErrorDesc) {
		this.aspErrorDesc = aspErrorDesc;
	}

	/**
	 * @return the is240Format
	 */
	public boolean isIs240Format() {
		return is240Format;
	}

	/**
	 * @param is240Format
	 *            the is240Format to set
	 */
	public void setIs240Format(boolean is240Format) {
		this.is240Format = is240Format;
	}

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

	public String getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getGstReturnStatus() {
		return gstReturnStatus;
	}

	public void setGstReturnStatus(String gstReturnStatus) {
		this.gstReturnStatus = gstReturnStatus;
	}

	public String getGstnErrorCode() {
		return gstnErrorCode;
	}

	public void setGstnErrorCode(String gstnErrorCode) {
		this.gstnErrorCode = gstnErrorCode;
	}

	public String getGstnErrorDesc() {
		return gstnErrorDesc;
	}

	public void setGstnErrorDesc(String gstnErrorDesc) {
		this.gstnErrorDesc = gstnErrorDesc;
	}

	public String getAccVoucherNo() {
		return accVoucherNo;
	}

	public void setAccVoucherNo(String accVoucherNo) {
		this.accVoucherNo = accVoucherNo;
	}

	public String getSupplierLegalName() {
		return supplierLegalName;
	}

	public void setSupplierLegalName(String supplierLegalName) {
		this.supplierLegalName = supplierLegalName;
	}

}
