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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
import lombok.extern.slf4j.Slf4j;

/**
 * @author BalaKrishna S
 *
 */
@Slf4j
@Component("BasicDocSummaryScreenSectionDaoImpl")
public class BasicDocSummaryScreenSectionDaoImpl
		implements BasicGstr1DocSummaryScreenSectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Override
	public List<Gstr1SummarySectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

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
		StringBuilder buildHdr = new StringBuilder();
		StringBuilder buildVertical = new StringBuilder();
		StringBuilder buildQueryGstr1 = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" SUPPLIER_GSTIN IN (:gstinList) ");
				buildHdr.append(" HDR.SUPPLIER_GSTIN IN (:gstinList) ");
				buildVertical.append(" SUPPLIER_GSTIN IN (:gstinList) ");
				buildQueryGstr1.append(" SUPPLIER_GSTIN IN (:gstinList) ");

			}
		}
		if (ewbGen != null && ewbGen.equalsIgnoreCase("YES")) {
			build.append(" AND EWB_NO_RESP IS NOT NULL ");
			buildHdr.append(" AND HDR.EWB_NO_RESP IS NOT NULL ");
			// buildVertical.append(" EWB_NO_RESP IS NULL ");
		}
		if (ewbGen != null && ewbGen.equalsIgnoreCase("NO")) {
			build.append(" AND EWB_NO_RESP IS NULL ");
			buildHdr.append(" AND HDR.EWB_NO_RESP IS NULL ");
			// buildVertical.append(" EWB_NO_RESP IS NULL ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("YES")) {
			build.append(" AND IRN_RESPONSE IS NOT NULL ");
			buildHdr.append(" AND HDR.IRN_RESPONSE IS NOT NULL ");
			// buildVertical.append(" EWB_NO_RESP IS NULL ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("NO")) {
			build.append(" AND IRN_RESPONSE IS NULL ");
			buildHdr.append(" AND HDR.IRN_RESPONSE IS NULL ");
			// buildVertical.append(" EWB_NO_RESP IS NULL ");
		}
		if (docFromDate != null && docToDate != null) {
			build.append(" AND DOC_DATE BETWEEN :docFromDate AND :docToDate ");
			buildHdr.append(
					" AND  HDR.DOC_DATE BETWEEN :docFromDate AND :docToDate ");
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND HDR.PROFIT_CENTRE IN (:pcList) ");
				buildHdr.append(" AND HDR.PROFIT_CENTRE IN (:pcList) ");
				buildVertical.append(" AND PROFIT_CENTRE IN (:pcList) ");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND HDR.PLANT_CODE IN (:plantList) ");
				buildHdr.append(" AND HDR.PLANT_CODE IN (:plantList) ");
				buildVertical.append(" AND PLANT_CODE IN (:plantList) ");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND HDR.SALES_ORGANIZATION IN (:salesList) ");
				buildHdr.append(" AND HDR.SALES_ORGANIZATION IN (:salesList) ");
				buildVertical
						.append(" AND SALES_ORGANIZATION IN (:salesList) ");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND HDR.PURCHASE_ORGANIZATION IN (:purcList) ");
				buildHdr.append(
						" AND HDR.PURCHASE_ORGANIZATION IN (:purcList) ");
				buildVertical
						.append(" AND PURCHASE_ORGANIZATION IN (:purcList) ");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND HDR.DISTRIBUTION_CHANNEL IN (:distList) ");
				buildHdr.append(
						" AND HDR.DISTRIBUTION_CHANNEL IN (:distList) ");
				buildVertical
						.append(" AND DISTRIBUTION_CHANNEL IN (:distList) ");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND HDR.DIVISION IN (:divisionList) ");
				buildHdr.append(" AND HDR.DIVISION IN (:divisionList) ");
				buildVertical.append(" AND DIVISION IN (:divisionList) ");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND HDR.LOCATION IN (:locationList) ");
				buildHdr.append(" AND HDR.LOCATION IN (:locationList) ");
				buildVertical.append(" AND LOCATION IN (:locationList) ");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND HDR.USERACCESS1 IN (:ud1List) ");
				buildHdr.append(" AND HDR.USERACCESS1 IN (:ud1List) ");
				buildVertical.append(" AND USERACCESS1 IN (:ud1List) ");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND HDR.USERACCESS2 IN (:ud2List) ");
				buildHdr.append(" AND HDR.USERACCESS2 IN (:ud2List) ");
				buildVertical.append(" AND USERACCESS2 IN (:ud2List) ");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND HDR.USERACCESS3 IN (:ud3List) ");
				buildHdr.append(" AND HDR.USERACCESS3 IN (:ud3List) ");
				buildVertical.append(" AND USERACCESS3 IN (:ud3List) ");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND HDR.USERACCESS4 IN (:ud4List) ");
				buildHdr.append(" AND HDR.USERACCESS4 IN (:ud4List) ");
				buildVertical.append(" AND USERACCESS4 IN (:ud4List) ");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND HDR.USERACCESS5 IN (:ud5List) ");
				buildHdr.append(" AND HDR.USERACCESS5 IN (:ud5List) ");
				buildVertical.append(" AND USERACCESS5 IN (:ud5List) ");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND HDR.USERACCESS6 IN (:ud6List) ");
				buildHdr.append(" AND HDR.USERACCESS6 IN (:ud6List) ");
				buildVertical.append(" AND USERACCESS6 IN (:ud6List) ");
			}
		}
		if (taxPeriod != 0) {

			build.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod ");
			buildHdr.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod ");
			buildVertical.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			buildQueryGstr1.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");

		}

		String buildQuery = build.toString();
		String buildQueryHdr = buildHdr.toString();
		String buildQuery2 = buildVertical.toString();
		String buildQueryForGstr1 = buildQueryGstr1.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = null;
		if (Boolean.TRUE.equals(isGstr1a)) {
			request.setReturnType(APIConstants.GSTR1A);
		}
		if (!Strings.isNullOrEmpty(request.getReturnType())
				&& APIConstants.GSTR1A
						.equalsIgnoreCase(request.getReturnType())) {
			queryStr = createQueryStringForGstr1a(buildQuery, buildQueryHdr,
					buildQuery2);
		} else {
			 queryStr = createQueryString(buildQuery, buildQueryForGstr1);		

		}
		if (LOGGER.isDebugEnabled()) {

			int chunkSize = 1000;

			if (queryStr.length() > chunkSize) {
				int start = 0;
				while (start < queryStr.length()) {
					// Determine the end index for the current chunk
					int end = Math.min(start + chunkSize, queryStr.length());

					// Log the current chunk
					String queryStrPrint = queryStr.substring(start, end);
					LOGGER.debug("Query Print {} ", queryStrPrint);
					// Move to the next chunk
					start = end;

				}
			} else {
				// If the string is shorter than the chunk size, log it as is
				LOGGER.debug("Executing Query For Sections is -->" + queryStr);
			}
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
			if (docFromDate != null && docToDate != null) {
				q.setParameter("docFromDate", docFromDate);
				q.setParameter("docToDate", docToDate);
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
		obj.setRecords((GenUtil.getBigInteger(arr[1])).intValue());
		obj.setInvValue((BigDecimal) arr[2]);
		obj.setIgst((BigDecimal) arr[3]);
		obj.setCgst((BigDecimal) arr[4]);
		obj.setSgst((BigDecimal) arr[5]);
		obj.setTaxableValue((BigDecimal) arr[6]);
		obj.setTaxPayable((BigDecimal) arr[7]);
		obj.setCess((BigDecimal) arr[8]);
		return obj;
	}

	/**
	 * @param buildQuery
	 * @return
	 */

	private String createQueryString(String buildQuery, String buildQueryGstr1) {
		LOGGER.debug("Outward Query Execution BEGIN ");

		
		// Max is added to identify 1 invoice value/doc amount for a given invoice and tax doc type
		
		String queryStr = "  WITH HDR AS ( SELECT HDR.ID, HDR.DATAORIGINTYPECODE, HDR.DOC_AMT, HDR.DOC_TYPE, " 
				+ "  HDR.DERIVED_RET_PERIOD, SUPPLIER_GSTIN, RETURN_PERIOD, ECOM_GSTIN, DIFF_PERCENT, " 
				+ "  POS FROM ANX_OUTWARD_DOC_HEADER HDR WHERE ASP_INVOICE_STATUS = 2  " 
				+ "  AND COMPLIANCE_APPLICABLE = TRUE AND IS_DELETE = FALSE AND RETURN_TYPE = 'GSTR1' AND  " 
				 
				+ buildQuery
				 
				+ "  ), ITM AS ( SELECT ID, ITM_TAX_DOC_TYPE, IGST_AMT, CGST_AMT, SGST_AMT, CESS_AMT, " 
				+ "  TAXABLE_VALUE, ONB_LINE_ITEM_AMT, LINE_ITEM_AMT, DOC_TYPE, DATAORIGINTYPECODE, " 
				+ "  SUPPLIER_GSTIN, RETURN_PERIOD, ECOM_GSTIN, DIFF_PERCENT, POS, TAX_RATE, " 
				+ "  (CASE WHEN RNK =1 THEN DOC_AMT ELSE 0 END) AS DOC_AMT FROM ( SELECT  " 
				+ "  HDR.ID, ITM.ITM_TAX_DOC_TYPE, IFNULL(ITM.IGST_AMT,0) AS IGST_AMT, " 
				+ "  IFNULL(ITM.CGST_AMT,0) AS CGST_AMT, IFNULL(ITM.SGST_AMT,0) AS SGST_AMT, " 
				+ "  IFNULL(ITM.CESS_AMT_SPECIFIC,0) + IFNULL(ITM.CESS_AMT_ADVALOREM,0) AS CESS_AMT, " 
				+ "  IFNULL(ITM.TAXABLE_VALUE,0) AS TAXABLE_VALUE,  " 
				+ "  IFNULL(ITM.ONB_LINE_ITEM_AMT,0) AS ONB_LINE_ITEM_AMT, " 
				+ "  IFNULL(ITM.LINE_ITEM_AMT,0) AS LINE_ITEM_AMT, HDR.DOC_TYPE, " 
				+ "  IFNULL(HDR.DOC_AMT,0) AS DOC_AMT, HDR.DATAORIGINTYPECODE, " 
				+ "  HDR.SUPPLIER_GSTIN, HDR.RETURN_PERIOD, HDR.ECOM_GSTIN, HDR.DIFF_PERCENT, HDR.POS, " 
				+ "  ITM.TAX_RATE, ROW_NUMBER() OVER (PARTITION BY ITM.DOC_HEADER_ID ORDER BY ITM.ID DESC) RNK " 
				+ "  FROM HDR INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID  " 
				+ "  AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD ) )     " 
				+ "  SELECT TAX_DOC_TYPE, SUM(RECORD_COUNT) RECORD_COUNT,  " 
				+ "  SUM(DOC_AMT) DOC_AMT, SUM(IGST_AMT) IGST_AMT, SUM(CGST_AMT) CGST_AMT,  " 
				+ "  SUM(SGST_AMT) SGST_AMT, SUM(TAXABLE_VALUE) TAXABLE_VALUE,  " 
				+ "  SUM(TAX_PAYABLE) TAX_PAYABLE, SUM(CESS) CESS FROM ( SELECT  " 
				+ "  TAX_DOC_TYPE, SUM(RECORD_COUNT) RECORD_COUNT, SUM(DOC_AMT) DOC_AMT,  " 
				+ "  SUM(IGST_AMT) IGST_AMT, SUM(CGST_AMT) CGST_AMT, SUM(SGST_AMT) SGST_AMT,  " 
				+ "  SUM(TAXABLE_VALUE) TAXABLE_VALUE, SUM(TAX_PAYABLE) TAX_PAYABLE,  " 
				+ "  SUM(CESS) CESS FROM ( SELECT ITM.ITM_TAX_DOC_TYPE AS TAX_DOC_TYPE,  " 
				+ "  COUNT(DISTINCT ID) AS RECORD_COUNT, SUM ( " 
				+ "  CASE WHEN DATAORIGINTYPECODE IN ('A', 'AI', 'B', 'BI') THEN DOC_AMT  " 
				+ "  WHEN DATAORIGINTYPECODE IN ('E', 'EI') THEN LINE_ITEM_AMT END " 
				+ "  ) AS DOC_AMT, SUM(ITM.IGST_AMT)  AS IGST_AMT,  " 
				+ "  SUM(ITM.CGST_AMT)  AS CGST_AMT, SUM(ITM.SGST_AMT)  AS SGST_AMT, " 
				+ "  SUM(ITM.TAXABLE_VALUE) AS TAXABLE_VALUE, " 
				+ "  SUM(ITM.IGST_AMT + ITM.CGST_AMT + ITM.SGST_AMT + ITM.CESS_AMT) AS TAX_PAYABLE,  " 
				+ "  SUM(ITM.CESS_AMT) AS CESS FROM ITM WHERE " 
				+ "  ITM.ITM_TAX_DOC_TYPE IN ( 'B2B', 'B2BA', 'B2CL', 'B2CLA', 'EXPORTS',  " 
				+ "  'EXPORTS-A' ) GROUP BY ITM.ITM_TAX_DOC_TYPE ) GROUP BY  " 
				+ "  TAX_DOC_TYPE UNION ALL SELECT TAX_DOC_TYPE,  " 
				+ "  COUNT(DISTINCT KEY) RECORD_COUNT, SUM(DOC_AMT) DOC_AMT,  " 
				+ "  SUM(IGST_AMT) IGST_AMT, SUM(CGST_AMT) CGST_AMT,  " 
				+ "  SUM(SGST_AMT) SGST_AMT, SUM(TAXABLE_VALUE) TAXABLE_VALUE,  " 
				+ "  SUM(TAX_PAYABLE) TAX_PAYABLE, SUM(CESS) CESS FROM ( " 
				+ "  SELECT ITM.ITM_TAX_DOC_TYPE as TAX_DOC_TYPE, ( " 
				+ "  IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || " 
				+ "  IFNULL(DIFF_PERCENT, '') || '|' || IFNULL(POS, 9999) || '|' || " 
				+ "  IFNULL(ECOM_GSTIN, '') || '|' || IFNULL(TAX_RATE, 9999) ) KEY,  " 
				+ "  SUM(CASE WHEN DOC_TYPE = 'CR' THEN -1*ONB_LINE_ITEM_AMT ELSE ONB_LINE_ITEM_AMT END) AS DOC_AMT, " 
				+ "  SUM(CASE WHEN DOC_TYPE = 'CR' THEN -1*IGST_AMT ELSE IGST_AMT END) AS IGST_AMT, " 
				+ "  SUM(CASE WHEN DOC_TYPE = 'CR' THEN -1*CGST_AMT ELSE CGST_AMT END) AS CGST_AMT, " 
				+ "  SUM(CASE WHEN DOC_TYPE = 'CR' THEN -1*SGST_AMT ELSE SGST_AMT END) AS SGST_AMT, " 
				+ "  SUM(CASE WHEN DOC_TYPE = 'CR' THEN -1*TAXABLE_VALUE ELSE TAXABLE_VALUE END) AS TAXABLE_VALUE, " 
				+ "  SUM(CASE WHEN DOC_TYPE = 'CR' THEN -1*(IGST_AMT + CGST_AMT + SGST_AMT + CESS_AMT) " 
				+ "  ELSE (IGST_AMT + CGST_AMT + SGST_AMT + CESS_AMT) END) AS TAX_PAYABLE, " 
				+ "  SUM(CASE WHEN DOC_TYPE = 'CR' THEN -1*CESS_AMT ELSE CESS_AMT END) AS CESS " 
				+ "  FROM ITM WHERE ITM.ITM_TAX_DOC_TYPE IN ('B2CS') GROUP BY  " 
				+ "  ITM.ITM_TAX_DOC_TYPE, SUPPLIER_GSTIN, RETURN_PERIOD,  " 
				+ "  POS, ECOM_GSTIN, DIFF_PERCENT, TAX_RATE UNION ALL  " 
				+ "  SELECT 'B2CS' AS TAX_DOC_TYPE, ( " 
				+ "  IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || " 
				+ "  IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) || '|' || " 
				+ "  IFNULL(NEW_ECOM_GSTIN, '') || '|' || IFNULL(NEW_RATE, 9999) " 
				+ "  ) KEY, SUM( " 
				+ "  IFNULL(NEW_TAXABLE_VALUE, 0)+ IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) " 
				+ "  ) AS DOC_AMT,  " 
				+ "  IFNULL( SUM(IGST_AMT), 0 ) AS IGST_AMT,  " 
				+ "  IFNULL( SUM(CGST_AMT), 0 ) AS CGST_AMT,  " 
				+ "  IFNULL( SUM(SGST_AMT), 0 ) AS SGST_AMT,  " 
				+ "  IFNULL( SUM(NEW_TAXABLE_VALUE), 0 ) AS TAXABLE_VALUE,  " 
				+ "  SUM( IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) " 
				+ "  ) AS TAX_PAYABLE, IFNULL( SUM(CESS_AMT), 0 ) AS CESS  " 
				+ "  FROM GSTR1_PROCESSED_B2CS HDR WHERE IS_DELETE = FALSE  " 
				+ "  AND IS_AMENDMENT = FALSE  AND "
				 
				+ buildQueryGstr1

				+ "  GROUP BY SUPPLIER_GSTIN, RETURN_PERIOD, TRAN_TYPE,  " 
				+ "  NEW_POS, NEW_ECOM_GSTIN, NEW_RATE ) GROUP BY KEY,  " 
				+ "  TAX_DOC_TYPE UNION ALL SELECT TAX_DOC_TYPE,  " 
				+ "  COUNT(*) RECORD_COUNT, SUM(DOC_AMT) DOC_AMT,  " 
				+ "  SUM(IGST_AMT) IGST_AMT, SUM(CGST_AMT) CGST_AMT,  " 
				+ "  SUM(SGST_AMT) SGST_AMT, SUM(TAXABLE_VALUE) TAXABLE_VALUE,  " 
				+ "  SUM(TAX_PAYABLE) TAX_PAYABLE, SUM(CESS) CESS  " 
				+ "  FROM ( SELECT 'B2CSA' AS TAX_DOC_TYPE, SUM( " 
				+ "  IFNULL(NEW_TAXABLE_VALUE, 0)+ IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ " 
				+ "  IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) ) AS DOC_AMT,  " 
				+ "  IFNULL( SUM(IGST_AMT), 0 ) AS IGST_AMT,  " 
				+ "  IFNULL( SUM(CGST_AMT), 0 ) AS CGST_AMT,  " 
				+ "  IFNULL( SUM(SGST_AMT), 0 ) AS SGST_AMT,  " 
				+ "  IFNULL( SUM(NEW_TAXABLE_VALUE), 0 ) AS TAXABLE_VALUE,  " 
				+ "  SUM( IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) " 
				+ "  ) AS TAX_PAYABLE, IFNULL( SUM(CESS_AMT), 0 ) AS CESS  " 
				+ "  FROM GSTR1_PROCESSED_B2CS HDR WHERE IS_DELETE = FALSE  " 
				+ "  AND IS_AMENDMENT = TRUE  AND " 

				+ buildQueryGstr1
				 
				+ "  GROUP BY SUPPLIER_GSTIN, RETURN_PERIOD,  " 
				+ "  TRAN_TYPE, MONTH, NEW_POS, NEW_ECOM_GSTIN " 
				+ "  ) GROUP BY TAX_DOC_TYPE UNION ALL SELECT  " 
				+ "  TAX_DOC_TYPE, SUM(RECORD_COUNT) RECORD_COUNT,  " 
				+ "  SUM(DOC_AMT) DOC_AMT, SUM(IGST_AMT) IGST_AMT,  " 
				+ "  SUM(CGST_AMT) CGST_AMT, SUM(SGST_AMT) SGST_AMT,  " 
				+ "  SUM(TAXABLE_VALUE) TAXABLE_VALUE,  " 
				+ "  SUM(TAX_PAYABLE) TAX_PAYABLE, SUM(CESS) CESS  " 
				+ "  FROM ( SELECT ITM.ITM_TAX_DOC_TYPE AS TAX_DOC_TYPE,  " 
				+ "  COUNT(DISTINCT ID) AS RECORD_COUNT, SUM ( " 
				+ "  CASE WHEN DATAORIGINTYPECODE IN ('A', 'AI', 'B', 'BI') " 
				+ "  THEN (CASE WHEN DOC_TYPE IN ('CR', 'RCR') THEN -1*DOC_AMT " 
				+ "  ELSE DOC_AMT END) WHEN DATAORIGINTYPECODE IN ('E', 'EI') " 
				+ "  THEN (CASE WHEN DOC_TYPE IN ('CR', 'RCR') THEN -1*LINE_ITEM_AMT " 
				+ "  ELSE LINE_ITEM_AMT END) END ) AS DOC_AMT, " 
				+ "  SUM(CASE WHEN DOC_TYPE IN ('CR', 'RCR') THEN -1*ITM.IGST_AMT ELSE ITM.IGST_AMT END)  AS IGST_AMT,  " 
				+ "  SUM(CASE WHEN DOC_TYPE IN ('CR', 'RCR') THEN -1*ITM.CGST_AMT ELSE ITM.CGST_AMT END)  AS CGST_AMT,  " 
				+ "  SUM(CASE WHEN DOC_TYPE IN ('CR', 'RCR') THEN -1*ITM.SGST_AMT ELSE ITM.SGST_AMT END)  AS SGST_AMT,  " 
				+ "  SUM(CASE WHEN DOC_TYPE IN ('CR', 'RCR') THEN -1*ITM.TAXABLE_VALUE ELSE ITM.TAXABLE_VALUE END) AS TAXABLE_VALUE, " 
				+ "  SUM(CASE WHEN DOC_TYPE IN ('CR', 'RCR') THEN -1*(ITM.IGST_AMT + " 
				+ "  ITM.CGST_AMT + ITM.SGST_AMT + ITM.CESS_AMT) ELSE (ITM.IGST_AMT + " 
				+ "  ITM.CGST_AMT + ITM.SGST_AMT + ITM.CESS_AMT) END)  AS TAX_PAYABLE,  " 
				+ "  SUM(CASE WHEN DOC_TYPE IN ('CR', 'RCR') THEN -1*ITM.CESS_AMT ELSE ITM.CESS_AMT END) AS CESS " 
				+ "  FROM ITM WHERE ITM.ITM_TAX_DOC_TYPE IN ( " 
				+ "  'CDNR', 'CDNRA', 'CDNUR', 'CDNUR-EXPORTS',  " 
				+ "  'CDNUR-B2CL', 'CDNURA' )  " 
				+ "  GROUP BY ITM.ITM_TAX_DOC_TYPE )  " 
				+ "  GROUP BY TAX_DOC_TYPE ) GROUP BY TAX_DOC_TYPE " ;
		LOGGER.debug("Outward FROM B2B TO EXPA Query Execution END ");

		return queryStr;
	}

	private static String createQueryStringForGstr1a(String buildQuery,
			String buildQueryHdr, String buildQuery2) {

		LOGGER.debug("Outward Query Execution BEGIN for GSTR1A ");

		String queryStr = "SELECT TAX_DOC_TYPE,SUM(RECORD_COUNT) RECORD_COUNT,"
				+ "SUM(DOC_AMT) DOC_AMT,SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
				+ "SUM(SGST_AMT) SGST_AMT,SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
				+ "SUM(TAX_PAYABLE) TAX_PAYABLE,SUM(CESS) CESS "
				+ "FROM ( SELECT TAX_DOC_TYPE,SUM(RECORD_COUNT) RECORD_COUNT,"
				+ "SUM(DOC_AMT)DOC_AMT,SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
				+ "SUM(SGST_AMT) SGST_AMT,SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
				+ "SUM(TAX_PAYABLE) TAX_PAYABLE,SUM(CESS) CESS "
				+ "FROM ( SELECT TAX_DOC_TYPE TAX_DOC_TYPE,"
				+ "COUNT(DISTINCT ID) AS RECORD_COUNT,"
				+ "IFNULL(SUM(DOC_AMT),0) AS DOC_AMT,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
				+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
				+ "IFNULL(SUM(TAXABLE_VALUE),0)  AS TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0)) AS TAX_PAYABLE,"
				+ "SUM(IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0)) AS CESS "
				+ "FROM ANX_OUTWARD_DOC_HEADER_1A HDR "
				+ "WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE "
				+ "AND IS_DELETE = FALSE "
				+ "AND RETURN_TYPE='GSTR1A' AND TAX_DOC_TYPE IN "
				+ "('B2B','B2BA','B2CL','B2CLA','EXPORTS','EXPORTS-A') AND "
				+ buildQuery + " GROUP BY TAX_DOC_TYPE ) "
				+ "GROUP BY TAX_DOC_TYPE " + "UNION ALL "
				+ "SELECT TAX_DOC_TYPE,COUNT(DISTINCT KEY) RECORD_COUNT,"
				+ "SUM(DOC_AMT) DOC_AMT,SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT)CGST_AMT,"
				+ "SUM(SGST_AMT)SGST_AMT,SUM(TAXABLE_VALUE)TAXABLE_VALUE,"
				+ "SUM(TAX_PAYABLE) TAX_PAYABLE,SUM(CESS) CESS FROM ( "
				+ "SELECT HDR.TAX_DOC_TYPE as TAX_DOC_TYPE,"
				+ "(IFNULL(SUPPLIER_GSTIN,'') ||'|'|| IFNULL(HDR.RETURN_PERIOD,'')  "
				+ "||'|'||IFNULL(HDR.DIFF_PERCENT,'')  ||'|'||IFNULL(HDR.POS,9999)||'|'||IFNULL(HDR.ECOM_GSTIN,'') "
				+ "||'|'||IFNULL(TAX_RATE,9999)) KEY,IFNULL(SUM(CASE WHEN "
				+ "DOC_TYPE IN ('INV') THEN ONB_LINE_ITEM_AMT END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR') THEN ONB_LINE_ITEM_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ONB_LINE_ITEM_AMT END),0) AS DOC_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN ITM.IGST_AMT END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR') THEN ITM.IGST_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.IGST_AMT END),0) AS  IGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN ITM.CGST_AMT END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR') THEN ITM.CGST_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.CGST_AMT END),0) AS  CGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN ITM.SGST_AMT END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR') THEN ITM.SGST_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.SGST_AMT END),0) AS  SGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN ITM.TAXABLE_VALUE END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR') THEN ITM.TAXABLE_VALUE END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.TAXABLE_VALUE END),0) AS  TAXABLE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN "
				+ "IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+IFNULL(ITM.SGST_AMT,0)+"
				+ "IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR') THEN "
				+ "IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+IFNULL(ITM.SGST_AMT,0)+"
				+ "IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN "
				+ "IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+IFNULL(ITM.SGST_AMT,0)+"
				+ "IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0) END),0) AS TAX_PAYABLE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN "
				+ "IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR') THEN "
				+ "IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN "
				+ "IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0) END),0) AS CESS "
				+ "FROM ANX_OUTWARD_DOC_HEADER_1A HDR "
				+ "INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM "
				+ "ON HDR.ID=ITM.DOC_HEADER_ID WHERE ASP_INVOICE_STATUS = 2 "
				+ "AND COMPLIANCE_APPLICABLE=TRUE AND RETURN_TYPE='GSTR1A' AND "
				+ "IS_DELETE = FALSE AND HDR.TAX_DOC_TYPE IN ('B2CS') AND "
				+ buildQueryHdr
				+ " GROUP BY HDR.TAX_DOC_TYPE,SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "HDR.POS,HDR.ECOM_GSTIN,HDR.DIFF_PERCENT,ITM.TAX_RATE  "
				+ "UNION ALL " + "SELECT 'B2CS' AS TAX_DOC_TYPE,"
				+ "(IFNULL(SUPPLIER_GSTIN,'') ||'|'|| IFNULL(RETURN_PERIOD,'') "
				+ "||'|'||IFNULL(TRAN_TYPE,'')  ||'|'||IFNULL(NEW_POS,9999)||'|'||IFNULL(NEW_ECOM_GSTIN,'') "
				+ "||'|'||IFNULL(NEW_RATE,9999)) KEY,"
				+ "SUM(IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) "
				+ "AS DOC_AMT,IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
				+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
				+ "IFNULL(SUM(NEW_TAXABLE_VALUE),0)  AS TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT,0)) AS TAX_PAYABLE,"
				+ "IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM GSTR1A_PROCESSED_B2CS "
				+ "WHERE IS_DELETE = FALSE AND IS_AMENDMENT=FALSE AND "
				+ buildQuery2 + " GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,"
				+ "TRAN_TYPE,NEW_POS,NEW_ECOM_GSTIN,NEW_RATE ) "
				+ "GROUP BY KEY,TAX_DOC_TYPE " + "UNION ALL "
				+ "SELECT TAX_DOC_TYPE,COUNT(*) RECORD_COUNT,"
				+ "SUM(DOC_AMT) DOC_AMT,SUM(IGST_AMT) IGST_AMT,"
				+ "SUM(CGST_AMT) CGST_AMT,"
				+ "SUM(SGST_AMT) SGST_AMT,SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
				+ "SUM(TAX_PAYABLE) TAX_PAYABLE,SUM(CESS) CESS "
				+ "FROM ( SELECT 'B2CSA' AS TAX_DOC_TYPE,"
				+ "SUM(IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) "
				+ "AS DOC_AMT,IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
				+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
				+ "IFNULL(SUM(NEW_TAXABLE_VALUE),0)  AS TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TAX_PAYABLE,"
				+ "IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM GSTR1A_PROCESSED_B2CS "
				+ "WHERE IS_DELETE = FALSE AND IS_AMENDMENT=TRUE AND "
				+ buildQuery2
				+ " GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,"
				+ "MONTH,NEW_POS,NEW_ECOM_GSTIN)" + "GROUP BY TAX_DOC_TYPE "
				+ "UNION ALL "
				+ "SELECT TAX_DOC_TYPE,SUM(RECORD_COUNT) RECORD_COUNT,"
				+ "SUM(DOC_AMT) DOC_AMT,SUM(IGST_AMT) IGST_AMT,"
				+ "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT,"
				+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,SUM(TAX_PAYABLE) TAX_PAYABLE,"
				+ "SUM(CESS) CESS " + "FROM ( SELECT TAX_DOC_TYPE,"
				+ "COUNT(*) AS RECORD_COUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN DOC_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN DOC_AMT END),0) "
				+ "AS DOC_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN IGST_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN IGST_AMT END),0) "
				+ "AS IGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN CGST_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN CGST_AMT END),0) "
				+ "AS CGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN SGST_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN SGST_AMT END),0) "
				+ "AS SGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN TAXABLE_VALUE END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN TAXABLE_VALUE END),0) "
				+ "AS TAXABLE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN "
				+ "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0) "
				+ "AS TAX_PAYABLE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN "
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0) "
				+ "AS CESS " + "FROM ANX_OUTWARD_DOC_HEADER_1A HDR "
				+ "WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE "
				+ "AND IS_DELETE = FALSE "
				+ "AND RETURN_TYPE='GSTR1A' AND TAX_DOC_TYPE IN "
				+ "('CDNR','CDNRA','CDNUR','CDNUR-EXPORTS','CDNUR-B2CL','CDNURA') AND "
				+ buildQuery + " GROUP BY TAX_DOC_TYPE) GROUP BY TAX_DOC_TYPE) "
				+ "GROUP BY TAX_DOC_TYPE ";

		LOGGER.debug("Outward FROM B2B TO EXPA Query Execution END ");
		return queryStr;

	}

}
