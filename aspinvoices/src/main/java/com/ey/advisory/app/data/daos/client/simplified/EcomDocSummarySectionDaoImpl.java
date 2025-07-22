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
@Component("EcomDocSummarySectionDaoImpl")
public class EcomDocSummarySectionDaoImpl
		implements BasicDocEcomSummarySectionDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EcomDocSummarySectionDaoImpl.class);

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
		
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList");
				buildb2c.append(" AND SUPPLIER_GSTIN IN :gstinList");
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
		}

		String buildQuery = build.toString();
		String buildb2cQuery = buildb2c.toString();

		String queryStr = createQueryString(buildQuery,buildb2cQuery);
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
		obj.setRecords((GenUtil.getBigInteger(arr[0])).intValue());
		obj.setTableSection((String) arr[1]);
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

		return obj;
	}

	private String createQueryString(String buildQuery,String buildb2cQuery) {

		LOGGER.debug("Executing Ecom Query BEGIN");
		/*String queryStr = "SELECT SUM(RECORD_COUNT) AS RECORD_COUNT,"
				+ "TABLE_SECTION," + "SUM(SUPPLIES_MADE) AS SUPPLIES_MADE,"
				+ "SUM(SUPPLIES_RETURN) AS SUPPLIES_RETURN,"
				+ "SUM(NET_SUPPLIES) AS NET_SUPPLIES,"
				+ "SUM(IGST_AMT) AS IGST,SUM(CGST_AMT) AS CGST,"
				+ "SUM(SGST_AMT) AS SGST,SUM(CESS_AMT) AS CESS,"
				+ "SUM(TAX_PAYABLE) AS TAX_PAYABLE,SUM(MEMOIGST) AS MIGST,"
				+ "SUM(MEMOCGST) AS MCGST,SUM(MEMOSGST) AS MSGST,"
				+ "SUM(MEMOTAX_PAYABLE) AS MTAX_PAYABLE FROM ("
				+ "(SELECT COUNT(ECOM_GSTIN) AS RECORD_COUNT, "
				+ "'4' AS TABLE_SECTION,SUM(CASE WHEN DOC_TYPE ='CR' "
				+ "THEN 0 ELSE TAXABLE_VALUE END) AS SUPPLIES_MADE,"
				+ "SUM(CASE WHEN DOC_TYPE ='CR' THEN TAXABLE_VALUE ELSE 0 END)"
				+ " AS SUPPLIES_RETURN,SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN TAXABLE_VALUE*-1 ELSE TAXABLE_VALUE END) AS NET_SUPPLIES,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN IGST_AMT*-1 ELSE "
				+ "IGST_AMT END) AS IGST_AMT,SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN CGST_AMT*-1 ELSE CGST_AMT END) AS CGST_AMT,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN SGST_AMT*-1 "
				+ "ELSE SGST_AMT END) AS SGST_AMT,(SUM(CASE WHEN "
				+ "DOC_TYPE='CR' THEN CESS_AMT_SPECIFIC*-1 ELSE "
				+ "CESS_AMT_SPECIFIC END)+SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN CESS_AMT_ADVALOREM*-1 ELSE CESS_AMT_ADVALOREM END)) "
				+ "AS CESS_AMT,(SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "IGST_AMT*-1 ELSE IGST_AMT END)+SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN CGST_AMT*-1 ELSE CGST_AMT END)+SUM(CASE WHEN "
				+ "DOC_TYPE='CR' THEN SGST_AMT*-1 ELSE SGST_AMT END)+"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN CESS_AMT_SPECIFIC*-1 ELSE "
				+ "CESS_AMT_SPECIFIC END)+SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "CESS_AMT_ADVALOREM*-1 ELSE CESS_AMT_ADVALOREM END)) "
				+ "AS TAX_PAYABLE,SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "MEMO_VALUE_IGST*-1 ELSE MEMO_VALUE_IGST END ) AS MEMOIGST,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_CGST*-1 "
				+ "ELSE MEMO_VALUE_CGST END ) AS MEMOCGST,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_SGST*-1 "
				+ "ELSE MEMO_VALUE_SGST END ) AS MEMOSGST,(SUM(CASE WHEN "
				+ "DOC_TYPE='CR' THEN MEMO_VALUE_IGST*-1 ELSE "
				+ "MEMO_VALUE_IGST END)+SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN MEMO_VALUE_CGST*-1 ELSE MEMO_VALUE_CGST END)+"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_SGST*-1 ELSE "
				+ "MEMO_VALUE_SGST END)) AS MEMOTAX_PAYABLE FROM "
				+ "ANX_OUTWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE AND "
				+ "IS_DELETE = FALSE AND AN_TABLE_SECTION IN "
				+ "('3A','3B','3C','3D','3E','3F','3G') AND "
				+ "TCS_FLAG = 'Y' " + buildQuery + ") " + "UNION "
				+ "(SELECT COUNT(ECOM_GSTIN) AS RECORD_COUNT,'4' "
				+ "AS TABLE_SECTION,SUM(ECOM_VAL_SUPMADE) AS SUPPLIES_MADE,"
				+ "SUM(ECOM_VAL_SUPRET) AS SUPPLIES_RETURN,"
				+ "SUM(ECOM_NETVAL_SUP) AS NET_SUPPLIES,SUM(IGST_AMT) "
				+ "AS IGST_AMT,SUM(CGST_AMT) AS CGST_AMT,"
				+ "SUM(SGST_AMT) AS SGST_AMT,SUM(CESS_AMT) AS CESS_AMT,"
				+ "SUM(IGST_AMT+CGST_AMT+SGST_AMT+CESS_AMT) AS TAX_PAYABLE,"
				+ "'0' AS MEMOIGST,'0' AS MEMOCGST,'0' AS MEMOSGST,"
				+ "'0' AS MEMOTAX_PAYABLE FROM ANX_PROCESSED_TABLE4 "
				+ "WHERE IS_DELETE = FALSE " + buildb2cQuery + ") " + "UNION "
				+ "(SELECT COUNT(ECOM_GSTIN) AS RECORD_COUNT,"
				+ "'4' AS TABLE_SECTION,SUM(ECOM_VAL_SUPMADE) "
				+ "AS SUPPLIES_MADE,SUM(ECOM_VAL_SUPRET) AS SUPPLIES_RETURN,"
				+ "SUM(ECOM_NETVAL_SUP) AS NET_SUPPLIES,"
				+ "SUM(IGST_AMT+CGST_AMT+SGST_AMT+CESS_AMT) AS TAX_PAYABLE,"
				+ "SUM(IGST_AMT) AS IGST_AMT,SUM(CGST_AMT) AS CGST_AMT,"
				+ "SUM(SGST_AMT) AS SGST_AMT, SUM(CESS_AMT) AS CESS_AMT,"
				+ "'0' AS MEMOTAX_PAYABLE,'0' AS MEMOIGST,'0' AS MEMOCGST,'0' "
				+ "AS MEMOSGST FROM ANX_PROCESSED_B2C WHERE "
				+ "IS_DELETE = FALSE " + buildb2cQuery + ")) X "
				+ "GROUP BY TABLE_SECTION";
*/
		String queryStr ="SELECT SUM(RECORD_COUNT) AS RECORD_COUNT,"
				+ "TABLE_SECTION,SUM(SUPPLIES_MADE) AS SUPPLIES_MADE,"
				+ "SUM(SUPPLIES_RETURN) AS SUPPLIES_RETURN,"
				+ "SUM(NET_SUPPLIES) AS NET_SUPPLIES,SUM(IGST_AMT) AS IGST,"
				+ "SUM(CGST_AMT) AS CGST,SUM(SGST_AMT) AS SGST,"
				+ "SUM(CESS_AMT) AS CESS,SUM(TAX_PAYABLE) AS TAX_PAYABLE,"
				+ "SUM(MEMOIGST) AS MIGST,SUM(MEMOCGST) AS MCGST,"
				+ "SUM(MEMOSGST) AS MSGST,"
				+ "SUM(MEMOTAX_PAYABLE) AS MTAX_PAYABLE FROM "
				+ "( SELECT COUNT(ECOM_GSTIN) AS RECORD_COUNT,"
				+ "'4' AS TABLE_SECTION,"
				+ "SUM(CASE WHEN DOC_TYPE ='CR' THEN 0 "
				+ "ELSE TAXABLE_VALUE END) AS SUPPLIES_MADE,"
				+ "SUM(CASE WHEN DOC_TYPE ='CR' THEN "
				+ "TAXABLE_VALUE ELSE 0 END) AS SUPPLIES_RETURN,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "TAXABLE_VALUE*-1 "
				+ "ELSE TAXABLE_VALUE END) AS NET_SUPPLIES,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "IGST_AMT*-1 ELSE IGST_AMT END) AS IGST_AMT,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "CGST_AMT*-1 ELSE CGST_AMT END) AS CGST_AMT,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "SGST_AMT*-1 ELSE SGST_AMT END) AS SGST_AMT,"
				+ "(SUM(CASE WHEN DOC_TYPE='CR' THEN CESS_AMT_SPECIFIC*-1 "
				+ "ELSE CESS_AMT_SPECIFIC END)+SUM(CASE WHEN "
				+ "DOC_TYPE='CR' THEN CESS_AMT_ADVALOREM*-1 ELSE "
				+ "CESS_AMT_ADVALOREM END)) AS CESS_AMT,"
				+ "(SUM(CASE WHEN DOC_TYPE='CR' THEN IGST_AMT*-1 "
				+ "ELSE IGST_AMT END)+ SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN CGST_AMT*-1 ELSE CGST_AMT END)+"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "SGST_AMT*-1 ELSE SGST_AMT END)+ "
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "CESS_AMT_SPECIFIC*-1 ELSE CESS_AMT_SPECIFIC END)+"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "CESS_AMT_ADVALOREM*-1 ELSE "
				+ "CESS_AMT_ADVALOREM END)) AS TAX_PAYABLE,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_IGST*-1 "
				+ "ELSE MEMO_VALUE_IGST END) AS MEMOIGST,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_CGST*-1 "
				+ "ELSE MEMO_VALUE_CGST END ) AS MEMOCGST,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_SGST*-1 "
				+ "ELSE MEMO_VALUE_SGST END ) AS MEMOSGST,(SUM(CASE WHEN "
				+ "DOC_TYPE='CR' THEN MEMO_VALUE_IGST*-1 ELSE "
				+ "MEMO_VALUE_IGST END)+SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN MEMO_VALUE_CGST*-1 ELSE MEMO_VALUE_CGST END)+ "
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_SGST*-1 "
				+ "ELSE MEMO_VALUE_SGST END)) AS MEMOTAX_PAYABLE "
				+ "FROM ANX_OUTWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE "
				+ "AND IS_DELETE = FALSE "
				+ "AND AN_TABLE_SECTION IN ('3A','3B','3C','3D','3E','3F','3G') "
				+ "AND AN_RETURN_TYPE='ANX1' "
				+ "AND TCS_FLAG = 'Y'"
				+ buildQuery 
				+ "UNION "
				+ "SELECT COUNT(ECOM_GSTIN) AS RECORD_COUNT,"
				+ "'4' AS TABLE_SECTION,"
				+ "SUM(ECOM_VAL_SUPMADE) AS SUPPLIES_MADE,"
				+ "SUM(ECOM_VAL_SUPRET) AS SUPPLIES_RETURN,"
				+ "SUM(ECOM_NETVAL_SUP) AS NET_SUPPLIES,"
				+ "SUM(IGST_AMT) AS IGST_AMT,SUM(CGST_AMT) AS CGST_AMT,"
				+ "SUM(SGST_AMT) AS SGST_AMT,SUM(CESS_AMT) AS CESS_AMT,"
				+ "SUM(IGST_AMT+CGST_AMT+SGST_AMT+CESS_AMT) AS TAX_PAYABLE,"
				+ "'0' AS MEMOIGST,'0' AS MEMOCGST,'0' AS MEMOSGST,"
				+ "'0' AS MEMOTAX_PAYABLE FROM ANX_PROCESSED_TABLE4 "
				+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='ANX-1' "
				+ buildb2cQuery 
				+ "UNION "
				+ "SELECT COUNT(ECOM_GSTIN) AS RECORD_COUNT,"
				+ "'4' AS TABLE_SECTION,SUM(ECOM_VAL_SUPMADE) "
				+ "AS SUPPLIES_MADE,SUM(ECOM_VAL_SUPRET) AS SUPPLIES_RETURN,"
				+ "SUM(ECOM_NETVAL_SUP) AS NET_SUPPLIES,"
				+ "SUM(IGST_AMT+CGST_AMT+SGST_AMT+CESS_AMT) AS TAX_PAYABLE,"
				+ "SUM(IGST_AMT) AS IGST_AMT,SUM(CGST_AMT) AS CGST_AMT,"
				+ "SUM(SGST_AMT) AS SGST_AMT, SUM(CESS_AMT) AS CESS_AMT, "
				+ "'0' AS MEMOTAX_PAYABLE,'0' AS MEMOIGST,'0' AS MEMOCGST,'0' "
				+ "AS MEMOSGST FROM ANX_PROCESSED_B2C WHERE  IS_DELETE = FALSE "
				+  "AND RETURN_TYPE='ANX-1' "
				+ buildb2cQuery 
				+ ") "
				+ "GROUP BY TABLE_SECTION ";
		LOGGER.debug("Executing Ecom Query END");
		return queryStr;
	}

}
