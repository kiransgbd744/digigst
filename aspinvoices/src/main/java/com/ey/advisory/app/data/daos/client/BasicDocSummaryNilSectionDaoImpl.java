package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr1NilRatedSummarySectionDto;
import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("BasicDocSummaryNilSectionDaoImpl")
public class BasicDocSummaryNilSectionDaoImpl 
		implements BasicDocSummaryNilSectionDao{
	
private static final Map<String, String> SECTION_VIEW_MAP = new HashMap<>();
	
	static {
		SECTION_VIEW_MAP.put("NIL", "GST_VIEW/NILNONEXMPT_GSTR1SUMMARY_VIEW");
		
	}
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;


	@Override
	public List<Gstr1NilRatedSummarySectionDto> loadBasicSummarySection(
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

		List<Gstr1NilRatedSummarySectionDto> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		
		
		return retList;
	} catch (Exception e) {
		e.printStackTrace();
		throw new AppException("Unexpected error in query execution.");
	}
	}

	private Gstr1NilRatedSummarySectionDto convert(Object[] arr) {
	//	BaseGstr1SummaryEntity obj = new BaseGstr1SummaryEntity();
		
		Gstr1NilRatedSummarySectionDto obj = new Gstr1NilRatedSummarySectionDto();
		obj.setRecordCount((Integer) arr[0]);
		obj.setTableSection((String) arr[1]);
		obj.setTotalExempted((BigDecimal) arr[2]);
		obj.setTotalNilRated((BigDecimal) arr[3]);
		obj.setTotalNonGST((BigDecimal) arr[4]);
		
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
			/*SUPPLIER_GSTIN*/
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
		/*String queryStr = "SELECT SUPPLIER_GSTIN, DERIVED_RET_PERIOD, "
				+ "TAXABLE_VALUE, TAX_PAYABLE, DOC_AMT, "
				+ "IGST, CGST, SGST, CESS, RECORD_COUNT FROM \"" + viewName
				+ "\" WHERE " + condition +" ORDER BY TABLE_SECTION";
*/
		String queryStr = "SELECT RECORD_COUNT,TABLE_SECTION,TOT_NIL_AMT,TOT_EXPT_AMT, "
				+  "TOT_NON_AMT FROM  \"" + viewName 
				+ "\" WHERE " + condition +" ORDER BY TABLE_SECTION";



		return queryStr;
	}

	
	

}
