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

import com.ey.advisory.app.data.views.client.DistributionTotalDto;
import com.ey.advisory.app.data.views.client.Gstr7TotalRecords;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.ErrorMasterUtil;

import com.google.common.base.Strings;

@Component("Gstr7VerticalTotalReportsDaoImpl")
public class Gstr7VerticalTotalReportsDaoImpl implements Gstr7VerticalDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr7VerticalTotalReportsDaoImpl.class);

	@Override
	public List<Object> getGstr7VerticalReports(Gstr1VerticalDownloadReportsReqDto request) {

		Long fileId = request.getFileId();

		StringBuilder buildQuery = new StringBuilder();
		if (fileId != null) {
			buildQuery.append(" AND TDS.FILE_ID= :fileId ");
		}
		String queryStr = createVerticalTotalQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		LOGGER.debug("Reading query " + queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();

		LOGGER.debug("Reading Resultset" + list);
		List<Object> retList = list.parallelStream().map(o -> convertVerticalTotal(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Gstr7TotalRecords convertVerticalTotal(Object[] arr) {
		Gstr7TotalRecords obj = new Gstr7TotalRecords();

		String errDesc = null;
		String errCode = (arr[0] != null ? arr[0].toString() : null);
		
		if(!Strings.isNullOrEmpty(errCode)){
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errDesc = ErrorMasterUtil.getErrorCodeWithoutIndex(errCodeList, "GSTR7");
		}
		obj.setAspError(errDesc);
		obj.setGstnStatus(arr[1] != null ? arr[1].toString() : null);
		obj.setGstnRefid(arr[2] != null ? arr[2].toString() : null);
		obj.setGstnRefidDateTime(arr[3] != null ? arr[3].toString() : null);
		obj.setgSTNErrorCode(arr[4] != null ? arr[4].toString() : null);
		obj.setGstnErrorDescription(arr[5] != null ? arr[5].toString() : null);
		obj.setTableNumber(arr[6] != null ? arr[6].toString() : null);
		obj.setReturnPeriod(arr[7] != null ? arr[7].toString() : null);
		obj.setActionType(arr[8] != null ? arr[8].toString() : null);
		obj.settDSDeductorGSTIN(arr[9] != null ? arr[9].toString() : null);
		obj.setOriginalTDSDeducteeGSTIN(arr[10] != null ? arr[10].toString() : null);
		obj.setOriginalReturnPeriod(arr[11] != null ? arr[11].toString() : null);
		obj.setOriginalGrossAmount(arr[12] != null ? arr[12].toString() : null);
		obj.settDSDeducteeGSTIN(arr[13] != null ? arr[13].toString() : null);
		obj.setGrossAmount(arr[14] != null ? arr[14].toString() : null);
		obj.settDSIGST(arr[15] != null ? arr[15].toString() : null);
		obj.settDSCGST(arr[16] != null ? arr[16].toString() : null);
		obj.settDSSGST(arr[17] != null ? arr[17].toString() : null);
		obj.setContractNumber(arr[18] != null ? arr[18].toString() : null);
		obj.setContractDate(arr[19] != null ? arr[19].toString() : null);
		obj.setContractValue(arr[20] != null ? arr[20].toString() : null);
		obj.setPaymentAdviceNumber(arr[21] != null ? arr[21].toString() : null);
		obj.setPaymentAdviceDate(arr[22] != null ? arr[22].toString() : null);
		obj.setDocumentNumber(arr[23] != null ? arr[23].toString() : null);
		obj.setDocumentDate(arr[24] != null ? arr[24].toString() : null);
		obj.setInvoiceValue(arr[25] != null ? arr[25].toString() : null);
		obj.setPlantCode(arr[26] != null ? arr[26].toString() : null);
		obj.setDivision(arr[27] != null ? arr[27].toString() : null);
		obj.setPurchaseOrganisation(arr[28] != null ? arr[28].toString() : null);
		obj.setProfitCentre1(arr[29] != null ? arr[29].toString() : null);
		obj.setProfitCentre2(arr[30] != null ? arr[30].toString() : null);
		obj.setUserDefinedField1(arr[31] != null ? arr[31].toString() : null);
		obj.setUserDefinedField2(arr[32] != null ? arr[32].toString() : null);
		obj.setUserDefinedField3(arr[33] != null ? arr[33].toString() : null);
		return obj;
	}

	private String createVerticalTotalQueryString(String buildQuery) {
		return "SELECT TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') ) AS ERROR_CODE_ASP, "
				+ "'' AS GSTN_SAVE_STATUS, GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, "
				+ "GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME,'' AS GSTN_ERROR_CODE, TRIM(', ' FROM "
				+ "IFNULL(ERROR_DESCRIPTION_GSTN,'')) AS GSTN_ERROR_DESCRIPTION, '' TABLE_NUM,"
				+ "TDS.RETURN_PERIOD,ACTION_TYPE, TDS_DEDUCTOR_GSTIN,ORG_TDS_DEDUCTEE_GSTIN,"
				+ "ORG_RETURN_PERIOD,ORG_GROSS_AMT, NEW_TDS_DEDUCTEE_GSTIN,NEW_GROSS_AMT,"
				+ "IGST_AMT,CGST_AMT,SGST_AMT,CONTRACT_NUMBER,CONTRACT_DATE,"
				+ "CONTRACT_VALUE,"
				+ "PAYMENT_ADV_NUM,PAYMENT_ADV_DATE, DOC_NUM,DOC_DATE,INVOICE_VALUE, "
				+ "PLANT_CODE,DIVISION,PURCHASE_ORGANIZATION ,PROFIT_CENTRE1,PROFIT_CENTRE2, "
				+ "USERDEFINED_FIELD1,USERDEFINED_FIELD2,USERDEFINED_FIELD3 "
				+ "FROM GSTR7_AS_ENTERED_TDS TDS LEFT OUTER JOIN "
				+ "(SELECT DOC_HEADER_ID ,INV_KEY ,FILE_ID ,STRING_AGG(ERROR_CODE_GSTN,',') "
				+ "ERROR_CODE_GSTN ,STRING_AGG(ERROR_DESCRIPTION_GSTN,',') "
				+ "ERROR_DESCRIPTION_GSTN ,STRING_AGG(INFO_ERROR_CODE_GSTN,',') "
				+ "INFO_ERROR_CODE_GSTN ,STRING_AGG(INFO_ERROR_DESCRIPTION_GSTN,',') "
				+ "INFO_ERROR_DESCRIPTION_GSTN ,STRING_AGG(ERROR_CODE_ASP,',') "
				+ "ERROR_CODE_ASP ,STRING_AGG(ERROR_DESCRIPTION_ASP,',') "
				+ "ERROR_DESCRIPTION_ASP ,STRING_AGG(INFO_ERROR_CODE_ASP,',') "
				+ "INFO_ERROR_CODE_ASP ,STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') "
				+ "INFO_ERROR_DESCRIPTION_ASP FROM ( SELECT DOC_HEADER_ID,INV_KEY,FILE_ID, "
				+ " '' AS ERROR_CODE_GSTN, '' AS ERROR_DESCRIPTION_GSTN, '' AS INFO_ERROR_CODE_GSTN,"
				+ " '' AS INFO_ERROR_DESCRIPTION_GSTN, (CASE WHEN ERROR_TYPE='ERR' "
				+ "THEN ERROR_CODE END) AS ERROR_CODE_ASP, (CASE WHEN ERROR_TYPE='ERR' "
				+ "THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_ASP, (CASE "
				+ "WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END) AS INFO_ERROR_CODE_ASP, "
				+ "(CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END) "
				+ "AS INFO_ERROR_DESCRIPTION_ASP FROM GSTR7_DOC_ERROR " 
				+ "WHERE ERROR_SOURCE='WEBUPLOAD' " + " UNION ALL "
				+ "SELECT DOC_HEADER_ID,INV_KEY,FILE_ID, (CASE WHEN ERROR_TYPE='ERR' "
				+ "THEN ERROR_CODE END) AS ERROR_CODE_GSTN, (CASE WHEN ERROR_TYPE='ERR' "
				+ "THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_GSTN , "
				+ "(CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END) "
				+ "AS INFO_ERROR_CODE_GSTN, (CASE WHEN ERROR_TYPE='INFO' "
				+ "THEN ERROR_DESCRIPTION END)AS INFO_ERROR_DESCRIPTION_GSTN, "
				+ " '' AS ERROR_CODE_ASP,'' AS ERROR_DESCRIPTION_ASP, "
				+ " '' AS INFO_ERROR_CODE_ASP,'' AS INFO_ERROR_DESCRIPTION_ASP "
				+ "FROM GSTR7_DOC_ERROR WHERE ERROR_SOURCE='GSTN' ) "
				+ "GROUP BY DOC_HEADER_ID,INV_KEY,FILE_ID) ERR ON TDS.ID= ERR.DOC_HEADER_ID "
				+ "AND TDS.FILE_ID=ERR.FILE_ID LEFT OUTER JOIN FILE_STATUS FIL ON "
				+ "TDS.FILE_ID=FIL.ID AND ERR.FILE_ID=FIL.ID AND ERR.INV_KEY=TDS.TDS_INVKEY "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH " + "ON GSTNBATCH.ID = TDS.BATCH_ID "
				+ "WHERE TDS.IS_DELETE=FALSE  " + buildQuery;

	}
}
