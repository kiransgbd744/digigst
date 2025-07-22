/*
 * package com.ey.advisory.app.services.jobs.gstr6;
 * 
 * import java.util.LinkedList; import java.util.List; import
 * java.util.stream.Collectors;
 * 
 * import org.apache.commons.collections.CollectionUtils; import
 * org.apache.commons.lang3.StringUtils; import org.slf4j.Logger; import
 * org.slf4j.LoggerFactory; import org.springframework.stereotype.Component;
 * 
 * import com.ey.advisory.app.docs.dto.simplified.TDSDetailedRespDto; import
 * com.ey.advisory.app.docs.dto.simplified.TDSDownlaodRequestDto; import
 * com.ey.advisory.common.GenUtil;
 * 
 * import jakarta.persistence.EntityManager; import
 * jakarta.persistence.PersistenceContext; import jakarta.persistence.Query;
 * 
 * @Component("TdsUploadfileDaoImpl") public class TdsUploadfileDaoImpl {
 * 
 * private static final Logger LOGGER = LoggerFactory
 * .getLogger(Gstr6SaveStatusDownloadDaoImpl.class);
 * 
 * @PersistenceContext(unitName = "clientDataUnit") private EntityManager
 * entityManager;
 * 
 * public List<TDSDetailedRespDto> fetchTdsByReq( TDSDownlaodRequestDto
 * criteria) { List<String> gstinList = criteria.getGstin(); String taxperiod =
 * criteria.getTaxPeriod(); String type = criteria.getType(); StringBuilder
 * buildQuery = new StringBuilder();
 * 
 * if (gstinList != null && gstinList.size() > 0) {
 * buildQuery.append(" AND HDR.GSTIN IN :gstinList"); }
 * 
 * if (StringUtils.isNotEmpty(type) && type!=null) {
 * buildQuery.append(" AND HDR.RECORD_TYPE = :type"); } if
 * (StringUtils.isNotBlank(taxperiod) && StringUtils.isNotBlank(taxperiod)) {
 * buildQuery.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod "); }
 * 
 * String queryStr = creategstnTransQueryString(buildQuery.toString());
 * 
 * Query outquery = entityManager.createNativeQuery(queryStr);
 * 
 * if (StringUtils.isNotBlank(taxperiod)) { outquery.setParameter("taxperiod",
 * GenUtil.convertTaxPeriodToInt(taxperiod)); } if
 * (CollectionUtils.isNotEmpty(gstinList)) { outquery.setParameter("gstinList",
 * gstinList); } if (StringUtils.isNotEmpty(type) && type!=null) {
 * outquery.setParameter("type", type); }
 * 
 * @SuppressWarnings("unchecked") List<Object[]> list =
 * outquery.getResultList(); return list.parallelStream().map(o ->
 * convertTransactionalLevel(o))
 * .collect(Collectors.toCollection(LinkedList::new)); }
 * 
 * private TDSDetailedRespDto convertTransactionalLevel(Object[] arr) {
 * TDSDetailedRespDto obj = new TDSDetailedRespDto();
 * 
 * obj.setCategory(arr[0] != null ? arr[0].toString() : null);
 * obj.setTaxPayerGSTIN(arr[1] != null ? arr[1].toString() : null);
 * obj.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
 * obj.setDeductorCollectorGSTIN( arr[3] != null ? arr[3].toString() : null);
 * obj.setMonthofDeductorCollectorUpload( arr[4] != null ? arr[4].toString() :
 * null); obj.setOriginalMonthofDeductorCollectorUpload( arr[5] != null ?
 * arr[5].toString() : null); obj.setTotalAmount(arr[6] != null ?
 * arr[6].toString() : null); obj.setSuppliesToRegisteredBuyers( arr[7] != null
 * ? arr[7].toString() : null); obj.setSuppliesReturnedbyRegisteredBuyers(
 * arr[8] != null ? arr[8].toString() : null); obj.setSuppliestoURbuyers(arr[9]
 * != null ? arr[9].toString() : null); obj.setSuppliesReturnedbyURbuyers(
 * arr[10] != null ? arr[10].toString() : null); obj.setIgst(arr[11] != null ?
 * arr[11].toString() : null); obj.setCgst(arr[12] != null ? arr[12].toString()
 * : null); obj.setSgst(arr[13] != null ? arr[13].toString() : null);
 * obj.setActionSavedatGSTN(arr[14] != null ? arr[14].toString() : null);
 * obj.setActionSavedatDigiGST( arr[15] != null ? arr[15].toString() : null);
 * obj.setUserID(arr[16] != null ? arr[16].toString() : null);
 * 
 * obj.setFileID(arr[17] != null ? arr[17].toString() : null);
 * obj.setFileName(arr[18] != null ? arr[18].toString() : null);
 * obj.setGSTNStatus(arr[19] != null ? arr[19].toString() : null);
 * 
 * obj.setGSTNRefid(arr[20] != null ? arr[20].toString() : null);
 * obj.setGSTNRefidDateTime(arr[21] != null ? arr[21].toString() : null);
 * obj.setGSTNErrorCode(arr[22] != null ? arr[22].toString() : null);
 * obj.setGSTNErrorDescription( arr[23] != null ? arr[23].toString() : null);
 * 
 * return obj;
 * 
 * }
 * 
 * private String creategstnTransQueryString(String buildQuery) {
 * 
 * LOGGER.error("bufferString-------------------------->" + buildQuery); return
 * " SELECT RECORD_TYPE AS CATEGORY,GSTIN,HDR.RET_PERIOD," +
 * "CTIN AS DED_COL_GSTIN,DEDUCTOR_UPL_MONTH, CASE WHEN" +
 * " RECORD_TYPE IN ('TDSA','TCSA') THEN ORG_DEDUCTOR_UPL_MONTH " +
 * "ELSE '' END AS ORG_DEDUCTOR_UPL_MONTH,TAXABLE_VALUE," +
 * " CASE WHEN RECORD_TYPE IN ('TCS','TCSA') THEN REG_SUP " +
 * "ELSE 0 END REG_SUP, CASE WHEN RECORD_TYPE IN ('TCS','TCSA')" +
 * " THEN REG_SUPRET ELSE 0 END REG_SUPRET, CASE WHEN " +
 * "RECORD_TYPE IN ('TCS','TCSA') THEN UNREG_SUP ELSE 0 END " +
 * "UNREG_SUP, CASE WHEN RECORD_TYPE IN ('TCS','TCSA') THEN" +
 * " UNREG_SUPRET ELSE 0 END UNREG_SUPRET, IGST_AMT,CGST_AMT," +
 * "SGST_AMT,SAVED_ACTION,USER_ACTION, FIL.CREATED_BY USER_ID," +
 * "FIL.ID FILE_ID,FIL.FILE_NAME FILE_NAME, (CASE WHEN" +
 * " IS_SAVED_TO_GSTN = TRUE THEN 'SAVED' WHEN IS_SAVED_TO_GSTN" +
 * " = FALSE AND GSTN_ERROR = TRUE THEN 'ERROR' WHEN " +
 * "IS_SAVED_TO_GSTN = FALSE AND GSTN_ERROR = FALSE THEN " +
 * "'NOT_SAVED' END) AS GSTN_SAVE_STATUS, " +
 * "GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, " +
 * "GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, TRIM(', ' FROM " +
 * "IFNULL(GSTN_ERROR_CODE,'')) AS GSTN_ERROR_CODE, TRIM(', '" +
 * " FROM IFNULL(GSTN_ERROR_DESCRIPTION,'')) AS " +
 * "GSTN_ERROR_DESCRIPTION FROM GSTR2X_PROCESSED_TCS_TDS HDR " +
 * "LEFT OUTER JOIN FILE_STATUS FIL ON FILE_ID=FIL.ID LEFT " +
 * "OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON" +
 * " GSTNBATCH.ID = BATCH_ID WHERE HDR.IS_DELETE=FALSE AND" +
 * " DATAORIGINTYPECODE='E' " + buildQuery;
 * 
 * }
 * 
 * }
 */