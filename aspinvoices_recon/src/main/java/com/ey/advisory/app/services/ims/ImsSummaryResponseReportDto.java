package com.ey.advisory.app.services.ims;

import com.google.gson.annotations.Expose;

import lombok.Data;
@Data
public class ImsSummaryResponseReportDto {
	
	@Expose
	public String gstin;
	
	@Expose
	public Integer gstnTotalCount;
	
	@Expose
	public Integer gstnNoActionCount;
	
	@Expose
	public Integer gstnAcceptedCount;
	
	@Expose
	public Integer gstnRejectedCount;
	
	@Expose
	public Integer gstnPendingCount;
	
	@Expose
	public Integer totalCount;
	
	@Expose
	public Integer noActionCount;
	
	@Expose
	public Integer acceptedCount;
	
	@Expose
	public Integer rejectedCount;
	
	@Expose
	public Integer pendingCount;
	
	

}

