package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("GetAnx2DetailStatusFetchDaoImpl")
public class GetAnx2DetailStatusFetchDaoImpl
		implements GetAnx2DetailStatusFetchDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetAnx2DetailStatusFetchDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object[]> getGstinsByEntityId(String entityId) {

		StringBuffer buffer = new StringBuffer();
		if (entityId != null && !entityId.equals("") && !entityId.equals("")) {
			buffer.append("SELECT GSTIN FROM "
					+ "GSTIN_INFO WHERE ENTITY_ID = :entityId");
		}

		Query query = entityManager.createNativeQuery(buffer.toString());

		if (entityId != null && !entityId.equals("") && !entityId.equals("")) {
			query.setParameter("entityId", entityId);
		}

		List<Object[]> gstinList = query.getResultList();

		return gstinList;
	}

	@Override
	public List<Object[]> getDataUploadedStatusDetails(String gstin,
			String taxPeriod) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(
				"SELECT * FROM ( SELECT * FROM (SELECT GSTIN,RETURN_PERIOD,DENSE_RANK() "
						+ "OVER(PARTITION BY GSTIN,GET_TYPE ORDER BY START_TIME DESC) "
						+ "AS NUM, GET_TYPE, START_TIME, STATUS"
						+ " FROM GETANX1_BATCH_TABLE GBT WHERE "
						+ "API_SECTION='ANX2') A WHERE NUM = 1 AND GSTIN = :gstin");
		if (taxPeriod != null || !taxPeriod.contentEquals("")) {
			buffer.append(" AND RETURN_PERIOD = :taxPeriod");
		}
		buffer.append(" UNION "
				+ " SELECT * FROM (SELECT GSTIN,RETURN_PERIOD,DENSE_RANK() OVER(PARTITION BY"
				+ " GSTIN,GET_TYPE ORDER BY START_TIME DESC) AS NUM, GET_TYPE, "
				+ "START_TIME, STATUS FROM GETANX1_BATCH_TABLE GBT "
				+ " WHERE STATUS = 'SUCCESS' AND API_SECTION='ANX2')"
				+ " A WHERE NUM = 1 AND GSTIN = :gstin");
		if (taxPeriod != null || !taxPeriod.contentEquals("")) {
			buffer.append(" AND RETURN_PERIOD = :taxPeriod");
		}
		buffer.append(") B ORDER BY " + "GET_TYPE, START_TIME DESC");

		Query query = entityManager.createNativeQuery(buffer.toString());

		query.setParameter("gstin", gstin);
		if (taxPeriod != null || !taxPeriod.contentEquals("")) {
			query.setParameter("taxPeriod", taxPeriod);
		}

		List<Object[]> itemsList = query.getResultList();
		return itemsList;
	}

}
