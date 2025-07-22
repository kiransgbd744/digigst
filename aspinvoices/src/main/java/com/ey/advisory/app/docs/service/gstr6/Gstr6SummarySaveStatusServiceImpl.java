package com.ey.advisory.app.docs.service.gstr6;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.gstr1.Gstr1SummarySaveStatusRespDto;
import com.ey.advisory.app.services.daos.gstr6.Gstr6SummarySaveStatusDaoImpl;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1SummarySaveStatusReqDto;

@Service("Gstr6SummarySaveStatusServiceImpl")
public class Gstr6SummarySaveStatusServiceImpl
		implements Gstr6SummarySaveStatusService {

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	@Qualifier("Gstr6SummarySaveStatusDaoImpl")
	private Gstr6SummarySaveStatusDaoImpl gstr6SummarySaveStatusDao;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6SummarySaveStatusServiceImpl.class);

	public List<Gstr1SummarySaveStatusRespDto> findByCriteria(
			Gstr1SummarySaveStatusReqDto criteria) {
		Gstr1SummarySaveStatusReqDto reqDto = processedRecordsCommonSecParam
				.setgstr2DataSecuritySearchParams(criteria);
		List<Gstr1SummarySaveStatusRespDto> summaryList = new ArrayList<>();
		try {
			Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
			List<String> gstinList = null;
			if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
					&& dataSecAttrs.size() > 0) {
				for (String key : dataSecAttrs.keySet()) {
					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
			List<Object[]> savedDataList = gstr6SummarySaveStatusDao
					.getDataUploadedStatusDetails(gstinList,
							criteria.getTaxPeriod());
			int i = 0;
			if (!savedDataList.isEmpty()) {
				for (Object[] data : savedDataList) {
					Gstr1SummarySaveStatusRespDto dto = new Gstr1SummarySaveStatusRespDto();
					dto.setSNo(++i);
					if (data[1] != null) {
						Timestamp timeStamp = (Timestamp) data[1];
						LocalDateTime modifiedDate = EYDateUtil
								.toISTDateTimeFromUTC(timeStamp);
						DateTimeFormatter locaDate = DateTimeFormatter
								.ofPattern("yyyy-MM-dd");
						DateTimeFormatter localTime = DateTimeFormatter
								.ofPattern("HH:mm:ss");
						String date = modifiedDate.format(locaDate);
						String time = modifiedDate.format(localTime);
						dto.setDate(String.valueOf(date));
						dto.setTime(String.valueOf(time));
					}
					dto.setRefId(
							data[2] != null ? String.valueOf(data[2]) : null);
					dto.setStatus(
							data[3] != null ? String.valueOf(data[3]) : null);
					dto.setErrorCount(
							data[4] != null ? String.valueOf(data[4]) : null);

					String action = data[6] != null ? String.valueOf(data[6])
							: null;
					dto.setErrorMsg(
							data[7] != null ? String.valueOf(data[7]) : null);
					if ("SAVE_CROSS_ITC".equalsIgnoreCase(action)) {
						boolean isItcCross = ((Boolean) data[8]).booleanValue();
						if (isItcCross) {
							dto.setSection("Cross ITC(User Edited)");
						} else {
							dto.setSection("Cross ITC (DigiGST)");
						}
					} else {
						dto.setSection(data[5] != null ? String.valueOf(data[5])
								: null);
					}
					dto.setAction(action);
					summaryList.add(dto);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		return summaryList;
	}
}
