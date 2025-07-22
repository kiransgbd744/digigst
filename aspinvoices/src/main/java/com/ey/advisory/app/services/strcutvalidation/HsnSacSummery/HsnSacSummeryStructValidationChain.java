package com.ey.advisory.app.services.strcutvalidation.HsnSacSummery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.SRFileToHsnDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.ProductDescriptionValidationRule;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

@Component("HsnSacSummeryStructValidationChain")
public class HsnSacSummeryStructValidationChain {

	@Autowired
	@Qualifier("SRFileToHsnDetailsConvertion")
	private SRFileToHsnDetailsConvertion sRFileToHsnDetailsConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] { new SerialNOValidator() })
			.put(1, new ValidationRule[] { new HsnSgstinValidationRule() })
			.put(2, new ValidationRule[] { new HsnRetPeriodValidationRule() })
			.put(3, new ValidationRule[] { new HsnRecordTypeValidationRule() })
			.put(4, new ValidationRule[] { new HsnMasterValidationRule() })
			.put(5, new ValidationRule[] {
					new ProductDescriptionValidationRule() })
			.put(6, new ValidationRule[] { new HsnUOMValidationRule() })
			.put(7, new ValidationRule[] { new HsnQuantityValidationRule() })
			.put(8, new ValidationRule[] { new RateValidationRule() })
			.put(9, new ValidationRule[] { new HsnTaxableValidationRule() })
			.put(10, new ValidationRule[] { new HsnIgstValidationRule() })
			.put(11, new ValidationRule[] { new HsnCgstValidationRule() })
			.put(12, new ValidationRule[] { new HsnSgstValidationRule() })
			.put(13, new ValidationRule[] { new HsnCessValidationRule() })
			.put(14, new ValidationRule[] { new HsnToatlValidationRule() })// invoice
																			// Value
			.build();

	public Map<String, List<ProcessingResult>> validation(
			Map<String, List<Object[]>> rawDocMap) {

		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();
		        rawDocMap.entrySet().forEach(entry -> {
		            String key = entry.getKey();
		            List<Object[]> value = entry.getValue();

		            // First do normal structural valdiations (cell by cell)
		            List<ProcessingResult> results = new ArrayList<>();
		            for (Object[] obj : value) {
		                for (int i = 0; i < 15; i++) {
		                    // First get the validators to be applied
		                    ValidationRule[] rules = STRUCT_VAL_RULES.get(i);
		                    Object cellVal = obj[i];
		                    Arrays.stream(rules).forEach(rule -> {
		                        List<ProcessingResult> errors = rule.isValid(cellVal, obj, null);
		                        results.addAll(errors);
		                    });
		                }
		            };

		            if (results != null && results.size() > 0) {

		                map.put(key, results);

		            }

		        });

		        return map;


		    }
}
