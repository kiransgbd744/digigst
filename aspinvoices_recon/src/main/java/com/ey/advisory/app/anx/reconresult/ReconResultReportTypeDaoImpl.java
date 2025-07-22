/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikhil.Duseja
 *
 */
@Slf4j
@Repository("ReconResultReportTypeDaoImpl")
public class ReconResultReportTypeDaoImpl implements ReconResultReportTypeDao {


	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	
	@Override
	public List<String> findReportTypeForGstins(int taxPeriod,
			List<String> gstins) {
		
			
			String queryStr = "SELECT DISTINCT CURRENT_REPORT_TYPE "
					+ "FROM LINK_A2_PR WHERE "
					+ "DERIVED_RET_PERIOD=:derivedReturnPer "
					+ "AND (A2_RECIPIENT_GSTIN IN(:gstins) OR "
					+ "PR_RECIPIENT_GSTIN IN (:gstins)) "
					+ "AND IS_ACTIVE=TRUE AND IS_DELETED=FALSE";
			Query q = entityManager.createNativeQuery(queryStr);

			q.setParameter("gstins", gstins);
			q.setParameter("derivedReturnPer", taxPeriod);

			@SuppressWarnings("unchecked")
			List<Object> list = q.getResultList();
			
			if(LOGGER.isDebugEnabled()) {
				 String msg = String.format("The No of reports for this "
				 		+ " Gstins are %d",list.size());
				 LOGGER.debug(msg);
			}

			return list.stream().map(o -> (String) o)
					.filter(o -> !o.equalsIgnoreCase("Potential Match"))
					.collect(Collectors.toList());
		
	}

}
