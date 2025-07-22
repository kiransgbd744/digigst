package com.ey.advisory.app.get.notices.handlers;

import java.util.List;



public interface GetApiCallDashboardStatusService {


	public List<NoticeEntitySummaryDto> fetchNoticeSummary(List<String> gstin,GstnNoticeReqDto reqDto) throws Exception;
}
