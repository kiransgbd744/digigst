package com.ey.advisory.app.services.strcutvalidation.refund;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.RefundsExcelEntity;
import com.ey.advisory.app.services.docs.SRFileToRefundExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("RefundStructValidationChain")
public class RefundStructValidationChain {

	@Autowired
	@Qualifier("SRFileToRefundExcelConvertion")
	private SRFileToRefundExcelConvertion sRFileToRefundExcelConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = 
			                                   new ImmutableMap.Builder()
			.put(0, new ValidationRule[] {
					new SerialNumberValidationRule() })
			.put(1, new ValidationRule[] { new RefundsSgstinValidationRule() })
			.put(2, new ValidationRule[] {
					new RefundsReturnPeriodValidationRule() })
			.put(3, new ValidationRule[] { new RefundsDescValidationRule() })
			.put(4, new ValidationRule[] {
					new RefundTaxValidationRule() })
			.put(5, new ValidationRule[] {
					new RefundInteresttValidationRule() })
			.put(6, new ValidationRule[] {
					new RefundPenaltyValidationRule() })
			.put(7, new ValidationRule[] { new RefundFeeValidationRule() })
			.put(8, new ValidationRule[] { new RefundOtherValidationRule() })
			.put(9, new ValidationRule[] { new RefundTotalValidationRule() })
			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap, List<RefundsExcelEntity> dumpExcelDatas) {

		Map<String, List<ProcessingResult>> map = 
				new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = dumpExcelDatas.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = sRFileToRefundExcelConvertion.getRefundInvKey(obj);
			for (int i = 0; i < 9; i++) {
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
				String keys = key.concat(GSTConstants.SLASH)
						.concat(id.toString());
				map.computeIfAbsent(keys,
						k -> new ArrayList<ProcessingResult>()).addAll(results);
			}
			is++;
		}
		return map;

	}
}
