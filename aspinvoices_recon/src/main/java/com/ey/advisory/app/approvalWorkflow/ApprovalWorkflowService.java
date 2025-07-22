package com.ey.advisory.app.approvalWorkflow;

import java.util.List;

public interface ApprovalWorkflowService {

	public List<ApprovalDataRespDto> getChekerMakerSummary(Long entityId,
			String retType);

	public String createChekerMakerdetails(ApprovalSubmitChecMakerDto reqDto,
			String userName);

}
