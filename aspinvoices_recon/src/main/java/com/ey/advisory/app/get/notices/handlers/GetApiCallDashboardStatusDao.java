package com.ey.advisory.app.get.notices.handlers;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

public interface GetApiCallDashboardStatusDao {

	public Map<String, Pair<String, String>> fetchNoticeGetAPICallStatus(
			List<String> gstin, GstnNoticeReqDto reqDto) throws Exception;
}
