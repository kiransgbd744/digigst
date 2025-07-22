package com.ey.advisory.gstr2.userdetails;

import lombok.Data;

@Data
public class GstinDto {
	
	private Long entityId;
	private String gstin;
	private String regType;
	private String status;
	private String erpStatus;
	private String initiatedOn;
	private String completedOn;
	private String stateName;

	
	
	public GstinDto() {}
	
	public GstinDto(String gstin) {
		super();
		this.gstin = gstin;
	}
	
	public GstinDto(String gstin, String status, 
			String initiatedOn, String completedOn, String erpStatus) {
		super();
		this.gstin = gstin;
		this.status = status;
		this.initiatedOn = initiatedOn;
		this.completedOn = completedOn;
		this.erpStatus = erpStatus;
		this.stateName = stateName;

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
	
	public String getRegType() {
		return regType;
	}

	public void setRegType(String regType) {
		this.regType = regType;
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
	


	
}
