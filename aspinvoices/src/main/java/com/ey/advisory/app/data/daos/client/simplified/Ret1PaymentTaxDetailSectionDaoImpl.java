package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.simplified.Ret1PaymentTaxDetailSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1RefundDetailSummaryDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
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
@Component("Ret1PaymentTaxDetailSectionDaoImpl")
public class Ret1PaymentTaxDetailSectionDaoImpl
		implements Ret1PaymentTaxDetailSectionDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1PaymentTaxDetailSectionDaoImpl.class);
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	
	@Override
	public List<Ret1PaymentTaxDetailSummaryDto> loadPaymentSummarySection(
			Annexure1SummaryReqDto req) {
			
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
				build.append(" AND GSTIN IN :gstinList ");
			}
		}

		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString();
		LOGGER.debug("Building Where Condition for Inward Query..");

		String queryStr = createQueryString(buildQuery);
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

			List<Ret1PaymentTaxDetailSummaryDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.");
		}
	}

	private Ret1PaymentTaxDetailSummaryDto convert(Object[] arr) {
		Ret1PaymentTaxDetailSummaryDto obj = new Ret1PaymentTaxDetailSummaryDto();

		LOGGER.debug("Converting array Data To dto ");
		obj.setId((GenUtil.getBigInteger(arr[0])).longValue());
		//obj.setId((Long) arr[0]);
		
		obj.setsNo((GenUtil.getBigInteger(arr[1])).intValue());
		obj.setGstin((String) arr[2]);
		obj.setTaxPeriod((String) arr[3]);
		obj.setReturnType((String) arr[4]);
		obj.setDesc((String) arr[5]);
		obj.setTaxPayableRc((BigDecimal) arr[6]);
		obj.setTaxPayableOtherRc((BigDecimal) arr[7]);
		obj.setTaxPaidRc((BigDecimal) arr[8]);
		obj.setTaxPaidOtherRc((BigDecimal) arr[9]);
		obj.setAdjLiabilityRc((BigDecimal) arr[10]);
		obj.setAdjLiabilityOtherRc((BigDecimal) arr[11]);
		obj.setItcPaidIgst((BigDecimal) arr[12]);
		obj.setItcPaidCgst((BigDecimal) arr[13]);
		obj.setItcPaidSgst((BigDecimal) arr[14]);
		obj.setItcPaidCess((BigDecimal) arr[15]);
		obj.setCashPaidTax((BigDecimal) arr[16]);
		obj.setCashPaidInterest((BigDecimal) arr[17]);
		obj.setCashPaidLateFee((BigDecimal) arr[18]);
		obj.setUserDefined1((String) arr[19]);
		obj.setUserDefined2((String) arr[20]);
		obj.setUserDefined3((String) arr[21]);
		return obj;
	}

	private String createQueryString(String buildQuery) {

		LOGGER.debug("Executing ASP Query Executing");
		
			String queryStr = " SELECT ID,IFNULL(SUM(SR_NO),0),"
					+ "GSTIN,RETURN_PERIOD,"
					+ "RETURN_TYPE,DESCRIPTION,"
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
					+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 "
					+ "FROM RET_PROCESSED_SETOFF_UTILIZATION "
					+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
					+ "AND DESCRIPTION IN ('igst','cgst','sgst','cess') "
					+ buildQuery
					+ "GROUP BY ID,SR_NO,GSTIN,RETURN_PERIOD,RETURN_TYPE,DESCRIPTION,"
					+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 ";
		LOGGER.debug("Executing Asp Query For  END ");
		return queryStr;
	}
