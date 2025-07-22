/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx13H3IVerticalProcessedInfoDto;
import com.ey.advisory.app.data.views.client.Anx1B2CVerticalProcessedInfoDto;
import com.ey.advisory.app.data.views.client.Anx1Table4VerticalProcessedInfoDto;
import com.ey.advisory.app.docs.dto.Anx1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;

/**
 * @author Laxmi.Salukuti
 *
 */

@Component("Anx1VerticalProcessedInfoReportsDaoImpl")
public class Anx1VerticalProcessedInfoReportsDaoImpl
		implements Anx1VerticalReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1VerticalProcessedInfoReportsDaoImpl.class);

	static String fileType = null;

	@Override
	public List<Object> getVerticalReports(
			Anx1VerticalDownloadReportsReqDto request) {

		Long fileId = request.getFileId();
		fileType = request.getFileType();

		StringBuilder buildb2cQuery = new StringBuilder();
		if (fileId != null) {
			buildb2cQuery.append(" HI.FILE_ID= :fileId ");
		}
		StringBuilder build3h3iQuery = new StringBuilder();
		if (fileId != null) {
			build3h3iQuery.append(" HI.FILE_ID= :fileId ");
		}
		StringBuilder buildtable4Query = new StringBuilder();
		if (fileId != null) {
			buildtable4Query.append(" HI.FILE_ID= :fileId ");
		}

		String queryStr = createVerticalProcessedQueryString(
				buildb2cQuery.toString(), 
				build3h3iQuery.toString(),
				buildtable4Query.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		LOGGER.debug("Reading query" + queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();

		LOGGER.debug("Reading Resultset" + list);

		List<Object> retList = list.parallelStream()
				.map(o -> convertVerticalProcessed(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Object convertVerticalProcessed(Object[] arr) {

		Object obj = null;

		Anx1B2CVerticalProcessedInfoDto obj1 = new Anx1B2CVerticalProcessedInfoDto();
		Anx13H3IVerticalProcessedInfoDto obj2 = new Anx13H3IVerticalProcessedInfoDto();
		Anx1Table4VerticalProcessedInfoDto obj3 = new Anx1Table4VerticalProcessedInfoDto();
		if ((DownloadReportsConstant.B2C).equalsIgnoreCase(fileType)) {

			obj1.setReturnType(arr[0] != null ? arr[0].toString() : null);
			obj1.setSgstin(arr[1] != null ? arr[1].toString() : null);
			obj1.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
			obj1.setDiffPercentage(arr[3] != null ? arr[3].toString() : null);
			obj1.setSec7ofIGSTFlag(arr[4] != null ? arr[4].toString() : null);
			obj1.setAutoPopltToRefund(
					arr[5] != null ? arr[5].toString() : null);
			obj1.setPos(arr[6] != null ? arr[6].toString() : null);
			obj1.setHsnOrSac(arr[7] != null ? arr[7].toString() : null);
			obj1.setUom(arr[8] != null ? arr[8].toString() : null);
			obj1.setQuantity(arr[9] != null ? arr[9].toString() : null);
			obj1.setRate(arr[10] != null ? arr[10].toString() : null);
			obj1.setStateAplyCess(arr[11] != null ? arr[11].toString() : null);
			obj1.setStateCessRate(arr[12] != null ? arr[12].toString() : null);
			obj1.setTaxableValue(arr[13] != null ? arr[13].toString() : null);
			obj1.setTcsFlag(arr[14] != null ? arr[14].toString() : null);
			obj1.seteComGSTIN(arr[15] != null ? arr[15].toString() : null);
			obj1.seteComValOfSuppliesMade(
					arr[16] != null ? arr[16].toString() : null);
			obj1.seteComValOfSuppliesReturned(
					arr[17] != null ? arr[17].toString() : null);
			obj1.seteComNetValOfSupplies(
					arr[18] != null ? arr[18].toString() : null);
			obj1.setIntegratedTaxAmount(
					arr[19] != null ? arr[19].toString() : null);
			obj1.setCentralTaxAmount(
					arr[20] != null ? arr[20].toString() : null);
			obj1.setStateUTTaxAmount(
					arr[21] != null ? arr[21].toString() : null);
			obj1.setCessAmount(arr[22] != null ? arr[22].toString() : null);
			obj1.settCSAmount(arr[23] != null ? arr[23].toString() : null);
			obj1.setStateCessAmount(
					arr[24] != null ? arr[24].toString() : null);
			obj1.setTotalValue(arr[25] != null ? arr[25].toString() : null);
			obj1.setProfitCentre(arr[26] != null ? arr[26].toString() : null);
			obj1.setPlant(arr[27] != null ? arr[27].toString() : null);
			obj1.setDivision(arr[28] != null ? arr[28].toString() : null);
			obj1.setLocation(arr[29] != null ? arr[29].toString() : null);
			obj1.setSalesOrganisation(
					arr[30] != null ? arr[30].toString() : null);
			obj1.setDistributionChannel(
					arr[31] != null ? arr[31].toString() : null);
			obj1.setUserAccess1(arr[32] != null ? arr[32].toString() : null);
			obj1.setUserAccess2(arr[33] != null ? arr[33].toString() : null);
			obj1.setUserAccess3(arr[34] != null ? arr[34].toString() : null);
			obj1.setUserAccess4(arr[35] != null ? arr[35].toString() : null);
			obj1.setUserAccess5(arr[36] != null ? arr[36].toString() : null);
			obj1.setUserAccess6(arr[37] != null ? arr[37].toString() : null);
			obj1.setUserdefinedfield1(
					arr[38] != null ? arr[38].toString() : null);
			obj1.setUserdefinedfield2(
					arr[39] != null ? arr[39].toString() : null);
			obj1.setUserdefinedfield3(
					arr[40] != null ? arr[40].toString() : null);
			obj1.setAspInformationCode(arr[41] != null ? arr[41].toString() : null);
			obj1.setAspInformationDesc(arr[42] != null ? arr[42].toString() : null);

			obj = obj1;
		}
		if ((DownloadReportsConstant.TABLE3H3I).equalsIgnoreCase(fileType)) {

			obj2.setReturnType(arr[0] != null ? arr[0].toString() : null);
			obj2.setRecipientGSTIN(arr[1] != null ? arr[1].toString() : null);
			obj2.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
			obj2.setTransactionFlag(arr[3] != null ? arr[3].toString() : null);
			obj2.setSupplierGSTINorPAN(
					arr[4] != null ? arr[4].toString() : null);
			obj2.setSupplierName(arr[5] != null ? arr[5].toString() : null);
			obj2.setDiffPercentageFlag(
					arr[6] != null ? arr[6].toString() : null);
			obj2.setSec7ofIGSTFlag(arr[7] != null ? arr[7].toString() : null);
			obj2.setAutoPopltToRefund(
					arr[8] != null ? arr[8].toString() : null);
			obj2.setPos(arr[9] != null ? arr[9].toString() : null);
			obj2.setHsnOrSac(arr[10] != null ? arr[10].toString() : null);
			obj2.setTaxableValue(arr[11] != null ? arr[11].toString() : null);
			obj2.setRate(arr[12] != null ? arr[12].toString() : null);
			obj2.setIntegratedTaxAmount(
					arr[13] != null ? arr[13].toString() : null);
			obj2.setCentralTaxAmount(
					arr[14] != null ? arr[14].toString() : null);
			obj2.setStateUTTaxAmount(
					arr[15] != null ? arr[15].toString() : null);
			obj2.setCessAmount(arr[16] != null ? arr[16].toString() : null);
			obj2.setTotalValue(arr[17] != null ? arr[17].toString() : null);
			obj2.setEligibilityIndicator(
					arr[18] != null ? arr[18].toString() : null);
			obj2.setAvailableIGST(arr[19] != null ? arr[19].toString() : null);
			obj2.setAvailableCGST(arr[20] != null ? arr[20].toString() : null);
			obj2.setAvailableSGST(arr[21] != null ? arr[21].toString() : null);
			obj2.setAvailableCess(arr[22] != null ? arr[22].toString() : null);
			obj2.setProfitCentre(arr[23] != null ? arr[23].toString() : null);
			obj2.setPlant(arr[24] != null ? arr[24].toString() : null);
			obj2.setDivision(arr[25] != null ? arr[25].toString() : null);
			obj2.setLocation(arr[26] != null ? arr[26].toString() : null);
			obj2.setPurchaseOrganisation(
					arr[27] != null ? arr[27].toString() : null);
			obj2.setUserAccess1(arr[28] != null ? arr[28].toString() : null);
			obj2.setUserAccess2(arr[29] != null ? arr[29].toString() : null);
			obj2.setUserAccess3(arr[30] != null ? arr[30].toString() : null);
			obj2.setUserAccess4(arr[31] != null ? arr[31].toString() : null);
			obj2.setUserAccess5(arr[32] != null ? arr[32].toString() : null);
			obj2.setUserAccess6(arr[33] != null ? arr[33].toString() : null);
			obj2.setUserdefinedfield1(
					arr[34] != null ? arr[34].toString() : null);
			obj2.setUserdefinedfield2(
					arr[35] != null ? arr[35].toString() : null);
			obj2.setUserdefinedfield3(
					arr[36] != null ? arr[36].toString() : null);
			obj2.setAspInformationCode(arr[37] != null ? arr[37].toString() : null);
			obj2.setAspInformationDesc(arr[38] != null ? arr[38].toString() : null);

			obj = obj2;

		}

		if ((DownloadReportsConstant.TABLE4).equalsIgnoreCase(fileType)) {

			obj3.setReturnType(arr[0] != null ? arr[0].toString() : null);
			obj3.setSupplierGSTIN(arr[1] != null ? arr[1].toString() : null);
			obj3.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
			obj3.seteComGSTIN(arr[3] != null ? arr[3].toString() : null);
			obj3.setValueOfSuppliesMade(
					arr[4] != null ? arr[4].toString() : null);
			obj3.setValueOfSuppliesReturned(
					arr[5] != null ? arr[5].toString() : null);
			obj3.setNetValueOfSupplies(
					arr[6] != null ? arr[6].toString() : null);
			obj3.setIntegratedTaxAmount(
					arr[7] != null ? arr[7].toString() : null);
			obj3.setCentralTaxAmount(arr[8] != null ? arr[8].toString() : null);
			obj3.setStateUTTaxAmount(arr[9] != null ? arr[9].toString() : null);
			obj3.setCessAmount(arr[10] != null ? arr[10].toString() : null);
			obj3.setProfitCentre(arr[11] != null ? arr[11].toString() : null);
			obj3.setPlant(arr[12] != null ? arr[12].toString() : null);
			obj3.setDivision(arr[13] != null ? arr[13].toString() : null);
			obj3.setLocation(arr[14] != null ? arr[14].toString() : null);
			obj3.setSalesOrganisation(
					arr[15] != null ? arr[15].toString() : null);
			obj3.setDistributionChannel(
					arr[16] != null ? arr[16].toString() : null);
			obj3.setUserAccess1(arr[17] != null ? arr[17].toString() : null);
			obj3.setUserAccess2(arr[18] != null ? arr[18].toString() : null);
			obj3.setUserAccess3(arr[19] != null ? arr[19].toString() : null);
			obj3.setUserAccess4(arr[20] != null ? arr[20].toString() : null);
			obj3.setUserAccess5(arr[21] != null ? arr[21].toString() : null);
			obj3.setUserAccess6(arr[22] != null ? arr[22].toString() : null);
			obj3.setUserdefinedfield1(
					arr[23] != null ? arr[23].toString() : null);
			obj3.setUserdefinedfield2(
					arr[24] != null ? arr[24].toString() : null);
			obj3.setUserdefinedfield3(
					arr[25] != null ? arr[25].toString() : null);
			obj3.setAspInformationCode(arr[26] != null ? arr[26].toString() : null);
			obj3.setAspInformationDesc(arr[27] != null ? arr[27].toString() : null);

			obj = obj3;

		}

		return obj;
	}

	private String createVerticalProcessedQueryString(String buildb2cQuery,
			String build3h3iQuery, String buildtable4Query) {
		String queryStr = "";
		if ((DownloadReportsConstant.B2C).equalsIgnoreCase(fileType)) {
			queryStr = "SELECT RETURN_TYPE,SUPPLIER_GSTIN,RETURN_PERIOD,"
					+ "DIFF_PERCENT,SEC7_OF_IGST_FLAG,AUTOPOPULATE_TO_REFUND,"
					+ "POS,HSNORSAC,UOM,QUANTITY,TAX_RATE,STATE_APPLYING_CESS,"
					+ "STATE_CESS_RATE,TAXABLE_VALUE,TCS_FLAG,ECOM_GSTIN,"
					+ "ECOM_VAL_SUPMADE,ECOM_VAL_SUPRET,ECOM_NETVAL_SUP,"
					+ "IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,TCS_AMT,"
					+ "STATE_CESS_AMT,TOTAL_VALUE,PROFIT_CENTER,PLANT,"
					+ "DIVISION,LOCATION,SALES_ORG,DISTRIBUTION_CHANNEL,"
					+ "USER_ACCESS1,USER_ACCESS2,USER_ACCESS3,USER_ACCESS4,"
					+ "USER_ACCESS5,USER_ACCESS6,USER_DEFINED1,USER_DEFINED2,"
					+ "USER_DEFINED3,"
					+ "TRIM(', ' FROM IFNULL(STRING_AGG(A_INFO_CODE,',' ),'') ) " 
	                + "AS ASP_INFO_CODE,"
	                + "TRIM(', ' FROM IFNULL(STRING_AGG(A_INFO_DESCRIPTION,',' ),'') ) "
	                + "AS ASP_INFO_DESCRIPTION "
					+ "FROM (SELECT HI.ID,RETURN_TYPE,SUPPLIER_GSTIN,"
					+ "RETURN_PERIOD,DIFF_PERCENT,SEC7_OF_IGST_FLAG,"
					+ "AUTOPOPULATE_TO_REFUND,POS,HSNORSAC,UOM,QUANTITY,"
					+ "TAX_RATE,STATE_APPLYING_CESS,"
					+ "STATE_CESS_RATE,TAXABLE_VALUE,TCS_FLAG,ECOM_GSTIN,"
					+ "ECOM_VAL_SUPMADE,ECOM_VAL_SUPRET,ECOM_NETVAL_SUP,IGST_AMT,"
					+ "CGST_AMT,SGST_AMT,CESS_AMT,TCS_AMT,STATE_CESS_AMT,"
					+ "TOTAL_VALUE,PROFIT_CENTER,PLANT,DIVISION,LOCATION,"
					+ "SALES_ORG,DISTRIBUTION_CHANNEL,USER_ACCESS1,USER_ACCESS2,"
					+ "USER_ACCESS3,USER_ACCESS4,USER_ACCESS5,USER_ACCESS6,"
					+ "USER_DEFINED1,USER_DEFINED2,USER_DEFINED3,"
					+ "(CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END) "
					+ "AS A_INFO_CODE,(CASE WHEN ERROR_TYPE='INFO' "
					+ "THEN ERROR_DESCRIPTION END)AS A_INFO_DESCRIPTION "
					+ "FROM ANX_PROCESSED_B2C HI LEFT OUTER JOIN "
					+ "ANX_VERTICAL_ERROR ERR ON  HI.AS_ENTERED_ID= ERR.COMMON_ID "
					+ "and HI.FILE_ID=ERR.FILE_ID AND ERR.TABLE_TYPE='B2C' "
					+ "LEFT OUTER JOIN FILE_STATUS FIL ON HI.FILE_ID=FIL.ID "
					+ "AND ERR.FILE_ID=FIL.ID AND ERR.INV_KEY=HI.B2C_INVKEY "
					+ "WHERE HI.IS_INFORMATION=TRUE AND "
					+ buildb2cQuery + ")"
					+ " GROUP BY "
					+ "ID,RETURN_TYPE,SUPPLIER_GSTIN,RETURN_PERIOD,"
					+ "DIFF_PERCENT,SEC7_OF_IGST_FLAG,AUTOPOPULATE_TO_REFUND,"
					+ "POS,HSNORSAC,UOM,QUANTITY,TAX_RATE,STATE_APPLYING_CESS,"
					+ "STATE_CESS_RATE,TAXABLE_VALUE,TCS_FLAG,ECOM_GSTIN,"
					+ "ECOM_VAL_SUPMADE,ECOM_VAL_SUPRET,ECOM_NETVAL_SUP,IGST_AMT,"
					+ "CGST_AMT,SGST_AMT,CESS_AMT,TCS_AMT,STATE_CESS_AMT,"
					+ "TOTAL_VALUE,PROFIT_CENTER,PLANT,DIVISION,LOCATION,"
					+ "SALES_ORG,DISTRIBUTION_CHANNEL,USER_ACCESS1,USER_ACCESS2,"
					+ "USER_ACCESS3,USER_ACCESS4,USER_ACCESS5,USER_ACCESS6,"
					+ "USER_DEFINED1,USER_DEFINED2,USER_DEFINED3";

		} else if ((DownloadReportsConstant.TABLE3H3I)
				.equalsIgnoreCase(fileType)) {

			queryStr = "SELECT RETURN_TYPE,CUST_GSTIN,RETURN_PERIOD,TRAN_FLAG,"
					+ "SUPPLIER_GSTIN_PAN,SUPPLIER_NAME,DIFF_PERCENT,"
					+ "SEC7_OF_IGST_FLAG,AUTOPOPULATE_TO_REFUND,POS,HSNORSAC,"
					+ "TAXABLE_VALUE,TAX_RATE,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,"
					+ "TOTAL_VALUE,ELGBL_INDICATOR,AVAIL_IGST,AVAIL_CGST,"
					+ "AVAIL_SGST,AVAIL_CESS,PROFIT_CENTER,PLANT,"
					+ "DIVISION,LOCATION,PURCHAGE_ORG,USER_ACCESS1,"
					+ "USER_ACCESS2,USER_ACCESS3,USER_ACCESS4,USER_ACCESS5,"
					+ "USER_ACCESS6,USERDEFINED1,USERDEFINED2,USERDEFINED3,"
					+ "TRIM(', ' FROM IFNULL(STRING_AGG(A_INFO_CODE,',' ),'') ) " 
	                + "AS ASP_INFO_CODE,"
	                + "TRIM(', ' FROM IFNULL(STRING_AGG(A_INFO_DESCRIPTION,',' ),'') ) "
	                + "AS ASP_INFO_DESCRIPTION "
					+ "FROM (SELECT HI.ID,RETURN_TYPE,CUST_GSTIN,"
					+ "RETURN_PERIOD,TRAN_FLAG,SUPPLIER_GSTIN_PAN,"
					+ "SUPPLIER_NAME,DIFF_PERCENT,"
					+ "SEC7_OF_IGST_FLAG,AUTOPOPULATE_TO_REFUND,POS,HSNORSAC,"
					+ "TAXABLE_VALUE,TAX_RATE,IGST_AMT,CGST_AMT,SGST_AMT,"
					+ "CESS_AMT,TOTAL_VALUE,ELGBL_INDICATOR,AVAIL_IGST,"
					+ "AVAIL_CGST,AVAIL_SGST,"
					+ "AVAIL_CESS,PROFIT_CENTER,PLANT,DIVISION,LOCATION,"
					+ "PURCHAGE_ORG,USER_ACCESS1,USER_ACCESS2,USER_ACCESS3,"
					+ "USER_ACCESS4,USER_ACCESS5,USER_ACCESS6,USERDEFINED1,"
					+ "USERDEFINED2,USERDEFINED3,(CASE WHEN ERROR_TYPE='INFO' "
					+ "THEN ERROR_CODE END) AS A_INFO_CODE,"
					+ "(CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END) "
					+ "AS A_INFO_DESCRIPTION "
					+ "FROM ANX_PROCESSED_3H_3I HI LEFT OUTER JOIN "
					+ "ANX_VERTICAL_ERROR ERR ON  HI.AS_ENTERED_ID= ERR.COMMON_ID "
					+ "and HI.FILE_ID=ERR.FILE_ID AND ERR.TABLE_TYPE= 'TABLE3H3I' "
					+ "LEFT OUTER JOIN FILE_STATUS "
					+ "FIL ON HI.FILE_ID=FIL.ID AND ERR.FILE_ID=FIL.ID "
					+ "AND ERR.INV_KEY=HI.INVKEY_3H_3I WHERE HI.IS_INFORMATION=TRUE AND "
					+ build3h3iQuery + ")"
					+ " GROUP BY "
					+ "ID,RETURN_TYPE,CUST_GSTIN,RETURN_PERIOD,TRAN_FLAG,"
					+ "SUPPLIER_GSTIN_PAN,SUPPLIER_NAME,DIFF_PERCENT,"
					+ "SEC7_OF_IGST_FLAG,AUTOPOPULATE_TO_REFUND,POS,HSNORSAC,"
					+ "TAXABLE_VALUE,TAX_RATE,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,"
					+ "TOTAL_VALUE,ELGBL_INDICATOR,AVAIL_IGST,AVAIL_CGST,"
					+ "AVAIL_SGST,AVAIL_CESS,PROFIT_CENTER,PLANT,DIVISION,LOCATION,"
					+ "PURCHAGE_ORG,USER_ACCESS1,USER_ACCESS2,USER_ACCESS3,"
					+ "USER_ACCESS4,USER_ACCESS5,USER_ACCESS6,USERDEFINED1,"
					+ "USERDEFINED2,USERDEFINED3";

		} else if ((DownloadReportsConstant.TABLE4)
				.equalsIgnoreCase(fileType)) {

			queryStr = "SELECT RETURN_TYPE,SUPPLIER_GSTIN,RETURN_PERIOD,"
					+ "ECOM_GSTIN,ECOM_VAL_SUPMADE,ECOM_VAL_SUPRET,"
					+ "ECOM_NETVAL_SUP,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,"
					+ "PROFIT_CENTER,PLANT,DIVISION,LOCATION,SALES_ORG,"
					+ "DISTRIBUTION_CHANNEL,USER_ACCESS1,USER_ACCESS2,"
					+ "USER_ACCESS3,USER_ACCESS4,USER_ACCESS5,USER_ACCESS6,"
					+ "USER_DEFINED1,USER_DEFINED2,USER_DEFINED3,"
					+ "TRIM(', ' FROM IFNULL(STRING_AGG(A_INFO_CODE,',' ),'') ) " 
	                + "AS ASP_INFO_CODE,"
	                + "TRIM(', ' FROM IFNULL(STRING_AGG(A_INFO_DESCRIPTION,',' ),'') ) "
	                + "AS ASP_INFO_DESCRIPTION "
					+ "FROM (SELECT HI.ID,RETURN_TYPE,SUPPLIER_GSTIN,"
					+ "RETURN_PERIOD,ECOM_GSTIN,ECOM_VAL_SUPMADE,"
					+ "ECOM_VAL_SUPRET,ECOM_NETVAL_SUP,IGST_AMT,CGST_AMT,"
					+ "SGST_AMT,CESS_AMT,PROFIT_CENTER,PLANT,"
					+ "DIVISION,LOCATION,SALES_ORG,DISTRIBUTION_CHANNEL,"
					+ "USER_ACCESS1,USER_ACCESS2,USER_ACCESS3,USER_ACCESS4,"
					+ "USER_ACCESS5,USER_ACCESS6,USER_DEFINED1,USER_DEFINED2,"
					+ "USER_DEFINED3,"
					+ "(CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END) "
					+ "AS A_INFO_CODE,"
					+ "(CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END) "
					+ "AS A_INFO_DESCRIPTION FROM ANX_PROCESSED_TABLE4 "
					+ "HI LEFT OUTER JOIN ANX_VERTICAL_ERROR ERR ON "
					+ "HI.AS_ENTERED_ID= ERR.COMMON_ID and HI.FILE_ID=ERR.FILE_ID "
					+ "AND ERR.TABLE_TYPE= 'TABLE4' "
					+ "LEFT OUTER JOIN FILE_STATUS FIL ON HI.FILE_ID=FIL.ID "
					+ "AND ERR.FILE_ID=FIL.ID AND ERR.INV_KEY=HI.TABLE4_INVKEY "
					+ "WHERE HI.IS_INFORMATION=TRUE AND "
					+ buildtable4Query + ")"
					+ " GROUP BY "
					+ "ID,RETURN_TYPE,SUPPLIER_GSTIN,RETURN_PERIOD,"
					+ "ECOM_GSTIN,ECOM_VAL_SUPMADE,ECOM_VAL_SUPRET,"
					+ "ECOM_NETVAL_SUP,IGST_AMT,CGST_AMT,SGST_AMT,"
					+ "CESS_AMT,PROFIT_CENTER,PLANT,"
					+ "DIVISION,LOCATION,SALES_ORG,DISTRIBUTION_CHANNEL,"
					+ "USER_ACCESS1,USER_ACCESS2,USER_ACCESS3,USER_ACCESS4,"
					+ "USER_ACCESS5,USER_ACCESS6,USER_DEFINED1,"
					+ "USER_DEFINED2,USER_DEFINED3;";
		}
		return queryStr;
	}

}
