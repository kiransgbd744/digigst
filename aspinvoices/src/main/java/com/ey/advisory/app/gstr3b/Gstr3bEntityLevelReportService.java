package com.ey.advisory.app.gstr3b;

import java.util.List;

import com.aspose.cells.Workbook;

public interface Gstr3bEntityLevelReportService {

	public Workbook getGstr3bReportData(
			String taxPeriod, List<String> gstins, String entityId);
	
	public String getGstr3bOnboardingDataByEntityId(String entityId);

}
