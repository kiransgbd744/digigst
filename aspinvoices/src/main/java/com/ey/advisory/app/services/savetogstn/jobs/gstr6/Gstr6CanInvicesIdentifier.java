package com.ey.advisory.app.services.savetogstn.jobs.gstr6;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
/**
 * 
 * @author Sri Bhavya
 *
 */
public interface Gstr6CanInvicesIdentifier {

	public List<SaveToGstnBatchRefIds> findGstr6CanInvoices(String gstin, String retPeriod, String groupCode, 
			String b2b, SaveToGstnOprtnType can,
			Long userRequestId, String string);

}
