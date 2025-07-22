package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.GenUtil;

@Service("GetCommonSaveStatusFetchDaoImpl")
public class GetCommonSaveStatusFetchDaoImpl implements GetSaveStatusFetchDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetCommonSaveStatusFetchDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")

	@Override
	public List<Object[]> getGstinsByEntityId(String entityId, String gstin) {

		StringBuffer buffer = new StringBuffer();
		if (entityId != null && !entityId.equals("") && gstin != null
				&& !gstin.equals("")) {
			buffer.append("SELECT GSTIN FROM "
					+ "GSTIN_INFO WHERE ENTITY_ID = :entityId and GSTIN = :gstin and IS_DELETE = false");
		}

		Query query = entityManager.createNativeQuery(buffer.toString());

		if (entityId != null && !entityId.equals("") && !entityId.equals("")) {
			query.setParameter("entityId", entityId);
		}

		if (gstin != null && !gstin.equals("") && !gstin.equals("")) {
			query.setParameter("gstin", gstin);
		}

		List<Object[]> gstinList = query.getResultList();
		LOGGER.debug(" entity gstin with query -> ", gstinList);
		return gstinList;
	}

	@Override
	public List<Object[]> getSaveStatusDetailsByReturnType(String gstin,
			String taxPeriod, String returnType) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ID,MODIFIED_ON," + " GSTN_SAVE_REF_ID,"
				+ " GSTN_SAVE_STATUS,"
				+ "IFNULL(ERROR_COUNT,0) ERROR_COUNT FROM GSTR1_GSTN_SAVE_BATCH"
				+ " WHERE IS_DELETE = FALSE AND RETURN_TYPE = ");
		buffer.append("'");
		buffer.append(returnType);
		buffer.append("'");
		buffer.append(" AND MODIFIED_ON IS NOT NULL AND  GSTN_SAVE_REF_ID ");
		buffer.append("IS NOT NULL AND GSTN_SAVE_STATUS IS NOT NULL ");
		buffer.append("AND SUPPLIER_GSTIN IN :gstin  ");
		if (taxPeriod != null || !taxPeriod.contentEquals("")) {
			buffer.append(" AND DERIVED_RET_PERIOD = :taxPeriod");
		}
		buffer.append(" GROUP BY ID,MODIFIED_ON,GSTN_SAVE_REF_ID,"
				+ "GSTN_SAVE_STATUS,ERROR_COUNT ORDER BY MODIFIED_ON DESC,"
				+ "GSTN_SAVE_STATUS");

		Query query = entityManager.createNativeQuery(buffer.toString());
		LOGGER.debug(" Saved gstin query -> ", query);
		query.setParameter("gstin", gstin);
		if (taxPeriod != null || !taxPeriod.contentEquals("")) {
			query.setParameter("taxPeriod",
					GenUtil.convertTaxPeriodToInt(taxPeriod));
		}
		List<Object[]> itemsList = query.getResultList();
		LOGGER.debug(" Saved gstin list with -> ", itemsList);
		return itemsList;
	}

}
