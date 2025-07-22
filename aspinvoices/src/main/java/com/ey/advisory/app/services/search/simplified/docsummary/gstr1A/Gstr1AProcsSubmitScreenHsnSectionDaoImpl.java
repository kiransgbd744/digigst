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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author Shashikant.Shukla
 *
 */
@Service("Gstr1AProcsSubmitScreenHsnSectionDaoImpl")
public class Gstr1AProcsSubmitScreenHsnSectionDaoImpl
		implements BasicGstr1ADocSummaryScreenHsnSectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AProcsSubmitScreenHsnSectionDaoImpl.class);

	@Override
	public List<Gstr1SummaryCDSectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto request) {

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		String taxPeriodFrom1 = req.getTaxPeriodFrom();
		String taxPeriodTo1 = req.getTaxPeriodTo();

		int taxPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom1);
		int taxPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo1);

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
		StringBuilder build = new StringBuilder();
		StringBuilder build2 = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList");
				build2.append(" AND SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND PLANT_CODE IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND SALES_ORGANIZATION IN :salesList");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND PURCHASE_ORGANIZATION IN :purcList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND USERACCESS6 IN :ud6List");
			}
		}
		if (StringUtils.isNotEmpty(taxPeriodFrom1)
				&& StringUtils.isNotEmpty(taxPeriodTo1)) {

			build.append(
					" AND DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");

			build2.append(
					" AND DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");

		}

		String buildQuery = build.toString().substring(4);
		String buildQuery2 = build2.toString().substring(4);
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createQueryString(buildQuery, buildQuery2);
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
			if (StringUtils.isNotEmpty(taxPeriodFrom1)
					&& StringUtils.isNotEmpty(taxPeriodTo1)) {
				q.setParameter("taxPeriodFrom", taxPeriodFrom);
				q.setParameter("taxPeriodTo", taxPeriodTo);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Gstr1SummaryCDSectionDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("After Execution getting the data ----->" + retList);
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception while Fetching Review Summary For HSN ", e);
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr1SummaryCDSectionDto convert(Object[] arr) {
		Gstr1SummaryCDSectionDto obj = new Gstr1SummaryCDSectionDto();
		LOGGER.debug("Array data Setting to Dto");

		obj.setTaxDocType((String) arr[0]);
		/*
		 * obj.setRecords( arr[1] != null ?
		 * Integer.parseInt(String.valueOf(arr[1])) : 0);
		 */

		obj.setAspCount((GenUtil.getBigInteger(arr[1])).intValue());
		obj.setAspTaxableValue((BigDecimal) arr[2]);
		obj.setAspTaxPayble((BigDecimal) arr[3]);
		obj.setAspInvoiceValue((BigDecimal) arr[4]);
		obj.setAspIgst((BigDecimal) arr[5]);
		obj.setAspCgst((BigDecimal) arr[6]);
		obj.setAspSgst((BigDecimal) arr[7]);
		obj.setAspCess((BigDecimal) arr[8]);
		obj.setGstnCount((GenUtil.getBigInteger(arr[9])).intValue());
		obj.setGstnTaxableValue((BigDecimal) arr[10]);
		obj.setGstnTaxPayble((BigDecimal) arr[11]);
		obj.setGstnInvoiceValue((BigDecimal) arr[12]);
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
	private String createQueryString(String buildQuery, String buildQuery2) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT TAX_DOC_TYPE,SUM(ASP_COUNT)ASP_COUNT,"
				+ "SUM( ASP_TAXABLE_VALUE) AS ASP_TAXABLE_VALUE,"
				+ "SUM(ASP_TOTAL_TAX) AS ASP_TOTAL_TAX,"
				+ "SUM( ASP_TOTAL_VALUE)AS ASP_TOTAL_VALUE,"
				+ "SUM(ASP_IGST_AMT)AS ASP_IGST_AMT,SUM( ASP_CGST_AMT)AS ASP_CGST_AMT,"
				+ "SUM( ASP_SGST_AMT)AS ASP_SGST_AMT,SUM( ASP_CESS_AMT)AS ASP_CESS_AMT,"
				+ "SUM(GSTN_COUNT)GSTN_COUNT,SUM(GSTN_TAXABLE_VALUE) AS GSTN_TAXABLE_VALUE,"
				+ "SUM(GSTN_TOTAL_TAX) AS GSTN_TOTAL_TAX,"
				+ "SUM( GSTN_TOTAL_VALUE)AS GSTN_TOTAL_VALUE,"
				+ "SUM(GSTN_IGST_AMT) AS GSTN_IGST_AMT,"
				+ "SUM(GSTN_CGST_AMT) AS GSTN_CGST_AMT,"
				+ "SUM( GSTN_SGST_AMT)AS GSTN_SGST_AMT,"
				+ "SUM( GSTN_CESS_AMT)AS GSTN_CESS_AMT FROM "
				+ "( SELECT TAX_DOC_TYPE, count(distinct ASP_COUNT) ASP_COUNT,"
				+ "SUM( ASP_TAXABLE_VALUE) AS ASP_TAXABLE_VALUE,"
				+ "SUM(ASP_TOTAL_TAX) AS ASP_TOTAL_TAX,"
				+ "SUM( ASP_TOTAL_VALUE)AS ASP_TOTAL_VALUE,"
				+ "SUM(ASP_IGST_AMT)AS ASP_IGST_AMT,SUM( ASP_CGST_AMT)AS ASP_CGST_AMT,"
				+ "SUM( ASP_SGST_AMT)AS ASP_SGST_AMT,SUM( ASP_CESS_AMT)AS ASP_CESS_AMT,"
				+ "count(distinct GSTN_COUNT)GSTN_COUNT,"
				+ "SUM(GSTN_TAXABLE_VALUE) AS GSTN_TAXABLE_VALUE,"
				+ "SUM(GSTN_TOTAL_TAX) AS GSTN_TOTAL_TAX,"
				+ "SUM( GSTN_TOTAL_VALUE)AS GSTN_TOTAL_VALUE,"
				+ "SUM(GSTN_IGST_AMT) AS GSTN_IGST_AMT,"
				+ "SUM(GSTN_CGST_AMT) AS GSTN_CGST_AMT,"
				+ "SUM( GSTN_SGST_AMT)AS GSTN_SGST_AMT,"
				+ "SUM( GSTN_CESS_AMT)AS GSTN_CESS_AMT FROM ( "
				+ "SELECT 'HSN' AS TAX_DOC_TYPE,"
				+ "((HSNSAC || '-' || (CASE WHEN LEFT(HSNSAC, 2) = "
				+ "'99' then 'NA' else ITM_UQC end))) as ASP_COUNT,"
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(TAXABLE_VALUE,0) END),0)-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TAXABLE_VALUE,0) END),0)) AS ASP_TAXABLE_VALUE, "
				+ " (IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(TOTAL_TAX,0) END),0)-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TOTAL_TAX,0) END),0)) AS ASP_TOTAL_TAX, "
				+ " (IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(TOTAL_VALUE,0) END),0)-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TOTAL_VALUE,0) END),0)) AS ASP_TOTAL_VALUE, "
				+ " (IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(IGST_AMT,0) END),0)-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(IGST_AMT,0) END),0)) AS ASP_IGST_AMT, "
				+ " (IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(CGST_AMT,0) END),0)-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(CGST_AMT,0) END),0)) AS ASP_CGST_AMT, "
				+ " (IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(SGST_AMT,0) END),0)-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(SGST_AMT,0) END),0)) AS ASP_SGST_AMT, "
				+ " (IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(CESS_AMT,0) END),0)-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(CESS_AMT,0) END),0)) AS ASP_CESS_AMT,"
				+ "null AS GSTN_COUNT,0 AS GSTN_TAXABLE_VALUE,"
				+ "0 AS GSTN_TOTAL_TAX,0 AS GSTN_TOTAL_VALUE,"
				+ "0 AS GSTN_IGST_AMT,0 AS GSTN_CGST_AMT,"
				+ "0 AS GSTN_SGST_AMT,0 AS GSTN_CESS_AMT,tax_rate "
				+ "FROM GSTR1A_SUBMITTED_PS_TRANS "
				+ "WHERE RECORD_TYPE ='ASP' AND TAX_DOC_TYPE ='HSN_DIGI' "
				+ "AND TABLE_NAME IN ('ANX_OUTWARD_DOC_HEADER_1A','GSTR1A_PROCESSED_B2CS') AND "
				+ buildQuery + "group by TAX_RATE,"
				+ "(HSNSAC || '-' || (CASE WHEN LEFT(HSNSAC, 2) = "
				+ "'99' then 'NA' else ITM_UQC end)) " + "UNION ALL "
				+ "SELECT 'HSN' AS TAX_DOC_TYPE,"
				+ "((HSNSAC || '-' || (CASE WHEN LEFT(HSNSAC, 2) = "
				+ "'99' then 'NA' else ITM_UQC end))) as ASP_COUNT, "
				+ " (IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(TAXABLE_VALUE,0) END),0)- "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TAXABLE_VALUE,0) END),0)) AS ASP_TAXABLE_VALUE, "
				+ " (IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(TOTAL_TAX,0) END),0)-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TOTAL_TAX,0) END),0)) AS ASP_TOTAL_TAX, "
				+ " (IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(TOTAL_VALUE,0) END),0)-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TOTAL_VALUE,0) END),0)) AS ASP_TOTAL_VALUE, "
				+ " (IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(IGST_AMT,0) END),0)-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(IGST_AMT,0) END),0)) AS ASP_IGST_AMT, "
				+ " (IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(CGST_AMT,0) END),0)-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(CGST_AMT,0) END),0)) AS ASP_CGST_AMT, "
				+ " (IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(SGST_AMT,0) END),0)-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(SGST_AMT,0) END),0)) AS ASP_SGST_AMT, "
				+ " (IFNULL(SUM(CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(CESS_AMT,0) END),0)-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(CESS_AMT,0) END),0)) AS ASP_CESS_AMT, "
				+ "null AS GSTN_COUNT,0 AS GSTN_TAXABLE_VALUE,"
				+ "0 AS GSTN_TOTAL_TAX,0 AS GSTN_TOTAL_VALUE,"
				+ "0 AS GSTN_IGST_AMT,0 AS GSTN_CGST_AMT,"
				+ "0 AS GSTN_SGST_AMT,0 AS GSTN_CESS_AMT,TAX_RATE "
				+ "FROM GSTR1A_SUBMITTED_PS_TRANS "
				+ "WHERE RECORD_TYPE ='ASP' AND TAX_DOC_TYPE ='HSN_DIGI' "
				+ "AND TABLE_NAME IN ('GSTR1A_SUMMARY_NILEXTNON') AND "
				+ buildQuery2 + "group by tax_rate,"
				+ "(HSNSAC || '-' || (CASE WHEN LEFT(HSNSAC, 2) = "
				+ "'99' then 'NA' else ITM_UQC end)) " + "UNION ALL "
				+ "SELECT 'HSN' AS TAX_DOC_TYPE,null as ASP_COUNT,"
				+ "0 AS ASP_TAXABLE_VALUE,0 AS ASP_TOTAL_TAX,"
				+ "0 AS ASP_TOTAL_VALUE,0 AS ASP_IGST_AMT,"
				+ "0 AS ASP_CGST_AMT,0 AS ASP_SGST_AMT," + "0 AS ASP_CESS_AMT,"
				+ "( (HSNSAC || '-' || (CASE WHEN LEFT(HSNSAC, 2) = "
				+ "'99' then 'NA' else ITM_UQC end))) AS GSTN_COUNT,"
				+ "SUM(TAXABLE_VALUE) AS GSTN_TAXABLE_VALUE,"
				+ "SUM(TOTAL_TAX) AS GSTN_TOTAL_TAX,"
				+ "SUM(TOTAL_VALUE) AS GSTN_TOTAL_VALUE,"
				+ "SUM(IGST_AMT) AS GSTN_IGST_AMT,"
				+ "SUM(CGST_AMT) AS GSTN_CGST_AMT,"
				+ "SUM(SGST_AMT) AS GSTN_SGST_AMT,"
				+ "SUM(CESS_AMT) AS GSTN_CESS_AMT,"
				+ "tax_rate FROM GSTR1A_SUBMITTED_PS_TRANS "
				+ "WHERE RECORD_TYPE ='GSTN' "
				+ "AND TAX_DOC_TYPE ='HSN_GSTN' AND " + buildQuery2
				+ "group by tax_rate,"
				+ "(HSNSAC || '-' || (CASE WHEN LEFT(HSNSAC, 2) = "
				+ "'99' then 'NA' else ITM_UQC end))) "
				+ "group by TAX_DOC_TYPE,tax_rate ) group by TAX_DOC_TYPE ";

		return queryStr;
	}

}
