package com.ey.advisory.app.data.daos.client.gstr2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr2.BaseGstr2SummaryEntity;
import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("Gstr2BasicDocSummarySectionDaoImpl")
public class Gstr2BasicDocSummarySectionDaoImpl 
			implements Gstr2BasicDocSummarySectionDao {
	
	private static final Map<String, String> SECTION_VIEW_MAP = new HashMap<>();

	
	static {
		// Initialize the static array.
		SECTION_VIEW_MAP.put("B2B", "GST_VIEW/B2B_GSTR2SUMMARY_VIEW");
	}

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<BaseGstr2SummaryEntity> loadBasicSummarySection(
			String sectionType, List<String> sgstins, List<Long> entityIds,
			int fromTaxPeriod, int toTaxPeriod) {
		// TODO Auto-generated method stub
		
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

		List<BaseGstr2SummaryEntity> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		
		
		return retList;
	} catch (Exception e) {
		e.printStackTrace();
		throw new AppException("Unexpected error in query execution.");
	}
	}

	private BaseGstr2SummaryEntity convert(Object[] arr) {
		BaseGstr2SummaryEntity obj = new BaseGstr2SummaryEntity();
		obj.setSupplierGstin((String) arr[0]);
		obj.setDerivedTaxPeriod((Integer) arr[1]);
		obj.setTableSection((String) arr[2]);
		obj.setTaxableValue((BigDecimal) arr[3]);
		obj.setIgst((BigDecimal) arr[4]);
		obj.setSgst((BigDecimal) arr[5]);
		obj.setCgst((BigDecimal) arr[6]);
		obj.setCess((BigDecimal) arr[7]);
		obj.setItcIgst((BigDecimal) arr[8]);
		obj.setItcSgst((BigDecimal) arr[9]);
		obj.setItcCgst((BigDecimal) arr[10]);
		obj.setItcCess((BigDecimal) arr[11]);
		obj.setRecords((Integer) arr[12]);
		
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


}
