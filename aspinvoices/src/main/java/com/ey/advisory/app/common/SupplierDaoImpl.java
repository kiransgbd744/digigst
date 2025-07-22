/**
 * 
 */
package com.ey.advisory.app.common;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author BalaKrishna S
 *
 */
@Component("SupplierDaoImpl")
public class SupplierDaoImpl implements SupplierdDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;	
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SupplierDaoImpl.class);
	
	/**
	 * 
	 */
	@Override
	public Map<String, String> getSupplierNamesForPans(List<String> sPans) {
		
		if (CollectionUtils.isEmpty(sPans)) {
			return new ImmutableMap.Builder<String, String>().build();
		}
		
		LOGGER.debug("Executing Query For getting Supplier Name For Pans");
		String queryStr = "SELECT * FROM (select DERIVED_SGSTIN_PAN , "
				+ "CUST_SUPP_NAME , ROW_NUMBER() OVER"
				+ " (PARTITION BY DERIVED_SGSTIN_PAN ORDER BY "
				+ "CUST_SUPP_NAME DESC) AS row_num from "
				+ "ANX_INWARD_DOC_HEADER "
				+ "WHERE CUST_SUPP_NAME IS NOT NULL AND DERIVED_SGSTIN_PAN IN "
				+ "(:sPans)) a where a.row_num =1";
		LOGGER.debug("Executing Query For getting Supplier Name For Pans End");

		Query q = entityManager.createNativeQuery(queryStr);

		q.setParameter("sPans", sPans);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		return list.stream().collect(
				Collectors.toMap(o -> (String) o[0], o -> (String) o[1]));
	}

}
