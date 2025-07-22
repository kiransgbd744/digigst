package com.ey.advisory.app.data.gstr1A.entities.client;

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
@Table(name = "GETGSTR1A_CDNURA_HEADER")
public class GetGstr1ACdnuraHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR1A_CDNURA_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Column(name = "FLAG")
	protected String flag;

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

	@Column(name = "ORG_NOTE_NUM")
	protected String orgNoteNum;

	@Column(name = "ORG_NOTE_DATE")
	protected LocalDate orgNoteDate;

	@Column(name = "PRE_GST")
	protected String preGst;

	@Column(name = "INV_NUM")
	protected String invNum;

	@Column(name = "INV_DATE")
	private String invDate;

	@Column(name = "INV_VALUE")
	protected BigDecimal invValue = BigDecimal.ZERO;

	@Column(name = "DIFF_PERCENT")
	protected BigDecimal diffPercent = BigDecimal.ZERO;

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

	@Column(name = "D_FLAG")
	protected String dFlag;

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
	protected List<GetGstr1ACdnuraItemEntity> lineItems = new ArrayList<>();

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

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
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

	public String getOrgNoteNum() {
		return orgNoteNum;
	}

	public void setOrgNoteNum(String orgNoteNum) {
		this.orgNoteNum = orgNoteNum;
	}

	public String getNoteDate() {
		return noteDate;
	}

	public void setNoteDate(String noteDate) {
		this.noteDate = noteDate;
	}

	public LocalDate getOrgNoteDate() {
		return orgNoteDate;
	}

	public void setOrgNoteDate(LocalDate orgNoteDate) {
		this.orgNoteDate = orgNoteDate;
	}

	public String getPreGst() {
		return preGst;
	}

	public void setPreGst(String preGst) {
		this.preGst = preGst;
	}

	public String getInvNum() {
		return invNum;
	}

	public void setInvNum(String invNum) {
		this.invNum = invNum;
	}

	public String getInvDate() {
		return invDate;
	}

	public void setInvDate(String invDate) {
		this.invDate = invDate;
	}

	public BigDecimal getInvValue() {
		return invValue;
	}

	public void setInvValue(BigDecimal invValue) {
		this.invValue = invValue;
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

	public String getdFlag() {
		return dFlag;
	}

	public void setdFlag(String dFlag) {
		this.dFlag = dFlag;
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

	public List<GetGstr1ACdnuraItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetGstr1ACdnuraItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

}
