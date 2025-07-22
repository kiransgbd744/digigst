package com.ey.advisory.app.services.jobs.gstr6;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.simplified.TDSDetailedRespDto;
import com.ey.advisory.app.docs.dto.simplified.TDSDownlaodRequestDto;
import com.ey.advisory.common.GenUtil;

@Component("TdsDetailsScreensDaoImpl")
public class TdsDetailsScreensDaoImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6SaveStatusDownloadDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<TDSDetailedRespDto> fetchTdsByReq(
			TDSDownlaodRequestDto criteria) {

		String taxPeriod = criteria.getTaxPeriod();
		String type = criteria.getType();
		List<String> gstinList = criteria.getGstin();
		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildQuery1 = new StringBuilder();
		StringBuilder buildQuery2 = new StringBuilder();

		if (gstinList != null && gstinList.size() > 0) {
			buildQuery.append(" WHERE GSTIN IN :gstinList ");
		}
		if (StringUtils.isNotBlank(taxPeriod)
				&& StringUtils.isNotBlank(taxPeriod)) {
			buildQuery.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}
		if (StringUtils.isNotEmpty(type) && type != null) {
			buildQuery.append(" AND CATEGORY = :type");
		}

		String queryStr = creategstnTransQueryString(buildQuery.toString(),
				buildQuery1.toString(), buildQuery2.toString());

		Query outquery = entityManager.createNativeQuery(queryStr);

		if (StringUtils.isNotBlank(taxPeriod)) {
			outquery.setParameter("taxPeriod",
					GenUtil.convertTaxPeriodToInt(taxPeriod));
		}
		if (CollectionUtils.isNotEmpty(gstinList)) {
			outquery.setParameter("gstinList", gstinList);
		}
		if (StringUtils.isNotEmpty(type) && type != null) {
			outquery.setParameter("type", type);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> list = outquery.getResultList();
		return list.parallelStream().map(o -> convertTransactionalLevel(o))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	private TDSDetailedRespDto convertTransactionalLevel(Object[] arr) {
		TDSDetailedRespDto obj = new TDSDetailedRespDto();
		
		obj.setActionSavedatDigiGST(arr[0] != null ? arr[0].toString() : null);
		obj.setDigiGstRemarks(arr[1] != null ? arr[1].toString() : null);
		obj.setDigiGstComment(arr[2] != null ? arr[2].toString() : null);
		obj.setGstin(
				arr[3] != null ? arr[3].toString() : null);
		obj.setType(
				arr[4] != null ? arr[4].toString() : null);
		obj.setTaxPeriod(
				arr[5] != null ? arr[5].toString() : null);
		obj.setMonth(arr[6] != null ? arr[6].toString() : null);
		obj.setGstinOfDeductor(
				arr[7] != null ? arr[7].toString() : null);
		obj.setDeductorName(
				arr[8] != null ? arr[8].toString() : null);
		obj.setDocNo(arr[9] != null ? arr[9].toString() : null);
		obj.setDocDate(
				arr[10] != null ? arr[10].toString() : null);
		obj.setOrgMonth(arr[11] != null ? arr[11].toString() : null);
		obj.setOrgDocNo(arr[12] != null ? arr[12].toString() : null);
		obj.setOrgDocDate(arr[13] != null ? arr[13].toString() : null);
		obj.setSuppliesCollected(arr[14] != null ? arr[14].toString() : null);
		obj.setSuppliesReturned(
				arr[15] != null ? arr[15].toString() : null);
		obj.setNetSupplies(
				arr[16] != null ? arr[16].toString() : null);
		obj.setIGST(
				arr[17] != null ? arr[17].toString() : null);
		obj.setCGST(
				arr[18] != null ? arr[18].toString() : null);
		obj.setSGST(
				arr[19] != null ? arr[19].toString() : null);
		obj.setInvoiceValue(
				arr[20] != null ? arr[20].toString() : null);
		obj.setOrgTaxableValue(
				arr[21] != null ? arr[21].toString() : null);
		obj.setOrgInvoiceValue(
				arr[22] != null ? arr[22].toString() : null);
		obj.setPos(
				arr[23] != null ? arr[23].toString() : null);
		obj.setChkSum(
				arr[24] != null ? arr[24].toString() : null);
		obj.setActionSavedatGSTN(
				arr[25] != null ? arr[25].toString() : null);
		obj.setGstnRemarks(
				arr[26] != null ? arr[26].toString() : null);
		obj.setGstnComment(
				arr[27] != null ? arr[27].toString() : null);

		

		return obj;

	}

	private String creategstnTransQueryString(String buildQuery,
			String buildQuery1, String buildQuery2) {

		return " SELECT CATEGORY,GSTIN,RET_PERIOD,DERIVED_RET_PERIOD,DED_COL_GSTIN,"
				+ "DEDUCTOR_UPL_MONTH,ORG_DEDUCTOR_UPL_MONTH, TAXABLE_VALUE,REG_SUP,REG_SUPRET,UNREG_SUP,"
				+ "UNREG_SUPRET, IGST_AMT,CGST_AMT,SGST_AMT,SAVED_ACTION,USER_ACTION,USER_ID, FILE_ID, "
				+ "FILE_NAME, GSTN_SAVE_STATUS, GSTIN_REF_ID, GSTIN_REF_ID_TIME, GSTN_ERROR_CODE, "
				+ "GSTN_ERROR_DESCRIPTION from ( SELECT RECORD_TYPE AS CATEGORY,TDS.GSTIN,TDS.RET_PERIOD,"
				+ "TDS.DERIVED_RET_PERIOD, TDS.CTIN AS DED_COL_GSTIN,DEDUCTOR_UPL_MONTH, CASE WHEN RECORD_TYPE='TDSA' "
				+ "THEN ORG_DEDUCTOR_UPL_MONTH ELSE '' END AS ORG_DEDUCTOR_UPL_MONTH,TAXABLE_VALUE, NULL REG_SUP, "
				+ "NULL REG_SUPRET, NULL UNREG_SUP, NULL UNREG_SUPRET, IGST_AMT, CGST_AMT,SGST_AMT,SAVED_ACTION, null USER_ACTION, "
				+ "null USER_ID, null FILE_ID,null FILE_NAME, null GSTN_SAVE_STATUS, null GSTIN_REF_ID, "
				+ "null GSTIN_REF_ID_TIME, null GSTN_ERROR_CODE, null GSTN_ERROR_DESCRIPTION FROM "
				+ "GETGSTR2X_TDS_TDSA TDS LEFT JOIN GETANX1_BATCH_TABLE BT ON TDS.BATCH_ID = BT.ID "
				+ "WHERE TDS.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND USER_ACTION_FLAG IS NULL "
				+ " UNION ALL SELECT RECORD_TYPE AS CATEGORY,TCS.GSTIN,TCS.RET_PERIOD,TCS.DERIVED_RET_PERIOD,"
				+ "TCS.CTIN AS DED_COL_GSTIN, DEDUCTOR_UPL_MONTH, CASE WHEN RECORD_TYPE='TCSA' "
				+ "THEN ORG_DEDUCTOR_UPL_MONTH ELSE '' END AS ORG_DEDUCTOR_UPL_MONTH,TAXABLE_VALUE, "
				+ "REG_SUP, REG_SUPRET, UNREG_SUP, UNREG_SUPRET, IGST_AMT,CGST_AMT,SGST_AMT, SAVED_ACTION, "
				+ "null USER_ACTION, null USER_ID, null FILE_ID,null FILE_NAME, null GSTN_SAVE_STATUS, "
				+ "null GSTIN_REF_ID, null GSTIN_REF_ID_TIME, null GSTN_ERROR_CODE, null GSTN_ERROR_DESCRIPTION "
				+ "FROM GETGSTR2X_TCS_TCSA TCS LEFT JOIN GETANX1_BATCH_TABLE BT ON TCS.BATCH_ID = BT.ID "
				+ "WHERE TCS.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND USER_ACTION_FLAG IS NULL "
				+ " UNION ALL "
				+ "SELECT RECORD_TYPE AS CATEGORY,GSTIN,HDR.RET_PERIOD,HDR.DERIVED_RET_PERIOD, CTIN AS DED_COL_GSTIN,"
				+ "DEDUCTOR_UPL_MONTH, CASE WHEN RECORD_TYPE IN ('TDSA','TCSA') "
				+ "THEN ORG_DEDUCTOR_UPL_MONTH ELSE '' END AS ORG_DEDUCTOR_UPL_MONTH,TAXABLE_VALUE, "
				+ "CASE WHEN RECORD_TYPE IN ('TCS','TCSA') THEN REG_SUP ELSE NULL END REG_SUP, "
				+ "CASE WHEN RECORD_TYPE IN ('TCS','TCSA') THEN REG_SUPRET ELSE NULL END REG_SUPRET, "
				+ "CASE WHEN RECORD_TYPE IN ('TCS','TCSA') THEN UNREG_SUP ELSE NULL END UNREG_SUP, "
				+ "CASE WHEN RECORD_TYPE IN ('TCS','TCSA') THEN UNREG_SUPRET ELSE NULL END UNREG_SUPRET, "
				+ "IGST_AMT,CGST_AMT, SGST_AMT,SAVED_ACTION,USER_ACTION, FIL.CREATED_BY USER_ID, "
				+ "FIL.ID FILE_ID,FIL.FILE_NAME FILE_NAME, (CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'SAVED' "
				+ "WHEN IS_SAVED_TO_GSTN = FALSE AND GSTN_ERROR = TRUE THEN 'ERROR' "
				+ "WHEN IS_SAVED_TO_GSTN = FALSE AND GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) "
				+ "AS GSTN_SAVE_STATUS, GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, "
				+ "GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, TRIM(', ' FROM IFNULL(GSTN_ERROR_CODE,'')) "
				+ "AS GSTN_ERROR_CODE, TRIM(', ' FROM IFNULL(GSTN_ERROR_DESCRIPTION,'')) "
				+ "AS GSTN_ERROR_DESCRIPTION FROM GSTR2X_PROCESSED_TCS_TDS HDR LEFT OUTER JOIN FILE_STATUS "
				+ "FIL ON FILE_ID=FIL.ID LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = BATCH_ID "
				+ " WHERE HDR.IS_DELETE=FALSE ) " + buildQuery
				+ " ORDER BY DED_COL_GSTIN, CATEGORY ";

	}

}
