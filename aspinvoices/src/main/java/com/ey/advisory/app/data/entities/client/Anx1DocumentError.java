package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

/**
 * This class represents the base class of all Tax Related Documents (individual
 * or rolled up), that we gather from our clients for the purpose of processing
 * of taxes. It can be Financial Documents at transactional level, Financial
 * Documents at a rolled up level OR other informational/summary documents like
 * HSN summary, Document Series issued etc. Since we gather all our documents
 * for tax processing/filing purposes, we can assume that tax period is attached
 * to every document. Other than the tax period, it has a unique identifier and
 * a bulk request id (i.e. acceptance id). The acceptance can be used by the end
 * user to check the status of each document submitted in a batch.
 * 
 * @author Umesha M
 *
 */
@MappedSuperclass
public class Anx1DocumentError {

	/**
	 * An acceptance Id is required when a batch of documents are submitted
	 * together for processing. The batch can arrive as part of a file OR a REST
	 * API call etc. An acceptance Id is issued when we accept the batch and
	 * dump it for later processing. The user can come back with the acceptance
	 * id later to find out the procesing status of the documents included
	 * inabstract the batch.
	 */
	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long acceptanceId;

	/**
	 * The tax period for which the document has to be filed. This period can be
	 * different from the actual period on which the transaction for this
	 * document took place.
	 */

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String taxperiod;

	/**
	 * The created Date of this document
	 */
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdDate;

	/**
	 * The updated Date of this document.
	 */
	@Column(name = "MODIFIED_ON")
	protected LocalDateTime updatedDate;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;
	
	@Transient
	private Long docId;

	/**
	 * This is the date part of the createdDate. This is required because there
	 * are use cases where we need to search between two dates, without
	 * considering the time. So, either we need to have a 'Date only column' on
	 * we need to apply a function on the created date at the DB level to
	 * extract the date part. In the latter scenario, certain databases do not
	 * support function based indexes, which can lead to a full table scan at
	 * the DB level. Hence, adding a received date column. The value of this
	 * field should be set at the time of creation of this object and it should
	 * be exactly same as the date part of the creationDate.
	 */
	@Column(name = "RECEIVED_DATE")
	protected LocalDate receivedDate;

	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	/**
	 * Logical delete flag for the entity.
	 */
	@Expose
	@SerializedName("isDeleted")
	@Column(name = "IS_DELETE")
	protected String isDeleted;

	public String getTaxperiod() {
		return taxperiod;
	}

	public void setTaxperiod(String taxperiod) {
		this.taxperiod = taxperiod;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Long getAcceptanceId() {
		return acceptanceId;
	}

	public void setAcceptanceId(Long acceptanceId) {
		this.acceptanceId = acceptanceId;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
		this.receivedDate = (createdDate != null) ? 
				createdDate.toLocalDate() : null;
	}

	public Integer getDerivedTaxperiod() {
		return derivedTaxperiod;
	}

	public void setDerivedTaxperiod(Integer derivedTaxperiod) {
		this.derivedTaxperiod = derivedTaxperiod;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public LocalDate getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(LocalDate receivedDate) {
		this.receivedDate = receivedDate;
	}
	

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	@Override
	public String toString() {
		return "Document [acceptanceId=" + acceptanceId
				+ ", taxperiod=" + taxperiod + ", createdDate=" + createdDate
				+ ", updatedDate=" + updatedDate + ", createdBy=" + createdBy
				+ ", modifiedBy=" + modifiedBy + ", receivedDate="
				+ receivedDate + ", derivedTaxperiod=" + derivedTaxperiod
				+ ", isDeleted=" + isDeleted + "]";
	}
}
