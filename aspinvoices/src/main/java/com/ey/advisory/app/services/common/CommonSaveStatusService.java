package com.ey.advisory.app.services.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.anx1.DataStatusFileSummaryFetchService;
import com.ey.advisory.app.docs.dto.anx1.SavestatusReqDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1SummarySaveStatusRespDto;
import com.ey.advisory.app.services.daos.get2a.GetSaveStatusFetchDao;

@Component("CommonSaveStatusService")
public class CommonSaveStatusService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DataStatusFileSummaryFetchService.class);

	private static final String ANX1 = "anx1";
	private static final String ANX1A = "anx1a";
	private static final String RET1 = "ret1";
	private static final String RET1A = "ret1a";
	private static final String GSTR6 = "gstr6";

	@Autowired
	@Qualifier("GetCommonSaveStatusFetchDaoImpl")
	private GetSaveStatusFetchDao gstr1DetailStatusFetchDao;

	public List<Gstr1SummarySaveStatusRespDto> findByCriteria(
			SavestatusReqDto criteria) throws Exception {

		List<Gstr1SummarySaveStatusRespDto> summaryList = new ArrayList<>();
		try {
			List<Object[]> gstinList = gstr1DetailStatusFetchDao
					.getGstinsByEntityId(criteria.getEntityId(),
							criteria.getGstin());
			if (criteria.getReturnType() != null
					&& criteria.getReturnType().equalsIgnoreCase(ANX1)) {
				getSaveStatusByReturnTypeByGstin(ANX1.toUpperCase(), gstinList,
						criteria, summaryList);
			} else if (criteria.getReturnType() != null
					&& criteria.getReturnType().equalsIgnoreCase(ANX1A)) {
				getSaveStatusByReturnTypeByGstin(ANX1A.toUpperCase(), gstinList,
						criteria, summaryList);
			} else if (criteria.getReturnType() != null
					&& criteria.getReturnType().equalsIgnoreCase(RET1)) {
				getSaveStatusByReturnTypeByGstin(RET1.toUpperCase(), gstinList,
						criteria, summaryList);
			} else if (criteria.getReturnType() != null
					&& criteria.getReturnType().equalsIgnoreCase(RET1A)) {
				getSaveStatusByReturnTypeByGstin(RET1A.toUpperCase(), gstinList,
						criteria, summaryList);
			} else if (criteria.getReturnType() != null
					&& criteria.getReturnType().equalsIgnoreCase(GSTR6)) {
				getSaveStatusByReturnTypeByGstin(GSTR6.toUpperCase(), gstinList,
						criteria, summaryList);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occred:{}", e);
		}
		return summaryList;

	}

	private void getSaveStatusByReturnTypeByGstin(String returnType,
			List<Object[]> gstinList, SavestatusReqDto criteria,
			List<Gstr1SummarySaveStatusRespDto> summaryList) throws Exception {
		if (gstinList != null && !gstinList.isEmpty()) {
			for (Object gstinObj : gstinList) {
				List<Object[]> savedDataList = gstr1DetailStatusFetchDao
						.getSaveStatusDetailsByReturnType((String) gstinObj,
								criteria.getTaxPeriod(), returnType);
				if (savedDataList != null && !savedDataList.isEmpty()) {
					for (Object[] data : savedDataList) {
						Gstr1SummarySaveStatusRespDto dto = new Gstr1SummarySaveStatusRespDto();
						if (data[1] == null) {
							dto.setDate("");
							dto.setTime("");
						} else {
							String originalString = String.valueOf(data[1]);
							Date date = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss")
											.parse(originalString);
							String dateStamp = new SimpleDateFormat(
									"yyyy-MM-dd").format(date);
							String newstr = new SimpleDateFormat("HH:mm:ss")
									.format(date);
							dto.setDate(dateStamp);
							dto.setTime(newstr);
						}
						if (data[2] == null) {
							dto.setRefId("");
						} else {
							dto.setRefId(String.valueOf(data[2]));
						}
						if (data[3] == null) {
							dto.setStatus("");
						} else {
							dto.setStatus(String.valueOf(data[3]));
						}
						dto.setErrorCount(String.valueOf(data[4]));
						summaryList.add(dto);
					}
				}
			}
		}
	}
}
