package com.ey.advisory.app.data.services.qrvspdf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRInwardEinvoiceTaggingEntity;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRInwardEinvoiceTaggingRepository;
import com.ey.advisory.app.data.services.pdfreader.PDFCommonUtility;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Component
@Slf4j
public class QRvsPdfCommonUtility {

	private final static String MATCH = "Match";

	private final static String MISMATCH = "MisMatch";

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPemtRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	@Autowired
	private PDFCommonUtility pdfUtility;
	
	
	@Autowired
	private QRInwardEinvoiceTaggingRepository qrInwardEinvoiceTaggingRepository;

	public static void main(String[] args) {
		// Sample JSON data as a string
		String jsonString = "{\n" + "    \"data\": {\n"
				+ "        \"Line-items\": [\n" + "            {\n"
				+ "                \"Description\": \"H-M Cetrimide Agar Plate MPH024 50PT\",\n"
				+ "                \"HSN number\": \"38220000\",\n"
				+ "                \"Quantity\": 8,\n"
				+ "                \"Unit Price\": 3016.0,\n"
				+ "                \"Taxable Amount\": 24128.0,\n"
				+ "                \"Unit\": \"NO.\",\n"
				+ "                \"Tax Rate\": 18.0,\n"
				+ "                \"Tax Amount\": 3016.0,\n"
				+ "                \"Line Item Number\": \"1\"\n"
				+ "            },\n" + "            {\n"
				+ "                \"Description\": \"HM Rappaport Vasiliadis Broth LQ104 (50X10ML) (Stored at 15-25c)\",\n"
				+ "                \"HSN number\": \"38220000\",\n"
				+ "                \"Quantity\": 8,\n"
				+ "                \"Unit Price\": 1780.0,\n"
				+ "                \"Taxable Amount\": 14240.0,\n"
				+ "                \"Unit\": \"NO.\",\n"
				+ "                \"Tax Rate\": 18.0,\n"
				+ "                \"Tax Amount\": 1780.0,\n"
				+ "                \"Total Amount\": 16020.0,\n"
				+ "                \"Line Item Number\": \"2\"\n"
				+ "            },\n" + "            {\n"
				+ "                \"Description\": \"H-M XLD Agar Plate MPH031 50PT\",\n"
				+ "                \"HSN number\": \"38210000\",\n"
				+ "                \"Quantity\": 5,\n"
				+ "                \"Unit Price\": 3016.0,\n"
				+ "                \"Taxable Amount\": 15080.0,\n"
				+ "                \"Unit\": \"NO.\",\n"
				+ "                \"Tax Rate\": 18.0,\n"
				+ "                \"Tax Amount\": 3016.0,\n"
				+ "                \"Line Item Number\": \"3\"\n"
				+ "            },\n" + "            {\n"
				+ "                \"Description\": \"HM R2A Agar Plate (Lockable plate) MP962LGT -50PT\",\n"
				+ "                \"HSN number\": \"38221090\",\n"
				+ "                \"Quantity\": 20,\n"
				+ "                \"Unit Price\": 4020.0,\n"
				+ "                \"Unit\": \"NO.\",\n"
				+ "                \"Tax Rate\": 18.0,\n"
				+ "                \"Tax Amount\": 4020.0,\n"
				+ "                \"Line Item Number\": \"4\"\n"
				+ "            },\n" + "            {\n"
				+ "                \"Description\": \"H-M Soyabean Casein Digest Medium LQ027XCK-10X90ML (LQ027XCK-10X90ML)\",\n"
				+ "                \"HSN number\": \"38230000\",\n"
				+ "                \"Quantity\": 27,\n"
				+ "                \"Unit Price\": 2264.0,\n"
				+ "                \"Taxable Amount\": 61128.0,\n"
				+ "                \"Unit\": \"NO.\",\n"
				+ "                \"Tax Rate\": 18.0,\n"
				+ "                \"Tax Amount\": 2264.0,\n"
				+ "                \"Total Amount\": 63392.0,\n"
				+ "                \"Line Item Number\": \"5\"\n"
				+ "            }\n" + "        ]\n" + "    }\n" + "}";

		// Parse the JSON data using Gson
		JsonObject jsonObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonArray lineItems = jsonObject.getAsJsonObject("data")
				.getAsJsonArray("Line-items");

		// Map to hold total amounts by HSN number
		Map<String, BigDecimal> hsnTotalAmounts = new HashMap<>();

		// Iterate through line items and aggregate total amounts by HSN number
		for (JsonElement element : lineItems) {
			JsonObject item = element.getAsJsonObject();
			String hsnNumber = item.get("HSN number").getAsString();
			BigDecimal totalAmount = item.get("Total Amount") != null
					? item.get("Total Amount").getAsBigDecimal()
					: BigDecimal.ZERO;
			hsnTotalAmounts.merge(hsnNumber, totalAmount, BigDecimal::add);
		}

		// Print out the total amounts by HSN number
		for (Map.Entry<String, BigDecimal> entry : hsnTotalAmounts.entrySet()) {
			System.out.println("HSN: " + entry.getKey() + ", Total Amount: "
					+ entry.getValue());
		}

		// Find and print the HSN with the maximum total amount
		String maxHsn = null;
		BigDecimal maxAmount = BigDecimal.ZERO;

		maxHsn = hsnTotalAmounts.entrySet().stream()
				.max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
				.orElse(null);

		System.out.println("HSN with the maximum total amount: " + maxHsn);
	}

