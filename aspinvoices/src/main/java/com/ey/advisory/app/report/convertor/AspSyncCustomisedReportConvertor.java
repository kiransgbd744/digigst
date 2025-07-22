package com.ey.advisory.app.report.convertor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.ErrorMasterUtil;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("AspSyncCustomisedReportConvertor")
public class AspSyncCustomisedReportConvertor {

	static final String OLDFARMATTER = "yyyy-MM-dd";
	static final String NEWFARMATTER = "dd-MM-yyyy";

	static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");

	@SuppressWarnings("unchecked")
	public Object convert(Map<String, Object> contextMap, DataStatusEinvoiceDto dto) {

		List<String> selectedFields = new ArrayList<>();
		if (!contextMap.isEmpty() && contextMap.containsKey("selectedList")) {
			selectedFields = (List<String>) contextMap.get("selectedList");
		}

		LOGGER.debug("selecteFields {} ", selectedFields);

		DataStatusEinvoiceDto obj = new DataStatusEinvoiceDto();
		String errDesc = null;
		String infoDesc = null;

		String errCode = selectedFields.indexOf("DigiGSTErrorDescription") != -1 && dto.getAspErrorDesc() != null
				? dto.getAspErrorDesc().toString() : null;

		if (!Strings.isNullOrEmpty(errCode)) {
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errCodeList.replaceAll(String::trim);
			errDesc = ErrorMasterUtil.getErrorInfo(errCodeList, "OUTWARD");
		}

		obj.setAspErrorDesc(errDesc);

		String infoCode = selectedFields.indexOf("DigiGSTInformationDescription") != -1
				&& dto.getAspInformationId() != null ? dto.getAspInformationId().toString() : "";

		if (!Strings.isNullOrEmpty(infoCode)) {
			String[] infoCodes = infoCode.split(",");
			List<String> infoCodeList = Arrays.asList(infoCodes);
			infoCodeList.replaceAll(String::trim);
			infoDesc = ErrorMasterUtil.getErrorInfo(infoCodeList, "OUTWARD");
		}

		obj.setAspInformationDesc(infoDesc);

		obj.setIrnStatus(selectedFields.indexOf("EINVStatus") != -1 && dto.getIrnStatus() != null
				? dto.getIrnStatus().toString() : "");

		obj.setIrnNo(
				selectedFields.indexOf("IRN(IRP)") != -1 && dto.getIrnNo() != null ? dto.getIrnNo().toString() : "");

		obj.setIrnAcknowledgmentNo(
				selectedFields.indexOf("EINVAcknowledgmentNo") != -1 && dto.getIrnAcknowledgmentNo() != null
						? dto.getIrnAcknowledgmentNo().toString() : "");

		String dbdate = selectedFields.indexOf("EINVAcknowledgmentDate") != -1 && dto.getIrnAcknowledgmentDate() != null
				? dto.getIrnAcknowledgmentDate().toString() : "";
		if (dbdate != null) {

			String[] dateTime = dbdate.split(" ");

			String date = dateTime[0];

			obj.setIrnAcknowledgmentDate(date);
		}

		String dbTime = selectedFields.indexOf("EINVAcknowledgmentTime") != -1 && dto.getIrnAcknowledgmentTime() != null
				? dto.getIrnAcknowledgmentTime().toString() : "";

		if (dbTime != null) {

			String[] dateTime = dbdate.split(" ");

			int ln = dateTime.length;
			String time = ln > 1 ? dateTime[1] : dateTime[0];

			obj.setIrnAcknowledgmentTime(DownloadReportsConstant.CSVCHARACTER.concat(time));
		}

		obj.setSignedQRCode(selectedFields.indexOf("SignedQRCode") != -1 && dto.getSignedQRCode() != null
				? dto.getSignedQRCode().toString() : "");

		obj.setIrpErrorCode(selectedFields.indexOf("EINVErrorCode") != -1 && dto.getIrpErrorCode() != null
				? dto.getIrpErrorCode().toString() : "");

		obj.setIrpErrorDescription(
				selectedFields.indexOf("EINVErrorDescription") != -1 && dto.getIrpErrorDescription() != null
						? dto.getIrpErrorDescription().toString() : "");

		obj.setEwbStatus(selectedFields.indexOf("EWBStatus") != -1 && dto.getEwbStatus() != null
				? dto.getEwbStatus().toString() : null);

		obj.setEwbValidupto(selectedFields.indexOf("EwbValidupto") != -1 && dto.getEwbValidupto() != null
				? dto.getEwbValidupto().toString() : null);

		String irnStatus = obj.getIrnStatus();

		if ("'GENERATED".equalsIgnoreCase(irnStatus)) {
			String dbdate1 = selectedFields.indexOf("EINVAcknowledgmentDate") != -1
					&& dto.getIrnAcknowledgmentDate() != null ? dto.getIrnAcknowledgmentDate().toString() : null;
			String[] dateTime = dbdate1.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];
			String[] datesplit = date.split("-");
			String[] timesplit = time.split(":");
			LocalDateTime irnDate = LocalDateTime.of(Integer.parseInt(datesplit[2]), Integer.parseInt(datesplit[1]),
					Integer.parseInt(datesplit[0]), Integer.parseInt(timesplit[0]), Integer.parseInt(timesplit[1]),
					Integer.parseInt(timesplit[2]));

			if (CommonUtility.deriveEinvStatus(irnDate)) {
				obj.setIrnStatus(
						DownloadReportsConstant.CSVCHARACTER.concat("Generated – Available for Cancellation "));
			}

		}

		// mapping doubt
		obj.setEwbNo(
				selectedFields.indexOf("EwbNo") != -1 && dto.getEwbNo() != null ? dto.getEwbNo().toString() : null);

		String ewbDate = selectedFields.indexOf("EwbDate") != -1 && dto.getEwbNo() != null ? dto.getEwbNo().toString()
				: null;
		if (ewbDate != null) {
			String[] dateTime = ewbDate.split(" ");

			String date = dateTime[0];

			obj.setEwbRespDate(date);
		}

		String ewbTime = selectedFields.indexOf("EWBTime") != -1 && dto.getEwbTime() != null
				? dto.getEwbTime().toString() : null;
		if (ewbTime != null) {
			String[] dateTime = ewbTime.split(" ");
			int ln = dateTime.length;
			String time = ln > 1 ? dateTime[1] : dateTime[0];

			obj.setEwbTime(DownloadReportsConstant.CSVCHARACTER.concat(time));
		}

		obj.setEwbErrorCode(selectedFields.indexOf("EWBErrorCode") != -1 && dto.getEwbErrorCode() != null
				? dto.getEwbErrorCode().toString() : null);

		obj.setEwbErrorDescription(
				selectedFields.indexOf("EWBErrorDescription") != -1 && dto.getEwbErrorDescription() != null
						? dto.getEwbErrorDescription().toString() : null);

		obj.setGstnStatus(selectedFields.indexOf("GSTNStatus") != -1 && dto.getGstnStatus() != null
				? dto.getGstnStatus().toString() : null);

		obj.setGstnRefid(selectedFields.indexOf("GSTNRefid") != -1 && dto.getGstnRefid() != null
				? dto.getGstnRefid().toString() : null);

		String gstinRefIdDate = selectedFields.indexOf("GSTNRefidDate") != -1 && dto.getGstnRefidDate() != null
				? dto.getGstnRefidDate().toString() : null;
		if (gstinRefIdDate != null) {

			String[] dateTime = gstinRefIdDate.split(" ");

			String date = dateTime[0];
			obj.setGstnRefidDate(date);

		}

		String gstinRefIdTime = selectedFields.indexOf("GSTNRefidTime") != -1 && dto.getGstnRefidTime() != null
				? dto.getGstnRefidTime().toString() : null;
		if (gstinRefIdTime != null) {
			
            LOGGER.debug("selectedFields {} ", gstinRefIdTime);
			
		    String cleanedTime = removeApostrophe(gstinRefIdTime);
		    String[] dateTime = cleanedTime.split(" ");
			int ln = dateTime.length;
			String time = ln > 1 ? dateTime[1] : dateTime[0];
		    
		    
			/*DateTimeFormatter utcFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
			LocalTime utcTime = LocalTime.parse(cleanedTime, utcFormatter);

			// Convert LocalTime to ZonedDateTime with UTC time zone
			ZonedDateTime utcDateTime = ZonedDateTime.of(1970, 1, 1, utcTime.getHour(), utcTime.getMinute(),
					utcTime.getSecond(), utcTime.getNano(), ZoneId.of("UTC"));

			// Convert UTC time to IST time
			ZonedDateTime istDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

			// Format the IST time
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
			String istTime = istDateTime.toLocalTime().format(formatter);
			String[] dateTime = istTime.split(" ");
			int ln = dateTime.length;
			String time = ln > 1 ? dateTime[1] : dateTime[0];
*/

			obj.setGstnRefidTime(DownloadReportsConstant.CSVCHARACTER.concat(time));
		}

