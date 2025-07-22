package com.ey.advisory.app.reconewbvsitc04;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */

@Slf4j
@Component("EwbVsItc04SummaryInitiateReconDaoImpl")
public class EwbVsItc04SummaryInitiateReconDaoImpl
		implements EwbVsItc04SummaryInitiateReconDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<EwbVsItc04SummaryInitiateReconLineItemDto> ewb3WayInitiateRecon(
			EwbVsItc04SummaryInitiateReconDto request) {

		Integer fromTaxPeriodItc = GenUtil
				.getDerivedTaxPeriod(request.getFromTaxPeriod());
		Integer toTaxPeriodItc = GenUtil
				.getDerivedTaxPeriod(request.getToTaxPeriod());
		Integer fromTaxPeriodEwb = null;
		Integer toTaxPeriodEwb = null;

		String fy1 = request.getFy().substring(0, 4);
		String fy2 = request.getFy().substring(5, 9);

		if (request.getFromTaxPeriod().contains("17")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "04");
			// toTaxPeriodEwb = Integer.parseInt(fy1 + "09");
		} else if (request.getFromTaxPeriod().contains("18")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "10");
			// toTaxPeriodEwb = Integer.parseInt(fy2 + "03");
		} else if (request.getFromTaxPeriod().contains("13")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "04");
			// toTaxPeriodEwb = Integer.parseInt(fy1 + "06");
		} else if (request.getFromTaxPeriod().contains("14")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "07");
			// toTaxPeriodEwb = Integer.parseInt(fy1 + "09");
		} else if (request.getFromTaxPeriod().contains("15")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "10");
			// toTaxPeriodEwb = Integer.parseInt(fy1 + "12");
		} else if (request.getFromTaxPeriod().contains("16")) {
			fromTaxPeriodEwb = Integer.parseInt(fy2 + "01");
			// toTaxPeriodEwb = Integer.parseInt(fy2 + "03");
		}

		if (request.getToTaxPeriod().contains("17")) {
			// fromTaxPeriodEwb = Integer.parseInt(fy1 + "04");
			toTaxPeriodEwb = Integer.parseInt(fy1 + "09");
		} else if (request.getToTaxPeriod().contains("18")) {
			// fromTaxPeriodEwb = Integer.parseInt(fy1 + "10");
			toTaxPeriodEwb = Integer.parseInt(fy2 + "03");
		} else if (request.getToTaxPeriod().contains("13")) {
			// fromTaxPeriodEwb = Integer.parseInt(fy1 + "04");
			toTaxPeriodEwb = Integer.parseInt(fy1 + "06");
		} else if (request.getToTaxPeriod().contains("14")) {
			// fromTaxPeriodEwb = Integer.parseInt(fy1 + "07");
			toTaxPeriodEwb = Integer.parseInt(fy1 + "09");
		} else if (request.getToTaxPeriod().contains("15")) {
			// fromTaxPeriodEwb = Integer.parseInt(fy1 + "10");
			toTaxPeriodEwb = Integer.parseInt(fy1 + "12");
		} else if (request.getToTaxPeriod().contains("16")) {
			// fromTaxPeriodEwb = Integer.parseInt(fy2 + "01");
			toTaxPeriodEwb = Integer.parseInt(fy2 + "03");
		}

		String criteria = request.getCriteria();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {

					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}

		try {

			StoredProcedureQuery storedProc = null;

			LOGGER.debug(
					"Invoking USP_EWBVSITC04_PRE_RECON_SUMMARY Stored Proc");

			storedProc = entityManager.createStoredProcedureQuery(
					"USP_EWBVSITC04_PRE_RECON_SUMMARY");

			storedProc.registerStoredProcedureParameter("P_CRITERIA",
					String.class, ParameterMode.IN);

			storedProc.setParameter("P_CRITERIA", criteria);

			storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
					String.class, ParameterMode.IN);

			storedProc.setParameter("P_GSTIN_LIST",
					String.join(",", gstinList));

			storedProc.registerStoredProcedureParameter("EWB_FROM_TAXPERIOD",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("EWB_FROM_TAXPERIOD", fromTaxPeriodEwb);

			storedProc.registerStoredProcedureParameter("EWB_TO_TAXPERIOD",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("EWB_TO_TAXPERIOD", toTaxPeriodEwb);

			storedProc.registerStoredProcedureParameter("ITC_FROM_TAXPERIOD",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("ITC_FROM_TAXPERIOD", fromTaxPeriodItc);
			
			storedProc.registerStoredProcedureParameter("ITC_TO_TAXPERIOD",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("ITC_TO_TAXPERIOD", toTaxPeriodItc);

			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();

			LOGGER.debug("Coverting object to the DTOList, BEGIN");
			List<EwbVsItc04SummaryInitiateReconLineItemDto> retList = list
					.stream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("Coverting object to the DTOList, END");
			return retList;

		} catch (Exception e) {
			LOGGER.error(
					"Converting ResultSet into List in EwbVsItc04SummaryInitiateReconLineItemDto ");
			throw new AppException(
					"Unexpected error in stored procedure " + "execution.", e);
		}
	}

	private EwbVsItc04SummaryInitiateReconLineItemDto convert(Object[] arr) {
		EwbVsItc04SummaryInitiateReconLineItemDto reconentity = new EwbVsItc04SummaryInitiateReconLineItemDto();

		reconentity.setSection((arr[0] != null) ? (String) arr[0] : null);
		reconentity.setItcCount(
				(arr[1] != null) ? GenUtil.getBigInteger(arr[1]) : BigInteger.ZERO);
		reconentity.setItcTaxableValue(
				(arr[2] != null) ? (BigDecimal) arr[2] : BigDecimal.ZERO);
		reconentity.setItcIgst(
				(arr[3] != null) ? (BigDecimal) arr[3] : BigDecimal.ZERO);
		reconentity.setItcCgst(
				(arr[4] != null) ? (BigDecimal) arr[4] : BigDecimal.ZERO);
		reconentity.setItcSgst(
				(arr[5] != null) ? (BigDecimal) arr[5] : BigDecimal.ZERO);
		reconentity.setItcCess(
				(arr[6] != null) ? (BigDecimal) arr[6] : BigDecimal.ZERO);
		reconentity.setEwbCount(
				(arr[7] != null) ? GenUtil.getBigInteger(arr[7]) : BigInteger.ZERO);
		reconentity.setEwbTaxableValue(
				(arr[8] != null) ? (BigDecimal) arr[8] : BigDecimal.ZERO);
		reconentity.setEwbIgst(
				(arr[9] != null) ? (BigDecimal) arr[9] : BigDecimal.ZERO);
		reconentity.setEwbCgst(
				(arr[10] != null) ? (BigDecimal) arr[10] : BigDecimal.ZERO);
		reconentity.setEwbSgst(
				(arr[11] != null) ? (BigDecimal) arr[11] : BigDecimal.ZERO);
		reconentity.setEwbCess(
				(arr[12] != null) ? (BigDecimal) arr[12] : BigDecimal.ZERO);

		return reconentity;
	}

	@Override
	public List<Gstr2ReconSummaryStatusDto> findEwbVsItc04SummStatus(
			Long entityId, EwbVsItc04SummaryInitiateReconDto criteria) {

		Integer fromTaxPeriodEwb = null;
		Integer toTaxPeriodEwb = null;

		String fy1 = criteria.getFy().substring(0, 4);
		String fy2 = criteria.getFy().substring(5, 9);

		if (criteria.getFromTaxPeriod().contains("17")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "04");
		} else if (criteria.getFromTaxPeriod().contains("18")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "10");
		} else if (criteria.getFromTaxPeriod().contains("13")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "04");
		} else if (criteria.getFromTaxPeriod().contains("14")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "07");
		} else if (criteria.getFromTaxPeriod().contains("15")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "10");
		} else if (criteria.getFromTaxPeriod().contains("16")) {
			fromTaxPeriodEwb = Integer.parseInt(fy2 + "01");
		}

		if (criteria.getToTaxPeriod().contains("17")) {
			toTaxPeriodEwb = Integer.parseInt(fy1 + "09");
		} else if (criteria.getToTaxPeriod().contains("18")) {
			toTaxPeriodEwb = Integer.parseInt(fy2 + "03");
		} else if (criteria.getToTaxPeriod().contains("13")) {
			toTaxPeriodEwb = Integer.parseInt(fy1 + "06");
		} else if (criteria.getToTaxPeriod().contains("14")) {
			toTaxPeriodEwb = Integer.parseInt(fy1 + "09");
		} else if (criteria.getToTaxPeriod().contains("15")) {
			toTaxPeriodEwb = Integer.parseInt(fy1 + "12");
		} else if (criteria.getToTaxPeriod().contains("16")) {
			toTaxPeriodEwb = Integer.parseInt(fy2 + "03");
		}

		String queryString = createQueryString(entityId, criteria);

		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Query created for findEwbVsItc04SummStatus "
							+ "on FilterConditions Provided By User : %s",
					queryString);
			LOGGER.debug(str);
		}

		Query q = entityManager.createNativeQuery(queryString);
		q.setParameter("entityId", entityId);
		q.setParameter("fromTaxPeriodEwb", fromTaxPeriodEwb);
		q.setParameter("toTaxPeriodEwb", toTaxPeriodEwb);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		if (LOGGER.isDebugEnabled()) {
			String msg = "Number of Records got Gstins and its Status is :"
					+ list.size();
			LOGGER.debug(msg);
		}
		List<Gstr2ReconSummaryStatusDto> retList = list.stream()
				.map(o -> convertToDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	@SuppressWarnings("unused")
	private String createQueryString(Long entityId, EwbVsItc04SummaryInitiateReconDto criteria) {
		
		Integer fromTaxPeriodEwb = null;
		Integer toTaxPeriodEwb = null;

		String fy1 = criteria.getFy().substring(0, 4);
		String fy2 = criteria.getFy().substring(5, 9);

		if (criteria.getFromTaxPeriod().contains("17")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "04");
		} else if (criteria.getFromTaxPeriod().contains("18")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "10");
		} else if (criteria.getFromTaxPeriod().contains("13")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "04");
		} else if (criteria.getFromTaxPeriod().contains("14")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "07");
		} else if (criteria.getFromTaxPeriod().contains("15")) {
			fromTaxPeriodEwb = Integer.parseInt(fy1 + "10");
		} else if (criteria.getFromTaxPeriod().contains("16")) {
			fromTaxPeriodEwb = Integer.parseInt(fy2 + "01");
		}

		if (criteria.getToTaxPeriod().contains("17")) {
			toTaxPeriodEwb = Integer.parseInt(fy1 + "09");
		} else if (criteria.getToTaxPeriod().contains("18")) {
			toTaxPeriodEwb = Integer.parseInt(fy2 + "03");
		} else if (criteria.getToTaxPeriod().contains("13")) {
			toTaxPeriodEwb = Integer.parseInt(fy1 + "06");
		} else if (criteria.getToTaxPeriod().contains("14")) {
			toTaxPeriodEwb = Integer.parseInt(fy1 + "09");
		} else if (criteria.getToTaxPeriod().contains("15")) {
			toTaxPeriodEwb = Integer.parseInt(fy1 + "12");
		} else if (criteria.getToTaxPeriod().contains("16")) {
			toTaxPeriodEwb = Integer.parseInt(fy2 + "03");
		}

		return " SELECT STATE_NAME,REG_TYPE,GSTIN,STATUS,STATUS_DATE FROM (SELECT MS.STATE_NAME, "
				+ " REQUESTID, G.REG_TYPE, G.GSTIN, "
				+ " CASE WHEN GC.STATUS='RECON_COMPLETED' OR GC.status = 'REPORT_GENERATED' THEN 'SUCCESS' "
				+ " WHEN GC.status = 'RECON_INITIATED' THEN 'IN_PROGRESS' "
				+ " WHEN GC.status = 'NO_DATA_FOUND' OR (GC.status = 'REPORT_GENERATION_FAILED' OR GC.status = 'RECON_FAILED') THEN 'FAILED' "
				+ " ELSE 'NOT_INITIATED' END AS STATUS, "
				+ " COMPLETED_ON AS STATUS_DATE, "
				+ " ITC_FROM_TAXPERIOD,ITC_TO_TAXPERIOD,FY,EWB_FROM_TAXPERIOD,EWB_TO_TAXPERIOD, "
				+ " ROW_NUMBER()OVER(PARTITION BY G.GSTIN,FY,EWB_FROM_TAXPERIOD,EWB_TO_TAXPERIOD ORDER BY REQUESTID DESC,COMPLETED_ON DESC) RN "
				+ " FROM GSTIN_INFO G INNER JOIN ENTITY_INFO E ON E.ID=:entityId AND E.ID=G.ENTITY_ID "
				+ " INNER JOIN master_state MS ON MS.state_code = G.state_code "
				+ " LEFT OUTER JOIN TBL_EWBVSITC04_RECON_CONFIG GC ON GC.GSTIN=G.GSTIN AND E.ID=GC.ENTITY_ID "
				+ " AND GC.EWB_FROM_TAXPERIOD=:fromTaxPeriodEwb AND GC.EWB_TO_TAXPERIOD=:toTaxPeriodEwb )"
				+ " WHERE RN=1 ";

	}

	private Gstr2ReconSummaryStatusDto convertToDto(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Gstr2ReconSummaryStatusDto object";
			LOGGER.debug(str);
		}
		Gstr2ReconSummaryStatusDto obj = new Gstr2ReconSummaryStatusDto();
		obj.setState(arr[0] != null ? (String) arr[0] : null);
		obj.setGstin(arr[2] != null ? (String) arr[2] : null);
		obj.setStatus(arr[3] != null ? (String) arr[3] : null);
		Timestamp dt = (Timestamp) arr[4];
		String ldt = ((dt != null) ? dateChange(dt) : null);
		obj.setStatusdate(ldt);

		obj.setGstinIdentifier(arr[1] != null ? (String) arr[1] : null);
		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"ReconEwbVsItc04SummaryStatusDto object " + "is %s", obj);
			LOGGER.debug(str);
		}
		return obj;
	}

	public String dateChange(Timestamp oldDate) {
		DateTimeFormatter formatter = null;
		String dateTime = oldDate.toString();
		char ch = 'T';
		dateTime = dateTime.substring(0, 10) + ch + dateTime.substring(10 + 1);
		String s1 = dateTime.substring(0, 19);
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

		LocalDateTime dateTimes = LocalDateTime.parse(s1, formatter);

		LocalDateTime dateTimeFormatter = EYDateUtil
				.toISTDateTimeFromUTC(dateTimes);
		DateTimeFormatter FOMATTER = DateTimeFormatter
				.ofPattern("dd-MM-yyyy : HH:mm:ss");
		String newdate = FOMATTER.format(dateTimeFormatter);
		return newdate;

	}

}
