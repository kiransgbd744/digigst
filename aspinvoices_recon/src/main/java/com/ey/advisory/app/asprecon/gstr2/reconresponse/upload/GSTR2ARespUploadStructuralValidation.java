package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.time.LocalDate;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("GSTR2ARespUploadStructuralValidation")
public class GSTR2ARespUploadStructuralValidation {

	private static final String UPLOAD = "Upload";
	private static final String INVALID = "Invalid %s Doc Date.";
	private static final String ERROR_CODE = "E";
	private static final String A2 = "2A";
	private static final String PR = "PR";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EhcacheGstinTaxperiod")
	private EhcacheGstinTaxperiod ehcachegstinTaxPeriod;

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	public void rowDataValidation(List<ProcessingResult> validationResult,
			GSTR2AAutoReconRespUploadDTO rowData, Set<String> activeDocNo2A,
			Set<String> activeDocNoPR, Set<String> uniqueImsKeyIds,
			boolean isImsValidationReq, boolean isGstr2AprValidationReq) {

		// 2apr validation
		if (isGstr2AprValidationReq) {
			isResponseValid(rowData.getUserResponse(), validationResult);

			if (Strings.isNullOrEmpty(rowData.getDocumentNumber2A())
					&& Strings.isNullOrEmpty(rowData.getBillOfEntry2A())
					&& Strings.isNullOrEmpty(rowData.getBillOfEntryDate2A())) {
				// No Table Type Validation has to do for above condition
			} else {
				// cross tagging
				isTableTypeValid(rowData.getTableType2A(), validationResult,
						rowData.getTaxPeriodPR(),
						rowData.getInvoiceKeyUploadPR(), PR);
			}

//			isDateValidValidation(rowData.getGSTR3BFilingDate(),
//					"GSTR3B-Filing Date", validationResult);

//			isDateValidValidation(rowData.getAccountingVoucherDate(),
//					"Accounting Voucher Date", validationResult);
//
//			isDateValidValidation(rowData.getAdjustementReferenceDate(),
//					"Adjustement Reference Date", validationResult);

//			isDateValidValidation(rowData.getGenerationDate2B(),
//					"GSTR2B Generation Date", validationResult);
//
//			isDateValidValidation(rowData.getGenerationDate2A(),
//					"GSTR2A Generation Date", validationResult);

//			isDateValidValidation(rowData.getGSTR1FilingDate(),
//					"GSTR1-Filing Date", validationResult);
//
//			isDateValidValidation(rowData.getCancellationDate(),
//					"SupplierGSTIN-Cancellation Date", validationResult);

//			isDateValidValidation(rowData.getOrgDocDate2A(), "OrgDoc Date (2A)",
//					validationResult);
//
//			isDateValidValidation(rowData.getOrgDocDatePR(),
//					"OrgDoc Date (PR)	", validationResult);

//			isDateValidValidation(rowData.getBoeReferenceDate(),
//					"BOE-Reference Date (2A)", validationResult);

//			isDateValidValidation(rowData.getGlPostingDate(), "GLPosting Date",
//					validationResult);

//			isDateValidValidation(rowData.getCustomerPORefDate(),
//					"Customer POReference Date	", validationResult);
//
//			isDateValidValidation(rowData.getEWayBillDate(), "EWB Date",
//					validationResult);
//
//			isDateValidValidation(rowData.getIrnDate2A(), "IRN Date (2A)",
//					validationResult);
//
//			isDateValidValidation(rowData.getIrnDatePR(), "IRN Date (PR)",
//					validationResult);
//
//			isDateValidValidation(rowData.getReconGeneratedDate(),
//					"Recon Generated Date", validationResult);
//			isDateValidValidation(rowData.getReverseIntegratedDate(),
//					"Reverse Integrated Date", validationResult);

			/*
			 * if(Strings.isNullOrEmpty(rowData.getBillOfEntry2A()) &&
			 * Strings.isNullOrEmpty(rowData.getBillOfEntryDate2A()) ||
			 * Strings.isNullOrEmpty(rowData.getDocumentNumber2A()) &&
			 * Strings.isNullOrEmpty(rowData.getDocumentDate2A())){ //No Supply
			 * Type Validation has to do for above condition } else {
			 * isSupplyTypeValid(rowData.getSupplyTypePR(), validationResult); }
			 */

			if (!Strings.isNullOrEmpty(rowData.getSupplyTypePR())
					&& !Strings.isNullOrEmpty(rowData.getRecipientGstinPR())) {
				if (!Arrays.asList("IMPG", "SEZG", "TAX", "SEZS", "DTA", "CBW",
						"DXP").contains(rowData.getSupplyTypePR()))
					isSupplyTypeValid(rowData.getSupplyTypePR(),
							validationResult, Stream.of("IMPG", "SEZG", "TAX",
									"SEZS", "DTA", "CBW", "DXP"));

				else if (Arrays.asList("IMPG")
						.contains(rowData.getSupplyTypePR())
						&& (Strings.isNullOrEmpty(rowData.getBillOfEntryPR())
								|| Strings.isNullOrEmpty(
										rowData.getBillOfEntryDatePR()))) {

					ProcessingResult result = new ProcessingResult(UPLOAD,
							ERROR_CODE,
							"Bill of Entry Number(PR) Does Not Exist In DigiGST Master");
					validationResult.add(result);

				} else if (Arrays.asList("TAX", "SEZS", "DTA", "CBW", "DXP")
						.contains(rowData.getSupplyTypePR())
						&& (Strings.isNullOrEmpty(rowData.getSupplierGstinPR())
								|| Strings.isNullOrEmpty(
										rowData.getRecipientGstinPR())
								|| Strings.isNullOrEmpty(rowData.getDocTypePR())
								|| Strings.isNullOrEmpty(
										rowData.getDocumentNumberPR())
								|| Strings.isNullOrEmpty(
										rowData.getDocumentDatePR()))) {

					ProcessingResult result = new ProcessingResult(UPLOAD,
							ERROR_CODE,
							"Document Number(PR) Does Not Exist In DigiGST Master");
					validationResult.add(result);
				}
			} else if (Strings.isNullOrEmpty(rowData.getSupplyTypePR())
					&& !Strings.isNullOrEmpty(rowData.getRecipientGstinPR())) {
				isSupplyTypeValid(rowData.getSupplyTypePR(), validationResult,
						Stream.of("IMPG", "SEZG", "TAX", "SEZS", "DTA", "CBW",
								"DXP"));
			}

			if (Arrays
					.asList("GETGSTR2A_IMPG_HEADER", "GETGSTR2A_IMPGSEZ_HEADER")
					.contains(rowData.getTableType2A())
					&& !Strings.isNullOrEmpty(rowData.getRecipientGstin2A())
					&& (Strings.isNullOrEmpty(rowData.getBillOfEntry2A())
							|| Strings.isNullOrEmpty(
									rowData.getBillOfEntryDate2A()))) {

				ProcessingResult result = new ProcessingResult(UPLOAD,
						ERROR_CODE,
						"Bill of Entry Number(2A) Does Not Exist In DigiGST Master");
				validationResult.add(result);

			} else if (!Arrays
					.asList("GETGSTR2A_IMPG_HEADER", "GETGSTR2A_IMPGSEZ_HEADER")
					.contains(rowData.getTableType2A())
					&& !Strings.isNullOrEmpty(rowData.getRecipientGstin2A())
					&& (Strings.isNullOrEmpty(rowData.getSupplierGstin2A())
							|| Strings.isNullOrEmpty(
									rowData.getRecipientGstin2A())
							|| Strings.isNullOrEmpty(rowData.getDocType2A())
							|| Strings.isNullOrEmpty(
									rowData.getDocumentNumber2A())
							|| Strings.isNullOrEmpty(
									rowData.getDocumentDate2A()))) {

				ProcessingResult result = new ProcessingResult(UPLOAD,
						ERROR_CODE,
						"Document Number(2A) Does Not Exist In DigiGST Master");
				validationResult.add(result);
			}

			if ((!Strings.isNullOrEmpty(rowData.getBillOfEntryPR())
					&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR())
					&& (!Strings.isNullOrEmpty(rowData.getSupplyTypePR())
							&& rowData.getSupplyTypePR()
									.equalsIgnoreCase("IMPG")
							|| !Strings.isNullOrEmpty(rowData.getSupplyTypePR())
									&& rowData.getSupplyTypePR()
											.equalsIgnoreCase("SEZG")))
					|| (!Strings.isNullOrEmpty(rowData.getBillOfEntry2A())
							&& !Strings.isNullOrEmpty(
									rowData.getBillOfEntryDate2A()))) {

				LOGGER.debug(rowData.toString());
				crossTagging(rowData, validationResult);
				if (Strings.isNullOrEmpty(rowData.getSupplierGstin2A())
						&& Strings
								.isNullOrEmpty(rowData.getSupplierGstinPR())) {
					LOGGER.debug("Inside IMPG");
					if (!Strings.isNullOrEmpty(rowData.getBillOfEntry2A())
							&& !Strings.isNullOrEmpty(
									rowData.getBillOfEntryDate2A())) {
						LOGGER.debug("Inside 2A Side");
						isResponseRemarksValid(rowData.getResponseRemarks(),
								validationResult);
						isBillOfEntry2AValid(rowData.getBillOfEntry2A(),
								validationResult);
						isBillOfEntryDate2AValid(rowData.getBillOfEntryDate2A(),
								validationResult);
						isAmountValid(rowData.getTaxableValue2A(),
								"TaxableValue2A ", validationResult);
						isAmountValid(rowData.getTotalTax2A(), "TotalTax2A ",
								validationResult);
						isAmountValid(rowData.getInvoiceValue2A(),
								"InvoiceValue2A ", validationResult);
					} else if (!Strings.isNullOrEmpty(rowData.getSupplyTypePR())
							&& rowData.getSupplyTypePR()
									.equalsIgnoreCase("IMPG")
							|| !Strings.isNullOrEmpty(rowData.getSupplyTypePR())
									&& rowData.getSupplyTypePR()
											.equalsIgnoreCase("SEZG")) {
						LOGGER.debug("Inside PR Side");
						isResponseRemarksValid(rowData.getResponseRemarks(),
								validationResult);
						isBillOfEntryPRValid(rowData.getBillOfEntryPR(),
								validationResult);
						isBillOfEntryDatePRValid(rowData.getBillOfEntryDatePR(),
								validationResult);
						isAmountValid(rowData.getTaxableValuePR(),
								"TaxableValuePR ", validationResult);
						isAmountValid(rowData.getTotalTaxPR(), "TotalTaxPR ",
								validationResult);
						isAmountValid(rowData.getInvoiceValuePR(),
								"InvoiceValuePR ", validationResult);
					} else {
						LOGGER.debug("Inside IMPG ALL");
						isResponseRemarksValid(rowData.getResponseRemarks(),
								validationResult);
						isBillOfEntry2AValid(rowData.getBillOfEntry2A(),
								validationResult);
						isBillOfEntryDate2AValid(rowData.getBillOfEntryDate2A(),
								validationResult);
						isBillOfEntryPRValid(rowData.getBillOfEntryPR(),
								validationResult);
						isBillOfEntryDatePRValid(rowData.getBillOfEntryDatePR(),
								validationResult);
						isAmountValid(rowData.getTaxableValue2A(),
								"TaxableValue2A ", validationResult);
						isAmountValid(rowData.getTaxableValuePR(),
								"TaxableValuePR ", validationResult);
						isAmountValid(rowData.getTotalTax2A(), "TotalTax2A ",
								validationResult);
						isAmountValid(rowData.getTotalTaxPR(), "TotalTaxPR ",
								validationResult);
						isAmountValid(rowData.getInvoiceValue2A(),
								"InvoiceValue2A ", validationResult);
						isAmountValid(rowData.getInvoiceValuePR(),
								"InvoiceValuePR ", validationResult);
					}
				} else if ((!Strings.isNullOrEmpty(rowData.getSupplierGstin2A())
						|| !Strings
								.isNullOrEmpty(rowData.getSupplierGstinPR()))) {
					LOGGER.debug("Inside IMPGSEZ");
					if (!Strings.isNullOrEmpty(rowData.getBillOfEntry2A())
							&& !Strings.isNullOrEmpty(
									rowData.getBillOfEntryDate2A())) {
						LOGGER.debug("Inside 2A Side");
						isResponseRemarksValid(rowData.getResponseRemarks(),
								validationResult);
						isBillOfEntry2AValid(rowData.getBillOfEntry2A(),
								validationResult);
						isBillOfEntryDate2AValid(rowData.getBillOfEntryDate2A(),
								validationResult);
						isAmountValid(rowData.getTaxableValue2A(),
								"TaxableValue2A ", validationResult);
						isAmountValid(rowData.getTotalTax2A(), "TotalTax2A ",
								validationResult);
						isAmountValid(rowData.getInvoiceValue2A(),
								"InvoiceValue2A ", validationResult);
					} else if (!Strings.isNullOrEmpty(rowData.getSupplyTypePR())
							&& rowData.getSupplyTypePR()
									.equalsIgnoreCase("IMPG")
							|| !Strings.isNullOrEmpty(rowData.getSupplyTypePR())
									&& rowData.getSupplyTypePR()
											.equalsIgnoreCase("SEZG")) {
						LOGGER.debug("Inside PR Side");
						isResponseRemarksValid(rowData.getResponseRemarks(),
								validationResult);
						isBillOfEntryPRValid(rowData.getBillOfEntryPR(),
								validationResult);
						isBillOfEntryDatePRValid(rowData.getBillOfEntryDatePR(),
								validationResult);
						isAmountValid(rowData.getTaxableValuePR(),
								"TaxableValuePR ", validationResult);
						isAmountValid(rowData.getTotalTaxPR(), "TotalTaxPR ",
								validationResult);
						isAmountValid(rowData.getInvoiceValuePR(),
								"InvoiceValuePR ", validationResult);
					} else {
						LOGGER.debug("Inside IMPGEZ ALL");
						isResponseRemarksValid(rowData.getResponseRemarks(),
								validationResult);
						isBillOfEntry2AValid(rowData.getBillOfEntry2A(),
								validationResult);
						isBillOfEntryDate2AValid(rowData.getBillOfEntryDate2A(),
								validationResult);
						isBillOfEntryPRValid(rowData.getBillOfEntryPR(),
								validationResult);
						isBillOfEntryDatePRValid(rowData.getBillOfEntryDatePR(),
								validationResult);
						isAmountValid(rowData.getTaxableValue2A(),
								"TaxableValue2A ", validationResult);
						isAmountValid(rowData.getTaxableValuePR(),
								"TaxableValuePR ", validationResult);
						isAmountValid(rowData.getTotalTax2A(), "TotalTax2A ",
								validationResult);
						isAmountValid(rowData.getTotalTaxPR(), "TotalTaxPR ",
								validationResult);
						isAmountValid(rowData.getInvoiceValue2A(),
								"InvoiceValue2A ", validationResult);
						isAmountValid(rowData.getInvoiceValuePR(),
								"InvoiceValuePR ", validationResult);
					}

				}
			} else {
				LOGGER.debug("Inside Main Validation");
				isSupplierGstinValid(rowData.getRecipientGstin2A(),
						rowData.getInvoiceKeyUploadA2(), A2, validationResult);

				isSupplierGstinValid(rowData.getSupplierGstinPR(),
						rowData.getInvoiceKeyUploadPR(), PR, validationResult);

				LocalDate docDatePR = isDateValid(rowData.getDocumentDatePR(),
						rowData.getInvoiceKeyUploadPR(), PR, validationResult);

				isDocNoPRValid(rowData.getDocumentNumberPR(),
						rowData.getInvoiceKeyUploadPR(), validationResult,
						activeDocNoPR, docDatePR);

				isDocTypePRValid(rowData.getDocTypePR(),
						rowData.getInvoiceKeyUploadPR(), validationResult);

				isDocType2AValid(rowData.getDocType2A(),
						rowData.getInvoiceKeyUploadA2(), validationResult);

				LocalDate docDate2A = isDateValid(rowData.getDocumentDate2A(),
						rowData.getInvoiceKeyUploadA2(), A2, validationResult);

				isDocNo2AValid(rowData.getDocumentNumber2A(),
						rowData.getInvoiceKeyUploadA2(), activeDocNo2A,
						validationResult, docDate2A, rowData.getUserResponse());
			}

			isRevChrgPRValid(rowData.getReverseChargeFlagPR(),
					rowData.getUserResponse(), validationResult);

			is2APRTaxPeriodValid(rowData.getTaxPeriod2A(),
					rowData.getInvoiceKeyUploadA2(), rowData.getUserResponse(),
					validationResult, A2);// 2ATaxperiod

			isRecipientGstinValid(rowData.getRecipientGstin2A(),
					rowData.getInvoiceKeyUploadA2(), A2, validationResult);

			is2APRTaxPeriodValid(rowData.getTaxPeriodPR(),
					rowData.getInvoiceKeyUploadPR(), rowData.getUserResponse(),
					validationResult, PR);// PRTaxperiod

			isRecipientGstinValid(rowData.getRecipientGstinPR(),
					rowData.getInvoiceKeyUploadPR(), PR, validationResult);

			isAmountValid(rowData.getAvailableIGST(), "AvailableIGST ",
					validationResult);
			isAmountValid(rowData.getAvailableCGST(), "AvailableCGST ",
					validationResult);
			isAmountValid(rowData.getAvailableSGST(), "AvailableSGST ",
					validationResult);
			isAmountValid(rowData.getAvailableCESS(), "AvailableCESS ",
					validationResult);

			if (is3BTaxPeriodValid(rowData.getUserResponse())) {
				isITCReversalIdentifierValid(rowData.getITCReversalIdentifier(),
						validationResult);
			}
			// isReconIDValid(rowData.getRequestID(), validationResult);

		}
		// Ims validation
		if (isImsValidationReq) {
			checkForImsValidResponse(rowData.getUserIMSResponse(),
					validationResult);
			checkForImsUniqueId(rowData.getImsUniqueID(),
					validationResult);
			checkForDuplicateImsUniqueId(rowData.getImsUniqueID(),
					validationResult, uniqueImsKeyIds);
		}

	}

	private void checkForImsValidResponse(String imsUserResponse,
			List<ProcessingResult> validationResult) {
		String errMsg = String.format("Invalid Action Response");

		if (!isPresent(imsUserResponse)) {

			ProcessingResult result = new ProcessingResult("UPLOAD", "ER103",
					errMsg);

			validationResult.add(result);
			return;
		}
		String response = (imsUserResponse).toString().trim();
		if (Stream.of("A", "P", "R", "Accept", "Reject", "Pending","No Action", "N")
				.noneMatch(response::equalsIgnoreCase)) {
			ProcessingResult result = new ProcessingResult("UPLOAD", "ER103",
					errMsg);
			validationResult.add(result);

		}
	}
	
	private void checkForImsUniqueId(String uniqueIdIms,
			List<ProcessingResult> validationResult) {
		String errMsg = String.format("Invalid IMS UniqueID");

		if (!isPresent(uniqueIdIms)) {

			ProcessingResult result = new ProcessingResult("UPLOAD", "ER104",
					errMsg);

			validationResult.add(result);
			return;
		}
	}

	private void checkForDuplicateImsUniqueId(String uniqueIdIms,
			List<ProcessingResult> validationResult,
			Set<String> uniqueImsKeyIds) {
		String errMsg = String.format("Duplicate UniqueID");

		if (!Strings.isNullOrEmpty(uniqueIdIms)) {
			String response = (uniqueIdIms).toString().trim();
			if (uniqueImsKeyIds != null
					&& uniqueImsKeyIds.contains(response.toUpperCase())) {
				ProcessingResult result = new ProcessingResult("UPLOAD",
						"ER104", errMsg);
				validationResult.add(result);

			} else {
				uniqueImsKeyIds.add(response.toUpperCase());
			}
		}
	}

	private void isBillOfEntry2AValid(String billOfEntry2A,
			List<ProcessingResult> validationResult) {

		if (!isPresent(billOfEntry2A)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					"Bill of Entry Number (2A) format is incorrect");
			validationResult.add(result);
			return;
		}

		billOfEntry2A = removeQuotes(billOfEntry2A);

		if (billOfEntry2A.length() > 16 && !billOfEntry2A.matches("[0-9]+")) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					"Bill of Entry Number (2A) format is incorrect");
			validationResult.add(result);
			return;
		}
	}

	private void isBillOfEntryDate2AValid(String billOfEntryDate2A,
			List<ProcessingResult> validationResult) {

		String errMsg = "Bill of Entry Date (2A) format is incorrect";

		if (!isPresent(billOfEntryDate2A)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		LocalDate dateInstance = DateFormatForStructuralValidatons
				.parseObjToDate(billOfEntryDate2A.trim());

		if (dateInstance == null || (dateInstance.getMonthValue() > 12)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

	}

	private void isITCReversalIdentifierValid(String itcReversalIdentifier,
			List<ProcessingResult> validationResult) {

		String errMsg = "Invalid ITC Reversal Identifier, Only T1/T2/T3/T4/Blank"
				+ " responses are allowed";
		if (!isPresent(itcReversalIdentifier)) {
			return;
		}

		if (Stream.of("T1", "T2", "T3", "T4")
				.noneMatch(itcReversalIdentifier.trim()::equalsIgnoreCase)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;

		}
	}

	private void isBillOfEntryDatePRValid(String billOfEntryDatePR,
			List<ProcessingResult> validationResult) {

		String errMsg = "Bill of Entry Date (PR) format is incorrect";

		if (!isPresent(billOfEntryDatePR)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		LocalDate dateInstance = DateFormatForStructuralValidatons
				.parseObjToDate(billOfEntryDatePR.trim());

		if (dateInstance == null || (dateInstance.getMonthValue() > 12)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

	}

	private void isBillOfEntryPRValid(String billOfEntryPR,
			List<ProcessingResult> validationResult) {

		if (!isPresent(billOfEntryPR)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					"Bill of Entry Number (PR) format is incorrect");
			validationResult.add(result);
			return;
		}

		billOfEntryPR = removeQuotes(billOfEntryPR);

		if (billOfEntryPR.length() > 16 && !billOfEntryPR.matches("[0-9]+")) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					"Bill of Entry Number (PR) format is incorrect");
			validationResult.add(result);
			return;
		}

	}

	private void isResponseRemarksValid(String responseRemarks,
			List<ProcessingResult> validationResult) {
		String errMsg = "The Response remarks is greater than 500 characters";

		if (responseRemarks != null && responseRemarks.length() > 500) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

	}

	private void isTableTypeValid(String tableType,
			List<ProcessingResult> validationResult, String retPeriod,
			String invKeyUploaded, String taxPeriodType) {

		if (!isValidationrequired(retPeriod, invKeyUploaded, taxPeriodType)) {
			return;
		}
		String errMsg = "Table Type (2A) is blank or doesn't have correct value";
		if (!isPresent(tableType)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		if (Stream
				.of("GETGSTR2A_B2B_HEADER", "GETGSTR2A_B2BA_HEADER",
						"GETGSTR2A_CDN_HEADER", "GETGSTR2A_CDNA_HEADER",
						"GETGSTR6A_B2B_HEADER", "GETGSTR6A_B2BA_HEADER",
						"GETGSTR6A_CDN_HEADER", "GETGSTR6A_CDNA_HEADER",
						"GETGSTR2A_IMPG_HEADER", "GETGSTR2A_IMPGSEZ_HEADER",
						"GETGSTR2A_ISD_HEADER", "GETGSTR2A_ISDA_HEADER",
						"GETGSTR2A_ECOM_HEADER", "GETGSTR2A_ECOM_HEADER",
						"GETGSTR2A_ECOMA_HEADER", "GETGSTR2A_ECOMA_HEADER")
				.noneMatch(tableType.trim()::equalsIgnoreCase)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;

		}

	}

	private void isResponseValid(String response,
			List<ProcessingResult> validationResult) {

		String errMsg = "Response is not uploaded in predefined format";

		if (!isPresent(response)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		if (Stream.of("LOCK", "UNLOCK", "LOCK2")
				.noneMatch(response.trim()::equalsIgnoreCase)) {

			if (response.contains("'")) {
				response = response.replace("'", "");
			}
			if (StringUtils.isNumeric(response.trim())) {
				is3BTaxPeriodRespValid(response.trim(), validationResult);
			} else {
				ProcessingResult result = new ProcessingResult(UPLOAD,
						ERROR_CODE, errMsg);
				validationResult.add(result);

			}

		}

	}

	private void isRevChrgPRValid(String revChrgPR, String userResponse,
			List<ProcessingResult> validationResult) {

		String errMsg = "Reverse Charge Flag(PR) - Y records cannot be locked under GSTR 3B";

		if (!isPresent(revChrgPR)) {

			return;
		}

		if ("Y".equalsIgnoreCase(revChrgPR)
				&& is3BTaxPeriodValid(userResponse)) {

			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);

		}

	}

	private static boolean is3BTaxPeriodRespValid(String retPeriod,
			List<ProcessingResult> validationResult) {

		String errMsg = "3B Tax Period value is not in MMYYYY format";
		boolean isValid = true;
		if (!isPresent(retPeriod)) {
			return false;
		}

		if (retPeriod.contains("'")) {
			retPeriod = retPeriod.replace("'", "");
		}

		String taxPeriod = retPeriod.trim();

		if (!taxPeriod.matches("[0-9]+") || taxPeriod.length() != 6) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return false;

		}

		int month = Integer.parseInt(taxPeriod.substring(0, 2));
		if (taxPeriod.length() != 6 || month > 12 || month == 00) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return false;
		}

		return isValid;
	}

	private boolean is3BTaxPeriodValid(String retPeriod) {

		boolean isValid = true;
		if (!isPresent(retPeriod)) {
			return false;
		}

		if (retPeriod.contains("'")) {
			retPeriod = retPeriod.replace("'", "");
		}

		String taxPeriod = retPeriod.trim();

		if (!taxPeriod.matches("[0-9]+") || taxPeriod.length() != 6) {
			return false;

		}

		int month = Integer.parseInt(taxPeriod.substring(0, 2));
		if (taxPeriod.length() != 6 || month > 12 || month == 00) {
			return false;
		}

		return isValid;
	}

	/*
	 * PR column should not be validated for Addition in 2A-InvoiceKeyPR is null
	 * 2A column should not be validated for Addition in PR-InvoiceKey2A is null
	 */
	private boolean isValidationrequired(String val, String invKeyUploaded,
			String taxPeriodType) {

		if (A2.equalsIgnoreCase(taxPeriodType)) {

			if (!isPresent(invKeyUploaded) && !isPresent(val)) {

				return false;

			}
		}

		if (PR.equalsIgnoreCase(taxPeriodType)) {
			if (!isPresent(invKeyUploaded) && !isPresent(val)) {

				return false;

			}

		}
		return true;
	}

	private void is2APRTaxPeriodValid(String retPeriod, String invKeyUploaded,
			String userResponse, List<ProcessingResult> validationResult,
			String taxPeriodType) {

		if (!isValidationrequired(retPeriod, invKeyUploaded, taxPeriodType)) {
			return;
		}

		String errMsg = String.format(
				"Taxperiod %s cannot be blank or beyond GSTR3B TaxPeriod",
				taxPeriodType);

		if (!isPresent(retPeriod)) {
			/*
			 * ProcessingResult result = new ProcessingResult(UPLOAD,
			 * ERROR_CODE, errMsg); validationResult.add(result);
			 */

			return;
		}

		if (retPeriod.contains("'")) {
			retPeriod = retPeriod.replace("'", "");
		}

		String taxPeriod = retPeriod.trim();

		if (!taxPeriod.matches("[0-9]+") || taxPeriod.length() != 6) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;

		}

		int month = Integer.parseInt(taxPeriod.substring(0, 2));
		if (taxPeriod.length() != 6 || month > 12 || month == 00) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		if (is3BTaxPeriodValid(userResponse)) {
			Integer taxPeriod2APR = GenUtil.getDerivedTaxPeriod(taxPeriod);

			if (userResponse.contains("'")) {
				userResponse = userResponse.replace("'", "");
			}

			Integer taxPeriod3B = GenUtil.getDerivedTaxPeriod(userResponse);

			if (taxPeriod2APR > taxPeriod3B) {
				ProcessingResult result = new ProcessingResult(UPLOAD,
						ERROR_CODE, errMsg);
				validationResult.add(result);

			}
		}
	}

	private void isRecipientGstinValid(String recipientGstin,
			String invKeyUploaded, String gstinType,
			List<ProcessingResult> validationResult) {

		if (!isValidationrequired(recipientGstin, invKeyUploaded, gstinType)) {
			return;
		}

		String errMsg = String
				.format("Recipient GSTIN (%s) format is incorrect", gstinType);

		if (!isPresent(recipientGstin)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		recipientGstin = recipientGstin.trim();
		String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(recipientGstin);

		if (!matcher.matches() || recipientGstin.length() != 15) {

			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);

			validationResult.add(result);
			return;
		}
	}

	private void isSupplierGstinValid(String supplierGstin,
			String invKeyUploaded, String gstinType,
			List<ProcessingResult> validationResult) {

		if (!isValidationrequired(supplierGstin, invKeyUploaded, gstinType)) {
			return;
		}

		String errMsg = String.format("Supplier GSTIN (%s) format is incorrect",
				gstinType);
		if (!isPresent(supplierGstin)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		supplierGstin = supplierGstin.trim();
		if (supplierGstin.length() == 15 || supplierGstin.length() == 10) {

			if (supplierGstin.length() == 15) {
				/*
				 * String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]" +
				 * "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]" +
				 * "[A-Za-z0-9][A-Za-z0-9]$";
				 * 
				 * Pattern pattern = Pattern.compile(regex);
				 * 
				 * Matcher matcher = pattern.matcher(supplierGstin);
				 * 
				 * if (!matcher.matches() || supplierGstin.length() != 15) {
				 */
				if (supplierGstin.toUpperCase().length() != 15
						|| !supplierGstin.toUpperCase().matches("[A-Za-z0-9]+")
						|| supplierGstin.toUpperCase().matches("[A-Za-z]+")
						|| supplierGstin.toUpperCase().matches("[0-9]+")) {
					ProcessingResult result = new ProcessingResult(UPLOAD,
							ERROR_CODE, errMsg);

					validationResult.add(result);
					return;
				}
			}

		} else {

			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
		}

	}

	private void isDocType2AValid(String docType, String invKeyUploaded,
			List<ProcessingResult> validationResult) {

		if (!isPresent(invKeyUploaded)) {
			if (!isPresent(docType)) {

				return;
			}

		}

		String errMsg = "DOC-TYPE (2A) format is incorrect";

		if (!isPresent(docType)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);

			validationResult.add(result);
			return;
		}

		if (docType.trim().length() > 5) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);

			validationResult.add(result);
		}
		if (Stream.of("R", "CR", "DR", "C", "D", "DE", "SEWP", "SEWOP", "CBW")
				.noneMatch(docType.trim()::equalsIgnoreCase)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

	}

	private void isDocTypePRValid(String docType, String invKeyUploaded,
			List<ProcessingResult> validationResult) {

		if (!isPresent(invKeyUploaded)) {
			if (!isPresent(docType)) {

				return;
			}

		}

		String errMsg = "DOC-TYPE (PR) format is incorrect";

		if (!isPresent(docType)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		if (docType.trim().length() > 5) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);

			validationResult.add(result);
		}
		if (Stream.of("INV", "R", "CR", "DR", "C", "D")
				.noneMatch(docType.trim()::equalsIgnoreCase)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

	}

	private LocalDate isDateValid(String date, String invKeyUploaded,
			String dateDesc, List<ProcessingResult> validationResult) {

		if (!isValidationrequired(date, invKeyUploaded, dateDesc)) {
			return null;
		}

		String errMsg = String.format("Document Date (%s) format is incorrect",
				dateDesc);

		if (!isPresent(date)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return null;
		}

		LocalDate dateInstance = DateFormatForStructuralValidatons
				.parseObjToDate(date.trim());

		if (dateInstance == null || (dateInstance.getMonthValue() > 12)) {
			errMsg = String.format(INVALID, dateDesc);
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return null;
		}
		return dateInstance;
	}

	private void isDocNoPRValid(String docNoPR, String invKeyUploaded,
			List<ProcessingResult> validationResult, Set<String> activeDocNoPR,
			LocalDate docDatePR) {

		if (!isPresent(invKeyUploaded)) {
			if (!isPresent(docNoPR)) {

				return;
			}

		}

		if (!isPresent(docNoPR)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					"Document Number(PR) cannot be empty");
			validationResult.add(result);
			return;
		}

		if (docNoPR.contains("'")) {
			docNoPR = docNoPR.replace("'", "");
		}

		if (docDatePR != null) {
			docNoPR = docNoPR + "-" + docDatePR;
		}

		if (!activeDocNoPR.contains(docNoPR)) {

			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					"Document Number(PR) Does Not Exist In DigiGST Master");
			validationResult.add(result);
			return;

		}

	}

	private void isDocNo2AValid(String docNo2A, String invKeyUploaded,
			Set<String> activeDocNo2A, List<ProcessingResult> validationResult,
			LocalDate docDate2A, String response) {

		if (!isPresent(invKeyUploaded)) {
			if (!isPresent(docNo2A)) {

				return;
			}

		}

		if (!isPresent(docNo2A)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					"Document Number(2A) cannot be empty");
			validationResult.add(result);
			return;
		}

		if (docNo2A.contains("'")) {
			docNo2A = docNo2A.replace("'", "");
		}

		if (docDate2A != null) {
			docNo2A = docNo2A + "-" + docDate2A;
		}
		if (!activeDocNo2A.contains(docNo2A)) {

			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					"Document Number(2A) Does Not Exist In DigiGST Master");
			validationResult.add(result);
			return;

		}
		
		if (!activeDocNo2A.contains(docNo2A)) {
	        // If the response is NOT "unlock", then add the error; otherwise bypass.
	        if (!"unlock".equalsIgnoreCase(response)) {
	            ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
	                    "Document Number(2A) Does Not Exist In DigiGST Master");
	            validationResult.add(result);
	            return;
	        } else {
	            LOGGER.debug("Response is unlock; bypassing document existence validation for 2B");
	            return;
	        }
	    }

	}

	private void isReconIDValid(String id,
			List<ProcessingResult> validationResult) {

		String errMsg = "RequestID cannot be blank or format is incorrect";

		if (!isPresent(id)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		if (id.contains("'")) {
			id = id.replace("'", "");
		}

		if (!StringUtils.isNumeric(id.trim())) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);

		}

	}

	private void isAmountValid(String amt, String amtDesc,
			List<ProcessingResult> validationResult) {

		String errMsg = String.format("%s is not in expected format", amtDesc);

		if (!isPresent(amt)) {
			return;
		}
		// new added

		if (amt.contains("'")) {
			amt = amt.replace("'", "");
		}

		if (!NumberFomatUtil.isNumber(amt)) {

			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}
		boolean isValid = NumberFomatUtil.is17And2digValidDec(amt.trim());

		if (!isValid) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

	}

	public Set<String> getActive2ADocNumbers(List<String> docNum2A, boolean skipValidation) {
		if (skipValidation) {
	        return new AbstractSet<String>() {
	            @Override
	            public Iterator<String> iterator() {
	                return Collections.emptyIterator();
	            }
	            @Override
	            public int size() {
	                return 0;
	            }
	            @Override
	            public boolean contains(Object o) {
	                return true;
	            }
	        };
	    }
		Set<String> activeInvKey2A = null;
		try {
			String queryString = "SELECT DISTINCT A2_DOC_NUM,A2_DOC_DATE FROM "
					+ "	 TBL_AUTO_2APR_LINK WHERE IS_ACTIVE = TRUE AND "
					+ "  A2_DOC_NUM  IN (?1) ;";
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter(1, docNum2A);
			String msg = String.format(
					"Inside TBL_AUTO_2APR_LINK docNum2A, " + " query - %s ",
					queryString);

			LOGGER.debug(msg);

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			activeInvKey2A = list.stream().map(o -> convertObjToString(o))
					.collect(Collectors.toCollection(HashSet::new));

		} catch (Exception ex) {
			String msg = "Exception Occured in "
					+ "getActive2ADocNumbers() method";
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return activeInvKey2A;
	}

	public Set<String> getActivePRDocNumbers(List<String> docNumPR, boolean skipValidation) {
		if (skipValidation) {
	        return new AbstractSet<String>() {
	            @Override
	            public Iterator<String> iterator() {
	                return Collections.emptyIterator();
	            }
	            @Override
	            public int size() {
	                return 0;
	            }
	            @Override
	            public boolean contains(Object o) {
	                return true;
	            }
	        };
	    }
		Set<String> activeDocNumPR = null;
		try {
			String queryString = "SELECT DISTINCT PR_DOC_NUM,PR_DOC_DATE FROM "
					+ "	 TBL_AUTO_2APR_LINK WHERE IS_ACTIVE = TRUE AND "
					+ "  PR_DOC_NUM  IN (?1) ;";
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter(1, docNumPR);
			String msg = String.format(
					"Inside TBL_AUTO_2APR_LINK docNumPR, " + " query - %s ",
					queryString);

			LOGGER.debug(msg);

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			activeDocNumPR = list.stream().map(o -> convertObjToString(o))
					.collect(Collectors.toCollection(HashSet::new));

		} catch (Exception ex) {
			String msg = "Exception Occured in "
					+ "getActivePRDocNumbers() method";
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return activeDocNumPR;
	}

	public Set<String> getActiveInvoiceKeys(List<String> invKeyPR,
			List<String> invKey2A) {
		Set<String> activeDocNumPR = null;
		String queryString = null;
		try {
			if (invKeyPR != null && invKey2A != null || !invKey2A.isEmpty()) {
				queryString = "SELECT DISTINCT INVOICEKEYPR, INVOICEKEYA2 FROM "
						+ "	 TBL_RECON_RESP_PSD WHERE ENDDTM IS NULL AND "
						+ "  INVOICEKEYPR  IN (?1)  AND INVOICEKEYA2 IN (?2) AND  "
						+ "  INVOICEKEYA2 IS NOT NULL AND INVOICEKEYPR IS NOT NULL;";
			} else if (invKeyPR != null && invKey2A == null
					|| invKey2A.isEmpty()) {
				queryString = "SELECT DISTINCT INVOICEKEYPR FROM "
						+ "	 TBL_RECON_RESP_PSD WHERE ENDDTM IS NULL AND "
						+ "  INVOICEKEYPR  IN (?1)  AND INVOICEKEYPR IS NOT NULL;";
			} else if (invKeyPR == null && invKey2A != null
					|| !invKey2A.isEmpty()) {
				queryString = "SELECT DISTINCT INVOICEKEYA2 FROM "
						+ "	 TBL_RECON_RESP_PSD WHERE ENDDTM IS NULL AND "
						+ "  INVOICEKEYA2 IN (?2) AND  "
						+ "  INVOICEKEYA2 IS NOT NULL;";
			}

			Query q = entityManager.createNativeQuery(queryString);
			if (invKeyPR != null) {
				q.setParameter(1, invKeyPR);
			}
			if (invKey2A != null) {
				q.setParameter(2, invKey2A);
			}
			String msg = String
					.format("Inside TBL_RECON_RESP_PSD getActiveInvoiceKeys, "
							+ " query - %s ", queryString);

			LOGGER.debug(msg);

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			activeDocNumPR = list.stream().map(o -> convertObjToInvKeyComb(o))
					.collect(Collectors.toCollection(HashSet::new));

		} catch (Exception ex) {
			String msg = "Exception Occured in "
					+ "getActiveInvoiceKeys() method";
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return activeDocNumPR;
	}

	public Set<String> getActiveInvoice2AKeys(List<String> invKey2A) {
		Set<String> activeInvoice2A = null;
		try {

			String queryString = "SELECT DISTINCT INVOICEKEYA2 FROM "
					+ "	 TBL_RECON_RESP_PSD WHERE ENDDTM IS NULL AND "
					+ "  INVOICEKEYA2 IN (?1) AND INVOICEKEYA2 IS NOT NULL"
					+ "  AND INVOICEKEYPR IS NULL;";
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter(1, invKey2A);
			String msg = String.format(
					"Inside TBL_RECON_RESP_PSD INVOICEKEYA2, " + " query - %s ",
					queryString);

			LOGGER.debug(msg);

			@SuppressWarnings("unchecked")
			List<Object> list = q.getResultList();
			activeInvoice2A = list.stream()
					.map(o -> convertSingleObjToString(o))
					.collect(Collectors.toCollection(HashSet::new));

		} catch (Exception ex) {
			String msg = "Exception Occured in "
					+ "getActiveInvoice2AKeys() method";
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return activeInvoice2A;
	}

	public Set<String> getActiveInvoicePRKeys(List<String> invKeyPR) {
		Set<String> activeInvoicePR = null;
		try {

			String queryString = "SELECT DISTINCT INVOICEKEYPR FROM "
					+ "	 TBL_RECON_RESP_PSD WHERE ENDDTM IS NULL AND "
					+ "  INVOICEKEYPR IN (?1) AND INVOICEKEYPR IS NOT "
					+ "  NULL AND INVOICEKEYA2 IS NULL;";
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter(1, invKeyPR);
			String msg = String.format(
					"Inside TBL_RECON_RESP_PSD INVOICEKEYPR, " + " query - %s ",
					queryString);

			LOGGER.debug(msg);

			@SuppressWarnings("unchecked")
			List<Object> list = q.getResultList();
			activeInvoicePR = list.stream()
					.map(o -> convertSingleObjToString(o))
					.collect(Collectors.toCollection(HashSet::new));

		} catch (Exception ex) {
			String msg = "Exception Occured in "
					+ "getActiveInvoicePRKeys() method";
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return activeInvoicePR;
	}

	private String convertSingleObjToString(Object obj) {
		return String.valueOf(obj);
	}

	private String convertObjToString(Object[] obj) {
		if (obj[1] != null) {
			return String.valueOf(obj[0]) + "-" + obj[1].toString();
		} else {
			return String.valueOf(obj[0]);
		}
	}

	private String convertObjToInvKeyComb(Object[] obj) {
		return String.valueOf(obj[0]) + "-" + String.valueOf(obj[1]);
	}

	private String removeQuotes(String data) {
		if (Strings.isNullOrEmpty(data)) {
			return null;
		}
		if (data.contains("'")) {
			return data.replace("'", "");
		}
		return data;

	}

	private LocalDate isDateValidValidation(String date, String dateDesc,
			List<ProcessingResult> validationResult) {

		if (Strings.isNullOrEmpty(date))
			return null;

		String errMsg = String.format("%s Format Is Incorrect", dateDesc);

		if (!isPresent(date)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return null;
		}

		LocalDate dateInstance = DateFormatForStructuralValidatons
				.parseObjToDate(removeQuotes(date.trim()));

		if (dateInstance == null || (dateInstance.getMonthValue() > 12)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return null;
		}
		return dateInstance;
	}

	private void crossTagging(GSTR2AAutoReconRespUploadDTO rowData,
			List<ProcessingResult> validationResult) {

		String errMsg = "Import record cannot be locked with Non-import record.";

		if (!Strings.isNullOrEmpty(rowData.getBillOfEntryPR())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR())) {

			if (Strings.isNullOrEmpty(rowData.getBillOfEntry2A())
					&& Strings.isNullOrEmpty(rowData.getBillOfEntryDate2A())) {

				if (!Strings.isNullOrEmpty(rowData.getRecipientGstin2A())
						&& !Strings.isNullOrEmpty(rowData.getSupplierGstin2A())
						&& !Strings.isNullOrEmpty(rowData.getDocType2A())
						&& !Strings.isNullOrEmpty(rowData.getDocumentNumber2A())
						&& !Strings
								.isNullOrEmpty(rowData.getDocumentDate2A())) {
					ProcessingResult result = new ProcessingResult(UPLOAD,
							ERROR_CODE, errMsg);
					validationResult.add(result);
					return;
				}

			} else if (!Strings.isNullOrEmpty(rowData.getBillOfEntry2A())
					&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDate2A())) {
				Set<String> activeDocNum2B = null;
				// Do a look of key columns and throw respective error
				try {
					String queryString = "SELECT DISTINCT A2_BILL_OF_ENTRY, A2_BILL_OF_ENTRY_DATE FROM "
							+ "TBL_LINK_2A_PR WHERE IS_ACTIVE = TRUE AND "
							+ "A2_BILL_OF_ENTRY  IN (?1) ;";
					Query q = entityManager.createNativeQuery(queryString);
					q.setParameter(1, rowData.getBillOfEntry2A());
					String msg = String.format(
							"Inside TBL_AUTO_2BPR_LINK docNumPR,query - %s ",
							queryString);

					LOGGER.debug(msg);

					@SuppressWarnings("unchecked")
					List<Object[]> list = q.getResultList();
					activeDocNum2B = list.stream()
							.map(o -> convertObjToString(o))
							.collect(Collectors.toCollection(HashSet::new));

				} catch (Exception ex) {
					String msg = "Exception Occured in "
							+ "getActivePRDocNumbers() method";
					LOGGER.error(msg, ex);
					throw new AppException(msg);
				}
				if (activeDocNum2B == null || activeDocNum2B.isEmpty()) {
					/*
					 * ProcessingResult result = new ProcessingResult(UPLOAD,
					 * ERROR_CODE,
					 * "Bill of Entry Number (2B) is not a active record");
					 * validationResult.add(result);
					 */
					return;
				}
			}

		} else if (!Strings.isNullOrEmpty(rowData.getBillOfEntry2A())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDate2A())) {

			if (Strings.isNullOrEmpty(rowData.getBillOfEntryPR())
					&& Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR())) {

				if (!Strings.isNullOrEmpty(rowData.getRecipientGstinPR())
						&& !Strings.isNullOrEmpty(rowData.getSupplierGstinPR())
						&& !Strings.isNullOrEmpty(rowData.getDocTypePR())
						&& !Strings.isNullOrEmpty(rowData.getDocumentNumberPR())
						&& !Strings
								.isNullOrEmpty(rowData.getDocumentDatePR())) {
					ProcessingResult result = new ProcessingResult(UPLOAD,
							ERROR_CODE, errMsg);
					validationResult.add(result);
					return;
				}

			} else if (!Strings.isNullOrEmpty(rowData.getBillOfEntryPR())
					&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR())) {
				// Do a look of key columns and throw respective error
				Set<String> activeDocNumPR = null;
				try {
					String queryString = "SELECT DISTINCT PR_BILL_OF_ENTRY, PR_BILL_OF_ENTRY_DATE FROM "
							+ "TBL_LINK_2A_PR WHERE IS_ACTIVE = TRUE AND "
							+ "PR_BILL_OF_ENTRY  IN (?1) ;";
					Query q = entityManager.createNativeQuery(queryString);
					q.setParameter(1, rowData.getBillOfEntryPR());
					String msg = String.format(
							"Inside TBL_AUTO_2BPR_LINK docNumPR,query - %s ",
							queryString);

					LOGGER.debug(msg);

					@SuppressWarnings("unchecked")
					List<Object[]> list = q.getResultList();
					activeDocNumPR = list.stream()
							.map(o -> convertObjToString(o))
							.collect(Collectors.toCollection(HashSet::new));

				} catch (Exception ex) {
					String msg = "Exception Occured in "
							+ "getActivePRBillofEntry() method";
					LOGGER.error(msg, ex);
					throw new AppException(msg);
				}
				if (activeDocNumPR == null || activeDocNumPR.isEmpty()) {
					/*
					 * ProcessingResult result = new ProcessingResult(UPLOAD,
					 * ERROR_CODE,
					 * "Bill of Entry Number (PR) is not a active record");
					 * validationResult.add(result);
					 */
					return;
				}
			}

		}

	}

	private void isSupplyTypeValid(String supplyType,
			List<ProcessingResult> validationResult,
			Stream<String> supplyTypes) {

		String errMsg = "Incorrect Supply Type (PR)";
		if (!isPresent(supplyType)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		if (supplyTypes.noneMatch(supplyType.trim()::equalsIgnoreCase)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;

		}

	}

}
