package com.ey.advisory.app.services.savetogstn.jobs.gstr7;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr7SaveInvicesIdentifier {
	
	public List<SaveToGstnBatchRefIds> findGstr7SaveInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			List<Long> docIds, SaveToGstnOprtnType operationType,
			Long userRequestId, ProcessingContext gstr7context,
			Map<Long, Long> orgCanIdsMap);

}
