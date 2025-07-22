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
@Component("Ret1AspVerticalSectionDaoImpl")
public class Ret1AspVerticalSectionDaoImpl
		implements Ret1AspVerticalSectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1AspVerticalSectionDaoImpl.class);

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
		StringBuilder build1 = new StringBuilder();
		StringBuilder build2 = new StringBuilder();
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList ");
				build1.append(" AND GSTIN IN :gstinList ");
				build2.append(" AND CUST_GSTIN IN :gstinList ");
			}
		}
		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			build1.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			build2.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString();
		String buildQuery1 = build1.toString();
		String buildQuery2 = build2.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createQueryString(buildQuery, buildQuery1,
				buildQuery2, table);

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
		obj.setType((String) arr[0]);

		obj.setCount((GenUtil.getBigInteger(arr[1])).intValue());
		obj.setTaxableValue((BigDecimal) arr[2]);
		obj.setIgst((BigDecimal) arr[3]);
		obj.setCgst((BigDecimal) arr[4]);
		obj.setSgst((BigDecimal) arr[5]);
		obj.setCess((BigDecimal) arr[6]);
		return obj;
	}

	private String createQueryString(String buildQuery, String buildQuery1,
			String buildQuery2, String table) {
		LOGGER.debug("Outward 3A TO 3G Query Execution BEGIN ");

		String queryString = "";
		if ("3C3".equalsIgnoreCase(table)) {
			queryString = " SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT FROM("
					+ "SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('3C3') " + buildQuery
					+ "UNION ALL SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='3C3'" + buildQuery1
					+ ") UNION ALL SELECT TYPE, SUM(COUNT) COUNT, "
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,SUM(IGST_AMT) IGST_AMT,"
					+ "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT,"
					+ "SUM(CESS_AMT) CESS_AMT FROM ( "
					+ "SELECT 'TRANSACTIONAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('3C3') " + buildQuery + "UNION ALL "
					+ "SELECT 'VERTICAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE "
					+ "AND RETURN_TYPE='RET-1' AND RETURN_TABLE='3C3'"
					+ buildQuery1 + ") " + "GROUP BY TYPE";
		}
		if ("3C4".equalsIgnoreCase(table)) {
			queryString = " SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,SUM(IGST_AMT) IGST_AMT,"
					+ "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT,"
					+ "SUM(CESS_AMT) CESS_AMT FROM (SELECT COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('3C4') " + buildQuery
					+ "UNION ALL SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE "
					+ "AND RETURN_TYPE='RET-1' AND RETURN_TABLE='3C4'"
					+ buildQuery1
					+ " ) UNION ALL SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,SUM(IGST_AMT) IGST_AMT,"
					+ "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT,"
					+ "SUM(CESS_AMT) CESS_AMT FROM ( "
					+ "SELECT 'TRANSACTIONAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('3C4') " + buildQuery
					+ "UNION ALL SELECT 'VERTICAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE "
					+ "AND RETURN_TYPE='RET-1' AND RETURN_TABLE='3C4' "
					+ buildQuery1 + ") " + "GROUP BY TYPE";
		}
		if ("3D1".equalsIgnoreCase(table)) {
			queryString = " SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM( SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('3D1') " + buildQuery
					+ "UNION ALL SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE "
					+ "IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='3D1' " + buildQuery1
					+ ") UNION ALL SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM( SELECT 'TRANSACTIONAL' AS TYPE,"
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('3D1') " + buildQuery
					+ "UNION ALL SELECT 'VERTICAL' AS TYPE, "
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='3D1' " + buildQuery1
					+ ") GROUP BY TYPE";
		}
		if ("3D2".equalsIgnoreCase(table)) {
			queryString = " SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM( SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('3D2') " + buildQuery
					+ "UNION ALL SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE "
					+ "IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='3D2' " + buildQuery1
					+ ") UNION ALL SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM( SELECT 'TRANSACTIONAL' AS TYPE,"
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('3D2') " + buildQuery
					+ "UNION ALL SELECT 'VERTICAL' AS TYPE, "
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='3D2' " + buildQuery1
					+ ") GROUP BY TYPE";
		}
		if ("3D3".equalsIgnoreCase(table)) {
			queryString = " SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM( SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('3D3') " + buildQuery
					+ "UNION ALL SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE "
					+ "IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='3D3' " + buildQuery1
					+ ") UNION ALL SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM( SELECT 'TRANSACTIONAL' AS TYPE,"
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('3D3') " + buildQuery
					+ "UNION ALL SELECT 'VERTICAL' AS TYPE, "
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='3D3' " + buildQuery1
					+ ") GROUP BY TYPE";
		}
		if ("3D4".equalsIgnoreCase(table)) {
			queryString = " SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM( SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('3D4') " + buildQuery
					+ "UNION ALL SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE "
					+ "IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='3D4' " + buildQuery1
					+ ") UNION ALL SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM( SELECT 'TRANSACTIONAL' AS TYPE,"
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('3D4') " + buildQuery
					+ "UNION ALL SELECT 'VERTICAL' AS TYPE, "
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='3D4' " + buildQuery1
					+ ") GROUP BY TYPE";
		}
		if ("4A10".equalsIgnoreCase(table)) {
			queryString = "SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM( SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_INWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('4A10')" + buildQuery2
					+ "UNION ALL " + "SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='4A10'" + buildQuery1 + ") "
					+ "UNION ALL SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM( SELECT 'TRANSACTIONAL' AS TYPE,"
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_INWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('4A10') " + buildQuery2
					+ "UNION ALL SELECT 'VERTICAL' AS TYPE,"
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='4A10'" + buildQuery1
					+ ") GROUP BY TYPE";
		}
		if ("4A11".equalsIgnoreCase(table)) {
			queryString = "SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM( SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_INWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('4A11')" + buildQuery2
					+ "UNION ALL " + "SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='4A11'" + buildQuery1 + ") "
					+ "UNION ALL SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM( SELECT 'TRANSACTIONAL' AS TYPE,"
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_INWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('4A11') " + buildQuery2
					+ "UNION ALL SELECT 'VERTICAL' AS TYPE,"
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='4A11'" + buildQuery1
					+ ") GROUP BY TYPE";
		}
		if ("4B2".equalsIgnoreCase(table)) {
			queryString = " SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM(SELECT COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_INWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('4B2') " + buildQuery2
					+ "UNION ALL SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE "
					+ "IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='4B2'" + buildQuery1
					+ ") UNION ALL SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT FROM( "
					+ "SELECT 'TRANSACTIONAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_INWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('4B2') " + buildQuery2
					+ "UNION ALL SELECT 'VERTICAL' AS TYPE,"
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='4B2'" + buildQuery1
					+ ") GROUP BY TYPE";
		}
		if ("4B3".equalsIgnoreCase(table)) {
			queryString = " SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM(SELECT COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_INWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('4B3') " + buildQuery2
					+ "UNION ALL SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE "
					+ "IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='4B3'" + buildQuery1
					+ ") UNION ALL SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT FROM( "
					+ "SELECT 'TRANSACTIONAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_INWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('4B3') " + buildQuery2
					+ "UNION ALL SELECT 'VERTICAL' AS TYPE,"
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='4B3'" + buildQuery1
					+ ") GROUP BY TYPE";
		}
		if ("4E1".equalsIgnoreCase(table)) {
			queryString = "SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM ( SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_INWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('4E1') " + buildQuery2
					+ "UNION ALL " + "SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT, "
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE "
					+ "IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='4E1' " + buildQuery1 + ") "
					+ "UNION ALL " + "SELECT TYPE, SUM(COUNT) COUNT, "
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM ( SELECT 'TRANSACTIONAL' AS TYPE,"
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_INWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
					+ "AND AN_TABLE_SECTION IN ('4E1') " + buildQuery2
					+ "UNION ALL SELECT 'VERTICAL' AS TYPE,"
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE "
					+ "IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='4E1'" + buildQuery1 + ") "
					+ "GROUP BY TYPE ";
		}
		if ("4E2".equalsIgnoreCase(table)) {
			queryString = "SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM ( SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_INWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('4E2') " + buildQuery2
					+ "UNION ALL SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE "
					+ "IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='4E2'" + buildQuery1
					+ ") UNION ALL SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT "
					+ "FROM ( SELECT 'TRANSACTIONAL' AS TYPE,"
					+ "COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT " + "FROM ANX_INWARD_DOC_HEADER WHERE "
					+ "IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
					+ "AN_TABLE_SECTION IN ('4E2') " + buildQuery2
					+ "UNION ALL "
					+ "SELECT 'VERTICAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE "
					+ "IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE='4E2'" + buildQuery1
					+ ") GROUP BY TYPE;";
		}

		return queryString;
	}

	// For RET1A Query For 3D1, 3D2, 3D3, 3D4

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
		StringBuilder build1 = new StringBuilder();
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList ");
				build1.append(" AND GSTIN IN :gstinList ");
			}
		}
		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			build1.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString();
		String buildQuery1 = build1.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createQueryStringRet1A(buildQuery, buildQuery1,table);

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

	private String createQueryStringRet1A(String buildQuery, String buildQuery1,String table) {
		LOGGER.debug("Outward 3D1 TO 3D4 Query Execution BEGIN ");

		String queryString = "";
		if ("3D1".equalsIgnoreCase(table)) {
			queryString = "SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT FROM ( "
					+ "SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
					+ "AND AN_RETURN_TYPE IN ('RET1A') AND "
					+ "AN_TABLE_SECTION IN ('3D1') "
					+ buildQuery 
					+ "UNION ALL "
					+ "SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE "
					+ "AND RETURN_TYPE='RET-1A' AND RETURN_TABLE='3D1' "
					+ buildQuery1
					+ ") UNION ALL "
					+ "SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,SUM(IGST_AMT) IGST_AMT,"
					+ "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT,"
					+ "SUM(CESS_AMT) CESS_AMT FROM ( SELECT "
					+ "'TRANSACTIONAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
					+ "AND AN_RETURN_TYPE IN ('RET1A') AND "
					+ "AN_TABLE_SECTION IN ('3D1') "
					+ buildQuery 
					+ "UNION ALL "
					+ "SELECT 'VERTICAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE "
					+ "AND RETURN_TYPE='RET-1A' AND RETURN_TABLE='3D1' "
					+ buildQuery1
					+ ") GROUP BY TYPE ";
		}
		if ("3D2".equalsIgnoreCase(table)) {
			queryString = "SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT FROM ( "
					+ "SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
					+ "AND AN_RETURN_TYPE IN ('RET1A') AND "
					+ "AN_TABLE_SECTION IN ('3D2') "
					+ buildQuery 
					+ "UNION ALL "
					+ "SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE "
					+ "AND RETURN_TYPE='RET-1A' AND RETURN_TABLE='3D2' "
					+ buildQuery1
					+ ") UNION ALL "
					+ "SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,SUM(IGST_AMT) IGST_AMT,"
					+ "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT,"
					+ "SUM(CESS_AMT) CESS_AMT FROM ( SELECT "
					+ "'TRANSACTIONAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
					+ "AND AN_RETURN_TYPE IN ('RET1A') AND "
					+ "AN_TABLE_SECTION IN ('3D2') "
					+ buildQuery 
					+ "UNION ALL "
					+ "SELECT 'VERTICAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE "
					+ "AND RETURN_TYPE='RET-1A' AND RETURN_TABLE='3D2' "
					+ buildQuery1
					+ ") GROUP BY TYPE ";
		}
		if ("3D3".equalsIgnoreCase(table)) {
			queryString = "SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT FROM ( "
					+ "SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
					+ "AND AN_RETURN_TYPE IN ('RET1A') AND "
					+ "AN_TABLE_SECTION IN ('3D3') "
					+ buildQuery 
					+ "UNION ALL "
					+ "SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE "
					+ "AND RETURN_TYPE='RET-1A' AND RETURN_TABLE='3D3' "
					+ buildQuery1
					+ ") UNION ALL "
					+ "SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,SUM(IGST_AMT) IGST_AMT,"
					+ "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT,"
					+ "SUM(CESS_AMT) CESS_AMT FROM ( SELECT "
					+ "'TRANSACTIONAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
					+ "AND AN_RETURN_TYPE IN ('RET1A') AND "
					+ "AN_TABLE_SECTION IN ('3D3') "
					+ buildQuery 
					+ "UNION ALL "
					+ "SELECT 'VERTICAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE "
					+ "AND RETURN_TYPE='RET-1A' AND RETURN_TABLE='3D3' "
					+ buildQuery1
					+ ") GROUP BY TYPE ";
		}
		if ("3D4".equalsIgnoreCase(table)) {
			queryString = "SELECT 'TOTAL' AS TYPE ,SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT FROM ( "
					+ "SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
					+ "AND AN_RETURN_TYPE IN ('RET1A') AND "
					+ "AN_TABLE_SECTION IN ('3D4') "
					+ buildQuery 
					+ "UNION ALL "
					+ "SELECT  COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE "
					+ "AND RETURN_TYPE='RET-1A' AND RETURN_TABLE='3D4' "
					+ buildQuery1
					+ ") UNION ALL "
					+ "SELECT TYPE, SUM(COUNT) COUNT,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,SUM(IGST_AMT) IGST_AMT,"
					+ "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT,"
					+ "SUM(CESS_AMT) CESS_AMT FROM ( SELECT "
					+ "'TRANSACTIONAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC),0) "
					+ "AS CESS_AMT FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
					+ "AND AN_RETURN_TYPE IN ('RET1A') AND "
					+ "AN_TABLE_SECTION IN ('3D4') "
					+ buildQuery 
					+ "UNION ALL "
					+ "SELECT 'VERTICAL' AS TYPE, COUNT(ID) AS COUNT,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE "
					+ "AND RETURN_TYPE='RET-1A' AND RETURN_TABLE='3D4' "
					+ buildQuery1
					+ ") GROUP BY TYPE ";
		}
		return queryString;

	}
}
