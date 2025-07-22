/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr6;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr6aReportsDownloadRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr6SaveStatusDownloadReqDto;
import com.google.common.collect.Lists;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Gstr6aSaveStatusDownloadV2DaoImpl")
public class Gstr6aSaveStatusDownloadV2DaoImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr6SaveStatusDownloadDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<Gstr6aReportsDownloadRespDto> fetchGst6aSaveSections(List<Gstr6SaveStatusDownloadReqDto> reqDtos) {
		List<Gstr6aReportsDownloadRespDto> respDtos = Lists.newLinkedList();
		reqDtos.forEach(regDto -> {
			respDtos.addAll(fetchSaveSectionsDataByReq(regDto));
		});
		return respDtos;
	}

	private List<Gstr6aReportsDownloadRespDto> fetchSaveSectionsDataByReq(Gstr6SaveStatusDownloadReqDto regDto) {
		// String status = regDto.getStatus();
		String gstin = regDto.getGstin();
		String taxperiod = regDto.getTaxPeriod();
		List<String> gstr6aSections = regDto.getGstr6aSections();

		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildQuery1 = new StringBuilder();
		// if (StringUtils.isNotEmpty(status)) {
		// buildQuery1.append(" CFS = :status");
		// }
		if (StringUtils.isNotEmpty(gstin)) {
			buildQuery.append(" AND HDR.CTIN = :gstin");
		}
		if (CollectionUtils.isNotEmpty(gstr6aSections)) {
			buildQuery1.append(" where CATEGORY IN :gstr6aSections");
		}

		if (StringUtils.isNotBlank(taxperiod) && StringUtils.isNotBlank(taxperiod)) {
			buildQuery.append(" AND HDR.TAX_PERIOD = :taxperiod ");
		}
		String queryStr = creategstnTransQueryString(buildQuery.toString(), buildQuery1.toString());

		LOGGER.error("bufferString-------------------------->" + queryStr);
		Query outquery = entityManager.createNativeQuery(queryStr);

		if (StringUtils.isNotEmpty(gstin)) {
			outquery.setParameter("gstin", gstin);
		}
		if (StringUtils.isNotBlank(taxperiod)) {
			outquery.setParameter("taxperiod", taxperiod);
		}
		// if (StringUtils.isNotBlank(status)) {
		// outquery.setParameter("status", status);h
		// }
		if (CollectionUtils.isNotEmpty(gstr6aSections)) {
			outquery.setParameter("gstr6aSections", gstr6aSections);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = outquery.getResultList();
		return list.parallelStream().map(o -> convertTransactionalLevel(o))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	private Gstr6aReportsDownloadRespDto convertTransactionalLevel(Object[] arr) {
		Gstr6aReportsDownloadRespDto obj = new Gstr6aReportsDownloadRespDto();

		obj.setCounterPartyReturnStatus(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplierGstin(arr[3] != null ? arr[3].toString() : null);
		if(arr[4] != null){
			if(arr[4].toString().equals("C")){
				obj.setDocumentType("CR");
			}else if(arr[4].toString().equals("D")){
				obj.setDocumentType("DR");
			} else{
				obj.setDocumentType(arr[4] != null ? arr[4].toString() : null);
			}
		}
		obj.setTransactionType(arr[5] != null ? arr[5].toString() : null);
		obj.setDlinkFlag(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[7] != null ? arr[7].toString() : null);
		obj.setDocumentDate(arr[8] != null ? arr[8].toString() : null);
		obj.setLineNumber(arr[9] != null ? arr[9].toString() : null);
		obj.setRecipentGSTIN(arr[10] != null ? arr[10].toString() : null);
		obj.setOriginalDocumentNumber(arr[11] != null ? arr[11].toString() : null);
		obj.setOriginalDocumentDate(arr[12] != null ? arr[12].toString() : null);
		obj.setOriginalInvoiceNo(arr[13] != null ? arr[13].toString() : null);
		obj.setOriginalInvoiceDate(arr[14] != null ? arr[14].toString() : null);
		obj.setCrdrPreGST(arr[15] != null ? arr[15].toString() : null);
		obj.setPos(arr[16] != null ? arr[16].toString() : null);
		obj.setTaxableValue(arr[17] != null ? arr[17].toString() : null);
		obj.setTaxRate(arr[18] != null ? appendDecimalDigit((BigDecimal)arr[18]) : null);
		if(arr[19] != null){
			obj.setIntegratedTaxAmount(arr[19].toString());
		} else{
			obj.setIntegratedTaxAmount("0.00");
		}
		if(arr[20] != null){
			obj.setCentralTaxAmount(arr[20].toString());
		} else{
			obj.setCentralTaxAmount("0.00");
		}
		if(arr[21] != null){
			obj.setStateUTTaxAmount(arr[21].toString());
		} else{
			obj.setStateUTTaxAmount("0.00");
		}
		if(arr[22] != null){
			obj.setCessAmount(arr[22].toString());
		} else{
			obj.setCessAmount("0.00");
		}
		obj.setInvoiceValue(arr[23] != null ? arr[23].toString() : null);
		obj.setDifferentialPercentage(arr[24] != null ? arr[24].toString() : null);
		obj.setReasonForCreditDebitNote(arr[25] != null ? arr[25].toString() : null);
		obj.setReverseCharge(arr[26] != null ? arr[26].toString() : null);
		obj.setCFp(arr[27] != null ? arr[27].toString() : null);
		obj.setLegalName(arr[28] != null ? arr[28].toString() : null);
		obj.setTradeName(arr[29] != null ? arr[29].toString() : null);

		return obj;

	}

	private String creategstnTransQueryString(String buildQuery, String buildQuery1) {

		return " select CATEGORY,CFS,TAX_PERIOD,HDR.GSTIN,DOC_TYPE,TRANS_TYPE, D_FLAG, DOC_NUMBER,DOC_DATE,ITM_NO,SUPPLIER_GSTIN,ORG_DOC_NUM,ORG_DOC_DATE,ORG_INV_NO,ORG_INV_DATE, CRDR_PER_GST,POS,TAXABLE_VALUE,TAX_RATE, IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT, DOC_AMT, DIFF_PER,REASON_CRDR,RCHRG,CFP,LEGAL_NAME_BS,TRADE_NAME FROM ( SELECT 'B2B' AS CATEGORY,HDR.CFS,TAX_PERIOD,GSTIN , 'INV' AS DOC_TYPE,'B2B' AS TRANS_TYPE,'' as D_FLAG, HDR.DOC_NUMBER, HDR.DOC_DATE,ITM_NO, CTIN AS SUPPLIER_GSTIN,NULL AS ORG_DOC_NUM,NULL AS ORG_DOC_DATE, NULL AS ORG_INV_NO,NULL AS ORG_INV_DATE, NULL AS CRDR_PER_GST,HDR.POS,ITM.TAXABLE_VALUE,ITM.TAX_RATE, ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT, ITM.DOC_AMT, NULL AS DIFF_PER, NULL AS REASON_CRDR,REVERSE_CHARGE AS RCHRG,CFP from  GETGSTR6A_B2B_HEADER HDR INNER JOIN  GETGSTR6A_B2B_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND IS_DELETE = FALSE"
				+ buildQuery
				+ " UNION SELECT 'B2BA' AS CATEGORY,HDR.CFS,TAX_PERIOD,GSTIN , 'RNV' AS DOC_TYPE,'B2BA' AS TRANS_TYPE,'' as D_FLAG, HDR.DOC_NUMBER, HDR.DOC_DATE,ITM_NO, CTIN AS SUPPLIER_GSTIN, HDR. ORG_DOC_NUMBER,HDR.ORG_DOC_DATE, NULL AS ORG_INV_NO,NULL AS ORG_INV_DATE, NULL AS CRDR_PER_GST,HDR.POS,ITM.TAXABLE_VALUE,ITM.TAX_RATE, ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT, ITM.DOC_AMT, NULL AS DIFF_PER, NULL AS REASON_CRDR,REVERSE_CHARGE AS RCHRG,CFP from  GETGSTR6A_B2BA_HEADER HDR INNER JOIN  GETGSTR6A_B2BA_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND IS_DELETE = FALSE"
				+ buildQuery
				+ " UNION ALL SELECT 'CDN' AS CATEGORY,HDR.CFS,TAX_PERIOD,GSTIN , HDR.NOTE_TYPE,'CDN' AS TRANS_TYPE, D_FLAG, HDR.NOTE_NUM AS DOC_NUMBER, HDR.NOTE_DATE AS  DOC_DATE,ITM_NO, CTIN AS SUPPLIER_GSTIN, null as ORG_DOC_NUMBER,null as ORG_DOC_DATE, NULL AS ORG_INV_NO,NULL AS ORG_INV_DATE, NULL AS CRDR_PER_GST,HDR.POS,ITM.TAXABLE_VALUE,ITM.TAX_RATE, ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT, ITM.INV_VALUE as  DOC_AMT, NULL AS DIFF_PER, NULL AS REASON_CRDR,NULL AS RCHRG,CFP from  GETGSTR6A_CDN_HEADER HDR INNER JOIN  GETGSTR6A_CDN_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND IS_DELETE = FALSE"
				+ buildQuery
				+ " UNION SELECT 'CDNA' AS CATEGORY,HDR.CFS,TAX_PERIOD,GSTIN , HDR.NOTE_TYPE,'CDNA' AS TRANS_TYPE, D_FLAG, HDR.NOTE_NUM AS DOC_NUMBER,  HDR.NOTE_DATE  AS DOC_DATE,ITM_NO, CTIN AS SUPPLIER_GSTIN, ORG_NOTE_NUM  as ORG_DOC_NUMBER, ORG_NOTE_DATE as ORG_DOC_DATE,NULL AS ORG_INV_NO,NULL AS ORG_INV_DATE, NULL AS CRDR_PER_GST, HDR.POS,ITM.TAXABLE_VALUE,ITM.TAX_RATE, ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT, ITM.INV_VALUE as  DOC_AMT, NULL AS DIFF_PER, NULL AS REASON_CRDR,NULL AS RCHRG,CFP from  GETGSTR6A_CDNA_HEADER HDR INNER JOIN  GETGSTR6A_CDNA_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND IS_DELETE = FALSE"
				+ buildQuery + ")"
				
				+ " HDR LEFT JOIN (SELECT DISTINCT VENDOR_GSTIN AS GSTIN, "
				+ " LEGAL_NAME AS LEGAL_NAME_BS,TRADE_NAME FROM "
				+ " TBL_VENDOR_MASTER_CONFIG WHERE GSTIN_STATUS "
				+ " IN ('Active','Suspended','Cancelled') AND "
				+ " IS_ACTIVE = TRUE AND IS_FETCHED = TRUE ) "
				+ " VD ON HDR.GSTIN = VD.GSTIN"
				+ buildQuery1;
	}
	private String appendDecimalDigit(BigDecimal b) {

		String val = b.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();



		String[] s = val.split("\\.");
		if (s.length == 2) {
		if (s[1].length() == 1)
		return "'" + (s[0] + "." + s[1] + "0");
		else {
		return "'" + val;
		}
		} else
		return "'" + (val + ".00");



		}
}
