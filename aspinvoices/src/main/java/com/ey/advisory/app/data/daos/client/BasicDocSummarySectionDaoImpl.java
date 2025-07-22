package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.BaseGstr1SummaryEntity;
import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("BasicDocSummarySectionDaoImpl")
public class BasicDocSummarySectionDaoImpl
		implements BasicDocSummarySectionDao {

	private static final Map<String, String> SECTION_VIEW_MAP = new HashMap<>();

	static {
		// Initialize the static array.
		SECTION_VIEW_MAP.put("B2B", "GST_VIEW/B2B_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("B2CL", "GST_VIEW/B2CL_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("B2CS", "GST_VIEW/B2CS_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("EXP", "GST_VIEW/EXP_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("HSN", "GST_VIEW/HSN_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("CDNR", "GST_VIEW/CDNR_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("CDNRA", "GST_VIEW/CDNRA_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("CDNUR", "GST_VIEW/CDNUR_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("CDNURA", "GST_VIEW/CDNURA_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("B2CSA", "GST_VIEW/B2CSA_GSTR1SUMMARY_VIEW");
		
		SECTION_VIEW_MAP.put("B2BA", "GST_VIEW/B2BA_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("B2CLA", "GST_VIEW/B2CLA_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("EXPA", "GST_VIEW/EXPA_GSTR1SUMMARY_VIEW");
		
		/*SECTION_VIEW_MAP.put("AT", "GST_VIEW/ADVADJ_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("ATA", "GST_VIEW/ADVADJ_AMD_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("TXPD", "GST_VIEW/ADVRCVD_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("TXPDA", "GST_VIEW/ADVRCVD_AMD_GSTR1SUMMARY_VIEW");*/
		
		SECTION_VIEW_MAP.put("TXPD", "GST_VIEW/ADVADJ_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("TXPDA", "GST_VIEW/ADVADJ_AMD_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("AT", "GST_VIEW/ADVRCVD_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("ATA", "GST_VIEW/ADVRCVD_AMD_GSTR1SUMMARY_VIEW");
		
	/*	SECTION_VIEW_MAP.put("NIL", "GST_VIEW/NILNONEXMPT_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("DOC", "GST_VIEW/DOCISSUED_GSTR1SUMMARY_VIEW");*/
		
		SECTION_VIEW_MAP.put("SEWOP",
				"GST_VIEW/SEZ_WITHOUT_PAYMENT_GSTR1SUMMARY_VIEW");
		SECTION_VIEW_MAP.put("SEWP",
				"GST_VIEW/SEZ_WITH_PAYMENT_GSTR1SUMMARY_VIEW");
		
		
	}

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<BaseGstr1SummaryEntity> loadBasicSummarySection(
			String sectionType, List<String> sgstins, List<Long> entityIds,
			int fromTaxPeriod, int toTaxPeriod) {

		String queryStr = createQueryString(sectionType, sgstins, entityIds,
				fromTaxPeriod, toTaxPeriod);
		try {
		Query q = entityManager.createNativeQuery(queryStr);
		
		if(sgstins != null && sgstins.size() > 0) {
			q.setParameter("sgstins", sgstins);		
			}
		if(entityIds !=null && entityIds.size() > 0) {
			q.setParameter("entityIds", entityIds);
		}
		if(fromTaxPeriod != 0 && toTaxPeriod != 0) {
			q.setParameter("fromRetPeriod", fromTaxPeriod);
			q.setParameter("toRetPeriod", toTaxPeriod);
		}
		
		List<Object[]> list = q.getResultList();

		List<BaseGstr1SummaryEntity> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		
		
		return retList;
	} catch (Exception e) {
		e.printStackTrace();
		throw new AppException("Unexpected error in query execution.");
	}
	}

	private BaseGstr1SummaryEntity convert(Object[] arr) {
		BaseGstr1SummaryEntity obj = new BaseGstr1SummaryEntity();
		obj.setSupplierGstin((String) arr[0]);
		obj.setDerivedTaxPeriod((Integer) arr[1]);
		obj.setTableSection((String) arr[2]);
		obj.setTaxbleValue((BigDecimal) arr[3]);
		obj.setTaxPayable((BigDecimal) arr[4]);
		obj.setInvoiceValue((BigDecimal) arr[5]);
		obj.setIgstAmt((BigDecimal) arr[6]);
		obj.setCgstAmt((BigDecimal) arr[7]);
		obj.setSgstAmt((BigDecimal) arr[8]);
		obj.setCessAmt((BigDecimal) arr[9]);
		obj.setRecordCount((BigDecimal) arr[10]);
		return obj;
	}

	private String createQueryString(String sectionType, List<String> sgstins,
			List<Long> entityIds, int fromTaxPeriod, int toTaxPeriod) {

		String viewName = SECTION_VIEW_MAP.get(sectionType);
		StringBuilder querybuilder = new StringBuilder();
		if(sgstins != null && sgstins.size() > 0) {
			querybuilder.append(" AND SUPPLIER_GSTIN IN :sgstins");
		}
		if(entityIds !=null && entityIds.size() > 0) {
			querybuilder.append(
					" AND SUPPLIER_GSTIN IN (SELECT DISTINCT GSTIN FROM "
							+ "GSTIN_INFO WHERE ENTITY_ID IN :entityIds)");
		}
		/**
		 * @Required field
		 */
		if(fromTaxPeriod != 0 && toTaxPeriod != 0) {
			querybuilder.append(" AND DERIVED_RET_PERIOD "
				+ "BETWEEN :fromRetPeriod AND :toRetPeriod");
		} else {
			throw new AppException("Insufficient Search.");
		}
		String condition = querybuilder.toString().substring(4);
		String queryStr = "SELECT SUPPLIER_GSTIN, DERIVED_RET_PERIOD, "
				+ "TABLE_SECTION, TAXABLE_VALUE, TAX_PAYABLE, DOC_AMT, "
				+ "IGST, CGST, SGST, CESS, RECORD_COUNT FROM \"" + viewName
				+ "\" WHERE " + condition +" ORDER BY TABLE_SECTION";

		return queryStr;
	}
	// For AT 
	
}
