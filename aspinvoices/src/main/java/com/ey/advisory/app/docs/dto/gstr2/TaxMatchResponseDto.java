package com.ey.advisory.app.docs.dto.gstr2;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaxMatchResponseDto {
	
	@Expose
	@SerializedName("sNo")
	private Long sNo;
	@Expose
	@SerializedName("entityName")
	private String entityName;
	@Expose
	@SerializedName("searchDocNo")
	private String searchDocNo;
	@Expose
	@SerializedName("docDate")
	private LocalDate docDate;
	@Expose
	@SerializedName("supplier")
	private String supplier;
	@Expose
	@SerializedName("invoiceNum")
	private String invoiceNum;
	/**
	 * @return the sNo
	 */
	public Long getsNo() {
		return sNo;
	}
	/**
	 * @param sNo the sNo to set
	 */
	public void setsNo(Long sNo) {
		this.sNo = sNo;
	}
	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}
	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	/**
	 * @return the searchDocNo
	 */
	public String getSearchDocNo() {
		return searchDocNo;
	}
	/**
	 * @param searchDocNo the searchDocNo to set
	 */
	public void setSearchDocNo(String searchDocNo) {
		this.searchDocNo = searchDocNo;
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
	 * @return the supplier
	 */
	public String getSupplier() {
		return supplier;
	}
	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	/**
	 * @return the invoiceNum
	 */
	public String getInvoiceNum() {
		return invoiceNum;
	}
	/**
	 * @param invoiceNum the invoiceNum to set
	 */
	public void setInvoiceNum(String invoiceNum) {
		this.invoiceNum = invoiceNum;
	}
	
	
	

}
