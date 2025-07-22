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
public interface Gstr1CancelledInvicesIdentifier {

	public Map<String, Map<Long, Long>> findOrgCanInvoicesMap(String gstin,
			String retPeriod, String groupCode, SaveToGstnOprtnType section, List<Long> docIds, ProcessingContext context);

	public List<SaveToGstnBatchRefIds> findCanInvoices(String gstin,
			String retPeriod, String groupCode,
			Map<String, Map<Long, Long>> map, Long userRequestId,
			String userSelectedSec, List<Long> docIds,ProcessingContext context);
}
