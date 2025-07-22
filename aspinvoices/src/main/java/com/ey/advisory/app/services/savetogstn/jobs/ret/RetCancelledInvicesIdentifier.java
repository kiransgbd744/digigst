/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.ret;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * @author Hemasundar.J
 *
 */
public interface RetCancelledInvicesIdentifier {

	public List<SaveToGstnBatchRefIds> findCanInvoices(String gstin,
			String retPeriod, String groupCode, SaveToGstnOprtnType section);
}