		obj.setGstnErrorCode(selectedFields.indexOf("GSTNErrorCode") != -1 && dto.getGstnErrorCode() != null
				? dto.getGstnErrorCode().toString() : null);

		obj.setGstnErrorDescription(
				selectedFields.indexOf("GSTNErrorDescription") != -1 && dto.getGstnErrorDescription() != null
						? dto.getGstnErrorDescription().toString() : null);

		obj.setReturnType(selectedFields.indexOf("ReturnType") != -1 && dto.getReturnType() != null
				? dto.getReturnType().toString() : null);

		obj.setTableNumber(selectedFields.indexOf("TableNumber") != -1 && dto.getTableNumber() != null
				? dto.getTableNumber().toString() : null);

		obj.setIrn(selectedFields.indexOf("IRN") != -1 && dto.getIrn() != null ? dto.getIrn().toString() : null);

		obj.setIrnDate(selectedFields.indexOf("IRNDate") != -1 && dto.getIrnDate() != null ? dto.getIrnDate().toString()
				: null);

		obj.setTaxScheme(selectedFields.indexOf("TaxScheme") != -1 && dto.getTaxScheme() != null
				? dto.getTaxScheme().toString() : null);

		obj.setCancellationReason(
				selectedFields.indexOf("CancellationReason") != -1 && dto.getCancellationReason() != null
						? dto.getCancellationReason().toString() : null);

		obj.setCancellationRemarks(
				selectedFields.indexOf("CancellationRemarks") != -1 && dto.getCancellationRemarks() != null
						? dto.getCancellationRemarks().toString() : null);

		obj.setSupplyType(selectedFields.indexOf("SupplyType") != -1 && dto.getSupplyType() != null
				? dto.getSupplyType().toString() : null);

		obj.setDocCategory(selectedFields.indexOf("DocCategory") != -1 && dto.getDocCategory() != null
				? dto.getDocCategory().toString() : null);

		obj.setDocumentType(selectedFields.indexOf("DocumentType") != -1 && dto.getDocumentType() != null
				? dto.getDocumentType().toString() : null);
		obj.setDocumentNumber(selectedFields.indexOf("DocumentNumber") != -1 && dto.getDocumentNumber() != null
				? dto.getDocumentNumber().toString() : null);

		String docDate = selectedFields.indexOf("DocumentDate") != -1 && dto.getDocumentDate() != null
				? dto.getDocumentDate().toString() : null;
	/*	if (docDate != null) {
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(docDate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setDocumentDate(newDate);
		} else {*/
			obj.setDocumentDate(docDate);
		//}

		obj.setReverseChargeFlag(selectedFields.indexOf("ReverseChargeFlag") != -1 && dto.getReverseChargeFlag() != null
				? dto.getReverseChargeFlag().toString() : null);

		obj.setSupplierGSTIN(selectedFields.indexOf("SupplierGSTIN") != -1 && dto.getSupplierGSTIN() != null
				? dto.getSupplierGSTIN().toString() : null);

		obj.setSupplierTradeName(selectedFields.indexOf("SupplierTradeName") != -1 && dto.getSupplierTradeName() != null
				? dto.getSupplierTradeName().toString() : null);

		obj.setSupplierLegalName(selectedFields.indexOf("SupplierLegalName") != -1 && dto.getSupplierLegalName() != null
				? dto.getSupplierLegalName().toString() : null);

		obj.setSupplierAddress1(selectedFields.indexOf("SupplierAddress1") != -1 && dto.getSupplierAddress1() != null
				? dto.getSupplierAddress1().toString() : null);

		obj.setSupplierAddress2(selectedFields.indexOf("SupplierAddress2") != -1 && dto.getSupplierAddress2() != null
				? dto.getSupplierAddress2().toString() : null);

		obj.setSupplierLocation(selectedFields.indexOf("SupplierLocation") != -1 && dto.getSupplierLocation() != null
				? dto.getSupplierLocation().toString() : null);

		obj.setSupplierPincode(selectedFields.indexOf("SupplierPincode") != -1 && dto.getSupplierPincode() != null
				? dto.getSupplierPincode().toString() : null);

		obj.setSupplierStateCode(selectedFields.indexOf("SupplierStateCode") != -1 && dto.getSupplierStateCode() != null
				? dto.getSupplierStateCode().toString() : null);

		obj.setSupplierPhone(selectedFields.indexOf("SupplierPhone") != -1 && dto.getSupplierPhone() != null
				? dto.getSupplierPhone().toString() : null);

		obj.setSupplierEmail(selectedFields.indexOf("SupplierEmail") != -1 && dto.getSupplierEmail() != null
				? dto.getSupplierEmail().toString() : null);

		obj.setCustomerGSTIN(selectedFields.indexOf("CustomerGSTIN") != -1 && dto.getCustomerGSTIN() != null
				? dto.getCustomerGSTIN().toString() : null);

		obj.setCustomerTradeName(selectedFields.indexOf("CustomerTradeName") != -1 && dto.getCustomerTradeName() != null
				? dto.getCustomerTradeName().toString() : null);

		obj.setCustomerLegalName(selectedFields.indexOf("CustomerLegalName") != -1 && dto.getCustomerLegalName() != null
				? dto.getCustomerLegalName().toString() : null);

		obj.setCustomerAddress1(selectedFields.indexOf("CustomerAddress1") != -1 && dto.getCustomerAddress1() != null
				? dto.getCustomerAddress1().toString() : null);

		obj.setCustomerAddress2(selectedFields.indexOf("CustomerAddress2") != -1 && dto.getCustomerAddress2() != null
				? dto.getCustomerAddress2().toString() : null);

		obj.setCustomerLocation(selectedFields.indexOf("CustomerLocation") != -1 && dto.getCustomerLocation() != null
				? dto.getCustomerLocation().toString() : null);

		obj.setCustomerPincode(selectedFields.indexOf("CustomerPincode") != -1 && dto.getCustomerPincode() != null
				? dto.getCustomerPincode().toString() : null);

		obj.setCustomerStateCode(selectedFields.indexOf("CustomerStateCode") != -1 && dto.getCustomerStateCode() != null
				? dto.getCustomerStateCode().toString() : null);

		obj.setBillingPOS(selectedFields.indexOf("BillingPOS") != -1 && dto.getBillingPOS() != null
				? dto.getBillingPOS().toString() : null);

		obj.setCustomerPhone(selectedFields.indexOf("CustomerPhone") != -1 && dto.getCustomerPhone() != null
				? dto.getCustomerPhone().toString() : null);

		obj.setCustomerEmail(selectedFields.indexOf("CustomerEmail") != -1 && dto.getCustomerEmail() != null
				? dto.getCustomerEmail().toString() : null);

		obj.setDispatcherGSTIN(selectedFields.indexOf("DispatcherGSTIN") != -1 && dto.getDispatcherGSTIN() != null
				? dto.getDispatcherGSTIN().toString() : null);

		obj.setDispatcherTradeName(
				selectedFields.indexOf("DispatcherTradeName") != -1 && dto.getDispatcherTradeName() != null
						? dto.getDispatcherTradeName().toString() : null);

		obj.setDispatcherAddress1(
				selectedFields.indexOf("DispatcherAddress1") != -1 && dto.getDispatcherAddress1() != null
						? dto.getDispatcherAddress1().toString() : null);

		obj.setDispatcherAddress2(
				selectedFields.indexOf("DispatcherAddress2") != -1 && dto.getDispatcherAddress2() != null
						? dto.getDispatcherAddress2().toString() : null);

		obj.setDispatcherLocation(
				selectedFields.indexOf("DispatcherLocation") != -1 && dto.getDispatcherLocation() != null
						? dto.getDispatcherLocation().toString() : null);

