package com.ey.advisory.app.services.structuralvalidation.gstr7;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
import com.ey.advisory.app.services.docs.Gstr7ExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr7StructuralValidatorChain")
public class Gstr7StructuralValidatorChain {

	@Autowired
	@Qualifier("Gstr7ExcelConvertion")
	private Gstr7ExcelConvertion gstr7ExcelConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()
			.put(0, new ValidationRule[] { new Gstr7ReturnPeriodValidationRule() })
			.put(1, new ValidationRule[] { new Gstr7ActionTypeValidationRule() })
			.put(2, new ValidationRule[] { new Gstr7TdsDeductorGstinValidationRule() })
			.put(3, new ValidationRule[] { new Gstr7OrgTdsGstinValidationRule() })
			.put(4, new ValidationRule[] { new Gstr7OrgReturnPeriodValidationRule() })
			.put(5, new ValidationRule[] { new Gstr7OrgGrossAmountValidtionRule() })
			.put(6, new ValidationRule[] { new Gstr7TdsDeducteeGstinValidationRule() })
			.put(7, new ValidationRule[] { new Gstr7GrossAmountValidationRule() })
			.put(8, new ValidationRule[] { new Gstr7TdsIgstValidationRule() })
			.put(9, new ValidationRule[] { new Gstr7TdsCgstValidationRule() })
			.put(10, new ValidationRule[] { new Gstr7TdsSgstValidationRule() })
			.put(11, new ValidationRule[] { new Gstr7ContractNoValidationRule() })
			.put(12, new ValidationRule[] { new Gstr7ContractDateValidationRule() })
			.put(13, new ValidationRule[] { new Gstr7ContractValueValidationRule() })
			.put(14, new ValidationRule[] { new Gstr7PayAdvNoValidationRule() })
			.put(15, new ValidationRule[] { new Gstr7PayAdvDateValidationRule() })
			.put(16, new ValidationRule[] { new Gstr7DocNumberValidationRule() })
			.put(17, new ValidationRule[] { new Gstr7DocDateValidationRule() })
			.put(18, new ValidationRule[] { new Gstr7InvoiceValueValidationRule() })
			.build();

	public Map<String, List<ProcessingResult>> validation(List<Object[]> rawDocMap,
			List<Gstr7AsEnteredTdsEntity> saveExcelAll) {

		Set<String> keySet = new HashSet<>();
		Map<String, List<ProcessingResult>> map = new HashMap<>();
		for (Object[] obj : rawDocMap) {
			List<ProcessingResult> results = new ArrayList<>();
			String key = gstr7ExcelConvertion.getFileProcessedKey(obj);
			if (!keySet.add(key)) {
				List<ProcessingResult> errors = new ArrayList<>();
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DOC_NO);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER2025", "Duplicate document.", location));
				results.addAll(errors);
			}

			for (int i = 0; i < 19; i++) {
				ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

				Object cellVal = obj[i];
				if (rules != null) {
					Arrays.stream(rules).forEach(rule -> {
						List<ProcessingResult> errors = rule.isValid(rawDocMap.indexOf(obj), cellVal, obj, null);
						results.addAll(errors);
					});
				}
			}

			if (!results.isEmpty()) {
				
				map.put(key, results);
			}
			
		}
		return map;
	}
}
