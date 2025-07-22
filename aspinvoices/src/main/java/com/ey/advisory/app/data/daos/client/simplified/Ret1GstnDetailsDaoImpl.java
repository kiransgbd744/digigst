package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.simplified.Ret1AspVerticalSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryReqDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Ret1GstnDetailsDaoImpl")
public class Ret1GstnDetailsDaoImpl implements Ret1AspVerticalSectionDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1GstnDetailsDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Ret1AspVerticalSummaryDto> lateBasicSummarySection(
			Ret1SummaryReqDto req) {
		String taxPeriodReq = req.getTaxPeriod();

		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		String table = req.getTable();
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}

		StringBuilder build = new StringBuilder();
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND GSTIN IN :gstinList");
			}
		}
		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString();

		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createQueryString(buildQuery, table);

		LOGGER.debug("Outward Query BEGIN----> 3A To 3G " + queryStr);

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Ret1AspVerticalSummaryDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.");
		}
	}

	private Ret1AspVerticalSummaryDto convert(Object[] arr) {
		Ret1AspVerticalSummaryDto obj = new Ret1AspVerticalSummaryDto();
		obj.setTaxableValue((BigDecimal) arr[0]);
		obj.setIgst((BigDecimal) arr[1]);
		obj.setCgst((BigDecimal) arr[2]);
		obj.setSgst((BigDecimal) arr[3]);
		obj.setCess((BigDecimal) arr[4]);
		return obj;
	}

	private String createQueryString(String buildQuery, String table) {
		LOGGER.debug("Outward 3A TO 3G Query Execution BEGIN ");

		String queryString = "";
		if ("3C3".equalsIgnoreCase(table)) {
			queryString = "SELECT IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_OUTWARD  WHERE GET_TABLE_SECTION= '3C3' "
					+ "AND GET_TABLE_TYPE= 'TABLE3' "
					+ "AND GET_RETURN_TYPE='RET-1' " + buildQuery;
		}
		if ("3C4".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_OUTWARD WHERE "
					+ "GET_TABLE_SECTION= '3C4' AND "
					+ "GET_TABLE_TYPE= 'TABLE3' AND GET_RETURN_TYPE='RET-1'"
					+ buildQuery;
		}
		if ("3D1".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_OUTWARD WHERE GET_TABLE_SECTION"
					+ " = '3D1' AND GET_TABLE_TYPE= 'TABLE3' "
					+ "AND GET_RETURN_TYPE='RET-1' " + buildQuery;
		}
		if ("3D2".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_OUTWARD WHERE "
					+ "GET_TABLE_SECTION= '3D2' "
					+ "AND GET_TABLE_TYPE= 'TABLE3' "
					+ "AND GET_RETURN_TYPE='RET-1'" + buildQuery;
		}
		if ("3D3".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_OUTWARD WHERE "
					+ "GET_TABLE_SECTION= '3D3' AND "
					+ "GET_TABLE_TYPE= 'TABLE3' "
					+ "AND GET_RETURN_TYPE='RET-1'" + buildQuery;
		}
		if ("3D4".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_OUTWARD WHERE "
					+ "GET_TABLE_SECTION= '3D4' AND "
					+ "GET_TABLE_TYPE= 'TABLE3' "
					+ "AND GET_RETURN_TYPE='RET-1'" + buildQuery;
		}
		if ("4A10".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_INWARD WHERE GET_TABLE_SECTION= '4A10' "
					+ "AND GET_TABLE_TYPE= 'TABLE4' "
					+ "AND GET_RETURN_TYPE='RET-1'" + buildQuery;
		}
		if ("4A11".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_INWARD WHERE GET_TABLE_SECTION= '411' "
					+ "AND GET_TABLE_TYPE= 'TABLE4' "
					+ "AND GET_RETURN_TYPE='RET-1'" + buildQuery;
		}
		if ("4B2".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_INWARD WHERE "
					+ "GET_TABLE_SECTION= '4B2' AND "
					+ "GET_TABLE_TYPE= 'TABLE4' "
					+ "AND GET_RETURN_TYPE='RET-1'" + buildQuery;
		}
		if ("4B3".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_INWARD WHERE " + "GET_TABLE_SECTION= '4B3' "
					+ "AND GET_TABLE_TYPE= 'TABLE4' "
					+ "AND GET_RETURN_TYPE='RET-1'" + buildQuery;
		}
		if ("4E1".equalsIgnoreCase(table)) {
			queryString = " SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_INWARD WHERE "
					+ "GET_TABLE_SECTION= '4E1' AND "
					+ "GET_TABLE_TYPE= 'TABLE4' "
					+ "AND GET_RETURN_TYPE='RET-1' " + buildQuery;
		}
		if ("4E2".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_INWARD  WHERE "
					+ "GET_TABLE_SECTION= '4E2' AND "
					+ "GET_TABLE_TYPE= 'TABLE4' "
					+ "AND GET_RETURN_TYPE='RET-1' " + buildQuery;
		}

		return queryString;
	}

	// RET1A GSTN Sections For 3D1 to 3D4 
	@Override
	public List<Ret1AspVerticalSummaryDto> lateBasicSummarySectionRet1A(
			Ret1SummaryReqDto req) {
		String taxPeriodReq = req.getTaxPeriod();

		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		String table = req.getTable();
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}

		StringBuilder build = new StringBuilder();
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND GSTIN IN :gstinList");
			}
		}
		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString();

		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createQueryStringRet1A(buildQuery, table);

		LOGGER.debug("Outward Query BEGIN----> 3A To 3G " + queryStr);

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Ret1AspVerticalSummaryDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.");
		}
	}

	private String createQueryStringRet1A(String buildQuery, String table) {
		LOGGER.debug("Outward 3D1 TO 3D4 Query Execution BEGIN ");

		String queryString = "";
		if ("3D1".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_OUTWARD WHERE GET_TABLE_SECTION= '3D1' "
					+ "AND GET_TABLE_TYPE= 'TABLE3' AND GET_RETURN_TYPE='RET-1A' "
					+ buildQuery;
		}
		if ("3D2".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_OUTWARD WHERE GET_TABLE_SECTION= '3D2' "
					+ "AND GET_TABLE_TYPE= 'TABLE3' AND GET_RETURN_TYPE='RET-1A' "
					+ buildQuery;
		}
		if ("3D3".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_OUTWARD WHERE GET_TABLE_SECTION= '3D3' "
					+ "AND GET_TABLE_TYPE= 'TABLE3' AND GET_RETURN_TYPE='RET-1A' "
					+ buildQuery;
		}
		if ("3D4".equalsIgnoreCase(table)) {
			queryString = "SELECT "
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GETRET1_OUTWARD WHERE GET_TABLE_SECTION= '3D2' "
					+ "AND GET_TABLE_TYPE= 'TABLE3' AND GET_RETURN_TYPE='RET-1A' "
					+ buildQuery;
		}

		return queryString;
	}
}