		obj.setDispatcherPincode(selectedFields.indexOf("DispatcherPincode") != -1 && dto.getDispatcherPincode() != null
				? dto.getDispatcherPincode().toString() : null);

		obj.setDispatcherStateCode(
				selectedFields.indexOf("DispatcherStateCode") != -1 && dto.getDispatcherStateCode() != null
						? dto.getDispatcherStateCode().toString() : null);

		obj.setShipToGSTIN(selectedFields.indexOf("ShipToGSTIN") != -1 && dto.getShipToGSTIN() != null
				? dto.getShipToGSTIN().toString() : null);

		obj.setShipToTradeName(selectedFields.indexOf("ShipToTradeName") != -1 && dto.getShipToTradeName() != null
				? dto.getShipToTradeName().toString() : null);

		obj.setShipToLegalName(selectedFields.indexOf("ShipToLegalName") != -1 && dto.getShipToLegalName() != null
				? dto.getShipToLegalName().toString() : null);

		obj.setShipToAddress1(selectedFields.indexOf("ShipToAddress1") != -1 && dto.getShipToAddress1() != null
				? dto.getShipToAddress1().toString() : null);

		obj.setShipToAddress2(selectedFields.indexOf("ShipToAddress2") != -1 && dto.getShipToAddress2() != null
				? dto.getShipToAddress2().toString() : null);

		obj.setShipToLocation(selectedFields.indexOf("ShipToLocation") != -1 && dto.getShipToLocation() != null
				? dto.getShipToLocation().toString() : null);

		obj.setShipToPincode(selectedFields.indexOf("ShipToPincode") != -1 && dto.getShipToPincode() != null
				? dto.getShipToPincode().toString() : null);

		obj.setShipToStateCode(selectedFields.indexOf("ShipToStateCode") != -1 && dto.getShipToStateCode() != null
				? dto.getShipToStateCode().toString() : null);

		obj.setItemSerialNumber(selectedFields.indexOf("ItemSerialNumber") != -1 && dto.getItemSerialNumber() != null
				? dto.getItemSerialNumber().toString() : null);

		obj.setProductSerialNumber(
				selectedFields.indexOf("ProductSerialNumber") != -1 && dto.getProductSerialNumber() != null
						? dto.getProductSerialNumber().toString() : null);

		obj.setProductName(selectedFields.indexOf("ProductName") != -1 && dto.getProductName() != null
				? dto.getProductName().toString() : null);

		obj.setProductDescription(
				selectedFields.indexOf("ProductDescription") != -1 && dto.getProductDescription() != null
						? dto.getProductDescription().toString() : null);

		obj.setIsService(selectedFields.indexOf("IS_SERVICE") != -1 && dto.getIsService() != null
				? dto.getIsService().toString() : null);

		obj.setHsn(selectedFields.indexOf("HSN") != -1 && dto.getHsn() != null ? dto.getHsn().toString() : null);

		obj.setBarcode(selectedFields.indexOf("Barcode") != -1 && dto.getBarcode() != null ? dto.getBarcode().toString()
				: null);

		obj.setBatchName(selectedFields.indexOf("BatchName") != -1 && dto.getBatchName() != null
				? dto.getBatchName().toString() : null);

		String batchExpiryDate = selectedFields.indexOf("BatchExpiryDate") != -1 && dto.getBatchExpiryDate() != null
				? dto.getBatchExpiryDate().toString() : null;
		/*if (batchExpiryDate != null) {
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(batchExpiryDate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setBatchExpiryDate(newDate);
		} else {*/
			obj.setBatchExpiryDate(batchExpiryDate);
		//}

		String warrantyDate = selectedFields.indexOf("WarrantyDate") != -1 && dto.getWarrantyDate() != null
				? dto.getWarrantyDate().toString() : null;

		/*if (warrantyDate != null) {

			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(warrantyDate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setWarrantyDate(newDate);
		} else {*/
			obj.setWarrantyDate(warrantyDate);
		//}

		obj.setOrderlineReference(
				selectedFields.indexOf("OrderLineReference") != -1 && dto.getOrderlineReference() != null
						? dto.getOrderlineReference().toString() : null);

		obj.setAttributeName(selectedFields.indexOf("AttributeName") != -1 && dto.getAttributeName() != null
				? dto.getAttributeName().toString() : null);

		obj.setAttributeValue(selectedFields.indexOf("AttributeValue") != -1 && dto.getAttributeValue() != null
				? dto.getAttributeValue().toString() : null);

		obj.setOriginCountry(selectedFields.indexOf("OriginCountry") != -1 && dto.getOriginCountry() != null
				? dto.getOriginCountry().toString() : null);

		obj.setFreeQuantity(selectedFields.indexOf("FreeQuantity") != -1 && dto.getFreeQuantity() != null
				? dto.getFreeQuantity().toString() : null);

		obj.setUnitPrice(selectedFields.indexOf("UnitPrice") != -1 && dto.getUnitPrice() != null
				? dto.getUnitPrice().toString() : null);

		obj.setItemAmount(selectedFields.indexOf("ItemAmount") != -1 && dto.getItemAmount() != null
				? dto.getItemAmount().toString() : null);

		obj.setItemDiscount(selectedFields.indexOf("ItemDiscount") != -1 && dto.getItemDiscount() != null
				? dto.getItemDiscount().toString() : null);

		obj.setPreTaxAmount(selectedFields.indexOf("PreTaxAmount") != -1 && dto.getPreTaxAmount() != null
				? dto.getPreTaxAmount().toString() : null);

		obj.setItemAssessableAmount(
				selectedFields.indexOf("ItemAssessableAmount") != -1 && dto.getItemAssessableAmount() != null
						? dto.getItemAssessableAmount().toString() : null);

		obj.setiGSTRate(selectedFields.indexOf("IGSTRate") != -1 && dto.getiGSTRate() != null
				? dto.getiGSTRate().toString() : null);

		obj.setiGSTAmount(selectedFields.indexOf("IGSTAmount") != -1 && dto.getiGSTAmount() != null
				? dto.getiGSTAmount().toString() : null);

		obj.setcGSTRate(selectedFields.indexOf("CGSTRate") != -1 && dto.getcGSTRate() != null
				? dto.getcGSTRate().toString() : null);

		obj.setcGSTAmount(selectedFields.indexOf("CGSTAmount") != -1 && dto.getcGSTAmount() != null
				? dto.getcGSTAmount().toString() : null);

		obj.setsGSTRate(selectedFields.indexOf("SGSTRate") != -1 && dto.getsGSTRate() != null
				? dto.getsGSTRate().toString() : null);

		obj.setsGSTAmount(selectedFields.indexOf("SGSTAmount") != -1 && dto.getsGSTAmount() != null
				? dto.getsGSTAmount().toString() : null);

		obj.setCessAdvaloremRate(selectedFields.indexOf("CessAdvaloremRate") != -1 && dto.getCessAdvaloremRate() != null
				? dto.getCessAdvaloremRate().toString() : null);

		obj.setCessAdvaloremAmount(
				selectedFields.indexOf("CessAdvaloremAmount") != -1 && dto.getCessAdvaloremAmount() != null
						? dto.getCessAdvaloremAmount().toString() : null);

		obj.setCessSpecificRate(selectedFields.indexOf("CessSpecificRate") != -1 && dto.getCessSpecificRate() != null
				? dto.getCessSpecificRate().toString() : null);

		obj.setCessSpecificAmount(
				selectedFields.indexOf("CessSpecificAmount") != -1 && dto.getCessSpecificAmount() != null
						? dto.getCessSpecificAmount().toString() : null);

		obj.setStateCessAdvaloremRate(
				selectedFields.indexOf("StateCessAdvaloremRate") != -1 && dto.getStateCessAdvaloremRate() != null
						? dto.getStateCessAdvaloremRate().toString() : null);

		obj.setStateCessAdvaloremAmount(
				selectedFields.indexOf("StateCessAdvaloremAmount") != -1 && dto.getStateCessAdvaloremAmount() != null
						? dto.getStateCessAdvaloremAmount().toString() : null);

		obj.setStateCessSpecificRate(
				selectedFields.indexOf("StateCessSpecificRate") != -1 && dto.getStateCessSpecificRate() != null
						? dto.getStateCessSpecificRate().toString() : null);

		obj.setStateCessSpecificAmount(
				selectedFields.indexOf("StateCessSpecificAmount") != -1 && dto.getStateCessSpecificAmount() != null
						? dto.getStateCessSpecificAmount().toString() : null);

