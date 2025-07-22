package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;


@Entity
@Table(name = "ANX_OUTWARD_DOC_ERROR")
public class OutwardTransDocError {
	
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ANX_OUTWARD_DOC_ERROR_SEQ", 
	 allocationSize = 100)
    @GeneratedValue(generator = "sequence", strategy=GenerationType.SEQUENCE)
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@Column(name = "DOC_HEADER_ID")
	private Long docHeaderId;
	
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;
	
	@Column(name = "RETURN_PERIOD")
	protected String taxperiod;
	
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;
	
	@Column(name = "FILE_ID")
	protected Long acceptanceId;
	
	@Column(name = "ITEM_INDEX")
	private Integer itemIndex;
	
	@Column(name = "ITM_NO")
	private Integer itemNum;
	
	@Column(name = "ERROR_FIELD")
	private String errorField;
	
	/**
	 * Specifies whether the type is INFO or ERROR
	 */
	@Column(name = "ERROR_TYPE")
	private String errorType;
	
	/**
	 * Specifies whether the type is Business/Structural Validation errors
	 */
	@Column(name = "VAL_TYPE")
	private String type;
	
	/**
	 * Specifies whether the source is GSTN or LOCAL
	 */
	@Column(name = "ERROR_SOURCE")
	private String source;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;
	
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

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
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
	 * @param docHeaderId the docHeaderId to set
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
	 * @param itemIndex the itemIndex to set
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
	 * @param itemNum the itemNum to set
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
	 * @param errorField the errorField to set
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
	 * @param errorType the errorType to set
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
	 * @param source the source to set
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
	 * @param errorCode the errorCode to set
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
	 * @param errorDesc the errorDesc to set
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
	 * @param createdBy the createdBy to set
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
	 * @param createdDate the createdDate to set
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
	 * @param modifiedBy the modifiedBy to set
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
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public String getSgstin() {
		return sgstin;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public String getTaxperiod() {
		return taxperiod;
	}

	public void setTaxperiod(String taxperiod) {
		this.taxperiod = taxperiod;
	}

	public Integer getDerivedTaxperiod() {
		return derivedTaxperiod;
	}

	public void setDerivedTaxperiod(Integer derivedTaxperiod) {
		this.derivedTaxperiod = derivedTaxperiod;
	}

	public Long getAcceptanceId() {
		return acceptanceId;
	}

	public void setAcceptanceId(Long acceptanceId) {
		this.acceptanceId = acceptanceId;
	}

	@Override
	public String toString() {
		return "OutwardTransDocError [id=" + id + ", docHeaderId=" + docHeaderId
				+ ", sgstin=" + sgstin + ", taxperiod=" + taxperiod
				+ ", derivedTaxperiod=" + derivedTaxperiod + ", itemIndex="
				+ itemIndex + ", itemNum=" + itemNum + ", errorField="
				+ errorField + ", errorType=" + errorType + ", type=" + type
				+ ", source=" + source + ", errorCode=" + errorCode
				+ ", errorDesc=" + errorDesc + ", createdBy=" + createdBy
				+ ", createdDate=" + createdDate + ", modifiedBy=" + modifiedBy
				+ ", modifiedDate=" + modifiedDate + "]";
	}

}
