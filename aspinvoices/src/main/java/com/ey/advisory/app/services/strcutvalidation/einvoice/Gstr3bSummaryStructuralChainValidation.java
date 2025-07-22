/**
 * 
 */
package com.ey.advisory.app.services.strcutvalidation.einvoice;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * @author Shashikant.Shukla
 *
 */

@Component("Gstr3bSummaryStructuralChainValidation")
public class Gstr3bSummaryStructuralChainValidation {

	@Autowired
	@Qualifier("EhcacheGstinTaxperiod")
	private EhcacheGstinTaxperiod ehcachegstinTaxPeriod;
	
	private static final String UPLOAD = "Upload";
	private static List<String> igstList = Arrays.asList("3.1.c", "3.1.e",
			"3.1.1.b", "5.a.2", "5.b.2");
	private static List<String> cgstList = Arrays.asList("3.1.b", "3.1.c",
			"3.1.e", "3.1.1.b", "3.2.a", "3.2.b", "3.2.c", "4.a.1", "4.a.2",
			"5.a.1", "5.b.1");
	private static List<String> sgstList = Arrays.asList("3.1.b", "3.1.c",
			"3.1.e", "3.1.1.b", "3.2.a", "3.2.b", "3.2.c", "4.a.1", "4.a.2",
			"5.a.1", "5.b.1");
	private static List<String> cessList = Arrays.asList("3.1.c", "3.1.e",
			"3.1.1.b", "3.2.a", "3.2.b", "3.2.c");
	private static List<String> posList = Arrays.asList("3.2.a", "3.2.b",
			"3.2.c");
	private static List<String> cgstSgstValidationList = Arrays.asList("5.1.a", "5.1.b", "4.b.2");
	private static List<String> negativeAmountAllowedList = Arrays.asList("4.a.1", "4.a.2",
			 "4.a.3", "4.a.4","4.a.5", "4.d.1", "4.d.2");
	
	private static List<String> onlyPositiveAmountAllowedList = Arrays.asList("4.b.1", "4.b.2", "4.d.2", "5.a.1", "5.a.2",
			 "5.b.1", "5.b.2", "5.1.a", "5.1.b");
	
