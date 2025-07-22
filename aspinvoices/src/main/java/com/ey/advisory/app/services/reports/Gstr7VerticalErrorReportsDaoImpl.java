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

import com.ey.advisory.app.data.views.client.Gstr7ErrorRecords;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.common.ErrorMasterUtil;

import com.google.common.base.Strings;

@Component("Gstr7VerticalErrorReportsDaoImpl")
public class Gstr7VerticalErrorReportsDaoImpl implements Gstr7VerticalDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr7VerticalErrorReportsDaoImpl.class);

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

	private Gstr7ErrorRecords convertVerticalTotal(Object[] arr) {
		Gstr7ErrorRecords obj = new Gstr7ErrorRecords();

		String errDesc = null;
		String errCode = (arr[0] != null ? arr[0].toString() : null);
		
		if(!Strings.isNullOrEmpty(errCode)){
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errDesc = ErrorMasterUtil.getErrorCodeWithoutIndex(errCodeList, "GSTR7");
		}
		obj.setAspError(errDesc);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setActionType(arr[2] != null ? arr[2].toString() : null);
		obj.settDSDeductorGSTIN(arr[3] != null ? arr[3].toString() : null);
		obj.setOriginalTDSDeducteeGSTIN(arr[4] != null ? arr[4].toString() : null);
		obj.setOriginalReturnPeriod(arr[5] != null ? arr[5].toString() : null);
		obj.setOriginalGrossAmount(arr[6] != null ? arr[6].toString() : null);
		obj.settDSDeducteeGSTIN(arr[7] != null ? arr[7].toString() : null);
		obj.setGrossAmount(arr[8] != null ? arr[8].toString() : null);
		obj.settDSIGST(arr[9] != null ? arr[9].toString() : null);
		obj.settDSCGST(arr[10] != null ? arr[10].toString() : null);
		obj.settDSSGST(arr[11] != null ? arr[11].toString() : null);
		obj.setContractNumber(arr[12] != null ? arr[12].toString() : null);
		obj.setContractDate(arr[13] != null ? arr[13].toString() : null);
		obj.setContractValue(arr[14] != null ? arr[14].toString() : null);
		obj.setPaymentAdviceNumber(arr[15] != null ? arr[15].toString() : null);
		obj.setPaymentAdviceDate(arr[16] != null ? arr[16].toString() : null);
		obj.setDocumentNumber(arr[17] != null ? arr[17].toString() : null);
		obj.setDocumentDate(arr[18] != null ? arr[18].toString() : null);
		obj.setInvoiceValue(arr[19] != null ? arr[19].toString() : null);
		obj.setPlantCode(arr[20] != null ? arr[20].toString() : null);
		obj.setDivision(arr[21] != null ? arr[21].toString() : null);
		obj.setPurchaseOrganisation(arr[22] != null ? arr[22].toString() : null);
		obj.setProfitCentre1(arr[23] != null ? arr[23].toString() : null);
		obj.setProfitCentre2(arr[24] != null ? arr[24].toString() : null);
		obj.setUserDefinedField1(arr[25] != null ? arr[25].toString() : null);
		obj.setUserDefinedField2(arr[26] != null ? arr[26].toString() : null);
		obj.setUserDefinedField3(arr[27] != null ? arr[27].toString() : null);
		return obj;
	}

	private String createVerticalTotalQueryString(String buildQuery) {
		return "SELECT TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') ) AS ERROR_CODE_ASP, "
				+ "TDS.RETURN_PERIOD,ACTION_TYPE, TDS_DEDUCTOR_GSTIN,"
				+ "ORG_TDS_DEDUCTEE_GSTIN,ORG_RETURN_PERIOD,ORG_GROSS_AMT,"
				+ "NEW_TDS_DEDUCTEE_GSTIN,NEW_GROSS_AMT,IGST_AMT,CGST_AMT,"
				+ "SGST_AMT, "
				+ "CONTRACT_NUMBER,CONTRACT_DATE,CONTRACT_VALUE,"
				+ "PAYMENT_ADV_NUM,PAYMENT_ADV_DATE, DOC_NUM,DOC_DATE,INVOICE_VALUE, "
				+ "PLANT_CODE,DIVISION,PURCHASE_ORGANIZATION ,PROFIT_CENTRE1,"
				+ "PROFIT_CENTRE2, USERDEFINED_FIELD1,USERDEFINED_FIELD2,USERDEFINED_FIELD3 "
				+ "FROM GSTR7_AS_ENTERED_TDS TDS LEFT OUTER JOIN " + "(SELECT DOC_HEADER_ID ,INV_KEY ,FILE_ID ,"
				+ "STRING_AGG(ERROR_CODE_ASP,',') ERROR_CODE_ASP ,"
				+ "STRING_AGG(ERROR_DESCRIPTION_ASP,',') ERROR_DESCRIPTION_ASP ,"
				+ "STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP ,"
				+ "STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') INFO_ERROR_DESCRIPTION_ASP "
				+ "FROM ( SELECT DOC_HEADER_ID,INV_KEY,FILE_ID, "
				+ "(CASE WHEN ERROR_TYPE='ERR' THEN ERROR_CODE END) "
				+ "AS ERROR_CODE_ASP, (CASE WHEN ERROR_TYPE='ERR' "
				+ "THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_ASP, "
				+ "(CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END) AS INFO_ERROR_CODE_ASP, "
				+ "(CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END) "
				+ "AS INFO_ERROR_DESCRIPTION_ASP FROM GSTR7_DOC_ERROR "
				+ "WHERE ERROR_SOURCE= 'WEBUPLOAD' ) GROUP BY DOC_HEADER_ID,INV_KEY,FILE_ID) "
				+ "ERR ON TDS.ID= ERR.DOC_HEADER_ID AND TDS.FILE_ID=ERR.FILE_ID "
				+ "LEFT OUTER JOIN FILE_STATUS FIL ON TDS.FILE_ID=FIL.ID "
				+ "AND ERR.FILE_ID=FIL.ID AND ERR.INV_KEY=TDS.TDS_INVKEY "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH "
				+ "ON GSTNBATCH.ID = TDS.BATCH_ID WHERE TDS.IS_DELETE=FALSE " + "AND TDS.IS_ERROR=TRUE  " + buildQuery;

	}
}
