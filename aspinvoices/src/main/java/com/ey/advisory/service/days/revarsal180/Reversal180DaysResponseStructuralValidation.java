package com.ey.advisory.service.days.revarsal180;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import com.ey.advisory.app.caches.EInvoiceDocTypeCache;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("Reversal180DaysResponseStructuralValidation")
public class Reversal180DaysResponseStructuralValidation {

	@Autowired
	@Qualifier("DefaultEInvoiceDocTypeCache")
	private EInvoiceDocTypeCache docTypeCache;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String UPLOAD = "Upload";
	private static final String RESP_NOT_ALLOWED = "Response is not allowed";
	private static final String INVALID = "Invalid %s .";
	private static final String DOC_DATE = "DocumentDate";
	private static final String INV_VAL = "InvoiceValue";
	private static final String STAT_DEDC_AMT = "StatutoryDeductionAmount";
	private static final String ANY_OTHER_AMT = "AnyOtherDeductionAmount";
	private static final String PMT_DUE_DATE = "DueDateofPayment";
	private static final String PMT_REF_DATE = "PaymentReferenceDate";
	private static final String AMT_PAID_TO_SUPP = "PaidAmounttoSupplier";
	private static final String AMT_UNPAID_TO_SUPP = "UnpaidAmounttoSupplier";
	private static final String POSTING_DATE = "PostingDate";
	private static final String DOC_DATE_PLUS_180 = "DocDate+180Days";
	private static final String RETPERIOD_PR = "ReturnPeriod-PR";
	private static final String RETPERIOD_RECON_RESP = "ReturnPeriod-ReconResponse";
	private static final String NO_REVERSAL = "No Reversal";
	private static final String REVERSAL_RECLAIM = "Reversal & Reclaim";
	private static final String RECLAIM = "Reclaim";
	private static final String REVERSAL = "Reversal";
	private static final String UR_RECLAIM = "UserResponse-TaxPeriod (ITC Reclaim)";
	private static final String UR_REVERSAL = "UserResponse-TaxPeriod (ITC Reversal)";
	private static final String ERRCODE_BLANK = "ER1055";
	private static final String ERRCODE_DESC = "ER1056";

	private static final String IGST_TAX_PAID_PR = "IGSTTaxPaid-PR";
	private static final String CGST_TAX_PAID_PR = "CGSTTaxPaid-PR";
	private static final String SGST_TAX_PAID_PR = "SGSTTaxPaid-PR";
	private static final String CESS_TAX_PAID_PR = "CessTaxPaid-PR";
	private static final String AVLBLE_IGST_PR = "AvailableIGST-PR";
	private static final String AVLBLE_CGST_PR = "AvailableCGST-PR";
	private static final String AVLBLE_SGST_PR = "AvailableSGST-PR";
	private static final String AVLBLE_CESS_PR = "AvailableCess-PR";

	private static final String ITC_REV_RETPERIOD = "ITCReversalReturnPeriod(DigiGST)-Indicative";
	private static final String REV_IGST_DIGI = "ReversalofIGST(DigiGST)-Indicative";
	private static final String REV_CGST_DIGI = "ReversalofCGST(DigiGST)-Indicative";
	private static final String REV_SGST_DIGI = "ReversalofSGST(DigiGST)-Indicative";
	private static final String REV_CESS_DIGI = "ReversalofCess(DigiGST)-Indicative";

	private static final String RECLAIM_RETPERIOD = "ReClaimReturnPeriod(DigiGST)-Indicative";
	private static final String RECLAIM_IGST = "ReClaimofIGST-Indicative";
	private static final String RECLAIM_CGST = "ReClaimofCGST-Indicative";
	private static final String RECLAIM_SGST = "ReClaimofSGST-Indicative";
	private static final String RECLAIM_CESS = "ReClaimofCess-Indicative";

