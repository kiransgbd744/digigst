package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Umesha.M
 *
 */
public class ELEntitlementFunctionalityDto {

	@Expose
	@SerializedName("funcId")
	private Long funcId;
	
	@Expose
	@SerializedName("functionalityCode")
	private String functCode;
	
	@Expose
	@SerializedName("funcDesc")
	private String funcDesc;

	/**
	 * @return the funcId
	 */
	public Long getFuncId() {
		return funcId;
	}

	/**
	 * @param funcId the funcId to set
	 */
	public void setFuncId(Long funcId) {
		this.funcId = funcId;
	}

	/**
	 * @return the funcDesc
	 */
	public String getFuncDesc() {
		return funcDesc;
	}

	/**
	 * @param funcDesc the funcDesc to set
	 */
	public void setFuncDesc(String funcDesc) {
		this.funcDesc = funcDesc;
	}

	/**
	 * @return the functCode
	 */
	public String getFunctCode() {
		return functCode;
	}

	/**
	 * @param functCode the functCode to set
	 */
	public void setFunctCode(String functCode) {
		this.functCode = functCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ELEntitlementFunctionalityDto [funcId=" + funcId
				+ ", functCode=" + functCode + ", funcDesc=" + funcDesc + "]";
	}
	
}
