package com.ey.advisory.app.services.jobs.erp;

import org.springframework.http.ResponseEntity;

import com.google.gson.JsonObject;
/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Gstr1SummaryRIDestination {
	
	public ResponseEntity<String> pushToErp(JsonObject reqObject, String destination);
}
