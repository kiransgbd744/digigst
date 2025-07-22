package com.ey.advisory.app.data.returns.compliance.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.entities.client.ClientFilingStatusEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.ClientFilingStatusRepositoty;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.services.noncomplaintvendor.NCVendorCommHelperService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.ey.advisory.core.dto.ReturnStusFilingDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("CompienceHistoryServiceImpl")
@Slf4j
public class CompienceHistoryServiceImpl {

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnstatusRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("ClientFilingStatusRepositoty")
	private ClientFilingStatusRepositoty clientFilingStatusRepositoty;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	@Qualifier("BasicGstr6SecCommonParam")
	BasicGstr6SecCommonParam basicGstr6SecCommonParam;

	@Autowired
	private NCVendorCommHelperService helperService;

	private static final String NOT_INITIATED = "Not Initiated";
	private static final String IN_PROGRESS = "InProgress";
	private static final String NOT_FILED = "Not Filed";
	private static final String FILED = "Filed";
	private static final String COMPLETED = "Completed";
	private static final List<String> RETURN_TYPE = ImmutableList.of(
			GSTConstants.GSTR1, GSTConstants.GSTR3B, GSTConstants.GSTR6,
			"GSTR7");

	private static final DateTimeFormatter FOMATTER = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	public List<ReturnStusFilingDto> findcomplienceSummeryRecords(
			Gstr2aProcessedDataRecordsReqDto reqdto) {
		String financialYear = reqdto.getFinancialYear();
		String returnType = reqdto.getReturnType();
		Map<String, List<String>> dataSecAttrs = reqdto.getDataSecAttrs();
		// overall taxPeriods
		List<String> returnperiodList = GenUtil
				.extractTaxPeriodsFromFY(financialYear, returnType);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("taxPeriod List{}", returnperiodList);

		}
		List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		gstinList = getGstins(gstinList, Long.parseLong(reqdto.getEntity()),
				reqdto.getReturnType(), dataSecAttrs);
		if (gstinList.isEmpty()) {
			String msg = String.format("No GSTIN is Onboarded for this Return Type");
			LOGGER.error(msg);
			return null;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("gstinList{}", gstinList);

		}
		List<ReturnStusFilingDto> dtoList = new ArrayList<>();
		for (String gstin : gstinList) {
			ReturnStusFilingDto dto = new ReturnStusFilingDto();
			dto.setGstin(gstin);
			List<ReturnStusFilingDto.ReturnPeriodDto> retList = new ArrayList<>();
			for (String returnperiod : returnperiodList) {
				ReturnStusFilingDto.ReturnPeriodDto retDto = new ReturnStusFilingDto.ReturnPeriodDto();
				retDto.setMonth(returnperiod);
				retDto.setStatus(NOT_INITIATED);
				retList.add(retDto);
			}
			dto.setReturnperiods(retList);
			dtoList.add(dto);
		}
		List<ClientFilingStatusEntity> clientFilingStatusEntityList = clientFilingStatusRepositoty
				.findByFinancialYearAndGstinInAndReturnType(financialYear,
						gstinList, returnType);
		List<String> subgstin = new ArrayList<>();
		clientFilingStatusEntityList.stream()
				.forEach(dto -> subgstin.add(dto.getGstin()));
		String currentFy = GenUtil.getCurrentFinancialYear();
		boolean isCurrentFy = currentFy.equals(financialYear);

		clientFilingStatusEntityList.stream().forEach(dto -> {
			String gstin = dto.getGstin();
			dtoList.stream().forEach(clientDto -> {
				if (gstin.equalsIgnoreCase(clientDto.getGstin())) {
					clientDto.getReturnperiods().forEach(retDto -> {
						retDto.setStatus(IN_PROGRESS);
						if (COMPLETED.equalsIgnoreCase(dto.getStatus()) ||
								"Failed".equalsIgnoreCase(dto.getStatus())) {
							retDto.setStatus(NOT_FILED);
						}
					});
				}
			});
		});
		List<GstrReturnStatusEntity> returnStatusEntities = returnstatusRepo
				.findByGstinInAndReturnTypeAndTaxPeriodInAndIsCounterPartyGstin(
						gstinList, returnType, returnperiodList, false);