	private static final String ITC_REVERSAL_COMPUTE_DATE_AND_TIME = "ITCReversalComputeDate&Time";
	private static final String RECONCILIATION_DATE_AND_TIME = "ReconciliationDate&Time&Time";
	private static final String ITCREVERSAL_COMPUTEREQUEST_ID = "ITCReversalComputeRequestID";
	private static final String RECONCILIATION_REQUESTID = "ReconciliationRequestID";

	public void rowDataValidation(List<ProcessingResult> validationResult,
			Reversal180DaysResponseUploadDto rowData, List<String> gstinList) {

		isActionTypeValid(rowData.getActionType(), validationResult);

		isCustGstinValid(rowData.getCustGstin(), validationResult, gstinList);
		isSupplierGstinValid(rowData.getSupplierGstin(), rowData.getCustGstin(),
				validationResult);
		isDocumentTypeValid(rowData.getDocumentType(), validationResult);
		isDocumentNoValid(rowData.getDocumentNum(), validationResult);

		isDateBlankAndValid(rowData.getDocumentDate(), DOC_DATE, "ER1044",
				"ER1043", validationResult);

		isAmountValid(rowData.getInvoiceValue(), INV_VAL, "ER1058",
				validationResult);

		isStatutoryDeductionsApplicableValid(
				rowData.getStatDeductionApplicable(), validationResult);
		isAmountValid(rowData.getStatDeductionAmt(), STAT_DEDC_AMT, "ER1060",
				validationResult);
		isAmountValid(rowData.getAnyOtherDeductionAmt(), ANY_OTHER_AMT,
				"ER1061", validationResult);
		isDateValid(rowData.getPaymentDueDate(), PMT_DUE_DATE, "ER1062",
				validationResult);

		isPaymentReferenceNumberValid(rowData.getPaymentRefNumber(),
				validationResult);

		isDateBlankAndValid(rowData.getPaymentRefDate(), PMT_REF_DATE, "ER1065",
				"ER1066", validationResult);

		isPaymentStatusValid(rowData.getPaymentStatus(), validationResult);

		isAmountValid(rowData.getPaidAmtToSupplier(), AMT_PAID_TO_SUPP,
				"ER1068", validationResult);

		isExchangeRateValid(rowData.getExchangeRate(), validationResult);

		isAmountValid(rowData.getPaidAmtToSupplier(), AMT_UNPAID_TO_SUPP,
				"ER1069", validationResult);

		isDateValid(rowData.getPostingDate(), POSTING_DATE, "ER1114",
				validationResult);

		isDateValid(rowData.getDocDate180Days(), DOC_DATE_PLUS_180, "ER1070",
				validationResult);

		isOtherTaxPeriodValid(rowData.getRetPeriodPR(), RETPERIOD_PR, "ER1071",
				"ER1072", true, validationResult);

		isOtherTaxPeriodValid(rowData.getRetPeriodReconResp(),
				RETPERIOD_RECON_RESP, "ER1073", "ER1074", true,
				validationResult);

		isAmountValid(rowData.getIgstTaxPaidPR(), IGST_TAX_PAID_PR, "ER1075",
				validationResult);

		isAmountValid(rowData.getCgstTaxPaidPR(), CGST_TAX_PAID_PR, "ER1076",
				validationResult);

		isAmountValid(rowData.getSgstTaxPaidPR(), SGST_TAX_PAID_PR, "ER1077",
				validationResult);

		isAmountValid(rowData.getCessTaxPaidPR(), CESS_TAX_PAID_PR, "ER1078",
				validationResult);

		isAmountValid(rowData.getAvailableIgstPR(), AVLBLE_IGST_PR, "ER1079",
				validationResult);

		isAmountValid(rowData.getAvailableCgstPR(), AVLBLE_CGST_PR, "ER1080",
				validationResult);

		isAmountValid(rowData.getAvailableSgstPR(), AVLBLE_SGST_PR, "ER1081",
				validationResult);

		isAmountValid(rowData.getAvailableCessPR(), AVLBLE_CESS_PR, "ER1082",
				validationResult);

		isOtherTaxPeriodValid(rowData.getItcRevRetPrdDigiIndicative(),
				ITC_REV_RETPERIOD, "ER1083", "ER1084", true, validationResult);

		isAmountValid(rowData.getRevIgstDigiIndicative(), REV_IGST_DIGI,
				"ER1085", validationResult);

		isAmountValid(rowData.getRevCgstDigiIndicative(), REV_CGST_DIGI,
				"ER1086", validationResult);

		isAmountValid(rowData.getRevSgstDigiIndicative(), REV_SGST_DIGI,
				"ER1087", validationResult);

		isAmountValid(rowData.getRevCessDigiIndicative(), REV_CESS_DIGI,
				"ER1088", validationResult);

		isOtherTaxPeriodValid(rowData.getReclaimRetPrdDigiIndicative(),
				RECLAIM_RETPERIOD, "ER1089", "ER1090", true, validationResult);

		isAmountValid(rowData.getReclaimIgstIndicative(), RECLAIM_IGST,
				"ER1091", validationResult);

		isAmountValid(rowData.getReclaimCgstIndicative(), RECLAIM_CGST,
				"ER1092", validationResult);

		isAmountValid(rowData.getReclaimSgstIndicative(), RECLAIM_SGST,
				"ER1093", validationResult);

		isAmountValid(rowData.getReclaimCessIndicative(), RECLAIM_CESS,
				"ER1094", validationResult);

		isRevReclaimStatusValid(rowData.getItcRevReclaimStatusDigi(),
				validationResult);

		isTimeStampValid(rowData.getItcRevComputeDateTime(),
				ITC_REVERSAL_COMPUTE_DATE_AND_TIME, "ER1102", validationResult);
		isTimeStampValid(rowData.getItcRevComputeDateTime(),
				RECONCILIATION_DATE_AND_TIME, "ER1103", validationResult);

		isIdValid(rowData.getComputeId(), ITCREVERSAL_COMPUTEREQUEST_ID,
				"ER1098", validationResult);
		isIdValid(rowData.getReconReportConfigID(), RECONCILIATION_REQUESTID,
				"ER1100", validationResult);

	}