	private void isGstinValid(Object[] rowData,
			List<ProcessingResult> validationResult, List<String> gstinList) {
		int index = 1;
		String errorCode = "ER052";
		String errMsg = "Invalid Supplier GSTIN.";
		if (!isPresent(rowData[index])) {
			errMsg = String.format("Invalid Supplier GSTIN.");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			return;

		}
		if (rowData[index] != null) {
			rowData[index] = rowData[index].toString().trim();

			if (rowData[index].toString().trim().length() == 15) {
				String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
						+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
						+ "[A-Za-z0-9][A-Za-z0-9]$";

				Pattern pattern = Pattern.compile(regex);
				String vendorGstin = "";
				Matcher matcher = pattern
						.matcher(rowData[index].toString().trim());
				vendorGstin = rowData[index].toString().trim();

				if (!matcher.matches() || vendorGstin.length() != 15) {
					errMsg = String.format("Invalid Supplier GSTIN.");
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCode, errMsg);
					validationResult.add(result);
					return;
				}
				if (!gstinList.contains(vendorGstin.trim().toUpperCase())) {
					String errMsg1 = "Invalid Supplier GSTIN.";
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SUPPLIER_GSTIN_OR_PAN);
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCode, errMsg1);
					validationResult.add(result);
					return;
				}
				if (rowData[index].toString().length() > 100) {
					rowData[index] = rowData[index].toString().substring(0,
							100);
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCode, errMsg);
					validationResult.add(result);
					return;
				}
			} else {
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}
		}
		return;
	}

	private void isValidTotalTaxableAmount(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 3;
		String errorCodeBlank = "ER075";

		if (isPresent(rowData[index])) {
			if (NumberFomatUtil.isNumber(rowData[index])) {
				if(onlyPositiveAmountAllowedList
						.contains(rowData[2].toString().toLowerCase())){
					if (NumberFomatUtil.getBigDecimal(rowData[index])
							.signum() == -1) {
						String errMsg = String
								.format("Invalid TotalTaxable Value");
						ProcessingResult result = new ProcessingResult(UPLOAD,
								errorCodeBlank, errMsg);
						validationResult.add(result);
						return;
					}
				}
				String errMsg = String.format("Invalid TotalTaxable Value");
				boolean isValid = NumberFomatUtil
						.is15And2digValidDec(rowData[index]);
				if (!isValid) {
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
				if (rowData[index].toString().length() > 100) {
					rowData[index] = rowData[index].toString().substring(0,
							100);
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
			} else {
				String errMsg = String.format("Invalid TotalTaxable Value");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);
				return;

			}
		}
		return;
	}

	private void isValidIgstAmount(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 4;
		String errorCodeBlank = "ER077";
		if (isPresent(rowData[index])) {
			if (NumberFomatUtil.isNumber(rowData[index])) {
				if (onlyPositiveAmountAllowedList
						.contains(rowData[2].toString().toLowerCase())) {
					if (NumberFomatUtil.getBigDecimal(rowData[index])
							.signum() == -1) {
						String errMsg = String.format("Invalid IGST Amount");
						ProcessingResult result = new ProcessingResult(UPLOAD,
								errorCodeBlank, errMsg);
						validationResult.add(result);
						return;
					}
				}
				String errMsg = String.format("Invalid IGST Amount");
				boolean isValid = NumberFomatUtil
						.is15And2digValidDec(rowData[index]);
				if (!isValid) {
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
				if (rowData[2] != null) {
					if (igstList
							.contains(rowData[2].toString().toLowerCase())) {

						ProcessingResult result = new ProcessingResult(UPLOAD,
								errorCodeBlank, errMsg);
						validationResult.add(result);
						return;
					}
				}
				if (rowData[index].toString().length() > 100) {
					rowData[index] = rowData[index].toString().substring(0,
							100);
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
			} else {
				String errMsg = String.format("Invalid IGST Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);
				return;

			}
		}
		return;

	}

	private void isValidCgstAmount(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 5;
		String errorCodeBlank = "ER079";

		if (isPresent(rowData[index])) {
			if (NumberFomatUtil.isNumber(rowData[index])) {
				if (onlyPositiveAmountAllowedList
						.contains(rowData[2].toString().toLowerCase())) {
					if (NumberFomatUtil.getBigDecimal(rowData[index])
							.signum() == -1) {
						String errMsg = String.format("Invalid CGST Amount");
						ProcessingResult result = new ProcessingResult(UPLOAD,
								errorCodeBlank, errMsg);
						validationResult.add(result);
						return;
					}
				}
				String errMsg = String.format("Invalid CGST Amount");
				boolean isValid = NumberFomatUtil
						.is15And2digValidDec(rowData[index]);
				if (!isValid) {
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
				if (rowData[2] != null) {
					if (cgstList
							.contains(rowData[2].toString().toLowerCase())) {
						ProcessingResult result = new ProcessingResult(UPLOAD,
								errorCodeBlank, errMsg);
						validationResult.add(result);
						return;
					}
				}
				if (!rowData[5].toString().trim().equalsIgnoreCase(rowData[6].toString().trim())) {
					if (!cgstSgstValidationList
							.contains(rowData[2].toString().trim().toLowerCase())) {
						String errMsg1 = String.format(
								"CGST And SGST should have the same value");
						ProcessingResult result = new ProcessingResult(UPLOAD,
								errorCodeBlank, errMsg1);
						validationResult.add(result);
						return;
					}
				}
				if (rowData[index].toString().length() > 100) {
					rowData[index] = rowData[index].toString().substring(0,
							100);
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
			} else {
				String errMsg = String.format("Invalid CGST Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);
				return;

			}
		}
		return;

	}

	private void isValidSgstAmount(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 6;
		String errorCodeBlank = "ER081";

		if (isPresent(rowData[index])) {
			if (NumberFomatUtil.isNumber(rowData[index])) {
				if (onlyPositiveAmountAllowedList
						.contains(rowData[2].toString().toLowerCase())) {
					if (NumberFomatUtil.getBigDecimal(rowData[index])
							.signum() == -1) {
						String errMsg = String
								.format("Invalid SGST / UTGST Amount");
						ProcessingResult result = new ProcessingResult(UPLOAD,
								errorCodeBlank, errMsg);
						validationResult.add(result);
						return;
					}
				}
				String errMsg = String.format("Invalid SGST / UTGST Amount");
				boolean isValid = NumberFomatUtil
						.is15And2digValidDec(rowData[index]);
				if (!isValid) {
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
				if (rowData[2] != null) {
					if (sgstList
							.contains(rowData[2].toString().toLowerCase())) {
						ProcessingResult result = new ProcessingResult(UPLOAD,
								errorCodeBlank, errMsg);
						validationResult.add(result);
						return;
					}
				}
				if (rowData[index].toString().length() > 100) {
					rowData[index] = rowData[index].toString().substring(0,
							100);
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
			} else {
				String errMsg = String.format("Invalid SGST / UTGST Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);
				return;

			}
		}
		return;

	}

	private void isValidCessAmount(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 7;
		String errorCodeBlank = "ER083";

		if (isPresent(rowData[index])) {
			if (NumberFomatUtil.isNumber(rowData[index])) {
				if (onlyPositiveAmountAllowedList
						.contains(rowData[2].toString().toLowerCase())) {
					if (NumberFomatUtil.getBigDecimal(rowData[index])
							.signum() == -1) {
						String errMsg = String.format("Invalid Cess Amount");
						ProcessingResult result = new ProcessingResult(UPLOAD,
								errorCodeBlank, errMsg);
						validationResult.add(result);
						return;
					}
				}
				String errMsg = String.format("Invalid Cess Amount");
				boolean isValid = NumberFomatUtil
						.is15And2digValidDec(rowData[index]);
				if (!isValid) {
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
				if (rowData[2] != null) {
					if (cessList
							.contains(rowData[2].toString().toLowerCase())) {
						ProcessingResult result = new ProcessingResult(UPLOAD,
								errorCodeBlank, errMsg);
						validationResult.add(result);
						return;
					}
				}
				if (rowData[index].toString().length() > 100) {
					rowData[index] = rowData[index].toString().substring(0,
							100);
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCodeBlank, errMsg);
					validationResult.add(result);
					return;
				}
			} else {
				String errMsg = String.format("Invalid Cess Amount");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);
				return;

			}
		}
		return;

	}

	private void isValidReturnPeriod(Object[] rowData,
			List<ProcessingResult> validationResult, List<String> gstinList) {

		int index = 0;
		String errorCode = "ER051";
		String errMsg = null;
		if (!isPresent(rowData[index])) {
			errMsg = String.format("Return Period cannot be left blank.");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			return;

		}
		if (rowData[index] != null) {
			rowData[index] = rowData[index].toString().trim();
			if (!rowData[index].toString().matches("[0-9]+")
					|| rowData[index].toString().trim().length() != 6) {
				errMsg = String.format("Invalid Return Period");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);

				validationResult.add(result);
				return;
			}

			if (rowData[index].toString().matches("[0-9]+")) {
				errMsg = String.format("Invalid Return Period");
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

			if (rowData[index] != null) {
				if (Integer.parseInt(rowData[index].toString().trim()
						.substring(2, 6)) != 0) {
					String tax = "01" + rowData[index].toString().trim();
					DateTimeFormatter formatter = DateTimeFormatter
							.ofPattern("ddMMyyyy");
					LocalDate returnPeriod = LocalDate.parse(tax, formatter);
					String pregst = "01082022";
					LocalDate validReturnDate = LocalDate.parse(pregst,
							formatter);
					if (validReturnDate != null) {
						if (returnPeriod.compareTo(validReturnDate) < 0) {
							errMsg = String.format(
									"GSTR-3B Form has been updated by the government, upload template cannot be processed for tax period before 082022");
							ProcessingResult result = new ProcessingResult(
									UPLOAD, errorCode, errMsg);
							validationResult.add(result);
							return;
						} else if (rowData[1] != null
								&& rowData[index] != null) {
							String gstin = rowData[1].toString().trim();
							String taxPeriod = rowData[index].toString().trim();

							ehcachegstinTaxPeriod = StaticContextHolder.getBean(
									"EhcacheGstinTaxperiod",
									EhcacheGstinTaxperiod.class);

							String groupCode = TenantContext.getTenantId();

							GstrReturnStatusEntity entity = ehcachegstinTaxPeriod
									.isGstinFiled(gstin, taxPeriod, "GSTR3B",
											"FILED", groupCode);

							if (entity != null) {
								errMsg = String.format(
										"GSTR3B for this tax period is already filed");
								ProcessingResult result = new ProcessingResult(
										UPLOAD, errorCode, errMsg);
								validationResult.add(result);
								return;
							}
						}
						if (rowData[index].toString().length() > 100) {
							rowData[index] = rowData[index].toString().substring(0,
									100);
							ProcessingResult result = new ProcessingResult(UPLOAD,
									errorCode, errMsg);
							validationResult.add(result);
							return;
						}
					} else {
						errMsg = String.format("Invalid Return Period");
						ProcessingResult result = new ProcessingResult(UPLOAD,
								errorCode, errMsg);

						validationResult.add(result);
						return;
					}
				}
			}
		} else {
			errMsg = String.format("Invalid Return Period");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			return;
		}
	}

	private void isValidPos(Object[] rowData,
			List<ProcessingResult> validationResult, List<String> gstinList) {
		int index = 8;
		String errorCode = "ER066";
		String errMsg = null;
		if (rowData[index] != null) {
			rowData[index] = rowData[index].toString().trim();

			String regex = "^[0-9]*$";
			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(rowData[index].toString().trim());
			if (rowData[index].toString().trim().length() > 2
					|| !matcher.matches()) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.POS);
				errMsg = String.format("Invalid POS");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;

			}
			if (rowData[2] != null) {
				if (!posList.contains(rowData[2].toString().toLowerCase())) {
					errMsg = String.format("Invalid POS");
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCode, errMsg);
					validationResult.add(result);
					return;
				}
			} else if (Integer.parseInt(rowData[index].toString().trim()) < 1
					|| Integer
							.parseInt(rowData[index].toString().trim()) > 38) {
				if (Integer.parseInt(rowData[index].toString().trim()) != 96
						&& Integer.parseInt(
								rowData[index].toString().trim()) != 97
						&& Integer.parseInt(
								rowData[index].toString().trim()) != 99) {
					errMsg = String.format("Invalid POS");
					ProcessingResult result = new ProcessingResult(UPLOAD,
							errorCode, errMsg);
					validationResult.add(result);
					return;
				}
			}
			if (rowData[index].toString().length() > 100) {
				rowData[index] = rowData[index].toString().substring(0,
						100);
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}
		}
		return;
	}

	public void rowDataValidation(List<ProcessingResult> validationResult,
			Object[] rowData, List<String> gstinList) {

		isValidReturnPeriod(rowData, validationResult, gstinList);

		isGstinValid(rowData, validationResult, gstinList);

		isValidTableNumber(rowData, validationResult, gstinList);

		isValidTotalTaxableAmount(rowData, validationResult);

		isValidIgstAmount(rowData, validationResult);

		isValidCgstAmount(rowData, validationResult);

		isValidSgstAmount(rowData, validationResult);

		isValidCessAmount(rowData, validationResult);

		isValidPos(rowData, validationResult, gstinList);
	}

	private void isValidTableNumber(Object[] rowData,
			List<ProcessingResult> validationResult, List<String> gstinList) {
		int index = 2;
		String errorCode = "ER053";
		String errMsg = "Invalid Table Number as per Master";
		List<String> tableNumberList = Arrays.asList("3.1.a", "3.1.b", "3.1.c",
				"3.1.d", "3.1.e", "3.1.1.a", "3.1.1.b", "3.2.a", "3.2.b",
				"3.2.c", "4.a.1", "4.a.2", "4.a.3", "4.a.4", "4.a.5", "4.b.1",
				"4.b.2", "4.d.1", "4.d.2", "5.a.1", "5.a.2", "5.b.1", "5.b.2",
				"5.1.a", "5.1.b");
		if (!isPresent(rowData[index])) {
			errMsg = String.format("Invalid Table Number");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		} else if (rowData[index] == null) {
			errMsg = String.format("Invalid Table Number");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		} else if (rowData[index] != null) {
			if (!tableNumberList
					.contains(rowData[index].toString().toLowerCase())) {
				errMsg = String.format("Invalid Table Number");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}
		}
	}
}