		obj.setItemOtherCharges(selectedFields.indexOf("ItemOtherCharges") != -1 && dto.getItemOtherCharges() != null
				? dto.getItemOtherCharges().toString() : null);

		obj.setTotalItemAmount(selectedFields.indexOf("TotalItemAmount") != -1 && dto.getTotalItemAmount() != null
				? dto.getTotalItemAmount().toString() : null);

		obj.setInvoiceOtherCharges(
				selectedFields.indexOf("InvoiceOtherCharges") != -1 && dto.getInvoiceOtherCharges() != null
						? dto.getInvoiceOtherCharges().toString() : null);

		obj.setInvoiceAssessableAmount(
				selectedFields.indexOf("InvoiceAssessableAmount") != -1 && dto.getInvoiceAssessableAmount() != null
						? dto.getInvoiceAssessableAmount().toString() : null);

		obj.setInvoiceIGSTAmount(selectedFields.indexOf("InvoiceIGSTAmount") != -1 && dto.getInvoiceIGSTAmount() != null
				? dto.getInvoiceIGSTAmount().toString() : null);

		obj.setInvoiceCGSTAmount(selectedFields.indexOf("InvoiceCGSTAmount") != -1 && dto.getInvoiceCGSTAmount() != null
				? dto.getInvoiceCGSTAmount().toString() : null);

		obj.setInvoiceSGSTAmount(selectedFields.indexOf("InvoiceSGSTAmount") != -1 && dto.getInvoiceSGSTAmount() != null
				? dto.getInvoiceSGSTAmount().toString() : null);

		obj.setInvoiceCessAdvaloremAmount(selectedFields.indexOf("InvoiceCessAdvaloremAmount") != -1
				&& dto.getInvoiceCessAdvaloremAmount() != null ? dto.getInvoiceCessAdvaloremAmount().toString() : null);

		obj.setInvoiceCessSpecificAmount(
				selectedFields.indexOf("InvoiceCessSpecificAmount") != -1 && dto.getInvoiceCessSpecificAmount() != null
						? dto.getInvoiceCessSpecificAmount().toString() : null);

		obj.setInvoiceStateCessAdvaloremAmount(selectedFields.indexOf("InvoiceStateCessAdvaloremAmount") != -1
				&& dto.getInvoiceStateCessAdvaloremAmount() != null
						? dto.getInvoiceStateCessAdvaloremAmount().toString() : null);

		obj.setInvoiceStateCessSpecificAmount(selectedFields.indexOf("InvoiceStateCessSpecificAmount") != -1
				&& dto.getInvoiceStateCessSpecificAmount() != null ? dto.getInvoiceStateCessSpecificAmount().toString()
						: null);

		obj.setInvoiceValue(selectedFields.indexOf("InvoiceValue") != -1 && dto.getInvoiceValue() != null
				? dto.getInvoiceValue().toString() : null);

		obj.setRoundOff(selectedFields.indexOf("RoundOff") != -1 && dto.getRoundOff() != null
				? dto.getRoundOff().toString() : null);

		obj.setTotalInvoiceValue(
				selectedFields.indexOf("TotalInvoiceValue(InWords)") != -1 && dto.getTotalInvoiceValue() != null
						? dto.getTotalInvoiceValue().toString() : null);

		obj.settCSFlagIncomeTax(selectedFields.indexOf("TCSFlagIncomeTax") != -1 && dto.gettCSFlagIncomeTax() != null
				? dto.gettCSFlagIncomeTax().toString() : null);

		obj.settCSRateIncomeTax(selectedFields.indexOf("TCSRateIncomeTax") != -1 && dto.gettCSRateIncomeTax() != null
				? dto.gettCSRateIncomeTax().toString() : null);

		obj.settCSAmountIncomeTax(
				selectedFields.indexOf("TCSAmountIncomeTax") != -1 && dto.gettCSAmountIncomeTax() != null
						? dto.gettCSAmountIncomeTax().toString() : null);

		obj.setCustomerPANOrAadhaar(
				selectedFields.indexOf("CustomerPANOrAadhaar") != -1 && dto.getCustomerPANOrAadhaar() != null
						? dto.getCustomerPANOrAadhaar().toString() : null);

		obj.setCurrencyCode(selectedFields.indexOf("CurrencyCode") != -1 && dto.getCurrencyCode() != null
				? dto.getCurrencyCode().toString() : null);

		obj.setCountryCode(selectedFields.indexOf("CountryCode") != -1 && dto.getCountryCode() != null
				? dto.getCountryCode().toString() : null);

		obj.setInvoiceValueFC(selectedFields.indexOf("InvoiceValueFC") != -1 && dto.getInvoiceValueFC() != null
				? dto.getInvoiceValueFC().toString() : null);

		obj.setPortCode(selectedFields.indexOf("PortCode") != -1 && dto.getPortCode() != null
				? dto.getPortCode().toString() : null);

		obj.setShippingBillNumber(
				selectedFields.indexOf("ShippingBillNumber") != -1 && dto.getShippingBillNumber() != null
						? dto.getShippingBillNumber().toString() : null);

		String shippingBillDate = selectedFields.indexOf("ShippingBillDate") != -1 && dto.getShippingBillDate() != null
				? dto.getShippingBillDate().toString() : null;
		/*if (shippingBillDate != null) {
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(shippingBillDate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setShippingBillDate(newDate);
		} else {*/
			obj.setShippingBillDate(shippingBillDate);
		//}

		obj.setInvoiceRemarks(selectedFields.indexOf("InvoiceRemarks") != -1 && dto.getInvoiceRemarks() != null
				? dto.getInvoiceRemarks().toString() : null);

		String invoicePeriodStartDate = selectedFields.indexOf("InvoicePeriodStartDate") != -1
				&& dto.getInvoicePeriodStartDate() != null ? dto.getInvoicePeriodStartDate().toString() : null;

		/*if (invoicePeriodStartDate != null) {
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(invoicePeriodStartDate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoicePeriodStartDate(newDate);
		} else {*/
			obj.setInvoicePeriodStartDate(invoicePeriodStartDate);
		//}

		String invoicePeriodEndDate = selectedFields.indexOf("InvoicePeriodEndDate") != -1
				&& dto.getInvoicePeriodEndDate() != null ? dto.getInvoicePeriodEndDate().toString() : null;

		/*if (invoicePeriodEndDate != null) {
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(invoicePeriodEndDate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoicePeriodEndDate(newDate);
		} else {*/
			obj.setInvoicePeriodEndDate(invoicePeriodEndDate);
		//}

		obj.setPreceedingInvoiceNumber(
				selectedFields.indexOf("PreceedingInvoiceNumber") != -1 && dto.getPreceedingInvoiceNumber() != null
						? dto.getPreceedingInvoiceNumber().toString() : null);

		String preceedingInvoiceDate = selectedFields.indexOf("PreceedingInvoiceDate") != -1
				&& dto.getPreceedingInvoiceDate() != null ? dto.getPreceedingInvoiceDate().toString() : null;

		/*if (preceedingInvoiceDate != null) {

			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(preceedingInvoiceDate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPreceedingInvoiceDate(newDate);
		} else {*/
			obj.setPreceedingInvoiceDate(preceedingInvoiceDate);
	//	}

		obj.setOtherReference(selectedFields.indexOf("OtherReference") != -1 && dto.getOtherReference() != null
				? dto.getOtherReference().toString() : null);

		obj.setReceiptAdviceReference(
				selectedFields.indexOf("ReceiptAdviceReference") != -1 && dto.getReceiptAdviceReference() != null
						? dto.getReceiptAdviceReference().toString() : null);

		obj.setReceiptAdviceDate(selectedFields.indexOf("ReceiptAdviceDate") != -1 && dto.getReceiptAdviceDate() != null
				? dto.getReceiptAdviceDate().toString() : null);

		obj.setTenderReference(selectedFields.indexOf("TenderReference") != -1 && dto.getTenderReference() != null
				? dto.getTenderReference().toString() : null);

		obj.setContractReference(selectedFields.indexOf("ContractReference") != -1 && dto.getContractReference() != null
				? dto.getContractReference().toString() : null);

		obj.setExternalReference(selectedFields.indexOf("ExternalReference") != -1 && dto.getExternalReference() != null
				? dto.getExternalReference().toString() : null);