	private void isOtherTaxPeriodValid(String retPeriod, String retPeriodDesc,
			String errCode, String errCodeBlank, boolean isBlankAllowed,
			List<ProcessingResult> validationResult) {

		String errMsg = null;
		if (!isPresent(retPeriod) && !isBlankAllowed) {

			errMsg = String.format("%s cannot be left blank.", retPeriodDesc);
			ProcessingResult result = new ProcessingResult(UPLOAD, errCodeBlank,
					errMsg);
			validationResult.add(result);
			return;

		}

		if (!isPresent(retPeriod)) {
			return;
		}
		if (retPeriod.contains("'")) {
			String returnPeriodWithOutQuotes = retPeriod.replace("'", "");
			retPeriod = returnPeriodWithOutQuotes;
		}

		String taxPeriod = retPeriod.trim();

		if (!taxPeriod.matches("[0-9]+") || taxPeriod.length() != 6) {
			errMsg = String.format(INVALID, retPeriodDesc);
			ProcessingResult result = new ProcessingResult(UPLOAD, errCode,
					errMsg);
			validationResult.add(result);
			return;

		}

		if (taxPeriod.matches("[0-9]+")) {

			int month = Integer.parseInt(taxPeriod.substring(0, 2));
			if (taxPeriod.length() != 6 || month > 12 || month == 00) {
				errMsg = String.format(INVALID, retPeriodDesc);
				ProcessingResult result = new ProcessingResult(UPLOAD, errCode,
						errMsg);
				validationResult.add(result);
			}
		}

	}

	private boolean isTaxperiodValid(String taxPeriod) {

		if (!isPresent(taxPeriod)) {
			return false;
		}
		if (taxPeriod.contains("'")) {
			String returnPeriodWithOutQuotes = taxPeriod.replace("'", "");
			taxPeriod = returnPeriodWithOutQuotes;
		}

		taxPeriod = taxPeriod.trim();

		if (!taxPeriod.matches("[0-9]+") || taxPeriod.length() != 6) {
			return false;

		}

		if (taxPeriod.matches("[0-9]+")) {

			int month = Integer.parseInt(taxPeriod.substring(0, 2));
			if (taxPeriod.length() != 6 || month > 12 || month == 00) {
				return false;
			}
		}
		return true;
	}

