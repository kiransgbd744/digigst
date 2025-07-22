package com.ey.advisory.app.approvalWorkflow;

import java.util.List;

public interface ApprovalCheckerRequestService {

	public List<ApprovalCheckerStatusSummaryDto> getCheckerRequestData(
			ApprovalCheckerRequestDto dto, String userName);

	public List<ApprovalCheckerGstinsDto> getCheckerGstinsData(String userName, Long entityId);

	public ApprovalCheckerTabSummaryDto findRequestTabCounts(String userName,
			Long entityId);

	public String submitandRevertRequestAction(String userName,
			ApprovalCheckerActionRequestDto dto);
}
