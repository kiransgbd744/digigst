/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.HsnOrSacMasterEntity;
import com.ey.advisory.admin.data.repositories.master.HsnOrSacRepository;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.views.client.HsnProcessSubmitdto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 * 
 */
@Component("AspProcessVsSubmitHsnReportDaoImpl")
@Slf4j
public class AspProcessVsSubmitHsnReportDaoImpl
		implements AspProcessVsSubmitDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("HsnOrSacRepositoryMaster")
	private HsnOrSacRepository hsnOrSacRepository;
	
	@Autowired
	@Qualifier("GstnApi")
	GstnApi gstnApi;
	
	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;
	
	private static final String DOC_KEY_JOINER = "|";

	@Override
	public List<Object> aspProcessVsSubmitDaoReports(SearchCriteria criteria) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		String taxPeriodFrom = request.getTaxPeriodFrom();
		String taxPeriodTo = request.getTaxPeriodTo();
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String GSTIN = null;
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
				}}
		}

		StringBuilder buildHeader = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeader.append(" WHERE SUPPLIER_GSTIN IN :gstinList ");

			}
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			buildHeader.append(" AND DERIVED_RET_PERIOD BETWEEN ");
			buildHeader.append(":taxPeriodFrom AND :taxPeriodTo ");

		}
		Boolean rateIncludedInHsn = gstnApi.isRateIncludedInHsn(taxPeriodFrom);
		
		String queryStr = createApiProcessedQueryString(buildHeader.toString(),rateIncludedInHsn);
		
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("HSN Rate Level Query {} ",queryStr);
		}
		List<Object> verticalHsnList = Lists.newArrayList();
		try {
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriodFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriodTo());
			q.setParameter("taxPeriodFrom", derivedRetPeriodFrom);
			q.setParameter("taxPeriodTo", derivedRetPeriodTo);
		}
		
		List<Object[]> list = q.getResultList();
		
		if (CollectionUtils.isNotEmpty(list)) {
			List<HsnOrSacMasterEntity> hsnOrSacMasterEntities = hsnOrSacRepository
					.findAll();
			Map<String, String> hsnMap = new HashMap<String, String>();
			hsnOrSacMasterEntities.forEach(entity -> {
				hsnMap.put(entity.getHsnSac(), entity.getDescription());
			});
			ProcessingContext context = new ProcessingContext();
			settingFiledGstins(context);
			for (Object arr[] : list) {
				verticalHsnList.add(convertProcessed(arr, hsnMap,rateIncludedInHsn, context));
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("While Fetching HSN Transational Data getting Error ",
					e);
		}
		return verticalHsnList;
	}

	private HsnProcessSubmitdto convertProcessed(Object[] arr,
			Map<String, String> hsnMap,Boolean rateIncludedInHsn, ProcessingContext context) {
		HsnProcessSubmitdto obj = new HsnProcessSubmitdto();
		
	
		if(rateIncludedInHsn){
			
			
			obj.setsNo(arr[0] != null ? arr[0].toString() : null);
			obj.setSupplierGSTIN(arr[1] != null ? arr[1].toString() : null);
			obj.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
			String hsnDesc = null;
			String hsnCode = (arr[3] != null ? arr[3].toString() : null);
			obj.setHsn(hsnCode);

			if (!Strings.isNullOrEmpty(hsnCode)) {
				hsnDesc = hsnMap.get(hsnCode);
			}
			obj.setDescription(hsnDesc);
			obj.setTaxRate(arr[5] != null ? arr[5].toString() : null);
			obj.setUqc(arr[6] != null ? arr[6].toString() : null);
			obj.setTotalQuantity(arr[7] != null ? arr[7].toString() : null);
			obj.setTaxableValue(arr[8] != null ? arr[8].toString() : null);
			obj.setIgstAmount(arr[9] != null ? arr[9].toString() : null);
			obj.setCgstAmount(arr[10] != null ? arr[10].toString() : null);
			obj.setsGSTUTGSTAmount(arr[11] != null ? arr[11].toString() : null);
			obj.setCessAmount(arr[12] != null ? arr[12].toString() : null);
			obj.setTotalValue(arr[13] != null ? arr[13].toString() : null);

			obj.setIsFiled(isGstinTaxperiodFiled(context,arr[1].toString(), arr[2].toString()));
			/*
			 * obj.setGstnSupplierGSTIN(arr[15] != null ? arr[15].toString() :
			 * null); obj.setGstnReturnPeriod(arr[16] != null ? arr[16].toString() :
			 * null);
			 */
			String hsnDes = null;
			String hsnCod = (arr[15] != null ? arr[15].toString() : null);
			obj.setGstnHsn(hsnCod);

			if (!Strings.isNullOrEmpty(hsnCod)) {
				hsnDes = hsnMap.get(hsnCod);
			}
			obj.setGstnDescription(hsnDes);
			obj.setGstnTaxRate(arr[17] != null ? arr[17].toString() : null);
			obj.setGstnUqc(arr[18] != null ? arr[18].toString() : null);
			obj.setGstnTotalQuantity(arr[19] != null ? arr[19].toString() : null);
			obj.setGstnTaxableValue(arr[20] != null ? arr[20].toString() : null);
			obj.setGstnIgstAmount(arr[21] != null ? arr[21].toString() : null);
			obj.setGstnCgstAmount(arr[22] != null ? arr[22].toString() : null);
			obj.setGstnSGSTUTGSTAmount(arr[23] != null ? arr[23].toString() : null);
			obj.setGstnCessAmount(arr[24] != null ? arr[24].toString() : null);
			obj.setGstnTotalValue(arr[25] != null ? arr[25].toString() : null);
			
		
			String uiHsnDes = null;
			String uiHsnCod = (arr[26] != null ? arr[26].toString() : null);
			obj.setUiHsn(uiHsnCod);

			if (!Strings.isNullOrEmpty(uiHsnCod)) {
				uiHsnDes = hsnMap.get(uiHsnCod);
			}
			obj.setUiDescription(uiHsnDes);
			obj.setUiTaxRate(arr[28] != null ? arr[28].toString() : null);
			obj.setUiUqc(arr[29] != null ? arr[29].toString() : null);
			obj.setUiTotalQuantity(arr[30] != null ? arr[30].toString() : null);
			obj.setUiTaxableValue(arr[31] != null ? arr[31].toString() : null);
			
			obj.setUiIgstAmount(arr[32] != null ? arr[32].toString() : null);
			obj.setUiCgstAmount(arr[33] != null ? arr[33].toString() : null);
			obj.setUiSGSTUTGSTAmount(arr[34] != null ? arr[34].toString() : null);
			obj.setUiCessAmount(arr[35] != null ? arr[35].toString() : null);
			obj.setUiTotalValue(arr[36] != null ? arr[36].toString() : null);
			
			obj.setDiffTaxableValue(arr[37] != null ? arr[37].toString() : null);
			obj.setDiffIgstAmount(arr[38] != null ? arr[38].toString() : null);
			obj.setDiffCgstAmount(arr[39] != null ? arr[39].toString() : null);
			obj.setDiffSGSTUTGSTAmount(arr[40] != null ? arr[40].toString() : null);
			obj.setDiffCessAmount(arr[41] != null ? arr[41].toString() : null);
			
			
			
			obj.setSaveStatus(arr[42] != null ? arr[42].toString() : null);
			obj.setgSTNRefID(arr[43] != null ? arr[43].toString() : null);
			obj.setgSTNRefIDTime(arr[44] != null ? arr[44].toString() : null);
			obj.setgSTNErrorcode(arr[45] != null ? arr[45].toString() : null);
			obj.setgSTNErrorDescription(
					arr[46] != null ? arr[46].toString() : null);
			
			
		}else{
	
		obj.setsNo(arr[0] != null ? arr[0].toString() : null);
		obj.setSupplierGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
		String hsnDesc = null;
		String hsnCode = (arr[3] != null ? arr[3].toString() : null);
		obj.setHsn(hsnCode);

		if (!Strings.isNullOrEmpty(hsnCode)) {
			hsnDesc = hsnMap.get(hsnCode);
		}
		obj.setDescription(hsnDesc);
		obj.setUqc(arr[5] != null ? arr[5].toString() : null);
		obj.setTotalQuantity(arr[6] != null ? arr[6].toString() : null);
		obj.setTaxableValue(arr[7] != null ? arr[7].toString() : null);
		obj.setIgstAmount(arr[8] != null ? arr[8].toString() : null);
		obj.setCgstAmount(arr[9] != null ? arr[9].toString() : null);
		obj.setsGSTUTGSTAmount(arr[10] != null ? arr[10].toString() : null);
		obj.setCessAmount(arr[11] != null ? arr[11].toString() : null);
		obj.setTotalValue(arr[12] != null ? arr[12].toString() : null);
		//obj.setIsFiled(arr[13] != null ? arr[13].toString() : null);
		obj.setIsFiled(isGstinTaxperiodFiled(context,arr[1].toString(), arr[2].toString()));
		/*
		 * obj.setGstnSupplierGSTIN(arr[15] != null ? arr[15].toString() :
		 * null); obj.setGstnReturnPeriod(arr[16] != null ? arr[16].toString() :
		 * null);
		 */
		String hsnDes = null;
		String hsnCod = (arr[14] != null ? arr[14].toString() : null);
		obj.setGstnHsn(hsnCod);

		if (!Strings.isNullOrEmpty(hsnCod)) {
			hsnDes = hsnMap.get(hsnCod);
		}
		obj.setGstnDescription(hsnDes);
		obj.setGstnUqc(arr[16] != null ? arr[16].toString() : null);
		obj.setGstnTotalQuantity(arr[17] != null ? arr[17].toString() : null);
		obj.setGstnTaxableValue(arr[18] != null ? arr[18].toString() : null);
		obj.setGstnIgstAmount(arr[19] != null ? arr[19].toString() : null);
		obj.setGstnCgstAmount(arr[20] != null ? arr[20].toString() : null);
		obj.setGstnSGSTUTGSTAmount(arr[21] != null ? arr[21].toString() : null);
		obj.setGstnCessAmount(arr[22] != null ? arr[22].toString() : null);
		obj.setGstnTotalValue(arr[23] != null ? arr[23].toString() : null);
		
	
		String uiHsnDes = null;
		String uiHsnCod = (arr[24] != null ? arr[24].toString() : null);
		obj.setUiHsn(uiHsnCod);

		if (!Strings.isNullOrEmpty(uiHsnCod)) {
			uiHsnDes = hsnMap.get(uiHsnCod);
		}
		obj.setUiDescription(uiHsnDes);
		
		obj.setUiUqc(arr[26] != null ? arr[26].toString() : null);
		obj.setUiTotalQuantity(arr[27] != null ? arr[27].toString() : null);
		obj.setUiTaxableValue(arr[28] != null ? arr[28].toString() : null);
		
		obj.setUiIgstAmount(arr[29] != null ? arr[29].toString() : null);
		obj.setUiCgstAmount(arr[30] != null ? arr[30].toString() : null);
		obj.setUiSGSTUTGSTAmount(arr[31] != null ? arr[31].toString() : null);
		obj.setUiCessAmount(arr[32] != null ? arr[32].toString() : null);
		obj.setUiTotalValue(arr[33] != null ? arr[33].toString() : null);
		
		obj.setDiffTaxableValue(arr[34] != null ? arr[34].toString() : null);
		obj.setDiffIgstAmount(arr[35] != null ? arr[35].toString() : null);
		obj.setDiffCgstAmount(arr[36] != null ? arr[36].toString() : null);
		obj.setDiffSGSTUTGSTAmount(arr[37] != null ? arr[37].toString() : null);
		obj.setDiffCessAmount(arr[38] != null ? arr[38].toString() : null);
		
		
		
		obj.setSaveStatus(arr[39] != null ? arr[39].toString() : null);
		obj.setgSTNRefID(arr[40] != null ? arr[40].toString() : null);
		obj.setgSTNRefIDTime(arr[41] != null ? arr[41].toString() : null);
		obj.setgSTNErrorcode(arr[42] != null ? arr[42].toString() : null);
		obj.setgSTNErrorDescription(
				arr[43] != null ? arr[43].toString() : null);
	}

		return obj;
	}

	private String createApiProcessedQueryString(String buildHeader,Boolean rateIncludedInHsn) {
		StringBuilder build = new StringBuilder();
		
		if(rateIncludedInHsn){
			
			build.append("SELECT ROW_NUMBER () OVER ( ORDER BY A.SUPPLIER_GSTIN ) SNO,"); 
			build.append("A.SUPPLIER_GSTIN,A.RETURN_PERIOD,A.GSTN_ITM_HSNSAC,"); 
			build.append("A.GSTN_ITM_DESCRIPTION,A.GSTN_TAX_RATE,A.GSTN_ITM_UQC,"); 
			build.append("sum(A.GSTN_ITM_QTY),sum(A.GSTN_TAXABLE_VALUE),sum(A.GSTN_IGST_AMT),"); 
			build.append("sum(A.GSTN_CGST_AMT),sum(A.GSTN_SGST_AMT),sum(A.GSTN_CESS_AMT),"); 
			build.append("sum(A.GSTN_TOT_VAL),A.IS_FILED,A.DG_ITM_HSNSAC,A.DG_ITM_DESCRIPTION,"); 
			build.append("A.DG_TAX_RATE,A.DG_ITM_UQC,sum(A.DG_ITM_QTY),sum(A.DG_TAXABLE_VALUE),"); 
			build.append("sum(A.DG_IGST_AMT),sum(A.DG_CGST_AMT),sum(A.DG_SGST_AMT),"); 
			build.append("sum(A.DG_CESS_AMT),sum(A.DG_TOT_VAL), A.UI_ITM_HSNSAC,A.UI_ITM_DESCRIPTION,"); 
			build.append("A.UI_TAX_RATE,A.UI_ITM_UQC,sum(A.UI_ITM_QTY),sum(A.UI_TAXABLE_VALUE),"); 
			build.append("sum(A.UI_IGST_AMT),sum(A.UI_CGST_AMT),sum(A.UI_SGST_AMT),sum(A.UI_CESS_AMT),"); 
			build.append("sum(A.UI_TOT_VAL),sum(A.DIFF_TAXABLE_VALUE),sum(A.DIFF_IGST_AMT),"); 
			build.append("sum(A.DIFF_CGST_AMT),sum(A.DIFF_SGST_AMT),sum(A.DIFF_CESS_AMT),"); 
			build.append("max(A.SAVE_STATUS),Max( ");
			build.append("CASE WHEN A.SAVE_STATUS = 'IS_SAVED' THEN GSTN_SAVE_REF_ID ELSE NULL END ");
			build.append(") AS GSTIN_REF_ID, max( CASE WHEN A.SAVE_STATUS = 'IS_SAVED' THEN TO_CHAR( ");
			build.append("ADD_SECONDS(BATCH_DATE, 19800), 'DD-MM-YYYY HH24:MI:SS' ) ELSE NULL END ");
			build.append(") AS GSTIN_REF_ID_TIME,ERROR_CODE,ERROR_DESC,A.DERIVED_RET_PERIOD "); 
			build.append("from ( with GSTN as ( SELECT PS.SUPPLIER_GSTIN,PS.RETURN_PERIOD, "); 
			build.append("PS.DERIVED_RET_PERIOD,HSNSAC AS GSTN_ITM_HSNSAC,");
			build.append("HSN_DESCRIPTION AS GSTN_ITM_DESCRIPTION, TAX_RATE GSTN_TAX_RATE,"); 
			build.append("(CASE WHEN LEFT(HSNSAC, 2) = '99' then 'NA' else ITM_UQC end ");
			build.append(") AS GSTN_ITM_UQC,SUM( ");
			build.append("( CASE WHEN LEFT(HSNSAC, 2) = '99' then 0 else ifnull(ITM_QTY, 0) end ");
			build.append(") ) AS GSTN_ITM_QTY,sum(IFNULL(TAXABLE_VALUE, 0) ) AS GSTN_TAXABLE_VALUE,"); 
			build.append("sum( IFNULL(IGST_AMT, 0) ) AS GSTN_IGST_AMT, "); 
			build.append("sum(IFNULL(CGST_AMT, 0) ) AS GSTN_CGST_AMT,"); 
			build.append("sum( IFNULL(SGST_AMT, 0) ) AS GSTN_SGST_AMT,"); 
			build.append("sum( IFNULL(CESS_AMT, 0) ) AS GSTN_CESS_AMT, "); 
			build.append("sum( IFNULL(TOTAL_VALUE, 0) ) AS GSTN_TOT_VAL, "); 
			build.append("ifnull(BT.IS_FILED, FALSE) IS_FILED "); 
			build.append("FROM GSTR1_SUBMITTED_PS_TRANS PS "); 
			build.append("LEFT JOIN GETANX1_BATCH_TABLE BT ON PS.BATCH_ID = BT.ID ");
			build.append("WHERE TAX_DOC_TYPE = 'HSN_GSTN' group by "); 
			build.append("PS.SUPPLIER_GSTIN,PS.RETURN_PERIOD,PS.DERIVED_RET_PERIOD,"); 
			build.append("HSNSAC,HSN_DESCRIPTION,TAX_RATE,ITM_UQC,BT.IS_FILED ), "); 
			build.append("digi as ( SELECT PS.SUPPLIER_GSTIN,PS.RETURN_PERIOD,PS.DERIVED_RET_PERIOD,"); 
			build.append("HSNSAC AS DG_ITM_HSNSAC, HSN_DESCRIPTION AS DG_ITM_DESCRIPTION,"); 
			build.append("TAX_RATE AS DG_TAX_RATE, ( ");
			build.append("CASE WHEN LEFT(HSNSAC, 2) = '99' then 'NA' else ITM_UQC end ");
			build.append(") AS DG_ITM_UQC, SUM( ( ");
			build.append("CASE WHEN LEFT(HSNSAC, 2) = '99' then 0 else ifnull(ITM_QTY, 0) end ");
			build.append(") ) AS DG_ITM_QTY,sum( (IFNULL((CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(TAXABLE_VALUE,0) END),0)- IFNULL((CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TAXABLE_VALUE,0) END),0)) ");
			build.append(") AS DG_TAXABLE_VALUE, sum( (IFNULL((CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(IGST_AMT,0) END),0)- IFNULL((CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(IGST_AMT,0) END),0)) ) AS DG_IGST_AMT,"); 
			build.append("sum( (IFNULL((CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(CGST_AMT,0) END),0)- IFNULL((CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(CGST_AMT,0) END),0)) ) AS DG_CGST_AMT, "); 
			build.append("sum( (IFNULL((CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(SGST_AMT,0) END),0)- IFNULL((CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(SGST_AMT,0) END),0)) ) AS DG_SGST_AMT, ");
			build.append("sum( (IFNULL((CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(CESS_AMT,0) END),0)- IFNULL((CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(CESS_AMT,0) END),0)) ");
			build.append(") AS DG_CESS_AMT,sum((IFNULL((CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(TOTAL_VALUE,0) END),0)- IFNULL((CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TOTAL_VALUE,0) END),0))  ) AS DG_TOT_VAL, "); 
			build.append("MAX( CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' ");
			build.append("ELSE 'NOT_SAVED' END ) DG_SAVE_STATUS, max(BATCH_ID) dg_batch_id "); 
			build.append("FROM GSTR1_SUBMITTED_PS_TRANS PS "); 
			build.append("WHERE TAX_DOC_TYPE = 'HSN_DIGI' group by PS.SUPPLIER_GSTIN, "); 
			build.append("PS.RETURN_PERIOD,PS.DERIVED_RET_PERIOD,HSNSAC,HSN_DESCRIPTION,"); 
			build.append("TAX_RATE,ITM_UQC ), ui as ( SELECT PS.SUPPLIER_GSTIN,PS.RETURN_PERIOD, "); 
			build.append("PS.DERIVED_RET_PERIOD,HSNSAC AS UI_ITM_HSNSAC,");
			build.append("HSN_DESCRIPTION AS UI_ITM_DESCRIPTION,TAX_RATE AS UI_TAX_RATE, "); 
			build.append("( CASE WHEN LEFT(HSNSAC, 2) = '99' then 'NA' else ITM_UQC end ");
			build.append(") AS UI_ITM_UQC, SUM( ( CASE WHEN LEFT(HSNSAC, 2) = '99' ");
			build.append("then 0 else ifnull(ITM_QTY, 0) end ) ) AS UI_ITM_QTY, "); 
			build.append("sum( IFNULL(TAXABLE_VALUE, 0) ) AS UI_TAXABLE_VALUE,"); 
			build.append("sum( IFNULL(IGST_AMT, 0) ) AS UI_IGST_AMT, "); 
			build.append("sum( IFNULL(CGST_AMT, 0) ) AS UI_CGST_AMT,sum( IFNULL(SGST_AMT, 0) ");
			build.append(") AS UI_SGST_AMT,sum( IFNULL(CESS_AMT, 0) ) AS UI_CESS_AMT, "); 
			build.append("sum( IFNULL(TOTAL_VALUE, 0) ) AS UI_TOT_VAL, "); 
			build.append("MAX(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' ");
			build.append("ELSE 'NOT_SAVED' END  ) UI_SAVE_STATUS, "); 
			build.append("max(BATCH_ID) UI_BATCH_ID FROM GSTR1_SUBMITTED_PS_TRANS PS "); 
			build.append("WHERE TAX_DOC_TYPE = 'HSN_UI' group by PS.SUPPLIER_GSTIN,"); 
			build.append("PS.RETURN_PERIOD,PS.DERIVED_RET_PERIOD,HSNSAC,HSN_DESCRIPTION,"); 
			build.append("TAX_RATE,ITM_UQC ) select IFNULL( ifnull( ");
			build.append("GSTN.SUPPLIER_GSTIN, UI.SUPPLIER_GSTIN ),DIGI.SUPPLIER_GSTIN ");
			build.append(") AS SUPPLIER_GSTIN,IFNULL(ifnull( GSTN.RETURN_PERIOD, ");
			build.append("UI.RETURN_PERIOD ),DIGI.RETURN_PERIOD ) AS RETURN_PERIOD, "); 
			build.append("IFNULL( ifnull( GSTN.DERIVED_RET_PERIOD, UI.DERIVED_RET_PERIOD ");
			build.append("),DIGI.DERIVED_RET_PERIOD ) AS DERIVED_RET_PERIOD, "); 
			build.append("GSTN_ITM_HSNSAC,GSTN_ITM_DESCRIPTION,GSTN_TAX_RATE,"); 
			build.append("GSTN_ITM_UQC,GSTN_ITM_QTY,GSTN_TAXABLE_VALUE,GSTN_IGST_AMT,"); 
			build.append("GSTN_CGST_AMT,GSTN_SGST_AMT,GSTN_CESS_AMT, ");
			build.append("GSTN_TOT_VAL,IS_FILED,DG_ITM_HSNSAC,DG_ITM_DESCRIPTION,"); 
			build.append("DG_TAX_RATE,DG_ITM_UQC,DG_ITM_QTY,DG_TAXABLE_VALUE,"); 
			build.append("DG_IGST_AMT,DG_CGST_AMT,DG_SGST_AMT,DG_CESS_AMT,DG_TOT_VAL,"); 
			build.append("UI_ITM_HSNSAC,UI_ITM_DESCRIPTION,UI_TAX_RATE,UI_ITM_UQC,UI_ITM_QTY,"); 
			build.append("UI_TAXABLE_VALUE,UI_IGST_AMT,UI_CGST_AMT,UI_SGST_AMT,"); 
			build.append("UI_CESS_AMT,UI_TOT_VAL,( ifnull(DG_TAXABLE_VALUE, 0)- ");
			build.append("ifnull(GSTN_TAXABLE_VALUE, 0) ) AS DIFF_TAXABLE_VALUE,"); 
			build.append("( ifnull(DG_IGST_AMT, 0)- ifnull(GSTN_IGST_AMT, 0) ");
			build.append(") AS DIFF_IGST_AMT, ( ifnull(DG_CGST_AMT, 0)- ifnull(GSTN_CGST_AMT, 0) ");
			build.append(") AS DIFF_CGST_AMT, ( ifnull(DG_SGST_AMT, 0)- ifnull(GSTN_SGST_AMT, 0) ");
			build.append(") AS DIFF_SGST_AMT,( ifnull(DG_CESS_AMT, 0)- ifnull(GSTN_CESS_AMT, 0) ");
			build.append(") AS DIFF_CESS_AMT,( case when UI_BATCH_ID > DG_BATCH_ID ");
			build.append("then UI_BATCH_ID else DG_BATCH_ID end ) as batch_id, "); 
			build.append("(case when ifnull(UI_SAVE_STATUS,'') > IFNULL(DG_SAVE_STATUS,'') ");
			build.append("then UI_SAVE_STATUS else DG_SAVE_STATUS end ) as SAVE_STATUS "); 
			build.append("from gstn full outer join ui on ");
			build.append("gstn.supplier_gstin = UI.supplier_gstin "); 
			build.append("and gstn.derived_ret_period = UI.derived_ret_period "); 
			build.append("and gstn.GSTN_ITM_HSNSAC = UI.UI_ITM_HSNSAC "); 
			build.append("and gstn.GSTN_ITM_UQC = UI.UI_ITM_UQC "); 
			build.append("and gstn.GSTN_TAX_RATE = UI.UI_TAX_RATE FULL "); 
			build.append("OUTER JOIN DIGI ON DIGI.supplier_gstin = IFNULL( ");
			build.append("UI.supplier_gstin, gstn.supplier_gstin ) "); 
			build.append("and DIGI.derived_ret_period = IFNULL( ");
			build.append("UI.derived_ret_period, gstn.derived_ret_period ) "); 
			build.append("and DIGI.DG_ITM_HSNSAC = IFNULL( ");
			build.append("UI.UI_ITM_HSNSAC, gstn.GSTN_ITM_HSNSAC ");
			build.append(" ) and DIGI.DG_ITM_UQC = IFNULL( UI.UI_ITM_UQC, gstn.GSTN_ITM_UQC ");
			build.append(") and DIGI.DG_TAX_RATE = IFNULL( UI.UI_TAX_RATE, gstn.GSTN_TAX_RATE ");
			build.append(") ) A left join ( select ID,GSTN_SAVE_STATUS,GSTN_SAVE_REF_ID, "); 
			build.append("BATCH_DATE, ERROR_CODE,ERROR_DESC from GSTR1_GSTN_SAVE_BATCH "); 
			build.append("where is_delete = FALSE ) batch on A.BATCH_ID = batch.id "); 
			build.append(buildHeader);
			build.append("group by A.SUPPLIER_GSTIN,A.RETURN_PERIOD,A.GSTN_ITM_HSNSAC,"); 
			build.append("A.GSTN_ITM_DESCRIPTION,A.GSTN_TAX_RATE,A.GSTN_ITM_UQC,A.IS_FILED,"); 
			build.append("A.DG_ITM_HSNSAC,A.DG_ITM_DESCRIPTION,A.DG_TAX_RATE,A.DG_ITM_UQC,"); 
			build.append("A.UI_ITM_HSNSAC,A.UI_ITM_DESCRIPTION,A.UI_TAX_RATE,A.UI_ITM_UQC,"); 
			  /*A.SAVE_STATUS, 
			  (
			    CASE WHEN A.SAVE_STATUS = 'IS_SAVED' THEN GSTN_SAVE_REF_ID ELSE NULL END
			  ), 
			  (
			    CASE WHEN A.SAVE_STATUS = 'IS_SAVED' THEN TO_CHAR(
			      ADD_SECONDS(BATCH_DATE, 19800), 
			      'DD-MM-YYYY HH24:MI:SS'
			    ) ELSE NULL END
			  ), */
			build.append("ERROR_CODE, ERROR_DESC,A.DERIVED_RET_PERIOD ");

		/*	
			
			build.append("SELECT ROW_NUMBER () OVER ( ORDER BY A.SUPPLIER_GSTIN) SNO,");
			build.append("A.SUPPLIER_GSTIN,A.RETURN_PERIOD,A.GSTN_ITM_HSNSAC,");
			build.append("A.GSTN_ITM_DESCRIPTION,A.GSTN_TAX_RATE,A.GSTN_ITM_UQC,");
			build.append("sum(A.GSTN_ITM_QTY),sum(A.GSTN_TAXABLE_VALUE),sum(A.GSTN_IGST_AMT),");
			build.append("sum(A.GSTN_CGST_AMT),sum(A.GSTN_SGST_AMT),sum(A.GSTN_CESS_AMT),");
			build.append("sum(A.GSTN_TOT_VAL),A.IS_FILED,A.DG_ITM_HSNSAC,A.DG_ITM_DESCRIPTION,");
			build.append("A.DG_TAX_RATE, A.DG_ITM_UQC,sum(A.DG_ITM_QTY),sum(A.DG_TAXABLE_VALUE),");
			build.append("sum(A.DG_IGST_AMT),sum( A.DG_CGST_AMT),sum(A.DG_SGST_AMT),sum(A.DG_CESS_AMT),");
			build.append("sum(A.DG_TOT_VAL), A.UI_ITM_HSNSAC, A.UI_ITM_DESCRIPTION,A.UI_TAX_RATE,");
			build.append("A.UI_ITM_UQC,sum(A.UI_ITM_QTY),sum(A.UI_TAXABLE_VALUE),sum(A.UI_IGST_AMT),");
			build.append("sum( A.UI_CGST_AMT),sum(A.UI_SGST_AMT),sum(A.UI_CESS_AMT),");
			build.append("sum(A.UI_TOT_VAL),sum(A.DIFF_TAXABLE_VALUE),sum(A.DIFF_IGST_AMT),");
			build.append("sum(A.DIFF_CGST_AMT),sum(A.DIFF_SGST_AMT),sum( A.DIFF_CESS_AMT),");
			build.append("A.SAVE_STATUS,(CASE WHEN A.SAVE_STATUS='IS_SAVED' THEN GSTN_SAVE_REF_ID "); 
			build.append("ELSE NULL END) AS GSTIN_REF_ID,");
			build.append("(CASE WHEN A.SAVE_STATUS='IS_SAVED' THEN TO_CHAR(ADD_SECONDS(BATCH_DATE,");
			build.append("19800),'DD-MM-YYYY HH24:MI:SS') ELSE NULL "); 
			build.append("END) AS GSTIN_REF_ID_TIME,ERROR_CODE,ERROR_DESC,A.DERIVED_RET_PERIOD "); 
			build.append("from ( with GSTN as ( SELECT PS.SUPPLIER_GSTIN,PS.RETURN_PERIOD,");
			build.append("PS.DERIVED_RET_PERIOD,HSNSAC AS GSTN_ITM_HSNSAC,");
			build.append("HSN_DESCRIPTION AS GSTN_ITM_DESCRIPTION,TAX_RATE GSTN_TAX_RATE,");
			build.append("(CASE WHEN LEFT(HSNSAC, 2) = '99' then 'NA' else ITM_UQC end)  ");
			build.append("AS GSTN_ITM_UQC,SUM((CASE WHEN LEFT(HSNSAC, 2) = '99' ");
			build.append("then 0 else ifnull(ITM_QTY,0) end)) AS GSTN_ITM_QTY,");
			build.append("sum(IFNULL(TAXABLE_VALUE,0)) AS GSTN_TAXABLE_VALUE,");
			build.append("sum(IFNULL(IGST_AMT,0)) AS GSTN_IGST_AMT,");
			build.append("sum(IFNULL(CGST_AMT,0)) AS GSTN_CGST_AMT,sum(IFNULL(SGST_AMT,");
			build.append("0)) AS GSTN_SGST_AMT,sum(IFNULL(CESS_AMT,0)) AS GSTN_CESS_AMT,");
			build.append("sum(IFNULL(TOTAL_VALUE,0)) AS GSTN_TOT_VAL,ifnull(BT.IS_FILED,");
			build.append("FALSE) IS_FILED FROM GSTR1_SUBMITTED_PS_TRANS PS "); 
			build.append("LEFT JOIN GETANX1_BATCH_TABLE BT ON PS.BATCH_ID = BT.ID "); 
			build.append("WHERE TAX_DOC_TYPE ='HSN_GSTN' group by PS.SUPPLIER_GSTIN,");
			build.append("PS.RETURN_PERIOD,PS.DERIVED_RET_PERIOD,HSNSAC,HSN_DESCRIPTION ,");
			build.append("TAX_RATE,ITM_UQC,BT.IS_FILED ) ,digi as ( SELECT PS.SUPPLIER_GSTIN,");
			build.append("PS.RETURN_PERIOD,PS.DERIVED_RET_PERIOD,HSNSAC AS DG_ITM_HSNSAC,");
			build.append("HSN_DESCRIPTION AS DG_ITM_DESCRIPTION,TAX_RATE AS DG_TAX_RATE,");
			build.append("(CASE WHEN LEFT(HSNSAC, 2) = '99' then 'NA' else ITM_UQC end) ");
			build.append("AS DG_ITM_UQC,SUM((CASE WHEN LEFT(HSNSAC, 2) = '99' ");
			build.append("then 0 else ifnull(ITM_QTY,0) end)) AS DG_ITM_QTY,");
			build.append("sum(IFNULL(TAXABLE_VALUE,0)) AS DG_TAXABLE_VALUE,");
			build.append("sum(IFNULL(IGST_AMT,0)) AS DG_IGST_AMT,");
			build.append("sum(IFNULL(CGST_AMT,0)) AS DG_CGST_AMT,");
			build.append("sum(IFNULL(SGST_AMT,0)) AS DG_SGST_AMT,");
			build.append("sum(IFNULL(CESS_AMT,0)) AS DG_CESS_AMT,");
			build.append("sum(IFNULL(TOTAL_VALUE,0)) AS DG_TOT_VAL,");
			build.append("MAX(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "); 
			build.append("ELSE 'NOT_SAVED' END) DG_SAVE_STATUS,");
			build.append("max(BATCH_ID) dg_batch_id FROM GSTR1_SUBMITTED_PS_TRANS PS "); 
			build.append("WHERE TAX_DOC_TYPE ='HSN_DIGI' group by PS.SUPPLIER_GSTIN,");
			build.append("PS.RETURN_PERIOD,PS.DERIVED_RET_PERIOD,HSNSAC , ");
			build.append("HSN_DESCRIPTION ,TAX_RATE,ITM_UQC  ) , ");
			build.append("ui as ( SELECT PS.SUPPLIER_GSTIN,PS.RETURN_PERIOD,");
			build.append("PS.DERIVED_RET_PERIOD,HSNSAC AS UI_ITM_HSNSAC,");
			build.append("HSN_DESCRIPTION AS UI_ITM_DESCRIPTION,TAX_RATE AS UI_TAX_RATE,");
			build.append("(CASE WHEN LEFT(HSNSAC, 2) = '99' then 'NA' else ITM_UQC end) ");
			build.append("AS UI_ITM_UQC,SUM((CASE WHEN LEFT(HSNSAC, 2) = '99' ");
			build.append("then 0 else ifnull(ITM_QTY,0) end)) AS UI_ITM_QTY,");
			build.append("sum(IFNULL(TAXABLE_VALUE,0)) AS UI_TAXABLE_VALUE,");
			build.append("sum(IFNULL(IGST_AMT,0)) AS UI_IGST_AMT,");
			build.append("sum(IFNULL(CGST_AMT,0)) AS UI_CGST_AMT,");
			build.append("sum(IFNULL(SGST_AMT,0)) AS UI_SGST_AMT,");
			build.append("sum(IFNULL(CESS_AMT,0)) AS UI_CESS_AMT,");
			build.append("sum(IFNULL(TOTAL_VALUE,0)) AS UI_TOT_VAL,");
			build.append("MAX(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "); 
			build.append("ELSE 'NOT_SAVED' END) UI_SAVE_STATUS,");
			build.append("max(BATCH_ID) UI_BATCH_ID FROM GSTR1_SUBMITTED_PS_TRANS PS "); 
			build.append("WHERE TAX_DOC_TYPE ='HSN_UI' group by PS.SUPPLIER_GSTIN,");
			build.append("PS.RETURN_PERIOD,PS.DERIVED_RET_PERIOD,HSNSAC ,");
			build.append("HSN_DESCRIPTION,TAX_RATE,ITM_UQC ) ");
			build.append("select IFNULL(ifnull(GSTN.SUPPLIER_GSTIN,");
			build.append("UI.SUPPLIER_GSTIN),DIGI.SUPPLIER_GSTIN) AS SUPPLIER_GSTIN,");
			build.append("IFNULL(ifnull(GSTN.RETURN_PERIOD,UI.RETURN_PERIOD),");
			build.append("DIGI.RETURN_PERIOD) AS RETURN_PERIOD,");
			build.append("IFNULL(ifnull(GSTN.DERIVED_RET_PERIOD,");
			build.append("UI.DERIVED_RET_PERIOD),");
			build.append("DIGI.DERIVED_RET_PERIOD) AS DERIVED_RET_PERIOD,");
			build.append("GSTN_ITM_HSNSAC,GSTN_ITM_DESCRIPTION,GSTN_TAX_RATE,");
			build.append("GSTN_ITM_UQC,GSTN_ITM_QTY,GSTN_TAXABLE_VALUE,");
			build.append("GSTN_IGST_AMT,GSTN_CGST_AMT,GSTN_SGST_AMT,GSTN_CESS_AMT,");
			build.append("GSTN_TOT_VAL,IS_FILED,DG_ITM_HSNSAC,DG_ITM_DESCRIPTION,");
			build.append("DG_TAX_RATE,DG_ITM_UQC,DG_ITM_QTY,DG_TAXABLE_VALUE,");
			build.append("DG_IGST_AMT,DG_CGST_AMT,DG_SGST_AMT,DG_CESS_AMT,");
			build.append("DG_TOT_VAL,UI_ITM_HSNSAC,UI_ITM_DESCRIPTION,UI_TAX_RATE,");
			build.append("UI_ITM_UQC,UI_ITM_QTY,UI_TAXABLE_VALUE,UI_IGST_AMT,");
			build.append("UI_CGST_AMT,UI_SGST_AMT,UI_CESS_AMT,UI_TOT_VAL,");
			build.append("(ifnull(DG_TAXABLE_VALUE,0)-ifnull(GSTN_TAXABLE_VALUE,");
			build.append("0)) AS DIFF_TAXABLE_VALUE,(ifnull(DG_IGST_AMT,");
			build.append("0)-ifnull(GSTN_IGST_AMT,0)) AS DIFF_IGST_AMT,");
			build.append("(ifnull(DG_CGST_AMT,0)-ifnull(GSTN_CGST_AMT,");
			build.append("0)) AS DIFF_CGST_AMT,(ifnull(DG_SGST_AMT,");
			build.append("0)-ifnull(GSTN_SGST_AMT,0)) AS DIFF_SGST_AMT,");
			build.append("(ifnull(DG_CESS_AMT,0)-ifnull(GSTN_CESS_AMT,");
			build.append("0)) AS DIFF_CESS_AMT,(case when UI_BATCH_ID> DG_BATCH_ID "); 
			build.append("then UI_BATCH_ID else DG_BATCH_ID "); 
			build.append("end) as batch_id,(case when UI_SAVE_STATUS> DG_SAVE_STATUS "); 
			build.append("then UI_SAVE_STATUS else DG_SAVE_STATUS "); 
			build.append("end) as SAVE_STATUS from gstn full outer join ui on ");
			build.append("gstn.supplier_gstin = UI.supplier_gstin  ");
			build.append("and gstn.derived_ret_period =UI.derived_ret_period  ");
			build.append("and gstn.GSTN_ITM_HSNSAC = UI.UI_ITM_HSNSAC  ");
			build.append("and gstn.GSTN_ITM_UQC = UI.UI_ITM_UQC  ");
			build.append("and gstn.GSTN_TAX_RATE = UI.UI_TAX_RATE ");
			build.append("FULL OUTER JOIN DIGI ON ");
			build.append("DIGI.supplier_gstin = IFNULL(UI.supplier_gstin,");
			build.append("gstn.supplier_gstin) ");
			build.append("and DIGI.derived_ret_period =IFNULL(UI.derived_ret_period,");
			build.append("gstn.derived_ret_period) ");
			build.append("and DIGI.DG_ITM_HSNSAC = IFNULL(UI.UI_ITM_HSNSAC,");
			build.append("gstn.GSTN_ITM_HSNSAC) ");
			build.append("and DIGI.DG_ITM_UQC = IFNULL(UI.UI_ITM_UQC,");
			build.append("gstn.GSTN_ITM_UQC) and DIGI.DG_TAX_RATE=IFNULL(UI.UI_TAX_RATE,");
			build.append("gstn.GSTN_TAX_RATE) ) A  left join ( select ID,");
			build.append("GSTN_SAVE_STATUS,GSTN_SAVE_REF_ID,BATCH_DATE,");
			build.append("ERROR_CODE,ERROR_DESC from GSTR1_GSTN_SAVE_BATCH "); 
			build.append("where is_delete = FALSE ) batch on A.BATCH_ID=batch.id ");
			build.append(buildHeader);
			build.append(" group by A.SUPPLIER_GSTIN,A.RETURN_PERIOD,");
			build.append("A.GSTN_ITM_HSNSAC,A.GSTN_ITM_DESCRIPTION,");
			build.append("A.GSTN_TAX_RATE,A.GSTN_ITM_UQC,A.IS_FILED,");
			build.append("A.DG_ITM_HSNSAC,A.DG_ITM_DESCRIPTION,A.DG_TAX_RATE,");
			build.append("A.DG_ITM_UQC,A.UI_ITM_HSNSAC,A.UI_ITM_DESCRIPTION,");
			build.append("A.UI_TAX_RATE,A.UI_ITM_UQC,A.SAVE_STATUS,");
			build.append("(CASE WHEN A.SAVE_STATUS='IS_SAVED' THEN GSTN_SAVE_REF_ID "); 
			build.append("ELSE NULL END) ,(CASE WHEN A.SAVE_STATUS='IS_SAVED' "); 
			build.append("THEN TO_CHAR(ADD_SECONDS(BATCH_DATE,");
			build.append("19800),'DD-MM-YYYY HH24:MI:SS') "); 
			build.append("ELSE NULL END) ,ERROR_CODE,ERROR_DESC,A.DERIVED_RET_PERIOD ");

			*/
			
		/*	
			
			
			
		
		build.append("SELECT ROW_NUMBER () OVER (ORDER BY A.SUPPLIER_GSTIN) SNO,A.SUPPLIER_GSTIN,");
		build.append("A.RETURN_PERIOD,A.GSTN_ITM_HSNSAC,A.GSTN_ITM_DESCRIPTION,A.GSTN_TAX_RATE,");
		build.append("A.GSTN_ITM_UQC,A.GSTN_ITM_QTY,A.GSTN_TAXABLE_VALUE,A.GSTN_IGST_AMT,");
		build.append("A.GSTN_CGST_AMT,A.GSTN_SGST_AMT,A.GSTN_CESS_AMT,A.GSTN_TOT_VAL,");
		build.append("A.IS_FILED,A.DG_ITM_HSNSAC,A.DG_ITM_DESCRIPTION,A.DG_TAX_RATE,");
		build.append("A.DG_ITM_UQC,A.DG_ITM_QTY,A.DG_TAXABLE_VALUE,A.DG_IGST_AMT,");
		build.append("A.DG_CGST_AMT,A.DG_SGST_AMT,A.DG_CESS_AMT,A.DG_TOT_VAL,A.UI_ITM_HSNSAC,A.UI_ITM_DESCRIPTION,");
		build.append("A.UI_TAX_RATE,A.UI_ITM_UQC,A.UI_ITM_QTY,A.UI_TAXABLE_VALUE,A.UI_IGST_AMT,");
		build.append("A.UI_CGST_AMT,A.UI_SGST_AMT,A.UI_CESS_AMT,A.UI_TOT_VAL,");
		build.append("A.DIFF_TAXABLE_VALUE,A.DIFF_IGST_AMT,A.DIFF_CGST_AMT,A.DIFF_SGST_AMT,");
		build.append("A.DIFF_CESS_AMT,A.SAVE_STATUS,");
		build.append("(CASE WHEN A.SAVE_STATUS='IS_SAVED' THEN GSTN_SAVE_REF_ID ELSE NULL END) AS GSTIN_REF_ID,");
		build.append("(CASE WHEN A.SAVE_STATUS='IS_SAVED' THEN TO_CHAR(ADD_SECONDS(BATCH_DATE,19800),'DD-MM-YYYY HH24:MI:SS') ELSE NULL END) AS GSTIN_REF_ID_TIME, ");
	//	build.append("(CASE WHEN A.SAVE_STATUS='IS_SAVED' THEN ADD_SECONDS(BATCH_DATE,19800) ELSE NULL END) AS GSTIN_REF_ID_TIME, ");
		build.append("ERROR_CODE, ERROR_DESC,A.DERIVED_RET_PERIOD from ");
		build.append("( with GSTN  as ( SELECT PS.SUPPLIER_GSTIN, ");
		build.append("PS.RETURN_PERIOD,	PS.DERIVED_RET_PERIOD,HSNSAC AS GSTN_ITM_HSNSAC,");
		build.append("HSN_DESCRIPTION AS GSTN_ITM_DESCRIPTION,TAX_RATE GSTN_TAX_RATE,");
		build.append("ITM_UQC AS GSTN_ITM_UQC,SUM(ifnull(ITM_QTY,0)) AS GSTN_ITM_QTY,");
		build.append("sum(IFNULL(TAXABLE_VALUE,0)) AS GSTN_TAXABLE_VALUE,sum(IFNULL(IGST_AMT,0)) AS GSTN_IGST_AMT,");
		build.append("sum(IFNULL(CGST_AMT,0)) AS GSTN_CGST_AMT,sum(IFNULL(SGST_AMT,0)) AS GSTN_SGST_AMT,");
		build.append("sum(IFNULL(CESS_AMT,0)) AS GSTN_CESS_AMT,sum(IFNULL(TOTAL_VALUE,0)) AS GSTN_TOT_VAL,");
		build.append("ifnull(BT.IS_FILED,FALSE) IS_FILED FROM  GSTR1_SUBMITTED_PS_TRANS PS ");
		build.append("LEFT JOIN GETANX1_BATCH_TABLE BT  ON PS.BATCH_ID = BT.ID	WHERE TAX_DOC_TYPE ='HSN_GSTN'  ");
		build.append("group by PS.SUPPLIER_GSTIN,PS.RETURN_PERIOD,PS.DERIVED_RET_PERIOD,HSNSAC, ");
		build.append("HSN_DESCRIPTION ,TAX_RATE,ITM_UQC,");
		//	--ifnull(ITM_QTY,0),
		build.append("BT.IS_FILED  ) , digi as ( ");
		build.append("SELECT PS.SUPPLIER_GSTIN, PS.RETURN_PERIOD,PS.DERIVED_RET_PERIOD,");
		build.append("HSNSAC AS DG_ITM_HSNSAC, HSN_DESCRIPTION AS DG_ITM_DESCRIPTION,");
		build.append("TAX_RATE AS DG_TAX_RATE, ITM_UQC AS DG_ITM_UQC,SUM(ifnull(ITM_QTY,0)) AS DG_ITM_QTY,");
		build.append("sum(IFNULL(TAXABLE_VALUE,0)) AS DG_TAXABLE_VALUE, sum(IFNULL(IGST_AMT,0)) AS DG_IGST_AMT,");
		build.append("sum(IFNULL(CGST_AMT,0)) AS DG_CGST_AMT,sum(IFNULL(SGST_AMT,0)) AS DG_SGST_AMT,");
		build.append("sum(IFNULL(CESS_AMT,0)) AS DG_CESS_AMT, sum(IFNULL(TOTAL_VALUE,0)) AS DG_TOT_VAL,");
		build.append("MAX(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' ELSE 'NOT_SAVED' END) DG_SAVE_STATUS,");
		build.append("max(BATCH_ID) dg_batch_id FROM GSTR1_SUBMITTED_PS_TRANS PS ");
		build.append("WHERE  TAX_DOC_TYPE ='HSN_DIGI'  group by PS.SUPPLIER_GSTIN, PS.RETURN_PERIOD,");
		build.append("PS.DERIVED_RET_PERIOD,HSNSAC ,HSN_DESCRIPTION ,TAX_RATE, ITM_UQC  ");
		//	 --ifnull(ITM_QTY,0)
		build.append(") , ui as ( SELECT PS.SUPPLIER_GSTIN, PS.RETURN_PERIOD,PS.DERIVED_RET_PERIOD,");
		build.append("HSNSAC AS UI_ITM_HSNSAC, HSN_DESCRIPTION AS UI_ITM_DESCRIPTION, TAX_RATE AS UI_TAX_RATE,");
		build.append("ITM_UQC AS UI_ITM_UQC,SUM(ifnull(ITM_QTY,0)) AS UI_ITM_QTY,");
		build.append("sum(IFNULL(TAXABLE_VALUE,0)) AS UI_TAXABLE_VALUE,");
		build.append("sum(IFNULL(IGST_AMT,0)) AS UI_IGST_AMT,");
		build.append("sum(IFNULL(CGST_AMT,0)) AS UI_CGST_AMT,");
		build.append("sum(IFNULL(SGST_AMT,0)) AS UI_SGST_AMT,");
		build.append("sum(IFNULL(CESS_AMT,0)) AS UI_CESS_AMT,");
		build.append("sum(IFNULL(TOTAL_VALUE,0)) AS UI_TOT_VAL,");
		build.append("MAX(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' ELSE 'NOT_SAVED' END) UI_SAVE_STATUS,");
		build.append("max(BATCH_ID) UI_BATCH_ID FROM GSTR1_SUBMITTED_PS_TRANS PS WHERE  TAX_DOC_TYPE ='HSN_UI'  ");
		build.append("group by PS.SUPPLIER_GSTIN,PS.RETURN_PERIOD,PS.DERIVED_RET_PERIOD,");
		build.append("HSNSAC ,HSN_DESCRIPTION,TAX_RATE,ITM_UQC  ");
		//	 --ifnull(ITM_QTY,0)

		build.append(") select  ");
		build.append("IFNULL(ifnull(GSTN.SUPPLIER_GSTIN,UI.SUPPLIER_GSTIN),DIGI.SUPPLIER_GSTIN) AS SUPPLIER_GSTIN,");
		build.append("IFNULL(ifnull(GSTN.RETURN_PERIOD,UI.RETURN_PERIOD),DIGI.RETURN_PERIOD) AS RETURN_PERIOD,");
		build.append("IFNULL(ifnull(GSTN.DERIVED_RET_PERIOD,UI.DERIVED_RET_PERIOD),DIGI.DERIVED_RET_PERIOD) AS DERIVED_RET_PERIOD,");
		build.append("GSTN_ITM_HSNSAC,GSTN_ITM_DESCRIPTION,GSTN_TAX_RATE,GSTN_ITM_UQC,GSTN_ITM_QTY,");
		build.append("GSTN_TAXABLE_VALUE,GSTN_IGST_AMT,GSTN_CGST_AMT,GSTN_SGST_AMT,GSTN_CESS_AMT,");
		build.append("GSTN_TOT_VAL,IS_FILED,DG_ITM_HSNSAC,DG_ITM_DESCRIPTION,");
		build.append("DG_TAX_RATE,DG_ITM_UQC,DG_ITM_QTY,DG_TAXABLE_VALUE,DG_IGST_AMT,");
		build.append("DG_CGST_AMT,DG_SGST_AMT,DG_CESS_AMT,DG_TOT_VAL,UI_ITM_HSNSAC,UI_ITM_DESCRIPTION,");
		build.append("UI_TAX_RATE,UI_ITM_UQC,UI_ITM_QTY,UI_TAXABLE_VALUE,UI_IGST_AMT,");
		build.append("UI_CGST_AMT,UI_SGST_AMT,UI_CESS_AMT,UI_TOT_VAL, ");
		build.append("(ifnull(DG_TAXABLE_VALUE,0)-ifnull(GSTN_TAXABLE_VALUE,0)) AS DIFF_TAXABLE_VALUE,");
		build.append("(ifnull(DG_IGST_AMT,0)-ifnull(GSTN_IGST_AMT,0)) AS DIFF_IGST_AMT,");
		build.append("(ifnull(DG_CGST_AMT,0)-ifnull(GSTN_CGST_AMT,0)) AS DIFF_CGST_AMT, ");
		build.append("(ifnull(DG_SGST_AMT,0)-ifnull(GSTN_SGST_AMT,0)) AS DIFF_SGST_AMT,");
		build.append("(ifnull(DG_CESS_AMT,0)-ifnull(GSTN_CESS_AMT,0)) AS DIFF_CESS_AMT,");
		build.append("(case when UI_BATCH_ID> DG_BATCH_ID then UI_BATCH_ID else DG_BATCH_ID  end) as batch_id,");
		build.append("(case when UI_SAVE_STATUS> DG_SAVE_STATUS then UI_SAVE_STATUS ");
		build.append("else DG_SAVE_STATUS  end) as SAVE_STATUS from gstn full outer join ui ");
		build.append("on gstn.supplier_gstin = UI.supplier_gstin and gstn.derived_ret_period =UI.derived_ret_period ");
		build.append("and gstn.GSTN_ITM_HSNSAC = UI.UI_ITM_HSNSAC and gstn.GSTN_ITM_UQC = UI.UI_ITM_UQC  and gstn.GSTN_TAX_RATE = UI.UI_TAX_RATE ");
		build.append("FULL OUTER JOIN DIGI ON DIGI.supplier_gstin = IFNULL(UI.supplier_gstin,gstn.supplier_gstin) ");
		build.append("and DIGI.derived_ret_period =IFNULL(UI.derived_ret_period,gstn.derived_ret_period) ");
		build.append("and DIGI.DG_ITM_HSNSAC = IFNULL(UI.UI_ITM_HSNSAC,gstn.GSTN_ITM_HSNSAC) ");
		build.append("and DIGI.DG_ITM_UQC = IFNULL(UI.UI_ITM_UQC,gstn.GSTN_ITM_UQC) and DIGI.DG_TAX_RATE=IFNULL(UI.UI_TAX_RATE,gstn.GSTN_TAX_RATE) ) A left join ");
		build.append("( select ID,GSTN_SAVE_STATUS,GSTN_SAVE_REF_ID,BATCH_DATE,	ERROR_CODE,ERROR_DESC ");
		build.append("from GSTR1_GSTN_SAVE_BATCH where is_delete = FALSE ");
		//	--and section='HSNSUM'
		build.append(") batch on  A.BATCH_ID=batch.id ");
		build.append(buildHeader);
		
		
		
		*/
		
			
			}else{
		
		build.append("SELECT ROW_NUMBER () OVER (ORDER BY A.SUPPLIER_GSTIN) SNO,");
		build.append("A.SUPPLIER_GSTIN,	A.RETURN_PERIOD,A.GSTN_ITM_HSNSAC,");
		build.append("A.GSTN_ITM_DESCRIPTION,A.GSTN_ITM_UQC,A.GSTN_ITM_QTY,");
		build.append("A.GSTN_TAXABLE_VALUE,	A.GSTN_IGST_AMT,A.GSTN_CGST_AMT,");
		build.append("A.GSTN_SGST_AMT,A.GSTN_CESS_AMT,A.GSTN_TOT_VAL,A.IS_FILED,");
		build.append("A.DG_ITM_HSNSAC,A.DG_ITM_DESCRIPTION,	A.DG_ITM_UQC,A.DG_ITM_QTY,");
		build.append("A.DG_TAXABLE_VALUE,A.DG_IGST_AMT,A.DG_CGST_AMT,A.DG_SGST_AMT,");
		build.append("A.DG_CESS_AMT,A.DG_TOT_VAL,A.UI_ITM_HSNSAC,A.UI_ITM_DESCRIPTION,");
		build.append("A.UI_ITM_UQC,	A.UI_ITM_QTY,A.UI_TAXABLE_VALUE,A.UI_IGST_AMT,");
		build.append("A.UI_CGST_AMT,A.UI_SGST_AMT,A.UI_CESS_AMT,A.UI_TOT_VAL,");
		build.append("A.DIFF_TAXABLE_VALUE,A.DIFF_IGST_AMT,	A.DIFF_CGST_AMT,");
		build.append("A.DIFF_SGST_AMT,A.DIFF_CESS_AMT,A.SAVE_STATUS,");
		build.append("(CASE WHEN A.SAVE_STATUS='IS_SAVED' THEN GSTN_SAVE_REF_ID ELSE NULL END) AS GSTIN_REF_ID,");
		build.append("(CASE WHEN A.SAVE_STATUS='IS_SAVED' THEN ADD_SECONDS(BATCH_DATE,19800) ELSE NULL END) AS GSTIN_REF_ID_TIME, ");
		build.append("ERROR_CODE, ERROR_DESC,A.DERIVED_RET_PERIOD from ");
		build.append("( with GSTN as ( ");
		build.append("SELECT PS.SUPPLIER_GSTIN,PS.RETURN_PERIOD,PS.DERIVED_RET_PERIOD,");
		build.append("HSNSAC AS GSTN_ITM_HSNSAC,HSN_DESCRIPTION AS GSTN_ITM_DESCRIPTION,");
		build.append("ITM_UQC AS GSTN_ITM_UQC,SUM(ifnull(ITM_QTY,0)) AS GSTN_ITM_QTY,");
		build.append("sum(IFNULL(TAXABLE_VALUE,0)) AS GSTN_TAXABLE_VALUE,");
		build.append("sum(IFNULL(IGST_AMT,0)) AS GSTN_IGST_AMT,");
		build.append("sum(IFNULL(CGST_AMT,0)) AS GSTN_CGST_AMT,");
		build.append("sum(IFNULL(SGST_AMT,0)) AS GSTN_SGST_AMT,");
		build.append("sum(IFNULL(CESS_AMT,0)) AS GSTN_CESS_AMT,");
		build.append("sum(IFNULL(TOTAL_VALUE,0)) AS GSTN_TOT_VAL,");
		build.append("ifnull(BT.IS_FILED,FALSE) IS_FILED FROM  GSTR1_SUBMITTED_PS_TRANS PS ");
		build.append("LEFT JOIN GETANX1_BATCH_TABLE BT  ON PS.BATCH_ID = BT.ID	");
		build.append("WHERE TAX_DOC_TYPE ='HSN_GSTN'  group by PS.SUPPLIER_GSTIN,");
		build.append("PS.RETURN_PERIOD,	PS.DERIVED_RET_PERIOD,HSNSAC, HSN_DESCRIPTION ,");
		build.append("ITM_UQC,");
		//	--ifnull(ITM_QTY,0),
		build.append("BT.IS_FILED  ) , digi as 	( ");
		build.append("SELECT  PS.SUPPLIER_GSTIN, PS.RETURN_PERIOD,");
		build.append("PS.DERIVED_RET_PERIOD, HSNSAC AS DG_ITM_HSNSAC,");
		build.append("HSN_DESCRIPTION AS DG_ITM_DESCRIPTION,ITM_UQC AS DG_ITM_UQC,");
		build.append("SUM(ifnull(ITM_QTY,0)) AS DG_ITM_QTY,");
		build.append("sum((IFNULL((CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(TAXABLE_VALUE,0) END),0)- IFNULL((CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TAXABLE_VALUE,0) END),0)) ) AS DG_TAXABLE_VALUE,");
		build.append("sum((IFNULL((CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(IGST_AMT,0) END),0)- IFNULL((CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(IGST_AMT,0) END),0))) AS DG_IGST_AMT,");
		build.append("sum((IFNULL((CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(CGST_AMT,0) END),0)- IFNULL((CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(CGST_AMT,0) END),0))) AS DG_CGST_AMT,");
		build.append("sum((IFNULL((CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(SGST_AMT,0) END),0)- IFNULL((CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(SGST_AMT,0) END),0))) AS DG_SGST_AMT,");
		build.append("sum((IFNULL((CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(CESS_AMT,0) END),0)- IFNULL((CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(CESS_AMT,0) END),0))) AS DG_CESS_AMT,");
		build.append("sum((IFNULL((CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IFNULL(TOTAL_VALUE,0) END),0)- IFNULL((CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TOTAL_VALUE,0) END),0))) AS DG_TOT_VAL,");
		build.append("MAX(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' ELSE 'NOT_SAVED' END) DG_SAVE_STATUS,");
		build.append("max(BATCH_ID) dg_batch_id FROM GSTR1_SUBMITTED_PS_TRANS PS ");
		build.append("WHERE  TAX_DOC_TYPE ='HSN_DIGI'  group by ");
		build.append("PS.SUPPLIER_GSTIN,PS.RETURN_PERIOD, PS.DERIVED_RET_PERIOD,");
		build.append("HSNSAC , HSN_DESCRIPTION , ITM_UQC  ");
	//		 --ifnull(ITM_QTY,0)
		build.append(") , ui as ( ");
		build.append("SELECT PS.SUPPLIER_GSTIN, PS.RETURN_PERIOD, PS.DERIVED_RET_PERIOD,");
		build.append("HSNSAC AS UI_ITM_HSNSAC, HSN_DESCRIPTION AS UI_ITM_DESCRIPTION,");
		build.append("ITM_UQC AS UI_ITM_UQC, SUM(ifnull(ITM_QTY,0)) AS UI_ITM_QTY,");
		build.append("sum(IFNULL(TAXABLE_VALUE,0)) AS UI_TAXABLE_VALUE,");
		build.append("sum(IFNULL(IGST_AMT,0)) AS UI_IGST_AMT,");
		build.append("sum(IFNULL(CGST_AMT,0)) AS UI_CGST_AMT,");
		build.append("sum(IFNULL(SGST_AMT,0)) AS UI_SGST_AMT,");
		build.append("sum(IFNULL(CESS_AMT,0)) AS UI_CESS_AMT,");
		build.append("sum(IFNULL(TOTAL_VALUE,0)) AS UI_TOT_VAL,");
		build.append("MAX(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' ELSE 'NOT_SAVED' END) UI_SAVE_STATUS,");
		build.append("max(BATCH_ID) UI_BATCH_ID FROM GSTR1_SUBMITTED_PS_TRANS PS ");
		build.append("WHERE  TAX_DOC_TYPE ='HSN_UI'  group by ");
		build.append("PS.SUPPLIER_GSTIN,PS.RETURN_PERIOD, PS.DERIVED_RET_PERIOD,");
		build.append("HSNSAC , HSN_DESCRIPTION, ITM_UQC  ");
		//	 --ifnull(ITM_QTY,0)
		build.append(") select  ");
		build.append("IFNULL(ifnull(GSTN.SUPPLIER_GSTIN,UI.SUPPLIER_GSTIN),DIGI.SUPPLIER_GSTIN) AS SUPPLIER_GSTIN,");
		build.append("IFNULL(ifnull(GSTN.RETURN_PERIOD,UI.RETURN_PERIOD),DIGI.RETURN_PERIOD) AS RETURN_PERIOD,");
		build.append("IFNULL(ifnull(GSTN.DERIVED_RET_PERIOD,UI.DERIVED_RET_PERIOD),DIGI.DERIVED_RET_PERIOD) AS DERIVED_RET_PERIOD,");
		build.append("GSTN_ITM_HSNSAC,GSTN_ITM_DESCRIPTION,GSTN_ITM_UQC,GSTN_ITM_QTY,GSTN_TAXABLE_VALUE,");
		build.append("GSTN_IGST_AMT,GSTN_CGST_AMT,GSTN_SGST_AMT,GSTN_CESS_AMT,GSTN_TOT_VAL,IS_FILED,");
		build.append("DG_ITM_HSNSAC,DG_ITM_DESCRIPTION,DG_ITM_UQC,DG_ITM_QTY,");
		build.append("DG_TAXABLE_VALUE,DG_IGST_AMT,DG_CGST_AMT,DG_SGST_AMT,");
		build.append("DG_CESS_AMT,DG_TOT_VAL,UI_ITM_HSNSAC,UI_ITM_DESCRIPTION,");
		build.append("UI_ITM_UQC,UI_ITM_QTY,UI_TAXABLE_VALUE,UI_IGST_AMT,");
		build.append("UI_CGST_AMT,UI_SGST_AMT,UI_CESS_AMT,UI_TOT_VAL,");
		build.append("(ifnull(DG_TAXABLE_VALUE,0)-ifnull(GSTN_TAXABLE_VALUE,0)) AS DIFF_TAXABLE_VALUE,");
		build.append("(ifnull(DG_IGST_AMT,0)-ifnull(GSTN_IGST_AMT,0)) AS DIFF_IGST_AMT,");
		build.append("(ifnull(DG_CGST_AMT,0)-ifnull(GSTN_CGST_AMT,0)) AS DIFF_CGST_AMT,");
		build.append("(ifnull(DG_SGST_AMT,0)-ifnull(GSTN_SGST_AMT,0)) AS DIFF_SGST_AMT,");
		build.append("(ifnull(DG_CESS_AMT,0)-ifnull(GSTN_CESS_AMT,0)) AS DIFF_CESS_AMT,");
		build.append("(case when UI_BATCH_ID> DG_BATCH_ID then UI_BATCH_ID ");
		build.append("else DG_BATCH_ID  end) as batch_id,");
		build.append("(case when ifnull(UI_SAVE_STATUS,'') > IFNULL(DG_SAVE_STATUS,'') then UI_SAVE_STATUS ");
		build.append("else DG_SAVE_STATUS  end) as SAVE_STATUS from gstn ");
		build.append("full outer join ui on gstn.supplier_gstin = UI.supplier_gstin ");
		build.append("and gstn.derived_ret_period =UI.derived_ret_period ");
		build.append("and gstn.GSTN_ITM_HSNSAC = UI.UI_ITM_HSNSAC and gstn.GSTN_ITM_UQC = UI.UI_ITM_UQC ");
		build.append("FULL OUTER JOIN DIGI ON DIGI.supplier_gstin = IFNULL(UI.supplier_gstin,gstn.supplier_gstin) ");
		build.append("and DIGI.derived_ret_period =IFNULL(UI.derived_ret_period,gstn.derived_ret_period) ");
		build.append("and DIGI.DG_ITM_HSNSAC = IFNULL(UI.UI_ITM_HSNSAC,gstn.GSTN_ITM_HSNSAC) ");
		build.append("and DIGI.DG_ITM_UQC = IFNULL(UI.UI_ITM_UQC,gstn.GSTN_ITM_UQC) ");
		build.append(") A left join ( ");
		build.append("select ID,GSTN_SAVE_STATUS,GSTN_SAVE_REF_ID,BATCH_DATE,ERROR_CODE,ERROR_DESC ");
		build.append("from GSTR1_GSTN_SAVE_BATCH where is_delete = FALSE ");
	//		--and section='HSNSUM'
		build.append(") batch on  A.BATCH_ID=batch.id ");
		build.append(buildHeader);
	}
						
		return build.toString();
}
	
	private void settingFiledGstins(ProcessingContext context) {

		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.findByReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
						"GSTR1", "FILED");
		Set<String> filedSet = new HashSet<>();
		for (GstrReturnStatusEntity entity : filedRecords) {
			filedSet.add(
					entity.getGstin() + DOC_KEY_JOINER + entity.getTaxPeriod());
		}
		context.seAttribute("filedSet", filedSet);
	}
	
	
	private String isGstinTaxperiodFiled(ProcessingContext context, String gstin, String taxPeriod) {
		String filingStatus = "False";
		Set<String> filedSet = (Set<String>) context.getAttribute("filedSet");
		String key = gstin + DOC_KEY_JOINER + taxPeriod;
		if (filedSet != null && filedSet.contains(key)) {
			filingStatus = "True";
		}
		return filingStatus;

	}
}
