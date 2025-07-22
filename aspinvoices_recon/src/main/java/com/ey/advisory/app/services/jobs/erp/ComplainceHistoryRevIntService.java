package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import com.ey.advisory.app.data.returns.compliance.service.ComplainceHistoryRevIntItemDto;

public interface ComplainceHistoryRevIntService {

	List<ComplainceHistoryRevIntItemDto> getComplainceHistory(String entityName,
			String gstin, String panNo, Long entityId, String regType,
			String finYear, String section);
}
