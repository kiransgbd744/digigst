package com.ey.advisory.app.gstr1a.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.master.HsnOrSacRepository;
import com.ey.advisory.app.data.views.client.Gstr1AspVerticalHsnDto;
import com.ey.advisory.app.services.search.simplified.docsummary.OnboardingConfigParmUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

import com.google.common.base.Strings;

@Component("Gstr1aAspHsnSummaryDaoImpl")
public class Gstr1aAspHsnSummaryDaoImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1aAspHsnSummaryDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("HsnOrSacRepositoryMaster")
	private HsnOrSacRepository hsnOrSacRepository;
	
	@Autowired
	@Qualifier("OnboardingConfigParmUtil")
	OnboardingConfigParmUtil onBoardingAnswer;

	// @Override
	public List<Gstr1AspVerticalHsnDto> getGstr1RSReports(
			SearchCriteria criteria) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();
		
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
				}

			}
		}

		StringBuilder buildSupplyType = new StringBuilder();
		StringBuilder buildDocType = new StringBuilder();
		StringBuilder buildHdr1SupplyType = new StringBuilder();
		List<Long> entityId = request.getEntityId();
		String supplyType = loadOnStartSupplyType(entityId);

		if (supplyType == null || supplyType.isEmpty()) {
			buildSupplyType.append(
					" AND SUPPLY_TYPE NOT IN ('NON','SCH3','NIL','EXT') ");
			buildHdr1SupplyType.append(
					" OR HDR.SUPPLY_TYPE NOT IN ('NON','SCH3','NIL','EXT') ");
		}
		if ("A".equalsIgnoreCase(supplyType)) {
			buildSupplyType.append(" AND SUPPLY_TYPE NOT IN ('NIL','EXT') ");
			buildHdr1SupplyType
					.append(" OR HDR.SUPPLY_TYPE NOT IN ('NIL','EXT') ");
		} else if ("B".equalsIgnoreCase(supplyType)) {
			buildSupplyType
					.append(" AND SUPPLY_TYPE NOT IN ('NON','SCH3','EXT') ");
			buildHdr1SupplyType
					.append(" OR HDR.SUPPLY_TYPE NOT IN ('NON','SCH3','EXT') ");
		} else if ("C".equalsIgnoreCase(supplyType)) {
			buildSupplyType
					.append(" AND SUPPLY_TYPE NOT IN ('NON','SCH3','NIL') ");
			buildHdr1SupplyType
					.append(" OR HDR.SUPPLY_TYPE NOT IN ('NON','SCH3','NIL') ");
		} else if ("A*B".equalsIgnoreCase(supplyType)) {
			buildSupplyType.append(" AND SUPPLY_TYPE NOT IN ('EXT') ");
			buildHdr1SupplyType.append(" OR HDR.SUPPLY_TYPE NOT IN ('EXT') ");
		} else if ("A*C".equalsIgnoreCase(supplyType)) {
			buildSupplyType.append(" AND SUPPLY_TYPE NOT IN ('NIL') ");
			buildHdr1SupplyType.append(" OR HDR.SUPPLY_TYPE NOT IN ('NIL') ");
		} else if ("B*C".equalsIgnoreCase(supplyType)) {
			buildSupplyType.append(" AND SUPPLY_TYPE NOT IN ('NON','SCH3') ");
			buildHdr1SupplyType
					.append(" OR HDR.SUPPLY_TYPE NOT IN ('NON','SCH3') ");
		} else if ("A*B*C".equalsIgnoreCase(supplyType)) {
			buildSupplyType.append(" AND SUPPLY_TYPE IS NOT NULL ");
			buildHdr1SupplyType.append(" OR HDR.SUPPLY_TYPE IS NOT NULL ");
		}

		String supplyTypeQuery = buildSupplyType.toString();
		String buildHdrSupplyType = buildHdr1SupplyType.toString();
		String docType1 = loadOnStartDocType(entityId);

		if ("A".equalsIgnoreCase(docType1)) {
			buildDocType.append("AND DOC_TYPE IN ('INV','BOS','CR','DR') ");
		} else if ("B".equalsIgnoreCase(docType1)) {
			buildDocType.append("AND DOC_TYPE IN ('INV','BOS') ");
		} else if ("C".equalsIgnoreCase(docType1)) {
			buildDocType.append("AND DOC_TYPE IN ('INV','BOS','CR') ");
		} else if ("D".equalsIgnoreCase(docType1)) {
			buildDocType.append("AND DOC_TYPE IN ('INV','BOS','DR')  ");
		} else if ("E".equalsIgnoreCase(docType1)) {
			buildDocType.append("AND DOC_TYPE IN ('CR','DR') ");
		}

		String docTypeQuery = buildDocType.toString();

		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildHeader = new StringBuilder();
		StringBuilder buildheader = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND HSN.SUPPLIER_GSTIN IN :gstinList");
				buildHeader.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");
				buildheader.append(" AND SUPPLIER_GSTIN IN :gstinList");
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND HSN.DERIVED_RET_PERIOD = :taxperiod ");
			buildHeader.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod ");
			buildheader.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
			
		}
		
		// getting onboarding answer
		String configAnswer = onBoardingAnswer.getConfigAnswer(entityId.get(0));

		String queryStr = createHsnSummaryQueryString(buildQuery.toString(),
				buildHeader.toString(), buildheader.toString(), supplyTypeQuery,
				buildHdrSupplyType, docTypeQuery);
		Query q = entityManager.createNativeQuery(queryStr);

		q.setParameter("configAnswer", configAnswer);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxperiod", derivedRetPeriod);
		}

		List<Object[]> list = q.getResultList();
		List<Gstr1AspVerticalHsnDto> verticalHsnList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(list)) {
			
			Map<String, String> hsnMap = new HashMap<String, String>();
			
			if (CollectionUtils.isNotEmpty(list)) {
				List<Object[]> hsnOrSacMasterEntities = hsnOrSacRepository
						.findHsnDesc();

				hsnOrSacMasterEntities.forEach(entity -> {
					hsnMap.put(entity[0].toString(), entity[1].toString());
				});
			}
			
			BigDecimal zero = BigDecimal.ZERO;

			for (Object arr[] : list) {

				Gstr1AspVerticalHsnDto convertProcessedHsn = convertProcessedHsn(
						arr, hsnMap);
				if (convertProcessedHsn.getTotalQuantity().compareTo(zero) == 0
						&& convertProcessedHsn.getTaxableValue()
								.compareTo(zero) == 0
						&& convertProcessedHsn.getIgstAmount()
								.compareTo(zero) == 0
						&& convertProcessedHsn.getCgstAmount()
								.compareTo(zero) == 0
						&& convertProcessedHsn.getSgstutgstAmount()
								.compareTo(zero) == 0
						&& convertProcessedHsn.getCessAmount()
								.compareTo(zero) == 0
						&& defaultValue(convertProcessedHsn.getTotalValue())
								.compareTo(zero) == 0
						&& convertProcessedHsn.getUtotalQuantity()
								.compareTo(zero) == 0
						&& convertProcessedHsn.getUtaxableValue()
								.compareTo(zero) == 0
						&& convertProcessedHsn.getUigstAmount()
								.compareTo(zero) == 0
						&& convertProcessedHsn.getUcgstAmount()
								.compareTo(zero) == 0
						&& convertProcessedHsn.getUsgstutgstAmount()
								.compareTo(zero) == 0
						&& convertProcessedHsn.getUcessAmount()
								.compareTo(zero) == 0
						&& defaultValue(convertProcessedHsn.getUtotalValue())
								.compareTo(zero) == 0) {
				} else {
					verticalHsnList.add(convertProcessedHsn);
				}

			}
		}
		return verticalHsnList;
	}

	private static BigDecimal defaultValue(String taxableValue) {
		if (taxableValue == null || taxableValue.isEmpty())
			return BigDecimal.ZERO;
		return new BigDecimal(taxableValue);

	}

	private Gstr1AspVerticalHsnDto convertProcessedHsn(Object[] arr,
			Map<String, String> hsnMap) {
		Gstr1AspVerticalHsnDto obj = new Gstr1AspVerticalHsnDto();

		obj.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		String hsnCode = (arr[3] != null ? arr[3].toString() : null);
		obj.setHsn(hsnCode);
		if (!Strings.isNullOrEmpty(hsnCode)) {
			String hsnDesc = hsnMap.get(hsnCode);
			obj.setDescription(hsnDesc);
		}
		obj.setUqc(arr[5] != null ? arr[5].toString() : null);
		BigDecimal bigDecimalTotalQuantity = (BigDecimal) arr[6];
		if (bigDecimalTotalQuantity != null) {
			BigDecimal total = new BigDecimal(
					bigDecimalTotalQuantity.doubleValue());
			obj.setTotalQuantity(total);
		}
		obj.setTotalValue(arr[7] != null ? arr[7].toString() : null);
		BigDecimal bigDecimalTaxableValue = (BigDecimal) arr[8];
		if (bigDecimalTaxableValue != null) {
			BigDecimal taxable = new BigDecimal(
					bigDecimalTaxableValue.doubleValue());
			obj.setTaxableValue(taxable);
		}

		BigDecimal bigDecimalIGST = (BigDecimal) arr[9];
		if (bigDecimalIGST != null) {
			BigDecimal igst = new BigDecimal(bigDecimalIGST.doubleValue());
			obj.setIgstAmount(igst);
		}
		BigDecimal bigDecimalCGST = (BigDecimal) arr[10];
		if (bigDecimalCGST != null) {
			BigDecimal cgst = new BigDecimal(bigDecimalCGST.doubleValue());
			obj.setCgstAmount(cgst);
		}
		BigDecimal bigDecimalSGST = (BigDecimal) arr[11];
		if (bigDecimalSGST != null) {
			BigDecimal sgst = new BigDecimal(bigDecimalSGST.doubleValue());
			obj.setSgstutgstAmount(sgst);
		}
		BigDecimal bigDecimalCess = (BigDecimal) arr[12];
		if (bigDecimalCess != null) {
			BigDecimal cess = new BigDecimal(bigDecimalCess.doubleValue());
			obj.setCessAmount(cess);
		}

		BigDecimal bigDecimalTotal = (BigDecimal) arr[13];
		if (bigDecimalTotal != null) {
			BigDecimal total = new BigDecimal(bigDecimalTotal.doubleValue());
			obj.setUtotalQuantity(total);
		}

		obj.setUtotalValue(arr[14] != null ? arr[14].toString() : null);
		BigDecimal bigDecimalTaxable = (BigDecimal) arr[15];
		if (bigDecimalTaxable != null) {
			BigDecimal taxable = new BigDecimal(
					bigDecimalTaxable.doubleValue());
			obj.setUtaxableValue(taxable);
		}

		BigDecimal bigDecimalIGS = (BigDecimal) arr[16];
		if (bigDecimalIGS != null) {
			BigDecimal igst = new BigDecimal(bigDecimalIGS.doubleValue());
			obj.setUigstAmount(igst);
		}
		BigDecimal bigDecimalCGS = (BigDecimal) arr[17];
		if (bigDecimalCGS != null) {
			BigDecimal cgst = new BigDecimal(bigDecimalCGS.doubleValue());
			obj.setUcgstAmount(cgst);
		}
		BigDecimal bigDecimalSGS = (BigDecimal) arr[18];
		if (bigDecimalSGS != null) {
			BigDecimal sgst = new BigDecimal(bigDecimalSGS.doubleValue());
			obj.setUsgstutgstAmount(sgst);
		}
		BigDecimal bigDecimalCes = (BigDecimal) arr[19];
		if (bigDecimalCes != null) {
			BigDecimal cess = new BigDecimal(bigDecimalCes.doubleValue());
			obj.setUcessAmount(cess);
		}

		obj.setSaveStatus(arr[20] != null ? arr[20].toString() : null);
		obj.setgSTNRefID(arr[21] != null ? arr[21].toString() : null);
		obj.setgSTNRefIDTime(arr[22] != null ? arr[22].toString() : null);
		obj.setgSTNErrorcode(arr[23] != null ? arr[23].toString() : null);
		obj.setgSTNErrorDescription(
				arr[24] != null ? arr[24].toString() : null);
		obj.setSerialNo(arr[25] != null ? arr[25].toString() : null);
		obj.setRecordType(null);

		return obj;
	}

	private String createHsnSummaryQueryString(String buildQuery,
			String buildHeader, String buildheader, String supplyTypeQuery,
			String buildHdrSupplyType, String docTypeQuery) {
		StringBuilder bufferString = new StringBuilder();
		bufferString.append(
				"SELECT SUPPLIER_GSTIN,RETURN_PERIOD, DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_DESCRIPTION, ITM_UQC,");
		bufferString.append("SUM(ASP_ITM_QTY)ASP_ITM_QTY, ");
		bufferString.append("SUM(ASP_TOTAL_VALUE)ASP_TOTAL_VALUE, ");
		bufferString.append("SUM(ASP_TAXABLE_VALUE)ASP_TAXABLE_VALUE,");
		bufferString.append("SUM(ASP_IGST)ASP_IGST,SUM(ASP_CGST)ASP_CGST, ");
		bufferString.append("SUM(ASP_SGST)ASP_SGST,SUM(ASP_CESS)ASP_CESS, ");
		bufferString.append("SUM(UI_ITM_QTY)UI_ITM_QTY,");
		bufferString.append("SUM(UI_TOTAL_VALUE)UI_TOTAL_VALUE, ");
		bufferString.append(
				"SUM(UI_TAXABLE_VALUE)UI_TAXABLE_VALUE,SUM(UI_IGST)UI_IGST, ");
		bufferString.append("SUM(UI_CGST)UI_CGST,");
		bufferString.append(
				"SUM(UI_SGST)UI_SGST ,SUM(UI_CESS)UI_CESS , SAVE_STATUS, ");
		bufferString.append(
				"CASE WHEN SAVE_STATUS='IS_SAVED' THEN GSTIN_REF_ID ELSE NULL END AS GSTIN_REF_ID,");
		bufferString.append("CASE WHEN SAVE_STATUS='IS_SAVED' THEN ");
		bufferString.append(
				"GSTIN_REF_ID_TIME ELSE NULL END AS GSTIN_REF_ID_TIME, ");
		bufferString.append("GSTIN_ERROR_CODE, ");
		bufferString.append(
				"GSTIN_ERROR_DESCRIPTION,ROW_NUMBER () OVER ( ORDER BY ");
		bufferString.append("SUPPLIER_GSTIN) SNO,TAX_RATE  ");
		bufferString.append(" FROM( SELECT SUPPLIER_GSTIN,RETURN_PERIOD, ");
		bufferString.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_DESCRIPTION,");
		bufferString.append("ITM_UQC, SUM(ASP_ITM_QTY)ASP_ITM_QTY, ");
		bufferString.append("SUM(ASP_TOTAL_VALUE)ASP_TOTAL_VALUE, ");
		bufferString.append("SUM(ASP_TAXABLE_VALUE)ASP_TAXABLE_VALUE, ");
		bufferString.append("SUM(ASP_IGST)ASP_IGST,SUM(ASP_CGST)ASP_CGST, ");
		bufferString.append("SUM(ASP_SGST)ASP_SGST,SUM(ASP_CESS)ASP_CESS, ");
		bufferString.append("SUM(UI_ITM_QTY)UI_ITM_QTY,");
		bufferString.append("SUM(UI_TOTAL_VALUE)UI_TOTAL_VALUE, ");
		bufferString.append(
				"SUM(UI_TAXABLE_VALUE)UI_TAXABLE_VALUE,SUM(UI_IGST)UI_IGST, ");
		bufferString.append("SUM(UI_CGST)UI_CGST,");
		bufferString.append("SUM(UI_SGST)UI_SGST ,SUM(UI_CESS)UI_CESS , ");
		bufferString.append(
				"MAX(SAVE_STATUS)SAVE_STATUS, MAX(GSTIN_REF_ID)GSTIN_REF_ID,");
		bufferString.append("MAX(GSTIN_REF_ID_TIME)GSTIN_REF_ID_TIME, ");
		bufferString.append("MAX(GSTIN_ERROR_CODE)GSTIN_ERROR_CODE,");
		bufferString.append(
				"MAX(GSTIN_ERROR_DESCRIPTION)GSTIN_ERROR_DESCRIPTION,IFNULL(TAX_RATE,0) TAX_RATE ");
		bufferString.append(
				" FROM ( SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD, ");
		bufferString.append("ITM_HSNSAC,''ITM_DESCRIPTION, ITM_UQC, ");
		bufferString.append(
				"ASP_ITM_QTY,ASP_TOTAL_VALUE,ASP_TAXABLE_VALUE,ASP_IGST,ASP_CGST,ASP_SGST,");
		bufferString.append(
				"ASP_CESS, 0 UI_ITM_QTY,0 UI_TOTAL_VALUE,0 UI_TAXABLE_VALUE, ");
		bufferString.append(
				"0 UI_IGST,0 UI_CGST,0 UI_SGST,0 UI_CESS, SAVE_STATUS,");
		bufferString.append(
				"GSTIN_REF_ID, GSTIN_REF_ID_TIME, GSTIN_ERROR_CODE, GSTIN_ERROR_DESCRIPTION,IFNULL(TAX_RATE,0) TAX_RATE ");
		bufferString.append(
				" from ( SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,");
		bufferString.append(
				"ITM_HSNSAC, ITM_UQC, ASP_ITM_QTY,ASP_TOTAL_VALUE, ASP_TAXABLE_VALUE, ASP_IGST, ASP_CGST, ASP_SGST,");
		bufferString.append(
				"ASP_CESS, SAVE_STATUS,GSTIN_REF_ID,GSTIN_REF_ID_TIME, GSTIN_ERROR_CODE,GSTIN_ERROR_DESCRIPTION,IFNULL(TAX_RATE,0) TAX_RATE ");
		bufferString.append(
				" FROM ( SELECT SUPPLIER_GSTIN,RETURN_PERIOD, DERIVED_RET_PERIOD,ITM_HSNSAC,");
		bufferString.append(" ITM_UQC, SUM(ASP_ITM_QTY)ASP_ITM_QTY,");
		bufferString.append(
				"SUM(ASP_TOTAL_VALUE)ASP_TOTAL_VALUE, SUM(ASP_TAXABLE_VALUE)ASP_TAXABLE_VALUE, ");
		bufferString.append("SUM(ASP_IGST)ASP_IGST,SUM(ASP_CGST)ASP_CGST,");
		bufferString.append(
				"SUM(ASP_SGST)ASP_SGST,SUM(ASP_CESS)ASP_CESS,MAX(SAVE_STATUS)SAVE_STATUS,MAX(GSTIN_REF_ID)GSTIN_REF_ID,");
		bufferString.append(
				"MAX(GSTIN_REF_ID_TIME)GSTIN_REF_ID_TIME, MAX(GSTIN_ERROR_CODE)GSTIN_ERROR_CODE, ");
		bufferString.append(
				"MAX(GSTIN_ERROR_DESCRIPTION)GSTIN_ERROR_DESCRIPTION,IFNULL(TAX_RATE,0) TAX_RATE  ");
		bufferString.append(
				" FROM( SELECT SUPPLIER_GSTIN,RETURN_PERIOD, DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC, ");
		bufferString.append(
				"SUM(ASP_ITM_QTY)ASP_ITM_QTY, SUM(ASP_TOTAL_VALUE)ASP_TOTAL_VALUE,");
		bufferString.append(
				"SUM(ASP_TAXABLE_VALUE)ASP_TAXABLE_VALUE, SUM(ASP_IGST)ASP_IGST,SUM(ASP_CGST)ASP_CGST, ");
		bufferString.append(
				"SUM(ASP_SGST)ASP_SGST,SUM(ASP_CESS)ASP_CESS, SAVE_STATUS, GSTIN_REF_ID,GSTIN_REF_ID_TIME, ");
	
		bufferString.append(
				"GSTIN_ERROR_CODE, GSTIN_ERROR_DESCRIPTION,IFNULL(TAX_RATE,0) TAX_RATE FROM ( SELECT SUPPLIER_GSTIN,RETURN_PERIOD,");
		bufferString.append(
				"DERIVED_RET_PERIOD,ITM_HSNSAC, ITM_UQC, ASP_ITM_QTY,ASP_TOTAL_VALUE, ASP_TAXABLE_VALUE, ");
		bufferString.append(
				"ASP_IGST, ASP_CGST, ASP_SGST, ASP_CESS, SAVE_STATUS, GSTIN_REF_ID, ");
		bufferString.append(
				" GSTIN_REF_ID_TIME, '' GSTIN_ERROR_CODE, '' GSTIN_ERROR_DESCRIPTION,IFNULL(TAX_RATE,0) TAX_RATE ");
		bufferString.append(
				" FROM ( SELECT SUPPLIER_GSTIN,RETURN_PERIOD, DERIVED_RET_PERIOD,ITM_HSNSAC, ");
		bufferString.append(
				"ITM_UQC,SAVE_STATUS,GSTIN_REF_ID,GSTIN_REF_ID_TIME,ASP_ITM_QTY,ASP_TOTAL_VALUE, ASP_TAXABLE_VALUE, ASP_IGST, ASP_CGST, ");
		bufferString.append(
				"ASP_SGST, ASP_CESS,IFNULL(TAX_RATE,0) TAX_RATE FROM (SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD, ");
		bufferString.append(
				"DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,SAVE_STATUS,GSTIN_REF_ID,GSTIN_REF_ID_TIME, ");
		bufferString.append(
				"SUM(IFNULL(ITM_QTY_A,0)- IFNULL(ITM_QTY_S,0)) AS ASP_ITM_QTY,");
		bufferString.append(
				"SUM(IFNULL(TOTAL_VALUE_A,0)- IFNULL(TOTAL_VALUE_S,0)) AS ASP_TOTAL_VALUE,");
		bufferString.append(
				"SUM(IFNULL(TAXABLE_VALUE_A,0)- IFNULL(TAXABLE_VALUE_S,0)) AS ASP_TAXABLE_VALUE,");
		bufferString
				.append("SUM(IFNULL(IGST_A,0)- IFNULL(IGST_S,0)) AS ASP_IGST,");
		bufferString
				.append("SUM(IFNULL(CGST_A,0)- IFNULL(CGST_S,0)) AS ASP_CGST,");
		bufferString
				.append("SUM(IFNULL(SGST_A,0)- IFNULL(SGST_S,0)) AS ASP_SGST,");
		bufferString.append(
				"SUM(IFNULL(CESS_A,0)- IFNULL(CESS_S,0)) AS ASP_CESS,IFNULL(TAX_RATE,0) TAX_RATE ");
		bufferString.append(
				" FROM ( SELECT  DOC_KEY,DERIVED_RET_PERIOD, SUPPLIER_GSTIN,");
		bufferString.append(
				"RETURN_PERIOD,ITM_HSNSAC,ITM_UQC,SAVE_STATUS,GSTIN_REF_ID,GSTIN_REF_ID_TIME,CASE WHEN  DOC_TYPE IN('INV','BOS','DR') THEN ITM_QTY END ITM_QTY_A,");
		bufferString.append(
				"CASE WHEN DOC_TYPE IN('CR') THEN ITM_QTY END ITM_QTY_S,");
		bufferString.append(
				"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN TOTAL_VALUE END TOTAL_VALUE_A,");
		bufferString.append(
				"CASE WHEN DOC_TYPE IN('CR') THEN TOTAL_VALUE END TOTAL_VALUE_S,");
		bufferString.append(
				"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN TAXABLE_VALUE END TAXABLE_VALUE_A,");
		bufferString.append(
				"CASE WHEN DOC_TYPE IN('CR') THEN TAXABLE_VALUE END TAXABLE_VALUE_S,");
		bufferString.append(
				"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IGST END IGST_A,");
		bufferString
				.append("CASE WHEN DOC_TYPE IN('CR') THEN IGST END IGST_S,");
		bufferString.append(
				"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN CGST END CGST_A,");
		bufferString
				.append("CASE WHEN DOC_TYPE IN('CR') THEN CGST END CGST_S,");
		bufferString.append(
				"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN SGST END SGST_A,");
		bufferString
				.append("CASE WHEN DOC_TYPE IN('CR') THEN SGST END SGST_S,");
		bufferString.append(
				"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN CESS END CESS_A,");
		bufferString.append(
				"CASE WHEN DOC_TYPE IN('CR') THEN CESS END CESS_S,TAX_RATE ");
		bufferString.append(
				" FROM ( SELECT B.DOC_KEY,B.DERIVED_RET_PERIOD,B.SUPPLIER_GSTIN,B.RETURN_PERIOD,B.SUPPLY_TYPE,B.DOC_TYPE, ");
		bufferString.append(
				"ITM_HSNSAC,ITM_UQC,SAVE_STATUS,GSTIN_REF_ID,GSTIN_REF_ID_TIME,ITM_QTY,ECP.QUESTION_CODE,ECP.ANSWER,");
		bufferString.append(
				"CASE WHEN (TABLE_SECTION IN ('4A','5A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','9B') ");
		bufferString.append(
				"AND QUESTION_CODE='O1' AND ANSWER IN ('A','B','C') ) ");
		bufferString.append("THEN IFNULL(ONB_LINE_ITEM_AMT,0)  ");
		bufferString.append(
				"WHEN (TABLE_SECTION IN ('4A','5A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','9B') ");
		bufferString.append("AND QUESTION_CODE='O1' AND ANSWER IN ('D') ) ");
		bufferString.append(
				"THEN (IFNULL(TAXABLE_VALUE,0) +IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT,0)+IFNULL(OTHER_VALUES,0)) ");
		bufferString.append(" WHEN TABLE_SECTION ='4B' THEN ");
		bufferString.append(
				"IFNULL(TAXABLE_VALUE,0)WHEN (ECP.QUESTION_CODE='O21' AND ECP.ANSWER IN ('B') AND SUPPLY_TYPE IN ('EXPT','EXPWT')) ");
		bufferString.append(
				"THEN IFNULL(TAXABLE_VALUE,0) ELSE (IFNULL(TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT,0)+IFNULL(OTHER_VALUES,0)) ");
		bufferString.append(
				"END AS TOTAL_VALUE,TAXABLE_VALUE,IGST_AMT IGST,CGST_AMT CGST,SGST_AMT SGST,CESS_AMT CESS,TAX_RATE ");
		bufferString.append(" FROM ( SELECT ( HDR.DERIVED_RET_PERIOD ||'|'|| ");
		bufferString.append(
				"HDR.SUPPLIER_GSTIN ||'|'|| IFNULL(ITM_UQC,'OTH') ||'|'|| IFNULL(ITM_HSNSAC,'null' )) DOC_KEY, ");
		bufferString.append(
				"ITM.DERIVED_RET_PERIOD,HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD, ");
		bufferString.append(
				"HDR.SUPPLY_TYPE,DOC_TYPE, ITM.ITM_HSNSAC,IFNULL(ITM.ITM_UQC,'OTH') AS ITM_UQC,(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN 'IS_SAVED' ELSE 'NOT_SAVED' END) SAVE_STATUS, ");
		bufferString.append(
				" (CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN GSTNBATCH.GSTN_SAVE_REF_ID ELSE NULL END) GSTIN_REF_ID, ");
		bufferString.append(
				" (CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN GSTNBATCH.BATCH_DATE ELSE NULL END) GSTIN_REF_ID_TIME, ");
		//bufferString.append("IFNULL(ITM_QTY,0) AS ITM_QTY, IFNULL(ONB_LINE_ITEM_AMT,0) AS TOTAL_VALUE, ");
		
		
		bufferString.append(
				"CASE WHEN 'B' =:configAnswer AND DOC_TYPE='CR' AND IFNULL(CRDR_REASON,'NA')  <>'SR' THEN 0 ");
		bufferString.append("WHEN 'B' =:configAnswer AND DOC_TYPE='DR' AND IFNULL(CRDR_REASON,'NA')  <>'SR' THEN 0 ");
		bufferString.append("ELSE IFNULL(ITM_QTY,0) END AS ITM_QTY, IFNULL(ONB_LINE_ITEM_AMT,0) AS TOTAL_VALUE, ");
		
		
		bufferString.append(
				"IFNULL(ITM.TAXABLE_VALUE,0) AS TAXABLE_VALUE,IFNULL(ITM.IGST_AMT,0) AS IGST_AMT,");
		bufferString.append(
				"IFNULL(ITM.CGST_AMT,0) AS CGST_AMT, IFNULL(ITM.SGST_AMT,0) AS SGST_AMT, ");
		bufferString.append(
				"IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0) AS CESS_AMT,");
		bufferString.append(
				"HDR.TAX_DOC_TYPE,HDR.TABLE_SECTION,ONB_LINE_ITEM_AMT ,ITM.OTHER_VALUES,TAX_RATE FROM ANX_OUTWARD_DOC_HEADER_1A ");
		bufferString.append(
				"HDR INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM ON HDR.ID = ITM.DOC_HEADER_ID ");
		bufferString.append(
				"AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT OUTER JOIN ");
		bufferString.append(
				"FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ");
		bufferString.append(
				"ON GSTNBATCH.ID = HDR.BATCH_ID WHERE ASP_INVOICE_STATUS = 2 ");
		bufferString.append(
				"AND COMPLIANCE_APPLICABLE = TRUE AND HDR.RETURN_TYPE='GSTR1A'");
		bufferString.append(
				"AND HDR.IS_DELETE=false  AND (TABLE_SECTION NOT IN('8','8A','8B','8C','8D')  ");
		
		bufferString.append(buildHdrSupplyType);
		bufferString.append(" ) ");
		bufferString.append(buildHeader);
		bufferString.append(
				" UNION ALL SELECT  (HDR.DERIVED_RET_PERIOD ||'|'|| HDR.SUPPLIER_GSTIN ||'|'|| ");
		bufferString.append(
				"IFNULL(NEW_UOM,'OTH') ||'|'|| IFNULL(NEW_RATE,0) ||'|'|| IFNULL(NEW_HSNORSAC,'null' )) DOC_KEY,");
		bufferString.append(
				"HDR.DERIVED_RET_PERIOD,HDR.SUPPLIER_GSTIN, HDR.RETURN_PERIOD, ");
		bufferString.append(
				" 'TAX' SUPPLY_TYPE, 'INV' DOC_TYPE, NEW_HSNORSAC AS ITM_HSNSAC,IFNULL(NEW_UOM,'OTH') AS ITM_UQC,(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE  THEN 'IS_SAVED' ELSE 'NOT_SAVED' END) SAVE_STATUS, ");
		bufferString.append(
				" (CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN GSTNBATCH.GSTN_SAVE_REF_ID ELSE NULL END) GSTIN_REF_ID, ");
		bufferString.append(
				" (CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN GSTNBATCH.BATCH_DATE ELSE NULL END) GSTIN_REF_ID_TIME, ");
		bufferString.append(
				"IFNULL(NEW_QNT,0) AS ITM_QTY,IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)");
		bufferString.append(
				" + IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0) +IFNULL(CESS_AMT,0) AS TOTAL_VALUE,");
		bufferString.append(
				"IFNULL(NEW_TAXABLE_VALUE,0) AS TAXABLE_VALUE,IFNULL(IGST_AMT,0) AS IGST_AMT,IFNULL(CGST_AMT,0) AS CGST_AMT, ");
		bufferString.append(
				"IFNULL(SGST_AMT,0) AS SGST_AMT, IFNULL(CESS_AMT,0) AS CESS_AMT,'B2CS' AS TAX_DOC_TYPE,");
		bufferString.append(
				" '7' TABLE_SECTION,IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+ IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0) ");
		bufferString.append(
				"+IFNULL(CESS_AMT,0) ONB_LINE_ITEM_AMT,0.00 OTHER_VALUES,NEW_RATE TAX_RATE  FROM GSTR1A_PROCESSED_B2CS ");
		bufferString.append(
				"HDR LEFT OUTER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID ");
		bufferString.append(
				"LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = HDR.BATCH_ID ");
		bufferString.append(
				" WHERE  HDR.IS_DELETE = FALSE AND IS_AMENDMENT=FALSE ");
		bufferString.append(buildHeader);
		bufferString.append(
				" UNION ALL SELECT (HDR.DERIVED_RET_PERIOD ||'|'|| HDR.SUPPLIER_GSTIN ||'|'|| ");
		bufferString.append(
				"IFNULL(ITM_UQC,'OTH') ||'|'|| IFNULL(ITM_HSNSAC,'null' )) DOC_KEY,");
		bufferString.append(
				"HDR.DERIVED_RET_PERIOD,HDR.SUPPLIER_GSTIN, HDR.RETURN_PERIOD, SUPPLY_TYPE,'INV' DOC_TYPE, ");
		bufferString.append(
				"ITM_HSNSAC,IFNULL(ITM_UQC,'OTH') AS ITM_UQC,(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN 'IS_SAVED' ELSE 'NOT_SAVED' END) SAVE_STATUS, ");
		bufferString.append(
				" (CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN GSTNBATCH.GSTN_SAVE_REF_ID ELSE NULL END) GSTIN_REF_ID, ");
		bufferString.append(
				" (CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN GSTNBATCH.BATCH_DATE ELSE NULL END) GSTIN_REF_ID_TIME, ");
		bufferString.append("IFNULL(ITM_QTY,0) AS ITM_QTY, ");
		bufferString.append(
				"IFNULL(TAXABLE_VALUE,0) AS TOTAL_VALUE, IFNULL(TAXABLE_VALUE,0) AS TAXABLE_VALUE,");
		bufferString.append(
				"0 AS IGST_AMT,0 CGST_AMT,0 AS SGST_AMT,0 AS CESS_AMT ,");
		bufferString.append(
				" 'NILEXTNON' AS TAX_DOC_TYPE, '8' TABLE_SECTION,IFNULL(TAXABLE_VALUE,0) ONB_LINE_ITEM_AMT,0.00 OTHER_VALUES,0.00 TAX_RATE ");
		bufferString
				.append(" FROM GSTR1A_SUMMARY_NILEXTNON HDR LEFT OUTER JOIN ");
		bufferString.append(
				"FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ");
		bufferString.append(
				"ON GSTNBATCH.ID = HDR.BATCH_ID WHERE HDR.IS_DELETE = FALSE ");
		bufferString.append(supplyTypeQuery);
		bufferString.append(buildHeader);
		bufferString.append(" ) B ");
		bufferString.append(
				"INNER JOIN GSTIN_INFO GSTN ON B.SUPPLIER_GSTIN = GSTN.GSTIN ");
		bufferString.append(
				"INNER JOIN ENTITY_CONFG_PRMTR ECP ON ECP.ENTITY_ID= GSTN.ENTITY_ID  ");
		bufferString.append(
				"AND ECP.IS_ACTIVE=TRUE AND ECP.IS_DELETE=FALSE AND ECP.QUESTION_CODE IN ('O21') ");
		
		bufferString.append(docTypeQuery);

		bufferString.append(
				" )) GROUP BY DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD, DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,SAVE_STATUS,GSTIN_REF_ID,GSTIN_REF_ID_TIME,TAX_RATE) ");
		bufferString.append(
				" WHERE ( ITM_HSNSAC!='0' OR ITM_UQC!='0' OR ASP_TOTAL_VALUE !='0' OR ASP_TAXABLE_VALUE!='0' ");
		bufferString.append(
				"OR ASP_IGST!='0' OR ASP_CGST!='0' OR ASP_SGST!='0' OR ASP_CESS!='0') ) ");
		bufferString.append(
				" UNION ALL SELECT SUPPLIER_GSTIN, RETURN_PERIOD, DERIVED_RET_PERIOD, ITM_HSNSAC, ITM_UQC, ");
		bufferString.append(
				"0 ASP_ITM_QTY, 0 ASP_TOTAL_VALUE, 0 ASP_TAXABLE_VALUE, 0 ASP_IGST, 0 ASP_CGST, ");
		bufferString.append(
				"0 ASP_SGST, 0 ASP_CESS, SAVE_STATUS,GSTIN_REF_ID, GSTIN_REF_ID_TIME, ");
		bufferString.append(
				"GSTIN_ERROR_CODE, GSTIN_ERROR_DESCRIPTION,TAX_RATE  FROM( SELECT DERIVED_RET_PERIOD, ");
		bufferString.append(
				"SUPPLIER_GSTIN,RETURN_PERIOD,ITM_HSNSAC, ITM_UQC,ROWNUM_DT, SAVE_STATUS, GSTIN_REF_ID,");
		bufferString.append(
				"GSTIN_REF_ID_TIME,GSTIN_ERROR_CODE,GSTIN_ERROR_DESCRIPTION,TAX_RATE from( SELECT DERIVED_RET_PERIOD, ");
		bufferString.append(
				"SUPPLIER_GSTIN,RETURN_PERIOD,ITM_HSNSAC, ITM_UQC, ROW_NUMBER() OVER(PARTITION BY ITM_HSNSAC,");
		bufferString.append(
				"ITM_UQC ORDER BY CREATED_ON DESC) AS ROWNUM_DT, SAVE_STATUS, GSTIN_REF_ID,GSTIN_REF_ID_TIME,");
		bufferString.append(
				"GSTIN_ERROR_CODE,GSTIN_ERROR_DESCRIPTION,TAX_RATE FROM( SELECT HDR.TAX_DOC_TYPE,HDR.DERIVED_RET_PERIOD,");
		bufferString.append(
				" HDR.SUPPLIER_GSTIN, HDR.CREATED_ON,HDR.RETURN_PERIOD,ITM_HSNSAC, IFNULL(ITM_UQC,'OTH') ITM_UQC, ");
		bufferString.append(
				"(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN 'IS_SAVED' ELSE 'NOT_SAVED' END) SAVE_STATUS, ");
		bufferString.append(
				"(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN GSTNBATCH.GSTN_SAVE_REF_ID ELSE NULL END) GSTIN_REF_ID, ");
		bufferString.append(
				"(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN GSTNBATCH.BATCH_DATE ELSE NULL END) GSTIN_REF_ID_TIME, ");
		bufferString.append(
				"(CASE WHEN GSTN_ERROR = TRUE AND HDR.IS_DELETE = FALSE THEN IFNULL( GSTNBATCH.ERROR_CODE,'') ELSE NULL END) GSTIN_ERROR_CODE, ");
		bufferString.append(
				"(CASE WHEN GSTN_ERROR = TRUE AND HDR.IS_DELETE = FALSE THEN IFNULL( GSTNBATCH.ERROR_DESC,'') ELSE NULL END) GSTIN_ERROR_DESCRIPTION,TAX_RATE ");
		bufferString.append(
				" FROM ANX_OUTWARD_DOC_HEADER_1A HDR INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM ON HDR.ID = ITM.DOC_HEADER_ID LEFT OUTER JOIN ");
		bufferString.append(
				"FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = HDR.BATCH_ID ");
		bufferString.append(
				"WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE=TRUE AND HDR.IS_DELETE=FALSE AND HDR.RETURN_TYPE='GSTR1A' ");
		bufferString.append(
				"AND (TABLE_SECTION NOT IN ('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','4B','6A')  ");
		bufferString.append(" ) ");

		bufferString.append(buildHeader);
		bufferString.append(" AND GSTNBATCH.RETURN_TYPE='GSTR1A' ");
		bufferString.append(
				" UNION ALL SELECT 'B2CS' AS TAX_DOC_TYPE,HDR.DERIVED_RET_PERIOD, HDR.SUPPLIER_GSTIN,HDR.CREATED_ON,HDR.RETURN_PERIOD,");
		bufferString.append(
				"NEW_HSNORSAC AS ITM_HSNSAC,IFNULL(NEW_UOM,'OTH') AS ITM_UQC, (CASE WHEN IS_SAVED_TO_GSTN = TRUE ");
		bufferString.append(
				"AND HDR.IS_DELETE = FALSE THEN 'IS_SAVED' ELSE 'NOT_SAVED' END) SAVE_STATUS, ");
		bufferString.append(
				"(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN GSTNBATCH.GSTN_SAVE_REF_ID ELSE NULL END) ");
		bufferString.append(
				" GSTIN_REF_ID, (CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN GSTNBATCH.BATCH_DATE ELSE NULL END) ");
		bufferString.append(
				"GSTIN_REF_ID_TIME, (CASE WHEN GSTN_ERROR = TRUE AND HDR.IS_DELETE = FALSE THEN IFNULL( GSTNBATCH.ERROR_CODE,'') ELSE NULL END) ");
		bufferString.append(
				"GSTIN_ERROR_CODE, (CASE WHEN GSTN_ERROR = TRUE AND HDR.IS_DELETE = FALSE THEN IFNULL( GSTNBATCH.ERROR_DESC,'') ELSE NULL END) ");
		bufferString.append(
				"GSTIN_ERROR_DESCRIPTION_ASP,NEW_RATE TAX_RATE FROM GSTR1A_PROCESSED_B2CS HDR LEFT OUTER JOIN FILE_STATUS FIL ");
		bufferString.append(
				"ON HDR.FILE_ID=FIL.ID LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = HDR.BATCH_ID ");
		bufferString
				.append(" WHERE HDR.IS_DELETE = FALSE AND IS_AMENDMENT=FALSE ");
		bufferString.append(buildHeader);
		bufferString.append(
				"UNION ALL SELECT 'NILEXTNON' AS TAX_DOC_TYPE,HDR.DERIVED_RET_PERIOD, HDR.SUPPLIER_GSTIN,HDR.CREATED_ON, ");
		bufferString.append(
				"HDR.RETURN_PERIOD,ITM_HSNSAC, IFNULL(ITM_UQC,'OTH') ITM_UQC, (CASE WHEN IS_SAVED_TO_GSTN = TRUE ");
		bufferString.append(
				"AND HDR.IS_DELETE = FALSE THEN 'IS_SAVED' ELSE 'NOT_SAVED' END) SAVE_STATUS, (CASE WHEN IS_SAVED_TO_GSTN = TRUE ");
		bufferString.append(
				"AND HDR.IS_DELETE = FALSE THEN GSTNBATCH.GSTN_SAVE_REF_ID ELSE NULL END) GSTIN_REF_ID, ");
		bufferString.append(
				"(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HDR.IS_DELETE = FALSE THEN GSTNBATCH.BATCH_DATE ELSE NULL END) ");
		bufferString.append(
				"GSTIN_REF_ID_TIME, (CASE WHEN GSTN_ERROR = TRUE AND HDR.IS_DELETE = FALSE ");
		bufferString.append(
				"THEN IFNULL( GSTNBATCH.ERROR_CODE,'') ELSE NULL END) GSTIN_ERROR_CODE, ");
		bufferString.append(
				"(CASE WHEN GSTN_ERROR = TRUE AND HDR.IS_DELETE = FALSE THEN IFNULL( GSTNBATCH.ERROR_DESC,'') ELSE NULL END) ");
		bufferString.append(
				"GSTIN_ERROR_DESCRIPTION_ASP,0.00 TAX_RATE  FROM GSTR1A_SUMMARY_NILEXTNON HDR LEFT OUTER JOIN ");
		bufferString.append(
				"FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON ");
		bufferString.append(
				"GSTNBATCH.ID = HDR.BATCH_ID WHERE HDR.IS_DELETE = FALSE ");
		bufferString.append(buildHeader);
		bufferString.append(")) WHERE ROWNUM_DT=1 )) ");
		bufferString.append(
				" GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD, DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC ,SAVE_STATUS, ");
		bufferString.append(
				"GSTIN_REF_ID, GSTIN_REF_ID_TIME, GSTIN_ERROR_CODE, GSTIN_ERROR_DESCRIPTION,TAX_RATE) ");
		bufferString.append(
				" GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD, DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,TAX_RATE) ) ");
		bufferString.append(
				" UNION ALL SELECT SUPPLIER_GSTIN, RETURN_PERIOD,DERIVED_RET_PERIOD,ITM_HSNSAC,''ITM_DESCRIPTION, ");
		bufferString.append(
				"ITM_UQC, 0 ASP_ITM_QTY, 0 ASP_TOTAL_VALUE, 0 ASP_TAXABLE_VALUE,0 ASP_IGST,0 ASP_CGST,0 ASP_SGST,");
		bufferString.append(
				"0 ASP_CESS, UI_ITM_QTY,UI_TOTAL_VALUE,UI_TAXABLE_VALUE,UI_IGST, UI_CGST,UI_SGST,UI_CESS, SAVE_STATUS, ");
		bufferString.append(
				"GSTIN_REF_ID,GSTIN_REF_ID_TIME,GSTIN_ERROR_CODE,GSTIN_ERROR_DESCRIPTION_ASP,TAX_RATE ");
		bufferString.append(
				" FROM ( SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,ITM_HSNSAC, ");
		bufferString.append(
				"IFNULL(ITM_UQC,'OTH') ITM_UQC, SUM( ITM_QTY) AS UI_ITM_QTY, ");
		bufferString.append(
				"SUM( TOTAL_VALUE)AS UI_TOTAL_VALUE, SUM( TAXABLE_VALUE) AS UI_TAXABLE_VALUE, ");
		bufferString.append(
				"SUM(IGST) AS UI_IGST,SUM(CGST) AS UI_CGST,SUM(SGST) AS UI_SGST, SUM(CESS) AS UI_CESS,");
		bufferString.append(
				"SAVE_STATUS,GSTIN_REF_ID,GSTIN_REF_ID_TIME,GSTIN_ERROR_CODE, GSTIN_ERROR_DESCRIPTION_ASP,TAX_RATE ");
		bufferString.append(
				" FROM ( SELECT HSN.SUPPLIER_GSTIN,HSN.RETURN_PERIOD, HSN.DERIVED_RET_PERIOD, ");
		bufferString.append(
				"HSNSAC AS ITM_HSNSAC,IFNULL(UQC,'OTH') ITM_UQC, IFNULL(SUM(ITM_QTY),0) ITM_QTY, ");
		bufferString.append(
				"IFNULL(SUM(TOTAL_VALUE),0) TOTAL_VALUE, IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE, ");
		bufferString.append(
				"IFNULL(SUM(IGST_AMT),0) IGST,IFNULL(SUM(CGST_AMT),0) CGST, IFNULL(SUM(SGST_AMT),0) SGST,IFNULL(SUM(CESS_AMT),0) CESS , ");
		bufferString.append(
				"(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HSN.IS_DELETE = FALSE THEN 'IS_SAVED' ELSE 'NOT_SAVED' END) SAVE_STATUS, ");
		bufferString.append(
				"(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HSN.IS_DELETE = FALSE THEN GSTNBATCH.GSTN_SAVE_REF_ID ELSE NULL END) GSTIN_REF_ID, ");
		bufferString.append(
				"(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HSN.IS_DELETE = FALSE THEN GSTNBATCH.BATCH_DATE ELSE NULL END) GSTIN_REF_ID_TIME, ");
		bufferString.append(
				"(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HSN.IS_DELETE = FALSE THEN IFNULL( GSTNBATCH.ERROR_CODE,'') ELSE NULL END) GSTIN_ERROR_CODE, ");
		bufferString.append(
				"(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND HSN.IS_DELETE = FALSE THEN IFNULL( GSTNBATCH.ERROR_DESC,'') ELSE NULL END) ");
		bufferString.append(
				"GSTIN_ERROR_DESCRIPTION_ASP,TAX_RATE  FROM GSTR1A_USERINPUT_HSNSAC HSN LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ");
		bufferString.append(
				"ON GSTNBATCH.ID = HSN.BATCH_ID WHERE HSN.IS_DELETE=FALSE ");
		bufferString.append(buildQuery);
		bufferString.append(
				" GROUP BY HSN.SUPPLIER_GSTIN, HSN.RETURN_PERIOD,HSN.DERIVED_RET_PERIOD,HSNSAC,UQC, ");
		bufferString.append(
				"IS_SAVED_TO_GSTN,HSN.IS_DELETE , GSTN_SAVE_REF_ID,BATCH_DATE,ERROR_CODE,ERROR_DESC,TAX_RATE ) ");
		bufferString.append(
				" GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD, ITM_HSNSAC, ITM_UQC , ");
		bufferString.append(
				"SAVE_STATUS,GSTIN_REF_ID,GSTIN_REF_ID_TIME,GSTIN_ERROR_CODE,GSTIN_ERROR_DESCRIPTION_ASP,TAX_RATE )) ");
		bufferString.append(
				" GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD, DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_DESCRIPTION,ITM_UQC,TAX_RATE  ) ");
		bufferString.append(
				" GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD, DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_DESCRIPTION,ITM_UQC, ");
		bufferString.append(
				"SAVE_STATUS,GSTIN_REF_ID,GSTIN_REF_ID_TIME,GSTIN_ERROR_CODE, GSTIN_ERROR_DESCRIPTION ,TAX_RATE ");
		return bufferString.toString();
	}

	public String loadOnStartSupplyType(List<Long> entityId) {

		StringBuilder buildQuery = new StringBuilder();

		if (entityId.size() > 0) {

			buildQuery.append("AND ENTITY_ID IN (:entityId)");

		}

		String sql = "SELECT ANSWER FROM ENTITY_CONFG_PRMTR "
				+ "WHERE QUESTION_CODE = 'O11' " + "AND IS_DELETE = FALSE "
				+ buildQuery.toString();

		Query q = entityManager.createNativeQuery(sql);

		q.setParameter("entityId", entityId);

		@SuppressWarnings("unchecked")
		List<Object> list = q.getResultList();

		String supplyType = null;
		if (list != null && !list.isEmpty()) {
			supplyType = (String) list.get(0);
		}

		return supplyType;
	}

	public String loadOnStartDocType(List<Long> entityId) {

		StringBuilder buildQuery = new StringBuilder();

		if (entityId.size() > 0) {

			buildQuery.append(" AND ENTITY_ID IN (:entityId) ");

		}
		String sql = "SELECT ANSWER FROM ENTITY_CONFG_PRMTR "
				+ "WHERE QUESTION_CODE = 'O20' " + "AND IS_DELETE = FALSE "
				+ buildQuery.toString();

		Query q = entityManager.createNativeQuery(sql);

		q.setParameter("entityId", entityId);

		@SuppressWarnings("unchecked")
		List<Object> list = q.getResultList();

		String supplyType = null;
		if (list != null && !list.isEmpty()) {
			supplyType = (String) list.get(0);
		}

		return supplyType;
	}
}
