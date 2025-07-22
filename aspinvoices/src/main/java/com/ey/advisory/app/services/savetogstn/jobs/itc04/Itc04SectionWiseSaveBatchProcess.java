package com.ey.advisory.app.services.savetogstn.jobs.itc04;

import java.util.List;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

public interface Itc04SectionWiseSaveBatchProcess {

	public List<SaveToGstnBatchRefIds> execute(SaveBatchProcessDto batchDto, 
			String groupCode, String section,
			Long userRequestId, String taxDocType);

}
