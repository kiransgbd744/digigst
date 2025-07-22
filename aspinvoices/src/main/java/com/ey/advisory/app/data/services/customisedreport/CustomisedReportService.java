package com.ey.advisory.app.data.services.customisedreport;

import com.google.gson.JsonObject;

public interface CustomisedReportService {

	public String saveSelectedFields(CustomisedReportReqDto reqDto );

	public JsonObject getSelectedFields(String entityId, String reportType);

}