		obj.setProjectReference(selectedFields.indexOf("ProjectReference") != -1 && dto.getProjectReference() != null
				? dto.getProjectReference().toString() : null);

		obj.setCustomerPOReferenceNumber(
				selectedFields.indexOf("CustomerPOReferenceNumber") != -1 && dto.getCustomerPOReferenceNumber() != null
						? dto.getCustomerPOReferenceNumber().toString() : null);

		obj.setCustomerPOReferenceDate(
				selectedFields.indexOf("CustomerPOReferenceDate") != -1 && dto.getCustomerPOReferenceDate() != null
						? dto.getCustomerPOReferenceDate().toString() : null);

		obj.setPayeeName(selectedFields.indexOf("PayeeName") != -1 && dto.getPayeeName() != null
				? dto.getPayeeName().toString() : null);

		obj.setModeOfPayment(selectedFields.indexOf("ModeOfPayment") != -1 && dto.getModeOfPayment() != null
				? dto.getModeOfPayment().toString() : null);

		obj.setBranchOrIFSCCode(selectedFields.indexOf("BranchOrIFSCCode") != -1 && dto.getBranchOrIFSCCode() != null
				? dto.getBranchOrIFSCCode().toString() : null);

		obj.setPaymentTerms(selectedFields.indexOf("PaymentTerms") != -1 && dto.getPaymentTerms() != null
				? dto.getPaymentTerms().toString() : null);

		obj.setPaymentInstruction(
				selectedFields.indexOf("PaymentInstruction") != -1 && dto.getPaymentInstruction() != null
						? dto.getPaymentInstruction().toString() : null);

		obj.setCreditTransfer(selectedFields.indexOf("CreditTransfer") != -1 && dto.getCreditTransfer() != null
				? dto.getCreditTransfer().toString() : null);

		obj.setDirectDebit(selectedFields.indexOf("DirectDebit") != -1 && dto.getDirectDebit() != null
				? dto.getDirectDebit().toString() : null);

		obj.setCreditDays(selectedFields.indexOf("CreditDays") != -1 && dto.getCreditDays() != null
				? dto.getCreditDays().toString() : null);

		obj.setPaidAmount(selectedFields.indexOf("PaidAmount") != -1 && dto.getPaidAmount() != null
				? dto.getPaidAmount().toString() : null);

		obj.setBalanceAmount(selectedFields.indexOf("BalanceAmount") != -1 && dto.getBalanceAmount() != null
				? dto.getBalanceAmount().toString() : null);

		String paymentDueDate = selectedFields.indexOf("PaymentDueDate") != -1 && dto.getPaymentDueDate() != null
				? dto.getPaymentDueDate().toString() : null;
		/*if (paymentDueDate != null) {

			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(paymentDueDate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPaymentDueDate(newDate);
		} else {*/
			obj.setPaymentDueDate(paymentDueDate);
		//}

		obj.setAccountDetail(selectedFields.indexOf("AccountDetail") != -1 && dto.getAccountDetail() != null
				? dto.getAccountDetail().toString() : null);

		obj.setEcomGSTIN(selectedFields.indexOf("EcomGSTIN") != -1 && dto.getEcomGSTIN() != null
				? dto.getEcomGSTIN().toString() : null);

		obj.setEcomTransactionID(selectedFields.indexOf("EcomTransactionID") != -1 && dto.getEcomTransactionID() != null
				? dto.getEcomTransactionID().toString() : null);

		obj.setSupportingDocURL(selectedFields.indexOf("SupportingDocURL") != -1 && dto.getSupportingDocURL() != null
				? dto.getSupportingDocURL().toString() : null);

		obj.setSupportingDocument(
				selectedFields.indexOf("SupportingDocument") != -1 && dto.getSupportingDocument() != null
						? dto.getSupportingDocument().toString() : null);

		obj.setAdditionalInformation(
				selectedFields.indexOf("AdditionalInformation") != -1 && dto.getAdditionalInformation() != null
						? dto.getAdditionalInformation().toString() : null);

		obj.setTransactionType(selectedFields.indexOf("TransactionType") != -1 && dto.getTransactionType() != null
				? dto.getTransactionType().toString() : null);

		obj.setSubSupplyType(selectedFields.indexOf("SubSupplyType") != -1 && dto.getSubSupplyType() != null
				? dto.getSubSupplyType().toString() : null);

		obj.setOtherSupplyTypeDescription(selectedFields.indexOf("OtherSupplyTypeDescription") != -1
				&& dto.getOtherSupplyTypeDescription() != null ? dto.getOtherSupplyTypeDescription().toString() : null);

		obj.setTransporterID(selectedFields.indexOf("TransporterId") != -1 && dto.getTransporterID() != null
				? dto.getTransporterID().toString() : null);

		obj.setTransporterName(selectedFields.indexOf("TransporterName") != -1 && dto.getTransporterName() != null
				? dto.getTransporterName().toString() : null);

		obj.setTransportMode(selectedFields.indexOf("TransportMode") != -1 && dto.getTransportMode() != null
				? dto.getTransportMode().toString() : null);

		obj.setTransportDocNo(selectedFields.indexOf("TransportDocNo") != -1 && dto.getTransportDocNo() != null
				? dto.getTransportDocNo().toString() : null);

		String transportDocDate = selectedFields.indexOf("TransportDocDate") != -1 && dto.getTransportDocDate() != null
				? dto.getTransportDocDate().toString() : null;
		/*if (transportDocDate != null) {
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(transportDocDate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setTransportDocDate(newDate);
		} else {*/
			obj.setTransportDocDate(transportDocDate);
		//}

		// mapping doubt
		obj.setDistance(selectedFields.indexOf("Distance") != -1 && dto.getDistance() != null
				? dto.getDistance().toString() : null);

		obj.setVehicleNo(selectedFields.indexOf("VehicleNo") != -1 && dto.getVehicleNo() != null
				? dto.getVehicleNo().toString() : null);

		obj.setVehicleType(selectedFields.indexOf("VehicleType") != -1 && dto.getVehicleType() != null
				? dto.getVehicleType().toString() : null);

		obj.setReturnPeriod(selectedFields.indexOf("ReturnPeriod") != -1 && dto.getReturnPeriod() != null
				? dto.getReturnPeriod().toString() : null);

		obj.setOriginalDocumentType(
				selectedFields.indexOf("OriginalDocumentType") != -1 && dto.getOriginalDocumentType() != null
						? dto.getOriginalDocumentType().toString() : null);

		obj.setOriginalCustomerGSTIN(
				selectedFields.indexOf("OriginalCustomerGSTIN") != -1 && dto.getOriginalCustomerGSTIN() != null
						? dto.getOriginalCustomerGSTIN().toString() : null);

		obj.setDifferentialPercentageFlag(selectedFields.indexOf("DifferentialPercentageFlag") != -1
				&& dto.getDifferentialPercentageFlag() != null ? dto.getDifferentialPercentageFlag().toString() : null);

		obj.setSec7ofIGSTFlag(selectedFields.indexOf("Section7OfIGSTFlag") != -1 && dto.getSec7ofIGSTFlag() != null
				? dto.getSec7ofIGSTFlag().toString() : null);

		obj.setClaimRefndFlag(selectedFields.indexOf("ClaimRefundFlag") != -1 && dto.getClaimRefndFlag() != null
				? dto.getClaimRefndFlag().toString() : null);

		obj.setAutoPopltToRefund(
				selectedFields.indexOf("AutoPopulateToRefund") != -1 && dto.getAutoPopltToRefund() != null
						? dto.getAutoPopltToRefund().toString() : null);

		obj.setcRDRPreGST(selectedFields.indexOf("CRDRPreGST") != -1 && dto.getcRDRPreGST() != null
				? dto.getcRDRPreGST().toString() : null);

		obj.setCustomerType(selectedFields.indexOf("CustomerType") != -1 && dto.getCustomerType() != null
				? dto.getCustomerType().toString() : null);

		obj.setCustomerCode(selectedFields.indexOf("CustomerCode") != -1 && dto.getCustomerCode() != null
				? dto.getCustomerCode().toString() : null);

		obj.setProductCode(selectedFields.indexOf("ProductCode") != -1 && dto.getProductCode() != null
				? dto.getProductCode().toString() : null);

		obj.setCategoryOfProduct(selectedFields.indexOf("CategoryOfProduct") != -1 && dto.getCategoryOfProduct() != null
				? dto.getCategoryOfProduct().toString() : null);

