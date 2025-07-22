package com.ey.advisory.app.approvalWorkflow;

import java.util.List;

public interface ApprovalCheckerRequestDao {

	public List<ApprovalCheckerStatusSummaryDto> getRequestSummary(ApprovalCheckerRequestDto dto, String userName);
}
