package com.ey.advisory.app.services.strcutvalidation.gstr9InOutward;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr9OutwardInwardAsEnteredEntity;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Gstr9InOutStructValidationChain")
public class Gstr9InOutStructValidationChain {

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] { new Gstr9InOutGstinValidation() })
			.put(1, new ValidationRule[] { new Gstr9InOutwardFyValidationRule() })
			.put(2, new ValidationRule[] { new Gstr9InOutwardTableNoValidationRule() })
			.put(3, new ValidationRule[] { new Gstr9NatureOfSupValidationRule() })
			.put(4, new ValidationRule[] { new Gstr9TaxValueValidation() })
			.put(5, new ValidationRule[] { new Gstr9InOutIgstAmtValidation() })
			.put(6, new ValidationRule[] { new Gstr9InOutCgstAmtValidation() })
			.put(7, new ValidationRule[] { new Gstr9InOutSgstAmtValidation() })
			.put(8, new ValidationRule[] { new Gstr9InOutCessValidation() })
			.put(9, new ValidationRule[] { new Gstr9InOutInterestValidation() })
			.put(10, new ValidationRule[] { new Gstr9InOutLateFeeValidation() })
			.put(11, new ValidationRule[] { new Gstr9InOutPenaltyValidation() })
			.put(12, new ValidationRule[] { new Gstr9InOutOtherValidation() }).build();

	public Map<String, List<ProcessingResult>> validation(List<Object[]> rawDocMap,
			List<Gstr9OutwardInwardAsEnteredEntity> excelDataSave) {

		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			List<ProcessingResult> results = new ArrayList<>();
			String key = getInvKey(obj);
			Long id = excelDataSave.get(is).getId();
			for (int i = 0; i < 13; i++) {
				// First get the validators to be applied
				ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

				Object cellVal = obj[i];
				Arrays.stream(rules).forEach(rule -> {
					List<ProcessingResult> errors = rule.isValid(cellVal, obj, null);
					results.addAll(errors);

				});
			}
			if (results != null && results.size() > 0) {
				String keys = key.concat(GSTConstants.SLASH).concat(id.toString());
				map.computeIfAbsent(keys, k -> new ArrayList<ProcessingResult>()).addAll(results);
			}
			is++;
		}
		return map;

	}

	private static String getInvKey(Object[] obj) {
		String gstin = (obj[0] != null) ? (String.valueOf(obj[0])).toUpperCase().trim() : "";
		String fy = (obj[1] != null) ? (String.valueOf(obj[1])).trim() : "";
		LocalDate date = DateFormatForStructuralValidatons.parseObjToDate(fy);
		if (date != null) {
			fy = date.toString();
		}

		String tableNo = (obj[2] != null) ? (String.valueOf(obj[2])).toUpperCase().trim() : "";
		return new StringJoiner("|").add(gstin).add(fy).add(tableNo).toString();
	}
}
