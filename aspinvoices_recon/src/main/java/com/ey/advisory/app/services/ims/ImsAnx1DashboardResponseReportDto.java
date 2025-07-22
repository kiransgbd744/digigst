package com.ey.advisory.app.services.ims;

import com.google.gson.annotations.Expose;

import lombok.Data;
@Data
public class ImsAnx1DashboardResponseReportDto {
	
	
	@Expose
	public String gstin;
	
	@Expose
	public String invCountGetCall;
	
	@Expose
	public String invDetailsGetCall;

}
