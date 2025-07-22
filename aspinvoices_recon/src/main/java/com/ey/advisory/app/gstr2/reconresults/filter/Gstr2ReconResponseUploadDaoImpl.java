package com.ey.advisory.app.gstr2.reconresults.filter;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.math.BigInteger;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2ReconResponseUploadDaoImpl")
public class Gstr2ReconResponseUploadDaoImpl
		implements Gstr2ReconResponseUploadDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Long> getReconLinkIds(Gstr2ReconResultsReqDto reqDto) {

		List<String> gstins = reqDto.getGstins();
		String toTaxPeriod = reqDto.getToTaxPeriod();
		String fromTaxPeriod = reqDto.getFromTaxPeriod();

		String reportType = reqDto.getReportType();
		try {
			String condtion = queryCondition(reqDto);
			String queryString = null;
			if (reqDto.getReconType().equalsIgnoreCase("2A_PR"))
				queryString = createQuery2APR(condtion, reqDto);
			else
				queryString = createQuery(condtion, reqDto);

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("gstin", gstins);
			q.setParameter("toTaxPeriod", toTaxPeriod);
			q.setParameter("fromTaxPeriod", fromTaxPeriod);

			if (reqDto.getReportType() != null) {
				q.setParameter("reportType", reportType);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data {} %s", reqDto);
			}
			@SuppressWarnings("unchecked")
			List<BigInteger> reconLinkObject = q.getResultList();
			List<Long> reconLinkList = reconLinkObject.stream().map(o->convertToLong(o)).collect(Collectors.toList());
			return reconLinkList;
		} catch (Exception ex) {
			String msg = String.format("Error Occured while executing query %s",
					ex);
			LOGGER.error(msg);
			throw new AppException(msg,ex);
		}

	}
	private Long convertToLong(BigInteger arr)
	{
		return arr.longValue();
	}

	private String createQuery(String condtion,
			Gstr2ReconResultsReqDto reqDto) {

		StringBuilder query = new StringBuilder();
		query.append("SELECT LT.RECON_LINK_ID FROM ");
		query.append("  TBL_LINK_2B_PR LT ");
		query.append(
				"  INNER JOIN TBL_RECON_REPORT_GSTIN_DETAILS GD ON LT.RECON_REPORT_CONFIG_ID = GD.RECON_REPORT_CONFIG_ID AND GD.IS_ACTIVE = TRUE ");
		query.append(
				"  INNER JOIN TBL_RECON_REPORT_CONFIG GC ON GC.RECON_REPORT_CONFIG_ID = GD.RECON_REPORT_CONFIG_ID AND GC.STATUS IN ('REPORT_GENERATED','RECON_COMPLETED') ");
		query.append(
				" AND (PR_RECIPIENT_GSTIN IN (:gstin)  OR  B2_RECIPIENT_GSTIN IN (:gstin))");
		query.append(condtion);
		query.append(
				"LEFT OUTER JOIN TBL_2BPR_RECON_RESP_PSD PSD ON ( LT.PR_INVOICE_KEY = PSD.INVOICEKEYPR OR"
						+ " LT.B2_INVOICE_KEY = PSD.INVOICEKEYB2 ) AND PSD.ENDDTM IS NULL ");
		query.append(
				"LEFT OUTER JOIN TBL_2BPR_STG_RECON_RESPONSE STG ON PSD.STGID = STG.ID ");
		query.append(" WHERE REPORT_TYPE_ID NOT IN (15, 16, 22)  ");

		createReportTypeQuery(reqDto, query);

		query.append("ORDER BY LT.RECON_LINK_ID");

		return query.toString();
	}

	private String queryCondition(Gstr2ReconResultsReqDto reqDto) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin Gstr2ReconResponseUploadDaoImpl.queryCondition() ";
			LOGGER.debug(msg);
		}

		StringBuilder condition1 = new StringBuilder();
		condition1.append(
				" AND (  ( FROM_RET_PERIOD >=:fromTaxPeriod AND TO_RET_PERIOD <=:toTaxPeriod ) ");

		condition1.append(")");

		return condition1.toString();
	}

	private void createReportTypeQuery(Gstr2ReconResultsReqDto reqDto,
			StringBuilder query) {

		if (reqDto.getReportType() != null) {

			if (reqDto.getReportType().contains(APIConstants.FORCE_MATCH)
					&& !reqDto.getReportType()
							.contains(APIConstants.RESPONSE_B3)) {
				query.append(
						" AND ( LT.CURRENT_REPORT_TYPE IN (:reportType) OR ");
				query.append(
						" (  LT.CURRENT_REPORT_TYPE IN ('ForceMatch/GSTR3B') AND "
								+ "  LT.GSTR_3B_TAX_PERIOD IS NULL ))");
			}

			if (!reqDto.getReportType().contains(APIConstants.FORCE_MATCH)
					&& reqDto.getReportType()
							.contains(APIConstants.RESPONSE_B3)) {

				query.append(
						" AND ( LT.CURRENT_REPORT_TYPE IN (:reportType) OR ");
				query.append(
						" (  LT.CURRENT_REPORT_TYPE IN ('ForceMatch/GSTR3B') AND "
								+ " LT.GSTR_3B_TAX_PERIOD IS NOT NULL )) ");
			}

			if (reqDto.getReportType().contains(APIConstants.FORCE_MATCH)
					&& reqDto.getReportType()
							.contains(APIConstants.RESPONSE_B3)) {

				query.append(
						" AND ( LT.CURRENT_REPORT_TYPE IN (:reportType) OR ");
				query.append(
						" (  LT.CURRENT_REPORT_TYPE IN ('ForceMatch/GSTR3B') )) ");
			}

			if (!reqDto.getReportType().contains(APIConstants.FORCE_MATCH)
					&& !reqDto.getReportType()
							.contains(APIConstants.RESPONSE_B3)) {

				query.append(" AND  LT.CURRENT_REPORT_TYPE IN (:reportType) ");
			}
		}
	}

	private String createQuery2APR(String condtion,
			Gstr2ReconResultsReqDto reqDto) {

		StringBuilder query = new StringBuilder();
		query.append("SELECT LT.RECON_LINK_ID ");
		query.append(" FROM TBL_AUTO_2APR_LINK LT ");
		query.append(
				"INNER JOIN TBL_RECON_REPORT_GSTIN_DETAILS GD ON LT.RECON_REPORT_CONFIG_ID = GD.RECON_REPORT_CONFIG_ID AND GD.IS_ACTIVE = TRUE ");
		query.append(
				"INNER JOIN TBL_RECON_REPORT_CONFIG GC ON GC.RECON_REPORT_CONFIG_ID = GD.RECON_REPORT_CONFIG_ID AND GC.STATUS IN ('REPORT_GENERATED','RECON_COMPLETED') ");
		query.append(
				" AND (PR_RECIPIENT_GSTIN IN (:gstin)  OR  A2_RECIPIENT_GSTIN IN (:gstin))");
		query.append(condtion);
		query.append(
				" LEFT OUTER JOIN TBL_RECON_RESP_PSD PSD ON ( LT.PR_INVOICE_KEY = PSD.INVOICEKEYPR OR"
						+ " LT.A2_INVOICE_KEY = PSD.INVOICEKEYA2 ) AND PSD.ENDDTM IS NULL ");
		query.append(
				" LEFT OUTER JOIN TBL_STG_RECON_RESPONSE STG ON PSD.STGID = STG.ID  ");
		query.append(" WHERE REPORT_TYPE_ID NOT IN (15, 16, 22)  ");

		createReportTypeQuery2APR(reqDto, query);

		query.append(" ORDER BY LT.RECON_LINK_ID");

		return query.toString();
	}

	private void createReportTypeQuery2APR(Gstr2ReconResultsReqDto reqDto,
			StringBuilder query) {

		if (reqDto.getReportType() != null) {

			if (reqDto.getReportType().contains(APIConstants.FORCE_MATCH)
					&& !reqDto.getReportType()
							.contains(APIConstants.RESPONSE_B3)) {
				query.append(
						" AND ( LT.CURRENT_REPORT_TYPE IN (:reportType) OR ");
				query.append(
						"   (LT.CURRENT_REPORT_TYPE IN ('ForceMatch/GSTR3B') AND "
								+ " LT.LOCK_STATUS = 'MANUAL LOCK' AND LT.GSTR_3B_TAX_PERIOD IS NULL ))");
			}

			if (!reqDto.getReportType().contains(APIConstants.FORCE_MATCH)
					&& reqDto.getReportType()
							.contains(APIConstants.RESPONSE_B3)) {
				query.append(
						" AND ( LT.CURRENT_REPORT_TYPE IN (:reportType) OR ");
				query.append(
						" (  LT.CURRENT_REPORT_TYPE IN ('ForceMatch/GSTR3B') AND "
								+ " LT.LOCK_STATUS = 'MANUAL LOCK' AND LT.GSTR_3B_TAX_PERIOD IS NOT NULL)) ");
			}

			if (reqDto.getReportType().contains(APIConstants.FORCE_MATCH)
					&& reqDto.getReportType()
							.contains(APIConstants.RESPONSE_B3)) {
				query.append(
						" AND ( LT.CURRENT_REPORT_TYPE IN (:reportType) OR ");
				query.append(
						"   (LT.CURRENT_REPORT_TYPE IN ('ForceMatch/GSTR3B') AND LT.LOCK_STATUS = 'MANUAL LOCK')) ");
			}

			if (!reqDto.getReportType().contains(APIConstants.FORCE_MATCH)
					&& !reqDto.getReportType()
							.contains(APIConstants.RESPONSE_B3)) {

				query.append(" AND  LT.CURRENT_REPORT_TYPE IN (:reportType) ");
			}
		}
	}

}
