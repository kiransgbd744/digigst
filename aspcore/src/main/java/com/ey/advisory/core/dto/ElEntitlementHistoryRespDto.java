package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Umesha.M
 *
 */
public class ElEntitlementHistoryRespDto {

	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("groupCode")
	private String groupCode;
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("entityName")
	private String entityName;
	
	@Expose
	@SerializedName("pan")
	private String pan;
	
	@Expose
	@SerializedName("gstinList")
	private List<String> gstinList;
	
	@Expose
	@SerializedName("functionality")
	private List<String> functionality;
	
	@Expose
	@SerializedName("fromTaxPeriod")
	private String fromTaxPeriod;
	
	@Expose
	@SerializedName("toTaxPeriod")
	private String toTaxPeriod;

	@Expose
	@SerializedName("elValue")
	private String elValue;
	
	@Expose
	@SerializedName("contractStartPeriod")
	private String contractStartPeriod;
	
	@Expose
	@SerializedName("contractEndPeriod")
	private String contractEndPeriod;
	
	@Expose
	@SerializedName("renewal")
	private String renewal;
	
	@Expose
	@SerializedName("updateDate")
	private String updateDate;
	
	@Expose
	@SerializedName("gfsId")
	private Long gfsId;
	
	
	

	/**
	 * @return the updateDate
	 */
	public String getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

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
	 * @return the pan
	 */
	public String getPan() {
		return pan;
	}

	/**
	 * @param pan the pan to set
	 */
	public void setPan(String pan) {
		this.pan = pan;
	}

	/**
	 * @return the gstinList
	 */
	public List<String> getGstinList() {
		return gstinList;
	}

	/**
	 * @param gstinList the gstinList to set
	 */
	public void setGstinList(List<String> gstinList) {
		this.gstinList = gstinList;
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
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * @return the functionality
	 */
	public List<String> getFunctionality() {
		return functionality;
	}

	/**
	 * @param functionality the functionality to set
	 */
	public void setFunctionality(List<String> functionality) {
		this.functionality = functionality;
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

	/**
	 * @return the gfsId
	 */
	public Long getGfsId() {
		return gfsId;
	}

	/**
	 * @param gfsId the gfsId to set
	 */
	public void setGfsId(Long gfsId) {
		this.gfsId = gfsId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ElEntitlementHistoryRespDto [id=" + id + ", groupCode="
				+ groupCode + ", entityId=" + entityId + ", entityName="
				+ entityName + ", pan=" + pan + ", gstinList=" + gstinList
				+ ", functionality=" + functionality + ", fromTaxPeriod="
				+ fromTaxPeriod + ", toTaxPeriod=" + toTaxPeriod + ", elValue="
				+ elValue + ", contractStartPeriod=" + contractStartPeriod
				+ ", contractEndPeriod=" + contractEndPeriod + ", renewal="
				+ renewal + ", updateDate=" + updateDate + ", gfsId=" + gfsId
				+ "]";
	}
}
