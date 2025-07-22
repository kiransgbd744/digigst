/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.views.client.Gstr1SavableB2CSTotalSummaryDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import com.google.common.base.Strings;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Gstr1SecB2CSSavableTotalSummaryDaoImpl")
public class Gstr1SecB2CSSavableTotalSummaryDaoImpl
		implements Gstr1ASPB2CSSavableTotalDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SecB2CSSavableTotalSummaryDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	@Override
	public List<Object> getGstr1B2CSSavableReports(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		
		if (Strings.isNullOrEmpty(request.getReturnType())) {
			request.setReturnType(APIConstants.GSTR1.toUpperCase());
		}

		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();
		String taxDocType = request.getTaxDocType();

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

		StringBuilder buildQueryheader = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {

				buildQueryheader
						.append(" AND B2CS.SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {

				buildQueryheader.append(" AND B2CS.PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {

				buildQueryheader.append(" AND B2CS.PLANT_CODE IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {

				buildQueryheader
						.append(" AND B2CS.SALES_ORGANIZATION IN :salesList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {

				buildQueryheader
						.append(" AND B2CS.DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {

				buildQueryheader.append(" AND B2CS.DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {

				buildQueryheader.append(" AND B2CS.LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {

				buildQueryheader.append(" AND B2CS.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {

				buildQueryheader.append(" AND B2CS.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {

				buildQueryheader.append(" AND B2CS.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {

				buildQueryheader.append(" AND B2CS.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {

				buildQueryheader.append(" AND B2CS.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {

				buildQueryheader.append(" AND B2CS.USERACCESS6 IN :ud6List");
			}
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {

			buildQueryheader
					.append(" AND B2CS.DERIVED_RET_PERIOD = :taxperiod ");
		}
		
		String queryStr = null; 
		
		String multiSupplyTypeAns = groupConfigPrmtRepository
				.findAnswerForMultiSupplyType(); 

		if (APIConstants.GSTR1A.equalsIgnoreCase(request.getReturnType())) {
			queryStr = createB2CSSavableTotalQueryStringForGstr1a(
					buildQueryheader.toString());

		} else {
			queryStr = createB2CSSavableTotalQueryString(
					buildQueryheader.toString(),multiSupplyTypeAns);
		}
		
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
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
		return list.parallelStream().map(o -> convertB2CSSummary(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1SavableB2CSTotalSummaryDto convertB2CSSummary(Object[] arr) {
		Gstr1SavableB2CSTotalSummaryDto obj = new Gstr1SavableB2CSTotalSummaryDto();

		obj.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setTransactionType(arr[2] != null ? arr[2].toString() : null);
		obj.setMonth(arr[3] != null ? arr[3].toString() : null);
		obj.setOrgPos(arr[4] != null ? arr[4].toString() : null);
		obj.setOrghsnOrSac(arr[5] != null ? arr[5].toString() : null);
		obj.setOrgUnitOfMeasurement(arr[6] != null ? arr[6].toString() : null);
		obj.setOrgQuantity(arr[7] != null ? arr[7].toString() : null);
		obj.setOrgRate(arr[8] != null ? arr[8].toString() : null);
		obj.setOrgTaxableValue(arr[9] != null ? arr[9].toString() : null);
		obj.setOrgEComGSTIN(arr[10] != null ? arr[10].toString() : null);
		obj.setOrgEComSupplyValue(arr[11] != null ? arr[11].toString() : null);
		obj.setNewPOS(arr[12] != null ? arr[12].toString() : null);
		obj.setNewHSNorSAC(arr[13] != null ? arr[13].toString() : null);
		obj.setNewUnitOfMeasurement(
				arr[14] != null ? arr[14].toString() : null);
		obj.setNewQuantity(arr[15] != null ? arr[15].toString() : null);
		obj.setNewRate(arr[16] != null ? arr[16].toString() : null);
		obj.setNewTaxableValue(arr[17] != null ? arr[17].toString() : null);
		obj.setNewEComGSTIN(arr[18] != null ? arr[18].toString() : null);
		obj.setNewEComSupplyValue(arr[19] != null ? arr[19].toString() : null);
		obj.setIntegratedTaxAmount(arr[20] != null ? arr[20].toString() : null);
		obj.setCentralTaxAmount(arr[21] != null ? arr[21].toString() : null);
		obj.setStateUTTaxAmount(arr[22] != null ? arr[22].toString() : null);
		obj.setCessAmount(arr[23] != null ? arr[23].toString() : null);
		obj.setTotalValue(arr[24] != null ? arr[24].toString() : null);
		obj.setProfitCentre(arr[25] != null ? arr[25].toString() : null);
		obj.setPlant(arr[26] != null ? arr[26].toString() : null);
		obj.setDivision(arr[27] != null ? arr[27].toString() : null);
		obj.setLocation(arr[28] != null ? arr[28].toString() : null);
		obj.setSalesOrganisation(arr[29] != null ? arr[29].toString() : null);
		obj.setDistributionChannel(arr[30] != null ? arr[30].toString() : null);
		obj.setUserAccess1(arr[31] != null ? arr[31].toString() : null);
		obj.setUserAccess2(arr[32] != null ? arr[32].toString() : null);
		obj.setUserAccess3(arr[33] != null ? arr[33].toString() : null);
		obj.setUserAccess4(arr[34] != null ? arr[34].toString() : null);
		obj.setUserAccess5(arr[35] != null ? arr[35].toString() : null);
		obj.setUserAccess6(arr[36] != null ? arr[36].toString() : null);
		obj.setUserdefinedfield1(arr[37] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[37].toString()) : null);
		obj.setUserdefinedfield2(arr[38] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[38].toString()) : null);
		obj.setUserdefinedfield3(arr[39] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[39].toString()) : null);
        String infoDesc = null;
		
		String infoCode = (arr[40] != null) ? arr[40].toString() : null;
		
		if(!Strings.isNullOrEmpty(infoCode)){
			String[] infoCodes = infoCode.split(",");
			List<String> infoCodeList = Arrays.asList(infoCodes);
			infoDesc = ErrorMasterUtil.getErrorInfo(infoCodeList, "OUTWARD");
		}
		obj.setAspInformationDescription(infoDesc);
		/*obj.setAspInformationID(arr[40] != null ? arr[40].toString() : null);
		obj.setAspInformationDescription(
				arr[41] != null ? arr[41].toString() : null);*/
		obj.setSaveStatus(arr[42] != null ? arr[42].toString() : null);
		obj.setGstnrefId(arr[43] != null ? arr[43].toString() : null);
		
		if (arr[44] != null) {
			String timestamp = removeQuotes(arr[44].toString());
			if(timestamp.length() > 19){
				 timestamp = timestamp.substring(0,19);
			}
			
			DateTimeFormatter utcFormatter = DateTimeFormatter
					.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime localDateTime = LocalDateTime.parse(timestamp,
					utcFormatter);

			ZonedDateTime utcDateTime = ZonedDateTime.of(localDateTime,
					ZoneId.of("UTC"));

			ZoneId istZone = ZoneId.of("Asia/Kolkata");
			ZonedDateTime istDateTime = utcDateTime
					.withZoneSameInstant(istZone);

			DateTimeFormatter istFormatter = DateTimeFormatter
					.ofPattern("yyyy-MM-dd HH:mm:ss");
			String istTime = istDateTime.format(istFormatter);
			obj.setGstnrefIdTime(istTime);

		}

		//obj.setGstnrefIdTime(arr[44] != null ? arr[44].toString() : null);
		obj.setGstnErrorCode(arr[45] != null ? arr[45].toString() : null);
		obj.setGstnErrorDescription(
				arr[46] != null ? arr[46].toString() : null);
		obj.setSourceId(arr[47] != null ? arr[47].toString() : null);
		obj.setFileName(arr[48] != null ? arr[48].toString() : null);
		obj.setAspDateTime(arr[49] != null ? arr[49].toString() : null);

		return obj;
	}

	public static void main(String[] args) {
		StringBuilder buildQueryheader = new StringBuilder();


				buildQueryheader
						.append(" AND B2CS.SUPPLIER_GSTIN IN :gstinList");

				buildQueryheader.append(" AND B2CS.PLANT_CODE IN :plantList");
	


			buildQueryheader
					.append(" AND B2CS.DERIVED_RET_PERIOD = :taxperiod ");
	String multiSupplyTypeAns= "A";
		String queryStr = createB2CSSavableTotalQueryString(
				buildQueryheader.toString(),multiSupplyTypeAns);
		System.out.println(queryStr);
	}
	private static String createB2CSSavableTotalQueryString(String buildQueryheader, String multiSupplyTypeAns) {

			return "SELECT SUPPLIER_GSTIN "
					+ "	,RETURN_PERIOD "
					+ "	,TRAN_TYPE "
					+ "	,MAX(MONTH) MONTH "
					+ "	,MAX(ORG_POS) ORG_POS "
					+ "	,MAX(ORG_HSNORSAC) ORG_HSNORSAC "
					+ "	,MAX(ORG_UOM) ORG_UOM "
					+ "	,MAX(ORG_QNT) ORG_QNT "
					+ "	,MAX(ORG_RATE) ORG_RATE "
					+ "	,SUM(ORG_TAXABLE_VALUE) ORG_TAXABLE_VALUE "
					+ "	,MAX(ORG_ECOM_GSTIN) ORG_ECOM_GSTIN "
					+ "	,SUM(ORG_ECOM_SUP_VAL) ORG_ECOM_SUP_VAL "
					+ "	,NEW_POS "
					+ "	,NEW_HSNORSAC "
					+ "	,MAX(NEW_UOM) NEW_UOM "
					+ "	,NEW_QNT "
					+ "	,NEW_RATE "
					+ "	,IFNULL(SUM(CASE "
					+ "				WHEN DOC_TYPE IN ( "
					+ "						'INV' "
					+ "						,'DR' "
					+ "						) "
					+ "					THEN NEW_TAXABLE_VALUE "
					+ "				END), 0) - IFNULL(SUM(CASE "
					+ "				WHEN DOC_TYPE IN ('CR') "
					+ "					THEN NEW_TAXABLE_VALUE "
					+ "				END), 0) AS NEW_TAXABLE_VALUE "
					+ "	,NEW_ECOM_GSTIN "
					+ "	,SUM(NEW_ECOM_SUP_VAL) NEW_ECOM_SUP_VAL "
					+ "	,IFNULL(SUM(CASE "
					+ "				WHEN DOC_TYPE IN ( "
					+ "						'INV' "
					+ "						,'DR' "
					+ "						) "
					+ "					THEN IGST_AMT "
					+ "				END), 0) - IFNULL(SUM(CASE "
					+ "				WHEN DOC_TYPE IN ('CR') "
					+ "					THEN IGST_AMT "
					+ "				END), 0) AS IGST_AMT "
					+ "	,IFNULL(SUM(CASE "
					+ "				WHEN DOC_TYPE IN ( "
					+ "						'INV' "
					+ "						,'DR' "
					+ "						) "
					+ "					THEN CGST_AMT "
					+ "				END), 0) - IFNULL(SUM(CASE "
					+ "				WHEN DOC_TYPE IN ('CR') "
					+ "					THEN CGST_AMT "
					+ "				END), 0) AS CGST_AMT "
					+ "	,IFNULL(SUM(CASE "
					+ "				WHEN DOC_TYPE IN ( "
					+ "						'INV' "
					+ "						,'DR' "
					+ "						) "
					+ "					THEN SGST_AMT "
					+ "				END), 0) - IFNULL(SUM(CASE "
					+ "				WHEN DOC_TYPE IN ('CR') "
					+ "					THEN SGST_AMT "
					+ "				END), 0) AS SGST_AMT "
					+ "	,IFNULL(SUM(CASE "
					+ "				WHEN DOC_TYPE IN ( "
					+ "						'INV' "
					+ "						,'DR' "
					+ "						) "
					+ "					THEN CESS_AMT "
					+ "				END), 0) - IFNULL(SUM(CASE "
					+ "				WHEN DOC_TYPE IN ('CR') "
					+ "					THEN CESS_AMT "
					+ "				END), 0) AS CESS_AMT "
					+ "	,IFNULL(SUM(CASE "
					+ "				WHEN DOC_TYPE IN ( "
					+ "						'INV' "
					+ "						,'DR' "
					+ "						) "
					+ "					THEN TOT_VAL "
					+ "				END), 0) - IFNULL(SUM(CASE "
					+ "				WHEN DOC_TYPE IN ('CR') "
					+ "					THEN TOT_VAL "
					+ "				END), 0) AS TOT_VAL "
					+ "	,PROFIT_CENTRE "
					+ "	,PLANT_CODE "
					+ "	,DIVISION "
					+ "	,LOCATION "
					+ "	,SALES_ORGANIZATION "
					+ "	,DISTRIBUTION_CHANNEL "
					+ "	,USERACCESS1 "
					+ "	,USERACCESS2 "
					+ "	,USERACCESS3 "
					+ "	,USERACCESS4 "
					+ "	,USERACCESS5 "
					+ "	,USERACCESS6 "
					+ "	,MAX(USERDEFINED_FIELD1) USERDEFINED_FIELD1 "
					+ "	,MAX(USERDEFINED_FIELD2) USERDEFINED_FIELD2 "
					+ "	,MAX(USERDEFINED_FIELD3) USERDEFINED_FIELD3 "
					+ "	,MAX(INFO_ERROR_CODE_ASP) INFO_ERROR_CODE_ASP "
					+ "	,MAX(INFO_ERROR_DESCRIPTION_ASP) INFO_ERROR_DESCRIPTION_ASP "
					+ "	,MAX(SAVE_STATUS) SAVE_STATUS "
					+ "	,MAX(GSTIN_REF_ID) GSTIN_REF_ID "
					+ "	,MAX(GSTIN_REF_ID_TIME) GSTIN_REF_ID_TIME "
					+ "	,MAX(GSTN_ERROR_CODE) GSTN_ERROR_CODE "
					+ "	,MAX(GSTN_ERROR_DESCRIPTION) GSTN_ERROR_DESCRIPTION "
					+ "	,MAX(SOURCE_ID) SOURCE_ID "
					+ "	,MAX(FILE_NAME) FILE_NAME "
					+ "	,MAX(ASP_DATE_TIME) ASP_DATE_TIME "
					+ "FROM ( "
					+ "	SELECT 'INV' AS DOC_TYPE "
					+ "		,(IFNULL(B2CS.SUPPLIER_GSTIN, '') || '|' || IFNULL(B2CS.RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) || '|' || IFNULL(NEW_ECOM_GSTIN, '') || '|' || IFNULL(NEW_RATE, 9999)) DOC_KEY "
					+ "		,B2CS.SUPPLIER_GSTIN "
					+ "		,B2CS.RETURN_PERIOD "
					+ "		,TRAN_TYPE "
					+ "		,'' MONTH "
					+ "		,NULL AS ORG_POS "
					+ "		,0 ORG_HSNORSAC "
					+ "		,'' ORG_UOM "
					+ "		,0 ORG_QNT "
					+ "		,NULL AS ORG_RATE "
					+ "		,ORG_TAXABLE_VALUE "
					+ "		,NULL AS ORG_ECOM_GSTIN "
					+ "		,ORG_ECOM_SUP_VAL "
					+ "		,NEW_POS "
					+ "		,NEW_HSNORSAC "
					+ "		,'' NEW_UOM "
					+ "		,NEW_QNT "
					+ "		,NEW_RATE "
					+ "		,NEW_TAXABLE_VALUE "
					+ "		,NEW_ECOM_GSTIN "
					+ "		,NEW_ECOM_SUP_VAL "
					+ "		,IGST_AMT "
					+ "		,CGST_AMT "
					+ "		,SGST_AMT "
					+ "		,CESS_AMT "
					+ "		,IFNULL(NEW_TAXABLE_VALUE, 0) + IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0) AS TOT_VAL "
					+ "		,PROFIT_CENTRE "
					+ "		,PLANT_CODE "
					+ "		,DIVISION "
					+ "		,LOCATION "
					+ "		,SALES_ORGANIZATION "
					+ "		,DISTRIBUTION_CHANNEL "
					+ "		,USERACCESS1 "
					+ "		,USERACCESS2 "
					+ "		,USERACCESS3 "
					+ "		,USERACCESS4 "
					+ "		,USERACCESS5 "
					+ "		,USERACCESS6 "
					+ "		,'' USERDEFINED_FIELD1 "
					+ "		,'' USERDEFINED_FIELD2 "
					+ "		,'' USERDEFINED_FIELD3 "
					+ "		,'' INFO_ERROR_CODE_ASP "
					+ "		,'' INFO_ERROR_DESCRIPTION_ASP "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN B2CS.IS_SAVED_TO_GSTN = TRUE "
					+ "					THEN 'IS_SAVED' "
					+ "				WHEN B2CS.IS_SAVED_TO_GSTN = FALSE "
					+ "					AND B2CS.GSTN_ERROR = TRUE "
					+ "					THEN 'IS_ERROR' "
					+ "				WHEN B2CS.IS_SAVED_TO_GSTN = FALSE "
					+ "					AND B2CS.GSTN_ERROR = FALSE "
					+ "					THEN 'NOT_SAVED' "
					+ "				END "
					+ "			) AS SAVE_STATUS "
					+ "		,GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID "
					+ "		,GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME "
					+ "		,'' AS GSTN_ERROR_CODE "
					+ "		,'' AS GSTN_ERROR_DESCRIPTION "
					+ "		,'' SOURCE_ID "
					+ "		,'' FILE_NAME "
					+ "		,'' ASP_DATE_TIME "
					+ "	FROM GSTR1_PROCESSED_B2CS B2CS "
					+ "	LEFT OUTER JOIN ( "
					+ "		SELECT COMMON_ID "
					+ "			,VAL_TYPE "
					+ "			,FILE_ID "
					+ "			,INV_KEY "
					+ "			,TABLE_TYPE "
					+ "			,STRING_AGG(INFO_ERROR_CODE_ASP, ',') INFO_ERROR_CODE_ASP "
					+ "			,STRING_AGG(INFO_ERROR_DESCRIPTION_ASP, ',') INFO_ERROR_DESCRIPTION_ASP "
					+ "		FROM ( "
					+ "			SELECT COMMON_ID "
					+ "				,VAL_TYPE "
					+ "				,FILE_ID "
					+ "				,INV_KEY "
					+ "				,TABLE_TYPE "
					+ "				,CASE "
					+ "					WHEN ERROR_TYPE = 'INFO' "
					+ "						THEN ERROR_CODE "
					+ "					END AS INFO_ERROR_CODE_ASP "
					+ "				,CASE "
					+ "					WHEN ERROR_TYPE = 'INFO' "
					+ "						THEN ERROR_DESCRIPTION "
					+ "					END AS INFO_ERROR_DESCRIPTION_ASP "
					+ "			FROM ANX_VERTICAL_ERROR "
					+ "			) "
					+ "		GROUP BY COMMON_ID "
					+ "			,VAL_TYPE "
					+ "			,FILE_ID "
					+ "			,INV_KEY "
					+ "			,TABLE_TYPE "
					+ "		) GSTRERR ON B2CS.AS_ENTERED_ID = GSTRERR.COMMON_ID "
					+ "		AND B2CS.FILE_ID = GSTRERR.FILE_ID "
					+ "		AND GSTRERR.TABLE_TYPE = 'B2CS' "
					+ "		AND RETURN_TYPE = 'GSTR1' "
					+ "	LEFT OUTER JOIN FILE_STATUS FIL ON B2CS.FILE_ID = FIL.ID "
					+ "		AND GSTRERR.FILE_ID = FIL.ID "
					+ "		AND GSTRERR.INV_KEY = B2CS.B2CS_INVKEY "
					+ "	LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = B2CS.BATCH_ID "
					+ "	WHERE B2CS.IS_DELETE = FALSE "
					+ "		AND IS_AMENDMENT = FALSE "
					+ buildQueryheader
					+ "	 "
					+ "	UNION ALL "
					+ "	 "
					+ "	SELECT DOC_TYPE "
					+ "		,(IFNULL(B2CS.SUPPLIER_GSTIN, '') || '|' || IFNULL(B2CS.RETURN_PERIOD, '') || '|' || IFNULL(B2CS.DIFF_PERCENT, '') || '|' || IFNULL(B2CS.POS, 9999) || '|' || IFNULL(ITM.ECOM_GSTIN, '') || '|' || IFNULL(ITM.TAX_RATE, 9999)) DOC_KEY "
					+ "		,B2CS.SUPPLIER_GSTIN "
					+ "		,B2CS.RETURN_PERIOD "
					+ "		,ITM.DIFF_PERCENT AS TRAN_TYPE "
					+ "		,'' AS MONTH "
					+ "		,NULL AS ORG_POS "
					+ "		,0 AS ORG_HSNORSAC "
					+ "		,'' AS ORG_UOM "
					+ "		,0 AS ORG_QNT "
					+ "		,NULL AS ORG_RATE "
					+ "		,0 AS ORG_TAXABLE_VALUE "
					+ "		,NULL AS ORG_ECOM_GSTIN "
					+ "		,0 AS ORG_ECOM_SUP_VAL "
					+ "		,B2CS.POS AS NEW_POS "
					+ "		,ITM_HSNSAC AS NEW_HSNORSAC "
					+ "		,'' NEW_UOM "
					+ "		,ITM_QTY AS NEW_QNT "
					+ "		,ITM.TAX_RATE AS NEW_RATE "
					+ "		,ITM.TAXABLE_VALUE AS NEW_TAXABLE_VALUE "
					+ "		,ITM.ECOM_GSTIN AS NEW_ECOM_GSTIN "
					+ "		,0 NEW_ECOM_SUP_VAL "
					+ "		,ITM.IGST_AMT AS IGST_AMT "
					+ "		,ITM.CGST_AMT AS CGST_AMT "
					+ "		,ITM.SGST_AMT AS SGST_AMT "
					+ "		,(ITM.CESS_AMT_SPECIFIC + ITM.CESS_AMT_ADVALOREM) AS CESS_AMT "
					+ "		,ITM.ONB_LINE_ITEM_AMT AS TOT_VAL "
					+ "		,B2CS.PROFIT_CENTRE "
					+ "		,B2CS.PLANT_CODE "
					+ "		,B2CS.DIVISION "
					+ "		,B2CS.LOCATION "
					+ "		,B2CS.SALES_ORGANIZATION "
					+ "		,B2CS.DISTRIBUTION_CHANNEL "
					+ "		,B2CS.USERACCESS1 "
					+ "		,B2CS.USERACCESS2 "
					+ "		,B2CS.USERACCESS3 "
					+ "		,B2CS.USERACCESS4 "
					+ "		,B2CS.USERACCESS5 "
					+ "		,B2CS.USERACCESS6 "
					+ "		,'' USERDEFINED_FIELD1 "
					+ "		,'' USERDEFINED_FIELD2 "
					+ "		,'' USERDEFINED_FIELD3 "
					+ "		,'' AS INFO_ERROR_CODE_ASP "
					+ "		,'' AS INFO_ERROR_DESCRIPTION_ASP "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN B2CS.IS_SAVED_TO_GSTN = TRUE "
					+ "					THEN 'IS_SAVED' "
					+ "				WHEN B2CS.IS_SAVED_TO_GSTN = FALSE "
					+ "					AND B2CS.GSTN_ERROR = TRUE "
					+ "					THEN 'IS_ERROR' "
					+ "				WHEN B2CS.IS_SAVED_TO_GSTN = FALSE "
					+ "					AND B2CS.GSTN_ERROR = FALSE "
					+ "					THEN 'NOT_SAVED' "
					+ "				END "
					+ "			) AS SAVE_STATUS "
					+ "		,GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID "
					+ "		,GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME "
					+ "		,'' GSTN_ERROR_CODE "
					+ "		,'' GSTN_ERROR_DESCRIPTION "
					+ "		,'' SOURCE_ID "
					+ "		,'' FILE_NAME "
					+ "		,'' ASP_DATE_TIME "
					+ "	FROM ANX_OUTWARD_DOC_HEADER B2CS "
					+ "	INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON B2CS.ID = ITM.DOC_HEADER_ID "
					+ "		AND B2CS.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
					+ "	LEFT OUTER JOIN FILE_STATUS FIL ON B2CS.FILE_ID = FIL.ID "
					+ "	LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = B2CS.BATCH_ID "
					+ "	WHERE B2CS.RETURN_TYPE = 'GSTR1' "
					+ "		AND B2CS.ASP_INVOICE_STATUS = 2 "
					+ "		AND B2CS.COMPLIANCE_APPLICABLE = TRUE "
					+ "		AND B2CS.IS_DELETE = FALSE "
					+ "		AND  ITM.ITM_TAX_DOC_TYPE = 'B2CS' "
					+ buildQueryheader 
					+ "	) "
					+ "GROUP BY SUPPLIER_GSTIN "
					+ "	,RETURN_PERIOD "
					+ "	,TRAN_TYPE "
					+ "	,NEW_POS "
					+ "	,NEW_ECOM_GSTIN "
					+ "	,NEW_RATE "
					+ "	,NEW_HSNORSAC "
					+ "	,NEW_QNT "
					+ "	,PROFIT_CENTRE "
					+ "	,PLANT_CODE "
					+ "	,DIVISION "
					+ "	,LOCATION "
					+ "	,SALES_ORGANIZATION "
					+ "	,DISTRIBUTION_CHANNEL "
					+ "	,USERACCESS1 "
					+ "	,USERACCESS2 "
					+ "	,USERACCESS3 "
					+ "	,USERACCESS4 "
					+ "	,USERACCESS5 "
					+ "	,USERACCESS6";
		
		
	}
	private String createB2CSSavableTotalQueryStringForGstr1a(String buildQueryheader) {

		return "SELECT SUPPLIER_GSTIN, RETURN_PERIOD, TRAN_TYPE,MAX(MONTH)MONTH,"
				+ "MAX(ORG_POS)ORG_POS, MAX(ORG_HSNORSAC)ORG_HSNORSAC,"
				+ "MAX(ORG_UOM)ORG_UOM, MAX(ORG_QNT)ORG_QNT,MAX(ORG_RATE)ORG_RATE, "
				+ "SUM(ORG_TAXABLE_VALUE)ORG_TAXABLE_VALUE, "
				+ "MAX(ORG_ECOM_GSTIN)ORG_ECOM_GSTIN, "
				+ "SUM(ORG_ECOM_SUP_VAL)ORG_ECOM_SUP_VAL,NEW_POS, "
				+ " NEW_HSNORSAC,MAX(NEW_UOM)NEW_UOM, "
				+ " NEW_QNT,NEW_RATE, "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') "
				+ "THEN NEW_TAXABLE_VALUE END),0)-IFNULL(SUM(CASE "
				+ "WHEN DOC_TYPE IN ('CR') THEN NEW_TAXABLE_VALUE END),0) "
				+ "AS NEW_TAXABLE_VALUE, NEW_ECOM_GSTIN, "
				+ "SUM(NEW_ECOM_SUP_VAL)NEW_ECOM_SUP_VAL , "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') "
				+ "THEN IGST_AMT END),0)-IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') "
				+ "THEN IGST_AMT END),0) AS IGST_AMT, "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') "
				+ "THEN CGST_AMT END),0)-IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') "
				+ "THEN CGST_AMT END),0) AS CGST_AMT, IFNULL(SUM(CASE "
				+ "WHEN DOC_TYPE IN ('INV','DR') "
				+ "THEN SGST_AMT END),0)-IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') "
				+ "THEN SGST_AMT END),0) AS SGST_AMT, IFNULL(SUM(CASE "
				+ "WHEN DOC_TYPE IN ('INV','DR') THEN CESS_AMT END),0)-IFNULL(SUM(CASE "
				+ "WHEN DOC_TYPE IN ('CR') THEN CESS_AMT END),0) "
				+ "AS CESS_AMT, IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') "
				+ "THEN TOT_VAL END),0)-IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') "
				+ "THEN TOT_VAL END),0) AS TOT_VAL, "
				+ "PROFIT_CENTRE,PLANT_CODE, "
				+ "DIVISION, LOCATION, "
				+ "SALES_ORGANIZATION, "
				+ "DISTRIBUTION_CHANNEL, "
				+ "USERACCESS1,USERACCESS2, "
				+ "USERACCESS3,USERACCESS4, "
				+ "USERACCESS5,USERACCESS6, "
				+ "MAX(USERDEFINED_FIELD1)USERDEFINED_FIELD1, "
				+ "MAX(USERDEFINED_FIELD2)USERDEFINED_FIELD2, "
				+ "MAX(USERDEFINED_FIELD3)USERDEFINED_FIELD3, "
				+ "MAX(INFO_ERROR_CODE_ASP)INFO_ERROR_CODE_ASP, "
				+ "MAX(INFO_ERROR_DESCRIPTION_ASP)INFO_ERROR_DESCRIPTION_ASP, "
				+ "MAX(SAVE_STATUS)SAVE_STATUS,MAX(GSTIN_REF_ID)GSTIN_REF_ID , "
				+ "MAX(GSTIN_REF_ID_TIME )GSTIN_REF_ID_TIME, "
				+ "MAX(GSTN_ERROR_CODE)GSTN_ERROR_CODE , "
				+ "MAX(GSTN_ERROR_DESCRIPTION)GSTN_ERROR_DESCRIPTION, "
				+ "MAX(SOURCE_ID)SOURCE_ID,MAX(FILE_NAME)FILE_NAME, "
				+ "MAX(ASP_DATE_TIME)ASP_DATE_TIME "
				+ "FROM ( SELECT 'INV' AS DOC_TYPE, (IFNULL(B2CS.SUPPLIER_GSTIN,'') ||'|'|| "
				+ "IFNULL(B2CS.RETURN_PERIOD,'') ||'|'||IFNULL(TRAN_TYPE,'') "
				+ "||'|'||IFNULL(NEW_POS,9999)||'|'||IFNULL(NEW_ECOM_GSTIN,'')||'|'||IFNULL(NEW_RATE,9999) ) "
				+ "DOC_KEY, B2CS.SUPPLIER_GSTIN,B2CS.RETURN_PERIOD, TRAN_TYPE, '' MONTH,"
				+ "NULL AS ORG_POS,0 ORG_HSNORSAC,''ORG_UOM, 0 ORG_QNT,NULL AS ORG_RATE, "
				+ "ORG_TAXABLE_VALUE, NULL AS ORG_ECOM_GSTIN, ORG_ECOM_SUP_VAL,NEW_POS, "
				+ " NEW_HSNORSAC,''NEW_UOM, NEW_QNT,NEW_RATE,NEW_TAXABLE_VALUE, "
				+ "NEW_ECOM_GSTIN, NEW_ECOM_SUP_VAL,IGST_AMT,CGST_AMT,SGST_AMT, "
				+ "CESS_AMT, IFNULL(NEW_TAXABLE_VALUE,0)"
				+ " +IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)"
				+ " +IFNULL(CESS_AMT,0) AS TOT_VAL, PROFIT_CENTRE, PLANT_CODE,DIVISION, "
				+ "  LOCATION, SALES_ORGANIZATION, DISTRIBUTION_CHANNEL, "
				+ "  USERACCESS1, USERACCESS2, USERACCESS3, USERACCESS4, "
				+ "  USERACCESS5, USERACCESS6,''USERDEFINED_FIELD1, "
				+ " ''USERDEFINED_FIELD2, ''USERDEFINED_FIELD3, ''INFO_ERROR_CODE_ASP,"
				+ " ''INFO_ERROR_DESCRIPTION_ASP, (CASE WHEN B2CS.IS_SAVED_TO_GSTN = TRUE "
				+ "THEN 'IS_SAVED' WHEN B2CS.IS_SAVED_TO_GSTN = FALSE "
				+ "AND B2CS.GSTN_ERROR = TRUE THEN 'IS_ERROR' "
				+ "WHEN B2CS.IS_SAVED_TO_GSTN = FALSE AND B2CS.GSTN_ERROR = FALSE "
				+ "THEN 'NOT_SAVED' END) AS SAVE_STATUS,GSTNBATCH.GSTN_SAVE_REF_ID "
				+ "AS GSTIN_REF_ID, GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, "
				+ " '' AS GSTN_ERROR_CODE,'' AS GSTN_ERROR_DESCRIPTION, '' SOURCE_ID,"
				+ " '' FILE_NAME,'' ASP_DATE_TIME FROM GSTR1A_PROCESSED_B2CS B2CS "
				+ "LEFT OUTER JOIN (SELECT COMMON_ID, VAL_TYPE,FILE_ID,INV_KEY,TABLE_TYPE, "
				+ "STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP, "
				+ "STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') INFO_ERROR_DESCRIPTION_ASP "
				+ "FROM (SELECT COMMON_ID,VAL_TYPE, FILE_ID,INV_KEY,TABLE_TYPE, "
				+ "CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END AS INFO_ERROR_CODE_ASP, "
				+ "CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END "
				+ "AS INFO_ERROR_DESCRIPTION_ASP FROM ANX_VERTICAL_ERROR_1A) "
				+ " GROUP BY "
				+ "COMMON_ID, VAL_TYPE,FILE_ID,INV_KEY,TABLE_TYPE) GSTRERR ON "
				+ "B2CS.AS_ENTERED_ID= GSTRERR.COMMON_ID AND B2CS.FILE_ID=GSTRERR.FILE_ID "
				+ "AND GSTRERR.TABLE_TYPE='B2CS' AND RETURN_TYPE='GSTR1A' "
				+ "LEFT OUTER JOIN FILE_STATUS FIL ON B2CS.FILE_ID=FIL.ID "
				+ "AND GSTRERR.FILE_ID=FIL.ID AND GSTRERR.INV_KEY=B2CS.B2CS_INVKEY "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH "
				+ "ON GSTNBATCH.ID = B2CS.BATCH_ID WHERE B2CS.IS_DELETE=FALSE "
				+ "AND IS_AMENDMENT=FALSE "
				+ buildQueryheader
				+ " UNION ALL "
				+ "SELECT DOC_TYPE, (IFNULL(B2CS.SUPPLIER_GSTIN,'') ||'|'|| "
				+ "IFNULL(B2CS.RETURN_PERIOD,'') ||'|'||IFNULL(B2CS.DIFF_PERCENT,'') "
				+ "||'|'||IFNULL(B2CS.POS,9999)||'|'||IFNULL(ITM.ECOM_GSTIN,'')"
				+ "||'|'||IFNULL(ITM.TAX_RATE,9999) ) DOC_KEY, B2CS.SUPPLIER_GSTIN,"
				+ "B2CS.RETURN_PERIOD,ITM.DIFF_PERCENT AS TRAN_TYPE, "
				+ " '' AS MONTH,NULL AS ORG_POS, 0 AS ORG_HSNORSAC,'' AS ORG_UOM, "
				+ "0 AS ORG_QNT, NULL AS ORG_RATE,0 AS ORG_TAXABLE_VALUE, "
				+ "NULL AS ORG_ECOM_GSTIN,0 AS ORG_ECOM_SUP_VAL, "
				+ "B2CS.POS AS NEW_POS,ITM_HSNSAC AS NEW_HSNORSAC, "
				+ " '' NEW_UOM,ITM_QTY AS NEW_QNT, ITM.TAX_RATE AS NEW_RATE,"
				+ "ITM.TAXABLE_VALUE AS NEW_TAXABLE_VALUE, "
				+ "ITM.ECOM_GSTIN AS NEW_ECOM_GSTIN, 0 NEW_ECOM_SUP_VAL,"
				+ "ITM.IGST_AMT AS IGST_AMT, ITM.CGST_AMT AS CGST_AMT, "
				+ "ITM.SGST_AMT AS SGST_AMT, (ITM.CESS_AMT_SPECIFIC+ITM.CESS_AMT_ADVALOREM) "
				+ "AS CESS_AMT, ITM.ONB_LINE_ITEM_AMT AS TOT_VAL, "
				+ " B2CS.PROFIT_CENTRE , B2CS.PLANT_CODE,B2CS.DIVISION, B2CS.LOCATION,"
				+ " B2CS.SALES_ORGANIZATION,B2CS.DISTRIBUTION_CHANNEL, B2CS.USERACCESS1,"
				+ " B2CS.USERACCESS2, B2CS.USERACCESS3, B2CS.USERACCESS4, "
				+ " B2CS.USERACCESS5,B2CS.USERACCESS6, '' USERDEFINED_FIELD1, "
				+ " '' USERDEFINED_FIELD2, '' USERDEFINED_FIELD3, '' AS INFO_ERROR_CODE_ASP, "
				+ " '' AS INFO_ERROR_DESCRIPTION_ASP, ( CASE WHEN B2CS.IS_SAVED_TO_GSTN = TRUE "
				+ "THEN 'IS_SAVED' WHEN B2CS.IS_SAVED_TO_GSTN = FALSE AND B2CS.GSTN_ERROR = TRUE "
				+ "THEN 'IS_ERROR' WHEN B2CS.IS_SAVED_TO_GSTN = FALSE "
				+ "AND B2CS.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) "
				+ "AS SAVE_STATUS, GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, "
				+ "GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, "
				+ "'' GSTN_ERROR_CODE, '' GSTN_ERROR_DESCRIPTION,'' SOURCE_ID, "
				+ "'' FILE_NAME, '' ASP_DATE_TIME FROM "
				+ "ANX_OUTWARD_DOC_HEADER_1A B2CS INNER JOIN "
				+ "ANX_OUTWARD_DOC_ITEM_1A ITM ON B2CS.ID = ITM.DOC_HEADER_ID "
				+ "AND B2CS.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT OUTER JOIN FILE_STATUS FIL ON B2CS.FILE_ID=FIL.ID "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON "
				+ "GSTNBATCH.ID = B2CS.BATCH_ID WHERE B2CS.RETURN_TYPE = 'GSTR1A' "
				+ "AND B2CS.ASP_INVOICE_STATUS = 2 "
				+ "AND B2CS.COMPLIANCE_APPLICABLE=TRUE AND B2CS.IS_DELETE=FALSE "
				+ "AND B2CS.TAX_DOC_TYPE='B2CS' "
				+ buildQueryheader + ") "
				+ "GROUP BY "
				+ "SUPPLIER_GSTIN, RETURN_PERIOD, TRAN_TYPE,NEW_POS,NEW_ECOM_GSTIN,NEW_RATE,NEW_HSNORSAC,NEW_QNT,"
				+ "PROFIT_CENTRE,PLANT_CODE,DIVISION, LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,"
				+ "USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6 ";
	}
	private String removeQuotes(String data) {
		if (Strings.isNullOrEmpty(data)) {
			return null;
		}
		if (data.contains("'")) {
			return data.replace("'", "");
		}
		if (data.contains("`")) {
			return data.replace("`", "");
		}
 
		return data;
 
	}
}
