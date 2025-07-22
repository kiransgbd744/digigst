package com.ey.advisory.app.asprecon.reconresponse;

import java.util.List;

import lombok.Data;

/**
* @author Sakshi.jain
*
*/

@Data
public class Gstr2ReconResponseButtonReqDto {
	
	private String entityId;
	private String responseRemarks;
	private String taxPeriodGstr3b;
	private String avaiIgst;
	private String avaiCgst;
	private String avaiSgst;
	private String avaiCess;
	private String reconType;
	private String taxPeriodPR;
	private String taxPeriod2A;
	private String reconLinkIdPR;
	private String reconLinkId2A;
	private String docNumberPR;
	private String docNumber2A;
	private String indentifier;
	private String cfsFlagPR;
	private String cfsFlag2A;
	private String itcReversal;
	
	
	//for error output
	private String errorDesc;
	private String type;
	
	//for multiLocking 
	private List<Gstr2ReconResponseDashboardDto> respList;
	
}
