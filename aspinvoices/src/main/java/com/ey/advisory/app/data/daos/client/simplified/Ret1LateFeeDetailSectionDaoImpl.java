package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeDetailSummaryDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;


@Component("Ret1LateFeeDetailSectionDaoImpl")
public class Ret1LateFeeDetailSectionDaoImpl implements Ret1LateFeeDetailSectionDao{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1LateFeeDetailSectionDaoImpl.class);
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Override
	public List<Ret1LateFeeDetailSummaryDto> loadBasicSummarySection(
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
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Building Where Condition for Inward Query.." + buildQuery);
		}

		String queryStr = createQueryString(buildQuery);
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Building Where Condition for Inward Query.." + queryStr);
		}
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

			List<Ret1LateFeeDetailSummaryDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.",e);
		}
	}

	private Ret1LateFeeDetailSummaryDto convert(Object[] arr) {
		Ret1LateFeeDetailSummaryDto obj = new Ret1LateFeeDetailSummaryDto();

		LOGGER.debug("Converting array Data To dto ");
		obj.setId((GenUtil.getBigInteger(arr[0])).longValue());
		//obj.setId((Long) (arr[0]));
		obj.setsNo((GenUtil.getBigInteger(arr[1])).intValue());
		//obj.setsNo((Integer) (arr[1]));
		obj.setGstin((String) arr[2]);
		obj.setTaxPeriod((String) arr[3]);
		obj.setReturnType((String) arr[4]);
		obj.setReturnTable((String) arr[5]);
		obj.setIgstInterest((BigDecimal) arr[6]);
		obj.setCgstInterest((BigDecimal) arr[7]);
		obj.setSgstInterest((BigDecimal) arr[8]);
		obj.setCessInterest((BigDecimal) arr[9]);
		obj.setCgstLateFee((BigDecimal) arr[10]);
		obj.setSgstLateFee((BigDecimal) arr[11]);
		obj.setUserDefined1((String) arr[12]);
		obj.setUserDefined2((String) arr[13]);
		obj.setUserDefined3((String) arr[14]);
		
		return obj;
	}

	private String createQueryString(String buildQuery) {

		LOGGER.debug("Executing ASP Query Executing");
		
			String queryStr = "SELECT ID,IFNULL(SUM(SR_NO),0) AS SR_NO,"
					+ "GSTIN,RETURN_PERIOD,"
					+ "RETURN_TYPE,RETURN_TABLE,"
					+ "IFNULL(SUM(INTEREST_IGST_AMT),0) AS  INTEREST_IGST_AMT,"
					+ "IFNULL(SUM(INTEREST_CGST_AMT),0) AS  INTEREST_CGST_AMT,"
					+ "IFNULL(SUM(INTEREST_SGST_AMT),0) AS  INTEREST_SGST_AMT,"
					+ "IFNULL(SUM(INTEREST_CESS_AMT),0) AS  INTEREST_CESS_AMT,"
					+ "IFNULL(SUM(LATEFEE_CGST_AMT),0) AS  LATEFEE_CGST_AMT,"
					+ "IFNULL(SUM(LATEFEE_SGST_AMT),0) AS  LATEFEE_SGST_AMT,"
					+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 "
					+ "FROM RET_PROCESSED_INTEREST_LATEFEE "
					+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE IN ('6(1)','6(2)','6(3)','6(4)') "
					+ buildQuery
					+ "GROUP BY ID,SR_NO,GSTIN,RETURN_PERIOD,"
					+ "RETURN_TYPE,RETURN_TABLE,USERDEFINED1,"
					+ "USERDEFINED2,USERDEFINED3 ";
		LOGGER.debug("Executing Asp Query For  END ");
		return queryStr;
	}

	@Override
	public List<Ret1LateFeeDetailSummaryDto> loadBasicSummarySectionRet1A(
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
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Building Where Condition for Inward Query..");
		}

		String queryStr = createQueryStringRet1A(buildQuery);
		
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Executing LateFee Vertical Query " + queryStr);
		}
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

			List<Ret1LateFeeDetailSummaryDto> retList = list.parallelStream()
					.map(o -> convertRet1A(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.",e);
		}
	}

	private Ret1LateFeeDetailSummaryDto convertRet1A(Object[] arr) {
		Ret1LateFeeDetailSummaryDto obj = new Ret1LateFeeDetailSummaryDto();

		LOGGER.debug("Converting array Data To dto ");
		obj.setReturnTable((String) arr[1]);
		obj.setIgstInterest((BigDecimal) arr[2]);
		obj.setCgstInterest((BigDecimal) arr[3]);
		obj.setSgstInterest((BigDecimal) arr[4]);
		obj.setCessInterest((BigDecimal) arr[5]);
		obj.setCgstLateFee((BigDecimal) arr[6]);
		obj.setSgstLateFee((BigDecimal) arr[7]);
		obj.setUserDefined1((String) arr[8]);
		obj.setUserDefined2((String) arr[9]);
		obj.setUserDefined3((String) arr[10]);
		
		return obj;
	}

	private String createQueryStringRet1A(String buildQuery) {

		LOGGER.debug("Executing ASP Query Executing");
		
			/*String queryStr = "SELECT RETURN_TYPE,RETURN_TABLE,"
					+ "IFNULL(SUM(INTEREST_IGST_AMT),0) AS  INTEREST_IGST_AMT,"
					+ "IFNULL(SUM(INTEREST_CGST_AMT),0) AS  INTEREST_CGST_AMT,"
					+ "IFNULL(SUM(INTEREST_SGST_AMT),0) AS  INTEREST_SGST_AMT,"
					+ "IFNULL(SUM(INTEREST_CESS_AMT),0) AS  INTEREST_CESS_AMT,"
					+ "IFNULL(SUM(LATEFEE_CGST_AMT),0) AS  LATEFEE_CGST_AMT,"
					+ "IFNULL(SUM(LATEFEE_SGST_AMT),0) AS  LATEFEE_SGST_AMT,"
					+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 "
					+ "FROM RET_PROCESSED_INTEREST_LATEFEE "
					+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1A' "
					+ "AND RETURN_TABLE IN ('5(1)','5(2)','5(3)','5(4)') "
					+ buildQuery
					+ "GROUP BY RETURN_TYPE,RETURN_TABLE,USERDEFINED1,"
					+ "USERDEFINED2,USERDEFINED3 ";*/
		
		String queryStr = "SELECT ID,IFNULL(SUM(SR_NO),0) AS SR_NO, "
					+ "GSTIN,RETURN_TYPE,RETURN_TABLE,"
					+ "IFNULL(SUM(INTEREST_IGST_AMT),0) AS  INTEREST_IGST_AMT,"
					+ "IFNULL(SUM(INTEREST_CGST_AMT),0) AS  INTEREST_CGST_AMT,"
					+ "IFNULL(SUM(INTEREST_SGST_AMT),0) AS  INTEREST_SGST_AMT,"
					+ "IFNULL(SUM(INTEREST_CESS_AMT),0) AS  INTEREST_CESS_AMT,"
					+ "IFNULL(SUM(LATEFEE_CGST_AMT),0) AS  LATEFEE_CGST_AMT,"
					+ "IFNULL(SUM(LATEFEE_SGST_AMT),0) AS  LATEFEE_SGST_AMT,"
					+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 "
					+ "FROM RET_PROCESSED_INTEREST_LATEFEE "
					+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1A' "
					+ "AND RETURN_TABLE IN ('5(1)','5(2)','5(3)','5(4)') "
					+ buildQuery
					+ "GROUP BY ID,SR_NO,GSTIN,RETURN_PERIOD,"
					+ "RETURN_TYPE,RETURN_TABLE,USERDEFINED1,"
					+ "USERDEFINED2,USERDEFINED3 ";
		
		LOGGER.debug("Executing Asp Query For  END ");
		return queryStr;
	}

	
}
