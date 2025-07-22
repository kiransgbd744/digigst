package com.ey.advisory.app.data.business.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
import com.ey.advisory.common.EwbLocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.MappedSuperclass;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
 * @author Sai.Pakanati
 *
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public class Document {

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
	@XmlElement(name = "file-id")
	protected Long acceptanceId;

	/**
	 * The tax period for which the document has to be filed. This period can be
	 * different from the actual period on which the transaction for this
	 * document took place.
	 */

	@Expose
	@SerializedName("returnPeriod")
	@XmlElement(name = "tx-period")
	protected String taxperiod;

	/**
	 * The created Date of this document
	 */
	@XmlElement(name = "created-on")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime createdDate;

	/**
	 * The updated Date of this document.
	 */
	@XmlElement(name = "modfid-on")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime updatedDate;

	@XmlElement(name = "cretd-by")
	protected String createdBy;

	@XmlElement(name = "modfied-by")
	protected String modifiedBy;

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
	@XmlElement(name = "rec-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate receivedDate;

	@Expose
	@SerializedName("derivedTaxperiod")
	@XmlElement(name = "dervd-tx-prd")
	protected Integer derivedTaxperiod;

	/**
	 * Logical delete flag for the entity.
	 */
	@Expose
	@SerializedName("isDeleted")
	@XmlElement(name = "is-delete")
	protected boolean isDeleted;


	public Long getAcceptanceId() {
		return acceptanceId;
	}

	public void setAcceptanceId(Long acceptanceId) {
		this.acceptanceId = acceptanceId;
	}

	public String getTaxperiod() {
		return taxperiod;
	}

	public void setTaxperiod(String taxperiod) {
		this.taxperiod = taxperiod;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
		this.receivedDate = (createdDate != null) ? createdDate.toLocalDate()
				: null;
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

	public LocalDate getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(LocalDate receivedDate) {
		this.receivedDate = receivedDate;
	}
	public Integer getDerivedTaxperiod() {
		return derivedTaxperiod;
	}

	public void setDerivedTaxperiod(Integer derivedTaxperiod) {
		this.derivedTaxperiod = derivedTaxperiod;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public String toString() {
		return "Document [id="  + ", acceptanceId=" + acceptanceId
				+ ", taxperiod=" + taxperiod + ", createdDate=" + createdDate
				+ ", updatedDate=" + updatedDate + ", createdBy=" + createdBy
				+ ", modifiedBy=" + modifiedBy + ", receivedDate="
				+ receivedDate + ", derivedTaxperiod=" + derivedTaxperiod
				+ ", isDeleted=" + isDeleted +"]";
	}

}
