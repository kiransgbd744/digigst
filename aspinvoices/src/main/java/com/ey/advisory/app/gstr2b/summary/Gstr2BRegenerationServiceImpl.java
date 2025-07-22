package com.ey.advisory.app.gstr2b.summary;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.gstr2b.Gstr2BDashBoardErrorDto;
import com.ey.advisory.app.gstr2b.Gstr2BRegenerationDashBoardDao;
import com.ey.advisory.app.gstr2b.Gstr2BRegenerationRespDto;
import com.ey.advisory.app.gstr2b.InnerDetailDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Component("Gstr2BRegenerationServiceImpl")
@Slf4j
public class Gstr2BRegenerationServiceImpl
		implements Gstr2BRegenerationService {

	@Autowired
	@Qualifier("Gstr2BRegenerationDashBoardDaoImpl")
	private Gstr2BRegenerationDashBoardDao batchDao;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Override
	public List<Gstr2BRegenerationRespDto> getStatusData(List<String> gstins,
			int derivedStartPeriod, int derivedEndPeriod,
			Map<String, String> stateNames, Map<String, String> authTokenStatus,
			String appendMonthYear) {
		List<Gstr2BRegenerationRespDto> dtoList = new ArrayList<>();
		try {
			List<Gstr2BDashBoardErrorDto> statusDetail = batchDao
					.getErrorCodeforGetCall(gstins, derivedStartPeriod,
							derivedEndPeriod);

			Map<Object, List<Gstr2BDashBoardErrorDto>> errorMap = statusDetail
					.stream().collect(Collectors.groupingBy(e -> e.getGstin()));

			String currtaxPeriod = (String.valueOf(
					(Calendar.getInstance().get(Calendar.MONTH)) + 1 > 9
							? String.valueOf(
									(Calendar.getInstance().get(Calendar.MONTH))
											+ 1)
							: "0" + String.valueOf(
									(Calendar.getInstance().get(Calendar.MONTH))
											+ 1)))
					+ String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

			List<String> totlTaxPeriods = getTaxPeriods(
					Integer.parseInt(appendMonthYear.substring(0, 4)),
					currtaxPeriod);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" Total elligible currtaxPeriod {} ",
						currtaxPeriod.toCharArray());
			}

			Map<String, GstrReturnStatusEntity> statusMap = getGstrReturnStatusMap(
					totlTaxPeriods,gstins);
			
			dtoList = gstins.stream().distinct()
					.map(o -> convertToDto(o, stateNames, authTokenStatus,
							errorMap, totlTaxPeriods, statusMap))
					.distinct().collect(Collectors.toList());

			dtoList.sort(
					Comparator.comparing(Gstr2BRegenerationRespDto::getGstin));

		} catch (Exception ex) {
			String msg = String.format("Error occured in "
					+ "Gstr2BDashboardServiceImpl.getStatusData() method "
					+ "%s gstins", gstins);
			LOGGER.error(msg, ex);
			ex.printStackTrace();

		}
		return dtoList;
	}

	private Gstr2BRegenerationRespDto convertToDto(String o,
			Map<String, String> stateNames, Map<String, String> authTokenStatus,
			Map<Object, List<Gstr2BDashBoardErrorDto>> errorMap,
			List<String> totlTaxPeriods, Map<String, GstrReturnStatusEntity> statusMap) {

		// LocalDate currentDate = LocalDate.now();

		Gstr2BRegenerationRespDto dto = new Gstr2BRegenerationRespDto();

		dto.setGstin(o);
		dto.setStateName(stateNames.get(o));
		dto.setAuthStatus(authTokenStatus.get(o));

		List<Gstr2BDashBoardErrorDto> errorList = errorMap.get(o) != null
				? errorMap.get(o) : new ArrayList<>();

		Map<String, Gstr2BDashBoardErrorDto> gstinErrorMap = errorList.stream()
				.collect(Collectors.toMap(Gstr2BDashBoardErrorDto::getTaxPeriod,
						Function.identity()));

		List<InnerDetailDto> taxPeriodDetails = new ArrayList<>();

		// run loop of all the eligible taxperiods 202402 022024

		for (String taxPeriod : totlTaxPeriods) {
			InnerDetailDto txDto = new InnerDetailDto();

			txDto.setTaxPeriod(taxPeriod);

			if (!gstinErrorMap.isEmpty()
					&& gstinErrorMap.containsKey(taxPeriod)) {
				Gstr2BDashBoardErrorDto errorDto = gstinErrorMap.get(taxPeriod);

				txDto.setInitiatedOn(errorDto.getCreatedOn());

				txDto.setReportStatus(errorDto.getStatus());
				
				if (errorDto.getErrorCode() != null &&
						errorDto.getErrorMsg() != null) {

				String trimmedErrorMsg = errorDto.getErrorMsg().substring(
						0,
						errorDto.getErrorMsg().length());

				txDto.setErrorMsg(errorDto.getErrorCode()
						.concat(" :: " + trimmedErrorMsg));

				if ("IMS2B007".equalsIgnoreCase(errorDto.getErrorCode())) {
					txDto.setReGenerate2b(false);
				}
				}
			}
			String gstinTaxperiod = o + "|" + taxPeriod;
			
			if (!statusMap.isEmpty() && statusMap.containsKey(gstinTaxperiod)) {
				txDto.setReGenerate2b(false);
			}
			taxPeriodDetails.add(txDto);
		}
		dto.setTaxPeriodDetails(taxPeriodDetails);

		return dto;
	}

	public static List<String> getTaxPeriods(int year, String currtaxPeriod) {
		List<String> taxPeriods = new ArrayList<>();
		Year currentYear = Year.now();
		int yearValue = currentYear.getValue();

		// Assuming tax year starts in April
		Calendar startPeriod = Calendar.getInstance();
		startPeriod.set(year, Calendar.APRIL, 1);
		Calendar endPeriod = Calendar.getInstance();
		endPeriod.set(year + 1, Calendar.MARCH, 31);

		// Iterate over the months in the tax period
		Calendar currentPeriod = (Calendar) startPeriod.clone();
		// System.out.println(currentPeriod);
		while (currentPeriod.compareTo(endPeriod) <= 0) {
			int month = currentPeriod.get(Calendar.MONTH) + 1;
			int yearInPeriod = currentPeriod.get(Calendar.YEAR);
			String taxPeriod = String.valueOf(month >= 10 ? month : "0" + month)
					+ String.valueOf(yearInPeriod);
			if (year == yearValue) {

				if (taxPeriod.equalsIgnoreCase(currtaxPeriod)) {
					taxPeriods.add(taxPeriod);
					break;
				}
			}
			taxPeriods.add(taxPeriod);

			currentPeriod.add(Calendar.MONTH, 1);
		}

		return taxPeriods;
	}

	/*public Map<String, GstrReturnStatusEntity> getGstrReturnStatusMap(
			List<String> totlTaxPeriods) {
		Map<String, GstrReturnStatusEntity> gstrReturnStatus = new HashMap<>();

		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.getAllwithTaxPeriod(totlTaxPeriods);

		LOGGER.debug("filedRecords {} ", filedRecords);
		if (filedRecords == null || filedRecords.isEmpty()) {
			return gstrReturnStatus;
		} else {
			gstrReturnStatus = filedRecords.stream()
					.collect(Collectors.toMap(
							GstrReturnStatusEntity::getTaxPeriod,
							GstrReturnStatusEntity));
		}
		LOGGER.debug("gstrReturnStatus {} ", gstrReturnStatus);
		return gstrReturnStatus;

	}*/
	public Map<String, GstrReturnStatusEntity> getGstrReturnStatusMap(
			List<String> totlTaxPeriods, List<String> gstins) {
		Map<String, GstrReturnStatusEntity> gstrReturnStatus = new HashMap<>();

		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.getAllwithTaxPeriod(totlTaxPeriods,gstins);

		LOGGER.debug("filedRecords {} ", filedRecords);
		for(GstrReturnStatusEntity entity : filedRecords){
			
			gstrReturnStatus.put(entity.getGstin() + "|" + entity.getTaxPeriod(), entity);
		}
		LOGGER.debug("gstrReturnStatus {} ", gstrReturnStatus);
		return gstrReturnStatus;

	}
public static void main(String[] args) {
	
	String trimmedErrorMsg = "API Authorization Failed".substring(
			0,
			"API Authorization Failed".length());

	System.out.println("RET2B1010"
			.concat(" :: " + trimmedErrorMsg));
}
}
