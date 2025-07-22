package com.ey.advisory.app.data.daos.client;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("CheckStatusDaoImpl")
public class CheckStatusDaoImpl implements CheckStatusDao{

	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Override
	public List<Gstr1SaveBatchEntity> checkStatusSection(String buildQuery,
			List<String> sgstins, String periodFrom, String periodTo) {
		
		
		String queryStr = createQueryString(buildQuery);
		Query q = entityManager.createQuery(queryStr);

		if (sgstins != null && sgstins.size() > 0) {
			q.setParameter("gstin", sgstins);
		}
		
		if((periodFrom != null && periodFrom.length() > 0)
				&&(periodTo !=null && periodTo.length()>0)){
			 
			int derRetPeriodFrom = GenUtil.convertTaxPeriodToInt(
					periodFrom);
			int derRetPeriodTo = GenUtil.convertTaxPeriodToInt(
					periodTo); 
				q.setParameter("periodFrom", derRetPeriodFrom);
				q.setParameter("periodTo", derRetPeriodTo);
		}
		List<Gstr1SaveBatchEntity> list = q.getResultList();
		return list ;
	}
	
private String createQueryString(String buildQuery) {
	String queryStr = "SELECT b FROM Gstr1SaveBatchEntity b WHERE "+buildQuery;
	 return queryStr ;
	}

}
