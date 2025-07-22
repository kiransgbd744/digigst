package com.ey.advisory.app.anx2.initiaterecon;

import java.util.Date;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Arun.KA
 *
 */

@Getter
@Setter
public class InitiateReconFetchGstinsInfoDto {
	
	@Expose
	private String gstins;
	
	@Expose
	private String stateName;
	
	@Expose
	private  String lastAnnx2FetchStatus;
	
	@Expose 
	private Date lastAnnx2FetchDate;
	
	public InitiateReconFetchGstinsInfoDto() {}

	public InitiateReconFetchGstinsInfoDto(String gstins, String stateName,
			String lastAnnx2FetchStatus, Date lastAnnx2FetchDate) {
		super();
		this.gstins = gstins;
		this.stateName = stateName;
		this.lastAnnx2FetchStatus = lastAnnx2FetchStatus;
		this.lastAnnx2FetchDate = lastAnnx2FetchDate;
	}

	@Override
	public String toString() {
		return "InitiateReconFetchGstinsInfoDto [gstins=" + gstins
				+ ", stateName=" + stateName + ", lastAnnx2FetchStatus="
				+ lastAnnx2FetchStatus + ", lastAnnx2FetchDate="
				+ lastAnnx2FetchDate + "]";
	}
	
	
	

}
