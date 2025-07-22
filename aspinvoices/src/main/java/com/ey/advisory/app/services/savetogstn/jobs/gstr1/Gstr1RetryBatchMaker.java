/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.List;

import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;

/**
 * @author Hemasundar.J
 *
 */
public interface Gstr1RetryBatchMaker {

	/**
	 * This method is responsible to take the user input and to and to get the
	 * list of eligible docs(invoices) from Hana with and to form the
	 * bathes(json Objects) with max size as < 5MB and to do the SaveToGstn
	 * Operation.
	 * 
	 * @param jsonReq
	 * @param groupCode
	 * @param section
	 * @return
	 */
	public List<SaveToGstnBatchRefIds> reTrySaveGstr1Data(List<Object[]> docs,
			Gstr1SaveBatchEntity batch);
}
