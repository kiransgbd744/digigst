/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ITCReversal180DaysDaoImpl")
public class ITCReversal180DaysDaoImpl implements ITCReversal180DaysDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<ITCReversal180DaysRespDto> getItcReversalDBResp(
			List<String> gstins) {

		List<ITCReversal180DaysRespDto> computeListDto = new ArrayList<>();
		try {
			String queryString = createStatusQuery(gstins);
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("gstins", gstins);

			String msg = String.format(
					"Inside ITCReversal180DaysDaoImpl, " + " query - %s ",
					queryString);

			LOGGER.debug(msg);

			@SuppressWarnings("unchecked")
			List<Object[]> statusList = q.getResultList();
			List<ITCReversal180DaysRespDto> respListDto = statusList.stream()
					.map(o -> convertDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			for (ITCReversal180DaysRespDto resp : respListDto) {

				try {
					String gstin = resp.getGstin();
					Long computeId = resp.getComputeId();

					String computeQuery = createComputeQuery(gstin, computeId);

					Query query = entityManager.createNativeQuery(computeQuery);
					query.setParameter("gstin", gstin);
					query.setParameter("computeId", computeId);
					msg = String
							.format("Inside ITCReversal180DaysDaoImpl executing "
									+ "compute, 2nd query - %s ", computeQuery);

					LOGGER.debug(msg);

					@SuppressWarnings("unchecked")
					List<Object[]> computeList = query.getResultList();

					ITCReversal180DaysRespDto dtoList = convertComputeDto(resp,
							computeList);

					computeListDto.add(dtoList);

				} catch (Exception e) {
					msg = String.format("error occured in "
							+ "ITCReversal180DaysDaoImpl.getItcReversalDbResp() "
							+ "method, compute query", e);
					LOGGER.error(msg, e);
					throw new AppException(e);
				}

			}

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "ITCReversal180DaysDaoImpl.getItcReversalDbResp() method");
		}
		return computeListDto;
	}

	private String createComputeQuery(String gstin, Long computeId) {

		String query = "SELECT GSTIN, TO_VARCHAR( COUNT(GSTIN) ) AS REC_CNT, "
				+ "IFNULL( SUM( IFNULL( CASE WHEN DOC_TYPE = 'CR' THEN -1 * REV_IGST_AMT ELSE REV_IGST_AMT END, 0 )+ "
				+ "IFNULL ( CASE WHEN DOC_TYPE = 'CR' THEN -1 * REV_CGST_AMT ELSE REV_CGST_AMT END, 0 )+ "
				+ "IFNULL ( CASE WHEN DOC_TYPE = 'CR' THEN -1 * REV_SGST_AMT ELSE REV_SGST_AMT END, 0 )+ "
				+ "IFNULL ( CASE WHEN DOC_TYPE = 'CR' THEN -1 * REV_CESS_AMT ELSE REV_CESS_AMT END, 0 ) ), 0 ) AS TOT_TAX_REV,"
				+ " IFNULL( SUM( CASE WHEN DOC_TYPE = 'CR' THEN -1 * REV_IGST_AMT ELSE REV_IGST_AMT END ), 0 ) AS IGST_REV, "
				+ "IFNULL( SUM( CASE WHEN DOC_TYPE = 'CR' THEN -1 * REV_CGST_AMT ELSE REV_CGST_AMT END ), 0 ) AS CGST_REV,"
				+ " IFNULL( SUM( CASE WHEN DOC_TYPE = 'CR' THEN -1 * REV_SGST_AMT ELSE REV_SGST_AMT END ), 0 ) AS SGST_REV,"
				+ " IFNULL( SUM( CASE WHEN DOC_TYPE = 'CR' THEN -1 * REV_CESS_AMT ELSE REV_CESS_AMT END ), 0 ) AS Cess_REV "
				+ "FROM TBL_180_DAYS_COMPUTE  "
				+ "WHERE GSTIN = :gstin AND COMPUTE_ID = :computeId "
				+ "	AND ITC_DIGI_STATUS IN ('Reversal','Reversal & Reclaim') "
				+ "	GROUP BY GSTIN";

		return query;
	}

	private String createStatusQuery(List<String> gstins) {

		String query = "SELECT * FROM ( "
				+ "   SELECT G.GSTIN, C.STATUS, C.MODIFIED_ON, C.COMPUTE_ID, "
				+ "	ROW_NUMBER() OVER (PARTITION BY G.GSTIN,C.COMPUTE_ID ORDER "
				+ "	BY C.COMPUTE_ID DESC)  AS RNK "
				+ "    FROM  TBL_180_DAYS_COMPUTE_GSTINS G "
				+ "    INNER JOIN  TBL_180_DAYS_COMPUTE_CONFIG C "
				+ "	ON C.COMPUTE_ID = G.COMPUTE_ID AND "
				+ "	IFNULL(G.IS_ACTIVE,FALSE) = TRUE "
				+ "    WHERE IFNULL(G.IS_ACTIVE,FALSE) = TRUE AND "
				+ "	IFNULL(C.IS_DELETE,FALSE) = FALSE "
				+ "    AND G.GSTIN IN (:gstins)) WHERE RNK = 1";

		return query;

	}

	private ITCReversal180DaysRespDto convertDto(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " ITCReversal180DaysRespDto object";
			LOGGER.debug(str);
		}

		ITCReversal180DaysRespDto obj = new ITCReversal180DaysRespDto();

		obj.setGstin((String) arr[0]);
		obj.setStatusDesc(
				(String) arr[1] != null ? (String) arr[1] : "Not Initiated");
		if (arr[2] != null) {
			Timestamp date = (Timestamp) arr[2];
			LocalDateTime dt = date.toLocalDateTime();
			obj.setStatus(EYDateUtil.toISTDateTimeFromUTC(dt).toString());
		}

		BigInteger b = GenUtil.getBigInteger(arr[3]);
		Long computeId = b.longValue();
		obj.setComputeId(computeId);

		return obj;
	}

	private ITCReversal180DaysRespDto convertComputeDto(
			ITCReversal180DaysRespDto resp, List<Object[]> objects) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " ITCReversal180DaysRespDto object";
			LOGGER.debug(str);
		}
		ITCReversal180DaysRespDto dto = new ITCReversal180DaysRespDto();

		dto.setGstin(resp.getGstin());
		dto.setStatus(resp.getStatus());
		dto.setStatusDesc(resp.getStatusDesc());

		if (objects != null && !objects.isEmpty()) {
			for (Object[] arr : objects) {
				if ((String) arr[0] != null) {
					if (resp.getGstin().equalsIgnoreCase((String) arr[0])) {
						dto.setCess((BigDecimal) arr[6]);
						dto.setCgst((BigDecimal) arr[4]);
						dto.setIgst((BigDecimal) arr[3]);
						dto.setSgst((BigDecimal) arr[5]);
						dto.setTotalTax((BigDecimal) arr[2]);
						dto.setCount((String) arr[1]);

					}
				}
			}
		}

		return dto;

	}

}
