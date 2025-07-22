package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Gstr1SaveInvicesIdentifier {

	public List<SaveToGstnBatchRefIds> findSaveInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			Map<Long, Long> orgCanIdsMap, SaveToGstnOprtnType operationType,
			Long userRequestId, List<Long> docIds, ProcessingContext context);
}
