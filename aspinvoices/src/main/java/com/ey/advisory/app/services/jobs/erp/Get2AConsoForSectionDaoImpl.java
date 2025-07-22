package com.ey.advisory.app.services.jobs.erp;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository("Get2AConsoForSectionDaoImpl")
public class Get2AConsoForSectionDaoImpl{

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager manager;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get2AConsoForSectionDaoImpl.class);

	public List<Object[]> findGet2AConsoForSection(final String gstin,
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
		}
		return objs;
	}

	public String getQuery(String section) {
		StringBuilder sql = new StringBuilder();
		if ("B2B".equalsIgnoreCase(section)) {
			sql.append("select HDR.CGSTIN,HDR.SGSTIN,HDR.TAX_PERIOD,");
			sql.append("HDR.CFS,HDR.CHKSUM,HDR.SUPPLIER_INV_NUM,");
			sql.append("HDR.SUPPLIER_INV_DATE,HDR.SUPPLIER_INV_VAL,HDR.POS,");
			sql.append("HDR.RCHRG,HDR.INV_TYPE,HDR.DIFF_PERCENT,HDR.BATCH_ID,");
			sql.append("HDR.IGST_AMT AS IGST_AMT,");
			sql.append("HDR.CGST_AMT AS CGST_AMT,");
			sql.append("HDR.SGST_AMT AS SGST_AMT,");
			sql.append("HDR.CESS_AMT AS CESS_AMT,");
			sql.append("HDR.TAXABLE_VALUE AS TAXABLE_VALUE,");
			sql.append("HDR.ACTION_TAKEN,HDR.API_SECTION,HDR.DATA_CATEGORY,");
			sql.append(
					"HDR.SUPPLIER_TRADE_LEGAL_NAME,HDR.CFS_GSTR3B,HDR.CANCEL_DATE,");
			sql.append("HDR.FILE_DATE,HDR.FILE_PERIOD,HDR.ORG_INV_AMD_PERIOD,");
			sql.append("HDR.ORG_INV_AMD_TYPE,HDR.SUPPLY_TYPE,HDR.IRN_NUM,");
			sql.append(
					"HDR.IRN_GEN_DATE,HDR.IRN_SOURCE_TYPE,HDR.DELTA_INV_STATUS,HDR.INV_KEY,");

			sql.append("ITM.ITEM_NUMBER AS ITEM_NUMBER,");
			sql.append("ITM.IGST_AMT AS ITM_IGST_AMT,");
			sql.append("ITM.CGST_AMT AS ITM_CGST_AMT,");
			sql.append("ITM.SGST_AMT AS ITM_SGST_AMT,");
			sql.append("ITM.CESS_AMT AS ITM_CESS_AMT,");
			sql.append("ITM.TAX_VALUE AS ITM_TAXABLE_VALUE,");
			sql.append("ITM.TAX_RATE AS TAX_RATE,");
			sql.append("ITM.ITEM_INDEX AS ITEM_INDEX,HDR.ID, ");
			sql.append("V.LEGAL_NAME,V.TRADE_NAME,CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END ");
			sql.append("FROM ");
			sql.append("GETGSTR2A_B2B_HEADER HDR ");
			sql.append("LEFT JOIN GETGSTR2A_B2B_ITEM ITM ");
			sql.append("ON HDR.ID = ITM.HEADER_ID "); 
			sql.append("LEFT JOIN TBL_VENDOR_MASTER_CONFIG V ");
			sql.append("ON HDR.SGSTIN = V.VENDOR_GSTIN ");
			//sql.append("AND HDR.IS_DELETE = FALSE ");
			sql.append("WHERE  ");
			sql.append("IS_SENT_TO_ERP=false ");
			sql.append("AND CGSTIN=:gstin AND TAX_PERIOD=:retPeriod ");
			sql.append("AND HDR.BATCH_ID=:batchId  ");
			sql.append("AND ERP_BATCH_ID=:erpBatchId ");
			sql.append("AND DELTA_INV_STATUS IN ('N','M','D') "); //added newly
			sql.append(
					"GROUP BY HDR.CGSTIN,HDR.TAX_PERIOD,HDR.CFS,HDR.CHKSUM,HDR.SUPPLIER_INV_NUM,");
			sql.append(
					"HDR.SUPPLIER_INV_DATE,HDR.SUPPLIER_INV_VAL,HDR.POS,HDR.RCHRG,HDR.INV_TYPE,");
			sql.append(
					"HDR.DIFF_PERCENT,HDR.BATCH_ID,HDR.IGST_AMT,HDR.CGST_AMT,HDR.SGST_AMT,");
			sql.append(
					"HDR.CESS_AMT,HDR.TAXABLE_VALUE,HDR.ACTION_TAKEN,HDR.API_SECTION,");
			sql.append(
					"HDR.DATA_CATEGORY,HDR.SUPPLIER_TRADE_LEGAL_NAME,HDR.CFS_GSTR3B,");
			sql.append(
					"HDR.CANCEL_DATE,HDR.FILE_DATE,HDR.FILE_PERIOD,HDR.ORG_INV_AMD_PERIOD,");
			sql.append(
					"HDR.ORG_INV_AMD_TYPE,HDR.SUPPLY_TYPE,HDR.IRN_NUM,HDR.IRN_GEN_DATE,");
			sql.append("HDR.IRN_SOURCE_TYPE,HDR.DELTA_INV_STATUS,HDR.INV_KEY,");
			sql.append("ITM.ITEM_INDEX,HDR.SGSTIN,ITM.ITEM_NUMBER,HDR.ID,");
			sql.append("ITM.ITEM_NUMBER,ITM.IGST_AMT,ITM.CGST_AMT,");
			sql.append("ITM.SGST_AMT,ITM.CESS_AMT,ITM.TAX_VALUE,ITM.TAX_RATE,");
			sql.append("V.LEGAL_NAME,V.TRADE_NAME,(CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END) ");
		} else if ("B2BA".equalsIgnoreCase(section)) {

			sql.append("select HDR.CGSTIN,HDR.SGSTIN,HDR.TAX_PERIOD, ");
			sql.append("HDR.CFS,HDR.CHKSUM,HDR.SUPPLIER_INV_NUM, ");
			sql.append("HDR.SUPPLIER_INV_DATE,HDR.SUPPLIER_INV_VAL, ");
			sql.append("HDR.POS,HDR.RCHRG,HDR.INV_TYPE, ");
			sql.append("HDR.DIFF_PERCENT,HDR.BATCH_ID, ");
			sql.append("HDR.IGST_AMT AS IGST_AMT, ");
			sql.append("HDR.CGST_AMT AS CGST_AMT, ");
			sql.append("HDR.SGST_AMT AS SGST_AMT, ");
			sql.append("HDR.CESS_AMT AS CESS_AMT, ");
			sql.append("HDR.TAXABLE_VALUE AS TAXABLE_VALUE, ");
			sql.append("HDR.ACTION_TAKEN,HDR.API_SECTION, ");
			sql.append("HDR.DATA_CATEGORY,HDR.SUPPLIER_TRADE_LEGAL_NAME, ");
			sql.append("HDR.CFS_GSTR3B,HDR.CANCEL_DATE,HDR.FILE_DATE, ");
			sql.append("HDR.FILE_PERIOD,HDR.ORG_INV_AMD_PERIOD, ");
			sql.append("HDR.ORG_INV_AMD_TYPE,HDR.SUPPLY_TYPE, ");
			sql.append("HDR.DELTA_INV_STATUS, HDR.INV_KEY,");
			sql.append("HDR.ORG_INV_NUM,HDR.ORG_INV_DATE, ");
			sql.append("ITM.ITEM_NUMBER AS ITEM_NUMBER, ");
			sql.append("ITM.IGST_AMT AS ITM_IGST_AMT, ");
			sql.append("ITM.CGST_AMT AS ITM_CGST_AMT, ");
			sql.append("ITM.SGST_AMT AS ITM_SGST_AMT, ");
			sql.append("ITM.CESS_AMT AS ITM_CESS_AMT, ");
			sql.append("ITM.TAX_VALUE AS ITM_TAXABLE_VALUE, ");
			sql.append("ITM.TAX_RATE AS TAX_RATE, ");
			sql.append("ITM.ITEM_INDEX AS ITEM_INDEX,HDR.ID, ");
			sql.append("V.LEGAL_NAME,V.TRADE_NAME,CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END ");
			sql.append("FROM ");
			sql.append("GETGSTR2A_B2BA_HEADER HDR ");
			sql.append("LEFT JOIN GETGSTR2A_B2BA_ITEM ITM  ");
			sql.append("ON HDR.ID = ITM.HEADER_ID ");
			sql.append("LEFT JOIN TBL_VENDOR_MASTER_CONFIG V ");
			sql.append("ON HDR.SGSTIN = V.VENDOR_GSTIN ");
			//sql.append("AND HDR.IS_DELETE = FALSE ");
			sql.append("WHERE  ");
			sql.append("IS_SENT_TO_ERP=false ");
			sql.append(" AND CGSTIN=:gstin AND TAX_PERIOD=:retPeriod ");
			sql.append("AND HDR.BATCH_ID=:batchId  ");
			sql.append("AND ERP_BATCH_ID=:erpBatchId ");
			sql.append("AND DELTA_INV_STATUS IN ('N','M','D') "); //added newly
			sql.append("GROUP BY HDR.CGSTIN,HDR.TAX_PERIOD, ");
			sql.append("HDR.CFS,HDR.CHKSUM,HDR.SUPPLIER_INV_NUM, ");
			sql.append("HDR.SUPPLIER_INV_DATE,HDR.SUPPLIER_INV_VAL, ");
			sql.append("HDR.POS,HDR.RCHRG,HDR.INV_TYPE, ");
			sql.append("HDR.DIFF_PERCENT,HDR.BATCH_ID,HDR.IGST_AMT, ");
			sql.append("HDR.CGST_AMT,HDR.SGST_AMT, ");
			sql.append("HDR.CESS_AMT,HDR.TAXABLE_VALUE, ");
			sql.append("HDR.ACTION_TAKEN,HDR.API_SECTION, ");
			sql.append("HDR.DATA_CATEGORY,HDR.SUPPLIER_TRADE_LEGAL_NAME, ");
			sql.append("HDR.CFS_GSTR3B,HDR.CANCEL_DATE,HDR.FILE_DATE, ");
			sql.append("HDR.FILE_PERIOD,HDR.ORG_INV_AMD_PERIOD, ");
			sql.append("HDR.ORG_INV_AMD_TYPE,HDR.SUPPLY_TYPE, ");
			sql.append("HDR.DELTA_INV_STATUS,ITM.ITEM_INDEX, ");
			sql.append("HDR.SGSTIN,ITM.ITEM_NUMBER, ");
			sql.append("HDR.ORG_INV_NUM,HDR.ORG_INV_DATE,HDR.INV_KEY,HDR.ID, ");
			sql.append("ITM.ITEM_NUMBER,ITM.IGST_AMT,ITM.CGST_AMT,");
			sql.append("ITM.SGST_AMT,ITM.CESS_AMT,ITM.TAX_VALUE,ITM.TAX_RATE,");
			sql.append("V.LEGAL_NAME,V.TRADE_NAME,(CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END) ");
		} else if ("CDN".equalsIgnoreCase(section)) {
			sql.append("SELECT HDR.CTIN,HDR.GSTIN,HDR.TAX_PERIOD, ");
			sql.append("HDR.CFS,HDR.CHKSUM,HDR.NOTE_TYPE, ");
			sql.append("HDR.NOTE_NUMBER,HDR.NOTE_DATE, ");
			sql.append("HDR.NOTE_VALUE, ");
			sql.append("HDR.INV_NUMBER,HDR.P_GST, ");
			sql.append("HDR.DIFF_PERCENT,HDR.INV_DATE, ");
			sql.append("HDR.BATCH_ID, ");
			sql.append("HDR.IGST_AMT AS IGST_AMT, ");
			sql.append("HDR.CGST_AMT AS CGST_AMT, ");
			sql.append("HDR.SGST_AMT AS SGST_AMT, ");
			sql.append("HDR.CESS_AMT AS CESS_AMT, ");
			sql.append("HDR.C_FLAG,HDR.FROM_TIME, ");
			sql.append("HDR.TAXABLE_VALUE AS TAXABLE_VALUE, ");
			sql.append("HDR.DATA_CATEGORY,");
			sql.append("HDR.SUPPLIER_TRADE_LEGAL_NAME, ");
			sql.append("HDR.POS,HDR.RCHRG,HDR.INV_TYPE, ");
			sql.append("HDR.D_FLAG,HDR.CFS_GSTR3B, ");
			sql.append("HDR.CANCEL_DATE,HDR.FILE_DATE, ");
			sql.append("HDR.FILE_PERIOD,HDR.ORG_INV_AMD_PERIOD, ");
			sql.append("HDR.ORG_INV_AMD_TYPE,HDR.SUPPLY_TYPE, ");
			sql.append("HDR.IRN_NUM,HDR.IRN_GEN_DATE,HDR.IRN_SOURCE_TYPE, ");
			sql.append("HDR.DELTA_INV_STATUS,HDR.INV_KEY,");
			sql.append("ITM.ITEM_NUMBER AS ITEM_NUMBER, ");
			sql.append("ITM.IGST_AMT AS ITM_IGST_AMT, ");
			sql.append("ITM.CGST_AMT AS ITM_CGST_AMT, ");
			sql.append("ITM.SGST_AMT AS ITM_SGST_AMT, ");
			sql.append("ITM.CESS_AMT AS ITM_CESS_AMT, ");
			sql.append("ITM.TAX_VALUE AS ITM_TAXABLE_VALUE, ");
			sql.append("ITM.TAX_RATE AS TAX_RATE, ");
			sql.append("ITM.ITEM_INDEX AS ITEM_INDEX,HDR.ID, ");
			sql.append("V.LEGAL_NAME,V.TRADE_NAME,CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END ");
			sql.append("FROM ");
			sql.append("GETGSTR2A_CDN_HEADER HDR ");
			sql.append("LEFT JOIN GETGSTR2A_CDN_ITEM ITM  ");
			sql.append("ON HDR.ID = ITM.HEADER_ID ");
			sql.append("LEFT JOIN TBL_VENDOR_MASTER_CONFIG V ");
			sql.append("ON HDR.GSTIN = V.VENDOR_GSTIN ");
			//sql.append("AND HDR.IS_DELETE = FALSE ");
			sql.append("WHERE  ");
			sql.append("IS_SENT_TO_ERP=false ");
			sql.append(" AND  CTIN=:gstin AND TAX_PERIOD=:retPeriod ");
			sql.append("AND HDR.BATCH_ID=:batchId  ");
			sql.append("AND ERP_BATCH_ID=:erpBatchId ");
			sql.append("AND DELTA_INV_STATUS IN ('N','M','D') "); //added newly
			sql.append("GROUP BY HDR.CTIN,HDR.TAX_PERIOD,HDR.CFS, ");
			sql.append("HDR.CHKSUM,HDR.POS,HDR.RCHRG,HDR.INV_TYPE, ");
			sql.append("HDR.DIFF_PERCENT,HDR.BATCH_ID,HDR.IGST_AMT, ");
			sql.append("HDR.CGST_AMT,HDR.SGST_AMT, ");
			sql.append("HDR.CESS_AMT,HDR.TAXABLE_VALUE, ");
			sql.append("HDR.DATA_CATEGORY,HDR.SUPPLIER_TRADE_LEGAL_NAME, ");
			sql.append("HDR.CFS_GSTR3B,HDR.CANCEL_DATE,HDR.FILE_DATE, ");
			sql.append("HDR.FILE_PERIOD,HDR.ORG_INV_AMD_PERIOD, ");
			sql.append("HDR.ORG_INV_AMD_TYPE,HDR.SUPPLY_TYPE, ");
			sql.append("HDR.DELTA_INV_STATUS,HDR.INV_KEY,");
			sql.append("ITM.ITEM_INDEX,HDR.GSTIN,ITM.ITEM_NUMBER, ");
			sql.append("HDR.NOTE_TYPE,HDR.NOTE_VALUE,HDR.NOTE_NUMBER,HDR.NOTE_DATE, ");
			sql.append("HDR.INV_NUMBER,HDR.P_GST,HDR.INV_DATE, ");
			sql.append("HDR.C_FLAG,HDR.FROM_TIME,HDR.D_FLAG, ");
			sql.append("HDR.IRN_NUM,HDR.IRN_GEN_DATE,HDR.IRN_SOURCE_TYPE,");
			sql.append("HDR.DATA_CATEGORY,HDR.ID, ");
			sql.append("ITM.ITEM_NUMBER,ITM.IGST_AMT,ITM.CGST_AMT,");
			sql.append("ITM.SGST_AMT,ITM.CESS_AMT,ITM.TAX_VALUE,ITM.TAX_RATE,");
			sql.append("V.LEGAL_NAME,V.TRADE_NAME,(CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END) ");
		} else if ("CDNA".equalsIgnoreCase(section)) {
			sql.append("SELECT HDR.CTIN,HDR.GSTIN,HDR.TAX_PERIOD,");
			sql.append("HDR.CFS,HDR.CHKSUM,HDR.NOTE_TYPE,");
			sql.append("HDR.NOTE_NUMBER,HDR.NOTE_DATE,");
			sql.append("HDR.NOTE_VALUE,");
			sql.append("HDR.ORG_NOTE_TYPE,HDR.ORG_NOTE_NUMBER,");
			sql.append("HDR.ORG_NOTE_DATE,HDR.INV_NUMBER,HDR.P_GST,");
			sql.append("HDR.DIFF_PERCENT,HDR.INV_DATE,");
			sql.append("HDR.BATCH_ID,");
			sql.append("HDR.IGST_AMT AS IGST_AMT,");
			sql.append("HDR.CGST_AMT AS CGST_AMT,");
			sql.append("HDR.SGST_AMT AS SGST_AMT,");
			sql.append("HDR.CESS_AMT AS CESS_AMT,");
			sql.append("HDR.TAXABLE_VALUE AS TAXABLE_VALUE,");
			sql.append("HDR.DATA_CATEGORY,");
			sql.append("HDR.SUPPLIER_TRADE_LEGAL_NAME,");
			sql.append("HDR.POS,HDR.RCHRG,HDR.INV_TYPE,");
			sql.append("HDR.D_FLAG,HDR.CFS_GSTR3B,");
			sql.append("HDR.CANCEL_DATE,HDR.FILE_DATE,");
			sql.append("HDR.FILE_PERIOD,HDR.ORG_INV_AMD_PERIOD,");
			sql.append("HDR.ORG_INV_AMD_TYPE,HDR.SUPPLY_TYPE,");
			sql.append("HDR.DELTA_INV_STATUS,HDR.INV_KEY,");
			sql.append("ITM.ITEM_NUMBER AS ITEM_NUMBER,");
			sql.append("ITM.IGST_AMT AS ITM_IGST_AMT,");
			sql.append("ITM.CGST_AMT AS ITM_CGST_AMT,");
			sql.append("ITM.SGST_AMT AS ITM_SGST_AMT,");
			sql.append("ITM.CESS_AMT AS ITM_CESS_AMT,");
			sql.append("ITM.TAX_VALUE AS ITM_TAXABLE_VALUE,");
			sql.append("ITM.TAX_RATE AS TAX_RATE,");
			sql.append("ITM.ITEM_INDEX AS ITEM_INDEX,HDR.ID, ");
			sql.append("V.LEGAL_NAME,V.TRADE_NAME,CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END ");
			sql.append("FROM ");
			sql.append("GETGSTR2A_CDNA_HEADER HDR ");
			sql.append("LEFT JOIN GETGSTR2A_CDNA_ITEM ITM ");
			sql.append("ON HDR.ID = ITM.HEADER_ID ");
			sql.append("LEFT JOIN TBL_VENDOR_MASTER_CONFIG V ");
			sql.append("ON HDR.GSTIN = V.VENDOR_GSTIN ");
			//sql.append( "AND HDR.IS_DELETE = FALSE ");
			sql.append("WHERE  ");
			sql.append("IS_SENT_TO_ERP=false ");
			sql.append(" AND CTIN=:gstin AND TAX_PERIOD=:retPeriod ");
			sql.append("AND HDR.BATCH_ID=:batchId  ");
			sql.append("AND ERP_BATCH_ID=:erpBatchId ");
			sql.append("AND DELTA_INV_STATUS IN ('N','M','D') "); //added newly
			sql.append("GROUP BY HDR.CTIN,HDR.TAX_PERIOD,HDR.CFS,");
			sql.append("HDR.CHKSUM,HDR.POS,HDR.RCHRG,HDR.INV_TYPE,");
			sql.append("HDR.DIFF_PERCENT,HDR.BATCH_ID,HDR.IGST_AMT,");
			sql.append("HDR.CGST_AMT,HDR.SGST_AMT,");
			sql.append("HDR.CESS_AMT,HDR.TAXABLE_VALUE,");
			sql.append("HDR.DATA_CATEGORY,HDR.SUPPLIER_TRADE_LEGAL_NAME,");
			sql.append("HDR.CFS_GSTR3B,HDR.CANCEL_DATE,HDR.FILE_DATE,");
			sql.append("HDR.FILE_PERIOD,HDR.ORG_INV_AMD_PERIOD,");
			sql.append("HDR.ORG_INV_AMD_TYPE,HDR.SUPPLY_TYPE,");
			sql.append("HDR.INV_KEY,");
			sql.append(
					"HDR.DELTA_INV_STATUS,ITM.ITEM_INDEX,HDR.GSTIN,ITM.ITEM_NUMBER,");
			sql.append("HDR.NOTE_TYPE,HDR.NOTE_NUMBER,HDR.NOTE_DATE,HDR.NOTE_VALUE,");
			sql.append("HDR.INV_NUMBER,HDR.P_GST,HDR.INV_DATE,HDR.D_FLAG,");
			sql.append("HDR.ORG_NOTE_TYPE,");
			sql.append("HDR.ORG_NOTE_NUMBER,HDR.ORG_NOTE_DATE,");
			sql.append("HDR.DATA_CATEGORY,HDR.ID, ");
			sql.append("ITM.ITEM_NUMBER,ITM.IGST_AMT,ITM.CGST_AMT,");
			sql.append("ITM.SGST_AMT,ITM.CESS_AMT,ITM.TAX_VALUE,ITM.TAX_RATE,");
			sql.append("V.LEGAL_NAME,V.TRADE_NAME,(CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END) ");
		} else if ("ISD".equalsIgnoreCase(section)) {
			sql.append("SELECT HDR.CTIN,HDR.GSTIN,HDR.RET_PERIOD,");
			sql.append("HDR.BATCH_ID,HDR.CFS,HDR.CHKSUM,");
			sql.append("HDR.DOC_NUM,HDR.DOC_DATE,HDR.ISD_DOC_TYPE,");
			sql.append("HDR.ITC_ELG,");
			sql.append("HDR.IGST_AMT AS IGST_AMT, ");
			sql.append("HDR.CGST_AMT AS CGST_AMT, ");
			sql.append("HDR.SGST_AMT AS SGST_AMT, ");
			sql.append("HDR.CESS_AMT AS CESS_AMT, ");
			sql.append("HDR.DATA_CATEGORY,HDR.SUPPLIER_TRADE_LEGAL_NAME, ");
			sql.append("HDR.SUPPLY_TYPE,");
			sql.append("HDR.DELTA_INV_STATUS, ");
			sql.append("HDR.INV_KEY,");
			sql.append("ITM.IGST_AMT AS ITM_IGST_AMT, ");
			sql.append("ITM.CGST_AMT AS ITM_CGST_AMT, ");
			sql.append("ITM.SGST_AMT AS ITM_SGST_AMT, ");
			sql.append("ITM.CESS_AMT AS ITM_CESS_AMT, ");
			sql.append("ITM.ITEM_INDEX AS ITEM_INDEX,HDR.ID, ");
			sql.append("V.LEGAL_NAME,V.TRADE_NAME,CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END ");
			sql.append("FROM ");
			sql.append("GETGSTR2A_ISD_HEADER HDR ");
			sql.append("LEFT JOIN GETGSTR2A_ISD_ITEM ITM  ");
			sql.append("ON HDR.ID = ITM.HEADER_ID ");
			sql.append("LEFT JOIN TBL_VENDOR_MASTER_CONFIG V ");
			sql.append("ON HDR.CTIN = V.VENDOR_GSTIN ");
			//sql.append( "AND HDR.IS_DELETE = FALSE ");
			sql.append("WHERE  ");
			sql.append("IS_SENT_TO_ERP=false ");
			sql.append("AND  GSTIN=:gstin AND RET_PERIOD=:retPeriod ");
			sql.append("AND HDR.BATCH_ID=:batchId  ");
			sql.append("AND ERP_BATCH_ID=:erpBatchId ");
			sql.append("AND DELTA_INV_STATUS IN ('N','M','D') "); //added newly
			sql.append("GROUP BY ");
			sql.append("HDR.CTIN,HDR.GSTIN,HDR.RET_PERIOD, ");
			sql.append("HDR.BATCH_ID,HDR.CFS,HDR.CHKSUM, ");
			sql.append("HDR.DOC_NUM,HDR.DOC_DATE,HDR.ISD_DOC_TYPE, ");
			sql.append(
					"HDR.ITC_ELG,HDR.DATA_CATEGORY,HDR.SUPPLIER_TRADE_LEGAL_NAME, ");
			sql.append("HDR.SUPPLY_TYPE,HDR.INV_KEY,");
			sql.append("HDR.DELTA_INV_STATUS,ITM.ITEM_INDEX,HDR.ID,");
			sql.append("ITM.IGST_AMT,ITM.CGST_AMT,");
			sql.append("ITM.SGST_AMT,ITM.CESS_AMT,");
			sql.append("HDR.IGST_AMT,HDR.CGST_AMT,HDR.SGST_AMT,HDR.CESS_AMT,");
			sql.append("V.LEGAL_NAME,V.TRADE_NAME,(CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END) ");
		} else if ("IMPG".equalsIgnoreCase(section)) {
			sql.append("SELECT HDR.GSTIN,HDR.RET_PERIOD,HDR.FROM_TIME, ");
			sql.append("HDR.BOE_REF_DATE,HDR.PORT_CODE,HDR.BOE_NUM, ");
			sql.append("HDR.BOE_CREATED_DATE, ");
			sql.append("HDR.IGST_AMT AS IGST_AMT, ");
			sql.append("HDR.CESS_AMT AS CESS_AMT, ");
			sql.append("HDR.TAXABLE_VALUE AS TAXABLE_VALUE, ");
			sql.append("HDR.BATCH_ID,HDR.SUPPLY_TYPE,HDR.AMDHIST_KEY, ");
			sql.append("HDR.IS_AMENDMENT_BOE, ");
			sql.append("HDR.DELTA_INV_STATUS,HDR.INV_KEY, ");
			sql.append("ITM.IGST_AMT AS ITM_IGST_AMT, ");
			sql.append("ITM.CESS_AMT AS ITM_CESS_AMT, ");
			sql.append("ITM.ITEM_INDEX,HDR.ID,CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END ");
			sql.append("FROM  ");
			sql.append("GETGSTR2A_IMPG_HEADER HDR ");
			sql.append("LEFT JOIN GETGSTR2A_IMPG_ITEM ITM  ");
			sql.append("ON HDR.ID = ITM.HEADER_ID ");
			//sql.append( "AND HDR.IS_DELETE = FALSE ");
			sql.append("WHERE  ");
			sql.append("IS_SENT_TO_ERP=false ");
			sql.append("AND HDR.GSTIN=:gstin AND RET_PERIOD=:retPeriod ");
			sql.append("AND HDR.BATCH_ID=:batchId  ");
			sql.append("AND ERP_BATCH_ID=:erpBatchId ");
			sql.append("AND DELTA_INV_STATUS IN ('N','M','D') "); //added newly
			sql.append("GROUP BY HDR.GSTIN,HDR.RET_PERIOD,HDR.FROM_TIME, ");
			sql.append("HDR.BOE_REF_DATE,HDR.PORT_CODE,HDR.BOE_NUM, ");
			sql.append("HDR.BOE_CREATED_DATE, ");
			sql.append("HDR.BATCH_ID,HDR.SUPPLY_TYPE,HDR.AMDHIST_KEY, ");
			sql.append("HDR.IS_AMENDMENT_BOE, ");
			sql.append("HDR.DELTA_INV_STATUS,HDR.INV_KEY,");
			sql.append("ITM.ITEM_INDEX,HDR.ID, ");
			sql.append("HDR.IGST_AMT,HDR.CESS_AMT,HDR.TAXABLE_VALUE,");
			sql.append("ITM.IGST_AMT,ITM.CESS_AMT,(CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END)");
		} else if("IMPGSEZ".equalsIgnoreCase(section)){
			sql.append("SELECT HDR.GSTIN,HDR.SGSTIN,HDR.RET_PERIOD,");
			sql.append("HDR.FROM_TIME,HDR.TRADE_NAME AS HEADERTRADENAME,");
			sql.append("HDR.BOE_REF_DATE,HDR.PORT_CODE,HDR.BOE_NUM,");
			sql.append("HDR.BOE_CREATED_DATE,");
			sql.append("HDR.IGST_AMT AS IGST_AMT,");
			sql.append("HDR.CESS_AMT AS CESS_AMT,");
			sql.append("HDR.TAXABLE_VALUE AS TAXABLE_VALUE,");
			sql.append("HDR.BATCH_ID,HDR.SUPPLY_TYPE,HDR.AMDHIST_KEY,");
			sql.append("HDR.IS_AMENDMENT_BOE,");
			sql.append("HDR.DELTA_INV_STATUS,HDR.INV_KEY,");
			sql.append("ITM.IGST_AMT AS ITM_IGST_AMT,");
			sql.append("ITM.CESS_AMT AS ITM_CESS_AMT,");
			sql.append("ITM.TAXABLE_VALUE AS ITM_TAXABLE_VALUE,");
			sql.append("ITM.ITEM_INDEX,HDR.ID, ");
			sql.append("V.LEGAL_NAME,V.TRADE_NAME,CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END ");
			sql.append("FROM ");
			sql.append("GETGSTR2A_IMPGSEZ_HEADER HDR ");
			sql.append("LEFT JOIN GETGSTR2A_IMPGSEZ_ITEM ITM  ");
			sql.append("ON HDR.ID = ITM.HEADER_ID ");
			sql.append("LEFT JOIN TBL_VENDOR_MASTER_CONFIG V ");
			sql.append("ON HDR.SGSTIN = V.VENDOR_GSTIN ");
			//sql.append( "AND HDR.IS_DELETE = FALSE ");
			sql.append("WHERE  ");
			sql.append("IS_SENT_TO_ERP=false ");
			sql.append("AND HDR.GSTIN=:gstin AND RET_PERIOD=:retPeriod ");
			sql.append("AND HDR.BATCH_ID=:batchId  ");
			sql.append("AND ERP_BATCH_ID=:erpBatchId ");
			sql.append("AND DELTA_INV_STATUS IN ('N','M','D') "); //added newly
			sql.append("GROUP BY HDR.GSTIN,HDR.RET_PERIOD,HDR.FROM_TIME, ");
			sql.append("HDR.BOE_REF_DATE,HDR.PORT_CODE,HDR.BOE_NUM, ");
			sql.append("HDR.BOE_CREATED_DATE, ");
			sql.append("HDR.BATCH_ID,HDR.SUPPLY_TYPE,HDR.AMDHIST_KEY, ");
			sql.append("HDR.IS_AMENDMENT_BOE,");
			sql.append("HDR.DELTA_INV_STATUS,HDR.INV_KEY,");
			sql.append("ITM.ITEM_INDEX,HDR.SGSTIN,HDR.TRADE_NAME,HDR.ID, ");
			sql.append("HDR.IGST_AMT,HDR.CESS_AMT,HDR.TAXABLE_VALUE,");
			sql.append("ITM.IGST_AMT,ITM.CESS_AMT,ITM.TAXABLE_VALUE,");
			sql.append("V.LEGAL_NAME,V.TRADE_NAME,(CASE WHEN HDR.DELTA_INV_STATUS = 'M' THEN HDR.CREATED_ON WHEN HDR.DELTA_INV_STATUS = 'D' THEN HDR.MODIFIED_ON END) ");
		}
		return sql.toString();
	}
}
