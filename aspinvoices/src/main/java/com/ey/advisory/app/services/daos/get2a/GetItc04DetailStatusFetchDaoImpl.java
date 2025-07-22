package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
/**
 * 
 * @author SriBhavya
 *
 */
@Component("GetItc04DetailStatusFetchDaoImpl")
public class GetItc04DetailStatusFetchDaoImpl implements GetItc04DetailStatusFetchDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetItc04DetailStatusFetchDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object[]> getGstinsByEntityId(String entityId) {

		StringBuffer buffer = new StringBuffer();
		if (entityId != null && !entityId.equals("") && !entityId.equals("")) {
			buffer.append("SELECT GSTIN FROM " + "GSTIN_INFO WHERE ENTITY_ID = :entityId " + " and IS_DELETE = false");
		}

		Query query = entityManager.createNativeQuery(buffer.toString());

		if (entityId != null && !entityId.equals("") && !entityId.equals("")) {
			query.setParameter("entityId", entityId);
		}

		List<Object[]> gstinList = query.getResultList();

		return gstinList;
	}

	@Override
	public List<Object[]> getDataUploadedStatusDetails(List<String> gstin, String taxPeriod) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ID,CREATED_ON,"
				+ " CASE WHEN SECTION='M2JW' THEN '4' "
				+ " WHEN SECTION='TABLE5A' THEN '5A' "
				+ " WHEN SECTION='TABLE5B' THEN '5B' "
				+ " WHEN SECTION='TABLE5C' THEN '5C' END AS SECTION,"
				+ "GSTN_SAVE_REF_ID, GSTN_SAVE_STATUS,"
				+ "IFNULL(ERROR_COUNT,0) ERROR_COUNT,OPERATION_TYPE"
				+ " FROM GSTR1_GSTN_SAVE_BATCH  WHERE RETURN_TYPE = 'ITC04'"
				+ " AND MODIFIED_ON IS NOT NULL AND  CREATED_ON IS NOT NULL AND GSTN_SAVE_REF_ID "
				+ "IS NOT NULL AND SUPPLIER_GSTIN IN :gstin  ");
		if (taxPeriod != null || !taxPeriod.contentEquals("")) {
			buffer.append(" AND RETURN_PERIOD = :taxPeriod ");
		}
		buffer.append("GROUP BY ID,CREATED_ON,GSTN_SAVE_REF_ID,OPERATION_TYPE,"
				+ " GSTN_SAVE_STATUS, ERROR_COUNT,SECTION ORDER BY CREATED_ON DESC,GSTN_SAVE_STATUS ");

		Query query = entityManager.createNativeQuery(buffer.toString());

		query.setParameter("gstin", gstin);
		if (taxPeriod != null || !taxPeriod.contentEquals("")) {
			query.setParameter("taxPeriod", taxPeriod);
		}

		List<Object[]> itemsList = query.getResultList();
		return itemsList;
	}

}
