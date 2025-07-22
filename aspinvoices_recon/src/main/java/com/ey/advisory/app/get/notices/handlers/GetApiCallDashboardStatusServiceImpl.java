package com.ey.advisory.app.get.notices.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GetApiCallDashboardStatusServiceImpl")
public class GetApiCallDashboardStatusServiceImpl
		implements GetApiCallDashboardStatusService {

	@Autowired
	@Qualifier("GetApiCallDashboardStatusDaoImpl")
	private GetApiCallDashboardStatusDao getApiCallDashboardStatusDAO;

	@Autowired
	@Qualifier("NoticeDetailServiceImpl")
	private NoticeDetailService noticeDetailService;

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	DefaultStateCache defaultStateCache;

	@Override
	public List<NoticeEntitySummaryDto> fetchNoticeSummary(
			List<String> gstinList, GstnNoticeReqDto reqDto) throws Exception {
		
		LOGGER.debug(" reqDto {} ",reqDto);
		
		List<NoticeEntitySummaryDto> respDto = new ArrayList<>();

		try {
			// reg type and auth token map
			Pair<Map<String, String>, Map<String, String>> gstnRegMap = commonUtility
					.getGstnRegMap();
			Map<String, String> gstinAuthMap = gstnRegMap.getValue0();
			Map<String, String> regTypeMap = gstnRegMap.getValue1();

			// get call status map
			Map<String, Pair<String, String>> gstinStatusMap = getApiCallDashboardStatusDAO
					.fetchNoticeGetAPICallStatus(gstinList, reqDto);

			// gst notice detail map
			Map<String, NoticeStats> noticeDetailMap = noticeDetailService
					.fetchNoticeStats(reqDto);

			for (String gstin : gstinList) {
				NoticeEntitySummaryDto dto = new NoticeEntitySummaryDto();
				dto.setGstin(gstin);
				String stateCode = gstin.substring(0, 2);
				String stateName = defaultStateCache.getStateName(stateCode);
				dto.setStateName(stateName);
				if (!regTypeMap.isEmpty()) {
					String regTypeName = regTypeMap.get(gstin);
					if (!Strings.isNullOrEmpty(regTypeName)) {
						dto.setGstinRegType(regTypeName.toUpperCase());
					} else {
						dto.setGstinRegType("");
					}
				}
				// Checking GSTIN Active Status.
				if (!gstinAuthMap.isEmpty()) {
					String gstnAct = gstinAuthMap.get(gstin);
					if (gstnAct.equalsIgnoreCase("A")) {
						dto.setIsAuthToken("Active");
					} else {
						dto.setIsAuthToken("Inactive");
					}
				} else {
					dto.setIsAuthToken("Inactive");
				}

				Pair<String, String> getCalStatusMap = gstinStatusMap
						.containsKey(gstin) ? gstinStatusMap.get(gstin)
								: new Pair<>("Not Initiated", null);

				dto.setStatus(getCalStatusMap.getValue0());
				dto.setGetCallTime(getCalStatusMap.getValue1());

				NoticeStats noticeDetailInfo = noticeDetailMap
						.containsKey(gstin) ? noticeDetailMap.get(gstin)
								: new NoticeStats(gstin, 0, 0, 0);
				dto.setNoticesIssued(noticeDetailInfo.getTotalIssued());
				dto.setNoticesResponded(noticeDetailInfo.getTotalResponded());
				dto.setPendingForResponse(noticeDetailInfo.getTotalPending());
				respDto.add(dto);
			}

		} catch (Exception ex) {
			LOGGER.error(" error while parsing the gst details {}", ex);
			throw new AppException(ex);
		}
		return respDto;

	}
}
