package com.ey.advisory.app.services.strcutvalidation.interest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.InterestExcelEntity;
import com.ey.advisory.app.services.docs.SRFileToIntersetExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("InterestStructValidationChain")
public class InterestStructValidationChain {

	@Autowired
	@Qualifier("SRFileToIntersetExcelConvertion")
	private SRFileToIntersetExcelConvertion sRFileToIntersetExcelConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES =
			new ImmutableMap.Builder()
			.put(0, new ValidationRule[] {
					new InterestSerialNumberValidationRule() })
			.put(1, new ValidationRule[] {
					new InterestReturnTypeValidationRule() })
			.put(2, new ValidationRule[] { new InterestSgstinValidationRule() })
			.put(3, new ValidationRule[] {
					new InterestReturnPeriodValidationRule() })
			.put(4, new ValidationRule[] { new InterestReturnTableValidationRule() })
			.put(5, new ValidationRule[] {
					new InterestIgstValidationRule() })
			.put(6, new ValidationRule[] {
					new InterestCgstValidationRule() })
			.put(7, new ValidationRule[] {
					new InterestSgstValidationRule() })
			.put(8, new ValidationRule[] { new InterestCessValidationRule() })
			.put(9, new ValidationRule[] { new LasteCgstValidationRule() })
			.put(10, new ValidationRule[] { new LasteSgstValidationRule() })
			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap, List<InterestExcelEntity> dumpExcelDatas) {

		Map<String, List<ProcessingResult>> map = 
				new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = dumpExcelDatas.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = sRFileToIntersetExcelConvertion
					.getInterestInvKey(obj);
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
			if (results != null && results.size() > 0) {
				String keys = 
						key.concat(GSTConstants.SLASH).concat(id.toString());
				map.computeIfAbsent(keys,
						k -> new ArrayList<ProcessingResult>())
				.addAll(results);
			}
			is++;
			}
		return map;

	}
}
