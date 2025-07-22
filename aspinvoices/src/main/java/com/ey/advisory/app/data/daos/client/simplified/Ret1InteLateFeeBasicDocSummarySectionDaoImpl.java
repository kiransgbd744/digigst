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
 * @author Mahesh.Golla
 *
 */
@Component("Ret1InteLateFeeBasicDocSummarySectionDaoImpl")
public class Ret1InteLateFeeBasicDocSummarySectionDaoImpl
		implements Ret1Int6BasicDocSummarySectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1InteLateFeeBasicDocSummarySectionDaoImpl.class);

	
	@Override
	public List<Ret1LateFeeSummarySectionDto> lateBasicSummarySection(
			Annexure1SummaryReqDto request) {
		
		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		String taxPeriodReq = req.getTaxPeriod();

		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String ProfitCenter = null;
		String plant = null;
		String sales = null;
		String division = null;
		String location = null;
		String purchase = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String gstin = null;

		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> purcList = null;
		List<String> distList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				/*if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					ProfitCenter = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC)
									.size() > 0) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (!dataSecAttrs.get(OnboardingConstant.PLANT).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PLANT)
									.size() > 0) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}
*/
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			/*	if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (!dataSecAttrs.get(OnboardingConstant.DIVISION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.DIVISION)
									.size() > 0) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.LOCATION)
									.size() > 0) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					if (!dataSecAttrs.get(OnboardingConstant.SO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.SO)
									.size() > 0) {
						salesList = dataSecAttrs.get(OnboardingConstant.SO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (dataSecAttrs.get(OnboardingConstant.PO) != null
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purcList = dataSecAttrs.get(OnboardingConstant.PO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					if (dataSecAttrs.get(OnboardingConstant.DC) != null
							&& dataSecAttrs.get(OnboardingConstant.DC)
									.size() > 0) {
						distList = dataSecAttrs.get(OnboardingConstant.DC);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD1) != null
							&& dataSecAttrs.get(OnboardingConstant.UD1)
									.size() > 0) {
						ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD2) != null
							&& dataSecAttrs.get(OnboardingConstant.UD2)
									.size() > 0) {
						ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD3) != null
							&& dataSecAttrs.get(OnboardingConstant.UD3)
									.size() > 0) {
						ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD4) != null
							&& dataSecAttrs.get(OnboardingConstant.UD4)
									.size() > 0) {
						ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD5) != null
							&& dataSecAttrs.get(OnboardingConstant.UD5)
									.size() > 0) {
						ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD6) != null
							&& dataSecAttrs.get(OnboardingConstant.UD6)
									.size() > 0) {
						ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
					}
				}
*/			}
		}

		StringBuilder build = new StringBuilder();
		StringBuilder build1 = new StringBuilder();
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");
				build1.append(" AND GSTIN IN :gstinList");
			}
		}
	/*	if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND ITM.PROFIT_CENTRE IN :pcList");
				build1.append(" AND PROFIT_CENTER IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND ITM.PLANT_CODE IN :plantList");
				build1.append(" AND PLANT IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND HDR.SALES_ORGANIZATION IN :salesList");
				build1.append(" AND SALES_ORG IN :salesList");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND HDR.PURCHASE_ORGANIZATION IN :purcList");
				build1.append(" AND PURCHASE_ORGANIZATION IN :purcList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");
				build1.append(" AND DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND HDR.DIVISION IN :divisionList");
				build1.append(" AND DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND ITM.LOCATION IN :locationList");
				build1.append(" AND LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND HDR.USERACCESS1 IN :ud1List");
				build1.append(" AND USER_ACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND HDR.USERACCESS2 IN :ud2List");
				build1.append(" AND USER_ACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND HDR.USERACCESS3 IN :ud3List");
				build1.append(" AND USER_ACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND HDR.USERACCESS4 IN :ud4List");
				build1.append(" AND USER_ACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND HDR.USERACCESS5 IN :ud5List");
				build1.append(" AND USER_ACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND HDR.USERACCESS6 IN :ud6List");
				build1.append(" AND USER_ACCESS6 IN :ud6List");
			}
		}
*/		if (taxPeriod != 0) {

			build.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod AND "
					+ "ITM.DERIVED_RET_PERIOD = :taxPeriod ");
			build1.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString();
		String buildQuery1 = build1.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createQueryString(buildQuery,buildQuery1);
		
		LOGGER.debug("Outward Query BEGIN----> 3A To 3G "+ queryStr);
		
		try {
			Query q = entityManager.createNativeQuery(queryStr);

		/*	if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
					q.setParameter("pcList", pcList);
				}
			}
			if (plant != null && !plant.isEmpty()) {
				if (plantList != null && !plantList.isEmpty()
						&& plantList.size() > 0) {
					q.setParameter("plantList", plantList);
				}
			}
			if (sales != null && !sales.isEmpty()) {
				if (salesList != null && !salesList.isEmpty()
						&& salesList.size() > 0) {
					q.setParameter("salesList", salesList);
				}
			}
*/			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
		/*	if (division != null && !division.isEmpty()) {
				if (divisionList != null && !divisionList.isEmpty()
						&& divisionList.size() > 0) {
					q.setParameter("divisionList", divisionList);
				}
			}
			if (location != null && !location.isEmpty()) {
				if (locationList != null && !locationList.isEmpty()
						&& locationList.size() > 0) {
					q.setParameter("locationList", locationList);
				}
			}
			if (purchase != null && !purchase.isEmpty()) {
				if (purcList != null && !purcList.isEmpty()
						&& purcList.size() > 0) {
					q.setParameter("purcList", purcList);
				}
			}
			if (distChannel != null && !distChannel.isEmpty()) {
				if (distList != null && !distList.isEmpty()
						&& distList.size() > 0) {
					q.setParameter("distList", distList);
				}
			}
			if (ud1 != null && !ud1.isEmpty()) {
				if (ud1List != null && !ud1List.isEmpty()
						&& ud1List.size() > 0) {
					q.setParameter("ud1List", ud1List);
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && !ud2List.isEmpty()
						&& ud2List.size() > 0) {
					q.setParameter("ud2List", ud2List);
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && !ud3List.isEmpty()
						&& ud3List.size() > 0) {
					q.setParameter("ud3List", ud3List);
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && !ud4List.isEmpty()
						&& ud4List.size() > 0) {
					q.setParameter("ud4List", ud4List);
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && !ud5List.isEmpty()
						&& ud5List.size() > 0) {
					q.setParameter("ud5List", ud5List);
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && !ud6List.isEmpty()
						&& ud6List.size() > 0) {
					q.setParameter("ud6List", ud6List);
				}
			}
*/			if (taxPeriod != 0) {
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

	private String createQueryString(String buildQuery,String buildQuery1) {
		LOGGER.debug("Outward 3A TO 3G Query Execution BEGIN ");
				
		String queryString = "SELECT TABLE,TYPE_OF_SUPPLY,"
				+ "SUM(ASP_INTEREST_IGST_AMT) AS ASP_INTEREST_IGST_AMT, "
				+ "SUM(ASP_INTEREST_CGST_AMT) AS ASP_INTEREST_CGST_AMT, "
				+ "SUM(ASP_INTEREST_SGST_AMT) AS ASP_INTEREST_SGST_AMT, "
				+ "SUM(ASP_INTEREST_CESS_AMT) AS ASP_INTEREST_CESS_AMT, "
				+ "SUM(ASP_LATEFEE_CGST_AMT) AS ASP_LATEFEE_CGST_AMT, "
				+ "SUM(ASP_LATEFEE_SGST_AMT) AS ASP_LATEFEE_SGST_AMT, "
				+ "SUM(GSTN_INTEREST_IGST_AMT) AS GSTN_INTEREST_IGST_AMT, "
				+ "SUM(GSTN_INTEREST_CGST_AMT) AS GSTN_INTEREST_CGST_AMT, "
				+ "SUM(GSTN_INTEREST_SGST_AMT) AS GSTN_INTEREST_SGST_AMT, "
				+ "SUM(GSTN_INTEREST_CESS_AMT) AS GSTN_INTEREST_CESS_AMT, "
				+ "SUM(GSTN_LATEFEE_CGST_AMT) AS GSTN_LATEFEE_CGST_AMT, "
				+ "SUM(GSTN_LATEFEE_SGST_AMT) AS GSTN_LATEFEE_SGST_AMT "
				+ "FROM (SELECT RETURN_TABLE AS TABLE,"
				+ "(case WHEN RETURN_TABLE='5(1)' THEN "
				+ "'a_interestNFeeLateFiling' WHEN RETURN_TABLE='5(2)' "
				+ "THEN 'a_interestItcReversal'WHEN RETURN_TABLE='5(3)' "
				+ "THEN 'a_lateReportingRCM' WHEN RETURN_TABLE='5(4)' "
				+ "THEN 'a_otherInterest' END ) AS "
				+ "TYPE_OF_SUPPLY,SUM(INTEREST_IGST_AMT) AS "
				+ "ASP_INTEREST_IGST_AMT,SUM(INTEREST_CGST_AMT) AS "
				+ "ASP_INTEREST_CGST_AMT,SUM(INTEREST_SGST_AMT) AS "
				+ "ASP_INTEREST_SGST_AMT,SUM(INTEREST_CESS_AMT) AS "
				+ "ASP_INTEREST_CESS_AMT,SUM(LATEFEE_CGST_AMT) AS "
				+ "ASP_LATEFEE_CGST_AMT, SUM(LATEFEE_SGST_AMT) AS "
				+ "ASP_LATEFEE_SGST_AMT,0 AS GSTN_INTEREST_IGST_AMT,"
				+ "0 AS GSTN_INTEREST_CGST_AMT,"
				+ "0 AS GSTN_INTEREST_SGST_AMT, "
				+ "0 AS GSTN_INTEREST_CESS_AMT,"
				+ "0 AS GSTN_LATEFEE_CGST_AMT, "
				+ "0 AS GSTN_LATEFEE_SGST_AMT "
				+ "FROM RET_PROCESSED_INTEREST_LATEFEE "
				+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1A' "
				+ "AND RETURN_TABLE IN ('5(1)','5(2)','5(3)','5(4)')"
				+ buildQuery1
				+ "GROUP BY RETURN_TABLE,RETURN_TABLE,RETURN_PERIOD,"
				+ "GSTIN,SR_NO,RETURN_TYPE "
				+ "UNION ALL "
				+ "SELECT GET_TABLE_SECTION AS TABLE,(case WHEN "
				+ "GET_TABLE_SECTION='5(1)' THEN 'a_interestNFeeLateFiling'"
				+ " WHEN GET_TABLE_SECTION='5(2)' THEN 'a_interestItcReversal'"
				+ " WHEN GET_TABLE_SECTION='5(3)' THEN 'a_lateReportingRCM' "
				+ " WHEN GET_TABLE_SECTION='5(4)' THEN 'a_otherInterest'"
				+ " END ) AS TYPE_OF_SUPPLY, 0 AS ASP_INTEREST_IGST_AMT,"
				+ "0 AS ASP_INTEREST_CGST_AMT,0 AS ASP_INTEREST_SGST_AMT, "
				+ "0 AS ASP_INTEREST_CESS_AMT,0 AS ASP_LATEFEE_CGST_AMT, "
				+ "0 AS ASP_LATEFEE_SGST_AMT,SUM(INTEREST_IGST_AMT) AS "
				+ "GSTN_INTEREST_IGST_AMT, SUM(INTEREST_CGST_AMT) AS "
				+ "GSTN_INTEREST_CGST_AMT,SUM(INTEREST_SGST_AMT) AS "
				+ "GSTN_INTEREST_SGST_AMT, SUM(INTEREST_CESS_AMT) AS "
				+ "GSTN_INTEREST_CESS_AMT,SUM(LATEFEE_CGST_AMT) AS "
				+ "GSTN_LATEFEE_CGST_AMT, SUM(LATEFEE_SGST_AMT) AS GSTN_"
				+ "LATEFEE_SGST_AMT FROM GETRET1_INTEREST_LATEFEE WHERE "
				+ "GET_RETURN_TYPE='RET-1A' AND IS_DELETE = FALSE  AND "
				+ "GET_TABLE_SECTION IN ('5(1)','5(2)','5(3)','5(4)')"
				+ buildQuery1
				+ "GROUP BY GET_TABLE_SECTION UNION ALL "
				+ "SELECT '5' AS TABLE,'a_interestNLateFee' AS TYPE_OF_SUPPLY"
				+ ",SUM(INTEREST_IGST_AMT) AS ASP_INTEREST_IGST_AMT,"
				+ "SUM(INTEREST_CGST_AMT) AS ASP_INTEREST_CGST_AMT,"
				+ "SUM(INTEREST_SGST_AMT) AS ASP_INTEREST_SGST_AMT,"
				+ "SUM(INTEREST_CESS_AMT) AS ASP_INTEREST_CESS_AMT,"
				+ "SUM(LATEFEE_CGST_AMT) AS ASP_LATEFEE_CGST_AMT,"
				+ "SUM(LATEFEE_SGST_AMT) AS ASP_LATEFEE_SGST_AMT,"
				+ "0 AS GSTN_INTEREST_IGST_AMT,0 AS GSTN_INTEREST_CGST_AMT,"
				+ "0 AS GSTN_INTEREST_SGST_AMT,0 AS GSTN_INTEREST_CESS_AMT,"
				+ "0 AS GSTN_LATEFEE_CGST_AMT, 0 AS GSTN_LATEFEE_SGST_AMT "
				+ "FROM RET_PROCESSED_INTEREST_LATEFEE WHERE IS_DELETE = FALSE "
				+ "AND RETURN_TYPE='RET-1A' AND RETURN_TABLE IN ('5(1)','5(2)',"
				+ "'5(3)','5(4)')"
				+ buildQuery1
				+ "GROUP BY RETURN_TABLE UNION ALL "
				+ "SELECT '5' AS TABLE,'a_interestNLateFee' AS TYPE_OF_SUPPLY,"
				+ "0 AS ASP_INTEREST_IGST_AMT,0 AS ASP_INTEREST_CGST_AMT,"
				+ "0 AS ASP_INTEREST_SGST_AMT, 0 AS ASP_INTEREST_CESS_AMT,"
				+ "0 AS ASP_LATEFEE_CGST_AMT, 0 AS ASP_LATEFEE_SGST_AMT,"
				+ "SUM(INTEREST_IGST_AMT) AS GSTN_INTEREST_IGST_AMT,"
				+ "SUM(INTEREST_CGST_AMT) AS GSTN_INTEREST_CGST_AMT,"
				+ "SUM(INTEREST_SGST_AMT) AS GSTN_INTEREST_SGST_AMT, "
				+ "SUM(INTEREST_CESS_AMT) AS GSTN_INTEREST_CESS_AMT,"
				+ "SUM(LATEFEE_CGST_AMT) AS GSTN_LATEFEE_CGST_AMT, "
				+ "SUM(LATEFEE_SGST_AMT) AS GSTN_LATEFEE_SGST_AMT "
				+ "FROM GETRET1_INTEREST_LATEFEE WHERE GET_RETURN_TYPE='RET-1A' AND "
				+ "IS_DELETE = FALSE  AND GET_TABLE_SECTION IN ('5(1)','5(2)',"
				+ "'5(3)','5(4)')  "
				+ buildQuery1
				+ "GROUP BY GET_TABLE_SECTION "
				+ ") GROUP BY TABLE,TYPE_OF_SUPPLY" ;
				
		LOGGER.debug("REVIEW SUMMARY FOR RET1A Interest and Late fee");
		return queryString;

	}

	
	

}
