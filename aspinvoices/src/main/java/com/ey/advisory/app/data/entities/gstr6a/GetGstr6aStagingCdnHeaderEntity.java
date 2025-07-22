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
 * @author  Arun.KA
 *
 */
@Entity
@Table(name = "GETGSTR6A_STAGING_CDN_HEADER")
public class GetGstr6aStagingCdnHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR6A_CDN_HEADER_SEQ", allocationSize = 100)
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

	@Column(name = "NOTE_TYPE")
	private String noteType;

	@Column(name = "NOTE_NUM")
	private String noteNum;

	@Column(name = "NOTE_DATE")
	private LocalDate noteDate;

	@Column(name = "DOC_NUMBER")
	private String docNum;

	@Column(name = "DOC_DATE")
	private LocalDate docDate;

	@Column(name = "POS")
	private String pos;

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

	@Column(name = "P_GST")
	private String pGst;

	@Column(name = "DERIVED_RET_PERIOD")
	private int derTaxPeriod;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "D_FLAG")
	private String dFlag;

	@Column(name = "INV_VALUE")
	private BigDecimal invValue;
	
	@Column(name = "IRN_NUM")
	private String irnNumber;
	
	@Column(name = "IRN_GEN_DATE")
	private LocalDate irnDate;
	
	@Column(name = "IRN_SOURCE_TYPE")
	private String irnSourceType;
	
	@Column(name = "INV_KEY")
	private String invKey;
	
	@Column(name = "INV_DATE")
	private LocalDate invDate;
	
	@Column(name = "SUPPLIER_INV_VAL")
	protected BigDecimal suppInvVal;

	@Column(name = "SUPPLIER_TRADE_LEGAL_NAME")
	protected String supTradeName;
	
	@Column(name = "NOTE_NUMBER")
	protected String noteNumber;
	
	@Column(name = "INV_TYPE")
	protected String invType;
	
	@Column(name = "SUPPLY_TYPE")
	protected String supType;
	
	public String getSupType() {
		return supType;
	}

	public void setSupType(String supType) {
		this.supType = supType;
	}

	public BigDecimal getSuppInvVal() {
		return suppInvVal;
	}

	public void setSuppInvVal(BigDecimal suppInvVal) {
		this.suppInvVal = suppInvVal;
	}

	public String getSupTradeName() {
		return supTradeName;
	}

	public void setSupTradeName(String supTradeName) {
		this.supTradeName = supTradeName;
	}

	public String getNoteNumber() {
		return noteNumber;
	}

	public void setNoteNumber(String noteNumber) {
		this.noteNumber = noteNumber;
	}

	public String getInvType() {
		return invType;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

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
	protected List<GetGstr6aStagingCdnItemEntity> lineItems = new ArrayList<>();

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

	public LocalDate getNoteDate() {
		return noteDate;
	}

	public void setNoteDate(LocalDate noteDate) {
		this.noteDate = noteDate;
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

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
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

	public String getpGst() {
		return pGst;
	}

	public void setpGst(String pGst) {
		this.pGst = pGst;
	}

	public int getDerTaxPeriod() {
		return derTaxPeriod;
	}

	public void setDerTaxPeriod(int derTaxPeriod) {
		this.derTaxPeriod = derTaxPeriod;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
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

	public String getdFlag() {
		return dFlag;
	}

	public void setdFlag(String dFlag) {
		this.dFlag = dFlag;
	}

	public BigDecimal getInvValue() {
		return invValue;
	}

	public void setInvValue(BigDecimal invValue) {
		this.invValue = invValue;
	}

	public List<GetGstr6aStagingCdnItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetGstr6aStagingCdnItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

	public String getIrnNumber() {
		return irnNumber;
	}

	public void setIrnNumber(String irnNumber) {
		this.irnNumber = irnNumber;
	}

	public LocalDate getIrnDate() {
		return irnDate;
	}

	public void setIrnDate(LocalDate irnDate) {
		this.irnDate = irnDate;
	}

	public String getIrnSourceType() {
		return irnSourceType;
	}

	public void setIrnSourceType(String irnSourceType) {
		this.irnSourceType = irnSourceType;
	}
	public LocalDate getInvDate() {
		return invDate;
	}

	public void setInvDate(LocalDate invDate) {
		this.invDate = invDate;
	}
	

}
