package com.ey.advisory.app.services.savetogstn.jobs.gstr7;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.SaveToGstnOprtnType;

/**
 * 
 * @author SriBhavya
 *
 */
public interface SaveGstr7DataFetcher {

	public List<Object[]> findGstr7CancelledData(String gstin, String retPeriod,
			String groupCode, String section, SaveToGstnOprtnType oprtnType,
			Long userRequestId, ProcessingContext gstr7context);

	public List<Object[]> findGstr7TransCancelledData(String gstin, String retPeriod,
			String groupCode, String section, SaveToGstnOprtnType oprtnType,
			Long userRequestId, ProcessingContext gstr7context);

	public List<Object[]> findGstr7InvoiceLevelData(String gstin,
			String retPeriod, String groupCode, String docType,
			Map<Long, Long> orgCanIdsMap, List<Long> docIds, ProcessingContext context);

}
