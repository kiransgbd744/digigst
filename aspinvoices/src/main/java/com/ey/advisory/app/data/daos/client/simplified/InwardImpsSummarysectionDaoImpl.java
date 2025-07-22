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

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("InwardImpsSummarysectionDaoImpl")
public class InwardImpsSummarysectionDaoImpl
		implements BasicDocInwardSummarySectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InwardImpsSummarysectionDaoImpl.class);

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;
	
	@Override
	public List<Annexure1SummarySectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto requestDto) {

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setInwardSumDataSecuritySearchParams(requestDto);

			
	String taxPeriodReq = req.getTaxPeriod();
		
		int taxPeriod = GenUtil
				.convertTaxPeriodToInt(taxPeriodReq);
		
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
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND CUST_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND PROFIT_CENTER IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND PLANT IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND SALES_ORGANIZATION IN :salesList");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND PURCHAGE_ORG IN :purcList");
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
				build.append(" AND USER_ACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND USER_ACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND USER_ACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND USER_ACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND USER_ACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND USER_ACCESS6 IN :ud6List");
			}
		}
		if (taxPeriod != 0) {
			
			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}
		
		String buildQuery = build.toString();
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Building Where condition For IMPS Query " +buildQuery);
		}
		
		String queryStr = createQueryString(buildQuery);
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Vertical Query For Summary Level  For Imps " + queryStr);
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
				if (ud1List != null && !ud1List.isEmpty() && ud1List.size() > 0) {
					q.setParameter("ud1List", ud1List);
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && !ud2List.isEmpty() && ud2List.size() > 0) {
					q.setParameter("ud2List", ud2List);
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && !ud3List.isEmpty() && ud3List.size() > 0) {
					q.setParameter("ud3List", ud3List);
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && !ud4List.isEmpty() && ud4List.size() > 0) {
					q.setParameter("ud4List", ud4List);
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && !ud5List.isEmpty() && ud5List.size() > 0) {
					q.setParameter("ud5List", ud5List);
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && !ud6List.isEmpty() && ud6List.size() > 0) {
					q.setParameter("ud6List", ud6List);
				}
			}
			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}
		
	
		List<Object[]> list = q.getResultList();
		LOGGER.debug("Converting ResultSet To List ");
		List<Annexure1SummarySectionDto> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		
		return retList;
	}catch (Exception e) {
		e.printStackTrace();
		throw new AppException("Unexpected error in query execution.",e);
	}
	}

	private String createQueryString(String buildQuery) {

		LOGGER.debug("Executing Query For IMPS  Vertical Uploads BEGIN ");
		/*String queryStr = "SELECT 'SLF' AS DOC_TYPE, '3I' AS TABLE_SECTION,"
				+ "SUM(IFNULL(TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) "
				+ "AS INVOICE_VALUE,"
				+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TAX_PAYABLE,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST,"
				+ "IFNULL(SUM(SGST_AMT),0) AS SGST,"
				+ "IFNULL(SUM(CESS_AMT),0) AS CESS,"
				+ "COUNT(INVKEY_GSTN_3H_3I) AS RECORDS_COUNT FROM ANX_PROCESSED_3H_3I WHERE "
				+ "TRAN_FLAG='IMPS' AND IS_DELETE = FALSE AND RETURN_TYPE='ANX-1' "
				+ buildQuery;*/
		
		String queryStr = "SELECT DOC_TYPE,TABLE_SECTION,"
				+ "SUM(INVOICE_VALUE) INVOICE_VALUE,"
				+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
				+ "SUM(TAX_PAYABLE) TAX_PAYABLE,"
				+ "SUM(IGST) IGST,SUM(CGST) CGST,SUM(SGST) SGST,"
				+ "SUM(CESS) CESS,COUNT(*) RECORD_COUNT FROM ( "
				+ "SELECT 'SLF' AS DOC_TYPE,"
				+ "(CASE WHEN TRAN_FLAG='IMPS' THEN '3I' END)  AS TABLE_SECTION,"
				+ "DIFF_PERCENT,SEC7_OF_IGST_FLAG,AUTOPOPULATE_TO_REFUND,"
				+ "POS,TAX_RATE,"
				+ "SUM(IFNULL(TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) "
				+ "AS INVOICE_VALUE,"
				+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TAX_PAYABLE,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST,"
				+ "IFNULL(SUM(SGST_AMT),0) AS SGST,"
				+ "IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM ANX_PROCESSED_3H_3I WHERE "
				+ "TRAN_FLAG='IMPS' AND IS_DELETE = FALSE "
				+ "AND RETURN_TYPE='ANX-1' "
				+ buildQuery
				+ "GROUP BY  DIFF_PERCENT,TRAN_FLAG,SEC7_OF_IGST_FLAG,"
				+ "AUTOPOPULATE_TO_REFUND ,POS,TAX_RATE ) "
				+ "GROUP BY  DOC_TYPE,TABLE_SECTION ";
		LOGGER.debug("Executing Query For IMPS END ");
		return queryStr;
	}

	private Annexure1SummarySectionDto convert(Object[] arr) {

		LOGGER.debug("Converting ResultSet to Dto");
		Annexure1SummarySectionDto obj = new Annexure1SummarySectionDto();
		obj.setDocType((String) arr[0]);
		obj.setTableSection((String) arr[1]);
		obj.setInvValue((BigDecimal) arr[2]);
		obj.setTaxableValue((BigDecimal) arr[3]);
		obj.setTaxPayble((BigDecimal) arr[4]);
		obj.setIgst((BigDecimal) arr[5]);
		obj.setCgst((BigDecimal) arr[6]);
		obj.setSgst((BigDecimal) arr[7]);
		obj.setCess((BigDecimal) arr[8]);
		obj.setRecords((GenUtil.getBigInteger(arr[9])).intValue());
		return obj;
	}
}
