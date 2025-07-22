/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
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

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Component("BasicGstr1AEcomSupScreenSectionDaoImpl")
public class BasicGstr1AEcomSupScreenSectionDaoImpl
		implements BasicGstr1ADocSummaryScreenSectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	@Override
	public List<Gstr1SummarySectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub
		int taxPeriod = 0;
		int taxPeriodFrom = 0;
		int taxPeriodTo = 0;

//		if (Strings.isNullOrEmpty(request.getReturnType())) {
//			request.setReturnType(APIConstants.GSTR1.toUpperCase());
//		}

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		if (req.getTaxPeriod() != null) {
			String taxPeriodReq = req.getTaxPeriod();
			taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		} else {
			String taxPeriodFromReq = req.getTaxPeriodFrom();
			String taxPeriodToReq = req.getTaxPeriodTo();
			taxPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFromReq);
			taxPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodToReq);
		}
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

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" SUPPLIER_GSTIN IN (:gstinList) ");
				buildHdr.append(" HDR.SUPPLIER_GSTIN IN (:gstinList) ");
				buildVertical.append(" SUPPLIER_GSTIN IN (:gstinList) ");
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
				build.append(" AND PROFIT_CENTRE IN (:pcList) ");
				buildHdr.append(" AND HDR.PROFIT_CENTRE IN (:pcList) ");
				buildVertical.append(" AND PROFIT_CENTRE IN (:pcList) ");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND PLANT_CODE IN (:plantList) ");
				buildHdr.append(" AND HDR.PLANT_CODE IN (:plantList) ");
				buildVertical.append(" AND PLANT_CODE IN (:plantList) ");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND SALES_ORGANIZATION IN (:salesList) ");
				buildHdr.append(" AND HDR.SALES_ORGANIZATION IN (:salesList) ");
				buildVertical
						.append(" AND SALES_ORGANIZATION IN (:salesList) ");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND PURCHASE_ORGANIZATION IN (:purcList) ");
				buildHdr.append(
						" AND HDR.PURCHASE_ORGANIZATION IN (:purcList) ");
				buildVertical
						.append(" AND PURCHASE_ORGANIZATION IN (:purcList) ");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND DISTRIBUTION_CHANNEL IN (:distList) ");
				buildHdr.append(
						" AND HDR.DISTRIBUTION_CHANNEL IN (:distList) ");
				buildVertical
						.append(" AND DISTRIBUTION_CHANNEL IN (:distList) ");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND DIVISION IN (:divisionList) ");
				buildHdr.append(" AND HDR.DIVISION IN (:divisionList) ");
				buildVertical.append(" AND DIVISION IN (:divisionList) ");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND LOCATION IN (:locationList) ");
				buildHdr.append(" AND HDR.LOCATION IN (:locationList) ");
				buildVertical.append(" AND LOCATION IN (:locationList) ");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND USERACCESS1 IN (:ud1List) ");
				buildHdr.append(" AND HDR.USERACCESS1 IN (:ud1List) ");
				buildVertical.append(" AND USERACCESS1 IN (:ud1List) ");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND USERACCESS2 IN (:ud2List) ");
				buildHdr.append(" AND HDR.USERACCESS2 IN (:ud2List) ");
				buildVertical.append(" AND USERACCESS2 IN (:ud2List) ");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND USERACCESS3 IN (:ud3List) ");
				buildHdr.append(" AND HDR.USERACCESS3 IN (:ud3List) ");
				buildVertical.append(" AND USERACCESS3 IN (:ud3List) ");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND USERACCESS4 IN (:ud4List) ");
				buildHdr.append(" AND HDR.USERACCESS4 IN (:ud4List) ");
				buildVertical.append(" AND USERACCESS4 IN (:ud4List) ");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND USERACCESS5 IN (:ud5List) ");
				buildHdr.append(" AND HDR.USERACCESS5 IN (:ud5List) ");
				buildVertical.append(" AND USERACCESS5 IN (:ud5List) ");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND USERACCESS6 IN (:ud6List) ");
				buildHdr.append(" AND HDR.USERACCESS6 IN (:ud6List) ");
				buildVertical.append(" AND USERACCESS6 IN (:ud6List) ");
			}
		}
		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			buildHdr.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod ");
			buildVertical.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}
		if (taxPeriodFrom != 0 && taxPeriodTo != 0) {
			/*
			 * buildHeader.append(" AND REC.DERIVED_RET_PERIOD BETWEEN ");
			 * buildHeader.append(":taxPeriodFrom AND :taxPeriodTo");
			 */

			build.append(
					" AND DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");
			buildHdr.append(
					" AND HDR.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");
			buildVertical.append(
					" AND DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");

		}

		String buildQuery = build.toString();
		String buildQueryHdr = buildHdr.toString();
		String buildQuery2 = buildVertical.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

