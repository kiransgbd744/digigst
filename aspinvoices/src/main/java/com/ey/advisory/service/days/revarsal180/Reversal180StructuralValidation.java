/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr2InwardDocRepository;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;

/**
 * @author vishal.verma
 *
 */

@Component("Reversal180StructuralValidation")
public class Reversal180StructuralValidation {

	private static final String UPLOAD = "Upload";
	

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;
	
	@Autowired
	@Qualifier("Gstr2InwardDocRepository")
	private Gstr2InwardDocRepository gstr2InwardDocRepo;

	private void isCustGstinValid(Object[] rowData,
			List<ProcessingResult> validationResult, List<String> gstinList) {
		int index = 1;
		String errorCode = "ER-1034";
		if (!isPresent(rowData[index])) {
			String errMsg = String.format("Customer GSTIN cannot be left blank.");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}

		String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(rowData[index].toString().trim());

		String vendorGstin = rowData[index].toString().trim();
		if (!matcher.matches() || vendorGstin.length() != 15) {
			String errMsg = "Invalid Customer GSTIN";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			return;
		}
		if (!gstinList.contains(rowData[index].toString().trim())) {

			String errMsg = "Customer GSTIN is not as per On-Boarding data";
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
		
		rowData[index] = rowData[index].toString().trim();

	}

	private void isDocType(Object[] rowData,
			List<ProcessingResult> validationResult, boolean flag) {

		int index = 5;
		String errorCode = "ER-1037";
		
		if (flag) {

			if (!isPresent(rowData[index])) {
				return;
			}
		} else {
			if (!isPresent(rowData[index])) {
				String errMsg = String
						.format("Document Type cannot be left blank");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}
		}
		
		String docType = rowData[index].toString();

		if (docType.length() > 5) {
			errorCode = "ER-1038";
			String errMsg = String.format("Invalid Document Type");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			if (rowData[index].toString().length() > 100)
				rowData[index] = rowData[index].toString().substring(0, 100);
			return;
		}

		String tableType = rowData[index].toString().trim();
		errorCode = "ER-1038";
		if (!Stream.of("INV", "RNV", "CR", "RCR", "DR", "RDR", "SLF", "RSLF")
				.anyMatch(tableType::equals)) {
			String errMsg = String.format("Invalid Document Type");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
	}

	private void isDocumentNumberValid(Object[] rowData,
			List<ProcessingResult> validationResult, boolean flag) {

		int index = 6;
		String errorCodeBlank = "ER-1041";
		
		if (flag) {
			if (!isPresent(rowData[index])) {
				return;
			}
		} else {
			if (!isPresent(rowData[index])) {

				String errMsg = String
						.format("Document Number cannot be left blank.");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);
				return;
			}
		}

		if (!isPresent(rowData[index])) {

			String errMsg = String.format("Document Number cannot be left blank.");
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			return;
		}

		String docNo = rowData[index].toString().trim();
		if (docNo.contains("'")) {
			String docWithOutQuotes = docNo.replace("'", "");
			rowData[index] = docWithOutQuotes;
			docNo = docWithOutQuotes;
		}

		String errorCode = "ER-1042";
		if (docNo.length() > 16) {

			String errMsg = String.format("Invalid Document Number");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			if (docNo.length() > 100)
				rowData[index] = rowData[index].toString().substring(0, 16);
			return;
		}

		String regex = "^[a-zA-Z0-9/-]*$";
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(docNo);
		if (!matcher.matches()) {
			String errMsg = String.format("Invalid Document Number");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
	}

	private void isDocumentDateValid(Object[] rowData,
			List<ProcessingResult> validationResult, boolean flag) {

		int index = 7;
		String errorCodeBlank = "ER-1043";

		if (flag) {
			if (!isPresent(rowData[index])) {
				return;
			}
		} else {
			if (!isPresent(rowData[index])) {
				String errMsg = String
						.format("Document Date cannot be left blank.");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);
				return;
			}

		}

		if (!isPresent(rowData[index])) {
			String errMsg = String.format("Document Date cannot be left blank.");
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			return;
		}

		LocalDate date = DateFormatForStructuralValidatons
				.parseObjToDate(rowData[index].toString().trim());
		
		//Integer month = date.getMonthValue() > 12 ? "Invalid Document Date" : null;

		String errorCode = "ER-1044";
		if (date == null || (date.getMonthValue() > 12)) {
			String errMsg = String.format("Invalid Document Date");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			rowData[index] = null;
			return;
		}

	}

	private void isSupplierGstinValid(Object[] rowData,
			List<ProcessingResult> validationResult, List<String> gstinList, boolean flag) {
		int index = 2;
		String errorCode = "ER1052";
		String errMsg = "Invalid Supplier GSTIN";
		
		if(flag) {
			
			if(!isPresent(rowData[index])) {
				
				errMsg = String.format("Supplier GSTIN cannot be left blank.");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				
				return;
			}
		}else {
			
			if(!isPresent(rowData[index])) {
				return;
			}
		}
		
		rowData[index] = rowData[index].toString().trim();
		if (GSTConstants.URP
				.equalsIgnoreCase(rowData[index].toString().trim())) {
			return;
		}
		if (isPresent(rowData[1]) && isPresent(rowData[2])) {

			errorCode = "ER110";

			String cgstin = rowData[1].toString();
			String sgstin = rowData[2].toString();
			if (cgstin.equalsIgnoreCase(sgstin)) {

				errMsg = String.format("customer GSTIN and supplier GSTIN "
						+ "can not be same ");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);

				validationResult.add(result);
				return;

			}
		}

		if (rowData[index].toString().trim().length() == 15
				|| rowData[index].toString().trim().length() == 10) {

			if (rowData[index].toString().trim().length() == 15) {
				String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
						+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
						+ "[A-Za-z0-9][A-Za-z0-9]$";

				Pattern pattern = Pattern.compile(regex);

				Matcher matcher = pattern.matcher(rowData[index].toString().trim());

				String vendorGstin = rowData[index].toString().trim();
				if (!matcher.matches() || vendorGstin.length() != 15) {
					errMsg = String.format("Invalid Supplier GSTIN");
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCode, errMsg);

					validationResult.add(result);
					return;
				}
			}

		} else {
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}

