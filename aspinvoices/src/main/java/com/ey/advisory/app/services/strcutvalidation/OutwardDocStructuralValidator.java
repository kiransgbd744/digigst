package com.ey.advisory.app.services.strcutvalidation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.strcutvalidation.sales.*;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

@Service("OutwardDocStructuralValidator")
public class OutwardDocStructuralValidator {
	
	@Autowired
	@Qualifier("OutwardDocOtherStructuralValidations")
private OutwardDocOtherStructuralValidations 
                                       OutwardDocOtherStructuralValidations;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardDocStructuralValidator.class);
	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES 
	= new ImmutableMap.Builder()
		.put(8, new ValidationRule[] { new TaxPeriodValidationRule() })
		.put(9, new ValidationRule[] { new SgstinValidationRule() })
		.put(10, new ValidationRule[] { new DocTypeValidationRule() })
		.put(11, new ValidationRule[] { new SupplyTypeValidationRule() })
		.put(12, new ValidationRule[] { new DocNoValidationRule() })
		.put(13, new ValidationRule[] { new DocDateValidationRule() })
		.put(14, new ValidationRule[] { new OrgDocNoValidationRule() })
		.put(15, new ValidationRule[] { new OrgDocDateValidationRule() })
		.put(16, new ValidationRule[] { new CDRPreGstValidationRule() })
		.put(17, new ValidationRule[] { new LineNumberValidationRule() })
		.put(18, new ValidationRule[] { new EcomGstinValidationRule() })
		.put(19, new ValidationRule[] { new UINorCompositionValidationRule()})
		.put(20, new ValidationRule[] { new OrginalCgstinValidationrule()})
		.put(21, new ValidationRule[] { new CustomerNameValidationRule()})
		.put(22, new ValidationRule[] { new CustomeCodeValidationRule()})
		.put(23, new ValidationRule[] { new BillToStateValidationRule()})
		.put(24, new ValidationRule[] { new ShipToStateValidationRule()})
		.put(25, new ValidationRule[] { new POSValidationRule() })
		.put(26, new ValidationRule[] { new PortCodeValidationRule() })
		.put(27, new ValidationRule[] { new ShippingBillNumberValidationRule()})
		.put(28, new ValidationRule[] { new ShippingBillDateValidationRule()})
		.put(29, new ValidationRule[] { new SGSTAmountValidationRule() })//fob
		.put(30, new ValidationRule[] { new SGSTAmountValidationRule() })//ExpertDuty
		.put(31, new ValidationRule[] { new SGSTAmountValidationRule() })
		.put(32, new ValidationRule[] { new ProductCodeValidationRule() })
		.put(33, new ValidationRule[] { new ProductDescriptionValidationRule()})
		.put(34, new ValidationRule[] { new CategoryOfProductValidationRule() })
		.put(35, new ValidationRule[] { new UOMValidationRule() })
		.put(36, new ValidationRule[] { new QuantityValidationRule() })
		.put(37, new ValidationRule[] { new SGSTAmountValidationRule()})//taxableValue
		.put(38, new ValidationRule[] { new SGSTRateValidationRule()})
		.put(39, new ValidationRule[] { new SGSTAmountValidationRule()})
		.put(40, new ValidationRule[] { new SGSTRateValidationRule()})
		.put(41, new ValidationRule[] { new SGSTAmountValidationRule()})
		.put(42, new ValidationRule[] { new SGSTRateValidationRule()})
		.put(43, new ValidationRule[] { new SGSTAmountValidationRule()})
		.put(44, new ValidationRule[] { new SGSTRateValidationRule()})
		.put(45, new ValidationRule[] { new SGSTAmountValidationRule()})
		.put(46, new ValidationRule[] { new SGSTRateValidationRule()})
		.put(47, new ValidationRule[] { new SGSTAmountValidationRule()})
		.put(48, new ValidationRule[] { new SGSTAmountValidationRule()})
		.put(49, new ValidationRule[] { new ReverseChargeValidationRule()})
		.put(50, new ValidationRule[] { new TCSFlagValidationRule()})
		.put(51, new ValidationRule[] { new EcomGstinValidationRule()})
		.put(52, new ValidationRule[] { new ItcFlagValidationRule()})
		.put(53, new ValidationRule[] { new ReasonForCrDrValidationRule()})
		.put(54, new ValidationRule[] { new AccountingVoucherNoValidationRule()})
		.put(55, new ValidationRule[] { new AccountingVoucherDateValidationRule()})
		
			.build();

	/**
	 * This
	 * 
	 * @param rawDoc
	 * @return
	 */
	
	
	public Map<String, List<ProcessingResult>> validation(
			Map<String, List<Object[]>> rawDocMap) {

		Map<String, List<ProcessingResult>> map = 
					new HashMap<String, List<ProcessingResult>>();
		rawDocMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();
			
			
			// First do normal structural valdiations (cell by cell)
			List<ProcessingResult> results = new ArrayList<>();

			for (Object[] obj : value) {

				for (int i = 8; i < 56; i++) {
					// First get the validators to be applied
					ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

					Object cellVal = obj[i];
					Arrays.stream(rules).forEach(rule -> {
						List<ProcessingResult> errors = rule.isValid(cellVal,
								obj, null);
						results.addAll(errors);

					});
				}
			}
			
			
			// check for incompatible values across rows in the same colummn
			List<ProcessingResult> errors1 = 
					     OutwardDocOtherStructuralValidations.validate(value);
			
			results.addAll(errors1);
			if (results != null && results.size() > 0) {
				map.put(key, results);
			}
		});
		return map;

	}
}