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

import com.ey.advisory.admin.services.onboarding.gstinfileupload.MasterDataToItemConverter;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.MasterDataToProductConverter;
import com.ey.advisory.app.services.strcutvalidation.item.CategoryOfItemValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ComSuppIndicatorValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.EligibilityIndicatorValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItcEntitlementValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItcReversalIdentifierValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItemCircularDateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItemCodeValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItemDescValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItemDifferentialValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItemGstinValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItemHsnOrSacValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItemNilNonExemptValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItemNotificationDateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItemNotificationNoValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItemRateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItemReverseChargeValidation;
import com.ey.advisory.app.services.strcutvalidation.item.ItemTdsValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.ItemUomValidationRule;
import com.ey.advisory.app.services.strcutvalidation.item.PerOfEligibilityValidationRule;
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
@Component("MasterItemStructuralValidatorChain")
public class MasterItemStructuralValidatorChain {

	@Autowired
	@Qualifier("MasterDataToItemConverter")
	private MasterDataToItemConverter masterDataToItemConverter;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MasterItemStructuralValidatorChain.class);

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()

			.put(0, new ValidationRule[] { new ItemGstinValidationRule() })
			.put(1, new ValidationRule[] { new ItemCodeValidationRule() })
			.put(2, new ValidationRule[] { new ItemDescValidationRule() })
			.put(3, new ValidationRule[] { new CategoryOfItemValidationRule() })
			.put(4, new ValidationRule[] { new ItemHsnOrSacValidationRule() })
			.put(5, new ValidationRule[] { new ItemUomValidationRule() })
			.put(6, new ValidationRule[] { new ItemReverseChargeValidation() })
			.put(7, new ValidationRule[] { new ItemTdsValidationRule() })
			.put(8, new ValidationRule[] {
					new ItemDifferentialValidationRule() })
			.put(9, new ValidationRule[] { new ItemNilNonExemptValidationRule() })
			.put(10, new ValidationRule[] {
					new ItemNotificationNoValidationRule() })
			.put(11, new ValidationRule[] {
					new ItemNotificationDateValidationRule() })
			.put(12, new ValidationRule[] { new ItemCircularDateValidationRule() })
			.put(13, new ValidationRule[] { new ItemRateValidationRule() })
			.put(14, new ValidationRule[] { new EligibilityIndicatorValidationRule() })
			.put(15, new ValidationRule[] { new PerOfEligibilityValidationRule() })
			.put(16, new ValidationRule[] { new ComSuppIndicatorValidationRule() })
			.put(17, new ValidationRule[] { new ItcReversalIdentifierValidationRule() })
			.put(18, new ValidationRule[] { new ItcEntitlementValidationRule() })

			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap) {

		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();

		for (Object[] obj : rawDocMap) {
			List<ProcessingResult> results = new ArrayList<>();
			String key = masterDataToItemConverter.getItemValues(obj);
			for (int i = 0; i < 19; i++) {
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