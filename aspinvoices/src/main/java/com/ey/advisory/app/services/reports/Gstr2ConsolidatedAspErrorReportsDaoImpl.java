package com.ey.advisory.app.services.reports;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1inwardapiErrorRecordsDto;
import com.ey.advisory.app.report.convertor.InwardErrorsFileStatusConvertor;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

import com.google.common.base.Strings;

@Component("Gstr2ConsolidatedAspErrorReportsDaoImpl")
public class Gstr2ConsolidatedAspErrorReportsDaoImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1TransLevelSummaryDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	 @Autowired
     @Qualifier("InwardErrorsFileStatusConvertor")
    private	InwardErrorsFileStatusConvertor convertor;

	public List<Object> getErrorReports(Gstr2ProcessedRecordsReqDto criteria) {

		LocalDate docRecvFrom = criteria.getDocRecvFrom();
		LocalDate docRecvTo = criteria.getDocRecvTo();
		String taxPeriodFrom = criteria.getTaxPeriodFrom();
		String taxPeriodTo = criteria.getTaxPeriodTo();
		List<String> tableType = criteria.getTableType();
		List<String> tableTypeUpperCase = tableType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());

		List<String> docType = criteria.getDocType();
		List<String> docTypeUpperCase = docType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());

		Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();
		List<String> gstinList = new ArrayList<>();
		String cgstin = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					cgstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);

					}
				}
			}
		}
		StringBuilder queryBuilder = new StringBuilder();

		if (cgstin != null && !cgstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				queryBuilder.append(" AND HDR.CUST_GSTIN IN :gstinList");
			}
		}
			if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
				queryBuilder.append(
						" AND  HDR.TAX_DOC_TYPE IN :tableTypeUpperCase");
			}
		
			if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
				queryBuilder.append(" AND  HDR.DOC_TYPE IN :docTypeUpperCase");
			}
		
		if (docRecvFrom != null && docRecvTo != null) {
			queryBuilder.append(
					" AND  HDR.DOC_DATE BETWEEN :docRecvFrom AND :docRecvTo  ");
		}
		if (StringUtils.isNotBlank(taxPeriodFrom)
				&& StringUtils.isNotBlank(taxPeriodTo)) {
			queryBuilder.append(
					" AND  ITM.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo  ");
		}
		String queryStr = creategstnTransQueryString(queryBuilder.toString());

		LOGGER.error("bufferString-------------------------->" + queryStr);
		Query outquery = entityManager.createNativeQuery(queryStr);

		if (cgstin != null && !cgstin.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				outquery.setParameter("gstinList", gstinList);
			}
		}
		if (docRecvFrom != null && docRecvTo != null) {
			outquery.setParameter("docRecvFrom", docRecvFrom);
			outquery.setParameter("docRecvTo", docRecvTo);
		}
		if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
			outquery.setParameter("docTypeUpperCase", docTypeUpperCase);
		}
		if (StringUtils.isNotBlank(taxPeriodFrom)
				&& StringUtils.isNotBlank(taxPeriodTo)) {
			outquery.setParameter("taxPeriodFrom",
					GenUtil.convertTaxPeriodToInt(taxPeriodFrom));
			outquery.setParameter("taxPeriodTo",
					GenUtil.convertTaxPeriodToInt(taxPeriodTo));
		}

		if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
			outquery.setParameter("tableTypeUpperCase", tableTypeUpperCase);
		}
		if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
			outquery.setParameter("docTypeUpperCase", docTypeUpperCase);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = outquery.getResultList();
		return list.parallelStream().map(o -> convertor.convert(o, null))
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	private String creategstnTransQueryString(String buildQuery) {

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("gstr2 error report buildQuery{}",buildQuery);
			}
		StringBuilder data=new StringBuilder();
		data.append("(SELECT TO_CHAR(HDR.IRN),");
		data.append( "TO_CHAR(HDR.IRN_DATE,'DD-MM-YYYY') IRN_DATE,");
		data.append( "TO_CHAR(TAX_SCHEME),TO_CHAR(HDR.SUPPLY_TYPE),");
		data.append( "TO_CHAR(ITM.DOC_CATEGORY),");
		data.append( "TO_CHAR(HDR.DOC_TYPE),TO_CHAR(HDR.DOC_NUM),");
		data.append( "TO_CHAR(HDR.DOC_DATE),TO_CHAR(ITM.REVERSE_CHARGE),");
		data.append( "TO_CHAR(HDR.SUPPLIER_GSTIN),TO_CHAR(ITM.SUPP_TRADE_NAME),");
		data.append( "TO_CHAR(ITM.CUST_SUPP_NAME),TO_CHAR(ITM.CUST_SUPP_ADDRESS1),");
		data.append( "TO_CHAR(ITM.CUST_SUPP_ADDRESS2),TO_CHAR(ITM.CUST_SUPP_ADDRESS3),");
		data.append( "TO_CHAR(ITM.CUST_SUPP_ADDRESS4),TO_CHAR(ITM.SUPP_STATE_CODE),");
		data.append( "TO_CHAR(ITM.SUPP_PHONE),TO_CHAR(ITM.SUPP_EMAIL),");
		data.append( "TO_CHAR(HDR.CUST_GSTIN),TO_CHAR(ITM.CUST_TRADE_NAME),");
		data.append( "TO_CHAR(ITM.CUST_LEGAL_NAME),TO_CHAR(ITM.CUST_BUILDING_NUM ),");
		data.append( "TO_CHAR(ITM.CUST_BUILDING_NAME),TO_CHAR(ITM.CUST_LOCATION),");
		data.append( "TO_CHAR(ITM.CUST_PINCODE),TO_CHAR(ITM.BILL_TO_STATE),");
		data.append( "TO_CHAR(ITM.POS),TO_CHAR(ITM.CUST_PHONE),TO_CHAR(ITM.CUST_EMAIL),");
		data.append( "TO_CHAR(HDR.DISPATCHER_GSTIN),TO_CHAR(ITM.DISPATCHER_TRADE_NAME),");
		data.append( "TO_CHAR(DISPATCHER_BUILDING_NUM),TO_CHAR(DISPATCHER_BUILDING_NAME),");
		data.append( "TO_CHAR(DISPATCHER_LOCATION),TO_CHAR(DISPATCHER_PINCODE),");
		data.append( "TO_CHAR(DISPATCHER_STATE_CODE),");
		data.append( "TO_CHAR(HDR.SHIP_TO_GSTIN),TO_CHAR(SHIP_TO_TRADE_NAME),");
		data.append( "TO_CHAR(SHIP_TO_LEGAL_NAME),TO_CHAR(SHIP_TO_BUILDING_NUM),");
		data.append( "TO_CHAR(SHIP_TO_BUILDING_NAME),TO_CHAR(SHIP_TO_LOCATION),");
		data.append( "TO_CHAR(SHIP_TO_PINCODE),TO_CHAR(SHIP_TO_STATE),");
		data.append( "TO_CHAR(ITM_NO),TO_CHAR(SERIAL_NUM2),TO_CHAR(PRODUCT_NAME),");
		data.append( "TO_CHAR(ITM_DESCRIPTION),TO_CHAR(IS_SERVICE),TO_CHAR(ITM_HSNSAC),");
		data.append( "TO_CHAR(BAR_CODE),TO_CHAR(BATCH_NAME_OR_NUM),");
		data.append( "TO_CHAR(BATCH_EXPIRY_DATE),TO_CHAR(WARRANTY_DATE),");
		data.append( "TO_CHAR(ORDER_ITEM_REFERENCE),TO_CHAR(ATTRIBUTE_NAME),");
		data.append( "TO_CHAR(ATTRIBUTE_VALUE),TO_CHAR(ORIGIN_COUNTRY),");
		data.append( "TO_CHAR(ITM_UQC),TO_CHAR(ITM_QTY),TO_CHAR(FREE_QTY),");
		data.append( "TO_CHAR(UNIT_PRICE),TO_CHAR(ITEM_AMT_UP_QTY),TO_CHAR(ITEM_DISCOUNT),");
		data.append( "TO_CHAR(PRE_TAX_AMOUNT),TO_CHAR(ITM.TAXABLE_VALUE),");
		data.append( "TO_CHAR(ITM.IGST_RATE),TO_CHAR(ITM.IGST_AMT),TO_CHAR(ITM.CGST_RATE),");
		data.append( "TO_CHAR(ITM.CGST_AMT),TO_CHAR(ITM.SGST_RATE),TO_CHAR(ITM.SGST_AMT),");
		data.append( "TO_CHAR(ITM.CESS_RATE_ADVALOREM),TO_CHAR(ITM.CESS_AMT_ADVALOREM),");
		data.append( "TO_CHAR(ITM.CESS_RATE_SPECIFIC),TO_CHAR(ITM.CESS_AMT_SPECIFIC),");
		data.append( "TO_CHAR(ITM.STATECESS_RATE),TO_CHAR(ITM.STATECESS_AMT),");
		data.append( "TO_CHAR(ITM.STATE_CESS_SPECIFIC_RATE),");
		data.append( "TO_CHAR(ITM.STATE_CESS_SPECIFIC_AMOUNT),TO_CHAR(ITM.OTHER_VALUES),");
		data.append( "TO_CHAR(TOT_ITEM_AMT),TO_CHAR(ITM.INV_OTHER_CHARGES),");
		data.append( "TO_CHAR(ITM.INV_ASSESSABLE_AMT),TO_CHAR(ITM.INV_IGST_AMT),");
		data.append( "TO_CHAR(ITM.INV_CGST_AMT),TO_CHAR(ITM.INV_SGST_AMT),");
		data.append( "TO_CHAR(ITM.INV_CESS_ADVLRM_AMT),TO_CHAR(ITM.INV_CESS_SPECIFIC_AMT),");
		data.append( "TO_CHAR(ITM.INV_STATE_CESS_AMT),TO_CHAR(ITM.INV_STATE_CESS_SPECIFIC_AMOUNT),");
		data.append( "TO_CHAR(ITM.LINE_ITEM_AMT),TO_CHAR(ITM.ROUND_OFF),");
		data.append( "TO_CHAR(ITM.TOT_INV_VAL_WORDS),TO_CHAR(ELIGIBILITY_INDICATOR),");
		data.append( "TO_CHAR(COMMON_SUP_INDICATOR),TO_CHAR(ITM.AVAILABLE_IGST),");
		data.append( "TO_CHAR(ITM.AVAILABLE_CGST),TO_CHAR(ITM.AVAILABLE_SGST),");
		data.append( "TO_CHAR(ITM.AVAILABLE_CESS),TO_CHAR(ITM.ITC_ENTITLEMENT),");
		data.append( "TO_CHAR(ITM.ITC_REVERSAL_IDENTIFER),TO_CHAR(ITM.TCS_FLAG_INCOME_TAX),");
		data.append( "TO_CHAR(TCS_RATE_INCOME_TAX),TO_CHAR(TCS_AMOUNT_INCOME_TAX),");
		data.append( "TO_CHAR(FOREIGN_CURRENCY),TO_CHAR(COUNTRY_CODE),TO_CHAR(INV_VAL_FC),");
		data.append( "TO_CHAR(ITM.SHIP_PORT_CODE),TO_CHAR(ITM.BILL_OF_ENTRY),");
		data.append( "TO_CHAR(ITM.BILL_OF_ENTRY_DATE),TO_CHAR(INV_REMARKS),");
		data.append( "TO_CHAR(INV_PERIOD_START_DATE),TO_CHAR(INV_PERIOD_END_DATE),");
		data.append( "TO_CHAR(ITM.ORIGINAL_DOC_NUM),TO_CHAR(ITM.ORIGINAL_DOC_DATE),");
		data.append( "TO_CHAR(ITM.INV_REFERENCE),TO_CHAR(RECEIPT_ADVICE_REFERENCE),");
		data.append( "TO_CHAR(RECEIPT_ADVICE_DATE),TO_CHAR(TENDER_REFERENCE),");
		data.append( "TO_CHAR(CONTRACT_REFERENCE),TO_CHAR(EXTERNAL_REFERENCE),");
		data.append( "TO_CHAR(PROJECT_REFERENCE),TO_CHAR(CONTRACT_NUMBER),");
		data.append( "TO_CHAR(CONTRACT_DATE),TO_CHAR(PAYEE_NAME),TO_CHAR(MODE_OF_PAYMENT),");
		data.append( "TO_CHAR(BRANCH_IFSC_CODE),TO_CHAR(PAYMENT_TERMS),");
		data.append( "TO_CHAR(PAYMENT_INSTRUCTION),TO_CHAR(CR_TRANSFER),TO_CHAR(DB_DIRECT),");
		data.append( "TO_CHAR(CR_DAYS),TO_CHAR(PAID_AMT),TO_CHAR(BAL_AMT),");
		data.append( "TO_CHAR(ITM.PAYMENT_DUE_DATE),TO_CHAR(ACCOUNT_DETAIL),");
		data.append( "TO_CHAR(ITM.ECOM_GSTIN),TO_CHAR(SUPPORTING_DOC_URL),");
		data.append( "TO_CHAR(SUPPORTING_DOC),TO_CHAR(ADDITIONAL_INFORMATION),");
		data.append( "TO_CHAR(TRANS_TYPE),TO_CHAR(SUB_SUPP_TYPE),TO_CHAR(OTHER_SUPP_TYPE_DESC),");
		data.append( "TO_CHAR(TRANSPORTER_ID),TO_CHAR(TRANSPORTER_NAME),TO_CHAR(TRANSPORT_MODE),");
		data.append( "TO_CHAR(TRANSPORT_DOC_NUM),TO_CHAR(TRANSPORT_DOC_DATE),TO_CHAR(DISTANCE),");
		data.append( "TO_CHAR(VEHICLE_NUM),TO_CHAR(VEHICLE_TYPE),TO_CHAR(HDR.RETURN_PERIOD),");
		data.append( "TO_CHAR(ITM.ORIGINAL_DOC_TYPE),");
		data.append( "TO_CHAR(ITM.ORIG_SUPPLIER_GSTIN),");
		data.append( "TO_CHAR(ITM.DIFF_PERCENT),");
		data.append( "TO_CHAR(ITM.SECTION7_OF_IGST_FLAG),");
		data.append( "TO_CHAR(ITM.CLAIM_REFUND_FLAG),");
		data.append( "TO_CHAR(ITM.AUTOPOPULATE_TO_REFUND),");
		data.append( "TO_CHAR(ITM.CRDR_PRE_GST),");
		data.append( "TO_CHAR(ITM.CUST_SUPP_TYPE),");
		data.append( "TO_CHAR(ITM.CUST_SUPP_CODE),");
		data.append( "TO_CHAR(ITM.PRODUCT_CODE),");
		data.append( "TO_CHAR(ITM.ITM_TYPE),");
		data.append( "TO_CHAR(ITM.STATE_APPLYING_CESS),");
		data.append( "TO_CHAR(ITM.CIF_VALUE),");
		data.append( "TO_CHAR(CUSTOM_DUTY),");
		data.append( "TO_CHAR(EXCHANGE_RATE),");
		data.append( "TO_CHAR(CRDR_REASON),");
		data.append( "TO_CHAR(ITM.TCS_FLAG),TO_CHAR(ITM.TCS_IGST_AMT),TO_CHAR(ITM.TCS_CGST_AMT),");
		data.append( "TO_CHAR(ITM.TCS_SGST_AMT),TO_CHAR(ITM.TDS_FLAG),");
		data.append( "TO_CHAR(ITM.TDS_IGST_AMT),TO_CHAR(ITM.TDS_CGST_AMT),");
		data.append( "TO_CHAR(ITM.TDS_SGST_AMT),TO_CHAR(ITM.USER_ID),");
		data.append( "TO_CHAR(ITM.COMPANY_CODE),TO_CHAR(ITM.SOURCE_IDENTIFIER),");
		data.append( "TO_CHAR(ITM.SOURCE_FILENAME),TO_CHAR(ITM.PLANT_CODE),");
		data.append( "TO_CHAR(ITM.DIVISION),TO_CHAR(ITM.SUB_DIVISION),");
		data.append( "TO_CHAR(ITM.LOCATION),TO_CHAR(ITM.PURCHASE_ORGANIZATION),");
		data.append( "TO_CHAR(ITM.PROFIT_CENTRE),TO_CHAR(ITM.PROFIT_CENTRE2),");
		data.append( "TO_CHAR(ITM.USERACCESS1),TO_CHAR(ITM.USERACCESS2),");
		data.append( "TO_CHAR(ITM.USERACCESS3),TO_CHAR(ITM.USERACCESS4),");
		data.append( "TO_CHAR(ITM.USERACCESS5),TO_CHAR(ITM.USERACCESS6),");
		data.append( "TO_CHAR(GLCODE_TAXABLEVALUE),TO_CHAR(GLCODE_IGST),");
		data.append( "TO_CHAR(GLCODE_CGST),TO_CHAR(GLCODE_SGST),");
		data.append( "TO_CHAR(GLCODE_ADV_CESS),TO_CHAR(GLCODE_SP_CESS),");
		data.append( "TO_CHAR(GLCODE_STATE_CESS),TO_CHAR(GL_STATE_CESS_SPECIFIC),");
		data.append( "TO_CHAR(ITM.POSTING_DATE),TO_CHAR(CONTRACT_VALUE),");
		data.append( "TO_CHAR(ITM.EWAY_BILL_NUM),TO_CHAR(ITM.EWAY_BILL_DATE),");
		data.append( "TO_CHAR(ITM.PURCHASE_VOUCHER_NUM),TO_CHAR(ITM.PURCHASE_VOUCHER_DATE),");
		data.append( "TO_CHAR(ITM.DOCUMENT_REFERENCE_NUMBER),TO_CHAR(ITM.USERDEFINED_FIELD1),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD2),TO_CHAR(ITM.USERDEFINED_FIELD3),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD4),TO_CHAR(ITM.USERDEFINED_FIELD5),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD6),TO_CHAR(ITM.USERDEFINED_FIELD7),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD8),TO_CHAR(ITM.USERDEFINED_FIELD9),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD10),TO_CHAR(ITM.USERDEFINED_FIELD11),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD12),TO_CHAR(ITM.USERDEFINED_FIELD13),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD14),TO_CHAR(ITM.USERDEFINED_FIELD15),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD16),TO_CHAR(ITM.USERDEFINED_FIELD17),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD18),TO_CHAR(ITM.USERDEFINED_FIELD19),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD20),TO_CHAR(ITM.USERDEFINED_FIELD21),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD22),TO_CHAR(ITM.USERDEFINED_FIELD23),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD24),TO_CHAR(ITM.USERDEFINED_FIELD25),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD26),TO_CHAR(ITM.USERDEFINED_FIELD27),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD28),TO_CHAR(ITM.USERDEFINED_FIELD29),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD30),");
		data.append( "TRIM(', ' FROM IFNULL(HDR.ERROR_CODES,'')  ||','|| IFNULL(ITM.ERROR_CODES,'')) ");
		data.append( "AS ERROR_CODE_ASP,NULL AS  ERROR_DESCRIPTION_ASP, ");
		data.append( "TRIM(',' FROM IFNULL(HDR.INFORMATION_CODES,'')||','||IFNULL(ITM.INFORMATION_CODES,'')) ");
		data.append( "AS INFO_ERROR_CODE_ASP,");
		data.append( "NULL AS  INFO_ERROR_DESCRIPTION_ASP,Case when HDR.FILE_ID IS NULL "); 
		data.append( "THEN 'API' when HDR.FILE_ID IS NOT NULL THEN 'WEB UPLOAD' END ");
		data.append( "UPLOAD_SOURCE,HDR.CREATED_BY AS SOURCE_ID, '' AS FILE_NAME, ");
		data.append( "HDR.CREATED_ON AS ASP_DATE_TIME ");
		data.append( " FROM ANX_INWARD_DOC_HEADER HDR ");
		data.append( " INNER JOIN ANX_INWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID ");
		data.append( "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT OUTER JOIN FILE_STATUS FIL ");
		data.append( "ON ITM.FILE_ID=FIL.ID WHERE IS_ERROR=TRUE AND HDR.RETURN_TYPE = 'GSTR2' ");
		data.append( "AND IS_DELETE = FALSE " +buildQuery+ " ORDER BY ITM.ID)");
		data.append( " UNION ALL ");
		data.append("(SELECT TO_CHAR(ITM.IRN),");
		//data.append( "TO_CHAR(ITM.IRN_DATE,'DD-MM-YYYY') IRN_DATE,");
		data.append( "TO_CHAR(ITM.IRN_DATE),");
		data.append( "TO_CHAR(ITM.TAX_SCHEME),TO_CHAR(ITM.SUPPLY_TYPE),");
		data.append( "TO_CHAR(ITM.DOC_CATEGORY),");
		data.append( "TO_CHAR(ITM.DOC_TYPE),TO_CHAR(ITM.DOC_NUM),");
		data.append( "TO_CHAR(ITM.DOC_DATE),TO_CHAR(ITM.REVERSE_CHARGE),");
		data.append( "TO_CHAR(ITM.SUPPLIER_GSTIN),TO_CHAR(ITM.SUPP_TRADE_NAME),");
		data.append( "TO_CHAR(ITM.CUST_SUPP_NAME),TO_CHAR(ITM.CUST_SUPP_ADDRESS1),");
		data.append( "TO_CHAR(ITM.CUST_SUPP_ADDRESS2),TO_CHAR(ITM.CUST_SUPP_ADDRESS3),");
		data.append( "TO_CHAR(ITM.CUST_SUPP_ADDRESS4),TO_CHAR(ITM.SUPP_STATE_CODE),");
		data.append( "TO_CHAR(ITM.SUPP_PHONE),TO_CHAR(ITM.SUPP_EMAIL),");
		data.append( "TO_CHAR(ITM.CUST_GSTIN),TO_CHAR(ITM.CUST_TRADE_NAME),");
		data.append( "TO_CHAR(ITM.CUST_LEGAL_NAME),TO_CHAR(ITM.CUST_BUILDING_NUM ),");
		data.append( "TO_CHAR(ITM.CUST_BUILDING_NAME),TO_CHAR(ITM.CUST_LOCATION),");
		data.append( "TO_CHAR(ITM.CUST_PINCODE),TO_CHAR(ITM.BILL_TO_STATE),");
		data.append( "TO_CHAR(ITM.POS),TO_CHAR(ITM.CUST_PHONE),TO_CHAR(ITM.CUST_EMAIL),");
		data.append( "TO_CHAR(ITM.DISPATCHER_GSTIN),TO_CHAR(ITM.DISPATCHER_TRADE_NAME),");
		data.append( "TO_CHAR(ITM.DISPATCHER_BUILDING_NUM),TO_CHAR(ITM.DISPATCHER_BUILDING_NAME),");
		data.append( "TO_CHAR(ITM.DISPATCHER_LOCATION),TO_CHAR(ITM.DISPATCHER_PINCODE),");
		data.append( "TO_CHAR(ITM.DISPATCHER_STATE_CODE),");
		data.append( "TO_CHAR(ITM.SHIP_TO_GSTIN),TO_CHAR(ITM.SHIP_TO_TRADE_NAME),");
		data.append( "TO_CHAR(ITM.SHIP_TO_LEGAL_NAME),TO_CHAR(ITM.SHIP_TO_BUILDING_NUM),");
		data.append( "TO_CHAR(ITM.SHIP_TO_BUILDING_NAME),TO_CHAR(ITM.SHIP_TO_LOCATION),");
		data.append( "TO_CHAR(ITM.SHIP_TO_PINCODE),TO_CHAR(ITM.SHIP_TO_STATE),");
		data.append( "TO_CHAR(ITM.ITM_NO),TO_CHAR(ITM.SERIAL_NUM2),TO_CHAR(ITM.PRODUCT_NAME),");
		data.append( "TO_CHAR(ITM.ITM_DESCRIPTION),TO_CHAR(ITM.IS_SERVICE),TO_CHAR(ITM.ITM_HSNSAC),");
		data.append( "TO_CHAR(ITM.BAR_CODE),TO_CHAR(ITM.BATCH_NAME_OR_NUM),");
		data.append( "TO_CHAR(ITM.BATCH_EXPIRY_DATE),TO_CHAR(ITM.WARRANTY_DATE),");
		data.append( "TO_CHAR(ITM.ORDER_ITEM_REFERENCE),TO_CHAR(ITM.ATTRIBUTE_NAME),");
		data.append( "TO_CHAR(ITM.ATTRIBUTE_VALUE),TO_CHAR(ITM.ORIGIN_COUNTRY),");
		data.append( "TO_CHAR(ITM.ITM_UQC),TO_CHAR(ITM.ITM_QTY),TO_CHAR(ITM.FREE_QTY),");
		data.append( "TO_CHAR(ITM.UNIT_PRICE),TO_CHAR(ITM.ITEM_AMT_UP_QTY),TO_CHAR(ITM.ITEM_DISCOUNT),");
		data.append( "TO_CHAR(ITM.PRE_TAX_AMOUNT),TO_CHAR(ITM.TAXABLE_VALUE),");
		data.append( "TO_CHAR(ITM.IGST_RATE),TO_CHAR(ITM.IGST_AMT),TO_CHAR(ITM.CGST_RATE),");
		data.append( "TO_CHAR(ITM.CGST_AMT),TO_CHAR(ITM.SGST_RATE),TO_CHAR(ITM.SGST_AMT),");
		data.append( "TO_CHAR(ITM.CESS_RATE_ADVALOREM),TO_CHAR(ITM.CESS_AMT_ADVALOREM),");
		data.append( "TO_CHAR(ITM.CESS_RATE_SPECIFIC),TO_CHAR(ITM.CESS_AMT_SPECIFIC),");
		data.append( "TO_CHAR(ITM.STATECESS_RATE),TO_CHAR(ITM.STATECESS_AMT),");
		data.append( "TO_CHAR(ITM.STATE_CESS_SPECIFIC_RATE),");
		data.append( "TO_CHAR(ITM.STATE_CESS_SPECIFIC_AMOUNT),TO_CHAR(ITM.OTHER_VALUES),");
		data.append( "TO_CHAR(ITM.TOT_ITEM_AMT),TO_CHAR(ITM.INV_OTHER_CHARGES),");
		data.append( "TO_CHAR(ITM.INV_ASSESSABLE_AMT),TO_CHAR(ITM.INV_IGST_AMT),");
		data.append( "TO_CHAR(ITM.INV_CGST_AMT),TO_CHAR(ITM.INV_SGST_AMT),");
		data.append( "TO_CHAR(ITM.INV_CESS_ADVLRM_AMT),TO_CHAR(ITM.INV_CESS_SPECIFIC_AMT),");
		data.append( "TO_CHAR(ITM.INV_STATE_CESS_AMT),TO_CHAR(ITM.INV_STATE_CESS_SPECIFIC_AMOUNT),");
		data.append( "TO_CHAR(ITM.LINE_ITEM_AMT),TO_CHAR(ITM.ROUND_OFF),");
		data.append( "TO_CHAR(ITM.TOT_INV_VAL_WORDS),TO_CHAR(ITM.ELIGIBILITY_INDICATOR),");
		data.append( "TO_CHAR(ITM.COMMON_SUP_INDICATOR),TO_CHAR(ITM.AVAILABLE_IGST),");
		data.append( "TO_CHAR(ITM.AVAILABLE_CGST),TO_CHAR(ITM.AVAILABLE_SGST),");
		data.append( "TO_CHAR(ITM.AVAILABLE_CESS),TO_CHAR(ITM.ITC_ENTITLEMENT),");
		data.append( "TO_CHAR(ITM.ITC_REVERSAL_IDENTIFER),TO_CHAR(ITM.TCS_FLAG_INCOME_TAX),");
		data.append( "TO_CHAR(ITM.TCS_RATE_INCOME_TAX),TO_CHAR(ITM.TCS_AMOUNT_INCOME_TAX),");
		data.append( "TO_CHAR(ITM.FOREIGN_CURRENCY),TO_CHAR(ITM.COUNTRY_CODE),TO_CHAR(ITM.INV_VAL_FC),");
		data.append( "TO_CHAR(ITM.SHIP_PORT_CODE),TO_CHAR(ITM.BILL_OF_ENTRY),");
		data.append( "TO_CHAR(ITM.BILL_OF_ENTRY_DATE),TO_CHAR(ITM.INV_REMARKS),");
		data.append( "TO_CHAR(ITM.INV_PERIOD_START_DATE),TO_CHAR(ITM.INV_PERIOD_END_DATE),");
		data.append( "TO_CHAR(ITM.ORIGINAL_DOC_NUM),TO_CHAR(ITM.ORIGINAL_DOC_DATE),");
		data.append( "TO_CHAR(ITM.INV_REFERENCE),TO_CHAR(ITM.RECEIPT_ADVICE_REFERENCE),");
		data.append( "TO_CHAR(ITM.RECEIPT_ADVICE_DATE),TO_CHAR(ITM.TENDER_REFERENCE),");
		data.append( "TO_CHAR(ITM.CONTRACT_REFERENCE),TO_CHAR(ITM.EXTERNAL_REFERENCE),");
		data.append( "TO_CHAR(ITM.PROJECT_REFERENCE),TO_CHAR(ITM.CONTRACT_NUMBER),");
		data.append( "TO_CHAR(ITM.CONTRACT_DATE),TO_CHAR(ITM.PAYEE_NAME),TO_CHAR(ITM.MODE_OF_PAYMENT),");
		data.append( "TO_CHAR(ITM.BRANCH_IFSC_CODE),TO_CHAR(ITM.PAYMENT_TERMS),");
		data.append( "TO_CHAR(ITM.PAYMENT_INSTRUCTION),TO_CHAR(ITM.CR_TRANSFER),TO_CHAR(ITM.DB_DIRECT),");
		data.append( "TO_CHAR(ITM.CR_DAYS),TO_CHAR(ITM.PAID_AMT),TO_CHAR(ITM.BAL_AMT),");
		data.append( "TO_CHAR(ITM.PAYMENT_DUE_DATE),TO_CHAR(ITM.ACCOUNT_DETAIL),");
		data.append( "TO_CHAR(ITM.ECOM_GSTIN),TO_CHAR(ITM.SUPPORTING_DOC_URL),");
		data.append( "TO_CHAR(ITM.SUPPORTING_DOC),TO_CHAR(ITM.ADDITIONAL_INFORMATION),");
		data.append( "TO_CHAR(ITM.TRANS_TYPE),TO_CHAR(ITM.SUB_SUPP_TYPE),TO_CHAR(ITM.OTHER_SUPP_TYPE_DESC),");
		data.append( "TO_CHAR(ITM.TRANSPORTER_ID),TO_CHAR(ITM.TRANSPORTER_NAME),TO_CHAR(ITM.TRANSPORT_MODE),");
		data.append( "TO_CHAR(ITM.TRANSPORT_DOC_NUM),TO_CHAR(ITM.TRANSPORT_DOC_DATE),TO_CHAR(ITM.DISTANCE),");
		data.append( "TO_CHAR(ITM.VEHICLE_NUM),TO_CHAR(ITM.VEHICLE_TYPE),TO_CHAR(ITM.RETURN_PERIOD),");
		data.append( "TO_CHAR(ITM.ORIGINAL_DOC_TYPE),");
		data.append( "TO_CHAR(ITM.ORIG_SUPPLIER_GSTIN),");
		data.append( "TO_CHAR(ITM.DIFF_PERCENT),");
		data.append( "TO_CHAR(ITM.SECTION7_OF_IGST_FLAG),");
		data.append( "TO_CHAR(ITM.CLAIM_REFUND_FLAG),");
		data.append( "TO_CHAR(ITM.AUTOPOPULATE_TO_REFUND),");
		data.append( "TO_CHAR(ITM.CRDR_PRE_GST),");
		data.append( "TO_CHAR(ITM.CUST_SUPP_TYPE),");
		data.append( "TO_CHAR(ITM.CUST_SUPP_CODE),");
		data.append( "TO_CHAR(ITM.PRODUCT_CODE),");
		data.append( "TO_CHAR(ITM.ITM_TYPE),");
		data.append( "TO_CHAR(ITM.STATE_APPLYING_CESS),");
		data.append( "TO_CHAR(ITM.CIF_VALUE),");
		data.append( "TO_CHAR(ITM.CUSTOM_DUTY),");
		data.append( "TO_CHAR(ITM.EXCHANGE_RATE),");
		data.append( "TO_CHAR(ITM.CRDR_REASON),");
		data.append( "TO_CHAR(ITM.TCS_FLAG),TO_CHAR(ITM.TCS_IGST_AMT),TO_CHAR(ITM.TCS_CGST_AMT),");
		data.append( "TO_CHAR(ITM.TCS_SGST_AMT),TO_CHAR(ITM.TDS_FLAG),");
		data.append( "TO_CHAR(ITM.TDS_IGST_AMT),TO_CHAR(ITM.TDS_CGST_AMT),");
		data.append( "TO_CHAR(ITM.TDS_SGST_AMT),TO_CHAR(ITM.USER_ID),");
		data.append( "TO_CHAR(ITM.COMPANY_CODE),TO_CHAR(ITM.SOURCE_IDENTIFIER),");
		data.append( "TO_CHAR(ITM.SOURCE_FILENAME),TO_CHAR(ITM.PLANT_CODE),");
		data.append( "TO_CHAR(ITM.DIVISION),TO_CHAR(ITM.SUB_DIVISION),");
		data.append( "TO_CHAR(ITM.LOCATION),TO_CHAR(ITM.PURCHASE_ORGANIZATION),");
		data.append( "TO_CHAR(ITM.PROFIT_CENTRE),TO_CHAR(ITM.PROFIT_CENTRE2),");
		data.append( "TO_CHAR(ITM.USERACCESS1),TO_CHAR(ITM.USERACCESS2),");
		data.append( "TO_CHAR(ITM.USERACCESS3),TO_CHAR(ITM.USERACCESS4),");
		data.append( "TO_CHAR(ITM.USERACCESS5),TO_CHAR(ITM.USERACCESS6),");
		data.append( "TO_CHAR(ITM.GLCODE_TAXABLEVALUE),TO_CHAR(ITM.GLCODE_IGST),");
		data.append( "TO_CHAR(ITM.GLCODE_CGST),TO_CHAR(ITM.GLCODE_SGST),");
		data.append( "TO_CHAR(ITM.GLCODE_ADV_CESS),TO_CHAR(ITM.GLCODE_SP_CESS),");
		data.append( "TO_CHAR(ITM.GLCODE_STATE_CESS),TO_CHAR(ITM.GL_STATE_CESS_SPECIFIC),");
		data.append( "TO_CHAR(ITM.POSTING_DATE),TO_CHAR(ITM.CONTRACT_VALUE),");
		data.append( "TO_CHAR(ITM.EWAY_BILL_NUM),TO_CHAR(ITM.EWAY_BILL_DATE),");
		data.append( "TO_CHAR(ITM.PURCHASE_VOUCHER_NUM),TO_CHAR(ITM.PURCHASE_VOUCHER_DATE),");
		data.append( "TO_CHAR(ITM.DOCUMENT_REFERENCE_NUMBER),TO_CHAR(ITM.USERDEFINED_FIELD1),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD2),TO_CHAR(ITM.USERDEFINED_FIELD3),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD4),TO_CHAR(ITM.USERDEFINED_FIELD5),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD6),TO_CHAR(ITM.USERDEFINED_FIELD7),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD8),TO_CHAR(ITM.USERDEFINED_FIELD9),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD10),TO_CHAR(ITM.USERDEFINED_FIELD11),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD12),TO_CHAR(ITM.USERDEFINED_FIELD13),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD14),TO_CHAR(ITM.USERDEFINED_FIELD15),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD16),TO_CHAR(ITM.USERDEFINED_FIELD17),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD18),TO_CHAR(ITM.USERDEFINED_FIELD19),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD20),TO_CHAR(ITM.USERDEFINED_FIELD21),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD22),TO_CHAR(ITM.USERDEFINED_FIELD23),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD24),TO_CHAR(ITM.USERDEFINED_FIELD25),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD26),TO_CHAR(ITM.USERDEFINED_FIELD27),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD28),TO_CHAR(ITM.USERDEFINED_FIELD29),");
		data.append( "TO_CHAR(ITM.USERDEFINED_FIELD30),");
		data.append( "TRIM(', ' FROM IFNULL(HDR.ERROR_CODES,'')  ||','|| IFNULL(ITM.ERROR_CODES,'')) AS ERROR_CODE_ASP,");
		data.append( "NULL AS  ERROR_DESCRIPTION_ASP, ");
		data.append( "NULL  AS INFO_ERROR_CODE_ASP,");
		data.append( "NULL AS  INFO_ERROR_DESCRIPTION_ASP,Case when HDR.FILE_ID IS NULL ");
		data.append( " THEN 'API' when HDR.FILE_ID IS NOT NULL THEN 'WEB UPLOAD' END ");
		data.append( " UPLOAD_SOURCE,HDR.CREATED_BY AS SOURCE_ID, FIL.FILE_NAME AS FILE_NAME, HDR.CREATED_ON AS ASP_DATE_TIME ");
		data.append( " FROM ANX_INWARD_ERROR_HEADER HDR INNER JOIN ANX_INWARD_ERROR_ITEM ITM ");
		data.append( " ON HDR.ID=ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD ");
		//data.append( " inner join GSTIN_INFO GIN ON HDR.CUST_GSTIN = GIN.GSTIN AND GIN.REG_TYPE = 'ISD' ");
		data.append( " LEFT OUTER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID WHERE HDR.IS_ERROR='true' "
				+buildQuery+ " ORDER BY ITM.ID ) ");

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("gstr2 error report query{}",data);
			}
		return data.toString();
			}

		}
