/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Component("Gstr1AProcessSubmitSummaryScreenSectionDaoImpl")
public class Gstr1AProcessSubmitSummaryScreenSectionDaoImpl
		implements BasicGstr1ADocSummaryScreenSectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Override
	public List<Gstr1SummarySectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		// String taxPeriodReq = req.getTaxPeriod();
		String taxPeriodFrom = req.getTaxPeriodFrom();
		String taxPeriodTo = req.getTaxPeriodTo();

		int taxPeriodFrom1 = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
		int taxPeriodTo1 = GenUtil.convertTaxPeriodToInt(taxPeriodTo);

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

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
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

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
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
			}
		}

		// List<String> gstinList

		/**
		 * Execution Start Prod For HSN
		 */

		/*
		 * LOGGER.debug("Executing HSN Proc {} "); String sgstin =
		 * gstinList.get(0); procService.getHsnProc(sgstin, taxPeriod);
		 */
		/**
		 * Execution End Prod For HSN
		 */

		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append("	AND SUPPLIER_GSTIN IN :gstinList ");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND PROFIT_CENTRE IN :pcList ");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND PLANT_CODE IN :plantList ");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND SALES_ORGANIZATION IN :salesList ");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND PURCHASE_ORGANIZATION IN :purcList ");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND DISTRIBUTION_CHANNEL IN :distList ");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND DIVISION IN :divisionList ");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND LOCATION IN :locationList ");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND USERACCESS1 IN :ud1List ");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND USERACCESS2 IN :ud2List ");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND USERACCESS3 IN :ud3List ");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND USERACCESS4 IN :ud4List ");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND USERACCESS5 IN :ud5List ");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND USERACCESS6 IN :ud6List ");
			}
		}
		if (taxPeriodFrom1 != 0 && taxPeriodTo1 != 0) {

			build.append(
					" AND DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom1 AND :taxPeriodTo1 ");
		}

		String buildQuery = build.toString().substring(4);
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
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
			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (division != null && !division.isEmpty()) {
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
			if (taxPeriodFrom1 != 0 && taxPeriodTo1 != 0) {
				q.setParameter("taxPeriodFrom1", taxPeriodFrom1);
				q.setParameter("taxPeriodTo1", taxPeriodTo1);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Gstr1SummarySectionDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("After Execution getting the data ----->" + retList);
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr1SummarySectionDto convert(Object[] arr) {
		Gstr1SummarySectionDto obj = new Gstr1SummarySectionDto();
		LOGGER.debug("Array data Setting to Dto");
		obj.setTaxDocType((String) arr[0]);
		obj.setAspCount((GenUtil.getBigInteger(arr[1])).intValue());
		obj.setAspInvoiceValue((BigDecimal) arr[2]);
		obj.setAspTaxableValue((BigDecimal) arr[3]);
		obj.setAspTaxPayble((BigDecimal) arr[4]);
		obj.setAspIgst((BigDecimal) arr[5]);
		obj.setAspCgst((BigDecimal) arr[6]);
		obj.setAspSgst((BigDecimal) arr[7]);
		obj.setAspCess((BigDecimal) arr[8]);
		obj.setGstnCount((GenUtil.getBigInteger(arr[9])).intValue());
		obj.setGstnInvoiceValue((BigDecimal) arr[10]);
		obj.setGstnTaxableValue((BigDecimal) arr[11]);
		obj.setGstnTaxPayble((BigDecimal) arr[12]);
		obj.setGstnIgst((BigDecimal) arr[13]);
		obj.setGstnCgst((BigDecimal) arr[14]);
		obj.setGstnSgst((BigDecimal) arr[15]);
		obj.setGstnCess((BigDecimal) arr[16]);

		return obj;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT (case when TAX_DOC_TYPE IN ('CDNUR','CDNUR-EXPORTS','CDNUR-B2CL') then 'CDNUR' else TAX_DOC_TYPE end)"
				+ " as TAX_DOC_TYPE,SUM(TOTAL_COUNT_IN_ASP)ASP_COUNT,"
				+ "SUM(ASP_TOTAL_VALUE)ASP_INVOICE_VALUE,"
				+ "SUM(ASP_TAXABLE_VALUE)ASP_TAXABLE_VALUE,"
				+ "SUM(ASP_TOTAL_TAX)ASP_TAX_PAYABLE,"
				+ "SUM(ASP_IGST_AMT)ASP_IGST_AMT,"
				+ "SUM(ASP_CGST_AMT)ASP_CGST_AMT,"
				+ "SUM(ASP_SGST_AMT)ASP_SGST_AMT,"
				+ "SUM(ASP_CESS_AMT)ASP_CESS_AMT,"
				+ "SUM(GSTN_COUNT)GSTN_COUNT,"
				+ "SUM(GSTN_TOTAL_VALUE)GSTN_INVOICE_VALUE,"
				+ "SUM(GSTN_TAXABLE_VALUE)GSTN_TAXABLE_VALUE,"
				+ "SUM(GSTN_TOTAL_TAX)GSTN_TAX_PAYABLE,"
				+ "SUM(GSTN_IGST_AMT)GSTN_IGST_AMT,SUM(GSTN_CGST_AMT)GSTN_CGST_AMT,"
				+ "SUM(GSTN_SGST_AMT)GSTN_SGST_AMT,"
				+ "SUM(GSTN_CESS_AMT)GSTN_CESS_AMT " + "FROM ( "
				+ "SELECT SUPPLIER_GSTIN,RETURN_PERIOD, 0 TOTAL_COUNT_IN_ASP,"
				+ "TAX_DOC_TYPE,ASP_TOTAL_VALUE,ASP_TAXABLE_VALUE,ASP_TOTAL_TAX,"
				+ "ASP_IGST_AMT,ASP_CGST_AMT,ASP_SGST_AMT,ASP_CESS_AMT,"
				+ "GSTN_COUNT,GSTN_TOTAL_VALUE,GSTN_TAXABLE_VALUE,GSTN_TOTAL_TAX,"
				+ "GSTN_IGST_AMT,GSTN_CGST_AMT,GSTN_SGST_AMT,GSTN_CESS_AMT "
				+ "FROM ( SELECT SUPPLIER_GSTIN,RETURN_PERIOD,TAX_DOC_TYPE,"
				+ "IFNULL(SUM(ASP_TOTAL_VALUE),0) AS ASP_TOTAL_VALUE,"
				+ "IFNULL(SUM(ASP_TAXABLE_VALUE),0) AS ASP_TAXABLE_VALUE,"
				+ "IFNULL(SUM(ASP_TOTAL_TAX),0) AS ASP_TOTAL_TAX,"
				+ "IFNULL(SUM(ASP_IGST_AMT),0) AS ASP_IGST_AMT,"
				+ "IFNULL(SUM(ASP_CGST_AMT),0) AS ASP_CGST_AMT,"
				+ "IFNULL(SUM(ASP_SGST_AMT),0) AS ASP_SGST_AMT,"
				+ "IFNULL(SUM(ASP_CESS_AMT),0) AS ASP_CESS_AMT,"
				+ "IFNULL(SUM(GSTN_COUNT),0) AS GSTN_COUNT,"
				+ "IFNULL(SUM(GSTN_TOTAL_VALUE),0) AS GSTN_TOTAL_VALUE,"
				+ "IFNULL(SUM(GSTN_TAXABLE_VALUE),0) AS GSTN_TAXABLE_VALUE,"
				+ "IFNULL(SUM(GSTN_TOTAL_TAX),0) AS GSTN_TOTAL_TAX,"
				+ "IFNULL(SUM(GSTN_IGST_AMT),0) AS GSTN_IGST_AMT,"
				+ "IFNULL(SUM(GSTN_CGST_AMT),0) AS GSTN_CGST_AMT,"
				+ "IFNULL(SUM(GSTN_SGST_AMT),0) AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(GSTN_CESS_AMT),0) AS GSTN_CESS_AMT "
				+ "FROM GSTR1A_SUBMITTED_PS "
				+ "WHERE IS_DELETE=FALSE AND TAX_DOC_TYPE NOT IN ('HSN','DOC_ISSUED','NILEXTNON') AND "
				+ buildQuery
				+ "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,TAX_DOC_TYPE,LAST_UPDATED) "
				+ "UNION ALL "
				+ "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,TOTAL_COUNT_IN_ASP,TAX_DOC_TYPE,"
				+ "0 ASP_TOTAL_VALUE,0 ASP_TAXABLE_VALUE,0 ASP_TOTAL_TAX,"
				+ "0 ASP_IGST_AMT,0 ASP_CGST_AMT,0 ASP_SGST_AMT,0 ASP_CESS_AMT,"
				+ "0 GSTN_COUNT,0 GSTN_TOTAL_VALUE,0 GSTN_TAXABLE_VALUE,0 GSTN_TOTAL_TAX,"
				+ "0 GSTN_IGST_AMT,0 GSTN_CGST_AMT,0 GSTN_SGST_AMT,0 GSTN_CESS_AMT "
				+ "FROM (SELECT SUPPLIER_GSTIN,RETURN_PERIOD,"
				+ "(CASE WHEN TAX_DOC_TYPE='B2CS' and SUM(ASP_COUNT) = 0 THEN "
				+ "IFNULL(SUM(TOTAL_COUNT_IN_ASP),0) "
				+ "WHEN TAX_DOC_TYPE='B2CS' and SUM(ASP_COUNT) <> 0 THEN IFNULL(SUM(ASP_COUNT),0) "
				+ "ELSE IFNULL(SUM(TOTAL_COUNT_IN_ASP),0) END) AS TOTAL_COUNT_IN_ASP, "
				+ "TAX_DOC_TYPE " + "FROM GSTR1A_SUBMITTED_PS "
				+ "WHERE IS_DELETE=FALSE AND TAX_DOC_TYPE NOT IN "
				+ "('HSN','DOC_ISSUED','NILEXTNON') AND " + buildQuery
				+ "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,TAX_DOC_TYPE,LAST_UPDATED)) "
				+ "GROUP BY (case when TAX_DOC_TYPE IN ('CDNUR','CDNUR-EXPORTS','CDNUR-B2CL') then 'CDNUR' else TAX_DOC_TYPE end)";
		LOGGER.debug("Outward FROM B2B TO EXPA Query Execution END ");
		return queryStr;
	}
}
