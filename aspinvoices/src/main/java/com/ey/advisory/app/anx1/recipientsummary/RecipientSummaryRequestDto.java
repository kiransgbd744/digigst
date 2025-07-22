/**
 * 
 */
package com.ey.advisory.app.anx1.recipientsummary;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Nikhil.Duseja
 * This is request dto for recipient summary this dto will
 * receive the data from user that is to be used to summarise reports
 * on various filter parameters
 */

@Getter
@Setter
public class RecipientSummaryRequestDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Expose
	private Long entityId = 0L;
	
	@Expose
	private List<String> gstins;
	
	@Expose
	private List<String> cPans;
	
	@Expose
	private List<String> cgstin;
	
	@Expose
	private String taxPeriod;
	
	@Expose
	private LocalDate startDocDate;
	
	@Expose
	private LocalDate endDocDate;
	
	@Expose
	private List<String> profitCentres;
	
	@Expose
	private List<String> plants;
	
	@Expose
	private List<String> divisions;
	
	@Expose
	private List<String> locations;
	
	@Expose
	private List<String> purchaseOrgs;
	
	@Expose
	private List<String> salesOrgs;
	
	@Expose
	private List<String> distributionChannels;
	
	@Expose
	private List<String> userAccess1;
	
	@Expose
	private List<String> userAccess2;
	
	@Expose
	private List<String> userAccess3;
	
	@Expose
	private List<String> userAccess4;
	
	@Expose
	private List<String> userAccess5;
	
	@Expose
	private List<String> userAccess6;

	@Override
	public String toString() {
		return "RecipientSummaryRequestDto [entityId=" + entityId + ", gstins="
				+ gstins + ", cPans=" + cPans + ", cgstin=" + cgstin
				+ ", taxPeriod=" + taxPeriod + ", startDocDate=" + startDocDate
				+ ", endDocDate=" + endDocDate + ", profitCentres="
				+ profitCentres + ", plants=" + plants + ", divisions="
				+ divisions + ", locations=" + locations + ", purchaseOrgs="
				+ purchaseOrgs + ", salesOrgs=" + salesOrgs
				+ ", distributionChannels=" + distributionChannels
				+ ", userAccess1=" + userAccess1 + ", userAccess2="
				+ userAccess2 + ", userAccess3=" + userAccess3
				+ ", userAccess4=" + userAccess4 + ", userAccess5="
				+ userAccess5 + ", userAccess6=" + userAccess6 + "]";
	}
	
	
}
