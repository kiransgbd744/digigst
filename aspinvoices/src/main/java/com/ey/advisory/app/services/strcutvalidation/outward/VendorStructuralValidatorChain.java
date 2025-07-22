package com.ey.advisory.app.services.strcutvalidation.outward;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.services.onboarding.gstinfileupload.VendorFileConversion;
import com.ey.advisory.app.services.strcutvalidation.vendor.LegalNameValidationRule;
import com.ey.advisory.app.services.strcutvalidation.vendor.SupplierTypeValidationRule;
import com.ey.advisory.app.services.strcutvalidation.vendor.SuppliergstinValidationRule;
import com.ey.advisory.app.services.strcutvalidation.vendor.VendorMobileNumberValidation;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Sasidhar Reddy
 *
 */
@Component("VendorStructuralValidatorChain")
public class VendorStructuralValidatorChain {

	@Autowired
	@Qualifier("VendorFileConversion")
	private VendorFileConversion vendorFileConversion;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(VendorStructuralValidatorChain.class);
	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

			.put(0, new ValidationRule[] { new SuppliergstinValidationRule() })
			.put(1, new ValidationRule[] { new LegalNameValidationRule() })
			.put(2, new ValidationRule[] { new SupplierTypeValidationRule() })
			.put(3, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(4, new ValidationRule[] { new OutsideIndiaValidationRule() })
			.put(5, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(6, new ValidationRule[] { new VendorMobileNumberValidation() })
			.build();

	/**
	 * This
	 * 
	 * @param rawDoc
	 * @return
	 */

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap) {

		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();

		for (Object[] obj : rawDocMap) {
			List<ProcessingResult> results = new ArrayList<>();
			String key = vendorFileConversion.getVendorValues(obj);

			for (int i = 0; i < 7; i++) {
				// First get the validators to be applied
				ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

				Object cellVal = obj[i];
				Arrays.stream(rules).forEach(rule -> {
					List<ProcessingResult> errors = rule.isValid(0, cellVal,
							obj, null);

					results.addAll(errors);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"STRUCT_VAL_RULES Checker Validations errros -> "
										+ errors);
					}
				});
			}

			if (results != null && results.size() > 0) {
				List<ProcessingResult> current = map.get(key);
				if (current == null) {
					current = new ArrayList<ProcessingResult>();
					map.put(key, results);
				} else {
					map.put(key, current);
					current.addAll(results);

				}
			}
		}
		return map;
	}
}
