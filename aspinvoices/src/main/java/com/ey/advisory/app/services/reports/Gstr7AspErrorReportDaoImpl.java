/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr7ConsolidatedAspErrRecords;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.common.GenUtil;
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

@Component("Gstr7AspErrorReportDaoImpl")
public class Gstr7AspErrorReportDaoImpl implements Gstr7AspErrorReportsDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr7AspErrorReportDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public List<Object> getGstr7AspReports(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		// String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		/*String ProfitCenter = null;
		String plant = null;
		String purchase = null;
		String division = null;
		String location = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;*/
		String GSTIN = null;

		/*List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> purchaseList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;*/
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

				/*if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
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
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (!dataSecAttrs.get(OnboardingConstant.PO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purchaseList = dataSecAttrs.get(OnboardingConstant.PO);
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
				}*/
			}
		}

		StringBuilder buildQuery = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND TDS_DEDUCTOR_GSTIN  IN :gstinList");

			}
		}
		/*if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
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
				buildQuery.append(" AND PURCHASE_ORGANIZATION IN :salesList");

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQuery.append(" AND DIVISION IN :divisionList");

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
		}*/

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND TDS.DERIVED_RET_PERIOD = :taxperiod ");

		}

		String queryStr = createApiProcessedQueryString(buildQuery.toString());
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
		/*if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
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
		if (purchase != null && !purchase.isEmpty()) {
			if (purchaseList != null && !purchaseList.isEmpty()
					&& purchaseList.size() > 0) {
				q.setParameter("purchaseList", purchaseList);
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
		}*/

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertProcessed(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr7ConsolidatedAspErrRecords convertProcessed(Object[] arr) {
		Gstr7ConsolidatedAspErrRecords obj = new Gstr7ConsolidatedAspErrRecords();

		String errDesc = null;
		String errCode = (arr[0] != null ? arr[0].toString() : null);

		if (!Strings.isNullOrEmpty(errCode)) {
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errDesc = ErrorMasterUtil.getErrorInfo(errCodeList, "GSTR7");
		}
		obj.setAspError(errDesc);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setActionType(arr[2] != null ? arr[2].toString() : null);
		obj.settDSDeductorGSTIN(arr[3] != null ? arr[3].toString() : null);
		obj.setOriginalTDSDeducteeGSTIN(
				arr[4] != null ? arr[4].toString() : null);
		obj.setOriginalReturnPeriod(arr[5] != null ? arr[5].toString() : null);
		obj.setOriginalGrossAmount(arr[6] != null ? arr[6].toString() : null);
		obj.settDSDeducteeGSTIN(arr[7] != null ? arr[7].toString() : null);
		obj.setGrossAmount(arr[8] != null ? arr[8].toString() : null);
		obj.settDSIGST(arr[9] != null ? arr[9].toString() : null);
		obj.settDSCGST(arr[10] != null ? arr[10].toString() : null);
		obj.settDSSGST(arr[11] != null ? arr[11].toString() : null);
		obj.setContractNumber(arr[12] != null ? arr[12].toString() : null);
		obj.setContractDate(arr[13] != null ? arr[13].toString() : null);
		obj.setContractValue(arr[14] != null ? arr[14].toString() : null);
		obj.setPaymentAdviceNumber(arr[15] != null ? arr[15].toString() : null);
		obj.setPaymentAdviceDate(arr[16] != null ? arr[16].toString() : null);
		obj.setDocumentNumber(arr[17] != null ? arr[17].toString() : null);
		obj.setDocumentDate(arr[18] != null ? arr[18].toString() : null);
		obj.setInvoiceValue(arr[19] != null ? arr[19].toString() : null);
		obj.setPlantCode(arr[20] != null ? arr[20].toString() : null);
		obj.setDivision(arr[21] != null ? arr[21].toString() : null);
		obj.setPurchaseOrganisation(
				arr[22] != null ? arr[22].toString() : null);
		obj.setProfitCentre1(arr[23] != null ? arr[23].toString() : null);
		obj.setProfitCentre2(arr[24] != null ? arr[24].toString() : null);
		obj.setUserDefinedField1(arr[25] != null ? arr[25].toString() : null);
		obj.setUserDefinedField2(arr[26] != null ? arr[26].toString() : null);
		obj.setUserDefinedField3(arr[27] != null ? arr[27].toString() : null);
		obj.setSource(arr[28] != null ? arr[28].toString() : null);
		obj.setUserId(arr[29] != null ? arr[29].toString() : null);
		obj.setFileId(arr[30] != null ? arr[30].toString() : null);
		obj.setFileName(arr[31] != null ? arr[31].toString() : null);
		//obj.setUploadDataDateTime(arr[32] != null ? arr[32].toString() : null);
		if (arr[32] == null || arr[32] == "null") {
			obj.setUploadDataDateTime("");
		} else {
			Timestamp date = (Timestamp) arr[32];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);

			obj.setUploadDataDateTime(newdate);
		}
		return obj;
	}

	private String createApiProcessedQueryString(String buildQuery) {

		return "SELECT TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') ) "
				+ "AS ERROR_CODE_ASP, TDS.RETURN_PERIOD,ACTION_TYPE, "
				+ "TDS_DEDUCTOR_GSTIN,ORG_TDS_DEDUCTEE_GSTIN,ORG_RETURN_PERIOD,"
				+ "ORG_GROSS_AMT, NEW_TDS_DEDUCTEE_GSTIN,"
				+ "NEW_GROSS_AMT,IGST_AMT,"
				+ "CGST_AMT,SGST_AMT, CONTRACT_NUMBER,"
				+ "CONTRACT_DATE,CONTRACT_VALUE,PAYMENT_ADV_NUM,"
				+ "PAYMENT_ADV_DATE, DOC_NUM,DOC_DATE,INVOICE_VALUE, "
				+ "PLANT_CODE,DIVISION,PURCHASE_ORGANIZATION ,"
				+ "PROFIT_CENTRE1,PROFIT_CENTRE2, USERDEFINED_FIELD1,"
				+ "USERDEFINED_FIELD2,USERDEFINED_FIELD3, FIL.SOURCE SOURCE,"
				+ "FIL.CREATED_BY USER_ID, FIL.ID FILE_ID,"
				+ "FIL.FILE_NAME FILE_NAME,FIL.UPLOAD_START_TIME "
				+ "FROM GSTR7_AS_ENTERED_TDS TDS LEFT OUTER JOIN "
				+ "(SELECT DOC_HEADER_ID,INV_KEY,FILE_ID,"
				+ "STRING_AGG(ERROR_CODE_ASP,',') ERROR_CODE_ASP "
				+ "FROM (SELECT DOC_HEADER_ID,INV_KEY,FILE_ID, "
				+ "(CASE WHEN ERROR_TYPE='ERR' THEN ERROR_CODE END) "
				+ "AS ERROR_CODE_ASP FROM "
				+ "GSTR7_DOC_ERROR WHERE ERROR_SOURCE='WEBUPLOAD') " + " GROUP BY "
				+ "DOC_HEADER_ID,INV_KEY,FILE_ID) ERR ON "
				+ "TDS.ID= ERR.DOC_HEADER_ID AND TDS.FILE_ID=ERR.FILE_ID "
				+ "AND ERR.INV_KEY=TDS.TDS_INVKEY LEFT OUTER JOIN "
				+ "FILE_STATUS FIL ON TDS.FILE_ID=FIL.ID LEFT OUTER JOIN "
				+ "GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON "
				+ "GSTNBATCH.ID = TDS.BATCH_ID WHERE "
				+ "TDS.IS_DELETE=FALSE AND IS_ERROR=TRUE  " + buildQuery;

	}
}
