/**
 * 
 */
package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author BalaKrishna S
 *
 */
@Service("SezBasicDocSummaryScreenSectionDaoImpl")
public class SezBasicDocSummaryScreenSectionDaoImpl
		implements SezBasicGstr1DocSummaryScreenSectionDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SezBasicDocSummaryScreenSectionDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Override
	public List<Gstr1SummarySectionDto> loadBasicSummarySEZTSection(
			Annexure1SummaryReqDto request) {
		
		if (Strings.isNullOrEmpty(request.getReturnType())) {
			request.setReturnType(APIConstants.GSTR1.toUpperCase());
		}


		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		
		String taxPeriodReq = req.getTaxPeriod();
		boolean isGstr1a = req.getIsGstr1a();
		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		LocalDate docFromDate = req.getDocFromDate();
		LocalDate docToDate = req.getDocToDate();
		List<String> einvGenerated = req.getEINVGenerated();
		List<String> ewbGenerated = req.getEWBGenerated();

		String einvGen = null;
		if (einvGenerated != null && einvGenerated.size() > 0) {
			einvGen = einvGenerated.get(0);
		}
		String einvResp = null;

		if (einvGen != null) {
			if (einvGen.equalsIgnoreCase("NO")) {
				einvResp = "IS NULL";
			} else if (einvGen.equalsIgnoreCase("YES") && einvGen != null) {
				einvResp = "IS NOT NULL";
			}
		}

		String ewbGen = null;
		if (ewbGenerated != null && ewbGenerated.size() > 0) {
			ewbGen = ewbGenerated.get(0);
		}
		String ewbResp = null;
		if (ewbGen != null) {
			if (ewbGen.equalsIgnoreCase("NO")) {
				ewbResp = "IS NULL";
			} else if (ewbGen.equalsIgnoreCase("YES") && ewbGen != null) {
				ewbResp = "IS NOT NULL";
			}
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

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (docFromDate != null && docToDate != null) {
			build.append(" AND DOC_DATE BETWEEN :docFromDate AND :docToDate ");

		}
		if (ewbGen != null && ewbGen.equalsIgnoreCase("YES")) {
			build.append(" AND EWB_NO_RESP IS NOT NULL ");
		}
		if (ewbResp != null && ewbGen.equalsIgnoreCase("NO")) {
			build.append(" AND EWB_NO_RESP IS NULL ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("YES")) {
			build.append(" AND IRN_RESPONSE IS NOT NULL ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("NO")) {
			build.append(" AND IRN_RESPONSE IS NULL ");
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
		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}
		if (einvGenerated != null && einvGenerated.size() > 0) {
			build.append(" AND EWB_NO_RESP IS NULL ");

		}
		if (ewbGenerated != null && ewbGenerated.size() > 0) {
			build.append(" AND IRN_RESPONSE IS NULL ");
		}
		if (docFromDate != null && docToDate != null) {
			build.append("  AND DOC_DATE BETWEEN :docFromDate AND :docToDate ");
		}

		String buildQuery = build.toString();
		LOGGER.debug("Prepared where Condition and apply in SEZ Query BEGIN");

		String queryStr = null;
		if (Boolean.TRUE.equals(isGstr1a)) {
		    request.setReturnType(APIConstants.GSTR1A);
		}
		if (!Strings.isNullOrEmpty(request.getReturnType())
				&& APIConstants.GSTR1A
						.equalsIgnoreCase(request.getReturnType())) {
			queryStr = createQuerySeztStringForGstr1a(buildQuery);

		} else {
			queryStr = createQuerySeztString(buildQuery);

		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SEZ without Tax Query Executed---->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
					q.setParameter("pcList", pcList);
				}
			}
			if (docFromDate != null && docToDate != null) {
				q.setParameter("docFromDate", docFromDate);
				q.setParameter("docToDate", docToDate);
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
			if (docFromDate != null && docToDate != null) {
				q.setParameter("docFromDate", docFromDate);
				q.setParameter("docToDate", docToDate);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Gstr1SummarySectionDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug(
					"Fetching the Data For Sez without Tax---->" + retList);
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
		obj.setRecords((GenUtil.getBigInteger(arr[1])).intValue());
		obj.setInvValue((BigDecimal) arr[3]);
		obj.setTaxableValue((BigDecimal) arr[4]);
		obj.setTaxPayable((BigDecimal) arr[5]);
		obj.setIgst((BigDecimal) arr[6]);
		obj.setCgst((BigDecimal) arr[7]);
		obj.setSgst((BigDecimal) arr[8]);
		obj.setCess((BigDecimal) arr[9]);
		return obj;
	}

	private String createQuerySeztString(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("SEZT Query Execution BEGIN ");

		String queryStr = "SELECT SUPPLY_TYPE,SUM(RECORD_COUNT) RECORD_COUNT,"
				+ "DOC_TYPE,SUM(INVOICE_VALUE) INVOICE_VALUE,"
				+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
				+ "SUM(TOTAL_TAX) TOTAL_TAX,SUM(IGST) IGST,SUM(CGST) CGST,"
				+ "SUM(SGST) SGST,SUM(CESS) CESS FROM ( "
				+ "SELECT (CASE WHEN SUPPLY_TYPE = 'SEZWP'  THEN 'SEZWP' "
				+ "WHEN SUPPLY_TYPE = 'SEZWOP' THEN 'SEZWOP' END) "
				+ "AS SUPPLY_TYPE,COUNT(*) AS RECORD_COUNT,DOC_TYPE,"
				+ "IFNULL(SUM(DOC_AMT),0) AS INVOICE_VALUE,"
				+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0)) AS TOTAL_TAX,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST,"
				+ "IFNULL(SUM(SGST_AMT),0) AS SGST,"
				+ "SUM(IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0)) AS CESS FROM "
				+ "ANX_OUTWARD_DOC_HEADER WHERE ASP_INVOICE_STATUS = 2 "
				+ "AND COMPLIANCE_APPLICABLE = TRUE "
				+ "AND RETURN_TYPE = 'GSTR1' AND SUPPLY_TYPE <> 'CAN' "
				+ "AND IS_DELETE = FALSE " + buildQuery
				+ "AND TABLE_SECTION ='6B' AND RETURN_TYPE='GSTR1' "
				+ "AND SUPPLY_TYPE IN ('SEZWP','SEZWOP') "
				+ "GROUP BY SUPPLY_TYPE,DOC_TYPE) "
				+ "GROUP BY SUPPLY_TYPE,DOC_TYPE ";
		LOGGER.debug("SEZT& SEZWT  Query Execution Completed");
		return queryStr;
	}
	
	private String createQuerySeztStringForGstr1a(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("SEZT Query Execution BEGIN ");

		String queryStr = "SELECT SUPPLY_TYPE,SUM(RECORD_COUNT) RECORD_COUNT,"
				+ "DOC_TYPE,SUM(INVOICE_VALUE) INVOICE_VALUE,"
				+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
				+ "SUM(TOTAL_TAX) TOTAL_TAX,SUM(IGST) IGST,SUM(CGST) CGST,"
				+ "SUM(SGST) SGST,SUM(CESS) CESS FROM ( "
				+ "SELECT (CASE WHEN SUPPLY_TYPE = 'SEZWP'  THEN 'SEZWP' "
				+ "WHEN SUPPLY_TYPE = 'SEZWOP' THEN 'SEZWOP' END) "
				+ "AS SUPPLY_TYPE,COUNT(*) AS RECORD_COUNT,DOC_TYPE,"
				+ "IFNULL(SUM(DOC_AMT),0) AS INVOICE_VALUE,"
				+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0)) AS TOTAL_TAX,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST,"
				+ "IFNULL(SUM(SGST_AMT),0) AS SGST,"
				+ "SUM(IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0)) AS CESS FROM "
				+ "ANX_OUTWARD_DOC_HEADER_1A WHERE ASP_INVOICE_STATUS = 2 "
				+ "AND COMPLIANCE_APPLICABLE = TRUE "
				+ "AND RETURN_TYPE = 'GSTR1A' AND SUPPLY_TYPE <> 'CAN' "
				+ "AND IS_DELETE = FALSE " + buildQuery
				+ "AND TABLE_SECTION ='6B' AND RETURN_TYPE='GSTR1A' "
				+ "AND SUPPLY_TYPE IN ('SEZWP','SEZWOP') "
				+ "GROUP BY SUPPLY_TYPE,DOC_TYPE) "
				+ "GROUP BY SUPPLY_TYPE,DOC_TYPE ";
		LOGGER.debug("SEZT& SEZWT  Query Execution Completed");
		return queryStr;
	}
}
