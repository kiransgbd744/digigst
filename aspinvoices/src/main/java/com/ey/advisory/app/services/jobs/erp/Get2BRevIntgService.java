package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import com.ey.advisory.app.docs.dto.erp.Get2BRevIntgInvDto;
import com.ey.advisory.app.docs.dto.erp.Get2BRevIntgSummaryDto;

public interface Get2BRevIntgService {

	public List<Get2BRevIntgInvDto> get2BTransactionalData(Long requestId,
			Integer chunkId, String entityName, String entityPan);

	List<Get2BRevIntgSummaryDto> get2BSummaryData(String gstin,
			String taxPeriod, String entityName, String entityPan);
}
