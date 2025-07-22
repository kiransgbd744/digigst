package com.ey.advisory.bcadmin.common.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class is responsible for getting req data from API request 
 * to switch the group code from current grp code to new grp code 
 * @author Mohana.Dasari
 *
 */
public class GroupCodeSwitchReqDto {

	@Expose
	@SerializedName("currGroupCode")
	private String currGroupCode;
	
	@Expose
	@SerializedName("newGroupCode")
	private String newGroupCode;
	/**
	 * @return the currGroupCode
	 */
	public String getCurrGroupCode() {
		return currGroupCode;
	}
	/**
	 * @param currGroupCode the currGroupCode to set
	 */
	public void setCurrGroupCode(String currGroupCode) {
		this.currGroupCode = currGroupCode;
	}
	/**
	 * @return the newGroupCode
	 */
	public String getNewGroupCode() {
		return newGroupCode;
	}
	/**
	 * @param newGroupCode the newGroupCode to set
	 */
	public void setNewGroupCode(String newGroupCode) {
		this.newGroupCode = newGroupCode;
	}
}

