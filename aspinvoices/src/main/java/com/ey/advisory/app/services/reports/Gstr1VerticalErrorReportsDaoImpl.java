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

import com.ey.advisory.app.data.views.client.Gstr1VerticalAdvAdjDto;
import com.ey.advisory.app.data.views.client.Gstr1VerticalAdvRecDto;
import com.ey.advisory.app.data.views.client.Gstr1VerticalB2csDto;
import com.ey.advisory.app.data.views.client.Gstr1VerticalInvoiceDto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Gstr1VerticalErrorReportsDaoImpl")
public class Gstr1VerticalErrorReportsDaoImpl
		implements Gstr1VerticalReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1VerticalErrorReportsDaoImpl.class);

	static String fileType = null;

	@Override
	public List<Object> getGstr1VerticalReports(
			Gstr1VerticalDownloadReportsReqDto request) {

		Long fileId = request.getFileId();
		fileType = request.getFileType();
		String dataType = request.getDataType();
		StringBuilder buildQuery = new StringBuilder();
		if (fileId != null) {
			buildQuery.append(" HI.FILE_ID= :fileId ");
		}
		// --gstr1A code
		String queryStr = null;
		if (dataType != null && dataType.equalsIgnoreCase("GSTR1A"))
			queryStr = createGstr1AVerticalErrorQueryString(
					buildQuery.toString());
		else
			queryStr = createVerticalErrorQueryString(buildQuery.toString());
		
		Query q = entityManager.createNativeQuery(queryStr);

		LOGGER.debug("Reading query " + queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();

		LOGGER.debug("Reading Resultset" + list);
		List<Object> retList = list.parallelStream()
				.map(o -> convertVerticalError(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Object convertVerticalError(Object[] arr) {

		Object obj = null;

		Gstr1VerticalB2csDto obj1 = new Gstr1VerticalB2csDto();
		Gstr1VerticalAdvRecDto obj2 = new Gstr1VerticalAdvRecDto();
		Gstr1VerticalAdvAdjDto obj3 = new Gstr1VerticalAdvAdjDto();
		Gstr1VerticalInvoiceDto obj4 = new Gstr1VerticalInvoiceDto();

		if ((DownloadReportsConstant.B2CS).equalsIgnoreCase(fileType)) {

			obj1.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
			obj1.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
			obj1.setTransactionType(arr[2] != null ? arr[2].toString() : null);
			obj1.setMonth(arr[3] != null ? arr[3].toString() : null);
			obj1.setOrgPos(arr[4] != null ? arr[4].toString() : null);
			obj1.setOrghsnOrSac(arr[5] != null ? arr[5].toString() : null);
			obj1.setOrgUnitOfMeasurement(
					arr[6] != null ? arr[6].toString() : null);
			obj1.setOrgQuantity(arr[7] != null ? arr[7].toString() : null);
			obj1.setOrgRate(arr[8] != null ? arr[8].toString() : null);
			obj1.setOrgTaxableValue(arr[9] != null ? arr[9].toString() : null);
			obj1.setOrgEComGSTIN(arr[10] != null ? arr[10].toString() : null);
			obj1.setOrgEComSupplyValue(
					arr[11] != null ? arr[11].toString() : null);
			obj1.setNewPOS(arr[12] != null ? arr[12].toString() : null);
			obj1.setNewHSNorSAC(arr[13] != null ? arr[13].toString() : null);
			obj1.setNewUnitOfMeasurement(
					arr[14] != null ? arr[14].toString() : null);
			obj1.setNewQuantity(arr[15] != null ? arr[15].toString() : null);
			obj1.setNewRate(arr[16] != null ? arr[16].toString() : null);
			obj1.setNewTaxableValue(
					arr[17] != null ? arr[17].toString() : null);
			obj1.setNewEComGSTIN(arr[18] != null ? arr[18].toString() : null);
			obj1.setNewEComSupplyValue(
					arr[19] != null ? arr[19].toString() : null);
			obj1.setIntegratedTaxAmount(
					arr[20] != null ? arr[20].toString() : null);
			obj1.setCentralTaxAmount(
					arr[21] != null ? arr[21].toString() : null);
			obj1.setStateUTTaxAmount(
					arr[22] != null ? arr[22].toString() : null);
			obj1.setCessAmount(
					arr[23] != null ? arr[23].toString() : null);
			obj1.setTotalValue(arr[24] != null ? arr[24].toString() : null);
			obj1.setProfitCentre(arr[25] != null ? arr[25].toString() : null);
			obj1.setPlant(arr[26] != null ? arr[26].toString() : null);
			obj1.setDivision(arr[27] != null ? arr[27].toString() : null);
			obj1.setLocation(arr[28] != null ? arr[28].toString() : null);
			obj1.setSalesOrganisation(
					arr[29] != null ? arr[29].toString() : null);
			obj1.setDistributionChannel(
					arr[30] != null ? arr[30].toString() : null);
			obj1.setUserAccess1(arr[31] != null ? arr[31].toString() : null);
			obj1.setUserAccess2(arr[32] != null ? arr[32].toString() : null);
			obj1.setUserAccess3(arr[33] != null ? arr[33].toString() : null);
			obj1.setUserAccess4(arr[34] != null ? arr[34].toString() : null);
			obj1.setUserAccess5(arr[35] != null ? arr[35].toString() : null);
			obj1.setUserAccess6(arr[36] != null ? arr[36].toString() : null);
			obj1.setUserdefinedfield1(
					arr[37] != null ? arr[37].toString() : null);
			obj1.setUserdefinedfield2(
					arr[38] != null ? arr[38].toString() : null);
			obj1.setUserdefinedfield3(
					arr[39] != null ? arr[39].toString() : null);
			obj1.setAspErrorCode(arr[40] != null ? arr[40].toString() : null);
			obj1.setAspErrorDescription(
					arr[41] != null ? arr[41].toString() : null);
			obj1.setAspInformationID(
					arr[42] != null ? arr[42].toString() : null);
			obj1.setAspInformationDescription(
					arr[43] != null ? arr[43].toString() : null);

			obj = obj1;
		}
		if ((DownloadReportsConstant.ADVANCERECEIVED)
				.equalsIgnoreCase(fileType)) {

			obj2.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
			obj2.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
			obj2.setTransactionType(arr[2] != null ? arr[2].toString() : null);
			obj2.setMonth(arr[3] != null ? arr[3].toString() : null);
			obj2.setOrgPos(arr[4] != null ? arr[4].toString() : null);
			obj2.setOrgRate(arr[5] != null ? arr[5].toString() : null);
			obj2.setOrgGrossAdvanceReceived(
					arr[6] != null ? arr[6].toString() : null);
			obj2.setNewPOS(arr[7] != null ? arr[7].toString() : null);
			obj2.setNewRate(arr[8] != null ? arr[8].toString() : null);
			obj2.setNewGrossAdvanceReceived(
					arr[9] != null ? arr[9].toString() : null);
			obj2.setIntegratedTaxAmount(
					arr[10] != null ? arr[10].toString() : null);
			obj2.setCentralTaxAmount(
					arr[11] != null ? arr[11].toString() : null);
			obj2.setStateUTTaxAmount(
					arr[12] != null ? arr[12].toString() : null);
			obj2.setCessAmount(arr[13] != null ? arr[13].toString() : null);
			obj2.setProfitCentre(arr[14] != null ? arr[14].toString() : null);
			obj2.setPlant(arr[15] != null ? arr[15].toString() : null);
			obj2.setDivision(arr[16] != null ? arr[16].toString() : null);
			obj2.setLocation(arr[17] != null ? arr[17].toString() : null);
			obj2.setSalesOrganisation(
					arr[18] != null ? arr[18].toString() : null);
			obj2.setDistributionChannel(
					arr[19] != null ? arr[19].toString() : null);
			obj2.setUserAccess1(arr[20] != null ? arr[20].toString() : null);
			obj2.setUserAccess2(arr[21] != null ? arr[21].toString() : null);
			obj2.setUserAccess3(arr[22] != null ? arr[22].toString() : null);
			obj2.setUserAccess4(arr[23] != null ? arr[23].toString() : null);
			obj2.setUserAccess5(arr[24] != null ? arr[24].toString() : null);
			obj2.setUserAccess6(arr[25] != null ? arr[25].toString() : null);
			obj2.setUserdefinedfield1(
					arr[26] != null ? arr[26].toString() : null);
			obj2.setUserdefinedfield2(
					arr[27] != null ? arr[27].toString() : null);
			obj2.setUserdefinedfield3(
					arr[28] != null ? arr[28].toString() : null);
			obj2.setAspErrorCode(arr[29] != null ? arr[29].toString() : null);
			obj2.setAspErrorDescription(
					arr[30] != null ? arr[30].toString() : null);
			obj2.setAspInformationID(
					arr[31] != null ? arr[31].toString() : null);
			obj2.setAspInformationDescription(
					arr[32] != null ? arr[32].toString() : null);

			obj = obj2;
		}

		if ((DownloadReportsConstant.ADVANCEADJUSTMENT)
				.equalsIgnoreCase(fileType)) {

			obj3.setSupplierGSTIN(arr[1] != null ? arr[1].toString() : null);
			obj3.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
			obj3.setTransactionType(arr[3] != null ? arr[3].toString() : null);
			obj3.setMonth(arr[4] != null ? arr[4].toString() : null);
			obj3.setOrgPos(arr[5] != null ? arr[5].toString() : null);
			obj3.setOrgRate(arr[6] != null ? arr[6].toString() : null);
			obj3.setOrgGrossAdvanceAdjusted(
					arr[7] != null ? arr[7].toString() : null);
			obj3.setNewPOS(arr[8] != null ? arr[8].toString() : null);
			obj3.setNewRate(arr[9] != null ? arr[9].toString() : null);
			obj3.setNewGrossAdvanceAdjusted(
					arr[10] != null ? arr[10].toString() : null);
			obj3.setIntegratedTaxAmount(
					arr[11] != null ? arr[11].toString() : null);
			obj3.setCentralTaxAmount(
					arr[12] != null ? arr[12].toString() : null);
			obj3.setStateUTTaxAmount(
					arr[13] != null ? arr[13].toString() : null);
			obj3.setCessAmount(arr[14] != null ? arr[14].toString() : null);
			obj3.setProfitCentre(arr[15] != null ? arr[15].toString() : null);
			obj3.setPlant(arr[16] != null ? arr[16].toString() : null);
			obj3.setDivision(arr[17] != null ? arr[17].toString() : null);
			obj3.setLocation(arr[18] != null ? arr[18].toString() : null);
			obj3.setSalesOrganisation(
					arr[19] != null ? arr[19].toString() : null);
			obj3.setDistributionChannel(
					arr[20] != null ? arr[20].toString() : null);
			obj3.setUserAccess1(arr[21] != null ? arr[21].toString() : null);
			obj3.setUserAccess2(arr[22] != null ? arr[22].toString() : null);
			obj3.setUserAccess3(arr[23] != null ? arr[23].toString() : null);
			obj3.setUserAccess4(arr[24] != null ? arr[24].toString() : null);
			obj3.setUserAccess5(arr[25] != null ? arr[25].toString() : null);
			obj3.setUserAccess6(arr[26] != null ? arr[26].toString() : null);
			obj3.setUserdefinedfield1(
					arr[27] != null ? arr[27].toString() : null);
			obj3.setUserdefinedfield2(
					arr[28] != null ? arr[28].toString() : null);
			obj3.setUserdefinedfield3(
					arr[29] != null ? arr[29].toString() : null);
			obj3.setAspErrorCode(arr[30] != null ? arr[30].toString() : null);
			obj3.setAspErrorDescription(
					arr[31] != null ? arr[31].toString() : null);
			obj3.setAspInformationID(
					arr[32] != null ? arr[32].toString() : null);
			obj3.setAspInformationDescription(
					arr[33] != null ? arr[33].toString() : null);

			obj = obj3;
		}
		if ((DownloadReportsConstant.INVOICE).equalsIgnoreCase(fileType)) {

			obj4.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
			obj4.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
			obj4.setSerialNo(arr[2] != null ? arr[2].toString() : null);
			obj4.setNatureOfDocument(arr[3] != null ? arr[3].toString() : null);
			obj4.setFrom(arr[7] != null ? arr[7].toString() : null);
			obj4.setTo(arr[8] != null ? arr[8].toString() : null);
			obj4.setTotalNumber(arr[4] != null ? arr[4].toString() : null);
			obj4.setCancelled(arr[5] != null ? arr[5].toString() : null);
			obj4.setNetNumber(arr[6] != null ? arr[6].toString() : null);
			obj4.setAspErrorCode(arr[9] != null ? arr[9].toString() : null);
			obj4.setAspErrorDescription(
					arr[10] != null ? arr[10].toString() : null);

			obj = obj4;
		}
		return obj;
	}

	private String createVerticalErrorQueryString(String buildQuery) {
		String queryStr = "";

		if ((DownloadReportsConstant.B2CS).equalsIgnoreCase(fileType)) {
			queryStr = "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,MONTH,"
					+ "ORG_POS,ORG_HSNORSAC,ORG_UOM,ORG_QNT,ORG_RATE,"
					+ "ORG_TAXABLE_VALUE,ORG_ECOM_GSTIN,ORG_ECOM_SUP_VAL,"
					+ "NEW_POS,NEW_HSNORSAC,NEW_UOM,NEW_QNT,NEW_RATE,"
					+ "NEW_TAXABLE_VALUE,NEW_ECOM_GSTIN,NEW_ECOM_SUP_VAL,"
					+ "IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,TOT_VAL,"
					+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
					+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
					+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
					+ "USERACCESS6,USERDEFINED_FIELD1,USERDEFINED_FIELD2,"
					+ "USERDEFINED_FIELD3,"
                    + "TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') ) "
                    + "AS ERROR_CODE_ASP,"
                    + "TRIM(', ' FROM IFNULL(ERROR_DESCRIPTION_ASP,'') ) "
                    + "AS ERROR_DESCRIPTION_ASP,"
                    + "TRIM(', ' FROM IFNULL(INFO_ERROR_CODE_ASP,'') ) "
                    + "AS INFO_ERROR_CODE_ASP,"
                    + "TRIM(', ' FROM IFNULL(INFO_ERROR_DESCRIPTION_ASP,'') ) "
                    + "AS INFO_ERROR_DESCRIPTION_ASP " 
					+ "FROM "
					+ "GSTR1_AS_ENTERED_B2CS HI LEFT OUTER JOIN "
					+ "TF_GSTR1_ERROR_INFO() ERR ON HI.ID = ERR.COMMON_ID "
					+ "AND HI.FILE_ID=ERR.FILE_ID LEFT OUTER JOIN FILE_STATUS FIL "
					+ "ON HI.FILE_ID=FIL.ID AND ERR.FILE_ID=FIL.ID AND "
					+ "ERR.INV_KEY=HI.B2CS_INVKEY WHERE HI.IS_ERROR = TRUE "
					+ "AND " + buildQuery;

		} else if ((DownloadReportsConstant.ADVANCERECEIVED)
				.equalsIgnoreCase(fileType)) {

			queryStr = "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,MONTH,"
					+ "ORG_POS,ORG_RATE,ORG_GROSS_ADV_RECEIVED,NEW_POS,"
					+ "NEW_RATE,NEW_GROSS_ADV_RECEIVED,IGST_AMT,CGST_AMT,"
					+ "SGST_AMT,CESS_AMT,PROFIT_CENTRE,PLANT_CODE,DIVISION,"
					+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED_FIELD1,"
					+ "USERDEFINED_FIELD2,USERDEFINED_FIELD3,"
					+ "TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') ) "
	                + "AS ERROR_CODE_ASP,"
	                + "TRIM(', ' FROM IFNULL(ERROR_DESCRIPTION_ASP,'') ) "
	                + "AS ERROR_DESCRIPTION_ASP,"
	                + "TRIM(', ' FROM IFNULL(INFO_ERROR_CODE_ASP,'') ) "
	                + "AS INFO_ERROR_CODE_ASP,"
	                + "TRIM(', ' FROM IFNULL(INFO_ERROR_DESCRIPTION_ASP,'') ) "
	                + "AS INFO_ERROR_DESCRIPTION_ASP " 
					+ "FROM "
					+ "GSTR1_AS_ENTERED_ADV_RECEVIED HI LEFT OUTER JOIN "
					+ "TF_GSTR1_ERROR_INFO() ERR ON HI.ID = ERR.COMMON_ID "
					+ "AND HI.FILE_ID=ERR.FILE_ID LEFT OUTER JOIN FILE_STATUS FIL "
					+ "ON HI.FILE_ID=FIL.ID AND ERR.FILE_ID=FIL.ID AND "
					+ "ERR.INV_KEY=HI.AT_INVKEY WHERE HI.IS_ERROR = TRUE  "
					+ "AND  " + buildQuery;

		} else if ((DownloadReportsConstant.ADVANCEADJUSTMENT)
				.equalsIgnoreCase(fileType)) {

			queryStr = "SELECT HI.ID,SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,"
					+ "MONTH,ORG_POS,ORG_RATE,ORG_GROSS_ADV_ADJUSTED,NEW_POS,"
					+ "NEW_RATE,NEW_GROSS_ADV_ADJUSTED,IGST_AMT,CGST_AMT,"
					+ "SGST_AMT,CESS_AMT,PROFIT_CENTRE,PLANT_CODE,DIVISION,"
					+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED_FIELD1,"
					+ "USERDEFINED_FIELD2,USERDEFINED_FIELD3,"
					+ "TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') ) "
	                + "AS ERROR_CODE_ASP,"
	                + "TRIM(', ' FROM IFNULL(ERROR_DESCRIPTION_ASP,'') ) "
	                + "AS ERROR_DESCRIPTION_ASP,"
	                + "TRIM(', ' FROM IFNULL(INFO_ERROR_CODE_ASP,'') ) "
	                + "AS INFO_ERROR_CODE_ASP,"
	                + "TRIM(', ' FROM IFNULL(INFO_ERROR_DESCRIPTION_ASP,'') ) "
	                + "AS INFO_ERROR_DESCRIPTION_ASP " 
					+ "FROM "
					+ "GSTR1_AS_ENTERED_ADV_ADJUSTMENT HI LEFT OUTER JOIN "
					+ "TF_GSTR1_ERROR_INFO() ERR ON HI.ID = ERR.COMMON_ID AND "
					+ "HI.FILE_ID = ERR.FILE_ID LEFT OUTER JOIN FILE_STATUS FIL "
					+ "ON HI.FILE_ID=FIL.ID AND ERR.FILE_ID=FIL.ID AND "
					+ "ERR.INV_KEY=HI.TXPD_INVKEY WHERE HI.IS_ERROR = TRUE "
					+ "AND " + buildQuery;

		} else if ((DownloadReportsConstant.INVOICE)
				.equalsIgnoreCase(fileType)) {

			queryStr = "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,SERIAL_NUM,"
					+ "NATURE_OF_DOC,TOT_NUM,CANCELED,NET_NUM,"
					+ "DOC_SERIES_FROM,DOC_SERIES_TO,ERROR_CODE_ASP,"
					+ "ERROR_DESCRIPTION_ASP FROM GSTR1_AS_ENTERED_INV_SERIES HI "
					+ "LEFT OUTER JOIN TF_GSTR1_ERROR_INFO() ERR ON HI.ID = "
					+ "ERR.COMMON_ID AND HI.FILE_ID = ERR.FILE_ID LEFT "
					+ "OUTER JOIN FILE_STATUS FIL ON HI.FILE_ID=FIL.ID AND "
					+ "ERR.FILE_ID=FIL.ID WHERE HI.IS_ERROR = TRUE AND "
					+ buildQuery;

		}
		return queryStr;
	}
	
	//gstr1a code
	private String createGstr1AVerticalErrorQueryString(String buildQuery) {
		String queryStr = "";

		if ((DownloadReportsConstant.B2CS).equalsIgnoreCase(fileType)) {
			queryStr = "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,MONTH,"
					+ "ORG_POS,ORG_HSNORSAC,ORG_UOM,ORG_QNT,ORG_RATE,"
					+ "ORG_TAXABLE_VALUE,ORG_ECOM_GSTIN,ORG_ECOM_SUP_VAL,"
					+ "NEW_POS,NEW_HSNORSAC,NEW_UOM,NEW_QNT,NEW_RATE,"
					+ "NEW_TAXABLE_VALUE,NEW_ECOM_GSTIN,NEW_ECOM_SUP_VAL,"
					+ "IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,TOT_VAL,"
					+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
					+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
					+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
					+ "USERACCESS6,USERDEFINED_FIELD1,USERDEFINED_FIELD2,"
					+ "USERDEFINED_FIELD3,"
                    + "TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') ) "
                    + "AS ERROR_CODE_ASP,"
                    + "TRIM(', ' FROM IFNULL(ERROR_DESCRIPTION_ASP,'') ) "
                    + "AS ERROR_DESCRIPTION_ASP,"
                    + "TRIM(', ' FROM IFNULL(INFO_ERROR_CODE_ASP,'') ) "
                    + "AS INFO_ERROR_CODE_ASP,"
                    + "TRIM(', ' FROM IFNULL(INFO_ERROR_DESCRIPTION_ASP,'') ) "
                    + "AS INFO_ERROR_DESCRIPTION_ASP " 
					+ "FROM "
					+ "GSTR1A_AS_ENTERED_B2CS HI LEFT OUTER JOIN "
					+ "TF_GSTR1A_ERROR_INFO() ERR ON HI.ID = ERR.COMMON_ID "
					+ "AND HI.FILE_ID=ERR.FILE_ID LEFT OUTER JOIN FILE_STATUS FIL "
					+ "ON HI.FILE_ID=FIL.ID AND ERR.FILE_ID=FIL.ID AND "
					+ "ERR.INV_KEY=HI.B2CS_INVKEY WHERE HI.IS_ERROR = TRUE "
					+ "AND " + buildQuery;

		} else if ((DownloadReportsConstant.ADVANCERECEIVED)
				.equalsIgnoreCase(fileType)) {

			queryStr = "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,MONTH,"
					+ "ORG_POS,ORG_RATE,ORG_GROSS_ADV_RECEIVED,NEW_POS,"
					+ "NEW_RATE,NEW_GROSS_ADV_RECEIVED,IGST_AMT,CGST_AMT,"
					+ "SGST_AMT,CESS_AMT,PROFIT_CENTRE,PLANT_CODE,DIVISION,"
					+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED_FIELD1,"
					+ "USERDEFINED_FIELD2,USERDEFINED_FIELD3,"
					+ "TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') ) "
	                + "AS ERROR_CODE_ASP,"
	                + "TRIM(', ' FROM IFNULL(ERROR_DESCRIPTION_ASP,'') ) "
	                + "AS ERROR_DESCRIPTION_ASP,"
	                + "TRIM(', ' FROM IFNULL(INFO_ERROR_CODE_ASP,'') ) "
	                + "AS INFO_ERROR_CODE_ASP,"
	                + "TRIM(', ' FROM IFNULL(INFO_ERROR_DESCRIPTION_ASP,'') ) "
	                + "AS INFO_ERROR_DESCRIPTION_ASP " 
					+ "FROM "
					+ "GSTR1A_AS_ENTERED_ADV_RECEVIED HI LEFT OUTER JOIN "
					+ "TF_GSTR1A_ERROR_INFO() ERR ON HI.ID = ERR.COMMON_ID "
					+ "AND HI.FILE_ID=ERR.FILE_ID LEFT OUTER JOIN FILE_STATUS FIL "
					+ "ON HI.FILE_ID=FIL.ID AND ERR.FILE_ID=FIL.ID AND "
					+ "ERR.INV_KEY=HI.AT_INVKEY WHERE HI.IS_ERROR = TRUE  "
					+ "AND  " + buildQuery;

		} else if ((DownloadReportsConstant.ADVANCEADJUSTMENT)
				.equalsIgnoreCase(fileType)) {

			queryStr = "SELECT HI.ID,SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,"
					+ "MONTH,ORG_POS,ORG_RATE,ORG_GROSS_ADV_ADJUSTED,NEW_POS,"
					+ "NEW_RATE,NEW_GROSS_ADV_ADJUSTED,IGST_AMT,CGST_AMT,"
					+ "SGST_AMT,CESS_AMT,PROFIT_CENTRE,PLANT_CODE,DIVISION,"
					+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED_FIELD1,"
					+ "USERDEFINED_FIELD2,USERDEFINED_FIELD3,"
					+ "TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') ) "
	                + "AS ERROR_CODE_ASP,"
	                + "TRIM(', ' FROM IFNULL(ERROR_DESCRIPTION_ASP,'') ) "
	                + "AS ERROR_DESCRIPTION_ASP,"
	                + "TRIM(', ' FROM IFNULL(INFO_ERROR_CODE_ASP,'') ) "
	                + "AS INFO_ERROR_CODE_ASP,"
	                + "TRIM(', ' FROM IFNULL(INFO_ERROR_DESCRIPTION_ASP,'') ) "
	                + "AS INFO_ERROR_DESCRIPTION_ASP " 
					+ "FROM "
					+ "GSTR1A_AS_ENTERED_ADV_ADJUSTMENT HI LEFT OUTER JOIN "
					+ "TF_GSTR1A_ERROR_INFO() ERR ON HI.ID = ERR.COMMON_ID AND "
					+ "HI.FILE_ID = ERR.FILE_ID LEFT OUTER JOIN FILE_STATUS FIL "
					+ "ON HI.FILE_ID=FIL.ID AND ERR.FILE_ID=FIL.ID AND "
					+ "ERR.INV_KEY=HI.TXPD_INVKEY WHERE HI.IS_ERROR = TRUE "
					+ "AND " + buildQuery;

		} else if ((DownloadReportsConstant.INVOICE)
				.equalsIgnoreCase(fileType)) {

			queryStr = "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,SERIAL_NUM,"
					+ "NATURE_OF_DOC,TOT_NUM,CANCELED,NET_NUM,"
					+ "DOC_SERIES_FROM,DOC_SERIES_TO,ERROR_CODE_ASP,"
					+ "ERROR_DESCRIPTION_ASP FROM GSTR1A_AS_ENTERED_INV_SERIES HI "
					+ "LEFT OUTER JOIN TF_GSTR1A_ERROR_INFO() ERR ON HI.ID = "
					+ "ERR.COMMON_ID AND HI.FILE_ID = ERR.FILE_ID LEFT "
					+ "OUTER JOIN FILE_STATUS FIL ON HI.FILE_ID=FIL.ID AND "
					+ "ERR.FILE_ID=FIL.ID WHERE HI.IS_ERROR = TRUE AND "
					+ buildQuery;

		}
		return queryStr;
	}

}
