package com.ey.advisory.app.services.reports.gstr1a;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr1AspVerticalAdvRecDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("Gstr1AAspAdvRecSecTotalSummaryDaoImpl")
public class Gstr1AAspAdvRecSecTotalSummaryDaoImpl implements Gstr1AAdvRecSavableDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AAspAdvRecSecTotalSummaryDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getGstr1AdvRecSavableReports(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();
		String taxDocType = request.getTaxDocType();
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

		StringBuilder buildQueryAdv = new StringBuilder();
		StringBuilder buildQueryAdvHdr = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQueryAdv.append(" AND REC.SUPPLIER_GSTIN IN :gstinList");
				buildQueryAdvHdr
						.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");

			}

		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildQueryAdv.append(" AND REC.PROFIT_CENTRE IN :pcList");
				buildQueryAdvHdr.append(" AND HDR.PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildQueryAdv.append(" AND REC.PLANT_CODE IN :plantList");
				buildQueryAdvHdr.append(" AND HDR.PLANT_CODE IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildQueryAdv
						.append(" AND REC.SALES_ORGANIZATION IN :salesList");
				buildQueryAdvHdr
						.append(" AND HDR.SALES_ORGANIZATION IN :salesList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildQueryAdv
						.append(" AND REC.DISTRIBUTION_CHANNEL IN :distList");
				buildQueryAdvHdr
						.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQueryAdv.append(" AND REC.DIVISION IN :divisionList");
				buildQueryAdvHdr.append(" AND HDR.DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildQueryAdv.append(" AND REC.LOCATION IN :locationList");
				buildQueryAdvHdr.append(" AND HDR.LOCATION IN :locationList");

			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildQueryAdv.append(" AND REC.USERACCESS1 IN :ud1List");
				buildQueryAdvHdr.append(" AND HDR.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {

				buildQueryAdv.append(" AND REC.USERACCESS2 IN :ud2List");
				buildQueryAdvHdr.append(" AND HDR.USERACCESS2 IN :ud2List");

			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {

				buildQueryAdv.append(" AND REC.USERACCESS3 IN :ud3List");
				buildQueryAdvHdr.append(" AND HDR.USERACCESS3 IN :ud3List");

			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQueryAdv.append(" AND REC.USERACCESS4 IN :ud4List");
				buildQueryAdvHdr.append(" AND HDR.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQueryAdv.append(" AND REC.USERACCESS5 IN :ud5List");
				buildQueryAdvHdr.append(" AND HDR.USERACCESS5 IN :ud5List");

			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQueryAdv.append(" AND REC.USERACCESS6 IN :ud6List");
				buildQueryAdvHdr.append(" AND HDR.USERACCESS6 IN :ud6List");
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {

			buildQueryAdv.append(" AND REC.DERIVED_RET_PERIOD = :taxperiod ");
			buildQueryAdvHdr
					.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod ");

		}
		
		if (taxPeriodFrom != null && taxPeriodTo != null) {
			buildQueryAdv.append(" AND REC.DERIVED_RET_PERIOD BETWEEN ");
			buildQueryAdv.append(":taxPeriodFrom AND :taxPeriodTo");
			
			buildQueryAdvHdr
			.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN ");
			buildQueryAdvHdr
			.append(":taxPeriodFrom AND :taxPeriodTo");
			
		}

		String queryStr = createApiProcessedQueryString(
				buildQueryAdv.toString(), buildQueryAdvHdr.toString());
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

		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxperiod", derivedRetPeriod);
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
		return list.parallelStream().map(o -> convertProcessed(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1AspVerticalAdvRecDto convertProcessed(Object[] arr) {
		Gstr1AspVerticalAdvRecDto obj = new Gstr1AspVerticalAdvRecDto();

		obj.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setTransactionType(arr[2] != null ? arr[2].toString() : null);
		obj.setMonth(arr[3] != null ? arr[3].toString() : null);
		obj.setOrgPos(arr[4] != null ? arr[4].toString() : null);
		obj.setOrgRate(arr[5] != null ? arr[5].toString() : null);
		obj.setOrgGrossAdvanceReceived(
				arr[6] != null ? arr[6].toString() : null);
		obj.setNewPOS(arr[7] != null ? arr[7].toString() : null);
		obj.setNewRate(arr[8] != null ? arr[8].toString() : null);
		obj.setNewGrossAdvanceReceived(
				arr[9] != null ? arr[9].toString() : null);
		obj.setIntegratedTaxAmount(arr[10] != null ? arr[10].toString() : null);
		obj.setCentralTaxAmount(arr[11] != null ? arr[11].toString() : null);
		obj.setStateUTTaxAmount(arr[12] != null ? arr[12].toString() : null);
		obj.setCessAmount(arr[13] != null ? arr[13].toString() : null);
		obj.setProfitCentre(arr[14] != null ? arr[14].toString() : null);
		obj.setPlant(arr[15] != null ? arr[15].toString() : null);
		obj.setDivision(arr[16] != null ? arr[16].toString() : null);
		obj.setLocation(arr[17] != null ? arr[17].toString() : null);
		obj.setSalesOrganisation(arr[18] != null ? arr[18].toString() : null);
		obj.setDistributionChannel(arr[19] != null ? arr[19].toString() : null);
		obj.setUserAccess1(arr[20] != null ? arr[20].toString() : null);
		obj.setUserAccess2(arr[21] != null ? arr[21].toString() : null);
		obj.setUserAccess3(arr[22] != null ? arr[22].toString() : null);
		obj.setUserAccess4(arr[23] != null ? arr[23].toString() : null);
		obj.setUserAccess5(arr[24] != null ? arr[24].toString() : null);
		obj.setUserAccess6(arr[25] != null ? arr[25].toString() : null);
		obj.setUserdefinedfield1(arr[26] != null ? arr[26].toString() : null);
		obj.setUserdefinedfield2(arr[27] != null ? arr[27].toString() : null);
		obj.setUserdefinedfield3(arr[28] != null ? arr[28].toString() : null);
		obj.setAspInformationID(arr[29] != null ? arr[29].toString() : null);
		obj.setAspInformationDescription(
				arr[30] != null ? arr[30].toString() : null);
		obj.setSaveStatus(arr[31] != null ? arr[31].toString() : null);
		obj.setgSTNRefID(arr[32] != null ? arr[32].toString() : null);

		if (arr[33] != null) {
			String timestamp = arr[33].toString();
			DateTimeFormatter utcFormatter = DateTimeFormatter
					.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
			LocalDateTime localDateTime = LocalDateTime.parse(timestamp,
					utcFormatter);

			// Assume the timestamp is in a specific time zone, e.g., UTC
			ZonedDateTime utcDateTime = ZonedDateTime.of(localDateTime,
					ZoneId.of("UTC"));

			// Convert to IST (Indian Standard Time)
			ZoneId istZone = ZoneId.of("Asia/Kolkata");
			ZonedDateTime istDateTime = utcDateTime
					.withZoneSameInstant(istZone);

			// Format the IST time as a string
			DateTimeFormatter istFormatter = DateTimeFormatter
					.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
			String istTime = istDateTime.format(istFormatter);
			obj.setgSTNRefIDTime(istTime);
		}
		// obj.setgSTNRefIDTime(arr[33] != null ? arr[33].toString() : null);
		obj.setgSTNErrorcode(arr[34] != null ? arr[34].toString() : null);
		obj.setgSTNErrorDescription(
				arr[35] != null ? arr[35].toString() : null);
		obj.setSourceId(arr[36] != null ? arr[36].toString() : null);
		obj.setFileName(arr[37] != null ? arr[37].toString() : null);
		obj.setAspDateTime(arr[38] != null ? arr[38].toString() : null);

		return obj;
	}

	private String createApiProcessedQueryString(String buildQueryAdv,
			String buildQueryHdr) {

		return "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,(CASE "
				+ "WHEN TRAN_TYPE IN ('ZL65','L65') THEN 'L65' "
				+ "WHEN (TRAN_TYPE IN ('Z','N','') OR TRAN_TYPE IS NULL) THEN 'N' END) "
				+ "TRAN_TYPE,MAX(MONTH)MONTH, "
				+ "MAX(ORG_POS)ORG_POS,MAX(ORG_RATE)ORG_RATE, "
				+ "MAX(ORG_GROSS_ADV_RECEIVED)ORG_GROSS_ADV_RECEIVED, "
				+ "NEW_POS,MAX(NEW_RATE)NEW_RATE, SUM(NEW_GROSS_ADV_RECEIVED)NEW_GROSS_ADV_RECEIVED, "
				+ "SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT, SUM(SGST_AMT)SGST_AMT, "
				+ "SUM(CESS_AMT)CESS_AMT, MAX(PROFIT_CENTRE)PROFIT_CENTRE,"
				+ "MAX(PLANT_CODE)PLANT_CODE, MAX(DIVISION)DIVISION,MAX(LOCATION)LOCATION, "
				+ "MAX(SALES_ORGANIZATION)SALES_ORGANIZATION, "
				+ "MAX(DISTRIBUTION_CHANNEL)DISTRIBUTION_CHANNEL, MAX(USERACCESS1)USERACCESS1, "
				+ "MAX(USERACCESS2)USERACCESS2,MAX(USERACCESS3)USERACCESS3, "
				+ "MAX(USERACCESS4)USERACCESS4, MAX(USERACCESS5)USERACCESS5, "
				+ "MAX(USERACCESS6)USERACCESS6, MAX(USERDEFINED_FIELD1)USERDEFINED_FIELD1,"
				+ "MAX(USERDEFINED_FIELD2)USERDEFINED_FIELD2, "
				+ "MAX(USERDEFINED_FIELD3)USERDEFINED_FIELD3,"
				+ "MAX(INFO_ERROR_CODE_ASP)INFO_ERROR_CODE_ASP, "
				+ "MAX(INFO_ERROR_DESCRIPTION_ASP),MAX(SAVE_STATUS)SAVE_STATUS,"
				+ "MAX(GSTIN_REF_ID)GSTIN_REF_ID, MAX(GSTIN_REF_ID_TIME)GSTIN_REF_ID_TIME,"
				+ "MAX(GSTN_ERROR_CODE)GSTN_ERROR_CODE, "
				+ "MAX(GSTN_ERROR_DESCRIPTION)GSTN_ERROR_DESCRIPTION, "
				+ "MAX(SOURCE_ID)SOURCE_ID, MAX(FILE_NAME)FILE_NAME,"
				+ "MAX(ASP_DATE_TIME)ASP_DATE_TIME "
				+ "FROM ( SELECT REC.SUPPLIER_GSTIN,REC.RETURN_PERIOD, TRAN_TYPE,MONTH, "
				+ "NULL ORG_POS,NULL ORG_RATE,ORG_GROSS_ADV_RECEIVED, NEW_POS,NULL NEW_RATE, "
				+ "NEW_GROSS_ADV_RECEIVED,IGST_AMT,CGST_AMT, SGST_AMT,CESS_AMT, "
				+ " '' PROFIT_CENTRE,''PLANT_CODE,'' DIVISION,''LOCATION, "
				+ " ''SALES_ORGANIZATION,''DISTRIBUTION_CHANNEL, ''USERACCESS1, "
				+ " ''USERACCESS2,''USERACCESS3,''USERACCESS4,''USERACCESS5,"
				+ " ''USERACCESS6, ''USERDEFINED_FIELD1,''USERDEFINED_FIELD2,"
				+ " ''USERDEFINED_FIELD3, '' INFO_ERROR_CODE_ASP, "
				+ " '' INFO_ERROR_DESCRIPTION_ASP, (CASE WHEN REC.IS_SAVED_TO_GSTN = TRUE "
				+ "THEN 'IS_SAVED' WHEN REC.IS_SAVED_TO_GSTN = FALSE AND REC.GSTN_ERROR = TRUE "
				+ "THEN 'IS_ERROR' WHEN REC.IS_SAVED_TO_GSTN = FALSE "
				+ "AND REC.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) AS SAVE_STATUS, "
				+ "GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, "
				+ "GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, '' AS GSTN_ERROR_CODE, "
				+ " '' AS GSTN_ERROR_DESCRIPTION, '' SOURCE_ID, '' FILE_NAME, "
				+ " '' ASP_DATE_TIME FROM GSTR1A_PROCESSED_ADV_RECEIVED REC "
				+ "LEFT OUTER JOIN TF_GSTR1A_ERROR_INFO() GSTRERR "
				+ "ON REC.AS_ENTERED_ID= GSTRERR.COMMON_ID AND "
				+ "REC.FILE_ID=GSTRERR.FILE_ID AND GSTRERR.TABLE_TYPE='ADV REC' "
				+ "AND RETURN_TYPE='GSTR1A' LEFT OUTER JOIN FILE_STATUS FIL "
				+ "ON REC.FILE_ID=FIL.ID AND GSTRERR.FILE_ID=FIL.ID "
				+ "AND GSTRERR.INV_KEY=REC.AT_INVKEY LEFT OUTER JOIN "
				+ "GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = REC.BATCH_ID "
				+ "WHERE REC.IS_DELETE=FALSE AND IS_AMENDMENT=FALSE "
				+ buildQueryAdv + " UNION ALL "
				+ "SELECT HDR.SUPPLIER_GSTIN, HDR.RETURN_PERIOD, ITM.DIFF_PERCENT AS TRAN_TYPE, "
				+ "'' AS MONTH, NULL ORG_POS, NULL ORG_RATE, 0 AS ORG_TAXABLE_VALUE,"
				+ " HDR.POS AS NEW_POS, NULL NEW_RATE, ITM.TAXABLE_VALUE AS NEW_TAXABLE_VALUE, "
				+ "ITM.IGST_AMT AS IGST_AMT, ITM.CGST_AMT AS CGST_AMT, ITM.SGST_AMT AS SGST_AMT,"
				+ " ( ITM.CESS_AMT_SPECIFIC + ITM.CESS_AMT_ADVALOREM ) AS CESS_AMT, HDR.PROFIT_CENTRE, "
				+ "HDR.PLANT_CODE, HDR.DIVISION, HDR.LOCATION, HDR.SALES_ORGANIZATION, "
				+ "HDR.DISTRIBUTION_CHANNEL, HDR.USERACCESS1, HDR.USERACCESS2, HDR.USERACCESS3, HDR.USERACCESS4, "
				+ "HDR.USERACCESS5, HDR.USERACCESS6, '' USERDEFINED_FIELD1, '' USERDEFINED_FIELD2, '' USERDEFINED_FIELD3, '' AS INFO_ERROR_CODE_ASP, '' AS INFO_ERROR_DESCRIPTION_ASP, "
				+ "(CASE WHEN HDR.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND "
				+ "HDR.GSTN_ERROR = TRUE THEN 'IS_ERROR' WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND "
				+ "HDR.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END ) AS SAVE_STATUS, GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, "
				+ "GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, '' GSTN_ERROR_CODE, '' GSTN_ERROR_DESCRIPTION, '' SOURCE_ID, '' FILE_NAME, '' ASP_DATE_TIME FROM ANX_OUTWARD_DOC_HEADER_1A HDR "
				+ "INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM ON HDR.ID = ITM.DOC_HEADER_ID LEFT OUTER JOIN FILE_STATUS FIL ON HDR.FILE_ID = FIL.ID LEFT "
				+ "OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = HDR.BATCH_ID "
				+ "WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND "
				+ "HDR.RETURN_TYPE = 'GSTR1A' AND HDR.IS_DELETE = FALSE AND HDR.IS_PROCESSED = TRUE "
				+ "AND HDR.TAX_DOC_TYPE='AT'" + buildQueryHdr + ") "
				+ " GROUP BY " + "SUPPLIER_GSTIN,RETURN_PERIOD,(CASE WHEN "
				+ "TRAN_TYPE IN ('ZL65','L65') THEN 'L65' "
				+ "WHEN (TRAN_TYPE IN ('Z','N','') OR TRAN_TYPE IS NULL) "
				+ "THEN 'N' END),NEW_POS ";
	}
}
