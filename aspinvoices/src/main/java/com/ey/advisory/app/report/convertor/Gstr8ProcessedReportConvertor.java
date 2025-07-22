package com.ey.advisory.app.report.convertor;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr8.Gstr8DownloadDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportConvertor;

@Component("Gstr8ProcessedReportConvertor")
public class Gstr8ProcessedReportConvertor implements ReportConvertor {

	@Override
	public Object convert(Object[] arr, String reportType) {
		Gstr8DownloadDto obj = new Gstr8DownloadDto();

		obj.setFileId((arr[0] != null) ? GenUtil.getBigInteger(arr[0]) : null);
		obj.setEcomGstin((arr[1] != null) ? (String) arr[1] : null);
		obj.setReturnPeriod((arr[2] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat((String) arr[2])
				: null);
		obj.setIdentifier((arr[3] != null) ? (String) arr[3] : null);
		obj.setOriginalReturnPeriod((arr[4] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat((String) arr[4])
				: null);
		obj.setOriginalNetSupplies(
				(arr[5] != null) ? (BigDecimal) arr[5] : null);
		obj.setDocType((arr[6] != null) ? (String) arr[6] : null);
		obj.setSupplyType((arr[7] != null) ? (String) arr[7] : null);
		obj.setSupplierGstinOrEnrolmentId(
				(arr[8] != null) ? (String) arr[8] : null);
		obj.setOriginalSupplierGstinOrEnrolmentId(
				(arr[9] != null) ? (String) arr[9] : null);
		obj.setSuppliesToRegistered(
				(arr[10] != null) ? (BigDecimal) arr[10] : null);
		obj.setReturnsFromRegistered(
				(arr[11] != null) ? (BigDecimal) arr[11] : null);
		obj.setSuppliesToUnregistered(
				(arr[12] != null) ? (BigDecimal) arr[12] : null);
		obj.setReturnsFromUnregistered(
				(arr[13] != null) ? (BigDecimal) arr[13] : null);
		obj.setNetSupplies((arr[14] != null) ? (BigDecimal) arr[14] : null);
		obj.setIntegratedTaxAmount(
				(arr[15] != null) ? (BigDecimal) arr[15] : null);
		obj.setCentralTaxAmount(
				(arr[16] != null) ? (BigDecimal) arr[16] : null);
		obj.setStateUTTaxAmount(
				(arr[17] != null) ? (BigDecimal) arr[17] : null);
		obj.setUserDefinedField1((arr[18] != null) ? (String) arr[18] : null);
		obj.setUserDefinedField2((arr[19] != null) ? (String) arr[19] : null);
		obj.setUserDefinedField3((arr[20] != null) ? (String) arr[20] : null);
		obj.setUserDefinedField4((arr[21] != null) ? (String) arr[21] : null);
		obj.setUserDefinedField5((arr[22] != null) ? (String) arr[22] : null);
		obj.setUserDefinedField6((arr[23] != null) ? (String) arr[23] : null);
		obj.setUserDefinedField7((arr[24] != null) ? (String) arr[24] : null);
		obj.setUserDefinedField8((arr[25] != null) ? (String) arr[25] : null);
		obj.setUserDefinedField9((arr[26] != null) ? (String) arr[26] : null);
		obj.setUserDefinedField10((arr[27] != null) ? (String) arr[27] : null);
		obj.setUserDefinedField11((arr[28] != null) ? (String) arr[28] : null);
		obj.setUserDefinedField12((arr[29] != null) ? (String) arr[29] : null);
		obj.setUserDefinedField13((arr[30] != null) ? (String) arr[30] : null);
		obj.setUserDefinedField14((arr[31] != null) ? (String) arr[31] : null);
		obj.setUserDefinedField15((arr[32] != null) ? (String) arr[32] : null);
		obj.setRecordType((arr[33] != null) ? (String) arr[33] : null);
		obj.setPos((arr[34] != null) ? (String) arr[34] : null);
		obj.setOriginalPos((arr[35] != null) ? (String) arr[35] : null);
		
		return obj;
	}

}