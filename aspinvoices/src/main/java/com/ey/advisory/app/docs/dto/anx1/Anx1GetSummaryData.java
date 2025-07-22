/**
 * 
 */
package com.ey.advisory.app.docs.dto.anx1;

import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Laxmi.Salukuti
 *
 */
public class Anx1GetSummaryData {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("rtnprd")
	private String returnPeriod;

	@Expose
	@SerializedName("checksum")
	private String checksum;

	@Expose
	@SerializedName("summtyp")
	private String summaryType;

	@Expose
	@SerializedName("lastSummaryTs")
	private LocalDateTime lastSummaryTs;
	@Expose
	@SerializedName("secsum")
	//private List<Anx1GetAllSummaryData> secsum;
	private List<Anx1GetSectionSummaryData> secsum;

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

	/**
	 * @return the checksum
	 */
	public String getChecksum() {
		return checksum;
	}

	/**
	 * @param checksum
	 *            the checksum to set
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	/**
	 * @return the summaryType
	 */
	public String getSummaryType() {
		return summaryType;
	}

	/**
	 * @param summaryType
	 *            the summaryType to set
	 */
	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}

	/**
	 * @return the lastSummaryTs
	 */
	public LocalDateTime getLastSummaryTs() {
		return lastSummaryTs;
	}

	/**
	 * @param lastSummaryTs
	 *            the lastSummaryTs to set
	 */
	public void setLastSummaryTs(LocalDateTime lastSummaryTs) {
		this.lastSummaryTs = lastSummaryTs;
	}

	/**
	 * @return the secsum
	 */
	public List<Anx1GetSectionSummaryData> getSecsum() {
		return secsum;
	}

	/**
	 * @param secsum the secsum to set
	 */
	public void setSecsum(List<Anx1GetSectionSummaryData> secsum) {
		this.secsum = secsum;
	}

	/**
	 * @return the secsum
	 */
	
}