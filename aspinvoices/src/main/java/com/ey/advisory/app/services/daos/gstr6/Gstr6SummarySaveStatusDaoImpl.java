package com.ey.advisory.app.services.daos.gstr6;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;

@Component("Gstr6SummarySaveStatusDaoImpl")
public class Gstr6SummarySaveStatusDaoImpl
		implements Gstr6SummarySaveStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object[]> getGstinsByEntityId(String entityId) {
		StringBuilder sql = new StringBuilder();
		if (entityId != null && !entityId.trim().isEmpty()) {
			sql.append("SELECT GSTIN FROM "
					+ "GSTIN_INFO WHERE ENTITY_ID = :entityId "
					+ " and IS_DELETE = false");
		}
		Query query = entityManager.createNativeQuery(sql.toString());

		if (entityId != null && !entityId.trim().isEmpty()) {
			query.setParameter("entityId", entityId);
		}
		List<Object[]> gstinList = query.getResultList();
		return gstinList;
	}

	@Override
	public List<Object[]> getDataUploadedStatusDetails(List<String> gstin,
			String taxPeriod) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT b.ID,b.CREATED_ON,b.GSTN_SAVE_REF_ID, ");
		sql.append("b.GSTN_SAVE_STATUS,IFNULL(b.ERROR_COUNT,0) ERROR_COUNT, ");
		sql.append("b.SECTION,b.OPERATION_TYPE,b.ERROR_DESC, ");
		sql.append("r.IS_CROSS_ITC_UI FROM GSTR1_GSTN_SAVE_BATCH b ");
		sql.append(
				"left join GSTN_USER_REQUEST r ON b.USER_REQUEST_ID = r.ID ");
		sql.append("WHERE b.RETURN_TYPE = 'GSTR6' ");
		sql.append("AND MODIFIED_ON IS NOT NULL AND b.CREATED_ON IS NOT NULL ");
		sql.append("AND GSTN_SAVE_REF_ID IS NOT NULL ");

		sql.append("AND b.SUPPLIER_GSTIN IN :gstin ");
		if (taxPeriod != null) {
			sql.append("AND b.DERIVED_RET_PERIOD = :taxPeriod ");
		}
		sql.append("GROUP BY b.ID,b.CREATED_ON,b.GSTN_SAVE_REF_ID, ");
		sql.append("b.SECTION,b.OPERATION_TYPE,b.ERROR_DESC, ");
		sql.append("b.GSTN_SAVE_STATUS,b.ERROR_COUNT,b.GSTN_SAVE_STATUS, ");
		sql.append("r.IS_CROSS_ITC_UI ORDER BY b.CREATED_ON DESC ");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter("gstin", gstin);
		if (taxPeriod != null) {
			query.setParameter("taxPeriod",
					GenUtil.convertTaxPeriodToInt(taxPeriod));
		}
		List<Object[]> itemsList = query.getResultList();
		return itemsList;
	}
}
