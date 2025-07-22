package com.ey.advisory.app.dashboard.recon.details;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@Component("DashboardReconDetailsDaoImpl")
public class DashboardReconDetailsDaoImpl implements DashboardReconDetailsDao {
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<DbResponseDto> getReconDetails(Long entityId,
			String taxPeriod) {
		try {
			String queryString = createReconSmryQuery(entityId, taxPeriod);
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("entityId", entityId);
			q.setParameter("taxPeriod", taxPeriod);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Executing query %s, entity Id %d,"
						+ " taxPeriod %s", queryString, entityId, taxPeriod);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<DbResponseDto> retList = list.parallelStream()
					.map(o -> convertToDbResponseDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "DashboardReconDetailsDaoImpl");
		}
	}
	
	private String createReconSmryQuery(Long entityId, String taxPeriod){
		
		String query = "SELECT REPORT_TYPE,(COUNT(REPORT_TYPE)*100)/"
				+ "(SELECT COUNT(A2PR.RECON_LINK_ID) FROM "
				+ "              \"CLIENT1_GST\".\"LINK_A2_PR\" A2PR "
				+ "INNER JOIN \"CLIENT1_GST\".\"GSTIN_INFO\" GI ON "
				+ "(GI.GSTIN=A2PR.PR_RECIPIENT_GSTIN "
				+ "            OR GI.GSTIN=A2PR.A2_RECIPIENT_GSTIN) AND "
				+ "GI.IS_DELETE=FALSE "
				+ "            AND A2PR.TAX_PERIOD=:taxPeriod AND "
				+ "A2PR.IS_INFORMATION_REPORT=0 "
				+ "INNER JOIN \"CLIENT1_GST\".\"ENTITY_INFO\" "
				+ "EI ON EI.ID=GI.ENTITY_ID "
				+ "  AND EI.IS_DELETE=FALSE AND EI.ID=:entityId) AS PERCENTAGE "
				+ "FROM " + "( " + "SELECT CASE "
				+ "              WHEN A2PR.CURRENT_REPORT_TYPE="
				+ "					'Exact Match' THEN 'Matched' "
				+ "              WHEN A2PR.CURRENT_REPORT_TYPE="
				+ "				'Match upto Tolerance' THEN 'Matched' "
				+ "              WHEN A2PR.CURRENT_REPORT_TYPE="
				+ "				'Value Mismatch' THEN 'Mismatched' "
				+ "              WHEN A2PR.CURRENT_REPORT_TYPE="
				+ "				'POS Mismatch' THEN 'Mismatched' "
				+ "              WHEN A2PR.CURRENT_REPORT_TYPE="
				+ "				'Document Date Mismatch' THEN 'Mismatched' "
				+ "              WHEN A2PR.CURRENT_REPORT_TYPE="
				+ "				'Multi-Mismatch' THEN 'Mismatched' "
				+ "              WHEN A2PR.CURRENT_REPORT_TYPE="
				+ "				'Document Type Mismatch' THEN 'Mismatched' "
				+ "              WHEN A2PR.CURRENT_REPORT_TYPE="
				+ "				'Fuzzy Match' THEN 'Probable' "
				+ "              WHEN A2PR.CURRENT_REPORT_TYPE="
				+ "				'Addition in ANX-2' THEN 'Addition in ANX-2' "
				+ "              WHEN A2PR.CURRENT_REPORT_TYPE="
				+ "				'Addition in PR' THEN 'Addition in PR' "
				+ "              ELSE 'Others' END AS REPORT_TYPE FROM "
				+ "              \"CLIENT1_GST\".\"LINK_A2_PR\" A2PR "
				+ "INNER JOIN \"CLIENT1_GST\".\"GSTIN_INFO\" GI ON "
				+ "	(GI.GSTIN=A2PR.PR_RECIPIENT_GSTIN "
				+ "            OR GI.GSTIN=A2PR.A2_RECIPIENT_GSTIN) "
				+ "	AND GI.IS_DELETE=FALSE "
				+ "            AND A2PR.TAX_PERIOD=:taxPeriod "
				+ "	AND A2PR.IS_INFORMATION_REPORT=0 "
				+ "INNER JOIN \"CLIENT1_GST\".\"ENTITY_INFO\" EI ON "
				+ "EI.ID=GI.ENTITY_ID "
				+ "            AND EI.IS_DELETE=FALSE AND EI.ID=:entityId)A "
				+ " GROUP BY REPORT_TYPE";

		return query;
	}
	
	@Override
	public List<DbResponseDto> getRespDetails(Long entityId,
			String taxPeriod) {
		try {
			String queryString = createRespSmryQuery(entityId, taxPeriod);
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("entityId", entityId);
			q.setParameter("taxPeriod", taxPeriod);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Executing query %s, entity Id %d,"
						+ " taxPeriod %s", queryString, entityId, taxPeriod);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<DbResponseDto> retList = list.parallelStream()
					.map(o -> convertToDbResponseDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "DashboardReconDetailsDaoImpl");
		}
	
	}

	private DbResponseDto convertToDbResponseDto(Object [] obj){
		DbResponseDto dto = new DbResponseDto();
		
		dto.setResponse((String) obj[0]);
		dto.setPercentage((BigDecimal) obj[1]);
		
		return dto;
		
	}
	
	
	private String createRespSmryQuery(Long entityId, String taxPeriod){
		
		String query = "SELECT USR_RESPONSE,"
				+ "(COUNT(USR_RESPONSE)*100)/(SELECT COUNT(A2PR.RECON_LINK_ID) "
				+ " FROM  \"CLIENT1_GST\".\"LINK_A2_PR\" A2PR "
				+ "       INNER JOIN \"CLIENT1_GST\".\"GSTIN_INFO\" GI ON"
				+ " (GI.GSTIN=A2PR.PR_RECIPIENT_GSTIN "
				+ "       OR GI.GSTIN=A2PR.A2_RECIPIENT_GSTIN) AND "
				+ " GI.IS_DELETE=FALSE "
				+ "       AND A2PR.TAX_PERIOD=:taxPeriod AND "
				+ "A2PR.IS_INFORMATION_REPORT=0 "
				+ "       INNER JOIN \"CLIENT1_GST\".\"ENTITY_INFO\" EI "
				+ "ON EI.ID=GI.ENTITY_ID "
				+ "       AND EI.IS_DELETE=FALSE AND EI.ID=:entityId "
				+ "       WHERE A2PR.USER_RESPONSE "
				+ " NOT IN ('A4','A1U1','A1U2'))AS PERCENTAGE "
				+ "FROM " + "( "
				+ "SELECT  CASE WHEN  A2PR.USER_RESPONSE "
				+ "IN ('A1','A2','A3') THEN 'Accept (ANX–2)' "
				+ "             WHEN  A2PR.USER_RESPONSE ='P1' "
				+ "THEN 'Pending (ANX–2)' "
				+ "             WHEN  A2PR.USER_RESPONSE IN "
				+ "('R1','R1U1','R1U2') THEN 'Reject (ANX–2)' "
				+ "             WHEN  A2PR.USER_RESPONSE IN "
				+ "('',NULL,'C1') AND USER_RESPONSE<>'Addition in PR' "
				+ "             THEN  'No Action (ANX–2)' "
				+ "             WHEN  A2PR.USER_RESPONSE IN "
				+ "('U1','U2') THEN 'Provisional Credit (Addition in PR)' "
				+ "            WHEN  A2PR.USER_RESPONSE IN ('P2','C1','',NULL) "
				+ " AND A2PR.USER_RESPONSE='Addition in PR' "
				+ " THEN 'Pending (Addition in PR)' "
				+ " "
				+ "      END   "
				+ "USR_RESPONSE FROM \"CLIENT1_GST\".\"LINK_A2_PR\" A2PR "
				+ "             INNER JOIN \"CLIENT1_GST\".\"GSTIN_INFO\" GI "
				+ " ON (GI.GSTIN=A2PR.PR_RECIPIENT_GSTIN "
				+ "             OR GI.GSTIN=A2PR.A2_RECIPIENT_GSTIN) AND "
				+ " GI.IS_DELETE=FALSE "
				+ "             AND A2PR.TAX_PERIOD=:taxPeriod AND "
				+ " A2PR.IS_INFORMATION_REPORT=0 "
				+ "             INNER JOIN "
				+ " \"CLIENT1_GST\".\"ENTITY_INFO\" EI ON EI.ID=GI.ENTITY_ID "
				+ "             AND EI.IS_DELETE=FALSE AND EI.ID=:entityId "
				+ "             WHERE A2PR.USER_RESPONSE NOT "
				+ " IN ('A4','A1U1','A1U2') "
				+ "             )A " + "  GROUP BY USR_RESPONSE";
		return query;
	}

}