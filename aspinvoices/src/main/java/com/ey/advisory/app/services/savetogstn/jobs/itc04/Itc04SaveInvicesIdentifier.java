package com.ey.advisory.app.services.savetogstn.jobs.itc04;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Itc04SaveInvicesIdentifier {

	public List<SaveToGstnBatchRefIds> findItc04SaveInvoices(String gstin, String retPeriod, String groupCode,
			String section, List<Long> docIds, SaveToGstnOprtnType operationType, Long userRequestId);

	public List<SaveToGstnBatchRefIds> findItc04CanInvoices(String gstin, String retPeriod, String groupCode,
			String section, List<Long> docIds, SaveToGstnOprtnType operationType, Long userRequestId);
}
