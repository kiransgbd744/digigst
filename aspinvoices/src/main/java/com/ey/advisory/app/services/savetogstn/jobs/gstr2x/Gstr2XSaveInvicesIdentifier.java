package com.ey.advisory.app.services.savetogstn.jobs.gstr2x;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * 
 * @author SriBhavya
 *
 */

public interface Gstr2XSaveInvicesIdentifier {
	
	public List<SaveToGstnBatchRefIds> findGstr2XSaveInvoices(String gstin, String retPeriod, String groupCode,
			String section,SaveToGstnOprtnType operationType, Long userRequestId);
}
