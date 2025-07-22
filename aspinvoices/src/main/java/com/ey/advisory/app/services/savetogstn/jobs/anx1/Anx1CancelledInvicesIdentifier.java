package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Anx1CancelledInvicesIdentifier {

	public List<SaveToGstnBatchRefIds> findCanInvoices(String gstin,
			String retPeriod, String groupCode, SaveToGstnOprtnType section);
}
