package com.ey.advisory.app.services.savetogstn.jobs.gstr7;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.ProcessingContext;

/**
 * 
 * @author Sribhavya
 *
 */
public interface Gstr7SaveDataFetcher {

	public List<Object[]> findInvoiceLevelData(String gstin, String retPeriod,
			String groupCode, String docType, Long userRequestId,
			ProcessingContext gstr7context);

	public List<Object[]> findGstr7TransInvoiceLevelData(String gstin,
			String retPeriod, String groupCode, Map<Long, Long> orgCanIdsMap,
			String docType, ProcessingContext context);

}
