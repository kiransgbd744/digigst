package com.ey.advisory.app.services.jobs.gstr6;

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
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.GSTR2PREntityLevelSummaryDto;
import com.ey.advisory.app.data.views.client.GSTR6EntityLevelSummaryDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Gstr6EntityLevelSummReportsDaoImpl")
public class Gstr6EntityLevelSummReportsDaoImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6EntityLevelSummReportsDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	

	public List<GSTR6EntityLevelSummaryDto> getEntityLevelSummReports(Gstr6SummaryRequestDto request) {
		String taxperiod = request.getTaxPeriod();
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String profitCenter = null;
		String plant = null;
		String purchase = null;
		String division = null;
		String location = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String gstn = null;

		List<String> pcList = new ArrayList<>();
		List<String> plantList = new ArrayList<>();
		List<String> divisionList = new ArrayList<>();
		List<String> locationList = new ArrayList<>();
		List<String> purchaseList = new ArrayList<>();
		List<String> ud1List = new ArrayList<>();
		List<String> ud2List = new ArrayList<>();
		List<String> ud3List = new ArrayList<>();
		List<String> ud4List = new ArrayList<>();
		List<String> ud5List = new ArrayList<>();
		List<String> ud6List = new ArrayList<>();
		List<String> gstinList = new ArrayList<>();

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)
						&& dataSecAttrs.get(OnboardingConstant.GSTIN) != null
						&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()) {
					gstn = key;
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					profitCenter = key;
					if (dataSecAttrs.get(OnboardingConstant.PC) != null
							&& !dataSecAttrs.get(OnboardingConstant.PC)
									.isEmpty()) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (dataSecAttrs.get(OnboardingConstant.PLANT) != null
							&& !dataSecAttrs.get(OnboardingConstant.PLANT)
									.isEmpty()) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (dataSecAttrs.get(OnboardingConstant.DIVISION) != null
							&& !dataSecAttrs.get(OnboardingConstant.DIVISION)
									.isEmpty()) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (dataSecAttrs.get(OnboardingConstant.LOCATION) != null
							&& !dataSecAttrs.get(OnboardingConstant.LOCATION)
									.isEmpty()) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (dataSecAttrs.get(OnboardingConstant.PO) != null
							&& !dataSecAttrs.get(OnboardingConstant.PO)
									.isEmpty()) {
						purchaseList = dataSecAttrs.get(OnboardingConstant.PO);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD1) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD1)
									.isEmpty()) {
						ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD2) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD2)
									.isEmpty()) {
						ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD3) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD3)
									.isEmpty()) {
						ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD4) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD4)
									.isEmpty()) {
						ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD5) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD5)
									.isEmpty()) {
						ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD6) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD6)
									.isEmpty()) {
						ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
					}
				}
			}
		}
		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildQueryIsd = new StringBuilder();

		if (gstn != null && !gstn.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND HDR.CUST_GSTIN IN :gstinList");
				buildQueryIsd.append(" AND ISD_GSTIN IN :gstinList");
			}
		}
		if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& !pcList.isEmpty()) {
			buildQuery.append(" AND HDR.PROFIT_CENTRE IN :pcList");
		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& !plantList.isEmpty()) {
			buildQuery.append(" AND HDR.PLANT_CODE IN :plantList");
		}
		if (purchase != null && !purchase.isEmpty() && purchaseList != null
				&& !purchaseList.isEmpty()) {
			buildQuery
					.append(" AND HDR.PURCHASE_ORGANIZATION IN :purchaseList");
		}

		if (division != null && !division.isEmpty() && divisionList != null
				&& !divisionList.isEmpty()) {
			buildQuery.append(" AND HDR.DIVISION IN :divisionList");
		}

		if (location != null && !location.isEmpty() && locationList != null
				&& !locationList.isEmpty()) {
			buildQuery.append(" AND HDR.LOCATION IN :locationList");
		}
		if (ud1 != null && !ud1.isEmpty() && ud1List != null
				&& !ud1List.isEmpty()) {
			buildQuery.append(" AND HDR.USERACCESS1 IN :ud1List");
		}
		if (ud2 != null && !ud2.isEmpty() && ud2List != null
				&& !ud2List.isEmpty()) {
			buildQuery.append(" AND HDR.USERACCESS2 IN :ud2List");
		}
		if (ud3 != null && !ud3.isEmpty() && ud3List != null
				&& !ud3List.isEmpty()) {
			buildQuery.append(" AND HDR.USERACCESS3 IN :ud3List");
		}
		if (ud4 != null && !ud4.isEmpty() && ud4List != null
				&& !ud4List.isEmpty()) {
			buildQuery.append(" AND HDR.USERACCESS4 IN :ud4List");
		}
		if (ud5 != null && !ud5.isEmpty() && ud5List != null
				&& !ud5List.isEmpty()) {
			buildQuery.append(" AND HDR.USERACCESS5 IN :ud5List");
		}
		if (ud6 != null && !ud6.isEmpty() && ud6List != null
				&& !ud6List.isEmpty()) {
			buildQuery.append(" AND HDR.USERACCESS6 IN :ud6List");
		}
		if (StringUtils.isNotBlank(taxperiod)
				&& StringUtils.isNotBlank(taxperiod)) {
			buildQuery.append(" AND  HDR.DERIVED_RET_PERIOD = :taxperiod ");
			buildQueryIsd.append(" AND  DERIVED_RET_PERIOD = :taxperiod ");
		}
		String queryStr = creategstnTransQueryString(buildQuery.toString(),buildQueryIsd.toString());

		LOGGER.error("bufferString-------------------------->" + queryStr);
		
		List<GSTR6EntityLevelSummaryDto> resp = null;
		try{
		Query outquery = entityManager.createNativeQuery(queryStr);

		if (gstn != null && !gstn.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				outquery.setParameter("gstinList", gstinList);
			}
		}
		if (StringUtils.isNotBlank(taxperiod)) {
			outquery.setParameter("taxperiod",
					GenUtil.convertTaxPeriodToInt(taxperiod));
		}
		if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& !pcList.isEmpty()) {
			outquery.setParameter("pcList", pcList);
		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& !plantList.isEmpty()) {
			outquery.setParameter("plantList", plantList);
		}
		if (purchase != null && !purchase.isEmpty() && purchaseList != null
				&& !purchaseList.isEmpty()) {
			outquery.setParameter("purchaseList", purchaseList);
		}
		if (division != null && !division.isEmpty() && divisionList != null
				&& !divisionList.isEmpty()) {
			outquery.setParameter("divisionList", divisionList);
		}
		if (location != null && !location.isEmpty() && locationList != null
				&& !locationList.isEmpty()) {
			outquery.setParameter("locationList", locationList);
		}
		if (ud1 != null && !ud1.isEmpty() && ud1List != null
				&& !ud1List.isEmpty()) {
			outquery.setParameter("ud1List", ud1List);
		}
		if (ud2 != null && !ud2.isEmpty() && ud2List != null
				&& !ud2List.isEmpty()) {
			outquery.setParameter("ud2List", ud2List);
		}
		if (ud3 != null && !ud3.isEmpty() && ud3List != null
				&& !ud3List.isEmpty()) {
			outquery.setParameter("ud3List", ud3List);
		}
		if (ud4 != null && !ud4.isEmpty() && ud4List != null
				&& !ud4List.isEmpty()) {
			outquery.setParameter("ud4List", ud4List);
		}
		if (ud5 != null && !ud5.isEmpty() && ud5List != null
				&& !ud5List.isEmpty()) {
			outquery.setParameter("ud5List", ud5List);
		}
		if (ud6 != null && !ud6.isEmpty() && ud6List != null
				&& !ud6List.isEmpty()) {
			outquery.setParameter("ud6List", ud6List);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = outquery.getResultList();
		 resp = list.parallelStream().map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
	} catch (Exception e) {
		e.printStackTrace();
		LOGGER.error("While Downloading Entity Level Summary Report ",e);
	//	throw new AppException("Unexpected error in query execution.", e);
	}
		return resp;
	}
	

	private GSTR6EntityLevelSummaryDto convert(
			Object[] arr) {

		GSTR6EntityLevelSummaryDto obj = new GSTR6EntityLevelSummaryDto();
		
		
		
		obj.setCount((GenUtil.getBigInteger(arr[0])).intValue());
		obj.setTableDescription(arr[1] != null ? arr[1].toString() : null);
		obj.setGSTIN(arr[2] != null ? arr[2].toString() : null);
		obj.setInvoiceValue((BigDecimal) arr[3]);
		obj.setTaxableValue((BigDecimal) arr[4]);
		obj.setTotalTax((BigDecimal) arr[5]);
		obj.setIGST((BigDecimal) arr[6]);
		obj.setCGST((BigDecimal) arr[7]);
		obj.setSGST((BigDecimal) arr[8]);
		obj.setCess((BigDecimal) arr[9]);
		obj.setTaxDocType(arr[10] != null ? arr[10].toString() : null);
		return obj;
	
	}

	private String creategstnTransQueryString(String buildQuery,String buildQueryIsd) {

		LOGGER.debug("bufferString-------------------------->" + buildQuery);
		
		
		
		String queryString = "SELECT IFNULL(SUM(ID_CT),0) as ID_CT,"
				+ "CASE WHEN ELIG_IND_ASP = 'TOTAL' THEN TAX_DOC_TYPE_ASP ELSE "
				+ "RIGHT(ELIG_IND_ASP,LENGTH (ELIG_IND_ASP) -2) END "
				+ "AS TABLE_DESCRIPTION,CUST_GSTIN_ASP,"
				+ "SUM(IFNULL(INVOICE_VAL_ASP,0)) AS INVOICE_VAL_ASP,"
				+ "SUM(IFNULL(TAXABLE_VAL_ASP,0)) AS TAXABLE_VAL_ASP,"
				+ "SUM(IFNULL(TOTAL_TAXASP,0)) AS TOTAL_TAXASP,"
				+ "SUM(IFNULL(IGST_AMOUNTASP,0)) AS IGST_AMOUNTASP,"
				+ "SUM(IFNULL(CGST_AMOUNTASP,0)) AS CGST_AMOUNTASP,"
				+ "SUM(IFNULL(SGST_AMOUNTASP,0)) AS SGST_AMOUNTASP,"
				+ "SUM(IFNULL(CESS_AMOUNTASP,0)) AS CESS_AMOUNTASP,TAX_DOC_TYPE_ASP "
				+ "FROM ( SELECT IFNULL(SUM(ID_CNT),0) as ID_CT,"
				+ "TAX_DOC_TYPE_ASP,ELIG_IND_ASP,CUST_GSTIN_ASP,"
				+ "SUM(IFNULL(INVOICE_VALUE_ASP,0)) AS INVOICE_VAL_ASP,"
				+ "SUM(IFNULL(TAXABLE_VALUE_ASP,0)) AS TAXABLE_VAL_ASP,"
				+ "SUM(IFNULL(TOTAL_TAX_ASP,0)) AS TOTAL_TAXASP,"
				+ "SUM(IFNULL(IGST_AMOUNT_ASP,0)) AS IGST_AMOUNTASP,"
				+ "SUM(IFNULL(CGST_AMOUNT_ASP,0)) AS CGST_AMOUNTASP,"
				+ "SUM(IFNULL(SGST_AMOUNT_ASP,0)) AS SGST_AMOUNTASP,"
				+ "SUM(IFNULL(CESS_AMOUNT_ASP,0)) AS CESS_AMOUNTASP "
				+ "FROM ( select SUM(ID_COUNT) as ID_CNT, ELIG_IND AS ELIG_IND_ASP,"
				+ "TAX_DOC_TYPE AS TAX_DOC_TYPE_ASP,CUST_GSTIN AS CUST_GSTIN_ASP,"
				+ "SUM(IFNULL(INVOICE_VALUE,0)) AS INVOICE_VALUE_ASP,"
				+ "SUM(IFNULL(TAXABLE_VALUE,0)) AS TAXABLE_VALUE_ASP,"
				+ "SUM(IFNULL(TOTAL_TAX,0)) AS TOTAL_TAX_ASP,"
				+ "SUM(IFNULL(IGST_AMOUNT,0)) AS IGST_AMOUNT_ASP,"
				+ "SUM(IFNULL(CGST_AMOUNT,0)) AS CGST_AMOUNT_ASP,"
				+ "SUM(IFNULL(SGST_AMOUNT,0)) AS SGST_AMOUNT_ASP,"
				+ "SUM(IFNULL(CESS_AMOUNT,0)) AS CESS_AMOUNT_ASP "
				+ "FROM ( SELECT COUNT( HDR.ID) AS ID_COUNT,"
				+ "IFNULL(SUM(CASE WHEN HDR.DOC_TYPE  IN "
				+ "('INV','DR','RNV','RDR')THEN IFNULL(HDR.DOC_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN IFNULL(HDR.DOC_AMT,0) END ),0) "
				+ "AS INVOICE_VALUE,IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN HDR.TAXABLE_VALUE END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') THEN "
				+ "HDR.TAXABLE_VALUE END),0) AS TAXABLE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(HDR.TAX_PAYABLE,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "IFNULL(HDR.TAX_PAYABLE,0) END),0) AS TOTAL_TAX,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(HDR.IGST_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(HDR.IGST_AMT,0) END),0) AS IGST_AMOUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(HDR.CGST_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(HDR.CGST_AMT,0)END),0)  AS CGST_AMOUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(HDR.SGST_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(HDR.SGST_AMT,0)END),0)  AS SGST_AMOUNT,"
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(HDR.CESS_AMT_SPECIFIC,0) END),0)  - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(HDR.CESS_AMT_SPECIFIC,0) END),0)) + "
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(HDR.CESS_AMT_ADVALOREM,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(HDR.CESS_AMT_ADVALOREM,0) END),0)) AS CESS_AMOUNT,"
				+ "'TOTAL' ELIG_IND,HDR.TAX_DOC_TYPE,HDR.CUST_GSTIN,HDR.RETURN_PERIOD "
				+ "FROM ANX_INWARD_DOC_HEADER HDR "
				+ "WHERE RETURN_TYPE = 'GSTR6'   AND HDR.SUPPLY_TYPE <> 'CAN' "
				+ "AND HDR.IS_PROCESSED=TRUE AND HDR.IS_DELETE=FALSE "
				+ buildQuery
				+ "GROUP BY HDR.TAX_DOC_TYPE,HDR.CUST_GSTIN,HDR.RETURN_PERIOD "
				+ "UNION  ALL "
				+ "SELECT 0 AS ID_COUNT,0 AS INVOICE_VALUE,0 AS TAXABLE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(ITM.IN_ELIGIBLE_TAX_PAYABLE,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(ITM.IN_ELIGIBLE_TAX_PAYABLE,0) END),0) AS TOTAL_TAX,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(ITM.IN_ELIGIBLE_IGST_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(ITM.IN_ELIGIBLE_IGST_AMT,0) END),0) AS IGST_AMOUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(ITM.IN_ELIGIBLE_CGST_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(ITM.IN_ELIGIBLE_CGST_AMT,0)END),0)  AS CGST_AMOUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(ITM.IN_ELIGIBLE_SGST_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(ITM.IN_ELIGIBLE_SGST_AMT,0)END),0)  AS SGST_AMOUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(ITM.IN_ELIGIBLE_CESS_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(ITM.IN_ELIGIBLE_CESS_AMT,0) END),0) AS CESS_AMOUNT,"
				+ "'1_Ineligible'  AS ELIG_IND,"
				+ "HDR.TAX_DOC_TYPE,HDR.CUST_GSTIN,HDR.RETURN_PERIOD "
				+ "FROM ANX_INWARD_DOC_HEADER HDR INNER JOIN "
				+ "ANX_INWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "INNER JOIN "
				+ "GSTIN_INFO GIF ON HDR.CUST_GSTIN = GIF.GSTIN INNER JOIN "
				+ "ENTITY_CONFG_PRMTR ECP ON "
				+ "GIF.ENTITY_ID = ECP.ENTITY_ID "
				+ "AND ECP.IS_DELETE = FALSE AND QUESTION_CODE = 'I15' "
				+ "WHERE RETURN_TYPE = 'GSTR6' AND HDR.SUPPLY_TYPE <> 'CAN' "
				+ "AND HDR.IS_PROCESSED=TRUE AND HDR.IS_DELETE=FALSE "
				+ buildQuery 
				+ "GROUP BY ELIGIBILITY_INDICATOR,HDR.TAX_DOC_TYPE,"
				+ "HDR.CUST_GSTIN,HDR.RETURN_PERIOD,ITM.ELIGIBILITY_INDICATOR "
				+ "UNION ALL SELECT 0 AS ID_COUNT,0 AS INVOICE_VALUE,0 AS TAXABLE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(ITM.ELIGIBLE_TAX_PAYABLE,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(ITM.ELIGIBLE_TAX_PAYABLE,0) END),0) AS TOTAL_TAX,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(ITM. ELIGIBLE_IGST_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(ITM.ELIGIBLE_IGST_AMT,0) END),0) AS IGST_AMOUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(ITM.ELIGIBLE_CGST_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(ITM.ELIGIBLE_CGST_AMT,0)END),0)  AS CGST_AMOUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(ITM.ELIGIBLE_SGST_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(ITM.ELIGIBLE_SGST_AMT,0)END),0)  AS SGST_AMOUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') "
				+ "THEN IFNULL(ITM.ELIGIBLE_CESS_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') "
				+ "THEN IFNULL(ITM.ELIGIBLE_CESS_AMT,0) END),0) AS CESS_AMOUNT,"
				+ "'2_Eligible'  AS ELIG_IND,"
				+ "HDR.TAX_DOC_TYPE,HDR.CUST_GSTIN,HDR.RETURN_PERIOD "
				+ "FROM ANX_INWARD_DOC_HEADER HDR INNER JOIN "
				+ "ANX_INWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD INNER JOIN "
				+ "GSTIN_INFO GIF ON HDR.CUST_GSTIN = GIF.GSTIN INNER JOIN "
				+ "ENTITY_CONFG_PRMTR ECP ON GIF.ENTITY_ID = ECP.ENTITY_ID "
				+ "AND ECP.IS_DELETE = FALSE AND QUESTION_CODE = 'I15' "
				+ "WHERE RETURN_TYPE = 'GSTR6' AND HDR.SUPPLY_TYPE <> 'CAN' "
				+ "AND HDR.IS_PROCESSED=TRUE AND HDR.IS_DELETE=FALSE "
				+ buildQuery
				+ "GROUP BY ELIGIBILITY_INDICATOR,HDR.TAX_DOC_TYPE,"
				+ "HDR.CUST_GSTIN,HDR.RETURN_PERIOD,ITM.ELIGIBILITY_INDICATOR "
				+ "UNION ALL "
				+ "select COUNT(ID) AS ID_COUNT ,0 as INVOICE_VALUE, 0 AS  TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(IGST_AMT_AS_SGST,0)+"
				+ "IFNULL(IGST_AMT_AS_CGST,0)+ IFNULL(SGST_AMT_AS_SGST,0)+"
				+ "IFNULL(SGST_AMT_AS_IGST,0)+ IFNULL(CGST_AMT_AS_CGST,0)+"
				+ "IFNULL(CGST_AMT_AS_IGST,0)+IFNULL(CESS_AMT,0)) as TOTAL_TAX,"
				+ "SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(CGST_AMT_AS_IGST,0)+"
				+ "IFNULL(SGST_AMT_AS_IGST,0)) as IGST_AMOUNT,"
				+ "SUM(IFNULL(IGST_AMT_AS_CGST,0)+ IFNULL(CGST_AMT_AS_CGST,0)) "
				+ "as CGST_AMOUNT,SUM(IFNULL(SGST_AMT_AS_SGST,0)+"
				+ "IFNULL(IGST_AMT_AS_SGST,0)) as SGST_AMOUNT,"
				+ "SUM(IFNULL(CESS_AMT,0)) as CESS_AMOUNT,'TOTAL' AS ELIG_IND,"
				+ "case when DOC_TYPE = 'INV' THEN 'DISTRIBUTION_INVOICE' "
				+ "WHEN DOC_TYPE IN ('CR','DR') THEN 'DISTRIBUTION_CREDIT' "
				+ "WHEN DOC_TYPE = 'RNV' THEN 'REDISTRIBUTION_INVOICE' "
				+ "WHEN DOC_TYPE IN ('RCR','RDR') THEN 'REDISTRIBUTION_CREDIT' "
				+ "END AS TAX_DOC_TYPE,ISD_GSTIN AS CUST_GSTIN,"
				+ "TAX_PERIOD AS RETURN_PERIOD FROM GSTR6_ISD_DISTRIBUTION "
				+ "WHERE IS_DELETE = FALSE AND SUPPLY_TYPE IS NULL "
				+ buildQueryIsd
				+ "GROUP BY DOC_TYPE,ELIGIBLE_INDICATOR,ISD_GSTIN,TAX_PERIOD "
				+ "UNION ALL "
				+ "select COUNT(ID) AS ID_COUNT ,0 as INVOICE_VALUE,"
				+ "0 AS  TAXABLE_VALUE,SUM(IFNULL(IGST_AMT_AS_IGST,0)+"
				+ "IFNULL(IGST_AMT_AS_SGST,0)+IFNULL(IGST_AMT_AS_CGST,0)+"
				+ "IFNULL(SGST_AMT_AS_SGST,0)+"
				+ "IFNULL(SGST_AMT_AS_IGST,0)+ IFNULL(CGST_AMT_AS_CGST,0)+"
				+ "IFNULL(CGST_AMT_AS_IGST,0)+IFNULL(CESS_AMT,0)) as TOTAL_TAX,"
				+ "SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(SGST_AMT_AS_IGST,0)+"
				+ "IFNULL(CGST_AMT_AS_IGST,0)) as IGST_AMOUNT,"
				+ "SUM(IFNULL(CGST_AMT_AS_CGST,0)+ IFNULL(IGST_AMT_AS_CGST,0)) "
				+ "as CGST_AMOUNT,"
				+ "SUM(IFNULL(SGST_AMT_AS_SGST,0)+IFNULL(IGST_AMT_AS_SGST,0)) as SGST_AMOUNT,"
				+ "SUM(IFNULL(CESS_AMT,0)) as CESS_AMOUNT,"
				+ "CASE WHEN  ELIGIBLE_INDICATOR = 'E'THEN '2_Eligible' "
				+ "WHEN ELIGIBLE_INDICATOR = 'IE'THEN '1_Ineligible' "
				+ "END AS ELIG_IND,"
				+ "case when DOC_TYPE = 'INV' THEN 'DISTRIBUTION_INVOICE' "
				+ "WHEN DOC_TYPE IN ('CR','DR') THEN 'DISTRIBUTION_CREDIT' "
				+ "WHEN DOC_TYPE = 'RNV' THEN 'REDISTRIBUTION_INVOICE' "
				+ "WHEN DOC_TYPE IN ('RCR','RDR') THEN 'REDISTRIBUTION_CREDIT' "
				+ "END AS TAX_DOC_TYPE,"
				+ "ISD_GSTIN AS CUST_GSTIN,TAX_PERIOD AS RETURN_PERIOD "
				+ "FROM GSTR6_ISD_DISTRIBUTION WHERE IS_DELETE = FALSE "
				+ "AND SUPPLY_TYPE IS NULL "
				+ buildQueryIsd
				+ "GROUP BY DOC_TYPE,ELIGIBLE_INDICATOR,ISD_GSTIN,TAX_PERIOD )"
				+ "GROUP BY CUST_GSTIN,TAX_DOC_TYPE,ELIG_IND "
				+ "UNION ALL "
				+ "SELECT COUNT( HDR.ID) AS ID_COUNT,'TOTAL' ELIG_IND_ASP,"
				+ "'Eligible/Ineligible ITC' AS TAX_DOC_TYPE_ASP,"
				+ "HDR.CUST_GSTIN AS CUST_GSTIN_ASP,"
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN IFNULL(HDR.DOC_AMT,0) END),0)"
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(HDR.DOC_AMT,0) END),0)) + "
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR') "
				+ "THEN IFNULL(HDR.DOC_AMT,0) - IFNULL(HDR.PRECEEDING_INVOICE_VALUE,0) END),0) "
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR') "
				+ "THEN IFNULL(HDR.DOC_AMT,0) - IFNULL(HDR.PRECEEDING_INVOICE_VALUE,0) END),0))  AS INVOICE_VALUE,"
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN IFNULL(HDR.TAXABLE_VALUE,0) END),0) "
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(HDR.TAXABLE_VALUE,0) END),0)) + "
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR') "
				+ "THEN IFNULL(HDR.TAXABLE_VALUE,0) - IFNULL(HDR.PRECEEDING_TAXABLE_VALUE,0) END),0) "
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR') "
				+ "THEN IFNULL(HDR.TAXABLE_VALUE,0) - "
				+ "IFNULL(HDR.PRECEEDING_TAXABLE_VALUE,0) END),0)) AS TAXABLE_VALUE,"
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN IFNULL(HDR.TAX_PAYABLE,0) END),0) "
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(HDR.TAX_PAYABLE,0) END),0)) + "
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR') "
				+ "THEN IFNULL(HDR.TAX_PAYABLE,0) - IFNULL(HDR.PRECEEDING_TOTAL_TAX,0) END),0) "
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR') "
				+ "THEN IFNULL(HDR.TAX_PAYABLE,0) - IFNULL(HDR.PRECEEDING_TOTAL_TAX,0) END),0)) AS TOTAL_TAX,"
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN IFNULL(HDR.IGST_AMT,0) END),0) "
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(HDR.IGST_AMT,0) END),0)) + "
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR') "
				+ "THEN IFNULL(HDR.IGST_AMT,0) - IFNULL(HDR.PRECEEDING_IGST_AMT,0) END),0) "
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR') "
				+ "THEN IFNULL(HDR.IGST_AMT,0) - IFNULL(HDR.PRECEEDING_IGST_AMT,0) END),0)) AS IGST_AMOUNT,"
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN IFNULL(HDR.CGST_AMT,0) END),0) "
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(HDR.CGST_AMT,0) END),0)) + "
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR') "
				+ "THEN IFNULL(HDR.CGST_AMT,0) - IFNULL(HDR.PRECEEDING_CGST_AMT,0) END),0) "
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR') "
				+ "THEN IFNULL(HDR.CGST_AMT,0) - IFNULL(HDR.PRECEEDING_CGST_AMT,0) END),0)) AS CGST_AMOUNT,"
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN IFNULL(HDR.SGST_AMT,0) END),0) "
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(HDR.SGST_AMT,0) END),0)) + "
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR') "
				+ "THEN IFNULL(HDR.SGST_AMT,0) - IFNULL(HDR.PRECEEDING_SGST_AMT,0) END),0) "
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR') "
				+ "THEN IFNULL(HDR.SGST_AMT,0) - IFNULL(HDR.PRECEEDING_SGST_AMT,0) END),0)) AS SGST_AMOUNT,"
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN (IFNULL(HDR.CESS_AMT_SPECIFIC,0) "
				+ "+ IFNULL(CESS_AMT_ADVALOREM,0))  END),0) "
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN "
				+ "(IFNULL(HDR.CESS_AMT_SPECIFIC,0) +IFNULL(CESS_AMT_ADVALOREM,0)) END),0)) + "
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR') "
				+ "THEN IFNULL((HDR.CESS_AMT_SPECIFIC +CESS_AMT_ADVALOREM),0) - "
				+ "IFNULL((HDR.PRECEEDING_CESS_AMT ),0) END),0) "
				+ "- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR') "
				+ "THEN IFNULL((HDR.CESS_AMT_SPECIFIC +CESS_AMT_ADVALOREM),0) - "
				+ "IFNULL((HDR.PRECEEDING_CESS_AMT ),0) END),0)) AS CESS_AMOUNT "
				+ "FROM ANX_INWARD_DOC_HEADER HDR "
				+ "WHERE RETURN_TYPE = 'GSTR6' AND HDR.SUPPLY_TYPE <> 'CAN' "
				+ "AND HDR.IS_PROCESSED=TRUE AND HDR.IS_DELETE=FALSE "
				+ "AND HDR.POS = SUBSTRING ( HDR.CUST_GSTIN, 1 , 2 ) "
				+ buildQuery 
				+ "GROUP BY HDR.CUST_GSTIN,HDR.RETURN_PERIOD "
				+ "UNION ALL "
				+ "select COUNT(ID) AS ID_COUNT ,'TOTAL' ELIG_IND_ASP,"
				+ "'Eligible/Ineligible ITC' AS TAX_DOC_TYPE_ASP,"
				+ "ISD_GSTIN AS CUST_GSTIN_ASP,0 as INVOICE_VALUE,"
				+ "0 AS  TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(IGST_AMT_AS_SGST,0)+"
				+ "IFNULL(IGST_AMT_AS_CGST,0)+ IFNULL(SGST_AMT_AS_SGST,0)+"
				+ "IFNULL(SGST_AMT_AS_IGST,0)+ IFNULL(CGST_AMT_AS_CGST,0)+ IFNULL(CGST_AMT_AS_IGST,0)+"
				+ "IFNULL(CESS_AMT,0)) as TOTAL_TAX,"
				+ "SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(CGST_AMT_AS_IGST,0)+"
				+ "IFNULL(SGST_AMT_AS_IGST,0)) as IGST_AMOUNT,"
				+ "SUM(IFNULL(CGST_AMT_AS_CGST,0)+ IFNULL(IGST_AMT_AS_CGST,0)) as CGST_AMOUNT,"
				+ "SUM(IFNULL(SGST_AMT_AS_SGST,0)+ IFNULL(IGST_AMT_AS_SGST,0)) as SGST_AMOUNT,"
				+ "SUM(IFNULL(CESS_AMT,0)) as CESS_AMOUNT "
				+ "FROM GSTR6_ISD_DISTRIBUTION "
				+ "WHERE IS_DELETE = FALSE AND SUPPLY_TYPE is NULL AND DOC_TYPE = 'CR' "
				+ buildQueryIsd
				+ "GROUP BY ELIGIBLE_INDICATOR,ISD_GSTIN,TAX_PERIOD "
				+ "UNION  ALL "
				+ "select COUNT(ID) AS ID_COUNT,"
				+ "CASE WHEN  ELIGIBLE_INDICATOR IN ('IS','E')THEN '2_Eligible' "
				+ "WHEN  ELIGIBLE_INDICATOR IN ('NO','IE')THEN '1_Ineligible' "
				+ "END AS ELIG_IND_ASP,'Eligible/Ineligible ITC' AS TAX_DOC_TYPE_ASP,"
				+ "ISD_GSTIN AS CUST_GSTIN_ASP,0 as INVOICE_VALUE, 0 AS  TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(IGST_AMT_AS_SGST,0)+"
				+ "IFNULL(IGST_AMT_AS_CGST,0)+ IFNULL(SGST_AMT_AS_SGST,0)+"
				+ "IFNULL(SGST_AMT_AS_IGST,0)+ IFNULL(CGST_AMT_AS_CGST,0)+ IFNULL(CGST_AMT_AS_IGST,0)+"
				+ "IFNULL(CESS_AMT,0)) as TOTAL_TAX,"
				+ "SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(CGST_AMT_AS_IGST,0)+"
				+ "IFNULL(SGST_AMT_AS_IGST,0)) as IGST_AMOUNT,"
				+ "SUM(IFNULL(CGST_AMT_AS_CGST,0)+ IFNULL(IGST_AMT_AS_CGST,0)) as CGST_AMOUNT,"
				+ "SUM(IFNULL(SGST_AMT_AS_SGST,0)+ IFNULL(IGST_AMT_AS_SGST,0)) as SGST_AMOUNT,"
				+ "SUM(IFNULL(CESS_AMT,0)) as CESS_AMOUNT "
				+ "FROM GSTR6_ISD_DISTRIBUTION "
				+ "WHERE IS_DELETE = FALSE AND SUPPLY_TYPE is NULL "
				+ "AND DOC_TYPE = 'INV' "
				+ buildQueryIsd
				+ "GROUP BY ELIGIBLE_INDICATOR,ISD_GSTIN,TAX_PERIOD ) "
				+ "GROUP BY ELIG_IND_ASP,TAX_DOC_TYPE_ASP,CUST_GSTIN_ASP)"
				+ "GROUP BY ELIG_IND_ASP,TAX_DOC_TYPE_ASP,CUST_GSTIN_ASP "
				+ "ORDER BY CUST_GSTIN_ASP,TAX_DOC_TYPE_ASP,ELIG_IND_ASP DESC";
		
		return queryString;
	}


	
	
}
