package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import com.ey.advisory.app.data.entities.client.SaveToGstnEventStatusEntity;
import com.google.gson.JsonObject;

/**
 * 
 * @author Hemasundar.J
 *
 */

public interface SaveToGstnEventStatus {

	public JsonObject findLatestStatus(String jsonString);

	public SaveToGstnEventStatusEntity EventEntry(String taxPeriod,
			String gstin, int statusCode, String groupCode);
	
	public SaveToGstnEventStatusEntity EventEntry(String taxPeriod,
			String gstin, int statusCode, String groupCode, String section);

	
	/**
	 * This method will give the job status code Which is used for avoiding 
	 * duplicate save request.
	 * @param gstin
	 * @param retPeriod
	 * @return
	 */
	public Integer findLastJobStatusCode(String gstin, String retPeriod,
			String groupCode);

	public SaveToGstnEventStatusEntity Itc04EventEntry(String taxPeriod, 
			String gstin, int statusCode, String groupCode);
}