	public ProcessingResult isResponseValid(String retPeriodRev,
			String retPeriodReclaim, String status) {
		ProcessingResult validationResult = null;

		if (REVERSAL.equalsIgnoreCase(status)) {

			if (!isPresent(retPeriodRev)) {
				String errDesc = String.format(INVALID, UR_REVERSAL);
				return new ProcessingResult(UPLOAD, ERRCODE_BLANK, errDesc);
			} else if (isPresent(retPeriodReclaim)) {
				String errDesc = " Reclaim tax period cannot be given for Reversal record";
				return new ProcessingResult(UPLOAD, "ER1096", errDesc);
			} else if (isPresent(retPeriodRev)) {
				boolean isValid = isTaxperiodValid(retPeriodRev);
				if (!isValid) {
					String errDesc = String.format(INVALID, UR_REVERSAL);
					return new ProcessingResult(UPLOAD, ERRCODE_DESC, errDesc);
				}
			}

		} else if (RECLAIM.equalsIgnoreCase(status)) {
			if (!isPresent(retPeriodReclaim)) {
				String errDesc = String.format(INVALID, UR_RECLAIM);
				return new ProcessingResult(UPLOAD, ERRCODE_BLANK, errDesc);
			} else if (isPresent(retPeriodRev)) {
				String errDesc = "Reversal tax period cannot be given for Reclaim record";
				return new ProcessingResult(UPLOAD, "ER1097", errDesc);
			} else if (isPresent(retPeriodReclaim)) {
				boolean isValid = isTaxperiodValid(retPeriodReclaim);
				if (!isValid) {
					String errDesc = String.format(INVALID, UR_RECLAIM);
					return new ProcessingResult(UPLOAD, ERRCODE_DESC, errDesc);
				}
			}

		} else if (REVERSAL_RECLAIM.equalsIgnoreCase(status)) {
			if (!isPresent(retPeriodRev) && !isPresent(retPeriodReclaim)) {
				String errDesc = String.format(INVALID + INVALID, UR_REVERSAL,
						UR_RECLAIM);
				return new ProcessingResult(UPLOAD, ERRCODE_BLANK, errDesc);
			} else if (isPresent(retPeriodRev)) {
				boolean isValid = isTaxperiodValid(retPeriodRev);
				if (!isValid) {
					String errDesc = String.format(INVALID, UR_REVERSAL);
					return new ProcessingResult(UPLOAD, ERRCODE_DESC, errDesc);
				}
			} else if (isPresent(retPeriodReclaim)) {
				boolean isValid = isTaxperiodValid(retPeriodReclaim);
				if (!isValid) {
					String errDesc = String.format(INVALID, UR_RECLAIM);
					return new ProcessingResult(UPLOAD, ERRCODE_DESC, errDesc);
				}
			}
		} else if (NO_REVERSAL.equalsIgnoreCase(status)) {
			if (isPresent(retPeriodRev) || isPresent(retPeriodReclaim)) {
				String errDesc = RESP_NOT_ALLOWED;
				return new ProcessingResult(UPLOAD, ERRCODE_DESC, errDesc);
			}
		}
		return validationResult;
	}

