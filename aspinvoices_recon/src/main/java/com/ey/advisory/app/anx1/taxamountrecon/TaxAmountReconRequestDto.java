/**
 * 
 */
package com.ey.advisory.app.anx1.taxamountrecon;

import java.time.LocalDate;
import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Khalid1.Khan
 *
 */
@Getter
@Setter
public class TaxAmountReconRequestDto {
	
	private static final long serialVersionUID = 1L;
	
	@Expose
	private Long entityId = 0L;
	
	@Expose
	private List<String> gstins;
	
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TaxAmountReconRequestDto [entityId=" + entityId + ", gstins="
				+ gstins + ", taxPeriod=" + taxPeriod + ", startDocDate="
				+ startDocDate + ", endDocDate=" + endDocDate
				+ ", profitCentres=" + profitCentres + ", plants=" + plants
				+ ", divisions=" + divisions + ", locations=" + locations
				+ ", purchaseOrgs=" + purchaseOrgs + ", salesOrgs=" + salesOrgs
				+ ", distributionChannels=" + distributionChannels
				+ ", userAccess1=" + userAccess1 + ", userAccess2="
				+ userAccess2 + ", userAccess3=" + userAccess3
				+ ", userAccess4=" + userAccess4 + ", userAccess5="
				+ userAccess5 + ", userAccess6=" + userAccess6 + "]";
	}

	

}
