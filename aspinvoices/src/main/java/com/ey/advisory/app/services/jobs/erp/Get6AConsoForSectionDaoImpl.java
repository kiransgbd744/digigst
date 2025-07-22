package com.ey.advisory.app.services.jobs.erp;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository("Get6AConsoForSectionDaoImpl")
public class Get6AConsoForSectionDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager manager;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get6AConsoForSectionDaoImpl.class);

	public List<Object[]> findGet6AConsoForSection(final String gstin,
			final String retPeriod, final String section, final Long batchId,
			final Long erpBatchId) {
		List<Object[]> objs = new ArrayList<>();
		try {
			String sql = getQuery(section);
			Query q = manager.createNativeQuery(sql);
			q.setParameter("gstin", gstin);
			q.setParameter("retPeriod", retPeriod);
			q.setParameter("batchId", batchId);
			q.setParameter("erpBatchId", erpBatchId);
			objs = q.getResultList();
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
			throw new AppException("Exception Occured:", e);
		}
		return objs;
	}

	public String getQuery(String section) {
		StringBuilder sql = new StringBuilder();
		if ("B2B".equalsIgnoreCase(section)) {
			sql.append("select HDR.GSTIN,HDR.CTIN,HDR.TAX_PERIOD,");
			sql.append("HDR.CFS,HDR.CHKSUM,HDR.SUPPLIER_INV_NUM,");
			sql.append(
					"HDR.SUPPLIER_INV_DATE,HDR.SUPPLIER_INV_VAL,HDR.POS,HDR.REVERSE_CHARGE,");
			sql.append("HDR.INV_TYPE,HDR.BATCH_ID,");
			sql.append("HDR.IGST_AMT AS IGST_AMT,");
			sql.append("HDR.CGST_AMT AS CGST_AMT,");
			sql.append("HDR.SGST_AMT AS SGST_AMT,");
			sql.append("HDR.CESS_AMT AS CESS_AMT,");
			sql.append("HDR.TAXABLE_VALUE AS TAXABLE_VALUE,");
			sql.append("HDR.SUPPLIER_TRADE_LEGAL_NAME,");
			sql.append("HDR.IRN_NUM,");
			sql.append(
					"HDR.IRN_GEN_DATE,HDR.IRN_SOURCE_TYPE,HDR.DELTA_INV_STATUS,HDR.INV_KEY,");
			sql.append("ITM.ITM_NO AS ITEM_NUMBER,");
			sql.append("ITM.IGST_AMT AS ITM_IGST_AMT,");
			sql.append("ITM.CGST_AMT AS ITM_CGST_AMT,");
			sql.append("ITM.SGST_AMT AS ITM_SGST_AMT,");
			sql.append("ITM.CESS_AMT AS ITM_CESS_AMT,");
			sql.append("ITM.TAXABLE_VALUE AS ITM_TAXABLE_VALUE,");
			sql.append("ITM.TAX_RATE AS TAX_RATE,");
			sql.append("HDR.ID, ");
			sql.append(
					"V.LEGAL_NAME,V.TRADE_NAME,CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END,HDR.CFP ");
			sql.append("FROM ");
			sql.append("GETGSTR6A_B2B_HEADER HDR ");
			sql.append("LEFT JOIN GETGSTR6A_B2B_ITEM ITM ");
			sql.append("ON HDR.ID = ITM.HEADER_ID ");
			sql.append("LEFT JOIN TBL_VENDOR_MASTER_CONFIG V ");
			sql.append("ON HDR.CTIN = V.VENDOR_GSTIN ");
			// sql.append("AND HDR.IS_DELETE = FALSE ");
			sql.append("WHERE  ");
			sql.append("IS_SENT_TO_ERP=false ");
			sql.append("AND CTIN=:gstin AND TAX_PERIOD=:retPeriod ");
			sql.append("AND HDR.BATCH_ID=:batchId  ");
			sql.append("AND ERP_BATCH_ID=:erpBatchId ");
			sql.append("AND DELTA_INV_STATUS IN ('N','M','D') "); // added newly
			sql.append(
					"GROUP BY HDR.GSTIN,HDR.TAX_PERIOD,HDR.CFS,HDR.CHKSUM,HDR.SUPPLIER_INV_NUM,");
			sql.append(
					"HDR.SUPPLIER_INV_DATE,HDR.SUPPLIER_INV_VAL,HDR.POS,HDR.REVERSE_CHARGE,HDR.INV_TYPE,");
			sql.append("HDR.BATCH_ID,HDR.IGST_AMT,HDR.CGST_AMT,HDR.SGST_AMT,");
			sql.append("HDR.CESS_AMT,HDR.TAXABLE_VALUE,");
			sql.append("HDR.SUPPLIER_TRADE_LEGAL_NAME,");
			sql.append("HDR.IRN_NUM,HDR.IRN_GEN_DATE,");
			sql.append("HDR.IRN_SOURCE_TYPE,HDR.DELTA_INV_STATUS,HDR.INV_KEY,");
			sql.append("HDR.CTIN,ITM.ITM_NO,HDR.ID,");
			sql.append("ITM.IGST_AMT,ITM.CGST_AMT,");
			sql.append(
					"ITM.SGST_AMT,ITM.CESS_AMT,ITM.TAXABLE_VALUE,ITM.TAX_RATE,");
			sql.append(
					"V.LEGAL_NAME,V.TRADE_NAME,(CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END),HDR.CFP ");
		} else if ("B2BA".equalsIgnoreCase(section)) {
			sql.append("select HDR.GSTIN,HDR.CTIN,HDR.TAX_PERIOD, ");
			sql.append("HDR.CFS,HDR.CHKSUM,HDR.SUPPLIER_INV_NUM, ");
			sql.append("HDR.SUPPLIER_INV_DATE,HDR.SUPPLIER_INV_VAL, ");
			sql.append("HDR.POS,HDR.REVERSE_CHARGE,HDR.INV_TYPE, ");
			sql.append("HDR.BATCH_ID, ");
			sql.append("HDR.IGST_AMT AS IGST_AMT, ");
			sql.append("HDR.CGST_AMT AS CGST_AMT, ");
			sql.append("HDR.SGST_AMT AS SGST_AMT, ");
			sql.append("HDR.CESS_AMT AS CESS_AMT, ");
			sql.append("HDR.TAXABLE_VALUE AS TAXABLE_VALUE, ");
			sql.append("HDR.SUPPLIER_TRADE_LEGAL_NAME, ");
			sql.append("HDR.DELTA_INV_STATUS, HDR.INV_KEY,");
			sql.append("HDR.ORG_INV_NUM,HDR.ORG_INV_DATE,");
			sql.append("ITM.ITM_NO AS ITEM_NUMBER, ");
			sql.append("ITM.IGST_AMT AS ITM_IGST_AMT, ");
			sql.append("ITM.CGST_AMT AS ITM_CGST_AMT, ");
			sql.append("ITM.SGST_AMT AS ITM_SGST_AMT, ");
			sql.append("ITM.CESS_AMT AS ITM_CESS_AMT, ");
			sql.append("ITM.TAXABLE_VALUE AS ITM_TAXABLE_VALUE, ");
			sql.append("ITM.TAX_RATE AS TAX_RATE, ");
			sql.append("ITM.ITEM_INDEX AS ITEM_INDEX,HDR.ID, ");
			sql.append(
					"V.LEGAL_NAME,V.TRADE_NAME,CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END,HDR.CFP ");
			sql.append("FROM ");
			sql.append("GETGSTR6A_B2BA_HEADER HDR ");
			sql.append("LEFT JOIN GETGSTR6A_B2BA_ITEM ITM  ");
			sql.append("ON HDR.ID = ITM.HEADER_ID ");
			sql.append("LEFT JOIN TBL_VENDOR_MASTER_CONFIG V ");
			sql.append("ON HDR.CTIN = V.VENDOR_GSTIN ");
			// sql.append("AND HDR.IS_DELETE = FALSE ");
			sql.append("WHERE  ");
			sql.append("IS_SENT_TO_ERP=false ");
			sql.append("AND CTIN=:gstin AND TAX_PERIOD=:retPeriod ");
			sql.append("AND HDR.BATCH_ID=:batchId  ");
			sql.append("AND ERP_BATCH_ID=:erpBatchId ");
			sql.append("AND DELTA_INV_STATUS IN ('N','M','D') "); // added newly
			sql.append("GROUP BY HDR.GSTIN,HDR.TAX_PERIOD, ");
			sql.append("HDR.CFS,HDR.CHKSUM,HDR.SUPPLIER_INV_NUM, ");
			sql.append("HDR.SUPPLIER_INV_DATE,HDR.SUPPLIER_INV_VAL, ");
			sql.append("HDR.POS,HDR.REVERSE_CHARGE,HDR.INV_TYPE, ");
			sql.append("HDR.BATCH_ID,HDR.IGST_AMT, ");
			sql.append("HDR.CGST_AMT,HDR.SGST_AMT, ");
			sql.append("HDR.CESS_AMT,HDR.TAXABLE_VALUE, ");
			sql.append("HDR.SUPPLIER_TRADE_LEGAL_NAME, ");
			sql.append("HDR.DELTA_INV_STATUS,ITM.ITEM_INDEX, ");
			sql.append("HDR.CTIN, ");
			sql.append("HDR.ORG_INV_NUM,HDR.ORG_INV_DATE,HDR.INV_KEY,HDR.ID, ");
			sql.append("ITM.ITM_NO,ITM.IGST_AMT,ITM.CGST_AMT,");
			sql.append(
					"ITM.SGST_AMT,ITM.CESS_AMT,ITM.TAXABLE_VALUE,ITM.TAX_RATE,");
			sql.append(
					"V.LEGAL_NAME,V.TRADE_NAME,(CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END),HDR.CFP ");
		} else if ("CDN".equalsIgnoreCase(section)) {
			sql.append("SELECT HDR.GSTIN,HDR.CTIN,HDR.TAX_PERIOD, ");
			sql.append("HDR.CFS,HDR.CHKSUM,HDR.NOTE_TYPE, ");
			sql.append("HDR.NOTE_NUMBER,HDR.NOTE_DATE, ");
			sql.append("HDR.P_GST, ");
			sql.append("HDR.INV_DATE,");
			sql.append("HDR.BATCH_ID,");
			sql.append("HDR.IGST_AMT AS IGST_AMT, ");
			sql.append("HDR.CGST_AMT AS CGST_AMT, ");
			sql.append("HDR.SGST_AMT AS SGST_AMT, ");
			sql.append("HDR.CESS_AMT AS CESS_AMT, ");
			sql.append("HDR.TAXABLE_VALUE AS TAXABLE_VALUE, ");
			sql.append("HDR.SUPPLIER_TRADE_LEGAL_NAME, HDR.INV_VALUE,");
			sql.append("HDR.POS, ");
			sql.append("HDR.IRN_NUM,HDR.IRN_GEN_DATE,");
			sql.append("HDR.DELTA_INV_STATUS,HDR.INV_KEY,");
			sql.append("ITM.ITM_NO AS ITEM_NUMBER, ");
			sql.append("ITM.IGST_AMT AS ITM_IGST_AMT, ");
			sql.append("ITM.CGST_AMT AS ITM_CGST_AMT, ");
			sql.append("ITM.SGST_AMT AS ITM_SGST_AMT, ");
			sql.append("ITM.CESS_AMT AS ITM_CESS_AMT, ");
			sql.append("ITM.TAXABLE_VALUE AS ITM_TAXABLE_VALUE, ");
			sql.append("ITM.TAX_RATE AS TAX_RATE, ");
			sql.append("HDR.ID, ");
			sql.append(
					"V.LEGAL_NAME,V.TRADE_NAME,HDR.D_FLAG,CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END,HDR.CFP ");
			sql.append("FROM ");
			sql.append("GETGSTR6A_CDN_HEADER HDR ");
			sql.append("LEFT JOIN GETGSTR6A_CDN_ITEM ITM  ");
			sql.append("ON HDR.ID = ITM.HEADER_ID ");
			sql.append("LEFT JOIN TBL_VENDOR_MASTER_CONFIG V ");
			sql.append("ON HDR.CTIN = V.VENDOR_GSTIN ");
			// sql.append("AND HDR.IS_DELETE = FALSE ");
			sql.append("WHERE  ");
			sql.append("IS_SENT_TO_ERP=false ");
			sql.append(" AND CTIN=:gstin AND TAX_PERIOD=:retPeriod ");
			sql.append("AND HDR.BATCH_ID=:batchId  ");
			sql.append("AND ERP_BATCH_ID=:erpBatchId ");
			sql.append("AND DELTA_INV_STATUS IN ('N','M','D') "); // added newly
			sql.append("GROUP BY HDR.CTIN,HDR.TAX_PERIOD,HDR.CFS, ");
			sql.append("HDR.CHKSUM,HDR.POS,HDR.INV_TYPE, ");
			sql.append("HDR.BATCH_ID,HDR.IGST_AMT, ");
			sql.append("HDR.CGST_AMT,HDR.SGST_AMT, ");
			sql.append("HDR.CESS_AMT,HDR.TAXABLE_VALUE, ");
			sql.append("HDR.SUPPLIER_TRADE_LEGAL_NAME,HDR.INV_VALUE,HDR.POS, ");
			sql.append("HDR.IRN_NUM,HDR.IRN_GEN_DATE,");
			sql.append("HDR.DELTA_INV_STATUS,HDR.INV_KEY,");
			sql.append("HDR.GSTIN, ");
			sql.append("HDR.NOTE_TYPE,HDR.NOTE_NUMBER,HDR.NOTE_DATE, ");
			sql.append("HDR.P_GST,HDR.INV_DATE, ");
			sql.append("HDR.IRN_NUM,HDR.IRN_GEN_DATE,");
			sql.append("HDR.ID,");
			sql.append("ITM.ITM_NO,ITM.IGST_AMT,ITM.CGST_AMT,");
			sql.append(
					"ITM.SGST_AMT,ITM.CESS_AMT,ITM.TAXABLE_VALUE,ITM.TAX_RATE,");
			sql.append(
					"V.LEGAL_NAME,V.TRADE_NAME,HDR.D_FLAG,(CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END),HDR.CFP ");
		} else if ("CDNA".equalsIgnoreCase(section)) {
			sql.append("SELECT HDR.GSTIN,HDR.CTIN,HDR.TAX_PERIOD,");
			sql.append("HDR.CFS,HDR.CHKSUM,HDR.NOTE_TYPE,");
			sql.append("HDR.NOTE_NUM,HDR.NOTE_DATE,");
			sql.append("HDR.ORG_NOTE_NUM,");
			sql.append("HDR.ORG_NOTE_DATE,HDR.INV_NUM,HDR.P_GST,");
			sql.append("HDR.INV_DATE,");
			sql.append("HDR.BATCH_ID,");
			sql.append("HDR.IGST_AMT AS IGST_AMT,");
			sql.append("HDR.CGST_AMT AS CGST_AMT,");
			sql.append("HDR.SGST_AMT AS SGST_AMT,");
			sql.append("HDR.CESS_AMT AS CESS_AMT,");
			sql.append("HDR.TAXABLE_VALUE AS TAXABLE_VALUE,");
			sql.append("HDR.SUPPLIER_TRADE_LEGAL_NAME,");
			sql.append("HDR.POS,");
			sql.append("HDR.DELTA_INV_STATUS,HDR.INV_KEY,");
			sql.append("ITM.ITM_NO AS ITEM_NUMBER,");
			sql.append("ITM.IGST_AMT AS ITM_IGST_AMT,");
			sql.append("ITM.CGST_AMT AS ITM_CGST_AMT,");
			sql.append("ITM.SGST_AMT AS ITM_SGST_AMT,");
			sql.append("ITM.CESS_AMT AS ITM_CESS_AMT,");
			sql.append("ITM.TAXABLE_VALUE AS ITM_TAXABLE_VALUE,");
			sql.append("ITM.TAX_RATE AS TAX_RATE,");
			sql.append("HDR.ID, ");
			sql.append(
					"V.LEGAL_NAME,V.TRADE_NAME,HDR.D_FLAG,HDR.INV_VALUE,CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END,HDR.CFP ");
			sql.append("FROM ");
			sql.append("GETGSTR6A_CDNA_HEADER HDR ");
			sql.append("LEFT JOIN GETGSTR6A_CDNA_ITEM ITM ");
			sql.append("ON HDR.ID = ITM.HEADER_ID ");
			sql.append("LEFT JOIN TBL_VENDOR_MASTER_CONFIG V ");
			sql.append("ON HDR.CTIN = V.VENDOR_GSTIN ");
			// sql.append( "AND HDR.IS_DELETE = FALSE ");
			sql.append("WHERE  ");
			sql.append("IS_SENT_TO_ERP=false ");
			sql.append(" AND CTIN=:gstin AND TAX_PERIOD=:retPeriod ");
			sql.append("AND HDR.BATCH_ID=:batchId  ");
			sql.append("AND ERP_BATCH_ID=:erpBatchId ");
			sql.append("AND DELTA_INV_STATUS IN ('N','M','D') "); // added newly
			sql.append("GROUP BY HDR.CTIN,HDR.TAX_PERIOD,HDR.CFS,");
			sql.append("HDR.CHKSUM,HDR.POS,");
			sql.append("HDR.BATCH_ID,HDR.IGST_AMT,");
			sql.append("HDR.CGST_AMT,HDR.SGST_AMT,");
			sql.append("HDR.CESS_AMT,HDR.TAXABLE_VALUE,");
			sql.append("HDR.SUPPLIER_TRADE_LEGAL_NAME,");
			sql.append("HDR.INV_KEY,");
			sql.append("HDR.DELTA_INV_STATUS,ITM.ITEM_INDEX,HDR.GSTIN,");
			sql.append("HDR.NOTE_TYPE,HDR.NOTE_NUM,HDR.NOTE_DATE,");
			sql.append("HDR.INV_NUM,HDR.P_GST,HDR.INV_DATE,");
			sql.append("HDR.ORG_NOTE_NUM,HDR.ORG_NOTE_DATE,");
			sql.append("HDR.ID, ");
			sql.append("ITM.ITM_NO,ITM.IGST_AMT,ITM.CGST_AMT,");
			sql.append(
					"ITM.SGST_AMT,ITM.CESS_AMT,ITM.TAXABLE_VALUE,ITM.TAX_RATE,");
			sql.append(
					"V.LEGAL_NAME,V.TRADE_NAME,HDR.D_FLAG,HDR.INV_VALUE,(CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END),HDR.CFP ");
		}
		return sql.toString();
	}
}
