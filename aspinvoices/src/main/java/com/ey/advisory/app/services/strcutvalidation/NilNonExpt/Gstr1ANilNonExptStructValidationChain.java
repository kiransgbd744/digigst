package com.ey.advisory.app.services.strcutvalidation.NilNonExpt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilNonExemptedAsEnteredEntity;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

@Component("Gstr1ANilNonExptStructValidationChain")
public class Gstr1ANilNonExptStructValidationChain {

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] { new NNESgstinValidationRule() })
			.put(1, new ValidationRule[] { new NNERetPeriodValidationRule() })
			.put(2, new ValidationRule[] { new NilHsnOrSac() })
			.put(3, new ValidationRule[] { new NilDescription() })
			.put(4, new ValidationRule[] { new NilUQC() })
			.put(5, new ValidationRule[] { new NilQuanity() })
			.put(6, new ValidationRule[] { new NilInterstateregistered() })
			.put(7, new ValidationRule[] { new NilIntrastateregistered() })
			.put(8, new ValidationRule[] { new NilInterstateUnregistered() })
			.put(9, new ValidationRule[] { new NilIntrastateUnRegistered() })
			.put(10, new ValidationRule[] { new ExmtInterstateregistered() })
			.put(11, new ValidationRule[] { new ExmtIntrastateregistered() })
			.put(12, new ValidationRule[] { new ExmtInterstateUnRegistered() })
			.put(13, new ValidationRule[] { new ExmtIntrastateUnregistered() })
			.put(14, new ValidationRule[] { new NonInterstateregistered() })
			.put(15, new ValidationRule[] { new NonIntrastateregistered() })
			.put(16, new ValidationRule[] { new NonInterstateUnRegistered() })
			.put(17, new ValidationRule[] { new NonIntrastateUnRegistered() })
			.build();

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES_OLD = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] { new NNESgstinValidationRule() })
			.put(1, new ValidationRule[] { new NNERetPeriodValidationRule() })
			.put(2, new ValidationRule[] { new NilInterstateregistered() })
			.put(3, new ValidationRule[] { new NilIntrastateregistered() })
			.put(4, new ValidationRule[] { new NilInterstateUnregistered() })
			.put(5, new ValidationRule[] { new NilIntrastateUnRegistered() })
			.put(6, new ValidationRule[] { new ExmtInterstateregistered() })
			.put(7, new ValidationRule[] { new ExmtIntrastateregistered() })
			.put(8, new ValidationRule[] { new ExmtInterstateUnRegistered() })
			.put(9, new ValidationRule[] { new ExmtIntrastateUnregistered() })
			.put(10, new ValidationRule[] { new NonInterstateregistered() })
			.put(11, new ValidationRule[] { new NonIntrastateregistered() })
			.put(12, new ValidationRule[] { new NonInterstateUnRegistered() })
			.put(13, new ValidationRule[] { new NonIntrastateUnRegistered() })
			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap,
			List<Gstr1ANilNonExemptedAsEnteredEntity> excelDataSave) {
		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = excelDataSave.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = getInvKey(obj);
			for (int i = 0; i < 18; i++) {
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

	private static String getInvKey(Object[] obj) {
		String sgstin = (obj[0] != null) ? (String.valueOf(obj[0])).trim() : "";
		String returnPeriod = (obj[1] != null) ? (String.valueOf(obj[1])).trim()
				: "";
		String hsn = (obj[2] != null) ? (String.valueOf(obj[2])).trim() : "";
		String description = (obj[3] != null) ? (String.valueOf(obj[3])).trim()
				: "";
		String uqc = (obj[4] != null) ? (String.valueOf(obj[4])).trim() : "";
		return new StringJoiner("|").add(sgstin).add(returnPeriod).add(hsn)
				.add(description).add(uqc).toString();

	}

	private static String getInvKeyOld(Object[] obj) {
		String sgstin = (obj[0] != null) ? (String.valueOf(obj[0])).trim() : "";
		String returnPeriod = (obj[1] != null) ? (String.valueOf(obj[1])).trim()
				: "";
		return new StringJoiner("|").add(sgstin).add(returnPeriod).toString();

	}

	public Map<String, List<ProcessingResult>> validationOld(
			List<Object[]> rawDocMap,
			List<Gstr1ANilNonExemptedAsEnteredEntity> excelDataSave) {
		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = excelDataSave.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = getInvKeyOld(obj);
			for (int i = 0; i < 14; i++) {
				// First get the validators to be applied
				ValidationRule[] rules = STRUCT_VAL_RULES_OLD.get(i);

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
}
