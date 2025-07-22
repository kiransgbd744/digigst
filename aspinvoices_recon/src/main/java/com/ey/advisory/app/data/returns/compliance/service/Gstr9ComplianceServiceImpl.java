package com.ey.advisory.app.data.returns.compliance.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.entities.client.ClientFilingStatusEntity;
import com.ey.advisory.app.data.repositories.client.ClientFilingStatusRepositoty;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.ey.advisory.core.dto.ReturnStusFilingDto;
import com.ey.advisory.core.dto.ReturnStusFilingDto.ReturnPeriodDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr9ComplianceServiceImpl")
public class Gstr9ComplianceServiceImpl implements ComplienceSummeryService {

	public static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr9ComplianceServiceImpl.class);

	private static final DateTimeFormatter format = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	@Autowired
	@Qualifier("CompienceHistoryServiceImpl")
	private CompienceHistoryServiceImpl complienceSummery;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("ClientFilingStatusRepositoty")
	private ClientFilingStatusRepositoty returnDataStorageStatusRepo;

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
				String stateName = statecodeRepository
						.findStateNameByCode(stateCode);
				res.setState(stateName);
				res.setGstin(gstin);
				res.setRegType(
						gstinInfoRepository.findByGstinAndIsDeleteFalse(gstin)
								.getRegistrationType());
				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
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
					res.setInitiatestatus(returnStatusDto.getStatus());
					res.setInitiateTime(formatDateTime);
				}

				/*
				 * List<ReturnPeriodDto> returnperiods = dto.getReturnperiods();
				 * setStatusReturnPeriodWise(res, returnperiods);
				 * listDto.add(res);
				 */

				ReturnPeriodDto returnperiods1 = dto.getReturnperiods().get(0);

				res.setFilingStatus(returnperiods1.getStatus());
				res.setAckNo(returnperiods1.getArnNo());

				String dateTime = returnperiods1.getTime();
				if (dateTime != null) {
					res.setDate(dateTime);
				}
				listDto.add(res);
			});
		}
		return listDto;

	}

	private static void setStatusReturnPeriodWise(
			ComplienceSummeryRespDto res,
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

}
