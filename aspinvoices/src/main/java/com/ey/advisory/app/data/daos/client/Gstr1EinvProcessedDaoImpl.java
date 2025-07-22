package com.ey.advisory.app.data.daos.client;

import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr1EinvProcessedDaoImpl")
public class Gstr1EinvProcessedDaoImpl implements Gstr1EinvProcessedDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getActiveDocsById(List<String> docHeaderIds) {

		String queryStr = createQueryString();
		Query q = entityManager.createNativeQuery(queryStr);
		q.setParameter(1, docHeaderIds);
		List<Object> resultList = q.getResultList();

		return resultList.stream().map(Object::toString)
				.collect(Collectors.toList());
	}

	private String createQueryString() {
		String query = ""
				+ "select ID from ANX_OUTWARD_DOC_HEADER where IS_DELETE = false and ID in (?1);";
		return query;
	}

	@Override
	@Transactional(value = "clientTransactionManager")
	public int updateStatusForProcessed(Long docHeaderId, String prevResp,
			String userResp) {
		int rowsUpdated = 0;

		String queryStr = createUpdateQueryString(docHeaderId, prevResp,
				userResp);
		if (!StringUtils.isEmpty(queryStr)) {
			Query q = entityManager.createNativeQuery(queryStr);
			q.setParameter(1, docHeaderId);
			rowsUpdated = q.executeUpdate();
		}
		return rowsUpdated;
	}

	private String createUpdateQueryString(Long docId, String prevResp,
			String userResp) {
		Pair<Boolean, Boolean> statusPair = checkSaveAndSentGstinStatus(docId);
		Boolean isSentToGstin = statusPair.getValue0();
		Boolean isSavedToGstin = statusPair.getValue1();
		String query = "UPDATE ANX_OUTWARD_DOC_HEADER SET ";
		String queryCondition = null;

		if (!StringUtils.hasText(prevResp)) {

			if (userResp.equalsIgnoreCase("S")) {
				queryCondition = "EINV_GSTN_SAVE_STATUS = 'S' where ID = (?1)";

			} else if (userResp.equalsIgnoreCase("N")) {
				queryCondition = "IS_SENT_TO_GSTN = TRUE ,IS_SAVED_TO_GSTN = TRUE,EINV_GSTN_SAVE_STATUS = 'N' where ID = (?1)";

			} else if (userResp.equalsIgnoreCase("D")) {

			}
		} else {

			if (prevResp.equalsIgnoreCase("S")) {

				if (userResp.equalsIgnoreCase("N")) {
					if (!isSentToGstin && !isSavedToGstin) {
						queryCondition = "IS_SENT_TO_GSTN = TRUE ,IS_SAVED_TO_GSTN = TRUE,EINV_GSTN_SAVE_STATUS = 'N' where ID = (?1)";
					}
				}

			} else if (prevResp.equalsIgnoreCase("N")) {

				if (userResp.equalsIgnoreCase("S")) {
					queryCondition = "IS_SENT_TO_GSTN = FALSE ,IS_SAVED_TO_GSTN = FALSE,EINV_GSTN_SAVE_STATUS = 'S' where ID = (?1)";

				}
			} else if (prevResp.equalsIgnoreCase("D")) {

				if (userResp.equalsIgnoreCase("S")
						|| userResp.equalsIgnoreCase("N")) {

					if (!isSentToGstin && !isSavedToGstin) {

						if (userResp.equalsIgnoreCase("S")) {
							queryCondition = "EINV_GSTN_SAVE_STATUS = 'S' where ID = (?1)";
						} else if (userResp.equalsIgnoreCase("N")) {
							queryCondition = "IS_SENT_TO_GSTN = TRUE ,IS_SAVED_TO_GSTN = TRUE,EINV_GSTN_SAVE_STATUS = 'N' where ID = (?1)";
						}
					}

				}

			}

		}
		if (!StringUtils.isEmpty(queryCondition)) {
			return query + queryCondition;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<Boolean, Boolean> checkSaveAndSentGstinStatus(
			Long docHeaderId) {
		Boolean isSentToGstin = null;
		Boolean isSaveToGstin = null;
		String query = "SELECT IS_SENT_TO_GSTN,IS_SAVED_TO_GSTN from ANX_OUTWARD_DOC_HEADER where ID = (?)";
		Query q = entityManager.createNativeQuery(query);
		q.setParameter(1, docHeaderId);
		List<Object[]> resultList = q.getResultList();

		for (Object[] o : resultList) {
			Byte sentToGstin = (Byte) o[0];
			Byte savedToGstin = (Byte) o[1];

			isSentToGstin = (sentToGstin.intValue() == 1) ? true : false;
			isSaveToGstin = (savedToGstin.intValue() == 1) ? true : false;
		}

		return new Pair<>(isSentToGstin, isSaveToGstin);
	}

}
