package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Entity
@Table(name = "GETGSTR8_URD_HEADER")
public class GetGstr8UrdHeaderEntity {
	
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR8_URD_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "GSTIN")
	protected String gstin;
	
	@Column(name = "RETURN_PERIOD")
	protected String returnPeriod;
	
	@Column(name = "FROM_TIME")
	protected String fromTime;
	
	@Column(name = "CHKSUM")
	private String chksum;

	@Column(name = "EID")
	protected String enrolmentId;
	
	@Column(name = "GROSS_SUPPLIES_MADE")
	private BigDecimal grossSupplierMade;

	@Column(name = "GROSS_SUPPLIES_RETURNED")
	private BigDecimal grossSuppliesReturned;

	@Column(name = "NET_AMOUNT")
	protected BigDecimal netAmount;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public String getEnrolmentId() {
		return enrolmentId;
	}

	public void setEnrolmentId(String enrolmentId) {
		this.enrolmentId = enrolmentId;
	}

	public BigDecimal getGrossSupplierMade() {
		return grossSupplierMade;
	}

	public void setGrossSupplierMade(BigDecimal grossSupplierMade) {
		this.grossSupplierMade = grossSupplierMade;
	}

	public BigDecimal getGrossSuppliesReturned() {
		return grossSuppliesReturned;
	}

	public void setGrossSuppliesReturned(BigDecimal grossSuppliesReturned) {
		this.grossSuppliesReturned = grossSuppliesReturned;
	}

	public BigDecimal getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(LocalDateTime modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
}