		obj.setiTCFlag(selectedFields.indexOf("ITCFlag") != -1 && dto.getiTCFlag() != null ? dto.getiTCFlag().toString()
				: null);

		obj.setStateApplyingCess(selectedFields.indexOf("StateApplyingCess") != -1 && dto.getStateApplyingCess() != null
				? dto.getStateApplyingCess().toString() : null);

		obj.setfOB(selectedFields.indexOf("FOB") != -1 && dto.getfOB() != null ? dto.getfOB().toString() : null);

		obj.setExportDuty(selectedFields.indexOf("ExportDuty") != -1 && dto.getExportDuty() != null
				? dto.getExportDuty().toString() : null);

		obj.setExchangeRate(selectedFields.indexOf("ExchangeRate") != -1 && dto.getExchangeRate() != null
				? dto.getExchangeRate().toString() : null);

		obj.setReasonForCreditDebitNote(
				selectedFields.indexOf("ReasonForCreditDebitNote") != -1 && dto.getReasonForCreditDebitNote() != null
						? dto.getReasonForCreditDebitNote().toString() : null);

		obj.settCSFlagGST(selectedFields.indexOf("TCSFlagGST") != -1 && dto.gettCSFlagGST() != null
				? dto.gettCSFlagGST().toString() : null);

		obj.settCSIGSTAmount(selectedFields.indexOf("TCSIGSTAmount") != -1 && dto.gettCSIGSTAmount() != null
				? dto.gettCSIGSTAmount().toString() : null);

		obj.settCSCGSTAmount(selectedFields.indexOf("TCSCGSTAmount") != -1 && dto.gettCSCGSTAmount() != null
				? dto.gettCSCGSTAmount().toString() : null);

		obj.settCSSGSTAmount(selectedFields.indexOf("TCSSGSTAmount") != -1 && dto.gettCSSGSTAmount() != null
				? dto.gettCSSGSTAmount().toString() : null);

		obj.settDSFlagGST(selectedFields.indexOf("TDSFlagGST") != -1 && dto.gettDSFlagGST() != null
				? dto.gettDSFlagGST().toString() : null);

		obj.settDSIGSTAmount(selectedFields.indexOf("TDSIGSTAmount") != -1 && dto.gettDSIGSTAmount() != null
				? dto.gettDSIGSTAmount().toString() : null);

		obj.settDSCGSTAmount(selectedFields.indexOf("TDSCGSTAmount") != -1 && dto.gettDSCGSTAmount() != null
				? dto.gettDSCGSTAmount().toString() : null);

		obj.settDSSGSTAmount(selectedFields.indexOf("TDSSGSTAmount") != -1 && dto.gettDSSGSTAmount() != null
				? dto.gettDSSGSTAmount().toString() : null);

		obj.setUserId(
				selectedFields.indexOf("UserID") != -1 && dto.getUserId() != null ? dto.getUserId().toString() : null);

		obj.setCompanyCode(selectedFields.indexOf("CompanyCode") != -1 && dto.getCompanyCode() != null
				? dto.getCompanyCode().toString() : null);

		obj.setSourceIdentifier(selectedFields.indexOf("SourceIdentifier") != -1 && dto.getSourceIdentifier() != null
				? dto.getSourceIdentifier().toString() : null);

		obj.setSourceFileName(selectedFields.indexOf("SourceFileName") != -1 && dto.getSourceFileName() != null
				? dto.getSourceFileName().toString() : null);

		obj.setPlantCode(selectedFields.indexOf("PlantCode") != -1 && dto.getPlantCode() != null
				? dto.getPlantCode().toString() : null);

		obj.setDivision(selectedFields.indexOf("Division") != -1 && dto.getDivision() != null
				? dto.getDivision().toString() : null);

		obj.setSubDivision(selectedFields.indexOf("SubDivision") != -1 && dto.getSubDivision() != null
				? dto.getSubDivision().toString() : null);

		obj.setLocation(selectedFields.indexOf("Location") != -1 && dto.getLocation() != null
				? dto.getLocation().toString() : null);

		obj.setSalesOrganisation(selectedFields.indexOf("SalesOrganisation") != -1 && dto.getSalesOrganisation() != null
				? dto.getSalesOrganisation().toString() : null);

		obj.setDistributionChannel(
				selectedFields.indexOf("DistributionChannel") != -1 && dto.getDistributionChannel() != null
						? dto.getDistributionChannel().toString() : null);

		obj.setProfitCentre1(selectedFields.indexOf("ProfitCentre1") != -1 && dto.getProfitCentre1() != null
				? dto.getProfitCentre1().toString() : null);

		obj.setProfitCentre2(selectedFields.indexOf("ProfitCentre2") != -1 && dto.getProfitCentre2() != null
				? dto.getProfitCentre2().toString() : null);

		obj.setProfitCentre3(selectedFields.indexOf("ProfitCentre3") != -1 && dto.getProfitCentre3() != null
				? dto.getProfitCentre3().toString() : null);

		obj.setProfitCentre4(selectedFields.indexOf("ProfitCentre4") != -1 && dto.getProfitCentre4() != null
				? dto.getProfitCentre4().toString() : null);

		obj.setProfitCentre5(selectedFields.indexOf("ProfitCentre5") != -1 && dto.getProfitCentre5() != null
				? dto.getProfitCentre5().toString() : null);

		obj.setProfitCentre6(selectedFields.indexOf("ProfitCentre6") != -1 && dto.getProfitCentre6() != null
				? dto.getProfitCentre6().toString() : null);

		obj.setProfitCentre7(selectedFields.indexOf("ProfitCentre7") != -1 && dto.getProfitCentre7() != null
				? dto.getProfitCentre7().toString() : null);

		obj.setProfitCentre8(selectedFields.indexOf("ProfitCentre8") != -1 && dto.getProfitCentre8() != null
				? dto.getProfitCentre8().toString() : null);

		obj.setGlAssessableValue(selectedFields.indexOf("GLAssessableValue") != -1 && dto.getGlAssessableValue() != null
				? dto.getGlAssessableValue().toString() : null);

		obj.setGlIGST(
				selectedFields.indexOf("GLIGST") != -1 && dto.getGlIGST() != null ? dto.getGlIGST().toString() : null);

		obj.setGlCGST(
				selectedFields.indexOf("GLCGST") != -1 && dto.getGlCGST() != null ? dto.getGlCGST().toString() : null);

		obj.setGlSGST(
				selectedFields.indexOf("GLSGST") != -1 && dto.getGlSGST() != null ? dto.getGlSGST().toString() : null);

		obj.setGlAdvaloremCess(selectedFields.indexOf("GLAdvaloremCess") != -1 && dto.getGlAdvaloremCess() != null
				? dto.getGlAdvaloremCess().toString() : null);

		obj.setGlSpecificCess(selectedFields.indexOf("GLSpecificCess") != -1 && dto.getGlSpecificCess() != null
				? dto.getGlSpecificCess().toString() : null);

		obj.setgLStateCessAdvalorem(
				selectedFields.indexOf("GLStateCessAdvalorem") != -1 && dto.getgLStateCessAdvalorem() != null
						? dto.getgLStateCessAdvalorem().toString() : null);

		obj.setgLStateCessSpecific(
				selectedFields.indexOf("GLStateCessSpecific") != -1 && dto.getgLStateCessSpecific() != null
						? dto.getgLStateCessSpecific().toString() : null);

		String gLPostingDate = selectedFields.indexOf("GLPostingDate") != -1 && dto.getGlPostingDate() != null
				? dto.getGlPostingDate().toString() : null;
/*
		if (gLPostingDate != null) {

			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(gLPostingDate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setGlPostingDate(newDate);
		} else {*/
			obj.setGlPostingDate(gLPostingDate);
		//}

		obj.setSalesOrderNumber(selectedFields.indexOf("SalesOrderNumber") != -1 && dto.getSalesOrderNumber() != null
				? dto.getSalesOrderNumber().toString() : null);

		// Mapping Doubt
		// EWBNo

		// EWBNumber
		obj.seteWBNumber(selectedFields.indexOf("EWBNumber") != -1 && dto.geteWBNumber() != null
				? dto.geteWBNumber().toString() : null);

		// Mapping Doubt
		// EWBDATE
		obj.seteWBDate(selectedFields.indexOf("EwbDate") != -1 && dto.geteWBDate() != null ? dto.geteWBDate().toString()
				: null);

