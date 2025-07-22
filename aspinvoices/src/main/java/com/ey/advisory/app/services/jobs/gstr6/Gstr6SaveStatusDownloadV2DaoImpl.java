package com.ey.advisory.app.services.jobs.gstr6;

import java.text.NumberFormat;
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

import com.ey.advisory.app.data.views.client.Gstr6SaveStatusReportsRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr6SaveStatusDownloadReqDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ibm.icu.math.BigDecimal;

@Component("Gstr6SaveStatusDownloadV2DaoImpl")
public class Gstr6SaveStatusDownloadV2DaoImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6SaveStatusDownloadDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<Gstr6SaveStatusReportsRespDto> fetchGst6SaveSections(
			List<Gstr6SaveStatusDownloadReqDto> reqDtos) {
		List<Gstr6SaveStatusReportsRespDto> respDtos = Lists.newLinkedList();
		reqDtos.forEach(regDto -> {
			respDtos.addAll(fetchSaveSectionsDataByReq(regDto));
		});
		return respDtos;
	}

	private List<Gstr6SaveStatusReportsRespDto> fetchSaveSectionsDataByReq(
			Gstr6SaveStatusDownloadReqDto regDto) {
		// String status = regDto.getStatus();
		String gstin = regDto.getGstin();
		String taxperiod = regDto.getTaxPeriod();
		List<String> gstr6Sections = regDto.getGstr6Sections();

		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildQuery1 = new StringBuilder();
		// if (StringUtils.isNotEmpty(status)) {
		// buildQuery1.append(" CFS = :status");
		// }
		if (StringUtils.isNotEmpty(gstin)) {
			buildQuery.append(" AND HDR.GSTIN = :gstin");
		}
		if (CollectionUtils.isNotEmpty(gstr6Sections)) {
			buildQuery1.append(" where CATEGORY IN :gstr6Sections");
		}

		if (StringUtils.isNotBlank(taxperiod)
				&& StringUtils.isNotBlank(taxperiod)) {
			buildQuery.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod ");
		}
		String queryStr = creategstnTransQueryString(buildQuery.toString(),
				buildQuery1.toString());

		LOGGER.error("bufferString-------------------------->" + queryStr);
		Query outquery = entityManager.createNativeQuery(queryStr);

		if (StringUtils.isNotEmpty(gstin)) {
			outquery.setParameter("gstin", gstin);
		}
		if (StringUtils.isNotBlank(taxperiod)) {
			outquery.setParameter("taxperiod",
					GenUtil.convertTaxPeriodToInt(taxperiod));
		}
		// if (StringUtils.isNotBlank(status)) {
		// outquery.setParameter("status", status);
		// }
		if (CollectionUtils.isNotEmpty(gstr6Sections)) {
			outquery.setParameter("gstr6Sections", gstr6Sections);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = outquery.getResultList();
		return list.parallelStream().map(o -> convertTransactionalLevel(o))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	private Gstr6SaveStatusReportsRespDto convertTransactionalLevel(
			Object[] arr) {

		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumFractionDigits(2);

		Gstr6SaveStatusReportsRespDto obj = new Gstr6SaveStatusReportsRespDto();

		obj.setCategory(arr[0] != null ? arr[0].toString() : null);
		obj.setCounterPartyReturnStatus(
				arr[1] != null ? arr[1].toString() : null);
		obj.setDlinkFlag(arr[2] != null ? arr[2].toString() : null);
		obj.setReturnPeriod(arr[3] != null ? arr[3].toString() : null);
		obj.setRecipentGSTIN(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setDocumentNumber(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentDate(arr[7] != null ? arr[7].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[8] != null ? arr[8].toString() : null);
		obj.setOriginalDocumentDate(arr[9] != null ? arr[9].toString() : null);
		obj.setOriginalInvoiceNo(arr[10] != null ? arr[10].toString() : null);
		obj.setOriginalInvoiceDate(arr[11] != null ? arr[11].toString() : null);
		obj.setCRDRPreGST(arr[12] != null ? arr[12].toString() : null);

		obj.setLineNumber(arr[13] != null ? arr[13].toString() : null);
		obj.setSupplierGstin(arr[14] != null ? arr[14].toString() : null);
		obj.setPos(arr[15] != null ? arr[15].toString() : null);
		obj.setTaxableValue(arr[16] != null ? arr[16].toString() : null);

		obj.setTaxRate(arr[17] != null ? numberFormat
				.format(Double.parseDouble(arr[17].toString())).toString()
				: null);
		obj.setIntegratedTaxAmount(arr[18] != null ? arr[18].toString()
				: BigDecimal.ZERO.toString() + ".00");
		obj.setCentralTaxAmount(arr[19] != null ? arr[19].toString()
				: BigDecimal.ZERO.toString() + ".00");

		obj.setStateUTTaxAmount(arr[20] != null ? arr[20].toString()
				: BigDecimal.ZERO.toString() + ".00");
		obj.setCessAmount(arr[21] != null ? arr[21].toString()
				: BigDecimal.ZERO.toString() + ".00");
		obj.setInvoiceValue(arr[22] != null ? arr[22].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[23] != null ? arr[23].toString() : null);
		obj.setReverseChargeFlag(arr[24] != null ? arr[24].toString() : null);
		obj.setDifferentialPercentage(
				arr[25] != null ? arr[25].toString() : null);

		return obj;

	}

	private String creategstnTransQueryString(String buildQuery,
			String buildQuery1) {

		LOGGER.error("bufferString-------------------------->" + buildQuery);
		return " select CATEGORY, CFS,D_FLAG, TAX_PERIOD, REC_GSTIN, "
				+ "DOC_TYPE, DOC_NUMBER, DOC_DATE, ORG_DOC_NUM, ORG_DOC_DATE, "
				+ "ORG_INV_NUM, ORG_INV_DATE, CRDR_PERGST, ITM_NO, SUP_GSTIN,"
				+ "POS, TAXABLE_VALUE, TAX_RATE, IGST_AMT, CGST_AMT, SGST_AMT,"
				+ " CESS_AMT, DOC_AMT, REASON_CR_DE, REV_CHARGE, DIFF_PER from"
				+ " ( SELECT 'B2B' AS CATEGORY,HDR.CFS,TAX_PERIOD,GSTIN AS "
				+ "REC_GSTIN,'INV' AS DOC_TYPE, HDR.DOC_NUMBER,HDR.DOC_DATE,"
				+ "NULL AS ORG_DOC_NUM,NULL AS ORG_DOC_DATE, NULL AS "
				+ "ORG_INV_NUM,NULL AS ORG_INV_DATE,NULL AS CRDR_PERGST,"
				+ " ITM.ITM_NO,HDR.CTIN AS SUP_GSTIN,ITM.TAXABLE_VALUE,"
				+ "ITM.TAX_RATE, ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,"
				+ "ITM.CESS_AMT, HDR.DOC_AMT, NULL AS REASON_CR_DE, NULL AS "
				+ "REV_CHARGE, NULL AS DIFF_PER,NULL AS D_FLAG,NULL AS POS"
				+ " FROM  GETGSTR6_B2B_HEADER HDR INNER JOIN  "
				+ "GETGSTR6_B2B_ITEM ITM ON HDR.ID = ITM.HEADER_ID WHERE "
				+ "IS_DELETE = FALSE " + buildQuery
				+ " UNION SELECT 'B2BA' AS CATEGORY,HDR.CFS,TAX_PERIOD,"
				+ "GSTIN AS REC_GSTIN,'RNV' AS DOC_TYPE, HDR.DOC_NUMBER,"
				+ "HDR.DOC_DATE, ORG_DOC_NUMBER AS ORG_DOC_NUM , ORG_DOC_DATE, "
				+ "NULL AS ORG_INV_NUM,NULL AS ORG_INV_DATE,NULL AS"
				+ " CRDR_PERGST, ITM.ITM_NO,HDR.CTIN AS SUP_GSTIN,"
				+ "ITM.TAXABLE_VALUE,ITM.TAX_RATE, ITM.IGST_AMT,"
				+ "ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT, HDR.DOC_AMT,"
				+ " NULL AS REASON_CR_DE, NULL AS REV_CHARGE, NULL AS"
				+ " DIFF_PER,NULL AS D_FLAG,NULL AS POS FROM "
				+ " GETGSTR6_B2BA_HEADER HDR INNER JOIN  GETGSTR6_B2BA_ITEM "
				+ "ITM ON HDR.ID = ITM.HEADER_ID WHERE IS_DELETE = FALSE "
				+ buildQuery
				+ " UNION SELECT 'CDN' AS CATEGORY,NULL AS CFS,TAX_PERIOD,"
				+ "GSTIN AS REC_GSTIN,HDR.NOTE_TYPE AS DOC_TYPE, HDR.NOTE_NUM"
				+ " AS DOC_NUMBER,HDR.NOTE_DATE AS DOC_DATE, NULL AS "
				+ "ORG_DOC_NUM , NULL AS ORG_DOC_DATE, DOC_NUMBER AS"
				+ " ORG_INV_NUM,DOC_DATE AS ORG_INV_DATE,NULL AS CRDR_PERGST,"
				+ " ITM.ITM_NO,HDR.CTIN AS SUP_GSTIN,ITM.TAXABLE_VALUE,"
				+ "ITM.TAX_RATE, ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,"
				+ "ITM.CESS_AMT, HDR.DOC_AMT, NULL AS REASON_CR_DE, "
				+ "NULL AS REV_CHARGE, NULL AS DIFF_PER,D_FLAG,POS FROM "
				+ " GETGSTR6_CDN_HEADER HDR INNER JOIN  GETGSTR6_CDN_ITEM"
				+ " ITM ON HDR.ID = ITM.HEADER_ID WHERE IS_DELETE = FALSE "
				+ buildQuery
				+ " UNION SELECT 'CDNA' AS CATEGORY,NULL AS CFS,TAX_PERIOD,"
				+ "GSTIN AS REC_GSTIN,HDR.NOTE_TYPE AS DOC_TYPE, HDR.NOTE_NUM"
				+ " AS DOC_NUMBER,HDR.NOTE_DATE AS DOC_DATE, HDR.ORG_NOTE_NUM "
				+ "AS ORG_DOC_NUM , HDR.ORG_NOTE_DATE AS ORG_DOC_DATE, "
				+ "DOC_NUMBER AS ORG_INV_NUM,DOC_DATE AS ORG_INV_DATE,"
				+ "NULL AS CRDR_PERGST, ITM.ITM_NO,HDR.CTIN AS SUP_GSTIN,"
				+ "ITM.TAXABLE_VALUE,ITM.TAX_RATE, ITM.IGST_AMT,ITM.CGST_AMT,"
				+ "ITM.SGST_AMT,ITM.CESS_AMT, HDR.DOC_AMT, NULL AS"
				+ " REASON_CR_DE, NULL AS REV_CHARGE, NULL AS DIFF_PER,"
				+ "D_FLAG,POS  FROM  GETGSTR6_CDNA_HEADER HDR INNER JOIN "
				+ " GETGSTR6_CDNA_ITEM ITM ON HDR.ID = ITM.HEADER_ID WHERE "
				+ "IS_DELETE = FALSE " + buildQuery + ")" + buildQuery1;

	}
}
