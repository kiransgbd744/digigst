package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.views.client.NilNonProcessSubmitdto;
import com.ey.advisory.app.data.views.client.Table14ProcessSubmitdto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

@Component("AspProcessVsSubmitTable14ReportDaoImpl")
public class AspProcessVsSubmitTable14ReportDaoImpl
		implements AspProcessVsSubmitDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	private static final String DOC_KEY_JOINER = "|";

	@Override
	public List<Object> aspProcessVsSubmitDaoReports(SearchCriteria criteria) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		
		String taxPeriodFrom = request.getTaxPeriodFrom();
		String taxPeriodTo = request.getTaxPeriodTo();
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String ProfitCenter = null;
		String plant = null;
		String sales = null;
		String division = null;
		String location = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String GSTIN = null;

		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
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

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					GSTIN = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

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

		StringBuilder buildHeaderHdr = new StringBuilder();
		StringBuilder buildHeader = new StringBuilder();
		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeaderHdr.append(" HDR.SUPPLIER_GSTIN IN :gstinList ");
				buildHeader.append(" GSTIN IN :gstinList ");

			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {

				buildHeaderHdr.append(" AND HDR.PROFIT_CENTRE IN :pcList");
				//buildHeader.append(" AND PROFIT_CENTRE IN :pcList");

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {

				buildHeaderHdr.append(" AND HDR.PLANT_CODE IN :plantList");
				//buildHeader.append(" AND PLANT_CODE IN :plantList");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {

				buildHeaderHdr.append(" AND HDR.SALES_ORGANIZATION IN :salesList");
				//buildHeader.append(" AND SALES_ORGANIZATION IN :salesList");

			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {

				buildHeaderHdr.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");
				
				//buildHeader.append(" AND NEN.DISTRIBUTION_CHANNEL IN :distList");

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {

				buildHeaderHdr.append(" AND HDR.DIVISION IN :divisionList");
				//buildHeader.append(" AND DIVISION IN :divisionList");

			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {

				buildHeaderHdr.append(" AND HDR.LOCATION IN :locationList");
				//buildHeader.append(" AND LOCATION IN :locationList");

			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {

				buildHeaderHdr.append(" AND HDR.USERACCESS1 IN :ud1List");
				//buildHeader.append(" AND USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {

				buildHeaderHdr.append(" AND HDR.USERACCESS2 IN :ud2List");
				//buildHeader.append(" AND USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {

				buildHeaderHdr.append(" AND HDR.USERACCESS3 IN :ud3List");
				//buildHeader.append(" AND USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {

				buildHeaderHdr.append(" AND HDR.USERACCESS4 IN :ud4List");
				//buildHeader.append(" AND USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {

				buildHeaderHdr.append(" AND HDR.USERACCESS5 IN :ud5List");
				//buildHeader.append(" AND USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {

				buildHeaderHdr.append(" AND HDR.USERACCESS6 IN :ud6List");
				//buildHeader.append(" AND USERACCESS6 IN :ud6List");
			}
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			
			buildHeader.append(" AND DERIVED_RET_PERIOD BETWEEN ");
			buildHeader.append(" :taxPeriodFrom AND :taxPeriodTo ");

			buildHeaderHdr.append(
					" AND HDR.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");
			

		}

		String queryStr = createApiProcessedQueryString(buildHeaderHdr.toString(),
				buildHeader.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriodFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriodTo());
			q.setParameter("taxPeriodFrom", derivedRetPeriodFrom);
			q.setParameter("taxPeriodTo", derivedRetPeriodTo);
		}
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

		List<Object[]> list = q.getResultList();
		ProcessingContext context = new ProcessingContext();
		settingFiledGstins(context);
		return list.parallelStream()
				.map(o -> convertApiInwardProcessedRecords(o, context))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Table14ProcessSubmitdto convertApiInwardProcessedRecords(
			Object[] arr, ProcessingContext context) {
		Table14ProcessSubmitdto obj = new Table14ProcessSubmitdto();

		obj.setSNo(arr[0] != null ? arr[0].toString() : null);
		obj.setSupplierGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
		obj.setEcomGSTIN(arr[3] != null ? arr[3].toString() : null);
		obj.setOriginalReturnPeriod(arr[4] != null ? arr[4].toString() : null);
		obj.setAspTaxableValue((BigDecimal) arr[5]);
		obj.setAspIgst((BigDecimal) arr[6]);
		obj.setAspCgst((BigDecimal) arr[7]);
		obj.setAspSgst((BigDecimal) arr[8]);
		obj.setAspCess((BigDecimal) arr[9]);
		obj.setGstnTaxableValue((BigDecimal) arr[10]);
		obj.setGstnIgst((BigDecimal) arr[11]);
		obj.setGstnCgst((BigDecimal) arr[12]);
		obj.setGstnSgst((BigDecimal) arr[13]);
		obj.setGstnCess((BigDecimal) arr[14]);
		obj.setDiffTaxableValue((BigDecimal) arr[15]);
		obj.setDiffIgst((BigDecimal) arr[16]);
		obj.setDiffCgst((BigDecimal) arr[17]);
		obj.setDiffSgst((BigDecimal) arr[18]);
		obj.setDiffCess((BigDecimal) arr[19]);

		return obj;
	}

	private String createApiProcessedQueryString(String buildHeaderHdr,String buildHeader) {

		StringBuilder build = new StringBuilder();
			
		build.append("SELECT  S_No, "
				+ "         GSTIN, "
				+ "         RETURN_PERIOD, "
				+ "	     ECOM_GSTIN, "
				+ "	     ORG_RET_PERIOD, "
				+ "	     ASP_TAXABLE_VALUE, "
				+ "		 ASP_IGST_AMT, "
				+ "		 ASP_CGST_AMT, "
				+ "		 ASP_SGST_AMT, "
				+ "		 ASP_CESS_AMT, "
				+ "		 GSTN_TAXABLE_VALUE, "
				+ "		 GSTN_IGST_AMT, "
				+ "		 GSTN_CGST_AMT, "
				+ "		 GSTN_SGST_AMT, "
				+ "		 GSTN_CESS_AMT, "
				+ "		 (ASP_TAXABLE_VALUE-GSTN_TAXABLE_VALUE) AS DIFF_TAXABLE_VALUE, "
				+ "		 (ASP_IGST_AMT-GSTN_IGST_AMT) AS DIFF_IGST_AMT, "
				+ "		 (ASP_CGST_AMT-GSTN_CGST_AMT) AS DIFF_CGST_AMT, "
				+ "		 (ASP_SGST_AMT-GSTN_SGST_AMT) AS DIFF_SGST_AMT, "
				+ "		 (ASP_CESS_AMT-GSTN_CESS_AMT) AS DIFF_CESS_AMT "
				+ "		  "
				+ "FROM "
				+ "( "
				+ "SELECT  S_No, "
				+ "        GSTIN, "
				+ "        RETURN_PERIOD, "
				+ "	    ECOM_GSTIN, "
				+ "	    ORG_RET_PERIOD, "
				+ "		SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE, "
				+ "		SUM(ASP_IGST_AMT) AS ASP_IGST_AMT, "
				+ "		SUM(ASP_CGST_AMT) ASP_CGST_AMT, "
				+ "		SUM(ASP_SGST_AMT) ASP_SGST_AMT, "
				+ "		SUM(ASP_CESS_AMT) ASP_CESS_AMT, "
				+ "		SUM(GSTN_TAXABLE_VALUE) GSTN_TAXABLE_VALUE, "
				+ "		SUM(GSTN_IGST_AMT) GSTN_IGST_AMT, "
				+ "		SUM(GSTN_CGST_AMT) GSTN_CGST_AMT, "
				+ "		SUM(GSTN_SGST_AMT) GSTN_SGST_AMT, "
				+ "		SUM(GSTN_CESS_AMT) GSTN_CESS_AMT "
				+ "FROM "
				+ "( "
				+ " SELECT S_No, "
				+ "        GSTIN, "
				+ "        RETURN_PERIOD, "
				+ "		ECOM_GSTIN, "
				+ "		ORG_RET_PERIOD, "
				+ "        ASP_TAXABLE_VALUE, "
				+ "	    ASP_IGST_AMT, "
				+ "	    ASP_CGST_AMT, "
				+ "	    ASP_SGST_AMT, "
				+ "	    ASP_CESS_AMT, "
				+ "	    0 AS GSTN_TAXABLE_VALUE, "
				+ "	    0 AS GSTN_IGST_AMT, "
				+ "	    0 AS GSTN_CGST_AMT, "
				+ "	    0 AS GSTN_SGST_AMT, "
				+ "	    0 AS GSTN_CESS_AMT "
				+ "FROM "
				+ " ( "
				+ " SELECT     'Table 14(i)' AS S_No, "
				+ "            HDR.SUPPLIER_GSTIN AS GSTIN, "
				+ "		    HDR.RETURN_PERIOD, "
				+ "			HDR.ECOM_GSTIN AS ECOM_GSTIN, "
				+ "			HDR.ORG_INV_DATE AS ORG_RET_PERIOD, "
				+ "            IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('INV') "
				+ "		 		THEN ITM.TAXABLE_VALUE "
				+ "		 	END), 0) + IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('DR') "
				+ "		 		THEN ITM.TAXABLE_VALUE "
				+ "		 	END), 0) - IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('CR') "
				+ "		 		THEN ITM.TAXABLE_VALUE "
				+ "		 	END), 0) AS ASP_TAXABLE_VALUE "
				+ "		 	,IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('INV') "
				+ "		 		THEN ITM.IGST_AMT "
				+ "		 	END), 0) + IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('DR') "
				+ "		 		THEN ITM.IGST_AMT "
				+ "		 	END), 0) - IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('CR') "
				+ "		 		THEN ITM.IGST_AMT "
				+ "		 	END), 0) AS ASP_IGST_AMT "
				+ "		 	,IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('INV') "
				+ "		 		THEN ITM.CGST_AMT "
				+ "		 	END), 0) + IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('DR') "
				+ "		 		THEN ITM.CGST_AMT "
				+ "		 	END), 0) - IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('CR') "
				+ "		 		THEN ITM.CGST_AMT "
				+ "		 	END), 0) AS ASP_CGST_AMT "
				+ "		 	,IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('INV') "
				+ "		 		THEN ITM.SGST_AMT "
				+ "		 	END), 0) + IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('DR') "
				+ "		 		THEN ITM.SGST_AMT "
				+ "		 	END), 0) - IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('CR') "
				+ "		 		THEN ITM.SGST_AMT "
				+ "		 	END), 0) AS ASP_SGST_AMT "
				+ "		 	,IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('INV') "
				+ "		 		THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "		 	END), 0) + IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('DR') "
				+ "		 		THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "		 	END), 0) - IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('CR') "
				+ "		 		THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "		 	END), 0) AS ASP_CESS_AMT "
				+ "		 FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "		 JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID "
				+ "		 WHERE HDR.TABLE_SECTION IN ('4A','5A','7A(1)','7B(1)','9B','6B','6C') "
				+ "		 AND HDR.TCS_FLAG = 'Y' AND HDR.RETURN_TYPE='GSTR1' AND HDR.ECOM_GSTIN IS NOT NULL "
				+ "		 AND ASP_INVOICE_STATUS = 2 "
				+ "		 AND COMPLIANCE_APPLICABLE = TRUE "
				+ "		 AND IS_DELETE = FALSE "
				+ " AND "
				+ buildHeaderHdr
				+ "		 GROUP BY HDR.RETURN_PERIOD "
				+ "		 	,SUPPLIER_GSTIN "
				+ "		 	,HDR.ECOM_GSTIN "
				+ "			,HDR.ORG_INV_DATE "
				+ "        UNION ALL "
				+ "	 SELECT 'Table 14(ii)' AS S_No, "
				+ "	        HDR.SUPPLIER_GSTIN AS GSTIN, "
				+ "		    HDR.RETURN_PERIOD, "
				+ "			HDR.ECOM_GSTIN AS ECOM_GSTIN, "
				+ "			HDR.ORG_INV_DATE AS ORG_RET_PERIOD, "
				+ "	        IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('INV') "
				+ "		 		THEN ITM.TAXABLE_VALUE "
				+ "		 	END), 0) + IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('DR') "
				+ "		 		THEN ITM.TAXABLE_VALUE "
				+ "		 	END), 0) - IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('CR') "
				+ "		 		THEN ITM.TAXABLE_VALUE "
				+ "		 	END), 0) AS ASP_TAXABLE_VALUE "
				+ "		 	,IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('INV') "
				+ "		 		THEN ITM.IGST_AMT "
				+ "		 	END), 0) + IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('DR') "
				+ "		 		THEN ITM.IGST_AMT "
				+ "		 	END), 0) - IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('CR') "
				+ "		 		THEN ITM.IGST_AMT "
				+ "		 	END), 0) AS ASP_IGST_AMT "
				+ "		 	,IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('INV') "
				+ "		 		THEN ITM.CGST_AMT "
				+ "		 	END), 0) + IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('DR') "
				+ "		 		THEN ITM.CGST_AMT "
				+ "		 	END), 0) - IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('CR') "
				+ "		 		THEN ITM.CGST_AMT "
				+ "		 	END), 0) AS ASP_CGST_AMT "
				+ "		 	,IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('INV') "
				+ "		 		THEN ITM.SGST_AMT "
				+ "		 	END), 0) + IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('DR') "
				+ "		 		THEN ITM.SGST_AMT "
				+ "		 	END), 0) - IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('CR') "
				+ "		 		THEN ITM.SGST_AMT "
				+ "		 	END), 0) AS ASP_SGST_AMT "
				+ "		 	,IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('INV') "
				+ "		 		THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "		 	END), 0) + IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('DR') "
				+ "		 		THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "		 	END), 0) - IFNULL(SUM(CASE "
				+ "		 	WHEN DOC_TYPE IN ('CR') "
				+ "		 		THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "		 	END), 0) AS ASP_CESS_AMT "
				+ "		 FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "		 JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID "
				+ "		 WHERE HDR.TABLE_SECTION = '14(ii)' "
				+ "		 AND HDR.TCS_FLAG='E' AND HDR.RETURN_TYPE='GSTR1' "
				+ "		 AND ASP_INVOICE_STATUS = 2 AND HDR.ECOM_GSTIN IS NOT NULL "
				+ "		 AND COMPLIANCE_APPLICABLE = TRUE "
				+ "		 AND IS_DELETE = FALSE "
				+ " AND "
				+ buildHeaderHdr
				+ "		 GROUP BY HDR.RETURN_PERIOD "
				+ "		 	,SUPPLIER_GSTIN "
				+ "		 	,HDR.ECOM_GSTIN "
				+ "			,HDR.ORG_INV_DATE "
				+ "			) "
				+ " UNION ALL "
				+ "         SELECT S_No, "
				+ "		        '' AS GSTIN, "
				+ "                '' AS RETURN_PERIOD, "
				+ "		        '' AS ECOM_GSTIN, "
				+ "		        '' AS ORG_RET_PERIOD, "
				+ "                0 AS ASP_TAXABLE_VALUE, "
				+ "	            0 AS ASP_IGST_AMT, "
				+ "	            0 AS ASP_CGST_AMT, "
				+ "	            0 AS ASP_SGST_AMT, "
				+ "	            0 AS ASP_CESS_AMT, "
				+ "	            GSTN_TAXABLE_VALUE, "
				+ "	            GSTN_IGST_AMT, "
				+ "	            GSTN_CGST_AMT, "
				+ "	            GSTN_SGST_AMT, "
				+ "	            GSTN_CESS_AMT "
				+ "		FROM "
				+ "        ( SELECT CASE WHEN TABLE_SECTION = '14(i)' THEN 'Table 14(i)' "
				+ "                 		ELSE 'Table 14(ii)' END AS S_No, "
				+ "		        SUM(SUPPLIER_VALUE) AS GSTN_TAXABLE_VALUE, "
				+ "				SUM(IGST_AMT) GSTN_IGST_AMT, "
				+ "		        SUM(CGST_AMT) GSTN_CGST_AMT, "
				+ "		        SUM(SGST_AMT) GSTN_SGST_AMT, "
				+ "		        SUM(CESS_AMT) GSTN_CESS_AMT, "
				+ "				DERIVED_RET_PERIOD, "
				+ "				MAX(CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "		 FROM GETGSTR1_SUPECOM_HEADER "
				+ "		 WHERE IS_DELETE = FALSE "
				+ "		 AND TABLE_SECTION IN ('14(i)','14(ii)') AND "
				+ buildHeader
				+ "		 GROUP BY GSTIN "
				+ "		 ,RETURN_PERIOD "
				+ "		 ,DERIVED_RET_PERIOD "
				+ "		 ,TABLE_SECTION "
				+ "		  "
				+ "		 UNION ALL "
				+ "		  "
				+ "		 SELECT CASE WHEN TABLE_SECTION = '14A(i)' THEN 'Table 14A(i)' "
				+ "                 ELSE 'Table 14A(ii)' END AS S_No, "
				+ "		        SUM(SUPPLIER_VALUE) AS GSTN_TAXABLE_VALUE, "
				+ "				SUM(IGST_AMT) GSTN_IGST_AMT, "
				+ "		        SUM(CGST_AMT) GSTN_CGST_AMT, "
				+ "		        SUM(SGST_AMT) GSTN_SGST_AMT, "
				+ "		        SUM(CESS_AMT) GSTN_CESS_AMT, "
				+ "				DERIVED_RET_PERIOD, "
				+ "				MAX(CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "		 FROM GETGSTR1_SUPECOM_AMD_HEADER "
				+ "		 WHERE IS_DELETE = FALSE "
				+ "		 AND TABLE_SECTION IN ('14A(i)','14A(ii)') AND "
				+ buildHeader
				+ "		 GROUP BY GSTIN "
				+ "		 ,RETURN_PERIOD "
				+ "		 ,DERIVED_RET_PERIOD "
				+ "		 ,TABLE_SECTION "
				+ "		 ) "
				+ "	) "
				+ "	GROUP BY  S_No, "
				+ "	          GSTIN, "
				+ "              RETURN_PERIOD, "
				+ "	          ECOM_GSTIN, "
				+ "	          ORG_RET_PERIOD "
				+ "	)");
		
		
		
				
		return build.toString();
		
	}

	private void settingFiledGstins(ProcessingContext context) {

		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.findByReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
						"GSTR1", "FILED");
		Set<String> filedSet = new HashSet<>();
		for (GstrReturnStatusEntity entity : filedRecords) {
			filedSet.add(
					entity.getGstin() + DOC_KEY_JOINER + entity.getTaxPeriod());
		}
		context.seAttribute("filedSet", filedSet);
	}

	private String isGstinTaxperiodFiled(ProcessingContext context,
			String gstin, String taxPeriod) {
		String filingStatus = "False";
		Set<String> filedSet = (Set<String>) context.getAttribute("filedSet");
		String key = gstin + DOC_KEY_JOINER + taxPeriod;
		if (filedSet != null && filedSet.contains(key)) {
			filingStatus = "True";
		}
		return filingStatus;

	}
}
