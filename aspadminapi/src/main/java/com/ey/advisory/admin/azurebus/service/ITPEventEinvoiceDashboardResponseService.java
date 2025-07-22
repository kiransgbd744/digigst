package com.ey.advisory.admin.azurebus.service;

import java.util.List;

import com.google.gson.JsonObject;

/**
 * @author Sakshi.jain
 *
 */
public interface ITPEventEinvoiceDashboardResponseService {

	String getEinvDist(JsonObject obj, List<String> gstins, Long entityId);
	
	String getEinvSts(JsonObject obj, List<String> gstins, Long entityId);
	
	String getEinvGenTrends(JsonObject obj, List<String> gstins, Long entityId);
	
	String getEinvErrorTrends(JsonObject obj, List<String> gstins, Long entityId);
	
	String getEinvSummry(JsonObject obj, List<String> gstins, Long entityId);
	
	List<String> getSuppGstins(Long entityId);
	

}
