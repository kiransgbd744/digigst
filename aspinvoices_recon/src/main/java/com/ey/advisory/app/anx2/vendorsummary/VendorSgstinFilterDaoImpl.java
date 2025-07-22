package com.ey.advisory.app.anx2.vendorsummary;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;


@Component("VendorSgstinFilterDaoImpl")
public class VendorSgstinFilterDaoImpl implements VendorSgstinFilterDao {
		
		@PersistenceContext(unitName = "clientDataUnit")
		private EntityManager entityManager;
		
		
		public List<String> findSgstinsForCgstins(List<String> cgstins,
							String taxPeriod) {

			
			String queryStr = "SELECT DISTINCT CASE WHEN A2_SUPPLIER_GSTIN "
					+ "IS NULL THEN PR_SUPPLIER_GSTIN ELSE A2_SUPPLIER_GSTIN "
					+ " END "
					+ "SUPPLIER_GSTIN FROM LINK_A2_PR  "
					+ "WHERE "
					+ "(A2_RECIPIENT_GSTIN IN (:cgstins) OR "
					+ "PR_RECIPIENT_GSTIN IN (:cgstins)) "
					+ "AND TAX_PERIOD=:taxPeriod AND IS_ACTIVE=TRUE "
					+ "AND IS_DELETED=FALSE";
					
					
					
					/*"SELECT DISTINCT A2_SUPPLIER_GSTIN, "
					+ "PR_SUPPLIER_GSTIN FROM LINK_A2_PR  WHERE CUST_GSTIN IN "
					+ "(:cgstins) AND IS_PROCESSED = true AND "
					+ "IS_DELETE = false AND Return_period =:taxPeriod AND "
					+ "SUPPLIER_GSTIN IS NOT NULL";*/
			
			Query q = entityManager.createNativeQuery(queryStr);

			q.setParameter("cgstins", cgstins);
			q.setParameter("taxPeriod", taxPeriod);

			@SuppressWarnings("unchecked")
			List<Object> list = q.getResultList();

			return list.stream().map(o -> (String) o)
					.collect(Collectors.toList());
		
	}

}