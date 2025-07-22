/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.ItcTotalRecords;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

import com.google.common.base.Strings;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("ITC04ConsolidatedGstnErrorReportDaoImpl")
public class ITC04ConsolidatedGstnErrorReportDaoImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ITC04ConsolidatedGstnErrorReportDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	public List<ItcTotalRecords> getGstnErrorReport(
			Gstr6SummaryRequestDto request) {
		String taxperiod = request.getTaxPeriod();
		List<String> tableno = request.getTableno();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		String ProfitCenter = null;
		String ProfitCenter2 = null;
		String plant = null;
		String sales = null;
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
		List<String> pc2List = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
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
				if (key.equalsIgnoreCase(OnboardingConstant.PC2)) {
					ProfitCenter2 = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC2).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC2)
									.size() > 0) {
						pc2List = dataSecAttrs.get(OnboardingConstant.PC2);
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

				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (!dataSecAttrs.get(OnboardingConstant.PO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purchaseList = dataSecAttrs.get(OnboardingConstant.PO);
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

		StringBuilder buildQuery = new StringBuilder();

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND HDR.QRETURN_PERIOD = :taxperiod");
		}
		
		if (tableno != null && tableno.size() > 0) {
			buildQuery.append(" AND TABLE_NUMBER IN :tableno");
		}
		
		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildQuery.append(" AND PROFIT_CENTRE1 IN :pcList");
			}
		}
		if (ProfitCenter2 != null && !ProfitCenter2.isEmpty()) {
			if (pc2List != null && pc2List.size() > 0) {
				buildQuery.append(" AND PROFIT_CENTRE2 IN :pc2List");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildQuery.append(" AND PLANT_CODE IN :plantList");
			}
		}

		if (purchase != null && !purchase.isEmpty()) {
			if (purchaseList != null && purchaseList.size() > 0) {
				buildQuery
						.append(" AND PURCHASE_ORGANIZATION IN :purchaseList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildQuery.append(" AND SALES_ORGANIZATION IN :salesList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildQuery.append(" AND DISTRIBUTION_CHANNEL IN :distList");
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
		}

		String queryStr = AspProcessedUploadedQueryString(
				buildQuery.toString());

		Query q = entityManager.createNativeQuery(queryStr);

		if (taxperiod != null) {
			q.setParameter("taxperiod", taxperiod);
		}
		
		if (tableno != null && tableno.size()>0) {
			q.setParameter("tableno", tableno);
		}

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
		if (ProfitCenter2 != null && !ProfitCenter2.isEmpty()) {
			if (pc2List != null && !pc2List.isEmpty() && pc2List.size() > 0) {
				q.setParameter("pc2List", pc2List);
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
		return list.parallelStream().map(o -> convertVerticalTotal(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private ItcTotalRecords convertVerticalTotal(Object[] arr) {
		ItcTotalRecords obj = new ItcTotalRecords();

		String errDesc = null;
		String infoDesc = null;

		String errCode = (arr[0] != null) ? arr[0].toString() : null;

		if (!Strings.isNullOrEmpty(errCode)) {
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errDesc = ErrorMasterUtil.getErrorInfo(errCodeList, "ITC04");
		}

		// obj.setAspErrorCode(errCode);
		obj.setAspErrorDescription(errDesc);
		String infoCode = (arr[2] != null) ? arr[2].toString() : null;

		if (!Strings.isNullOrEmpty(infoCode)) {
			String[] infoCodes = infoCode.split(",");
			List<String> infoCodeList = Arrays.asList(infoCodes);
			infoDesc = ErrorMasterUtil.getErrorInfo(infoCodeList, "ITC04");
		}

		obj.setAspInformationDescription(infoDesc);
		obj.setTableNumber(arr[4] != null ? arr[4].toString() : null);
		obj.setActionType(arr[5] != null ? arr[5].toString() : null);
		obj.setFy(arr[6] != null ? arr[6].toString() : null);
		obj.setReturnPeriod(arr[7] != null ? arr[7].toString() : null);
		obj.setSupplierGstin(arr[8] != null ? arr[8].toString() : null);
		obj.setDeliveryChallanNumber(arr[9] != null ? arr[9].toString() : null);
		obj.setDeliveryChallanDate(fmtDate(arr[10]));
		obj.setjWDeliveryChallanNumber(
				arr[11] != null ? arr[11].toString() : null);
		obj.setjWDeliveryChallanDate(fmtDate(
				arr[12]));
		obj.setGoodsReceivingDate(fmtDate(arr[13]));
		obj.setInvoiceNumber(arr[14] != null ? arr[14].toString() : null);
		obj.setInvoiceDate(fmtDate(arr[15]));
		obj.setJobWorkerGSTIN(arr[16] != null ? arr[16].toString() : null);
		obj.setJobWorkerStateCode(arr[17] != null ? arr[17].toString() : null);
		obj.setJobWorkerType(arr[18] != null ? arr[18].toString() : null);
		obj.setJobWorkerID(arr[19] != null ? arr[19].toString() : null);
		obj.setJobWorkerName(arr[20] != null ? arr[20].toString() : null);
		obj.setTypeOfGoods(arr[21] != null ? arr[21].toString() : null);
		obj.setItemSerialNumber(arr[22] != null ? arr[22].toString() : null);
		obj.setProductDescription(arr[23] != null ? arr[23].toString() : null);
		obj.setProductCode(arr[24] != null ? arr[24].toString() : null);
		obj.setNatureOfJW(arr[25] != null ? arr[25].toString() : null);
		;
		obj.setHsn(arr[26] != null ? arr[26].toString() : null);
		obj.setUqc(arr[27] != null ? arr[27].toString() : null);
		obj.setQuantity(removeDecimalDigit(arr[28]));
		obj.setLossesUQC(arr[29] != null ? arr[29].toString() : null);
		obj.setLossesQuantity(removeDecimalDigit(arr[30]));
		obj.setItemAssessableAmount(appendDoubleDecimalDigit(
				arr[31]));
		obj.setiGSTRate(appendDoubleDecimalDigit(arr[32]));
		obj.setiGSTAmount(appendDoubleDecimalDigit(arr[33]));
		obj.setcGSTRate(appendDoubleDecimalDigit(arr[34]));
		obj.setcGSTAmount(appendDoubleDecimalDigit(arr[35]));
		obj.setsGSTRate(appendDoubleDecimalDigit(arr[36]));
		obj.setsGSTAmount(appendDoubleDecimalDigit(arr[37]));
		obj.setCessAdvaloremRate(appendDoubleDecimalDigit(arr[38]));
		obj.setCessAdvaloremAmount(appendDoubleDecimalDigit(arr[39]));
		obj.setCessSpecificRate(appendDoubleDecimalDigit(arr[40]));
		obj.setCessSpecificAmount(appendDoubleDecimalDigit(arr[41]));
		obj.setStateCessAdvaloremRate(appendDoubleDecimalDigit(
				arr[42]));
		obj.setStateCessAdvaloremAmount(appendDoubleDecimalDigit(
				arr[43]));
		obj.setStateCessSpecificRate(appendDoubleDecimalDigit(
				arr[44]));
		obj.setStateCessSpecificAmount(appendDoubleDecimalDigit(
				arr[45]));
		obj.setTotalValue(appendDoubleDecimalDigit(arr[46]));
		obj.setPostingDate(arr[47] != null ? arr[47].toString() : null);
		obj.setUserId(arr[48] != null ? arr[48].toString() : null);
		obj.setCompanyCode(arr[49] != null ? arr[49].toString() : null);
		obj.setSourceIdentifier(arr[50] != null ? arr[50].toString() : null);
		obj.setSourcefileName(arr[51] != null ? arr[51].toString() : null);
		obj.setPlant(arr[52] != null ? arr[52].toString() : null);
		obj.setDivision(arr[53] != null ? arr[53].toString() : null);
		obj.setProfitCentre1(arr[54] != null ? arr[54].toString() : null);
		obj.setProfitCentre2(arr[55] != null ? arr[55].toString() : null);
		obj.setAccountingVoucherNumber(
				arr[56] != null ? arr[56].toString() : null);
		obj.setAccountingVoucherDate(
				arr[57] != null ? arr[57].toString() : null);
		obj.setUserDefinedField1(arr[58] != null ? arr[58].toString() : null);
		obj.setUserDefinedField2(arr[59] != null ? arr[59].toString() : null);
		obj.setUserDefinedField3(arr[60] != null ? arr[60].toString() : null);
		obj.setUserDefinedField4(arr[61] != null ? arr[61].toString() : null);
		obj.setUserDefinedField5(arr[62] != null ? arr[62].toString() : null);
		obj.setUserDefinedField6(arr[63] != null ? arr[63].toString() : null);
		obj.setUserDefinedField7(arr[64] != null ? arr[64].toString() : null);
		obj.setUserDefinedField8(arr[65] != null ? arr[65].toString() : null);
		obj.setUserDefinedField9(arr[66] != null ? arr[66].toString() : null);
		obj.setUserDefinedField10(arr[67] != null ? arr[67].toString() : null);
		obj.setUserID(arr[68] != null ? arr[68].toString() : null);
		obj.setFileID(arr[69] != null ? arr[69].toString() : null);
		obj.setFileName(arr[70] != null ? arr[70].toString() : null);
		obj.setSource(arr[71] != null ? arr[71].toString() : null);
		obj.setGstnStatus(arr[72] != null ? arr[72].toString() : null);
		obj.setGstnRefid(arr[73] != null ? arr[73].toString() : null);
		obj.setGstnRefidDateTime(arr[74] != null ? arr[74].toString() : null);
		obj.setgSTNErrorCode(arr[75] != null ? arr[75].toString() : null);
		obj.setgSTNErrorDesc(arr[76] != null ? arr[76].toString() : null);
		return obj;

	}

	private String AspProcessedUploadedQueryString(String buildQuery) {

		return "SELECT TRIM(', ' FROM IFNULL(HDR.ERROR_CODES,'') ||','|| "
				+ "IFNULL(ITM.ERROR_CODES,'')) AS ERROR_CODE_ASP, "
				+ " '' AS ERROR_DESCRIPTION_ASP, TRIM(', ' FROM "
				+ "IFNULL(HDR.INFORMATION_CODES,'') ||','|| "
				+ "IFNULL(ITM.INFORMATION_CODES,'')) AS INFO_ERROR_CODE_ASP, "
				+ " '' AS INFO_ERROR_DESCRIPTION_ASP, TABLE_NUMBER ,"
				+ "ACTION_TYPE ,FI_YEAR,HDR.RETURN_PERIOD,"
				+ "HDR.SUPPLIER_GSTIN, DELIVERY_CHALLAN_NO,"
				+ "DELIVERY_CHALLAN_DATE,JW_DELIVERY_CHALLAN_NO,"
				+ "JW_DELIVERY_CHALLAN_DATE, GOODS_RECEIVING_DATE,INV_NUM ,"
				+ "INV_DATE,JW_GSTIN ,JW_STATE_CODE ,JW_WORKER_TYPE,JW_ID,"
				+ "JW_NAME, GOODS_TYPE,ITM_SER_NO,PRODUCT_DESC,PRODUCT_CODE,"
				+ "NATURE_OF_JW, ITM_HSNSAC ,ITM_UQC,ITM_QTY ,LOSSES_UQC,"
				+ "LOSSES_QTY, ITM.TAXABLE_VALUE ITM_ACCESSABLE_AMT ,"
				+ "IGST_RATE,ITM.IGST_AMT,CGST_RATE,ITM.CGST_AMT,SGST_RATE,"
				+ "ITM.SGST_AMT, CESS_RATE_ADVALOREM,ITM.CESS_AMT_ADVALOREM,"
				+ "CESS_RATE_SPECIFIC ,ITM.CESS_AMT_SPECIFIC, "
				+ "STATE_CESS_RATE_ADVALOREM,ITM.STATE_CESS_AMT_ADVALOREM,"
				+ "STATE_CESS_RATE_SPECIFIC,ITM.STATE_CESS_AMT_SPECIFIC,"
				+ "ITM.TOTAL_VALUE, POSTING_DATE,USER_ID,COMPANY_CODE,"
				+ "SOURCE_IDENTIFIER, SOURCE_FILE_NAME , PLANT_CODE ,"
				+ "DIVISION,PROFIT_CENTRE1,PROFIT_CENTRE2, ACCOUNTING_VOUCHER_NUM,"
				+ "ACCOUNTING_VOUCHER_DATE, USERDEFINED_FIELD1,USERDEFINED_FIELD2,"
				+ "USERDEFINED_FIELD3,USERDEFINED_FIELD4,USERDEFINED_FIELD5, "
				+ "USERDEFINED_FIELD6,USERDEFINED_FIELD7,USERDEFINED_FIELD8,"
				+ "USERDEFINED_FIELD9,USERDEFINED_FIELD10, "
				+ "FIL.CREATED_BY USERID, FIL.ID FILE_ID,FIL.FILE_NAME FILE_NAME,"
				+ "(case when DATAORIGINTYPECODE IN ('A','AI') "
				+ "then 'ERP_PUSH' when DATAORIGINTYPECODE IN ('E','EI') THEN FIL.SOURCE END )SOURCE, "
				+ "(CASE WHEN IS_SAVED_TO_GSTN = TRUE "
				+ "THEN 'IS_SAVED' WHEN IS_SAVED_TO_GSTN = FALSE "
				+ "AND GSTN_ERROR = TRUE THEN 'GSTN_ERROR' "
				+ "WHEN IS_SAVED_TO_GSTN = FALSE AND GSTN_ERROR = FALSE "
				+ "THEN 'NOT_SAVED' END) AS GSTN_SAVE_STATUS, "
				+ "GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, "
				+ "GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, "
				+ "TRIM(', ' FROM IFNULL( HDR.GSTN_ERROR_CODE,'')) "
				+ "AS GSTIN_ERROR_CODE, TRIM(', ' FROM IFNULL( HDR.GSTN_ERROR_DESC,'')) "
				+ "AS GSTIN_ERROR_DESCRIPTION_ASP,HDR.QRETURN_PERIOD "
				+ "FROM ITC04_HEADER "
				+ "HDR INNER JOIN ITC04_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID "
				+ "AND HDR.RETURN_PERIOD=ITM.RETURN_PERIOD "
				+ "LEFT OUTER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH "
				+ "ON GSTNBATCH.ID = HDR.BATCH_ID "
				+ "WHERE GSTN_ERROR=TRUE AND GSTN_SAVE_STATUS='PE' "
				+ " AND GSTN_ERROR=TRUE " + buildQuery;
	}
	

	private String removeDecimalDigit(Object value) {
		if (value != null) {
			if (value instanceof BigDecimal) {
				String val = ((BigDecimal) value)
						.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
				String[] s = val.split("\\.");
				if (s.length == 2) {
					return s[0];
				} else {
					return val;
				}
			} else if (value instanceof String) {
				String[] s = ((String) value).split("\\.");
				if (s.length == 2) {
					return s[0];
				} else {
					return (String) value;
				}
			}
			return value.toString();
		}
		return "0";
	}

	private String appendDoubleDecimalDigit(Object value) {
		if (value != null) {
			if (value instanceof BigDecimal) {
				String val = ((BigDecimal) value)
						.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
				String[] s = val.split("\\.");
				if (s.length == 2) {
					if (s[1].length() == 1)
						return (s[0] + "." + s[1] + "0");
					else if (s[1].length() == 0)
						return s[0] + ".00";
					else {
						return s[0] + "." + s[1].substring(0, 2);
					}
				} else
					return "'" + (s[0] + ".00");
			} else if (value instanceof String) {
				String[] s = ((String) value).split("\\.");
				if (s.length == 2) {
					if (s[1].length() == 1)
						return (s[0] + "." + s[1] + "0");
					else if (s[1].length() == 0)
						return s[0] + ".00";
					else {
						return s[0] + "." + s[1].substring(0, 2);
					}
				} else
					return "'" + (s[0] + ".00");
			}
		}
		return "0.00";
	}

	public static String fmtDate(Object ldt) {
		if (ldt != null) {
			if (ldt instanceof LocalDate) {
				DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				return f.format((LocalDate) ldt);
			} else if (ldt instanceof String) {
				String[] s = ((String) ldt).split("-|/");
				if (s.length == 3) {
					if (s[0].length() == 4)
						return s[2] + "-" + s[1] + "-" + s[0];
					else if (s[2].length() == 4)
						return s[0] + "-" + s[1] + "-" + s[2];
				} else {
					return null;
				}
			} else {
				String dateString = ldt.toString();
				String[] s = dateString.split("-|/");
				if (s.length == 3) {
					if (s[0].length() == 4)
						return s[2] + "-" + s[1] + "-" + s[0];
					else if (s[2].length() == 4)
						return s[0] + "-" + s[1] + "-" + s[2];
				} else {
					return null;
				}
			}
		}
		return null;
	}
}
