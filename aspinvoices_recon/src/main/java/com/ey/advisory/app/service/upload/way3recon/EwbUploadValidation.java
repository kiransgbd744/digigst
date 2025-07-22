/**
 * 
 */
package com.ey.advisory.app.service.upload.way3recon;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.FormatValidationUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author vishal.verma
 *
 */

@Component("EwbUploadValidation")
public class EwbUploadValidation {

	private static ThreadLocal<NumberFormat> numberFormatter = ThreadLocal
			.withInitial(() -> new DecimalFormat("0"));

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	public void rowDataValidation(List<ProcessingResult> validationResult,
			Object[] rowData, List<String> gstinList, int idx) {

		isValidIgst(rowData, validationResult, idx);

		isValidCess(rowData, validationResult, idx);

		isValidCessAdv(rowData, validationResult, idx);

		isDocumentNumberValid(rowData, validationResult, idx);

		isValidCgst(rowData, validationResult, idx);

		isValidSgst(rowData, validationResult, idx);

		isValidItemAssessableAmount(rowData, validationResult, idx);

		isValidEwbNum(rowData, validationResult, idx);

		isDocumentDateValid(rowData, validationResult, idx);

		isValidOtherAmt(rowData, validationResult, idx);

		isValidTotalInvoiceValue(rowData, validationResult, idx);

		isValidTillDate(rowData, validationResult);

		isValidModeOfgeneration(rowData, validationResult);

		isValidCanBy(rowData, validationResult);

		isValidcanCelledDate(rowData, validationResult, idx);

		isvalidHsnDesc(rowData, validationResult, idx);

		isvalidHsnCode(rowData, validationResult, idx);

		isvalidItemSerialNumber(rowData, validationResult, idx);

		isValidStatus(rowData, validationResult);

		isValidTransporterId(rowData, validationResult);

		isValidOthGstin(rowData, validationResult);

		isvalidSupplyType(rowData, validationResult, idx);

		isvalidEWBDateTime(rowData, validationResult, idx);

		isSuppGstinValid(rowData, validationResult, gstinList, idx);

		isCustGstinValid(rowData, validationResult, gstinList, idx);

	}

	private void isValidEwbNum(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {
		int index = 0;
		String errorCodeBlank = "EWB-1001";

		if (!isPresent(rowData[index])) {
			String errMsg = String.format("EWB number cannot be left blank");
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add("EWB NUMBER");
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION,
					errorCodeBlank, errMsg, location));
			return;
		}