		Map<String, String> filingTypeMap = new HashMap<>();

//		returnStatusEntities.forEach((obj) -> {
//			helperService.getTypeGstFiling(returnStatusEntities, obj,
//					filingTypeMap);
//		});

		if (isCurrentFy) {
			// get valid confirmed taxPeriods
			Map<String, Boolean> map = helperService.getValidConfirmantRange(
					gstinList, returnType, financialYear, filingTypeMap,
					isCurrentFy);

			dtoList.stream().forEach(dto -> {
				String gstin = dto.getGstin();
				dto.getReturnperiods().stream().forEach(returPeriodDto -> {
					String key = gstin + returPeriodDto.getMonth() + returnType;
					Boolean boolean1 = map.get(key);
					if (boolean1 == null || !boolean1) {
						returPeriodDto.setStatus(NOT_INITIATED);

					}
					if (isCurrentFy
							&& RETURN_TYPE.contains(returnType.toUpperCase())
							&& subgstin.contains(gstin)) {
						String Currmonth = String
								.valueOf(LocalDate.now().getMonthValue());
						if (Currmonth.length() == 1) {
							Currmonth = "0" + Currmonth;
						}
						String Premonth = String
								.valueOf(LocalDate.now().getMonthValue() - 1);
						if (Premonth.length() == 1) {
							Premonth = "0" + Premonth;
						}
						String year = String.valueOf(LocalDate.now().getYear());
						String currentPeriod = Currmonth + year;
						String prePeriod = Premonth + year;
						if (returPeriodDto.getMonth()
								.equalsIgnoreCase(currentPeriod)
								|| returPeriodDto.getMonth()
										.equalsIgnoreCase(prePeriod)) {
							returPeriodDto.setStatus(NOT_FILED);
						}

					}
				});

			});
		}
		returnStatusEntities.stream().forEach(statusDto -> {
			String statusgstin = statusDto.getGstin();
			String taxperiod = statusDto.getTaxPeriod();
			// String status = statusDto.getStatus();
			dtoList.stream().forEach(dto -> {
				String gstin = dto.getGstin();
				dto.getReturnperiods().stream().forEach(retDto -> {
					if (gstin.equalsIgnoreCase(statusgstin)
							&& taxperiod.equalsIgnoreCase(retDto.getMonth())
							//&& subgstin.contains(gstin)
							) {
						// if (status.equalsIgnoreCase(FILED)) {
						retDto.setStatus(FILED);
						retDto.setArnNo(statusDto.getArnNo());
						if(statusDto.getFilingDate() != null){
							String newdate = FOMATTER.format(statusDto.getFilingDate());
							retDto.setTime(newdate);
						}
						
						/*
						 * } else { retDto.setStatus(NOT_FILED); }
						 */
					}
				});
			});
		});

		return dtoList;
	}

	private List<String> getGstins(List<String> gstinList, Long entityId,
			String returnType, Map<String, List<String>> dataSecAttrs) {
		if (!gstinList.isEmpty())
			return gstinList;
		if ("GSTR6".equalsIgnoreCase(returnType)) {
			Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
			dto.setDataSecAttrs(dataSecAttrs);
			dto.setEntityId(Arrays.asList(entityId));
			Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
					.setGstr6DataSecuritySearchParams(dto);
			return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);
		}
		if ("GSTR7".equalsIgnoreCase(returnType)) {
			Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
			dto.setDataSecAttrs(dataSecAttrs);
			dto.setEntityId(Arrays.asList(entityId));
			Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
					.setGstr7DataSecuritySearchParams(dto);
			return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);
		}
		// gstr1,gstr3b,gstr9,itc04
		Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
		dto.setDataSecAttrs(dataSecAttrs);
		dto.setEntityId(Arrays.asList(entityId));
		Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr1DataSecuritySearchParams(dto);

		return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);

	}
}
