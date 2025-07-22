package com.ey.advisory.app.services.daos.gstr6a;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataRequestDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataResponseDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;

/**
 * 
 * @author SriBhavya
 *
 */
@Component("Gstr6ASummaryDataDaoImpl")
public class Gstr6ASummaryDataDaoImpl implements Gstr6ASummaryDataDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr6ASummaryDataResponseDto> getGstr6ASummaryData(
			Gstr6ASummaryDataRequestDto criteria) {
		
		String fromPeriod = criteria.getFromPeriod(); 
		String toPeriod = criteria.getToPeriod();
		List<String> tableSection = criteria.getTableType();
		if(!tableSection.isEmpty() && tableSection != null){
			tableSection.replaceAll(String::toUpperCase);
		}
		List<String> docType = criteria.getDocType();
		
		if(docType.contains("CR")){
			docType.remove("CR");
			docType.add("C");
		} 
		if(docType.contains("DR")){
			docType.remove("DR");
			docType.add("D");
		} 
		
		Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();

		String GSTIN = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					GSTIN = key;
					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}

		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildQuery1 = new StringBuilder();
		if (GSTIN != null && !GSTIN.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			buildQuery.append(" AND CTIN IN :gstinList ");
		}
		if (tableSection != null && !tableSection.isEmpty()) {
			buildQuery1.append(" WHERE TABLE_SECTION IN :tableSection ");
		}
		if (docType != null && !docType.isEmpty()) {
			if (tableSection != null && !tableSection.isEmpty()) {
				buildQuery1.append(" AND DOC_TYPE IN :docType ");
			} else {
				buildQuery1.append(" WHERE DOC_TYPE IN :docType ");
			}
		}
		if (StringUtils.isNotBlank(fromPeriod)
				&& StringUtils.isNotBlank(toPeriod)) {
			buildQuery.append(
					" AND DERIVED_RET_PERIOD BETWEEN :fromPeriod AND :toPeriod ");

		}

		String queryStr = createQueryString(buildQuery,buildQuery1);
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			q.setParameter("gstinList", gstinList);
		}
		if (tableSection != null && !tableSection.isEmpty()) {
			q.setParameter("tableSection", tableSection);
		}
		if (docType != null && !docType.isEmpty()) {
			q.setParameter("docType", docType);
		}
		if (StringUtils.isNotBlank(fromPeriod)
				&& StringUtils.isNotBlank(toPeriod)) {
			q.setParameter("fromPeriod",
					GenUtil.convertTaxPeriodToInt(fromPeriod));
			q.setParameter("toPeriod",
					GenUtil.convertTaxPeriodToInt(toPeriod));
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertGetSummaryData(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr6ASummaryDataResponseDto convertGetSummaryData(Object[] arr) {
		Gstr6ASummaryDataResponseDto responseCriteria = new Gstr6ASummaryDataResponseDto();
		responseCriteria.setTable((String) arr[8]);
		responseCriteria.setDocType((String) arr[1]);
		if (arr[0] == null || arr[0].toString().isEmpty()) {
			responseCriteria.setCount(BigInteger.ZERO);
		} else {
			responseCriteria.setCount(GenUtil.getBigInteger(arr[0]));
		}
		if (arr[7] == null || arr[7].toString().isEmpty()) {
			responseCriteria.setInVoiceVal(BigDecimal.ZERO);
		} else {
			responseCriteria.setInVoiceVal((BigDecimal) arr[7]);
		}
		if (arr[2] == null || arr[2].toString().isEmpty()) {
			responseCriteria.setTaxableValue(BigDecimal.ZERO);
		} else {
			responseCriteria.setTaxableValue((BigDecimal) arr[2]);
		}
		if (arr[9] == null || arr[9].toString().isEmpty()) {
			responseCriteria.setTotalTax(BigDecimal.ZERO);
		} else {
			responseCriteria.setTotalTax((BigDecimal) arr[9]);
		}
		if (arr[3] == null || arr[3].toString().isEmpty()) {
			responseCriteria.setIgst(BigDecimal.ZERO);
		} else {
			responseCriteria.setIgst((BigDecimal) arr[3]);
		}
		if (arr[4] == null || arr[4].toString().isEmpty()) {
			responseCriteria.setCgst(BigDecimal.ZERO);
		} else {
			responseCriteria.setCgst((BigDecimal) arr[4]);
		}
		if (arr[5] == null || arr[5].toString().isEmpty()) {
			responseCriteria.setSgst(BigDecimal.ZERO);
		} else {
			responseCriteria.setSgst((BigDecimal) arr[5]);
		}
		if (arr[6] == null || arr[6].toString().isEmpty()) {
			responseCriteria.setCess(BigDecimal.ZERO);
		} else {
			responseCriteria.setCess((BigDecimal) arr[6]);
		}
		return responseCriteria;
	}

	private String createQueryString(StringBuilder buildQuery, StringBuilder buildQuery1) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM (SELECT COUNT(ID) AS ID,");
		sb.append("'INV' AS DOC_TYPE,");
		sb.append("SUM(IFNULL(TAXABLE_VALUE,0)) AS TAXABLE_VALUE1,");
		sb.append("SUM(IFNULL(IGST_AMT,0)) AS IGST_AMT1,");
		sb.append("SUM(IFNULL(CGST_AMT,0)) AS CGST_AMT1,");
		sb.append("SUM(IFNULL(SGST_AMT,0)) AS SGST_AMT1,");
		sb.append("SUM(IFNULL(CESS_AMT,0)) AS CESS_AMT1,");
		sb.append("SUM(IFNULL(DOC_AMT,0)) AS DOC_AMT1,");
		sb.append("'B2B' AS TABLE_SECTION,");
		sb.append("SUM(IFNULL(IGST_AMT,0) + IFNULL(SGST_AMT,0) +");
		sb.append(" IFNULL(CGST_AMT,0) +IFNULL(CESS_AMT,0)) AS TOTAL_TAX");
		sb.append(" FROM GETGSTR6A_B2B_HEADER");
		sb.append(" WHERE IS_DELETE = FALSE");
		sb.append(buildQuery);
		sb.append(" GROUP BY CTIN ");
		sb.append(" UNION ALL ");
		sb.append("SELECT COUNT(ID) AS ID,'INV' AS DOC_TYPE,");
		sb.append("SUM(IFNULL(TAXABLE_VALUE,0)) AS TAXABLE_VALUE1 ,");
		sb.append("SUM(IFNULL(IGST_AMT,0)) AS IGST_AMT1,");
		sb.append("SUM(IFNULL(CGST_AMT,0)) AS CGST_AMT1,");
		sb.append("SUM(IFNULL(SGST_AMT,0)) AS SGST_AMT1,");
		sb.append("SUM(IFNULL(CESS_AMT,0)) AS CESS_AMT1,");
		sb.append(
				"SUM(IFNULL(DOC_AMT,0)) AS DOC_AMT1,'B2BA' AS TABLE_SECTION,");
		sb.append("SUM(IFNULL(IGST_AMT,0) + IFNULL(SGST_AMT,0) +");
		sb.append(" IFNULL(CGST_AMT,0) +IFNULL(CESS_AMT,0)) AS TOTAL_TAX ");
		sb.append(" FROM GETGSTR6A_B2BA_HEADER ");
		sb.append(" WHERE IS_DELETE = FALSE ");
		sb.append(buildQuery);
		sb.append(" GROUP BY CTIN ");
		sb.append(" UNION ALL ");
		sb.append("SELECT COUNT(ID) AS ID,NOTE_TYPE AS DOC_TYPE,");
		sb.append("SUM(IFNULL(TAXABLE_VALUE,0)) AS TAXABLE_VALUE1 ,");
		sb.append("SUM(IFNULL(IGST_AMT,0)) AS IGST_AMT1,");
		sb.append("SUM(IFNULL(CGST_AMT,0)) AS CGST_AMT1,");
		sb.append("SUM(IFNULL(SGST_AMT,0)) AS SGST_AMT1,");
		sb.append("SUM(IFNULL(CESS_AMT,0)) AS CESS_AMT1,");
		sb.append("SUM(IFNULL(INV_VALUE,0)) AS DOC_AMT1,");
		sb.append("'CDN' AS TABLE_SECTION,");
		sb.append("SUM(IFNULL(IGST_AMT,0) + IFNULL(SGST_AMT,0)");
		sb.append(" + IFNULL(CGST_AMT,0) +IFNULL(CESS_AMT,0)) AS TOTAL_TAX");
		sb.append(" FROM GETGSTR6A_CDN_HEADER ");
		sb.append("WHERE IS_DELETE = FALSE ");
		sb.append(buildQuery);
		sb.append(" GROUP BY CTIN,NOTE_TYPE ");
		sb.append("UNION ALL ");
		sb.append("SELECT COUNT(ID) AS ID,");
		sb.append("NOTE_TYPE AS DOC_TYPE,");
		sb.append("SUM(IFNULL(TAXABLE_VALUE,0)) AS TAXABLE_VALUE1 ,");
		sb.append("SUM(IFNULL(IGST_AMT,0)) AS IGST_AMT1,");
		sb.append("SUM(IFNULL(CGST_AMT,0)) AS CGST_AMT1,");
		sb.append("SUM(IFNULL(SGST_AMT,0)) AS SGST_AMT1,");
		sb.append("SUM(IFNULL(CESS_AMT,0)) AS CESS_AMT1,");
		sb.append("SUM(IFNULL(INV_VALUE,0)) AS DOC_AMT1,");
		sb.append("'CDNA' AS TABLE_SECTION,");
		sb.append("SUM(IFNULL(IGST_AMT,0) + IFNULL(SGST_AMT,0) +");
		sb.append(" IFNULL(CGST_AMT,0) +IFNULL(CESS_AMT,0)) AS TOTAL_TAX");
		sb.append(" FROM GETGSTR6A_CDNA_HEADER ");
		sb.append(" WHERE IS_DELETE = FALSE ");
		sb.append(buildQuery);
		sb.append(" GROUP BY CTIN,NOTE_TYPE ");
		sb.append(")");
		sb.append(buildQuery1);
		return sb.toString();
	}

}
