package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Ret1LateFeeSummarySectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Ret1Int_6_BasicDocSummarySectionDaoImpl")
public class Ret1Int_6_BasicDocSummarySectionDaoImpl
		implements Ret1Int6BasicDocSummarySectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1Int_6_BasicDocSummarySectionDaoImpl.class);

	
	@Override
	public List<Ret1LateFeeSummarySectionDto> lateBasicSummarySection(
			Annexure1SummaryReqDto request) {
		
		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		String taxPeriodReq = req.getTaxPeriod();

		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

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

		String queryStr = createQueryString(buildQuery);
		
		LOGGER.debug("Outward Query BEGIN----> 3A To 3G "+ queryStr);
		
		try {
			Query q = entityManager.createNativeQuery(queryStr);

					if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Ret1LateFeeSummarySectionDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.");
		}
	}

	private Ret1LateFeeSummarySectionDto convert(Object[] arr) {
		Ret1LateFeeSummarySectionDto obj = new Ret1LateFeeSummarySectionDto();
		LOGGER.debug("Array data Setting to Dto");
		obj.setTable((String)arr[0]);
		obj.setSupplyType((String)arr[1]);
		obj.setAspIgst((BigDecimal) arr[2]);
		obj.setAspCgst((BigDecimal) arr[3]);
		obj.setAspSgst((BigDecimal) arr[4]);
		obj.setAspCess((BigDecimal) arr[5]);
		obj.setAsplatefeeCgst((BigDecimal) arr[6]);
		obj.setAsplatefeeSgst((BigDecimal) arr[7]);
		obj.setGstnIgst((BigDecimal) arr[8]);
		obj.setGstnCgst((BigDecimal) arr[9]);
		obj.setGstnSgst((BigDecimal) arr[10]);
		obj.setGstnCess((BigDecimal) arr[11]);
		obj.setGstnlatefeeCgst((BigDecimal) arr[12]);
		obj.setGstnlatefeeSgst((BigDecimal) arr[13]);
		return obj;
	}

	private String createQueryString(String buildQuery) {
		LOGGER.debug("Outward 3A TO 3G Query Execution BEGIN ");
				
		String queryString = "SELECT TABLE,TYPE_OF_SUPPLY,"
				+ "IFNULL(SUM(ASP_INTEREST_IGST_AMT),0) AS ASP_INTEREST_IGST_AMT,"
				+ "IFNULL(SUM(ASP_INTEREST_CGST_AMT),0) AS ASP_INTEREST_CGST_AMT,"
				+ "IFNULL(SUM(ASP_INTEREST_SGST_AMT),0) AS ASP_INTEREST_SGST_AMT,"
				+ "IFNULL(SUM(ASP_INTEREST_CESS_AMT),0) AS ASP_INTEREST_CESS_AMT,"
				+ "SUM(ASP_LATEFEE_CGST_AMT) AS ASP_LATEFEE_CGST_AMT,"
				+ "IFNULL(SUM(ASP_LATEFEE_SGST_AMT),0) AS ASP_LATEFEE_SGST_AMT,"
				+ "IFNULL(SUM(GSTN_INTEREST_IGST_AMT),0) AS GSTN_INTEREST_IGST_AMT,"
				+ "IFNULL(SUM(GSTN_INTEREST_CGST_AMT),0) AS GSTN_INTEREST_CGST_AMT,"
				+ "IFNULL(SUM(GSTN_INTEREST_SGST_AMT),0) AS GSTN_INTEREST_SGST_AMT,"
				+ "IFNULL(SUM(GSTN_INTEREST_CESS_AMT),0) AS GSTN_INTEREST_CESS_AMT,"
				+ "IFNULL(SUM(GSTN_LATEFEE_CGST_AMT),0) AS GSTN_LATEFEE_CGST_AMT,"
				+ "IFNULL(SUM(GSTN_LATEFEE_SGST_AMT),0) AS GSTN_LATEFEE_SGST_AMT "
				+ "FROM (SELECT RETURN_TABLE AS TABLE,"
				+ "(case WHEN RETURN_TABLE='6(1)' THEN 'interestNFeeLateFiling' "
				+ "WHEN RETURN_TABLE='6(2)' THEN 'interestItcReversal' "
				+ "WHEN RETURN_TABLE='6(3)' THEN 'lateReportingRCM' "
				+ "WHEN RETURN_TABLE='6(4)' "
				+ "THEN 'otherInterest' END ) AS TYPE_OF_SUPPLY,"
				+ "IFNULL(SUM(INTEREST_IGST_AMT),0) AS ASP_INTEREST_IGST_AMT,"
				+ "IFNULL(SUM(INTEREST_CGST_AMT),0) AS ASP_INTEREST_CGST_AMT,"
				+ "IFNULL(SUM(INTEREST_SGST_AMT),0) AS ASP_INTEREST_SGST_AMT,"
				+ "IFNULL(SUM(INTEREST_CESS_AMT),0) AS ASP_INTEREST_CESS_AMT,"
				+ "IFNULL(SUM(LATEFEE_CGST_AMT),0) AS ASP_LATEFEE_CGST_AMT,"
				+ "IFNULL(SUM(LATEFEE_SGST_AMT),0) AS ASP_LATEFEE_SGST_AMT,"
				+ "0 AS GSTN_INTEREST_IGST_AMT,0 AS GSTN_INTEREST_CGST_AMT,"
				+ "0 AS GSTN_INTEREST_SGST_AMT,0 AS GSTN_INTEREST_CESS_AMT,"
				+ "0 AS GSTN_LATEFEE_CGST_AMT,"
				+ "0 AS GSTN_LATEFEE_SGST_AMT "
				+ "FROM RET_PROCESSED_INTEREST_LATEFEE WHERE "
				+ "IS_DELETE = FALSE AND RETURN_TYPE='RET-1' AND "
				+ "RETURN_TABLE IN ('6(1)','6(2)','6(3)','6(4)') "
				+ buildQuery 
				+ "GROUP BY RETURN_TABLE,RETURN_PERIOD,GSTIN,SR_NO,RETURN_TYPE "
				+ "UNION ALL "
				+ "SELECT GET_TABLE_SECTION AS TABLE,"
				+ "(case WHEN GET_TABLE_SECTION='6(1)' "
				+ "THEN 'interestNFeeLateFiling' "
				+ "WHEN GET_TABLE_SECTION='6(2)' THEN 'interestItcReversal' "
				+ "WHEN GET_TABLE_SECTION='6(3)' THEN 'lateReportingRCM' "
				+ "WHEN GET_TABLE_SECTION='6(4)' THEN 'otherInterest' "
				+ "END ) AS TYPE_OF_SUPPLY,0 AS ASP_INTEREST_IGST_AMT,"
				+ "0 AS ASP_INTEREST_CGST_AMT,0 AS ASP_INTEREST_SGST_AMT,"
				+ "0 AS ASP_INTEREST_CESS_AMT,0 AS ASP_LATEFEE_CGST_AMT,"
				+ "0 AS ASP_LATEFEE_SGST_AMT,"
				+ "IFNULL(SUM(INTEREST_IGST_AMT),0) AS GSTN_INTEREST_IGST_AMT,"
				+ "IFNULL(SUM(INTEREST_CGST_AMT),0) AS GSTN_INTEREST_CGST_AMT,"
				+ "IFNULL(SUM(INTEREST_SGST_AMT),0) AS GSTN_INTEREST_SGST_AMT,"
				+ "IFNULL(SUM(INTEREST_CESS_AMT),0) AS GSTN_INTEREST_CESS_AMT,"
				+ "IFNULL(SUM(LATEFEE_CGST_AMT),0) AS GSTN_LATEFEE_CGST_AMT,"
				+ "IFNULL(SUM(LATEFEE_SGST_AMT),0) AS GSTN_LATEFEE_SGST_AMT "
				+ "FROM GETRET1_INTEREST_LATEFEE WHERE IS_DELETE = FALSE "
				+ "AND GET_RETURN_TYPE='RET-1' "
				+ "AND GET_TABLE_SECTION IN ('6(1)','6(2)','6(3)','6(4)') "
				+ buildQuery
				+ "GROUP BY GET_TABLE_SECTION "
				+ "UNION ALL "
				+ "SELECT '6' AS TABLE,'interestNLateFee' AS TYPE_OF_SUPPLY,"
				+ "IFNULL(SUM(INTEREST_IGST_AMT),0) AS ASP_INTEREST_IGST_AMT,"
				+ "IFNULL(SUM(INTEREST_CGST_AMT),0) AS ASP_INTEREST_CGST_AMT,"
				+ "IFNULL(SUM(INTEREST_SGST_AMT),0) AS ASP_INTEREST_SGST_AMT,"
				+ "IFNULL(SUM(INTEREST_CESS_AMT),0) AS ASP_INTEREST_CESS_AMT,"
				+ "IFNULL(SUM(LATEFEE_CGST_AMT),0) AS ASP_LATEFEE_CGST_AMT,"
				+ "IFNULL(SUM(LATEFEE_SGST_AMT),0) AS ASP_LATEFEE_SGST_AMT,"
				+ "0 AS GSTN_INTEREST_IGST_AMT,0 AS GSTN_INTEREST_CGST_AMT,"
				+ "0 AS GSTN_INTEREST_SGST_AMT,0 AS GSTN_INTEREST_CESS_AMT,"
				+ "0 AS GSTN_LATEFEE_CGST_AMT,0 AS GSTN_LATEFEE_SGST_AMT "
				+ "FROM RET_PROCESSED_INTEREST_LATEFEE "
				+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
				+ "AND RETURN_TABLE IN ('6(1)','6(2)','6(3)','6(4)') "
				+ buildQuery
				+ "GROUP BY RETURN_TABLE "
				+ "UNION ALL SELECT '6' AS TABLE, "
				+ "'interestNLateFee' AS TYPE_OF_SUPPLY,"
				+ "0 AS ASP_INTEREST_IGST_AMT,0 AS ASP_INTEREST_CGST_AMT,"
				+ "0 AS ASP_INTEREST_SGST_AMT, 0 AS ASP_INTEREST_CESS_AMT,"
				+ "0 AS ASP_LATEFEE_CGST_AMT, 0 AS ASP_LATEFEE_SGST_AMT,"
				+ "IFNULL(SUM(INTEREST_IGST_AMT),0) AS GSTN_INTEREST_IGST_AMT,"
				+ "IFNULL(SUM(INTEREST_CGST_AMT),0) AS GSTN_INTEREST_CGST_AMT,"
				+ "IFNULL(SUM(INTEREST_SGST_AMT),0) AS GSTN_INTEREST_SGST_AMT,"
				+ "IFNULL(SUM(INTEREST_CESS_AMT),0) AS GSTN_INTEREST_CESS_AMT,"
				+ "IFNULL(SUM(LATEFEE_CGST_AMT),0) AS GSTN_LATEFEE_CGST_AMT,"
				+ "IFNULL(SUM(LATEFEE_SGST_AMT),0) AS GSTN_LATEFEE_SGST_AMT "
				+ "FROM GETRET1_INTEREST_LATEFEE WHERE IS_DELETE = FALSE "
				+ "AND GET_RETURN_TYPE='RET-1' "
				+ "AND GET_TABLE_SECTION IN ('6(1)','6(2)','6(3)','6(4)') "
				+ buildQuery
				+ "GROUP BY GET_TABLE_SECTION ) GROUP BY TABLE,TYPE_OF_SUPPLY";
		LOGGER.debug("REVIEW SUMMARY FOR Interest and Late fee");
		return queryString;

	}

	
	

}
