package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository("Get2ARevIntgDaoImpl")
public class Get2ARevIntgDaoImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get2ARevIntgDaoImpl.class);
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager manager;

	public List<Object[]> get2ARevIntgDao(final String gstin, int chunkId) {
		List<Object[]> obj = null;
		try {
			String sql = getQuery();
			Query query = manager.createNativeQuery(sql);
			query.setParameter("gstin", gstin);
			query.setParameter("chunkId", chunkId);
			obj = query.getResultList();
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Size of consolidated data : {} for gstin : {} and "
						+ "chunkId : {}",obj.size(), gstin, chunkId);
			}
			
			return obj;
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
			throw new AppException(e);
		}
		
	}

	private String getQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT SGSTIN,CGSTIN,TAX_PERIOD,CFS,SUPPLIER_NAME,");
		sql.append("STATE_NAME,DOC_NUM,DOC_DATE,INV_NUM,INV_DATE,");
		sql.append("POS,RCHRG,INV_TYPE,DIFF_PERCENT,ORG_INV_NUM,ORG_INV_DATE,");
		sql.append("ITEM_NUMBER,CRDR_PRE_GST,ITC_ELIGIBLE,TAXABLE_VALUE,");
		sql.append(
				"TAX_RATE,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,INV_VAL,INV_KEY,SECTION ");
		sql.append(
				"FROM GETGSTR2A_ERP_CONSOLIDATED WHERE CGSTIN=:gstin AND IS_DELETE=FALSE ");
		sql.append("AND IS_SENT_TO_ERP=false AND CHUNK_ID = :chunkId");

		return sql.toString();
	}
}
