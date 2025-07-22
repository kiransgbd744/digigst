package com.ey.advisory.app.report.convertor;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.gstr1.einv.IsdReconErrorReportDto;
import com.ey.advisory.common.ReportConvertor;

import lombok.extern.slf4j.Slf4j;

@Component("IsdReconReportConverter")
@Slf4j
public class IsdReconReportConverter implements ReportConvertor {

	@Override
	public Object convert(Object[] arr, String reportType) {

		IsdReconErrorReportDto obj = new IsdReconErrorReportDto();
		obj.setErrorCode(arr[0] != null ? arr[0].toString() : null);
		obj.setErrorMessage(arr[1] != null ? arr[1].toString() : null);
		String sourceType = arr[2] != null ? arr[2].toString() : null;
		if (sourceType.equalsIgnoreCase("E")) {
			obj.setSourceType("File Upload");
		} else if (sourceType.equalsIgnoreCase("S")) {
			obj.setSourceType("SFTP");
		} else {
			obj.setSourceType("API");
		}
		obj.setIsdgstin(arr[3] != null ? arr[3].toString() : null);
		obj.setSupplierGstin(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setDocumentNumber(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentDate(arr[7] != null && !arr[7].toString().isEmpty()
				? "'" + arr[7].toString() : null);
		obj.setItemSerialNumber(arr[8] != null && !arr[8].toString().isEmpty()
				? "'" + arr[8].toString() : null);
		obj.setGstinforDistribution(
				arr[9] != null && !arr[9].toString().isEmpty()
						? "'" + arr[9].toString() : null);

		obj.setCreatedOn(arr[10] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[10].toString()) : null);
		obj.setActionType(arr[11] != null ? arr[11].toString() : null);
		return obj;
	}
}
