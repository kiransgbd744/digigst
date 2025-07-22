package com.ey.advisory.app.get.notices.handlers;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.FastDateFormat;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GetApiCallDashboardStatusDaoImpl")
public class GetApiCallDashboardStatusDaoImpl
		implements GetApiCallDashboardStatusDao {

	@Autowired
	private GetAnx1BatchRepository batchRepo;

	private static final FastDateFormat FAST_DATE_FORMAT = FastDateFormat
			.getInstance("dd-MM-yyyy");
	private static final FastDateFormat DATE_TIME_FORMAT = FastDateFormat
			.getInstance("dd-MM-yyyy HH:mm:ss");

	@Override
	public Map<String, Pair<String, String>> fetchNoticeGetAPICallStatus(
			List<String> gstin, GstnNoticeReqDto reqDto) throws Exception {

		Map<String, Pair<String, String>> gstinStatusMap = new LinkedHashMap<>();
		try {
			
			List<Object[]> gstinGetStatusMap = batchRepo.findActiveGstr1GetStatus(gstin,"GST_NOTICE",Arrays.asList("GST_NOTICE"),"000000");

			 gstinStatusMap = gstinGetStatusMap.stream()
					.filter(arr -> arr[0] != null) // Ensure arr[0] is not null
					.collect(Collectors.toMap(
							arr -> arr[0].toString(), // Key: gstin
							arr -> Pair.with(
									arr[1] != null ? arr[1].toString() : null, // First value in Pair
									arr[2] != null ? arr[2].toString() : null  // Second value in Pair
							),
							(existing, replacement) -> existing, // Handle duplicate keys
							LinkedHashMap::new // Maintain insertion order
					));
			
		} catch (Exception e) {
			LOGGER.error(" Method : ftchNoticeGetAPICallStatus", e);
			throw new AppException(e);
		}
		return gstinStatusMap;
	}

}
