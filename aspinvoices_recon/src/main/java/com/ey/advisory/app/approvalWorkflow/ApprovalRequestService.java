package com.ey.advisory.app.approvalWorkflow;

import java.util.List;

public interface ApprovalRequestService {

	public List<ApprovalMakerDataDto> getMakerRequestData(
			ApprovalMakerRequestDto dto, String userName);

	public String submitRequestData(MakerRequestDto reqDto, String userName);

	public List<ApprovalRequestSummaryDto> getRequestSummaryData(
			ApprovalMakerRequestDto dto, String userName);

	public Object[] getSaveAndSignData(ApprovalMakerRequestDto dto,
			String userName);
}
