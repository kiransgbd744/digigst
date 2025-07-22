package com.ey.advisory.app.services.savetogstn.jobs.gstr6;

import java.util.List;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * 
 * @author Sri Bhavya
 *
 */

public interface Gstr6SectionWiseSaveBatchProcess {

	public List<SaveToGstnBatchRefIds> execute(SaveBatchProcessDto batchDto,
			String groupCode, String section, Long userRequestId, 
			String taxDocType, ProcessingContext context);
}
	