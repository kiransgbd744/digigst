package com.ey.advisory.app.services.reports;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1ProcessedRecordsView;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;

@Component("Anx1ProcessedReportsDaoImpl")
public class Anx1ProcessedReportsDaoImpl implements Anx1ProcessedReportsDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ProcessedReportsDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	static Integer answer = null;
	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public List<Anx1ProcessedRecordsView> getProcessedReports(
			Anx1FileStatusReportsReqDto request) {

		Long fileId = request.getFileId();
		String status = request.getStatus();
		answer = request.getAnswer();

		StringBuilder buildQuery = new StringBuilder();
		if (fileId != null) {
			buildQuery.append(" AND HDR.FILE_ID= :fileId");
		}
		if (status.equalsIgnoreCase(DownloadReportsConstant.PROCESSEDACTIVE)) {
			buildQuery.append(" AND IS_DELETE = FALSE");
		}
		if (status
				.equalsIgnoreCase(DownloadReportsConstant.PROCESSEDINACTIVE)) {
			buildQuery.append(" AND IS_DELETE = TRUE");
		}
		String queryStr = createProcessedQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DB data," + list);
		}
		return list.stream().map(o -> convertProcessed(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Anx1ProcessedRecordsView convertProcessed(Object[] arr) {
		Anx1ProcessedRecordsView obj = new Anx1ProcessedRecordsView();

		obj.setUserID(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnType(arr[2] != null ? arr[2].toString() : null);
		obj.setDataCategory(arr[3] != null ? arr[3].toString() : null);
		obj.setTableNumber(arr[4] != null ? arr[4].toString() : null);
		obj.setSourceFileName(arr[5] != null ? arr[5].toString() : null);
		obj.setProfitCentre(arr[125] != null ? arr[125].toString() : null);
		obj.setPlant(arr[126] != null ? arr[126].toString() : null);
		obj.setDivision(arr[8] != null ? arr[8].toString() : null);
		obj.setLocation(arr[127] != null ? arr[127].toString() : null);
		obj.setSalesOrganisation(arr[10] != null ? arr[10].toString() : null);
		obj.setDistributionChannel(arr[11] != null ? arr[11].toString() : null);
		obj.setUserAccess1(arr[12] != null ? arr[12].toString() : null);
		obj.setUserAccess2(arr[13] != null ? arr[13].toString() : null);
		obj.setUserAccess3(arr[14] != null ? arr[14].toString() : null);
		obj.setUserAccess4(arr[15] != null ? arr[15].toString() : null);
		obj.setUserAccess5(arr[16] != null ? arr[16].toString() : null);
		obj.setUserAccess6(arr[17] != null ? arr[17].toString() : null);
		obj.setReturnPeriod(arr[18] != null ? arr[18].toString() : null);
		obj.setSgstin(arr[19] != null ? arr[19].toString() : null);
		obj.setDocType(arr[20] != null ? arr[20].toString() : null);
		obj.setSupplyType(arr[21] != null ? arr[21].toString() : null);
		obj.setDocNum(arr[22] != null ? arr[22].toString() : null);

		if (arr[23] != null) {
			String strdate = arr[23].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setDocDate(newDate);
		} else {
			obj.setDocDate(null);
		}
		if (arr[24] != null) {
			String strdate = arr[24].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPrecedDate(newDate);
		} else {
			obj.setPrecedDate(null);
		}
		obj.setPrecedNum(arr[25] != null ? arr[25].toString() : null);
		obj.setOrgDocType(arr[26] != null ? arr[26].toString() : null);
		obj.setCrdrPreGST(arr[27] != null ? arr[27].toString() : null);
		obj.setRecpGSTIN(arr[28] != null ? arr[28].toString() : null);
		obj.setRecpType(arr[29] != null ? arr[29].toString() : null);
		obj.setDiffPerFlag(arr[30] != null ? arr[30].toString() : null);
		obj.setOrgRecpGSTIN(arr[31] != null ? arr[31].toString() : null);
		obj.setRecpName(arr[32] != null ? arr[32].toString() : null);
		obj.setRecpCode(arr[33] != null ? arr[33].toString() : null);
		obj.setRecpAdd1(arr[34] != null ? arr[34].toString() : null);
		obj.setRecpAdd2(arr[35] != null ? arr[35].toString() : null);
		obj.setRecpAdd3(arr[36] != null ? arr[36].toString() : null);
		obj.setRecpAdd4(arr[37] != null ? arr[37].toString() : null);
		obj.setBillToState(arr[38] != null ? arr[38].toString() : null);
		obj.setShipToState(arr[39] != null ? arr[39].toString() : null);
		obj.setPos(arr[40] != null ? arr[40].toString() : null);
		obj.setStateAplyCess(arr[41] != null ? arr[41].toString() : null);
		obj.setPortCode(arr[42] != null ? arr[42].toString() : null);
		obj.setShippingBillNum(arr[43] != null ? arr[43].toString() : null);
		if (arr[44] != null) {
			String strdate = arr[44].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setShippingBillDate(newDate);
		} else {
			obj.setShippingBillDate(null);
		}
		obj.setSec7ofIGSTFlag(arr[45] != null ? arr[45].toString() : null);
		obj.setInvoiceValueAsp(arr[46] != null ? arr[46].toString() : null);
		obj.setInvoiceValue(arr[124] != null ? arr[124].toString() : null);
		obj.setReverseChargeFlag(arr[47] != null ? arr[47].toString() : null);
		obj.setTcsFlag(arr[48] != null ? arr[48].toString() : null);
		obj.seteComGSTIN(arr[49] != null ? arr[49].toString() : null);
		obj.setClaimRefndFlag(arr[50] != null ? arr[50].toString() : null);
		obj.setAutoPopltToRefund(arr[51] != null ? arr[51].toString() : null);
		obj.setAccVoucherNum(arr[52] != null ? arr[52].toString() : null);
		if (arr[53] != null) {
			String strdate = arr[53].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setAccVoucherDate(newDate);
		} else {
			obj.setAccVoucherDate(null);
		}
		obj.setEwaybillNum(arr[54] != null ? arr[54].toString() : null);
		obj.setEwaybillDate(arr[55] != null ? arr[55].toString() : null);
		obj.setgLCodeTaxableValue(arr[59] != null ? arr[59].toString() : null);
		obj.setgLCodeIGST(arr[60] != null ? arr[60].toString() : null);
		obj.setgLCodeCGST(arr[61] != null ? arr[61].toString() : null);
		obj.setgLCodeSGST(arr[62] != null ? arr[62].toString() : null);
		obj.setgLCodeAdvlormCess(arr[63] != null ? arr[63].toString() : null);
		obj.setgLCodeSpecificCess(arr[64] != null ? arr[64].toString() : null);
		obj.setgLCodeStateCess(arr[65] != null ? arr[65].toString() : null);
		obj.setItemNumber(arr[66] != null ? arr[66].toString() : null);
		obj.setHsnOrSac(arr[67] != null ? arr[67].toString() : null);
		obj.setProductCode(arr[68] != null ? arr[68].toString() : null);
		obj.setProductDesc(arr[69] != null ? arr[69].toString() : null);
		obj.setCategoryOfProduct(arr[70] != null ? arr[70].toString() : null);
		obj.setUom(arr[71] != null ? arr[71].toString() : null);
		obj.setQuantity(arr[72] != null ? arr[72].toString() : null);
		obj.setItcFlag(arr[73] != null ? arr[73].toString() : null);
		obj.setReasonForCrDbNote(arr[74] != null ? arr[74].toString() : null);
		obj.setFob(arr[75] != null ? arr[75].toString() : null);
		obj.setExportDuty(arr[76] != null ? arr[76].toString() : null);
		obj.setUserDefField1(arr[77] != null ? arr[77].toString() : null);
		obj.setUserDefField2(arr[78] != null ? arr[78].toString() : null);
		obj.setUserDefField3(arr[79] != null ? arr[79].toString() : null);
		obj.setUserDefField4(arr[80] != null ? arr[80].toString() : null);
		obj.setUserDefField5(arr[81] != null ? arr[81].toString() : null);
		obj.setUserDefField6(arr[82] != null ? arr[82].toString() : null);
		obj.setUserDefField7(arr[83] != null ? arr[83].toString() : null);
		obj.setUserDefField8(arr[84] != null ? arr[84].toString() : null);
		obj.setUserDefField9(arr[85] != null ? arr[85].toString() : null);
		obj.setUserDefField10(arr[86] != null ? arr[86].toString() : null);
		obj.setUserDefField11(arr[87] != null ? arr[87].toString() : null);
		obj.setUserDefField12(arr[88] != null ? arr[88].toString() : null);
		obj.setUserDefField13(arr[89] != null ? arr[89].toString() : null);
		obj.setUserDefField14(arr[90] != null ? arr[90].toString() : null);
		obj.setUserDefField15(arr[91] != null ? arr[91].toString() : null);
		obj.setOtherValue(arr[92] != null ? arr[92].toString() : null);
		obj.setAdjRefNo(arr[93] != null ? arr[93].toString() : null);
		if (arr[94] != null) {
			String strdate = arr[94].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setAdjRefDate(newDate);
		} else {
			obj.setAdjRefDate(null);
		}
		obj.setTaxableValue(arr[95] != null ? arr[95].toString() : null);
		obj.setIntegTaxRate(arr[96] != null ? arr[96].toString() : null);
		obj.setIntegTaxAmt(arr[97] != null ? arr[97].toString() : null);
		obj.setCentralTaxRate(arr[98] != null ? arr[98].toString() : null);
		obj.setCentralTaxAmt(arr[99] != null ? arr[99].toString() : null);
		obj.setStateUTTaxRate(arr[100] != null ? arr[100].toString() : null);
		obj.setStateUTTaxAmt(arr[101] != null ? arr[101].toString() : null);
		obj.setSpecfcCessRate(arr[102] != null ? arr[102].toString() : null);
		obj.setSpecfcCessAmt(arr[103] != null ? arr[103].toString() : null);
		obj.setAdvmCessRate(arr[104] != null ? arr[104].toString() : null);
		obj.setAdvmCessAmt(arr[105] != null ? arr[105].toString() : null);
		obj.setStateCessRate(arr[106] != null ? arr[106].toString() : null);
		obj.setStateCessAmt(arr[107] != null ? arr[107].toString() : null);
		obj.setTaxableValueAdj(arr[108] != null ? arr[108].toString() : null);
		obj.setIntegdTaxAmtAdj(arr[109] != null ? arr[109].toString() : null);
		obj.setCentralTaxAmtAdj(arr[110] != null ? arr[110].toString() : null);
		obj.setStateUTTaxAmtAdj(arr[111] != null ? arr[111].toString() : null);
		obj.setAdvlmCessAmtAdj(arr[112] != null ? arr[112].toString() : null);
		obj.setSpecfCessAmtAdj(arr[113] != null ? arr[113].toString() : null);
		obj.setStateCessAmtAdj(arr[114] != null ? arr[114].toString() : null);
		obj.setTcsAmt(arr[115] != null ? arr[115].toString() : null);
		obj.setAspInformationID(arr[116] != null ? arr[116].toString() : null);
		obj.setAspInformationDesc(
				arr[117] != null ? arr[117].toString() : null);
		obj.setIntegratedTaxamountASP(
				arr[118] != null ? arr[118].toString() : null);
		obj.setCentralTaxamountASP(
				arr[119] != null ? arr[119].toString() : null);
		obj.setStateUTTaxamountASP(
				arr[120] != null ? arr[120].toString() : null);
		obj.setIntegratedTaxamountRET1impact(
				arr[121] != null ? arr[121].toString() : null);
		obj.setCentralTaxamountRET1impact(
				arr[122] != null ? arr[122].toString() : null);
		obj.setStateUTTaxamountRET1impact(
				arr[123] != null ? arr[123].toString() : null);

		return obj;
	}

	private String createProcessedQueryString(String buildQuery) {
		return "SELECT HDR.ID AS HID,HDR.USER_ID,"
				+ "CASE WHEN HDR.DERIVED_RET_PERIOD  >= " + answer
				+ " THEN HDR.AN_RETURN_TYPE "
				+ "ELSE HDR.RETURN_TYPE END AS RET_TYPE, "
				+ "CASE WHEN HDR.DERIVED_RET_PERIOD  >= " + answer
				+ " THEN HDR.AN_TAX_DOC_TYPE "
				+ "ELSE HDR.TAX_DOC_TYPE END AS DATA_CATEGORY, "
				+ "CASE WHEN HDR.DERIVED_RET_PERIOD  >= " + answer
				+ " THEN HDR.AN_TABLE_SECTION "
				+ "ELSE HDR.TABLE_SECTION END AS TABLE_NUMBER, "
				+ "HDR.SOURCE_FILENAME,HDR.PROFIT_CENTRE,"
				+ "HDR.PLANT_CODE,HDR.DIVISION,HDR.LOCATION,"
				+ "HDR.SALES_ORGANIZATION," + "HDR.DISTRIBUTION_CHANNEL,"
				+ "HDR.USERACCESS1,"
				+ "HDR.USERACCESS2,HDR.USERACCESS3,HDR.USERACCESS4,"
				+ "HDR.USERACCESS5,HDR.USERACCESS6,HDR.RETURN_PERIOD,"
				+ "HDR.SUPPLIER_GSTIN,HDR.DOC_TYPE,HDR.SUPPLY_TYPE,HDR.DOC_NUM,"
				+ "HDR.DOC_DATE,"
				+ "ITM.PRECEEDING_INV_DATE,"
				+ "ITM.PRECEEDING_INV_NUM,"
				+ "HDR.ORIGINAL_DOC_TYPE,HDR.CRDR_PRE_GST,HDR.CUST_GSTIN,"
				+ "HDR.CUST_SUPP_TYPE,HDR.DIFF_PERCENT,HDR.ORIGINAL_CUST_GSTIN,"
				+ "HDR.CUST_SUPP_NAME,HDR.CUST_SUPP_CODE,HDR.CUST_SUPP_ADDRESS1,"
				+ "HDR.CUST_SUPP_ADDRESS2,HDR.CUST_SUPP_ADDRESS3,"
				+ "HDR.CUST_SUPP_ADDRESS4,HDR.BILL_TO_STATE,HDR.SHIP_TO_STATE,"
				+ "HDR.POS,HDR.STATE_APPLYING_CESS,HDR.SHIP_PORT_CODE,"
				+ "HDR.SHIP_BILL_NUM,HDR.SHIP_BILL_DATE,HDR.SECTION7_OF_IGST_FLAG,"
				+ "HDR.DOC_AMT AS INV_VALUE,HDR.REVERSE_CHARGE,HDR.TCS_FLAG,HDR."
				+ "ECOM_GSTIN,HDR.CLAIM_REFUND_FLAG,HDR.AUTOPOPULATE_TO_REFUND,"
				+ "HDR.ACCOUNTING_VOUCHER_NUM,HDR.ACCOUNTING_VOUCHER_DATE,"
				+ "HDR.EWAY_BILL_NUM,HDR.EWAY_BILL_DATE,HDR.FILE_ID,"
				+ "FIL.ID,ITM.DOC_HEADER_ID,ITM.GLCODE_TAXABLEVALUE,"
				+ "ITM.GLCODE_IGST,ITM.GLCODE_CGST,ITM.GLCODE_SGST,"
				+ "ITM.GLCODE_ADV_CESS,ITM.GLCODE_SP_CESS,ITM.GLCODE_STATE_CESS,"
				+ "ITM.ITM_NO,ITM.ITM_HSNSAC,ITM.PRODUCT_CODE,"
				+ "ITM.ITM_DESCRIPTION,"
				+ "ITM.ITM_TYPE,ITM.ITM_UQC,ITM.ITM_QTY,ITM.ITC_FLAG,"
				+ "ITM.CRDR_REASON,ITM.FOB,ITM.EXPORT_DUTY,"
				+ "ITM.USERDEFINED_FIELD1,"
				+ "ITM.USERDEFINED_FIELD2,ITM.USERDEFINED_FIELD3,"
				+ "ITM.USERDEFINED_FIELD4,ITM.USERDEFINED_FIELD5,"
				+ "ITM.USERDEFINED_FIELD6,ITM.USERDEFINED_FIELD7,"
				+ "ITM.USERDEFINED_FIELD8,ITM.USERDEFINED_FIELD9,"
				+ "ITM.USERDEFINED_FIELD10,ITM.USERDEFINED_FIELD11,"
				+ "ITM.USERDEFINED_FIELD12,ITM.USERDEFINED_FIELD13,"
				+ "ITM.USERDEFINED_FIELD14,ITM.USERDEFINED_FIELD15,"
				+ "ITM.OTHER_VALUES,ITM.ADJ_REF_NO,ITM.ADJ_REF_DATE,"
				+ "ITM.TAXABLE_VALUE,ITM.IGST_RATE,ITM.IGST_AMT,ITM.CGST_RATE,"
				+ "ITM.CGST_AMT,ITM.SGST_RATE,ITM.SGST_AMT,ITM.CESS_RATE_SPECIFIC,"
				+ "ITM.CESS_AMT_SPECIFIC,ITM.CESS_RATE_ADVALOREM,"
				+ "ITM.CESS_AMT_ADVALOREM,ITM.STATECESS_RATE,ITM.STATECESS_AMT,"
				+ "ITM.ADJ_TAXABLE_VALUE,ITM.ADJ_IGST_AMT,ITM.ADJ_CGST_AMT,"
				+ "ITM.ADJ_SGST_AMT,ITM. ADJ_CESS_AMT_ADVALOREM,"
				+ "ITM.ADJ_CESS_AMT_SPECIFIC,ITM.ADJ_STATECESS_AMT,ITM.TCS_AMT,"
				+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
				+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
				+ "AS INFO_ERROR_DESCRIPTION_ASP,"
				+ "ITM.MEMO_VALUE_IGST,ITM.MEMO_VALUE_CGST,ITM.MEMO_VALUE_SGST,"
				+ "ITM.IGST_RET1_IMPACT," + "ITM.CGST_RET1_IMPACT,"
				+ "ITM.SGST_RET1_IMPACT,"
				+ "ITM.LINE_ITEM_AMT AS ITM_INV_VALUE,"
				+ "ITM.PROFIT_CENTRE AS ITM_PROFIT_CENTRE,"
				+ "ITM.PLANT_CODE AS ITM_PLANT_CODE,"
				+ "ITM.LOCATION AS ITM_LOCATION "
				+ "FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "LEFT OUTER JOIN TF_OUTWARD_HEADER_ERROR_INFO () ERRH ON "
				+ "HDR.ID = ERRH.DOC_HEADER_ID INNER JOIN "
				+ "ANX_OUTWARD_DOC_ITEM  ITM ON HDR.ID = ITM.DOC_HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD"
				+ " LEFT OUTER JOIN TF_OUTWARD_ITEM_ERROR_INFO() ERRI ON "
				+ "ITM.DOC_HEADER_ID = ERRI.DOC_HEADER_ID AND "
				+ " IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' )  "
				+ "AND ITM.ITM_NO = ERRI.ITM_NO INNER JOIN FILE_STATUS FIL "
				+ "ON HDR.FILE_ID=FIL.ID WHERE IS_ERROR=FALSE " + buildQuery
				+ " ORDER BY DOC_NUM,ITM_NO";

	}
}
