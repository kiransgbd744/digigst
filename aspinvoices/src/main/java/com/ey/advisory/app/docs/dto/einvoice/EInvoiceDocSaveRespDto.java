/**
 * 
 */
package com.ey.advisory.app.docs.dto.einvoice;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Laxmi.Salukuti
 *
 *         This class is responsible for transferring document from java server
 *         to SCI or to UI
 */

public class EInvoiceDocSaveRespDto {

	private Long id;

	private Long oldId;

	private String docType;

	private String docNo;

	private String supplierGstin;

	private String accountVoucherNo;

	private LocalDate docDate;

	private String ewbNo;

	private List<EInvoiceDocErrorDto> errors;

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
	 * @return the supplierGstin
	 */
	public String getSupplierGstin() {
		return supplierGstin;
	}

	/**
	 * @param supplierGstin
	 *            the supplierGstin to set
	 */
	public void setSupplierGstin(String supplierGstin) {
		this.supplierGstin = supplierGstin;
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
	 * @return the oldId
	 */
	public Long getOldId() {
		return oldId;
	}

	/**
	 * @param oldId
	 *            the oldId to set
	 */
	public void setOldId(Long oldId) {
		this.oldId = oldId;
	}

	/**
	 * @return the errors
	 */
	public List<EInvoiceDocErrorDto> getErrors() {
		return errors;
	}

	/**
	 * @param errors
	 *            the errors to set
	 */
	public void setErrors(List<EInvoiceDocErrorDto> errors) {
		this.errors = errors;
	}

	public String getAccountVoucherNo() {
		return accountVoucherNo;
	}

	public void setAccountVoucherNo(String accountVoucherNo) {
		this.accountVoucherNo = accountVoucherNo;
	}

	public String getEwbNo() {
		return ewbNo;
	}

	public void setEwbNo(String ewbNo) {
		this.ewbNo = ewbNo;
	}

}
