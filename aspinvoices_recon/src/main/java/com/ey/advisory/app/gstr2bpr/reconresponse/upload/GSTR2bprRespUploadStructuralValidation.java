package com.ey.advisory.app.gstr2bpr.reconresponse.upload;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.math.BigDecimal;
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
import org.springframework.stereotype.Component;

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
@Component("GSTR2bprRespUploadStructuralValidation")
public class GSTR2bprRespUploadStructuralValidation {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String UPLOAD = "Upload";
	private static final String ERROR_CODE = "E";
	private static final String B2 = "2B";
	private static final String PR = "PR";
	private static final String TAX_PERIOD_REGEX = "[0-9]+";
	private static final String B2PR_ENDDTM_NULL = " TBL_2BPR_RECON_RESP_PSD WHERE ENDDTM IS NULL AND ";

	public void rowDataValidation(List<ProcessingResult> validationResult,
			GSTR2bprAutoReconRespUploadDTO rowData, Set<String> activeDocNo2B,
			Set<String> activeDocNoPR, boolean computeOnBoardingPrm,
			String optedOption3B, Set<String> uniqueImsKeyIds,
			boolean isimsValidationReq, boolean isGstr2bValidationReq) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					" isimsValidationReq {}, rowData document number2b {} pr{} ",
					isimsValidationReq, rowData.getDocumentNumber2B(),
					rowData.getDocumentNumberPR());
		}

		if (isGstr2bValidationReq) {
			isResponseValid(rowData.getUserResponse(), validationResult);

			if (Strings.isNullOrEmpty(rowData.getDocumentNumber2B())
					&& Strings.isNullOrEmpty(rowData.getBillOfEntry2B())
					&& Strings.isNullOrEmpty(rowData.getBillOfEntryDate2B())) {
				// No Table Type Validation has to do for above condition
			} else {
				// cross tagging
				isTableTypeValid(rowData.getTableType2B(), validationResult,
						rowData.getTaxPeriodPR(),
						rowData.getInvoiceKeyUploadPR(), PR);
			}

//			isDateValidValidation(rowData.getAccountingVoucherDate(),
//					"Accounting Voucher Date", validationResult);

//			isDateValidValidation(rowData.getAdjustementReferenceDate(),
//					"Adjustement Reference Date", validationResult);

//			isDateValidValidation(rowData.getBillOfEntryCreatedDate2B(),
//					"Bill of Entry Created Date (2B)", validationResult);

//			isDateValidValidation(rowData.getGenerationDate2B(),
//					"GSTR2B Generation Date", validationResult);

//			isDateValidValidation(rowData.getGSTR1FilingDate(),
//					"GSTR1-Filing Date", validationResult);

//			isDateValidValidation(rowData.getCancellationDate(),
//					"SupplierGSTIN-Cancellation Date", validationResult);

//			isDateValidValidation(rowData.getOrgDocDate2B(), "OrgDoc Date (2B)",
//					validationResult);
//
//			isDateValidValidation(rowData.getOrgDocDatePR(),
//					"OrgDoc Date (PR)	", validationResult);

//			isDateValidValidation(rowData.getBoeReferenceDate(),
//					"BOE-Reference Date (2B)", validationResult);

//			isDateValidValidation(rowData.getGlPostingDate(), "GLPosting Date",
//					validationResult);
//
//			isDateValidValidation(rowData.getCustomerPORefDate(),
//					"Customer POReference Date	", validationResult);

//			isDateValidValidation(rowData.getEWayBillDate(), "EWB Date",
//					validationResult);
//
//			isDateValidValidation(rowData.getIrnDate2B(), "IRN Date (2B)",
//					validationResult);
//
//			isDateValidValidation(rowData.getIrnDatePR(), "IRN Date (PR)",
//					validationResult);

//			isDateValidValidation(rowData.getGstr3bFilingDate(),
//					"GSTR3B-Filing Date", validationResult);
		}
		if (isimsValidationReq) {
			checkForImsValidResponse(rowData.getImsUserResponse(),
					validationResult);
			checkForDuplicateImsUniqueId(rowData.getImsUniqueId(),
					validationResult, uniqueImsKeyIds);
		}

		if (isGstr2bValidationReq) {
			boolean docNo2BValid = false;
			boolean isIMPGSupplyTypeValidationApplicable = false;
			boolean isIMPGSEZSupplyTypeValidationApplicable = false;
			boolean isNonIMPGSupplyTypeApplicable = false;

			/*
			 * if(!Strings.isNullOrEmpty(rowData.getBillOfEntryPR()) &&
			 * !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR()) &&
			 * !Strings.isNullOrEmpty(rowData.getRecipientGstinPR())){
			 * isIMPGSupplyTypeValidationApplicable = true;//IMPG } else
			 * if(!Strings.isNullOrEmpty(rowData.getBillOfEntryPR()) &&
			 * !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR()) &&
			 * !Strings.isNullOrEmpty(rowData.getSupplierGstinPR()) &&
			 * !Strings.isNullOrEmpty(rowData.getRecipientGstinPR())){
			 * isIMPGSEZSupplyTypeValidationApplicable = true;//IMPGSEZ } else
			 * if(!Strings.isNullOrEmpty(rowData.getDocumentNumberPR()) &&
			 * !Strings.isNullOrEmpty(rowData.getDocumentDatePR()) &&
			 * !Strings.isNullOrEmpty(rowData.getDocTypePR()) &&
			 * !Strings.isNullOrEmpty(rowData.getSupplierGstinPR()) &&
			 * !Strings.isNullOrEmpty(rowData.getRecipientGstinPR())){
			 * isNonIMPGSupplyTypeApplicable = true;//Non Impg }
			 * 
			 * if(isIMPGSupplyTypeValidationApplicable){
			 * isSupplyTypeValid(rowData.getSupplyTypePR(), validationResult,
			 * Stream.of("IMPG","SEZG")); }
			 * 
			 * if(isNonIMPGSupplyTypeApplicable){
			 * isSupplyTypeValid(rowData.getSupplyTypePR(),
			 * validationResult,Stream.of("TAX","SEZS", "DTA", "CBW", "DXP")); }
			 * if(isIMPGSEZSupplyTypeValidationApplicable){
			 * isSupplyTypeValid(rowData.getSupplyTypePR(), validationResult,
			 * Stream.of("IMPG","SEZG")); }
			 */

			/*
			 * If SupplyTypePR IS not empty AND (PRRecipientGSTIN IS not empty)
			 * AND SupplyTypePR not in (IMPG, SEZG, TAX, SEZS, DTA, CBW, DXP)
			 * --> 7 supplytypes then Error_IncorrectSupplyTypePR
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
					.asList("GETGSTR2B_IMPG_HEADER", "GETGSTR2B_IMPGSEZ_HEADER","IMPGSEZ_Rejected","IMPG_Rejected")
					.contains(rowData.getTableType2B())
					&& !Strings.isNullOrEmpty(rowData.getRecipientGstin2B())
					&& (Strings.isNullOrEmpty(rowData.getBillOfEntry2B())
							|| Strings.isNullOrEmpty(
									rowData.getBillOfEntryDate2B()))) {

				ProcessingResult result = new ProcessingResult(UPLOAD,
						ERROR_CODE,
						"Bill of Entry Number(2B) Does Not Exist In DigiGST Master");
				validationResult.add(result);

			} else if (!Arrays
					.asList("GETGSTR2B_IMPG_HEADER", "GETGSTR2B_IMPGSEZ_HEADER","IMPGSEZ_Rejected","IMPG_Rejected")
					.contains(rowData.getTableType2B())
					&& !Strings.isNullOrEmpty(rowData.getRecipientGstin2B())
					&& (Strings.isNullOrEmpty(rowData.getSupplierGstin2B())
							|| Strings.isNullOrEmpty(
									rowData.getRecipientGstin2B())
							|| Strings.isNullOrEmpty(rowData.getDocType2B())
							|| Strings.isNullOrEmpty(
									rowData.getDocumentNumber2B())
							|| Strings.isNullOrEmpty(
									rowData.getDocumentDate2B()))) {

				ProcessingResult result = new ProcessingResult(UPLOAD,
						ERROR_CODE,
						"Document Number(2B) Does Not Exist In DigiGST Master");
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
					|| (!Strings.isNullOrEmpty(rowData.getBillOfEntry2B())
							&& !Strings.isNullOrEmpty(
									rowData.getBillOfEntryDate2B()))) {

				crossTagging(rowData, validationResult);

				if (Strings.isNullOrEmpty(rowData.getSupplierGstin2B())
						&& Strings
								.isNullOrEmpty(rowData.getSupplierGstinPR())) {
					LOGGER.debug("Inside IMPG");
					if (!Strings.isNullOrEmpty(rowData.getBillOfEntry2B())
							&& !Strings.isNullOrEmpty(
									rowData.getBillOfEntryDate2B())) {
						LOGGER.debug("Inside 2B Side");
						isResponseRemarksValid(rowData.getResponseRemarks(),
								validationResult);
						isBillOfEntry2BValid(rowData.getBillOfEntry2B(),
								validationResult);
						isBillOfEntryDate2BValid(rowData.getBillOfEntryDate2B(),
								validationResult);
						isAmountValid(rowData.getTaxableValue2B(),
								"TaxableValue2B ", validationResult);
						isAmountValid(rowData.getTotalTax2B(), "TotalTax2B ",
								validationResult);
						isAmountValid(rowData.getInvoiceValue2B(),
								"InvoiceValue2B ", validationResult);
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
						isBillOfEntry2BValid(rowData.getBillOfEntry2B(),
								validationResult);
						isBillOfEntryDate2BValid(rowData.getBillOfEntryDate2B(),
								validationResult);
						isBillOfEntryPRValid(rowData.getBillOfEntryPR(),
								validationResult);
						isBillOfEntryDatePRValid(rowData.getBillOfEntryDatePR(),
								validationResult);
						isAmountValid(rowData.getTaxableValue2B(),
								"TaxableValue2B ", validationResult);
						isAmountValid(rowData.getTaxableValuePR(),
								"TaxableValuePR ", validationResult);
						isAmountValid(rowData.getTotalTax2B(), "TotalTax2B ",
								validationResult);
						isAmountValid(rowData.getTotalTaxPR(), "TotalTaxPR ",
								validationResult);
						isAmountValid(rowData.getInvoiceValue2B(),
								"InvoiceValue2B ", validationResult);
						isAmountValid(rowData.getInvoiceValuePR(),
								"InvoiceValuePR ", validationResult);
					}
				} else if ((!Strings.isNullOrEmpty(rowData.getSupplierGstin2B())
						|| !Strings
								.isNullOrEmpty(rowData.getSupplierGstinPR()))) {

					LOGGER.debug("Inside IMPGSEZ");
					if (!Strings.isNullOrEmpty(rowData.getBillOfEntry2B())
							&& !Strings.isNullOrEmpty(
									rowData.getBillOfEntryDate2B())) {
						LOGGER.debug("Inside IMPGSEZ 2B Side");
						isResponseRemarksValid(rowData.getResponseRemarks(),
								validationResult);
						isBillOfEntry2BValid(rowData.getBillOfEntry2B(),
								validationResult);
						isBillOfEntryDate2BValid(rowData.getBillOfEntryDate2B(),
								validationResult);
						isAmountValid(rowData.getTaxableValue2B(),
								"TaxableValue2B ", validationResult);
						isAmountValid(rowData.getTotalTax2B(), "TotalTax2B ",
								validationResult);
						isAmountValid(rowData.getInvoiceValue2B(),
								"InvoiceValue2B ", validationResult);
					} else if (!Strings.isNullOrEmpty(rowData.getSupplyTypePR())
							&& rowData.getSupplyTypePR()
									.equalsIgnoreCase("IMPG")
							|| !Strings.isNullOrEmpty(rowData.getSupplyTypePR())
									&& rowData.getSupplyTypePR()
											.equalsIgnoreCase("SEZG")) {
						LOGGER.debug("Inside IMPGSEZ PR Side");
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
						LOGGER.debug("Inside IMPGSEZ ALL");
						isResponseRemarksValid(rowData.getResponseRemarks(),
								validationResult);
						isBillOfEntry2BValid(rowData.getBillOfEntry2B(),
								validationResult);
						isBillOfEntryDate2BValid(rowData.getBillOfEntryDate2B(),
								validationResult);
						isBillOfEntryPRValid(rowData.getBillOfEntryPR(),
								validationResult);
						isBillOfEntryDatePRValid(rowData.getBillOfEntryDatePR(),
								validationResult);
						isAmountValid(rowData.getTaxableValue2B(),
								"TaxableValue2B ", validationResult);
						isAmountValid(rowData.getTaxableValuePR(),
								"TaxableValuePR ", validationResult);
						isAmountValid(rowData.getTotalTax2B(), "TotalTax2B ",
								validationResult);
						isAmountValid(rowData.getTotalTaxPR(), "TotalTaxPR ",
								validationResult);
						isAmountValid(rowData.getInvoiceValue2B(),
								"InvoiceValue2B ", validationResult);
						isAmountValid(rowData.getInvoiceValuePR(),
								"InvoiceValuePR ", validationResult);
					}

				}

			}

			/*
			 * if (rowData.getReportType().contains("Imports")) {
			 * 
			 * if ((rowData.getSupplierGstin2B() == null ||
			 * rowData.getSupplierGstin2B().isEmpty()) &&
			 * (rowData.getSupplierGstinPR() == null ||
			 * rowData.getSupplierGstinPR().isEmpty())) {
			 * LOGGER.debug("Inside IMPG"); if ((rowData.getIDPR() == null ||
			 * rowData.getIDPR().isEmpty()) && (rowData.getID2B() != null ||
			 * !rowData.getID2B().isEmpty())) {
			 * LOGGER.debug("Inside IMPG getIDPR null");
			 * isResponseRemarksValid(rowData.getResponseRemarks(),
			 * validationResult);
			 * isBillOfEntry2BValid(rowData.getBillOfEntry2B(),
			 * validationResult);
			 * isBillOfEntryDate2BValid(rowData.getBillOfEntryDate2B(),
			 * validationResult); isAmountValid(rowData.getTaxableValue2B(),
			 * "TaxableValue2B ", validationResult);
			 * isAmountValid(rowData.getTotalTax2B(), "TotalTax2B ",
			 * validationResult); isAmountValid(rowData.getInvoiceValue2B(),
			 * "InvoiceValue2B ", validationResult); } else if
			 * ((rowData.getIDPR() != null || !rowData.getIDPR().isEmpty()) &&
			 * (rowData.getID2B() == null || rowData.getID2B().isEmpty())) {
			 * LOGGER.debug("Inside IMPG getIDPR null");
			 * isResponseRemarksValid(rowData.getResponseRemarks(),
			 * validationResult);
			 * isBillOfEntryPRValid(rowData.getBillOfEntryPR(),
			 * validationResult);
			 * isBillOfEntryDatePRValid(rowData.getBillOfEntryDatePR(),
			 * validationResult); isAmountValid(rowData.getTaxableValuePR(),
			 * "TaxableValuePR ", validationResult);
			 * isAmountValid(rowData.getTotalTaxPR(), "TotalTaxPR ",
			 * validationResult); isAmountValid(rowData.getInvoiceValuePR(),
			 * "InvoiceValuePR ", validationResult); } else {
			 * LOGGER.debug("Inside IMPG ALL");
			 * isResponseRemarksValid(rowData.getResponseRemarks(),
			 * validationResult);
			 * isBillOfEntry2BValid(rowData.getBillOfEntry2B(),
			 * validationResult);
			 * isBillOfEntryDate2BValid(rowData.getBillOfEntryDate2B(),
			 * validationResult);
			 * isBillOfEntryPRValid(rowData.getBillOfEntryPR(),
			 * validationResult);
			 * isBillOfEntryDatePRValid(rowData.getBillOfEntryDatePR(),
			 * validationResult); isAmountValid(rowData.getTaxableValue2B(),
			 * "TaxableValue2B ", validationResult);
			 * isAmountValid(rowData.getTaxableValuePR(), "TaxableValuePR ",
			 * validationResult); isAmountValid(rowData.getTotalTax2B(),
			 * "TotalTax2B ", validationResult);
			 * isAmountValid(rowData.getTotalTaxPR(), "TotalTaxPR ",
			 * validationResult); isAmountValid(rowData.getInvoiceValue2B(),
			 * "InvoiceValue2B ", validationResult);
			 * isAmountValid(rowData.getInvoiceValuePR(), "InvoiceValuePR ",
			 * validationResult); } } else if
			 * (!Strings.isNullOrEmpty(rowData.getSupplierGstin2B()) &&
			 * !Strings.isNullOrEmpty(rowData.getSupplierGstinPR())) {
			 * LOGGER.debug("Inside IMPGSEZ"); if ((rowData.getIDPR() == null ||
			 * rowData.getIDPR().isEmpty()) && (rowData.getID2B() != null ||
			 * !rowData.getID2B().isEmpty())) {
			 * LOGGER.debug("Inside IMPGSEZ getIDPR null");
			 * isResponseRemarksValid(rowData.getResponseRemarks(),
			 * validationResult);
			 * isBillOfEntry2BValid(rowData.getBillOfEntry2B(),
			 * validationResult);
			 * isBillOfEntryDate2BValid(rowData.getBillOfEntryDate2B(),
			 * validationResult); isAmountValid(rowData.getTaxableValue2B(),
			 * "TaxableValue2B ", validationResult);
			 * isAmountValid(rowData.getTotalTax2B(), "TotalTax2B ",
			 * validationResult); isAmountValid(rowData.getInvoiceValue2B(),
			 * "InvoiceValue2B ", validationResult); } else if
			 * ((rowData.getIDPR() != null || !rowData.getIDPR().isEmpty()) &&
			 * (rowData.getID2B() == null || rowData.getID2B().isEmpty())) {
			 * LOGGER.debug("Inside IMPGSEZ getIDPR nut null");
			 * isResponseRemarksValid(rowData.getResponseRemarks(),
			 * validationResult);
			 * isBillOfEntryPRValid(rowData.getBillOfEntryPR(),
			 * validationResult);
			 * isBillOfEntryDatePRValid(rowData.getBillOfEntryDatePR(),
			 * validationResult); isAmountValid(rowData.getTaxableValuePR(),
			 * "TaxableValuePR ", validationResult);
			 * isAmountValid(rowData.getTotalTaxPR(), "TotalTaxPR ",
			 * validationResult); isAmountValid(rowData.getInvoiceValuePR(),
			 * "InvoiceValuePR ", validationResult); } else {
			 * LOGGER.debug("Inside IMPGSEZ ALL");
			 * isResponseRemarksValid(rowData.getResponseRemarks(),
			 * validationResult);
			 * isBillOfEntry2BValid(rowData.getBillOfEntry2B(),
			 * validationResult);
			 * isBillOfEntryDate2BValid(rowData.getBillOfEntryDate2B(),
			 * validationResult);
			 * isBillOfEntryPRValid(rowData.getBillOfEntryPR(),
			 * validationResult);
			 * isBillOfEntryDatePRValid(rowData.getBillOfEntryDatePR(),
			 * validationResult); isAmountValid(rowData.getTaxableValue2B(),
			 * "TaxableValue2B ", validationResult);
			 * isAmountValid(rowData.getTaxableValuePR(), "TaxableValuePR ",
			 * validationResult); isAmountValid(rowData.getTotalTax2B(),
			 * "TotalTax2B ", validationResult);
			 * isAmountValid(rowData.getTotalTaxPR(), "TotalTaxPR ",
			 * validationResult); isAmountValid(rowData.getInvoiceValue2B(),
			 * "InvoiceValue2B ", validationResult);
			 * isAmountValid(rowData.getInvoiceValuePR(), "InvoiceValuePR ",
			 * validationResult); }
			 * 
			 * } } else if ((!Strings.isNullOrEmpty(rowData.getBillOfEntryPR())
			 * && !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR())) ||
			 * (!Strings.isNullOrEmpty(rowData.getBillOfEntry2B()) &&
			 * !Strings.isNullOrEmpty( rowData.getBillOfEntryDate2B()))) {
			 * 
			 * if((!Strings.isNullOrEmpty(rowData.getBillOfEntryPR()) &&
			 * !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR()))){
			 * LOGGER.debug("Inside Unclock BillOfEntryPR");
			 * isBillOfEntryPRValid(rowData.getBillOfEntryPR(),
			 * validationResult);
			 * isBillOfEntryDatePRValid(rowData.getBillOfEntryDatePR(),
			 * validationResult); isAmountValid(rowData.getTaxableValuePR(),
			 * "TaxableValuePR ", validationResult);
			 * isAmountValid(rowData.getTotalTaxPR(), "TotalTaxPR ",
			 * validationResult); isAmountValid(rowData.getInvoiceValuePR(),
			 * "InvoiceValuePR ", validationResult); } else
			 * if((!Strings.isNullOrEmpty(rowData.getBillOfEntry2B()) &&
			 * !Strings.isNullOrEmpty( rowData.getBillOfEntryDate2B()))){
			 * LOGGER.debug("Inside Unclock BillOfEntry2B");
			 * isBillOfEntry2BValid(rowData.getBillOfEntry2B(),
			 * validationResult);
			 * isBillOfEntryDate2BValid(rowData.getBillOfEntryDate2B(),
			 * validationResult); isAmountValid(rowData.getTaxableValue2B(),
			 * "TaxableValue2B ", validationResult);
			 * isAmountValid(rowData.getTotalTax2B(), "TotalTax2B ",
			 * validationResult); isAmountValid(rowData.getInvoiceValue2B(),
			 * "InvoiceValue2B ", validationResult); }
			 * 
			 * }
			 */else {

				if (!Strings.isNullOrEmpty(rowData.getSupplierGstin2B())) {
					isSupplierGstinValid(rowData.getSupplierGstin2B(),
							rowData.getInvoiceKeyUpload2B(), B2,
							validationResult);
				}

				if (!Strings.isNullOrEmpty(rowData.getSupplierGstinPR())) {
					isSupplierGstinValid(rowData.getSupplierGstinPR(),
							rowData.getInvoiceKeyUploadPR(), PR,
							validationResult);
				}

				if (!Strings.isNullOrEmpty(rowData.getDocType2B())) {
					isDocType2BValid(rowData.getDocType2B(),
							rowData.getInvoiceKeyUpload2B(), validationResult);
				}

				if (!Strings.isNullOrEmpty(rowData.getDocTypePR())) {
					isDocTypePRValid(rowData.getDocTypePR(),
							rowData.getInvoiceKeyUploadPR(), validationResult);
				}

				if (!Strings.isNullOrEmpty(rowData.getDocumentDatePR())) {
					LocalDate docDatePR = isDateValid(
							rowData.getDocumentDatePR(),
							rowData.getInvoiceKeyUploadPR(), PR,
							validationResult);
					isDocNoPRValid(rowData.getDocumentNumberPR(),
							rowData.getInvoiceKeyUploadPR(), validationResult,
							activeDocNoPR, docDatePR, rowData.getUserResponse());
				}

				if (!Strings.isNullOrEmpty(rowData.getDocumentDate2B())) {
					LocalDate docDate2B = isDateValid(
							rowData.getDocumentDate2B(),
							rowData.getInvoiceKeyUpload2B(), B2,
							validationResult);

					docNo2BValid = isDocNo2BValid(rowData.getDocumentNumber2B(),
							rowData.getInvoiceKeyUpload2B(), activeDocNo2B,
							validationResult, docDate2B, rowData.getUserResponse());
				}

			}

			if ("A".equalsIgnoreCase(optedOption3B)
					|| "C".equalsIgnoreCase(optedOption3B)) {
				isRevChrgPRValid(rowData.getReverseChargeFlagPR(),
						rowData.getUserResponse(), validationResult);
			}

			is2BPRTaxPeriodValid(rowData.getTaxPeriod2B(),
					rowData.getInvoiceKeyUpload2B(), rowData.getUserResponse(),
					validationResult, B2);// 2BTaxperiod

			isRecipientGstinValid(rowData.getRecipientGstin2B(),
					rowData.getInvoiceKeyUpload2B(), B2, validationResult);

			is2BPRTaxPeriodValid(rowData.getTaxPeriodPR(),
					rowData.getInvoiceKeyUploadPR(), rowData.getUserResponse(),
					validationResult, PR);// PRTaxperiod

			isRecipientGstinValid(rowData.getRecipientGstinPR(),
					rowData.getInvoiceKeyUploadPR(), PR, validationResult);

			if (is3BTaxPeriodValid(rowData.getUserResponse())) {
				isITCReversalIdentifierValid(rowData.getITCReversalIdentifier(),
						validationResult);
			}
			boolean isAvailableIGSTValid = isAmountValid(
					rowData.getAvailableIGST(), "AvailableIGST ",
					validationResult);
			boolean isAvailableCGSTValid = isAmountValid(
					rowData.getAvailableCGST(), "AvailableCGST ",
					validationResult);
			boolean isAvailableSGSTValid = isAmountValid(
					rowData.getAvailableSGST(), "AvailableSGST ",
					validationResult);
			boolean isAvailableCessValid = isAmountValid(
					rowData.getAvailableCESS(), "AvailableCESS ",
					validationResult);

			if (computeOnBoardingPrm
					&& is3BTaxPeriodValid(rowData.getUserResponse())) {

				if (rowData.getReportCategory().contains("IMPG")
						&& !Strings.isNullOrEmpty(rowData.getBillOfEntry2B())
						&& !Strings
								.isNullOrEmpty(rowData.getBillOfEntryDate2B())
						&& !Strings.isNullOrEmpty(rowData.getPortCode2B())) {
					// Skipping validation for 2B base records IMPG
				} else {
					if (rowData.getReportCategory().contains("IMPG")
							&& !Strings
									.isNullOrEmpty(rowData.getBillOfEntryPR())
							&& !Strings.isNullOrEmpty(
									rowData.getBillOfEntryDatePR())
							&& !Strings
									.isNullOrEmpty(rowData.getPortCodePR())) {
						LocalDate docDate2B = isDateValid(
								rowData.getDocumentDatePR(),
								rowData.getInvoiceKeyUploadPR(), PR,
								validationResult);

						docNo2BValid = isDocNo2BValid(
								rowData.getDocumentNumber2B(),
								rowData.getInvoiceKeyUpload2B(), activeDocNo2B,
								validationResult, docDate2B, rowData.getUserResponse());
					}

					isGSTR2BBaseResponseValid(docNo2BValid, validationResult);
				}

				boolean isIGST2BValid = is2BAmountValid(rowData.getIgst2B());
				boolean isCGST2BValid = is2BAmountValid(rowData.getCgst2B());
				boolean isSGST2BValid = is2BAmountValid(rowData.getSgst2B());
				boolean isCess2BValid = is2BAmountValid(rowData.getCess2B());

				// removed based on US. 71139

				/*
				 * if (isAvailableIGSTValid && isIGST2BValid) {
				 * isCompareAvailableAndTaxAmt(rowData.getIgst2B(),
				 * rowData.getAvailableIGST(), "AvailableIGST",
				 * validationResult); } if (isAvailableCGSTValid &&
				 * isCGST2BValid) {
				 * isCompareAvailableAndTaxAmt(rowData.getCgst2B(),
				 * rowData.getAvailableCGST(), "AvailableCGST",
				 * validationResult); } if (isAvailableSGSTValid &&
				 * isSGST2BValid) {
				 * isCompareAvailableAndTaxAmt(rowData.getSgst2B(),
				 * rowData.getAvailableSGST(), "AvailableSGST",
				 * validationResult); } if (isAvailableCessValid &&
				 * isCess2BValid) {
				 * isCompareAvailableAndTaxAmt(rowData.getCess2B(),
				 * rowData.getAvailableCESS(), "AvailableCESS",
				 * validationResult); }
				 * 
				 */ }

		}

	}

	private void checkForDuplicateImsUniqueId(String uniqueIdIms,
			List<ProcessingResult> validationResult,
			Set<String> uniqueImsKeyIds) {
		String errMsg = String.format("Duplicate UniqueID");

		/*if (!isPresent(uniqueIdIms)) {

			ProcessingResult result = new ProcessingResult("UPLOAD", "ER104",
					errMsg);

			validationResult.add(result);
			return;
		}*/
		
		if(!Strings.isNullOrEmpty(uniqueIdIms))
		{
		String response = (uniqueIdIms).toString().trim();
		if (uniqueImsKeyIds != null
				&& uniqueImsKeyIds.contains(response.toUpperCase())) {
			ProcessingResult result = new ProcessingResult("UPLOAD", "ER104",
					errMsg);
			validationResult.add(result);

		} else {
			uniqueImsKeyIds.add(response.toUpperCase());
		}
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

	private void isBillOfEntryDate2BValid(String billOfEntryDate2B,
			List<ProcessingResult> validationResult) {

		String errMsg = "Bill of Entry Date (2B) format is incorrect";

		if (!isPresent(billOfEntryDate2B)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		LocalDate dateInstance = DateFormatForStructuralValidatons
				.parseObjToDate(billOfEntryDate2B.trim());

		if (dateInstance == null || (dateInstance.getMonthValue() > 12)) {
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

	private void isBillOfEntry2BValid(String billOfEntry2B,
			List<ProcessingResult> validationResult) {

		if (!isPresent(billOfEntry2B)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					"Bill of Entry Number (2B) format is incorrect");
			validationResult.add(result);
			return;
		}

		billOfEntry2B = removeQuotes(billOfEntry2B);

		if (billOfEntry2B.length() > 20
				&& !billOfEntry2B.matches("[a-zA-Z0-9]+")) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					"Bill of Entry Number (2B) format is incorrect");
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

		if (billOfEntryPR.length() > 20
				&& !billOfEntryPR.matches("[a-zA-Z0-9]+")) {
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

	private void isGSTR2BBaseResponseValid(boolean docNo2BValid,
			List<ProcessingResult> validationResult) {
		String errMsg = "Documents cannot be locked without corresponding GSTR-2B record";
		if (!docNo2BValid) {
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

	private void isTableTypeValid(String tableType,
			List<ProcessingResult> validationResult, String retPeriod,
			String invKeyUploaded, String taxPeriodType) {

		if (!isValidationrequired(retPeriod, invKeyUploaded, taxPeriodType)) {
			return;
		}

		String errMsg = "Table Type (2B) is blank or doesn't have correct value";
		if (!isPresent(tableType)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		if (Stream
				.of("GETGSTR2B_B2B_HEADER", "GETGSTR2B_B2BA_HEADER",
						"GETGSTR2B_CDNR_HEADER", "GETGSTR2B_CDNRA_HEADER",
						"GETGSTR2B_ISD_HEADER", "GETGSTR2B_ISDA_HEADER",
						"GETGSTR2B_IMPG_HEADER", "GETGSTR2B_IMPGSEZ_HEADER",
						"GETGSTR2B_ECOM_HEADER", "GETGSTR2B_ECOMA_HEADER")
				.noneMatch(tableType.trim()::equalsIgnoreCase)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;

		}

	}

	private void crossTagging(GSTR2bprAutoReconRespUploadDTO rowData,
			List<ProcessingResult> validationResult) {

		String errMsg = "Import record cannot be locked with Non-import record.";

		if (!Strings.isNullOrEmpty(rowData.getBillOfEntryPR())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR())) {

			if (Strings.isNullOrEmpty(rowData.getBillOfEntry2B())
					&& Strings.isNullOrEmpty(rowData.getBillOfEntryDate2B())) {

				if (!Strings.isNullOrEmpty(rowData.getRecipientGstin2B())
						&& !Strings.isNullOrEmpty(rowData.getSupplierGstin2B())
						&& !Strings.isNullOrEmpty(rowData.getDocType2B())
						&& !Strings.isNullOrEmpty(rowData.getDocumentNumber2B())
						&& !Strings
								.isNullOrEmpty(rowData.getDocumentDate2B())) {
					ProcessingResult result = new ProcessingResult(UPLOAD,
							ERROR_CODE, errMsg);
					validationResult.add(result);
					return;
				}

			} else if (!Strings.isNullOrEmpty(rowData.getBillOfEntry2B())
					&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDate2B())) {
				Set<String> activeDocNum2B = null;
				// Do a look of key columns and throw respective error
				try {
					String queryString = "SELECT DISTINCT B2_BILL_OF_ENTRY, B2_BILL_OF_ENTRY_DATE FROM "
							+ "TBL_LINK_2B_PR WHERE IS_ACTIVE = TRUE AND "
							+ "B2_BILL_OF_ENTRY  IN (?1) ;";
					Query q = entityManager.createNativeQuery(queryString);
					q.setParameter(1, rowData.getBillOfEntry2B());
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

		} else if (!Strings.isNullOrEmpty(rowData.getBillOfEntry2B())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDate2B())) {

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
							+ "TBL_LINK_2B_PR WHERE IS_ACTIVE = TRUE AND "
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

	private void isResponseValid(String response,
			List<ProcessingResult> validationResult) {

		String errMsg = "Response Is Not Uploaded In Predefined Format";

		if (!isPresent(response)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		if (Stream.of("LOCK", "UNLOCK", "LOCK2")
				.noneMatch(response.trim()::equalsIgnoreCase)) {

			response = removeQuotes(response);
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

	private boolean is3BTaxPeriodRespValid(String retPeriod,
			List<ProcessingResult> validationResult) {

		String errMsg = "3B Tax Period Value Is Not In MMYYYY format";
		boolean isValid = true;
		if (!isPresent(retPeriod)) {
			return false;
		}
		retPeriod = removeQuotes(retPeriod);

		retPeriod = retPeriod.trim();

		if (!retPeriod.matches(TAX_PERIOD_REGEX) || retPeriod.length() != 6) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return false;

		}

		int month = Integer.parseInt(retPeriod.substring(0, 2));
		if (retPeriod.length() != 6 || month > 12 || month == 00) {
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

		retPeriod = removeQuotes(retPeriod);

		retPeriod = retPeriod.trim();

		if (!retPeriod.matches(TAX_PERIOD_REGEX) || retPeriod.length() != 6) {
			return false;

		}

		int month = Integer.parseInt(retPeriod.substring(0, 2));
		if (retPeriod.length() != 6 || month > 12 || month == 00) {
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

		if (B2.equalsIgnoreCase(taxPeriodType)) {

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

	private void is2BPRTaxPeriodValid(String retPeriod, String invKeyUploaded,
			String userResponse, List<ProcessingResult> validationResult,
			String taxPeriodType) {

		if (!isValidationrequired(retPeriod, invKeyUploaded, taxPeriodType)) {
			return;
		}

		String errMsg = String.format(
				"Taxperiod %s cannot Be Blank Or Beyond GSTR3B TaxPeriod",
				taxPeriodType);

		if (!isPresent(retPeriod)) {
			/*
			 * ProcessingResult result = new ProcessingResult(UPLOAD,
			 * ERROR_CODE, errMsg); validationResult.add(result);
			 */

			return;
		}

		retPeriod = removeQuotes(retPeriod);

		retPeriod = retPeriod.trim();

		if (!retPeriod.matches(TAX_PERIOD_REGEX) || retPeriod.length() != 6) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;

		}

		int month = Integer.parseInt(retPeriod.substring(0, 2));
		if (retPeriod.length() != 6 || month > 12 || month == 00) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;
		}

		if (is3BTaxPeriodValid(userResponse)) {
			Integer taxPeriod2APR = GenUtil.getDerivedTaxPeriod(retPeriod);

			userResponse = removeQuotes(userResponse);

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
				.format("Recipient GSTIN (%s) Format Is Incorrect", gstinType);

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
		}
	}

	private void isSupplierGstinValid(String supplierGstin,
			String invKeyUploaded, String gstinType,
			List<ProcessingResult> validationResult) {

		if (!isValidationrequired(supplierGstin, invKeyUploaded, gstinType)) {
			return;
		}

		String errMsg = String.format("Supplier GSTIN (%s) Format Is Incorrect",
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
				if (supplierGstin.toUpperCase().length() != 15
						|| !supplierGstin.toUpperCase().matches("[A-Za-z0-9]+")
						|| supplierGstin.toUpperCase().matches("[A-Za-z]+")
						|| supplierGstin.toUpperCase().matches("[0-9]+")) {
					ProcessingResult result = new ProcessingResult(UPLOAD,
							ERROR_CODE, errMsg);

					validationResult.add(result);
				}
			}

		} else {

			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
		}

	}

	private void isDocType2BValid(String docType, String invKeyUploaded,
			List<ProcessingResult> validationResult) {

		if (!isPresent(invKeyUploaded)) {
			if (!isPresent(docType)) {

				return;
			}

		}

		String errMsg = "DOC-TYPE (2B) Format Is Incorrect";

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
			return;
		}
		if (Stream.of("R", "CR", "DR", "C", "D", "DE", "SEWP", "SEWOP", "CBW")
				.noneMatch(docType.trim()::equalsIgnoreCase)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
		}

	}

	private void isDocTypePRValid(String docType, String invKeyUploaded,
			List<ProcessingResult> validationResult) {

		if (!isPresent(invKeyUploaded)) {
			if (!isPresent(docType)) {

				return;
			}

		}

		String errMsg = "DOC-TYPE (PR) Format Is Incorrect";

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
			return;
		}
		if (Stream.of("INV", "R", "CR", "DR", "C", "D")
				.noneMatch(docType.trim()::equalsIgnoreCase)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
		}

	}

	private LocalDate isDateValid(String date, String invKeyUploaded,
			String dateDesc, List<ProcessingResult> validationResult) {

		if (!isValidationrequired(date, invKeyUploaded, dateDesc)) {
			return null;
		}

		String errMsg = String.format("Document Date (%s) Format Is Incorrect",
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
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return null;
		}
		return dateInstance;
	}

	private void isDocNoPRValid(String docNoPR, String invKeyUploaded,
			List<ProcessingResult> validationResult, Set<String> activeDocNoPR,
			LocalDate docDatePR, String response) {

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

		docNoPR = removeQuotes(docNoPR);

		if (docDatePR != null) {
			docNoPR = docNoPR + "-" + docDatePR;
		}

		if (!activeDocNoPR.contains(docNoPR)) {
	        if (!"unlock".equalsIgnoreCase(response)) {
	            ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
	                    "Document Number(PR) Does Not Exist In DigiGST Master");
	            validationResult.add(result);
	        } else {
	            LOGGER.debug("Response is unlock; bypassing document existence validation for PR");
	        }
	    }

	}

	private boolean isDocNo2BValid(String docNo2B, String invKeyUploaded,
			Set<String> activeDocNo2B, List<ProcessingResult> validationResult,
			LocalDate docDate2B, String response) {

		if (!isPresent(invKeyUploaded)) {
			if (!isPresent(docNo2B)) {

				return false;
			}

		}

		if (!isPresent(docNo2B)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					"Document Number(2B) cannot be empty");
			validationResult.add(result);
			return false;
		}
		docNo2B = removeQuotes(docNo2B);

		if (docDate2B != null) {
			docNo2B = docNo2B + "-" + docDate2B;
		}

		String msg = String.format("docNo2B - %s", docNo2B);

		LOGGER.debug(msg);

		activeDocNo2B.forEach(
				docNo -> LOGGER.debug("Active Document Number 2B: {}", docNo));
		
		if (!activeDocNo2B.contains(docNo2B)) {
	        // If the response is NOT "unlock", then add the error; otherwise bypass.
	        if (!"unlock".equalsIgnoreCase(response)) {
	            ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
	                    "Document Number(2B) Does Not Exist In DigiGST Master");
	            validationResult.add(result);
	            return false;
	        } else {
	            LOGGER.debug("Response is unlock; bypassing document existence validation for 2B");
	            return true;
	        }
	    }
		return true;
	}

	private boolean isAmountValid(String amt, String amtDesc,
			List<ProcessingResult> validationResult) {

		String errMsg = String.format("%s is not in Expected format", amtDesc);

		if (!isPresent(amt)) {
			return false;
		}

		if (!NumberFomatUtil.isNumber(amt)) {

			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return false;
		}
		boolean isValid = NumberFomatUtil.is17And2digValidDec(amt.trim());

		if (!isValid) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return false;
		}
		return true;
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
				.parseObjToDate(date.trim());

		if (dateInstance == null || (dateInstance.getMonthValue() > 12)) {
			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return null;
		}
		return dateInstance;
	}

	private void isCompareAvailableAndTaxAmt(String taxAmt, String availableAmt,
			String amtDesc, List<ProcessingResult> validationResult) {

		String errMsg = String.format(
				"%s tax amount should not be more than 2B tax amount.",
				amtDesc);

		if (!isPresent(taxAmt) && !isPresent(availableAmt)) {
			return;
		}
		taxAmt = taxAmt.trim();
		availableAmt = availableAmt.trim();
		BigDecimal taxAmtBd = new BigDecimal(taxAmt);
		BigDecimal availableAmtBD = new BigDecimal(availableAmt);

		if (availableAmtBD.compareTo(taxAmtBd) == 1) {

			ProcessingResult result = new ProcessingResult(UPLOAD, ERROR_CODE,
					errMsg);
			validationResult.add(result);
			return;

		}

	}

	private boolean is2BAmountValid(String amt) {

		if (!isPresent(amt)) {
			return false;
		}

		if (!NumberFomatUtil.isNumber(amt)) {

			return false;
		}
		boolean isValid = NumberFomatUtil.is17And2digValidDec(amt.trim());

		if (!isValid) {
			return false;
		}
		return true;
	}

	public Set<String> getActive2BDocNumbers(List<String> docNum2B, boolean skipValidation) {
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
			String queryString = "SELECT DISTINCT B2_DOC_NUM,B2_DOC_DATE FROM "
					+ "TBL_LINK_2B_PR WHERE IS_ACTIVE = TRUE AND "
					+ "B2_DOC_NUM  IN (?1) ;";
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter(1, docNum2B);
			String msg = String.format(
					"Inside TBL_AUTO_2BPR_LINK docNum2B,query - %s",
					queryString);

			LOGGER.debug(msg);

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			activeInvKey2A = list.stream().map(o -> convertObjToString(o))
					.collect(Collectors.toCollection(HashSet::new));

		} catch (Exception ex) {
			String msg = "Exception Occured in "
					+ "getActive2BDocNumbers() method";
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
					+ "TBL_LINK_2B_PR WHERE IS_ACTIVE = TRUE AND "
					+ "PR_DOC_NUM  IN (?1) ;";
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter(1, docNumPR);
			String msg = String.format(
					"Inside TBL_AUTO_2BPR_LINK docNumPR,query - %s ",
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
		try {
			String queryString = "SELECT DISTINCT INVOICEKEYPR, INVOICEKEYB2 FROM "
					+ B2PR_ENDDTM_NULL
					+ "  INVOICEKEYPR  IN (?1)  AND INVOICEKEYB2 IN (?2) AND  "
					+ "  INVOICEKEYB2 IS NOT NULL AND INVOICEKEYPR IS NOT NULL;";
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter(1, invKeyPR);
			q.setParameter(2, invKey2A);
			String msg = String.format(
					"Inside TBL_2BPR_RECON_RESP_PSD getActiveInvoiceKeys,query - %s ",
					queryString);

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

	public Set<String> getActiveInvoice2BKeys(List<String> invKey2B) {
		Set<String> activeInvoice2B = null;
		try {

			String queryString = "SELECT DISTINCT INVOICEKEYB2 FROM "
					+ B2PR_ENDDTM_NULL
					+ "  INVOICEKEYB2 IN (?1) AND INVOICEKEYB2 IS NOT NULL"
					+ "  AND INVOICEKEYPR IS NULL;";
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter(1, invKey2B);
			String msg = String
					.format("Inside TBL_2BPR_RECON_RESP_PSD INVOICEKEYA2, "
							+ " query - %s ", queryString);

			LOGGER.debug(msg);

			@SuppressWarnings("unchecked")
			List<Object> list = q.getResultList();
			activeInvoice2B = list.stream()
					.map(o -> convertSingleObjToString(o))
					.collect(Collectors.toCollection(HashSet::new));

		} catch (Exception ex) {
			String msg = "Exception Occured in "
					+ "getActiveInvoice2BKeys() method";
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return activeInvoice2B;
	}

	public Set<String> getActiveInvoicePRKeys(List<String> invKeyPR) {
		Set<String> activeInvoicePR = null;
		try {

			String queryString = "SELECT DISTINCT INVOICEKEYPR FROM "
					+ B2PR_ENDDTM_NULL
					+ "  INVOICEKEYPR IN (?1) AND INVOICEKEYPR IS NOT "
					+ "  NULL AND INVOICEKEYB2 IS NULL;";
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter(1, invKeyPR);
			String msg = String
					.format("Inside TBL_2BPR_RECON_RESP_PSD INVOICEKEYPR, "
							+ " query - %s ", queryString);

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
		return String.valueOf(obj[0]) + "-" + obj[1];
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
