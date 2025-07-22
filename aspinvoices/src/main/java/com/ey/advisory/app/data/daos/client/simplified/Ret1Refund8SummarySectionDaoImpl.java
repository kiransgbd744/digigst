package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Ret1RefundSummarySectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Slf4j
@Component("Ret1Refund8SummarySectionDaoImpl")
public class Ret1Refund8SummarySectionDaoImpl
		implements Ret1Refund8SummarySectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Override
	public List<Ret1RefundSummarySectionDto> lateBasicSummarySection(
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

		LOGGER.debug(" Refunf Section 8 Query  " + queryStr);

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
			List<Ret1RefundSummarySectionDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.");
		}
	}

	private Ret1RefundSummarySectionDto convert(Object[] arr) {
		Ret1RefundSummarySectionDto obj = new Ret1RefundSummarySectionDto();
		LOGGER.debug("Array data Setting to Dto");
		obj.setTable((String) arr[0]);
		obj.setDesc((String) arr[1]);
		obj.setUsrTax((BigDecimal) arr[2]);
		obj.setUsrInterest((BigDecimal) arr[3]);
		obj.setUsrPenality((BigDecimal) arr[4]);
		obj.setUsrFee((BigDecimal) arr[5]);
		obj.setUsrOther((BigDecimal) arr[6]);
		obj.setUsrTotal((BigDecimal) arr[7]);
		obj.setGstnTax((BigDecimal) arr[8]);
		obj.setGstnInterest((BigDecimal) arr[9]);
		obj.setGstnPenality((BigDecimal) arr[10]);
		obj.setGstnFee((BigDecimal) arr[11]);
		obj.setGstnOther((BigDecimal) arr[12]);
		obj.setGstnTotal((BigDecimal) arr[13]);
		return obj;
	}

	private String createQueryString(String buildQuery) {
		LOGGER.debug(
				"REVIEW SUMMARY FOR PAYMENT OF TAX  Query Execution BEGIN ");

		String queryString = "SELECT TABLE1,TYPE_OF_SUPPLY,"
				+ "SUM(ASP_TAX) AS ASP_TAX,SUM(ASP_INTEREST) AS ASP_INTEREST,"
				+ "SUM(ASP_PENALTY) AS ASP_PENALTY,SUM(ASP_FEE) AS ASP_FEE,"
				+ "SUM(ASP_OTHER) AS ASP_OTHER,SUM(ASP_TOTAL) AS ASP_TOTAL,"
				+ "SUM(GSTN_TAX) AS GSTN_TAX,"
				+ "SUM(GSTN_INTEREST) AS GSTN_INTEREST,"
				+ "SUM(GSTN_PENALTY) AS GSTN_PENALTY,SUM(GSTN_FEE) AS GSTN_FEE,"
				+ "SUM(GSTN_OTHER) AS GSTN_OTHER,SUM(GSTN_TOTAL) AS GSTN_TOTAL "
				+ "FROM (SELECT DESCRIPTION AS TYPE_OF_SUPPLY,"
				+ "(case WHEN DESCRIPTION='igst' THEN '8(1)' "
				+ "WHEN DESCRIPTION='cgst' THEN '8(2)' "
				+ "WHEN DESCRIPTION='sgst' THEN '8(3)' "
				+ "WHEN DESCRIPTION='cess' THEN '8(4)' END ) AS TABLE1,"
				+ "IFNULL(SUM(TAX),0) AS ASP_TAX,"
				+ "IFNULL(SUM(INTEREST),0) AS ASP_INTEREST,"
				+ "IFNULL(SUM(PENALTY),0) AS ASP_PENALTY,"
				+ "IFNULL(SUM(FEE),0) AS ASP_FEE,"
				+ "IFNULL(SUM(OTHER),0) AS ASP_OTHER,"
				+ "IFNULL(SUM(TOTAL),0) AS ASP_TOTAL,"
				+ "0 AS GSTN_TAX,0 AS GSTN_INTEREST,0 AS GSTN_PENALTY, "
				+ "0 AS GSTN_FEE,0 AS GSTN_OTHER,0 AS GSTN_TOTAL "
				+ "FROM RET_PROCESSED_REFUND_FROM_E_CASHLEDGER "
				+ "WHERE IS_DELETE = FALSE "
				+ "AND DESCRIPTION IN ('igst','sgst','cgst','cess')"
				+ buildQuery + "GROUP BY DESCRIPTION,RETURN_PERIOD,GSTIN,SR_NO "
				+ "UNION ALL " + "SELECT GET_DESCRIPTION AS TYPE_OF_SUPPLY,"
				+ "(case WHEN GET_DESCRIPTION='igst' THEN '8(1)' "
				+ "WHEN GET_DESCRIPTION='cgst' THEN '8(2)' "
				+ "WHEN GET_DESCRIPTION='sgst' THEN '8(3)' "
				+ "WHEN GET_DESCRIPTION='cess' THEN '8(4)' END ) AS TABLE1,"
				+ "0 AS ASP_TAX,0 AS ASP_INTEREST,0 AS ASP_PENALTY,"
				+ "0 AS ASP_FEE,0 AS ASP_OTHER,0 AS ASP_TOTAL,"
				+ "IFNULL(SUM(TAX),0) AS GSTN_TAX,"
				+ "IFNULL(SUM(INTEREST),0) AS GSTN_INTEREST,"
				+ "IFNULL(SUM(PENALTY),0) AS GSTN_PENALTY,"
				+ "IFNULL(SUM(FEE),0) AS GSTN_FEE,"
				+ "IFNULL(SUM(OTHER),0) AS GSTN_OTHER,"
				+ "IFNULL(SUM(TOTAL),0) AS GSTN_TOTAL "
				+ "FROM GETRET1_REFUND_CLAIMED_FROM_E_CASHLEDGER "
				+ "WHERE IS_DELETE = FALSE  "
				+ "AND GET_DESCRIPTION IN ('igst','sgst','cgst','cess') "
				+ buildQuery + "GROUP BY GET_DESCRIPTION " + "UNION ALL "
				+ "SELECT 'cashLedgerRefund' AS TYPE_OF_SUPPLY,"
				+ "'8' AS TABLE1,IFNULL(SUM(TAX),0) AS ASP_TAX,"
				+ "IFNULL(SUM(INTEREST),0) AS ASP_INTEREST,"
				+ "IFNULL(SUM(PENALTY),0) AS ASP_PENALTY,"
				+ "IFNULL(SUM(FEE),0) AS ASP_FEE,"
				+ "IFNULL(SUM(OTHER),0) AS ASP_OTHER,"
				+ "IFNULL(SUM(TOTAL),0) AS ASP_TOTAL,"
				+ "0 AS GSTN_TAX,0 AS GSTN_INTEREST,"
				+ "0 AS GSTN_PENALTY,0 AS GSTN_FEE,0 AS GSTN_OTHER,"
				+ "0 AS GSTN_TOTAL "
				+ "FROM RET_PROCESSED_REFUND_FROM_E_CASHLEDGER  "
				+ "WHERE IS_DELETE = FALSE  "
				+ "AND DESCRIPTION IN ('igst','sgst','cgst','cess') "
				+ buildQuery + "GROUP BY DESCRIPTION " + "UNION ALL "
				+ "SELECT 'cashLedgerRefund' AS TYPE_OF_SUPPLY,"
				+ "'8' AS TABLE1,0 AS ASP_TAX,0 AS ASP_INTEREST,"
				+ "0 AS ASP_PENALTY,0 AS ASP_FEE,0 AS ASP_OTHER,0 AS ASP_TOTAL,"
				+ "IFNULL(SUM(TAX),0) AS GSTN_TAX,"
				+ "IFNULL(SUM(INTEREST),0) AS GSTN_INTEREST,"
				+ "IFNULL(SUM(PENALTY),0) AS GSTN_PENALTY,"
				+ "IFNULL(SUM(FEE),0) AS GSTN_FEE,"
				+ "IFNULL(SUM(OTHER),0) AS GSTN_OTHER,"
				+ "IFNULL(SUM(TOTAL),0) AS GSTN_TOTAL "
				+ "FROM GETRET1_REFUND_CLAIMED_FROM_E_CASHLEDGER "
				+ "WHERE IS_DELETE = FALSE "
				+ "AND GET_DESCRIPTION IN ('igst','sgst','cgst','cess') "
				+ buildQuery
				+ "GROUP BY GET_DESCRIPTION ) GROUP BY TABLE1,TYPE_OF_SUPPLY";
		LOGGER.debug("REVIEW SUMMARY FOR PAYMENT OF TAX END");
		return queryString;

	}

}
