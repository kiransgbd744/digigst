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

import com.ey.advisory.admin.services.onboarding.gstinfileupload.MasterDataToProductConverter;
import com.ey.advisory.app.services.strcutvalidation.product.CategoryOfProdValidationRule;
import com.ey.advisory.app.services.strcutvalidation.product.CircularDateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.product.DifferentialFlagValidationRule;
import com.ey.advisory.app.services.strcutvalidation.product.GstinValidationRule;
import com.ey.advisory.app.services.strcutvalidation.product.HsnOrSacValidationRule;
import com.ey.advisory.app.services.strcutvalidation.product.ITFlagValidationRule;
import com.ey.advisory.app.services.strcutvalidation.product.NilNonExemptValidationRule;
import com.ey.advisory.app.services.strcutvalidation.product.NotificationDateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.product.NotificationNoValidationRule;
import com.ey.advisory.app.services.strcutvalidation.product.ProdDescValidationRule;
import com.ey.advisory.app.services.strcutvalidation.product.ProductCodValidationRule;
import com.ey.advisory.app.services.strcutvalidation.product.RateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.product.ReverseChargeValidation;
import com.ey.advisory.app.services.strcutvalidation.product.TdsValidationRule;
import com.ey.advisory.app.services.strcutvalidation.product.uomValidationRule;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Anand3.M
 *
 */

@Component("MasterProductStructuralValidatorChain")
public class MasterProductStructuralValidatorChain {

	@Autowired
	@Qualifier("MasterDataToProductConverter")
	private MasterDataToProductConverter masterDataToProductConverter;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MasterProductStructuralValidatorChain.class);

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()

			.put(0, new ValidationRule[] { new GstinValidationRule() })
			.put(1, new ValidationRule[] { new ProductCodValidationRule() })
			.put(2, new ValidationRule[] { new ProdDescValidationRule() })
			.put(3, new ValidationRule[] { new CategoryOfProdValidationRule() })
			.put(4, new ValidationRule[] { new HsnOrSacValidationRule() })
			.put(5, new ValidationRule[] { new uomValidationRule() })
			.put(6, new ValidationRule[] { new ReverseChargeValidation() })
			.put(7, new ValidationRule[] { new TdsValidationRule() })
			.put(8, new ValidationRule[] {
					new DifferentialFlagValidationRule() })
			.put(9, new ValidationRule[] { new NilNonExemptValidationRule() })
			.put(10, new ValidationRule[] {
					new NotificationNoValidationRule() })
			.put(11, new ValidationRule[] {
					new NotificationDateValidationRule() })
			.put(12, new ValidationRule[] { new CircularDateValidationRule() })
			.put(13, new ValidationRule[] { new RateValidationRule() })
			.put(14, new ValidationRule[] { new ITFlagValidationRule() })

			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap) {

		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();

		for (Object[] obj : rawDocMap) {
			List<ProcessingResult> results = new ArrayList<>();
			String key = masterDataToProductConverter.getProductValues(obj);
			for (int i = 0; i < 15; i++) {
				// First get the validators to be applied
				ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

				Object cellVal = obj[i];
				Arrays.stream(rules).forEach(rule -> {
					List<ProcessingResult> errors = rule.isValid(cellVal, obj,
							null);
					if (errors != null) {
						results.addAll(errors);
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