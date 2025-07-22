package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Entity
@Table(name = "GETGSTR8_TCSA_HEADER")
public class GetGstr8TcsaHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR8_TCSA_HEADER_SEQ", allocationSize = 100)
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

	@Column(name = "STIN")
	protected String stin;

	@Column(name = "ORIGINAL_SUPPLIER_GSTIN")
	protected String originalSupplierGstin;

	@Column(name = "ORIGINAL_RETURN_PERIOD")
	protected String originalReturnPeriod;

	@Column(name = "SUPPLIES_MADE_REGISTERED")
	private BigDecimal suppliesMadeRegistered;

	@Column(name = "SUPPLIES_RETURNED_REGISTERED")
	private BigDecimal suppliesReturnedRegistered;

	@Column(name = "SUPPLIES_MADE_UNREGISTERED")
	private BigDecimal suppliesMadeUnRegistered;

	@Column(name = "SUPPLIES_RETURNED_UNREGISTERED")
	private BigDecimal suppliesReturnedUnRegistered;

	@Column(name = "NET_AMOUNT")
	protected BigDecimal netAmount;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Column(name = "SUPPLIER_GSTIN_NAME")
	private String supplierGstinName;

	@Column(name = "SOURCE")
	private String source;
	
	@Column(name = "ACTION")
	private String action;

	@Column(name = "ORIGINAL_SUPPLIER_GSTIN_NAME")
	private String originalSupplierGstinName;

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

	public String getStin() {
		return stin;
	}

	public void setStin(String stin) {
		this.stin = stin;
	}

	public BigDecimal getSuppliesMadeRegistered() {
		return suppliesMadeRegistered;
	}

	public void setSuppliesMadeRegistered(BigDecimal suppliesMadeRegistered) {
		this.suppliesMadeRegistered = suppliesMadeRegistered;
	}

	public BigDecimal getSuppliesReturnedRegistered() {
		return suppliesReturnedRegistered;
	}

	public void setSuppliesReturnedRegistered(
			BigDecimal suppliesReturnedRegistered) {
		this.suppliesReturnedRegistered = suppliesReturnedRegistered;
	}

	public BigDecimal getSuppliesMadeUnRegistered() {
		return suppliesMadeUnRegistered;
	}

	public void setSuppliesMadeUnRegistered(BigDecimal suppliesMadeUnRegistered) {
		this.suppliesMadeUnRegistered = suppliesMadeUnRegistered;
	}

	public BigDecimal getSuppliesReturnedUnRegistered() {
		return suppliesReturnedUnRegistered;
	}

	public void setSuppliesReturnedUnRegistered(
			BigDecimal suppliesReturnedUnRegistered) {
		this.suppliesReturnedUnRegistered = suppliesReturnedUnRegistered;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public BigDecimal getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}

	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}

	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}

	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = cgstAmt;
	}

	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}

	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
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

	public String getSupplierGstinName() {
		return supplierGstinName;
	}

	public void setSupplierGstinName(String supplierGstinName) {
		this.supplierGstinName = supplierGstinName;
	}

	public String getOriginalSupplierGstin() {
		return originalSupplierGstin;
	}

	public void setOriginalSupplierGstin(String originalSupplierGstin) {
		this.originalSupplierGstin = originalSupplierGstin;
	}

	public String getOriginalReturnPeriod() {
		return originalReturnPeriod;
	}

	public void setOriginalReturnPeriod(String originalReturnPeriod) {
		this.originalReturnPeriod = originalReturnPeriod;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getOriginalSupplierGstinName() {
		return originalSupplierGstinName;
	}

	public void setOriginalSupplierGstinName(String originalSupplierGstinName) {
		this.originalSupplierGstinName = originalSupplierGstinName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
