package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Ret1PaymentSummarySectionDto;
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
@Component("Ret1Payment_7_BasicDocSummarySectionDaoImpl")
public class Ret1Payment_7_BasicDocSummarySectionDaoImpl
		implements Ret1Payment7BasicDocSummarySectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Override
	public List<Ret1PaymentSummarySectionDto> lateBasicSummarySection(
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

		LOGGER.debug("Prepared where Condition and apply For Section 8 Query");

		String queryStr = createQueryString(buildQuery);

		LOGGER.debug(
				"Outward Query BEGIN----> Sectionb 8.1 to 8.4 " + queryStr);

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
			List<Ret1PaymentSummarySectionDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			LOGGER.debug("ResultList data for Section 8 ---->" + retList);
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.");
		}
	}

	private Ret1PaymentSummarySectionDto convert(Object[] arr) {
		Ret1PaymentSummarySectionDto obj = new Ret1PaymentSummarySectionDto();
		LOGGER.debug("Array data Setting to Dto");
		obj.setTable((String) arr[0]);
		obj.setSupplyType((String) arr[1]);
		obj.setAsptaxPayableRc((BigDecimal) arr[2]);
		obj.setAsptaxPayableOtherRc((BigDecimal) arr[3]);
		obj.setAsptaxAlreadyPaidRc((BigDecimal) arr[4]);
		obj.setAsptaxAlreadyPaidotherRc((BigDecimal) arr[5]);
		obj.setAspadjOflibRc((BigDecimal) arr[6]);
		obj.setAspadjOflibOtherRc((BigDecimal) arr[7]);
		obj.setAsppaidThroughIgst((BigDecimal) arr[8]);
		obj.setAsppaidThroughCgst((BigDecimal) arr[9]);
		obj.setAsppaidThroughSgst((BigDecimal) arr[10]);
		obj.setAsppaidThroughCess((BigDecimal) arr[11]);
		obj.setAsppaidIncash_tax((BigDecimal) arr[12]);
		obj.setAsppaidIncash_interest((BigDecimal) arr[13]);
		obj.setAsppaidIncash_latefee((BigDecimal) arr[14]);
		obj.setGstntaxPayable_rc((BigDecimal) arr[15]);
		obj.setGstntaxPayable_Other_rc((BigDecimal) arr[16]);
		obj.setGstnalready_paid_rc((BigDecimal) arr[17]);
		obj.setGstnalready_paid_Other_rc((BigDecimal) arr[18]);
		obj.setGstnadjOflibRc((BigDecimal) arr[19]);
		obj.setGstnadjOflibOtherRc((BigDecimal) arr[20]);
		obj.setGstnpaidThroughIgst((BigDecimal) arr[21]);
		obj.setGstnpaidThroughCgst((BigDecimal) arr[22]);
		obj.setGstnpaidThroughSgst((BigDecimal) arr[23]);
		obj.setGstnpaidThroughCess((BigDecimal) arr[24]);
		obj.setGstnpaidIncash_tax((BigDecimal) arr[25]);
		obj.setGstnpaidIncash_interest((BigDecimal) arr[26]);
		obj.setGstnpaidIncash_latefee((BigDecimal) arr[27]);

		return obj;
	}

	private String createQueryString(String buildQuery) {
		LOGGER.debug(
				"REVIEW SUMMARY FOR PAYMENT OF TAX  Query Execution BEGIN ");

		String queryString = "SELECT TABLE,TYPE_OF_SUPPLY,"
				+ "SUM(ASP_TAX_PAYABLE_REVCHARGE) AS ASP_TAX_PAYABLE_REVCHARGE,"
				+ "SUM(ASP_TAX_PAYABLE_OTHERTHAN_REVCHARGE) "
				+ "AS ASP_TAX_PAYABLE_OTHERTHAN_REVCHARGE,"
				+ "SUM(ASP_TAX_ALREADYPAID_REVCHARGE) "
				+ "AS ASP_TAX_ALREADYPAID_REVCHARGE,"
				+ "SUM(ASP_TAX_ALREADYPAID_OTHERTHAN_REVCHARGE) "
				+ "AS ASP_TAX_ALREADYPAID_OTHERTHAN_REVCHARGE,"
				+ "SUM(ASP_ADJ_NEG_LIB_PRETAXPERIOD_RC) "
				+ "AS ASP_ADJ_NEG_LIB_PRETAXPERIOD_RC,"
				+ "SUM(ASP_ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC) "
				+ "AS ASP_ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC,"
				+ "SUM(ASP_PAID_THROUGH_ITC_IGST) AS ASP_PAID_THROUGH_ITC_IGST,"
				+ "SUM(ASP_PAID_THROUGH_ITC_CGST) AS ASP_PAID_THROUGH_ITC_CGST,"
				+ "SUM(ASP_PAID_THROUGH_ITC_SGST) AS ASP_PAID_THROUGH_ITC_SGST,"
				+ "SUM(ASP_PAID_THROUGH_ITC_CESS) AS ASP_PAID_THROUGH_ITC_CESS,"
				+ "SUM(ASP_PAID_IN_CASH_TAX_CESS) AS ASP_PAID_IN_CASH_TAX_CESS,"
				+ "SUM(ASP_PAID_IN_CASH_INTEREST) AS ASP_PAID_IN_CASH_INTEREST,"
				+ "SUM(ASP_PAID_IN_CASH_LATEFEE) AS ASP_PAID_IN_CASH_LATEFEE,"
				+ "SUM(GSTN_TAX_PAYABLE_REVCHARGE) "
				+ "AS GSTN_TAX_PAYABLE_REVCHARGE,"
				+ "SUM(GSTN_TAX_PAYABLE_OTHERTHAN_REVCHARGE) "
				+ "AS GSTN_TAX_PAYABLE_OTHERTHAN_REVCHARGE,"
				+ "SUM(GSTN_TAX_ALREADYPAID_REVCHARGE) "
				+ "AS GSTN_TAX_ALREADYPAID_REVCHARGE,"
				+ "SUM(GSTN_TAX_ALREADYPAID_OTHERTHAN_REVCHARGE) "
				+ "AS GSTN_TAX_ALREADYPAID_OTHERTHAN_REVCHARGE,"
				+ "SUM(GSTN_ADJ_NEG_LIB_PRETAXPERIOD_RC) "
				+ "AS GSTN_ADJ_NEG_LIB_PRETAXPERIOD_RC,"
				+ "SUM(GSTN_ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC) "
				+ "AS GSTN_ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC,"
				+ "SUM(GSTN_PAID_THROUGH_ITC_IGST) AS GSTN_PAID_THROUGH_ITC_IGST,"
				+ "SUM(GSTN_PAID_THROUGH_ITC_CGST) AS GSTN_PAID_THROUGH_ITC_CGST,"
				+ "SUM(GSTN_PAID_THROUGH_ITC_SGST) AS GSTN_PAID_THROUGH_ITC_SGST,"
				+ "SUM(GSTN_PAID_THROUGH_ITC_CESS) AS GSTN_PAID_THROUGH_ITC_CESS,"
				+ "SUM(GSTN_PAID_IN_CASH_TAX_CESS) AS GSTN_PAID_IN_CASH_TAX_CESS,"
				+ "SUM(GSTN_PAID_IN_CASH_INTEREST) AS GSTN_PAID_IN_CASH_INTEREST,"
				+ "SUM(GSTN_PAID_IN_CASH_LATEFEE) AS GSTN_PAID_IN_CASH_LATEFEE "
				+ "FROM (SELECT DESCRIPTION AS TYPE_OF_SUPPLY,"
				+ "(case WHEN DESCRIPTION='igst' THEN '7(1)' "
				+ "WHEN DESCRIPTION='cgst' THEN '7(2)' "
				+ "WHEN DESCRIPTION='sgst' THEN '7(3)' "
				+ "WHEN DESCRIPTION='cess' THEN '7(4)' END ) AS TABLE,"
				+ "IFNULL(SUM(TAX_PAYABLE_REVCHARGE),0) "
				+ "AS ASP_TAX_PAYABLE_REVCHARGE,"
				+ "IFNULL(SUM(TAX_PAYABLE_OTHERTHAN_REVCHARGE),0) "
				+ "AS ASP_TAX_PAYABLE_OTHERTHAN_REVCHARGE,"
				+ "IFNULL(SUM(TAX_ALREADYPAID_REVCHARGE),0) "
				+ "AS ASP_TAX_ALREADYPAID_REVCHARGE,"
				+ "IFNULL(SUM(TAX_ALREADYPAID_OTHERTHAN_REVCHARGE),0) "
				+ "AS ASP_TAX_ALREADYPAID_OTHERTHAN_REVCHARGE,"
				+ "IFNULL(SUM(ADJ_NEG_LIB_PRETAXPERIOD_RC),0) "
				+ "AS ASP_ADJ_NEG_LIB_PRETAXPERIOD_RC,"
				+ "IFNULL(SUM(ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC),0) "
				+ "AS ASP_ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_IGST),0) "
				+ "AS ASP_PAID_THROUGH_ITC_IGST,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_CGST),0) "
				+ "AS ASP_PAID_THROUGH_ITC_CGST,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_SGST),0) "
				+ "AS ASP_PAID_THROUGH_ITC_SGST,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_CESS),0) "
				+ "AS ASP_PAID_THROUGH_ITC_CESS,"
				+ "IFNULL(SUM(PAID_IN_CASH_TAX_CESS),0) "
				+ "AS ASP_PAID_IN_CASH_TAX_CESS,"
				+ "IFNULL(SUM(PAID_IN_CASH_INTEREST),0) "
				+ "AS ASP_PAID_IN_CASH_INTEREST,"
				+ "IFNULL(SUM(PAID_IN_CASH_LATEFEE),0) "
				+ "AS ASP_PAID_IN_CASH_LATEFEE,"
				+ "0 AS GSTN_TAX_PAYABLE_REVCHARGE,"
				+ "0 AS GSTN_TAX_PAYABLE_OTHERTHAN_REVCHARGE,"
				+ "0 AS GSTN_TAX_ALREADYPAID_REVCHARGE,"
				+ "0 AS GSTN_TAX_ALREADYPAID_OTHERTHAN_REVCHARGE,"
				+ "0 AS GSTN_ADJ_NEG_LIB_PRETAXPERIOD_RC,"
				+ "0 AS GSTN_ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC,"
				+ "0 AS GSTN_PAID_THROUGH_ITC_IGST,"
				+ "0 AS GSTN_PAID_THROUGH_ITC_CGST,"
				+ "0 AS GSTN_PAID_THROUGH_ITC_SGST,"
				+ "0 AS GSTN_PAID_THROUGH_ITC_CESS,"
				+ "0 AS GSTN_PAID_IN_CASH_TAX_CESS,"
				+ "0 AS GSTN_PAID_IN_CASH_INTEREST,"
				+ "0 AS GSTN_PAID_IN_CASH_LATEFEE "
				+ "FROM RET_PROCESSED_SETOFF_UTILIZATION "
				+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' "
				+ "AND DESCRIPTION IN ('igst','sgst','cgst','cess') "
				+ buildQuery + "GROUP BY DESCRIPTION,RETURN_PERIOD,"
				+ "GSTIN,SR_NO,RETURN_TYPE " 
				+ "UNION ALL "
				+ "SELECT GET_DESCRIPTION AS TYPE_OF_SUPPLY,"
				+ "(case WHEN GET_DESCRIPTION='igst' THEN '7(1)' "
				+ "WHEN GET_DESCRIPTION='cgst' THEN '7(2)' "
				+ "WHEN GET_DESCRIPTION='sgst' THEN '7(3)' "
				+ "WHEN GET_DESCRIPTION='cess' THEN '7(4)' END ) AS TABLE,"
				+ "0 AS ASP_TAX_PAYABLE_REVCHARGE,"
				+ "0 AS ASP_TAX_PAYABLE_OTHERTHAN_REVCHARGE,"
				+ "0 AS ASP_TAX_ALREADYPAID_REVCHARGE,"
				+ "0 AS ASP_TAX_ALREADYPAID_OTHERTHAN_REVCHARGE,"
				+ "0 AS ASP_ADJ_NEG_LIB_PRETAXPERIOD_RC,"
				+ "0 AS ASP_ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC,"
				+ "0 AS ASP_PAID_THROUGH_ITC_IGST,"
				+ "0 AS ASP_PAID_THROUGH_ITC_CGST,"
				+ "0 AS ASP_PAID_THROUGH_ITC_SGST,"
				+ "0 AS ASP_PAID_THROUGH_ITC_CESS,"
				+ "0 AS ASP_PAID_IN_CASH_TAX_CESS,"
				+ "0 AS ASP_PAID_IN_CASH_INTEREST,"
				+ "0 AS ASP_PAID_IN_CASH_LATEFEE,"
				+ "IFNULL(SUM(TAX_PAYABLE_REVCHARGE),0) "
				+ "AS GSTN_TAX_PAYABLE_REVCHARGE,"
				+ "IFNULL(SUM(TAX_PAYABLE_OTHERTHAN_REVCHARGE),0) "
				+ "AS GSTN_TAX_PAYABLE_OTHERTHAN_REVCHARGE,"
				+ "IFNULL(SUM(TAX_ALREADYPAID_REVCHARGE),0) "
				+ "AS GSTN_TAX_ALREADYPAID_REVCHARGE,"
				+ "IFNULL(SUM(TAX_ALREADYPAID_OTHERTHAN_REVCHARGE),0) "
				+ "AS GSTN_TAX_ALREADYPAID_OTHERTHAN_REVCHARGE,"
				+ "IFNULL(SUM(ADJ_NEG_LIB_PRETAXPERIOD_RC),0) "
				+ "AS GSTN_ADJ_NEG_LIB_PRETAXPERIOD_RC,"
				+ "IFNULL(SUM(ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC),0) "
				+ "AS GSTN_ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_IGST),0) "
				+ "AS GSTN_PAID_THROUGH_ITC_IGST,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_CGST),0) "
				+ "AS GSTN_PAID_THROUGH_ITC_CGST,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_SGST),0) "
				+ "AS GSTN_PAID_THROUGH_ITC_SGST,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_CESS),0) "
				+ "AS GSTN_PAID_THROUGH_ITC_CESS,"
				+ "IFNULL(SUM(PAID_IN_CASH_TAX_CESS),0) "
				+ "AS GSTN_PAID_IN_CASH_TAX_CESS,"
				+ "IFNULL(SUM(PAID_IN_CASH_INTEREST),0) "
				+ "AS GSTN_PAID_IN_CASH_INTEREST,"
				+ "IFNULL(SUM(PAID_IN_CASH_LATEFEE),0) "
				+ "AS GSTN_PAID_IN_CASH_LATEFEE "
				+ "FROM GETRET1_PAYMENT_OF_TAX WHERE IS_DELETE = FALSE "
				+ "AND GET_DESCRIPTION IN ('igst','sgst','cgst','cess') "
				+ buildQuery + "GROUP BY GET_DESCRIPTION " + "UNION ALL "
				+ "SELECT 'paymentTax' AS TYPE_OF_SUPPLY,'7' AS TABLE,"
				+ "IFNULL(SUM(TAX_PAYABLE_REVCHARGE),0) "
				+ "AS ASP_TAX_PAYABLE_REVCHARGE,"
				+ "IFNULL(SUM(TAX_PAYABLE_OTHERTHAN_REVCHARGE),0) "
				+ "AS ASP_TAX_PAYABLE_OTHERTHAN_REVCHARGE,"
				+ "IFNULL(SUM(TAX_ALREADYPAID_REVCHARGE),0) "
				+ "AS ASP_TAX_ALREADYPAID_REVCHARGE,"
				+ "IFNULL(SUM(TAX_ALREADYPAID_OTHERTHAN_REVCHARGE),0) "
				+ "AS ASP_TAX_ALREADYPAID_OTHERTHAN_REVCHARGE,"
				+ "IFNULL(SUM(ADJ_NEG_LIB_PRETAXPERIOD_RC),0) "
				+ "AS ASP_ADJ_NEG_LIB_PRETAXPERIOD_RC,"
				+ "IFNULL(SUM(ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC),0) "
				+ "AS ASP_ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_IGST),0) "
				+ "AS ASP_PAID_THROUGH_ITC_IGST,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_CGST),0) "
				+ "AS ASP_PAID_THROUGH_ITC_CGST,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_SGST),0) "
				+ "AS ASP_PAID_THROUGH_ITC_SGST, "
				+ "IFNULL(SUM(PAID_THROUGH_ITC_CESS),0) "
				+ "AS ASP_PAID_THROUGH_ITC_CESS,"
				+ "IFNULL(SUM(PAID_IN_CASH_TAX_CESS),0) "
				+ "AS ASP_PAID_IN_CASH_TAX_CESS,"
				+ "IFNULL(SUM(PAID_IN_CASH_INTEREST),0) "
				+ "AS ASP_PAID_IN_CASH_INTEREST,"
				+ "IFNULL(SUM(PAID_IN_CASH_LATEFEE),0) "
				+ "AS ASP_PAID_IN_CASH_LATEFEE,"
				+ "0 AS GSTN_TAX_PAYABLE_REVCHARGE,"
				+ "0 AS GSTN_TAX_PAYABLE_OTHERTHAN_REVCHARGE,"
				+ "0 AS GSTN_TAX_ALREADYPAID_REVCHARGE,"
				+ "0 AS GSTN_TAX_ALREADYPAID_OTHERTHAN_REVCHARGE,"
				+ "0 AS GSTN_ADJ_NEG_LIB_PRETAXPERIOD_RC,"
				+ "0 AS GSTN_ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC,"
				+ "0 AS GSTN_PAID_THROUGH_ITC_IGST,"
				+ "0 AS GSTN_PAID_THROUGH_ITC_CGST,"
				+ "0 AS GSTN_PAID_THROUGH_ITC_SGST,"
				+ "0 AS GSTN_PAID_THROUGH_ITC_CESS,"
				+ "0 AS GSTN_PAID_IN_CASH_TAX_CESS,"
				+ "0 AS GSTN_PAID_IN_CASH_INTEREST,"
				+ "0 AS GSTN_PAID_IN_CASH_LATEFEE "
				+ "FROM RET_PROCESSED_SETOFF_UTILIZATION WHERE "
				+ "IS_DELETE = FALSE AND RETURN_TYPE='RET-1' AND "
				+ "DESCRIPTION IN ('igst','sgst','cgst','cess') " + buildQuery
				+ "GROUP BY DESCRIPTION " + "UNION ALL "
				+ "SELECT 'paymentTax' AS TYPE_OF_SUPPLY,'7' AS TABLE,"
				+ "0 AS ASP_TAX_PAYABLE_REVCHARGE,"
				+ "0 AS ASP_TAX_PAYABLE_OTHERTHAN_REVCHARGE,"
				+ "0 AS ASP_TAX_ALREADYPAID_REVCHARGE,"
				+ "0 AS ASP_TAX_ALREADYPAID_OTHERTHAN_REVCHARGE,"
				+ "0 AS ASP_ADJ_NEG_LIB_PRETAXPERIOD_RC,"
				+ "0 AS ASP_ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC,"
				+ "0 AS ASP_PAID_THROUGH_ITC_IGST,"
				+ "0 AS ASP_PAID_THROUGH_ITC_CGST,"
				+ "0 AS ASP_PAID_THROUGH_ITC_SGST,"
				+ "0 AS ASP_PAID_THROUGH_ITC_CESS,"
				+ "0 AS ASP_PAID_IN_CASH_TAX_CESS,"
				+ "0 AS ASP_PAID_IN_CASH_INTEREST,"
				+ "0 AS ASP_PAID_IN_CASH_LATEFEE,"
				+ "IFNULL(SUM(TAX_PAYABLE_REVCHARGE),0) "
				+ "AS GSTN_TAX_PAYABLE_REVCHARGE,"
				+ "IFNULL(SUM(TAX_PAYABLE_OTHERTHAN_REVCHARGE),0) "
				+ "AS GSTN_TAX_PAYABLE_OTHERTHAN_REVCHARGE,"
				+ "IFNULL(SUM(TAX_ALREADYPAID_REVCHARGE),0) "
				+ "AS GSTN_TAX_ALREADYPAID_REVCHARGE,"
				+ "IFNULL(SUM(TAX_ALREADYPAID_OTHERTHAN_REVCHARGE),0) "
				+ "AS GSTN_TAX_ALREADYPAID_OTHERTHAN_REVCHARGE,"
				+ "IFNULL(SUM(ADJ_NEG_LIB_PRETAXPERIOD_RC),0) "
				+ "AS GSTN_ADJ_NEG_LIB_PRETAXPERIOD_RC,"
				+ "IFNULL(SUM(ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC),0) "
				+ "AS GSTN_ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_IGST),0) "
				+ "AS GSTN_PAID_THROUGH_ITC_IGST,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_CGST),0) "
				+ "AS GSTN_PAID_THROUGH_ITC_CGST,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_SGST),0) "
				+ "AS GSTN_PAID_THROUGH_ITC_SGST,"
				+ "IFNULL(SUM(PAID_THROUGH_ITC_CESS),0) "
				+ "AS GSTN_PAID_THROUGH_ITC_CESS,"
				+ "IFNULL(SUM(PAID_IN_CASH_TAX_CESS),0) "
				+ "AS GSTN_PAID_IN_CASH_TAX_CESS,"
				+ "IFNULL(SUM(PAID_IN_CASH_INTEREST),0) "
				+ "AS GSTN_PAID_IN_CASH_INTEREST,"
				+ "IFNULL(SUM(PAID_IN_CASH_LATEFEE),0) "
				+ "AS GSTN_PAID_IN_CASH_LATEFEE "
				+ "FROM GETRET1_PAYMENT_OF_TAX WHERE IS_DELETE = FALSE "
				+ "AND GET_DESCRIPTION IN ('igst','sgst','cgst','cess') "
				+ buildQuery + "GROUP BY GET_DESCRIPTION )"
				+ "GROUP BY TABLE,TYPE_OF_SUPPLY ";
		LOGGER.debug("REVIEW SUMMARY FOR PAYMENT OF TAX END");
		return queryString;

	}

}
