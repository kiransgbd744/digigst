package com.ey.advisory.app.docs.dto;

import java.util.List;

/**
 * This class is responsible for providing Outward Doc Structural 
 * Error Documents Save Response
 * 
 * @author Mohana.Dasari
 *
 */
public class OutwardDocSvErrSaveRespDto {
	
	private Long id;

	private Long oldId;

	private String docType;

	private String docNo;

	private String supplierGstin;

	private String docDate;

	private List<DocErrorDto> errors;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the oldId
	 */
	public Long getOldId() {
		return oldId;
	}

	/**
	 * @param oldId the oldId to set
	 */
	public void setOldId(Long oldId) {
		this.oldId = oldId;
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
	 * @return the docNo
	 */
	public String getDocNo() {
		return docNo;
	}

	/**
	 * @param docNo the docNo to set
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
	 * @param supplierGstin the supplierGstin to set
	 */
	public void setSupplierGstin(String supplierGstin) {
		this.supplierGstin = supplierGstin;
	}

	/**
	 * @return the docDate
	 */
	public String getDocDate() {
		return docDate;
	}

	/**
	 * @param docDate the docDate to set
	 */
	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}

	/**
	 * @return the errors
	 */
	public List<DocErrorDto> getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List<DocErrorDto> errors) {
		this.errors = errors;
	}

}