	private void isActionTypeValid(String action,
			List<ProcessingResult> validationResult) {

		if (!isPresent(action)) {
			return;
		}

		if (Stream.of("CAN").noneMatch(action.trim()::equalsIgnoreCase)) {
			String errMsg = "Invalid Action Type.";
			String errorCode = "ER1057";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);

		}
	}

	private void isCustGstinValid(String custGstin,
			List<ProcessingResult> validationResult, List<String> gstinList) {

		String errorCode = null;
		if (!isPresent(custGstin)) {
			errorCode = "ER1035";
			String errMsg = "Customer GSTIN cannot be blank";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}

		String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(custGstin.trim());

		String vendorGstin = custGstin.trim();
		if (!matcher.matches() || vendorGstin.length() != 15) {
			errorCode = "ER1034";
			String errMsg = "Invalid Customer GSTIN.";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			return;
		}
		if (!gstinList.contains(vendorGstin)) {
			errorCode = "ER1036";
			String errMsg = "Customer GSTIN is not available in DigiGST.";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
		}

	}

	private void isSupplierGstinValid(String supplierGstin, String custGstin,
			List<ProcessingResult> validationResult) {

		String errorCode = "ER1052";
		String errMsg = null;
		if (!isPresent(supplierGstin)) {
			return;
		}
		if (GSTConstants.URP.equalsIgnoreCase(supplierGstin.trim())) {
			return;
		}
		if (isPresent(supplierGstin) && isPresent(custGstin)) {

			if (custGstin.equalsIgnoreCase(supplierGstin)) {

				errMsg = String.format("Customer GSTIN and Supplier GSTIN "
						+ "can not be same (%s).", custGstin);
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;

			}
		}

		supplierGstin = supplierGstin.trim();
		if (supplierGstin.trim().length() == 15
				|| supplierGstin.trim().length() == 10) {

			if (supplierGstin.trim().length() == 15) {
				String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
						+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
						+ "[A-Za-z0-9][A-Za-z0-9]$";

				Pattern pattern = Pattern.compile(regex);

				Matcher matcher = pattern.matcher(supplierGstin.trim());

				supplierGstin = supplierGstin.trim();
				if (!matcher.matches() || supplierGstin.length() != 15) {
					errMsg = "Invalid Supplier GSTIN.";
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCode, errMsg);

					validationResult.add(result);
				}
			}

			if (supplierGstin.trim().length() == 10) {
				String regex = "^[A-Za-z][A-Za-z][A-Za-z]"
						+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z]$";

				String regex1 = "^[A-Za-z][A-Za-z][A-Za-z]"
						+ "[A-Za-z][0-9][0-9][0-9][0-9][0-9][A-Za-z]$";
				Pattern pattern = Pattern.compile(regex);

				Pattern pattern1 = Pattern.compile(regex1);

				Matcher matcher = pattern.matcher(supplierGstin.trim());
				Matcher matcher1 = pattern1.matcher(supplierGstin.trim());
				if (matcher.matches() || matcher1.matches()) {

				} else {
					errMsg = "Invalid Supplier GSTIN.";
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCode, errMsg);

					validationResult.add(result);
				}
			}

		} else {
			errMsg = "Invalid Supplier GSTIN.";

			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
		}

	}

	private void isDocumentTypeValid(String docType,
			List<ProcessingResult> validationResult) {

		String errorCode = null;
		String errMsg = null;

		if (!isPresent(docType)) {
			errMsg = "Document Type cannot be left blank.";
			errorCode = "ER1037";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			return;
		}

		if (docType.trim().length() > 5) {
			errMsg = "Invalid Document Type.";
			errorCode = "ER1038";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			return;

		}

		docTypeCache = StaticContextHolder.getBean(
				"DefaultEInvoiceDocTypeCache", EInvoiceDocTypeCache.class);
		int n = docTypeCache
				.finddocType(trimAndConvToUpperCase(docType.trim()));
		if (n == 0) {
			errorCode = "ER1038";
			errMsg = "Invalid Document Type.";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);

		}

	}

	private void isDocumentNoValid(String docNo,
			List<ProcessingResult> validationResult) {

		String errorCode = null;
		String errMsg = null;

		if (!isPresent(docNo)) {
			errMsg = "Document Number cannot be left blank.";
			errorCode = "ER1041";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			return;
		}

		if (docNo.length() > 16) {
			errMsg = " Invalid Document Number.";
			errorCode = "ER1042";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}

		String regex = "^[a-zA-Z0-9/-]*$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(docNo.trim());
		if (!matcher.matches()) {
			errMsg = " Invalid Document Number.";
			errorCode = "ER1042";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);

		}
	}

	private void isDateBlankAndValid(String date, String dateDesc,
			String errorCode, String errorCodeBlank,
			List<ProcessingResult> validationResult) {

		String errMsg = null;

		if (!isPresent(date)) {
			errMsg = String.format("%s cannot be left blank.", dateDesc);
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			return;
		}

		LocalDate dateInstance = DateFormatForStructuralValidatons
				.parseObjToDate(date.trim());

		if (dateInstance == null || (dateInstance.getMonthValue() > 12)) {
			errMsg = String.format(INVALID, dateDesc);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);

		}

	}

	private void isAmountValid(String amt, String amtDesc, String errorCode,
			List<ProcessingResult> validationResult) {
		String errMsg = null;
		if (!isPresent(amt)) {
			return;
		}
		

		if (amt.contains("'")) {
			amt = amt.replace("'", "");
		}


		if (!NumberFomatUtil.isNumber(amt)) {

			errMsg = String.format(INVALID, amtDesc);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
		boolean isValid = NumberFomatUtil.is17And2digValidDec(amt.trim());

		if (!isValid) {
			errMsg = String.format(INVALID, amtDesc);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
		}

	}

	private void isStatutoryDeductionsApplicableValid(String isStatDedcAppl,
			List<ProcessingResult> validationResult) {

		String errorCode = "ER1059";

		if (!isPresent(isStatDedcAppl)) {
			return;
		}
		String response = isStatDedcAppl.trim();
		if (Stream.of("Y", "N").noneMatch(response::equalsIgnoreCase)) {
			String errMsg = "Invalid Statutory Deductions Applicable.";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);

		}

	}

	private void isDateValid(String date, String dateDesc, String errorCode,
			List<ProcessingResult> validationResult) {
		String errMsg = null;

		if (!isPresent(date)) {
			return;
		}

		LocalDate dateInstance = DateFormatForStructuralValidatons
				.parseObjToDate(date.trim());

		if (dateInstance == null) {
			errMsg = String.format(INVALID, dateDesc);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);

		}

	}

	private void isPaymentReferenceNumberValid(String pmtRefNum,
			List<ProcessingResult> validationResult) {
		String errMsg = null;
		String errorCode = null;

		if (!isPresent(pmtRefNum)) {
			errMsg = "Payment Reference number cannot be left blank";
			errorCode = "ER1063";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
		if (pmtRefNum.trim().length() > 100) {
			errMsg = "Invalid PaymentReferenceNumber";
			errorCode = "ER1064";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
		}

	}

	private void isPaymentStatusValid(String pmtStatus,
			List<ProcessingResult> validationResult) {
		String errorCode = "ER1067";

		if (!isPresent(pmtStatus)) {
			return;
		}
		String response = pmtStatus.trim();
		if (Stream.of("PP", "FP").noneMatch(response::equalsIgnoreCase)) {
			String errMsg = "Invalid PaymentStatus(FullorPartial).";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);

		}

	}

	private void isExchangeRateValid(String exchngRate,
			List<ProcessingResult> validationResult) {

		String errorCode = null;
		if (!isPresent(exchngRate)) {
			return;
		}
		if (exchngRate.contains("'")) {
			exchngRate = exchngRate.replace("'", "");
		}

		if (!StringUtils.isNumeric(exchngRate.trim())
				|| exchngRate.trim().length() > 100) {
			String errMsg = "Invalid Exchange Rate.";
			errorCode = "ER10127";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
		}

	}

	private void isIdValid(String id, String idDesc, String errorCode,
			List<ProcessingResult> validationResult) {

		String errMsg = null;

		if (!isPresent(id)) {
			return;
		}

		if (id.contains("'")) {
			id = id.replace("'", "");
		}

		if (!StringUtils.isNumeric(id.trim())) {
			errMsg = String.format(INVALID, idDesc);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);

		}

	}

	private void isRevReclaimStatusValid(String status,
			List<ProcessingResult> validationResult) {

		if (!isPresent(status)) {
			return;
		}

		if (Stream.of(REVERSAL, RECLAIM, REVERSAL_RECLAIM, NO_REVERSAL)
				.noneMatch(status.trim()::equalsIgnoreCase)) {
			String errMsg = "Invalid ITCReversal/ReclaimStatus(DigiGST) Type.";
			String errorCode = "ER1104";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);

		}
	}

	public void isTimeStampValid(String timeStamp, String timeDesc,
			String errorCode, List<ProcessingResult> validationResult) {

		String errMsg = null;

		if (!isPresent(timeStamp)) {
			return;
		}
		if (timeStamp.contains("'")) {
			timeStamp = timeStamp.replace("'", "");
		}

		LocalDate dateInstance = DateFormatForStructuralValidatons
				.parseObjToDate(timeStamp.trim());

		if (dateInstance == null) {
			errMsg = String.format(INVALID, timeDesc);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);

		}

	}

	public Map<String, Reversal180DaysResponseUploadDto> getActivePaymentDocKeys(
			List<String> docKeysList) {
		Map<String, Reversal180DaysResponseUploadDto> pmtDocKeyMap = new HashMap<>();
		try {
			String queryString = ""
					+ "SELECT PAYMENT_DTLS_DOC_KEY, ITC_DIGI_STATUS AS ITCREV_RECLAIM_STATUS_DIGI, "
					+ "INV_VALUE, STATUTORY_DEDUCTION_AMT, ANY_OTHER_DEDUCTION_AMT, PAID_AMT,UNPAID_AMT, REV_DATE_181 AS DOC_DATE_180_DAYS, "
					+ "IGST_AMT AS IGST_TAX_PAID_PR, CGST_AMT AS CGST_TAX_PAID_PR, SGST_AMT AS SGST_TAX_PAID_PR, CESS_AMT AS CESS_TAX_PAID_PR, "
					+ "AVAILABLE_IGST, AVAILABLE_CGST, AVAILABLE_SGST, AVAILABLE_CESS, "
					+ "REV_IGST_AMT,REV_CGST_AMT,REV_SGST_AMT,REV_CESS_AMT, "
					+ "REC_IGST_AMT,REC_CGST_AMT,REC_SGST_AMT,REC_CESS_AMT, "
					+ "COMPUTE_ID, RECON_REPORT_CONFIG_ID, "
					+ "REV_REVERSAL_PERIOD AS ITCREV_RET_PRD_DIGI_INDICATIVE, REV_REAVAIL_PERIOD AS RECLAIM_RET_PRD_DIGI_INDICATIVE "
					+ "FROM ( SELECT PAYMENT_DTLS_DOC_KEY,ITC_DIGI_STATUS, DENSE_RANK() OVER ( ORDER BY COMPUTE_ID DESC ) AS RNK, "
					+ "INV_VALUE, STATUTORY_DEDUCTION_AMT, ANY_OTHER_DEDUCTION_AMT, PAID_AMT,UNPAID_AMT, REV_DATE_181, "
					+ "IGST_AMT, CGST_AMT, SGST_AMT, CESS_AMT, AVAILABLE_IGST, AVAILABLE_CGST, AVAILABLE_SGST, AVAILABLE_CESS, "
					+ "REV_IGST_AMT,REV_CGST_AMT,REV_SGST_AMT,REV_CESS_AMT, REC_IGST_AMT,REC_CGST_AMT,REC_SGST_AMT,REC_CESS_AMT, "
					+ "COMPUTE_ID, RECON_REPORT_CONFIG_ID, REV_REAVAIL_PERIOD,REV_REVERSAL_PERIOD "
					+ "FROM TBL_180_DAYS_COMPUTE WHERE IS_DELETE = FALSE "
					+ "AND PAYMENT_DTLS_DOC_KEY IN " + "(?1)) "
					+ "WHERE RNK = 1;";
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter(1, docKeysList);
			String msg = String.format(
					"Inside TBL_180_DAYS_COMPUTE PMTdocKeys, " + " query - %s ",
					queryString);

			LOGGER.debug(msg);

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();

			pmtDocKeyMap = convertObjToMap(list);

		} catch (Exception ex) {
			String msg = "Exception Occured in "
					+ "getActivePaymentDocKeys() method";
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return pmtDocKeyMap;
	}

	public Set<String> getActivePRDocKeys(List<String> docKeysList) {
		Set<String> prDocKeyList = null;
		try {
			String queryString = "SELECT DISTINCT PR_DOC_KEY FROM "
					+ "	 TBL_180_DAYS_COMPUTE WHERE IS_DELETE = FALSE AND "
					+ "  PR_DOC_KEY  IN (?1) ;";
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter(1, docKeysList);
			String msg = String.format(
					"Inside TBL_180_DAYS_COMPUTE PR docKeys, " + " query - %s ",
					queryString);

			LOGGER.debug(msg);

			@SuppressWarnings("unchecked")
			List<Object> list = q.getResultList();
			prDocKeyList = list.stream().map(o -> convertObjToString(o))
					.collect(Collectors.toCollection(HashSet::new));

		} catch (Exception ex) {
			String msg = "Exception Occured in "
					+ "getActivePRDocKeys() method";
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return prDocKeyList;
	}

	private String convertObjToString(Object obj) {
		return String.valueOf(obj);
	}

	private Map<String, Reversal180DaysResponseUploadDto> convertObjToMap(
			List<Object[]> list) {
		Map<String, Reversal180DaysResponseUploadDto> docKeyMap = new HashMap<>();
		for (Object[] obj : list) {
			Reversal180DaysResponseUploadDto dto = new Reversal180DaysResponseUploadDto();
			dto.setItcRevReclaimStatusDigi(checkNull(obj[1]));
			dto.setInvoiceValue(checkNull(obj[2]));
			dto.setStatDeductionAmt(checkNull(obj[3]));
			dto.setAnyOtherDeductionAmt(checkNull(obj[4]));
			dto.setPaidAmtToSupplier(checkNull(obj[5]));
			dto.setUnpaidAmtToSupplier(checkNull(obj[6]));
			dto.setDocDate180Days(checkNull(obj[7]));
			dto.setIgstTaxPaidPR(checkNull(obj[8]));
			dto.setCgstTaxPaidPR(checkNull(obj[9]));
			dto.setSgstTaxPaidPR(checkNull(obj[10]));
			dto.setCessTaxPaidPR(checkNull(obj[11]));
			dto.setAvailableIgstPR(checkNull(obj[12]));
			dto.setAvailableCgstPR(checkNull(obj[13]));
			dto.setAvailableSgstPR(checkNull(obj[14]));
			dto.setAvailableCessPR(checkNull(obj[15]));
			dto.setRevIgstDigiIndicative(checkNull(obj[16]));
			dto.setRevCgstDigiIndicative(checkNull(obj[17]));
			dto.setRevSgstDigiIndicative(checkNull(obj[18]));
			dto.setRevCessDigiIndicative(checkNull(obj[19]));
			dto.setReclaimIgstIndicative(checkNull(obj[20]));
			dto.setReclaimCgstIndicative(checkNull(obj[21]));
			dto.setReclaimSgstIndicative(checkNull(obj[22]));
			dto.setReclaimCessIndicative(checkNull(obj[23]));
			dto.setComputeId(checkNull(obj[24]));
			dto.setReconReportConfigID(checkNull(obj[25]));
			dto.setItcRevRetPrdDigiIndicative(checkNull(obj[26]));
			dto.setReclaimRetPrdDigiIndicative(checkNull(obj[27]));
			docKeyMap.put(String.valueOf(obj[0]), dto);
		}
		return docKeyMap;
	}

	private String checkNull(Object obj) {
		return (obj != null) ? obj.toString() : null;
	}

}