		return;
	}

	private void isActionValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 0;
		String errorCode = "ER-106";
		
		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() != 3) {
				String errMsg = String.format("Invalid Action Type");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				if (rowData[index].toString().length() > 100)
					rowData[index] = rowData[index].toString().substring(0, 100);
				return;
			}
		}
		if (isPresent(rowData[index])) {
			String response = rowData[index].toString().trim();
			if (!Stream.of("can", "CAN").anyMatch(response::equals)) {
				String errMsg = String.format("Invalid Action Type");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}
		}
	}

	private void isValidPaymentReferenceDate(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 15;
		String errorCodeBlank = "ER-105";

		if (!isPresent(rowData[index])) {
			String errMsg = String
					.format("Payment Reference date cannot be left blank.");
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			return;
		}

		LocalDate date = DateFormatForStructuralValidatons
				.parseObjToDate(rowData[index].toString().trim());

		String errorCode = "ER-105";
		if (date == null || (date.getMonthValue() > 12)) {
			String errMsg = String.format("Invalid Payment Reference date");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			rowData[index] = null;
			return;
		}

	}

	private void isValidPaymentReferenceNumber(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 14;
		String errorCode = "ER-1006";
		if (!isPresent(rowData[index])) {
			String errMsg = String
					.format("Payment reference Number cannot be left blank");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
		if (rowData[index].toString().length() > 100) {
			String errMsg = String.format("Invalid Payment Reference Number");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			if (rowData[index].toString().length() > 100)
				rowData[index] = rowData[index].toString().substring(0, 100);
			return;
		}

	}

	private void isValidDueDateofPayment(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 13;

		if (isPresent(rowData[index])) {

			LocalDate date = DateFormatForStructuralValidatons
					.parseObjToDate(rowData[index].toString().trim());

			String errorCode = "ER-104";
			if (date == null || (date.getMonthValue() > 12)) {
				String errMsg = String.format("Invalid DueDateofPayment");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				rowData[index] = null;
				return;
			}
		}
	}

	private void isValidPostingDate(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 22;

		if (isPresent(rowData[index])) {

			LocalDate date = DateFormatForStructuralValidatons
					.parseObjToDate(rowData[index].toString().trim());

			String errorCode = "ER-106";
			if (date == null || (date.getMonthValue() > 12)) {
				String errMsg = String.format("Invalid posting date");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				rowData[index] = null;
				return;
			}
		}
	}

	private void isValidBalanceAmount(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 21;
		String errorCodeBlank = "ER-106";

		if (isPresent(rowData[index])) {
			if (NumberFomatUtil.isNumber(rowData[index])) {
				if (NumberFomatUtil.getBigDecimal(rowData[index])
						.signum() == -1) {
					String errMsg = String
							.format("Invalid UnpaidAmounttoSupplier");
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
			} else {
				String errMsg = String
						.format("Invalid UnpaidAmounttoSupplier");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);
				return;

			}
		}
		return;

	}

	private void isValidExchangeRate(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 20;
		String errorCodeBlank = "ER-107";

		if (isPresent(rowData[index])) {
			rowData[index] = rowData[index].toString().trim();
			if (NumberFomatUtil.isNumber(rowData[index])) {
				if (NumberFomatUtil.getBigDecimal(rowData[index])
						.signum() == -1) {
					String errMsg = String
							.format("Invalid Exchange Rate");
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					
			if (rowData[index].toString().length() > 100)
						rowData[index] = rowData[index].toString()
						.substring(0, 100);
					return;
				} else {
					if (rowData[index].toString().length() > 100)
						rowData[index] = rowData[index].toString()
						.substring(0, 100);
				}
			} else {
				String errMsg = String
						.format("Invalid Exchange Rate");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);

				if (rowData[index].toString().length() > 100)
					rowData[index] = rowData[index].toString().substring(0, 100);
				return;

			}
		}
		return;

	}

	private void isValidTotalInvoiceAmountDue(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 8;
		String errorCodeBlank = "ER-109";

		if (isPresent(rowData[index])) {
			if (NumberFomatUtil.isNumber(rowData[index])) {
				if (NumberFomatUtil.getBigDecimal(rowData[index])
						.signum() == -1) {
					String errMsg = String
							.format("Invalid Invoice Value");
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
			} else {
				String errMsg = String
						.format("Invalid Invoice Value");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);
				return;

			}
		}
		return;

	}

	private void isValidPaidAmount(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 18;
		String errorCodeBlank = "ER-108";

		if (isPresent(rowData[index])) {
			if (NumberFomatUtil.isNumber(rowData[index])) {
				if (NumberFomatUtil.getBigDecimal(rowData[index])
						.signum() == -1) {
					String errMsg = String
							.format("Invalid PaidAmounttoSupplier");
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
			} else {
				String errMsg = String
						.format("Invalid PaidAmounttoSupplier.");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);
				return;

			}
		}
		return;

	}

	private void isStatutoryDeductionsApplicableValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 9;
		String errorCode = "ER-1039";

		if (isPresent(rowData[index])) {
			String staDedu = rowData[index].toString();
			if (staDedu.length() > 1) {
				String errMsg = String.format(
						"Invalid Statutory Deductions Applicable.",
						rowData[index].toString());
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				if (rowData[index].toString().length() > 100)
					rowData[index] = rowData[index].toString().substring(0, 100);
				return;
			}
		}

		if (isPresent(rowData[index])) {
			String response = rowData[index].toString().trim();
			if (!Stream.of("Y", "y", "N", "n").anyMatch(response::equals)) {
				String errMsg = String.format(
						"Invalid Statutory Deductions Applicable");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}
		}

	}

	private void isStatutoryDeductionAmount(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 10;
		String errorCodeBlank = "ER-111";

		if (isPresent(rowData[index])) {
			if (NumberFomatUtil.isNumber(rowData[index])) {
				if (NumberFomatUtil.getBigDecimal(rowData[index])
						.signum() == -1) {
					String errMsg = String
							.format("Invalid Statutory Deduction Amount");
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
			} else {
				String errMsg = String.format(
						"Invalid Statutory Deduction Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);
				return;

			}
		}
		return;

	}

	private void isAnyOtherDeductionAmount(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 11;
		String errorCodeBlank = "ER-112";

		if (isPresent(rowData[index])) {
			if (NumberFomatUtil.isNumber(rowData[index])) {
				if (NumberFomatUtil.getBigDecimal(rowData[index])
						.signum() == -1) {
					String errMsg = String
							.format("Invalid AnyOtherDeductionAmount");
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
			} else {
				String errMsg = String.format("Invalid AnyOtherDeductionAmount");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);
				return;

			}
		}
		return;

	}

	private void isPaymentStatusValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 17;
		String errorCode = "ER-1040";
		if (isPresent(rowData[index])) {
			String isPaymentStatus = rowData[index].toString();

			if (isPaymentStatus.length() > 2) {
				String errMsg = String.format(
						"Invalid PaymentStatus(FullorPartial)",
						rowData[index].toString());
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				if (rowData[index].toString().length() > 100)
					rowData[index] = rowData[index].toString().substring(0, 100);
				return;
			}
		}

		if (isPresent(rowData[index])) {
			String response = rowData[index].toString().trim();
			if (!Stream.of("FP", "fp", "PP", "pp").anyMatch(response::equals)) {
				String errMsg = String.format(
						"Invalid PaymentStatus(FullorPartial)");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}
		}

	}

	public void rowDataValidation(List<ProcessingResult> validationResult,
			Object[] rowData, List<String> gstinList, boolean flag) {

		isSupplierGstinValid(rowData, validationResult, gstinList, flag);

		isCustGstinValid(rowData, validationResult, gstinList);

		isDocType(rowData, validationResult, flag);

		isDocumentNumberValid(rowData, validationResult, flag);

		isDocumentDateValid(rowData, validationResult, flag);

		isActionValid(rowData, validationResult);

		isValidTotalInvoiceAmountDue(rowData, validationResult);

		isValidDueDateofPayment(rowData, validationResult);

		isValidPaymentReferenceNumber(rowData, validationResult);

		isValidPaymentReferenceDate(rowData, validationResult);

		isValidPaidAmount(rowData, validationResult);

		isValidExchangeRate(rowData, validationResult);

		isValidBalanceAmount(rowData, validationResult);

		isValidPostingDate(rowData, validationResult);

		isStatutoryDeductionsApplicableValid(rowData, validationResult);

		isStatutoryDeductionAmount(rowData, validationResult);

		isAnyOtherDeductionAmount(rowData, validationResult);

		isPaymentStatusValid(rowData, validationResult);

		isValidSupplierCode(rowData, validationResult);

		isValidSupplierName(rowData, validationResult);

		isValidPlantCode(rowData, validationResult);

		isValidDivision(rowData, validationResult);

		isValidProfitCentre(rowData, validationResult);
		
		isValidCurrencyCode(rowData, validationResult);
		
		isVaidRemarksforDeductions(rowData, validationResult);
		
		isVaidPaymentDescription(rowData, validationResult);
		
		isVaidUserDefinedField1(rowData, validationResult);
		
		isVaidUserDefinedField2(rowData, validationResult);
		
		isVaidUserDefinedField3(rowData, validationResult);
	}

	private void isVaidUserDefinedField3(Object[] rowData,
			List<ProcessingResult> validationResult) {
		
		 int index = 28;

			if (isPresent(rowData[index])) {
				if (rowData[index].toString().length() > 500) {
					rowData[index] = rowData[index].toString().substring(0, 500);
				}
			}
			return;
		
	}

	private void isVaidUserDefinedField2(Object[] rowData,
			List<ProcessingResult> validationResult) {
		
		 int index = 27;

			if (isPresent(rowData[index])) {
				if (rowData[index].toString().length() > 500) {
					rowData[index] = rowData[index].toString().substring(0, 500);
				}
			}
			return;
		
	}

	private void isVaidUserDefinedField1(Object[] rowData,
			List<ProcessingResult> validationResult) {
		
		 int index = 26;

			if (isPresent(rowData[index])) {
				if (rowData[index].toString().length() > 500) {
					rowData[index] = rowData[index].toString().substring(0, 500);
				}
			}
			return;
		
	}

	private void isVaidPaymentDescription(Object[] rowData,
			List<ProcessingResult> validationResult) {
		
		 int index = 16;

			if (isPresent(rowData[index])) {
				if (rowData[index].toString().length() > 500) {
					rowData[index] = rowData[index].toString().substring(0, 500);
				}
			}
			return;
		
	}

	private void isVaidRemarksforDeductions(Object[] rowData,
			List<ProcessingResult> validationResult) {
		
		 int index = 12;

			if (isPresent(rowData[index])) {
				if (rowData[index].toString().length() > 100) {
					rowData[index] = rowData[index].toString().substring(0, 100);
				}
			}
			return;
		
	}

	private void isValidProfitCentre(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 24;

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
		}
		return;

	}

	private void isValidDivision(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 25;

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
		}
		return;

	}

	private void isValidPlantCode(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 23;

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
		}
		return;

	}

	private void isValidSupplierName(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 3;

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
		}
		return;

	}

	private void isValidSupplierCode(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 4;

		if (isPresent(rowData[index])) {
			if (rowData[index].toString().length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
		}
		return;

	}

	
	 private void isValidCurrencyCode(Object[] rowData, List<ProcessingResult>
	  validationResult) {
		 int index = 19;

			if (isPresent(rowData[index])) {
				if (rowData[index].toString().length() > 16) {
					rowData[index] = rowData[index].toString().substring(0, 16);
				}
			}
			return;
	  }
	 
}
