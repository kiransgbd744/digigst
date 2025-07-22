/**
 * 
 */
package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.EinvoiceProcessedReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author BalaKrishna S
 *
 */
@Slf4j
@Component("EinvoiceSummaryScreenSectionDaoImpl")
public class EinvoiceSummaryScreenSectionDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	
	public List<Gstr1SummarySectionDto> loadBasicSummarySection(
			EinvoiceProcessedReqDto request) {
		// TODO Auto-generated method stub

		List<Long> entityId = request.getEntityId();
		String taxPeriod1 = request.getTaxPeriodFrom();
		String taxPeriod2 = request.getTaxPeriodTo();
		List<String> gstinAsList = new ArrayList<>();
		
		List<String> tableType = request.getTableType();
	//	List<String> docType = gstr1ProcessedRecordsReqDto.getDocType();

		LocalDate docFromDate = request.getDocFromDate();
		LocalDate docToDate = request.getDocToDate();
		
		String docFromDateStr = null;
		if(docFromDate != null){
			 docFromDateStr = docFromDate.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		
		String docToDateStr = null;
		if(docToDate != null){
		 docToDateStr = docToDate.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		List<String> einvGenerated = request
				.getEINVGenerated();
		List<String> ewbGenerated = request
				.getEWBGenerated();
		
		List<String> draftedGSTN = request.getAutoDraftedGSTN();
		
		int taxPeriodFrom = 0;
		int taxPeriodTo = 0;
		if (taxPeriod1 != null && taxPeriod2 != null) {
			taxPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriod1);
			taxPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriod2);
		}
		
		String einvGen = null;
		if (einvGenerated != null && einvGenerated.size() > 0) {
			einvGen = einvGenerated.get(0);
		}

		String ewbResp = null;
		if (ewbGenerated != null && ewbGenerated.size() > 0) {
			ewbResp = ewbGenerated.get(0);
		}
		String draftGstn = null;
		if (draftedGSTN != null && draftedGSTN.size()>0){
			
			draftGstn = draftedGSTN.get(0);
			
		}
		
	
		Map<String, List<String>> dataSecAttrs = request
				.getDataSecAttrs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("EinvoiceSummaryScreenFetchDaoImpl->"
					+ "and selected criteria are:: entityId-> {}, "
					+ "gstins-> {}, " + "taxPeriod-> {}, dataSecAttrs -> {}",
					request);
		}
	
		List<String> gstinList = new ArrayList<>();
		String sgstin = null;
			if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					sgstin = key;
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}
		StringBuilder queryBuilderB2bExp = new StringBuilder();
		StringBuilder queryBuilderCdnrCdnur = new StringBuilder();
		StringBuilder queryBuilderTable = new StringBuilder();
		StringBuilder queryBuilderOutward = new StringBuilder();
		
		
		if (taxPeriodFrom != 0 && taxPeriodTo != 0) {
			queryBuilderB2bExp.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");
			queryBuilderCdnrCdnur.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");
			queryBuilderOutward.append(" AND DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");
		}
		if (docFromDate != null && docToDate != null) {
			queryBuilderOutward.append(
					" AND DOC_DATE BETWEEN :docFromDate AND :docToDate ");
		}
		if (docFromDateStr != null && docToDateStr != null) {
			queryBuilderB2bExp.append(
					" AND INV_DATE BETWEEN :docFromDateStr AND :docToDateStr ");
			queryBuilderCdnrCdnur.append(
					" AND NOTE_DATE BETWEEN :docFromDateStr AND :docToDateStr ");
			
		}
		if (gstinList != null && gstinList.size() > 0) {
			queryBuilderB2bExp.append(" AND HDR.GSTIN IN (:gstinList) ");
			queryBuilderCdnrCdnur.append(" AND HDR.GSTIN IN (:gstinList) ");
			queryBuilderOutward.append(" AND SUPPLIER_GSTIN IN (:gstinList) ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("YES")) {
			queryBuilderB2bExp.append("AND EINV_STATUS IN ('ACT') ");
			queryBuilderCdnrCdnur.append("AND EINV_STATUS IN ('ACT') ");
			queryBuilderOutward.append("AND IRN_RESPONSE IS NOT NULL AND EINV_STATUS IN (7,10,11) ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("NO")) {
			queryBuilderB2bExp.append("  AND EINV_STATUS NOT IN ('ACT') ");
			queryBuilderCdnrCdnur.append("  AND EINV_STATUS NOT IN ('ACT') ");
			queryBuilderOutward.append("  AND IRN_RESPONSE IS NULL AND EINV_STATUS IN (5) ");
			
		}
		if (draftGstn != null && draftGstn.equalsIgnoreCase("YES")) {
			queryBuilderB2bExp.append("AND AUTODFT IN ('Auto-populated') ");
			queryBuilderCdnrCdnur.append("AND AUTODFT IN ('Auto-populated') ");
		}
		if (draftGstn != null && draftGstn.equalsIgnoreCase("NO")) {
			queryBuilderB2bExp.append(" AND AUTODFT NOT IN ('Auto-populated') ");
			queryBuilderCdnrCdnur.append(" AND AUTODFT NOT IN ('Auto-populated') ");
		}
		if (tableType != null && tableType.size() > 0) {
		//	queryBuilder.append(" AND TAX_DOC_TYPE IN (:tableType) ");
			queryBuilderTable.append(" WHERE TAX_DOC_TYPE IN (:tableType) ");
		}

	
		String buildQueryb2bExp = queryBuilderB2bExp.toString().substring(4);
		String buildQueryCdn = queryBuilderCdnrCdnur.toString().substring(4);
		String buildQueryOut = queryBuilderOutward.toString().substring(4);
		String buildQuerytable = queryBuilderTable.toString();
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");
		}

		String queryStr = createQueryString(buildQueryb2bExp,buildQueryCdn,buildQuerytable,buildQueryOut);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}
		

	

		try {
		//	Query q = entityManager.createNativeQuery(queryStr);

			Query Q = entityManager.createNativeQuery(queryStr);
			if (taxPeriodFrom != 0 && taxPeriodTo != 0) {
				Q.setParameter("taxPeriodFrom", taxPeriodFrom);
				Q.setParameter("taxPeriodTo", taxPeriodTo);
			}
			if (gstinList != null && gstinList.size() > 0
					&& !gstinList.contains("")) {
				Q.setParameter("gstinList", gstinList);
			}
			if (tableType != null && tableType.size() > 0
					&& !tableType.contains("")) {
				Q.setParameter("tableType", tableType);
			}
		/*	if (docType != null && docType.size() > 0
					&& !docType.contains("")) {
				Q.setParameter("docType", docType);
			}*/
			if (docFromDate != null && docToDate != null) {
				Q.setParameter("docFromDate", docFromDate);
				Q.setParameter("docToDate", docToDate);
			}
			
			if (docFromDateStr != null && docToDateStr != null) {
				
				Q.setParameter("docFromDateStr", docFromDateStr);
				Q.setParameter("docToDateStr", docToDateStr);
				
			}
			List<Object[]> list = Q.getResultList();
			if(LOGGER.isDebugEnabled()){
			LOGGER.debug("ResultList data Converting to Dto");
			}
			List<Gstr1SummarySectionDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			if(LOGGER.isDebugEnabled()){
			LOGGER.debug("After Execution getting the data ----->" + retList);
			}
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr1SummarySectionDto convert(Object[] arr) {
		Gstr1SummarySectionDto obj = new Gstr1SummarySectionDto();
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Array data Setting to Dto");
		}
		obj.setTaxDocType((String) arr[0]);
		obj.setAspCount((GenUtil.getBigInteger(arr[1])).intValue());
		obj.setAspInvoiceValue((BigDecimal) arr[2]);
		obj.setAspTaxableValue((BigDecimal) arr[3]);
		obj.setAspTaxPayble((BigDecimal) arr[4]);
		obj.setAspIgst((BigDecimal) arr[5]);
		obj.setAspCgst((BigDecimal) arr[6]);
		obj.setAspSgst((BigDecimal) arr[7]);
		obj.setAspCess((BigDecimal) arr[8]);
		obj.setGstnCount((GenUtil.getBigInteger(arr[9])).intValue());
		obj.setGstnInvoiceValue((BigDecimal) arr[10]);
		obj.setGstnTaxableValue((BigDecimal) arr[11]);
		obj.setGstnTaxPayble((BigDecimal) arr[12]);
		obj.setGstnIgst((BigDecimal) arr[13]);
		obj.setGstnCgst((BigDecimal) arr[14]);
		obj.setGstnSgst((BigDecimal) arr[15]);
		obj.setGstnCess((BigDecimal) arr[16]);
		
		return obj;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString(String buildQueryb2bExp,String buildQueryCdn,String buildWheretable,String buildQueryOut) {
		// TODO Auto-generated method stub
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Outward Query Execution BEGIN ");
		}

		String queryStr = "SELECT TAX_DOC_TYPE,SUM(DG_RECORD_COUNT)DG_RECORD_COUNT,"
				+ "SUM(DG_INVOICE_VALUE) DG_INVOICE_VALUE,"
				+ "SUM(DG_ASSESSABLE_AMT) DG_ASSESSABLE_AMT,"
				+ "SUM(DG_TOT_TAX) DG_TOT_TAX,SUM(DG_IGST_AMT) DG_IGST_AMT,"
				+ "SUM(DG_CGST_AMT) DG_CGST_AMT,"
				+ "SUM(DG_SGST_AMT) DG_SGST_AMT,SUM(DG_CESS_AMT) DG_CESS_AMT,"
				+ "SUM(AD_RECORD_COUNT)AD_RECORD_COUNT,"
				+ "SUM(AD_INVOICE_VALUE) AD_INVOICE_VALUE,"
				+ "SUM(AD_ASSESSABLE_AMT) AD_ASSESSABLE_AMT,"
				+ "SUM(AD_TOT_TAX) AD_TOT_TAX,SUM(AD_IGST_AMT) AD_IGST_AMT,"
				+ "SUM(AD_CGST_AMT) AD_CGST_AMT,"
				+ "SUM(AD_SGST_AMT) AD_SGST_AMT,SUM(AD_CESS_AMT) AD_CESS_AMT "
				+ "FROM ( SELECT SUPPLIER_GSTIN,DERIVED_RET_PERIOD,TAX_DOC_TYPE,"
				+ "DG_RECORD_COUNT,DG_INVOICE_VALUE,DG_ASSESSABLE_AMT,"
				+ "DG_TOT_TAX,DG_IGST_AMT,DG_CGST_AMT,DG_SGST_AMT,DG_CESS_AMT,"
				+ "0 AD_RECORD_COUNT,0 AD_INVOICE_VALUE,"
				+ "0 AD_ASSESSABLE_AMT,0 AD_TOT_TAX,0 AD_IGST_AMT,"
				+ "0 AD_CGST_AMT,0 AD_SGST_AMT,0 AD_CESS_AMT "
				+ "FROM ( SELECT SUPPLIER_GSTIN,DERIVED_RET_PERIOD,"
				+ "TAX_DOC_TYPE,SUM(RECORD_COUNT) DG_RECORD_COUNT,"
				+ "SUM(DOC_AMT) DG_INVOICE_VALUE,"
				+ "SUM(TAXABLE_VALUE) DG_ASSESSABLE_AMT,"
				+ "SUM(TAX_PAYABLE) DG_TOT_TAX,SUM(IGST_AMT) DG_IGST_AMT,"
				+ "SUM(CGST_AMT) DG_CGST_AMT,"
				+ "SUM(SGST_AMT) DG_SGST_AMT,SUM(CESS) DG_CESS_AMT "
				+ "FROM ( SELECT SUPPLIER_GSTIN,DERIVED_RET_PERIOD,"
				+ "TAX_DOC_TYPE TAX_DOC_TYPE,"
				+ "COUNT(DISTINCT ID) AS RECORD_COUNT,"
				+ "IFNULL(SUM(DOC_AMT),0) AS DOC_AMT,"
				+ "IFNULL(SUM(TAXABLE_VALUE),0)  AS TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0)) AS TAX_PAYABLE,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
				+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,SUM(IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0)) AS CESS "
				+ "FROM ANX_OUTWARD_DOC_HEADER "
				+ "WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE "
				+ "AND IS_DELETE = FALSE AND RETURN_TYPE='GSTR1' "
				+ "AND TAX_DOC_TYPE IN ('B2B','EXPORTS') AND "
				+ buildQueryOut
                + "GROUP BY TAX_DOC_TYPE,SUPPLIER_GSTIN,DERIVED_RET_PERIOD "
                + "UNION ALL "
                + "SELECT SUPPLIER_GSTIN,DERIVED_RET_PERIOD,TAX_DOC_TYPE,"
                + "COUNT(*) AS RECORD_COUNT,"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN DOC_AMT END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN DOC_AMT END),0) "
                + "AS DOC_AMT,"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN TAXABLE_VALUE END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN TAXABLE_VALUE END),0) AS TAXABLE_VALUE,"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN "
                + "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
                + "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
                + "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
                + "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0) "
                + "AS TAX_PAYABLE,IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN IGST_AMT END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN IGST_AMT END),0) "
                + "AS IGST_AMT,IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN CGST_AMT END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN CGST_AMT END),0) "
                + "AS CGST_AMT,IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN SGST_AMT END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN SGST_AMT END),0) "
                + "AS SGST_AMT,IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN "
                + "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
                + "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0) "
                + "AS CESS FROM ANX_OUTWARD_DOC_HEADER "
                + "WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE "
                + "AND IS_DELETE = FALSE "
                + "AND RETURN_TYPE='GSTR1' AND TAX_DOC_TYPE IN ('CDNR','CDNUR','CDNUR-EXPORTS','CDNUR-B2CL') AND "
                + buildQueryOut 
                + "GROUP BY TAX_DOC_TYPE,SUPPLIER_GSTIN,DERIVED_RET_PERIOD ) "
                + "GROUP BY TAX_DOC_TYPE,SUPPLIER_GSTIN,DERIVED_RET_PERIOD) "
                + "UNION ALL "
                + "SELECT SUPPLIER_GSTIN,DERIVED_RET_PERIOD,TAX_DOC_TYPE,"
                + "0 DG_RECORD_COUNT,0 DG_INVOICE_VALUE,0 DG_ASSESSABLE_AMT,"
                + "0 DG_TOT_TAX,0 DG_TOT_TAX,0 DG_CGST_AMT,0 DG_SGST_AMT,"
                + "0 DG_CESS_AMT,AD_RECORD_COUNT,AD_INVOICE_VALUE,"
                + "AD_ASSESSABLE_AMT,AD_TOT_TAX,AD_IGST_AMT,AD_CGST_AMT,"
                + "AD_SGST_AMT,AD_CESS_AMT "
                + "FROM ( SELECT SUPPLIER_GSTIN,DERIVED_RET_PERIOD,"
                + "TAX_DOC_TYPE,SUM(COUNT)AD_RECORD_COUNT,"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN INVOICE_VALUE END),0)+"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D') THEN INVOICE_VALUE END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C') THEN INVOICE_VALUE END),0) AS AD_INVOICE_VALUE,"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN TAXABLE_VALUE END),0)+"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D') THEN TAXABLE_VALUE END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C') THEN TAXABLE_VALUE END),0) AS AD_ASSESSABLE_AMT,"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN TAX_PAYABLE END),0)+"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D') THEN TAX_PAYABLE END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C') THEN TAX_PAYABLE END),0) AS AD_TOT_TAX,"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN IGST_AMT END),0)+"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D') THEN IGST_AMT END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C') THEN IGST_AMT END),0) AS AD_IGST_AMT,"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN CGST_AMT END),0)+"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D') THEN CGST_AMT END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C') THEN CGST_AMT END),0) AS AD_CGST_AMT,"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN SGST_AMT END),0)+"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D') THEN SGST_AMT END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C') THEN SGST_AMT END),0) AS AD_SGST_AMT,"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN CESS_AMT END),0)+"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D') THEN CESS_AMT END),0)-"
                + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C') THEN CESS_AMT END),0) AS AD_CESS_AMT "
                + "FROM ( SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
                + "HDR.DERIVED_RET_PERIOD,'B2B' AS TAX_DOC_TYPE,'INV' AS DOC_TYPE,"
                + "INV_DATE,EINV_STATUS,AUTODFT,COUNT( DISTINCT HDR.ID) AS COUNT,"
                + "IFNULL(SUM(HDR.INV_VALUE),0) AS INVOICE_VALUE,"
                + "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
                + "SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+"
                + "IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)) AS TAX_PAYABLE,"
                + "IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT,"
                + "IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT,"
                + "IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT,"
                + "IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT "
                + "FROM GETGSTR1_EINV_B2B_HEADER HDR "
                + "INNER JOIN  GETGSTR1_EINV_B2B_ITEM ITM "
                + "ON HDR.ID = ITM.HEADER_ID "
                + "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
                + "LEFT OUTER JOIN GETANX1_BATCH_TABLE BT "
                + "ON HDR.BATCH_ID = BT.ID "
                + "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
                + "AND BT.GET_TYPE IN ('B2B','WEB_UPLOAD') AND "
                + buildQueryb2bExp
                + "GROUP BY HDR.GSTIN,HDR.RETURN_PERIOD,"
                + "HDR.DERIVED_RET_PERIOD,INV_DATE,EINV_STATUS,AUTODFT "
                + "UNION ALL "
                + "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
                + "HDR.DERIVED_RET_PERIOD,"
                + "'EXPORTS' AS TAX_DOC_TYPE,'INV' AS DOC_TYPE,INV_DATE,EINV_STATUS,AUTODFT,"
                + "COUNT( DISTINCT HDR.ID) AS COUNT,"
                + "IFNULL(SUM(HDR.INV_VALUE),0) AS INVOICE_VALUE,"
                + "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
                + "SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)) AS TAX_PAYABLE,"
                + "IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT,0 AS CGST_AMT,"
                + "0 AS SGST_AMT,IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT "
                + "FROM GETGSTR1_EINV_EXP_HEADER HDR "
                + "INNER JOIN  GETGSTR1_EINV_EXP_ITEM ITM "
                + "ON HDR.ID = ITM.HEADER_ID AND "
                + "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
                + "LEFT OUTER JOIN GETANX1_BATCH_TABLE BT "
                + "ON HDR.BATCH_ID = BT.ID "
                + "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
                + "AND BT.GET_TYPE  IN ('EXP','WEB_UPLOAD') AND "
                + buildQueryb2bExp                       
                + "GROUP BY HDR.GSTIN,HDR.RETURN_PERIOD,"
                + "HDR.DERIVED_RET_PERIOD,INV_DATE,EINV_STATUS,AUTODFT "
                + "UNION ALL "
                + "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
                + "HDR.DERIVED_RET_PERIOD,"
                + "'CDNR' AS TAX_DOC_TYPE,NOTE_TYPE AS DOC_TYPE,"
                + "NOTE_DATE AS INV_DATE,EINV_STATUS,AUTODFT,"
                + "COUNT( DISTINCT HDR.ID) AS COUNT,"
                + "IFNULL(SUM(HDR.INV_VALUE),0) AS INVOICE_VALUE,"
                + "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
                + "SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+"
                + "IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)) AS TAX_PAYABLE,"
                + "IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT,"
                + "IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT,"
                + "IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT,"
                + "IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT "
                + "FROM GETGSTR1_EINV_CDNR_HEADER HDR "
                + "INNER JOIN  GETGSTR1_EINV_CDNR_ITEM ITM "
                + "ON HDR.ID = ITM.HEADER_ID AND "
                + "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
                + "LEFT OUTER JOIN GETANX1_BATCH_TABLE BT "
                + "ON HDR.BATCH_ID = BT.ID "
                + "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
                + "AND BT.GET_TYPE IN ('CDNR','WEB_UPLOAD') AND "
                + buildQueryCdn 
                + "GROUP BY HDR.GSTIN,HDR.RETURN_PERIOD,"
                + "HDR.DERIVED_RET_PERIOD,NOTE_DATE,EINV_STATUS,AUTODFT ,NOTE_TYPE "
                + "UNION ALL "
                + "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
                + "HDR.DERIVED_RET_PERIOD,"
                + "'CDNUR' AS TAX_DOC_TYPE,NOTE_TYPE AS DOC_TYPE,"
                + "NOTE_DATE AS INV_DATE,EINV_STATUS,AUTODFT,COUNT( DISTINCT HDR.ID) AS COUNT,"
                + "IFNULL(SUM(HDR.INV_VALUE),0) AS INVOICE_VALUE,"
                + "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
                + "SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)) AS TAX_PAYABLE,"
                + "IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT,0 AS CGST_AMT,"
                + "0 AS SGST_AMT,IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT "
                + "FROM GETGSTR1_EINV_CDNUR_HEADER HDR "
                + "INNER JOIN  GETGSTR1_EINV_CDNUR_ITEM ITM "
                + "ON HDR.ID = ITM.HEADER_ID "
                + "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
                + "LEFT OUTER JOIN GETANX1_BATCH_TABLE BT "
                + "ON HDR.BATCH_ID = BT.ID "
                + "WHERE HDR.IS_DELETE = FALSE "
                + "AND BT.IS_DELETE = FALSE AND BT.GET_TYPE  IN ('CDNUR','WEB_UPLOAD') AND "
                + buildQueryCdn 
                + "GROUP BY HDR.GSTIN,HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,"
                + "NOTE_DATE,EINV_STATUS,AUTODFT,NOTE_TYPE) "
                + "GROUP BY SUPPLIER_GSTIN,DERIVED_RET_PERIOD,TAX_DOC_TYPE )) "
                + buildWheretable
                + "GROUP BY TAX_DOC_TYPE";
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Outward FROM B2B TO EXPA Query Execution END ");
		}
		return queryStr;
	}
}
