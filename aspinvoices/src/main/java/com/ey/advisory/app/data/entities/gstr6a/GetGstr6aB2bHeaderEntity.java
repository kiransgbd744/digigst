package com.ey.advisory.app.data.entities.gstr6a;

import java.math.BigDecimal;
import java.time.LocalDate;
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
 * @author Anand3.M
 *
 */
@Entity
@Table(name = "GETGSTR6A_B2B_HEADER")
public class GetGstr6aB2bHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR6A_B2B_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "CTIN")
	protected String ctin;

	@Column(name = "CFS")
	private String cfs;

	@Column(name = "CFP")
	private String cfp;

	@Column(name = "CHKSUM")
	private String chksum;

	@Column(name = "DOC_NUMBER")
	private String docNum;

	@Column(name = "DOC_DATE")
	private LocalDate docDate;

	@Column(name = "DOC_AMT")
	private BigDecimal docAmt;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@Column(name = "REVERSE_CHARGE")
	private String reverseCharge;

	@Column(name = "POS")
	private String pos;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "DERIVED_RET_PERIOD")
	private int derTaxPeriod;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "IRN_NUM")
	private String irnNumber;
	
	@Column(name = "IRN_GEN_DATE")
	private LocalDate irnDate;
	
	@Column(name = "IRN_SOURCE_TYPE")
	private String irnSourceType;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "INV_KEY")
	private String invKey;
	
	@Column(name = "SENT_TO_ERP_DATE")
	protected LocalDate sentToErpDate;
	
	@Column(name = "IS_SENT_TO_ERP")
	protected boolean isSentToErp;
	
	@Column(name = "DELTA_INV_STATUS")
	protected String deltaInStatus;
	
	@Column(name = "ERP_BATCH_ID")
	protected Long erpBatchId;

	@Column(name = "SUPPLIER_INV_NUM")
	protected String suppInvNum;

	@Column(name = "SUPPLIER_INV_DATE")
	protected LocalDate suppInvDate;
	
	@Column(name = "SUPPLIER_INV_VAL")
	protected BigDecimal suppInvVal;

	@Column(name = "RCHRG")
	protected String rchrg;

	@Column(name = "INV_TYPE")
	protected String invType;

	@Column(name = "SUPPLIER_TRADE_LEGAL_NAME")
	protected String supTradeName;
		
	public String getInvKey() {
		return invKey;
	}

	public void setInvKey(String invKey) {
		this.invKey = invKey;
	}

	@OneToMany(mappedBy = "header")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<GetGstr6aB2bItemEntity> lineItems = new ArrayList<>();

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

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public String getCtin() {
		return ctin;
	}

	public void setCtin(String ctin) {
		this.ctin = ctin;
	}

	public String getCfs() {
		return cfs;
	}

	public void setCfs(String cfs) {
		this.cfs = cfs;
	}

	public String getCfp() {
		return cfp;
	}

	public void setCfp(String cfp) {
		this.cfp = cfp;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public String getDocNum() {
		return docNum;
	}

	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}

	public LocalDate getDocDate() {
		return docDate;
	}

	public void setDocDate(LocalDate docDate) {
		this.docDate = docDate;
	}

	public BigDecimal getDocAmt() {
		return docAmt;
	}

	public void setDocAmt(BigDecimal docAmt) {
		this.docAmt = docAmt;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
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

	public String getReverseCharge() {
		return reverseCharge;
	}

	public void setReverseCharge(String reverseCharge) {
		this.reverseCharge = reverseCharge;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public int getDerTaxPeriod() {
		return derTaxPeriod;
	}

	public void setDerTaxPeriod(int derTaxPeriod) {
		this.derTaxPeriod = derTaxPeriod;
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

	public List<GetGstr6aB2bItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetGstr6aB2bItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

	public String getIrnNumber() {
		return irnNumber;
	}

	public void setIrnNumber(String irnNumber) {
		this.irnNumber = irnNumber;
	}

	public String getIrnSourceType() {
		return irnSourceType;
	}

	public void setIrnSourceType(String irnSourceType) {
		this.irnSourceType = irnSourceType;
	}

	public LocalDate getIrnDate() {
		return irnDate;
	}

	public void setIrnDate(LocalDate irnDate) {
		this.irnDate = irnDate;
	}
	
	public LocalDate getSentToErpDate() {
		return sentToErpDate;
	}

	public void setSentToErpDate(LocalDate sentToErpDate) {
		this.sentToErpDate = sentToErpDate;
	}

	public boolean isSentToErp() {
		return isSentToErp;
	}

	public void setSentToErp(boolean isSentToErp) {
		this.isSentToErp = isSentToErp;
	}

	public String getDeltaInStatus() {
		return deltaInStatus;
	}

	public void setDeltaInStatus(String deltaInStatus) {
		this.deltaInStatus = deltaInStatus;
	}

	public Long getErpBatchId() {
		return erpBatchId;
	}

	public void setErpBatchId(Long erpBatchId) {
		this.erpBatchId = erpBatchId;
	}

	public String getSuppInvNum() {
		return suppInvNum;
	}

	public void setSuppInvNum(String suppInvNum) {
		this.suppInvNum = suppInvNum;
	}

	public LocalDate getSuppInvDate() {
		return suppInvDate;
	}

	public void setSuppInvDate(LocalDate suppInvDate) {
		this.suppInvDate = suppInvDate;
	}

	public BigDecimal getSuppInvVal() {
		return suppInvVal;
	}

	public void setSuppInvVal(BigDecimal suppInvVal) {
		this.suppInvVal = suppInvVal;
	}

	public String getRchrg() {
		return rchrg;
	}

	public void setRchrg(String rchrg) {
		this.rchrg = rchrg;
	}

	public String getInvType() {
		return invType;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	public String getSupTradeName() {
		return supTradeName;
	}

	public void setSupTradeName(String supTradeName) {
		this.supTradeName = supTradeName;
	}

	
	

}