// For Refund Section 
	@Override
	public List<Ret1RefundDetailSummaryDto> loadRefundSummarySection(
			Annexure1SummaryReqDto req) {
		
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
				build.append(" AND GSTIN IN :gstinList ");
			}
		}

		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString();
		LOGGER.debug("Building Where Condition for Inward Query..");

		String queryStr = createQueryStringRefund(buildQuery);
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

			List<Ret1RefundDetailSummaryDto> retList = list.parallelStream()
					.map(o -> convertRefund(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.");
		}
	}

	private Ret1RefundDetailSummaryDto convertRefund(Object[] arr) {
		Ret1RefundDetailSummaryDto obj = new Ret1RefundDetailSummaryDto();

		LOGGER.debug("Converting array Data To dto ");
	
		obj.setId((GenUtil.getBigInteger(arr[0])).longValue());
		obj.setsNo((GenUtil.getBigInteger(arr[1])).intValue());
		//obj.setsNo((Integer) arr[1]);
		obj.setGstin((String) arr[2]);
		obj.setTaxPeriod((String) arr[3]);
		obj.setDesc((String) arr[4]);
		obj.setTax((BigDecimal) arr[5]);
		obj.setInterest((BigDecimal) arr[6]);
		obj.setPenalty((BigDecimal) arr[7]);
		obj.setFee((BigDecimal) arr[8]);
		obj.setOther((BigDecimal) arr[9]);
		obj.setTotal((BigDecimal) arr[10]);
		obj.setUserDefined1((String) arr[11]);
		obj.setUserDefined2((String) arr[12]);
		obj.setUserDefined3((String) arr[13]);
		
		return obj;
	}

	private String createQueryStringRefund(String buildQuery) {

		LOGGER.debug("Executing ASP Query Executing");
		
			String queryStr = "SELECT ID,IFNULL(SUM(SR_NO),0),GSTIN,RETURN_PERIOD,"
					+ "DESCRIPTION,SUM(TAX) AS TAX,"
					+ "SUM(INTEREST) AS INTEREST,SUM(PENALTY) AS PENALTY,"
					+ "SUM(FEE) AS FEE,SUM(OTHER) AS OTHER,"
					+ "SUM(TOTAL) AS TOTAL,USERDEFINED1,"
					+ "USERDEFINED2,USERDEFINED3 "
					+ "FROM RET_PROCESSED_REFUND_FROM_E_CASHLEDGER WHERE "
					+ "IS_DELETE= FALSE AND "
					+ "DESCRIPTION IN ('igst','cgst','sgst','cess') "
					+ buildQuery
					+ "GROUP BY ID,SR_NO,GSTIN,RETURN_PERIOD,"
					+ "DESCRIPTION,USERDEFINED1,"
					+ "USERDEFINED2,USERDEFINED3";
		LOGGER.debug("Executing Asp Query For  END ");
		return queryStr;
	}

	
	// RET1A Payment Tax  Detail
	@Override
	public List<Ret1PaymentTaxDetailSummaryDto> loadPaymentSummarySectionrRet1A(
			Annexure1SummaryReqDto req) {
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
				build.append(" AND GSTIN IN :gstinList ");
			}
		}

		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString();
		LOGGER.debug("Building Where Condition for Inward Query..");

		String queryStr = createQueryStringRet1A(buildQuery);
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

			List<Ret1PaymentTaxDetailSummaryDto> retList = list.parallelStream()
					.map(o -> convertRet1A(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.");
		}
	}

	private Ret1PaymentTaxDetailSummaryDto convertRet1A(Object[] arr) {
		Ret1PaymentTaxDetailSummaryDto obj = new Ret1PaymentTaxDetailSummaryDto();

		LOGGER.debug("Converting array Data To dto ");
		
		obj.setDesc((String) arr[1]);
		obj.setTaxPayableRc((BigDecimal) arr[2]);
		obj.setTaxPayableOtherRc((BigDecimal) arr[3]);
		obj.setTaxPaidRc((BigDecimal) arr[3]);
		obj.setTaxPaidOtherRc((BigDecimal) arr[4]);
		obj.setAdjLiabilityRc((BigDecimal) arr[5]);
		obj.setAdjLiabilityOtherRc((BigDecimal) arr[6]);
		obj.setItcPaidIgst((BigDecimal) arr[7]);
		obj.setItcPaidCgst((BigDecimal) arr[8]);
		obj.setItcPaidSgst((BigDecimal) arr[9]);
		obj.setItcPaidCess((BigDecimal) arr[10]);
		obj.setCashPaidTax((BigDecimal) arr[11]);
		obj.setCashPaidInterest((BigDecimal) arr[12]);
		obj.setCashPaidLateFee((BigDecimal) arr[13]);
		obj.setUserDefined1((String) arr[14]);
		obj.setUserDefined2((String) arr[15]);
		obj.setUserDefined3((String) arr[16]);
		return obj;
	}

	private String createQueryStringRet1A(String buildQuery) {

		LOGGER.debug("Executing ASP Query Executing");
		
			String queryStr = " SELECT RETURN_TYPE,DESCRIPTION,"
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
					+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 "
					+ "FROM RET_PROCESSED_SETOFF_UTILIZATION "
					+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
					+ "AND DESCRIPTION IN ('igst','cgst','sgst','cess') "
					+ buildQuery
					+ "GROUP BY RETURN_TYPE,DESCRIPTION,"
					+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 ";
		LOGGER.debug("Executing Asp Query For  END ");
		return queryStr;
	}

	

}