	public QrvsPdfValidatorFinalRespDto reconPdfvsQRResp(
			QRValidatorRespDto qrResp, PdfValidatorRespDto pdfResp) {

		List<String> matchReasons = new ArrayList<>();
		List<String> misMatchReasons = new ArrayList<>();
		String reportCategory = null;
		try {
			QrvsPdfValidatorFinalRespDto finalDto = new QrvsPdfValidatorFinalRespDto();
			GSTNDetailEntity gstinDetails = gstinInfoRepository
					.findByGstinAndIsDeleteFalse("29AAAPH9357H000");

			if (gstinDetails == null) {
				finalDto.setErrMsg(
						"Recipient GSTIN/Customer GSTIN is not onboarded in DigiGST.");
				return finalDto;
			}

			//
			// Map<String, Config> configMap = configManager
			// .getConfigs("QRVALIDATOR", "total.tolerance", "DEFAULT");
			//
			// String totalToleranceValue = configMap
			// .get("total.tolerance.value") == null ? "100"
			// : configMap.get("total.tolerance.value").getValue();

			QRInwardEinvoiceTaggingEntity taggEntity = new QRInwardEinvoiceTaggingEntity();

			BigDecimal totalInvTolerance = getTotalValTolLimit(
					gstinDetails.getEntityId());
			populateFinalDto(qrResp, pdfResp, finalDto);

			compareAndAddReason("SGSTIN", finalDto.getSupplierGstinPDF(),
					finalDto.getSupplierGstinQR(), matchReasons,
					misMatchReasons);
			compareAndAddReason("RGSTIN", finalDto.getRecipientGstinPDF(),
					finalDto.getRecipientGstinQR(), matchReasons,
					misMatchReasons);

			Pair<Boolean, String> pdfDocDtls = removeSpecialCharactersAndPrefixZeros(
					finalDto.getDocNoPDF());
			Pair<Boolean, String> qrDocDtls = removeSpecialCharactersAndPrefixZeros(
					finalDto.getDocNoQR());

			if (finalDto.getDocNoQR() != null && finalDto.getDocNoPDF() != null
					&& finalDto.getDocNoPDF()
							.equalsIgnoreCase(finalDto.getDocNoQR())) {
				// Document numbers are exactly matching
				compareAndAddReason("Doc No", finalDto.getDocNoPDF(),
						finalDto.getDocNoQR(), matchReasons, misMatchReasons);
			} else {
				if (pdfDocDtls.getValue0() || qrDocDtls.getValue0()) {
					compareAndAddReason("Doc No(SC)", pdfDocDtls.getValue1(),
							qrDocDtls.getValue1(), matchReasons,
							misMatchReasons);
				} else {
					compareAndAddReason("Doc No", pdfDocDtls.getValue1(),
							qrDocDtls.getValue1(), matchReasons,
							misMatchReasons);
				}
			}
			compareAndAddReason("Doc Date", finalDto.getDocDtPDF(),
					finalDto.getDocDtQR(), matchReasons, misMatchReasons);
			compareAndAddReason("Doc Type", finalDto.getDocTypPDF(),
					finalDto.getDocTypQR(), matchReasons, misMatchReasons);
			compareAndAddReason("Total Invoice Value",
					finalDto.getTotalInvoiceValuePDF(),
					finalDto.getTotalInvoiceValueQR(), totalInvTolerance,
					matchReasons, misMatchReasons);
			compareAndAddReason("HSN", finalDto.getMainHsnCodePDF(),
					finalDto.getMainHsnCodeQR(), matchReasons, misMatchReasons);
			compareAndAddReason("IRN", finalDto.getIrnPDF(),
					finalDto.getIrnQR(), matchReasons, misMatchReasons);
			compareAndAddReason("IRN Date", finalDto.getIrnDatePDF(),
					finalDto.getIrnDateQR() == null ? null
							: finalDto.getIrnDateQR().toLocalDate(),
					matchReasons, misMatchReasons);

			if (finalDto.getSignature() != null
					&& "Valid".equalsIgnoreCase(finalDto.getSignature())) {
				matchReasons.add("Signature");
			} else {
				misMatchReasons.add("Signature");
			}
			if (matchReasons.size() == 10) {
				reportCategory = MATCH;
			} else if (matchReasons.size() == 9
					&& !matchReasons.contains("IRN Date")) {
				reportCategory = MATCH;
			} else {
				reportCategory = MISMATCH;
			}
			finalDto.setReportCategory(reportCategory);
			finalDto.setMatchCount(matchReasons.size());
			finalDto.setMismatchCount(misMatchReasons.size());
			finalDto.setMatchReasons(String.join(", ", matchReasons));
			finalDto.setMismatchReasons(String.join(", ", misMatchReasons));

			if (!Strings.isNullOrEmpty(finalDto.getIrnQR())) {
				taggEntity.setIrn(finalDto.getIrnQR());
				taggEntity.setQrCodeValidated("Yes");
				taggEntity.setQrCodeMatchCount(finalDto.getMatchCount());
				taggEntity.setQrCodeMismatchCount(finalDto.getMismatchCount());
				taggEntity.setIsTagged(false);
				taggEntity.setQrCodeValidationResult(
						finalDto.getReportCategory());
				taggEntity.setQrCodeMismatchAttributes(
						finalDto.getMismatchReasons());
				taggEntity.setMode("QRvsPDF");
				taggEntity.setIsDelete(false);
				taggEntity.setCreatedOn(LocalDateTime.now());
				qrInwardEinvoiceTaggingRepository.updateIsDeleteStatus(
						Arrays.asList(finalDto.getIrnQR()));

				qrInwardEinvoiceTaggingRepository.save(taggEntity);
			}
			return finalDto;
		} catch (Exception ex) {
			String errMsg = "Exception while processing the Recon request.";
			LOGGER.error("Exception in QR Code Validator {}", ex);
			throw new AppException(errMsg);
		}
	}

