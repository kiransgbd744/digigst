package com.ey.advisory.app.services.strcutvalidation.setoffandutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.SetOffAndUtilExcelEntity;
import com.ey.advisory.app.services.docs.SRFileToSetOffAndUtilExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("SetOffAndUtilStructValidationChain")
public class SetOffAndUtilStructValidationChain {

	@Autowired
	@Qualifier("SRFileToSetOffAndUtilExcelConvertion")
	private SRFileToSetOffAndUtilExcelConvertion sRFileToSetOffAndUtilExcelConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] {
					new SetOffSerialNumberValidationRule() })
			.put(1, new ValidationRule[] { new SetOffReturnTypeValidationRule() })
			.put(2, new ValidationRule[] {
					new SetOffSgstinValidationRule() })
			.put(3, new ValidationRule[] { new SetOffReturnPeriodValidationRule() })
			.put(4, new ValidationRule[] {
					new SetOffDecsValidationRule() })
			.put(5, new ValidationRule[] {
					new SetOffTaxPayRevCharValidationRule() })
			.put(6, new ValidationRule[] {
					new SetOffTaxPayOthRevCharValidationRule() })
			.put(7, new ValidationRule[] { new SetOffTaxAlreadyRevCharValidationRule() })
			.put(8, new ValidationRule[] { new SetOffTaxAlreadyOthRevCharValidationRule() })
			.put(9, new ValidationRule[] { new SetOffAdjRevChargeValidationRule() })
			.put(10, new ValidationRule[] { new SetOffAdjRevOthChargeValidationRule() })
			.put(11, new ValidationRule[] {
					new SetOffPaidThroughIgstValidationRule() })
			.put(12, new ValidationRule[] { new SetOffPaidThroughCgstValidationRule() })
			.put(13, new ValidationRule[] {
					new SetOffPaidThroughSgstValidationRule() })
			.put(14, new ValidationRule[] {
					new SetOffPaidThroughCessValidationRule() })
			.put(15, new ValidationRule[] {
					new SetOffPaidInCashCessValidationRule() })
			.put(16, new ValidationRule[] {
					new SetOffPaidInCashInterestValidationRule() })
			.put(17, new ValidationRule[] {
					new SetOffPaidInCashLateFeeValidationRule() })
			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap, List<SetOffAndUtilExcelEntity> dumpExcelDatas) {

		Map<String, List<ProcessingResult>> map = 
				new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = dumpExcelDatas.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = sRFileToSetOffAndUtilExcelConvertion
					                        .getSetOffInvKey(obj);
			for (int i = 0; i < 17; i++) {
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
