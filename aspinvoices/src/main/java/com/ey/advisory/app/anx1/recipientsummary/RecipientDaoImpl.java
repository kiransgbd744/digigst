package com.ey.advisory.app.anx1.recipientsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

/**
 * @author Arun KA
 *
 */

@Component("RecipientDaoImpl")
public class RecipientDaoImpl implements RecipientDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<String> getCgstinsForEntity(String sPan) {

		String queryStr = "SELECT DISTINCT CUST_GSTIN FROM CLIENT1_GST"
				+ ".ANX_OUTWARD_DOC_HEADER ODH WHERE "
				+ "DERIVED_SGSTIN_PAN = :sPan AND IS_PROCESSED = true AND "
				+ "IS_DELETE = false AND CUST_GSTIN IS NOT NULL";

		Query q = entityManager.createNativeQuery(queryStr);

		q.setParameter("sPan", sPan);

		@SuppressWarnings("unchecked")
		List<Object> list = q.getResultList();

		return list.stream().map(o -> (String) o)
				.collect(Collectors.toList());
	}

	@Override
	public Map<String, String> getCNamesForCPans(List<String> cPans) {

		if (CollectionUtils.isEmpty(cPans)) {
			return new ImmutableMap.Builder<String, String>().build();
		}

		String queryStr = "SELECT * FROM (select DERIVED_CGSTIN_PAN , "
				+ "CUST_SUPP_NAME , ROW_NUMBER() OVER"
				+ " (PARTITION BY DERIVED_CGSTIN_PAN ORDER BY "
				+ "CUST_SUPP_NAME DESC) AS row_num from "
				+ "ANX_OUTWARD_DOC_HEADER "
				+ "WHERE CUST_SUPP_NAME IS NOT NULL AND DERIVED_CGSTIN_PAN IN "
				+ "(:cPans)) a where a.row_num =1";

		Query q = entityManager.createNativeQuery(queryStr);

		q.setParameter("cPans", cPans);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		return list.stream().collect(
				Collectors.toMap(o -> (String) o[0], o -> (String) o[1]));

	}

	
	public List<CgstinSgstinDto> getCgstinsForGstins(List<String> gstins, String taxPeriod) {
		String queryStr = "SELECT DISTINCT SUPPLIER_GSTIN ,CUST_GSTIN FROM CLIENT1_GST"
				+ ".ANX_OUTWARD_DOC_HEADER WHERE "
				+ " SUPPLIER_GSTIN in (:gstin) AND IS_PROCESSED = true AND "
				+ "IS_DELETE = false AND CUST_GSTIN IS NOT NULL AND RETURN_PERIOD = :taxPeriod";

		Query q = entityManager.createNativeQuery(queryStr);

		q.setParameter("gstin", gstins);
		q.setParameter("taxPeriod", taxPeriod);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		
		return list
				.stream()
				.map(o -> convert(o))
				.collect(Collectors.toList());

	}

	private CgstinSgstinDto convert(Object[] o) {
		return new CgstinSgstinDto((String)o[1], (String)o[0],
				((String) o[1]).substring(2,12));
	}

}
