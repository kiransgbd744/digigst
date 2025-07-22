package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusReqDto;

@Component("GetGstr1StatusFetchDaoImpl")
public class GetGstr1StatusFetchDaoImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetGstr1StatusFetchDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<Object[]> getGstinsByEntityId(
			GetAnx2DetailStatusReqDto criteria) {

		List<String> gstins = criteria.getGstin();
		String entityId = criteria.getEntityId();
		StringBuffer buffer = new StringBuffer();
		if (StringUtils.isNotBlank(entityId)) {
			buffer.append("SELECT GSTIN FROM "
					+ "GSTIN_INFO WHERE ENTITY_ID = :entityId");
		}

		if (CollectionUtils.isNotEmpty(gstins)) {
			buffer.append(" AND GSTIN IN :gstins");
		}

		Query query = entityManager.createNativeQuery(buffer.toString());

		if (StringUtils.isNotBlank(entityId)) {
			query.setParameter("entityId", entityId);
		}

		if (CollectionUtils.isNotEmpty(gstins)) {
			query.setParameter("gstins", gstins);
		}

		List<Object[]> gstinList = query.getResultList();

		return gstinList;
	}

	public List<Object[]> getDataUploadedStatusDetails(String gstin,
			String taxPeriod) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(
				"SELECT * FROM ( SELECT * FROM (SELECT GSTIN,RETURN_PERIOD,DENSE_RANK() "
						+ "OVER(PARTITION BY GSTIN,GET_TYPE ORDER BY START_TIME DESC) "
						+ "AS NUM, GET_TYPE, START_TIME, STATUS"
						+ " FROM GETANX1_BATCH_TABLE GBT WHERE "
						+ "API_SECTION='GSTR1' AND GSTIN = :gstin");
		if (StringUtils.isNotBlank(taxPeriod)) {
			buffer.append(" AND RETURN_PERIOD = :taxPeriod");
		}
		buffer.append(") A WHERE NUM = 1 UNION "
				+ " SELECT * FROM (SELECT GSTIN,RETURN_PERIOD,DENSE_RANK() OVER(PARTITION BY"
				+ " GSTIN,GET_TYPE ORDER BY START_TIME DESC) AS NUM, GET_TYPE, "
				+ "START_TIME, STATUS FROM GETANX1_BATCH_TABLE GBT "
				+ " WHERE STATUS = 'SUCCESS' AND API_SECTION='GSTR1' AND GSTIN = :gstin");
		if (StringUtils.isNotBlank(taxPeriod)) {
			buffer.append(" AND RETURN_PERIOD = :taxPeriod");
		}
		buffer.append(") A WHERE NUM = 1) B ORDER BY GET_TYPE, START_TIME DESC");

		Query query = entityManager.createNativeQuery(buffer.toString());

		query.setParameter("gstin", gstin);
		if (StringUtils.isNotBlank(taxPeriod)) {
			query.setParameter("taxPeriod", taxPeriod);
		}

		List<Object[]> itemsList = query.getResultList();
		return itemsList;
	}

}
