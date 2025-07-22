/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.ret;

import java.util.List;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;

/**
 * @author Hemasundar.J
 *
 */
public interface RateDataToRetConverter {

	/**
	 * This method is responsible to convert the list of docs as batches (Json
	 * Objects) with Maximum size is less than 5MB.
	 * 
	 * @param objects
	 * @param section
	 * @param groupCode
	 * @param supplyType
	 * @return
	 */
	public SaveBatchProcessDto convertToRetObject(List<Object[]> objects,
			String section, String groupCode, String taxDocType);
}
