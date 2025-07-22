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
public interface Gstr7CancelledInvicesIdentifier {

	public List<SaveToGstnBatchRefIds> findGstr7CanInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			SaveToGstnOprtnType oprtnType, Long userRequestId,
			ProcessingContext gstr7context);

	public List<SaveToGstnBatchRefIds> findGstr7TransCanInvoices(String gstin,
			String retPeriod, String groupCode,
			Map<String, Map<Long, Long>> map, Long userRequestId,
			ProcessingContext gstr7context);

	public Map<String, Map<Long, Long>> findOrgCanInvoicesMap(String gstin,
			String retPeriod, String groupCode,
			SaveToGstnOprtnType oprtnType, Long userRequestId,
			ProcessingContext gstr7context);

	
	
}
