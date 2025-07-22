package com.ey.advisory.app.report.convertor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.itc04stocktrack.Itc04StockTrackingDownloadDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.ReportConvertor;

@Component("Itc04StockTrackingReportConvertor")
public class Itc04StockTrackingReportConvertor implements ReportConvertor {

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public Object convert(Object[] arr, String reportType) {
		Itc04StockTrackingDownloadDto obj = new Itc04StockTrackingDownloadDto();
		obj.setTableNumber((arr[0] != null) ? (String) arr[0] : null);
		obj.setFiscalYear((arr[1] != null) ? (String) arr[1] : null);
		obj.setReturnPeriod((arr[2] != null) ? (String) arr[2] : null);
		obj.setSupplierGSTIN((arr[3] != null) ? (String) arr[3] : null);
		obj.setDeliveryChallanNumber((arr[4] != null) ? (String) arr[4] : null);

		if (arr[5] != null) {
			String strdate = arr[5].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setDeliveryChallanDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setDeliveryChallanDate(null);
		}
		obj.setJobWorkerGSTIN((arr[6] != null) ? (String) arr[6] : null);
		obj.setJobWorkerStateCode((arr[7] != null) ? (String) arr[7] : null);
		obj.setJobWorkerType((arr[8] != null) ? (String) arr[8] : null);
		obj.setJobWorkerID((arr[9] != null) ? (String) arr[9] : null);
		obj.setJobWorkerName((arr[10] != null) ? (String) arr[10] : null);
		obj.setTypeOfGoods((arr[11] != null) ? (String) arr[11] : null);
		obj.setItemSerialNumber((arr[12] != null) ? (String) arr[12] : null);
		obj.setProductDescription((arr[13] != null) ? (String) arr[13] : null);
		obj.setProductCode((arr[14] != null) ? (String) arr[14] : null);
		obj.setNatureOfJW((arr[15] != null) ? (String) arr[15] : null);
		obj.setHSN((arr[16] != null) ? (String) arr[16] : null);
		obj.setUQC((arr[17] != null) ? (String) arr[17] : null);
		obj.setQuantity((arr[18] != null) ? (BigDecimal) arr[18] : null);
		obj.setLossesUQC((arr[19] != null) ? (String) arr[19] : null);
		obj.setLossesQuantity((arr[20] != null) ? (BigDecimal) arr[20] : null);
		obj.setItemAssessableAmount(
				(arr[21] != null) ? (BigDecimal) arr[21] : null);
		obj.setIgstRate((arr[22] != null) ? (BigDecimal) arr[22] : null);
		obj.setIgstAmount((arr[23] != null) ? (BigDecimal) arr[23] : null);
		obj.setCgstRate((arr[24] != null) ? (BigDecimal) arr[24] : null);
		obj.setCgstAmount((arr[25] != null) ? (BigDecimal) arr[25] : null);
		obj.setSgstRate((arr[26] != null) ? (BigDecimal) arr[26] : null);
		obj.setSgstAmount((arr[27] != null) ? (BigDecimal) arr[27] : null);
		obj.setCessAdvaloremRate(
				(arr[28] != null) ? (BigDecimal) arr[28] : null);
		obj.setCessAdvaloremAmount(
				(arr[29] != null) ? (BigDecimal) arr[29] : null);
		obj.setCessSpecificRate(
				(arr[30] != null) ? (BigDecimal) arr[30] : null);
		obj.setCessSpecificAmount(
				(arr[31] != null) ? (BigDecimal) arr[31] : null);
		obj.setStateCessAdvaloremRate(
				(arr[32] != null) ? (BigDecimal) arr[32] : null);
		obj.setStateCessAdvaloremAmount(
				(arr[33] != null) ? (BigDecimal) arr[33] : null);
		obj.setStateCessSpecificRate(
				(arr[34] != null) ? (BigDecimal) arr[34] : null);
		obj.setStateCessSpecificAmount(
				(arr[35] != null) ? (BigDecimal) arr[35] : null);
		obj.setTotalValue((arr[36] != null) ? (BigDecimal) arr[36] : null);

		if (arr[37] != null) {
			String strdate = arr[37].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPostingDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setPostingDate(null);
		}
		obj.setUserID((arr[38] != null) ? (String) arr[38] : null);
		obj.setCompanyCode((arr[39] != null) ? (String) arr[39] : null);
		obj.setPlantCode((arr[40] != null) ? (String) arr[40] : null);
		obj.setDivision((arr[41] != null) ? (String) arr[41] : null);
		obj.setProfitCentre1((arr[42] != null) ? (String) arr[42] : null);
		obj.setProfitCentre2((arr[43] != null) ? (String) arr[43] : null);
		obj.setAccountingVoucherNumber(
				(arr[44] != null) ? (String) arr[44] : null);
		if (arr[45] != null) {
			String strdate = arr[45].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setAccountingVoucherDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setAccountingVoucherDate(null);
		}
		obj.setUserDefinedField1((arr[46] != null) ? (String) arr[46] : null);
		obj.setUserDefinedField2((arr[47] != null) ? (String) arr[47] : null);
		obj.setUserDefinedField3((arr[48] != null) ? (String) arr[48] : null);
		obj.setStatus((arr[49] != null) ? (String) arr[49] : null);
		obj.setGoodsReceived((arr[50] != null) ? (String) arr[50] : null);
		obj.setJwDeliveryChallanNumber(
				(arr[51] != null) ? (String) arr[51] : null);

		if (arr[52] != null) {
			String strdate = arr[52].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setJwDeliveryChallanDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setJwDeliveryChallanDate(null);
		}

		if (arr[53] != null) {
			String strdate = arr[53].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setGoodsReceivingDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setGoodsReceivingDate(null);
		}

		obj.setInvoiceNumber((arr[54] != null) ? (String) arr[54] : null);

		if (arr[55] != null) {
			String strdate = arr[55].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoiceDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setInvoiceDate(null);
		}
		obj.setAgingInDays((arr[56] != null) ? (String) arr[56] : null);

		return obj;
	}
}
