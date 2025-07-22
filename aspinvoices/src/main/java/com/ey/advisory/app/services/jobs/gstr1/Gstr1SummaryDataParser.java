package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1SummaryEntity;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Gstr1SummaryDataParser {

	public List<GetGstr1SummaryEntity> parseSummaryData(String apiResp,
			Long batchId);

	public void saveSummaryData(List<GetGstr1SummaryEntity> entities,
			String groupCode, String sGstin, String taxPeriod);

	public List<GetGstr1SummaryEntity> parseGstr1ASummaryData(String apiResp,
			Long batchId);

	public void saveGstr1ASummaryData(List<GetGstr1SummaryEntity> entities,
			String groupCode, String sGstin, String taxPeriod);

}
