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
@Component("GetGstr2XDetailStatusFetchDaoImpl")
public class GetGstr2XDetailStatusFetchDaoImpl implements GetGstr2XDetailStatusFetchDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetGstr2XDetailStatusFetchDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object[]> getDataUploadedStatusDetails(List<String> gstin, String taxPeriod, String type) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ID,CREATED_ON,SECTION," + "GSTN_SAVE_REF_ID, GSTN_SAVE_STATUS,"
				+ "IFNULL(ERROR_COUNT,0) ERROR_COUNT,OPERATION_TYPE"
				+ " FROM GSTR1_GSTN_SAVE_BATCH  WHERE RETURN_TYPE = 'GSTR2X'"
				+ " AND MODIFIED_ON IS NOT NULL AND  CREATED_ON IS NOT NULL AND GSTN_SAVE_REF_ID "
				+ "IS NOT NULL AND SUPPLIER_GSTIN IN :gstin  ");
		if (taxPeriod != null && !taxPeriod.isEmpty()) {
			buffer.append(" AND RETURN_PERIOD = :taxPeriod ");
		}
		if (type != null && !type.isEmpty()) {
			buffer.append(" AND SECTION = :type ");
		}
		buffer.append("GROUP BY ID,CREATED_ON,GSTN_SAVE_REF_ID,OPERATION_TYPE,"
				+ " GSTN_SAVE_STATUS, ERROR_COUNT,SECTION ORDER BY CREATED_ON DESC,GSTN_SAVE_STATUS ");

		Query query = entityManager.createNativeQuery(buffer.toString());

		query.setParameter("gstin", gstin);
		if (taxPeriod != null && !taxPeriod.isEmpty()) {
			query.setParameter("taxPeriod", taxPeriod);
		}
		if (type != null && !type.isEmpty()) {
			query.setParameter("type", type);
		}

		List<Object[]> itemsList = query.getResultList();
		return itemsList;
	}

}
