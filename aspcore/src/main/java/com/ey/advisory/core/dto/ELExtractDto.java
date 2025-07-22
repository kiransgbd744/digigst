package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Umesha.M
 *
 */
public class ELExtractDto {

	

	/**
	 * 
	 */
	public ELExtractDto() {
		// TODO Auto-generated constructor stub
	}

	@Expose
	@SerializedName("elId")
	private Long elId;
	@Expose
	@SerializedName("groupCode")
	private String groupCode;
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	@Expose
	@SerializedName("gstin")
	private String gstin;
	@Expose
	@SerializedName("functionality")
	private String functionality;
	@Expose
	@SerializedName("contractStartPeriod")
	private String contractStartPeriod;
	@Expose
	@SerializedName("contractEndPeriod")
	private String contractEndPeriod;
	@Expose
	@SerializedName("renewal")
	private String renewal;

	
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
	 * @return the functionality
	 */
	public String getFunctionality() {
		return functionality;
	}


	/**
	 * @param functionality the functionality to set
	 */
	public void setFunctionality(String functionality) {
		this.functionality = functionality;
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


	/**
	 * @param renewal the renewal to set
	 */
	public void setRenewal(String renewal) {
		this.renewal = renewal;
	}


	@Override
	public String toString() {
		return "ELExtractDto [elId=" + elId + ", groupCode=" + groupCode
				+ ", entityId=" + entityId + ", gstin=" + gstin
				+ ", functionality=" + functionality + ", contractStartPeriod="
				+ contractStartPeriod + ", contractEndPeriod="
				+ contractEndPeriod + ", renewal=" + renewal + "]";
	}

	
}
