package com.ey.advisory.app.report.convertor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.ims.ImsFileStatusErrorReportDto;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.ReportTypeConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Component("ImsFileStatusReportConverter")
@Slf4j
public class ImsFileStatusReportConverter implements ReportConvertor {

	@Override
	public Object convert(Object[] arr, String reportType) {

		ImsFileStatusErrorReportDto obj = new ImsFileStatusErrorReportDto();

		if (!ReportTypeConstants.PROCESSED_RECORDS
				.equalsIgnoreCase(reportType)) {
			obj.setDigiGstErrorDescription(
					arr[0] != null ? arr[0].toString() : null);
			obj.setErrorId(arr[1] != null ? arr[1].toString() : null);
			obj.setErrorDescription(arr[2] != null ? arr[2].toString() : null);

			obj.setActionResponse(arr[3] != null ? arr[3].toString() : null);
			obj.setResponseRemarks(arr[4] != null ? arr[4].toString() : null);
			obj.setGstnAction(arr[5] != null ? arr[5].toString() : null);
			obj.setDigiGstAction(arr[6] != null ? arr[6].toString() : null);

			if (arr[7] != null && !arr[7].toString().isEmpty()) {
				
				try{
					String timestamp = removeQuotes(arr[7].toString());
					
					if(timestamp.length() > 19){
						 timestamp = timestamp.substring(0,19);
						}
					
					DateTimeFormatter utcFormatter = DateTimeFormatter
							.ofPattern("dd-MM-yyyy HH:mm:ss");
					LocalDateTime localDateTime = LocalDateTime.parse(timestamp,
							utcFormatter);

					ZonedDateTime utcDateTime = ZonedDateTime.of(localDateTime,
							ZoneId.of("UTC"));

					ZoneId istZone = ZoneId.of("Asia/Kolkata");
					ZonedDateTime istDateTime = utcDateTime
							.withZoneSameInstant(istZone);

					DateTimeFormatter istFormatter = DateTimeFormatter
							.ofPattern("dd-MM-yyyy HH:mm:ss");
					String istTime = istDateTime.format(istFormatter);

					obj.setDigiGstActionDateTime(
							DownloadReportsConstant.CSVCHARACTER.concat(istTime));
				}catch(Exception e) {
					
					LOGGER.debug(
							"Exception while Parsing DigiGST DateTime ");
					
				}

				
			}

			obj.setSavedToGstn(arr[8] != null ? arr[8].toString() : null);
			obj.setAvailableInImsGstn(
					arr[9] != null ? arr[9].toString() : null);
			obj.setTableType(arr[10] != null ? arr[10].toString() : null);
			obj.setRecipientGstin(arr[11] != null ? arr[11].toString() : null);
			obj.setSupplierGstin(arr[12] != null ? arr[12].toString() : null);
			obj.setSupplierLegalName(
					arr[13] != null ? arr[13].toString() : null);
			obj.setSupplierTradeName(
					arr[14] != null ? arr[14].toString() : null);
			obj.setDocumentType(arr[15] != null ? arr[15].toString() : null);
			obj.setDocumentNumber(
					arr[16] != null && !arr[16].toString().isEmpty()
							? "'" + arr[16].toString() : null);
			obj.setDocumentDate(arr[17] != null ? arr[17].toString() : null);
			obj.setTaxableValue(arr[18] != null ? arr[18].toString() : null);
			obj.setIgst(arr[19] != null ? arr[19].toString() : null);
			obj.setCgst(arr[20] != null ? arr[20].toString() : null);
			obj.setSgst(arr[21] != null ? arr[21].toString() : null);
			obj.setCess(arr[22] != null ? arr[22].toString() : null);
			obj.setTotalTax(arr[23] != null ? arr[23].toString() : null);
			obj.setInvoiceValue(arr[24] != null ? arr[24].toString() : null);
			obj.setPos(arr[25] != null ? arr[25].toString() : null);
			obj.setFormType(arr[26] != null ? arr[26].toString() : null);
			obj.setGstr1FilingStatus(
					arr[27] != null ? arr[27].toString() : null);
			obj.setGstr1FilingPeriod(
					arr[28] != null ? arr[28].toString() : null);
			obj.setOriginalDocNo(
					arr[29] != null && !arr[29].toString().isEmpty()
							? "'" + arr[29].toString() : null);
			obj.setOriginalDocDate(arr[30] != null ? arr[30].toString() : null);
			obj.setPendingActionBlocked(
					arr[31] != null ? arr[31].toString() : null);
			obj.setCheckSum(arr[32] != null ? arr[32].toString() : null);
			obj.setGetCallDateTime(arr[33] != null ? arr[33].toString() : null);
			obj.setImsUniqueId(arr[34] != null ? arr[34].toString() : null);

			obj.setItcRedReq((arr[35] != null) ? arr[35].toString() : null);
			obj.setDeclIgst((arr[36] != null) ? arr[36].toString() : null);
			obj.setDeclCgst((arr[37] != null) ? arr[37].toString() : null);
			obj.setDeclSgst((arr[38] != null) ? arr[38].toString() : null);
			obj.setDeclCess((arr[39] != null) ? arr[39].toString() : null);
			
		} else {
			obj.setActionResponse(arr[0] != null ? arr[0].toString() : null);
			obj.setResponseRemarks(arr[1] != null ? arr[1].toString() : null);
			obj.setGstnAction(arr[2] != null ? arr[2].toString() : null);
			obj.setDigiGstAction(arr[3] != null ? arr[3].toString() : null);

			if (arr[4] != null && !arr[4].toString().isEmpty()) {

				String timestamp = arr[4].toString();
				if(timestamp.length() > 19){
					 timestamp = timestamp.substring(0,19);
					}
				
				DateTimeFormatter utcFormatter = DateTimeFormatter
						.ofPattern("dd-MM-yyyy HH:mm:ss");
				LocalDateTime localDateTime = LocalDateTime.parse(timestamp,
						utcFormatter);

				ZonedDateTime utcDateTime = ZonedDateTime.of(localDateTime,
						ZoneId.of("UTC"));

				ZoneId istZone = ZoneId.of("Asia/Kolkata");
				ZonedDateTime istDateTime = utcDateTime
						.withZoneSameInstant(istZone);

				DateTimeFormatter istFormatter = DateTimeFormatter
						.ofPattern("dd-MM-yyyy HH:mm:ss");
				String istTime = istDateTime.format(istFormatter);

				obj.setDigiGstActionDateTime(
						DownloadReportsConstant.CSVCHARACTER.concat(istTime));
			}

			obj.setSavedToGstn(arr[5] != null ? arr[5].toString() : null);
			obj.setAvailableInImsGstn(
					arr[6] != null ? arr[6].toString() : null);
			obj.setTableType(arr[7] != null ? arr[7].toString() : null);
			obj.setRecipientGstin(arr[8] != null ? arr[8].toString() : null);
			obj.setSupplierGstin(arr[9] != null ? arr[9].toString() : null);
			obj.setSupplierLegalName(
					arr[10] != null ? arr[10].toString() : null);
			obj.setSupplierTradeName(
					arr[11] != null ? arr[11].toString() : null);
			obj.setDocumentType(arr[12] != null ? arr[12].toString() : null);
			obj.setDocumentNumber(
					arr[13] != null && !arr[13].toString().isEmpty()
							? "'" + arr[13].toString() : null);
			obj.setDocumentDate(arr[14] != null ? arr[14].toString() : null);
			obj.setTaxableValue(arr[15] != null ? arr[15].toString() : null);
			obj.setIgst(arr[16] != null ? arr[16].toString() : null);
			obj.setCgst(arr[17] != null ? arr[17].toString() : null);
			obj.setSgst(arr[18] != null ? arr[18].toString() : null);
			obj.setCess(arr[19] != null ? arr[19].toString() : null);
			obj.setTotalTax(arr[20] != null ? arr[20].toString() : null);
			obj.setInvoiceValue(arr[21] != null ? arr[21].toString() : null);
			obj.setPos(arr[22] != null ? arr[22].toString() : null);
			obj.setFormType(arr[23] != null ? arr[23].toString() : null);
			obj.setGstr1FilingStatus(
					arr[24] != null ? arr[24].toString() : null);
			obj.setGstr1FilingPeriod(
					arr[25] != null ? arr[25].toString() : null);
			obj.setOriginalDocNo(
					arr[26] != null && !arr[26].toString().isEmpty()
							? "'" + arr[26].toString() : null);
			obj.setOriginalDocDate(arr[27] != null ? arr[27].toString() : null);
			obj.setPendingActionBlocked(
					arr[28] != null ? arr[28].toString() : null);
			obj.setCheckSum(arr[29] != null ? arr[29].toString() : null);
			obj.setGetCallDateTime(arr[30] != null ? arr[30].toString() : null);
			obj.setImsUniqueId(arr[31] != null ? arr[31].toString() : null);
			
			obj.setItcRedReq((arr[32] != null) ? arr[32].toString() : null);
			obj.setDeclIgst((arr[33] != null) ? arr[33].toString() : null);
			obj.setDeclCgst((arr[34] != null) ? arr[34].toString() : null);
			obj.setDeclSgst((arr[35] != null) ? arr[35].toString() : null);
			obj.setDeclCess((arr[36] != null) ? arr[36].toString() : null);
		}

		return obj;
	}

	public static void main(String[] args) {

		String timestamp = "30-09-2024 11:23:54";
		DateTimeFormatter utcFormatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm:ss");
		LocalDateTime localDateTime = LocalDateTime.parse(timestamp,
				utcFormatter);

		// Assume the timestamp is in a specific time zone, e.g., UTC
		ZonedDateTime utcDateTime = ZonedDateTime.of(localDateTime,
				ZoneId.of("UTC"));

		// Convert to IST (Indian Standard Time)
		ZoneId istZone = ZoneId.of("Asia/Kolkata");
		ZonedDateTime istDateTime = utcDateTime.withZoneSameInstant(istZone);

		// Format the IST time as a string
		DateTimeFormatter istFormatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm:ss");
		String istTime = istDateTime.format(istFormatter);

		System.out
				.println(DownloadReportsConstant.CSVCHARACTER.concat(istTime));

	}
	private String removeQuotes(String data) {
		if (Strings.isNullOrEmpty(data)) {
			return null;
		}
		if (data.contains("'")) {
			return data.replace("'", "");
		}
		if (data.contains("`")) {
			return data.replace("`", "");
		}
 
		return data;
 
	}
}
