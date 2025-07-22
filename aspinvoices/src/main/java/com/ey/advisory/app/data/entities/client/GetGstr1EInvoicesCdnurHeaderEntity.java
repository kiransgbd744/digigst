package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * 
 * @author Anand3.M
 *
 */
@Entity
@Table(name = "GETGSTR1_EINV_CDNUR_HEADER")
public class GetGstr1EInvoicesCdnurHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR1_EINV_CDNUR_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Column(name = "TYPE")
	protected String type;

	@Column(name = "INV_CHKSUM")
	protected String invChkSum;

	@Column(name = "NOTE_TYPE")
	protected String noteType;

	@Column(name = "NOTE_NUM")
	protected String noteNum;

	@Column(name = "NOTE_DATE")
	protected String noteDate;

	@Column(name = "INV_VALUE")
	protected BigDecimal invValue = BigDecimal.ZERO;

	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxValue = BigDecimal.ZERO;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt = BigDecimal.ZERO;

	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt = BigDecimal.ZERO;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "DOC_KEY")
	protected String docKey;

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "POS")
	protected String pos;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "IRN_NUM")
	private String irnNum;

	@Column(name = "IRN_GEN_DATE")
	private String irnGenDate;

	@Column(name = "IRN_SOURCE_TYPE")
	private String irnSrcType;

	@Column(name = "EINV_STATUS")
	private String eInvStatus;

	@Column(name = "AUTODFT")
	private String autoDraftStatus;

	@Column(name = "AUTODFT_DATE")
	private String autoDraftDate;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_DESC")
	private String errorDesc;

	/*
	 * @OneToMany(mappedBy = "document", fetch = FetchType.EAGER, cascade =
	 * CascadeType.ALL)
	 */
	@OneToMany(mappedBy = "document")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@Access(AccessType.FIELD)
	protected List<GetGstr1EInvoicesCdnurItemEntity> lineItems = new ArrayList<>();

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInvChkSum() {
		return invChkSum;
	}

	public void setInvChkSum(String invChkSum) {
		this.invChkSum = invChkSum;
	}

	public String getNoteType() {
		return noteType;
	}

	public void setNoteType(String noteType) {
		this.noteType = noteType;
	}

	public String getNoteNum() {
		return noteNum;
	}

	public void setNoteNum(String noteNum) {
		this.noteNum = noteNum;
	}

	public String getNoteDate() {
		return noteDate;
	}

	public void setNoteDate(String noteDate) {
		this.noteDate = noteDate;
	}

	public BigDecimal getInvValue() {
		return invValue;
	}

	public void setInvValue(BigDecimal invValue) {
		this.invValue = invValue;
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

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
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

	public String getIrnNum() {
		return irnNum;
	}

	public void setIrnNum(String irnNum) {
		this.irnNum = irnNum;
	}

	public String getIrnGenDate() {
		return irnGenDate;
	}

	public void setIrnGenDate(String irnGenDate) {
		this.irnGenDate = irnGenDate;
	}

	public String getIrnSrcType() {
		return irnSrcType;
	}

	public void setIrnSrcType(String irnSrcType) {
		this.irnSrcType = irnSrcType;
	}

	public String geteInvStatus() {
		return eInvStatus;
	}

	public void seteInvStatus(String eInvStatus) {
		this.eInvStatus = eInvStatus;
	}

	public String getAutoDraftStatus() {
		return autoDraftStatus;
	}

	public void setAutoDraftStatus(String autoDraftStatus) {
		this.autoDraftStatus = autoDraftStatus;
	}

	public String getAutoDraftDate() {
		return autoDraftDate;
	}

	public void setAutoDraftDate(String autoDraftDate) {
		this.autoDraftDate = autoDraftDate;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public List<GetGstr1EInvoicesCdnurItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetGstr1EInvoicesCdnurItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

}
