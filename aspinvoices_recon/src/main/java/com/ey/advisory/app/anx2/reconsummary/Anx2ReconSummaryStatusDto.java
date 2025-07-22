package com.ey.advisory.app.anx2.reconsummary;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Anx2ReconSummaryStatusDto {
    
	@Expose
	private String gstin;
		  
	@Expose
	private String state;
	
	@Expose
	private String status;
	
	@Expose
	private String statusdate;
		  
}
