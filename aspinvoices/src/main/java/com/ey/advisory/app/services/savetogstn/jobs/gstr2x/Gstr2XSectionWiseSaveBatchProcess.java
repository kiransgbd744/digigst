package com.ey.advisory.app.services.savetogstn.jobs.gstr2x;

import java.util.List;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr2XSectionWiseSaveBatchProcess {

	public List<SaveToGstnBatchRefIds> execute(SaveBatchProcessDto batchDto,
			String groupCode, String section, Long userRequestId, 
			String taxDocType);
}