//		String multiSupplyTypeAns = groupConfigPrmtRepository
//				.findAnswerForMultiSupplyType();

		LOGGER.debug(" req {} ", req);
		LOGGER.debug(" request {} ", request);
		String queryStr = null;

			LOGGER.debug(" INSIDE GSTR1A block ");
			queryStr = createGstr1AQueryString(buildQuery, buildQueryHdr,
					buildQuery2);


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
			if (taxPeriodFrom != 0 && taxPeriodTo != 0) {
				q.setParameter("taxPeriodFrom", taxPeriodFrom);
				q.setParameter("taxPeriodTo", taxPeriodTo);
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
		obj.setTaxableValue((BigDecimal) arr[2]);
		obj.setIgst((BigDecimal) arr[3]);
		obj.setSgst((BigDecimal) arr[4]);
		obj.setCgst((BigDecimal) arr[5]);
		obj.setCess((BigDecimal) arr[6]);
		return obj;
	}


	// TODO
	private static String createGstr1AQueryString(String buildQuery1,
			String buildQueryHdr, String buildQuery2) {

		String queryStr = "SELECT TABLE_SECTION, COUNT(DISTINCT ECOM_GSTIN) RECORD_COUNT, IFNULL( SUM( "
				+ " CASE WHEN DOC_TYPE IN ('INV') THEN TAXABLE_VALUE END ),"
				+ " 0 )+ IFNULL( SUM( CASE WHEN DOC_TYPE IN ('DR') THEN TAXABLE_VALUE END ), "
				+ " 0 )- IFNULL( SUM( CASE WHEN DOC_TYPE IN ('CR') THEN TAXABLE_VALUE END ),  0 "
				+ " ) AS TAXABLE_VALUE, IFNULL( SUM( "
				+ " CASE WHEN DOC_TYPE IN ('INV') THEN IGST_AMT END ), 0)+ IFNULL( "
				+ " SUM( CASE WHEN DOC_TYPE IN ('DR') THEN IGST_AMT END ), "
				+ "  0 )- IFNULL( SUM( CASE WHEN DOC_TYPE IN ('CR') THEN IGST_AMT END ), 0 "
				+ " ) AS IGST_AMT,  IFNULL( SUM( CASE WHEN DOC_TYPE IN ('INV') THEN SGST_AMT END "
				+ " ), 0 )+ IFNULL( SUM( CASE WHEN DOC_TYPE IN ('DR') THEN SGST_AMT END ), "
				+ " 0 )- IFNULL( SUM( CASE WHEN DOC_TYPE IN ('CR') THEN SGST_AMT END "
				+ " ), 0 ) AS SGST_AMT, IFNULL( SUM( CASE WHEN DOC_TYPE IN ('INV') THEN CGST_AMT END ), 0 )+ "
				+ " IFNULL( SUM( CASE WHEN DOC_TYPE IN ('DR') THEN CGST_AMT END ), 0 )-  "
				+ " IFNULL( SUM( CASE WHEN DOC_TYPE IN ('CR') THEN CGST_AMT END ),  0 ) AS CGST_AMT, "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN CESS_AMT END ),  0 )+  "
				+ " IFNULL( SUM( CASE WHEN DOC_TYPE IN ('DR') THEN CESS_AMT END ), 0 )-  "
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN CESS_AMT END ), 0 ) AS CESS_AMT "
				+ " FROM (SELECT CASE WHEN HDR.TABLE_SECTION IN ('4A','5A','7A(1)','7B(1)','9B','6B','6C') AND "
				+ "HDR.TCS_FLAG = 'Y' AND IFNULL(HDR.ECOM_GSTIN,'')<>'' THEN '14(i)' "
				+ "ELSE TABLE_SECTION END AS TABLE_SECTION, "
				+ " HDR.ECOM_GSTIN, DOC_TYPE, HDR.TCS_FLAG,  "
				+ "  IFNULL(CASE WHEN HDR.TCS_FLAG = 'Y' AND HDR.TABLE_SECTION IN ('4A','5A','7A(1)','7B(1)','9B','6B','6C')  "
				+ " AND IFNULL(HDR.ECOM_GSTIN,'')<>'' THEN ITM.TAXABLE_VALUE WHEN HDR.TCS_FLAG = 'E'  "
				+ " AND HDR.TABLE_SECTION = '14(ii)' THEN ITM.TAXABLE_VALUE ELSE 0 END,0) AS TAXABLE_VALUE,  "
				+ " IFNULL(CASE WHEN HDR.TCS_FLAG = 'Y'  AND HDR.TABLE_SECTION IN ('4A','5A','7A(1)','7B(1)','9B','6B','6C')  "
				+ " AND IFNULL(HDR.ECOM_GSTIN,'')<>'' THEN ITM.IGST_AMT WHEN HDR.TCS_FLAG = 'E'  "
				+ " AND HDR.TABLE_SECTION = '14(ii)' THEN ITM.IGST_AMT ELSE 0 END,0) AS IGST_AMT,  "
				+ " IFNULL(CASE WHEN HDR.TCS_FLAG = 'Y' AND HDR.TABLE_SECTION IN ('4A','5A','7A(1)','7B(1)','9B','6B','6C')  "
				+ " AND IFNULL(HDR.ECOM_GSTIN,'')<>'' THEN ITM.SGST_AMT WHEN HDR.TCS_FLAG = 'E'  "
				+ "  AND HDR.TABLE_SECTION = '14(ii)' THEN ITM.SGST_AMT ELSE 0 END,0) AS SGST_AMT,  "
				+ "  IFNULL(CASE WHEN HDR.TCS_FLAG = 'Y' AND HDR.TABLE_SECTION IN ('4A','5A','7A(1)','7B(1)','9B','6B','6C') "
				+ " AND IFNULL(HDR.ECOM_GSTIN,'')<>'' THEN ITM.CGST_AMT WHEN HDR.TCS_FLAG = 'E'  "
				+ " AND HDR.TABLE_SECTION = '14(ii)' THEN ITM.CGST_AMT ELSE 0 END,0) AS CGST_AMT,  "
				+ " IFNULL(IFNULL(CASE WHEN HDR.TCS_FLAG = 'Y' "
				+ " AND HDR.TABLE_SECTION IN ('4A','5A','7A(1)','7B(1)','9B','6B','6C') "
				+ " AND IFNULL(HDR.ECOM_GSTIN,'')<>'' THEN ITM.CESS_AMT_SPECIFIC  "
				+ "  WHEN HDR.TCS_FLAG = 'E' AND HDR.TABLE_SECTION = '14(ii)'  "
				+ " THEN ITM.CESS_AMT_SPECIFIC ELSE 0 END,0)+ IFNULL(CASE WHEN HDR.TCS_FLAG = 'Y' AND  "
				+ " HDR.TABLE_SECTION IN ('4A','5A','7A(1)','7B(1)','9B','6B','6C')  "
				+ " AND IFNULL(HDR.ECOM_GSTIN,'')<>'' THEN ITM.CESS_AMT_ADVALOREM WHEN HDR.TCS_FLAG = 'E'  "
				+ " AND HDR.TABLE_SECTION = '14(ii)' "
				+ " THEN ITM.CESS_AMT_ADVALOREM ELSE 0 END,0) ,0 ) AS CESS_AMT  "
				+ " FROM ANX_OUTWARD_DOC_HEADER_1A HDR INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM  "
				+ " ON HDR.ID = ITM.DOC_HEADER_ID WHERE ASP_INVOICE_STATUS = 2  "
				+ " AND COMPLIANCE_APPLICABLE = TRUE  "
				+ " AND RETURN_TYPE = 'GSTR1A'  " + " AND IS_DELETE = FALSE  "
				+ " AND IS_PROCESSED = TRUE  "
				+ " AND HDR.TABLE_SECTION IN ('4A','5A','7A(1)','7B(1)','9B','6B','6C', '14(ii)') AND "
				+ buildQueryHdr + " AND DOC_TYPE IN ('INV', 'DR', 'CR') ) "
				+ "  WHERE TABLE_SECTION IN ('14(i)','14(ii)')  GROUP BY TABLE_SECTION "
				+ " UNION ALL"
				+ " SELECT HDR.TABLE_SECTION, COUNT( DISTINCT CASE WHEN HDR.TABLE_SECTION = '15(i)' THEN DOC_KEY WHEN HDR.TABLE_SECTION = '15(ii)' THEN IFNULL(HDR.ECOM_GSTIN, '')|| '|' || IFNULL(HDR.POS, '')|| '|' || IFNULL(ITM.TAX_RATE, 9999) WHEN HDR.TABLE_SECTION = '15(iii)' THEN DOC_KEY WHEN HDR.TABLE_SECTION = '15(iv)' THEN IFNULL(HDR.POS, '')|| '|' || IFNULL(ITM.TAX_RATE, 9999) END ) AS RECORD_COUNT, "
				+ "SUM( IFNULL( CASE WHEN HDR.DOC_TYPE = 'CR' THEN -1 * ITM.TAXABLE_VALUE ELSE ITM.TAXABLE_VALUE END, 0 ) ) AS TAXABLE_VALUE,"
				+ " SUM( IFNULL( CASE WHEN HDR.DOC_TYPE = 'CR' THEN -1 * ITM.IGST_AMT ELSE ITM.IGST_AMT END, 0 ) ) AS IGST_AMT,"
				+ " SUM( IFNULL( CASE WHEN HDR.DOC_TYPE = 'CR' THEN -1 * ITM.SGST_AMT ELSE ITM.SGST_AMT END, 0 ) ) AS SGST_AMT,"
				+ " SUM( IFNULL( CASE WHEN HDR.DOC_TYPE = 'CR' THEN -1 * ITM.CGST_AMT ELSE ITM.CGST_AMT END, 0 ) ) AS CGST_AMT,"
				+ " SUM( IFNULL( CASE WHEN HDR.DOC_TYPE = 'CR' THEN -1 * ITM.CESS_AMT_SPECIFIC ELSE ITM.CESS_AMT_SPECIFIC END, 0 )+ IFNULL( CASE WHEN DOC_TYPE = 'CR' THEN -1 * ITM.CESS_AMT_ADVALOREM ELSE ITM.CESS_AMT_ADVALOREM END, 0 ) ) AS CESS_AMT "
				+ "FROM ANX_OUTWARD_DOC_HEADER_1A HDR INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM "
				+ "ON HDR.ID = ITM.DOC_HEADER_ID WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE "
				+ "AND RETURN_TYPE = 'GSTR1A' AND IS_DELETE = FALSE AND IS_PROCESSED = TRUE "
				+ "AND HDR.TABLE_SECTION IN ( '15(i)', '15(ii)', '15(iii)', '15(iv)' ) AND "
				+ buildQueryHdr + " GROUP BY TABLE_SECTION "
				+ "UNION ALL SELECT '15' AS TABLE_SECTION, "
				+ "COUNT(DISTINCT CASE WHEN HDR.TABLE_SECTION = '15(i)' THEN DOC_KEY WHEN HDR.TABLE_SECTION = '15(ii)' THEN IFNULL(HDR.ECOM_GSTIN, '')|| '|' || IFNULL(HDR.POS, '')|| '|' || IFNULL(ITM.TAX_RATE, 9999) WHEN HDR.TABLE_SECTION = '15(iii)' THEN DOC_KEY WHEN HDR.TABLE_SECTION = '15(iv)' THEN IFNULL(HDR.POS, '')|| '|' || IFNULL(ITM.TAX_RATE, 9999) END) AS RECORD_COUNT,"
				+ " SUM(IFNULL(CASE WHEN HDR.DOC_TYPE='CR' THEN -1*ITM.TAXABLE_VALUE ELSE ITM.TAXABLE_VALUE END, 0)) AS TAXABLE_VALUE,"
				+ " SUM(IFNULL(CASE WHEN HDR.DOC_TYPE='CR' THEN -1*ITM.IGST_AMT ELSE ITM.IGST_AMT END, 0)) AS IGST_AMT, SUM(IFNULL(CASE WHEN HDR.DOC_TYPE='CR' THEN -1*ITM.SGST_AMT ELSE ITM.SGST_AMT END, 0)) AS SGST_AMT,"
				+ " SUM(IFNULL(CASE WHEN HDR.DOC_TYPE='CR' THEN -1*ITM.CGST_AMT ELSE ITM.CGST_AMT END, 0)) AS CGST_AMT,"
				+ " SUM(IFNULL(CASE WHEN HDR.DOC_TYPE='CR' THEN -1*ITM.CESS_AMT_SPECIFIC ELSE ITM.CESS_AMT_SPECIFIC END, 0)+IFNULL(CASE WHEN DOC_TYPE='CR' THEN -1*ITM.CESS_AMT_ADVALOREM ELSE ITM.CESS_AMT_ADVALOREM END, 0)) AS CESS_AMT "
				+ "FROM ANX_OUTWARD_DOC_HEADER_1A HDR INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM ON HDR.ID = ITM.DOC_HEADER_ID WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND RETURN_TYPE = 'GSTR1A' AND IS_DELETE = FALSE AND IS_PROCESSED = TRUE AND "
				+ "HDR.TABLE_SECTION IN ( '15(i)', '15(ii)', '15(iii)', '15(iv)' ) AND "
				+ buildQueryHdr + "UNION ALL SELECT '14' AS TABLE_SECTION, "
				+ "COUNT(DISTINCT ECOM_GSTIN) RECORD_COUNT, + IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN TAXABLE_VALUE END), 0)+ IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR') THEN TAXABLE_VALUE END),0)- IFNULL( "
				+ "SUM(CASE WHEN DOC_TYPE IN ('CR') THEN TAXABLE_VALUE END), 0) AS TAXABLE_VALUE, IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN IGST_AMT END), 0)+ IFNULL( "
				+ "SUM(CASE WHEN DOC_TYPE IN ('DR') THEN IGST_AMT END), 0)- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN IGST_AMT END), 0) AS IGST_AMT, IFNULL( "
				+ "SUM(CASE WHEN DOC_TYPE IN ('INV') THEN SGST_AMT END), 0)+ IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR') THEN SGST_AMT END), 0)- IFNULL( "
				+ "SUM(CASE WHEN DOC_TYPE IN ('CR') THEN SGST_AMT END), 0) AS SGST_AMT, IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN CGST_AMT END), 0)+ IFNULL( "
				+ "SUM(CASE WHEN DOC_TYPE IN ('DR') THEN CGST_AMT END), 0)- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN CGST_AMT END), 0) AS CGST_AMT, IFNULL( "
				+ "SUM(CASE WHEN DOC_TYPE IN ('INV') THEN CESS_AMT END), 0)+ IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR') THEN CESS_AMT END), 0)- IFNULL( "
				+ "SUM(CASE WHEN DOC_TYPE IN ('CR') THEN CESS_AMT END), 0) AS CESS_AMT FROM (SELECT CASE WHEN HDR.TABLE_SECTION IN ('4A', '5A', '7A(1)', '7B(1)', '9B', '6B', '6C') "
				+ "AND HDR.TCS_FLAG = 'Y' AND IFNULL(HDR.ECOM_GSTIN, '')<> '' THEN '14(i)' ELSE TABLE_SECTION END AS TABLE_SECTION, HDR.ECOM_GSTIN, DOC_TYPE, HDR.TCS_FLAG, "
				+ "IFNULL(CASE WHEN HDR.TCS_FLAG = 'Y' AND HDR.TABLE_SECTION IN ('4A', '5A', '7A(1)', '7B(1)', '9B', '6B', '6C') AND IFNULL(HDR.ECOM_GSTIN, '')<> '' THEN ITM.TAXABLE_VALUE WHEN HDR.TCS_FLAG = 'E' "
				+ "AND HDR.TABLE_SECTION = '14(ii)' THEN ITM.TAXABLE_VALUE ELSE 0 END, 0) AS TAXABLE_VALUE, IFNULL(CASE WHEN HDR.TCS_FLAG = 'Y' AND HDR.TABLE_SECTION IN ('4A', '5A', '7A(1)', '7B(1)', '9B', '6B', '6C') "
				+ "AND IFNULL(HDR.ECOM_GSTIN, '')<> '' THEN ITM.IGST_AMT WHEN HDR.TCS_FLAG = 'E' AND HDR.TABLE_SECTION = '14(ii)' THEN ITM.IGST_AMT ELSE 0 END, 0) AS IGST_AMT, "
				+ "IFNULL(CASE WHEN HDR.TCS_FLAG = 'Y' AND HDR.TABLE_SECTION IN ('4A', '5A', '7A(1)', '7B(1)', '9B', '6B', '6C') AND IFNULL(HDR.ECOM_GSTIN, '')<> '' THEN ITM.SGST_AMT WHEN HDR.TCS_FLAG = 'E' "
				+ "AND HDR.TABLE_SECTION = '14(ii)' THEN ITM.SGST_AMT ELSE 0 END, 0) AS SGST_AMT, IFNULL(CASE WHEN HDR.TCS_FLAG = 'Y' AND HDR.TABLE_SECTION IN ('4A', '5A', '7A(1)', '7B(1)', '9B', '6B', '6C') "
				+ "AND IFNULL(HDR.ECOM_GSTIN, '')<> '' THEN ITM.CGST_AMT WHEN HDR.TCS_FLAG = 'E' AND HDR.TABLE_SECTION = '14(ii)' THEN ITM.CGST_AMT ELSE 0 END, 0) AS CGST_AMT, "
				+ "IFNULL(IFNULL(CASE WHEN HDR.TCS_FLAG = 'Y' AND HDR.TABLE_SECTION IN ('4A', '5A', '7A(1)', '7B(1)', '9B', '6B', '6C') AND IFNULL(HDR.ECOM_GSTIN, '')<> '' THEN ITM.CESS_AMT_SPECIFIC WHEN HDR.TCS_FLAG = 'E' "
				+ "AND HDR.TABLE_SECTION = '14(ii)' THEN ITM.CESS_AMT_SPECIFIC ELSE 0 END, 0)+ IFNULL(CASE WHEN HDR.TCS_FLAG = 'Y' AND HDR.TABLE_SECTION IN ('4A', '5A', '7A(1)', '7B(1)', '9B', '6B', '6C') "
				+ "AND IFNULL(HDR.ECOM_GSTIN, '')<> '' THEN ITM.CESS_AMT_ADVALOREM WHEN HDR.TCS_FLAG = 'E' AND HDR.TABLE_SECTION = '14(ii)' THEN ITM.CESS_AMT_ADVALOREM ELSE 0 END, 0), 0) AS CESS_AMT "
				+ "FROM ANX_OUTWARD_DOC_HEADER_1A HDR INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM ON HDR.ID = ITM.DOC_HEADER_ID WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE "
				+ "AND RETURN_TYPE = 'GSTR1A' AND IS_DELETE = FALSE AND IS_PROCESSED = TRUE AND HDR.TABLE_SECTION IN ('4A', '5A', '7A(1)', '7B(1)', '9B', '6B', '6C', '14(ii)') AND "
				+ buildQueryHdr + "AND DOC_TYPE IN ('INV', 'DR', 'CR'))"
				+ "WHERE TABLE_SECTION IN ('14(i)', '14(ii)')";

		LOGGER.debug("Nil Section Query Execution END ");
		return queryStr;

	}

}
