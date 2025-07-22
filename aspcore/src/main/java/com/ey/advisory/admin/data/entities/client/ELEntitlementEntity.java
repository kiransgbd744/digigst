package com.ey.advisory.admin.data.entities.client;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Umesha.M
 *
 */
@Entity
@Table(name = "EL_ENTITLEMENT")
public class ELEntitlementEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long elId;
	
	
	@Column(name = "GROUP_ID")
	private Long groupId;
	
	@Column(name="GROUP_CODE")
	private String groupCode;
	
	@Column(name="ENTITY_ID")
	private Long entityId;
	
	@Column(name="FUNCTIONALITY_ID")
	private Long functionalityId;

	@Column(name = "FUNCTIONALITY_CODE")
	private String functionalityCode;
	
	@Column(name="FROM_TAXPERIOD")
	private String fromTaxPeriod;
	
	@Column(name="TO_TAXPERIOD")
	private String toTaxPeriod;
	
	@Column(name = "EL_VALUE")
	private String elValue;
	
	@Column(name = "FROM_CONTRACT_PERIOD")
	private String contractStartPeriod;
	
	@Column(name = "TO_CONTRACT_PERIOD")
	private String contractEndPeriod;
	
	@Column(name="RENEWAL")
	private String renewal;

	@Column(name = "GFIS_ID")
	private String gfisId;
	
	@Column(name = "PACE_ID")
	private String paceId;
	
	@Column(name="IS_DELETE")
	private Boolean isDelete;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	/*@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;*/
	
	@Column(name = "CREATED_ON")
	@JsonSerialize
	private  LocalDateTime createdOn;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "MODIFIED_ON")
	private  LocalDate modifiedOn;
	
	/*@OneToMany(mappedBy = "bookCategory", cascade = CascadeType.ALL)
	private List<ELExtractFunEntity> functionality;*/
	
	
	/**
	 * @return the functionalityId
	 */
	public Long getFunctionalityId() {
		return functionalityId;
	}
	public String getPaceId() {
		return paceId;
	}
	public void setPaceId(String paceId) {
		this.paceId = paceId;
	}
	/**
	 * @return the groupId
	 */
	public Long getGroupId() {
		return groupId;
	}
	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	/**
	 * @return the functionalityCode
	 */
	public String getFunctionalityCode() {
		return functionalityCode;
	}
	/**
	 * @param functionalityCode the functionalityCode to set
	 */
	public void setFunctionalityCode(String functionalityCode) {
		this.functionalityCode = functionalityCode;
	}
	/**
	 * @param functionalityId the functionalityId to set
	 */
	public void setFunctionalityId(Long functionalityId) {
		this.functionalityId = functionalityId;
	}
	/*@OneToMany(mappedBy = "bookCategory", cascade = CascadeType.ALL)
	private List<Functionality> functionality;*/
	/**
	 * @return the elId
	 */
	public Long getElId() {
		return elId;
	}
	/**
	 * @param elId the elId to set
	 */
	public void setElId(Long elId) {
		this.elId = elId;
	}
	/**
	 * @return the groupCode
	 */
	public String getGroupCode() {
		return groupCode;
	}
	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	/**
	 * @return the entityId
	 */
	public Long getEntityId() {
		return entityId;
	}
	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	
	public String getGfisId() {
		return gfisId;
	}
	public void setGfisId(String gfisId) {
		this.gfisId = gfisId;
	}
	/**
	 * @return the renewal
	 */
	public String getRenewal() {
		return renewal;
	}
	/**
	 * @param renewal
	 */
	public void setRenewal(String renewal) {
		this.renewal = renewal;
	}
	
	/**
	 * @return the isDelete
	 */
	public Boolean getIsDelete() {
		return isDelete;
	}
	/**
	 * @param isDelete the isDelete to set
	 */
	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}
	/**
	 * @return the fromTaxPeriod
	 */
	public String getFromTaxPeriod() {
		return fromTaxPeriod;
	}
	/**
	 * @param fromTaxPeriod the fromTaxPeriod to set
	 */
	public void setFromTaxPeriod(String fromTaxPeriod) {
		this.fromTaxPeriod = fromTaxPeriod;
	}
	/**
	 * @return the toTaxPeriod
	 */
	public String getToTaxPeriod() {
		return toTaxPeriod;
	}
	/**
	 * @param toTaxPeriod the toTaxPeriod to set
	 */
	public void setToTaxPeriod(String toTaxPeriod) {
		this.toTaxPeriod = toTaxPeriod;
	}
	/**
	 * @return the elValue
	 */
	public String getElValue() {
		return elValue;
	}
	/**
	 * @param elValue the elValue to set
	 */
	public void setElValue(String elValue) {
		this.elValue = elValue;
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
	 * @return the createdOn
	 */
	public LocalDateTime getCreatedOn() {
		return createdOn;
	}
	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}
	
	/**
	 * @return the modifiedOn
	 */
	public LocalDate getModifiedOn() {
		return modifiedOn;
	}
	/**
	 * @param modifiedOn the modifiedOn to set
	 */
	public void setModifiedOn(LocalDate modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	/**
	 * @return the contractStartPeriod
	 */
	public String getContractStartPeriod() {
		return contractStartPeriod;
	}
	/**
	 * @param contractStartPeriod the contractStartPeriod to set
	 */
	public void setContractStartPeriod(String contractStartPeriod) {
		this.contractStartPeriod = contractStartPeriod;
	}
	/**
	 * @return the contractEndPeriod
	 */
	public String getContractEndPeriod() {
		return contractEndPeriod;
	}
	/**
	 * @param contractEndPeriod the contractEndPeriod to set
	 */
	public void setContractEndPeriod(String contractEndPeriod) {
		this.contractEndPeriod = contractEndPeriod;
	}
}
