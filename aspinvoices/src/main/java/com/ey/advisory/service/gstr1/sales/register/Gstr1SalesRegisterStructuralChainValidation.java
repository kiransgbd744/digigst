/**
 * 
 */
package com.ey.advisory.service.gstr1.sales.register;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

//import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.base.Strings;

/**
 * @author Shashikant.Shukla
 *
 */

@Component("Gstr1SalesRegisterStructuralChainValidation")
public class Gstr1SalesRegisterStructuralChainValidation {

	private static final String UPLOAD = "Upload";

	private void isbusinessPlaceValid(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 1;
		String errorCode = "ER052";
		String errMsg = "Invalid BusinessPlace / BusinessPlace is missing";
		if (!isPresent(rowData[index])) {
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			return;
		}
	}

	private void isDocumentNumber(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 5;
		String errorCode = "ER052";
		String errMsg = "DocumentNumber is missing";
		if (!isPresent(rowData[index])) {
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			return;
		}
	}

	private void isValidItemSerialNumber(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 7;
		String errorCode = "ER052";
		String errMsg = "Invalid ItemSerialNumber / ItemSerialNumber is missing";
		if (!isPresent(rowData[index])) {
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			return;
		}
	}

	private void isValidReturnPeriod(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 0;
		String errorCode = "ER051";
		String errMsg = null;
		if (!isPresent(rowData[index])) {
			errMsg = String
					.format("Invalid ReturnPeriod / ReturnPeriod is missing");
			ProcessingResult result = new ProcessingResult(UPLOAD, null,
					errMsg);

			validationResult.add(result);
			return;

		}
		if (rowData[index] != null) {
			rowData[index] = removeQuotes(rowData[index].toString().trim());
			if (!rowData[index].toString().matches("[0-9]+")
					|| rowData[index].toString().trim().length() != 6) {
				errMsg = String.format(
						"Invalid ReturnPeriod / ReturnPeriod is missing");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);

				validationResult.add(result);
				return;
			}

			if (rowData[index].toString().matches("[0-9]+")) {
				errMsg = String.format(
						"Invalid ReturnPeriod / ReturnPeriod is missing");
				int month = Integer
						.valueOf(rowData[index].toString().substring(0, 2));
				if (rowData[index].toString().trim().length() != 6 || month > 12
						|| month == 00) {
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCode, errMsg);

					validationResult.add(result);
					return;
				}
			}
		} else {
			errMsg = String
					.format("Invalid ReturnPeriod / ReturnPeriod is missing");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			return;
		}
	}

	public void rowDataValidation(List<ProcessingResult> validationResult,
			Object[] rowData) {

		isValidReturnPeriod(rowData, validationResult);

		isbusinessPlaceValid(rowData, validationResult);

		isDocumentType(rowData, validationResult);

		isDocumentNumber(rowData, validationResult);

		isValidItemSerialNumber(rowData, validationResult);

		isValidDate(rowData, validationResult);

		isValidItemAssessableValue(rowData, validationResult);
		isValidIGST(rowData, validationResult);
		isValidCGST(rowData, validationResult);
		isValidSGST(rowData, validationResult);
		isValidAdvaloremCess(rowData, validationResult);
		isValidSpecificCess(rowData, validationResult);
		isValidInvoiceValue(rowData, validationResult);
	}

	private void isDocumentType(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 3;
		String errorCode = "ER053";
		String errMsg = "Invalid Table Number as per Master";
		List<String> documentList = Arrays.asList("inv", "cr", "dr", "bos",
				"adv", "adj");
		if (!isPresent(rowData[index])) {
			errMsg = String
					.format("Invalid DocumentType / DocumentType is missing");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		} else if (rowData[index] == null) {
			errMsg = String
					.format("Invalid DocumentType / DocumentType is missing");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		} else if (rowData[index] != null) {
			rowData[index] = removeQuotes(rowData[index].toString().trim());
			if (!documentList
					.contains(rowData[index].toString().toLowerCase())) {
				errMsg = String.format(
						"Invalid DocumentType / DocumentType is missing");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}
		}
	}

	private void isValidDate(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 6;
		String errorCode = "ER051";
		String errMsg = null;
		if (!isPresent(rowData[index])) {
			errMsg = String
					.format("Invalid DocumentDate / DocumentDate is missing");
			ProcessingResult result = new ProcessingResult(UPLOAD, null,
					errMsg);

			validationResult.add(result);
			return;

		}
		if (rowData[index] != null) {
			LocalDate date = DateUtil
					.parseObjToDate(removeQuotes(rowData[index].toString()).trim());
			if (date == null) {
				errMsg = String.format(
						"Invalid DocumentDate / DocumentDate is missing");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);

				validationResult.add(result);
				return;
			}
		} else {
			errMsg = String
					.format("Invalid DocumentDate / DocumentDate is missing");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			return;
		}
	}

	private void isValidItemAssessableValue(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 10;
		String errorCode = "ER051";
		String errMsg = null;
		List<ProcessingResult> errors = new ArrayList<>();

		// First check if the input object is a valid decimal number.
		if (rowData[index] != null) {
			if (!isDecimal(removeQuotes(rowData[index].toString().trim()))) {

				errMsg = String.format("Invalid ItemAssessableValue Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
			boolean isValid = NumberFomatUtil.is15And2digValidDec(
					removeQuotes(rowData[index].toString().trim()));
			if (!isValid) {

				errMsg = String.format("Invalid ItemAssessableValue Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
		}
	}

	private void isValidIGST(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 11;
		String errorCode = "ER051";
		String errMsg = null;
		List<ProcessingResult> errors = new ArrayList<>();

		// First check if the input object is a valid decimal number.
		if (rowData[index] != null) {
			if (!isDecimal(removeQuotes(rowData[index].toString().trim()))) {

				errMsg = String.format("Invalid IGST Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
			boolean isValid = NumberFomatUtil.is15And2digValidDec(
					removeQuotes(rowData[index].toString().trim()));
			if (!isValid) {

				errMsg = String.format("Invalid IGST Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
		}

	}

	private void isValidCGST(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 12;
		String errorCode = "ER051";
		String errMsg = null;
		List<ProcessingResult> errors = new ArrayList<>();

		// First check if the input object is a valid decimal number.
		if (rowData[index] != null) {
			if (!isDecimal(removeQuotes(rowData[index].toString().trim()))) {

				errMsg = String.format("Invalid CGST Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
			boolean isValid = NumberFomatUtil.is15And2digValidDec(
					removeQuotes(rowData[index].toString().trim()));
			if (!isValid) {

				errMsg = String.format("Invalid CGST Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
		}

	}

	private void isValidSGST(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 13;
		String errorCode = "ER051";
		String errMsg = null;
		List<ProcessingResult> errors = new ArrayList<>();

		// First check if the input object is a valid decimal number.
		if (rowData[index] != null) {
			if (!isDecimal(removeQuotes(rowData[index].toString().trim()))) {

				errMsg = String.format("Invalid SGST Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
			boolean isValid = NumberFomatUtil.is15And2digValidDec(
					removeQuotes(rowData[index].toString().trim()));
			if (!isValid) {

				errMsg = String.format("Invalid SGST Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
		}

	}

	private void isValidAdvaloremCess(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 14;
		String errorCode = "ER051";
		String errMsg = null;
		List<ProcessingResult> errors = new ArrayList<>();

		// First check if the input object is a valid decimal number.
		if (rowData[index] != null) {
			if (!isDecimal(removeQuotes(rowData[index].toString().trim()))) {

				errMsg = String.format("Invalid AdvaloremCess Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
			boolean isValid = NumberFomatUtil.is15And2digValidDec(
					removeQuotes(rowData[index].toString().trim()));
			if (!isValid) {

				errMsg = String.format("Invalid AdvaloremCess Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
		}

	}

	private void isValidSpecificCess(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 15;
		String errorCode = "ER051";
		String errMsg = null;
		List<ProcessingResult> errors = new ArrayList<>();

		// First check if the input object is a valid decimal number.
		if (rowData[index] != null) {
			if (!isDecimal(removeQuotes(rowData[index].toString().trim()))) {

				errMsg = String.format("Invalid SpecificCess Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
			boolean isValid = NumberFomatUtil.is15And2digValidDec(
					removeQuotes(rowData[index].toString().trim()));
			if (!isValid) {

				errMsg = String.format("Invalid SpecificCess Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
		}

	}

	private void isValidInvoiceValue(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 16;
		String errMsg = null;
		List<ProcessingResult> errors = new ArrayList<>();

		// First check if the input object is a valid decimal number.
		if (rowData[index] != null) {
			if (!isDecimal(removeQuotes(rowData[index].toString().trim()))) {

				errMsg = String.format("Invalid InvoiceValue Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
			boolean isValid = NumberFomatUtil.is15And2digValidDec(
					removeQuotes(rowData[index].toString().trim()));
			if (!isValid) {

				errMsg = String.format("Invalid InvoiceValue Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD, null,
						errMsg);

				validationResult.add(result);
				return;
			}
		}
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
}
