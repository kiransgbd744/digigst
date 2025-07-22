package com.ey.advisory.app.docs.dto.anx2;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * 
 * @author Dibyakanta.sahoo
 *
 */

public class Anx2GetSummaryData {
	
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
	@SerializedName("secsum")
    private List<Anx2GetSectionSummaryData> secsum;

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getSummaryType() {
		return summaryType;
	}

	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}

	public List<Anx2GetSectionSummaryData> getSecsum() {
		return secsum;
	}

	public void setSecsum(List<Anx2GetSectionSummaryData> secsum) {
		this.secsum = secsum;
	}

	


}
