/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.ItcTotalRecords;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("ITC04AspProcessSavableReportDaoImpl")
public class ITC04AspProcessSavableReportDaoImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ITC04AspProcessSavableReportDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<ItcTotalRecords> getAspUploadedReport(
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
				buildQuery.append(" AND PROFIT_CENTRE2 IN :pcList");
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

		if (tableno != null && tableno.size() > 0) {
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

		/*
		 * String errDesc = null; String infoDesc = null;
		 * 
		 * 
		 * String errCode = (arr[0] != null) ? arr[0].toString() : null;
		 * 
		 * if (!Strings.isNullOrEmpty(errCode)) { String[] errorCodes =
		 * errCode.split(","); List<String> errCodeList =
		 * Arrays.asList(errorCodes); errDesc =
		 * ErrorMasterUtil.getErrorInfo(errCodeList, "ITC04RAW"); }
		 * 
		 * // obj.setAspErrorCode(errCode); obj.setAspErrorDescription(errDesc);
		 * 
		 * String infoCode = (arr[0] != null) ? arr[0].toString() : null;
		 * 
		 * if (!Strings.isNullOrEmpty(infoCode)) { String[] infoCodes =
		 * infoCode.split(","); List<String> infoCodeList =
		 * Arrays.asList(infoCodes); infoDesc =
		 * ErrorMasterUtil.getErrorInfo(infoCodeList, "ITC04"); }
		 * 
		 * obj.setAspInformationDescription(infoDesc);
		 */
		obj.setTableNumber(arr[0] != null ? arr[0].toString() : null);
		obj.setActionType(arr[1] != null ? arr[1].toString() : null);
		obj.setFy(arr[2] != null ? arr[2].toString() : null);
		obj.setReturnPeriod(arr[3] != null ? arr[3].toString() : null);
		obj.setSupplierGstin(arr[4] != null ? arr[4].toString() : null);
		obj.setDeliveryChallanNumber(arr[5] != null ? arr[5].toString() : null);
		obj.setDeliveryChallanDate(fmtDate(arr[6]));
		obj.setjWDeliveryChallanNumber(
				arr[7] != null ? arr[7].toString() : null);
		obj.setjWDeliveryChallanDate(fmtDate(arr[8]));
		obj.setGoodsReceivingDate(fmtDate(arr[9]));
		obj.setInvoiceNumber(arr[10] != null ? arr[10].toString() : null);
		obj.setInvoiceDate(fmtDate(arr[11]));
		obj.setJobWorkerGSTIN(arr[12] != null ? arr[12].toString() : null);
		obj.setJobWorkerStateCode(arr[13] != null ? arr[13].toString() : null);
		obj.setJobWorkerType(arr[14] != null ? arr[14].toString() : null);
		obj.setJobWorkerID(arr[15] != null ? arr[15].toString() : null);
		obj.setJobWorkerName(arr[16] != null ? arr[16].toString() : null);
		obj.setTypeOfGoods(arr[17] != null ? arr[17].toString() : null);
		obj.setItemSerialNumber(arr[18] != null ? arr[18].toString() : null);
		obj.setProductDescription(arr[19] != null ? arr[19].toString() : null);
		obj.setProductCode(arr[20] != null ? arr[20].toString() : null);
		obj.setNatureOfJW(arr[21] != null ? arr[21].toString() : null);
		obj.setHsn(arr[22] != null ? arr[22].toString() : null);
		obj.setUqc(arr[23] != null ? arr[23].toString() : null);
		obj.setQuantity(removeDecimalDigit(arr[24]));
		obj.setLossesUQC(arr[25] != null ? arr[25].toString() : null);
		obj.setLossesQuantity(removeDecimalDigit(arr[26]));
		obj.setItemAssessableAmount(appendDoubleDecimalDigit(arr[27]));
		obj.setiGSTRate(appendDoubleDecimalDigit(arr[28]));
		obj.setiGSTAmount(appendDoubleDecimalDigit(arr[29]));
		obj.setcGSTRate(appendDoubleDecimalDigit(arr[30]));
		obj.setcGSTAmount(appendDoubleDecimalDigit(arr[31]));
		obj.setsGSTRate(appendDoubleDecimalDigit(arr[32]));
		obj.setsGSTAmount(appendDoubleDecimalDigit(arr[33]));
		obj.setCessAdvaloremRate(appendDoubleDecimalDigit(arr[34]));
		obj.setCessAdvaloremAmount(appendDoubleDecimalDigit(arr[35]));
		obj.setCessSpecificRate(appendDoubleDecimalDigit(arr[36]));
		obj.setCessSpecificAmount(appendDoubleDecimalDigit(arr[37]));
		obj.setStateCessAdvaloremRate(appendDoubleDecimalDigit(arr[38]));
		obj.setStateCessAdvaloremAmount(appendDoubleDecimalDigit(arr[39]));
		obj.setStateCessSpecificRate(appendDoubleDecimalDigit(arr[40]));
		obj.setStateCessSpecificAmount(appendDoubleDecimalDigit(arr[41]));
		obj.setTotalValue(appendDoubleDecimalDigit(arr[42]));
		obj.setPostingDate(arr[43] != null ? arr[43].toString() : null);
		obj.setUserId(arr[44] != null ? arr[44].toString() : null);
		obj.setCompanyCode(arr[45] != null ? arr[45].toString() : null);
		obj.setSourceIdentifier(arr[46] != null ? arr[46].toString() : null);
		obj.setSourcefileName(arr[47] != null ? arr[47].toString() : null);
		obj.setPlant(arr[48] != null ? arr[48].toString() : null);
		obj.setDivision(arr[49] != null ? arr[49].toString() : null);
		obj.setProfitCentre1(arr[50] != null ? arr[50].toString() : null);
		obj.setProfitCentre2(arr[51] != null ? arr[51].toString() : null);
		obj.setAccountingVoucherNumber(
				arr[52] != null ? arr[52].toString() : null);
		obj.setAccountingVoucherDate(
				arr[53] != null ? arr[53].toString() : null);
		obj.setUserDefinedField1(arr[54] != null ? arr[54].toString() : null);
		obj.setUserDefinedField2(arr[55] != null ? arr[55].toString() : null);
		obj.setUserDefinedField3(arr[56] != null ? arr[56].toString() : null);
		obj.setUserDefinedField4(arr[57] != null ? arr[57].toString() : null);
		obj.setUserDefinedField5(arr[58] != null ? arr[58].toString() : null);
		obj.setUserDefinedField6(arr[59] != null ? arr[59].toString() : null);
		obj.setUserDefinedField7(arr[60] != null ? arr[60].toString() : null);
		obj.setUserDefinedField8(arr[61] != null ? arr[61].toString() : null);
		obj.setUserDefinedField9(arr[62] != null ? arr[62].toString() : null);
		obj.setUserDefinedField10(arr[63] != null ? arr[63].toString() : null);
		obj.setUserID(arr[64] != null ? arr[64].toString() : null);
		obj.setFileID(arr[65] != null ? arr[65].toString() : null);
		obj.setFileName(arr[66] != null ? arr[66].toString() : null);
		obj.setSource(arr[67] != null ? arr[67].toString() : null);
		obj.setGstnStatus(arr[68] != null ? arr[68].toString() : null);
		obj.setGstnRefid(arr[69] != null ? arr[69].toString() : null);
		obj.setGstnRefidDateTime(arr[70] != null ? arr[70].toString() : null);
		/*
		 * if (arr[72] != null) { Timestamp timeStamp = (Timestamp) arr[72];
		 * LocalDateTime localDT = timeStamp.toLocalDateTime(); LocalDateTime
		 * convertref = EYDateUtil .toISTDateTimeFromUTC(localDT);
		 * obj.setGstnRefidDateTime(convertref.toString()); } else {
		 * obj.setGstnRefidDateTime(null); }
		 */
		obj.setgSTNErrorCode(arr[71] != null ? arr[71].toString() : null);
		obj.setgSTNErrorDesc(arr[72] != null ? arr[72].toString() : null);
		return obj;

	}

	private String AspProcessedUploadedQueryString(String buildQuery) {

		return "SELECT TABLE_NUMBER, ACTION_TYPE ,FI_YEAR,RETURN_PERIOD,"
				+ "SUPPLIER_GSTIN, DELIVERY_CHALLAN_NO,DELIVERY_CHALLAN_DATE,"
				+ "MAX(JW_DELIVERY_CHALLAN_NO)JW_DELIVERY_CHALLAN_NO, "
				+ "MAX(JW_DELIVERY_CHALLAN_DATE)JW_DELIVERY_CHALLAN_DATE,"
				+ "MAX(GOODS_RECEIVING_DATE)GOODS_RECEIVING_DATE, "
				+ "MAX(INV_NUM)INV_NUM,MAX(INV_DATE)INV_DATE,JW_GSTIN,JW_STATE_CODE,"
				+ "MAX(JW_WORKER_TYPE)JW_WORKER_TYPE,MAX(JW_ID)JW_ID, "
				+ "MAX(JW_NAME)JW_NAME,GOODS_TYPE,ITM_SER_NO,PRODUCT_DESC,"
				+ "MAX(PRODUCT_CODE)PRODUCT_CODE,MAX(NATURE_OF_JW)NATURE_OF_JW,"
				+ "MAX(ITM_HSNSAC)ITM_HSNSAC, ITM_UQC,IFNULL(SUM(ITM_QTY),0) "
				+ "AS ITM_QTY,MAX(LOSSES_UQC)LOSSES_UQC,MAX(LOSSES_QTY)LOSSES_QTY, "
				+ "IFNULL(SUM(ITM_ACCESSABLE_AMT),0) AS ITM_ACCESSABLE_AMT ,"
				+ "IGST_RATE,IFNULL(SUM(IGST_AMT),0) AS IGST_AMT, CGST_RATE,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT, SGST_RATE,"
				+ "IFNULL(SUM( SGST_AMT),0) AS SGST_AMT, CESS_RATE_ADVALOREM,"
				+ "IFNULL(SUM( CESS_AMT_ADVALOREM),0) AS CESS_AMT_ADVALOREM, "
				+ "CESS_RATE_SPECIFIC , IFNULL(SUM(CESS_AMT_SPECIFIC),0) "
				+ "AS CESS_AMT_SPECIFIC, STATE_CESS_RATE_ADVALOREM,"
				+ "IFNULL(SUM( STATE_CESS_AMT_ADVALOREM),0) "
				+ "AS STATE_CESS_AMT_ADVALOREM, STATE_CESS_RATE_SPECIFIC,"
				+ "IFNULL(SUM( STATE_CESS_AMT_SPECIFIC),0) "
				+ "AS STATE_CESS_AMT_SPECIFIC, IFNULL(SUM( TOTAL_VALUE),0) "
				+ "AS TOTAL_VALUE, MAX(POSTING_DATE)POSTING_DATE,MAX(USER_ID)USER_ID,"
				+ "MAX(COMPANY_CODE)COMPANY_CODE,"
				+ "MAX(SOURCE_IDENTIFIER)SOURCE_IDENTIFIER,"
				+ "MAX( SOURCE_FILE_NAME )SOURCE_FILE_NAME, "
				+ "MAX(PLANT_CODE )PLANT_CODE,MAX(DIVISION)DIVISION,"
				+ "MAX(PROFIT_CENTRE1)PROFIT_CENTRE1,"
				+ "MAX(PROFIT_CENTRE2)PROFIT_CENTRE2,"
				+ "MAX( ACCOUNTING_VOUCHER_NUM)ACCOUNTING_VOUCHER_NUM, "
				+ "MAX(ACCOUNTING_VOUCHER_DATE)ACCOUNTING_VOUCHER_DATE, "
				+ "MAX(USERDEFINED_FIELD1)USERDEFINED_FIELD1,"
				+ "MAX(USERDEFINED_FIELD2)USERDEFINED_FIELD2,"
				+ "MAX(USERDEFINED_FIELD3)USERDEFINED_FIELD3,"
				+ "MAX(USERDEFINED_FIELD4)USERDEFINED_FIELD4,"
				+ "MAX(USERDEFINED_FIELD5)USERDEFINED_FIELD5, "
				+ "MAX(USERDEFINED_FIELD6)USERDEFINED_FIELD6,"
				+ "MAX(USERDEFINED_FIELD7)USERDEFINED_FIELD7,"
				+ "MAX(USERDEFINED_FIELD8)USERDEFINED_FIELD8,"
				+ "MAX(USERDEFINED_FIELD9)USERDEFINED_FIELD9,"
				+ "MAX(USERDEFINED_FIELD10)USERDEFINED_FIELD10, "
				+ "MAX( USER_ID1)USER_ID1, MAX( FILE_ID)FILE_ID,"
				+ "MAX( FILE_NAME)FILE_NAME,MAX(SOURCE)SOURCE, "
				+ "MAX(GSTN_SAVE_STATUS)GSTN_SAVE_STATUS,MAX(GSTIN_REF_ID),"
				+ "MAX(GSTIN_REF_ID_TIME),MAX(GSTIN_ERROR_CODE),"
				+ "MAX(GSTIN_ERROR_DESCRIPTION_ASP)GSTIN_ERROR_DESCRIPTION_ASP,"
				+ "QRETURN_PERIOD FROM ( SELECT TABLE_NUMBER, ACTION_TYPE ,"
				+ "FI_YEAR,HDR.RETURN_PERIOD,HDR.SUPPLIER_GSTIN, "
				+ "DELIVERY_CHALLAN_NO,DELIVERY_CHALLAN_DATE,"
				+ " ''JW_DELIVERY_CHALLAN_NO,''JW_DELIVERY_CHALLAN_DATE, "
				+ " ''GOODS_RECEIVING_DATE,''INV_NUM ,''INV_DATE,JW_GSTIN ,"
				+ "JW_STATE_CODE ,''JW_WORKER_TYPE,''JW_ID,''JW_NAME, GOODS_TYPE,"
				+ "ITM_SER_NO,PRODUCT_DESC,''PRODUCT_CODE,''NATURE_OF_JW, "
				+ " ''ITM_HSNSAC ,ITM_UQC, ITM.ITM_QTY ,''LOSSES_UQC,0 LOSSES_QTY, "
				+ "ITM.TAXABLE_VALUE AS ITM_ACCESSABLE_AMT ,ITM.IGST_RATE, "
				+ "ITM.IGST_AMT , ITM.CGST_RATE,ITM.CGST_AMT, ITM.SGST_RATE,ITM.SGST_AMT, "
				+ "ITM.CESS_RATE_ADVALOREM,ITM.CESS_AMT_ADVALOREM, ITM.CESS_RATE_SPECIFIC , "
				+ "ITM.CESS_AMT_SPECIFIC, ITM.STATE_CESS_RATE_ADVALOREM,"
				+ "ITM.STATE_CESS_AMT_ADVALOREM, ITM.STATE_CESS_RATE_SPECIFIC,"
				+ "ITM.STATE_CESS_AMT_SPECIFIC, ITM.TOTAL_VALUE , "
				+ " ''POSTING_DATE,''USER_ID,''COMPANY_CODE,''SOURCE_IDENTIFIER,"
				+ " '' SOURCE_FILE_NAME , ''PLANT_CODE ,''DIVISION,''PROFIT_CENTRE1,"
				+ " ''PROFIT_CENTRE2,'' ACCOUNTING_VOUCHER_NUM,"
				+ " ''ACCOUNTING_VOUCHER_DATE, ''USERDEFINED_FIELD1,"
				+ " ''USERDEFINED_FIELD2,''USERDEFINED_FIELD3,''USERDEFINED_FIELD4,"
				+ " ''USERDEFINED_FIELD5, ''USERDEFINED_FIELD6,"
				+ " ''USERDEFINED_FIELD7,''USERDEFINED_FIELD8,"
				+ " ''USERDEFINED_FIELD9,''USERDEFINED_FIELD10, '' USER_ID1, "
				+ " '' FILE_ID,'' FILE_NAME,'' SOURCE, "
				+ "(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
				+ "WHEN IS_SAVED_TO_GSTN = FALSE AND GSTN_ERROR = TRUE "
				+ "THEN 'GSTN_ERROR' WHEN IS_SAVED_TO_GSTN = FALSE "
				+ "AND GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) "
				+ "AS GSTN_SAVE_STATUS, GSTNBATCH.GSTN_SAVE_REF_ID "
				+ "AS GSTIN_REF_ID, GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, "
				+ "TRIM(', ' FROM IFNULL( HDR.GSTN_ERROR_CODE,'')) "
				+ "AS GSTIN_ERROR_CODE, TRIM(', ' FROM IFNULL( HDR.GSTN_ERROR_DESC,'')) "
				+ "AS GSTIN_ERROR_DESCRIPTION_ASP,"
				+ "HDR.QRETURN_PERIOD FROM ITC04_HEADER "
				+ "HDR INNER JOIN ITC04_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID "
				+ "AND HDR.RETURN_PERIOD=ITM.RETURN_PERIOD "
				+ "LEFT OUTER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH "
				+ "ON GSTNBATCH.ID = HDR.BATCH_ID WHERE HDR.IS_DELETE=FALSE "
				+ "AND IS_PROCESSED=TRUE AND TABLE_NUMBER='4' "
				+ "AND (ACTION_TYPE <> 'CAN' OR ACTION_TYPE IS NULL) "
				+ buildQuery + " ) "
				+ "GROUP BY TABLE_NUMBER, ACTION_TYPE ,FI_YEAR, "
				+ "RETURN_PERIOD,SUPPLIER_GSTIN, DELIVERY_CHALLAN_NO,"
				+ "DELIVERY_CHALLAN_DATE,JW_GSTIN ,JW_STATE_CODE,GOODS_TYPE,"
				+ "ITM_SER_NO,PRODUCT_DESC,ITM_UQC, IGST_RATE,"
				+ "CGST_RATE,SGST_RATE,CESS_RATE_ADVALOREM,"
				+ "CESS_RATE_SPECIFIC,STATE_CESS_RATE_ADVALOREM,"
				+ "STATE_CESS_RATE_SPECIFIC ,QRETURN_PERIOD " + " UNION ALL "
				+ "SELECT TABLE_NUMBER, ACTION_TYPE ,FI_YEAR,RETURN_PERIOD,"
				+ "SUPPLIER_GSTIN, DELIVERY_CHALLAN_NO,DELIVERY_CHALLAN_DATE,"
				+ "JW_DELIVERY_CHALLAN_NO, JW_DELIVERY_CHALLAN_DATE,"
				+ "MAX(GOODS_RECEIVING_DATE)GOODS_RECEIVING_DATE, "
				+ "MAX(INV_NUM)INV_NUM,MAX(INV_DATE)INV_DATE,"
				+ "JW_GSTIN,JW_STATE_CODE,MAX(JW_WORKER_TYPE)JW_WORKER_TYPE,"
				+ "MAX(JW_ID)JW_ID, MAX(JW_NAME)JW_NAME,"
				+ "MAX(GOODS_TYPE)GOODS_TYPE,ITM_SER_NO,PRODUCT_DESC,"
				+ "MAX(PRODUCT_CODE)PRODUCT_CODE,NATURE_OF_JW,"
				+ "MAX(ITM_HSNSAC)ITM_HSNSAC, ITM_UQC,MAX(ITM_QTY)ITM_QTY,"
				+ "LOSSES_UQC,SUM(LOSSES_QTY)LOSSES_QTY, "
				+ "MAX(ITM_ACCESSABLE_AMT)ITM_ACCESSABLE_AMT,"
				+ "MAX(IGST_RATE)IGST_RATE,MAX(IGST_AMT)IGST_AMT, "
				+ "MAX(CGST_RATE)CGST_RATE,MAX(CGST_AMT)CGST_AMT, "
				+ "MAX(SGST_RATE)SGST_RATE, MAX(SGST_AMT)SGST_AMT,"
				+ "MAX(CESS_RATE_ADVALOREM)CESS_RATE_ADVALOREM, "
				+ "MAX(CESS_AMT_ADVALOREM)CESS_AMT_ADVALOREM, "
				+ "MAX(CESS_RATE_SPECIFIC)CESS_RATE_SPECIFIC , "
				+ "MAX(CESS_AMT_SPECIFIC)CESS_AMT_SPECIFIC, "
				+ "MAX(STATE_CESS_RATE_ADVALOREM)STATE_CESS_RATE_ADVALOREM,"
				+ "MAX(STATE_CESS_AMT_ADVALOREM)STATE_CESS_AMT_ADVALOREM, "
				+ "MAX(STATE_CESS_RATE_SPECIFIC)STATE_CESS_RATE_SPECIFIC,"
				+ "MAX(STATE_CESS_AMT_SPECIFIC)STATE_CESS_AMT_SPECIFIC, "
				+ "MAX(TOTAL_VALUE)TOTAL_VALUE, MAX(POSTING_DATE)POSTING_DATE,"
				+ "MAX(USER_ID)USER_ID,MAX(COMPANY_CODE)COMPANY_CODE,"
				+ "MAX(SOURCE_IDENTIFIER)SOURCE_IDENTIFIER,"
				+ "MAX( SOURCE_FILE_NAME )SOURCE_FILE_NAME, "
				+ "MAX(PLANT_CODE )PLANT_CODE,MAX(DIVISION)DIVISION,"
				+ "MAX(PROFIT_CENTRE1)PROFIT_CENTRE1,"
				+ "MAX(PROFIT_CENTRE2)PROFIT_CENTRE2,"
				+ "MAX( ACCOUNTING_VOUCHER_NUM)ACCOUNTING_VOUCHER_NUM, "
				+ "MAX(ACCOUNTING_VOUCHER_DATE)ACCOUNTING_VOUCHER_DATE, "
				+ "MAX(USERDEFINED_FIELD1)USERDEFINED_FIELD1,"
				+ "MAX(USERDEFINED_FIELD2)USERDEFINED_FIELD2,"
				+ "MAX(USERDEFINED_FIELD3)USERDEFINED_FIELD3,"
				+ "MAX(USERDEFINED_FIELD4)USERDEFINED_FIELD4,"
				+ "MAX(USERDEFINED_FIELD5)USERDEFINED_FIELD5, "
				+ "MAX(USERDEFINED_FIELD6)USERDEFINED_FIELD6,"
				+ "MAX(USERDEFINED_FIELD7)USERDEFINED_FIELD7,"
				+ "MAX(USERDEFINED_FIELD8)USERDEFINED_FIELD8,"
				+ "MAX(USERDEFINED_FIELD9)USERDEFINED_FIELD9,"
				+ "MAX(USERDEFINED_FIELD10)USERDEFINED_FIELD10, "
				+ "MAX( USER_ID1)USER_ID1, MAX( FILE_ID)FILE_ID,"
				+ "MAX( FILE_NAME)FILE_NAME,MAX(SOURCE)SOURCE, "
				+ "MAX(GSTN_SAVE_STATUS)GSTN_SAVE_STATUS,MAX(GSTIN_REF_ID),"
				+ "MAX(GSTIN_REF_ID_TIME),MAX(GSTIN_ERROR_CODE),"
				+ "MAX(GSTIN_ERROR_DESCRIPTION_ASP)GSTIN_ERROR_DESCRIPTION_ASP,"
				+ "QRETURN_PERIOD FROM ( SELECT TABLE_NUMBER, ACTION_TYPE ,"
				+ "FI_YEAR,HDR.RETURN_PERIOD,HDR.SUPPLIER_GSTIN, "
				+ "DELIVERY_CHALLAN_NO,DELIVERY_CHALLAN_DATE, "
				+ "JW_DELIVERY_CHALLAN_NO,JW_DELIVERY_CHALLAN_DATE, "
				+ " ''GOODS_RECEIVING_DATE,''INV_NUM ,''INV_DATE,JW_GSTIN ,"
				+ "JW_STATE_CODE ,''JW_WORKER_TYPE,''JW_ID,''JW_NAME, "
				+ " ''GOODS_TYPE,ITM_SER_NO,PRODUCT_DESC,''PRODUCT_CODE,"
				+ "NATURE_OF_JW, ''ITM_HSNSAC ,ITM_UQC, 0 ITM_QTY ,"
				+ "LOSSES_UQC,LOSSES_QTY, ITM.TAXABLE_VALUE AS ITM_ACCESSABLE_AMT , "
				+ "ITM.IGST_RATE, ITM.IGST_AMT ,ITM.CGST_RATE, ITM.CGST_AMT, "
				+ "ITM.SGST_RATE, ITM.SGST_AMT, ITM.CESS_RATE_ADVALOREM,"
				+ "ITM.CESS_AMT_ADVALOREM, ITM.CESS_RATE_SPECIFIC , "
				+ "ITM.CESS_AMT_SPECIFIC, ITM.STATE_CESS_RATE_ADVALOREM,"
				+ "ITM.STATE_CESS_AMT_ADVALOREM, ITM.STATE_CESS_RATE_SPECIFIC,"
				+ "ITM.STATE_CESS_AMT_SPECIFIC, ITM.TOTAL_VALUE , "
				+ " ''POSTING_DATE,''USER_ID,''COMPANY_CODE,''SOURCE_IDENTIFIER,"
				+ " '' SOURCE_FILE_NAME , ''PLANT_CODE ,''DIVISION,"
				+ " ''PROFIT_CENTRE1,''PROFIT_CENTRE2,'' ACCOUNTING_VOUCHER_NUM,"
				+ " ''ACCOUNTING_VOUCHER_DATE, ''USERDEFINED_FIELD1,"
				+ " ''USERDEFINED_FIELD2,''USERDEFINED_FIELD3,''USERDEFINED_FIELD4,"
				+ " ''USERDEFINED_FIELD5, ''USERDEFINED_FIELD6,"
				+ " ''USERDEFINED_FIELD7,''USERDEFINED_FIELD8,"
				+ " ''USERDEFINED_FIELD9,''USERDEFINED_FIELD10, '' USER_ID1, "
				+ " '' FILE_ID,'' FILE_NAME,'' SOURCE, "
				+ "(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
				+ "WHEN IS_SAVED_TO_GSTN = FALSE AND GSTN_ERROR = TRUE "
				+ "THEN 'GSTN_ERROR' WHEN IS_SAVED_TO_GSTN = FALSE "
				+ "AND GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) "
				+ "AS GSTN_SAVE_STATUS, GSTNBATCH.GSTN_SAVE_REF_ID "
				+ "AS GSTIN_REF_ID, GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, "
				+ "TRIM(', ' FROM IFNULL( HDR.GSTN_ERROR_CODE,'')) "
				+ "AS GSTIN_ERROR_CODE, TRIM(', ' FROM "
				+ "IFNULL( HDR.GSTN_ERROR_DESC,'')) "
				+ "AS GSTIN_ERROR_DESCRIPTION_ASP,"
				+ "HDR.QRETURN_PERIOD FROM ITC04_HEADER "
				+ "HDR INNER JOIN ITC04_ITEM ITM "
				+ "ON HDR.ID=ITM.DOC_HEADER_ID "
				+ "AND HDR.RETURN_PERIOD=ITM.RETURN_PERIOD "
				+ "LEFT OUTER JOIN FILE_STATUS FIL "
				+ "ON HDR.FILE_ID=FIL.ID LEFT OUTER JOIN "
				+ "GSTR1_GSTN_SAVE_BATCH GSTNBATCH "
				+ "ON GSTNBATCH.ID = HDR.BATCH_ID "
				+ "WHERE HDR.IS_DELETE=FALSE AND IS_PROCESSED=TRUE "
				+ "AND TABLE_NUMBER IN ('5A','5B') "
				+ "AND (ACTION_TYPE <> 'CAN' OR ACTION_TYPE IS NULL) "
				+ buildQuery + " ) " + " GROUP BY "
				+ "TABLE_NUMBER, ACTION_TYPE ,FI_YEAR, RETURN_PERIOD,"
				+ "SUPPLIER_GSTIN, DELIVERY_CHALLAN_NO,"
				+ "DELIVERY_CHALLAN_DATE,JW_GSTIN ,JW_STATE_CODE,"
				+ "GOODS_TYPE,ITM_SER_NO,PRODUCT_DESC,ITM_UQC, "
				+ "IGST_RATE,CGST_RATE,SGST_RATE,CESS_RATE_ADVALOREM,"
				+ "CESS_RATE_SPECIFIC,STATE_CESS_RATE_ADVALOREM,"
				+ "STATE_CESS_RATE_SPECIFIC ,QRETURN_PERIOD ,"
				+ "JW_DELIVERY_CHALLAN_NO, JW_DELIVERY_CHALLAN_DATE,"
				+ "NATURE_OF_JW,LOSSES_UQC UNION ALL SELECT TABLE_NUMBER, "
				+ "ACTION_TYPE ,FI_YEAR,RETURN_PERIOD,SUPPLIER_GSTIN, "
				+ "DELIVERY_CHALLAN_NO,DELIVERY_CHALLAN_DATE,"
				+ "MAX(JW_DELIVERY_CHALLAN_NO)JW_DELIVERY_CHALLAN_NO, "
				+ "MAX(JW_DELIVERY_CHALLAN_DATE)JW_DELIVERY_CHALLAN_DATE,"
				+ "MAX(GOODS_RECEIVING_DATE)GOODS_RECEIVING_DATE, INV_NUM, "
				+ "INV_DATE,JW_GSTIN,JW_STATE_CODE,"
				+ "MAX(JW_WORKER_TYPE)JW_WORKER_TYPE,MAX(JW_ID)JW_ID, "
				+ "MAX(JW_NAME)JW_NAME,MAX(GOODS_TYPE)GOODS_TYPE,ITM_SER_NO,"
				+ "PRODUCT_DESC,MAX(PRODUCT_CODE)PRODUCT_CODE,NATURE_OF_JW,"
				+ "MAX(ITM_HSNSAC)ITM_HSNSAC, ITM_UQC,MAX(ITM_QTY)ITM_QTY,"
				+ "LOSSES_UQC,SUM(LOSSES_QTY)LOSSES_QTY, "
				+ "MAX(ITM_ACCESSABLE_AMT)ITM_ACCESSABLE_AMT,"
				+ "MAX(IGST_RATE)IGST_RATE,MAX(IGST_AMT)IGST_AMT, "
				+ "MAX(CGST_RATE)CGST_RATE,MAX(CGST_AMT)CGST_AMT, "
				+ "MAX(SGST_RATE)SGST_RATE, MAX(SGST_AMT)SGST_AMT,"
				+ "MAX(CESS_RATE_ADVALOREM)CESS_RATE_ADVALOREM, "
				+ "MAX(CESS_AMT_ADVALOREM)CESS_AMT_ADVALOREM, "
				+ "MAX(CESS_RATE_SPECIFIC)CESS_RATE_SPECIFIC , "
				+ "MAX(CESS_AMT_SPECIFIC)CESS_AMT_SPECIFIC, "
				+ "MAX(STATE_CESS_RATE_ADVALOREM)STATE_CESS_RATE_ADVALOREM,"
				+ "MAX(STATE_CESS_AMT_ADVALOREM)STATE_CESS_AMT_ADVALOREM, "
				+ "MAX(STATE_CESS_RATE_SPECIFIC)STATE_CESS_RATE_SPECIFIC,"
				+ "MAX(STATE_CESS_AMT_SPECIFIC)STATE_CESS_AMT_SPECIFIC, "
				+ "MAX(TOTAL_VALUE)TOTAL_VALUE, MAX(POSTING_DATE)POSTING_DATE,"
				+ "MAX(USER_ID)USER_ID,MAX(COMPANY_CODE)COMPANY_CODE,"
				+ "MAX(SOURCE_IDENTIFIER)SOURCE_IDENTIFIER,"
				+ "MAX( SOURCE_FILE_NAME )SOURCE_FILE_NAME, "
				+ "MAX(PLANT_CODE )PLANT_CODE,MAX(DIVISION)DIVISION,"
				+ "MAX(PROFIT_CENTRE1)PROFIT_CENTRE1,"
				+ "MAX(PROFIT_CENTRE2)PROFIT_CENTRE2,"
				+ "MAX( ACCOUNTING_VOUCHER_NUM)ACCOUNTING_VOUCHER_NUM, "
				+ "MAX(ACCOUNTING_VOUCHER_DATE)ACCOUNTING_VOUCHER_DATE, "
				+ "MAX(USERDEFINED_FIELD1)USERDEFINED_FIELD1,"
				+ "MAX(USERDEFINED_FIELD2)USERDEFINED_FIELD2,"
				+ "MAX(USERDEFINED_FIELD3)USERDEFINED_FIELD3,"
				+ "MAX(USERDEFINED_FIELD4)USERDEFINED_FIELD4,"
				+ "MAX(USERDEFINED_FIELD5)USERDEFINED_FIELD5, "
				+ "MAX(USERDEFINED_FIELD6)USERDEFINED_FIELD6,"
				+ "MAX(USERDEFINED_FIELD7)USERDEFINED_FIELD7,"
				+ "MAX(USERDEFINED_FIELD8)USERDEFINED_FIELD8,"
				+ "MAX(USERDEFINED_FIELD9)USERDEFINED_FIELD9,"
				+ "MAX(USERDEFINED_FIELD10)USERDEFINED_FIELD10, "
				+ "MAX( USER_ID1)USER_ID1, MAX( FILE_ID)FILE_ID,"
				+ "MAX( FILE_NAME)FILE_NAME,MAX(SOURCE)SOURCE, "
				+ "MAX(GSTN_SAVE_STATUS)GSTN_SAVE_STATUS,MAX(GSTIN_REF_ID),"
				+ "MAX(GSTIN_REF_ID_TIME),MAX(GSTIN_ERROR_CODE),"
				+ "MAX(GSTIN_ERROR_DESCRIPTION_ASP)GSTIN_ERROR_DESCRIPTION_ASP,"
				+ "QRETURN_PERIOD FROM ( SELECT TABLE_NUMBER, ACTION_TYPE ,"
				+ "FI_YEAR,HDR.RETURN_PERIOD,HDR.SUPPLIER_GSTIN, "
				+ "DELIVERY_CHALLAN_NO,DELIVERY_CHALLAN_DATE,"
				+ " ''JW_DELIVERY_CHALLAN_NO,''JW_DELIVERY_CHALLAN_DATE, "
				+ " ''GOODS_RECEIVING_DATE,INV_NUM ,INV_DATE,JW_GSTIN ,"
				+ "JW_STATE_CODE ,''JW_WORKER_TYPE,''JW_ID,''JW_NAME, "
				+ " ''GOODS_TYPE,ITM_SER_NO,PRODUCT_DESC,''PRODUCT_CODE,NATURE_OF_JW, "
				+ " ''ITM_HSNSAC ,ITM_UQC, 0 ITM_QTY ,LOSSES_UQC,LOSSES_QTY, "
				+ "ITM.TAXABLE_VALUE AS ITM_ACCESSABLE_AMT , ITM.IGST_RATE, ITM.IGST_AMT , "
				+ "ITM.CGST_RATE, ITM.CGST_AMT, ITM.SGST_RATE, ITM.SGST_AMT, "
				+ "ITM.CESS_RATE_ADVALOREM, ITM.CESS_AMT_ADVALOREM,"
				+ "ITM.CESS_RATE_SPECIFIC , ITM.CESS_AMT_SPECIFIC, "
				+ "ITM.STATE_CESS_RATE_ADVALOREM, ITM.STATE_CESS_AMT_ADVALOREM, "
				+ "ITM.STATE_CESS_RATE_SPECIFIC, ITM.STATE_CESS_AMT_SPECIFIC, "
				+ "ITM.TOTAL_VALUE , ''POSTING_DATE,''USER_ID,''COMPANY_CODE,"
				+ " ''SOURCE_IDENTIFIER,'' SOURCE_FILE_NAME , ''PLANT_CODE ,"
				+ " ''DIVISION,''PROFIT_CENTRE1,''PROFIT_CENTRE2,"
				+ " '' ACCOUNTING_VOUCHER_NUM,''ACCOUNTING_VOUCHER_DATE, "
				+ " ''USERDEFINED_FIELD1,''USERDEFINED_FIELD2,''USERDEFINED_FIELD3,"
				+ " ''USERDEFINED_FIELD4,''USERDEFINED_FIELD5, ''USERDEFINED_FIELD6,"
				+ " ''USERDEFINED_FIELD7,''USERDEFINED_FIELD8,''USERDEFINED_FIELD9,"
				+ " ''USERDEFINED_FIELD10, '' USER_ID1, '' FILE_ID,'' FILE_NAME,"
				+ " '' SOURCE, (CASE WHEN IS_SAVED_TO_GSTN = TRUE "
				+ "THEN 'IS_SAVED' WHEN IS_SAVED_TO_GSTN = FALSE "
				+ "AND GSTN_ERROR = TRUE THEN 'GSTN_ERROR' "
				+ "WHEN IS_SAVED_TO_GSTN = FALSE "
				+ "AND GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) "
				+ "AS GSTN_SAVE_STATUS, GSTNBATCH.GSTN_SAVE_REF_ID "
				+ "AS GSTIN_REF_ID, GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, "
				+ "TRIM(', ' FROM IFNULL( HDR.GSTN_ERROR_CODE,'')) "
				+ "AS GSTIN_ERROR_CODE, TRIM(', ' FROM "
				+ "IFNULL( HDR.GSTN_ERROR_DESC,'')) "
				+ "AS GSTIN_ERROR_DESCRIPTION_ASP,HDR.QRETURN_PERIOD "
				+ "FROM ITC04_HEADER HDR INNER JOIN ITC04_ITEM ITM "
				+ "ON HDR.ID=ITM.DOC_HEADER_ID AND "
				+ "HDR.RETURN_PERIOD=ITM.RETURN_PERIOD "
				+ "LEFT OUTER JOIN FILE_STATUS FIL ON "
				+ "HDR.FILE_ID=FIL.ID LEFT OUTER JOIN "
				+ "GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON "
				+ "GSTNBATCH.ID = HDR.BATCH_ID WHERE HDR.IS_DELETE=FALSE "
				+ "AND IS_PROCESSED=TRUE AND TABLE_NUMBER='5C' "
				+ "AND (ACTION_TYPE <> 'CAN' OR ACTION_TYPE IS NULL) "
				+ buildQuery + " ) " + " GROUP BY "
				+ "TABLE_NUMBER, ACTION_TYPE ,FI_YEAR, RETURN_PERIOD,"
				+ "SUPPLIER_GSTIN, DELIVERY_CHALLAN_NO,DELIVERY_CHALLAN_DATE,"
				+ "JW_GSTIN ,JW_STATE_CODE,ITM_SER_NO,PRODUCT_DESC,"
				+ "ITM_UQC,NATURE_OF_JW,LOSSES_UQC ,QRETURN_PERIOD,INV_NUM, "
				+ "INV_DATE ";

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
