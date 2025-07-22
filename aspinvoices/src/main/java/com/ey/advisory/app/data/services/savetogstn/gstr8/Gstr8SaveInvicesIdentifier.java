package com.ey.advisory.app.data.services.savetogstn.gstr8;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * 
 * @author Siva.Reddy
 *
 */
public interface Gstr8SaveInvicesIdentifier {
	public List<SaveToGstnBatchRefIds> findGstr8SaveInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			List<Long> docIds, SaveToGstnOprtnType operationType,
			Long userRequestId);

	public List<SaveToGstnBatchRefIds> findGstr8CanInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			SaveToGstnOprtnType oprtnType, Long userRequestId);

}
