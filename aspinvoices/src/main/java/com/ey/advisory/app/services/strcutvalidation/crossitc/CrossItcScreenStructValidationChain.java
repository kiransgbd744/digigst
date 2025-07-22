package com.ey.advisory.app.services.strcutvalidation.crossitc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.CrossItcAsEnteredEntity;
import com.ey.advisory.app.services.docs.SRFileToCrossItcDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Component("CrossItcScreenStructValidationChain")
public class CrossItcScreenStructValidationChain {

	@Autowired
	@Qualifier("SRFileToCrossItcDetailsConvertion")
	private SRFileToCrossItcDetailsConvertion sRFileToCrossItcDetailsConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] {
					new CrossItcIsdGstinValidationRule() })
			.put(1, new ValidationRule[] {
					new CrossItcTaxPeriodValidationRule() })
			.put(2, new ValidationRule[] { new CrossItcIgstUsedAsIgst() })
			.put(3, new ValidationRule[] { new CrossItcSgstUsedAsIgst() })// month
			.put(4, new ValidationRule[] { new CrossItcCgstUsedAsIgst() })// orgpos
			.put(5, new ValidationRule[] { new CrossItcSgstUsedAsSgst() })
			.put(6, new ValidationRule[] { new CrossItcIgstUsedAsSgst() })
			.put(7, new ValidationRule[] { new CrossItcCgstUsedAsCgst() })// orgquantity
			.put(8, new ValidationRule[] { new CrossItcIgstUsedAsCgst() })
			.put(9, new ValidationRule[] { new CrossItcCessUsedAsCess() })
			.build();

	public List<ProcessingResult> validation(
			List<Object[]> rawDocMap) {

	//	Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();
		List<ProcessingResult> results = new ArrayList<>();
		
		for (Object[] obj : rawDocMap) {
		//	Long id = excelDataSave.get(is).getId();
			
		//	String key = sRFileToCrossItcDetailsConvertion.getInvKey(obj);
			for (int i = 0; i < 10; i++) {
				// First get the validators to be applied
				ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

				Object cellVal = obj[i];
				Arrays.stream(rules).forEach(rule -> {
					List<ProcessingResult> errors = rule.isValid(cellVal, obj,
							null);
					results.addAll(errors);

				});
			}
		}

		return results;

	}

}
