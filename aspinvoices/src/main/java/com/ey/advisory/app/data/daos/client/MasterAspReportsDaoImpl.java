package com.ey.advisory.app.data.daos.client;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.simplified.client.MasterAspReportsEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("MasterAspReportsDaoImpl")
public class MasterAspReportsDaoImpl implements MasterAspReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<MasterAspReportsEntity> getMasterAspReports() throws Exception {
		Query reportQuery = entityManager.createQuery(
				"from MasterAspReportsEntity", MasterAspReportsEntity.class);
		List<MasterAspReportsEntity> reportList = reportQuery.getResultList();
		return reportList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> loadSupplyTypeDetails(String reportsKey) {
		String queryStr = "SELECT SUPPLY_TYPE,SUPPLY_TYPE_DESC FROM "
				+ "MASTER_SUPPLY_TYPE WHERE  REPORTS_KEY = :reportsKey";
		Query q = entityManager.createNativeQuery(queryStr);
		q.setParameter("reportsKey", reportsKey);
		List<Object[]> list = q.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> loadDocumentTypeDetails(String reportsKey) {
		String queryStr = "SELECT DOC_TYPE,DOC_TYPE_DESC FROM "
				+ "MASTER_DOCTYPE  WHERE  REPORTS_KEY =:reportsKey";
		Query q = entityManager.createNativeQuery(queryStr);
		q.setParameter("reportsKey", reportsKey);
		List<Object[]> list = q.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> loadAttributes(String reportsKey) {
		String queryStr = "SELECT ATTRIBUTES_NAME FROM "
				+ "MASTER_ATTRIBUTES WHERE  REPORTS_KEY = :reportsKey";
		Query q = entityManager.createNativeQuery(queryStr);
		q.setParameter("reportsKey", reportsKey);
		List<Object[]> list = q.getResultList();
		return list;
	}

}
