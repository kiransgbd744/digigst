package com.ey.advisory.app.gstr3b;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class Gstr3bEntityLevelReportFinalDto {
	
	@Expose
	private String gstin;
	
	@Expose
	private String taxPeriod;
	
	@Expose
	private String tableSection;
	
	@Expose
	private String tableHeading;
	
	@Expose
	private String tableDesc;
	
	@Expose
	private String computeTaxableVal;
	
	@Expose
	private String computeIgst;
	
	@Expose
	private String computeCgst;
	
	@Expose
	private String computeSgst;
	
	@Expose
	private String computeCess;
	
	@Expose
	private String userTaxableVal;
	
	@Expose
	private String userIgst;
	
	@Expose
	private String userCgst;
	
	@Expose
	private String userSgst;
	
	@Expose
	private String userCess;
	
	@Expose
	private String gstnTaxableVal;
	
	@Expose
	private String gstnIgst;
	
	@Expose
	private String gstnCgst;
	
	@Expose
	private String gstnSgst;
	
	@Expose
	private String gstnCess;
	
	@Expose
	private String diffTaxableVal;
	
	@Expose
	private String diffIgst;
	
	@Expose
	private String diffCgst;
	
	@Expose
	private String diffSgst;
	
	@Expose
	private String diffCess;



}
