package com.ey.advisory.app.docs.dto.gstr2;

import java.util.List;

import com.ey.advisory.app.docs.dto.InwardDocErrorDto;

/**
 * This class is responsible for providing response of inward document
 * structural error records save
 * 
 * @author Mohana.Dasari
 *
 */
public class InwardDocSvErrSaveRespDto {

	private Long id;

	private Long oldId;

	private String docType;

	private String docNo;

	private String supplierGstin;

	private String custGstin;

	private String docDate;

	private List<InwardDocErrorDto> errors;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOldId() {
		return oldId;
	}

	public void setOldId(Long oldId) {
		this.oldId = oldId;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public String getSupplierGstin() {
		return supplierGstin;
	}

	public void setSupplierGstin(String supplierGstin) {
		this.supplierGstin = supplierGstin;
	}

	public String getCustGstin() {
		return custGstin;
	}

	public void setCustGstin(String custGstin) {
		this.custGstin = custGstin;
	}

	public String getDocDate() {
		return docDate;
	}

	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}

	public List<InwardDocErrorDto> getErrors() {
		return errors;
	}

	public void setErrors(List<InwardDocErrorDto> errors) {
		this.errors = errors;
	}
}