		obj.setAccountingVoucherNumber(
				selectedFields.indexOf("AccountingVoucherNumber") != -1 && dto.getAccountingVoucherNumber() != null
						? dto.getAccountingVoucherNumber().toString() : null);

		String accountingVoucherDate = selectedFields.indexOf("AccountingVoucherDate") != -1
				&& dto.getAccountingVoucherDate() != null ? dto.getAccountingVoucherDate().toString() : null;

	/*	if (accountingVoucherDate != null) {

			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(accountingVoucherDate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setAccountingVoucherDate(newDate);
		} else {*/
			obj.setAccountingVoucherDate(accountingVoucherDate);
		//}

		obj.setDocumentReferenceNumber(
				selectedFields.indexOf("DocumentReferenceNumber") != -1 && dto.getDocumentReferenceNumber() != null
						? dto.getDocumentReferenceNumber().toString() : null);
		obj.setCustomerTAN(selectedFields.indexOf("CustomerTAN") != -1 && dto.getCustomerTAN() != null
				? dto.getCustomerTAN().toString() : null);

		obj.setUserDefField1(selectedFields.indexOf("UserDefinedField1") != -1 && dto.getUserDefField1() != null
				? dto.getUserDefField1().toString() : null);
		obj.setUserDefField2(selectedFields.indexOf("UserDefinedField2") != -1 && dto.getUserDefField2() != null
				? dto.getUserDefField2().toString() : null);
		obj.setUserDefField3(selectedFields.indexOf("UserDefinedField3") != -1 && dto.getUserDefField3() != null
				? dto.getUserDefField3().toString() : null);
		obj.setUserDefField4(selectedFields.indexOf("UserDefinedField4") != -1 && dto.getUserDefField4() != null
				? dto.getUserDefField4().toString() : null);
		obj.setUserDefField5(selectedFields.indexOf("UserDefinedField5") != -1 && dto.getUserDefField5() != null
				? dto.getUserDefField5().toString() : null);
		obj.setUserDefField6(selectedFields.indexOf("UserDefinedField6") != -1 && dto.getUserDefField6() != null
				? dto.getUserDefField6().toString() : null);
		obj.setUserDefField7(selectedFields.indexOf("UserDefinedField7") != -1 && dto.getUserDefField7() != null
				? dto.getUserDefField7().toString() : null);
		obj.setUserDefField8(selectedFields.indexOf("UserDefinedField8") != -1 && dto.getUserDefField8() != null
				? dto.getUserDefField8().toString() : null);
		obj.setUserDefField9(selectedFields.indexOf("UserDefinedField9") != -1 && dto.getUserDefField9() != null
				? dto.getUserDefField9().toString() : null);
		obj.setUserDefField10(selectedFields.indexOf("UserDefinedField10") != -1 && dto.getUserDefField10() != null
				? dto.getUserDefField10().toString() : null);
		obj.setUserDefField11(selectedFields.indexOf("UserDefinedField11") != -1 && dto.getUserDefField11() != null
				? dto.getUserDefField11().toString() : null);
		obj.setUserDefField12(selectedFields.indexOf("UserDefinedField12") != -1 && dto.getUserDefField12() != null
				? dto.getUserDefField12().toString() : null);
		obj.setUserDefField13(selectedFields.indexOf("UserDefinedField13") != -1 && dto.getUserDefField13() != null
				? dto.getUserDefField13().toString() : null);
		obj.setUserDefField14(selectedFields.indexOf("UserDefinedField14") != -1 && dto.getUserDefField14() != null
				? dto.getUserDefField14().toString() : null);
		obj.setUserDefField15(selectedFields.indexOf("UserDefinedField15") != -1 && dto.getUserDefField15() != null
				? dto.getUserDefField15().toString() : null);
		obj.setUserDefField16(selectedFields.indexOf("UserDefinedField16") != -1 && dto.getUserDefField16() != null
				? dto.getUserDefField16().toString() : null);
		obj.setUserDefField17(selectedFields.indexOf("UserDefinedField17") != -1 && dto.getUserDefField17() != null
				? dto.getUserDefField17().toString() : null);
		obj.setUserDefField18(selectedFields.indexOf("UserDefinedField18") != -1 && dto.getUserDefField18() != null
				? dto.getUserDefField18().toString() : null);
		obj.setUserDefField19(selectedFields.indexOf("UserDefinedField19") != -1 && dto.getUserDefField19() != null
				? dto.getUserDefField19().toString() : null);
		obj.setUserDefField20(selectedFields.indexOf("UserDefinedField20") != -1 && dto.getUserDefField20() != null
				? dto.getUserDefField20().toString() : null);
		obj.setUserDefField21(selectedFields.indexOf("UserDefinedField21") != -1 && dto.getUserDefField21() != null
				? dto.getUserDefField21().toString() : null);
		obj.setUserDefField22(selectedFields.indexOf("UserDefinedField22") != -1 && dto.getUserDefField22() != null
				? dto.getUserDefField22().toString() : null);
		obj.setUserDefField23(selectedFields.indexOf("UserDefinedField23") != -1 && dto.getUserDefField23() != null
				? dto.getUserDefField23().toString() : null);
		obj.setUserDefField24(selectedFields.indexOf("UserDefinedField24") != -1 && dto.getUserDefField24() != null
				? dto.getUserDefField24().toString() : null);
		obj.setUserDefField25(selectedFields.indexOf("UserDefinedField25") != -1 && dto.getUserDefField25() != null
				? dto.getUserDefField25().toString() : null);
		obj.setUserDefField26(selectedFields.indexOf("UserDefinedField26") != -1 && dto.getUserDefField26() != null
				? dto.getUserDefField26().toString() : null);
		obj.setUserDefField27(selectedFields.indexOf("UserDefinedField27") != -1 && dto.getUserDefField27() != null
				? dto.getUserDefField27().toString() : null);
		obj.setUserDefField28(selectedFields.indexOf("UserDefinedField28") != -1 && dto.getUserDefField28() != null
				? dto.getUserDefField28().toString() : null);
		obj.setUserDefField29(selectedFields.indexOf("UserDefinedField29") != -1 && dto.getUserDefField29() != null
				? dto.getUserDefField29().toString() : null);
		obj.setUserDefField30(selectedFields.indexOf("UserDefinedField30") != -1 && dto.getUserDefField30() != null
				? dto.getUserDefField30().toString() : null);

		obj.setSupplyTypeASP(selectedFields.indexOf("DerivedSupplyType-DigiGST") != -1 && dto.getSupplyTypeASP() != null
				? dto.getSupplyTypeASP().toString() : null);

		obj.setDistanceProvidedByUser(
				selectedFields.indexOf("Distance(Provided by user)") != -1 && dto.getDistanceProvidedByUser() != null
						? dto.getDistanceProvidedByUser().toString() : null);
		
		obj.setApproximateDistanceASP(selectedFields.indexOf("Distance (Computed by DigiGST)") != -1
				&& dto.getApproximateDistanceASP() != null ? dto.getApproximateDistanceASP().toString() : null);

		obj.setDistanceSavedtoEWB(selectedFields.indexOf("Distance (NIC)") != -1 && dto.getDistanceSavedtoEWB() != null
				? dto.getDistanceSavedtoEWB().toString() : null);

		obj.setUserId1(selectedFields.indexOf("USER_ID") != -1 && dto.getUserId1() != null ? dto.getUserId1().toString()
				: null);

		obj.setFileID(
				selectedFields.indexOf("FileID") != -1 && dto.getFileID() != null ? dto.getFileID().toString() : null);

		obj.setFileName(selectedFields.indexOf("FileName") != -1 && dto.getFileName() != null
				? dto.getFileName().toString() : null);

		obj.setInvoiceOtherChargesASP(
				selectedFields.indexOf("InvoiceOtherCharges-DigiGST") != -1 && dto.getInvoiceOtherChargesASP() != null
						? dto.getInvoiceOtherChargesASP().toString() : null);

		obj.setInvoiceAssessableAmountASP(selectedFields.indexOf("InvoiceAssessableAmount-DigiGST") != -1
				&& dto.getInvoiceAssessableAmountASP() != null ? dto.getInvoiceAssessableAmountASP().toString() : null);

