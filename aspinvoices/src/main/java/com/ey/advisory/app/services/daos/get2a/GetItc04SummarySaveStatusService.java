package com.ey.advisory.app.services.daos.get2a;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr1.Gstr1SummarySaveStatusRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1SummarySaveStatusReqDto;
/**
 * 
 * @author Sri Bhavya
 *
 */
@Component("GetItc04SummarySaveStatusService")
public class GetItc04SummarySaveStatusService {	
	
	@Autowired
	@Qualifier("GetItc04DetailStatusFetchDaoImpl")
	private GetItc04DetailStatusFetchDao itc04DetailStatusFetchDao;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;
	
	public List<Gstr1SummarySaveStatusRespDto> findByCriteria(
			Gstr1SummarySaveStatusReqDto criteria) throws Exception {
		Gstr1SummarySaveStatusReqDto reqDto = processedRecordsCommonSecParam
				.setgstr2DataSecuritySearchParams(criteria);

		List<Gstr1SummarySaveStatusRespDto> summaryList = new ArrayList<Gstr1SummarySaveStatusRespDto>();
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		List<String> GstinList = null;
		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					GstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}
		List<Object[]> savedDataList = itc04DetailStatusFetchDao
				.getDataUploadedStatusDetails(GstinList,
						criteria.getTaxPeriod());
		if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
			for (Object[] data : savedDataList) {
				Gstr1SummarySaveStatusRespDto dto = new Gstr1SummarySaveStatusRespDto();
				if (data[1] == null) {
					dto.setDate("");
					dto.setTime("");
				} else {
					Timestamp timestamp = (Timestamp) data[1];
					LocalDateTime dt = timestamp.toLocalDateTime();
					LocalDateTime localDateTime = EYDateUtil
							.toISTDateTimeFromUTC(dt);
					DateTimeFormatter dateFormatter = DateTimeFormatter
							.ofPattern("dd-MM-yyyy");
					DateTimeFormatter timeFormatter = DateTimeFormatter
							.ofPattern("HH:mm:ss");
					String date = dateFormatter.format(localDateTime);
					String time = timeFormatter.format(localDateTime);

					dto.setDate(date);
					dto.setTime(time);
				}
				if (data[2] == null) {
					dto.setSection("");
				} else {
					dto.setSection(String.valueOf(data[2]));
				}
				if (data[3] == null) {
					dto.setRefId("");
				} else {
					dto.setRefId(String.valueOf(data[3]));
				}
				if (data[4] == null) {
					dto.setStatus("");
				} else {
					dto.setStatus(String.valueOf(data[4]));
				}
				dto.setErrorCount(String.valueOf(data[5]));
				if (data[6] == null) {
					dto.setAction("");
				} else {
					dto.setAction(String.valueOf(data[6]));
				}
				summaryList.add(dto);
			}
		}
		return summaryList;

	}

}
