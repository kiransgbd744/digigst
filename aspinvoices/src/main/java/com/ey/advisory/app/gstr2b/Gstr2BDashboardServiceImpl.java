package com.ey.advisory.app.gstr2b;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Hema G M
 *
 */

@Slf4j
@Component("Gstr2BDashboardServiceImpl")
public class Gstr2BDashboardServiceImpl implements Gstr2BDashboardService {

	@Autowired
	@Qualifier("Gstr2BDashBoardDaoImpl")
	private Gstr2BDashBoardDao batchDao;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr2BDashBoardRespDto> getStatusData(List<String> gstins,
			int derivedStartPeriod, int derivedEndPeriod,
			Map<String, String> stateNames,
			Map<String, String> authTokenStatus) {

		List<Gstr2BDashBoardRespDto> dtoList = new ArrayList<>();

		try {

			List<Gstr2BDashBoardErrorDto> statusDetail = batchDao
					.getErrorCodeforGetCall(gstins, derivedStartPeriod,
							derivedEndPeriod);

			Map<Object, List<Gstr2BDashBoardErrorDto>> errorMap = statusDetail
					.stream().collect(Collectors.groupingBy(e -> e.getGstin()));

			dtoList = gstins.stream()
					.distinct().map(o -> convertToDto(o, stateNames,
							authTokenStatus, errorMap))
					.distinct().collect(Collectors.toList());

			dtoList.sort(
					Comparator.comparing(Gstr2BDashBoardRespDto::getGstin));

		} catch (Exception ex) {
			String msg = String.format("Error occured in "
					+ "Gstr2BDashboardServiceImpl.getStatusData() method "
					+ "%s gstins", gstins);
			LOGGER.error(msg, ex);
			ex.printStackTrace();

		}
		return dtoList;

	}

	private Gstr2BDashBoardRespDto convertToDto(String o,
			Map<String, String> stateNames, Map<String, String> authTokenStatus,
			Map<Object, List<Gstr2BDashBoardErrorDto>> errorMap) {

		Gstr2BDashBoardRespDto dto = new Gstr2BDashBoardRespDto();

		dto.setGstin(o);
		dto.setStateName(stateNames.get(o));
		dto.setAuthStatus(authTokenStatus.get(o));
		dto.setStatus2B(determine2BStatus(o));

		List<Gstr2BDashBoardErrorDto> errorList = errorMap.get(o) != null
				? errorMap.get(o) : new ArrayList<>();

		if (errorList != null && !errorList.isEmpty()) {
			List<InnerDetailDto> collect = errorList.stream()
					.map(e -> setValues(e)).collect(Collectors.toList());

			dto.setTaxPeriodDetails(collect);
		}
		return dto;
	}

	private InnerDetailDto setValues(Gstr2BDashBoardErrorDto entity) {

		InnerDetailDto txDto = new InnerDetailDto();

		txDto.setFilePath(entity.getFilePath());
		/*
		 * if (entity.getFilePath() != null || entity.getDocId() != null) {
		 * txDto.setFlag(true); } else { txDto.setFlag(false); }
		 */
		txDto.setInitiatedOn(entity.getCreatedOn());
		
		txDto.setReportStatus(entity.getStatus());
		
		txDto.setTaxPeriod(entity.getTaxPeriod());
		
		if (entity.getErrorCode() != null &&
				entity.getErrorMsg() != null) {
			
			String trimmedErrorMsg = entity.getErrorMsg()
					.substring(entity.getErrorCode().length(),
					entity.getErrorMsg().length());
			
			txDto.setErrorMsg(entity.getErrorCode()	
					.concat(" :: " + trimmedErrorMsg));
		}
		return txDto;

	}
	
	/*Determine and return the status based on genDate*/
	private String determine2BStatus(String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside determine2BStatus method");
		}
		LocalDate currentDate = LocalDate.now();
		LocalDate periodStart = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 14);
		YearMonth applicableMonth = currentDate.isBefore(periodStart) ? YearMonth.from(currentDate.minusMonths(2))
				: YearMonth.from(currentDate.minusMonths(1));

		String formattedMonth = applicableMonth.getMonth().name().substring(0, 1).toUpperCase()
                + applicableMonth.getMonth().name().substring(1).toLowerCase();
        String applicableMonthFormatted = formattedMonth.substring(0, 3) + " " + applicableMonth.getYear();
		String formattedTaxPeriod = String.format("%02d%d", applicableMonth.getMonthValue(), applicableMonth.getYear());

		String queryStr = "SELECT max(GENDT) FROM GETGSTR2B_ITC_ITM "
				+ "WHERE RGSTIN = :gstin AND TAX_PERIOD = :taxPeriod AND IS_DELETE = false";
		Query query = entityManager.createNativeQuery(queryStr);
		query.setParameter("gstin", gstin);
		query.setParameter("taxPeriod", formattedTaxPeriod);
		Object genDateObj = query.getSingleResult();

		if (genDateObj == null) {
			return "Not Initiated (" + applicableMonthFormatted + ")";
		}
		LocalDate genDate = null;
		if (genDateObj instanceof java.sql.Date) {
			java.sql.Date sqlDate = (java.sql.Date) genDateObj;
			genDate = sqlDate.toLocalDate();
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("2B Generation date{}", genDate);
		}

		LocalDate startOfCutoff = applicableMonth.plusMonths(1).atDay(14);
		LocalDate endOfCutoff = applicableMonth.plusMonths(2).atDay(13);

		if (genDate.isEqual(startOfCutoff)) {
			return "Draft (" + applicableMonthFormatted + ")";
		} else if (!genDate.isBefore(startOfCutoff) && !genDate.isAfter(endOfCutoff)) {
			return "Regenerated (" + applicableMonthFormatted + ")";
		}
		return "Not Initiated (" + applicableMonthFormatted + ")";
	}

}
