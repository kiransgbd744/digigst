package com.ey.advisory.app.data.daos.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("ComprehensiveStatusDaoImpl")
public class ComprehensiveStatusDaoImpl implements FileStatusDao {
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object[]> fileStatus(String build, String dataType,
			String fileType, LocalDate dataRecvFrom, LocalDate dataRecvTo,
			String source) {
		Query q = entityManager.createNativeQuery(build);
		q.setParameter("dataType", dataType);
		q.setParameter("fileType", fileType);
		q.setParameter("dataRecvFrom", dataRecvFrom);
		q.setParameter("dataRecvTo", dataRecvTo);
		if (source != null) {
			q.setParameter("source", source);
		}
		List<Object[]> list = q.getResultList();
		return list;
	}

	@Override
	public List<Gstr1FileStatusEntity> fileStatusSection(String sectionType,
			String buildQuery, LocalDate dataRecvFrom, LocalDate dataRecvTo,
			String fileType, String dataType, String source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object[]> masterFileStatus(String buildQuery,
			LocalDate fromDate, LocalDate toDate, String fileType,
			Long entityId) {
		// TODO Auto-generated method stub
		return null;
	}
}