	private void populateFinalDto(QRValidatorRespDto qrResp,
			PdfValidatorRespDto pdfResp,
			QrvsPdfValidatorFinalRespDto finalDto) {
		finalDto.setRecipientGstinQR(
				truncateValue(qrResp.getBuyerGstin(), 15, 15, true));
		finalDto.setRecipientGstinPDF(
				truncateValue(pdfResp.getCustomerTaxId(), 15, 15, true));
		finalDto.setSupplierGstinQR(
				truncateValue(qrResp.getSellerGstin(), 15, 15, true));
		finalDto.setSupplierGstinPDF(
				truncateValue(pdfResp.getVendorTaxId(), 15, 15, true));
		finalDto.setDocNoQR(truncateValue(qrResp.getDocNo(), 16, 16, false));
		finalDto.setDocNoPDF(
				truncateValue(pdfResp.getInvoiceId(), 16, 16, false));
		finalDto.setDocDtQR(tryConvertUsingFormat(
				truncateValue(qrResp.getDocDt(), 100, 100, false),
				DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		finalDto.setDocDtPDF(tryConvertUsingFormat(
				truncateValue(pdfResp.getInvoiceDate(), 10, 10, false),
				DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		finalDto.setDocTypQR(mapDocumentType(qrResp.getDocTyp()));
		finalDto.setDocTypPDF(mapDocumentType(pdfResp.getDocumentType()));
		finalDto.setTotalInvoiceValueQR(truncateValue(qrResp.getTotInvVal()));
		finalDto.setTotalInvoiceValuePDF(
				truncateValue(pdfUtility.decimalCheck(pdfResp.getInvoiceTotal())));
		finalDto.setItemCountQR(qrResp.getItemCnt());
		finalDto.setMainHsnCodeQR(qrResp.getMainHsnCode());
		finalDto.setMainHsnCodePDF(truncateValue(
				getHsnWithHighestAmount(pdfResp.getLineItems()), 8, 8, true));
		finalDto.setIrnQR(truncateValue(qrResp.getIrn(), 64, 64, true));
		finalDto.setIrnPDF(truncateValue(pdfResp.getIrnNumber(), 64, 64, true));
		finalDto.setIrnDateQR(stringToTime(qrResp.getIrnDt(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		finalDto.setIrnDatePDF(tryConvertUsingFormat(
				truncateValue(pdfResp.getIrnDate(), 10, 10, false),
				DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		finalDto.setSignature(qrResp.getSignature());
		finalDto.setFileName(pdfResp.getFileName());
		finalDto.setValidatedDate(LocalDate.now());
		finalDto.setDeclarationRule48(qrResp.getDeclaration());
	}

	private String truncateValue(String value, int maxlengthTobeTruncated,
			int lengthToBeTruncated, boolean spTrunNeeded) {

		if (Strings.isNullOrEmpty(value)) {
			return null;
		}
		if (spTrunNeeded) {
			String specialCharacters = "[/\\\\.,\\-_|:;#!@%&*\\(\\)'\"\\s]";
			value = value.replaceAll(specialCharacters, "");
		}

		if (value.length() > maxlengthTobeTruncated) {
			return value.substring(0, lengthToBeTruncated);
		}

		return value;
	}

	private BigDecimal truncateValue(BigDecimal value) {

		if (value == null) {
			return null;
		}
		return value.setScale(2, BigDecimal.ROUND_DOWN);

	}

	private void compareAndAddReason(String fieldName, String valuePDF,
			String valueQR, List<String> matchReasons,
			List<String> misMatchReasons) {
		if (Strings.isNullOrEmpty(valuePDF) || Strings.isNullOrEmpty(valueQR)) {
			misMatchReasons.add(fieldName.replace("(SC)", ""));
		} else if (valuePDF.equalsIgnoreCase(valueQR)) {
			matchReasons.add(fieldName);
		} else {
			misMatchReasons.add(fieldName.replace("(SC)", ""));
		}
	}

	private void compareAndAddReason(String fieldName, LocalDate valuePDF,
			LocalDate valueQR, List<String> matchReasons,
			List<String> misMatchReasons) {
		if (valuePDF == null || valueQR == null) {
			misMatchReasons.add(fieldName);
			return;
		}
		int comparisonResult = valuePDF.compareTo(valueQR);
		if (comparisonResult == 0) {
			matchReasons.add(fieldName);
		} else {
			misMatchReasons.add(fieldName);
		}
	}

	private void compareAndAddReason(String fieldName, BigDecimal valuePDF,
			BigDecimal valueQR, BigDecimal toleranceLimit,
			List<String> matchReasons, List<String> misMatchReasons) {
		if (valuePDF == null || valueQR == null) {
			misMatchReasons.add(fieldName);
			return;
		}
		valuePDF = valuePDF.abs();
		valueQR = valueQR.abs();
		BigDecimal difference = valuePDF.subtract(valueQR).abs();
		if (difference.compareTo(BigDecimal.ZERO) == 0) {
			matchReasons.add(fieldName);
		} else if (difference.compareTo(toleranceLimit) <= 0) {
			matchReasons.add(fieldName.concat("(Tolerance)"));
		} else {
			misMatchReasons.add(fieldName);
		}
	}

	private Pair<Boolean, String> removeSpecialCharactersAndPrefixZeros(
			String input) {
		if (Strings.isNullOrEmpty(input)) {
			return new Pair<Boolean, String>(false, "");
		}
		String originalInput = input;
		// Remove special characters (/ - \ |)
		String withoutSpecialChars = input.replaceAll("[/\\\\|\\-]", "");
		// Remove prefix zeros
		String withoutPrefixZeros = withoutSpecialChars.replaceFirst("^0+", "");
		// Check if any changes were made
		boolean changesMade = !originalInput.equals(withoutPrefixZeros);
		return new Pair<Boolean, String>(changesMade,
				changesMade ? withoutPrefixZeros : input);
	}

	private String getHsnWithHighestAmount(
			List<PdfValidatorLineItemRespDto> lineItems) {
		if (lineItems == null || lineItems.isEmpty()) {
			return null;
		}
		Map<String, BigDecimal> hsnTotalAmounts = new HashMap<>();

		for (PdfValidatorLineItemRespDto element : lineItems) {
			BigDecimal currentAmount = element.getAmount() != null
					? pdfUtility.decimalCheck(element.getAmount()).abs() : BigDecimal.ZERO;
			String currentHsnNumber = element.getHsnNumber();

			if (currentAmount != null
					&& !Strings.isNullOrEmpty(currentHsnNumber)) {
				currentHsnNumber = currentHsnNumber.replace(" ", "");

				hsnTotalAmounts.merge(currentHsnNumber, currentAmount,
						BigDecimal::add);

			}
		}
		// Find the HSN with the maximum total amount
		return hsnTotalAmounts.entrySet().stream()
				.max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
				.orElse(null);
	}

	private static LocalDate tryConvertUsingFormat(String docDate,
			DateTimeFormatter dateFormat) {
		try {
			return LocalDate.parse(docDate, dateFormat);
		} catch (Exception e) {
			return null;
		}
	}

	private static LocalDateTime stringToTime(String dateTime,
			DateTimeFormatter formatter) {
		if (Strings.isNullOrEmpty(dateTime)) {
			return null;
		}
		return LocalDateTime.parse(dateTime, formatter);
	}

	private BigDecimal getTotalValTolLimit(Long entityId) {
		String optVal = groupConfigPemtRepo
				.findAnswerByQuestionCodeAndQuestionType("G29", "T");
		if (Strings.isNullOrEmpty(optVal)) {
			return BigDecimal.ZERO;
		} else {
			return new BigDecimal(optVal);
		}
	}

	private String mapDocumentType(String docType) {
		if (!Strings.isNullOrEmpty(docType)) {
			if (docType.contains("CRN") || docType.contains("Credit")) {
				return "CR";
			} else if (docType.contains("DRN") || docType.contains("Debit")) {
				return "DR";
			} else {
				return "INV";
			}
		}
		return null; // or return a default value based on your requirement
	}
}
