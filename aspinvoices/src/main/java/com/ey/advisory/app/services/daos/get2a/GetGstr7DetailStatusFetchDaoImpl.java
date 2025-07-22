package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("GetGstr7DetailStatusFetchDaoImpl")
public class GetGstr7DetailStatusFetchDaoImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetGstr7DetailStatusFetchDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<Object[]> getGstinsByEntityId(String entityId) {

		StringBuffer buffer = new StringBuffer();
		if (entityId != null && !entityId.equals("") && !entityId.equals("")) {
			buffer.append("SELECT GSTIN FROM "
					+ "GSTIN_INFO WHERE ENTITY_ID = :entityId "
					+ " and IS_DELETE = false");
		}

		Query query = entityManager.createNativeQuery(buffer.toString());

		if (entityId != null && !entityId.equals("") && !entityId.equals("")) {
			query.setParameter("entityId", entityId);
		}

		List<Object[]> gstinList = query.getResultList();

		return gstinList;
	}

	public List<Object[]> getDataUploadedStatusDetails(List<String> gstin,
			String taxPeriod, String returnType) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(
				"SELECT ID,CREATED_ON,SECTION,GSTN_SAVE_REF_ID, GSTN_SAVE_STATUS,"
						+ "IFNULL(ERROR_COUNT,0) ERROR_COUNT FROM GSTR1_GSTN_SAVE_BATCH"
						+ " WHERE RETURN_TYPE = :returnType "
						+ " AND MODIFIED_ON IS NOT NULL AND "
						+ " CREATED_ON IS NOT NULL AND GSTN_SAVE_REF_ID "
						+ "IS NOT NULL AND SUPPLIER_GSTIN IN :gstin  ");
		if (!Strings.isNullOrEmpty(taxPeriod)) {
			buffer.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}
		if (!Strings.isNullOrEmpty(returnType)) {
			buffer.append(" AND RETURN_TYPE = :returnType ");
		}
		buffer.append("GROUP BY ID,CREATED_ON,GSTN_SAVE_REF_ID,"
				+ " GSTN_SAVE_STATUS, ERROR_COUNT,SECTION ORDER BY CREATED_ON DESC,"
				+ "GSTN_SAVE_STATUS");

		Query query = entityManager.createNativeQuery(buffer.toString());

		query.setParameter("gstin", gstin);
		if (!Strings.isNullOrEmpty(taxPeriod)) {
			query.setParameter("taxPeriod",
					GenUtil.convertTaxPeriodToInt(taxPeriod));
		}
		query.setParameter("returnType", returnType);

		List<Object[]> itemsList = query.getResultList();
		return itemsList;
	}

}
