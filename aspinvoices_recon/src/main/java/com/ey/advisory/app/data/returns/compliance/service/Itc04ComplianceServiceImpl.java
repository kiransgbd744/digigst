package com.ey.advisory.app.data.returns.compliance.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.data.entities.client.ClientFilingStatusEntity;
import com.ey.advisory.app.data.repositories.client.ClientFilingStatusRepositoty;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.ey.advisory.core.dto.ReturnStusFilingDto;
import com.ey.advisory.core.dto.ReturnStusFilingDto.ReturnPeriodDto;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Itc04ComplianceServiceImpl")
public class Itc04ComplianceServiceImpl implements ComplienceSummeryService {

	public static final Logger LOGGER = LoggerFactory
			.getLogger(Itc04ComplianceServiceImpl.class);

	private static final DateTimeFormatter format = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	@Qualifier("CompienceHistoryServiceImpl")
	private CompienceHistoryServiceImpl complienceSummery;

	@Autowired
	@Qualifier("ClientFilingStatusRepositoty")
	private ClientFilingStatusRepositoty returnDataStorageStatusRepo;

	@Autowired
	DefaultStateCache defaultStateCache;

	private static final String Q1 = "13";
	private static final String Q2 = "14";
	private static final String Q3 = "15";
	private static final String Q4 = "16";
	private static final String H1 = "17";
	private static final String H2 = "18";

	public List<ComplienceSummeryRespDto> findcomplienceSummeryRecords(
			Gstr2aProcessedDataRecordsReqDto reqdto) {

		String financialYear = reqdto.getFinancialYear();
		String returnType = reqdto.getReturnType();
		List<ComplienceSummeryRespDto> listDto = new ArrayList<>();
		Pair<Map<String, String>, Map<String, String>> gstnRegMap = commonUtility
				.getGstnRegMap();
		List<ReturnStusFilingDto> statusdto = complienceSummery
				.findcomplienceSummeryRecords(reqdto);
		if (statusdto != null) {
			statusdto.stream().forEach(dto -> {
				String gstin = dto.getGstin();
				ComplienceSummeryRespDto res = new ComplienceSummeryRespDto();
				String stateCode = gstin.substring(0, 2);
				String stateName = defaultStateCache.getStateName(stateCode);
				res.setState(stateName);
				res.setGstin(gstin);
				if (!gstnRegMap.getValue1().isEmpty()) {
					res.setRegType(gstnRegMap.getValue1().get(gstin));
				}
				if (!gstnRegMap.getValue0().isEmpty()) {
					String gstnAct = gstnRegMap.getValue0().get(gstin);
					if (gstnAct.equalsIgnoreCase("A")) {
						res.setAuthToken("Active");
					} else {
						res.setAuthToken("Inactive");
					}
				} else {
					res.setAuthToken("Inactive");
				}

				ClientFilingStatusEntity returnStatusDto = returnDataStorageStatusRepo
						.findByFinancialYearAndGstinAndReturnType(financialYear,
								gstin, returnType);
				if (returnStatusDto == null) {
					res.setInitiatestatus("");
					res.setInitiateTime("");
				} else {
					LocalDateTime istCreatedDate = EYDateUtil
							.toISTDateTimeFromUTC(
									returnStatusDto.getModifiedOn());
					String formatDateTime = istCreatedDate.format(format);
					res.setInitiateTime(formatDateTime);
					res.setInitiatestatus(returnStatusDto.getStatus());
				}
				List<ReturnPeriodDto> returnperiods = dto.getReturnperiods();
				setStatusReturnPeriodWise(res, returnperiods);
				listDto.add(res);

			});
		}
		return listDto;

	}

	private static void setStatusReturnPeriodWise(ComplienceSummeryRespDto res,
			List<ReturnPeriodDto> returnperiods) {
		returnperiods.stream().forEach(priod -> {
			String month = priod.getMonth().substring(0, 2);
			switch (month) {
			case Q1:
				res.setQ1Status(priod.getStatus());
				res.setQ1Timestamp(priod.getTime());
				break;
			case Q2:
				res.setQ2Status(priod.getStatus());
				res.setQ2Timestamp(priod.getTime());
				break;
			case Q3:
				res.setQ3Status(priod.getStatus());
				res.setQ3Timestamp(priod.getTime());
				break;
			case Q4:
				res.setQ4Status(priod.getStatus());
				res.setQ4Timestamp(priod.getTime());
				break;
			case H1:
				res.setH1Status(priod.getStatus());
				res.setH1Timestamp(priod.getTime());
				break;
			case H2:
				res.setH2Status(priod.getStatus());
				res.setH2Timestamp(priod.getTime());
				break;

			default:
				break;
			}

		});

	}

}
