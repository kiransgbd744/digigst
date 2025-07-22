package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRInwardEinvoiceTaggingEntity;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRInwardEinvoiceTaggingRepository;
import com.ey.advisory.app.data.services.pdfreader.PDFCommonUtility;
import com.ey.advisory.app.data.services.qrvspdf.PdfValidatorLineItemRespDto;
import com.ey.advisory.app.data.services.qrvspdf.PdfValidatorRespDto;
import com.ey.advisory.app.data.services.qrvspdf.QRValidatorRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.ConfigManager;

import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReconJsonVsPdfUtility {

	private final static String MATCH = "Match";

	private final static String MISMATCH = "MisMatch";

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPemtRepo;


	@Autowired
	@Qualifier("GetIrnDetailsJsonVsPdfReconServiceImpl")
	private GetIrnDetailsJsonVsPdfReconService getIrnDetails;

	@Autowired
	private StatecodeRepository stateRepo;
	
	@Autowired
	private PDFCommonUtility pdfUtility;
	
	@Autowired
	private QRInwardEinvoiceTaggingRepository qrInwardEinvoiceTaggingRepository;

	public JsonPdfValidatorFinalRespDto reconPdfvsJsonResp(
			QRValidatorRespDto qrResp, PdfValidatorRespDto pdfResp) {

		List<String> matchReasons = new ArrayList<>();
		List<String> misMatchReasons = new ArrayList<>();
		String reportCategory = null;
		try {
			JsonPdfValidatorFinalRespDto finalDto = new JsonPdfValidatorFinalRespDto();

			/*
			 * GSTNDetailEntity gstinDetails = gstinInfoRepository
			 * .findByGstinAndIsDeleteFalse(qrResp.getBuyerGstin());
			 */

			/*
			GSTNDetailEntity gstinDetails = gstinInfoRepository
					.findByGstinAndIsDeleteFalse("29AAAPH9357H000");

			if (gstinDetails == null) {
				finalDto.setErrMsg(
						"Recipient GSTIN/Customer GSTIN is not onboarded in DigiGST.");
				return finalDto;
			}
			*/

			JsonValidatorRespDto jsonResp = new JsonValidatorRespDto();

			if (qrResp.getIrn() != null && qrResp.getBuyerGstin() != null) {
				jsonResp = getIrnDetails.getIrnforRecon(qrResp.getIrn(),
						TenantContext.getTenantId(), qrResp.getBuyerGstin());
			}
			LOGGER.debug("jsonResp {} ", jsonResp);

			if (jsonResp.getErrMsg() != null) {
				finalDto.setErrMsg(jsonResp.getErrMsg());
				return finalDto;
			}

			/*
			 * Map<String, Config> configMap = configManager
			 * .getConfigs("QRVALIDATOR", "total.tolerance", "DEFAULT");
			 * 
			 * String totalToleranceValue = configMap
			 * .get("total.tolerance.value") == null ? "100" :
			 * configMap.get("total.tolerance.value").getValue();
			 */
			BigDecimal totalInvTolerance = getTotalValTolLimit("INV_VALUE");

			BigDecimal totalTaxTolerance = getTotalValTolLimit("TAX_VALUE");

			BigDecimal totalIgstTolerance = getTotalValTolLimit("IGST_VALUE");

			BigDecimal totalCgstTolerance = getTotalValTolLimit("CGST_VALUE");

			BigDecimal totalSgstTolerance = getTotalValTolLimit("SGST_VALUE");

			BigDecimal totalCessTolerance = getTotalValTolLimit("CESS_VALUE");

			BigDecimal totalTaxableTolerance = getTotalValTolLimit("TAXABLE_VALUE");

			LOGGER.debug(
					"totalInvTolerance {} totalTaxTolerance {} totalIgstTolerance {} totalCgstTolerance {} totalSgstTolerance {} totalCessTolerance {} totalTaxableTolerance {} ",
					totalInvTolerance, totalTaxTolerance, totalIgstTolerance,
					totalCgstTolerance, totalSgstTolerance, totalCessTolerance,
					totalTaxableTolerance);

			populateFinalDto(jsonResp, pdfResp, finalDto, qrResp);

			compareAndAddReason("SGSTIN", finalDto.getSupplierGstinPDF(),
					finalDto.getSupplierGstinJSON(), matchReasons,
					misMatchReasons);

			compareAndAddReason("RGSTIN", finalDto.getRecipientGstinPDF(),
					finalDto.getRecipientGstinJSON(), matchReasons,
					misMatchReasons);

			Pair<Boolean, String> pdfDocDtls = removeSpecialCharactersAndPrefixZeros(
					finalDto.getDocNoPDF());

			Pair<Boolean, String> jsonDocDtls = removeSpecialCharactersAndPrefixZeros(
					finalDto.getDocNoJSON());

			if (pdfDocDtls.getValue0() || jsonDocDtls.getValue0()) {
				compareAndAddReason("Doc No(SC)", pdfDocDtls.getValue1(),
						jsonDocDtls.getValue1(), matchReasons, misMatchReasons);
			} else {
				compareAndAddReason("Doc No", pdfDocDtls.getValue1(),
						jsonDocDtls.getValue1(), matchReasons, misMatchReasons);
			}
			compareAndAddReason("Doc Date", finalDto.getDocDtPDF(),
					finalDto.getDocDtJSON(), matchReasons, misMatchReasons);

			compareAndAddReason("Doc Type", finalDto.getDocTypPDF(),
					finalDto.getDocTypJSON(), matchReasons, misMatchReasons);

			compareAndAddReason("Total Invoice Value",
					finalDto.getTotalInvoiceValuePDF(),
					finalDto.getTotalInvoiceValueJSON(), totalInvTolerance,
					matchReasons, misMatchReasons);

			compareAndAddReason("HSN", finalDto.getMainHsnCodePDF(),
					finalDto.getMainHsnCodeJSON(), matchReasons,
					misMatchReasons);

			compareAndAddReason("IRN", finalDto.getIrnPDF(),
					finalDto.getIrnJSON(), matchReasons, misMatchReasons);

			compareAndAddReason("IRN Date", finalDto.getIrnDatePDF(),
					finalDto.getIrnDateJSON() == null ? null
							: finalDto.getIrnDateJSON().toLocalDate(),
					matchReasons, misMatchReasons);

			compareAndAddReason("Taxable Value", finalDto.getTaxablePdf(),
					finalDto.getTaxableJSON(), totalTaxableTolerance,
					matchReasons, misMatchReasons);

			compareAndAddReason("IGST", finalDto.getIgstPdf(),
					finalDto.getIgstJSON(), totalIgstTolerance, matchReasons,
					misMatchReasons);

			compareAndAddReason("CGST", finalDto.getCgstPdf(),
					finalDto.getCgstJSON(), totalCgstTolerance, matchReasons,
					misMatchReasons);

			compareAndAddReason("SGST/UTGST", finalDto.getSgstUgstPdf(),
					finalDto.getSgstUgstJSON(), totalSgstTolerance,
					matchReasons, misMatchReasons);

			compareAndAddReason("CESS", finalDto.getCessPdf(),
					finalDto.getCessJSON(), totalCessTolerance, matchReasons,
					misMatchReasons);

			compareAndAddReason("Total Tax", finalDto.getTotalPdf(),
					finalDto.getTotalJSON(), totalTaxTolerance, matchReasons,
					misMatchReasons);

			if (finalDto.getRcmJSON() != null && finalDto.getRcmPDF() != null
					&& finalDto.getRcmPDF()
							.equalsIgnoreCase(finalDto.getRcmJSON())) {
				matchReasons.add("Reverse Charge");
			} else {
				misMatchReasons.add("Reverse Charge");
			}

			if (finalDto.getPosJSON() != null && finalDto.getPosPdf() != null
					&& finalDto.getPosJSON()
							.compareTo(finalDto.getPosPdf()) == 0) {
				matchReasons.add("POS");
			} else {
				misMatchReasons.add("POS");
			}

			if ("Active".equalsIgnoreCase(finalDto.getIrnStatusJSON())) {
				matchReasons.add("IRN Status");
			} else {
				misMatchReasons.add("IRN Status");
			}

			if ("Valid".equalsIgnoreCase(finalDto.getSignature())) {
				matchReasons.add("Signature");
			} else {
				misMatchReasons.add("Signature");
			}

			if (matchReasons.size() == 19) {
				reportCategory = MATCH;
			} else if (misMatchReasons.contains("IRN Status")) {
				reportCategory = MISMATCH;
			} else if (matchReasons.size() == 16
					&& !matchReasons.contains("IRN Date")
					&& !matchReasons.contains("Reverse Charge")) {
				reportCategory = MATCH;
			} else {
				reportCategory = MISMATCH;
			}

			finalDto.setReportCategory(reportCategory);
			finalDto.setMatchCount(matchReasons.size());
			finalDto.setMismatchCount(misMatchReasons.size());
			finalDto.setMatchReasons(String.join(", ", matchReasons));
			finalDto.setMismatchReasons(String.join(", ", misMatchReasons));

			if (!Strings.isNullOrEmpty(finalDto.getIrnJSON())) {
				QRInwardEinvoiceTaggingEntity taggEntity = new QRInwardEinvoiceTaggingEntity();
				taggEntity.setIrn(finalDto.getIrnJSON());
				taggEntity.setQrCodeValidated("Yes");
				taggEntity.setQrCodeMatchCount(finalDto.getMatchCount());
				taggEntity.setQrCodeMismatchCount(finalDto.getMismatchCount());
				taggEntity.setIsTagged(false);
				taggEntity.setQrCodeValidationResult(
						finalDto.getReportCategory());
				taggEntity.setQrCodeMismatchAttributes(
						finalDto.getMismatchReasons());
				taggEntity.setMode("PDFvsJson");
				taggEntity.setIsDelete(false);
				taggEntity.setCreatedOn(LocalDateTime.now());
				qrInwardEinvoiceTaggingRepository.updateIsDeleteStatus(
						Arrays.asList(finalDto.getIrnJSON()));

				qrInwardEinvoiceTaggingRepository.save(taggEntity);
			}
			return finalDto;

		} catch (Exception ex) {
			String errMsg = "Exception while progressing the Recon request.";
			LOGGER.error("Exception in QR/JSON vs PDF Code Validator {}", ex);
			throw new AppException(errMsg);
		}
	}

	private void populateFinalDto(JsonValidatorRespDto jsonResp,
			PdfValidatorRespDto pdfResp, JsonPdfValidatorFinalRespDto finalDto,
			QRValidatorRespDto qrResp) {

		LOGGER.debug("pdfresp - {}", pdfResp);
		LOGGER.debug("jsonResp - {} ", jsonResp);

		finalDto.setRecipientGstinJSON(jsonResp.getRecipientGstin());
		finalDto.setRecipientGstinPDF(
				truncateValue(pdfResp.getCustomerTaxId(), 15, 15, true));

		finalDto.setSupplierGstinJSON(jsonResp.getSupplierGstin());
		finalDto.setSupplierGstinPDF(
				truncateValue(pdfResp.getVendorTaxId(), 15, 15, true));

		finalDto.setDocNoJSON(jsonResp.getDocNum());
		finalDto.setDocNoPDF(
				truncateValue(pdfResp.getInvoiceId(), 16, 16, false));

		finalDto.setDocDtJSON(tryConvertUsingFormat(jsonResp.getDocDate(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		finalDto.setDocDtPDF(tryConvertUsingFormat(
				truncateValue(pdfResp.getInvoiceDate(), 10, 10, false),
				DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		if (jsonResp.getDocType() != null) {
			finalDto.setDocTypJSON(mapDocumentType(jsonResp.getDocType()));
		}
		finalDto.setDocTypPDF(mapDocumentType(pdfResp.getDocumentType()));

		finalDto.setTotalInvoiceValueJSON(jsonResp.getTotalInvValue() != null
				? truncateValue(jsonResp.getTotalInvValue()) : null);
		finalDto.setTotalInvoiceValuePDF(
				truncateValue(pdfUtility.decimalCheck(pdfResp.getInvoiceTotal())));

		finalDto.setItemCountJSON(jsonResp.getItemCnt());
		finalDto.setItemCountPDF(pdfResp.getLineItems().size());

		finalDto.setSignature(qrResp.getSignature());

		finalDto.setIrnJSON(jsonResp.getIrn());
		finalDto.setIrnPDF(truncateValue(pdfResp.getIrnNumber(), 64, 64, true));

		if (jsonResp != null && jsonResp.getIrnDate() != null) {
			finalDto.setIrnDateJSON(stringToTime(
					jsonResp.getIrnDate().toString().replace("T", " "),
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		}
		finalDto.setIrnDatePDF(tryConvertUsingFormat(
				truncateValue(pdfResp.getIrnDate(), 10, 10, false),
				DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		if (jsonResp != null && jsonResp.getIrnCanDate() != null) {

			finalDto.setIrnCancelDtJSON(stringToTime(
					jsonResp.getIrnCanDate() != null ? jsonResp.getIrnCanDate()
							.toString().replace("T", " ") : null,
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		}

		if (jsonResp.getPos() != null) {

			finalDto.setPosJSON(Integer.valueOf(jsonResp.getPos()));
		}
		if (pdfResp.getPlaceOfSupply() != null) {

			if (pdfResp.getPlaceOfSupply().contains("(")) {
				int start = pdfResp.getPlaceOfSupply().indexOf('(');
				int end = pdfResp.getPlaceOfSupply().indexOf(')');

				finalDto.setPosPdf(Integer.valueOf(
						pdfResp.getPlaceOfSupply().substring(start + 1, end)));
			} else {
				finalDto.setPosPdf(Integer.valueOf(stateRepo.findStateCode(
						pdfResp.getPlaceOfSupply().toLowerCase())));
			}
		}

		finalDto.setTaxableJSON(jsonResp.getTaxableValue() != null
				? truncateValue(jsonResp.getTaxableValue()) : null);
		finalDto.setTaxablePdf(truncateValue(pdfUtility.decimalCheck(pdfResp.getSubTotal())));

		finalDto.setIgstJSON(jsonResp.getIGst() != null
				? truncateValue(jsonResp.getIGst()) : null);
		finalDto.setIgstPdf(truncateValue(pdfUtility.decimalCheck(pdfResp.getIgstAmount())));

		finalDto.setCgstJSON(jsonResp.getCGst() != null
				? truncateValue(jsonResp.getCGst()) : null);
		finalDto.setCgstPdf(truncateValue(pdfUtility.decimalCheck(pdfResp.getCgstAmount())));

		finalDto.setSgstUgstJSON(jsonResp.getSGst() != null
				? truncateValue(jsonResp.getSGst()) : null);
		finalDto.setSgstUgstPdf(truncateValue(pdfUtility.decimalCheck(pdfResp.getSgstAmount())));

		finalDto.setCessJSON(jsonResp.getCess() != null
				? truncateValue(jsonResp.getCess()) : null);
		finalDto.setCessPdf(truncateValue(pdfUtility.decimalCheck(pdfResp.getCessAmount())));

		finalDto.setTotalJSON(jsonResp.getTotalTax() != null
				? truncateValue(jsonResp.getTotalTax()) : null);
		finalDto.setTotalPdf(truncateValue(pdfUtility.decimalCheck(pdfResp.getTotalTax())));

		finalDto.setRcmJSON(jsonResp.getRcm());
		finalDto.setRcmPDF(pdfResp.getRCm() != null
				? pdfResp.getRCm().equalsIgnoreCase("YES")
						|| pdfResp.getRCm().equalsIgnoreCase("Y") ? "Y" : "N"
				: "N");

		// purchase order number

		if (jsonResp.getQuantity() != null) {
			finalDto.setQuantityJSON(Strings.join(
					jsonResp.getQuantity().stream().map(o -> String.valueOf(o))
							.collect(Collectors.toList()),
					"|"));
		}
		finalDto.setQuantityPDF(
				getPipeSeperatedValues(pdfResp.getLineItems(), "Quantity"));

		if (jsonResp.getUnitPrice() != null) {

			finalDto.setUnitPriceJSON(Strings.join(
					jsonResp.getUnitPrice().stream().map(o -> String.valueOf(o))
							.collect(Collectors.toList()),
					"|"));
		}
		finalDto.setUnitPricePDF(
				getPipeSeperatedValues(pdfResp.getLineItems(), "UnitPrice"));

		if (jsonResp.getLineItemAmt() != null) {
			finalDto.setLineItemJSON(Strings.join(jsonResp.getLineItemAmt()
					.stream().map(o -> String.valueOf(o))
					.collect(Collectors.toList()), "|"));
		}
		finalDto.setLineItemPDF(
				getPipeSeperatedValues(pdfResp.getLineItems(), "LineItemAmt"));

		finalDto.setPurchaseOrderPdf(pdfResp.getPurchaseOrder());
		finalDto.setPurchaseOrderJson(jsonResp.getPurchaseOrderNo());

		if (jsonResp.getMainHsnCode() != null) {
			finalDto.setMainHsnCodeJSON(
					String.valueOf(findMaxInteger(jsonResp.getMainHsnCode())));
		}
		finalDto.setMainHsnCodePDF(truncateValue(
				getHsnWithHighestAmount(pdfResp.getLineItems()), 8, 8, true));
		finalDto.setIrnStatusJSON(jsonResp.getIrnStatus());

		finalDto.setJsonResponse(jsonResp.getIrnJsonPayload());

		finalDto.setFileName(pdfResp.getFileName());
		finalDto.setValidatedDate(LocalDate.now());
		finalDto.setDeclarationRule48(qrResp.getDeclaration());
	}

	private void compareAndAddReason(String fieldName, String valuePDF,
			String valueQR, List<String> matchReasons,
			List<String> misMatchReasons) {
		if (Strings.isNullOrEmpty(valuePDF) || Strings.isNullOrEmpty(valueQR)) {
			misMatchReasons.add(fieldName);
		} else if (valuePDF.equalsIgnoreCase(valueQR)) {
			matchReasons.add(fieldName);
		} else {
			misMatchReasons.add(fieldName);
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
		BigDecimal maxAmount = null;
		String hsnWithMaxAmount = null;
		if (lineItems.isEmpty() || lineItems == null) {
			return null;
		}
		for (PdfValidatorLineItemRespDto element : lineItems) {
			BigDecimal currentAmount = element.getAmount() != null
					? pdfUtility.decimalCheck(element.getAmount()).abs() : null;
			String currentHsnNumber = element.getHsnNumber();
			if (currentAmount != null
					&& !Strings.isNullOrEmpty(currentHsnNumber)) {
				currentAmount = pdfUtility.decimalCheck(element.getAmount()).abs();
				currentHsnNumber = element.getHsnNumber().replace(" ", "");
				if (currentAmount != null && currentHsnNumber != null) {
					if (maxAmount == null
							|| currentAmount.compareTo(maxAmount) > 0) {
						maxAmount = currentAmount;
						hsnWithMaxAmount = currentHsnNumber;
					}
				}
			}
		}
		return hsnWithMaxAmount;
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
		System.out.println(dateTime.length());

		if (dateTime.length() == 16) {
			return LocalDateTime.parse(dateTime,
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		} else
			return LocalDateTime.parse(dateTime, formatter);
	}

	private BigDecimal getTotalValTolLimit(String identifier) {

		String optVal = null;
		switch (identifier) {
		case "INV_VALUE":
			optVal = groupConfigPemtRepo.findAnswerByQuestionCodeAndQuestionType("G29","T");
			break;

		case "CGST_VALUE":
			optVal = groupConfigPemtRepo.findAnswerByQuestionCodeAndQuestionType("G32","T");
			break;

		case "TAX_VALUE":
			optVal = groupConfigPemtRepo.findAnswerByQuestionCodeAndQuestionType("G30","T");
			break;

		case "IGST_VALUE":
			optVal = groupConfigPemtRepo.findAnswerByQuestionCodeAndQuestionType("G31","T");
			break;

		case "SGST_VALUE":
			optVal = groupConfigPemtRepo.findAnswerByQuestionCodeAndQuestionType("G33","T");
			break;

		case "CESS_VALUE":
			optVal =groupConfigPemtRepo.findAnswerByQuestionCodeAndQuestionType("G34","T");
			break;

		case "TAXABLE_VALUE":
			optVal = groupConfigPemtRepo.findAnswerByQuestionCodeAndQuestionType("G35","T");
			break;

		}
		if (Strings.isNullOrEmpty(optVal)) {
			return BigDecimal.ZERO;
		} else {
			return new BigDecimal(optVal);
		}
	}

	private String mapDocumentType(String docType) {
		if (!Strings.isNullOrEmpty(docType)) {
			if (docType.contains("CRN") || docType.contains("Credit")
					|| docType.contains("CREDIT")) {
				return "CR";
			} else if (docType.contains("DBN") || docType.contains("Debit")
					|| docType.contains("DEBIT")) {
				return "DR";
			} else {
				return "INV";
			}
		}
		return null; // or return a default value based on your requirement
	}

	private String getPipeSeperatedValues(
			List<PdfValidatorLineItemRespDto> list, String identifier) {
		List<String> values = new ArrayList<>();

		if (list != null) {
			switch (identifier) {
			case "Quantity":
				values = list.stream()
						.map(o -> String.valueOf(
								o.getQuantity() != null ? o.getQuantity() : 0))
						.collect(Collectors.toList());
				break;

			case "UnitPrice":
				values = list.stream()
						.map(o -> String.valueOf(o.getUnitPrice() != null
								? o.getUnitPrice() : 0))
						.collect(Collectors.toList());
				break;

			case "LineItemAmt":
				values = list.stream()
						.map(o -> String.valueOf(
								o.getAmount() != null ? o.getAmount() : 0))
						.collect(Collectors.toList());
				break;

			}
		}

		if (!values.isEmpty()) {
			return Strings.join(values, "|");
		} else
			return null;

	}

	private int findMaxInteger(List<String> integerStrings) {

		if (integerStrings.isEmpty()) {
			return 0;
		}
		List<Integer> integers = integerStrings.stream().map(Integer::valueOf)
				.collect(Collectors.toList());

		// Find the maximum integer
		return integers.stream().max(Integer::compareTo).orElse(0); // Default
																	// value if
																	// the list
																	// is empty
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

	public static void main(String[] args) {
		String docDate = "2023-11-28";

		System.out.println(tryConvertUsingFormat(docDate,
				DateTimeFormatter.ofPattern("yyyy-MM-dd")));
	}
}
