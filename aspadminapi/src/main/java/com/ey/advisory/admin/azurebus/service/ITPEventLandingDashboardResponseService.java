package com.ey.advisory.admin.azurebus.service;

import com.google.gson.JsonObject;

/**
 * @author Sakshi.jain
 *
 */
public interface ITPEventLandingDashboardResponseService {

	String getReturnStatus(Long entityId, String taxPeriod, Integer id, JsonObject obj);
	
	String getOutwardSupply(Long entityId, String taxPeriod,Integer id,JsonObject obj);
	
	String getReconSumary2A(Long entityId, String taxPeriod,Integer id,JsonObject obj);
	
	String getReconSumary2BPR(Long entityId, String taxPeriod,Integer id,JsonObject obj);
	

}
