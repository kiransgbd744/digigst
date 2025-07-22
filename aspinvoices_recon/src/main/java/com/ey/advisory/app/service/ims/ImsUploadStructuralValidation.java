package com.ey.advisory.app.service.ims;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.ProcessingResult;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("ImsUploadStructuralValidation")
public class ImsUploadStructuralValidation {

	private static final List<String> TableTypeList = Arrays.asList("B2B",
			"B2BA", "CN", "DN", "CNA", "DNA", "ECOM", "ECOMA", "CDN", "CDNA");

	public void rowDataValidation(List<ProcessingResult> validationResult,
			Object[] rowData, List<Long> notAvailbleIds,
			Map<String, ImsHeaderDto> headerObjListMap) {

		LOGGER.debug("Inside ImsUploadStructuralValidation");

		isResponseValid(rowData, validationResult);
		isUniqueIdValid(rowData, validationResult, notAvailbleIds,
				headerObjListMap);

		isValidateItcReductionFields(rowData, validationResult);

	}

	/*
	 * private void isPendingActionBlockValid(Object[] rowData,
	 * List<ProcessingResult> validationResult, Long id, Map<String,
	 * ImsHeaderDto> headerObjListMap, String uniqueId) {
	 * 
	 * String errMsg = String.format("Pending action is not allowed");
	 * 
	 * ImsHeaderDto dto = headerObjListMap.get(uniqueId);
	 * 
	 * if (!isPresent(dto)) {
	 * 
	 * return; } if (!isPresent(dto.getIsPendingActionBlocked())) {
	 * 
	 * return; } String pendingActionResp = dto.getIsPendingActionBlocked(); if
	 * (isPresent(rowData[0])) {
	 * 
	 * String actionTaken = (rowData[0]).toString().trim();
	 * 
	 * if ((pendingActionResp.equalsIgnoreCase("Yes") ||
	 * pendingActionResp.equalsIgnoreCase("Y")) &&
	 * (actionTaken.equalsIgnoreCase("P") ||
	 * actionTaken.equalsIgnoreCase("Pending"))) { ProcessingResult result = new
	 * ProcessingResult("UPLOAD", "ER101", errMsg);
	 * validationResult.add(result); }
	 * 
	 * }
	 * 
	 * }
	 */

	private void isUniqueIdValid(Object[] rowData,
			List<ProcessingResult> validationResult, List<Long> notAvailbleIds,
			Map<String, ImsHeaderDto> headerObjListMap) {

		int index = 36;
		String errMsg = String.format("Invalid UniqueID");

		if (!isPresent(rowData[index])) {

			ProcessingResult result = new ProcessingResult("UPLOAD", "ER102",
					errMsg);

			validationResult.add(result);
			return;
		}

		String uniqueId = (rowData[index]).toString().trim();
		Long id = 0L;

		String[] idStrArr = uniqueId.split("-");
		if (idStrArr.length == 2) {

			String tableType = idStrArr[0];
			if (!TableTypeList.contains(tableType)) {
				ProcessingResult result = new ProcessingResult("UPLOAD",
						"ER102", errMsg);
				validationResult.add(result);
				return;
			}
			String idStr = idStrArr[1];
			try {
				id = Long.parseLong(idStr);
				if (notAvailbleIds.contains(id)) {
					ProcessingResult result = new ProcessingResult("UPLOAD",
							"ER102", errMsg);
					validationResult.add(result);
					return;
				}

			} catch (NumberFormatException e) {
				LOGGER.error("not a valid number givenin uniqueId :", e);
				ProcessingResult result = new ProcessingResult("UPLOAD",
						"ER102", errMsg);
				validationResult.add(result);
				return;
			}
		} else {
			ProcessingResult result = new ProcessingResult("UPLOAD", "ER102",
					errMsg);
			validationResult.add(result);
			return;
		}

		/*
		 * isPendingActionBlockValid(rowData, validationResult, id,
		 * headerObjListMap, uniqueId);
		 */

	}

	private void isResponseValid(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 0;
		String errMsg = String.format("Invalid Action Response");

		if (!isPresent(rowData[index])) {

			ProcessingResult result = new ProcessingResult("UPLOAD", "ER103",
					errMsg);

			validationResult.add(result);
			return;
		}
		String response = (rowData[index]).toString().trim();
		if (Stream.of("A", "P", "R", "N", "Accept", "Reject", "Pending",
				"No Action").noneMatch(response::equalsIgnoreCase)) {
			ProcessingResult result = new ProcessingResult("UPLOAD", "ER103",
					errMsg);
			validationResult.add(result);

		}

	}

