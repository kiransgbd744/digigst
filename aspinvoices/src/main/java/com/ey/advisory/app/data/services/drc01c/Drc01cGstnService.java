package com.ey.advisory.app.data.services.drc01c;

import java.util.List;

import com.google.gson.JsonArray;


public interface Drc01cGstnService {

	public JsonArray getDrcRetComList(List<String> gstinList,
			String taxPeriod, boolean isPeriodicJob, Long entityId);

	public void getDrcRetComSummary(String gstin, String taxPeriod,
			String refId, String userName);
}