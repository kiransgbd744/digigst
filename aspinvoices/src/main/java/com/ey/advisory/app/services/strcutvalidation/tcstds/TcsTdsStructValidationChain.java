package com.ey.advisory.app.services.strcutvalidation.tcstds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr2XExcelTcsTdsEntity;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("TcsTdsStructValidationChain")
public class TcsTdsStructValidationChain {

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] {
					new TcsTdsActionSaveToDigiGstValidation() })
			.put(1, new ValidationRule[] {
					new TcsTdsDigiGstRemarksValidation() })
			.put(2, new ValidationRule[] {
					new TcsTdsDigiGstCommentsValidation() })
			.put(3, new ValidationRule[] {
					new TcsTdsTaxpayerGstinValidationRule() })
			.put(4, new ValidationRule[] { new TcsTdsCategoryValidation() })
			
			.put(5, new ValidationRule[] {
					new TcsTdsRetPeriodValidationRule() })
			.put(6, new ValidationRule[] {
					new TcsTdsMonthOfDeductorValidation() })
			.put(7, new ValidationRule[] {
					new TcsTdsDeductorGstinValidationRule() })
			.put(8, new ValidationRule[] {
					new TcsTdsDeductorNameValidationRule() })
			.put(9, new ValidationRule[] {
					new TcsTdsDocNoValidation() })
			.put(10, new ValidationRule[] {
					new TcsTdsDocDateValidation() })
			
			.put(11, new ValidationRule[] {
					new TcsTdsOrgMonthOfDeductorValidation() })
			
			.put(12, new ValidationRule[] {
					new TcsTdsOrgDocNoValidation() })
			
			.put(13, new ValidationRule[] {
					new TcsTdsOrgDocDateValidation() })
			
			.put(14, new ValidationRule[] { new TcsTdsSupToRegBuyer() })
			.put(15, new ValidationRule[] {
					new TcsTdsSuRetByRegBuyersValidation() })
			
			.put(16, new ValidationRule[] { new TcsTdsTotalAmtValidation() })
			
			.put(17, new ValidationRule[] { new TcsTdsIgstAmtValidation() })
			.put(18, new ValidationRule[] { new TcsTdsCgstAmtValidation() })
			.put(19, new ValidationRule[] { new TcsTdsSgstAmtValidation() })
			
			.put(20, new ValidationRule[] { new TcsTdsInvoiceValueValidation() })
			.put(21, new ValidationRule[] {
					new TcsTdsOrgTaxableValueValidation() })
			.put(22, new ValidationRule[] {
					new TcsTdsOrgInvoiceValueValidation() })
			.put(23, new ValidationRule[] {
					new TcsTdsPosValidation() })
			.put(24, new ValidationRule[] {
					new TcsTdsCheckSumValidation() })
			.put(25, new ValidationRule[] {
					new TcsTdsActionSaveToGstinValidation() })
			.put(26, new ValidationRule[] {
					new TcsTdsGstnRemarksValidation() })
			
			.put(27, new ValidationRule[] {
					new TcsTdsGstnCommentsValidation() })
			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap,
			List<Gstr2XExcelTcsTdsEntity> excelDataSave) {
		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		Map<String, List<Object[]>> allDocsMap = rawDocMap.stream()
				.collect(Collectors.groupingBy(doc -> getInvKey(doc)));

		for (Object[] obj : rawDocMap) {
			List<ProcessingResult> results = new ArrayList<>();
			Long id = excelDataSave.get(is).getId();
			String key = getInvKey(obj);
			List<Object[]> value = allDocsMap.get(key);
			List<String> listStr = new ArrayList<>();
			if (value != null && value.size() > 1) {
				for (Object[] val : value) {
					String userAction = getUserAction(val);
					listStr.add(userAction);
				}

				List<ProcessingResult> errors = new ArrayList<>();
				String userAction = getUserAction(obj);
				if (!listStr.get(0).equalsIgnoreCase(userAction)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.DIGIGST_ACTION);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
							"ER5935",
							"User Action should be same across all line items of a document.",
							location));
					results.addAll(errors);

				}
			}

			for (int i = 0; i < 28; i++) {
				// First get the validators to be applied
				ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

				Object cellVal = obj[i];
				Arrays.stream(rules).forEach(rule -> {
					List<ProcessingResult> errors = rule.isValid(cellVal, obj,
							null);
					results.addAll(errors);

				});
			}
			if (results != null && results.size() > 0) {
				String keys = key.concat(GSTConstants.SLASH)
						.concat(id.toString());
				map.computeIfAbsent(keys,
						k -> new ArrayList<ProcessingResult>()).addAll(results);
			}
			is++;
		}

		return map;

	}

	private String getUserAction(Object[] obj) {
		String userAction = (obj[0] != null) ? (String.valueOf(obj[0])).trim()
				: "N";
		return userAction;
	}

	private static String getInvKey(Object[] obj) {
		String type = (obj[4] != null) ? (String.valueOf(obj[4])).trim()
				: "";
		String gstin = (obj[3] != null) ? (String.valueOf(obj[3])).trim()
				: "";
		String taxPeriod = (obj[5] != null) ? (String.valueOf(obj[5])).trim()
				: "";
		String gstinOfDeductorCollector = (obj[7] != null) ? (String.valueOf(obj[7])).trim()
				: "";
		String month = (obj[6] != null) ? (String.valueOf(obj[6])).trim()
				: "";
		String orgMonth = (obj[11] != null)
				? (String.valueOf(obj[11])).trim() : "";
		return new StringJoiner("|").add(type).add(gstin)
				.add(taxPeriod).add(gstinOfDeductorCollector).add(month)
				.add(orgMonth).toString();
	}
}