	private void isValidateItcReductionFields(Object[] rowData,
			List<ProcessingResult> validationResult) {

		/* ------------------------------------------------------------------ */
		/* 1. Read raw values with full null-safety */
		/* ------------------------------------------------------------------ */
		String itcReductionRequired = (rowData[26] != null)
				? rowData[26].toString().trim().toUpperCase()
				: "";

		// amountFields = [ IGST, CGST, SGST, Cess ]
		String[] amountFields = new String[4];
		for (int i = 0; i < 4; i++) {
			amountFields[i] = (rowData[27 + i] != null)
					? rowData[27 + i].toString().trim()
					: "";
		}

		/* ------------------------------------------------------------------ */
		/* 2. Validate flag column */
		/* ------------------------------------------------------------------ */
		if (!itcReductionRequired.isEmpty() && !Stream.of("Y", "YES", "N", "NO")
				.anyMatch(itcReductionRequired::equals)) {
			validationResult.add(new ProcessingResult("UPLOAD", "ER104",
					"ITC Reduction required can have only Y/Yes or N/No"));
		}

		/* ------------------------------------------------------------------ */
		/* 3. Validate each amount’s numeric format (Decimal(15,2)) */
		/* ------------------------------------------------------------------ */
		String[] amountErrorMsgs = {
				"Invalid IGST amount declared for ITC reduction",
				"Invalid CGST amount declared for ITC reduction",
				"Invalid SGST amount declared for ITC reduction",
				"Invalid Cess amount declared for ITC reduction" };

		BigDecimal[] parsedAmounts = new BigDecimal[4]; // keep parsed numbers
														// for later use
		for (int i = 0; i < amountFields.length; i++) {
			String raw = amountFields[i];
			if (raw.isEmpty()) {
				parsedAmounts[i] = BigDecimal.ZERO; // treat blank as zero for
													// downstream checks
				continue;
			}
			try {
				BigDecimal val = new BigDecimal(raw);
				if (val.scale() > 2 || val.precision() > 17) { // Decimal(17,2)
					throw new NumberFormatException();
				}
				
				 // Check for negative value
		        if (val.signum() < 0) {
		            validationResult.add(new ProcessingResult("UPLOAD", "ER105",
		                    amountErrorMsgs[i] + " - Negative amount is not allowed"));
		            parsedAmounts[i] = BigDecimal.ZERO;
		            continue;
		        }

				parsedAmounts[i] = val;
			} catch (NumberFormatException e) {
				validationResult.add(new ProcessingResult("UPLOAD", "ER105",
						amountErrorMsgs[i]));
				parsedAmounts[i] = BigDecimal.ZERO; // prevent cascaded NFE
			}
		}

		/* ------------------------------------------------------------------ */
		/* 4. CGST & SGST must be equal (only if at least one provided) */
		/* ------------------------------------------------------------------ */
		if (parsedAmounts[1].compareTo(BigDecimal.ZERO) != 0
				|| parsedAmounts[2].compareTo(BigDecimal.ZERO) != 0) {

			if (parsedAmounts[1].compareTo(parsedAmounts[2]) != 0) {
				validationResult.add(new ProcessingResult("UPLOAD", "ER106",
						"CGST & SGST amount declared for ITC reduction are not matched"));
			}
		}

		/* ------------------------------------------------------------------ */
		/* 5. If flag = N/blank, all amounts must be zero */
		/* ------------------------------------------------------------------ */
		if (itcReductionRequired.isEmpty() || itcReductionRequired.equals("N")
				|| itcReductionRequired.equals("NO")) {

			boolean anyNonZero = Arrays.stream(parsedAmounts)
					.anyMatch(a -> a.compareTo(BigDecimal.ZERO) != 0);

			if (anyNonZero) {
				validationResult.add(new ProcessingResult("UPLOAD", "ER107",
						"ITC reduced amount should be Zero as ITC reduction required flag is No/Blank"));
			}
		}

		/* ------------------------------------------------------------------ */
		/* 6. If Action ≠ Accept, no amount may be non-zero */
		/* ------------------------------------------------------------------ */
		String action = isPresent(rowData[0])
				? rowData[0].toString().trim().toUpperCase()
				: "";
		if (!action.equals("A") && !action.equals("ACCEPT")) {
			boolean anyNonZero = Arrays.stream(parsedAmounts)
					.anyMatch(a -> a.compareTo(BigDecimal.ZERO) != 0);
			if (anyNonZero) {
				validationResult.add(new ProcessingResult("UPLOAD", "ER108",
						"Only for Accept IMS action ITC reduction attributes can be provided"));
			}
		} else if (action.isEmpty()) { // action missing entirely
			validationResult.add(new ProcessingResult("UPLOAD", "ER108",
					"Only for Accept IMS action ITC reduction attributes can be provided"));
		}

		/* ------------------------------------------------------------------ */
		/* 7. Block ITC reduction for B2B / ECOM / DN */
		/* ------------------------------------------------------------------ */
		String tableType = extractTableTypeFromUniqueId(rowData[36]); 
		if (Stream.of("B2B", "ECOM", "DN").anyMatch(tableType::equals)) {
			boolean flagY = Stream.of("Y", "YES")
					.anyMatch(itcReductionRequired::equals);
			boolean anyNonZero = Arrays.stream(parsedAmounts)
					.anyMatch(a -> a.compareTo(BigDecimal.ZERO) != 0);

			if (flagY || anyNonZero) {
				validationResult.add(new ProcessingResult("UPLOAD", "ER109",
						"ITC reduction is not allowed for IMS records from table B2B, ECOM, DN"));
			}
		}
	}

	private String extractTableTypeFromUniqueId(Object uniqueIdObj) {
		if (uniqueIdObj == null) {
			return "";
		}

		String uniqueId = uniqueIdObj.toString().trim();

		if (uniqueId.isEmpty() || !uniqueId.contains("-")) {
			return "";
		}

		String[] parts = uniqueId.split("-");

		if (parts.length != 2 || parts[0].trim().isEmpty()
				|| parts[1].trim().isEmpty()) {
			return "";
		}

		return parts[0].trim().toUpperCase(); // expected tableType
	}

}
