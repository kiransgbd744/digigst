/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Laxmi.Salukuti
 *
 */
public class Anx1SummaryReqDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("rtnprd")
	private String returnPeriod;

	/**
	 * @return the gstin
	 */
	public String getGstin() {
		return gstin;
	}

	/**
	 * @param gstin
	 *            the gstin to set
	 */
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	/**
	 * @return the returnPeriod
	 */
	public String getReturnPeriod() {
		return returnPeriod;
	}

	/**
	 * @param returnPeriod
	 *            the returnPeriod to set
	 */
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

}
