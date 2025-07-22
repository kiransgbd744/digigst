package com.ey.advisory.app.services.structuralvalidation.gstr9;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnAsEnteredEntity;
import com.ey.advisory.app.services.docs.Gstr9HsnExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr9HsnStructuralValidatorChain")
public class Gstr9HsnStructuralValidatorChain {

	@Autowired
	@Qualifier("Gstr9HsnExcelConvertion")
	private Gstr9HsnExcelConvertion gstr9ExcelConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()
			.put(0, new ValidationRule[] { new Gstr9HsnGstinValidationRule() })
			.put(1, new ValidationRule[] { new Gstr9FyValidationRule() })
			.put(2, new ValidationRule[] { new Gstr9TableValidationRule() })
			.put(3, new ValidationRule[] { new Gstr9HsnValidationRule() })
			.put(4, new ValidationRule[] { new Gstr9DescValidationRule() })
			.put(5, new ValidationRule[] { new Gstr9RateValidationRule() })
			.put(6, new ValidationRule[] { new Gstr9UqcValidationRule() })
			.put(7, new ValidationRule[] {
					new Gstr9TotalQuantityValidationRule() })
			.put(8, new ValidationRule[] {
					new Gstr9TaxableValueValidationRule() })
			.put(9, new ValidationRule[] {
					new Gstr9ConcessionalValidationRule() })
			.put(10, new ValidationRule[] { new Gstr9IgstValidationRule() })
			.put(11, new ValidationRule[] { new Gstr9CgstValidationRule() })
			.put(12, new ValidationRule[] { new Gstr9SgstValidationRule() })
			.put(13, new ValidationRule[] { new Gstr9CessValidationRule() })

			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap,
			List<Gstr9HsnAsEnteredEntity> saveExcelAll) {

		Set<String> keySet = new HashSet<String>();
		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();

		Map<String, List<Object[]>> allDocsMap = rawDocMap.stream()
				.collect(Collectors.groupingBy(doc -> getInvKey(doc)));

		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = saveExcelAll.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = getInvKey(obj);
			List<Object[]> values = allDocsMap.get(key); // All values matched
															// the key
			List<String> listStr = new ArrayList<>();// All value line items

			if (CollectionUtils.isNotEmpty(values)) {
				values.stream().filter(value -> value != null)
						.forEach(value -> listStr.add(getAllLineItems(value)));
			}

			String sno = getAllLineItems(obj); // single line item
			if (CollectionUtils.isNotEmpty(listStr)
					&& listStr.stream().findFirst().isPresent() && !listStr
							.stream().findFirst().get().equalsIgnoreCase(sno)) { // always
																					// taking
																					// first
																					// value
				List<ProcessingResult> errors = new ArrayList<>();
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DUP_RECORD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER6165",
						"Values should not be same across all line items of a document.",
						location));
				results.addAll(errors);
				// continue;
			}

			for (int i = 0; i < 14; i++) {
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
				/*
				 * String keys = key.concat(GSTConstants.SLASH)
				 * .concat(id.toString()); map.computeIfAbsent(keys, k -> new
				 * ArrayList<ProcessingResult>()).addAll(results);
				 */

				map.put(key, results);
			}
			// is++;

		}
		return map;
	}

	private String getAllLineItems(Object[] obj) {
		String desc = (obj[4] != null) ? (String.valueOf(obj[4])).trim() : "";

		String total = (obj[7] != null) ? (String.valueOf(obj[7])).trim() : "";
		String taxValue = (obj[8] != null) ? (String.valueOf(obj[8])).trim()
				: "";
		String concessional = (obj[9] != null) ? (String.valueOf(obj[9])).trim()
				: "";
		String igst = (obj[10] != null) ? (String.valueOf(obj[10])).trim() : "";

		String cgst = (obj[11] != null) ? (String.valueOf(obj[11])).trim() : "";
		String sgst = (obj[12] != null) ? (String.valueOf(obj[12])).trim() : "";
		String cess = (obj[13] != null) ? (String.valueOf(obj[13])).trim() : "";

		return new StringJoiner("|").add(desc).add(total).add(taxValue)
				.add(concessional).add(igst).add(cgst).add(sgst).add(cess)
				.toString();
	}

	public String getInvKey(Object[] obj) {

		String gstin = (obj[0] != null && !obj[0].toString().trim().isEmpty())
				? String.valueOf(obj[0]).trim() : null;

		String fy = (obj[1] != null && !obj[1].toString().trim().isEmpty())
				? String.valueOf(obj[1]).trim() : null;

		LocalDate date = DateFormatForStructuralValidatons.parseObjToDate(fy);
		if (date != null) {
			fy = date.toString();
		}

		String tableNumber = (obj[2] != null
				&& !obj[2].toString().trim().isEmpty())
						? String.valueOf(obj[2]).trim() : null;

		String hsnL = (obj[3] != null && !obj[3].toString().trim().isEmpty())
				? String.valueOf(obj[3]).trim() : null;

		String rate = (obj[5] != null && !obj[5].toString().trim().isEmpty())
				? String.valueOf(obj[5]).trim() : null;

		String uqc = (obj[6] != null && !obj[6].toString().trim().isEmpty())
				? String.valueOf(obj[6]).trim() : null;

		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(gstin).add(fy)
				.add(tableNumber).add(hsnL).add(rate).add(uqc).toString();
	}

}
