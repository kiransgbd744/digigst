package com.ey.advisory.app.services.savetogstn.jobs.gstr6;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * 
 * @author Sri Bhavya
 *
 */

public interface Gstr6SaveInvicesIdentifier {
	public List<SaveToGstnBatchRefIds> findGstr6SaveInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			String isdDocType, SaveToGstnOprtnType operationType, Long userRequestId);
}
