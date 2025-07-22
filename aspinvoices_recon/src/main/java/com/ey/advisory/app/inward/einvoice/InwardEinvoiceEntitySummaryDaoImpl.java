package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.ImmutableList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@Component("InwardEinvoiceEntitySummaryDaoImpl")
public class InwardEinvoiceEntitySummaryDaoImpl
		implements InwardEinvoiceEntitySummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("UserCreationRepository")
	UserCreationRepository repo;

	private static final List<String> GETIRN_SUPPLY_TYPES = ImmutableList.of(
			APIConstants.INV_TYPE_B2B, APIConstants.INV_TYPE_SEZWP,
			APIConstants.INV_TYPE_SEZWOP, APIConstants.INV_TYPE_DXP,
			APIConstants.INV_TYPE_EXPWP, APIConstants.INV_TYPE_EXPWOP);

	@Override
	public List<InwardEinvoiceEntitySummaryResponseDto> findTableData(
			InwardEinvoiceEntitySummaryReqDto dto, Map<String, String> regMap,
			Map<String, String> stateNames,
			Map<String, String> authTokenStatus) {
		List<InwardEinvoiceEntitySummaryResponseDto> respDto = new ArrayList<>();

		try {

			List<Object[]> list = getEntityLevelData(dto);

			Map<String, LocalDateTime> resultMap = getGstinAndtimeStampMap(dto);

			respDto = list.stream()
					.map(o -> convert(o, resultMap, regMap, stateNames,
							authTokenStatus))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			String msg = String.format("Error Occured while executing "
					+ "InwardEinvoiceEntitySummaryDaoImpl Entity "
					+ "Screen query", ex);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		return respDto;

	}

	/**
	 * @param dto
	 * @return
	 */
	private List<Object[]> getEntityLevelData(
			InwardEinvoiceEntitySummaryReqDto dto) {
		String gstins = "";
		String supplyType = "";

		if (!CollectionUtils.isEmpty(dto.getGstins())) {
			gstins = String.join(",", dto.getGstins());
		}

		StoredProcedureQuery sp = entityManager
				.createStoredProcedureQuery("USP_EINVOICE_IRN_INWARD_DETAIL");

		sp.registerStoredProcedureParameter("P_GSTIN", String.class,
				ParameterMode.IN);

		sp.setParameter("P_GSTIN", gstins);

		sp.registerStoredProcedureParameter("P_TAX_PERIOD", String.class,
				ParameterMode.IN);

		sp.setParameter("P_TAX_PERIOD", dto.getTaxPeriod());

		sp.registerStoredProcedureParameter("P_SUPPLY_TYPE", String.class,
				ParameterMode.IN);

		if (!CollectionUtils.isEmpty(dto.getSupplyType())) {
			supplyType = String.join(",", dto.getSupplyType());
		} else {
/*			 if("DXP".equalsIgnoreCase(dto.getType())){
	               	dto.setType(APIConstants.INV_TYPE_DEXP);
	               }
*/			supplyType = String.join(",", GETIRN_SUPPLY_TYPES);
		}

		sp.setParameter("P_SUPPLY_TYPE", supplyType);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Data proc Executed USP_EINVOICE_IRN_INWARD_DETAIL"
							+ ": gstin '%s', taxPeriod %s and supplyType %s",
					dto.getGstins(), dto.getTaxPeriod(), supplyType);
			LOGGER.debug(msg);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = sp.getResultList();
		return list;
	}

	/**
	 * @param dto
	 * @param resultMap
	 */
	private Map<String, LocalDateTime> getGstinAndtimeStampMap(
			InwardEinvoiceEntitySummaryReqDto dto) {
		Map<String, LocalDateTime> resultMap = new HashMap<>();
		String condtion1 = queryCondition1(dto);
		String queryString1 = createQuery1(condtion1);

		Query q1 = entityManager.createNativeQuery(queryString1);

		q1.setParameter("gstins", dto.getGstins());
		q1.setParameter("taxPeriod", dto.getTaxPeriod());

		if (!CollectionUtils.isEmpty(dto.getSupplyType())) {
			q1.setParameter("supplyType", dto.getSupplyType());
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list1 = q1.getResultList();

		for (Object[] row : list1) {
			String gstin = (String) row[0];
			Timestamp maxCreatedOn = (Timestamp) row[1];
			LocalDateTime createdOn = EYDateUtil
					.toISTDateTimeFromUTC(maxCreatedOn.toLocalDateTime());
			resultMap.put(gstin, createdOn);
		}
		return resultMap;
	}

	private InwardEinvoiceEntitySummaryResponseDto convert(Object[] arr,
			Map<String, LocalDateTime> resultMap, Map<String, String> regMap,
			Map<String, String> stateNames,
			Map<String, String> authTokenStatus) {
		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " InwardEinvoiceEntitySummaryResponseDto object";
			LOGGER.debug(str);
		}

		InwardEinvoiceEntitySummaryResponseDto convert = new InwardEinvoiceEntitySummaryResponseDto();

		convert.setGstin(arr[0] != null ? arr[0].toString() : null);
		convert.setStatus(arr[1] != null ? arr[1].toString() : null);
		convert.setCountSuppGstn(
				arr[2] != null ? Integer.parseInt(arr[2].toString()) : null);
		convert.setCountEinv(
				arr[3] != null ? Integer.parseInt(arr[3].toString()) : null);
		convert.setCanclEinv(
				arr[5] != null ? Integer.parseInt(arr[5].toString()) : null);
		convert.setActiveEinv(
				arr[4] != null ? Integer.parseInt(arr[4].toString()) : null);
		convert.setTotlInvAmt(
				arr[6] != null ? new BigDecimal(arr[6].toString()) : null);

		if (!resultMap.isEmpty()) {
			String timeStmp = resultMap.get(convert.getGstin()).toString()
					.replace("T", " ");
			String[] timeStmpArr = timeStmp.split("\\.");
			String dateTime = timeStmpArr[0];
			convert.setGetCallTimestamp(dateTime);
		}
		convert.setAuthToken(
				authTokenStatus.get(convert.getGstin()).toString());
		convert.setState(stateNames.get(convert.getGstin()).toString());
		convert.setRegType(regMap.get(convert.getGstin()).toString());

		return convert;
	}

	private String createQuery1(String condition) {

		StringBuilder query = new StringBuilder();

		query.append("SELECT GSTIN, MAX(Created_on) AS Max_Created_on "
				+ " FROM GETANX1_BATCH_TABLE "
				+ " WHERE API_SECTION = 'INWARD_EINVOICE' "
				+ "      AND RETURN_PERIOD =:taxPeriod"
				+ "      AND GSTIN IN (:gstins)" + condition
				+ "    GROUP BY GSTIN");
		return query.toString();
	}

	private String queryCondition1(InwardEinvoiceEntitySummaryReqDto reqDto) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin InwardEinvoiceEntitySummaryDaoImpl"
					+ ".queryCondition1() ";
			LOGGER.debug(msg);
		}

		StringBuilder condition = new StringBuilder();

		if (!CollectionUtils.isEmpty(reqDto.getSupplyType())) {
			condition.append(" AND GET_TYPE IN (:supplyType) ");
		}

		return condition.toString();
	}

	@Override
	public List<InwardEinvoiceDetailedInfoResponseDto> findTableDetailedData(
			InwardEinvoiceEntitySummaryReqDto dto, Map<String, String> regMap,
			Map<String, String> stateNames, Map<String, String> authTokenStatus,
			Map<String, String> emailMap) {

		List<InwardEinvoiceDetailedInfoResponseDto> respDto = new ArrayList<>();

		try {

			List<Object[]> list = getEntityLevelData(dto);

			Map<String, LocalDateTime> resultMap = getGstinAndtimeStampMap(dto);

			respDto = list.stream()
					.map(o -> convertDetailedList(o, resultMap, regMap,
							stateNames, authTokenStatus, emailMap))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			String msg = String.format("Error Occured while executing "
					+ "InwardEinvoiceEntitySummaryDaoImpl Detailed "
					+ "Screen query", ex);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		return respDto;

	}

	private InwardEinvoiceDetailedInfoResponseDto convertDetailedList(
			Object[] arr, Map<String, LocalDateTime> resultMap,
			Map<String, String> regMap, Map<String, String> stateNames,
			Map<String, String> authTokenStatus, Map<String, String> emailMap) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " InwardEinvoiceDetailedInfoResponseDto object";
			LOGGER.debug(str);
		}

		InwardEinvoiceDetailedInfoResponseDto convert = new InwardEinvoiceDetailedInfoResponseDto();

		convert.setMode("Manual");
		convert.setGstin(arr[0] != null ? arr[0].toString() : null);

		convert.setStatus(arr[1] != null ? arr[1].toString() : null);

		String userName = arr[7] != null ? arr[7].toString() : null;

		boolean isAuto = arr[8] != null ? Boolean.valueOf(arr[8].toString())
				: false;

		if (isAuto) {
			convert.setMode("Auto");
		} else {
			convert.setMode("Manual");
		}
		convert.setRequestedBy(userName);

		/*
		 * if (userName != null) { if (userName.equalsIgnoreCase("SYSTEM")) {
		 * convert.setRequestedBy("SYSTEM"); } else {
		 * convert.setRequestedBy(emailMap.get(userName)); } } else {
		 * convert.setRequestedBy("Auto"); convert.setMode("Auto"); }
		 */
		if (!resultMap.isEmpty()) {
			String timeStmp = resultMap.get(convert.getGstin()).toString()
					.replace("T", " ");
			String[] timeStmpArr = timeStmp.split("\\.");
			String dateTime = timeStmpArr[0];
			convert.setCompletedOn(dateTime);
		}

		convert.setAuthToken(
				authTokenStatus.get(convert.getGstin()).toString());
		convert.setState(stateNames.get(convert.getGstin()).toString());
		convert.setRegType(regMap.get(convert.getGstin()).toString());

		return convert;

	}

	@Override
	public List<InwardEinvoiceStatusScreenResponseDto> findStatusData(
			InwardEinvoiceEntitySummaryReqDto dto, Map<String, String> regMap,
			Map<String, String> stateNames,
			Map<String, String> authTokenStatus) {

		List<InwardEinvoiceStatusScreenResponseDto> respDto = new ArrayList<>();

		try {
			StoredProcedureQuery sp = entityManager.createStoredProcedureQuery(
					"USP_EINVOICE_IRN_SECTION_DETAIL");

			sp.registerStoredProcedureParameter("P_GSTIN", String.class,
					ParameterMode.IN);

			sp.setParameter("P_GSTIN", dto.getGstins().get(0));

			sp.registerStoredProcedureParameter("P_TAX_PERIOD", String.class,
					ParameterMode.IN);

			sp.setParameter("P_TAX_PERIOD", dto.getTaxPeriod());

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Data proc Executed"
								+ " USP_EINVOICE_IRN_SECTION_DETAIL"
								+ ": gstin '%s', taxPeriod %s ",
						dto.getGstins().get(0), dto.getTaxPeriod());
				LOGGER.debug(msg);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> sectionRecords = sp.getResultList();

			Map<String, LocalDateTime> resultMap = getGstinAndtimeStampMap(dto);

			respDto = sectionRecords.stream()
					.map(o -> convertStatusList(o, resultMap, regMap,
							stateNames, authTokenStatus,
							dto.getGstins().get(0)))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			String msg = String.format("Error Occured while executing "
					+ "InwardEinvoiceEntitySummaryDaoImpl section level status "
					+ "Screen query", ex);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		return respDto;

	}

	private InwardEinvoiceStatusScreenResponseDto convertStatusList(
			Object[] arr, Map<String, LocalDateTime> resultMap,
			Map<String, String> regMap, Map<String, String> stateNames,
			Map<String, String> authTokenStatus, String gstin) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " InwardEinvoiceStatusScreenResponseDto object";
			LOGGER.debug(str);
		}

		InwardEinvoiceStatusScreenResponseDto convert = new InwardEinvoiceStatusScreenResponseDto();

		convert.setGstin(gstin);

		convert.setStatus(arr[1] != null ? arr[1].toString() : "Not Initiated");

		if (!resultMap.isEmpty()) {
			String timeStmp = resultMap.get(gstin).toString().replace("T", " ");
			String[] timeStmpArr = timeStmp.split("\\.");
			String dateTime = timeStmpArr[0];
			convert.setOverallTimeStamp(dateTime);
		}

		convert.setB2bStatus(
				arr[3] != null ? arr[3].toString() : "Not Initiated");
		convert.setB2bTimeStamp(arr[4] != null ? (arr[4]).toString() : null);

		convert.setDexpStatus(
				arr[9] != null ? arr[9].toString() : "Not Initiated");
		convert.setDexpTimeStamp(arr[10] != null ? (arr[10]).toString() : null);

		convert.setExpwopStatus(
				arr[13] != null ? arr[13].toString() : "Not Initiated");
		convert.setExpwopTimeStamp(
				arr[14] != null ? (arr[14]).toString() : null);

		convert.setExpwpStatus(
				arr[11] != null ? arr[11].toString() : "Not Initiated");
		convert.setExpwpTimeStamp(
				arr[12] != null ? (arr[12]).toString() : null);

		convert.setSezwopStatus(
				arr[7] != null ? arr[7].toString() : "Not Initiated");
		convert.setSezwopTimeStamp(arr[8] != null ? (arr[8]).toString() : null);

		convert.setSezwpStatus(
				arr[5] != null ? arr[5].toString() : "Not Initiated");
		convert.setSezwpTimeStamp(arr[6] != null ? (arr[6]).toString() : null);

		convert.setAuthToken(authTokenStatus.get(gstin).toString());
		convert.setState(stateNames.get(gstin).toString());
		convert.setRegType(regMap.get(gstin).toString());

		return convert;

	}

}
