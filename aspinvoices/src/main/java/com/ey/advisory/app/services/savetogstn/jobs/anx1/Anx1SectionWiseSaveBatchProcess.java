/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.util.List;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * @author Hemasundar.J
 *
 */
public interface Anx1SectionWiseSaveBatchProcess {

	/**
	 * This Method is responsible to do SaveToGstn Operation by accepting the 
	 * SaveBatchProcessDto objects and to update the section wise origin tables
	 *  and save_batch tables.
	 * 
	 */
	public List<SaveToGstnBatchRefIds> execute(SaveBatchProcessDto batchDto,
			String groupCode, String section/*, String operationType,
			Map<Long, Long> orgCanIdsMap*/);
}
