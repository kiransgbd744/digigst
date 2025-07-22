package com.ey.advisory.app.services.strcutvalidation.einvoice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.strcutvalidation.common.credit.reversal.CommonSupplyIndicatorValidator;
import com.ey.advisory.app.services.strcutvalidation.common.credit.reversal.CustomerGstinValidation;
import com.ey.advisory.app.services.strcutvalidation.common.credit.reversal.DocumentDateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.common.credit.reversal.DocumentNoValidationRule;
import com.ey.advisory.app.services.strcutvalidation.common.credit.reversal.DocumentTypeValidationRule;
import com.ey.advisory.app.services.strcutvalidation.common.credit.reversal.ItemSerialNoValidationRule;
import com.ey.advisory.app.services.strcutvalidation.common.credit.reversal.ReturnPeriodValidationRule;
import com.ey.advisory.app.services.strcutvalidation.common.credit.reversal.SGstinValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.EinvoiceHeaderStructuralValidationUtil;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

@Component("CommonCreditReversalStructuralChain")
public class CommonCreditReversalStructuralChain {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CommonCreditReversalStructuralChain.class);
	private static final Map<Integer, ValidationRule[]> COMMON_CREDIT_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

			.put(0, new ValidationRule[] { new CustomerGstinValidation() })
			.put(1, new ValidationRule[] { new DocumentTypeValidationRule() })
			.put(2, new ValidationRule[] { new DocumentNoValidationRule() })
			.put(3, new ValidationRule[] { new DocumentDateValidationRule() })
			.put(4, new ValidationRule[] { new SGstinValidationRule() })
			.put(5, new ValidationRule[] { new ItemSerialNoValidationRule() })
			.put(6, new ValidationRule[] {
					new CommonSupplyIndicatorValidator() })
			.put(7, new ValidationRule[] { new ReturnPeriodValidationRule() })
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

				for (int i = 0; i < 8; i++) {
					// First get the validators to be applied
					ValidationRule[] rules = COMMON_CREDIT_STRUCT_VAL_RULES
							.get(i);
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
			// List<ProcessingResult> sameColummnErrors = strut.validate(value);

			// results.addAll(sameColummnErrors);
			List<ProcessingResult> errorsResult = EinvoiceHeaderStructuralValidationUtil
					.eliminateDuplicates(results);

			if (errorsResult != null && errorsResult.size() > 0) {
				map.put(key, errorsResult);
			}
		});
		return map;

	}
}
