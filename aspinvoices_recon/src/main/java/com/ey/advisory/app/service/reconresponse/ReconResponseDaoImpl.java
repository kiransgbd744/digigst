/**
 * 
 */
package com.ey.advisory.app.service.reconresponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("ReconResponseDaoImpl")
@Slf4j
public class ReconResponseDaoImpl implements ReconResponseDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<ErrorResponseDto> getErrorRecords(Long fileId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begin ReconResponseDaoImpl.getErrorRecords configId ="
					+ fileId);
		}
		String queryStr = " SELECT DISTINCT A.* "
				+" ,E.ERROR_DESCRIPTION "
				+" ,E.USER_RESPONSE as USER_ERROR_RESPONSE  "
				+" ,E.RECON_REPORT_ID "
				+" FROM  "
				+"  (  "
				+"               SELECT '' AS A2_ITC_ENTITLEMENT "
				+"               ,HD.ITC_ENTITLEMENT AS PR_ITC_ENTITLEMENT "
				+"               ,(CASE WHEN A2_INVOICE_KEY IS NULL THEN NULL ELSE LT.PRE_REPORT_A2 END) PREVIOUSE_REPORT_A2 "
				+"               ,(CASE WHEN PR_INVOICE_KEY IS NULL THEN NULL ELSE LT.PRE_REPORT_PR END) PREVIOUSE_REPORT_PR "
				+"               ,LT.USER_RESPONSE "
				+"               ,LT.SUGGESTED_RESPONSE "
				+"               ,LT.CURRENT_MISMATCH_REASON "
				+"               ,LT.FORCED_MATCH_RESPONSE "
				+"               ,'' AS INFORMATION_REPORT_REFERENCE "
				+"               ,LT.ADDITION_A2_PR_RESPONSE_TAX_PERIOD "
				+"               ,(CASE WHEN A2_INVOICE_KEY IS NULL THEN NULL ELSE LT.PRE_USER_RESPONSE_A2 END) PREVIOUSE_RESPONSE_A2 "
				+"               ,(CASE WHEN PR_INVOICE_KEY IS NULL THEN NULL ELSE LT.PRE_USER_RESPONSE_PR END) PREVIOUSE_RESPONSE_PR "
				+"               ,LT.CURRENT_REPORT "
				+"               ,LT.PR_DOC_NUM "
				+"               ,LT.A2_DOC_NUM "
				+"               ,LT.A2_RECIPIENT_GSTIN "
				+"               ,LT.PR_RECIPIENT_GSTIN  "
				+"               ,LT.A2_DOC_TYPE  "
				+"               ,LT.PR_DOC_TYPE "
				+"               ,LT.A2_DOC_DATE  "
				+"               ,LT.PR_DOC_DATE  "
				+"               ,LT.A2_TAXABLE_VALUE "
				+"               ,LT.PR_TAXABLE_VALUE "
				+"               ,LT.A2_CGST  "
				+"               ,LT.PR_CGST  "
				+"               ,LT.A2_SGST  "
				+"               ,LT.PR_SGST  "
				+"               ,LT.A2_IGST  "
				+"               ,LT.PR_IGST  "
				+"               ,LT.A2_CESS  "
				+"               ,LT.PR_CESS "
				+"               ,LT.A2_POS  "
				+"               ,LT.PR_POS "
				+"               ,'' AS A2_SAVED_RESPONSE_REPORTTYPE "
				+"               ,'' AS A2_DIGIGST_SAVEDRESPONSE "
				+"               ,LT.A2_GSTN_SAVED_RESPONSE AS GSTN_SAVED_RESPONSE "
				+"               ,LT.MATCHING_SCORE AS MATCHING_SCORE "
				+"               ,LT.TAX_PERIOD AS RECON_TAXPERIOD "
				+"               , '' AS CALENDAR_MONTH_PR "
				+"               ,'' AS DATE_UPLOAD "
				+"               , '' AS ORG_MONTH_A2 "
				+"               , '' AS ORG_MONTH_PR "
				+"               ,'' AS A2_SUPPLIER_NAME "
				+"               ,HD.CUST_SUPP_NAME AS PR_SUPPLIER_NAME "
				+"               , '' AS A2_TOTAL_TAX "
				+"               , '' AS PR_TOTAL_TAX "
				+"               ,A2.DOC_AMT AS A2_INVOICE_VALE "
				+"               ,HD.DOC_AMT AS PR_INVOICE_VALE "
				+"               ,HD.AVAILABLE_IGST AS A_IGST "
				+"               ,HD.AVAILABLE_CGST AS A_CGST "
				+"               , HD.AVAILABLE_SGST AS A_SGST "
				+"               ,HD.AVAILABLE_CESS AS A_CESS "
				+"               , '' AS A2_TABLE_TYPE "
				+"               ,HD.AN_TAX_DOC_TYPE AS PR_TABLE_TYPE "
				+"               , '' AS CFS_FLAG "
				+"               ,'' AS A2_CLAIM_REFUND "
				+"               , '' AS PR_CLAIM_REFUND "
				+"               ,'' AS A2_SECTION7_IGST "
				+"               , HD.SECTION7_OF_IGST_FLAG AS PR_SECTION7_IGST "
				+"               , '' AS A2_AUTOPOPULATED_REFUND "
				+"               ,HD.AUTOPOPULATE_TO_REFUND AS PR_AUTOPOPULATED_REFUND "
				+"               ,'' AS A2_DIFF_PERCENT "
				+"               , HD.DIFF_PERCENT AS PR_DIFF_PERCENT "
				+"               ,'' AS A2_ORIG_DOC_TYPE "
				+"               ,HD.ORIGINAL_DOC_TYPE AS PR_ORIG_DOC_TYPE "
				+"               , ''AS A2_ORIG_DOC_NUM "
				+"               ,HD.ORIGINAL_DOC_NUM AS PR_ORIG_DOC_NUM "
				+"               ,'' AS A2_ORIG_DOC_DATE "
				+"               ,HD.ORIGINAL_DOC_DATE AS PR_ORIG_DOC_DATE "
				+"               ,LT.PR_SUPPLIER_GSTIN "
				+"               ,HD.USER_ID AS USER_ID "
				+"               ,HD.SOURCE_FILENAME AS SOURCE_FLIENAME "
				+"               ,HD.PROFIT_CENTRE AS PROFIT_CENTRE "
				+"               ,HD.PLANT_CODE AS PLANT "
				+"               ,HD.DIVISION AS DIVISION "
				+"               ,HD.LOCATION AS LOCATION "
				+"               ,HD.PURCHASE_ORGANIZATION AS PURCHASE_ORGANISATION "
				+"               ,HD.USERACCESS1 AS USERACCESS1 "
				+"               ,HD.USERACCESS2 AS USERACCESS2 "
				+"               ,HD.USERACCESS3 AS USERACCESS3 "
				+"               ,HD.USERACCESS4 AS USERACCESS4 "
				+"               ,HD.USERACCESS5 AS USERACCESS5, HD.USERACCESS6  "
				+"               AS USERACCESS6, '' AS GLCODE_TAXABLE_VALUE, ''  "
				+"               AS GL_CODE_IGST, '' AS GL_CODE_CGST, '' AS GL_CODE_SGST, '' "
				+"               AS GL_CODE_ADVALOREM_CESS, '' AS GL_CODE_SPECIFIC_CESS,''  "
				+"               AS GL_CODESTATE_CESS, HD.SUPPLY_TYPE AS SUPPLY_TYPE,  "
				+"               HD.CRDR_PRE_GST AS CRDRPERGST, HD.CUST_SUPP_TYPE AS  "
				+"               SUPPLIER_TYPE, HD.CUST_SUPP_CODE AS SUPPLIER_CODE,  "
				+"               HD.CUST_SUPP_ADDRESS1 AS SUPPLIER_ADDERSS1,  "
				+"               HD.CUST_SUPP_ADDRESS2 AS SUPPLIER_ADDERSS2,  "
				+"               HD.CUST_SUPP_ADDRESS3 AS SUPPLIER_ADDERSS3,  "
				+"               HD.CUST_SUPP_ADDRESS4 AS SUPPLIER_ADDERSS4, "
				+"               HD.STATE_APPLYING_CESS AS STATE_APPLYING_CESS,  "
				+"               HD.SHIP_PORT_CODE AS PORT_CODE "
				+"               , HD.BILL_OF_ENTRY AS  "
				+"               BILL_OF_ENTRY, HD.BILL_OF_ENTRY_DATE AS BILL_OF_ENTRY_DATE, "
				+"               '' AS CIF_VALUE, '' AS CUSTOM_DUTY, '' AS QUANTITY,  "
				+"               '' AS CESS_AMT_ADVALOREM, '' AS CESS_AMT_SPECIFIC, '' AS "
				+"               STATE_CESS_AMOUNT, '' AS OTHER_VALUE, '' AS  "
				+"               PURCHASE_VOUCHER_DATE, '' AS PURCHASE_VOUCHER_NUM, '' AS "
				+"               POSTING_DATE, '' AS PAYMENT_VOUCHER_NUM, '' AS PAYMENT_DATE, "
				+"               '' AS CONTRACT_NUM, '' AS CONTRACT_VAL, HD.EWAY_BILL_NUM AS "
				+"               E_WAYBILLNUMBER, HD.EWAY_BILL_DATE AS E_WAYBILLDATE, '' AS  "
				+"               USERDEFINED_FIELD1, '' AS USERDEFINED_FIELD2, '' AS  "
				+"               USERDEFINED_FIELD3, '' AS USERDEFINED_FIELD4, '' AS  "
				+"               USERDEFINED_FIELD5, '' AS USERDEFINED_FIELD6, '' AS  "
				+"               USERDEFINED_FIELD7, '' AS USERDEFINED_FIELD8, ''  "
				+"               AS USERDEFINED_FIELD9, '' AS USERDEFINED_FIELD10, ''  "
				+"               AS USERDEFINED_FIELD11, '' AS USERDEFINED_FIELD12, '' AS  "
				+"               USERDEFINED_FIELD13, '' AS USERDEFINED_FIELD14, '' AS  "
				+"               USERDEFINED_FIELD15, '' AS MATCHING_ID "
				+"               , HD.ID AS PR_ID "
				+"               ,A2.ID AS A2_ID "
				+"               , LT.PR_INVOICE_KEY "
				+"               , LT.A2_INVOICE_KEY "
				+"               ,LT.A2_SUPPLIER_GSTIN  "
				+"               FROM  "
				+"                      (  "
				+"                      SELECT IC.* "
				+"                       "
				+"                      FROM "
				+"                      ( "
				+"                      SELECT PR_DOC_NUM "
				+"                      , A2_DOC_NUM "
				+"                      ,A2_RECIPIENT_GSTIN "
				+"                      , PR_RECIPIENT_GSTIN  "
				+"                      , PR_SUPPLIER_GSTIN "
				+"                      ,A2_SUPPLIER_GSTIN "
				+"                      , BUCKET_TYPE AS CURRENT_REPORT "
				+"                      ,MATCHING_SCORE "
				+"                      , A2_DOC_TYPE  "
				+"                      , A2_GSTN_SAVED_RESPONSE "
				+"                      ,PR_DOC_TYPE "
				+"                      , A2_DOC_DATE  "
				+"                      , PR_DOC_DATE  "
				+"                      , A2_TAXABLE_VALUE "
				+"                      ,PR_TAXABLE_VALUE  "
				+"                      , A2_CGST  "
				+"                      , PR_CGST  "
				+"                      , A2_SGST  "
				+"                      , PR_SGST  "
				+"                      ,A2_IGST  "
				+"                      , PR_IGST  "
				+"                      , A2_CESS  "
				+"                      , PR_CESS  "
				+"                      , A2_POS  "
				+"                      , PR_POS  "
				+"                      ,TAX_PERIOD "
				+"                      , A2_INVOICE_KEY "
				+"                      , PR_INVOICE_KEY "
				+"                      , USER_RESPONSE "
				+"                      ,SUGGESTED_RESPONSE "
				+"                      , CURRENT_MISMATCH_REASON "
				+"                      ,FORCED_MATCH_RESPONSE "
				+"                      ,RECON_REPORT_CONFIG_ID "
				+"                      ,ADDITION_A2_PR_RESPONSE_TAX_PERIOD "
				+"                       ,BUCKET_TYPE "
				+"                      ,LAG(BUCKET_TYPE) OVER(PARTITION BY A2_INVOICE_KEY ORDER BY RECON_REPORT_CONFIG_ID ) AS PRE_REPORT_A2 "
				+"                      ,LAG(BUCKET_TYPE)OVER(PARTITION BY PR_INVOICE_KEY ORDER BY RECON_REPORT_CONFIG_ID ) AS PRE_REPORT_PR "
				+"                      ,LAG(USER_RESPONSE) OVER(PARTITION BY A2_INVOICE_KEY ORDER BY RECON_REPORT_CONFIG_ID ) AS PRE_USER_RESPONSE_A2 "
				+"                      ,LAG(USER_RESPONSE) OVER(PARTITION BY PR_INVOICE_KEY ORDER BY RECON_REPORT_CONFIG_ID ) AS PRE_USER_RESPONSE_PR  "
				+"                      ,RANK() OVER ( PARTITION BY IFNULL(A2_INVOICE_KEY,'A'),IFNULL(PR_INVOICE_KEY,'A') ORDER BY RECON_REPORT_CONFIG_ID DESC ) AS " +" Rank "
				+"                      FROM "
				+"                      LINK_A2_PR  "
				+"                      WHERE  ( A2_RECIPIENT_GSTIN IN(SELECT DISTINCT GSTIN  "
				+"                                                                       FROM RECON_REPORT_GSTIN_DETAILS  "
				+"                                                                       WHERE IS_ACTIVE = TRUE  "
				+"                                                                       )  "
				+"                             OR PR_RECIPIENT_GSTIN IN( SELECT DISTINCT GSTIN  "
				+"                                                                       FROM RECON_REPORT_GSTIN_DETAILS  "
				+"                                                                       WHERE IS_ACTIVE = TRUE  "
				+"                                                                      )  "
				+"                                   )  "
				+"                       "
				+"                      )IC "
				+" 					WHERE IC.RANK=1 "
				+"                      )LT  "
				+"               FULL OUTER JOIN  "
				+"               ( "
				+"                      SELECT ITC_ENTITLEMENT "
				+"                      ,CUST_SUPP_NAME,DOC_AMT,AVAILABLE_IGST,CUST_GSTIN,  "
				+"                      AVAILABLE_CGST,AVAILABLE_SGST,AVAILABLE_CESS,AN_TAX_DOC_TYPE, "
				+"                      SECTION7_OF_IGST_FLAG,AUTOPOPULATE_TO_REFUND,DIFF_PERCENT,  "
				+"                      ORIGINAL_DOC_TYPE,ORIGINAL_DOC_NUM,ORIGINAL_DOC_DATE,  "
				+"                      USER_ID,SOURCE_FILENAME,PROFIT_CENTRE, PLANT_CODE,DIVISION, "
				+"                      LOCATION,PURCHASE_ORGANIZATION, USERACCESS1,USERACCESS2, "
				+"                      USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6, SUPPLY_TYPE, "
				+"                      CRDR_PRE_GST,CUST_SUPP_TYPE,CUST_SUPP_CODE,  "
				+"                      CUST_SUPP_ADDRESS1,CUST_SUPP_ADDRESS2,CUST_SUPP_ADDRESS3,  "
				+"                      CUST_SUPP_ADDRESS4,STATE_APPLYING_CESS,SHIP_PORT_CODE,ID,  "
				+"                      BILL_OF_ENTRY,BILL_OF_ENTRY_DATE,EWAY_BILL_NUM, "
				+"                      EWAY_BILL_DATE,DOC_DATE,SUPPLIER_GSTIN,DOC_TYPE,DOC_NUM  "
				+"                      FROM ANX_INWARD_DOC_HEADER  "
				+"                      WHERE IS_PROCESSED = TRUE "
				+"                     AND IS_DELETE = FALSE  "
				+"                      AND AN_RETURN_TYPE = 'ANX2'  "
				+"                      AND AN_TAX_DOC_TYPE IN ('B2B','DXP','SEZWP','SEZWOP')  "
				+"                      ) HD  "
				+"               ON     LT.PR_DOC_DATE= HD.DOC_DATE  "
				+"               AND LT.PR_SUPPLIER_GSTIN= HD.SUPPLIER_GSTIN  "
				+"               AND LT.PR_RECIPIENT_GSTIN = HD.CUST_GSTIN "
				+"               AND LT.PR_DOC_TYPE= HD.DOC_TYPE  "
				+"               AND LT.PR_DOC_NUM= HD.DOC_NUM "
				+"               LEFT OUTER JOIN  "
				+"               ( SELECT ID "
				+"               , ITCEntitlement  "
				+"               , DOC_AMT "
				+"               , CFS "
				+"               ,SUPPLIER_INV_NUM "
				+"               ,SUPPLIER_INV_DATE "
				+"               ,INV_TYPE "
				+"               ,SGSTIN "
				+"               ,CGSTIN "
				+"               ,Section7_IGST "
				+"               ,DIFF_PERCENT "
				+"               ,'B2B' AS TABLE_TYPE  "
				+"               FROM  "
				+"               (SELECT ID "
				+"               ,ITC_ENT AS ITCEntitlement  "
				+"               ,SUPPLIER_INV_VAL as DOC_AMT "
				+"               ,CFS AS CFS "
				+"               ,SUPPLIER_INV_NUM "
				+"               ,SUPPLIER_INV_DATE "
				+"               ,INV_TYPE "
				+"               ,SGSTIN "
				+"               ,CGSTIN "
				+"               ,SEC_7_ACT AS Section7_IGST "
				+"               ,DIFF_PERCENT AS DIFF_PERCENT "
				+"               ,'B2B'AS TABLE_TYPE  "
				+"               FROM GETANX2_B2B_HEADER  "
				+"               WHERE IS_DELETE = FALSE  "
				+"               )  "
				+"               UNION ALL  "
				+"               (SELECT ID "
				+"               ,ITC_ENT AS ITCEntitlement  "
				+"               , SUPPLIER_INV_VAL as DOC_AMT "
				+"               ,CFS AS CFS "
				+"               ,SUPPLIER_INV_NUM "
				+"               ,SUPPLIER_INV_DATE "
				+"               ,INV_TYPE "
				+"               ,SGSTIN "
				+"               ,CGSTIN "
				+"               ,SEC_7_ACT AS Section7_IGST "
				+"               ,DIFF_PERCENT AS DIFF_PERCENT "
				+"               ,'DE'AS TABLE_TYPE  "
				+"               FROM GETANX2_DE_HEADER  "
				+"               WHERE IS_DELETE = FALSE )  "
				+"               UNION ALL  "
				+"               ( SELECT ID "
				+"               ,ITC_ENT AS ITCEntitlement  "
				+"               , SUPPLIER_INV_VAL as DOC_AMT  "
				+"               ,CFS AS CFS "
				+"               ,SUPPLIER_INV_NUM "
				+"               ,SUPPLIER_INV_DATE "
				+"               ,INV_TYPE "
				+"               ,SGSTIN "
				+"               ,CGSTIN "
				+"               ,'' AS Section7_IGST "
				+"               ,DIFF_PERCENT AS DIFF_PERCENT "
				+"               ,'SEZWP'AS TABLE_TYPE  "
				+"               FROM GETANX2_SEZWP_HEADER  "
				+"               WHERE IS_DELETE = FALSE  "
				+"               )  "
				+"               UNION ALL  "
				+"               ( SELECT ID "
				+"               ,ITC_ENT AS ITCEntitlement  "
				+"               ,SUPPLIER_INV_VAL as DOC_AMT "
				+"               ,CFS AS CFS "
				+"               , SUPPLIER_INV_NUM "
				+"               ,SUPPLIER_INV_DATE "
				+"               ,INV_TYPE "
				+"               ,SGSTIN "
				+"               ,CGSTIN "
				+"               , '' AS Section7_IGST "
				+"               ,0 AS DIFF_PERCENT "
				+"               ,'SEZWP' AS TABLE_TYPE  "
				+"               FROM GETANX2_SEZWOP_HEADER  "
				+"               WHERE IS_DELETE = FALSE )  "
				+"               ) "
				+"               A2  "
				+"               ON LT.A2_DOC_DATE = A2.SUPPLIER_INV_DATE  "
				+"               AND IFNULL(LT.A2_SUPPLIER_GSTIN,'A') = IFNULL(A2.SGSTIN,'A')  "
				+"               AND IFNULL(LT.A2_RECIPIENT_GSTIN,'A') = IFNULL(A2.CGSTIN,'A')  "
				+"               AND LT.A2_DOC_TYPE= A2.INV_TYPE  "
				+"               AND LT.A2_DOC_NUM = A2.SUPPLIER_INV_NUM  "
				+"  )A  "
				+"  INNER JOIN ANX2_USER_RESPONSE_STATUS E "
				+" ON IFNULL(A.A2_INVOICE_KEY,'A') = IFNULL(E.A2_INVOICE_KEY,'A')  "
				+"  AND IFNULL(A.PR_INVOICE_KEY,'A') = IFNULL(E.PR_INVOICE_KEY,'A') "
				+" AND E.FILE_ID =   " + fileId
				+" ORDER BY A2_DOC_NUM , PR_DOC_NUM ";

		

		if (LOGGER.isDebugEnabled()) {
			String msg = "ReconResponseDaoImpl.getErrorRecords Executing query";
			LOGGER.debug(msg);
		}
		Query q = entityManager.createNativeQuery(queryStr);
		List<Object[]> list = q.getResultList();
		if (LOGGER.isDebugEnabled()) {
			String msg = "ReconResponseDaoImpl.getErrorRecords query"
					+ " executed resultSize = " + list.size();
			LOGGER.debug(msg);
		}
		return list.parallelStream().map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private ErrorResponseDto convert(Object[] arr) {
		ErrorResponseDto obj = new ErrorResponseDto();
		obj.setPreviousReportTypeANX2(
				(arr[2] != null) ? arr[2].toString() : null);
		obj.setPreviousReportTypePR(
				(arr[3] != null) ? arr[3].toString() : null);
		obj.setUserResponse((arr[140] != null) ? arr[140].toString() : null);
		obj.setSuggestedResponse((arr[5] != null) ? arr[5].toString() : null);
		obj.setCurrentMismatchReason(
				(arr[6] != null) ? arr[6].toString() : null);
		obj.setForcedMatchResponse((arr[7] != null) ? arr[7].toString() : null);
		obj.setInformationReportReference(
				(arr[8] != null) ? arr[8].toString() : null);
		obj.setResponseTaxPeriod(
				(arr[9] != null) ? "`".concat(arr[9].toString()) : null);
		obj.setPreviousResponseANX2(
				(arr[10] != null) ? arr[10].toString() : null);
		obj.setPreviousResponsePR(
				(arr[11] != null) ? arr[11].toString() : null);
		obj.setCurrentReportType((arr[12] != null) ? arr[12].toString() : null);
		obj.setDocumentNumberPR((arr[13] != null) ? arr[13].toString() : null);
		obj.setDocumentNumberANX2(
				(arr[14] != null) ? arr[14].toString() : null);
		obj.setRecipientGSTINANX2(
				(arr[15] != null) ? arr[15].toString() : null);
		obj.setRecipientGSTINPR((arr[16] != null) ? arr[16].toString() : null);
		obj.setDocTypeANX2((arr[17] != null) ? arr[17].toString() : null);
		obj.setDocTypePR((arr[18] != null) ? arr[18].toString() : null);
		obj.setDocumentDateANX2((arr[19] != null) ? arr[19].toString() : null);
		obj.setDocumentDatePR((arr[20] != null) ? arr[20].toString() : null);
		obj.setTaxableValueANX2((arr[21] != null) ? arr[21].toString() : null);
		obj.setTaxableValuePR((arr[22] != null) ? arr[22].toString() : null);
		obj.setCGSTANX2((arr[23] != null) ? arr[23].toString() : null);
		obj.setCGSTPR((arr[24] != null) ? arr[24].toString() : null);
		obj.setSGSTANX2((arr[25] != null) ? arr[25].toString() : null);
		obj.setSGSTPR((arr[26] != null) ? arr[26].toString() : null);
		obj.setIGSTANX2((arr[27] != null) ? arr[27].toString() : null);
		obj.setIGSTPR((arr[28] != null) ? arr[28].toString() : null);
		obj.setCessANX2((arr[29] != null) ? arr[29].toString() : null);
		obj.setCessPR((arr[30] != null) ? arr[30].toString() : null);
		obj.setPOSANX2((arr[31] != null) ? arr[31].toString() : null);
		obj.setPOSPR((arr[32] != null) ? arr[32].toString() : null);
		obj.setSavedResponseReportTypeANX2(
				(arr[33] != null) ? arr[33].toString() : null);
		obj.setDigiGSTSavedResponseANX2(
				(arr[34] != null) ? arr[34].toString() : null);
		obj.setGSTNSavedResponseANX2(
				(arr[35] != null) ? arr[35].toString() : null);
		obj.setMatchingScore((arr[36] != null) ? arr[36].toString() : null);
		obj.setReconTaxPeriod(
				(arr[37] != null) ? "`".concat(arr[37].toString()) : null);
		obj.setCalendarMonthPR((arr[38] != null) ? arr[38].toString() : null);
		obj.setDateofUploadANX2((arr[39] != null) ? arr[39].toString() : null);
		obj.setOrgMonthANX2((arr[40] != null) ? arr[40].toString() : null);
		obj.setOrgMonthPR((arr[41] != null) ? arr[41].toString() : null);
		obj.setSupplierNameANX2((arr[42] != null) ? arr[42].toString() : null);
		obj.setSupplierNamePR((arr[43] != null) ? arr[43].toString() : null);
		obj.setTotalTaxANX2((arr[44] != null) ? arr[44].toString() : null);
		obj.setTotalTaxPR((arr[45] != null) ? arr[45].toString() : null);
		obj.setInvoiceValueANX2((arr[46] != null) ? arr[46].toString() : null);
		obj.setInvoiceValuePR((arr[47] != null) ? arr[47].toString() : null);
		obj.setAvailableIGST((arr[48] != null) ? arr[48].toString() : null);
		obj.setAvailableCGST((arr[49] != null) ? arr[49].toString() : null);
		obj.setAvailableSGST((arr[50] != null) ? arr[50].toString() : null);
		obj.setAvailableCESS((arr[51] != null) ? arr[51].toString() : null);
		obj.setTableTypeANX2((arr[52] != null) ? arr[52].toString() : null);
		obj.setTableTypePR((arr[53] != null) ? arr[53].toString() : null);
		obj.setCFSFlagANX2((arr[54] != null) ? arr[54].toString() : null);
		obj.setClaimRefundFlagANX2(
				(arr[55] != null) ? arr[55].toString() : null);
		obj.setClaimRefundFlagPR((arr[56] != null) ? arr[56].toString() : null);
		obj.setSection7ofIGSTANX2(
				(arr[57] != null) ? arr[57].toString() : null);
		obj.setSection7ofIGSTPR((arr[58] != null) ? arr[58].toString() : null);
		obj.setAutoPopulateToRefundANX2(
				(arr[59] != null) ? arr[59].toString() : null);
		obj.setAutoPopulateToRefundPR(
				(arr[60] != null) ? arr[60].toString() : null);
		obj.setDifferentialPercentageANX2(
				(arr[61] != null) ? arr[61].toString() : null);
		obj.setDifferentialPercentagePR(
				(arr[62] != null) ? arr[62].toString() : null);
		obj.setOrgDocTypeANX2((arr[63] != null) ? arr[63].toString() : null);
		obj.setOrgDocTypePR((arr[64] != null) ? arr[64].toString() : null);
		obj.setOrgDocNumberANX2((arr[65] != null) ? arr[65].toString() : null);
		obj.setOrgDocNumberPR((arr[66] != null) ? arr[66].toString() : null);
		obj.setOrgDocDateANX2((arr[67] != null) ? arr[67].toString() : null);
		obj.setOrgDocDatePR((arr[68] != null) ? arr[68].toString() : null);
		obj.setSupplierGSTINPR((arr[69] != null) ? arr[69].toString() : null);
		obj.setUserID((arr[70] != null) ? arr[70].toString() : null);
		obj.setSourceFileName((arr[71] != null) ? arr[71].toString() : null);
		obj.setProfitCentre((arr[72] != null) ? arr[72].toString() : null);
		obj.setPlant((arr[73] != null) ? arr[73].toString() : null);
		obj.setDivision((arr[74] != null) ? arr[74].toString() : null);
		obj.setLocation((arr[75] != null) ? arr[75].toString() : null);
		obj.setPurchaseOrganisation(
				(arr[76] != null) ? arr[76].toString() : null);
		obj.setUserAccess1((arr[77] != null) ? arr[77].toString() : null);
		obj.setUserAccess2((arr[78] != null) ? arr[78].toString() : null);
		obj.setUserAccess3((arr[79] != null) ? arr[79].toString() : null);
		obj.setUserAccess4((arr[80] != null) ? arr[80].toString() : null);
		obj.setUserAccess5((arr[81] != null) ? arr[81].toString() : null);
		obj.setUserAccess6((arr[82] != null) ? arr[82].toString() : null);
		obj.setGlCodeTaxableValue(
				(arr[83] != null) ? arr[83].toString() : null);
		obj.setGlCodeIGST((arr[84] != null) ? arr[84].toString() : null);
		obj.setGlCodeCGST((arr[85] != null) ? arr[85].toString() : null);
		obj.setGlCodeSGST((arr[86] != null) ? arr[86].toString() : null);
		obj.setGlCodeAdvaloremCess(
				(arr[87] != null) ? arr[87].toString() : null);
		obj.setGlCodeSpecificCess(
				(arr[88] != null) ? arr[88].toString() : null);
		obj.setGlCodeStateCess((arr[89] != null) ? arr[89].toString() : null);
		obj.setSupplyType((arr[90] != null) ? arr[90].toString() : null);
		obj.setCRDRPreGST((arr[91] != null) ? arr[91].toString() : null);
		obj.setSupplierType((arr[92] != null) ? arr[92].toString() : null);
		obj.setSupplierCode((arr[93] != null) ? arr[93].toString() : null);
		obj.setSupplierAddress1((arr[94] != null) ? arr[94].toString() : null);
		obj.setSupplierAddress2((arr[95] != null) ? arr[95].toString() : null);
		obj.setSupplierAddress3((arr[96] != null) ? arr[96].toString() : null);
		obj.setSupplierAddress4((arr[97] != null) ? arr[97].toString() : null);
		obj.setStateApplyingCess((arr[98] != null) ? arr[98].toString() : null);
		obj.setPortCode((arr[99] != null) ? arr[99].toString() : null);
		obj.setBillOfEntry((arr[100] != null) ? arr[100].toString() : null);
		obj.setBillOfEntryDate((arr[101] != null) ? arr[101].toString() : null);
		obj.setCIFValue((arr[102] != null) ? arr[102].toString() : null);
		obj.setCustomDuty((arr[103] != null) ? arr[103].toString() : null);
		obj.setQuantity((arr[104] != null) ? arr[104].toString() : null);
		obj.setCessAmountAdvalorem(
				(arr[105] != null) ? arr[105].toString() : null);
		obj.setCessAmountSpecific(
				(arr[106] != null) ? arr[106].toString() : null);
		obj.setStateCessAmount((arr[107] != null) ? arr[107].toString() : null);
		obj.setOtherValue((arr[108] != null) ? arr[108].toString() : null);
		obj.setPurchaseVoucherDate(
				(arr[109] != null) ? arr[109].toString() : null);
		obj.setPurchaseVoucherNumber(
				(arr[110] != null) ? arr[110].toString() : null);
		obj.setPostingDate((arr[111] != null) ? arr[111].toString() : null);
		obj.setPaymentVoucherNumber(
				(arr[112] != null) ? arr[112].toString() : null);
		obj.setPaymentDate((arr[113] != null) ? arr[113].toString() : null);
		obj.setContractNumber((arr[114] != null) ? arr[114].toString() : null);
		obj.setContractValue((arr[115] != null) ? arr[115].toString() : null);
		obj.setEWayBillNumber((arr[116] != null) ? arr[116].toString() : null);
		obj.setEWayBillDate((arr[117] != null) ? arr[117].toString() : null);
		obj.setUserDefinedField1(
				(arr[118] != null) ? arr[118].toString() : null);
		obj.setUserDefinedField2(
				(arr[119] != null) ? arr[119].toString() : null);
		obj.setUserDefinedField3(
				(arr[120] != null) ? arr[120].toString() : null);
		obj.setUserDefinedField4(
				(arr[121] != null) ? arr[121].toString() : null);
		obj.setUserDefinedField5(
				(arr[122] != null) ? arr[122].toString() : null);
		obj.setUserDefinedField6(
				(arr[123] != null) ? arr[123].toString() : null);
		obj.setUserDefinedField7(
				(arr[124] != null) ? arr[124].toString() : null);
		obj.setUserDefinedField8(
				(arr[125] != null) ? arr[125].toString() : null);
		obj.setUserDefinedField9(
				(arr[126] != null) ? arr[126].toString() : null);
		obj.setUserDefinedField10(
				(arr[127] != null) ? arr[127].toString() : null);
		obj.setUserDefinedField11(
				(arr[128] != null) ? arr[128].toString() : null);
		obj.setUserDefinedField12(
				(arr[129] != null) ? arr[129].toString() : null);
		obj.setUserDefinedField13(
				(arr[130] != null) ? arr[130].toString() : null);
		obj.setUserDefinedField14(
				(arr[131] != null) ? arr[131].toString() : null);
		obj.setUserDefinedField15(
				(arr[132] != null) ? arr[132].toString() : null);
		obj.setMatchingID((arr[133] != null) ? arr[133].toString() : null);
		obj.setIDPR((arr[134] != null) ? arr[134].toString() : null);
		obj.setIDA2((arr[135] != null) ? arr[135].toString() : null);
		obj.setInvoiceKeyPR((arr[136] != null) ? arr[136].toString() : null);
		obj.setInvoiceKeyA2((arr[137] != null) ? arr[137].toString() : null);
		obj.setSupplierGSTINANX2(
				(arr[138] != null) ? arr[138].toString() : null);
		obj.setErrorMessage((arr[139] != null) ? arr[139].toString() : null);
		obj.setRequestID(
				(arr[141] != null) ? "`".concat(arr[141].toString()) : null);

		return obj;

	}

}