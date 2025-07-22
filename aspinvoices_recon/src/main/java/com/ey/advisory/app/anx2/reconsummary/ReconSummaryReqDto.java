package com.ey.advisory.app.anx2.reconsummary;

import java.util.List;

/*
 * @author : Nikhil.Duseja
 * This Class is DTO for anx2 recon summary
 * Screen
 */

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReconSummaryReqDto {

	@Expose
	private Long entityId;
	
	@Expose
	private List<String> gstins;
	
	@Expose
	private String taxPeriod;
	
	/*
	 * DataSecurities are commited as per discussion with Saurabh Dhariwal
	 * and Anand Srinivasan as we cannot get datasecurity for reconsolitate
	 * removing it 
	 */
	
	
	/*@Expose
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
	private List<String> userAccess6;*/
	
	
	
}
