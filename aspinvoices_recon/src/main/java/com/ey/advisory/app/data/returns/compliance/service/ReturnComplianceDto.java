/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

/**
 * @author Sujith.Nanga
 *
 */
public class ReturnComplianceDto {
	
	private String gStin;
	private String entityName;
	private String registrationType;
	private String stateCode;
	private String stateName;
	private String taxPeriod;
	private String returnType;
	private String fillingSubDate;
	private String fillingSubTime;
	private String arnNo;
	private String status;
	/**
	 * @return the gStin
	 */
	public String getgStin() {
		return gStin;
	}
	/**
	 * @param gStin the gStin to set
	 */
	public void setgStin(String gStin) {
		this.gStin = gStin;
	}
	/**
	 * @return the registrationType
	 */
	public String getRegistrationType() {
		return registrationType;
	}
	/**
	 * @param registrationType the registrationType to set
	 */
	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}
	/**
	 * @return the stateName
	 */
	public String getStateName() {
		return stateName;
	}
	/**
	 * @param stateName the stateName to set
	 */
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	/**
	 * @return the taxPeriod
	 */
	public String getTaxPeriod() {
		return taxPeriod;
	}
	/**
	 * @param taxPeriod the taxPeriod to set
	 */
	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}
	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}
	/**
	 * @param returnType the returnType to set
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	/**
	 * @return the fillingSubDate
	 */
	public String getFillingSubDate() {
		return fillingSubDate;
	}
	/**
	 * @param fillingSubDate the fillingSubDate to set
	 */
	public void setFillingSubDate(String fillingSubDate) {
		this.fillingSubDate = fillingSubDate;
	}
	/**
	 * @return the fillingSubTime
	 */
	public String getFillingSubTime() {
		return fillingSubTime;
	}
	/**
	 * @param fillingSubTime the fillingSubTime to set
	 */
	public void setFillingSubTime(String fillingSubTime) {
		this.fillingSubTime = fillingSubTime;
	}
	/**
	 * @return the arnNo
	 */
	public String getArnNo() {
		return arnNo;
	}
	/**
	 * @param arnNo the arnNo to set
	 */
	public void setArnNo(String arnNo) {
		this.arnNo = arnNo;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	
	/**
	 * @return the stateCode
	 */
	public String getStateCode() {
		return stateCode;
	}
	/**
	 * @param stateCode the stateCode to set
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	@Override
	public String toString() {
		return "ReturnComplianceDto [gStin=" + gStin + ", entityName="
				+ entityName + ", registrationType=" + registrationType
				+ ", stateCode=" + stateCode + ", stateName=" + stateName
				+ ", taxPeriod=" + taxPeriod + ", returnType=" + returnType
				+ ", fillingSubDate=" + fillingSubDate + ", fillingSubTime="
				+ fillingSubTime + ", arnNo=" + arnNo + ", status=" + status
				+ "]";
	}
	
	
	

}
