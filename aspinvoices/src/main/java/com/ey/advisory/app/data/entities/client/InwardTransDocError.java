package com.ey.advisory.app.data.entities.client;

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
 * @author Mohana.Dasari
 *
 */
@Entity
@Table(name = "ANX_INWARD_DOC_ERROR")
public class InwardTransDocError {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ANX_INWARD_DOC_ERROR_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "DOC_HEADER_ID")
	private Long docHeaderId;

	@Column(name = "ITEM_INDEX")
	private Integer itemIndex;

	@Column(name = "ERROR_FIELD")
	private String errorField;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	/**
	 * Specifies whether the type is INFO or ERROR
	 */
	@Column(name = "ERROR_TYPE")
	private String errorType;

	/**
	 * Specifies whether the source is GSTN or LOCAL
	 */
	@Column(name = "ERROR_SOURCE")
	private String source;

	@Column(name = "ERROR_DESCRIPTION")
	private String errorDesc;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdDate;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedDate;

	@Column(name = "ITM_NO")
	private Integer itemNum;

	@Column(name = "VAL_TYPE")
	private String valType;

	@Column(name = "RETURN_PERIOD")
	protected String taxperiod;

	@Column(name = "FILE_ID")
	protected Long acceptanceId;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Column(name = "CUST_GSTIN")
	protected String cgstin;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the docHeaderId
	 */
	public Long getDocHeaderId() {
		return docHeaderId;
	}

	/**
	 * @param docHeaderId
	 *            the docHeaderId to set
	 */
	public void setDocHeaderId(Long docHeaderId) {
		this.docHeaderId = docHeaderId;
	}

	/**
	 * @return the itemIndex
	 */
	public Integer getItemIndex() {
		return itemIndex;
	}

	/**
	 * @param itemIndex
	 *            the itemIndex to set
	 */
	public void setItemIndex(Integer itemIndex) {
		this.itemIndex = itemIndex;
	}

	/**
	 * @return the itemNum
	 */
	public Integer getItemNum() {
		return itemNum;
	}

	/**
	 * @param itemNum
	 *            the itemNum to set
	 */
	public void setItemNum(Integer itemNum) {
		this.itemNum = itemNum;
	}

	/**
	 * @return the errorField
	 */
	public String getErrorField() {
		return errorField;
	}

	/**
	 * @param errorField
	 *            the errorField to set
	 */
	public void setErrorField(String errorField) {
		this.errorField = errorField;
	}

	/**
	 * @return the errorType
	 */
	public String getErrorType() {
		return errorType;
	}

	/**
	 * @param errorType
	 *            the errorType to set
	 */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorDesc
	 */
	public String getErrorDesc() {
		return errorDesc;
	}

	/**
	 * @param errorDesc
	 *            the errorDesc to set
	 */
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy
	 *            the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedDate
	 */
	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate
	 *            the modifiedDate to set
	 */
	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the valType
	 */
	public String getValType() {
		return valType;
	}

	/**
	 * @param valType
	 *            the valType to set
	 */
	public void setValType(String valType) {
		this.valType = valType;
	}

	/**
	 * @return the taxperiod
	 */
	public String getTaxperiod() {
		return taxperiod;
	}

	/**
	 * @param taxperiod
	 *            the taxperiod to set
	 */
	public void setTaxperiod(String taxperiod) {
		this.taxperiod = taxperiod;
	}

	/**
	 * @return the acceptanceId
	 */
	public Long getAcceptanceId() {
		return acceptanceId;
	}

	/**
	 * @param acceptanceId
	 *            the acceptanceId to set
	 */
	public void setAcceptanceId(Long acceptanceId) {
		this.acceptanceId = acceptanceId;
	}

	/**
	 * @return the derivedTaxperiod
	 */
	public Integer getDerivedTaxperiod() {
		return derivedTaxperiod;
	}

	/**
	 * @param derivedTaxperiod
	 *            the derivedTaxperiod to set
	 */
	public void setDerivedTaxperiod(Integer derivedTaxperiod) {
		this.derivedTaxperiod = derivedTaxperiod;
	}

	/**
	 * @return the cgstin
	 */
	public String getCgstin() {
		return cgstin;
	}

	/**
	 * @param cgstin
	 *            the cgstin to set
	 */
	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
	}

	@Override
	public String toString() {
		return "InwardTransDocError [id=" + id + ", docHeaderId=" + docHeaderId
				+ ", itemIndex=" + itemIndex + ", errorField=" + errorField
				+ ", errorCode=" + errorCode + ", errorType=" + errorType
				+ ", source=" + source + ", errorDesc=" + errorDesc
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate
				+ ", modifiedBy=" + modifiedBy + ", modifiedDate="
				+ modifiedDate + ", itemNum=" + itemNum + ", valType=" + valType
				+ ", taxperiod=" + taxperiod + ", acceptanceId=" + acceptanceId
				+ ", derivedTaxperiod=" + derivedTaxperiod + ", cgstin="
				+ cgstin + "]";
	}
}