		obj.setInvoiceIGSTAmountASP(
				selectedFields.indexOf("InvoiceIGSTAmount-DigiGST") != -1 && dto.getInvoiceIGSTAmountASP() != null
						? dto.getInvoiceIGSTAmountASP().toString() : null);

		obj.setInvoiceCGSTAmountASP(
				selectedFields.indexOf("InvoiceCGSTAmount-DigiGST") != -1 && dto.getInvoiceCGSTAmountASP() != null
						? dto.getInvoiceCGSTAmountASP().toString() : null);

		obj.setInvoiceSGSTAmountASP(
				selectedFields.indexOf("InvoiceSGSTAmount-DigiGST") != -1 && dto.getInvoiceSGSTAmountASP() != null
						? dto.getInvoiceSGSTAmountASP().toString() : null);

		obj.setInvoiceCessAdvaloremAmountASP(selectedFields.indexOf("InvoiceCessAdvaloremAmount-DigiGST") != -1
				&& dto.getInvoiceCessAdvaloremAmountASP() != null ? dto.getInvoiceCessAdvaloremAmountASP().toString()
						: null);

		obj.setInvoiceCessSpecificAmountASP(selectedFields.indexOf("InvoiceCessSpecificAmount-DigiGST") != -1
				&& dto.getInvoiceCessSpecificAmountASP() != null ? dto.getInvoiceCessSpecificAmountASP().toString()
						: null);

		obj.setInvoiceStateCessAdvaloremAmountASP(
				selectedFields.indexOf("InvoiceStateCessAdvaloremAmount-DigiGST") != -1
						&& dto.getInvoiceStateCessAdvaloremAmountASP() != null
								? dto.getInvoiceStateCessAdvaloremAmountASP().toString() : null);

		obj.setInvoiceStateCessSpecificAmountASP(selectedFields.indexOf("InvoiceStateCessSpecificAmount-DigiGST") != -1
				&& dto.getInvoiceStateCessSpecificAmountASP() != null
						? dto.getInvoiceStateCessSpecificAmountASP().toString() : null);

		obj.setInvoiceValueASP(selectedFields.indexOf("InvoiceValue-DigiGST") != -1 && dto.getInvoiceValueASP() != null
				? dto.getInvoiceValueASP().toString() : null);

		obj.setIntegratedTaxAmountASP(
				selectedFields.indexOf("IGSTAmount-DigiGST") != -1 && dto.getIntegratedTaxAmountASP() != null
						? dto.getIntegratedTaxAmountASP().toString() : null);

		obj.setCentralTaxAmountASP(
				selectedFields.indexOf("CGSTAmount-DigiGST") != -1 && dto.getCentralTaxAmountASP() != null
						? dto.getCentralTaxAmountASP().toString() : null);

		obj.setStateUTTaxAmountASP(
				selectedFields.indexOf("SGSTAmount-DigiGST") != -1 && dto.getStateUTTaxAmountASP() != null
						? dto.getStateUTTaxAmountASP().toString() : null);

		obj.setCessAdvaloremAmountASP(
				selectedFields.indexOf("CessAdvaloremAmount-DigiGST") != -1 && dto.getCessAdvaloremAmountASP() != null
						? dto.getCessAdvaloremAmountASP().toString() : null);

		obj.setStateCessAdvaloremAmountASP(selectedFields.indexOf("StateCessAdvaloremAmount-DigiGST") != -1
				&& dto.getStateCessAdvaloremAmountASP() != null ? dto.getStateCessAdvaloremAmountASP().toString()
						: null);

		obj.setIntegratedTaxAmountRET1Impact(
				selectedFields.indexOf("IGSTAmount-Difference") != -1 && dto.getIntegratedTaxAmountRET1Impact() != null
						? dto.getIntegratedTaxAmountRET1Impact().toString() : null);

		obj.setCentralTaxAmountRET1Impact(
				selectedFields.indexOf("CGSTAmount-Difference") != -1 && dto.getCentralTaxAmountRET1Impact() != null
						? dto.getCentralTaxAmountRET1Impact().toString() : null);

		obj.setStateUTTaxAmountRET1Impact(
				selectedFields.indexOf("SGSTAmount-Difference") != -1 && dto.getStateUTTaxAmountRET1Impact() != null
						? dto.getStateUTTaxAmountRET1Impact().toString() : null);

		obj.setCessAdvaloremAmountDifference(selectedFields.indexOf("CessAdvaloremAmount-Difference") != -1
				&& dto.getCessAdvaloremAmountDifference() != null ? dto.getCessAdvaloremAmountDifference().toString()
						: null);

		obj.setStateCessAdvaloremAmountDifference(selectedFields.indexOf("StateCessAdvaloremAmount-Difference") != -1
				&& dto.getStateCessAdvaloremAmountDifference() != null
						? dto.getStateCessAdvaloremAmountDifference().toString() : null);

		obj.setRecordStatus(selectedFields.indexOf("RecordStatus") != -1 && dto.getRecordStatus() != null
				? dto.getRecordStatus().toString() : null);

		String eINVCancellationDate = selectedFields.indexOf("EINVCancellationDate") != -1
				&& dto.getIrnCancellationDate() != null ? dto.getIrnCancellationDate().toString() : null;

		if (eINVCancellationDate != null) {
			String[] dateTime = eINVCancellationDate.split(" ");

			String date = dateTime[0];

			obj.setIrnCancellationDate(date);
		}

		String eINVCancellationTime = selectedFields.indexOf("EINVCancellationTime") != -1
				&& dto.getIrnCancellationTime() != null ? dto.getIrnCancellationTime().toString() : null;
		if (eINVCancellationTime != null) {

			String[] dateTime = eINVCancellationTime.split(" ");
			int ln = dateTime.length;
			String time = ln > 1 ? dateTime[1] : dateTime[0];

			obj.setIrnCancellationTime(DownloadReportsConstant.CSVCHARACTER.concat(time));
		}

		String eWBCancellationDate = selectedFields.indexOf("EWBCancellationDate") != -1
				&& dto.geteWBCancellationDate() != null ? dto.geteWBCancellationDate().toString() : null;

		if (eWBCancellationDate != null) {

			String[] dateTime = eWBCancellationDate.split(" ");
			String date = dateTime[0];

			obj.seteWBCancellationDate(date);
		}

		String eWBCancellationTime = selectedFields.indexOf("EWBCancellationTime") != -1
				&& dto.geteWBCancellationTime() != null ? dto.geteWBCancellationTime().toString() : null;

		if (eWBCancellationTime != null) {

			String[] dateTime = eWBCancellationTime.split(" ");
			int ln = dateTime.length;
			String time = ln > 1 ? dateTime[1] : dateTime[0];

			obj.seteWBCancellationTime(DownloadReportsConstant.CSVCHARACTER.concat(time));
		}

		obj.setDigiGSTStatus(selectedFields.indexOf("DigiGSTStatus") != -1 && dto.getDigiGSTStatus() != null
				? dto.getDigiGSTStatus().toString() : null);

		obj.setInfoErrorCode(selectedFields.indexOf("EWBInformationCode") != -1 && dto.getInfoErrorCode() != null
				? dto.getInfoErrorCode().toString() : null);

		obj.setInfoErrorMsg(selectedFields.indexOf("EWBInformationDescription") != -1 && dto.getInfoErrorMsg() != null
				? dto.getInfoErrorMsg().toString() : null);

		String itemUqc = selectedFields.indexOf("UQC") != -1 && dto.getuQC() != null ? dto.getuQC().toString() : null;

		// Doubt
		// String itemQty = dto[311] != null ? dto[311].toString() : null;

		// Doubt
		// String itemUqcUser = dto[86] != null ? dto[86].toString() : null;

		String itemQtyUser = selectedFields.indexOf("Quantity") != -1 && dto.getQuantity() != null
				? dto.getQuantity().toString() : null;

		String uqc = itemUqc;
		String qty = itemQtyUser;

		obj.setuQC(uqc != null ? uqc : null);
		obj.setQuantity(qty != null ? qty : null);
		
		obj.setEINVvsGstr1Reponse(selectedFields.indexOf("EINVvsGstr1Reponse") != -1 && dto.getEINVvsGstr1Reponse() != null
				? dto.getEINVvsGstr1Reponse().toString() : null);
		return obj;
	}
	public String removeApostrophe(String input) {
        if (input.startsWith("'")) {
            return input.substring(1);
        } else {
            return input;
        }
	}
}
