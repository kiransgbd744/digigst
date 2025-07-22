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

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionEcomDto;
import com.ey.advisory.app.services.search.simplified.docsummary.BasicDocEcomSummarySectionDao;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author BalaKrishna S
 *
 */
@Component("Anx1EcomSummarySectionDaoImpl")
public class Anx1EcomSummarySectionDaoImpl
		implements BasicDocEcomSummarySectionDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1EcomSummarySectionDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Override
	public List<Annexure1SummarySectionEcomDto> loadBasicSummarySection(
			Annexure1SummaryReqDto requestDto) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(requestDto);

		String taxPeriodReq = req.getTaxPeriod();
		int taxPeriod = 0;
		if (taxPeriodReq != null) {
			taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		}
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
		StringBuilder buildb2c = new StringBuilder();
		StringBuilder buildEtin = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList");
				buildb2c.append(" AND SUPPLIER_GSTIN IN :gstinList");
				buildEtin.append(" AND ETIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND PROFIT_CENTRE IN :pcList");
				buildb2c.append(" AND PROFIT_CENTER IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND PLANT_CODE IN :plantList");
				buildb2c.append(" AND PLANT IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND SALES_ORGANIZATION IN :salesList");
				buildb2c.append(" AND SALES_ORG IN :salesList");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND PURCHASE_ORGANIZATION IN :purcList");
				buildb2c.append(" AND PURCHASE_ORGANIZATION IN :purcList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND DISTRIBUTION_CHANNEL IN :distList");
				buildb2c.append(" AND DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND DIVISION IN :divisionList");
				buildb2c.append(" AND DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND LOCATION IN :locationList");
				buildb2c.append(" AND LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND USERACCESS1 IN :ud1List");
				buildb2c.append(" AND USER_ACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND USERACCESS2 IN :ud2List");
				buildb2c.append(" AND USER_ACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND USERACCESS3 IN :ud3List");
				buildb2c.append(" AND USER_ACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND USERACCESS4 IN :ud4List");
				buildb2c.append(" AND USER_ACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND USERACCESS5 IN :ud5List");
				buildb2c.append(" AND USER_ACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND USERACCESS6 IN :ud6List");
				buildb2c.append(" AND USER_ACCESS6 IN :ud6List");
			}
		}
		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			buildb2c.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			buildEtin.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");

		}

		String buildQuery = build.toString();
		String buildb2cQuery = buildb2c.toString();
		String buildEtinQuery = buildEtin.toString();

		String queryStr = createQueryString(buildQuery, buildb2cQuery,
				buildEtinQuery);
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
			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}

			List<Object[]> list = q.getResultList();

			List<Annexure1SummarySectionEcomDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(
					"Converting ResultSet to List in Ecomm Query Execution ");
			throw new AppException("Unexpected error in query execution.");
		}
	}

	/**
	 * @param o
	 * @return
	 */
	private Annexure1SummarySectionEcomDto convert(Object[] arr) {
		Annexure1SummarySectionEcomDto obj = new Annexure1SummarySectionEcomDto();

		LOGGER.debug(" Setting the values from List to Dto.. ");
		// obj.setRecords((GenUtil.getBigInteger(arr[0])).intValue());
		obj.setTableSection((String) arr[0]);
		obj.setReturnPeriod((String) arr[1]);
		obj.setSupplyMade((BigDecimal) arr[2]);
		obj.setSupplyReturn((BigDecimal) arr[3]);
		obj.setNetSupply((BigDecimal) arr[4]);
		obj.setIgst((BigDecimal) arr[5]);
		obj.setCgst((BigDecimal) arr[6]);
		obj.setSgst((BigDecimal) arr[7]);
		obj.setCess((BigDecimal) arr[8]);
		obj.setTaxPayble((BigDecimal) arr[9]);
		obj.setMemoIgst((BigDecimal) arr[10]);
		obj.setMemoCgst((BigDecimal) arr[11]);
		obj.setMemoSgst((BigDecimal) arr[12]);
		obj.setMemoTaxPayable((BigDecimal) arr[13]);
		obj.setGstnSupplyMade((BigDecimal) arr[14]);
		obj.setGstnSupplyReturn((BigDecimal) arr[15]);
		obj.setGstnNetSupply((BigDecimal) arr[16]);
		obj.setGstnIgst((BigDecimal) arr[17]);
		obj.setGstnCgst((BigDecimal) arr[18]);
		obj.setGstnSgst((BigDecimal) arr[19]);
		obj.setGstnCess((BigDecimal) arr[20]);
		obj.setGstnTaxPayble((BigDecimal) arr[21]);

		return obj;
	}

	private String createQueryString(String buildQuery, String buildb2cQuery,
			String buildEtinQuery) {

		LOGGER.debug("Executing Ecom Query BEGIN");
		/*
		 * String queryStr ="SELECT TABLE_SECTION," +
		 * "SUM(ASP_SUPPLIES_MADE) AS ASP_SUPPLIES_MADE," +
		 * "SUM(ASP_SUPPLIES_RETURN) AS ASP_SUPPLIES_RETURN," +
		 * "SUM(ASP_NET_SUPPLIES) AS ASP_NET_SUPPLIES," +
		 * "SUM(ASP_IGST_AMT) ASP_IGST_AMT,SUM(ASP_CGST_AMT) ASP_CGST_AMT," +
		 * "SUM(ASP_SGST_AMT) ASP_SGST_AMT,SUM(ASP_CESS_AMT) ASP_CESS_AMT," +
		 * "SUM(ASP_TAX_PAYABLE) ASP_TAX_PAYABLE," +
		 * "SUM(ASP_MEMOIGST) ASP_MEMOIGST,SUM(ASP_MEMOCGST) ASP_MEMOCGST," +
		 * "SUM(ASP_MEMOSGST) ASP_MEMOSGST,SUM(ASP_MEMOTAX_PAYABLE) " +
		 * "ASP_MEMOTAX_PAYABLE,SUM(GSTN_SUPPLIES_MADE) GSTN_SUPPLIES_MADE," +
		 * "SUM(GSTN_SUPPLIES_RETURN) GSTN_SUPPLIES_RETURN," +
		 * "SUM(GSTN_NET_SUPPLIES) GSTN_NET_SUPPLIES," +
		 * "SUM(GSTN_IGST_AMT) GSTN_IGST_AMT,SUM(GSTN_CGST_AMT) GSTN_CGST_AMT,"
		 * +
		 * "SUM(GSTN_SGST_AMT) GSTN_SGST_AMT,SUM(GSTN_CESS_AMT) GSTN_CESS_AMT,"
		 * + "SUM(GSTN_TAX_PAYABLE) GSTN_TAX_PAYABLE FROM ( " +
		 * "SELECT TABLE_SECTION,ASP_SUPPLIES_MADE,ASP_SUPPLIES_RETURN," +
		 * "ASP_NET_SUPPLIES,ASP_IGST_AMT,ASP_CGST_AMT,ASP_SGST_AMT," +
		 * "ASP_CESS_AMT, ASP_TAX_PAYABLE,ASP_MEMOIGST,ASP_MEMOCGST ," +
		 * "ASP_MEMOSGST ,ASP_MEMOTAX_PAYABLE,0 GSTN_SUPPLIES_MADE," +
		 * "0 GSTN_SUPPLIES_RETURN,0 GSTN_NET_SUPPLIES,0 GSTN_IGST_AMT," +
		 * "0 GSTN_CGST_AMT,0 GSTN_SGST_AMT,0 GSTN_CESS_AMT," +
		 * "0 GSTN_TAX_PAYABLE FROM ( (SELECT '4' AS TABLE_SECTION," +
		 * "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN TAXABLE_VALUE END),0) +  "
		 * + "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN TAXABLE_VALUE END),0) " +
		 * "AS ASP_SUPPLIES_MADE,IFNULL(SUM(CASE WHEN DOC_TYPE='CR' " +
		 * "THEN TAXABLE_VALUE END),0)  AS ASP_SUPPLIES_RETURN," +
		 * "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN TAXABLE_VALUE END),0) +  "
		 * + "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN TAXABLE_VALUE END),0)- " +
		 * "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN TAXABLE_VALUE END),0) " +
		 * "AS ASP_NET_SUPPLIES,IFNULL(SUM(CASE WHEN DOC_TYPE='INV' " +
		 * "THEN IGST_AMT END),0) +  IFNULL(SUM(CASE WHEN DOC_TYPE='DR' " +
		 * "THEN IGST_AMT END),0)- IFNULL(SUM(CASE WHEN DOC_TYPE='CR' " +
		 * "THEN IGST_AMT END),0) AS ASP_IGST_AMT," +
		 * "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN CGST_AMT END),0) + " +
		 * " IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN CGST_AMT END),0)- " +
		 * "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN CGST_AMT END),0) " +
		 * "AS ASP_CGST_AMT,IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN SGST_AMT END),0) +  "
		 * + "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN SGST_AMT END),0)- " +
		 * "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN SGST_AMT END),0) " +
		 * "AS ASP_SGST_AMT,IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN " +
		 * "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)+" +
		 * "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN IFNULL(CESS_AMT_SPECIFIC,0)+"
		 * +
		 * "IFNULL(CESS_AMT_ADVALOREM,0) END),0)-IFNULL(SUM(CASE WHEN DOC_TYPE='CR' "
		 * +
		 * "THEN IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0) "
		 * + "AS ASP_CESS_AMT,IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN " +
		 * "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+" +
		 * "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)+" +
		 * "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN IFNULL(IGST_AMT,0)+" +
		 * "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
		 * +
		 * "IFNULL(CESS_AMT_ADVALOREM,0) END),0)-IFNULL(SUM(CASE WHEN DOC_TYPE='CR' "
		 * + "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+" +
		 * "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0) " +
		 * "AS ASP_TAX_PAYABLE,IFNULL(SUM(CASE WHEN DOC_TYPE='INV' " +
		 * "THEN MEMO_VALUE_IGST END),0) +  IFNULL(SUM(CASE WHEN DOC_TYPE='DR' "
		 * + "THEN MEMO_VALUE_IGST END),0)- IFNULL(SUM(CASE WHEN DOC_TYPE='CR' "
		 * + "THEN MEMO_VALUE_IGST END),0) AS ASP_MEMOIGST," +
		 * "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN MEMO_VALUE_CGST END),0) +  "
		 * + "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN MEMO_VALUE_CGST END),0)- "
		 * + "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_CGST END),0) "
		 * + "AS ASP_MEMOCGST,IFNULL(SUM(CASE WHEN DOC_TYPE='INV' " +
		 * "THEN MEMO_VALUE_SGST END),0) +  IFNULL(SUM(CASE WHEN DOC_TYPE='DR' "
		 * + "THEN MEMO_VALUE_SGST END),0)- IFNULL(SUM(CASE WHEN DOC_TYPE='CR' "
		 * + "THEN MEMO_VALUE_SGST END),0) AS ASP_MEMOSGST," +
		 * "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN IFNULL(MEMO_VALUE_IGST,0)+"
		 * + "IFNULL(MEMO_VALUE_CGST,0)+IFNULL(MEMO_VALUE_SGST,0)+" +
		 * "IFNULL(MEMO_VALUE_CESS,0) END),0)+IFNULL(SUM(CASE WHEN DOC_TYPE='DR' "
		 * + "THEN IFNULL(MEMO_VALUE_IGST,0)+IFNULL(MEMO_VALUE_CGST,0)+" +
		 * "IFNULL(MEMO_VALUE_SGST,0)+IFNULL(MEMO_VALUE_CESS,0)  END),0)-" +
		 * "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN IFNULL(MEMO_VALUE_IGST,0)+"
		 * + "IFNULL(MEMO_VALUE_CGST,0)+IFNULL(MEMO_VALUE_SGST,0)+" +
		 * "IFNULL(MEMO_VALUE_CESS,0)  END),0) AS ASP_MEMOTAX_PAYABLE " +
		 * "FROM ANX_OUTWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE " +
		 * "AND IS_DELETE = FALSE AND AN_TABLE_SECTION " +
		 * "IN ('3A','3B','3C','3D','3E','3F','3G') AND TCS_FLAG = 'Y'" +
		 * buildQuery + ") UNION (SELECT '4' AS TABLE_SECTION," +
		 * "IFNULL(SUM(ECOM_VAL_SUPMADE),0) AS ASP_SUPPLIES_MADE," +
		 * "IFNULL(SUM(ECOM_VAL_SUPRET),0) AS ASP_SUPPLIES_RETURN," +
		 * "IFNULL(SUM(ECOM_NETVAL_SUP),0) AS ASP_NET_SUPPLIES," +
		 * "IFNULL(SUM(IGST_AMT),0) AS ASP_IGST_AMT," +
		 * "IFNULL(SUM(CGST_AMT),0) AS ASP_CGST_AMT," +
		 * "IFNULL(SUM(SGST_AMT),0) AS ASP_SGST_AMT," +
		 * "IFNULL(SUM(CESS_AMT),0) AS ASP_CESS_AMT," +
		 * "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+" +
		 * "IFNULL(CESS_AMT,0)) AS ASP_TAX_PAYABLE,'0' AS ASP_MEMOIGST," +
		 * "'0' AS ASP_MEMOCGST,'0' AS ASP_MEMOSGST," +
		 * "'0' AS ASP_MEMOTAX_PAYABLE " +
		 * "FROM ANX_PROCESSED_TABLE4 WHERE IS_DELETE = FALSE " + buildb2cQuery
		 * + ") UNION (SELECT '4' AS TABLE_SECTION," +
		 * "IFNULL(SUM(ECOM_VAL_SUPMADE),0) AS ASP_SUPPLIES_MADE," +
		 * "IFNULL(SUM(ECOM_VAL_SUPRET),0) AS ASP_SUPPLIES_RETURN," +
		 * "IFNULL(SUM(ECOM_NETVAL_SUP),0) AS ASP_NET_SUPPLIES," +
		 * "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+" +
		 * "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS ASP_TAX_PAYABLE," +
		 * "IFNULL(SUM(IGST_AMT),0) AS ASP_IGST_AMT," +
		 * "IFNULL(SUM(CGST_AMT),0) AS ASP_CGST_AMT," +
		 * "IFNULL(SUM(SGST_AMT),0) AS ASP_SGST_AMT," +
		 * "IFNULL(SUM(CESS_AMT),0) AS ASP_CESS_AMT," +
		 * "'0' AS ASP_MEMOTAX_PAYABLE,'0' AS ASP_MEMOIGST," +
		 * "'0' AS ASP_MEMOCGST,'0' AS ASP_MEMOSGST " +
		 * "FROM ANX_PROCESSED_B2C " + "WHERE IS_DELETE = FALSE" + buildb2cQuery
		 * + ")) UNION ALL SELECT TABLE_SECTION,0 ASP_SUPPLIES_MADE," +
		 * "0 ASP_SUPPLIES_RETURN,0 ASP_NET_SUPPLIES,0 ASP_IGST_AMT," +
		 * "0 ASP_CGST_AMT,0 ASP_SGST_AMT,0 ASP_CESS_AMT," +
		 * "0 ASP_TAX_PAYABLE,0 ASP_MEMOIGST ,0 ASP_MEMOCGST ," +
		 * "0 ASP_MEMOSGST ,0 ASP_MEMOTAX_PAYABLE,GSTN_SUPPLIES_MADE," +
		 * "GSTN_SUPPLIES_RETURN,GSTN_NET_SUPPLIES,GSTN_IGST_AMT," +
		 * "GSTN_CGST_AMT,GSTN_SGST_AMT,GSTN_CESS_AMT,GSTN_TAX_PAYABLE " +
		 * "FROM ( SELECT '4' AS TABLE_SECTION," +
		 * "IFNULL(SUM(SUPP_MADE_VALUE),0) AS GSTN_SUPPLIES_MADE," +
		 * "IFNULL(SUM(SUPP_RET_VALUE),0) AS GSTN_SUPPLIES_RETURN," +
		 * "IFNULL(SUM(SUPP_NET_VALUE),0) AS GSTN_NET_SUPPLIES," +
		 * "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+" +
		 * "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS GSTN_TAX_PAYABLE," +
		 * "IFNULL(SUM(IGST_AMT),0) AS GSTN_IGST_AMT," +
		 * "IFNULL(SUM(CGST_AMT),0) AS GSTN_CGST_AMT," +
		 * "IFNULL(SUM(SGST_AMT),0) AS GSTN_SGST_AMT," +
		 * "IFNULL(SUM(CESS_AMT),0) AS GSTN_CESS_AMT " +
		 * "FROM GETANX1_ECOM_DETAILS WHERE IS_DELETE = FALSE " + buildEtinQuery
		 * + ")) GROUP BY  TABLE_SECTION";
		 */

		String queryStr = "SELECT TABLE_SECTION,RETURN_PERIOD,"
				+ "SUM(ASP_SUPPLIES_MADE) AS ASP_SUPPLIES_MADE,"
				+ "SUM(ASP_SUPPLIES_RETURN) AS ASP_SUPPLIES_RETURN,"
				+ "SUM(ASP_NET_SUPPLIES) AS ASP_NET_SUPPLIES,"
				+ "SUM(ASP_IGST_AMT) ASP_IGST_AMT,SUM(ASP_CGST_AMT) ASP_CGST_AMT,"
				+ "SUM(ASP_SGST_AMT) ASP_SGST_AMT,SUM(ASP_CESS_AMT) ASP_CESS_AMT,"
				+ "SUM(ASP_TAX_PAYABLE) ASP_TAX_PAYABLE,"
				+ "SUM(ASP_MEMOIGST) ASP_MEMOIGST,SUM(ASP_MEMOCGST) ASP_MEMOCGST,"
				+ "SUM(ASP_MEMOSGST) ASP_MEMOSGST,"
				+ "SUM(ASP_MEMOTAX_PAYABLE) ASP_MEMOTAX_PAYABLE,	"
				+ "SUM(GSTN_SUPPLIES_MADE) GSTN_SUPPLIES_MADE,"
				+ "SUM(GSTN_SUPPLIES_RETURN) GSTN_SUPPLIES_RETURN,"
				+ "SUM(GSTN_NET_SUPPLIES) GSTN_NET_SUPPLIES,"
				+ "SUM(GSTN_IGST_AMT) GSTN_IGST_AMT,SUM(GSTN_CGST_AMT) GSTN_CGST_AMT,"
				+ "SUM(GSTN_SGST_AMT) GSTN_SGST_AMT,SUM(GSTN_CESS_AMT) GSTN_CESS_AMT,"
				+ "SUM(GSTN_TAX_PAYABLE) GSTN_TAX_PAYABLE " + "FROM ( "
				+ "SELECT TABLE_SECTION, RETURN_PERIOD,"
				+ "ASP_SUPPLIES_MADE,ASP_SUPPLIES_RETURN,"
				+ "ASP_NET_SUPPLIES,ASP_IGST_AMT,ASP_CGST_AMT,ASP_SGST_AMT,"
				+ "ASP_CESS_AMT, ASP_TAX_PAYABLE,ASP_MEMOIGST,ASP_MEMOCGST ,"
				+ "ASP_MEMOSGST ,ASP_MEMOTAX_PAYABLE,0 GSTN_SUPPLIES_MADE,"
				+ "0 GSTN_SUPPLIES_RETURN,0 GSTN_NET_SUPPLIES,0 GSTN_IGST_AMT,"
				+ "0 GSTN_CGST_AMT,0 GSTN_SGST_AMT,0 GSTN_CESS_AMT,"
				+ "0 GSTN_TAX_PAYABLE " + "FROM ((	"
				+ "SELECT '4' AS TABLE_SECTION,  RETURN_PERIOD,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN TAXABLE_VALUE END),0)+	"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN TAXABLE_VALUE END),0) "
				+ "AS ASP_SUPPLIES_MADE,IFNULL(SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN TAXABLE_VALUE END),0)  AS ASP_SUPPLIES_RETURN,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN TAXABLE_VALUE END),0)+	"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN TAXABLE_VALUE END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN TAXABLE_VALUE END),0) "
				+ "AS ASP_NET_SUPPLIES,IFNULL(SUM(CASE WHEN DOC_TYPE='INV' "
				+ "THEN IGST_AMT END),0) +  IFNULL(SUM(CASE WHEN DOC_TYPE='DR' "
				+ "THEN IGST_AMT END),0)- IFNULL(SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN IGST_AMT END),0) AS ASP_IGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN CGST_AMT END),0)  +	"
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN CGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN CGST_AMT END),0) "
				+ "AS ASP_CGST_AMT,IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN SGST_AMT END),0)+	"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN SGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN SGST_AMT END),0) "
				+ "AS ASP_SGST_AMT,IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN "
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)+	"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN IFNULL(CESS_AMT_SPECIFIC,0)+	"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0)-IFNULL(SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0) "
				+ "AS ASP_CESS_AMT,IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN "
				+ "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+	"
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)+ "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN IFNULL(IGST_AMT,0)+	"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+	"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0)-IFNULL(SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+	"
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0) "
				+ "AS ASP_TAX_PAYABLE,IFNULL(SUM(CASE WHEN DOC_TYPE='INV' "
				+ "THEN MEMO_VALUE_IGST END),0) +  IFNULL(SUM(CASE WHEN DOC_TYPE='DR' "
				+ "THEN MEMO_VALUE_IGST END),0)- IFNULL(SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN MEMO_VALUE_IGST END),0) AS ASP_MEMOIGST,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN MEMO_VALUE_CGST END),0)+	"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN MEMO_VALUE_CGST END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_CGST END),0) "
				+ "AS ASP_MEMOCGST,IFNULL(SUM(CASE WHEN DOC_TYPE='INV' "
				+ "THEN MEMO_VALUE_SGST END),0) +  IFNULL(SUM(CASE WHEN DOC_TYPE='DR' "
				+ "THEN MEMO_VALUE_SGST END),0)- IFNULL(SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN MEMO_VALUE_SGST END),0) AS ASP_MEMOSGST,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN IFNULL(MEMO_VALUE_IGST,0)+	"
				+ "IFNULL(MEMO_VALUE_CGST,0)+IFNULL(MEMO_VALUE_SGST,0)+	"
				+ "IFNULL(MEMO_VALUE_CESS,0) END),0)+IFNULL(SUM(CASE WHEN DOC_TYPE='DR' "
				+ "THEN IFNULL(MEMO_VALUE_IGST,0)+IFNULL(MEMO_VALUE_CGST,0)+	"
				+ "IFNULL(MEMO_VALUE_SGST,0)+IFNULL(MEMO_VALUE_CESS,0)  END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN IFNULL(MEMO_VALUE_IGST,0)+	"
				+ "IFNULL(MEMO_VALUE_CGST,0)+IFNULL(MEMO_VALUE_SGST,0)+	"
				+ "IFNULL(MEMO_VALUE_CESS,0)  END),0) AS ASP_MEMOTAX_PAYABLE "
				+ "FROM ANX_OUTWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE "
				+ "AND IS_DELETE = FALSE AND AN_TABLE_SECTION "
				+ "IN ('3A','3B','3C','3D','3E','3F','3G') AND TCS_FLAG = 'Y' "
				+ "AND AN_RETURN_TYPE='ANX1' "
				+ buildQuery + " GROUP BY" + " RETURN_PERIOD )	"
				+ "UNION ALL	"
				+ "(SELECT '4' AS TABLE_SECTION,  RETURN_PERIOD,"
				+ "IFNULL(SUM(ECOM_VAL_SUPMADE),0) AS ASP_SUPPLIES_MADE,"
				+ "IFNULL(SUM(ECOM_VAL_SUPRET),0) AS ASP_SUPPLIES_RETURN,"
				+ "IFNULL(SUM(ECOM_NETVAL_SUP),0) AS ASP_NET_SUPPLIES,"
				+ "IFNULL(SUM(IGST_AMT),0) AS ASP_IGST_AMT,"
				+ "IFNULL(SUM(CGST_AMT),0) AS ASP_CGST_AMT,"
				+ "IFNULL(SUM(SGST_AMT),0) AS ASP_SGST_AMT,"
				+ "IFNULL(SUM(CESS_AMT),0) AS ASP_CESS_AMT,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+	"
				+ "IFNULL(CESS_AMT,0)) AS ASP_TAX_PAYABLE,'0' AS ASP_MEMOIGST,"
				+ "'0' AS ASP_MEMOCGST,'0' AS ASP_MEMOSGST,"
				+ "'0' AS ASP_MEMOTAX_PAYABLE "
				+ "FROM ANX_PROCESSED_TABLE4 WHERE IS_DELETE = FALSE "
				+ "AND RETURN_TYPE='ANX-1' "
				+ buildb2cQuery + " GROUP BY" + " RETURN_PERIOD )"
				+ "UNION ALL (SELECT '4' AS TABLE_SECTION,  RETURN_PERIOD,"
				+ "IFNULL(SUM(ECOM_VAL_SUPMADE),0) AS ASP_SUPPLIES_MADE,"
				+ "IFNULL(SUM(ECOM_VAL_SUPRET),0) AS ASP_SUPPLIES_RETURN,"
				+ "IFNULL(SUM(ECOM_NETVAL_SUP),0) AS ASP_NET_SUPPLIES,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+	"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS ASP_TAX_PAYABLE,"
				+ "IFNULL(SUM(IGST_AMT),0) AS ASP_IGST_AMT,"
				+ "IFNULL(SUM(CGST_AMT),0) AS ASP_CGST_AMT,"
				+ "IFNULL(SUM(SGST_AMT),0) AS ASP_SGST_AMT,"
				+ "IFNULL(SUM(CESS_AMT),0) AS ASP_CESS_AMT,"
				+ "'0' AS ASP_MEMOTAX_PAYABLE,'0' AS ASP_MEMOIGST,"
				+ "'0' AS ASP_MEMOCGST,'0' AS ASP_MEMOSGST "
				+ "FROM ANX_PROCESSED_B2C " + "WHERE IS_DELETE = FALSE "
				+ "AND RETURN_TYPE='ANX-1' "
				+ buildb2cQuery + " GROUP BY" + " RETURN_PERIOD))	"
				+ "UNION ALL " + "SELECT TABLE_SECTION, RETURN_PERIOD,"
				+ "0 ASP_SUPPLIES_MADE,"
				+ "0 ASP_SUPPLIES_RETURN,0 ASP_NET_SUPPLIES,0 ASP_IGST_AMT,"
				+ "0 ASP_CGST_AMT,0 ASP_SGST_AMT,0 ASP_CESS_AMT,"
				+ "0 ASP_TAX_PAYABLE,0 ASP_MEMOIGST ,0 ASP_MEMOCGST ,"
				+ "0 ASP_MEMOSGST ,0 ASP_MEMOTAX_PAYABLE,GSTN_SUPPLIES_MADE,"
				+ "GSTN_SUPPLIES_RETURN,GSTN_NET_SUPPLIES,GSTN_IGST_AMT,"
				+ "GSTN_CGST_AMT,GSTN_SGST_AMT,GSTN_CESS_AMT,GSTN_TAX_PAYABLE "
				+ "FROM ( SELECT '4' AS TABLE_SECTION,   TAX_PERIOD AS RETURN_PERIOD,	"
				+ "IFNULL(SUM(SUPP_MADE_VALUE),0) AS GSTN_SUPPLIES_MADE,"
				+ "IFNULL(SUM(SUPP_RET_VALUE),0) AS GSTN_SUPPLIES_RETURN,"
				+ "IFNULL(SUM(SUPP_NET_VALUE),0) AS GSTN_NET_SUPPLIES,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+	"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS GSTN_TAX_PAYABLE,"
				+ "IFNULL(SUM(IGST_AMT),0) AS GSTN_IGST_AMT,"
				+ "IFNULL(SUM(CGST_AMT),0) AS GSTN_CGST_AMT,"
				+ "IFNULL(SUM(SGST_AMT),0) AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(CESS_AMT),0) AS GSTN_CESS_AMT "
				+ "FROM GETANX1_ECOM_DETAILS WHERE IS_DELETE = FALSE "
				+ buildEtinQuery + " GROUP BY  TAX_PERIOD))	"
				+ " GROUP BY  TABLE_SECTION, RETURN_PERIOD";

		LOGGER.debug("Executing Ecom Query END");
		return queryStr;
	}

}
