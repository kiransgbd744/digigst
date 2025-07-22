package com.ey.advisory.app.services.strcutvalidation.einvoice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.strcutvalidation.Isd.ActionTypeValidation;
import com.ey.advisory.app.services.strcutvalidation.Isd.GstinDistributionValidation;
import com.ey.advisory.app.services.strcutvalidation.Isd.IsdGstnValidation;
import com.ey.advisory.app.services.strcutvalidation.Isd.ItemSerialNoValidationRule;
import com.ey.advisory.app.services.strcutvalidation.Isd.SGstinValidation;
import com.ey.advisory.app.services.strcutvalidation.outward.DocumentDateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.DocumentNoValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.DocumentTypeValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.EinvoiceHeaderStructuralValidationUtil;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

@Component("IsdDistributionStructuralChain")
public class IsdDistributionStructuralChain {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(IsdDistributionStructuralChain.class);
	private static final Map<Integer, ValidationRule[]> ISD_STRUCT_VAL_RULES = 
			new ImmutableMap.Builder<Integer, ValidationRule[]>()

			.put(0, new ValidationRule[] { new IsdGstnValidation() })
			.put(1, new ValidationRule[] { new SGstinValidation() })
			.put(2, new ValidationRule[] { new DocumentTypeValidationRule() })
																		
			.put(3, new ValidationRule[] { new DocumentNoValidationRule() })
																		
			.put(4, new ValidationRule[] { new DocumentDateValidationRule()})
			.put(5, new ValidationRule[] { new ItemSerialNoValidationRule() })
			.put(6, new ValidationRule[] { new GstinDistributionValidation() })
			.put(7, new ValidationRule[] { new ActionTypeValidation() })
			.build();

	
	public Map<String, List<ProcessingResult>> validation(
			Map<String, List<Object[]>> rawDocMap) {

		Map<String, List<ProcessingResult>> map 
		          = new HashMap<String, List<ProcessingResult>>();

		rawDocMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();

			// First do normal structural valdiations (cell by cell)
			List<ProcessingResult> results = new ArrayList<>();

			for (Object[] obj : value) {

				for (int i = 0; i < 8; i++) {
					// First get the validators to be applied
					ValidationRule[] rules = ISD_STRUCT_VAL_RULES.get(i);
					LOGGER.debug(rules.toString());
					Object cellVal = obj[i];
					Arrays.stream(rules).forEach(rule -> {
						List<ProcessingResult> errors = rule.isValid(
								value.indexOf(obj), cellVal, obj, null);
						results.addAll(errors);

					});
				}
			}

			// check for incompatible values across rows in the same colummn
//			List<ProcessingResult> sameColummnErrors = strut.validate(value);

//			results.addAll(sameColummnErrors);
			List<ProcessingResult> errorsResult = EinvoiceHeaderStructuralValidationUtil
					.eliminateDuplicates(results);

			if (errorsResult != null && errorsResult.size() > 0) {
				map.put(key, errorsResult);
			}
		});
		return map;

	}
}
