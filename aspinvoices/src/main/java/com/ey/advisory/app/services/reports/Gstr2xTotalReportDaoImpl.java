/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.TdsTdsaTotaldto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.common.ErrorMasterUtil;

import com.google.common.base.Strings;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Gstr2xTotalReportDaoImpl")
public class Gstr2xTotalReportDaoImpl implements Gstr2xReportDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2xTotalReportDaoImpl.class);

	@Override
	public List<Object> getGstr2xReports(
			Gstr1VerticalDownloadReportsReqDto request) {

		Long fileId = request.getFileId();

		StringBuilder buildQuery = new StringBuilder();
		if (fileId != null) {
			buildQuery.append(" AND HDR.FILE_ID= :fileId ");
		}

		String queryStr = createVerticalTotalQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();

		List<Object> retList = list.parallelStream()
				.map(o -> convertVerticalTotal(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private TdsTdsaTotaldto convertVerticalTotal(Object[] arr) {
		TdsTdsaTotaldto obj = new TdsTdsaTotaldto();
		
        String errDesc = null;
        String infoDesc = null;
		
		String errCode = (arr[0] != null) ? arr[0].toString() : null;
		obj.setAspErrorCode(errCode);

		if (!Strings.isNullOrEmpty(errCode)) {
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errDesc = ErrorMasterUtil.getErrorInfo(errCodeList, "GSTR2X_VERTICAL");
		}

		obj.setAspErrorDesc(errDesc);
		String infoCode = (arr[2] != null) ? arr[2].toString() : null;
		obj.setAspInformationCode(infoCode);

		if (!Strings.isNullOrEmpty(infoCode)) {
			String[] infoCodes = infoCode.split(",");
			List<String> infoCodeList = Arrays.asList(infoCodes);
			infoDesc = ErrorMasterUtil.getErrorInfo(infoCodeList, "GSTR2X_VERTICAL");
		}

		obj.setAspInformationDesc(infoDesc);
		obj.setActionSavedatDigiGST(arr[4] != null ? arr[4].toString() : null);
		obj.setDigiGstRemarks(arr[5] != null ? arr[5].toString() : null);
		obj.setDigiGstComment(arr[6] != null ? arr[6].toString() : null);
		obj.setGstin(
				arr[7] != null ? arr[7].toString() : null);
		obj.setType(
				arr[8] != null ? arr[8].toString() : null);
		obj.setTaxPeriod(
				arr[9] != null ? arr[9].toString() : null);
		obj.setMonth(arr[10] != null ? arr[10].toString() : null);
		obj.setGstinOfDeductor(
				arr[11] != null ? arr[11].toString() : null);
		obj.setDeductorName(
				arr[12] != null ? arr[12].toString() : null);
		obj.setDocNo(arr[13] != null ? arr[13].toString() : null);
		obj.setDocDate(
				arr[14] != null ? arr[14].toString() : null);
		obj.setOrgMonth(arr[15] != null ? arr[15].toString() : null);
		obj.setOrgDocNo(arr[16] != null ? arr[16].toString() : null);
		obj.setOrgDocDate(arr[17] != null ? arr[17].toString() : null);
		obj.setSuppliesCollected(arr[18] != null ? arr[18].toString() : null);
		obj.setSuppliesReturned(
				arr[19] != null ? arr[19].toString() : null);
		obj.setNetSupplies(
				arr[20] != null ? arr[20].toString() : null);
		obj.setIGST(
				arr[21] != null ? arr[21].toString() : null);
		obj.setCGST(
				arr[22] != null ? arr[22].toString() : null);
		obj.setSGST(
				arr[23] != null ? arr[23].toString() : null);
		obj.setInvoiceValue(
				arr[24] != null ? arr[24].toString() : null);
		obj.setOrgTaxableValue(
				arr[25] != null ? arr[25].toString() : null);
		obj.setOrgInvoiceValue(
				arr[26] != null ? arr[26].toString() : null);
		obj.setPos(
				arr[27] != null ? arr[27].toString() : null);
		obj.setChkSum(
				arr[28] != null ? arr[28].toString() : null);
		obj.setActionSavedatGSTN(
				arr[29] != null ? arr[29].toString() : null);
		obj.setGstnRemarks(
				arr[30] != null ? arr[30].toString() : null);
		obj.setGstnComment(
				arr[31] != null ? arr[31].toString() : null);

		return obj;
	}

	private String createVerticalTotalQueryString(String buildQuery) {
		return "SELECT TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') ) "
				+ "AS ASP_ERROR_CODE, "	
				+ " '' AS ASP_ERROR_DESCRIPTION, TRIM(', ' FROM "
				+ "IFNULL(INFO_ERROR_CODE_ASP,'') ) AS ASP_INFO_CODE, "	
				+ " '' AS ASP_INFO_DESCRIPTION, RECORD_TYPE AS CATEGORY,GSTIN,"
				+ "RET_PERIOD,CTIN AS DED_COL_GSTIN,DEDUCTOR_UPL_MONTH, "
				+ "CASE WHEN RECORD_TYPE IN ('TDSA','TCSA') "
				+ "THEN ORG_DEDUCTOR_UPL_MONTH ELSE '' END AS ORG_DEDUCTOR_UPL_MONTH, "
				+ "TAXABLE_VALUE AS TOT_AMT, "
				+ "CASE WHEN RECORD_TYPE IN ('TCS','TCSA') "
				+ "THEN REG_SUP ELSE null END REG_SUP, "
				+ "CASE WHEN RECORD_TYPE IN ('TCS','TCSA') "
				+ "THEN REG_SUPRET ELSE null END REG_SUPRET, "
				+ "CASE WHEN RECORD_TYPE IN ('TCS','TCSA') "
				+ "THEN UNREG_SUP ELSE null END UNREG_SUP, "
				+ "CASE WHEN RECORD_TYPE IN ('TCS','TCSA') "
				+ "THEN UNREG_SUPRET ELSE null END UNREG_SUPRET, IGST_AMT,"
				+ "CGST_AMT,SGST_AMT,SAVED_ACTION,USER_ACTION "
				+ "FROM GSTR2X_AS_ENTERED_TCS_TDS "
				+ "HDR LEFT OUTER JOIN (SELECT COMMON_ID,INV_KEY,FILE_ID ,"
				+ "STRING_AGG(ERROR_CODE_ASP,',') ERROR_CODE_ASP ,"
				+ "STRING_AGG(ERROR_DESCRIPTION_ASP,',') ERROR_DESCRIPTION_ASP ,"
				+ "STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP ,"
				+ "STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') "
				+ "INFO_ERROR_DESCRIPTION_ASP "
				+ "FROM (SELECT COMMON_ID,INV_KEY,FILE_ID, "
				+ "(CASE WHEN ERROR_TYPE='ERROR' THEN ERROR_CODE END) "
				+ "AS ERROR_CODE_ASP, (CASE WHEN ERROR_TYPE='ERR' "
				+ "THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_ASP, "
				+ "(CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END) "
				+ "AS INFO_ERROR_CODE_ASP, (CASE WHEN ERROR_TYPE='INFO' "
				+ "THEN ERROR_DESCRIPTION END) AS INFO_ERROR_DESCRIPTION_ASP "
				+ "FROM VERTICAL_ERROR WHERE ERROR_SOURCE='WEB_UPLOAD') "
				+ " GROUP BY "
				+ "COMMON_ID,INV_KEY,FILE_ID) ERR ON HDR.ID= ERR.COMMON_ID "
				+ "AND HDR.FILE_ID=ERR.FILE_ID LEFT OUTER JOIN "
				+ "FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
				+ "AND ERR.FILE_ID=FIL.ID AND ERR.INV_KEY=HDR.DOC_KEY "
				+ " WHERE HDR.IS_DELETE=FALSE AND DATAORIGINTYPECODE='E' " + buildQuery;

	}
}
