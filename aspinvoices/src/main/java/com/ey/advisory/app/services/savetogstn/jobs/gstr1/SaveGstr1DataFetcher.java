package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.ProcessingContext;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface SaveGstr1DataFetcher {

	public List<Object[]> findGstr1InvoiceLevelData(String gstin,
			String retPeriod, String groupCode, String docType,
			Map<Long, Long> orgCanIdsMap, List<Long> docIds, ProcessingContext context);

	public List<Object[]> findGstr1SummaryData(String gstin, String retPeriod,
			String groupCode, String docType, Long userRequestId, ProcessingContext context);

	public List<Object[]> findGstr1CancelledData(String gstin, String retPeriod,
			String groupCode, String docType, List<Long> docIds, ProcessingContext context);
}
