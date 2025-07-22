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

import com.ey.advisory.app.data.views.client.Anx1ErrorReportsView;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;

@Component("Anx1ErrorReportsDaoImpl")
public class Anx1ErrorReportsDaoImpl implements Anx1ErrorReportsDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ErrorReportsDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	static String errorType = null;
	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public List<Anx1ErrorReportsView> getErrorReports(
			Anx1FileStatusReportsReqDto request) {

		Long fileId = request.getFileId();
		errorType = request.getErrorType();

		StringBuffer buildQuery = new StringBuffer();
		StringBuffer buildQuery1 = new StringBuffer();
		if (fileId != null) {
			buildQuery.append(" FILE_ID= :fileId");
		}

		if (errorType.equalsIgnoreCase(DownloadReportsConstant.ERRORACTIVE)) {
			buildQuery1.append(" AND IS_DELETE = FALSE");
		}
		if (errorType.equalsIgnoreCase(DownloadReportsConstant.ERRORINACTIVE)) {
			buildQuery1.append(" AND IS_DELETE = TRUE");
		}

		String queryStr = createErrorQueryString(buildQuery.toString(),
				buildQuery1.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DB data," + list);
		}
		return list.stream().map(o -> convertError(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Anx1ErrorReportsView convertError(Object[] arr) {
		Anx1ErrorReportsView obj = new Anx1ErrorReportsView();

		obj.setUserID(arr[1] != null ? arr[1].toString() : null);
		obj.setSourceFileName(arr[2] != null ? arr[2].toString() : null);
		obj.setProfitCentre(arr[3] != null ? arr[3].toString() : null);
		obj.setPlant(arr[4] != null ? arr[4].toString() : null);
		obj.setDivision(arr[5] != null ? arr[5].toString() : null);
		obj.setLocation(arr[6] != null ? arr[6].toString() : null);
		obj.setSalesOrganisation(arr[7] != null ? arr[7].toString() : null);
		obj.setDistributionChannel(arr[8] != null ? arr[8].toString() : null);
		obj.setUserAccess1(arr[9] != null ? arr[9].toString() : null);
		obj.setUserAccess2(arr[10] != null ? arr[10].toString() : null);
		obj.setUserAccess3(arr[11] != null ? arr[11].toString() : null);
		obj.setUserAccess4(arr[12] != null ? arr[12].toString() : null);
		obj.setUserAccess5(arr[13] != null ? arr[13].toString() : null);
		obj.setUserAccess6(arr[14] != null ? arr[14].toString() : null);
		obj.setReturnPeriod(arr[15] != null ? arr[15].toString() : null);
		obj.setSgstin(arr[16] != null ? arr[16].toString() : null);
		obj.setDocType(arr[17] != null ? arr[17].toString() : null);
		obj.setSupplyType(arr[117] != null ? arr[117].toString() : null);
		obj.setDocNum(arr[19] != null ? arr[19].toString() : null);
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[20] != null) {
				String strdate = arr[20].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setDocDate(newDate);
			} else {
				obj.setDocDate(null);
			}
		} else {
			obj.setDocDate(arr[20] != null ? arr[20].toString() : null);
		}
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[21] != null) {
				String strdate = arr[21].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setPrecedDate(newDate);
			} else {
				obj.setPrecedDate(null);
			}
		} else {
			obj.setPrecedDate(arr[21] != null ? arr[21].toString() : null);
		}
		obj.setPrecedNum(arr[22] != null ? arr[22].toString() : null);
		obj.setOrgDocType(arr[23] != null ? arr[23].toString() : null);
		obj.setCrdrPreGST(arr[24] != null ? arr[24].toString() : null);
		obj.setRecpGSTIN(arr[25] != null ? arr[25].toString() : null);
		obj.setRecpType(arr[26] != null ? arr[26].toString() : null);
		obj.setDiffPerFlag(arr[27] != null ? arr[27].toString() : null);
		obj.setOrgRecpGSTIN(arr[28] != null ? arr[28].toString() : null);
		obj.setRecpName(arr[29] != null ? arr[29].toString() : null);
		obj.setRecpCode(arr[30] != null ? arr[30].toString() : null);
		obj.setRecpAdd1(arr[31] != null ? arr[31].toString() : null);
		obj.setRecpAdd2(arr[32] != null ? arr[32].toString() : null);
		obj.setRecpAdd3(arr[33] != null ? arr[33].toString() : null);
		obj.setRecpAdd4(arr[34] != null ? arr[34].toString() : null);
		obj.setBillToState(arr[35] != null ? arr[35].toString() : null);
		obj.setShipToState(arr[36] != null ? arr[36].toString() : null);
		obj.setPos(arr[37] != null ? arr[37].toString() : null);
		obj.setStateAplyCess(arr[38] != null ? arr[38].toString() : null);
		obj.setPortCode(arr[39] != null ? arr[39].toString() : null);
		obj.setShippingBillNum(arr[40] != null ? arr[40].toString() : null);
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[41] != null) {
				String strdate = arr[41].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setShippingBillDate(newDate);
			} else {
				obj.setShippingBillDate(null);
			}
		} else {
			obj.setShippingBillDate(
					arr[41] != null ? arr[41].toString() : null);
		}
		obj.setSec7ofIGSTFlag(arr[42] != null ? arr[42].toString() : null);
		obj.setInvoiceValue(arr[118] != null ? arr[118].toString() : null);
		obj.setReverseChargeFlag(arr[44] != null ? arr[44].toString() : null);
		obj.setTcsFlag(arr[45] != null ? arr[45].toString() : null);
		obj.seteComGSTIN(arr[46] != null ? arr[46].toString() : null);
		obj.setClaimRefndFlag(arr[47] != null ? arr[47].toString() : null);
		obj.setAutoPopltToRefund(arr[48] != null ? arr[48].toString() : null);
		obj.setAccVoucherNum(arr[49] != null ? arr[49].toString() : null);
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[50] != null) {
				String strdate = arr[50].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setAccVoucherDate(newDate);
			} else {
				obj.setAccVoucherDate(null);
			}
		} else {
			obj.setAccVoucherDate(arr[50] != null ? arr[50].toString() : null);
		}
		obj.setEwaybillNum(arr[51] != null ? arr[51].toString() : null);
		obj.setEwaybillDate(arr[52] != null ? arr[52].toString() : null);
		obj.setgLCodeTaxableValue(arr[56] != null ? arr[56].toString() : null);
		obj.setgLCodeIGST(arr[57] != null ? arr[57].toString() : null);
		obj.setgLCodeCGST(arr[58] != null ? arr[58].toString() : null);
		obj.setgLCodeSGST(arr[59] != null ? arr[59].toString() : null);
		obj.setgLCodeAdvlormCess(arr[60] != null ? arr[60].toString() : null);
		obj.setgLCodeSpecificCess(arr[61] != null ? arr[61].toString() : null);
		obj.setgLCodeStateCess(arr[62] != null ? arr[62].toString() : null);
		obj.setItemNumber(arr[63] != null ? arr[63].toString() : null);
		obj.setHsnOrSac(arr[64] != null ? arr[64].toString() : null);
		obj.setProductCode(arr[65] != null ? arr[65].toString() : null);
		obj.setProductDesc(arr[66] != null ? arr[66].toString() : null);
		obj.setCategoryOfProduct(arr[67] != null ? arr[67].toString() : null);
		obj.setUom(arr[68] != null ? arr[68].toString() : null);
		obj.setQuantity(arr[69] != null ? arr[69].toString() : null);
		obj.setItcFlag(arr[70] != null ? arr[70].toString() : null);
		obj.setReasonForCrDbNote(arr[71] != null ? arr[71].toString() : null);
		obj.setFob(arr[72] != null ? arr[72].toString() : null);
		obj.setExportDuty(arr[73] != null ? arr[73].toString() : null);
		obj.setUserDefField1(arr[74] != null ? arr[74].toString() : null);
		obj.setUserDefField2(arr[75] != null ? arr[75].toString() : null);
		obj.setUserDefField3(arr[76] != null ? arr[76].toString() : null);
		obj.setUserDefField4(arr[77] != null ? arr[77].toString() : null);
		obj.setUserDefField5(arr[78] != null ? arr[78].toString() : null);
		obj.setUserDefField6(arr[79] != null ? arr[79].toString() : null);
		obj.setUserDefField7(arr[80] != null ? arr[80].toString() : null);
		obj.setUserDefField8(arr[81] != null ? arr[81].toString() : null);
		obj.setUserDefField9(arr[82] != null ? arr[82].toString() : null);
		obj.setUserDefField10(arr[83] != null ? arr[83].toString() : null);
		obj.setUserDefField11(arr[84] != null ? arr[84].toString() : null);
		obj.setUserDefField12(arr[85] != null ? arr[85].toString() : null);
		obj.setUserDefField13(arr[86] != null ? arr[86].toString() : null);
		obj.setUserDefField14(arr[87] != null ? arr[87].toString() : null);
		obj.setUserDefField15(arr[88] != null ? arr[88].toString() : null);
		obj.setOtherValue(arr[89] != null ? arr[89].toString() : null);
		obj.setAdjRefNo(arr[90] != null ? arr[90].toString() : null);
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[91] != null) {
				String strdate = arr[91].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setAdjRefDate(newDate);
			} else {
				obj.setAdjRefDate(null);
			}
		} else {
			obj.setAdjRefDate(arr[91] != null ? arr[91].toString() : null);
		}
		obj.setTaxableValue(arr[92] != null ? arr[92].toString() : null);
		obj.setIntegTaxRate(arr[93] != null ? arr[93].toString() : null);
		obj.setIntegTaxAmt(arr[94] != null ? arr[94].toString() : null);
		obj.setCentralTaxRate(arr[95] != null ? arr[95].toString() : null);
		obj.setCentralTaxAmt(arr[96] != null ? arr[96].toString() : null);
		obj.setStateUTTaxRate(arr[97] != null ? arr[97].toString() : null);
		obj.setStateUTTaxAmt(arr[98] != null ? arr[98].toString() : null);
		obj.setSpecfcCessRate(arr[99] != null ? arr[99].toString() : null);
		obj.setSpecfcCessAmt(arr[100] != null ? arr[100].toString() : null);
		obj.setAdvmCessRate(arr[101] != null ? arr[101].toString() : null);
		obj.setAdvmCessAmt(arr[102] != null ? arr[102].toString() : null);
		obj.setStateCessRate(arr[103] != null ? arr[103].toString() : null);
		obj.setStateCessAmt(arr[104] != null ? arr[104].toString() : null);
		obj.setTaxableValueAdj(arr[105] != null ? arr[105].toString() : null);
		obj.setIntegdTaxAmtAdj(arr[106] != null ? arr[106].toString() : null);
		obj.setCentralTaxAmtAdj(arr[107] != null ? arr[107].toString() : null);
		obj.setStateUTTaxAmtAdj(arr[108] != null ? arr[108].toString() : null);
		obj.setAdvlmCessAmtAdj(arr[109] != null ? arr[109].toString() : null);
		obj.setSpecfCessAmtAdj(arr[110] != null ? arr[110].toString() : null);
		obj.setStateCessAmtAdj(arr[111] != null ? arr[111].toString() : null);
		obj.setTcsAmt(arr[112] != null ? arr[112].toString() : null);
		obj.setAspInformationID(arr[113] != null ? arr[113].toString() : null);
		obj.setAspInformationDesc(
				arr[114] != null ? arr[114].toString() : null);
		obj.setAspErrorCode(arr[115] != null ? arr[115].toString() : null);
		obj.setAspErrorDesc(arr[116] != null ? arr[116].toString() : null);

		return obj;
	}

	private String createErrorQueryString(String buildQuery,
			String buildQuery1) {
		String queryStr = "";

		if ((DownloadReportsConstant.ERRORTOTAL).equalsIgnoreCase(errorType)) {

			queryStr = "SELECT HDR.ID AS HID,"
					+ "TO_CHAR(ITM.USER_ID) AS USER_ID,"
					+ "TO_CHAR(ITM.SOURCE_FILENAME) AS SOURCE_FILENAME,"
					+ "TO_CHAR(ITM.PROFIT_CENTRE) AS PROFIT_CENTRE,"
					+ "TO_CHAR(ITM.PLANT_CODE) AS PLANT_CODE,"
					+ "TO_CHAR(ITM.DIVISION) AS DIVISION,"
					+ "TO_CHAR(ITM.LOCATION) AS LOCATION,"
					+ "TO_CHAR(ITM.SALES_ORGANIZATION) AS SALES_ORGANIZATION,"
					+ "TO_CHAR(ITM.DISTRIBUTION_CHANNEL) AS DISTRIBUTION_CHANNEL,"
					+ "TO_CHAR(ITM.USERACCESS1) AS USERACCESS1,"
					+ "TO_CHAR(ITM.USERACCESS2) AS USERACCESS2,"
					+ "TO_CHAR(ITM.USERACCESS3) AS USERACCESS3,"
					+ "TO_CHAR(ITM.USERACCESS4) AS USERACCESS4,"
					+ "TO_CHAR(ITM.USERACCESS5) AS USERACCESS5,"
					+ "TO_CHAR(ITM.USERACCESS6) AS USERACCESS6,"
					+ "TO_CHAR(ITM.RETURN_PERIOD) AS RETURN_PERIOD ,"
					+ "TO_CHAR(HDR.SUPPLIER_GSTIN) AS SUPPLIER_GSTIN,"
					+ "TO_CHAR(HDR.DOC_TYPE) AS DOC_TYPE,"
					+ "TO_CHAR(HDR.SUPPLY_TYPE) AS SUPPLY_TYPE,"
					+ "TO_CHAR(HDR.DOC_NUM) AS DOC_NUM,TO_CHAR(ITM.DOC_DATE) "
					+ "AS DOC_DATE,"
					+ "TO_CHAR(ITM.PRECEEDING_INV_DATE) AS PRECEEDING_INV_DATE,"
					+ "TO_CHAR(ITM.PRECEEDING_INV_NUM) AS PRECEEDING_INV_NUM,"
					+ "TO_CHAR(ITM.ORIGINAL_DOC_TYPE) AS ORIGINAL_DOC_TYPE,"
					+ "TO_CHAR(ITM.CRDR_PRE_GST) AS CRDR_PRE_GST,"
					+ "TO_CHAR(ITM.CUST_GSTIN) AS CUST_GSTIN,"
					+ "TO_CHAR(ITM.CUST_SUPP_TYPE) AS CUST_SUPP_TYPE,"
					+ "TO_CHAR(ITM.DIFF_PERCENT) AS DIFF_PERCENT,"
					+ "TO_CHAR(ITM.ORIGINAL_CUST_GSTIN) AS ORIGINAL_CUST_GSTIN,"
					+ "TO_CHAR(ITM.CUST_SUPP_NAME) AS CUST_SUPP_NAME,"
					+ "TO_CHAR(ITM.CUST_SUPP_CODE) AS CUST_SUPP_CODE,"
					+ "TO_CHAR(ITM.CUST_SUPP_ADDRESS1) AS CUST_SUPP_ADDRESS1,"
					+ "TO_CHAR(ITM.CUST_SUPP_ADDRESS2) AS CUST_SUPP_ADDRESS2,"
					+ "TO_CHAR(HDR.CUST_SUPP_ADDRESS3) AS CUST_SUPP_ADDRESS3,"
					+ "TO_CHAR(ITM.CUST_SUPP_ADDRESS4) AS CUST_SUPP_ADDRESS4,"
					+ "TO_CHAR(ITM.BILL_TO_STATE) AS BILL_TO_STATE,"
					+ "TO_CHAR(ITM.SHIP_TO_STATE) AS SHIP_TO_STATE,"
					+ "TO_CHAR(ITM.POS) AS POS,"
					+ "TO_CHAR(ITM.STATE_APPLYING_CESS) AS STATE_APPLYING_CESS,"
					+ "TO_CHAR(ITM.SHIP_PORT_CODE) AS SHIP_PORT_CODE,"
					+ "TO_CHAR(ITM.SHIP_BILL_NUM) AS SHIP_BILL_NUM,"
					+ "TO_CHAR(ITM.SHIP_BILL_DATE) AS SHIP_BILL_DATE,"
					+ "TO_CHAR(ITM.SECTION7_OF_IGST_FLAG) AS SECTION7_OF_IGST_FLAG,"
					+ "TO_CHAR(HDR.DOC_AMT) AS INV_VALUE,"
					+ "TO_CHAR(ITM.REVERSE_CHARGE) AS REVERSE_CHARGE,"
					+ "TO_CHAR(ITM.TCS_FLAG) AS TCS_FLAG,"
					+ "TO_CHAR(ITM.ECOM_GSTIN) AS ECOM_GSTIN,"
					+ "TO_CHAR(ITM.CLAIM_REFUND_FLAG) AS CLAIM_REFUND_FLAG,"
					+ "TO_CHAR(ITM.AUTOPOPULATE_TO_REFUND) AS AUTOPOPULATE_TO_REFUND,"
					+ "TO_CHAR(ITM.ACCOUNTING_VOUCHER_NUM) AS ACCOUNTING_VOUCHER_NUM,"
					+ "TO_CHAR(ITM.ACCOUNTING_VOUCHER_DATE) "
					+ "AS ACCOUNTING_VOUCHER_DATE,TO_CHAR(ITM.EWAY_BILL_NUM) AS EWAY_BILL_NUM,"
					+ "TO_CHAR(ITM.EWAY_BILL_DATE) AS EWAY_BILL_DATE,HDR.FILE_ID,"
					+ "FIL.ID,ITM.DOC_HEADER_ID,"
					+ "TO_CHAR(ITM.GLCODE_TAXABLEVALUE) AS GLCODE_TAXABLEVALUE,"
					+ "TO_CHAR(ITM.GLCODE_IGST) AS GLCODE_IGST,"
					+ "TO_CHAR(ITM.GLCODE_CGST) AS GLCODE_CGST,"
					+ "TO_CHAR(ITM.GLCODE_SGST) AS GLCODE_SGST,"
					+ "TO_CHAR(ITM.GLCODE_ADV_CESS) AS GLCODE_ADV_CESS,"
					+ "TO_CHAR(ITM.GLCODE_SP_CESS) AS GLCODE_SP_CESS,"
					+ "TO_CHAR(ITM.GLCODE_STATE_CESS) AS GLCODE_STATE_CESS,"
					+ "TO_CHAR(ITM.ITM_NO) AS ITM_NO ,TO_CHAR(ITM.ITM_HSNSAC) "
					+ "AS ITM_HSNSAC,TO_CHAR(ITM.PRODUCT_CODE) AS PRODUCT_CODE,"
					+ "TO_CHAR(ITM.ITM_DESCRIPTION) AS ITM_DESCRIPTION,"
					+ "TO_CHAR(ITM.ITM_TYPE) AS ITM_TYPE,TO_CHAR(ITM.ITM_UQC) AS ITM_UQC,"
					+ "TO_CHAR(ITM.ITM_QTY) AS ITM_QTY,TO_CHAR(ITM.ITC_FLAG) AS ITC_FLAG,"
					+ "TO_CHAR(ITM.CRDR_REASON) AS CRDR_REASON,TO_CHAR(ITM.FOB)"
					+ "AS FOB,TO_CHAR(ITM.EXPORT_DUTY) AS EXPORT_DUTY,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD1) AS USERDEFINED_FIELD1,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD2) AS USERDEFINED_FIELD2,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD3) AS USERDEFINED_FIELD3,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD4) AS USERDEFINED_FIELD4,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD5) AS USERDEFINED_FIELD5,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD6) AS USERDEFINED_FIELD6,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD7) AS USERDEFINED_FIELD7,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD8) AS USERDEFINED_FIELD8,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD9) AS USERDEFINED_FIELD9,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD10) AS USERDEFINED_FIELD10,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD11) AS USERDEFINED_FIELD11,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD12) AS USERDEFINED_FIELD12,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD13) AS USERDEFINED_FIELD13,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD14) AS USERDEFINED_FIELD14,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD15) AS USERDEFINED_FIELD15,"
					+ "TO_CHAR(ITM.OTHER_VALUES) "
					+ "AS OTHER_VALUES,ITM.ADJ_REF_NO,"
					+ "TO_CHAR(ITM.ADJ_REF_DATE) AS ADJ_REF_DATE,"
					+ "TO_CHAR(ITM.TAXABLE_VALUE) AS TAXABLE_VALUE,"
					+ "TO_CHAR(ITM.IGST_RATE) AS IGST_RATE,"
					+ "TO_CHAR(ITM.IGST_AMT) AS IGST_AMT,"
					+ "TO_CHAR(ITM.CGST_RATE) AS CGST_RATE,"
					+ "TO_CHAR(ITM.CGST_AMT) AS CGST_AMT,"
					+ "TO_CHAR(ITM.SGST_RATE) AS SGST_RATE,"
					+ "TO_CHAR(ITM.SGST_AMT) AS SGST_AMT,"
					+ "TO_CHAR(ITM.CESS_RATE_SPECIFIC) AS CESS_RATE_SPECIFIC,"
					+ "TO_CHAR(ITM.CESS_AMT_SPECIFIC) AS CESS_AMT_SPECIFIC,"
					+ "TO_CHAR(ITM.CESS_RATE_ADVALOREM) AS CESS_RATE_ADVALOREM,"
					+ "TO_CHAR(ITM.CESS_AMT_ADVALOREM) AS CESS_AMT_ADVALOREM,"
					+ "TO_CHAR(ITM.STATECESS_RATE) AS STATECESS_RATE,"
					+ "TO_CHAR(ITM.STATECESS_AMT) AS STATECESS_AMT,"
					+ "TO_CHAR(ITM.ADJ_TAXABLE_VALUE) AS ADJ_TAXABLE_VALUE,"
					+ "TO_CHAR(ITM.ADJ_IGST_AMT) AS ADJ_IGST_AMT,"
					+ "TO_CHAR(ITM.ADJ_CGST_AMT) AS ADJ_CGST_AMT,"
					+ "TO_CHAR(ITM.ADJ_SGST_AMT) AS ADJ_SGST_AMT,"
					+ "TO_CHAR(ITM. ADJ_CESS_AMT_ADVALOREM) AS ADJ_CESS_AMT_ADVALOREM,"
					+ "TO_CHAR(ITM.ADJ_CESS_AMT_SPECIFIC) AS ADJ_CESS_AMT_SPECIFIC,"
					+ "TO_CHAR(ITM.ADJ_STATECESS_AMT) AS ADJ_STATECESS_AMT,"
					+ "TO_CHAR(ITM.TCS_AMT) AS TCS_AMT,"
					+ "TRIM(',' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
					+ "TRIM(',' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
					+ "AS INFO_ERROR_DESCRIPTION_ASP,"
					+ "TRIM(',' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP,"
					+ "TRIM(',' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) "
					+ "AS ERROR_DESCRIPTION_ASP,"
					+ "TO_CHAR(ITM.SUPPLY_TYPE) AS ITM_SUPPLY_TYPE,"
					+ "TO_CHAR(ITM.LINE_ITEM_AMT) AS ITM_INV_VALUE "
					+ "FROM ANX_OUTWARD_DOC_HEADER HDR "
					+ "LEFT OUTER JOIN TF_OUTWARD_HEADER_ERROR_INFO() ERRH ON "
					+ "HDR.ID = ERRH.DOC_HEADER_ID INNER JOIN ANX_OUTWARD_DOC_ITEM "
					+ "ITM ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD "
					+ "= ITM.DERIVED_RET_PERIOD LEFT OUTER JOIN "
					+ "TF_OUTWARD_ITEM_ERROR_INFO() ERRI ON ITM.DOC_HEADER_ID = "
					+ "ERRI.DOC_HEADER_ID AND "
					+ "IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' ) AND "
					+ "  IFNULL(ITM.ITM_NO,'0' ) = IFNULL(ERRI.ITM_NO,'0' ) "
					+ "INNER JOIN " + "FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
					+ "WHERE HDR.IS_ERROR = TRUE  AND ITM.DOC_HEADER_ID IN "
					+ "(SELECT DOC_HEADER_ID FROM ANX_OUTWARD_DOC_ERROR WHERE VAL_TYPE = 'BV' ) "
					+ "AND " + buildQuery + " AND IS_DELETE = FALSE "
					+ " UNION ALL "
					+ "SELECT HDR.ID AS HID,ITM.USER_ID,ITM.SOURCE_FILENAME,"
					+ "ITM.PROFIT_CENTRE,ITM.PLANT_CODE,ITM.DIVISION,ITM.LOCATION,"
					+ "ITM.SALES_ORGANIZATION,ITM.DISTRIBUTION_CHANNEL,ITM.USERACCESS1,"
					+ "ITM.USERACCESS2,ITM.USERACCESS3,ITM.USERACCESS4,ITM.USERACCESS5,"
					+ "ITM.USERACCESS6,ITM.RETURN_PERIOD,HDR.SUPPLIER_GSTIN,"
					+ "HDR.DOC_TYPE,HDR.SUPPLY_TYPE,HDR.DOC_NUM,ITM.DOC_DATE,"
					+ "ITM.PRECEEDING_INV_DATE,ITM.PRECEEDING_INV_NUM,"
					+ "ITM.ORIGINAL_DOC_TYPE,ITM.CRDR_PRE_GST,ITM.CUST_GSTIN,"
					+ "ITM.CUST_SUPP_TYPE,ITM.DIFF_PERCENT,ITM.ORIGINAL_CUST_GSTIN,"
					+ "ITM.CUST_SUPP_NAME,ITM.CUST_SUPP_CODE,ITM.CUST_SUPP_ADDRESS1,"
					+ "ITM.CUST_SUPP_ADDRESS2,HDR.CUST_SUPP_ADDRESS3,"
					+ "ITM.CUST_SUPP_ADDRESS4,ITM.BILL_TO_STATE,ITM.SHIP_TO_STATE,"
					+ "ITM.POS,ITM.STATE_APPLYING_CESS,ITM.SHIP_PORT_CODE,"
					+ "ITM.SHIP_BILL_NUM,ITM.SHIP_BILL_DATE,ITM.SECTION7_OF_IGST_FLAG,"
					+ "HDR.DOC_AMT,ITM.REVERSE_CHARGE,ITM.TCS_FLAG,ITM.ECOM_GSTIN,"
					+ "ITM.CLAIM_REFUND_FLAG,ITM.AUTOPOPULATE_TO_REFUND,"
					+ "ITM.ACCOUNTING_VOUCHER_NUM,ITM.ACCOUNTING_VOUCHER_DATE,"
					+ "ITM.EWAY_BILL_NUM,ITM.EWAY_BILL_DATE,HDR.FILE_ID,FIL.ID,"
					+ "ITM.DOC_HEADER_ID,ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST,"
					+ "ITM.GLCODE_CGST,ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS,"
					+ "ITM.GLCODE_SP_CESS,ITM.GLCODE_STATE_CESS,ITM.ITM_NO ,"
					+ "ITM.ITM_HSNSAC,ITM.PRODUCT_CODE,ITM.ITM_DESCRIPTION,ITM.ITM_TYPE,"
					+ "ITM.ITM_UQC,ITM.ITM_QTY,ITM.ITC_FLAG,ITM.CRDR_REASON,ITM.FOB,"
					+ "ITM.EXPORT_DUTY,ITM.USERDEFINED_FIELD1,ITM.USERDEFINED_FIELD2,"
					+ "ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4,"
					+ "ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6,"
					+ "ITM.USERDEFINED_FIELD7,ITM.USERDEFINED_FIELD8,"
					+ "ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10,"
					+ "ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12,"
					+ "ITM.USERDEFINED_FIELD13,ITM.USERDEFINED_FIELD14,"
					+ "ITM.USERDEFINED_FIELD15,ITM.OTHER_VALUES,ITM.ADJ_REF_NO,"
					+ "ITM.ADJ_REF_DATE,ITM.TAXABLE_VALUE,ITM.IGST_RATE,ITM.IGST_AMT,"
					+ "ITM.CGST_RATE,ITM.CGST_AMT,ITM.SGST_RATE,ITM.SGST_AMT,"
					+ "ITM.CESS_RATE_SPECIFIC,ITM.CESS_AMT_SPECIFIC,"
					+ "ITM.CESS_RATE_ADVALOREM,ITM.CESS_AMT_ADVALOREM,"
					+ "ITM.STATECESS_RATE,ITM.STATECESS_AMT,ITM.ADJ_TAXABLE_VALUE,"
					+ "ITM.ADJ_IGST_AMT,ITM.ADJ_CGST_AMT,ITM.ADJ_SGST_AMT,"
					+ "ITM.ADJ_CESS_AMT_ADVALOREM,ITM.ADJ_CESS_AMT_SPECIFIC,"
					+ "ITM.ADJ_STATECESS_AMT,ITM.TCS_AMT,"
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
					+ "AS INFO_ERROR_DESCRIPTION_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) "
					+ "AS ERROR_DESCRIPTION_ASP," + "ITM.SUPPLY_TYPE,"
					+ "ITM.LINE_ITEM_AMT " + "FROM ANX_OUTWARD_ERR_HEADER HDR "
					+ "LEFT OUTER JOIN TF_OUTWARD_HEADER_ERROR_INFO () ERRH ON "
					+ "HDR.ID = ERRH.DOC_HEADER_ID INNER JOIN ANX_OUTWARD_ERR_ITEM "
					+ "ITM ON HDR.ID = ITM.DOC_HEADER_ID LEFT OUTER JOIN "
					+ "TF_OUTWARD_ITEM_ERROR_INFO() ERRI ON ITM.DOC_HEADER_ID = "
					+ "ERRI.DOC_HEADER_ID AND "
					+ "IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' ) AND "
					+ "IFNULL(ITM.ITM_NO,'0' ) = IFNULL(ERRI.ITM_NO,'0' )"
					+ "INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID WHERE "
					+ "HDR.IS_ERROR = 'true'  AND ITM.DOC_HEADER_ID IN "
					+ "(SELECT DOC_HEADER_ID FROM "
					+ "ANX_OUTWARD_DOC_ERROR WHERE VAL_TYPE = 'SV' ) " + " AND "
					+ buildQuery + " ORDER BY DOC_NUM,ITM_NO";

		} else if ((DownloadReportsConstant.ERRORACTIVE)
				.equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			queryStr = "SELECT HDR.ID AS HID,ITM.USER_ID AS USER_ID,"
					+ "ITM.SOURCE_FILENAME AS SOURCE_FILENAME,"
					+ "ITM.PROFIT_CENTRE AS PROFIT_CENTRE,ITM.PLANT_CODE AS "
					+ "PLANT_CODE,ITM.DIVISION  AS DIVISION,ITM.LOCATION AS "
					+ "LOCATION,ITM.SALES_ORGANIZATION AS SALES_ORGANIZATION,"
					+ "ITM.DISTRIBUTION_CHANNEL AS DISTRIBUTION_CHANNEL,"
					+ "ITM.USERACCESS1 AS USERACCESS1,ITM.USERACCESS2 AS USERACCESS2,"
					+ "ITM.USERACCESS3 AS USERACCESS3,ITM.USERACCESS4 AS USERACCESS4,"
					+ "ITM.USERACCESS5 AS USERACCESS5,ITM.USERACCESS6 AS USERACCESS6,"
					+ "ITM.RETURN_PERIOD AS RETURN_PERIOD ,HDR.SUPPLIER_GSTIN AS "
					+ "SUPPLIER_GSTIN,HDR.DOC_TYPE AS DOC_TYPE,HDR.SUPPLY_TYPE "
					+ "AS SUPPLY_TYPE,HDR.DOC_NUM AS DOC_NUM,ITM.DOC_DATE AS "
					+ "DOC_DATE,"
					+ "ITM.PRECEEDING_INV_DATE AS PRECEEDING_INV_DATE,"
					+ "ITM.PRECEEDING_INV_NUM AS PRECEEDING_INV_NUM,"
					+ "ITM.ORIGINAL_DOC_TYPE AS ORIGINAL_DOC_TYPE,"
					+ "ITM.CRDR_PRE_GST AS CRDR_PRE_GST,ITM.CUST_GSTIN AS CUST_GSTIN,"
					+ "ITM.CUST_SUPP_TYPE AS CUST_SUPP_TYPE,ITM.DIFF_PERCENT AS "
					+ "DIFF_PERCENT,ITM.ORIGINAL_CUST_GSTIN AS ORIGINAL_CUST_GSTIN,"
					+ "ITM.CUST_SUPP_NAME AS CUST_SUPP_NAME,"
					+ "ITM.CUST_SUPP_CODE AS CUST_SUPP_CODE,"
					+ "ITM.CUST_SUPP_ADDRESS1 AS CUST_SUPP_ADDRESS1,"
					+ "ITM.CUST_SUPP_ADDRESS2 AS CUST_SUPP_ADDRESS2,"
					+ "HDR.CUST_SUPP_ADDRESS3 AS CUST_SUPP_ADDRESS3,"
					+ "ITM.CUST_SUPP_ADDRESS4 AS CUST_SUPP_ADDRESS4,"
					+ "ITM.BILL_TO_STATE AS BILL_TO_STATE,ITM.SHIP_TO_STATE AS "
					+ "SHIP_TO_STATE,ITM.POS AS POS,ITM.STATE_APPLYING_CESS AS "
					+ "STATE_APPLYING_CESS,ITM.SHIP_PORT_CODE AS SHIP_PORT_CODE,"
					+ "ITM.SHIP_BILL_NUM AS SHIP_BILL_NUM,ITM.SHIP_BILL_DATE AS "
					+ "SHIP_BILL_DATE,ITM.SECTION7_OF_IGST_FLAG AS "
					+ "SECTION7_OF_IGST_FLAG,HDR.DOC_AMT AS INV_VALUE,"
					+ "ITM.REVERSE_CHARGE AS REVERSE_CHARGE,ITM.TCS_FLAG AS TCS_FLAG,"
					+ "ITM.ECOM_GSTIN AS ECOM_GSTIN,ITM.CLAIM_REFUND_FLAG AS "
					+ "CLAIM_REFUND_FLAG,ITM.AUTOPOPULATE_TO_REFUND AS "
					+ "AUTOPOPULATE_TO_REFUND,ITM.ACCOUNTING_VOUCHER_NUM AS "
					+ "ACCOUNTING_VOUCHER_NUM,ITM.ACCOUNTING_VOUCHER_DATE AS "
					+ "ACCOUNTING_VOUCHER_DATE,ITM.EWAY_BILL_NUM AS EWAY_BILL_NUM,"
					+ "ITM.EWAY_BILL_DATE AS EWAY_BILL_DATE,HDR.FILE_ID,"
					+ "FIL.ID,ITM.DOC_HEADER_ID,ITM.GLCODE_TAXABLEVALUE AS "
					+ "GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST AS GLCODE_IGST,"
					+ "ITM.GLCODE_CGST AS GLCODE_CGST,ITM.GLCODE_SGST AS GLCODE_SGST,"
					+ "ITM.GLCODE_ADV_CESS AS GLCODE_ADV_CESS,ITM.GLCODE_SP_CESS "
					+ "AS GLCODE_SP_CESS,ITM.GLCODE_STATE_CESS AS GLCODE_STATE_CESS,"
					+ "ITM.ITM_NO AS ITM_NO ,ITM.ITM_HSNSAC AS ITM_HSNSAC,"
					+ "ITM.PRODUCT_CODE AS PRODUCT_CODE,ITM.ITM_DESCRIPTION AS "
					+ "ITM_DESCRIPTION,ITM.ITM_TYPE AS ITM_TYPE,ITM.ITM_UQC AS "
					+ "ITM_UQC,ITM.ITM_QTY AS ITM_QTY,ITM.ITC_FLAG AS ITC_FLAG,"
					+ "ITM.CRDR_REASON AS CRDR_REASON,ITM.FOB "
					+ "AS FOB,ITM.EXPORT_DUTY AS EXPORT_DUTY,"
					+ "ITM.USERDEFINED_FIELD1 AS USERDEFINED_FIELD1,"
					+ "ITM.USERDEFINED_FIELD2 AS USERDEFINED_FIELD2,"
					+ "ITM.USERDEFINED_FIELD3 AS USERDEFINED_FIELD3,"
					+ "ITM.USERDEFINED_FIELD4 AS USERDEFINED_FIELD4,"
					+ "ITM.USERDEFINED_FIELD5 AS USERDEFINED_FIELD5,"
					+ "ITM.USERDEFINED_FIELD6 AS USERDEFINED_FIELD6,"
					+ "ITM.USERDEFINED_FIELD7 AS USERDEFINED_FIELD7,"
					+ "ITM.USERDEFINED_FIELD8 AS USERDEFINED_FIELD8,"
					+ "ITM.USERDEFINED_FIELD9 AS USERDEFINED_FIELD9,"
					+ "ITM.USERDEFINED_FIELD10 AS USERDEFINED_FIELD10,"
					+ "ITM.USERDEFINED_FIELD11 AS USERDEFINED_FIELD11,"
					+ "ITM.USERDEFINED_FIELD12 AS USERDEFINED_FIELD12,"
					+ "ITM.USERDEFINED_FIELD13 AS USERDEFINED_FIELD13,"
					+ "ITM.USERDEFINED_FIELD14 AS USERDEFINED_FIELD14,"
					+ "ITM.USERDEFINED_FIELD15 AS USERDEFINED_FIELD15,"
					+ "ITM.OTHER_VALUES AS OTHER_VALUES,ITM.ADJ_REF_NO,"
					+ "ITM.ADJ_REF_DATE AS ADJ_REF_DATE,ITM.TAXABLE_VALUE "
					+ "AS TAXABLE_VALUE,ITM.IGST_RATE AS IGST_RATE,ITM.IGST_AMT "
					+ "AS IGST_AMT,ITM.CGST_RATE AS CGST_RATE,ITM.CGST_AMT AS "
					+ "CGST_AMT,ITM.SGST_RATE AS SGST_RATE,ITM.SGST_AMT AS SGST_AMT,"
					+ "ITM.CESS_RATE_SPECIFIC AS CESS_RATE_SPECIFIC,"
					+ "ITM.CESS_AMT_SPECIFIC AS CESS_AMT_SPECIFIC,"
					+ "ITM.CESS_RATE_ADVALOREM AS CESS_RATE_ADVALOREM,"
					+ "ITM.CESS_AMT_ADVALOREM AS CESS_AMT_ADVALOREM,"
					+ "ITM.STATECESS_RATE AS STATECESS_RATE,ITM.STATECESS_AMT "
					+ "AS STATECESS_AMT,ITM.ADJ_TAXABLE_VALUE AS ADJ_TAXABLE_VALUE,"
					+ "ITM.ADJ_IGST_AMT AS ADJ_IGST_AMT,ITM.ADJ_CGST_AMT AS "
					+ "ADJ_CGST_AMT,ITM.ADJ_SGST_AMT AS ADJ_SGST_AMT,"
					+ "ITM. ADJ_CESS_AMT_ADVALOREM AS ADJ_CESS_AMT_ADVALOREM,"
					+ "ITM.ADJ_CESS_AMT_SPECIFIC AS ADJ_CESS_AMT_SPECIFIC,"
					+ "ITM.ADJ_STATECESS_AMT AS ADJ_STATECESS_AMT,"
					+ "ITM.TCS_AMT AS TCS_AMT,"
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
					+ "AS INFO_ERROR_DESCRIPTION_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) "
					+ "AS ERROR_DESCRIPTION_ASP,"
					+ "ITM.SUPPLY_TYPE AS ITM_SUPPLY_TYPE,"
					+ "ITM.LINE_ITEM_AMT AS ITM_INV_VALUE "
					+ "FROM ANX_OUTWARD_DOC_HEADER HDR LEFT OUTER JOIN "
					+ "TF_OUTWARD_HEADER_ERROR_INFO() ERRH ON "
					+ "HDR.ID = ERRH.DOC_HEADER_ID INNER JOIN "
					+ "ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND "
					+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
					+ "LEFT OUTER JOIN TF_OUTWARD_ITEM_ERROR_INFO() ERRI ON "
					+ "ITM.DOC_HEADER_ID = ERRI.DOC_HEADER_ID AND "
					+ "IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' ) "
					+ "AND "
					+ "IFNULL(ITM.ITM_NO,'0' ) = IFNULL(ERRI.ITM_NO,'0' ) "
					+ "INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID WHERE "
					+ "HDR.IS_ERROR = TRUE AND ITM.DOC_HEADER_ID IN "
					+ "(SELECT DOC_HEADER_ID FROM ANX_OUTWARD_DOC_ERROR "
					+ "WHERE VAL_TYPE = 'BV' ) " + buildQuery1 + " AND "
					+ buildQuery + " ORDER BY DOC_NUM,ITM_NO";

		} else if ((DownloadReportsConstant.ERRORSV)
				.equalsIgnoreCase(errorType)) {

			queryStr = "SELECT HDR.ID AS HID,ITM.USER_ID,ITM.SOURCE_FILENAME,"
					+ "ITM.PROFIT_CENTRE,ITM.PLANT_CODE,ITM.DIVISION,ITM.LOCATION,"
					+ "ITM.SALES_ORGANIZATION,ITM.DISTRIBUTION_CHANNEL,ITM.USERACCESS1,"
					+ "ITM.USERACCESS2,ITM.USERACCESS3,ITM.USERACCESS4,ITM.USERACCESS5,"
					+ "ITM.USERACCESS6,ITM.RETURN_PERIOD,HDR.SUPPLIER_GSTIN,"
					+ "HDR.DOC_TYPE,HDR.SUPPLY_TYPE,HDR.DOC_NUM,ITM.DOC_DATE,"
					+ "ITM.PRECEEDING_INV_DATE,ITM.PRECEEDING_INV_NUM,"
					+ "ITM.ORIGINAL_DOC_TYPE,ITM.CRDR_PRE_GST,ITM.CUST_GSTIN,"
					+ "ITM.CUST_SUPP_TYPE,ITM.DIFF_PERCENT,ITM.ORIGINAL_CUST_GSTIN,"
					+ "ITM.CUST_SUPP_NAME,ITM.CUST_SUPP_CODE,ITM.CUST_SUPP_ADDRESS1,"
					+ "ITM.CUST_SUPP_ADDRESS2,HDR.CUST_SUPP_ADDRESS3,"
					+ "ITM.CUST_SUPP_ADDRESS4,ITM.BILL_TO_STATE,ITM.SHIP_TO_STATE,"
					+ "ITM.POS,ITM.STATE_APPLYING_CESS,ITM.SHIP_PORT_CODE,"
					+ "ITM.SHIP_BILL_NUM,ITM.SHIP_BILL_DATE,ITM.SECTION7_OF_IGST_FLAG,"
					+ "HDR.DOC_AMT,ITM.REVERSE_CHARGE,ITM.TCS_FLAG,ITM.ECOM_GSTIN,"
					+ "ITM.CLAIM_REFUND_FLAG,ITM.AUTOPOPULATE_TO_REFUND,"
					+ "ITM.ACCOUNTING_VOUCHER_NUM,ITM.ACCOUNTING_VOUCHER_DATE,"
					+ "ITM.EWAY_BILL_NUM,ITM.EWAY_BILL_DATE,HDR.FILE_ID,FIL.ID,"
					+ "ITM.DOC_HEADER_ID,ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST,"
					+ "ITM.GLCODE_CGST,ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS,"
					+ "ITM.GLCODE_SP_CESS,ITM.GLCODE_STATE_CESS,ITM.ITM_NO,"
					+ "ITM.ITM_HSNSAC,ITM.PRODUCT_CODE,ITM.ITM_DESCRIPTION,ITM.ITM_TYPE,"
					+ "ITM.ITM_UQC,ITM.ITM_QTY,ITM.ITC_FLAG,ITM.CRDR_REASON,ITM.FOB,"
					+ "ITM.EXPORT_DUTY,ITM.USERDEFINED_FIELD1,ITM.USERDEFINED_FIELD2,"
					+ "ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4,"
					+ "ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6,"
					+ "ITM.USERDEFINED_FIELD7,ITM.USERDEFINED_FIELD8,"
					+ "ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10,"
					+ "ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12,"
					+ "ITM.USERDEFINED_FIELD13,ITM.USERDEFINED_FIELD14,"
					+ "ITM.USERDEFINED_FIELD15,ITM.OTHER_VALUES,ITM.ADJ_REF_NO,"
					+ "ITM.ADJ_REF_DATE,ITM.TAXABLE_VALUE,ITM.IGST_RATE,ITM.IGST_AMT,"
					+ "ITM.CGST_RATE,ITM.CGST_AMT,ITM.SGST_RATE,ITM.SGST_AMT,"
					+ "ITM.CESS_RATE_SPECIFIC,ITM.CESS_AMT_SPECIFIC,"
					+ "ITM.CESS_RATE_ADVALOREM,ITM.CESS_AMT_ADVALOREM,"
					+ "ITM.STATECESS_RATE,ITM.STATECESS_AMT,ITM.ADJ_TAXABLE_VALUE,"
					+ "ITM.ADJ_IGST_AMT,ITM.ADJ_CGST_AMT,ITM.ADJ_SGST_AMT,"
					+ "ITM.ADJ_CESS_AMT_ADVALOREM,ITM.ADJ_CESS_AMT_SPECIFIC,"
					+ "ITM.ADJ_STATECESS_AMT,ITM.TCS_AMT,"
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
					+ "AS INFO_ERROR_DESCRIPTION_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) "
					+ "AS ERROR_DESCRIPTION_ASP,"
					+ "ITM.SUPPLY_TYPE AS ITM_SUPPLY_TYPE,"
					+ "ITM.LINE_ITEM_AMT FROM ANX_OUTWARD_ERR_HEADER HDR "
					+ "LEFT OUTER JOIN TF_OUTWARD_HEADER_ERROR_INFO () ERRH ON "
					+ "HDR.ID = ERRH.DOC_HEADER_ID INNER JOIN ANX_OUTWARD_ERR_ITEM "
					+ "ITM ON HDR.ID = ITM.DOC_HEADER_ID LEFT OUTER JOIN "
					+ "TF_OUTWARD_ITEM_ERROR_INFO() ERRI ON ITM.DOC_HEADER_ID = "
					+ "ERRI.DOC_HEADER_ID AND "
					+ " IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' ) "
					+ "AND "
					+ "IFNULL(ITM.ITM_NO,'0' ) = IFNULL(ERRI.ITM_NO,'0' ) "
					+ "INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID WHERE "
					+ "HDR.IS_ERROR = 'true'  AND " + "ITM.DOC_HEADER_ID IN "
					+ "(SELECT DOC_HEADER_ID FROM ANX_OUTWARD_DOC_ERROR "
					+ "WHERE VAL_TYPE = 'SV' ) " + "AND " + buildQuery
					+ " ORDER BY DOC_NUM,ITM_NO";
		}
		return queryStr;
	}
}