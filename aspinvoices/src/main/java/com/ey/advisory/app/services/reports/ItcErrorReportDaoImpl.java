/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.ItcTotalRecords;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.ErrorMasterUtil;

import com.google.common.base.Strings;

/**
 * @author Sujith.Nanga
 *
 * 
 */
	@Component("ItcErrorReportDaoImpl")
	public class ItcErrorReportDaoImpl implements ItcReportDao {

		@PersistenceContext(unitName = "clientDataUnit")
		private EntityManager entityManager;
		static String errorType = null;

		private static final Logger LOGGER = LoggerFactory
				.getLogger(ItcErrorReportDaoImpl.class);

		@Override
		public List<Object> getItcReports(
				Gstr1VerticalDownloadReportsReqDto request) {

			Long fileId = request.getFileId();
			errorType = request.getErrorType();

			StringBuilder buildQuery = new StringBuilder();
			StringBuilder buildQuery1 = new StringBuilder();
			if (fileId != null) {
				buildQuery.append(" AND HDR.FILE_ID= :fileId ");
			}
			
			if (errorType.equalsIgnoreCase(DownloadReportsConstant.ACTIVE)) {
				buildQuery1.append(" AND HDR.IS_DELETE = FALSE");
			}
			if (errorType.equalsIgnoreCase(DownloadReportsConstant.INACTIVE)) {
				buildQuery1.append(" AND HDR.IS_DELETE = TRUE");
			}
			String queryStr = createVerticalTotalQueryString(buildQuery.toString(),buildQuery1.toString());
			Query q = entityManager.createNativeQuery(queryStr);

			LOGGER.debug("Reading query " + queryStr);

			if (fileId != null) {
				q.setParameter("fileId", fileId);
			}

			List<Object[]> list = q.getResultList();

			LOGGER.debug("Reading Resultset" + list);
			List<Object> retList = list.parallelStream()
					.map(o -> convertVerticalTotal(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		}

		private ItcTotalRecords convertVerticalTotal(Object[] arr) {
			ItcTotalRecords obj = new ItcTotalRecords();

			String errDesc = null;
			String infoDesc = null;
			
			String errCode = (arr[0] != null) ? arr[0].toString() : null;
			obj.setAspErrorCode(errCode);

			if (!Strings.isNullOrEmpty(errCode)) {
				String[] errorCodes = errCode.split(",");
				List<String> errCodeList = Arrays.asList(errorCodes);
				errDesc = ErrorMasterUtil.getErrorInfo(errCodeList, "ITC04");
			}
			// obj.setAspErrorCode(errCode);
			obj.setAspErrorDescription(errDesc);
			String infoCode = (arr[2] != null) ? arr[2].toString() : null;
			obj.setAspInformationId(infoCode);
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
			obj.setDeliveryChallanDate(arr[10] != null ? arr[10].toString() : null);
			obj.setjWDeliveryChallanNumber(
					arr[11] != null ? arr[11].toString() : null);
			obj.setjWDeliveryChallanDate(
					arr[12] != null ? arr[12].toString() : null);
			obj.setGoodsReceivingDate(arr[13] != null ? arr[13].toString() : null);
			obj.setInvoiceNumber(arr[14] != null ? arr[14].toString() : null);
			obj.setInvoiceDate(arr[15] != null ? arr[15].toString() : null);
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
			obj.setQuantity(arr[28] != null ? arr[28].toString() : null);
			obj.setLossesUQC(arr[29] != null ? arr[29].toString() : null);
			obj.setLossesQuantity(arr[30] != null ? arr[30].toString() : null);
			obj.setItemAssessableAmount(
					arr[31] != null ? arr[31].toString() : null);
			obj.setiGSTRate(arr[32] != null ? arr[32].toString() : null);
			obj.setiGSTAmount(arr[33] != null ? arr[33].toString() : null);
			obj.setcGSTRate(arr[34] != null ? arr[34].toString() : null);
			obj.setcGSTAmount(arr[35] != null ? arr[35].toString() : null);
			obj.setsGSTRate(arr[36] != null ? arr[36].toString() : null);
			obj.setsGSTAmount(arr[37] != null ? arr[37].toString() : null);
			obj.setCessAdvaloremRate(arr[38] != null ? arr[38].toString() : null);
			obj.setCessAdvaloremAmount(arr[39] != null ? arr[39].toString() : null);
			obj.setCessSpecificRate(arr[40] != null ? arr[40].toString() : null);
			obj.setCessSpecificAmount(arr[41] != null ? arr[41].toString() : null);
			obj.setStateCessAdvaloremRate(
					arr[42] != null ? arr[42].toString() : null);
			obj.setStateCessAdvaloremAmount(
					arr[43] != null ? arr[43].toString() : null);
			obj.setStateCessSpecificRate(
					arr[44] != null ? arr[44].toString() : null);
			obj.setStateCessSpecificAmount(
					arr[45] != null ? arr[45].toString() : null);
			obj.setTotalValue(arr[46] != null ? arr[46].toString() : null);
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

		private String createVerticalTotalQueryString(String buildQuery,String buildQuery1) {
			
			String queryStr = "";

			if ((DownloadReportsConstant.ERRORTOTAL).equalsIgnoreCase(errorType)) {
				queryStr = "SELECT TRIM(', ' FROM IFNULL(HDR.ERROR_CODES,'') ||','|| "
						+ "IFNULL(ITM.ERROR_CODES,'')) AS ERROR_CODE_ASP, "
						+ " '' AS ERROR_DESCRIPTION_ASP, TRIM(', ' FROM "
						+ "IFNULL(HDR.INFORMATION_CODES,'') ||','|| "
						+ "IFNULL(ITM.INFORMATION_CODES,'')) AS INFO_ERROR_CODE_ASP, "
						+ " '' AS INFO_ERROR_DESCRIPTION_ASP, TO_CHAR(TABLE_NUMBER),"
						+ "TO_CHAR(ACTION_TYPE ),TO_CHAR(FI_YEAR),TO_CHAR(HDR.RETURN_PERIOD),"
						+ "TO_CHAR(HDR.SUPPLIER_GSTIN), TO_CHAR(DELIVERY_CHALLAN_NO),"
						+ "TO_CHAR(DELIVERY_CHALLAN_DATE),TO_CHAR(JW_DELIVERY_CHALLAN_NO),"
						+ "TO_CHAR(JW_DELIVERY_CHALLAN_DATE), TO_CHAR(GOODS_RECEIVING_DATE),"
						+ "TO_CHAR(INV_NUM ),TO_CHAR(INV_DATE),TO_CHAR(JW_GSTIN ),"
						+ "TO_CHAR(JW_STATE_CODE ),TO_CHAR(JW_WORKER_TYPE),TO_CHAR(JW_ID),"
						+ "TO_CHAR(JW_NAME), TO_CHAR(GOODS_TYPE),TO_CHAR(ITM_SER_NO),"
						+ "TO_CHAR(PRODUCT_DESC),TO_CHAR(PRODUCT_CODE),TO_CHAR(NATURE_OF_JW), "
						+ "TO_CHAR(ITM_HSNSAC ),TO_CHAR(ITM_UQC),TO_CHAR(ITM_QTY ),"
						+ "TO_CHAR(LOSSES_UQC),TO_CHAR(LOSSES_QTY), "
						+ "TO_CHAR(ITM.TAXABLE_VALUE )ITM_ACCESSABLE_AMT ,"
						+ "TO_CHAR(IGST_RATE),TO_CHAR(ITM.IGST_AMT),TO_CHAR(CGST_RATE),"
						+ "TO_CHAR(ITM.CGST_AMT),TO_CHAR(SGST_RATE),TO_CHAR(ITM.SGST_AMT), "
						+ "TO_CHAR(CESS_RATE_ADVALOREM),TO_CHAR(ITM.CESS_AMT_ADVALOREM),"
						+ "TO_CHAR(CESS_RATE_SPECIFIC ),TO_CHAR(ITM.CESS_AMT_SPECIFIC), "
						+ "TO_CHAR(STATE_CESS_RATE_ADVALOREM),"
						+ "TO_CHAR(ITM.STATE_CESS_AMT_ADVALOREM),"
						+ "TO_CHAR(STATE_CESS_RATE_SPECIFIC),"
						+ "TO_CHAR(ITM.STATE_CESS_AMT_SPECIFIC),TO_CHAR(ITM.TOTAL_VALUE), "
						+ "TO_CHAR(POSTING_DATE),TO_CHAR(USER_ID),TO_CHAR(COMPANY_CODE),"
						+ "TO_CHAR(SOURCE_IDENTIFIER), TO_CHAR(SOURCE_FILE_NAME) , "
						+ "TO_CHAR(PLANT_CODE ),TO_CHAR(DIVISION),"
						+ "TO_CHAR(PROFIT_CENTRE1),TO_CHAR(PROFIT_CENTRE2), "
						+ "TO_CHAR(ACCOUNTING_VOUCHER_NUM),TO_CHAR(ACCOUNTING_VOUCHER_DATE), "
						+ "TO_CHAR(USERDEFINED_FIELD1),TO_CHAR(USERDEFINED_FIELD2),"
						+ "TO_CHAR(USERDEFINED_FIELD3),TO_CHAR(USERDEFINED_FIELD4),"
						+ "TO_CHAR(USERDEFINED_FIELD5), TO_CHAR(USERDEFINED_FIELD6),"
						+ "TO_CHAR(USERDEFINED_FIELD7),TO_CHAR(USERDEFINED_FIELD8),"
						+ "TO_CHAR(USERDEFINED_FIELD9),TO_CHAR(USERDEFINED_FIELD10), "
						+ "FIL.CREATED_BY USERID, FIL.ID FILE_ID,FIL.FILE_NAME FILE_NAME,"
						+ "FIL.SOURCE SOURCE, '' AS GSTN_SAVE_STATUS, "
						+ " '' AS GSTIN_REF_ID, '' AS GSTIN_REF_ID_TIME, "
						+ " '' AS GSTIN_ERROR_CODE, '' AS GSTIN_ERROR_DESCRIPTION_ASP "
						+ " FROM ITC04_HEADER HDR INNER JOIN ITC04_ITEM ITM "
						+ "ON HDR.ID=ITM.DOC_HEADER_ID AND "
						+ "HDR.RETURN_PERIOD=ITM.RETURN_PERIOD "
						+ "AND HDR.FILE_ID = ITM.FILE_ID INNER JOIN "
						+ "FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
						+ "WHERE HDR.IS_DELETE=FALSE AND IS_ERROR=TRUE "
						+ buildQuery
						+ " UNION ALL "
						+ "SELECT TRIM(', ' FROM IFNULL(HDR.ERROR_CODES,'') ||','|| "
						+ "IFNULL(ITM.ERROR_CODES,'')) AS ERROR_CODE_ASP, "
						+ " '' AS ERROR_DESCRIPTION_ASP, "
						+ "TRIM(', ' FROM IFNULL(HDR.INFORMATION_CODES,'') ||','|| "
						+ "IFNULL(ITM.INFORMATION_CODES,'')) AS INFO_ERROR_CODE_ASP, "
						+ " '' AS INFO_ERROR_DESCRIPTION_ASP, "
						+ "ITM.TABLE_NUMBER ,ITM.ACTION_TYPE ,ITM.FI_YEAR,"
						+ "ITM.RETURN_PERIOD,ITM.SUPPLIER_GSTIN, "
						+ "ITM.DELIVERY_CHALLAN_NO,ITM.DELIVERY_CHALLAN_DATE,"
						+ "ITM.JW_DELIVERY_CHALLAN_NO,ITM.JW_DELIVERY_CHALLAN_DATE, "
						+ "ITM.GOODS_RECEIVING_DATE,ITM.INV_NUM ,ITM.INV_DATE,"
						+ "ITM.JW_GSTIN ,ITM.JW_STATE_CODE ,ITM.JW_WORKER_TYPE,"
						+ "ITM.JW_ID,ITM.JW_NAME, ITM.GOODS_TYPE,ITM.ITM_SER_NO,"
						+ "ITM.PRODUCT_DESC,ITM.PRODUCT_CODE,ITM.NATURE_OF_JW, "
						+ "ITM_HSNSAC , ITM_UQC, ITM_QTY , LOSSES_UQC, LOSSES_QTY, "
						+ "ITM.TAXABLE_VALUE ITM_ACCESSABLE_AMT , IGST_RATE, "
						+ "ITM.IGST_AMT,CGST_RATE, ITM.CGST_AMT,SGST_RATE, "
						+ "ITM.SGST_AMT, CESS_RATE_ADVALOREM, "
						+ "ITM.CESS_AMT_ADVALOREM,CESS_RATE_SPECIFIC ,"
						+ "ITM.CESS_AMT_SPECIFIC, "
						+ "STATE_CESS_RATE_ADVALOREM,ITM.STATE_CESS_AMT_ADVALOREM,"
						+ "STATE_CESS_RATE_SPECIFIC,ITM.STATE_CESS_AMT_SPECIFIC,"
						+ "ITM.TOTAL_VALUE, ITM.POSTING_DATE,ITM.USER_ID,"
						+ "ITM.COMPANY_CODE,ITM.SOURCE_IDENTIFIER, "
						+ "ITM.SOURCE_FILE_NAME , ITM.PLANT_CODE ,"
						+ "ITM.DIVISION,ITM.PROFIT_CENTRE1,ITM.PROFIT_CENTRE2, "
						+ "ITM.ACCOUNTING_VOUCHER_NUM,ITM.ACCOUNTING_VOUCHER_DATE, "
						+ "USERDEFINED_FIELD1,USERDEFINED_FIELD2,USERDEFINED_FIELD3,"
						+ "USERDEFINED_FIELD4,USERDEFINED_FIELD5, USERDEFINED_FIELD6,"
						+ "USERDEFINED_FIELD7,USERDEFINED_FIELD8,USERDEFINED_FIELD9,"
						+ "USERDEFINED_FIELD10, FIL.CREATED_BY USERID, "
						+ "FIL.ID FILE_ID,FIL.FILE_NAME FILE_NAME,FIL.SOURCE SOURCE, "
						+ " '' AS GSTN_SAVE_STATUS, '' AS GSTIN_REF_ID, "
						+ " '' AS GSTIN_REF_ID_TIME, '' AS GSTIN_ERROR_CODE, "
						+ " '' AS GSTIN_ERROR_DESCRIPTION_ASP "
						+ "FROM ITC04_ERR_HEADER HDR INNER JOIN ITC04_ERR_ITEM "
						+ "ITM ON HDR.ID=ITM.DOC_HEADER_ID "
						+ "AND HDR.FILE_ID = ITM.FILE_ID INNER JOIN FILE_STATUS FIL "
						+ "ON HDR.FILE_ID=FIL.ID WHERE HDR.IS_ERROR='true' "
						+ buildQuery;
						
			} else if ((DownloadReportsConstant.ACTIVE)
					.equalsIgnoreCase(errorType)) {
					
						queryStr ="SELECT TRIM(', ' FROM IFNULL(HDR.ERROR_CODES,'') ||','|| "
								+ "IFNULL(ITM.ERROR_CODES,'')) AS ERROR_CODE_ASP, "
								+ " '' AS ERROR_DESCRIPTION_ASP, TRIM(', ' FROM "
								+ "IFNULL(HDR.INFORMATION_CODES,'') ||','|| "
								+ "IFNULL(ITM.INFORMATION_CODES,'')) AS INFO_ERROR_CODE_ASP, "
								+ " '' AS INFO_ERROR_DESCRIPTION_ASP, TABLE_NUMBER ,"
								+ "ACTION_TYPE ,FI_YEAR,HDR.RETURN_PERIOD,HDR.SUPPLIER_GSTIN, "
								+ "DELIVERY_CHALLAN_NO,DELIVERY_CHALLAN_DATE,JW_DELIVERY_CHALLAN_NO,"
								+ "JW_DELIVERY_CHALLAN_DATE, GOODS_RECEIVING_DATE,INV_NUM ,"
								+ "INV_DATE,JW_GSTIN ,JW_STATE_CODE ,JW_WORKER_TYPE,JW_ID,JW_NAME, "
								+ "GOODS_TYPE,ITM_SER_NO,PRODUCT_DESC,PRODUCT_CODE,NATURE_OF_JW, "
								+ "ITM_HSNSAC ,ITM_UQC,ITM_QTY ,LOSSES_UQC,LOSSES_QTY, "
								+ "ITM.TAXABLE_VALUE ITM_ACCESSABLE_AMT ,IGST_RATE,ITM.IGST_AMT,"
								+ "CGST_RATE,ITM.CGST_AMT,SGST_RATE,ITM.SGST_AMT, CESS_RATE_ADVALOREM,"
								+ "ITM.CESS_AMT_ADVALOREM,CESS_RATE_SPECIFIC ,ITM.CESS_AMT_SPECIFIC, "
								+ "STATE_CESS_RATE_ADVALOREM,ITM.STATE_CESS_AMT_ADVALOREM,"
								+ "STATE_CESS_RATE_SPECIFIC,ITM.STATE_CESS_AMT_SPECIFIC,"
								+ "ITM.TOTAL_VALUE, POSTING_DATE,USER_ID,COMPANY_CODE,"
								+ "SOURCE_IDENTIFIER, SOURCE_FILE_NAME , PLANT_CODE ,DIVISION,"
								+ "PROFIT_CENTRE1,PROFIT_CENTRE2, ACCOUNTING_VOUCHER_NUM,"
								+ "ACCOUNTING_VOUCHER_DATE, USERDEFINED_FIELD1,USERDEFINED_FIELD2,"
								+ "USERDEFINED_FIELD3,USERDEFINED_FIELD4,USERDEFINED_FIELD5, "
								+ "USERDEFINED_FIELD6,USERDEFINED_FIELD7,USERDEFINED_FIELD8,"
								+ "USERDEFINED_FIELD9,USERDEFINED_FIELD10, FIL.CREATED_BY USERID, "
								+ "FIL.ID FILE_ID,FIL.FILE_NAME FILE_NAME,FIL.SOURCE SOURCE, "
								+ "(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
								+ "WHEN IS_SAVED_TO_GSTN = FALSE AND GSTN_ERROR = TRUE "
								+ "THEN 'GSTN_ERROR' WHEN IS_SAVED_TO_GSTN = FALSE "
								+ "AND GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) AS GSTN_SAVE_STATUS, "
								+ " '' AS GSTIN_REF_ID, '' AS GSTIN_REF_ID_TIME, "
								+ " '' AS GSTIN_ERROR_CODE, '' AS GSTIN_ERROR_DESCRIPTION_ASP "
								+ "FROM ITC04_HEADER HDR INNER JOIN ITC04_ITEM ITM "
								+ "ON HDR.ID=ITM.DOC_HEADER_ID "
								+ "AND HDR.RETURN_PERIOD=ITM.RETURN_PERIOD "
								+ "AND HDR.FILE_ID = ITM.FILE_ID LEFT OUTER JOIN "
								+ "FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
								+ " WHERE HDR.IS_DELETE=FALSE AND IS_ERROR=TRUE " 
								+ buildQuery;
						
						
						
						} else if ((DownloadReportsConstant.INACTIVE)
								.equalsIgnoreCase(errorType)) {
						       
							queryStr ="SELECT TRIM(', ' FROM IFNULL(HDR.ERROR_CODES,'') ||','|| "
									+ "IFNULL(ITM.ERROR_CODES,'')) AS ERROR_CODE_ASP, "
									+ " '' AS ERROR_DESCRIPTION_ASP, TRIM(', ' FROM "
									+ "IFNULL(HDR.INFORMATION_CODES,'') ||','|| "
									+ "IFNULL(ITM.INFORMATION_CODES,'')) AS INFO_ERROR_CODE_ASP, "
									+ " '' AS INFO_ERROR_DESCRIPTION_ASP, TABLE_NUMBER ,"
									+ "ACTION_TYPE ,FI_YEAR,HDR.RETURN_PERIOD,HDR.SUPPLIER_GSTIN, "
									+ "DELIVERY_CHALLAN_NO,DELIVERY_CHALLAN_DATE,JW_DELIVERY_CHALLAN_NO,"
									+ "JW_DELIVERY_CHALLAN_DATE, GOODS_RECEIVING_DATE,INV_NUM ,"
									+ "INV_DATE,JW_GSTIN ,JW_STATE_CODE ,JW_WORKER_TYPE,JW_ID,JW_NAME, "
									+ "GOODS_TYPE,ITM_SER_NO,PRODUCT_DESC,PRODUCT_CODE,NATURE_OF_JW, "
									+ "ITM_HSNSAC ,ITM_UQC,ITM_QTY ,LOSSES_UQC,LOSSES_QTY, "
									+ "ITM.TAXABLE_VALUE ITM_ACCESSABLE_AMT ,IGST_RATE,ITM.IGST_AMT,"
									+ "CGST_RATE,ITM.CGST_AMT,SGST_RATE,ITM.SGST_AMT, CESS_RATE_ADVALOREM,"
									+ "ITM.CESS_AMT_ADVALOREM,CESS_RATE_SPECIFIC ,ITM.CESS_AMT_SPECIFIC, "
									+ "STATE_CESS_RATE_ADVALOREM,ITM.STATE_CESS_AMT_ADVALOREM,"
									+ "STATE_CESS_RATE_SPECIFIC,ITM.STATE_CESS_AMT_SPECIFIC,"
									+ "ITM.TOTAL_VALUE, POSTING_DATE,USER_ID,COMPANY_CODE,"
									+ "SOURCE_IDENTIFIER, SOURCE_FILE_NAME , PLANT_CODE ,DIVISION,"
									+ "PROFIT_CENTRE1,PROFIT_CENTRE2, ACCOUNTING_VOUCHER_NUM,"
									+ "ACCOUNTING_VOUCHER_DATE, USERDEFINED_FIELD1,USERDEFINED_FIELD2,"
									+ "USERDEFINED_FIELD3,USERDEFINED_FIELD4,USERDEFINED_FIELD5, "
									+ "USERDEFINED_FIELD6,USERDEFINED_FIELD7,USERDEFINED_FIELD8,"
									+ "USERDEFINED_FIELD9,USERDEFINED_FIELD10, FIL.CREATED_BY USERID, "
									+ "FIL.ID FILE_ID,FIL.FILE_NAME FILE_NAME,FIL.SOURCE SOURCE, "
									+ "(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
									+ "WHEN IS_SAVED_TO_GSTN = FALSE AND GSTN_ERROR = TRUE "
									+ "THEN 'GSTN_ERROR' WHEN IS_SAVED_TO_GSTN = FALSE "
									+ "AND GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) AS GSTN_SAVE_STATUS, "
									+ " '' AS GSTIN_REF_ID, '' AS GSTIN_REF_ID_TIME, "
									+ " '' AS GSTIN_ERROR_CODE, '' AS GSTIN_ERROR_DESCRIPTION_ASP "
									+ "FROM ITC04_HEADER HDR INNER JOIN ITC04_ITEM ITM "
									+ "ON HDR.ID=ITM.DOC_HEADER_ID "
									+ "AND HDR.RETURN_PERIOD=ITM.RETURN_PERIOD "
									+ "AND HDR.FILE_ID = ITM.FILE_ID LEFT OUTER JOIN "
									+ "FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
									+ " WHERE HDR.IS_DELETE=TRUE AND IS_ERROR=TRUE " 
									+ buildQuery;
							
			}
			 else if ((DownloadReportsConstant.ERRORSV)
						.equalsIgnoreCase(errorType)) {

					queryStr ="SELECT TRIM(', ' FROM IFNULL(HDR.ERROR_CODES,'') ||','|| "
							+ "IFNULL(ITM.ERROR_CODES,'')) AS ERROR_CODE_ASP, "
							+ " '' AS ERROR_DESCRIPTION_ASP, TRIM(', ' FROM "
							+ "IFNULL(HDR.INFORMATION_CODES,'') ||','|| "
							+ "IFNULL(ITM.INFORMATION_CODES,'')) AS INFO_ERROR_CODE_ASP, "
							+ " '' AS INFO_ERROR_DESCRIPTION_ASP, "
							+ "ITM.TABLE_NUMBER ,ITM.ACTION_TYPE ,ITM.FI_YEAR,"
							+ "ITM.RETURN_PERIOD,ITM.SUPPLIER_GSTIN, ITM.DELIVERY_CHALLAN_NO,"
							+ "ITM.DELIVERY_CHALLAN_DATE,ITM.JW_DELIVERY_CHALLAN_NO,"
							+ "ITM.JW_DELIVERY_CHALLAN_DATE, ITM.GOODS_RECEIVING_DATE,"
							+ "ITM.INV_NUM ,ITM.INV_DATE,ITM.JW_GSTIN ,ITM.JW_STATE_CODE ,"
							+ "ITM.JW_WORKER_TYPE,ITM.JW_ID,ITM.JW_NAME, ITM.GOODS_TYPE,"
							+ "ITM.ITM_SER_NO,ITM.PRODUCT_DESC,ITM.PRODUCT_CODE,ITM.NATURE_OF_JW, "
							+ "ITM_HSNSAC , ITM_UQC, ITM_QTY , LOSSES_UQC, LOSSES_QTY, "
							+ "ITM.TAXABLE_VALUE ITM_ACCESSABLE_AMT , IGST_RATE, "
							+ "ITM.IGST_AMT,CGST_RATE, ITM.CGST_AMT,SGST_RATE, "
							+ "ITM.SGST_AMT, CESS_RATE_ADVALOREM, "
							+ "ITM.CESS_AMT_ADVALOREM,CESS_RATE_SPECIFIC ,"
							+ "ITM.CESS_AMT_SPECIFIC, STATE_CESS_RATE_ADVALOREM,"
							+ "ITM.STATE_CESS_AMT_ADVALOREM,STATE_CESS_RATE_SPECIFIC,"
							+ "ITM.STATE_CESS_AMT_SPECIFIC,ITM.TOTAL_VALUE, "
							+ "ITM.POSTING_DATE,ITM.USER_ID,ITM.COMPANY_CODE,"
							+ "ITM.SOURCE_IDENTIFIER, ITM.SOURCE_FILE_NAME , "
							+ "ITM.PLANT_CODE ,ITM.DIVISION,ITM.PROFIT_CENTRE1,"
							+ "ITM.PROFIT_CENTRE2, "
							+ "ITM.ACCOUNTING_VOUCHER_NUM,ITM.ACCOUNTING_VOUCHER_DATE, "
							+ "USERDEFINED_FIELD1,USERDEFINED_FIELD2,USERDEFINED_FIELD3,"
							+ "USERDEFINED_FIELD4,USERDEFINED_FIELD5, USERDEFINED_FIELD6,"
							+ "USERDEFINED_FIELD7,USERDEFINED_FIELD8,USERDEFINED_FIELD9,"
							+ "USERDEFINED_FIELD10, FIL.CREATED_BY USERID, FIL.ID FILE_ID,"
							+ "FIL.FILE_NAME FILE_NAME,FIL.SOURCE SOURCE, "
							+ " '' AS GSTN_SAVE_STATUS, '' AS GSTIN_REF_ID, "
							+ " '' AS GSTIN_REF_ID_TIME, '' AS GSTIN_ERROR_CODE, "
							+ " '' AS GSTIN_ERROR_DESCRIPTION_ASP FROM "
							+ "ITC04_ERR_HEADER HDR INNER JOIN ITC04_ERR_ITEM ITM "
							+ "ON HDR.ID=ITM.DOC_HEADER_ID "
							+ "AND HDR.FILE_ID = ITM.FILE_ID "
							+ "INNER JOIN FILE_STATUS FIL ON "
							+ "HDR.FILE_ID=FIL.ID WHERE HDR.IS_ERROR='true' "
							+buildQuery;
			 }
					return queryStr;
				}
			}