/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.ret;

import java.util.List;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * @author Hemasundar.J
 *
 */
public interface RetSaveBatchProcess {

	/**
	 * This Method is responsible to do SaveToGstn Operation by accepting the 
	 * SaveBatchProcessDto objects and to update the Gstr1_Doc_Header and 
	 * Gstr1_save_batch tables.
	 * 
	 * @param batchDto
	 * @param groupCode
	 * @param section
	 * @return
	 */
	public List<SaveToGstnBatchRefIds> execute(SaveBatchProcessDto batchDto,
			String groupCode, String section);
}
