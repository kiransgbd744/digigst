package com.ey.advisory.app.data.gstr1A.entities.client;

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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Anand3.M
 *
 */

@Entity
@Table(name = "GETGSTR1A_B2CSA_HEADER")
public class GetGstr1AB2csaHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR1A_B2CSA_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("batchId")
	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Expose
	@SerializedName("ret_period")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Expose
	@SerializedName("etin")
	@Column(name = "ETIN")
	protected String etin;

	@Expose
	@SerializedName("ecomType")
	@Column(name = "ECOM_TYPE")
	protected String ecomType;

	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	protected String pos;

	@Expose
	@SerializedName("orgPos")
	@Column(name = "ORG_POS")
	protected String orgPos;

	@Expose
	@SerializedName("orgInvMonth")
	@Column(name = "ORG_INV_MONTH")
	protected String orgInvMonth;

	@Expose
	@SerializedName("suppType")
	@Column(name = "SUPPLY_TYPE")
	protected String suppType;

	@Expose
	@SerializedName("diff_percent")
	@Column(name = "DIFF_PERCENT")
	protected BigDecimal diffPercent;

	@Expose
	@SerializedName("taxValue")
	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxValue;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Expose
	@SerializedName("cessAmt")
	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Expose
	@SerializedName("docKey")
	@Column(name = "DOC_KEY")
	protected String docKey;

	@Expose
	@SerializedName("gstin")
	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@OneToMany(mappedBy = "document")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<GetGstr1AB2csaItemEntity> lineItems = new ArrayList<>();

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

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public Integer getDerivedTaxperiod() {
		return derivedTaxperiod;
	}

	public void setDerivedTaxperiod(Integer derivedTaxperiod) {
		this.derivedTaxperiod = derivedTaxperiod;
	}

	public String getEtin() {
		return etin;
	}

	public void setEtin(String etin) {
		this.etin = etin;
	}

	public String getEcomType() {
		return ecomType;
	}

	public void setEcomType(String ecomType) {
		this.ecomType = ecomType;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getOrgPos() {
		return orgPos;
	}

	public void setOrgPos(String orgPos) {
		this.orgPos = orgPos;
	}

	public String getOrgInvMonth() {
		return orgInvMonth;
	}

	public void setOrgInvMonth(String orgInvMonth) {
		this.orgInvMonth = orgInvMonth;
	}

	public String getSuppType() {
		return suppType;
	}

	public void setSuppType(String suppType) {
		this.suppType = suppType;
	}

	public BigDecimal getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(BigDecimal diffPercent) {
		this.diffPercent = diffPercent;
	}

	public BigDecimal getTaxValue() {
		return taxValue;
	}

	public void setTaxValue(BigDecimal taxValue) {
		this.taxValue = taxValue;
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

	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
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

	public List<GetGstr1AB2csaItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetGstr1AB2csaItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

}
