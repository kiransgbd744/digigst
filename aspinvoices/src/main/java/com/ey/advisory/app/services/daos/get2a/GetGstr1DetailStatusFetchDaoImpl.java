package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;

@Component("GetGstr1DetailStatusFetchDaoImpl")
public class GetGstr1DetailStatusFetchDaoImpl
		implements GetGstr1DetailStatusFetchDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
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

	@Override
	public List<Object[]> getDataUploadedStatusDetails(List<String> gstin,
			String taxPeriod, String origin) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ID,CREATED_ON,SECTION, "
				+ "GSTN_SAVE_REF_ID, GSTN_SAVE_STATUS,"
				+ "IFNULL(ERROR_COUNT,0) ERROR_COUNT,OPERATION_TYPE"
				+ " FROM GSTR1_GSTN_SAVE_BATCH  WHERE RETURN_TYPE IN ('GSTR1','GSTR1_NIL_RETURN')"
				+ " AND MODIFIED_ON IS NOT NULL AND  CREATED_ON IS NOT NULL AND GSTN_SAVE_REF_ID "
				+ "IS NOT NULL AND SUPPLIER_GSTIN IN :gstin  ");
		if (!Strings.isNullOrEmpty(taxPeriod)) {
			buffer.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}
		if (APIConstants.INVMGMT.equalsIgnoreCase(origin)) {
			buffer.append(" AND ORIGIN = 'INVMGMT' ");
		} else {
			buffer.append(" AND (ORIGIN is NULL OR ORIGIN <> 'INVMGMT') ");
		}
		buffer.append("GROUP BY ID,CREATED_ON,GSTN_SAVE_REF_ID,OPERATION_TYPE,"
				+ " GSTN_SAVE_STATUS, ERROR_COUNT,SECTION ORDER BY CREATED_ON DESC,"
				+ "GSTN_SAVE_STATUS");

		Query query = entityManager.createNativeQuery(buffer.toString());

		query.setParameter("gstin", gstin);
		if (!Strings.isNullOrEmpty(taxPeriod)) {
			query.setParameter("taxPeriod",
					GenUtil.convertTaxPeriodToInt(taxPeriod));
		}

		List<Object[]> itemsList = query.getResultList();
		return itemsList;
	}

	@Override
	public List<Object[]> getGstr8SaveStatusDetails(List<String> gstin,
			String taxPeriod) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ID,CREATED_ON,SECTION, "
				+ "GSTN_SAVE_REF_ID, GSTN_SAVE_STATUS,"
				+ "IFNULL(ERROR_COUNT,0) ERROR_COUNT,OPERATION_TYPE"
				+ " FROM GSTR1_GSTN_SAVE_BATCH  WHERE RETURN_TYPE IN ('GSTR8',)"
				+ " AND MODIFIED_ON IS NOT NULL AND  CREATED_ON IS NOT NULL AND GSTN_SAVE_REF_ID "
				+ "IS NOT NULL AND SUPPLIER_GSTIN IN :gstin  ");
		if (!Strings.isNullOrEmpty(taxPeriod)) {
			buffer.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		buffer.append("GROUP BY ID,CREATED_ON,GSTN_SAVE_REF_ID,OPERATION_TYPE,"
				+ " GSTN_SAVE_STATUS, ERROR_COUNT,SECTION ORDER BY CREATED_ON DESC,"
				+ "GSTN_SAVE_STATUS");

		Query query = entityManager.createNativeQuery(buffer.toString());

		query.setParameter("gstin", gstin);
		if (!Strings.isNullOrEmpty(taxPeriod)) {
			query.setParameter("taxPeriod",
					GenUtil.convertTaxPeriodToInt(taxPeriod));
		}

		List<Object[]> itemsList = query.getResultList();
		return itemsList;
	}
	
	@Override
	public List<Object[]> getGstr1ADataUploadedStatusDetails(List<String> gstin,
			String taxPeriod, String origin) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ID,CREATED_ON,SECTION, "
				+ "GSTN_SAVE_REF_ID, GSTN_SAVE_STATUS,"
				+ "IFNULL(ERROR_COUNT,0) ERROR_COUNT,OPERATION_TYPE"
				+ " FROM GSTR1_GSTN_SAVE_BATCH  WHERE RETURN_TYPE IN ('GSTR1A','GSTR1A_NIL_RETURN')"
				+ " AND MODIFIED_ON IS NOT NULL AND  CREATED_ON IS NOT NULL AND GSTN_SAVE_REF_ID "
				+ "IS NOT NULL AND SUPPLIER_GSTIN IN :gstin  ");
		if (!Strings.isNullOrEmpty(taxPeriod)) {
			buffer.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}
		if (APIConstants.INVMGMT.equalsIgnoreCase(origin)) {
			buffer.append(" AND ORIGIN = 'INVMGMT' ");
		} else {
			buffer.append(" AND (ORIGIN is NULL OR ORIGIN <> 'INVMGMT') ");
		}
		buffer.append("GROUP BY ID,CREATED_ON,GSTN_SAVE_REF_ID,OPERATION_TYPE,"
				+ " GSTN_SAVE_STATUS, ERROR_COUNT,SECTION ORDER BY CREATED_ON DESC,"
				+ "GSTN_SAVE_STATUS");

		Query query = entityManager.createNativeQuery(buffer.toString());

		query.setParameter("gstin", gstin);
		if (!Strings.isNullOrEmpty(taxPeriod)) {
			query.setParameter("taxPeriod",
					GenUtil.convertTaxPeriodToInt(taxPeriod));
		}

		List<Object[]> itemsList = query.getResultList();
		return itemsList;
	}
}
