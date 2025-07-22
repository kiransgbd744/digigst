/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("OutwardSftpResponseRevIntInprogressCount")
public class OutwardSftpResponseRevIntInprogressCount {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public Integer getInprogressCount(Long fileId) {

		StringBuilder buildQuery = new StringBuilder();

		if (fileId != null) {

			buildQuery.append(" AND HDR.FILE_ID = :fileId");
		}
		String queryStr = createInprogressCountQueryString(
				buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}
		Integer totalCount = ((Number) q.getSingleResult()).intValue();
		return totalCount;
	}

	private String createInprogressCountQueryString(String buildQuery) {

		return "select COUNT(HDR.ID) FROM ANX_OUTWARD_DOC_HEADER "
				+ "HDR LEFT OUTER JOIN EINV_MASTER EINV "
				+ "ON HDR.IRN_RESPONSE = EINV.IRN "
				+ "LEFT OUTER JOIN EWB_MASTER EWB "
				+ "ON HDR.EWB_NO_RESP = EWB.EWB_NUM "
				+ "WHERE HDR.IS_DELETE = FALSE AND IS_SUBMITTED = FALSE "
				+ "AND HDR.IRN_STATUS IN (3,9,10)" + buildQuery;
	}
}
