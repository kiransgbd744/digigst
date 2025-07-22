package com.ey.advisory.app.data.services.drc;

import java.util.List;

import com.google.gson.JsonArray;

/**
 *
 * @author Siva.Reddy
 *
 */
public interface DrcGstnService {

	public JsonArray getDrcRetComList(List<String> gstinList,
			String taxPeriod, boolean isPeriodicJob, Long entityId);

	public void getDrcRetComSummary(String gstin, String taxPeriod,
			String refId, String userName);
}
