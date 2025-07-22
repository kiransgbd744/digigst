/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.anx2;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * @author Hemasundar.J
 *
 */
public interface Anx2SaveInvicesIdentifier {

	public List<SaveToGstnBatchRefIds> findAnx2SaveInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			List<Long> docIds, SaveToGstnOprtnType operationType);
}
