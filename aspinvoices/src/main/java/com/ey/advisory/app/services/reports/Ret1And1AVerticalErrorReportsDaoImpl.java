/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Ret1And1AInterestAndLateFeeErrorDto;
import com.ey.advisory.app.data.views.client.Ret1And1ARefundsErrorDto;
import com.ey.advisory.app.data.views.client.Ret1And1ASetoffUtilizationErrorDto;
import com.ey.advisory.app.data.views.client.Ret1And1AUserInputsErrorDto;
import com.ey.advisory.app.docs.dto.Anx1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Ret1And1AVerticalErrorReportsDaoImpl")
public class Ret1And1AVerticalErrorReportsDaoImpl
		implements Ret1And1AVerticalReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1And1AVerticalErrorReportsDaoImpl.class);

	static String fileType = null;

	@Override
	public List<Object> getRet1VerticalReports(
			Anx1VerticalDownloadReportsReqDto request) {

		Long fileId = request.getFileId();
		fileType = request.getFileType();

		StringBuilder buildQuery = new StringBuilder();
		if (fileId != null) {
			buildQuery.append(" HI.FILE_ID= :fileId ");
		}
		/*
		 * StringBuilder build3h3iQuery = new StringBuilder(); if (fileId !=
		 * null) { build3h3iQuery.append(" HI.FILE_ID= :fileId "); }
		 * StringBuilder buildtable4Query = new StringBuilder(); if (fileId !=
		 * null) { buildtable4Query.append(" HI.FILE_ID= :fileId "); }
		 */
		String queryStr = createVerticalErrorQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		LOGGER.debug("Reading query " + queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		LOGGER.debug("Reading Resultset" + list);
		List<Object> retList = list.parallelStream()
				.map(o -> convertVerticalError(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Object convertVerticalError(Object[] arr) {

		Object obj = null;

		Ret1And1AUserInputsErrorDto obj1 = new Ret1And1AUserInputsErrorDto();
		Ret1And1AInterestAndLateFeeErrorDto obj2 = new Ret1And1AInterestAndLateFeeErrorDto();
		Ret1And1ASetoffUtilizationErrorDto obj3 = new Ret1And1ASetoffUtilizationErrorDto();
		Ret1And1ARefundsErrorDto obj4 = new Ret1And1ARefundsErrorDto();
		if ((DownloadReportsConstant.RET1AND1A).equalsIgnoreCase(fileType)) {

			obj1.setReturnType(arr[0] != null ? arr[0].toString() : null);
			obj1.setGstin(arr[1] != null ? arr[1].toString() : null);
			obj1.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
			obj1.setReturnTable(arr[3] != null ? arr[3].toString() : null);
			obj1.setValue(arr[4] != null ? arr[4].toString() : null);
			obj1.setIntegratedTaxAmount(
					arr[5] != null ? arr[5].toString() : null);
			obj1.setCentralTaxAmount(arr[6] != null ? arr[6].toString() : null);
			obj1.setStateUTTaxAmount(arr[7] != null ? arr[7].toString() : null);
			obj1.setCessAmount(arr[8] != null ? arr[8].toString() : null);
			obj1.setProfitCentre(arr[9] != null ? arr[9].toString() : null);
			obj1.setPlant(arr[10] != null ? arr[10].toString() : null);
			obj1.setDivision(arr[11] != null ? arr[11].toString() : null);
			obj1.setLocation(arr[12] != null ? arr[12].toString() : null);
			obj1.setSalesOrganiastion(
					arr[13] != null ? arr[13].toString() : null);
			obj1.setDistributionChannel(
					arr[14] != null ? arr[14].toString() : null);
			obj1.setUserAccess1(arr[15] != null ? arr[15].toString() : null);
			obj1.setUserAccess2(arr[16] != null ? arr[16].toString() : null);
			obj1.setUserAccess3(arr[17] != null ? arr[17].toString() : null);
			obj1.setUserAccess4(arr[18] != null ? arr[18].toString() : null);
			obj1.setUserAccess5(arr[19] != null ? arr[19].toString() : null);
			obj1.setUserAccess6(arr[20] != null ? arr[20].toString() : null);
			obj1.setUserDefined1(arr[21] != null ? arr[21].toString() : null);
			obj1.setUserDefined2(arr[22] != null ? arr[22].toString() : null);
			obj1.setUserDefined3(arr[23] != null ? arr[23].toString() : null);
			obj1.setAspErrorCode(arr[24] != null ? arr[24].toString() : null);
			obj1.setAspErrorDescription(
					arr[25] != null ? arr[25].toString() : null);
			obj1.setAspInformationId(
					arr[26] != null ? arr[26].toString() : null);
			obj1.setAspInformationDescription(
					arr[27] != null ? arr[27].toString() : null);
			obj = obj1;
		}
		if ((DownloadReportsConstant.INTEREST).equalsIgnoreCase(fileType)) {

			obj2.setReturnType(arr[0] != null ? arr[0].toString() : null);
			obj2.setGstin(arr[1] != null ? arr[1].toString() : null);
			obj2.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
			obj2.setReturnTable(arr[3] != null ? arr[3].toString() : null);
			obj2.setInterestIGST(arr[4] != null ? arr[4].toString() : null);
			obj2.setInterestCGST(arr[5] != null ? arr[5].toString() : null);
			obj2.setInterestSGST(arr[6] != null ? arr[6].toString() : null);
			obj2.setInterestCess(arr[7] != null ? arr[7].toString() : null);
			obj2.setLateFeeCGST(arr[8] != null ? arr[8].toString() : null);
			obj2.setLateFeeSGST(arr[9] != null ? arr[9].toString() : null);
			obj2.setUserDefined1(arr[10] != null ? arr[10].toString() : null);
			obj2.setUserDefined2(arr[11] != null ? arr[11].toString() : null);
			obj2.setUserDefined3(arr[12] != null ? arr[12].toString() : null);
			obj2.setAspErrorCode(arr[13] != null ? arr[13].toString() : null);
			obj2.setAspErrorDescription(
					arr[14] != null ? arr[14].toString() : null);
			obj2.setAspInformationId(
					arr[15] != null ? arr[15].toString() : null);
			obj2.setAspInformationDescription(
					arr[16] != null ? arr[16].toString() : null);

			obj = obj2;
		}

		if ((DownloadReportsConstant.SETOFFANDUTIL)
				.equalsIgnoreCase(fileType)) {

			obj3.setReturnType(arr[0] != null ? arr[0].toString() : null);
			obj3.setGstin(arr[1] != null ? arr[1].toString() : null);
			obj3.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
			obj3.setDescription(arr[3] != null ? arr[3].toString() : null);
			obj3.setTaxpayableReversecharge(
					arr[4] != null ? arr[4].toString() : null);
			obj3.setTaxpayableOtherthanReversecharge(
					arr[5] != null ? arr[5].toString() : null);
			obj3.setTaxalreadyPaidReversecharge(
					arr[6] != null ? arr[6].toString() : null);
			obj3.setTaxalreadyPaidOtherthanReversecharge(
					arr[7] != null ? arr[7].toString() : null);
			obj3.setAdjustmemtReversecharge(
					arr[8] != null ? arr[8].toString() : null);
			obj3.setAdjustmemtOtherthanReversecharge(
					arr[9] != null ? arr[9].toString() : null);
			obj3.setPaidthroughITCIntegratedtax(
					arr[10] != null ? arr[10].toString() : null);
			obj3.setPaidthroughITCentraltax(
					arr[11] != null ? arr[11].toString() : null);
			obj3.setPaidthroughITCStatUTtax(
					arr[12] != null ? arr[12].toString() : null);
			obj3.setPaidthroughITCCess(
					arr[13] != null ? arr[13].toString() : null);
			obj3.setPaidincashTaxCess(
					arr[14] != null ? arr[14].toString() : null);
			obj3.setPaidincashInterest(
					arr[15] != null ? arr[15].toString() : null);
			obj3.setPaidincashLateFee(
					arr[16] != null ? arr[16].toString() : null);
			obj3.setUserDefined1(arr[17] != null ? arr[17].toString() : null);
			obj3.setUserDefined2(arr[18] != null ? arr[18].toString() : null);
			obj3.setUserDefined3(arr[19] != null ? arr[19].toString() : null);
			obj3.setAspErrorCode(arr[20] != null ? arr[20].toString() : null);
			obj3.setAspErrorDescription(
					arr[21] != null ? arr[21].toString() : null);
			obj3.setAspInformationId(
					arr[22] != null ? arr[22].toString() : null);
			obj3.setAspInformationDescription(
					arr[23] != null ? arr[23].toString() : null);
			obj = obj3;
		}
		if ((DownloadReportsConstant.REFUNDS).equalsIgnoreCase(fileType)) {

			obj4.setGstin(arr[0] != null ? arr[0].toString() : null);
			obj4.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
			obj4.setDescription(arr[2] != null ? arr[2].toString() : null);
			obj4.setTax(arr[3] != null ? arr[3].toString() : null);
			obj4.setInterest(arr[4] != null ? arr[4].toString() : null);
			obj4.setPenalty(arr[5] != null ? arr[5].toString() : null);
			obj4.setFee(arr[6] != null ? arr[6].toString() : null);
			obj4.setOther(arr[7] != null ? arr[7].toString() : null);
			obj4.setTotal(arr[8] != null ? arr[8].toString() : null);
			obj4.setUserDefined1(arr[9] != null ? arr[9].toString() : null);
			obj4.setUserDefined2(arr[10] != null ? arr[10].toString() : null);
			obj4.setUserDefined3(arr[11] != null ? arr[11].toString() : null);
			obj4.setAspErrorCode(arr[12] != null ? arr[12].toString() : null);
			obj4.setAspErrorDescription(
					arr[13] != null ? arr[13].toString() : null);
			obj4.setAspInformationId(
					arr[14] != null ? arr[14].toString() : null);
			obj4.setAspInformationDescription(
					arr[15] != null ? arr[15].toString() : null);
			obj = obj4;
		}
		return obj;
	}

	private String createVerticalErrorQueryString(String buildQuery) {
		String queryStr = "";

		if ((DownloadReportsConstant.RET1AND1A).equalsIgnoreCase(fileType)) {
			queryStr = "SELECT RETURN_TYPE,GSTIN,RETURN_PERIOD,"
					+ "RETURN_TABLE,TAXABLE_VALUE,IGST_AMT,CGST_AMT,SGST_AMT,"
					+ "CESS_AMT,PROFIT_CENTER,PLANT,DIVISION,LOCATION,"
					+ "SALES_ORG,DISTRIBUTION_CHANNEL,USER_ACCESS1,USER_ACCESS2,"
					+ "USER_ACCESS3,USER_ACCESS4,USER_ACCESS5,USER_ACCESS6,"
					+ "USERDEFINED1,USERDEFINED2,USERDEFINED3,"
					+ "TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') ) "
	                + "AS ERROR_CODE_ASP,"
	                + "TRIM(', ' FROM IFNULL(ERROR_DESCRIPTION_ASP,'') ) "
	                + "AS ERROR_DESCRIPTION_ASP,"
	                + "TRIM(', ' FROM IFNULL(INFO_ERROR_CODE_ASP,'') ) "
	                + "AS INFO_ERROR_CODE_ASP,"
	                + "TRIM(', ' FROM IFNULL(INFO_ERROR_DESCRIPTION_ASP,'') ) "
	                + "AS INFO_ERROR_DESCRIPTION_ASP " 
					+ "FROM RET_AS_ENTERED_USERINPUT HI "
					+ "LEFT OUTER JOIN TF_GSTR1_ERROR_INFO() ERR "
					+ "ON HI.ID = ERR.COMMON_ID AND HI.FILE_ID = ERR.FILE_ID "
					+ "LEFT OUTER JOIN FILE_STATUS FIL "
					+ "ON HI.FILE_ID = FIL.ID AND ERR.FILE_ID = FIL.ID "
					+ "AND ERR.INV_KEY = HI.INVKEY_RET_USERINPUT "
					+ "WHERE HI.IS_DELETE = FALSE AND HI.IS_ERROR = TRUE "
					+ "AND " + buildQuery;

		} else if ((DownloadReportsConstant.INTEREST)
				.equalsIgnoreCase(fileType)) {

			queryStr = "SELECT RETURN_TYPE,GSTIN,RETURN_PERIOD,RETURN_TABLE,"
					+ "INTEREST_IGST_AMT,INTEREST_CGST_AMT,INTEREST_SGST_AMT,"
					+ "INTEREST_CESS_AMT,LATEFEE_CGST_AMT,LATEFEE_SGST_AMT,"
					+ "USERDEFINED1,USERDEFINED2,USERDEFINED3,"
					+ "ERROR_CODE_ASP,ERROR_DESCRIPTION_ASP,INFO_ERROR_CODE_ASP,"
					+ "INFO_ERROR_DESCRIPTION_ASP "
					+ "FROM RET_AS_ENTERED_INTEREST_LATEFEE "
					+ "HI LEFT OUTER JOIN TF_GSTR1_ERROR_INFO() ERR "
					+ "ON HI.ID = ERR.COMMON_ID "
					+ "AND HI.FILE_ID = ERR.FILE_ID "
					+ "LEFT OUTER JOIN FILE_STATUS FIL ON HI.FILE_ID = FIL.ID "
					+ "AND ERR.FILE_ID = FIL.ID AND "
					+ "ERR.INV_KEY = HI.INVKEY_RET_INTEREST_LATEFEE "
					+ "WHERE HI.IS_DELETE = FALSE AND HI.IS_ERROR = TRUE "
					+ "AND " + buildQuery;

		} else if ((DownloadReportsConstant.SETOFFANDUTIL)
				.equalsIgnoreCase(fileType)) {

			queryStr = "SELECT RETURN_TYPE,GSTIN,RETURN_PERIOD,DESCRIPTION,"
					+ "TAX_PAYABLE_REVCHARGE,TAX_PAYABLE_OTHERTHAN_REVCHARGE,"
					+ "TAX_ALREADYPAID_REVCHARGE,"
					+ "TAX_ALREADYPAID_OTHERTHAN_REVCHARGE,"
					+ "ADJ_NEG_LIB_PRETAXPERIOD_RC,"
					+ "ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC,"
					+ "PAID_THROUGH_ITC_IGST,PAID_THROUGH_ITC_CGST,"
					+ "PAID_THROUGH_ITC_SGST,PAID_THROUGH_ITC_CESS,"
					+ "PAID_IN_CASH_TAX_CESS,PAID_IN_CASH_INTEREST,"
					+ "PAID_IN_CASH_LATEFEE,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3,ERROR_CODE_ASP,ERROR_DESCRIPTION_ASP,"
					+ "INFO_ERROR_CODE_ASP,INFO_ERROR_DESCRIPTION_ASP "
					+ "FROM RET_AS_ENTERED_SETOFF_UTILIZATION HI "
					+ "LEFT OUTER JOIN TF_GSTR1_ERROR_INFO() ERR "
					+ "ON HI.ID = ERR.COMMON_ID "
					+ "AND HI.FILE_ID = ERR.FILE_ID "
					+ "LEFT OUTER JOIN FILE_STATUS FIL "
					+ "ON HI.FILE_ID = FIL.ID AND ERR.FILE_ID = FIL.ID "
					+ "AND ERR.INV_KEY = HI.INVKEY_RET_SETOFF_UTILIZATION "
					+ "WHERE HI.IS_DELETE = FALSE AND HI.IS_ERROR = TRUE "
					+ "AND " + buildQuery;

		} else if ((DownloadReportsConstant.REFUNDS)
				.equalsIgnoreCase(fileType)) {

			queryStr = "SELECT GSTIN,RETURN_PERIOD,DESCRIPTION,"
					+ "TAX,INTEREST,PENALTY,FEE,OTHER,TOTAL,USERDEFINED1,"
					+ "USERDEFINED2,USERDEFINED3,ERROR_CODE_ASP,"
					+ "ERROR_DESCRIPTION_ASP,INFO_ERROR_CODE_ASP,"
					+ "INFO_ERROR_DESCRIPTION_ASP FROM "
					+ "RET_AS_ENTERED_REFUND_FROM_E_CASHLEDGER HI "
					+ "LEFT OUTER JOIN TF_GSTR1_ERROR_INFO() ERR "
					+ "ON HI.ID = ERR.COMMON_ID AND HI.FILE_ID = ERR.FILE_ID "
					+ "LEFT OUTER JOIN FILE_STATUS FIL "
					+ "ON HI.FILE_ID = FIL.ID AND ERR.FILE_ID = FIL.ID "
					+ "AND ERR.INV_KEY = HI.INVKEY_RET_REFUND_FROM_E_CASHLEDGER "
					+ "WHERE HI.IS_DELETE = FALSE AND HI.IS_ERROR = TRUE "
					+ "AND " + buildQuery;
		}
		return queryStr;
	}

}
