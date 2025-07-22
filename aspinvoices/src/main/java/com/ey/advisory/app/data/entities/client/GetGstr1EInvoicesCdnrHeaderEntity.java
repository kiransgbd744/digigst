package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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

/**
 * 
 * @author Anand3.M
 *
 */
@Entity
@Table(name = "GETGSTR1_EINV_CDNR_HEADER")
public class GetGstr1EInvoicesCdnrHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR1_EINV_CDNR_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Column(name = "FROM_TIME")
	protected LocalDate fromTime;

	@Column(name = "CTIN")
	protected String ctin;

	@Column(name = "TRADE_NAME")
	private String tradeName;

	@Column(name = "INV_CHKSUM")
	protected String chksum;

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

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt = BigDecimal.ZERO;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt = BigDecimal.ZERO;

	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt = BigDecimal.ZERO;

	@Column(name = "TOKEN")
	protected String token;

	@Column(name = "ESTIMATED_TIME")
	protected String estimatedTime;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "DOC_KEY")
	protected String docKey;

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "POS")
	protected String pos;

	@Column(name = "RCHRG")
	protected String revCharge;

	@Column(name = "INV_TYPE")
	protected String invType;

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
	protected List<GetGstr1EInvoicesCdnrItemEntity> lineItems = new ArrayList<>();

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

	public LocalDate getFromTime() {
		return fromTime;
	}

	public void setFromTime(LocalDate fromTime) {
		this.fromTime = fromTime;
	}

	public String getCtin() {
		return ctin;
	}

	public void setCtin(String ctin) {
		this.ctin = ctin;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(String estimatedTime) {
		this.estimatedTime = estimatedTime;
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

	public String getRevCharge() {
		return revCharge;
	}

	public void setRevCharge(String revCharge) {
		this.revCharge = revCharge;
	}

	public String getInvType() {
		return invType;
	}

	public void setInvType(String invType) {
		this.invType = invType;
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

	public List<GetGstr1EInvoicesCdnrItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetGstr1EInvoicesCdnrItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

}