		if (NumberFomatUtil.isNumber(rowData[index])) {
			if (rowData[index] instanceof Number) {
				if (NumberFomatUtil
						.getBigDecimal(
								numberFormatter.get().format(rowData[index]))
						.signum() == -1) {
					String errMsg = String.format("Invalid EWB Number");
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add("EWB NUMBER");
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					validationResult.add(new ProcessingResult(APP_VALIDATION,
							"EWB-1002", errMsg, location));
					return;
				} else if (numberFormatter.get().format(rowData[index])
						.toString().length() != 12) {
					if (rowData[index] instanceof Number) {
						String errMsg = String.format("Invalid EWB Number");
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add("EWB NUMBER");
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						validationResult.add(new ProcessingResult(
								APP_VALIDATION, "EWB-1002", errMsg, location));
						return;

					}
				}

			}
		} else {
			String errMsg = String.format("Invalid EWB Number");
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add("EWB NUMBER");
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION,
					"EWB-1002", errMsg, location));
			return;
		}
	}

	private void isValidItemAssessableAmount(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {
		int index = 13;

		if (!isPresent(rowData[index])) {
			return;
		}

		if (!isDecimal(rowData[index].toString().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add("AssessableAmt");
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1013",
					"Invalid Assessable value", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		boolean isValid = NumberFomatUtil
				.is17And2digValidDec(rowData[index].toString().trim());
		if (!isValid) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add("AssessableAmt");
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1013",
					"Invalid Assessable value", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		return;

	}

	private void isValidSgst(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {
		int index = 14;

		if (!isPresent(rowData[index])) {
			return;
		}

		if (!isDecimal(rowData[index].toString().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SGST_AMOUNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1014",
					"Invalid SGST Amount", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		boolean isValid = NumberFomatUtil
				.is17And2digValidDec(rowData[index].toString().trim());
		if (!isValid) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SGST_AMOUNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1014",
					"Invalid SGST Amount", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		return;
	}

	private void isValidCgst(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {
		int index = 15;

		if (!isPresent(rowData[index])) {
			return;
		}

		if (!isDecimal(rowData[index].toString().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CGST_AMOUNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1015",
					"Invalid CGST Amount", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		boolean isValid = NumberFomatUtil
				.is17And2digValidDec(rowData[index].toString().trim());
		if (!isValid) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CGST_AMOUNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1015",
					"Invalid CGST Amount", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		return;

	}

	private void isValidIgst(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {
		int index = 16;
		if (!isPresent(rowData[index])) {
			return;
		}
		if (!isDecimal(rowData[index].toString().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.IGST_AMOUNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1016",
					"Invalid IGST Amount", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		boolean isValid = NumberFomatUtil
				.is17And2digValidDec(rowData[index].toString().trim());
		if (!isValid) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.IGST_AMOUNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1016",
					"Invalid IGST Amount", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}

		return;

	}

	private void isValidCess(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {
		int index = 17;

		if (!isPresent(rowData[index])) {
			return;
		}
		if (!isDecimal(rowData[index].toString().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CESS_AMOUNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1017",
					"Invalid CESS Amount", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		boolean isValid = NumberFomatUtil
				.is17And2digValidDec(rowData[index].toString().trim());
		if (!isValid) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CESS_AMOUNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1017",
					"Invalid CESS Amount", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		return;

	}

	private void isValidCessAdv(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {
		int index = 18;

		if (!isPresent(rowData[index])) {
			return;
		}

		if (!isDecimal(rowData[index].toString().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CESS_AMT_ADVALOREM);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1018",
					"Invalid Specific CESS Amount", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		boolean isValid = NumberFomatUtil
				.is17And2digValidDec(rowData[index].toString().trim());
		if (!isValid) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CESS_AMT_ADVALOREM);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1018",
					"Invalid Specific CESS Amount", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		return;
	}

	private void isValidOtherAmt(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {
		int index = 19;

		if (!isPresent(rowData[index])) {
			return;
		}

		if (!isDecimal(rowData[index].toString().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.OTHERVALUE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1019",
					"Invalid Other Value", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		boolean isValid = NumberFomatUtil
				.is17And2digValidDec(rowData[index].toString().trim());
		if (!isValid) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.OTHERVALUE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1019",
					"Invalid Other Value", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		return;

	}

	private void isValidTotalInvoiceValue(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {
		int index = 20;
		if (!isPresent(rowData[index])) {
			return;
		}

		if (!isDecimal(rowData[index].toString().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TOTAL_INV_VALUE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1020",
					"Invalid Invoice Value", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		boolean isValid = NumberFomatUtil
				.is17And2digValidDec(rowData[index].toString().trim());
		if (!isValid) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TOTAL_INV_VALUE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, "ER-1020",
					"Invalid Invoice Value", location));
			// if it's not a number, then we can return the errors
			// immediately
			return;
		}
		return;

	}

	private void isValidTillDate(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 21;

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
		}
		return;

	}

	private void isValidModeOfgeneration(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 22;

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
		}
		return;

	}

	private void isValidCanBy(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 23;

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
		}
		return;

	}

	private void isValidcanCelledDate(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {
		int index = 24;

		if (isPresent(rowData[index])) {

			LocalDate date = DateFormatForStructuralValidatons
					.parseObjToDate(rowData[index].toString().trim());

			String errorCode = "ER-1024";
			if (date == null || (date.getMonthValue() > 12)) {
				String errMsg = String.format("Invalid cancelled date");
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.CAN_REMARKS);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				validationResult.add(new ProcessingResult(APP_VALIDATION,
						errorCode, errMsg, location));
				return;
			}
		}
	}

	private void isvalidHsnDesc(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {

		int index = 12;
		String errorCode = "ER-1012";

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() < 3
					|| rowData[index].toString().length() > 300) {
				String errMsg = String.format("Invalid HSN or SAC Desc");
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.HSN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				validationResult.add(new ProcessingResult(APP_VALIDATION,
						errorCode, errMsg, location));
				if (rowData[index].toString().length() > 300)
					rowData[index] = rowData[index].toString().substring(0,
							300);
				return;
			}
		}
	}

	private void isvalidHsnCode(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {

		int index = 11;
		String errorCode = "ER-1011";

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() < 4
					|| rowData[index].toString().length() > 8) {
				String errMsg = String.format("Invalid HSN or SAC Code");
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.HSN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				validationResult.add(new ProcessingResult(APP_VALIDATION,
						errorCode, errMsg, location));
				return;
			}
		}
	}

	private void isvalidItemSerialNumber(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {

		int index = 10;

		if (!isPresent(rowData[index])) {
			String errMsg = String
					.format("Item serial number CanNot be left Blank");
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.ITEM_SERIAL_NUMBER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION,
					"ER-1010-1", errMsg, location));

			return;
		}

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() < 1
					|| rowData[index].toString().length() > 6) {
				String errMsg = String.format("Invalid Item serial number");
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ITEM_SERIAL_NUMBER);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				validationResult.add(new ProcessingResult(APP_VALIDATION,
						"ER-1010-2", errMsg, location));

				return;
			}

			if (!FormatValidationUtil.isInteger(rowData[index])) {
				String errMsg = String.format("Invalid Item serial number");
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ITEM_SERIAL_NUMBER);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				validationResult.add(new ProcessingResult(APP_VALIDATION,
						"ER-1010-2", errMsg, location));
				return;
			}
		}
	}

	private void isValidStatus(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 9;

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
		}
		return;

	}

	private void isValidTransporterId(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 6;

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
		}
		return;

	}

	private void isValidOthGstin(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 5;

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
		}
		return;

	}

	private void isDocumentNumberValid(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {

		int index = 3;
		String errorCodeBlank = "ER-1003";

		if (!isPresent(rowData[index])) {

			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DOC_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult
					.add(new ProcessingResult(APP_VALIDATION, errorCodeBlank,
							"Document Number cannot be left blank.", location));
			return;
		}

		String docNo = rowData[index].toString().trim();
		if (docNo.contains("'")) {
			String docWithOutQuotes = docNo.replace("'", "");
			rowData[index] = docWithOutQuotes;
			docNo = docWithOutQuotes;
		}

		String errorCode = "ER-1004";
		if (docNo.length() > 16) {

			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DOC_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, errorCode,
					"Invalid Document Number.", location));

			if (docNo.length() > 100)
				rowData[index] = rowData[index].toString().substring(0, 16);
			return;
		}

		String regex = "^[a-zA-Z0-9/-]*$";
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(docNo);
		if (!matcher.matches()) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DOC_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, errorCode,
					"Invalid Document Number.", location));
			return;
		}
	}

	private void isDocumentDateValid(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {

		int index = 4;
		String errorCodeBlank = "ER-1004-1";

		if (!isPresent(rowData[index])) {
			String errMsg = String
					.format("Document Date cannot be left blank.");
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DOC_DATE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION,
					errorCodeBlank, errMsg, location));
			return;
		}

		LocalDate date = DateFormatForStructuralValidatons
				.parseObjToDate(rowData[index].toString().trim());

		String errorCode = "ER-1004-2";
		if (date == null || (date.getMonthValue() > 12)) {
			String errMsg = String.format("Invalid Document Date");
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DOC_DATE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, errorCode,
					errMsg, location));
			rowData[index] = null;
			return;
		}

	}

	private void isvalidSupplyType(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {

		int index = 2;
		String errorCode = "ER-1002-1";

		if (!isPresent(rowData[index])) {
			String errMsg = String.format("Supply Type cannot be left blank");
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SupplyType);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, errorCode,
					errMsg, location));
			return;
		}

		if (isPresent(rowData[index])) {
			if (!getdocTypeList().contains(rowData[index].toString().trim())) {
				if (rowData[index].toString().trim().startsWith("Inward")) {
					return;
				}
				String errMsg = String.format("Invalid Supply Type");
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SupplyType);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				validationResult.add(new ProcessingResult(APP_VALIDATION,
						"ER-1002-2", errMsg, location));
				return;
			}
		}
	}

	private void isvalidEWBDateTime(Object[] rowData,
			List<ProcessingResult> validationResult, int idx) {

		int index = 1;
		String errorCode = "ER-1001-1";

		if (!isPresent(rowData[index])) {
			String errMsg = String.format("EWB date cannot be left blank");
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.EWay_BillDate);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, errorCode,
					errMsg, location));
			return;
		}

		LocalDate date = DateFormatForStructuralValidatons
				.parseObjToDate(rowData[index].toString().trim());

		if (date == null) {

			String[] split = rowData[index].toString().split(" ");
			LocalDate date1 = DateFormatForStructuralValidatons
					.parseObjToDate(split[0]);

			if (date1 == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.EWay_BillDate);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				validationResult.add(new ProcessingResult(APP_VALIDATION,
						"ER-1001-2", "Invalid date and time", location));
				return;
			}

		}

	}

	private void isSuppGstinValid(Object[] rowData,
			List<ProcessingResult> validationResult, List<String> gstinList,
			int idx) {
		int index = 0;
		if (isPresent(rowData[2])) {
			
				if (rowData[2].toString().trim().startsWith("Inward")) {
					index = 8;
				} else
					index = 7;
			
		}
		String errorCode = "ER-1007";
		if (!isPresent(rowData[index])) {
			String errMsg = String
					.format("FromGSTIN Info cannot be left blank.");
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SUPPLIER_GSTIN_OR_PAN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, errorCode,
					errMsg, location));
			return;
		}
		String gstin = "";
		if (isPresent(rowData[index])) {

			if (rowData[index].toString().trim().length() > 15 || 
					rowData[index].toString().trim().length() == 15) {
				gstin = rowData[index].toString().trim().substring(0, 15);
			}

			if (gstin.length() != 15) {
				String errMsg = String.format("Invalid Supplier GSTIN");
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SUPPLIER_GSTIN_OR_PAN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				validationResult.add(new ProcessingResult(APP_VALIDATION,
						"ER-1007-1", errMsg, location));
				return;
			}

			String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";

			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(gstin.trim());

			// String vendorGstin =
			// rowData[index].toString().trim().substring(0, 15);
			if (!matcher.matches()) {
				String errMsg = "Invalid Supplier GSTIN";
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SUPPLIER_GSTIN_OR_PAN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				validationResult.add(new ProcessingResult(APP_VALIDATION,
						"ER-1007-1", errMsg, location));
				return;
			}

			if (!gstinList.contains(gstin.trim())) {

				String errMsg = "Supplier GSTIN is not as per On-Boarding data";
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SUPPLIER_GSTIN_OR_PAN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				validationResult.add(new ProcessingResult(APP_VALIDATION,
						"ER-1007-2", errMsg, location));
				return;
			}

			rowData[index] = rowData[index].toString().trim();
		}
	}

	private void isCustGstinValid(Object[] rowData,
			List<ProcessingResult> validationResult, List<String> gstinList,
			int idx) {
		int index = 0;
		if (isPresent(rowData[2])) {
			if (rowData[2].toString().trim().startsWith("Inward")) {
				index = 7;
			} else
				index = 8;

		}
		// int index = 8;
		String errorCode = "ER-1008";
		if (!isPresent(rowData[index])) {
			String errMsg = String.format("ToGSTIN Info cannot be left blank.");
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CUSTOMER_PAN_ADHAR);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			validationResult.add(new ProcessingResult(APP_VALIDATION, errorCode,
					errMsg, location));
			return;
		}

		/*
		 * String gstin = ""; if (isPresent(rowData[index])) {
		 * 
		 * if (rowData[index].toString().trim().length() > 15) { gstin =
		 * rowData[index].toString().trim().substring(0, 15); }
		 * 
		 * String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]" +
		 * "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]" +
		 * "[A-Za-z0-9][A-Za-z0-9]$";
		 * 
		 * Pattern pattern = Pattern.compile(regex);
		 * 
		 * Matcher matcher = pattern.matcher(gstin.trim());
		 * 
		 * if (!matcher.matches()) { String errMsg = "Invalid Customer GSTIN";
		 * Set<String> errorLocations = new HashSet<>();
		 * errorLocations.add(GSTConstants.CUSTOMER_PAN_ADHAR);
		 * TransDocProcessingResultLoc location = new
		 * TransDocProcessingResultLoc(null, errorLocations.toArray());
		 * validationResult.add(new ProcessingResult(APP_VALIDATION,
		 * "ER-1008-1", errMsg, location)); return; } }
		 */
		rowData[index] = rowData[index].toString().trim();

	}

	private List<String> getdocTypeList() {

		List<String> list = new ArrayList<>();

		list.add("Outward Supply");
		list.add("Outward Export");
		list.add("Outward Job Work");
		list.add("Outward SKD/CKD");
		list.add("Outward Recipient not known");
		list.add("Outward For own use");
		list.add("Outward Exhibition or Fairs");
		list.add("Outward Line Sales");
		list.add("Outward Others");
		list.add("Inward");

		return list;
	}
}
