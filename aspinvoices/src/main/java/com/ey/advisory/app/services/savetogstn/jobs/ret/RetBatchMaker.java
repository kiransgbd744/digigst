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
public interface RetBatchMaker {

	/**
	 * This method is resposible to take the user input and to and to get the list
	 * of eligible docs(invoices) from Hana with and to form the bathes(json Objects)
	 *  with max size as < 5MB and to do the SaveToGstn Operation.
	 * 
	 * @param jsonReq
	 * @param groupCode
	 * @param section
	 * @return
	 */
		public List<SaveToGstnBatchRefIds> saveRetData(String groupCode, 
				String section, List<Object[]> docs, SaveToGstnOprtnType operationType);
}
