package com.ey.advisory.admin.data.entities.client;

import org.springframework.stereotype.Component;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "EL_EXTRACT")
@Component
public class ELExtractEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID'S", nullable = false)
	private Long elId;
	@Expose
	@Column(name="GROUP_CODE")
	private String groupCode;
	
	@Expose
	@Column(name="ENTITY_ID")
	private Long entityId;
	@Expose
	@Column(name="GSTIN")
	private String gstin;
	@Expose
	@Column(name="FUNCTIONALITY")
	private String functionality;
	@Expose
	@Column(name="START_PERIOD")
	private String contractStartPeriod;
	@Expose
	@Column(name="END_PERIOD")
	private String contractEndPeriod;
	@Expose
	@Column(name="RENEWAL")
	private String renewal;
	@Expose
	@Column(name="IS_DELETE")
	private Boolean isFlag;
	
	
	
	/*@OneToMany(mappedBy = "bookCategory", cascade = CascadeType.ALL)
	private List<ELExtractFunEntity> functionality;*/
	
	public String getFunctionality() {
		return functionality;
	}
	public void setFunctionality(String functionality) {
		this.functionality = functionality;
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
	/**
	 * @return the gstin
	 */
	public String getGstin() {
		return gstin;
	}
	/**
	 * @param gstin the gstin to set
	 */
	public void setGstin(String gstin) {
		this.gstin = gstin;
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
	/**
	 * @return the renewal
	 */
	public String getRenewal() {
		return renewal;
	}
	public void setRenewal(String renewal) {
		this.renewal = renewal;
	}
	
	
	/**
	 * @return the isFlag
	 */
	public Boolean getIsFlag() {
		return isFlag;
	}
	/**
	 * @param isFlag the isFlag to set
	 */
	public void setIsFlag(Boolean isFlag) {
		this.isFlag = isFlag;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ELExtractEntity [elId=" + elId + ", groupCode=" + groupCode
				+ ", entityId=" + entityId + ", gstin=" + gstin
				+ ", functionality=" + functionality + ", contractStartPeriod="
				+ contractStartPeriod + ", contractEndPeriod="
				+ contractEndPeriod + ", renewal=" + renewal + ", isFlag="
				+ isFlag + "]";
	}
	
	
	

}
