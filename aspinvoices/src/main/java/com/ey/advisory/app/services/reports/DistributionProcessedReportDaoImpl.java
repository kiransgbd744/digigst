package com.ey.advisory.app.services.reports;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.DistributionProcessedDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;


	
	@Component("DistributionProcessedReportDaoImpl")
	public class DistributionProcessedReportDaoImpl implements Gstr6ProcessedDao {

		@PersistenceContext(unitName = "clientDataUnit")
		private EntityManager entityManager;

		private static final String OLDFARMATTER = "yyyy-MM-dd";
		private static final String NEWFARMATTER = "dd-MM-yyyy";

		@Override
		public List<Object> getGstr6Reports(SearchCriteria criteria) {

			Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

			String dataType = request.getDataType();
			String taxperiod = request.getTaxperiod();
			Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

			String ProfitCenter = null;
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
			String GSTIN = null;

			List<String> pcList = null;
			List<String> plantList = null;
			List<String> divisionList = null;
			List<String> locationList = null;
			List<String> purchaseList = null;
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
								&& dataSecAttrs.get(OnboardingConstant.GSTIN).size() > 0) {
							gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
						}
					}

					if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
						ProfitCenter = key;
						if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.PC).size() > 0) {
							pcList = dataSecAttrs.get(OnboardingConstant.PC);
						}
					}

					if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

						plant = key;
						if (!dataSecAttrs.get(OnboardingConstant.PLANT).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.PLANT).size() > 0) {
							plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
						division = key;
						if (!dataSecAttrs.get(OnboardingConstant.DIVISION).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.DIVISION).size() > 0) {
							divisionList = dataSecAttrs.get(OnboardingConstant.DIVISION);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
						location = key;
						if (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.LOCATION).size() > 0) {
							locationList = dataSecAttrs.get(OnboardingConstant.LOCATION);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
						purchase = key;
						if (!dataSecAttrs.get(OnboardingConstant.PO).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.PO).size() > 0) {
							purchaseList = dataSecAttrs.get(OnboardingConstant.PO);
						}
					}

					if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
						ud1 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD1) != null
								&& dataSecAttrs.get(OnboardingConstant.UD1).size() > 0) {
							ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
						ud2 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD2) != null
								&& dataSecAttrs.get(OnboardingConstant.UD2).size() > 0) {
							ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
						ud3 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD3) != null
								&& dataSecAttrs.get(OnboardingConstant.UD3).size() > 0) {
							ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
						ud4 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD4) != null
								&& dataSecAttrs.get(OnboardingConstant.UD4).size() > 0) {
							ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
						ud5 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD5) != null
								&& dataSecAttrs.get(OnboardingConstant.UD5).size() > 0) {
							ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
						ud6 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD6) != null
								&& dataSecAttrs.get(OnboardingConstant.UD6).size() > 0) {
							ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
						}
					}
				}
			}

			StringBuilder buildQuery = new StringBuilder();

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					buildQuery.append(" WHERE ISD_GSTIN IN :gstinList");
				}
			}
			if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && pcList.size() > 0) {
					buildQuery.append(" AND PROFIT_CENTRE IN :pcList");
				}
			}
			if (plant != null && !plant.isEmpty()) {
				if (plantList != null && plantList.size() > 0) {
					buildQuery.append(" AND PLANT_CODE IN :plantList");
				}
			}
			if (purchase != null && !purchase.isEmpty()) {
				if (purchaseList != null && purchaseList.size() > 0) {
					buildQuery.append(" AND PURCHASE_ORGANIZATION IN :purchaseList");
				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && divisionList.size() > 0) {
					buildQuery.append(" AND HDIVISION IN :divisionList");
				}
			}
			if (location != null && !location.isEmpty()) {
				if (locationList != null && locationList.size() > 0) {
					buildQuery.append(" AND LOCATION IN :locationList");
				}
			}
			if (ud1 != null && !ud1.isEmpty()) {
				if (ud1List != null && ud1List.size() > 0) {
					buildQuery.append(" AND USERACCESS1 IN :ud1List");
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && ud2List.size() > 0) {
					buildQuery.append(" AND USERACCESS2 IN :ud2List");
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && ud3List.size() > 0) {
					buildQuery.append(" AND USERACCESS3 IN :ud3List");
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && ud4List.size() > 0) {
					buildQuery.append(" AND USERACCESS4 IN :ud4List");
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && ud5List.size() > 0) {
					buildQuery.append(" AND USERACCESS5 IN :ud5List");
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && ud6List.size() > 0) {
					buildQuery.append(" AND USERACCESS6 IN :ud6List");
				}
			}
			
			if (taxperiod != null && !taxperiod.isEmpty()) {
				buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
				buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
			}

			String queryStr = createInwardApiProcessedRecQueryString(buildQuery.toString());
			Query q = entityManager.createNativeQuery(queryStr);

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty() && gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}

			if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
					q.setParameter("pcList", pcList);
				}
			}
			if (plant != null && !plant.isEmpty()) {
				if (plantList != null && !plantList.isEmpty() && plantList.size() > 0) {
					q.setParameter("plantList", plantList);
				}
			}
			if (purchase != null && !purchase.isEmpty()) {
				if (purchaseList != null && !purchaseList.isEmpty() && purchaseList.size() > 0) {
					q.setParameter("purchaseList", purchaseList);
				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && !divisionList.isEmpty() && divisionList.size() > 0) {
					q.setParameter("divisionList", divisionList);
				}
			}
			if (location != null && !location.isEmpty()) {
				if (locationList != null && !locationList.isEmpty() && locationList.size() > 0) {
					q.setParameter("locationList", locationList);
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
			
			if (taxperiod != null && !taxperiod.isEmpty()) {
				int derivedRetPeriod = GenUtil
						.convertTaxPeriodToInt(request.getTaxperiod());
				q.setParameter("taxperiod", derivedRetPeriod);
			}

			List<Object[]> list = q.getResultList();
			return list.parallelStream().map(o -> convertApiInwardProcessedRecords(o))
					.collect(Collectors.toCollection(ArrayList::new));
		}
		private DistributionProcessedDto convertApiInwardProcessedRecords(Object[] arr) {
			DistributionProcessedDto obj = new DistributionProcessedDto();

			obj.setReturnPeriod(
					arr[0] != null ? DownloadReportsConstant.CSVCHARACTER
							.concat(arr[0].toString()) : null);
			obj.setiSDGSTIN(arr[1] != null ? arr[1].toString() : null);
			obj.setRecipientGSTIN(arr[2] != null ? arr[2].toString() : null);
			//obj.setStateCode(arr[3] != null ? arr[3].toString() : null);
			obj.setStateCode(arr[3] != null ? DownloadReportsConstant.CSVCHARACTER
					.concat(arr[3].toString()) : null);
			obj.setOriginalRecipeintGSTIN(
					arr[4] != null ? arr[4].toString() : null);
			obj.setOriginalStatecode(arr[5] != null ? arr[5].toString() : null);
			obj.setDocumentType(arr[6] != null ? arr[6].toString() : null);
			obj.setSupplyType(arr[7] != null ? arr[7].toString() : null);
			//obj.setDocumentNumber(arr[8] != null ? arr[8].toString() : null);
			obj.setDocumentNumber(
					arr[8] != null ? DownloadReportsConstant.CSVCHARACTER
							.concat(arr[8].toString()) : null);
			obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
			obj.setOriginalDocumentNumber(
					arr[10] != null ? arr[10].toString() : null);
			obj.setOriginalDocumentDate(
					arr[11] != null ? arr[11].toString() : null);
			obj.setOriginalCreditNoteNumber(
					arr[12] != null ? arr[12].toString() : null);
			obj.setOriginalCreditNoteDate(
					arr[13] != null ? arr[13].toString() : null);
			obj.setEligibleIndicator(arr[14] != null ? arr[14].toString() : null);
			obj.setiGSTasIGST(arr[15] != null ? arr[15].toString() : null);
			obj.setiGSTasSGST(arr[16] != null ? arr[16].toString() : null);
			obj.setiGSTasCGST(arr[17] != null ? arr[17].toString() : null);
			obj.setsGSTasSGST(arr[18] != null ? arr[18].toString() : null);
			obj.setsGSTasIGST(arr[19] != null ? arr[19].toString() : null);
			obj.setcGSTasCGST(arr[20] != null ? arr[20].toString() : null);
			obj.setcGSTasIGST(arr[21] != null ? arr[21].toString() : null);
			obj.setcESSAmount(arr[22] != null ? arr[22].toString() : null);
			obj.setReturnType(arr[23] != null ? arr[23].toString() : null);
			obj.setCategory(arr[24] != null ? arr[24].toString() : null);
			obj.setTableNumber(arr[25] != null ? arr[25].toString() : null);
			/*obj.setAspErrorCode(arr[24] != null ? arr[24].toString() : null);
			obj.setAspErrorDesc(arr[25] != null ? arr[25].toString() : null);*/
			obj.setAspInformationID(arr[26] != null ? arr[26].toString() : null);
			obj.setAspInformationDesc(arr[27] != null ? arr[27].toString() : null);
			obj.setUploadSouce(arr[28] != null ? arr[28].toString() : null);
			obj.setSouceId(arr[29] != null ? arr[29].toString() : null);
			obj.setFileName(arr[30] != null ? arr[30].toString() : null);
			//obj.setAspDateTime(arr[31] != null ? arr[31].toString() : null);

			if (arr[31] != null) {
	            Timestamp timeStamp = (Timestamp) arr[31];
	            obj.setAspDateTime(dateChange(timeStamp));
	        } else {
	            obj.setAspDateTime(null);
	        }
			return obj;
		}

		private String createInwardApiProcessedRecQueryString(String buildQuery) {
			return "SELECT TAX_PERIOD,ISD_GSTIN,CUST_GSTIN, STATE_CODE,"
					+ "ORG_CUST_GSTIN,ORG_STATE_CODE, DOC_TYPE,SUPPLY_TYPE, "
					+ "DOC_NUM,DOC_DATE,ORG_DOC_NUM, ORG_DOC_DATE,ORG_CR_NUM,ORG_CR_DATE, "
					+ "ELIGIBLE_INDICATOR,IGST_AMT_AS_IGST, IGST_AMT_AS_SGST,IGST_AMT_AS_CGST, "
					+ "SGST_AMT_AS_SGST,SGST_AMT_AS_IGST, CGST_AMT_AS_CGST,CGST_AMT_AS_IGST, "
					+ "CESS_AMT, 'GSTR6' AS RETURN_TYPE, (CASE WHEN DOC_TYPE = 'INV' "
					+ "AND ELIGIBLE_INDICATOR IN('IS','E') THEN 'DISTRIBUTION_INV_ELIGIBLE' "
					+ "WHEN DOC_TYPE = 'INV' AND ELIGIBLE_INDICATOR IN( 'NO','IE') "
					+ "THEN 'DISTRIBUTION_INV_INELIGIBLE' WHEN DOC_TYPE = 'RNV' "
					+ "AND ELIGIBLE_INDICATOR IN('IS','E') THEN 'RE_DISTRIBUTION_INV_ELIGIBLE' "
					+ "WHEN DOC_TYPE = 'RNV' AND ELIGIBLE_INDICATOR IN('NO','IE') "
					+ "THEN 'RE_DISTRIBUTION_INV_INELIGIBLE' WHEN DOC_TYPE = 'CR' "
					+ "AND ELIGIBLE_INDICATOR IN('IS','E') THEN 'DISTRIBUTION_CR_ELIGIBLE' "
					+ "WHEN DOC_TYPE = 'CR' AND ELIGIBLE_INDICATOR IN('NO','IE') "
					+ "THEN 'DISTRIBUTION_CR_INELIGIBLE' WHEN DOC_TYPE = 'RCR' "
					+ "AND ELIGIBLE_INDICATOR IN('IS','E') THEN 'RE_DISTRIBUTION_CR_ELIGIBLE' "
					+ "WHEN DOC_TYPE = 'RCR' AND ELIGIBLE_INDICATOR IN('NO','IE') "
					+ "THEN 'RE_DISTRIBUTION_CR_INELIGIBLE' END) CATEGORY, "
					+ "(CASE WHEN DOC_TYPE = 'INV' THEN '5' WHEN DOC_TYPE = 'CR' "
					+ "THEN '8' WHEN DOC_TYPE in ('RNV','RCR') THEN '9'END) TABLE_NUMBER, "
					+ "TRIM(', ' FROM IFNULL(GFN.INFO_ERROR_CODE_ASP,'')) INFO_CODE_ASP, "
					+ "TRIM(', ' FROM IFNULL(GFN.INFO_ERROR_DESCRIPTION_ASP,'')) "
					+ "INFO_DESCRIPTION_ASP, 'FILE_UPLOAD' AS UPLOAD_SOURCE, "
					+ "FIL.CREATED_BY AS SOURCE_ID, FIL.FILE_NAME, FIL.CREATED_ON "
					+ "FROM GSTR6_ISD_DISTRIBUTION GID INNER JOIN FILE_STATUS FIL "
					+ "ON GID.FILE_ID = FIL.ID LEFT JOIN "
					+ "UTF_GSTR6_ISD_DISTRIBUTION_ERROR_INFO() GFN "
					+ "ON GID.AS_ENTERED_ID = GFN.DOC_HEADER_ID  "
					+ buildQuery 
					+ "AND GID.IS_DELETE=FALSE AND GID.SUPPLY_TYPE IS NULL " ;

		}
	

	public String dateChange(Timestamp oldDate)
	{
		  DateTimeFormatter formatter = null;
			String dateTime = oldDate.toString();
			char ch = 'T';
			dateTime = dateTime.substring(0, 10) + ch
					+ dateTime.substring(10 + 1);
			String s1 = dateTime.substring(0, 19);
			formatter = DateTimeFormatter
					.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

			LocalDateTime dateTimes = LocalDateTime.parse(s1, formatter);

			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dateTimes);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);
			return newdate;
		
	}

}