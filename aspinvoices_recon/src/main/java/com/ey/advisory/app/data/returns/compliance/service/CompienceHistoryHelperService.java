package com.ey.advisory.app.data.returns.compliance.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.data.entities.client.ClientFilingStatusEntity;
import com.ey.advisory.app.data.repositories.client.ClientFilingStatusRepositoty;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.ey.advisory.core.dto.ReturnStusFilingDto;
import com.ey.advisory.core.dto.ReturnStusFilingDto.ReturnPeriodDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.base.Strings;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("CompienceHistoryHelperService")
public class CompienceHistoryHelperService  {

	private static final DateTimeFormatter format = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");
	@Autowired
	@Qualifier("CompienceHistoryServiceImpl")
	private CompienceHistoryServiceImpl complienceSummery;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;
	@Autowired
	@Qualifier("ClientFilingStatusRepositoty")
	private ClientFilingStatusRepositoty returnDataStorageStatusRepo;
	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;
	
	@Autowired
	DefaultStateCache defaultStateCache;
	
	@Autowired
	private CommonUtility commonUtility;

	private static final String APRIL = "04";
	private static final String MAY = "05";
	private static final String JUNE = "06";
	private static final String JULY = "07";
	private static final String AUG = "08";
	private static final String SEP = "09";
	private static final String OCT = "10";
	private static final String NOV = "11";
	private static final String DEC = "12";
	private static final String JAN = "01";
	private static final String FEB = "02";
	private static final String MARCH = "03";

	public List<ComplienceSummeryRespDto> findcomplienceSummeryRecords(
			Gstr2aProcessedDataRecordsReqDto reqdto) {
		String financialYear = reqdto.getFinancialYear();
		String returnType = reqdto.getReturnType();

		List<ComplienceSummeryRespDto> listDto = new ArrayList<>();
		List<ReturnStusFilingDto> statusdto = complienceSummery
				.findcomplienceSummeryRecords(reqdto);
		if (statusdto != null) {
			statusdto.stream().forEach(dto -> {
				String gstin = dto.getGstin();
				ComplienceSummeryRespDto res = new ComplienceSummeryRespDto();
				String stateCode = gstin.substring(0, 2);
				/*String stateName = statecodeRepository
						.findStateNameByCode(stateCode);
				res.setState(stateName);*/
				res.setGstin(gstin);
				String stateName = defaultStateCache.getStateName(stateCode);
				res.setState(stateName);
				
				Pair<Map<String, String>, Map<String, String>> gstnRegMap = commonUtility
						.getGstnRegMap();
				
				Map<String, String> gstinAuthMap = gstnRegMap.getValue0();
				Map<String, String> regTypeMap = gstnRegMap.getValue1();
				
				if (!regTypeMap.isEmpty()) {
					String regTypeName = regTypeMap.get(gstin);
					if (regTypeName == null
							|| regTypeName.equalsIgnoreCase("normal")) {
						res.setRegType("");
					} else if (!Strings.isNullOrEmpty(regTypeName)) {
						res.setRegType(regTypeName.toUpperCase());
					} else {
						res.setRegType("");
					}
				}
				
				/*res.setRegType(
						gstinInfoRepository.findByGstinAndIsDeleteFalse(gstin)
								.getRegistrationType());
				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(gstin);*/
				/*if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						res.setAuthToken("Active");
					} else {
						res.setAuthToken("Inactive");
					}
				} else {
					res.setAuthToken("Inactive");
				}*/
				if (!gstinAuthMap.isEmpty()) {
					String gstnAct = gstinAuthMap.get(gstin);
					if ("A".equalsIgnoreCase(gstnAct)) {
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
			case APRIL:
				res.setAprilStatus(priod.getStatus());
				res.setApriltimestamp(priod.getTime());
				break;
			case MAY:
				res.setMayStatus(priod.getStatus());
				res.setMayTimeStamp(priod.getTime());
				break;
			case JUNE:
				res.setJuneStatus(priod.getStatus());
				res.setJuneTimeStamp(priod.getTime());
				break;
			case JULY:
				res.setJulyStatus(priod.getStatus());
				res.setJulyTimestamp(priod.getTime());
				break;
			case AUG:
				res.setAugStatus(priod.getStatus());
				res.setAugTimeStamp(priod.getTime());
				break;
			case SEP:
				res.setSepStatus(priod.getStatus());
				res.setSepTimeStamp(priod.getTime());
				break;
			case OCT:
				res.setOctStatus(priod.getStatus());
				res.setOctTimestamp(priod.getTime());
				break;
			case NOV:
				res.setNovStatus(priod.getStatus());
				res.setNovTimeStamp(priod.getTime());
				break;
			case DEC:
				res.setDecStatus(priod.getStatus());
				res.setDecTimestamp(priod.getTime());
				break;
			case JAN:
				res.setJanStatus(priod.getStatus());
				res.setJanTimestamp(priod.getTime());
				break;
			case FEB:
				res.setFebStatus(priod.getStatus());
				res.setFebTimeStamp(priod.getTime());
				break;
			case MARCH:
				res.setMarchStatus(priod.getStatus());
				res.setMarchTimestamp(priod.getTime());
				break;
			default:
				break;
			}

		});

	}

	public static void main(String[] args) {
		String s = "2023-05-09 05:52:57.0";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		System.out.println(s.substring(0,19));
	}
}
