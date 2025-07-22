package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Anx1SaveInvicesIdentifier {

	public List<SaveToGstnBatchRefIds> findAnx1SaveInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			List<Long> docIds, SaveToGstnOprtnType operationType);
}
