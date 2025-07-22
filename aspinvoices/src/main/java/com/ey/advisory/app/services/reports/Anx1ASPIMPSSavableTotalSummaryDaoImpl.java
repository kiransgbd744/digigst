/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1AspImpsTotSumDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Anx1ASPIMPSSavableTotalSummaryDaoImpl")
public class Anx1ASPIMPSSavableTotalSummaryDaoImpl
		implements Anx1ASPIMPSSavableSummaryDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ASPIMPSSavableTotalSummaryDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getAnx1IMPSSavableReports(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String ProfitCenter = null;
		String plant = null;
		String purchase = null;
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
		List<String> purchaseList = null;
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
				}
			}
		}

		StringBuilder buildQuery = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" WHERE CUST_GSTIN IN :gstinList");

			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildQuery.append(" AND PROFIT_CENTRE IN :pcList");

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildQuery.append(" AND PLANT IN :plantList");

			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purchaseList != null && purchaseList.size() > 0) {
				buildQuery.append(" AND PURCHAGE_ORG IN :salesList");

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
				buildQuery.append(" AND USER_ACCESS1 IN :ud1List");

			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" AND USER_ACCESS2 IN :ud2List");

			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" AND USER_ACCESS3 IN :ud3List");

			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" AND USER_ACCESS4 IN :ud4List");

			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" AND USER_ACCESS5 IN :ud5List");

			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" AND USER_ACCESS6 IN :ud6List");

			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod ");

		}

		String queryStr = createIMPSSavableTotalQueryString(
				buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
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
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxperiod", derivedRetPeriod);
		}

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertImpsSummary(o))
				.collect(Collectors.toCollection(ArrayList::new));

	}

	private Anx1AspImpsTotSumDto convertImpsSummary(Object[] arr) {
		Anx1AspImpsTotSumDto obj = new Anx1AspImpsTotSumDto();

		obj.setReturnType(arr[0] != null ? arr[0].toString() : null);
		obj.setRecipientGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
		obj.setTransactionFlag(arr[3] != null ? arr[3].toString() : null);
		obj.setSupplierGSTINorPAN(arr[4] != null ? arr[4].toString() : null);
		obj.setSupplierName(arr[5] != null ? arr[5].toString() : null);
		obj.setDiffPercentageFlag(arr[6] != null ? arr[6].toString() : null);
		obj.setSec7ofIGSTFlag(arr[7] != null ? arr[7].toString() : null);
		obj.setAutoPopltToRefund(arr[8] != null ? arr[8].toString() : null);
		obj.setPos(arr[9] != null ? arr[9].toString() : null);
		obj.setHsnOrSac(arr[10] != null ? arr[10].toString() : null);
		obj.setTaxableValue(arr[11] != null ? arr[11].toString() : null);
		obj.setRate(arr[12] != null ? arr[12].toString() : null);
		obj.setIntegratedTaxAmount(arr[13] != null ? arr[13].toString() : null);
		obj.setTotalValue(arr[14] != null ? arr[14].toString() : null);
		obj.setCentralTaxAmount(arr[15] != null ? arr[15].toString() : null);
		obj.setStateUTTaxAmount(arr[16] != null ? arr[16].toString() : null);
		obj.setCessAmount(arr[17] != null ? arr[17].toString() : null);
		obj.setEligibilityIndicator(
				arr[18] != null ? arr[18].toString() : null);
		obj.setAvailableIGST(arr[19] != null ? arr[19].toString() : null);
		obj.setAvailableCGST(arr[20] != null ? arr[20].toString() : null);
		obj.setAvailableSGST(arr[21] != null ? arr[21].toString() : null);
		obj.setAvailableCess(arr[22] != null ? arr[22].toString() : null);
		obj.setProfitCentre(arr[23] != null ? arr[23].toString() : null);
		obj.setPlant(arr[24] != null ? arr[24].toString() : null);
		obj.setDivision(arr[25] != null ? arr[25].toString() : null);
		obj.setLocation(arr[26] != null ? arr[26].toString() : null);
		obj.setPurchaseOrganisation(
				arr[27] != null ? arr[27].toString() : null);
		obj.setUserAccess1(arr[28] != null ? arr[28].toString() : null);
		obj.setUserAccess2(arr[29] != null ? arr[29].toString() : null);
		obj.setUserAccess3(arr[30] != null ? arr[30].toString() : null);
		obj.setUserAccess4(arr[31] != null ? arr[31].toString() : null);
		obj.setUserAccess5(arr[32] != null ? arr[32].toString() : null);
		obj.setUserAccess6(arr[33] != null ? arr[33].toString() : null);
		obj.setUserdefinedfield1(arr[34] != null ? arr[34].toString() : null);
		obj.setUserdefinedfield2(arr[35] != null ? arr[35].toString() : null);
		obj.setUserdefinedfield3(arr[36] != null ? arr[36].toString() : null);
		obj.setAspInformationCode(arr[37] != null ? arr[37].toString() : null);
		obj.setAspInformationDesc(arr[38] != null ? arr[38].toString() : null);
		obj.setSaveStatus(arr[39] != null ? arr[39].toString() : null);
		obj.setGstnRefID(arr[40] != null ? arr[40].toString() : null);
		obj.setGstnRefIDTime(arr[41] != null ? arr[41].toString() : null);
		obj.setGstnErrorCode(arr[42] != null ? arr[42].toString() : null);
		obj.setGstnErrorDescription(
				arr[43] != null ? arr[43].toString() : null);
		//
		obj.setSourceID(arr[45] != null ? arr[45].toString() : null);
		obj.setFileName(arr[46] != null ? arr[46].toString() : null);
		obj.setAspDateTime(arr[47] != null ? arr[47].toString() : null);

		return obj;
	}

	private String createIMPSSavableTotalQueryString(String buildQuery) {

		return "SELECT RETURN_TYPE,CUST_GSTIN,RETURN_PERIOD,TRAN_FLAG,"
				+ "SUPPLIER_GSTIN_PAN,SUPPLIER_NAME,DIFF_PERCENT,"
				+ "SEC7_OF_IGST_FLAG,AUTOPOPULATE_TO_REFUND,POS,"
				+ "HSNORSAC,SUM(TAXABLE_VALUE),TAX_RATE,SUM(IGST_AMT),"
				+ "SUM(TOTAL_VALUE),sum(CGST_AMT),SUM(SGST_AMT),"
				+ "SUM(CESS_AMT),ELGBL_INDICATOR,sum(AVAIL_IGST),"
				+ "sum(AVAIL_CGST),sum(AVAIL_SGST),sum(AVAIL_CESS),"
				+ "PROFIT_CENTRE,PLANT,DIVISION,LOCATION,PURCHAGE_ORG,"
				+ "USER_ACCESS1,USER_ACCESS2,USER_ACCESS3,USER_ACCESS4,"
				+ "USER_ACCESS5,USER_ACCESS6,USERDEFINED1,USERDEFINED2,"
				+ "USERDEFINED3,INFO_ERROR_CODE_ASP,INFO_ERROR_DESCRIPTION_ASP,"
				+ "SAVE_STATUS,GSTIN_REF_ID,GSTIN_REF_ID_TIME,"
				+ "ERROR_CODE_GSTIN,ERROR_DESCRIPTION_GSTIN,UPLOAD_SOURCE,"
				+ "SOURCE_ID, FILE_NAME, ASP_DATE_TIME FROM "
				+ "((SELECT HDR.AN_RETURN_TYPE AS RETURN_TYPE,HDR.CUST_GSTIN,"
				+ "HDR.RETURN_PERIOD, 'IMPS' AS TRAN_FLAG,HDR.SUPPLIER_GSTIN "
				+ "AS SUPPLIER_GSTIN_PAN,HDR.CUST_SUPP_NAME AS SUPPLIER_NAME,"
				+ "HDR.DIFF_PERCENT,HDR.SECTION7_OF_IGST_FLAG AS "
				+ "SEC7_OF_IGST_FLAG,HDR.AUTOPOPULATE_TO_REFUND,HDR.POS,"
				+ "ITM.ITM_HSNSAC AS HSNORSAC,ITM.TAXABLE_VALUE,"
				+ "ITM.TAX_RATE,ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,"
				+ "SUM(IFNULL(ITM.CESS_AMT_SPECIFIC,'0')+ "
				+ "IFNULL (ITM.CESS_AMT_ADVALOREM,'0')) AS CESS_AMT,"
				+ "ITM.ELIGIBILITY_INDICATOR AS ELGBL_INDICATOR,"
				+ "ITM.AVAILABLE_IGST AS AVAIL_IGST,ITM.AVAILABLE_CGST AS "
				+ "AVAIL_CGST,ITM.AVAILABLE_SGST AS AVAIL_SGST,"
				+ "ITM.AVAILABLE_CESS AS AVAIL_CESS,ITM.PROFIT_CENTRE,"
				+ "ITM.PLANT_CODE AS PLANT,HDR.DIVISION,ITM.LOCATION,"
				+ "HDR.PURCHASE_ORGANIZATION AS PURCHAGE_ORG,"
				+ "HDR.USERACCESS1 AS USER_ACCESS1,"
				+ "HDR.USERACCESS2 AS USER_ACCESS2,HDR.USERACCESS3 "
				+ "AS USER_ACCESS3,HDR.USERACCESS4 AS USER_ACCESS4,"
				+ "HDR.USERACCESS5 AS USER_ACCESS5,HDR.USERACCESS6 "
				+ "AS USER_ACCESS6,ITM.USERDEFINED_FIELD1 AS USERDEFINED1,"
				+ "ITM.USERDEFINED_FIELD2 AS USERDEFINED2,"
				+ "ITM.USERDEFINED_FIELD3 AS USERDEFINED3,"
				+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) "
				+ "AS INFO_ERROR_CODE_ASP, TRIM(', ' FROM "
				+ "IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
				+ "AS INFO_ERROR_DESCRIPTION_ASP, case when "
				+ "HDR.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
				+ "WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND HDR.GSTN_ERROR = TRUE "
				+ "THEN 'IS_ERROR' WHEN HDR.IS_SAVED_TO_GSTN = FALSE "
				+ "AND HDR.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END "
				+ "AS SAVE_STATUS,GSB.GSTN_SAVE_REF_ID AS GSTIN_REF_ID,"
				+ "GSB.GSTN_RESP_DATE AS GSTIN_REF_ID_TIME,"
				+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_GSTIN,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_CODE_GSTIN,'')) AS ERROR_CODE_GSTIN,"
				+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_GSTIN,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_DESCRIPTION_GSTIN,'')) "
				+ "AS ERROR_DESCRIPTION_GSTIN,FIL.SOURCE AS UPLOAD_SOURCE,"
				+ "FIL.CREATED_BY AS SOURCE_ID,FIL.FILE_NAME,HDR.CREATED_ON "
				+ "AS ASP_DATE_TIME,ITM.LINE_ITEM_AMT AS TOTAL_VALUE,"
				+ "HDR.DERIVED_RET_PERIOD "
				+ "FROM ANX_INWARD_DOC_HEADER HDR LEFT OUTER JOIN "
				+ "TF_INWARD_HEADER_GSTIN_ASP_ERROR_INFO () ERRH ON "
				+ "HDR.ID= ERRH.DOC_HEADER_ID INNER JOIN ANX_INWARD_DOC_ITEM "
				+ "ITM ON HDR.ID=ITM.DOC_HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT OUTER JOIN TF_INWARD_ITEM_GSTIN_ASP_ERROR_INFO () "
				+ "ERRI ON ITM.DOC_HEADER_ID= ERRI.DOC_HEADER_ID AND "
				+ "IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' ) "
				+ "AND IFNULL(ITM.ITM_NO,'0' ) = IFNULL(ERRI.ITM_NO,'0' ) "
				+ "INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH "
				+ "GSB ON GSB.ID = HDR.BATCH_ID WHERE IS_ERROR=FALSE "
				+ "AND HDR.AN_RETURN_TYPE IN ('ANX1') AND "
				+ "AN_TABLE_SECTION = '3I' group by HDR.AN_RETURN_TYPE,"
				+ "HDR.CUST_GSTIN,HDR.RETURN_PERIOD,HDR.DOC_TYPE,"
				+ "HDR.CUST_SUPP_NAME,HDR.DERIVED_RET_PERIOD,HDR.DIFF_PERCENT,"
				+ "HDR.SECTION7_OF_IGST_FLAG,HDR.SUPPLIER_GSTIN,"
				+ "HDR.AUTOPOPULATE_TO_REFUND,ITM.TAXABLE_VALUE,ITM.TAX_RATE,"
				+ "ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,"
				+ "ITM.ELIGIBILITY_INDICATOR,ITM.AVAILABLE_IGST,"
				+ "ITM.AVAILABLE_CGST,ITM.AVAILABLE_SGST,ITM.AVAILABLE_CESS,"
				+ "ITM.PROFIT_CENTRE,ITM.PLANT_CODE,HDR.DIVISION,"
				+ "ITM.LOCATION,HDR.PURCHASE_ORGANIZATION,HDR.USERACCESS1,"
				+ "HDR.USERACCESS2, HDR.USERACCESS3,HDR.USERACCESS4,"
				+ "HDR.USERACCESS5,HDR.USERACCESS6,ITM.USERDEFINED_FIELD1,"
				+ "ITM.USERDEFINED_FIELD2,IS_SAVED_TO_GSTN,GSTN_ERROR,"
				+ "ITM.USERDEFINED_FIELD3,ERRH.INFO_ERROR_CODE_ASP,"
				+ "ERRI.INFO_ERROR_CODE_ASP,ERRH.INFO_ERROR_DESCRIPTION_ASP,"
				+ "ERRI.INFO_ERROR_DESCRIPTION_ASP,GSB.GSTN_SAVE_REF_ID,"
				+ "GSB.GSTN_RESP_DATE,ERRH.ERROR_CODE_GSTIN,ERRI.ERROR_CODE_GSTIN,"
				+ "ERRH.ERROR_DESCRIPTION_GSTIN,ERRI.ERROR_DESCRIPTION_GSTIN,"
				+ "FIL.SOURCE,FIL.CREATED_BY,FIL.FILE_NAME,HDR.CREATED_ON,"
				+ "DOC_NUM,ITM.ITM_NO,HDR.POS,ITM.ITM_HSNSAC,"
				+ "ITM.LINE_ITEM_AMT ORDER BY DOC_NUM,ITM.ITM_NO ) "
				+ " UNION ALL "
				+ "(SELECT HI.RETURN_TYPE AS AN_RETURN_TYPE,HI.CUST_GSTIN,"
				+ "HI.RETURN_PERIOD,HI.TRAN_FLAG,HI.SUPPLIER_GSTIN_PAN,"
				+ "HI.SUPPLIER_NAME,HI.DIFF_PERCENT,HI.SEC7_OF_IGST_FLAG,"
				+ "HI.AUTOPOPULATE_TO_REFUND,HI.POS,HI.HSNORSAC,"
				+ "HI.TAXABLE_VALUE,HI.TAX_RATE,HI.IGST_AMT,HI.CGST_AMT,"
				+ "HI.SGST_AMT,HI.CESS_AMT,HI.ELGBL_INDICATOR,HI.AVAIL_IGST,"
				+ "HI.AVAIL_CGST,AVAIL_SGST,HI.AVAIL_CESS,HI.PROFIT_CENTER,"
				+ "HI.PLANT,HI.DIVISION,HI.LOCATION,HI.PURCHAGE_ORG,"
				+ "HI.USER_ACCESS1,HI.USER_ACCESS2,HI.USER_ACCESS3,"
				+ "HI.USER_ACCESS4,HI.USER_ACCESS5,HI.USER_ACCESS6,"
				+ "HI.USERDEFINED1,HI.USERDEFINED2,HI.USERDEFINED3,"
				+ "ERR.INFO_ERROR_CODE_ASP AS INFO_CODE_ASP,"
				+ "ERR.INFO_ERROR_DESCRIPTION_ASP AS INFO_DESCRIPTION_ASP,"
				+ "CASE WHEN HI.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
				+ "WHEN HI.IS_SAVED_TO_GSTN = FALSE AND HI.GSTN_ERROR = TRUE "
				+ "THEN 'IS_ERROR' WHEN HI.IS_SAVED_TO_GSTN = FALSE "
				+ "AND HI.GSTN_ERROR = FALSE THEN 'NOT_SAVED' "
				+ "END AS SAVE_STATUS, BT.GSTN_SAVE_REF_ID AS GSTIN_REF_ID,"
				+ "BT.GSTN_RESP_DATE AS GSTIN_REF_ID_TIME, "
				+ " '' AS ERROR_CODE_GSTIN,'' AS ERROR_DESCRIPTION_GSTIN,"
				+ "FIL.SOURCE AS UPLOAD_SOURCE, HI.CREATED_BY AS SOURCE_ID,"
				+ "FIL.FILE_NAME,HI.CREATED_ON AS ASP_DATE_TIME,HI.TOTAL_VALUE,"
				+ "HI.DERIVED_RET_PERIOD "
				+ "FROM ANX_PROCESSED_3H_3I HI LEFT OUTER JOIN "
				+ "TF_GSTR1_ERROR_INFO () ERR ON HI.AS_ENTERED_ID= ERR.COMMON_ID "
				+ "AND HI.FILE_ID=ERR.FILE_ID AND ERR.INV_KEY = HI.INVKEY_3H_3I "
				+ "LEFT OUTER JOIN FILE_STATUS FIL ON "
				+ "HI.FILE_ID=FIL.ID AND ERR.FILE_ID=FIL.ID LEFT OUTER JOIN "
				+ "GSTR1_GSTN_SAVE_BATCH BT ON HI.BATCH_ID = BT.ID AND "
				+ "HI.FILE_ID = BT.ID AND "
				+ "HI.DERIVED_RET_PERIOD = BT.DERIVED_RET_PERIOD "
				+ "WHERE HI.IS_DELETE = FALSE AND HI.TRAN_FLAG ='IMPS'))  "
				+ buildQuery + " GROUP BY "
				+ "RETURN_TYPE,CUST_GSTIN,RETURN_PERIOD,TRAN_FLAG,"
				+ "SUPPLIER_GSTIN_PAN,SUPPLIER_NAME,DIFF_PERCENT,"
				+ "SEC7_OF_IGST_FLAG,AUTOPOPULATE_TO_REFUND,POS,"
				+ "HSNORSAC,TAX_RATE,ELGBL_INDICATOR,PROFIT_CENTRE,PLANT,"
				+ "DIVISION,LOCATION,PURCHAGE_ORG,USER_ACCESS1,USER_ACCESS2,"
				+ "USER_ACCESS3,USER_ACCESS4,USER_ACCESS5,USER_ACCESS6,"
				+ "USERDEFINED1,USERDEFINED2,USERDEFINED3,INFO_ERROR_CODE_ASP,"
				+ "INFO_ERROR_DESCRIPTION_ASP, SAVE_STATUS,GSTIN_REF_ID,"
				+ "GSTIN_REF_ID_TIME,ERROR_CODE_GSTIN,ERROR_DESCRIPTION_GSTIN,"
				+ "UPLOAD_SOURCE,SOURCE_ID, FILE_NAME, ASP_DATE_TIME ";
	}
}
